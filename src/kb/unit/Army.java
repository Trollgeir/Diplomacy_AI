package kb.unit;

import java.util.ArrayList;

import kb.Power;
import kb.Node;


/**
 * An army, these can only move on land nodes.
 * @author Koen
 *
 */
public class Army extends Unit {

	public Army(Power owner, Node location) {
		super(owner, location);
	}

	@Override
	public ArrayList<String> daide() {
		ArrayList<String> ret = owner.daide();
		ret.add("AMY");
		ret.addAll(location.daide());
		
		return ret; 
	}

	@Override
	public boolean canMoveOn(Node node)
	{
		return node.isLand();
	}
	
}
