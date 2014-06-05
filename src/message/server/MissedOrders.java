package message.server; 

import message.DaideList;
import message.DaideMessage;

public class MissedOrders implements DaideMessage {

/**
	Message class to request the missed orders
**/
	
	public MissedOrders() {
		/* Nothing to do */
	}

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		result.add("MIS"); 
		return result;
	}

}

