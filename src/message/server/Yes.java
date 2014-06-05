package message.server; 

import message.DaideList;
import message.DaideMessage;

public class Yes implements DaideMessage {

	/**
		Message class to confirm messages
	**/

	private String message;

	public Yes(String message) {
		this.message = message; 
	} 

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		
        result.add2("YES");
        result.add2("(", message, ")");

		return result;
	}
}

