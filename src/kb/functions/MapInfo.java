package kb.functions; 

import kb.*; 
import kb.unit.*; 
import kb.province.*;
import java.util.ArrayList;
import ai.dodo.ProvinceData;
import java.util.Comparator;
import java.util.Collections;
import java.lang.Comparable;
import ai.dodo.UnitData; 

public class MapInfo {


	public enum SCO_type {
		OURS,
		NOT_OURS,
		ALL,
		NUM
	}

	Map map; 
	Power power;
	ArrayList<Unit> units;
	ArrayList<ProvinceData> provinceData; 

	public MapInfo(Map map, Power power) {
		this.map = map; 
		this.power = power; 
		units = map.getUnitsByOwner(power);

		provinceData = new ArrayList<ProvinceData>();

		System.out.println("initProvinceDataList");
		initProvinceDataList();
	}

	public ArrayList<ProvinceData> getSortedTargets() {
		ArrayList<ProvinceData> sortedList = new ArrayList<ProvinceData>();
		sortedList.addAll(provinceData);

		//sort in descending order.
		Collections.sort(sortedList,
			new Comparator<ProvinceData>(){
					public int compare(ProvinceData p1, ProvinceData p0) {
					return (int)(100 * Math.abs(p0.weight) -  100 * Math.abs(p1.weight));
				}
			});

		return sortedList;
	}

	public ArrayList<ProvinceData> filterTakeable(ArrayList<ProvinceData> targets) {
		ArrayList<ProvinceData> result = new ArrayList<ProvinceData>();

		for (ProvinceData p : targets) {
			if (p.isTakeable()) result.add(p);
		}

		return result; 
	}

	public ArrayList<UnitData> getSortedUnits(ProvinceData target) {
		return target.getAvailebleUnits();
	}

	
	public void updateByMove(ArrayList<UnitData> usedUnits, ArrayList<ProvinceData> movedTo) {
		//for each province, remove the units that are used in the attack.
		for (ProvinceData p : provinceData){

			//the provinces to which is moved don't need to be updated, since those will be removed.
			//if (!movedTo.contains(p)) p.update(usedUnits);
			p.update(usedUnits);
		}

		//remove the provinces to which is moved. 
		provinceData.removeAll(movedTo);

		//recompute the weights for the provinces left.
		//the gains will remain the same. 
		for (ProvinceData p : provinceData) p.computeWeight();

		//dont't call the determineSharedUnits, since the near units are locally updated during the p.update() call.   
	}
	

	public ArrayList<Unit> getUnits() {
		return units; 
	}

	public <T> T getRandomElement(ArrayList<T> list) {
		return list.get((int)(Math.random() * list.size())); 
	}

	/*
	Create province data
	*/
	private void addProvinceData(Unit u, Node n) {
		for (ProvinceData data : provinceData) {
			if (data.province == n.province) {
				return; 
			}
		}
		provinceData.add(new ProvinceData(power, n.province, map.powers));
	}

	/*
	Create for every province we can move to, a new provinceData object 
	*/
	private void initProvinceDataList() {
		for (Unit u : units) {
			addProvinceData(u, u.location); 
			ArrayList<Node> neighbourhood = map.getValidNeighbours(u);
			for (Node n : neighbourhood) {
				addProvinceData(u, n);
			}
		}
	
		for (ProvinceData p : provinceData) p.determineSharedUnits(provinceData);

		for (ProvinceData p : provinceData) {
			p.computeWeight();
		}
	}

	/*
	Return a list of home SCO's under our control, which do not have our units on there 
	*/
	public ArrayList<Province> filterEmptyHomeSCOs(ArrayList<Province> provinces, ArrayList<Province> noGo, ArrayList<Unit> units) {
		ArrayList<Province> result = new ArrayList<Province>(); 
		for (Province p : provinces) {
			if (noGo.contains(p)) continue; 
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

	/*
	Filters all nogo neighbours out
	*/
	public ArrayList<Node> filterNeighbours(ArrayList<Node> neighbours, ArrayList<Node> noGo) {
		ArrayList<Node> result = new ArrayList<Node>();
		for (Node n : neighbours) {
			if (!noGo.contains(n.province)) result.add(n);
		} 
		return result;
	}

	/*
	Return a node which progresses given unit to a supply center of given type. 
	*/
	public Node moveToSupplyCenter(Unit unit, ArrayList<Node> neighbourhood, ArrayList<Node> noGo, boolean randomOnFail, SCO_type type) {
		ArrayList<Node> supplyCenters = getSupplyCenterNodes(neighbourhood, type); 
		if (supplyCenters.size() > 0) {
			return getRandomElement(supplyCenters);  
		} else {
			ArrayList<Node> indirectSupplyCenters = new ArrayList<Node>(); 
			for (Node node : neighbourhood) {
				ArrayList<Node> n_neigbourhood = filterNeighbours(map.getValidNeighbours(unit, node), noGo);
				ArrayList<Node> n_supplyCenters = getSupplyCenterNodes(n_neigbourhood, type);
				if (n_supplyCenters.size() > 0) indirectSupplyCenters.add(node); 
			}
			if (indirectSupplyCenters.size() > 0) {
				return getRandomElement(indirectSupplyCenters);
			}
		}
		return randomOnFail ? getRandomElement(neighbourhood) : null; 
	}

	/* 
	return from a list of given nodes, all supply center nodes of a given type
	*/
	public ArrayList<Node> getSupplyCenterNodes(ArrayList<Node> nodes, SCO_type type) {
		ArrayList<Node> result = new ArrayList<Node>(); 
		for (Node n : nodes) {
			if (n.province.isSupplyCenter()) {
				if (type == SCO_type.OURS && n.province.getOwner() == power) {
					result.add(n);
				} else if (type == SCO_type.NOT_OURS && n.province.getOwner() != power) {
					result.add(n);
				} else if (type == SCO_type.ALL) {
					result.add(n);
				}
			}
		}
		return result; 
	}



}