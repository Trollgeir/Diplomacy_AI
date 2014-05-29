package kb.province;

/**
 * A sea province. Only accessible by fleets.
 * @author Koen
 *
 */

public class Sea extends Province {

	
	Sea(String name)
	{
		super(name);
	}
	
	@Override
	public boolean isSea() 
	{
		return true;
	}
}
