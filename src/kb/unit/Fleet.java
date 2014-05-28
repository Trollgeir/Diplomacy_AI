package kb.unit;

import java.util.ArrayList;

import kb.Power;
import kb.Node;
import kb.order.Hold;
import kb.order.Move;
import kb.order.Order;
import kb.order.SupportToHold;
import kb.order.SupportToMove;

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
