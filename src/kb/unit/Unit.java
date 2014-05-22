package kb.unit;

import kb.Node;
import kb.Power;

/**
 * A unit on the board
 * @author Koen
 *
 */
public abstract class Unit {

	Power		owner;
	Node		location;
	
	
	Unit(Power owner, Node location)
	{
		this.owner = owner;
		this.location = location;
	}
	
	public abstract String daide();
	
}
