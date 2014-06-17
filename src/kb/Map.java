package kb;

import java.util.ArrayList;

import ai.AI;
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

public class Map extends Receiver {

	public ArrayList<Province>		provinces;
	public ArrayList<Power>		powers;
	ArrayList<Unit>			units;
	boolean					isStandard;
	Phase					phase;
	int						year;
	AI						ai;
	
	String[] mapMessage; 

	public Map()
	{
		powers = new ArrayList<Power>();
		provinces = new ArrayList<Province>();
		units = new ArrayList<Unit>();

		isStandard = false;
		phase = Phase.SUM;
		year = -1;
		
		ai = null;
	}
	
	public void setAI(AI ai)
	{
		this.ai = ai;
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
	
	public int getYear()
	{
		return year;
	}
	public Phase getPhase()
	{
		return phase;
	}
	public boolean getStandard()
	{
		return isStandard;
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
	


	public ArrayList<Unit> getUnitsByOwner(Power power)
	{
		ArrayList<Unit> ret = new ArrayList<Unit>();
		
		for (int i = 0; i < units.size(); i++)
		{
			if (units.get(i).owner.name.equals(power.name))
				ret.add(units.get(i));
		}
		
		return ret;
	}
	
	public ArrayList<Province> getProvincesByOwner(Power power)
	{
		ArrayList<Province> ret = new ArrayList<Province>();
		
		for (int i = 0; i < provinces.size(); i++)
		{
			if(provinces.get(i).getOwner() != null)
			{
				if (provinces.get(i).getOwner().name.equals(power.name))
					ret.add(provinces.get(i));
			}
		}
		
		return ret;
	}
	
	public ArrayList<Node> getValidNeighbours(Unit unit) {
		ArrayList<Node> result = new ArrayList<Node>(); 
		//filter based on unit type
		if (unit.isArmy()) {
			result.addAll(unit.location.landNeighbors);
		} else {
			result.addAll(unit.location.seaNeighbors);
		}
		return result;
	}

	public ArrayList<Node> getValidNeighbours(Unit unit, Node node) {
		ArrayList<Node> result = new ArrayList<Node>(); 
		//filter based on unit type
		if (unit.isArmy()) {
			result.addAll(node.landNeighbors);
		} else {
			result.addAll(node.seaNeighbors);
		}
		return result;
	}
	
	public void printMap()
	{
		System.out.println("=========MAP=========");
		System.out.println(phase.toString() + " of year " + year);
		
		for (int i = 0; i < provinces.size(); i++)
		{
			System.out.println();
			Province cProv = provinces.get(i);
			System.out.println("Province: " + cProv.daide());
			if (cProv.getOwner() != null)
				System.out.println("Owner: " + cProv.getOwner().getName());
			
			if (cProv.occupied())
			{
				System.out.println("Occupied by: " + cProv.unit().daide());
			}
			
			System.out.print(" LAND ");
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
					System.out.print(" SEA " + cProv.coastLine.get(c).coastName() + " ");
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
				System.out.print(" SEA ");
				for (int j = 0; j < provinces.get(i).getCentralNode().seaNeighbors.size(); j++)
				{
					ArrayList<Node> n = provinces.get(i).getCentralNode().seaNeighbors;
					Node an = n.get(j);
					System.out.print(" " + an.daide());
				}
				System.out.println();
			}
			
		}
		
		System.out.println("====================");
	}
	
	public void processSCO(String[] message)
	{
		int pWord = 1;
		
		while (pWord < message.length)
		{
			int powerStart = pWord;
			int powerEnd = unBracket(message, powerStart);
			
			Power power = getPower(message[powerStart + 1]);
			
			for (int i = powerStart + 2; i < powerEnd; i++)
			{
				getProvince(message[i]).setOwner(power);
			}
			
			pWord = powerEnd+1;
		}
		
		//printMap();
	}
	
	public void processRetreatList(Unit u, String[] message, int start) {
		int i = start; 
		while (!message[i].equals(")")) {
			if (message[i].equals("(")) {
				u.retreatTo.add(getNode(message[i + 1], message[i + 2]));
				i = i + 4; 
			} else {
				u.retreatTo.add(getNode(message[i]));
				i = i + 1;  
			}
		}
	}

	public void processNOW(String[] message) {
		units.clear();
		for (int i = 0; i < provinces.size(); i++)
			provinces.get(i).removeUnit();
		
		phase = Phase.valueOf(message[2]);
		year = Integer.parseInt(message[3]);
		
		for (Power p : powers)
			p.alive = false;
		
		int uWord = 5;
		
		while (uWord < message.length)
		{
			int unitStart = uWord;
			int unitEnd = unBracket(message, unitStart);
			
			Power pow = getPower(message[unitStart + 1]);
			pow.alive = true;
			String uType = message[unitStart + 2];
			Node loc = null;
			
			int possibleMRT = unitStart + 4; 

			if (message[unitStart + 3].equals("(")) {
				loc = getNode(message[unitStart + 4], message[unitStart + 5]);
				possibleMRT = unitStart + 7; 
			 } else {
				loc = getNode(message[unitStart + 3]);
			}
			
			if (loc == null)
			{
				if (message[unitStart + 3].equals("("))
					System.out.println("Coast");
				
				System.out.println(provinces.size());
				for (int i = 0; i < provinces.size(); i++)
					System.out.println(provinces.get(i).daide());
				
				for (int i = unitStart; i < unitEnd; i++)
				{
					System.out.println(message[i]);
				}
			}

			Unit newUnit = null; 
			if (uType.equals("AMY"))
			{
				newUnit = new Army(pow, loc);
			}
			else
			{
				newUnit = new Fleet(pow, loc);
			}
			units.add(newUnit);

			if (possibleMRT < message.length && message[possibleMRT].equals("MRT")) {
				newUnit.mustRetreat = true; 
				processRetreatList(newUnit, message, possibleMRT + 2); 
			}

			
			uWord = unitEnd + 1;
		}
		
		ai.newTurn();
		
		//printMap();
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
				
				for (int p = powSupStart + 2; p < powSupEnd; p++)
				{
					Province newProvince = new Province(message[p], true, pow);
					provinces.add(newProvince);
					if (pow != null)
						pow.homeProvinces.add(newProvince);
				}
				
				sWord = powSupEnd+1;
			}
			
			//Non supply centers
			int nonSupStart = supEnd+1;
			int nonSupEnd = unBracket(message, nonSupStart);
		
			for (int n = nonSupStart + 1; n < nonSupEnd; n++)
			{
				provinces.add(new Province(message[n], false, null));
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
		
		//printMap();
	}
	
	@Override
	public void onMessage(String[] message) {
		if (message[0].equals("MAP")) 
		{
			System.out.println(message[2]); 
			isStandard = message[2].equals("'STANDARD'");
			MapDefinition mapdef = new MapDefinition();
			Game.server.send(mapdef);
			mapMessage = message; 
		}
		else if (message[0].equals("MDF"))
		{
			processMDF(message);
			Yes yes = new Yes(mapMessage);
			Game.server.send(yes);
		}
		else if (message[0].equals("NOW"))
		{
			processNOW(message);
		}
		else if (message[0].equals("SCO"))
		{
			processSCO(message);
		}
	}

	public static void main(String[] args) {
		new Map().processNOW(args); 
	}
}
