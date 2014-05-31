package kb.province;

/**
 * Inland province. These only have a central node and are only accessible by armies.
 * @author Koen
 *
 */

public class Land extends Province {

	public Land(String name, boolean hasSupply)
	{
		super(name, hasSupply);
	}
	

	@Override
	public boolean isLand() 
	{
		return true;
	}
}
