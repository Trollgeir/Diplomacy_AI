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

	public DodoBuildPhase(ExtendedDodo ai) {
		super(ai); 
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

	public int numFleets() {
		int num = 0;
		ArrayList<Unit> units = map.getUnitsByOwner(this.getPower());
		for (Unit unit : units) {
			if (unit.isFleet()) num++;
		}

		return num; 
	}

	public Node getCoastNode(Province p) {
		int size = p.coastLine.size();
		if (size == 0) {
			if (p.getCentralNode().seaNeighbors.size() > 0) {
				return p.getCentralNode();
			} else {
				return null; 
			}
		} else {
			return p.coastLine.get((int)(Math.random() * size)); 
		}
	}

	public boolean buildFleet() {
		int numFleets = numFleets() ;
		if (power.getName().equals("ENG")) return true;
		if (power.getName().equals("AUS") && numFleets < 3) return Math.random() < 0.75; 
		return numFleets < 2 && Math.random() < 0.75; 
	}

	public void run(LinkedBlockingQueue<Order> queue) {
		/*
			BUILD PHASE
		*/

		MapInfo mapInfo = new MapInfo(map, power);

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

		ArrayList<ProvinceData> targets = mapInfo.getSortedTargets(); 
		targets = mapInfo.filterOccupied(targets, power);


		for (int i = 0; i < error; ++i) {
			int idx = targets.size() - i - 1; 
			if (idx == -1) break; 
			queue.add(new Remove(targets.get(idx).province.getUnit()));
		}

		while (error < 0) {
			//BUILD
			ArrayList<Province> scs = filterProvinces(map.getProvincesByOwner(power), built, units);

			if (scs.size() == 0) { 
				System.out.println("No room to build! All SC's are occupied :(");
				queue.add(new WaiveBuild(power));
			} else {
				int idx = (int)(Math.random() * scs.size());
				Node coastNode = getCoastNode(scs.get(idx));
				System.out.println("Can build on coastLine " + scs.get(idx).getName() + ": " + (scs.get(idx).coastLine.size() > 0));
				
				if (coastNode != null && buildFleet()) {
					queue.add(new Build(new Fleet(power, coastNode)));
				} else {
					queue.add(new Build(new Army(power, scs.get(idx).getCentralNode())));
					System.out.println("Building unit in " + scs.get(idx).getCentralNode().daide());
				}
			
				built.add(scs.get(idx));
			}
			error++; 
		}
	}
}