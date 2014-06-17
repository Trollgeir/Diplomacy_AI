package message.press;

import kb.Power;
import message.DaideList;

public class Peace extends Arrangement {
	
	Power[] power;
	
	public Peace(Power[] power) {
		this.power = power;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		ret.add2("PCE", "(");
		for (int i = 0; i < power.length; i++)
			ret.add(power[i].getName());
		ret.add(")");
		
		return ret;
	}

}
