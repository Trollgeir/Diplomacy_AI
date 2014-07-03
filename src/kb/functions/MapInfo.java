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
import ai.dodo.NeighborData;

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

	public float[] willAttack;
	public float[] willDefend;  

	public MapInfo(Map map, Power power, float[] willAttack, float[] willDefend) {
		this.map = map; 
		this.power = power; 
		units = map.getUnitsByOwner(power);

		this.willAttack = willAttack;
		this.willDefend =willDefend; 

		provinceData = new ArrayList<ProvinceData>();

		System.out.println("initProvinceDataList");
		initProvinceDataList();
	}

	/*
	Create for every province we can move to, a new provinceData object 
	*/
	private void initProvinceDataList() {
		for (Province prov : map.provinces) {
			provinceData.add(new ProvinceData(power, prov, map.powers, willAttack, willDefend));
		}

		for (ProvinceData p : provinceData) p.computeAdjProvDatas(provinceData);

		for (ProvinceData p : provinceData) p.determineSharedUnits(provinceData);

		for (ProvinceData p : provinceData) p.computeOwnSupport();
	}

	public void computeGains() {
		for (ProvinceData p : provinceData) p.computeNegSupportValues();

		for (ProvinceData p : provinceData) p.computeGains(provinceData);

		for (ProvinceData p : provinceData) p.computeSmoothedGains();

		for (ProvinceData p : provinceData) p.computeWeight();
	}

	public ArrayList<ProvinceData> getSortedTargets() {
		ArrayList<ProvinceData> sortedList = new ArrayList<ProvinceData>();
		sortedList.addAll(provinceData);

		//sort in descending order.
		Collections.sort(sortedList,
			new Comparator<ProvinceData>(){
				public int compare(ProvinceData p1, ProvinceData p0) {
					//return ((int)(100 * Math.abs(p0.weight) -  100 * Math.abs(p1.weight)));
					if (Math.abs(p0.weight) > Math.abs(p1.weight)) return 1;
					if (Math.abs(p0.weight) < Math.abs(p1.weight)) return -1;
					return 0;
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

	public ArrayList<ProvinceData> filterOccupied(ArrayList<ProvinceData> targets, Power power) {
		ArrayList<ProvinceData> result = new ArrayList<ProvinceData>();

		for (ProvinceData p : targets) {
			if (p.province.getUnit() != null && p.province.getUnit().owner == power) result.add(p);
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

		//add smoothedgains to nearby provinces to simulate cohesion. 
		float kernelWeight[] = {0, 0, 0};
		
		/*
		float kernelWeight[] = {0, 8, 5};
		ArrayList<ArrayList<ProvinceData>> kernel = getProvinceKernel(movedTo, 2);
		for (int i = 0; i < kernel.size(); ++i ) {
			System.out.println("Order " + i);
			for (ProvinceData provData : kernel.get(i)) {
				System.out.println("\t" + provData.province.getName());
				provData.smoothedGains += kernelWeight[i]; 
			}
		}
		*/


		//recompute the weights for the provinces left.
		//the gains will remain the same. 
		for (ProvinceData p : provinceData) p.computeWeight();

		//dont't call the determineSharedUnits, since the near units are locally updated during the p.update() call.   
	}
	
	public static ArrayList<ArrayList<ProvinceData>> getProvinceKernel(ArrayList<ProvinceData> order0, int order) {
		 ArrayList<ArrayList<ProvinceData>> result = new  ArrayList<ArrayList<ProvinceData>>();
		 for (int i = 0; i <= order; ++i) {
		 	result.add(new ArrayList<ProvinceData>());
		 }

		 result.get(0).addAll(order0);

		 //Add order i to result;
		 for (int i = 1; i <= order; ++i) {
		 	//add all the adjecent provinces from the previous order
		 	for (ProvinceData provData : result.get(i-1)) {
		 		for (ProvinceData adjProvData : provData.adjProvDatas) {
		 			//add only when it is not already contained in the 0 .. i-th order.  
		 			boolean contained = false;
		 			for (int j = 0; j <= i; ++j) {
		 				if (result.get(j).contains(adjProvData)) {
		 					contained = true;
		 					break;
		 				}
		 			}

		 			if (!contained) result.get(i).add(adjProvData);
		 		}
		 	} 	
		}

		return result; 
	}

	public ArrayList<Unit> getUnits() {
		return units; 
	}

	public <T> T getRandomElement(ArrayList<T> list) {
		return list.get((int)(Math.random() * list.size())); 
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

	public ArrayList<NeighborData> getNeighborCount() {
		ArrayList<ArrayList<Unit>> units = new ArrayList<ArrayList<Unit>>();
		for (int i = 0; i < map.powers.size(); ++i) units.add(new ArrayList<Unit>());

		int powerIdx = map.powers.indexOf(power);

		for (ProvinceData provData : provinceData) {
			for (int i = 0; i < map.powers.size(); ++i) {
				if (i == powerIdx || provData.support == 0) continue;

				for (UnitData unitData : provData.nearUnits.get(i)) {
					if (!units.get(i).contains(unitData.unit)) {
						units.get(i).add(unitData.unit);
					}
				}
			}
		}

		ArrayList<NeighborData> res = new ArrayList<NeighborData>();
		for (int i = 0; i < map.powers.size(); ++i) {
			NeighborData data = new NeighborData(units.get(i), map.powers.get(i));

			ArrayList<Province> ownedProvinces = map.getProvincesByOwner(data.power);
			for (Province prov : ownedProvinces) {
				if (prov.isSupplyCenter()) data.numSCO++; 
			}

			res.add(data);
		}

		
		//sort in descending order.
		Collections.sort(res,
			new Comparator<NeighborData>(){
					public int compare(NeighborData d0, NeighborData d1) {
						return d1.getSize() - d0.getSize();
				}
			});
		
		return res; 
	}



}