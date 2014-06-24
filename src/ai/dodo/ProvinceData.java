package ai.dodo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.lang.Comparable;
import kb.Node;
import kb.unit.Unit; 
import kb.*;
import kb.province.Province;

public class ProvinceData {

	public static float c_supply = 2.0f;
	public static float c_adjSupply = 1.0f;
	public static float c_shared = 0.0f;
	public static float risk = 0.75f; 
	public static float sharedRisk = 0.5f;
	public static float defence = 1.0f; 

	public int supplyType; 
	public int neighborSupply; 

	public Power power; 
	public Province province;
	public ArrayList<Power> powers;

	public ArrayList<Province> adjProvinces; 
	public ArrayList<ArrayList<Node>> destNodes; 
	public ArrayList<ArrayList<UnitData>> nearUnits;
	public int[] shared;  

	public int support; 
	public int maxNegSupport; 
	public int mainEnemy; 

	public float weight; 
	public float gains; 

	public ProvinceData(Power power, Province province, ArrayList<Power> powers) {
		//TODO set supply type

		this.power = power; 
		this.province = province;
		this.powers = powers; 

		adjProvinces = new ArrayList<Province>();
		nearUnits = new ArrayList<ArrayList<UnitData>>();
		destNodes = new ArrayList<ArrayList<Node>>();
		
		for (int i = 0; i < powers.size(); ++i) {
			nearUnits.add(new ArrayList<UnitData>());	
			destNodes.add(new ArrayList<Node>());
		}
		shared = new int[powers.size()];

		computeAdjProvinces();

		/*
		System.out.println(province.name + "adjecent with: ");
		for (Province p : adjProvinces) {
			System.out.println("\t" + p.name);
		}
		*/

		addNearUnits(province);
		for (Province p : adjProvinces) addNearUnits(p);

		computeGains(power);
	}

	//be sure that determineSharedUnits() is already called.
	public void computeWeight() {
		computeSupport(power);
		computeNegSupport(power);
		computeWeight(power);
	}

	//remove the used units from nearUnits 
	public void update(ArrayList<UnitData> usedUnits) {
		int idx = powers.indexOf(power);
		ArrayList<UnitData> removeList = new ArrayList<UnitData>();

		//check whether a nearUnit is the same a usedUnit.
		for (UnitData unitUsed : usedUnits) {
			
			for (UnitData unitData : nearUnits.get(idx)) {
				if (unitUsed.unit == unitData.unit) {
					support--;
					//if this unit was shared then the shared state needs to be decremented.
					if (unitData.shared > 0) shared[idx]--;
				 	
				 	//remove the unit from the nearUnits
					removeList.add(unitData);
				}
			}
		} 

		nearUnits.get(idx).removeAll(removeList);
	}

	public boolean isTakeable() {
		if (mainEnemy == -1) {
			return support > 0;
		}
		return support > risk * (maxNegSupport - shared[mainEnemy]) + sharedRisk * shared[mainEnemy]; 
	}

	public int getSupportNeeded() {
		if (mainEnemy == -1) {
			return 1; 
		}
		return (int)Math.ceil(risk * (maxNegSupport - shared[mainEnemy]) + sharedRisk * shared[mainEnemy]);
	}

	//find all the adjecent provinces
	private void computeAdjProvinces() {
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.add(province.getCentralNode());
		nodes.addAll(province.coastLine);

		//loop through all the nodes our province contains.
		for (Node node : nodes) {
			//add the reachable provinces of node
			computeAdjProvinces(node);
		}
	}

	//add all the adjecent provinces of node to adjProvinces 
	private void computeAdjProvinces(Node node) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.addAll(node.landNeighbors);
		nodes.addAll(node.seaNeighbors);

