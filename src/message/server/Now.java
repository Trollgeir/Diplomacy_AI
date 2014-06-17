/*
 * Ask the server for the starting positions for the "variant"
 */

package message.server;

import message.DaideList;
import message.DaideMessage;

public class Now extends DaideMessage {
	public Now()
	{
		// No parameters are needed
	}
	
	@Override
	public DaideList daide()
	{
		DaideList result = new DaideList();
		result.add("NOW");
		return result;
	}
}