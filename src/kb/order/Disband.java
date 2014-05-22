package kb.order;

import kb.unit.Unit;

/**
 * The order to disband a unit during the Retreat phase.
 * @author Koen
 *
 */

public class Disband implements Order {

	Unit 		unit;
	
	Disband(Unit unit)
	{
		this.unit = unit;
	}
	
	@Override
	public String daide() {
		return "(" + unit.daide() + ") DSB";
	}

}
