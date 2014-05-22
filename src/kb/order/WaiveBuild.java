package kb.order;

import kb.Power;

/**
 * The order for a power to decide to build nothing in this turn.
 * @author Koen
 *
 */

public class WaiveBuild implements Order {

	Power		power;
	
	WaiveBuild(Power power)
	{
		this.power = power;
	}
	
	@Override
	public String daide() {
		return power.daide() + " WVE";
	}

}
