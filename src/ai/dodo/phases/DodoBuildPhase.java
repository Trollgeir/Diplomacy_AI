package ai.dodo.phases; 

import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import message.order.*; 
import message.press.*;
import kb.functions.*;
import kb.province.*; 
import kb.unit.*; 
import kb.*; 
import ai.dodo.*; 

public class DodoBuildPhase extends DodoPhase {

	public DodoBuildPhase(DodoAI ai) {
		super(ai); 
	} 


	public ArrayList<Node> filterNeighbours(ArrayList<Node> neighbours, ArrayList<Province> occupied) {
		ArrayList<Node> result = new ArrayList<Node>();
		for (Node n : neighbours) {
			if (!occupied.contains(n.province)) result.add(n);
		} 
		return result;
	}

	public ArrayList<Province> filterProvinces(ArrayList<Province> provinces, ArrayList<Province> built, ArrayList<Unit> units) {
		ArrayList<Province> result = new ArrayList<Province>(); 
		for (Province p : provinces) {
			if (built.contains(p)) continue; 
			if (!power.homeProvinces.contains(p)) continue;
			boolean empty = true; 
			for (Unit u : units) {
				if (u.location.province == p) {
					empty = false; 
					break; 
				} 
			}
			if (empty) result.add(p);
		}
		return result;
	};

	public ArrayList<Node> getSupplyCenterNodes(ArrayList<Node> nodes, boolean ours) {
		//only return those nodes which are in a province with supply center which are NOT ours!
		ArrayList<Node> result = new ArrayList<Node>(); 
		for (Node n : nodes) {
			if (n.province.isSupplyCenter()) {
				if (ours && n.province.getOwner() == power) {
					result.add(n);
				} else if (!ours && n.province.getOwner() != power) {
					result.add(n);
				}
			}
		}
		return result; 
	}

	public Node moveToSupplyCenter(Unit unit, ArrayList<Node> neighbourhood, ArrayList<Province> occupied, boolean move, boolean ours) {
		ArrayList<Node> supplyCenters = getSupplyCenterNodes(neighbourhood, ours); 
		if (supplyCenters.size() > 0) {
			return getRandomElement(supplyCenters);  
		} else {
			ArrayList<Node> indirectSupplyCenters = new ArrayList<Node>(); 
			for (Node node : neighbourhood) {
				ArrayList<Node> n_neigbourhood = filterNeighbours(map.getValidNeighbours(unit, node), occupied);
				ArrayList<Node> n_supplyCenters = getSupplyCenterNodes(n_neigbourhood, ours);
				if (n_supplyCenters.size() > 0) indirectSupplyCenters.add(node); 
			}
			if (indirectSupplyCenters.size() > 0) {
				return getRandomElement(indirectSupplyCenters);
			}
		}
		if(move)
			return getRandomElement(neighbourhood); 
		else
			return null;
	}

	public <T> T getRandomElement(ArrayList<T> list) {
		return list.get((int)(Math.random() * list.size())); 
	}


	public void run(LinkedBlockingQueue<Order> queue) {
		/*
			BUILD PHASE
			*/
					ArrayList<Unit> units = map.getUnitsByOwner(this.getPower());
		ArrayList<Province> home = power.homeProvinces;
		ArrayList<Province> provinces = map.getProvincesByOwner(this.getPower()); 
		ArrayList<Province> occupied = new ArrayList<Province>();
		for (Unit u : units) occupied.add(u.location.province);
			
			System.out.println("---------- BUILD/REMOVE ----------");
			ArrayList<Province> built = new ArrayList<Province>();
			ArrayList<Unit> canBeRemoved = new ArrayList<Unit>();
			canBeRemoved.addAll(units); 
			int error = units.size() - provinces.size(); 
			// error > 0 means more units then provinces so REMOVE
			// error < 0 means more provinces then units so  BUILD
			while(error > 0)
			{
				Unit remove = null; int best = 0; int j = 0;
				for(Unit u : canBeRemoved)
				{
					if (u.location.province.isSupplyCenter()) {
						j = 0;
					} else {
						ArrayList<Node> nbh = filterNeighbours(map.getValidNeighbours(u), occupied);
						Node n = moveToSupplyCenter(u, nbh, occupied, false, true);
						if (n == null) {
							j = 3;
						} else if (n.province.isSupplyCenter()) {
							j = 1;
						} else {
							j = 2;
						}
					}
					if(j > best)
					{
						remove = u;
						best = j;
					}
					else if(j == best)
						if(Math.random() > 0.5)
							remove = u;
				}
				queue.add(new Remove(remove));
				canBeRemoved.remove(remove); 
				error--;
			}
			while (error < 0) {
				//BUILD
				ArrayList<Province> scs = filterProvinces(map.getProvincesByOwner(power), built, units);

				if (scs.size() == 0) { 
					System.out.println("No room to build! All SC's are occupied :(");
					queue.add(new WaiveBuild(power));
				} else {
					int idx = (int)(Math.random() * scs.size());
					queue.add(new Build(new Army(power, scs.get(idx).getCentralNode())));
					System.out.println("Building unit in " + scs.get(idx).getCentralNode().daide());
					built.add(scs.get(idx));
				}
				error++; 
			}
	}
}