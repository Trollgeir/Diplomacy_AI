package message.server; 

import message.DaideList;
import message.DaideMessage;

public class REJ implements DaideMessage {

	/*
		Message class to reject
	*/

	private String[] messsage; 

	public Connect(String name) {
		this.name = name; 
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

