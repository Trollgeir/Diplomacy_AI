package kb.order;

import kb.unit.Unit;

/**
 * The order for a unit to defend their current location.
 * @author Koen
 *
 */

public class Hold implements Order {

	Unit 		unit;
	
	Hold(Unit unit)
	{
		this.unit = unit;
	}
	
	@Override
	public String daide() {
		return "(" + unit.daide() + ") HLD"; 
	}

}
