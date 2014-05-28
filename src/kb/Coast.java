package kb;

import java.util.ArrayList;

import kb.unit.Unit;

/**
 * Coastal province, these have a coastline that consists of nodes as well 
 * as the regular central node that all other provinces have.
 * @author Koen
 *
 */

public class Coast extends Land{

	ArrayList<Node>		coastLine;
	
	@Override
	public Unit unit()
	{
		if (centralNode.unit != null)
		{
			return centralNode.unit;
		}
		
		for (int i = 0; i < coastLine.size(); i++)
		{
			if (coastLine.get(i) != null)
			{
				return coastLine.get(i).unit;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean isLand() 
	{
		return false;
	}
	
	@Override
	public boolean isCoast() 
	{
		return true;
	}
}
