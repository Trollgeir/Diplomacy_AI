package message; 

import communication.server.MessageListener;
import ai.AI; 
import kb.Map; 

public class Dispatcher implements MessageListener {

	public AI ai; 
	public Map map; 


	public Dispatcher(AI ai, Map map) {
		this.ai = ai; 
		this.map = map; 
	}	

	@Override
	public void messageReceived(String[] message) {
		/*
			TODO: send message to AI or map depending on layout
		*/
		if (forAI) {
			ai.onMessage(message); 
		} else {
			map.onMessage(message); 
		}
	}

};