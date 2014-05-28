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
 * An army, these can only move on land nodes.
 * @author Koen
 *
 */
public class Army extends Unit {

	Army(Power owner, Node location) {
		super(owner, location);
	}

	@Override
	public String daide() {
		return owner.daide() + " AMY " + location.daide(); 
	}

	@Override
	public boolean canMoveOn(Node node)
	{
		return node.equals(node.isLand());
	}
	
}
