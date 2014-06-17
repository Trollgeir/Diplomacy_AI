package message.server; 

import message.DaideList;
import message.DaideMessage;

public class Not extends DaideMessage {

	/**
		Message class to un-confirm messages
	**/

	private String[] message;

	public Not(String[] message) {
		this.message = message; 
	} 

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		
        result.add2("NOT", "(");
        result.add2(message);
        result.add2(")");

		return result;
	}
}

