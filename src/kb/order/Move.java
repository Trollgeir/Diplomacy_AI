package kb.order;

import kb.*;
import kb.unit.*;

/**
 * The order to move a unit to an adjacent node.
 * @author Koen
 *
 */

public class Move implements Order {

	Unit 		unit;
	Node 		moveTo;
	
	public Move(Unit unit, Node moveTo)
	{
		this.unit = unit;
		this.moveTo = moveTo;
	}
	
	@Override
	public String daide()
	{
		return "(" + unit.daide() + ") MTO " + moveTo.daide();
	}
	
}
