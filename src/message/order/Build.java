package message.order;

import message.DaideList;
import kb.unit.Unit;

/**
 * The order to disband a unit during the Build phase.
 * @author Koen
 *
 */

public class Build implements Order {

	Unit		unit;
	
	public Build(Unit unit)
	{
		this.unit = unit;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add2(")", "BLD");
		
		return ret;
	}

}
