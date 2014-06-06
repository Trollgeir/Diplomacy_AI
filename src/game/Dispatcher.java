package game; 

import communication.server.MessageListener;
import ai.AI; 
import kb.Map; 

public class Dispatcher implements MessageListener {

	public AI ai; 
	public Map map; 
	public Game game; 


	public Dispatcher(AI ai, Map map, Game game) {
		this.ai = ai; 
		this.map = map; 
		this.game = game; 
	}	

	@Override
	public void messageReceived(String[] message) {
		System.out.println("Message received:\n"); 
		for (String s : message) {
			System.out.print(s);
			System.out.print(" "); 
		}	
		System.out.println("\n");
		/*
			TODO: send message to AI or map depending on layout
		*/
		if (forMap(message)) {
			map.onMessage(message); 
			System.out.println("This message is for map."); 
		} else if (forGame(message)) {
			game.onMessage(message); 
		} else {
			ai.onMessage(message);
		}
	}

	public boolean forMap(String[] message) {
		return in(message[0], "MDF");
	}

	public boolean forGame(String[] message) {
		return true;
	}


	private boolean in(String a, String ... b) {
		for (String bb : b)
			if (a.equalsIgnoreCase(bb)) return true;
		return false;
	}

};