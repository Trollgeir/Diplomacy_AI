package dodo;

import java.net.InetAddress;
import java.net.UnknownHostException;

import communication.server.DisconnectedException;
import communication.server.Server;
import communication.server.UnknownTokenException;
import ai.AI;

public class DodoAI extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */

	public static void main(String[] args) throws UnknownHostException {
			
		Server serv = new Server(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
		try {
			serv.send(args);
		} catch (UnknownTokenException | DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
