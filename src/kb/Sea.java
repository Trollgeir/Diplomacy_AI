package kb;

/**
 * A sea province. Only accessible by fleets.
 * @author Koen
 *
 */

public class Sea extends Province {

	
	@Override
	public boolean isSea() 
	{
		return true;
	}
}
