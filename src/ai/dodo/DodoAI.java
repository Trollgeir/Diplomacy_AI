package ai.dodo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import kb.Map;
import kb.Node;
import kb.province.Province;
import kb.unit.Unit;
import negotiator.Negotiator;
import message.order.Hold;
import message.order.Move;
import message.order.Order;
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

public class DodoAI extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */
	
	boolean key_to_send = false; 
	Names names = null; 
	ArrayList<Province> visitedProvinces = new ArrayList<Province>();

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
			Province p = this.adjacencyList.get(i).get(j).province;
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
	}
	@Override
	protected void handleSLO(String[] message)
	{
		if (message[1].equals(getPower().getName())) {
			// hooraay I won!!
		} else {
			// I lost.... :(
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
			System.out.println("" + m);
		}
	}
	@Override
	protected void handleTHX(String[] message)
	{
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

	/*	
		Server serv = new Server(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
		try {
			serv.connect();
		} catch (IOException | DisconnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Connect connect = new Connect("DodoAI", "0.0.0.1"); 
			serv.send(connect);
		} catch (UnknownTokenException | DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MapDefinition mapdef = new MapDefinition();
		try {
			serv.send(mapdef);
		} catch (UnknownTokenException | DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] str = {"MAP", "(", "'STANDARD'", ")"};
		Yes yes = new Yes(str);
		try {
			serv.send(yes);
		} catch (UnknownTokenException | DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/
	}
	
	
	public void newTurn()
	{
		ArrayList<Unit> units = map.getUnitsByOwner(this.getPower());
		this.adjacencyList = new ArrayList<ArrayList<Node>>();
		for(int i = 0; i < units.size(); i++)
		{
			this.adjacencyList.add(new ArrayList<Node>());
		}

		findAdjacent();
		
		double d = Math.random();
		
		for(int i = 0; i < units.size(); i++)
			visitedProvinces.add(units.get(i).location.province);
		
		for(int i = 0; i < units.size(); i++)
		{
			d = Math.random();
			if(d <= 1)
				queue.add(this.offensiveMove(i));
			else
				queue.add(this.defensiveMove(i));
		}
		
		if (key_to_send) {
			try {
				System.out.println("Press key to continue.");
				System.in.read(); 
			} catch (IOException e) {
				//Should never happen though...
				e.printStackTrace(); 
			}
		}
		handleQueue();
	}
	
}
