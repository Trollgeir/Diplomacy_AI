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
	public static Server server; 


	public static void printUsage(AI ai) {
		System.out.println("usage: " + ai.getClass().getName() + " [ip] [port] " + ai.getUsage()); 
	}

	public Game(AI ai, Map map, String[] args) {
		this.ai = ai;
		this.map = map; 
		this.dispatcher = new Dispatcher(ai, map, this); 

		try {
			ai.init(args); 
			System.out.println("namee" + ai.getName()); 
			String name = args[0];
			String port = args[1];
			server = new Server(InetAddress.getByName(name), Integer.parseInt(port));
			server.addMessageListener(dispatcher); 
			server.connect();
			System.out.println("connecting as" + ai.getName()); 
			Connect connect = new Connect(ai.getName(), ai.getVersion()); 
			server.send(connect);
		} catch (ArrayIndexOutOfBoundsException e) {
			printUsage(ai);
			System.exit(-1);
		} catch (IOException e) {
			/*TODO handle exceptions*/ 
			e.printStackTrace();
		} catch (DisconnectedException e) {
			/*TODO handle exceptions*/ 
			e.printStackTrace();
		} 

	}

	@Override
	public void onMessage(String[] message) {
		if (message[0].equals("OFF")) {
			System.out.println("Server was manually terminated."); 
			System.exit(0); 
		}
		
	};
}