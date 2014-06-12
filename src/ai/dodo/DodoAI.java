package ai.dodo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import kb.Map;
import kb.unit.Unit;
import negotiator.Negotiator;
import message.order.Hold;
import message.order.Order;
import message.server.Connect;
import message.server.MapDefinition;
import message.server.Submit;
import message.server.Yes;
import communication.server.DisconnectedException;
import communication.server.Server;
import communication.server.UnknownTokenException;
import ai.AI;
import game.Game;
import communication.LogReader;

public class DodoAI extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */

	String logpath = null; 

	public DodoAI(Map map) {
		super("DodoAI", "0.0.0.0.1", map);
	}


	
	@Override
	protected void handleHLO(String[] message)
	{
		this.setPower(map.getPower(message[1]));
		this.setPasscode(message[2]);
		this.setLVL(message[4]);
		if (logpath != null) {
			new LogReader().readLog(logpath);
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
		if (args.length < 3) return; 
		setName(args[2]);
		if (args.length < 4) return;
		logpath = args[3];
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
		ArrayList<Unit> units = map.powerUnits(getPower());
		
		Order[] oList = new Order[units.size()];  
		
		for (int i = 0; i < units.size(); i++)
		{
			oList[i] = new Hold(units.get(i));
		}
		
		Game.server.send(new Submit(oList));
	}
	
}
