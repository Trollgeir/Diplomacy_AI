package message.order;

import java.util.ArrayList;

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
	public ArrayList<String> daide()
	{
		ArrayList<String> ret = new ArrayList<String>();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add(")");
		ret.add("MTO");
		ret.addAll(moveTo.daide());
		
		return ret;
	}
	
}
