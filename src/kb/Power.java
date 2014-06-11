package kb;

import java.util.ArrayList;

import kb.province.Province;
import message.DaideList;
import message.DaideMessage;

/**
 * One of the powers (players) in the game.
 * @author Koen
 *
 */

public class Power implements DaideMessage{

	String						name;
	public ArrayList<Province>	homeProvinces;
	
	Power(String name)
	{
		this.name = name;
		this.homeProvinces = new ArrayList<Province>();
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
	
	public String getName()
	{
		return name;
	}
}
