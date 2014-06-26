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
import kb.Power;
import kb.functions.MapInfo;
import kb.functions.MapInfo.SCO_type; 

public class ExtendedDodo extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */
	
	boolean key_to_send = false; 
	Names names = null; 
	// /ArrayList<Province> visitedProvinces = new ArrayList<Province>();
	DodoBeliefBase		belief;
	
	public ExtendedDodo(Map map) {
		super("ExtendedDodo", "0.0.0.0.1", map);
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
		//TODO: determine if we kill of the alliance entirely
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
		int needed = target.getSupportNeeded();
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
	**********************************/

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

	/***********************************
	||   ||     ==        =====   ||   // 
	||   ||   //  \\     //       ||  //
	|=====|  ||===||    ||        || //
	||   ||  ||   ||    \\        || \\
	||   ||  ||   ||      =====   ||  \\
	***********************************/


	public void newTurn()
	{
		
		Province test = map.getProvince("STP");
		System.out.println("All nodes!");
		for (Province p : map.provinces) {
			System.out.println("\t" + p.getCentralNode().daide() + " " + p.getCentralNode().unit);
			for (Node n : p.coastLine) {
				System.out.println("\t" + n.daide() + " " + n.unit);
			} 
		}


		if (!power.alive) return;
		MapInfo mapInfo = new MapInfo(map, power);

		System.out.println();
		System.out.println("The new Dodo lives!");
		System.out.println("New turn for " + power.getName());
		System.out.println("Year: " + map.getYear() + " -----------  Phase: " + map.getPhase()); 

		ArrayList<Unit> units = map.getUnitsByOwner(this.getPower());
		ArrayList<Province> home = power.homeProvinces;
		ArrayList<Province> provinces = map.getProvincesByOwner(this.getPower()); 
		ArrayList<Province> occupied = new ArrayList<Province>();
		for (Unit u : units) occupied.add(u.location.province);
		//ArrayList<Province> occupied = new ArrayList<Province>();

		if (map.getPhase() == Phase.SPR || map.getPhase() == Phase.FAL) {
			/*
			MOVEMENT PHASES
			*/

			System.out.println("Init MapInfo");
			System.out.println("power: " + power);
			
			ArrayList<Unit> availableUnits = new ArrayList<Unit>(); 
			availableUnits.addAll(units);

			while (true) {
				ArrayList<ProvinceData> targets = mapInfo.getSortedTargets(); 
				targets = mapInfo.filterTakeable(targets);
/*
				System.out.println("Sorted targets:" + targets.size());
				for (ProvinceData p : targets) {
					System.out.println(p.toString()); 
				}*/

				float totalWeight = 0; 
				for (ProvinceData target : targets) totalWeight += target.weight;

				if (totalWeight == 0) break; 

				int targetIdx = 0;
				float val = 0;
				float targetVal = (float)Math.random() * totalWeight;
				while ((val += targets.get(targetIdx).weight) < targetVal && targetIdx++ < targets.size());

				//System.out.println("attacking: " + targets.get(targetIdx).province.name);
				ArrayList<UnitData> sortedUnits = mapInfo.getSortedUnits(targets.get(targetIdx));

				//Be sure that the unit standing on the target is always used (or do something intelligent...)
				
				ArrayList<UnitData> usedUnits = new ArrayList<UnitData>();
				ArrayList<Province> usedProvinces = new ArrayList<Province>(); 
				
				attack(targets.get(targetIdx), sortedUnits, usedUnits, usedProvinces);
	
				for (UnitData ud : usedUnits) {
					availableUnits.remove(ud.unit); 
				}

				ArrayList<ProvinceData> usedProvinceData =  new ArrayList<ProvinceData>();

				for (Province p0 : usedProvinces) {
					for (ProvinceData p1 : targets) {
						if (p0 == p1.province) {
							usedProvinceData.add(p1);
							break;
						}
					}
				}

				System.out.println("------------- ORDERS -------------");
				for (Order order : queue) {
					System.out.println(order.daide());
				}


				mapInfo.updateByMove(usedUnits, usedProvinceData);

			}
			for (Unit u : availableUnits) {
				queue.add(new Hold(u));
			}

			/*
			for (Unit u : availableUnits) {
				ArrayList<Node> nbh = filterNeighbours(map.getValidNeighbours(u), occupied);
				if (nbh.size() == 0) {
					//There are no possible moves so hold
					queue.add(new Hold(u)); 
				} else {
					Node destination = moveToSupplyCenter(u, nbh, occupied, true, false);
					occupied.remove(u.location.province); 
					occupied.add(destination.province);
					System.out.println("Unit in " + u.location.daide() + " -> " + destination.daide()); 
					queue.add(new Move(u, destination)); 				}
			}
			*/

			
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
