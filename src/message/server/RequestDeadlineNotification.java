package message.server; 

import message.DaideList;
import message.DaideMessage;

public class RequestDeadlineNotification implements DaideMessage {

	/**
		Message class to request a deadline notification at the requested number of seconds
	**/

	private int seconds;

	public RequestDeadlineNotification(int seconds) {
		this.seconds = seconds; 
	} 

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		
        result.add2("TME");
        result.add2("(",""+seconds, ")");
		return result;
	}
}

