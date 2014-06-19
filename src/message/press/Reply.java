package message.press;

import message.DaideList;
import message.DaideMessage;

public abstract class Reply extends DaideMessage {

	public abstract DaideList daide();
	
	public static int pressLevel() {
		return 10;
	}
}
