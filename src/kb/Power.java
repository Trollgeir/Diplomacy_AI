package kb;

import message.DaideList;
import message.DaideMessage;

/**
 * One of the powers (players) in the game.
 * @author Koen
 *
 */

public class Power implements DaideMessage{

	String		name;
	
	Power(String name)
	{
		this.name = name;
	}
	
	/**
	 * Get the DAIDE message syntax representation of this power.
	 * @return The DAIDE version of this power.
	 */
	public DaideList daide()
	{
		DaideList ret = new DaideList();
		ret.add(name);
		return ret;
	}
	
}
