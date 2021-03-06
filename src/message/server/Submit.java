/*
 * Says we accept a draw at the current time
 */

package message.server;

import message.DaideList;
import message.DaideMessage;
import message.order.Order;

public class Submit extends DaideMessage{

	Order[] orders = null;


	public Submit(Order ... orders) {
		this.orders = orders;
	}

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();

		result.add("SUB");
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

