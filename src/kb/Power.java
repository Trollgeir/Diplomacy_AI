package kb;

import java.util.ArrayList;

import kb.province.Province;
import message.DaideList;

/**
 * One of the powers (players) in the game.
 * @author Koen
 *
 */

public class Power{

	String						name;
	public ArrayList<Province>	homeProvinces;
	public boolean				alive;
	
	Power(String name)
	{
		this.alive = true;
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
