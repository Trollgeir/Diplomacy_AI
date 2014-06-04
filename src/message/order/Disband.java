package message.order;

import java.util.ArrayList;

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
	public ArrayList<String> daide() {
ArrayList<String> ret = new ArrayList<String>();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add(")");
		ret.add("DSB");
		
		return ret;
	}

}
