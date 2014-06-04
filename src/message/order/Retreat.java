package message.order;

import java.util.ArrayList;

import kb.Node;
import kb.unit.Unit;

/**
 * The order for a unit to retreat to a neighboring province.
 * @author Koen
 *
 */

public class Retreat implements Order {

	Unit 		unit;
	Node		retreatTo;
	
	Retreat(Unit unit, Node retreatTo)
	{
		this.unit = unit;
		this.retreatTo = retreatTo;
	}
	
	@Override
	public ArrayList<String> daide() {
		ArrayList<String> ret = new ArrayList<String>();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add(")");
		ret.add("RTO");
		ret.addAll(retreatTo.daide());
		
		return ret;
	}

}
