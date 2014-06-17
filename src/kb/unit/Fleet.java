package kb.unit;

import message.DaideList;
import kb.Power;
import kb.Node;

/**
 * A fleet, these can move through sea provinces and coast nodes.
 * @author Koen
 *
 */

public class Fleet extends Unit {

	public Fleet(Power owner, Node location) {
		super(owner, location);
	}

	public boolean isArmy()
	{
		return false;
	}
	public boolean isFleet()
	{
		return true;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = owner.daide();
		
		ret.add("FLT");
		/*
		if (!location.coastName().equals(""))
			ret.add("(");
		*/
		ret.addAll(location.daide());
		/*
		if (!location.coastName().equals(""))
			ret.add(")");
		*/
		return ret;  
	}
	
	@Override
	public boolean canMoveOn(Node node)
	{
		return node.isSea() || node.isCoast();
	}
	
}
