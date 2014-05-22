package kb;

/**
 * This is where the units will actually move around. 
 * Nodes & Provinces are separate classes because coastal provinces can have more than one node.
 * @author Koen
 *
 */

public class Node {

	String 		name;
	
	/**
	 * Get the DAIDE message syntax representation of this node.
	 * @return The DAIDE version of this node.
	 */
	public String daide()
	{
		return name;
	}
	
}
