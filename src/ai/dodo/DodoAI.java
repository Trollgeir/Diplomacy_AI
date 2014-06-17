package ai.dodo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import kb.Map;
import kb.unit.*;
import kb.Node;
import kb.province.Province;
import kb.unit.Unit;
import negotiator.Negotiator;
import message.order.*;
import message.server.Connect;
import message.server.MapDefinition;
import message.server.Submit;
import message.server.Yes;
import communication.server.DisconnectedException;
import communication.server.Server;
import communication.server.UnknownTokenException;
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
		
	@Override
	protected Order offensiveMove(int i)
	{	
		int[] shuffled = shuffle(this.adjacencyList.get(i).size());
		ArrayList<Unit> units = map.getUnitsByOwner(this.power);
		
		for(int j = 0; j < shuffled.length; j++)
		{
			Province p = this.adjacencyList.get(i).get(shuffled[j]).province;
			if(!visitedProvinces.contains(p))
			{
				visitedProvinces.remove(units.get(i).location.province);
				visitedProvinces.add(p);
				return new Move(units.get(i), this.adjacencyList.get(i).get(j));
			}
		}
		return new Hold(units.get(i));
	}
	
	@Override
	protected Order defensiveMove(int i)
	{
		ArrayList<Unit> units = map.getUnitsByOwner(this.power); 
		return new Hold(units.get(i));
	}
	
	@Override
	protected void handleHLO(String[] message)
	{
		this.power =  map.getPower(message[1]);
		this.passcode = message[2];
		this.lvl = message[4];
		if (names != null) {
			names.init(map);
		}
		
		belief = new DodoBeliefBase(map, power);
	}
	@Override
	protected void handleSLO(String[] message)
	{
		if (message[1].equals(getPower().getName())) {
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

	public ArrayList<Node> getCommandCenterNodes(ArrayList<Node> nodes) {
		//only return those nodes which are in a province with command center which are NOT ours!
		ArrayList<Node> result = new ArrayList<Node>(); 
		for (Node n : nodes) {
			if (n.province.isSupplyCenter() && n.province.getOwner() != power) {
				result.add(n); 
			}
		}
		return result; 
	}

	public Node moveToCommandCenter(Unit unit, ArrayList<Node> neighbourhood, ArrayList<Province> occupied) {
		ArrayList<Node> commandCenters = getCommandCenterNodes(neighbourhood); 
		if (commandCenters.size() > 0) {
			return getRandomElement(commandCenters);  
		} else {
			ArrayList<Node> indirectCommandCenters = new ArrayList<Node>(); 
			for (Node node : neighbourhood) {
				ArrayList<Node> n_neigbourhood = filterNeighbours(map.getValidNeighbours(unit, node), occupied);
				ArrayList<Node> n_commandCenters = getCommandCenterNodes(n_neigbourhood);
				if (n_commandCenters.size() > 0) indirectCommandCenters.add(node); 
			}
			if (indirectCommandCenters.size() > 0) {
				return getRandomElement(indirectCommandCenters);
			}
		}
		return getRandomElement(neighbourhood); 
	}
	
	public void newTurn()
	{
		if (!power.alive)
			return;
		
		belief.calcThreats();
		
		System.out.println();
		System.out.println("New turn for " + power.getName());
		System.out.println("Year: " + map.getYear() + " -- Phase: " + map.getPhase()); 
		
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
			queue = Heuristics.getOpeningMovesSpring(this.getPower(), map.getStandard(), map);
		} else if (map.getPhase() == Phase.SPR || map.getPhase() == Phase.FAL) {
			/*
			MOVEMENT PHASES
			*/
			System.out.println("------------- ORDERS -------------");
			for (Unit u : units) {
				ArrayList<Node> nbh = filterNeighbours(map.getValidNeighbours(u), occupied);
				if (nbh.size() == 0) {
					//There are no possible moves so hold
					queue.add(new Hold(u)); 
				} else {
					Node destination = moveToCommandCenter(u, nbh, occupied);
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
						Node destination = moveToCommandCenter(u, u.retreatTo, occupied);
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
			int error = units.size() - provinces.size(); 
			// error > 0 means more units then provinces so REMOVE
			// error < 0 means more provinces then units so  BUILD
			while (error > 0) {
				//REMOVE
				
				int idx = (int)(Math.random() * units.size());
				queue.add(new Remove(units.get(idx)));
				System.out.println("Removing unit in " + units.get(idx).location.daide());
				units.remove(idx);
				
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
