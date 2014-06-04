package message.order;

import message.DaideList;
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
	public DaideList daide()
	{
		DaideList ret = new DaideList();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add2(")", "MTO");
		ret.addAll(moveTo.daide());
		
		return ret;
	}
	
}
