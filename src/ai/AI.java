package ai;

import java.net.InetAddress;
import java.net.UnknownHostException;

import communication.server.DisconnectedException;
import communication.server.Server;
import communication.server.UnknownTokenException;

public abstract class AI {

	public static void main(String[] args) throws UnknownHostException {
			
		Server serv = new Server(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
		try {
			serv.send(args);
		} catch (UnknownTokenException | DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onMessage(String[] message) {		
	}

}
