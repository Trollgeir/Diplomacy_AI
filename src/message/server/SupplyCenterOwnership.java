/*
 * Asks the server for Supply Center Ownership
 */

package message.server;

import message.DaideList;
import message.DaideMessage;

public class SupplyCenterOwnership implements DaideMessage {
	public SupplyCenterOwnership()
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