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

public class Game {

	AI ai; 

	public Game(AI ai, String ip, String port) {
		this.ai = ai;
		try {
			Server serv = new Server(InetAddress.getByName(ip), Integer.parseInt(port));
			serv.connect();
			Connect connect = new Connect(ai.getName(), ai.getVersion()); 
			serv.send(connect);
			//TODO everything below here is a hack
			MapDefinition mapdef = new MapDefinition();
			serv.send(mapdef);
			String[] str = {"MAP", "(", "'STANDARD'", ")"};
			Yes yes = new Yes(str);
			serv.send(yes);
		} catch (IOException | DisconnectedException | UnknownTokenException e) {
			/*TODO handle exceptions*/ 
			e.printStackTrace();
		}

	}

}