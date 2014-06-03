package kb.order;

import java.util.ArrayList;

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
	public ArrayList<String> daide() {
		ArrayList<String> ret = new ArrayList<String>();
		
		ret.addAll(power.daide());
		ret.add("WVE");
		
		return ret;
	}

}
