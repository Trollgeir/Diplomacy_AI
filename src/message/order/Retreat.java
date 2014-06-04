package message.order;

import message.DaideList;
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
	public DaideList daide() {
		DaideList ret = new DaideList();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add2(")", "RTO");
		ret.addAll(retreatTo.daide());
		
		return ret;
	}

}
