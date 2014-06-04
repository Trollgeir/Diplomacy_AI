package message.order;

import message.DaideList;
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
	public DaideList daide() {
		DaideList ret = new DaideList();
		
		ret.addAll(power.daide());
		ret.add("WVE");
		
		return ret;
	}

}
