/*
 * Says we accept a draw at the current time
 */

package message.server;

import message.DaideList;
import message.DaideMessage;
import message.order.Server;
import message.server.TurnHistory;

public class Submit implements DaideMessage{

	Order[] orders = null;
	TurnHistory turn  = null; 


	public Submit(Order ... orders) {
		this.orders = orders;
	}

	public Submit(TurnHistory turn, Order ... orders) {
		this.turn = turn;
		this.orders = orders; 
	}
	
	@Override
	public DaideList daide() {
		DaideList result = new DaideList();

		if (turn != null) {
			result.add2("(", turn, ")");
		} 

		if (orders != null) {
			for (Order o : orders) {
				result.add2("(", o, ")");
			}
		}

		return result;
	}
}

