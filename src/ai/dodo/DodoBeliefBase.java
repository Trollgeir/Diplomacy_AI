package ai.dodo;

import java.util.ArrayList;

import kb.Map;
import kb.Node;
import kb.Power;
import kb.province.Province;
import kb.unit.Unit;

class AllianceInfo
{
	public AllianceInfo()
	{
		//TODO: Just some default values, may need tweaking.
		paranoia = 0.0f;
		supFavor = 0;
		against = new ArrayList<Power>();
		time = 0;
	}
	
	public double 			paranoia;
	public int				supFavor;
	public int				time;
	public Power			with;
	public ArrayList<Power>	against;
}

class PowerInfo
{
	public PowerInfo()
	{
		trust = 0.5;
		peace = false;
		peaceActuality = 1;
	}
	
	public boolean peace;
	public double peaceActuality;
	public double trust;
}

class ProvinceInfo
{
	public ProvinceInfo()
	{
		threat = 0.0;
		threatChange = 0.0;
	}
	public double threat;
	public double threatChange;
}

public class DodoBeliefBase {
	Map		map;
	Power	self;
	ExtendedDodo ai;
	
	
	public ArrayList<AllianceInfo>					allianceInfo;
	public java.util.Map<Province, ProvinceInfo>	provinceInfo;
	public java.util.Map<Power, PowerInfo>			powerInfo;
	
	
	public DodoBeliefBase(Map map, Power self)
	{
		this.map = map;
		this.self = self;
		
		
		provinceInfo = new java.util.HashMap<Province, ProvinceInfo>();
		powerInfo = new java.util.HashMap<Power, PowerInfo>();
		
		for (Power p : map.powers)
		{
			if (!p.equals(self))
				powerInfo.put(p, new PowerInfo());
		}
		
		for (Province p : map.provinces)
		{
			provinceInfo.put(p, new ProvinceInfo());
		}
	}
	
	public int distance(Node start, Node goal)
	{
		java.util.Map<Node, Integer> distanceList = new java.util.HashMap<Node, Integer>();
		ArrayList<Node> procdList = new ArrayList<Node>();
		ArrayList<Node> addedList = new ArrayList<Node>();
		
		addedList.add(start);
		distanceList.put(start, 0);
		
		for (int j = 0; j < addedList.size(); j++)
		{
			Node cNode = addedList.get(j);
			ArrayList<Node> neighbors = cNode.allNeighbors();
			
			for (int i = 0; i < neighbors.size(); i++)
			{
				Node adjNode = neighbors.get(i);
				int adjDist = distanceList.get(cNode) + 1;
				
				if (adjNode.equals(goal))
					return adjDist;
				
				if (!distanceList.containsKey(adjNode))
				{
					distanceList.put(adjNode, adjDist);
					addedList.add(adjNode);
				}
			}
			procdList.add(cNode);
		}
		
		return -1;
	}
	
	public void calcThreats()
	{		
		for (Province p : map.provinces)
		{
			if (!self.equals(p.getOwner()))
			{
				provinceInfo.put(p, new ProvinceInfo());
			}
			else
			{
				double prevThreat = provinceInfo.get(p).threat;
				double threatVal = 0.0;
				
				for (int pow = 0; pow < map.powers.size(); pow++)
				{
					Power cPow = map.powers.get(pow);
					if (cPow.equals(self))
						continue;
					
					ArrayList<Unit> threatUnits = map.getUnitsByOwner(cPow);
					
					for (int u = 0; u < threatUnits.size(); u++)
					{
						double trustFactor = 1.0 - powerInfo.get(cPow).trust;
						int dist = distance(p.getCentralNode(), threatUnits.get(u).location);
						
						double threatFromUnit;
						
						if (dist != -1)
							threatFromUnit = trustFactor * Math.pow(0.5, dist);
						else
							threatFromUnit = 0.0;
						
						threatVal += threatFromUnit;
					}
				}
				provinceInfo.get(p).threat = threatVal;
				if (prevThreat != -1.0)
					provinceInfo.get(p).threatChange = threatVal - prevThreat;
				else
					provinceInfo.get(p).threatChange = 0.0;
			}
		}
	}
	
	public boolean isAlly(Power power)
	{
		for (AllianceInfo alliance : allianceInfo)
		{
			if (alliance.with.equals(power))
				return true;
		}
		return false;
	}
	
	public void incrementAllianceTime()
	{
		for (int i = 0; i < allianceInfo.size(); i++)
		{
			AllianceInfo alliance = allianceInfo.get(i);
			
			alliance.time++;
			alliance.paranoia = 1.0 - Math.pow(1.0 - ai.decrement, alliance.time) * ai.initialTrust;
		}
	}
	
	public void deleteAllAlliancesWith(Power p)
	{
		for(int i = 0; i < allianceInfo.size(); i++)
		{
			if(allianceInfo.get(i).with == p)
				allianceInfo.remove(i);
		}
	}
}
