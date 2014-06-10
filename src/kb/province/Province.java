package kb.province;

import java.util.ArrayList;

import kb.Node;
import kb.Power;
import kb.unit.Unit;

/**
 * A province. Provinces contain nodes, which is where the units will actually walk around.
 * @author Koen
 *
 */

public class Province {
	
	String 				name;
	Node				centralNode;
	boolean				supplyCenter;
	
	public ArrayList<Node>		coastLine;
	boolean				isLand, isSea, isCoast;
	
	public Province(String name, boolean hasSupply)
	{
		this.name = name;
		this.supplyCenter = hasSupply;
		centralNode = new Node(this);
		coastLine = new ArrayList<Node>();
	}
	
	public void addCoastalNode(Node n)
	{
		coastLine.add(n);
	}
	
	public void setIsLand(boolean l)
	{
		isLand = l;
	}
	public void setIsSea(boolean l)
	{
		isSea = l;
	}
	public void setIsCoast(boolean l)
	{
		isCoast = l;
	}
	
	public int coastAmt()
	{
		return coastLine.size();
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public Node getCentralNode()
	{
		return centralNode;
	}
	public Node getCoastNode(String name)
	{
		for (int i = 0; i < coastLine.size(); i++)
		{
			if (coastLine.get(i).coastName().equals(name))
			{
				return coastLine.get(i);
			}
		}
		return null;
	}
	
	public boolean occupied()
	{
		return unit() != null;
	}
	
	public Unit unit()
	{
		if (!isCoast)
		{
			return centralNode.unit;
		}
		else
		{
			for (int i = 0; i < coastLine.size(); i++)
			{
				if (coastLine.get(i).unit != null)
				{
					return coastLine.get(i).unit;
				}
			}
		}
		return null;
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
		return isLand;
	}
	public boolean isSea() 
	{
		return isSea;
	}
	public boolean isCoast() 
	{
		return isCoast;
	}
}
