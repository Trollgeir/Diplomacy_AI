package message;

public abstract class DaideMessage {

	/**
	 * 
	 * @return The DAIDE syntax representation of this object.
	 */
	public abstract DaideList daide();
	
	
	public static int pressLevel()
	{
		return 0;
	}
}
