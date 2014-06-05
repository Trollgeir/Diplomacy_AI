/*
 * Says we accept a draw at the current time
 */

package message.server;

import message.DaideList;
import message.DaideMessage;

public class Draw implements DaideMessage{

	String[] powers = null;
	public Draw()
	{
		// Again no parameters
	}

	public Draw(String[] powers)
	{
		// Partial Draw Allowed?
		// you can declare a draw with certain powers
		this.powers = powers;
	}
	
	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		result.add("DRW");
		if(this.powers != null)
		{
			result.add("(");
			result.add2(this.powers);
			result.add(")");
		}			
		return result;
	}
}
