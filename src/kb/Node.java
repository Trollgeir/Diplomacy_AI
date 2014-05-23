package kb;

/**
 * This is where the units will actually move around. 
 * Nodes & Provinces are separate classes because coastal provinces can have more than one node.
 * @author Koen
 *
 */

public class Node {

	Province		province;
	boolean			coastal;
	String			coastName;
	
	/**
	 * Create a non-coastal Node.
	 * @param isIn
	 */
	Node(Province province)
	{
		this.province = province;
		this.coastal = false;
		this.coastName = "";
	}
	
	/**
	 * Create a coastal Node.
	 * @param isIn
	 * @param coastName
	 */
	Node(Province province, String coastName)
	{
		this.province = province;
		this.coastal = true;
		this.coastName = coastName;
	}
	
	/**
	 * Get the DAIDE message syntax representation of this node.
	 * @return The DAIDE version of this node.
	 */
	public String daide()
	{
		if (!coastal)
			return province.daide();
		else
			return province.daide() + " " + coastName;
	}
	
	
	public boolean isCoastal()
	{
		return coastal;
	}
	
	public boolean isLand()
	{
		if (!coastal)
			return province.isLand();
		return false;
	}
	
	public boolean isSea()
	{
		if (!coastal)
			return province.isSea();
		return false;
	}
}
