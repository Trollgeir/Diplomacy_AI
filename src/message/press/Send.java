package message.press;

import kb.Power;
import message.DaideList;
import message.DaideMessage;

public class Send implements DaideMessage {

	Power[] recipients;
	DaideMessage message;
	
	public Send(DaideMessage message, Power ... recipients)
	{
		this.message = message;
		this.recipients = recipients;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		ret.add2("SND", "(");
		for (Power p : recipients) ret.add(p.getName());
		ret.add2(")", "(");
		ret.addAll(message.daide());
		ret.add(")");
		
		return ret;
	}

}
