package message.order;

import message.DaideList;
import kb.Node;
import kb.unit.Unit;

/**
 * The order for a unit to support an adjacent unit with its movement to an adjacent node.
 * @author Koen
 *
 */

public class SupportToMove extends Order {

	Unit		unit, supportedUnit;
	Node		movingTo;
	
	public SupportToMove(Unit unit, Unit supportedUnit, Node movingTo)
	{
		this.unit = unit;
		this.supportedUnit = supportedUnit;
		this.movingTo = movingTo;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add2(")", "SUP", "(");
		ret.addAll(supportedUnit.daide());
		ret.add2(")", "MTO");
		ret.addAll(movingTo.daide());
		
		return ret;
	}

}
