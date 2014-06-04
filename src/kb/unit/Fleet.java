package kb.unit;

import java.util.ArrayList;

import message.DaideList;
import kb.Power;
import kb.Node;

/**
 * A fleet, these can move through sea provinces and coast nodes.
 * @author Koen
 *
 */

public class Fleet extends Unit {

	Fleet(Power owner, Node location) {
		super(owner, location);
	}

	@Override
	public DaideList daide() {
		DaideList ret = owner.daide();
		
		ret.add("FLT");
		ret.addAll(location.daide());
		
		return ret;  
	}
	
	@Override
	public boolean canMoveOn(Node node)
	{
		return node.isSea() || node.isCoast();
	}
	
}
