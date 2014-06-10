package kb;

import java.net.UnknownHostException;
import java.util.ArrayList;

import kb.province.*;
import game.Receiver;
import message.server.*;
import communication.server.*;
import game.Game;
/**
 * The map/knowledge base.
 * @author Koen
 *
 */

public class Map extends Receiver {

	ArrayList<Province>		provinces;
	ArrayList<Power>		powers;
	boolean					isStandard;

	public static void main(String[] args) throws UnknownHostException {
	}

	public Node getNode(String name)
	{
		for (int i = 0; i < provinces.size(); i++)
		{
			Province ret = provinces.get(i);
			if (ret.getName().equals(name))
			{
				return ret.getCentralNode();
			}
		}
		
		return null;
	}
	
	public Node getNode(String name, String coast)
	{		
		for (int i = 0; i < provinces.size(); i++)
		{
			Province cst = provinces.get(i);
			if (cst != null && cst.isCoast() && cst.getName().equals(name))
			{
				return cst.getCoastNode(coast);
			}
		}
		
		return null;
	}
	
	public Power getPower(String name)
	{
		for (int i = 0; i < powers.size(); i++)
		{
			Power ret = powers.get(i);
			if (ret.getName().equals(name))
			{
				return ret;
			}
		}
		
		return null;
	}
	
	public Province getProvince(String name)
	{
		for (int i = 0; i < provinces.size(); i++)
		{
			Province ret = provinces.get(i);
			if (ret.getName() == name)
			{
				return ret;
			}
		}
		
		return null;
	}
	
	public static int unBracket(String[] messageIn, int start)
	{
		int end = 0;
		int bracketCount = 0;
		
		//Is the message even bracketed
		if (!messageIn[start].equals("("))
			return -1;
		
		//Iterate over all the words
		for (int i = start; i < messageIn.length; i++)
		{
			String cWord = messageIn[i];
			//Take care of nested brackets
			if (cWord.equals("("))
			{
				bracketCount++;
				continue;
			}
			
			if (cWord.equals(")"))
			{
				bracketCount--;
				if (bracketCount == 0)
				{
					//Stop searching if all of the nested brackets are closed
					end = i;
					break;
				}
			}
		}
		if (bracketCount != 0)
			return -1;
		
		return end;
	}
	
	public void processMDF(String[] message) {
		
		//Powers
		int powStart = 1;
		int powEnd = unBracket(message, powStart);
		
		for (int i = powStart + 1; i < powEnd; i++)
		{
			powers.add(new Power(message[i]));
		}
		
		//Province
		int provStart = powEnd+1;		
		int provEnd = unBracket(message, provStart);
			
			//Supply centers
			int supStart = provStart+1;
			int supEnd = unBracket(message, supStart);
			
			int sWord = supStart + 1;
			while (supStart < supEnd)
			{
				int powSupStart = sWord;
				int powSupEnd = unBracket(message, powSupStart);
				
				Power pow = getPower(message[powSupStart + 1]);
				
				//TODO UNO & power home centers
				
				for (int p = powSupStart + 2; p < powSupEnd; p++)
				{
					provinces.add(new Province(message[p], true));
				}
				
				sWord = powSupEnd+1;
			}
			
			//Non supply centers
			int nonSupStart = supEnd+1;
			int nonSupEnd = unBracket(message, nonSupStart);
		
			for (int n = nonSupStart + 1; n < nonSupEnd; n++)
			{
				provinces.add(new Province(message[n], false));
			}
		
		
		//Adjacencies
		int adjStart = provEnd+1;
		int adjEnd = unBracket(message, adjStart);
		
		int cWord = adjStart + 1;
		while (cWord < adjEnd)
		{
			int provAdjStart = cWord;
			int provAdjEnd = unBracket(message, provAdjStart);
			
			Province province = getProvince(message[provAdjStart+1]);
			
			int uWord = provAdjStart+2;
			while (uWord < provAdjEnd)
			{
				int unitAdjStart = uWord;
				int unitAdjEnd = unBracket(message, unitAdjStart);
				
				String uType = message[unitAdjStart+1];
				
				if (uType.equals("AMY"))
				{
					for (int a = unitAdjStart+2; a < unitAdjEnd; a++)
						province.getCentralNode().neighbors.add(getNode(message[a]));
				}
				else if (uType.equals("FLT"))
				{
					for (int a = unitAdjStart+2; a < unitAdjEnd; a++)
					{
						Node adjNode;
						if (message[a].equals("("))
						{
							adjNode = getNode(message[a+1], message[a+2]);
							if (adjNode == null)
							{
								Province nProvince = getProvince(message[a+1]);
								adjNode = new Node(nProvince, message[a+2]);
								nProvince.addCoastalNode(adjNode);
								a += 2;
							}
						}
						else
						{
							adjNode = getNode(message[a]);
						}
						
						province.getCentralNode().neighbors.add(adjNode);
					}
				}
				else if (uType.equals("("))
				{
					String coastName = message[unitAdjStart+3];
					Node thisNode = province.getCoastNode(coastName);
					if (thisNode == null)
					{
						thisNode = new Node(province, coastName);
						province.addCoastalNode(thisNode);
					}
					
					for (int a = unitAdjStart+5; a < unitAdjEnd; a++)
					{
						Node adjNode;
						if (message[a].equals("("))
						{
							adjNode = getNode(message[a+1], message[a+2]);
							if (adjNode == null)
							{
								Province nProvince = getProvince(message[a+1]);
								adjNode = new Node(nProvince, message[a+2]);
								nProvince.addCoastalNode(adjNode);
								a += 2;
							}
						}
						else
						{
							adjNode = getNode(message[a]);
						}
						
						thisNode.neighbors.add(adjNode);
					}
				}
				
				uWord = unitAdjEnd+1;
			}
		}
	}
	
	@Override
	public void onMessage(String[] message) {
		if (message[0].equals("MAP")) 
		{
			isStandard = message[2].equals("STANDARD");
			Yes yes = new Yes(message);
			try {
				Game.server.send(yes);
				MapDefinition mapdef = new MapDefinition();
				server.send(mapdef);
			} catch (UnknownTokenException | DisconnectedException e) {
				/*TODO I don't know*/
				e.printStackTrace(); 
			}
		}
		else if (message[0].equals("MDF"))
		{
			processMDF(message);
		} 
	}
}
