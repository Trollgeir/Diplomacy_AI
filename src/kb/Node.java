package kb;

import java.util.ArrayList;

import kb.province.Province;
import kb.unit.Unit;

/**
 * This is where the units will actually move around. 
 * Nodes & Provinces are separate classes because coastal provinces can have more than one node.
 * @author Koen
 *
 */

public class Node {

	public Province			province;
	boolean					coastal;
	String					coastName;
	public ArrayList<Node>	neighbors;
	public Unit				unit;
	
	/**
	 * Create a non-coastal Node.
	 * @param isIn
	 */
	Node(Province province)
	{
		this.province = province;
		this.coastal = false;
		this.coastName = "";
		unit = null;
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
		unit = null;
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
	
	public boolean occupied()
	{
		return unit != null;
	}
	
	
	public boolean isCoast()
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
