package message.order;

import message.DaideList;
import kb.unit.Unit;

/**
 * The order for a unit to defend their current location.
 * @author Koen
 *
 */

public class Hold extends Order {

	Unit 		unit;
	
	public Hold(Unit unit)
	{
		this.unit = unit;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add2(")", "HLD");
		
		return ret;
	}

}
