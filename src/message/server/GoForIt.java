package message.server; 

import message.DaideList;
import message.DaideMessage;

public class GoForIt implements DaideMessage {

/**
	Message class to tell the server that it should go forward with the game after orders are sent
**/
	
	public GoForIt() {
		/* Nothing to do */
	}

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		result.add("GOF"); 
		return result;
	}

}

