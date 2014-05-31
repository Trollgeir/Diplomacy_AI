package kb.province;

import kb.Node;
import kb.unit.Unit;

/**
 * A province. Provinces contain nodes, which is where the units will actually walk around.
 * @author Koen
 *
 */

public abstract class Province {
	
	String 			name;
	Node			centralNode;
	boolean			supplyCenter;
	
	Province(String name, boolean hasSupply)
	{
		this.name = name;
		this.supplyCenter = hasSupply;
	}
	
	
	public String name()
	{
		return name;
	}
	
	public Node getNode()
	{
		return centralNode;
	}
	
	public Node getNode(String coastName)
	{
		return null;
	}
	
	public boolean occupied()
	{
		return unit() != null;
	}
	
	public Unit unit()
	{
		return centralNode.unit;
	}
	
	/**
	 * Get the DAIDE message syntax representation of this province.
	 * @return The DAIDE version of this province.
	 */
	public String daide()
	{
		return name;
	}
	
	/**
	 * 
	 * @return Whether or not an army can walk on this Province
	 */
	public boolean armyAccess()
	{
		return isLand() || isCoast();
	}
	/**
	 * 
	 * @return Whether or not a fleet can move on this Province
	 */
	public boolean fleetAccess()
	{
		return isSea() || isCoast();
	}

	
	public boolean isLand() 
	{
		return false;
	}
	public boolean isSea() 
	{
		return false;
	}
	public boolean isCoast() 
	{
		return false;
	}
}
