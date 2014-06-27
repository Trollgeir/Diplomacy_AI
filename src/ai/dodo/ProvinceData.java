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

	public static float c_smooth = 0.25f;
	public static float c_threat = 0.5f;
	
	public static float c_shared = 0.25f;
	//public static float risk = 1.0f; 
	//public static float sharedRisk = 1.0f;
	public static float defence = 1.0f; 

	public static float takeOverRisk = 0.5f;

	public static float normalSuply = 2.5f;
	public static float homeSuply = 4.0f; 

	public static float c_neutralCSO = 1.0f;
	public static float c_ownedCSO = 1.5f;

	public int neighborSupply; 

	public Power power; 
	public Province province;
	public ArrayList<Power> powers;
	public ArrayList<ProvinceData> adjProvDatas; 
	public ArrayList<Province> adjProvinces; 
	
	public ArrayList<ArrayList<UnitData>> nearUnits;
	public int[] shared;  

	public int support; 
	public int supportNeeded;
	public int maxNegSupport; 
	public int mainEnemy; 
	//public float expectedNeg;

	public float weight; 
	public float gains; 
	public float smoothedGains; 

	public ProvinceData(Power power, Province province, ArrayList<Power> powers) {
		//TODO set supply type

		this.power = power; 
		this.province = province;
		this.powers = powers; 

		adjProvinces = new ArrayList<Province>();
		adjProvDatas = new ArrayList<ProvinceData>();
		nearUnits = new ArrayList<ArrayList<UnitData>>();
	
		for (int i = 0; i < powers.size(); ++i) {
			nearUnits.add(new ArrayList<UnitData>());	
		}
		shared = new int[powers.size()];

		computeAdjProvinces();
		//computeGains(power);
	}

	public void computeAdjProvDatas(ArrayList<ProvinceData> provDatas) {
		for (ProvinceData provData : provDatas) {
			for (Province prov : adjProvinces) {
				if (provData.province == prov) adjProvDatas.add(provData);
			}
		}

		addNearUnits(this);
		for (ProvinceData p : adjProvDatas) addNearUnits(p);
	}

	private void computeWeight(Power power) {
		if (!isTakeable()) {
			weight = 0;
			return;
		}

		//init weight on the gains.
		weight = smoothedGains; 

		int idx = powers.indexOf(power);
		for (UnitData unit : nearUnits.get(idx)) {
			if (unit.provData == this) continue;
			weight -= c_shared * unit.provData.smoothedGains;
		}

		if (weight < 0) weight = 0; 

		//if (weight >= c_neutralCSO * normalSuply && mainEnemy == -1) weight += 1;

		weight *= weight; 
	}

	public void computeGains() {
		//gains = c_supply * getSupplyWeight(province) * isOwnCenter(province, power) * defence;
		int isOwn = isOwnCenter(province, power);
		gains = getSupplyWeight(province) * (1 - isOwn);

		//add gain when our SCO is threaded. 
		if (getSupplyWeight(province) * isOwn > 0) {
			gains += c_threat * maxNegSupport;
		}
	}

	public void computeSmoothedGains() {
		smoothedGains = gains;
		for (ProvinceData provData : adjProvDatas) {
			smoothedGains += c_smooth * provData.gains; 
		}
	}

	public void computeSupportValues() { 
		computeSupport(power);
		computeNegSupport(power);
		computeSupportNeeded();
	}

	//be sure that determineSharedUnits() is already called.
	public void computeWeight() { 
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
		return support >= supportNeeded; 
	}

	public void computeSupportNeeded() {
		if (mainEnemy == -1) {
			supportNeeded = 1;
			return; 
		}

		int knownSupport;
		int enemyIdx;
		int possibleNegSupport;
		
		//if there is an enemy unit on the province, we will need at least 2 units for the attack.
		//if the province is free, we need 1 unit to move in.
		if (province.unit() != null && province.unit().owner != power) {
			knownSupport = 2;
			enemyIdx = powers.indexOf(province.unit().owner);
			//maybe take enemy units in account
			possibleNegSupport = nearUnits.get(enemyIdx).size() - 1; 
		} else {
			knownSupport = 1; 
			enemyIdx = mainEnemy; 
			possibleNegSupport = nearUnits.get(enemyIdx).size();  	
		}

		supportNeeded = knownSupport;
		int supportLeft = support - knownSupport;
		int k = possibleNegSupport;

		for (int i = 0; i < supportLeft; ++i) {
			if (possibleNegSupport == 0) break;
			
			if (Math.random() > Math.pow(1 - takeOverRisk, k)) {
				possibleNegSupport--;
				supportNeeded++;
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

		/*
		if (mainEnemy == -1) {
			expectedNeg = 0; 
		} else{
			expectedNeg =  risk * (maxNegSupport - shared[mainEnemy]) + sharedRisk * shared[mainEnemy];
		}
		*/
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
	private void addNearUnits(ProvinceData nearProv) {
		Unit unit = nearProv.province.getUnit();
		if (unit == null) return;

		Node destNode = findDestNode(unit, province);
		if (destNode != null) {
			int powerIdx = powers.indexOf(unit.owner);
			nearUnits.get(powerIdx).add(new UnitData(unit, destNode, nearProv));
		}
	
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

	//TODO: take main supply in account
	private float getSupplyWeight(Province province) {
		if (!province.isSupplyCenter()) return 0;

		float c_supply = province.getOwner() != null ? c_ownedCSO : c_neutralCSO; 

		for (Power p : powers) {
			if (p.homeProvinces.contains(province)) return c_supply * homeSuply; 
		}

		return c_supply * normalSuply;
	}

	private int isOwnCenter(Province province, Power power) {
		return province.getOwner() == power ? 1 : 0;  
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

		return sortedList; 
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


	public String toString() {
		String str = "";
		str += "\tname:    " + province.daide().toString() + "\n";
		str += "\tgains:   " + gains + "\n";
		str += "\tsmoothed gains:   " + smoothedGains + "\n";
		str += "\tweight:  " + weight + "\n";
		str += "\tsupport: " + support + "\n";
		str += "\tsupportNeeded: " + supportNeeded + "\n"; 
		str += "\tmaxNegSupport: " + maxNegSupport + "\n"; 
		str += "\tmainEnemy:     " + mainEnemy + "\n";
		return str; 
	}
}