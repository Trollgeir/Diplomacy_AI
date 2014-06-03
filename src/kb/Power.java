package kb;

import java.util.ArrayList;

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
	public ArrayList<String> daide()
	{
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(name);
		return ret;
	}
	
}
