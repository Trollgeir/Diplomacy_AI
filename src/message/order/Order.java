package message.order;

import java.util.ArrayList;

import kb.DaideMessage;

/**
 * An order, these can be translated to DAIDE message syntax.
 * @author Koen
 *
 */

public interface Order extends DaideMessage {

	
	/**
	 * Get the DAIDE message syntax representation of this order.
	 * @return The DAIDE version of this order.
	 */
	ArrayList<String> daide();
	
}
