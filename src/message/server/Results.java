package message.server;

import message.DaideList;
import message.DaideMessage;
import message.order.Order;

public class Results extends DaideMessage{

	Order[] orders = null;


	public Results(Order ... orders) {
		this.orders = orders;
	}

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();

		result.add("ORD");
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
