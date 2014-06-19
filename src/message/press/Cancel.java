package message.press;

import message.DaideList;
import message.DaideMessage;

public abstract class Cancel extends DaideMessage {
	
	Proposal proposal;
	
	public Cancel(Proposal proposal) {
		this.proposal = proposal;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		ret.add2("CCL", "(");
		ret.addAll(proposal.daide());
		ret.add(")");
		
		return ret;
	}
	
	public static int pressLevel() {
		return 10;
	}

}
