package kb.order;


public interface Order {

	
	/**
	 * Translate this order to DAIDE, allowing the server to process the order.
	 * @return The DAIDE version of this order.
	 */
	String translateToDAIDE();
	
}