	 	for (Node n : nodes) {
			Province adjProv = n.province;
			//Ignore our own province and province which are already found
			if (adjProv != province && !adjProvinces.contains(adjProv)) {
				adjProvinces.add(adjProv); 
			}
		}
	}


	private void computeNegSupport(Power power) {
		maxNegSupport = 0;
		mainEnemy = -1;

		for (int i = 0; i < powers.size(); ++i) {
			if (power != powers.get(i) && nearUnits.get(i).size() > maxNegSupport) {
				maxNegSupport = nearUnits.get(i).size();
				mainEnemy = i; 
			}
		}
	}

	private void computeSupport(Power power) {
		for (int i = 0; i < powers.size(); ++i) {
			if (power == powers.get(i)) {
				support = nearUnits.get(i).size();
			}
		}
	}

	//Add the units from nearProv to nearUnits.
	//This is done seperatly for each power. 
	private void addNearUnits(Province nearProv) {
		//System.out.println("Adding near units of " + nearProv.daide() + "to " +  province.daide() );
		
		Unit unit = nearProv.getUnit();
		if (unit == null) return;

		Node destNode = findDestNode(unit, province);
		if (destNode != null) {
			int powerIdx = powers.indexOf(unit.owner);
			nearUnits.get(powerIdx).add(new UnitData(unit, destNode));
			//System.out.println("Found an unit");
		}

		/*

		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.add(nearProv.getCentralNode());
		nodes.addAll(nearProv.coastLine);
		
		//Search through each node for a unit.
		for (Node node : nodes) {
			if (node.unit != null) {
				Unit unit = node.unit;
				
				//add a new UnitData to nearUnits.
				if (!containsUnit(unit)) {
					//destNode is the node by which this unit can reach this province.
					Node destNode = findDestNode(unit, province);
					if (destNode != null) {
						int powerIdx = powers.indexOf(unit.owner);
						nearUnits.get(powerIdx).add(new UnitData(unit, destNode));
					}
					
				}

				//stop searching when a unit is found, also when it's not already contained by nearUnits. 
				//since each province can contain maximal one unit. 
				return; 
			}
		}
		*/		
	}

	private Node findDestNode(Unit unit, Province destProv) {	
		//add to destNodes all the nodes that are reachable from src 		
		ArrayList<Node> destNodes = unit.getAdjNodes();

		for (Node dest : destNodes) {
			if (dest.province == destProv) return dest;
		}

		return null;
	}

	private boolean containsUnit(Unit unit) {
		Power p = unit.owner; 
		int powerIdx = powers.indexOf(p);

		for (UnitData unitData : nearUnits.get(powerIdx)) {
			if (unitData.unit == unit) return true;
		}

		return false;
	}

	private void computeGains(Power power) {
		//gains = c_supply * getSupplyWeight(province) * isOwnCenter(province, power) * defence;
		gains = c_supply * getSupplyWeight(province) * (1 - isOwnCenter(province, power));
		
		for (Province p : adjProvinces) {
			//gains += c_adjSupply  * getSupplyWeight(p) * isOwnCenter(province, power) * defence; 
			gains += c_adjSupply  * getSupplyWeight(p) * (1 - isOwnCenter(p, power));
		}
	}

	//TODO: take main supply in account
	private float getSupplyWeight(Province province) {
		if (province.isSupplyCenter()) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}

	private int isOwnCenter(Province province, Power power) {
		return province.getOwner() == power ? 1 : 0;  
	}

	private void computeWeight(Power power) {
		if (!isTakeable()) {
			weight = 0;
			return;
		}

		//init weight on the gains.
		weight = gains; 
	
		if (mainEnemy != -1) {
			int idx = powers.indexOf(power);

			//expected negative support
			float expectedNeg =  risk * (maxNegSupport - shared[mainEnemy]) + sharedRisk * shared[mainEnemy];

			//penalty for each unit that needs to support while it is shared.
			int numFree = support - shared[idx];
			weight -= c_shared * Math.max(expectedNeg - numFree, 0);		
		}	
	}

	public void determineSharedUnits(ArrayList<ProvinceData> provinces) {
		for (ProvinceData p : provinces) {
			if (this == p) continue;

			//Ignore provinces with no gains.
			if (p.gains == 0) continue;
			
			//check for all unit lists of all the powers.
			for (ArrayList<UnitData> subList : nearUnits) {
				
				for (UnitData unitData : subList) {  
					int powerIdx = powers.indexOf(power);

					//contains can be used since equals() is overwritten
					if (p.nearUnits.get(powerIdx).contains(unitData)) {
						shared[powerIdx]++;
						unitData.shared++;
					}
				}
			}
			
		}
	}

	
	public ArrayList<UnitData> getAvailebleUnits() {
		int powerIdx = powers.indexOf(power);
		ArrayList<UnitData> sortedList = new ArrayList<UnitData> ();
		sortedList.addAll(nearUnits.get(powerIdx));

		Collections.sort(sortedList,
					new Comparator<UnitData>(){
   						public int compare(UnitData u0, UnitData u1) {
        					return u0.shared -  u1.shared;
        				}
        			});
		
		for (UnitData unitData : sortedList) {
/*			System.out.println("prov:   " + unitData.unit.location.province.name);
			System.out.println("shared: " + unitData.shared);
			System.out.println();*/
		}

		return sortedList; 
	}
	
	public String toString() {
		String str = "";
		str += "\tname:    " + province.daide().toString() + "\n";
		str += "\tgains:   " + gains + "\n";
		str += "\tweight:  " + weight + "\n";
		str += "\tsupport: " + support + "\n";
		str += "\tmaxNegSupport: " + maxNegSupport + "\n"; 
		return str; 
	}
}