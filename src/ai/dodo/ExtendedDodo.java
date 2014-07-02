package ai.dodo;

import java.io.IOException;
import java.util.ArrayList;

import ai.dodo.phases.*;
import kb.Map;
import kb.unit.*;
import kb.Node;
import kb.province.Province;
import kb.unit.Unit;
import message.DaideMessage;
import message.order.*;
import message.press.*;
import message.press.Proposal;
import message.press.Send;
import message.server.Huh;
import ai.AI;
import ai.Heuristics;
import game.Game;
import kb.Names; 
import kb.Phase; 
import kb.Power;
import kb.functions.MapInfo;
import kb.functions.MapInfo.SCO_type; 

public class ExtendedDodo extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */
	
	public double initialTrust = 0.5;
	public double decay = 0.05;
	public double righteousness = 0.5;
	public double supportSteep = 0.5;
	
	protected Negotiator negotiator;
	
	boolean key_to_send = false; 
	Names names = null; 
	// /ArrayList<Province> visitedProvinces = new ArrayList<Province>();
	DodoBeliefBase		belief;
	
	public ExtendedDodo(Map map) {
		super("ExtendedDodo", "0.0.0.0.1", map);
		
		negotiator = new Negotiator(this, map);
	}

	@Override
	public String getUsage() {
		return "\nOptional flags:\n\t-n [name] (Changes name of this AI)\n\t-l [path to log file] (Enables reading names from log file)\n\t-k (Enables require key to send messages)" ;
	}

	public void parseCommandLineArguments(String[] args) {
		// first two argument are always ip and port
		for (int i = 2; i < args.length; i ++) {
			String flag = args[i]; 
			if (flag.equals("-n")) {
				setName(args[++i]);  
			} else if (flag.equals("-l")) {
				names = new Names(args[++i]); 
			} else if (flag.equals("-k")) {
				key_to_send = true; 
			} else  {
				//hack to throw outofboundexception
				String error = args[-20]; 
			}
		}
	}

	public void findGains()
	{}
		
	
	
	@Override
	protected void handleHLO(String[] message)
	{
		this.power =  map.getPower(message[2]);		
		this.passcode = message[5];
		this.lvl = message[10];
		if (names != null) {
			names.init(map);
		}
		
		belief = new DodoBeliefBase(map, power);
	}
	@Override
	protected void handleSLO(String[] message)
	{
		if (message[2].equals(getPower().getName())) {
			System.out.println("I won!"); 
		} else {
			System.out.println("I lost."); 
		}
	}
	@Override
	protected void handleFRM(String[] message)
	{
		this.negotiator.addProposal(message);
	}
	@Override
	protected void handleSMR(String[] message)
	{
		System.out.println("\n endgame info: \n");
		for (String m : message) {
			if (m == null) break; 
			System.out.println("" + m);
		
		}
		//TODO Write belief base info to file. thanks.
		System.out.println("TO BE CONTINUED?"); 
		System.exit(0); 
	}
	@Override
	protected void handleTHX(String[] message)
	{
		//System.out.println("Server thanks " + this.getPower().getName() + " for his order."); 
		this.setCanMessage(true);
	}
	@Override
	protected void handleYES(String[] message)
	{
		/*TODO, need to know all about sent messages..*/
	}
	@Override
	protected void handleREJ(String[] message)
	{
		/*TODO, need to know all about sent messages..*/
		System.out.println("Rejected order muh."); 
	}
	@Override
	protected void handleHUH(String[] message)
	{
		/*TODO, need to know all about sent messages..*/
	}
	
	@Override
	public void handleORD(String[] message)
	{
		Power from = map.getPower(message[7]);
		String orderType = message[11];
		String target = "";
		Power supportPower = null;
		
		if (orderType.equals("MTO")) {
			target = message[12];
			//System.out.println("" + from.getName() + " " + orderType + " " + Target);
		} else if (orderType.equals("SUP")) {
			supportPower = map.getPower(message[13]);
			target = message[18];
			//System.out.println("" + from.getName() + " " + orderType + " " + supportPower.getName() + " in " + Target );
		}
		
		ArrayList<Province> myProvinces = map.getProvincesByOwner(getPower());
		if(target != "" && myProvinces.contains(target))
		{
			isDefected(from, supportPower);
		}
	
		if (map.getPhase() == Phase.SUM || map.getPhase() == Phase.AUT){
			//We're only interested in processing results after a movement phase. Parser needed!
		}
		
	}
	
	@Override
	public void init(String[] args) throws ArrayIndexOutOfBoundsException {
		parseCommandLineArguments(args);
	}

	public static void main(String[] args) {
		Map map = new Map();
		AI ai = new ExtendedDodo(map);
		new Game(ai, map, args);
	}
	
	public void isDefected(Power mover, Power supporter)
	{
		if(supporter != null) // There is a supporter; check if he is in alliance or peace with us
		{
			if(belief.isAlly(mover) || belief.powerInfo.get(mover).peace) // The other power was trying to invade our province while being allied with us
			{
				handleBackstab(mover);
			}
			else if (belief.isAlly(supporter) || belief.powerInfo.get(supporter).peace)
			{
				handleBackstab(supporter);
			}
		}
		else // No supporter; check if the mover has an alliance or peace with us
			if(belief.isAlly(mover) || belief.powerInfo.get(mover).peace) // The other power was trying to invade our province while being allied with us
			{
				handleBackstab(mover);
			}
	}
	
	public void handleBackstab(Power p)
	{
		AllianceInfo allianceInfo = belief.allianceByPower(p);
		// Decrement trust of the power
		belief.defectDec(allianceInfo.time, p);
		// Disband the alliance ??
		belief.deleteAllAlliancesWith(p);
	}
	
	public void attack(ProvinceData target, ArrayList<UnitData> units, ArrayList<UnitData> usedUnits, ArrayList<Province> usedProvinces) {
		UnitData holdingUnit = null;
		UnitData movingUnit = null;
		for (UnitData u : units) {
			if (u.unit.location.province == target.province) {
				holdingUnit = u;
				usedUnits.add(u);
				break;
			}
		}

		if (holdingUnit != null) {
			units.remove(holdingUnit);
			usedUnits.add(holdingUnit);
			queue.add(new Hold(holdingUnit.unit));
		} else {
			movingUnit = units.get(0);
			usedUnits.add(movingUnit);
			units.remove(movingUnit);
			queue.add(new Move(movingUnit.unit, movingUnit.destNode));
		}
		usedProvinces.add(target.province);

		int used = 0;
		int needed = target.supportNeeded;
		while (used < needed - 1) {
			UnitData supportUnit = units.get(used); 
			if (holdingUnit == null) {
				queue.add(new SupportToMove(supportUnit.unit, movingUnit.unit, movingUnit.destNode.province));
			} else {
				queue.add(new SupportToHold(supportUnit.unit, holdingUnit.unit));
			}

			usedUnits.add(supportUnit);
			usedProvinces.add(supportUnit.unit.location.province);
			used++;
		}
	}


	/***********************************
	||   ||     ==        =====   ||   // 
	||   ||   //  \\     //       ||  //
	|=====|  ||===||    ||        || //
	||   ||  ||   ||    \\        || \\
	||   ||  ||   ||      =====   ||  \\
	***********************************/

	public ArrayList<Node> filterNeighbours(ArrayList<Node> neighbours, ArrayList<Province> occupied) {
		ArrayList<Node> result = new ArrayList<Node>();
		for (Node n : neighbours) {
			if (!occupied.contains(n.province)) result.add(n);
		} 
		return result;
	}

	public ArrayList<Province> filterProvinces(ArrayList<Province> provinces, ArrayList<Province> built, ArrayList<Unit> units) {
		ArrayList<Province> result = new ArrayList<Province>(); 
		for (Province p : provinces) {
			if (built.contains(p)) continue; 
			if (!power.homeProvinces.contains(p)) continue;
			boolean empty = true; 
			for (Unit u : units) {
				if (u.location.province == p) {
					empty = false; 
					break; 
				} 
			}
			if (empty) result.add(p);
		}
		return result;
	};

	public ArrayList<Node> getSupplyCenterNodes(ArrayList<Node> nodes, boolean ours) {
		//only return those nodes which are in a province with supply center which are NOT ours!
		ArrayList<Node> result = new ArrayList<Node>(); 
		for (Node n : nodes) {
			if (n.province.isSupplyCenter()) {
				if (ours && n.province.getOwner() == power) {
					result.add(n);
				} else if (!ours && n.province.getOwner() != power) {
					result.add(n);
				}
			}
		}
		return result; 
	}

	public Node moveToSupplyCenter(Unit unit, ArrayList<Node> neighbourhood, ArrayList<Province> occupied, boolean move, boolean ours) {
		ArrayList<Node> supplyCenters = getSupplyCenterNodes(neighbourhood, ours); 
		if (supplyCenters.size() > 0) {
			return getRandomElement(supplyCenters);  
		} else {
			ArrayList<Node> indirectSupplyCenters = new ArrayList<Node>(); 
			for (Node node : neighbourhood) {
				ArrayList<Node> n_neigbourhood = filterNeighbours(map.getValidNeighbours(unit, node), occupied);
				ArrayList<Node> n_supplyCenters = getSupplyCenterNodes(n_neigbourhood, ours);
				if (n_supplyCenters.size() > 0) indirectSupplyCenters.add(node); 
			}
			if (indirectSupplyCenters.size() > 0) {
				return getRandomElement(indirectSupplyCenters);
			}
		}
		if(move)
			return getRandomElement(neighbourhood); 
		else
			return null;
	}

	public <T> T getRandomElement(ArrayList<T> list) {
		return list.get((int)(Math.random() * list.size())); 
	}

	public DodoBeliefBase getBeliefBase() {
		return belief;
	}

	/***********************************
	||   ||     ==        =====   ||   // 
	||   ||   //  \\     //       ||  //
	|=====|  ||===||    ||        || //
	||   ||  ||   ||    \\        || \\
	||   ||  ||   ||      =====   ||  \\
	***********************************/


	public void newTurn()
	{
		if (!power.alive) return;
		MapInfo mapInfo = new MapInfo(map, power);

		System.out.println();
		System.out.println("The new Dodo lives!");
		System.out.println("New turn for " + power.getName());
		System.out.println("Year: " + map.getYear() + " -----------  Phase: " + map.getPhase()); 

		if (power.getName().equals("ITA")) {
			System.out.println("Hey, I am Italy and I want peace with Austria! :D"); 
			Power[] peace = new Power[2];
			peace[0] = power; 
			peace[1] = map.getPower("AUS");
			Game.server.send(new Send(new Proposal(new Peace(peace)), peace[1]));  
		}

		//Suggesting alliances to EVERYONE!
		/*Power[] me = {power};
		
		for ( Power ally : map.powers )
		{
			for ( Power enemy : map.powers )
			{
				Power[] against = {enemy};
				DaideMessage msg = new Send(new Proposal(new Alliance(me, against)), ally);
				
				if (ally != enemy && ally != power && enemy != power)
				{
					Game.server.send(msg);
					System.out.println(msg);
				}
			}
		}*/
		
		
		
		if (map.getPhase() == Phase.SPR || map.getPhase() == Phase.FAL) {
			DodoMovementPhase movementPhase = new DodoMovementPhase(this); 
			movementPhase.run(queue);
			
		} else if(map.getPhase() == Phase.SUM || map.getPhase() == Phase.AUT){
			
			DodoRetreatPhase retreatPhase = new DodoRetreatPhase(this);
			retreatPhase.run(queue);
		} else if (map.getPhase() == Phase.WIN) { 
			
			DodoBuildPhase buildPhase = new DodoBuildPhase(this);
			buildPhase.run(queue);
		}

		if (key_to_send) {
			try {
				System.out.println("Press enter to continue.");
				System.in.read(); 
			} catch (IOException e) {
				//Should never happen though...
				e.printStackTrace(); 
			}
		}
		
		handleQueue();
		
		System.out.println("");
		System.out.println(belief);
		System.out.println("");
		System.out.println("-----------END OF TURN -----------");
		System.out.println("");
		//System.out.println(this.getPower().getName() + " sent his order!"); 
	}
	
}
