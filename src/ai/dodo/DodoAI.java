package ai.dodo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import message.server.Connect;
import message.server.MapDefinition;
import message.server.Yes;
import communication.server.DisconnectedException;
import communication.server.Server;
import communication.server.UnknownTokenException;
import ai.AI;
import game.Game;

public class DodoAI extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */

	public DodoAI() {
		super("DodoAI", "0.0.0.0.1");
	}

	@Override
	public void onMessage(String[] message) {
		
		String[] newMessage = new String[8];
		int j = 0;
		for(int i = 0; i < message.length; i++)
		{
			if(!message[i].equals(")") && !message[i].equals("("))
			{
				newMessage[j] = message[i];
				j++;
			}
		}		
		
		// handle the HLO message
		if (message[0].equals ("HLO")) {
			this.setPower(newMessage[1]);
			this.setPasscode(newMessage[2]);
			this.setLVL(newMessage[4]);
		}
		
		//Handle the SLO message
		if (message[0].equals("SLO")) {
			if (newMessage[1].equals(getPower())) {
				// hooraay I won!!
			} 
		}
		
		// Handle the SMR message
		if (message[0].equals("SMR")){
			System.out.println("\n endgame info: \n");
			for (String m : message) {
				System.out.println("" + m);
			}
		}
		
		// Handle the THX message
		if(message[0].equals("THX")){
			this.setCanMessage(true);
		}
		//
		/*TODO*/
	} 

	public static void main(String[] args) {
		new Game(new DodoAI(), args);

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
	
}
