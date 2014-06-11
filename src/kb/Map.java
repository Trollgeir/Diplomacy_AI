package kb;

import java.util.ArrayList;

import kb.province.*;
import game.Receiver;
import message.server.*;
import game.Game;
import kb.unit.*;
/**
 * The map/knowledge base.
 * @author Koen
 *
 */

enum Phase
{
	SUM, FAL, AUT, WIN, SPR
};

public class Map extends Receiver {

	ArrayList<Province>		provinces;
	ArrayList<Power>		powers;
	ArrayList<Unit>			units;
	boolean					isStandard;
	Phase					phase;
	int						year;
	
	public Map()
	{
		powers = new ArrayList<Power>();
		provinces = new ArrayList<Province>();
		units = new ArrayList<Unit>();

		phase = Phase.SUM;
		year = -1;
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
			if (cst != null && cst.getName().equals(name))
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
	
	public ArrayList<Unit> powerUnits(Power power)
	{
		ArrayList<Unit> ret = new ArrayList<Unit>();
		
		for (int i = 0; i < units.size(); i++)
		{
			if (units.get(i).owner.name.equals(power.name))
				ret.add(units.get(i));
		}
		
		return ret;
	}
	
	public void printMap()
	{
		System.out.println(phase.toString() + " of year " + year);
		
		for (int i = 0; i < provinces.size(); i++)
		{
			Province cProv = provinces.get(i);
			System.out.println(cProv.daide());
			
			if (cProv.occupied())
			{
				System.out.println("Occupied by: " + cProv.unit().daide());
			}
			
			System.out.print("	LAND	");
			for (int j = 0; j < cProv.getCentralNode().landNeighbors.size(); j++)
			{
				ArrayList<Node> n = cProv.getCentralNode().landNeighbors;
				Node an = n.get(j);
				System.out.print(" " + an.daide());
			}
			System.out.println();
			
			if (cProv.coastAmt() != 0)
			{
				for (int c = 0; c < cProv.coastAmt(); c++)
				{
					System.out.print("	SEA	" + cProv.coastLine.get(c).coastName() + " ");
					for (int j = 0; j < cProv.coastLine.get(c).seaNeighbors.size(); j++)
					{
						ArrayList<Node> n = cProv.coastLine.get(c).seaNeighbors;
						Node an = n.get(j);
						System.out.print(" " + an.daide());
					}
					System.out.println();
				}
			}
			else
			{
				System.out.print("	SEA		");
				for (int j = 0; j < provinces.get(i).getCentralNode().seaNeighbors.size(); j++)
				{
					ArrayList<Node> n = provinces.get(i).getCentralNode().seaNeighbors;
					Node an = n.get(j);
					System.out.print(" " + an.daide());
				}
				System.out.println();
			}
			
		}
	}
	
	public void processNOW(String[] message) {
		units.clear();
		for (int i = 0; i < provinces.size(); i++)
			provinces.get(i).removeUnit();
		
		phase = Phase.valueOf(message[2]);
		year = Integer.parseInt(message[3]);
		
		int uWord = 5;
		
		while (uWord < message.length)
		{
			int unitStart = uWord;
			int unitEnd = unBracket(message, unitStart);
			
			Power pow = getPower(message[unitStart + 1]);
			String uType = message[unitStart + 2];
			Node loc = null;
			
			if (message[unitStart + 3].equals("("))
				loc = getNode(message[unitStart + 4], message[unitStart + 5]);
			else
				loc = getNode(message[unitStart + 3]);
			
			if (uType.equals("AMY"))
			{
				units.add(new Army(pow, loc));
			}
			else
			{
				units.add(new Fleet(pow, loc));
			}
			
			uWord = unitEnd + 1;
		}
		
		printMap();
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
			while (sWord < supEnd)
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
						province.getCentralNode().landNeighbors.add(getNode(message[a]));
				}
				else if (uType.equals("FLT"))
				{
					for (int a = unitAdjStart+2; a < unitAdjEnd; a++)
					{
						Node adjNode = null;
						if (message[a].equals("("))
						{
							adjNode = getNode(message[a+1], message[a+2]);
							if (adjNode == null)
							{
								Province nProvince = getProvince(message[a+1]);
								adjNode = new Node(nProvince, message[a+2]);
								nProvince.addCoastalNode(adjNode);
							}
							a += 3;
						}
						else
						{
							adjNode = getNode(message[a]);
						}
						
						province.getCentralNode().seaNeighbors.add(adjNode);
					}
				}
				else if (uType.equals("("))
				{
					Node thisNode = province.getCoastNode(message[unitAdjStart+3]);
					if (thisNode == null)
					{
						thisNode = new Node(province, message[unitAdjStart+3]);
						province.addCoastalNode(thisNode);
					}
					
					for (int a = unitAdjStart+5; a < unitAdjEnd; a++)
					{
						Node adjNode = null;
						if (message[a].equals("("))
						{
							adjNode = getNode(message[a+1], message[a+2]);
							if (adjNode == null)
							{
								Province nProvince = getProvince(message[a+1]);
								adjNode = new Node(nProvince, message[a+2]);
								nProvince.addCoastalNode(adjNode);
							}
							a += 3;
						}
						else
						{
							adjNode = getNode(message[a]);
						}
						
						thisNode.seaNeighbors.add(adjNode);
					}
				}
				
				uWord = unitAdjEnd+1;
			}
			
			cWord = provAdjEnd+1;
		}
		
		printMap();
	}
	
	@Override
	public void onMessage(String[] message) {
		if (message[0].equals("MAP")) 
		{
			isStandard = message[2].equals("STANDARD");
			Yes yes = new Yes(message);
			Game.server.send(yes);
			MapDefinition mapdef = new MapDefinition();
			Game.server.send(mapdef);
		}
		else if (message[0].equals("MDF"))
		{
			processMDF(message);
		}
		else if (message[0].equals("NOW"))
		{
			processNOW(message);
		}
	}
}
