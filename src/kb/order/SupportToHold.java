package kb.order;

import java.util.ArrayList;

import kb.unit.Unit;

/**
 * The order for a unit to support an adjacent unit in defending its current location.
 * @author Koen
 *
 */

public class SupportToHold implements Order {

	Unit		unit, supportedUnit;
	
	public SupportToHold(Unit unit, Unit supportedUnit)
	{
		this.unit = unit;
		this.supportedUnit = supportedUnit;
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
		
		return ret;
	}

}
