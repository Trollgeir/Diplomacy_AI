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

	//@Override
	public void messageReceived(String[] message) {
		/*System.out.println("Message received:\n"); 
		for (String s : message) {
			System.out.print(s);
			System.out.print(" "); 
		}	
		System.out.println("\n");*/
		/*
			TODO: send message to AI or map depending on layout
		*/
		if (forMap(message)) {
			map.onMessage(message); 
		}
		if (forGame(message)) {
			game.onMessage(message); 
		} 
		if (forAI(message)) {
			ai.onMessage(message);
		}
		if (!forMap(message) && !forAI(message) && !forGame(message)) {
			System.out.println("Unhandled message: " + message[0]); 
		}
	}

	public boolean forMap(String[] message) {
		return in(message[0], "MDF","NOW","SCO","ORD","OUT", "MAP");
	}

	public boolean forGame(String[] message) {
		return in(message[0], "MIS","PRN", "OFF");
	}

	public boolean forAI(String[] message) {
		return in(message[0], "HLO","FRM","SLO","SMR","THX","YES","REJ","HUH");
	}


	private boolean in(String a, String ... b) {
		for (String bb : b)
			if (a.equalsIgnoreCase(bb)) return true;
		return false;
	}

};