package message.server; 

import message.DaideList;
import message.DaideMessage;

public class Reject implements DaideMessage {

	/*
		Message class to reject
	*/

	private String[] message; 

	public Reject(String[] message) {
		this.message = message; 
	} 

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		
        result.add2("REJ", "(");
        result.add2(message);
        result.add2(")");

		return result;
	}
}

