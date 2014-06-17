package message.order;

import message.DaideList;
import message.DaideMessage;

/**
 * An order, these can be translated to DAIDE message syntax.
 * @author Koen
 *
 */

public abstract class Order extends DaideMessage {

	
	/**
	 * Get the DAIDE message syntax representation of this order.
	 * @return The DAIDE version of this order.
	 */
	public abstract DaideList daide();
}
