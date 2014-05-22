package kb.order;

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
	public String daide() {
		return "(" + unit.daide() + ") RTO " + retreatTo.daide();
	}

}
