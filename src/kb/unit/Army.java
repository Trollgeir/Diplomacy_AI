package kb.unit;

import kb.Power;
import kb.Node;


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

}
