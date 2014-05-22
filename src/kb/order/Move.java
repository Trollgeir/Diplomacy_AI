package kb.order;

import kb.*;
import kb.unit.*;

/**
 * "Move" is the order to move a unit to an adjacent node
 * @author Koen
 *
 */

public class Move implements Order {

	Unit 		unit;
	Node 		moveTo;
	
	Move(Unit _unit, Node _moveTo)
	{
		unit = _unit;
		moveTo = _moveTo;
	}
	
	public String translateToDAIDE()
	{
		return "(" + "Unit indicator here!" + ") MTO " + "Province indicator here!";
	}
	
}
