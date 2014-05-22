package kb.order;

import kb.unit.Unit;

/**
 * The order to disband a unit during the Build phase.
 * @author Koen
 *
 */

public class Remove implements Order {

	Unit		unit;
	
	Remove(Unit unit)
	{
		this.unit = unit;
	}
	
	@Override
	public String daide() {
		return "(" + unit.daide() + ") REM";
	}

}
