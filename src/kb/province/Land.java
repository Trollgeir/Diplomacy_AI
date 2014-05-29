package kb.province;

/**
 * Inland province. These only have a central node and are only accessible by armies.
 * @author Koen
 *
 */

public class Land extends Province {

	@Override
	public boolean isLand() 
	{
		return true;
	}
}
