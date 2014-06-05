package kb;

import java.net.UnknownHostException;
import java.util.ArrayList;

import kb.province.Province;

/**
 * The map/knowledge base.
 * @author Koen
 *
 */

public class Map {

	ArrayList<Province>		provinces;
	ArrayList<Power>		powers;
	
	public static void main(String[] args) throws UnknownHostException {
	}

	public Node getNode(String name)
	{
		for (int i = 0; i < provinces.size(); i++)
		{
			Province ret = provinces.get(i);
			if (ret.name() == name)
			{
				return ret.getNode();
			}
		}
		
		return null;
	}
	
	public void onMessage(String[] message) { 
	}
	
}
