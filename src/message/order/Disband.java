package message.order;

import kb.unit.Unit;
import message.DaideList;

/**
 * The order to disband a unit during the Retreat phase.
 * @author Koen
 *
 */

public class Disband implements Order {

	Unit 		unit;
	
	public Disband(Unit unit)
	{
		this.unit = unit;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add2(")", "DSB");
		
		return ret;
	}

}
