package message.server; 

import kb.Power;
import message.DaideList;
import message.DaideMessage;

public class Reconnect extends DaideMessage {

	/**
		Message class to ask the server to rejoin the game after connection loss (IAM)
	**/

	private Power power;
	private String passcode;

	public Reconnect(Power power, String passcode) {
		this.power = power; 
		this.passcode = passcode;
	} 

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		
        result.add2("IAM","(");
        result.addAll(power.daide());
        result.add2(")");
        result.add2("(",passcode, ")");

		return result;
	}
}

