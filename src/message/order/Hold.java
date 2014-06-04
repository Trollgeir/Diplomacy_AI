package message.order;

import java.util.ArrayList;

import kb.unit.Unit;

/**
 * The order for a unit to defend their current location.
 * @author Koen
 *
 */

public class Hold implements Order {

	Unit 		unit;
	
	public Hold(Unit unit)
	{
		this.unit = unit;
	}
	
	@Override
	public ArrayList<String> daide() {
		ArrayList<String> ret = new ArrayList<String>();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add(")");
		ret.add("HLD");
		
		return ret;
	}

}
