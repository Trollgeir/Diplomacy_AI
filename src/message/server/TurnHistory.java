package message.server; 

import message.DaideList;
import message.DaideMessage;

public class TurnHistory extends DaideMessage {

	/**
		Message class to retrieve the results from any previous turn
	**/

	private String turn;

	public TurnHistory(String turn) {
		this.turn = turn; 
	} 

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		
        result.add2("HST");
        result.add2("(",turn, ")");
		return result;
	}
}

