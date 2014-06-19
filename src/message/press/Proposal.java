package message.press;

import message.DaideList;
import message.DaideMessage;
import message.press.Arrangement;

/**
 * An proposal, works like the press_message variable in the DAIDE syntax
 * @autho Thijs
 *
 */

public abstract class Proposal extends DaideMessage{
	
	Arrangement arrangement;
	
	public Proposal(Arrangement arrangement) {
		this.arrangement = arrangement;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		ret.add2("PRP", "(");
		ret.addAll(arrangement.daide());
		ret.add(")");
		
		return ret;
	}
	
	public static int pressLevel() {
		return 10;
	}
}
