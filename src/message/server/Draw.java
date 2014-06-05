/*
 * Says we accept a draw at the current time
 */

package message.server;

import message.DaideList;
import message.DaideMessage;

public class Draw implements DaideMessage{

	public Draw()
	{
		// Again no parameters
	}

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		result.add("DRW");
		return result;
	}
}
