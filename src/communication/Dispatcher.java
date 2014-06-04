package communication; 

import communication.server.MessageListener;
import ai.AI; 
import kb.Map; 

public class Dispatcher implements MessageReceiver {
	
	private Server server; 
	public AI ai; 
	public Map map; 


	public Dispatcher(Server server) {
		this.server = server; 
	}

	@Override
	public void messageReceived(String[] message) {
		/*
			send message to AI or map depending on layout
		*/
	}

};