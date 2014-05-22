package kb;

/**
 * A province. Provinces contain nodes, which is where the units will actually walk around.
 * @author Koen
 *
 */

public abstract class Province {
	
	String 		name;
	Node		centralNode;
	
	/**
	 * Get the DAIDE message syntax representation of this province.
	 * @return The DAIDE version of this province.
	 */
	String daide()
	{
		return name;
	}

}
