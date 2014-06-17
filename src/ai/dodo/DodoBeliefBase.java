package ai.dodo;

import java.util.ArrayList;

import kb.Map;
import kb.Node;
import kb.Power;
import kb.province.Province;
import kb.unit.Unit;

public class DodoBeliefBase {
	Map		map;
	Power	self;
	
	public java.util.Map<Province, Double>		threat;
	public java.util.Map<Province, Double>		threatChange;
	public java.util.Map<Power, Double>			trust;
		
	public DodoBeliefBase(Map map, Power self)
	{
		this.map = map;
		this.self = self;
		
		threat = new java.util.HashMap<Province, Double>();
		threatChange = new java.util.HashMap<Province, Double>();
		trust = new java.util.HashMap<Power, Double>();
		
		for (Power p : map.powers)
		{
			if (!p.equals(self))
				trust.put(p, 0.5);
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
		threatChange.clear();
		
		ArrayList<Province> ownedProvinces = map.getProvincesByOwner(self);
		
		java.util.HashMap<Province, Double> nThreat = new java.util.HashMap<Province, Double>();
		for (int t = 0; t < ownedProvinces.size(); t++)
		{
			if (threat.containsKey(ownedProvinces.get(t)))
			{
				nThreat.put(ownedProvinces.get(t), threat.get(ownedProvinces.get(t)));
			}
		}
		threat = nThreat;
		
		for (int p = 0; p < ownedProvinces.size(); p++)
		{
			Province cProv = ownedProvinces.get(p);
			
			double prevThreat;
			if (threat.containsKey(cProv))
				prevThreat = threat.get(cProv);
			else
				prevThreat = -1.0;
				
			double threatVal = 0.0;
			
			for (int pow = 0; pow < map.powers.size(); pow++)
			{
				Power cPow = map.powers.get(pow);
				if (cPow.equals(self))
					continue;
				
				ArrayList<Unit> threatUnits = map.getUnitsByOwner(cPow);
				
				for (int u = 0; u < threatUnits.size(); u++)
				{
					double trustFactor = 1.0 - trust.get(cPow);
					int dist = distance(cProv.getCentralNode(), threatUnits.get(u).location);
					
					double threatFromUnit;
					
					if (dist != -1)
						threatFromUnit = trustFactor * Math.pow(0.5, dist);
					else
						threatFromUnit = 0.0;
					
					threatVal += threatFromUnit;
				}
			}
			threat.put(cProv, threatVal);
			if (prevThreat != -1.0)
				threatChange.put(cProv, threatVal - prevThreat);
			else
				threatChange.put(cProv, 0.0);
		}
		
		System.out.println(threat);
	}
	
	
}
