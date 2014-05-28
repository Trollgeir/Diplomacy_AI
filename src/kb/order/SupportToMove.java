package kb.order;

import kb.Node;
import kb.unit.Unit;

/**
 * The order for a unit to support an adjacent unit with its movement to an adjacent node.
 * @author Koen
 *
 */

public class SupportToMove implements Order {

	Unit		unit, supportedUnit;
	Node		movingTo;
	
	public SupportToMove(Unit unit, Unit supportedUnit, Node movingTo)
	{
		this.unit = unit;
		this.supportedUnit = supportedUnit;
		this.movingTo = movingTo;
	}
	
	@Override
	public String daide() {
		return "(" + unit.daide() + ") SUP (" + supportedUnit.daide() + ") MTO " + movingTo.daide();
	}

}
