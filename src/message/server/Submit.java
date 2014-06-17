/*
 * Says we accept a draw at the current time
 */

package message.server;

import message.DaideList;
import message.DaideMessage;
import message.order.Order;
import message.server.TurnHistory;

public class Submit extends DaideMessage{

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

		result.add("SUB");
		
		if (turn != null) {
			result.add("(");
			result.addAll(turn.daide());
			result.add(")");
		} 

		if (orders != null) {
			for (Order o : orders) {
				result.add("(");
				result.addAll(o.daide());
				result.add(")");
			}
		}

		return result;
	}
}

