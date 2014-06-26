package ai.dodo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import kb.Map;
import kb.unit.*;
import kb.Node;
import kb.province.Province;
import kb.unit.Unit;
import message.DaideMessage;
import message.order.*;
import message.press.Alliance;
import message.press.Arrangement;
import message.press.Proposal;
import message.press.Send;
import ai.dodo.Negotiator;
import ai.AI;
import ai.Heuristics;
import game.Game;
import kb.Names; 
import kb.Phase; 
import kb.Power;

public class DodoAI extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */
	
	boolean key_to_send = false; 
	Names names = null; 
	String fileName = "";
	String name = "changeMe!";
	double initialTrust = 0.5;
	double halflife = 0.05;
	double rightesnous = 0.5;
	double supportSteep = 0.5;
	
	ArrayList<Province> visitedProvinces = new ArrayList<Province>();
	DodoBeliefBase		belief;
	
	public DodoAI(Map map) {
		super("DodoAI", "0.0.0.0.1", map);
		negotiator.dodoAI = this;
		negotiator.map = map;
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
			} else if (flag.equals("-f")) {
				fileName = args[++i];
			} else  {
				//hack to throw outofboundexception
				String error = args[-20]; 
			}
		}
	}
	public void parseTextFile(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			try {
				String line = br.readLine();
				int index = 0;
				String result = "";
				while (line != null) {
					index = line.lastIndexOf("=");
					result = line.substring(index+1);
					if (result.startsWith(" ")) {
						result = result.substring(1);
					}
					if (line.startsWith("name")) {
						name = result;
					} else if (line.startsWith("initialTrust")) {
						initialTrust = Double.parseDouble(result);
					} else if (line.startsWith("halflife")) {
						halflife = Double.parseDouble(result);
					} else if (line.startsWith("rightesnous")) {
						rightesnous = Double.parseDouble(result);
					} else if (line.startsWith("supportSteep")) {
						supportSteep = Double.parseDouble(result);
					}
					line = br.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		System.out.println("name: " + name + " initialTrust: " + initialTrust + " halflife: " + halflife + " rightesnous: " + rightesnous + " supportSteep: " + supportSteep );
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
	public void init(String[] args) throws ArrayIndexOutOfBoundsException {
		parseCommandLineArguments(args);
		if (!fileName.equals("")) {
			parseTextFile(fileName);
		}
	}

	public static void main(String[] args) {
		Map map = new Map();
		AI ai = new ExtendedDodo(map);
		new Game(ai, map, args);
	}
	
	public <T> T getRandomElement(ArrayList<T> list) {
		return list.get((int)(Math.random() * list.size())); 
	} 

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

	public void addAdjSupplyCenter(Node n, ArrayList<AdjSupplyCenter> centers, Unit u) {
		for (AdjSupplyCenter c : centers) {
			if (c.n == n) {
				c.adjUnits.add(u);
				return;
			} 
		}

		centers.add(new AdjSupplyCenter(n, u, map.powers));
	}

	public ArrayList<AdjSupplyCenter> getAdjSupplyCenters(ArrayList<Unit> units) {
		ArrayList<AdjSupplyCenter> adjCenters =  new ArrayList<AdjSupplyCenter>();

		for (Unit u : units) {
			ArrayList<Node> adj = map.getValidNeighbours(u);
			for (Node n : adj) {
				if (n.province.isSupplyCenter()) {
					addAdjSupplyCenter(n, adjCenters, u);	
				}
			}

		}

		return adjCenters; 
	}

	public void determineResistance(ArrayList<AdjSupplyCenter> centers) {
		for (AdjSupplyCenter c : centers) {
			for (Node n : c.n.landNeighbors) {
				if (n.unit != null && n.unit.owner != power) {
					c.addPower(n.unit.owner);
				}
			}
		}
	}

	public ArrayList<AdjSupplyCenter> getTakableCenters(ArrayList<AdjSupplyCenter> centers) {
		ArrayList<AdjSupplyCenter> result = new ArrayList<AdjSupplyCenter>();
		for (AdjSupplyCenter c : centers) {
			if (c.isTakable(power)) result.add(c);
		}

		return result; 
	} 

	public void newTurn()
	{
		if (!power.alive)
			return;
		
		//belief.calcThreats();
		
		System.out.println();
		System.out.println("New turn for " + power.getName());
		System.out.println("Year: " + map.getYear() + " -----------  Phase: " + map.getPhase()); 
		
		ArrayList<Unit> units = map.getUnitsByOwner(this.getPower());
		ArrayList<Province> home = power.homeProvinces;
		ArrayList<Province> provinces = map.getProvincesByOwner(this.getPower()); 
			
		//Keep track of where units are and are sent
		ArrayList<Province> occupied = new ArrayList<Province>();
		for (Unit u : units) occupied.add(u.location.province);
		
		if (map.getYear() == 1901 && map.getPhase() == Phase.SPR) {
			/*
			Opening moves
			*/
			System.out.println("Game is Standard, checking if our power has heuristics...");
			
			// propose preferred alliances:
			// uncomment if you want awesome alliance proposals to be sent the first turn
			/*
			Power prefer = Heuristics.preferredAlliance(this.getPower(), map.getStandard(), map);
			Power enemy = Heuristics.preferredEnemy(this.getPower(), map.getStandard(), map);
			Power[] alliance = {this.getPower(), prefer};
			Power[] against = {enemy};
			DaideMessage propose = new Proposal(new Alliance(alliance, against)){};
			DaideMessage send = new Send(propose,prefer);
			
			Game.server.send(send);
			*/
			
			queue = Heuristics.getOpeningMovesSpring(this.getPower(), map.getStandard(), map);
		} else if (map.getPhase() == Phase.SPR || map.getPhase() == Phase.FAL) {
			/*
			MOVEMENT PHASES
			*/

			System.out.println("Power: " + power.daide());
			ArrayList<AdjSupplyCenter> adjCenters = getAdjSupplyCenters(units);	
			System.out.println("num adj: " + adjCenters.size());

			determineResistance(adjCenters);

			ArrayList<AdjSupplyCenter> takableCenters = getTakableCenters(adjCenters);

/*			for (AdjSupplyCenter c : takableCenters) {
				System.out.println(c.n.daide() + " can be taken by (" + c.adjUnits.size() + 
					" versus " + c.supportNeeded +"): ");
				for (Unit u : c.adjUnits) {
					System.out.println("\t" + u.location.daide());
				}
			}*/
			
			ArrayList<Unit> availableUnits = new ArrayList<Unit>();
			availableUnits.addAll(units);

			//TODO sort takablecenters
			for (int i = 0; i < takableCenters.size(); ++i) {
				AdjSupplyCenter c = takableCenters.get(i); 
				ArrayList<Unit> aaUnits_free = new ArrayList<Unit>();
				ArrayList<Unit> aaUnits_occ = new ArrayList<Unit>();
				ArrayList<Unit> assigned = new ArrayList<Unit>();
				for (Unit unit : c.adjUnits) {
					if (availableUnits.contains(unit)) {
						boolean occ = false;
						for (int j = i + 1; j < takableCenters.size(); ++j) {
							if (takableCenters.get(j).adjUnits.contains(unit)) {
								occ = true;
								break;
							}
						}
						if (occ) aaUnits_occ.add(unit);
						else aaUnits_free.add(unit);
					}
				}

				int numberToAssign = 0; 
				if (aaUnits_free.size() + aaUnits_occ.size() > c.supportNeeded) {
					numberToAssign = c.supportNeeded + 1; 
				} else if (aaUnits_free.size() + aaUnits_occ.size() == c.supportNeeded) {
					numberToAssign = c.supportNeeded; 
				}

				int free_needed = Math.min(aaUnits_free.size(), numberToAssign);
				int occ_needed = numberToAssign - free_needed;
				System.out.println(power.daide() + " Numbers: " + free_needed + ", " + occ_needed);
				for (int x = 0; x < free_needed; ++x ) {
					assigned.add(aaUnits_free.get(x)); 
					availableUnits.remove(aaUnits_free.get(x));
				} 
				for (int x = 0; x < occ_needed; ++x) {
					assigned.add(aaUnits_occ.get(x));
					availableUnits.remove(aaUnits_occ.get(x));
				}
				for (int x = 0; x < assigned.size(); ++x) {
					if (x == 0) {
						occupied.remove(assigned.get(x).location.province);
						occupied.add(c.n.province);
						queue.add(new Move(assigned.get(x), c.n)); 
					} else {
						//queue.add(new SupportToMove(assigned.get(x), assigned.get(0), c.n));
					}
				}
				System.out.println("I will invade " + c.n.daide() + " with ");
				for (Unit u : assigned) {
					System.out.println("\t" + u.daide()); 
				} 

			}



			System.out.println("------------- ORDERS -------------");
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
