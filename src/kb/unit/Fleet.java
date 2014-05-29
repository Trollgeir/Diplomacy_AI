package kb.unit;

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
	public String daide() {
		return owner.daide() + " FLT " + location.daide(); 
	}
	
	@Override
	public boolean canMoveOn(Node node)
	{
		return node.equals(node.isSea() || node.isCoast());
	}
	
}
