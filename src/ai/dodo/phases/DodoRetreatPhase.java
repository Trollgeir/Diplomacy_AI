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

public class DodoRetreatPhase extends DodoPhase {

	public DodoRetreatPhase(DodoAI ai) {
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
		ArrayList<Unit> units = map.getUnitsByOwner(this.getPower());
		ArrayList<Province> home = power.homeProvinces;
		ArrayList<Province> provinces = map.getProvincesByOwner(this.getPower()); 
		ArrayList<Province> occupied = new ArrayList<Province>();
		for (Unit u : units) occupied.add(u.location.province);

		for(Unit u : units)
			{
				if(u.mustRetreat) {
					// No place to retreat to, so we must disband. 
					if(u.retreatTo.size() == 0) queue.add(new Disband(u)); 
					else {
						//Try to move to a supply center
						Node destination = moveToSupplyCenter(u, u.retreatTo, occupied, false, false);
						if(destination == null) queue.add(new Disband(u)); 
						else {
							occupied.remove(u.location.province); 
							occupied.add(destination.province);
							System.out.println(power.getName() + " : " + "I want to retreat " + u.location.daide() + " to " + destination.daide()); 
							queue.add(new Retreat(u, destination)); 
						}
					}
				}
			}
	}

}