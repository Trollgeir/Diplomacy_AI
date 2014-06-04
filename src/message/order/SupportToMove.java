package message.order;

import java.util.ArrayList;

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
	public ArrayList<String> daide() {
ArrayList<String> ret = new ArrayList<String>();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add(")");
		ret.add("SUP");
		ret.add("(");
		ret.addAll(supportedUnit.daide());
		ret.add(")");
		ret.add("MTO");
		ret.addAll(movingTo.daide());
		
		return ret;
	}

}
