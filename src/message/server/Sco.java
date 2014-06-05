/*
 * Asks the server for Supply Centre Ownership
 */

package message.server;

import message.DaideList;
import message.DaideMessage;

public class Sco implements DaideMessage {
	public Sco()
	{
		// No parameters are needed
	}
	
	@Override
	public DaideList daide()
	{
		DaideList result = new DaideList();
		result.add("SCO");
		return result;
	}
}