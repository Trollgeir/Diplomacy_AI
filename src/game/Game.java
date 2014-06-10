package game; 

import ai.AI;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import message.server.Connect;
import message.server.MapDefinition;
import message.server.Yes;
import communication.server.DisconnectedException;
import communication.server.Server;
import communication.server.UnknownTokenException;

import kb.Map;

public class Game extends Receiver {

	AI ai; 
	Map map; 
	Dispatcher dispatcher;


	public static void printUsage(AI ai) {
		System.out.println("usage: " + ai.getClass().getName() + " [ip] [port] " + ai.getUsage()); 
	}

	public Game(AI ai, String[] args) {
		this.ai = ai;
		this.map = new Map(); 
		this.dispatcher = new Dispatcher(ai, map, this); 

		try {
			ai.init(args); 
			String name = args[0];
			String port = args[1];
			Server serv = new Server(InetAddress.getByName(name), Integer.parseInt(port));
			serv.addMessageListener(dispatcher); 
			serv.connect();
			Connect connect = new Connect(ai.getName(), ai.getVersion()); 
			serv.send(connect);
			//TODO everything below here is a hack
			MapDefinition mapdef = new MapDefinition();
			serv.send(mapdef);
			String[] str = {"MAP", "(", "'STANDARD'", ")"};
			Yes yes = new Yes(str);
			serv.send(yes);
		} catch (ArrayIndexOutOfBoundsException e) {
			printUsage(ai);
			System.exit(-1);
		} catch (IOException | DisconnectedException | UnknownTokenException e) {
			/*TODO handle exceptions*/ 
			e.printStackTrace();
		} 

	}

	@Override
	public void onMessage(String[] message) {};
}