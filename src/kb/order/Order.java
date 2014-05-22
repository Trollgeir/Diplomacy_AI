package kb.order;

/**
 * An order, these can be translated to DAIDE message syntax.
 * @author Koen
 *
 */

public interface Order {

	
	/**
	 * Get the DAIDE message syntax representation of this order.
	 * @return The DAIDE version of this order.
	 */
	String daide();
	
}
