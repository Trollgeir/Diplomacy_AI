package kb;

import java.util.ArrayList;

import message.DaideList;
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
	public String			coastName;
	public ArrayList<Node>	landNeighbors, seaNeighbors;
	public Unit				unit;
	
	/**
	 * Create a non-coastal Node.
	 * @param isIn
	 */
	public Node(Province province)
	{
		this.province = province;
		this.coastal = province.isCoast;
		this.coastName = "";
		landNeighbors = new ArrayList<Node>();
		seaNeighbors = new ArrayList<Node>();
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
		this.coastal = province.isCoast;
		this.coastName = coastName;
		landNeighbors = new ArrayList<Node>();
		seaNeighbors = new ArrayList<Node>();
		unit = null;
	}
	
	/**
	 * Get the DAIDE message syntax representation of this node.
	 * @return The DAIDE version of this node.
	 */
	public DaideList daide()
	{
		DaideList ret = new DaideList();
		
		if (coastal)
			ret.add("("); 

		ret.add(province.daide());
		
		if (coastal) {
			ret.add(coastName);
			ret.add(")");
		}
		
		return ret;
	}
	
	public ArrayList<Node> allNeighbors()
	{
		ArrayList<Node> ret = new ArrayList<Node>();
		
		if (isLand()) {
			for (Node n : landNeighbors) {
				if (!n.isCoast()) {
					ret.add(n);
				}
			}
		} else {
			ret.addAll(seaNeighbors);
		}
		
		return ret;
	}
	
	public boolean occupied()
	{
		return unit != null;
	}
	
	
	public boolean isCoast()
	{
		return coastal;
	}
	
	public String coastName()
	{
		return coastName;
	}
	
	public boolean isLand()
	{

		return province.isLand();
	}
	
	public boolean isSea()
	{
		return province.isSea();
		
	}

	public void setUnit(Unit unit) {	
		this.unit = unit;
	}
}
