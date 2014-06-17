package message.order;

import message.DaideList;
import kb.unit.Unit;

/**
 * The order for a unit to support an adjacent unit in defending its current location.
 * @author Koen
 *
 */

public class SupportToHold extends Order {

	Unit		unit, supportedUnit;
	
	public SupportToHold(Unit unit, Unit supportedUnit)
	{
		this.unit = unit;
		this.supportedUnit = supportedUnit;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add2(")", "SUP", "(");
		ret.addAll(supportedUnit.daide());
		ret.add(")");
		
		return ret;
	}

}
