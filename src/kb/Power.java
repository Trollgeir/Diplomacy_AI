package kb;

/**
 * One of the powers (players) in the game.
 * @author Koen
 *
 */

public class Power {

	String		name;
	
	Power(String name)
	{
		this.name = name;
	}
	
	/**
	 * Get the DAIDE message syntax representation of this power.
	 * @return The DAIDE version of this power.
	 */
	public String daide()
	{
		return name;
	}
	
}
