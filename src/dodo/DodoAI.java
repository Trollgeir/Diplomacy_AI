package dodo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import communication.Sender; 
import message.Connect;

import communication.server.DisconnectedException;
import communication.server.Server;
import communication.server.UnknownTokenException;
import ai.AI;

public class DodoAI extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */

	public static void main(String[] args) throws UnknownHostException {
			
		Server serv = new Server(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
		try {
			serv.connect();
		} catch (IOException | DisconnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Connect connect = new Connect("DodoAI", "0.0.0.1"); 
			serv.send(Sender.toDaide(connect));
		} catch (UnknownTokenException | DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
