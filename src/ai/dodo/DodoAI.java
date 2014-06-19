package ai.dodo;

import java.io.IOException;
import java.util.ArrayList;
import kb.Map;
import kb.unit.*;
import kb.Node;
import kb.province.Province;
import kb.unit.Unit;
import message.order.*;
import ai.AI;
import ai.Heuristics;
import game.Game;
import kb.Names; 
import kb.Phase; 

public class DodoAI extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */
	
	boolean key_to_send = false; 
	Names names = null; 
	ArrayList<Province> visitedProvinces = new ArrayList<Province>();
	DodoBeliefBase		belief;
	
	public DodoAI(Map map) {
		super("DodoAI", "0.0.0.0.1", map);
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
	
	public <T> T getRandomElement(ArrayList<T> list) {
		return list.get((int)(Math.random() * list.size())); 
	}

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
	public void init(String[] args) throws ArrayIndexOutOfBoundsException {
		parseCommandLineArguments(args);
	}

	public static void main(String[] args) {
		Map map = new Map();
		AI ai = new DodoAI(map);
		new Game(ai, map, args);
	}

	public void newTurn()
	{
		if (!power.alive)
			return;
		
		MapInfo info = new MapInfo(map, power);
		ArrayList<Province> occupied = new ArrayList<Province>();
		occupied.addAll(info.getUnits()); 
		//belief.calcThreats();
		
		System.out.println();
		System.out.println("New turn for " + power.getName());
		System.out.println("Year: " + map.getYear() + " -----------  Phase: " + map.getPhase()); 
		
		ArrayList<Unit> units = map.getUnitsByOwner(this.getPower());
		ArrayList<Province> home = power.homeProvinces;
		ArrayList<Province> provinces = map.getProvincesByOwner(this.getPower()); 
		
		if (map.getYear() == 1901 && map.getPhase() == Phase.SPR) {
			/*
			Opening moves
			*/
			System.out.println("Game is Standard, checking if our power has heuristics...");
			queue = Heuristics.getOpeningMovesSpring(this.getPower(), map.getStandard(), map);
		} else if (map.getPhase() == Phase.SPR || map.getPhase() == Phase.FAL) {
			/*
			MOVEMENT PHASES
			*/

			

			System.out.println("Power: " + power.daide());
			System.out.println("num adj: " + adjCenters.size());

			
		} else if(map.getPhase() == Phase.SUM || map.getPhase() == Phase.AUT){
			for(Unit u : units)
			{
				if(u.mustRetreat) {
					// No place to retreat to, so we must disband. 
					if(u.retreatTo.size() == 0) queue.add(new Disband(u)); 
					else {
						//Try to move to a supply center
						Node destination = moveToSupplyCenter(u, u.retreatTo, occupied, true, false);
						occupied.remove(u.location.province); 
						occupied.add(destination.province);
						System.out.println(power.getName() + " : " + "I want to retreat " + u.location.daide() + " to " + destination.daide()); 
						queue.add(new Retreat(u, destination)); 
					}
				}
			}
		} else if (map.getPhase() == Phase.WIN) { 
			/*
			BUILD PHASE
			*/
			System.out.println("---------- BUILD/REMOVE ----------");
			ArrayList<Province> built = new ArrayList<Province>();
			ArrayList<Unit> canBeRemoved = new ArrayList<Unit>();
			canBeRemoved.addAll(units); 
			int error = units.size() - provinces.size(); 
			// error > 0 means more units then provinces so REMOVE
			// error < 0 means more provinces then units so  BUILD
			while(error > 0)
			{
				Unit remove = null; int best = 0; int j = 0;
				for(Unit u : canBeRemoved)
				{
					if (u.location.province.isSupplyCenter()) {
						j = 0;
					} else {
						ArrayList<Node> nbh = filterNeighbours(map.getValidNeighbours(u), occupied);
						Node n = moveToSupplyCenter(u, nbh, occupied, false, true);
						if (n == null) {
							j = 3;
						} else if (n.province.isSupplyCenter()) {
							j = 1;
						} else {
							j = 2;
						}
					}
					if(j > best)
					{
						remove = u;
						best = j;
					}
					else if(j == best)
						if(Math.random() > 0.5)
							remove = u;
				}
				queue.add(new Remove(remove));
				canBeRemoved.remove(remove); 
				error--;
			}
			while (error < 0) {
				//BUILD
				ArrayList<Province> scs = filterProvinces(map.getProvincesByOwner(power), built, units);

				if (scs.size() == 0) { 
					System.out.println("No room to build! All SC's are occupied :(");
					queue.add(new WaiveBuild(power));
				} else {
					int idx = (int)(Math.random() * scs.size());
					queue.add(new Build(new Army(power, scs.get(idx).getCentralNode())));
					System.out.println("Building unit in " + scs.get(idx).getCentralNode().daide());
					built.add(scs.get(idx));
				}
				error++; 
			}
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
		System.out.println("-----------END OF TURN -----------");
		System.out.println("");
		System.out.println("");
		//System.out.println(this.getPower().getName() + " sent his order!"); 
	}
	
}
