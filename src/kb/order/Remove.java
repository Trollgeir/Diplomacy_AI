package kb.order;

import java.util.ArrayList;

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
	public ArrayList<String> daide() {
		ArrayList<String> ret = new ArrayList<String>();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add(")");
		ret.add("REM");
		
		return ret;
	}

}
