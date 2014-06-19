package message.press;

import kb.Power;
import message.DaideList;

public class Alliance extends Arrangement {
	
	Power[] power;
	Power[] against;
	
	public Alliance(Power[] power, Power[] against) {
		this.power = power;
		this.against = against;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		ret.add2("ALY", "(");
		for (int i = 0; i < power.length; i++)
			ret.add(power[i].getName());
		
		ret.add2(")","VSS","(");
		for (int i = 0; i < against.length; i++)
			ret.add(against[i].getName());		
		
		ret.add(")");
		
		return ret;
	}

}
