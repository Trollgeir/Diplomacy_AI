package kb.order;

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
	public String daide() {
		return "(" + unit.daide() + ") SUP (" + supportedUnit.daide() + ")";
	}

}
