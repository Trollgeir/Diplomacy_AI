package kb;

import java.util.ArrayList;

public class Coast extends Land{

	ArrayList<Node>		coastLine;
	
	@Override
	public boolean isLand() 
	{
		return false;
	}
	
	@Override
	public boolean isCoast() 
	{
		return true;
	}
}
