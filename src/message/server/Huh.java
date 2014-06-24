package message.server; 

import message.DaideList;
import message.DaideMessage;

public class Huh extends DaideMessage {

	/**
		Message class for messages the AI doesnt understand
	**/

	private String[] message;

	public Huh(String[] message) {
		this.message = message; 
	} 

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		
        result.add2("HUH", "(");
        result.add2(message);
        result.add2(")");

		return result;
	}
}

