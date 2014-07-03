package ai.dodo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.lang.Comparable;
import kb.Node;
import kb.unit.Unit; 
import kb.*;
import kb.functions.MapInfo; 
import kb.province.Province;

public class ProvinceData {

	public static float c_smooth = 0.25f;
	public static float c_threat = 1.0f;
	
	public static float c_shared = 0.0f;
	//public static float risk = 1.0f; 
	//public static float sharedRisk = 1.0f;
	public static float defence = 1.0f; 

	public static float takeOverRisk = 0.5f;

	public static float c_normalNotOurs = 3.0f; 
	public static float c_homeNotOurs = 3.2f; 

	public static float c_normalOurs = 0.25f; 
	public static float c_homeOurs = 0.30f; 

	public static float c_normalOursThreat = 3.0f; 
	public static float c_homeOursThreat = 3.2f; 

	public static float c_homeOursTaken = 5.0f; 

	public static float c_threatThreshold = 1.10f; 

	public static float c_counter = 5.5f; 

	public static float c_kill = 5.0f; 

	public static float attackThreshold = 3.0f; 

/*
	public static float c_normalSuply = 1.0f;
	public static float c_homeSuply = 1.2f; 

	public static float c_takenHomeSuply = 5.0f; 

	public static float c_enemySCO = 1.0f;
	public static float c_ownSCO = 0.0f; 

	public static float c_defendHome = 3.0f;
	public static float c_defendNormal = 1.0f;*/

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
	public float supplyGain;
	public float counterGain; 
	public float defendGain; 
	public float killGain; 
	public float smoothedGains; 

	public float[] willAttack;
	public float[] willDefend;  
	
	public ProvinceData(Power power, Province province, ArrayList<Power> powers, 
						float[] willAttack, float[] willDefend) {
		
		this.power = power; 
		this.province = province;
		this.powers = powers; 

		this.willAttack = willAttack;
		this.willDefend =willDefend; 

		adjProvinces = new ArrayList<Province>();
		adjProvDatas = new ArrayList<ProvinceData>();
		nearUnits = new ArrayList<ArrayList<UnitData>>();
	
		for (int i = 0; i < powers.size(); ++i) {
			nearUnits.add(new ArrayList<UnitData>());	
		}
		shared = new int[powers.size()];

		computeAdjProvinces();
	}

	public void computeGains(ArrayList<ProvinceData> provinces) {
		ArrayList<ProvinceData> order0 = new ArrayList<ProvinceData>();
		order0.add(this);
		ArrayList<ArrayList<ProvinceData>> provinceKernel = MapInfo.getProvinceKernel(order0, 2);

		
		supplyGain = getSupplyGain();
	
		counterGain = getCounterGain(); 

		float[] weightKernel = {1, 1f, 0.25f};
		defendGain = getDefendSupplyGain(provinceKernel, weightKernel);
		
		killGain = getKillGain(provinceKernel);
 
		gains = supplyGain + defendGain + counterGain + killGain;
	}

	public float getKillGain(ArrayList<ArrayList<ProvinceData>> provinceKernel) {
		Unit u = province.getUnit(); 
		if (u == null || u.owner == power) return 0; 		

		Power unitOwner = u.owner; 

		//Search for a team unit
		for (int order = 1; order < provinceKernel.size(); ++order) {
			for (ProvinceData provData : provinceKernel.get(order)) {
				Unit unit = provData.province.getUnit();
				if (unit != null && unit.owner == unitOwner) {
					return 0; 
				}
			}
		}

		int numEscapeRoutes = 0; 
		for (Province prov : adjProvinces) {
			Node node = findDestNode(u, prov);
			if (node != null && prov.getUnit() == null) numEscapeRoutes++; 
		}
		
		if (support > 0 && numEscapeRoutes <= 1) System.out.println("I AM GOING TO KILL YOU: " + province.getName());
		
		int idx = powers.indexOf(unitOwner);
		return numEscapeRoutes <= 1 ? willAttack[idx] * c_kill : 0;  
	}

	public float getCounterGain() {
		Unit u = province.getUnit(); 
		if (u == null || u.owner == power) return 0; 

		int idx = powers.indexOf(u.owner);
		float w = willDefend[idx];  

		for (ProvinceData provData : adjProvDatas) {
			Power isHomeSCO = provData.isHomeSCO(province);
			if (isHomeSCO == power) return w * c_counter; 
		}
		return 0; 
	}

	public float getSupplyGain() {
		boolean isOwn = province.getOwner() == power; 
		boolean isSCO = province.isSupplyCenter(); 
		Power isHomeSCO = isHomeSCO(province);
		
		//If it's not a supply center, it does not get supply center gain
		if (!isSCO) return 0; 

		float w;
		if (getProvinceOwner() != -1 && !isOwn) {
			w = willAttack[getProvinceOwner()];
		} else {
			w = 1; 
		}

		if (isHomeSCO != null) {
			if (isHomeSCO == power) {
				return isOwn ? c_homeOurs : c_homeOursTaken; 
			} else {
				if (isOwn) return c_homeOurs;
				return isHomeSCO == province.getOwner() ? w * c_homeNotOurs : w * c_normalNotOurs;   
			}
		} else {
			//This is not a home sco
			return isOwn ? c_normalOurs : w * c_normalNotOurs; 
		}

	}

	public float getDefendSupplyGain(ArrayList<ArrayList<ProvinceData>> provinceKernel, float[] weightKernel) {
		boolean isSCO = province.isSupplyCenter(); 
		Power isHomeSCO = isHomeSCO(province);
		
		if (!isSCO) return 0; 

		//if (province.getOwner != power && isHomeSCO != power) return 0;
		if (province.getOwner() != power) return 0; 

		for (int i = 0; i < powers.size(); ++i) {
			if (willDefend[i] == 0) {
				System.out.println("I do not care about " + powers.get(i).getName());
			}
		}

		float c_supplyType = isHomeSCO != null ? c_homeOursThreat : c_normalOursThreat; 
		float threat = 0; 

		float[] enemyWeight = new float[powers.size()]; 
		for (int  i = 0; i < powers.size(); ++i) enemyWeight[i] = 0;

		for (int i = 0; i < provinceKernel.size(); ++i) {
			for (ProvinceData provData : provinceKernel.get(i)) {
				Unit unit = provData.province.getUnit();

				if (unit != null && unit.owner != power) {
					int powerIdx = powers.indexOf(unit.owner);
					enemyWeight[powerIdx] += willDefend[powerIdx] * weightKernel[i];
				}
			}
		}

		int bestIdx = -1;
		float bestWeight = 0; 
		for (int i = 0; i < powers.size(); ++i) {
			if (powers.get(i) == power) continue;

			if (enemyWeight[i] > bestWeight) {
				bestWeight = enemyWeight[i];
				bestIdx = i; 
			}
		}  

		if (bestIdx != -1) {
			System.out.println("Threat " + province.getName() + ": " + powers.get(bestIdx).getName());
			System.out.println("\t" + bestWeight);
		}

		threat = bestIdx == -1 ? 0 : bestWeight; 
		if (threat > c_threatThreshold) return c_supplyType; 

		return 0;  
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

		int ownerIdx = getProvinceOwner(); 
		if (ownerIdx != -1 && ownerIdx != powers.indexOf(power)) {
			if (willAttack[ownerIdx] < 1 && weight < attackThreshold) weight = 0;
		}

		weight *= weight; 
	}

	public void computeSmoothedGains() {
		
		float total = 0;
		float max = 0; 
		int num = 0;
		for (ProvinceData provData : adjProvDatas) {
			if (provData.gains > max) {
				max = provData.gains; 
			}
			total += c_smooth * provData.gains; 
			num++; 
		}

		smoothedGains = Math.max(gains, max * 0.5f);
		//if (num > 0) smoothedGains += total / num; 
	}

	public void computeOwnSupport() {
		int idx = powers.indexOf(power);
		support = nearUnits.get(idx).size();
	}

	public void computeNegSupportValues() { 
		computeNegSupport(power);
		computeSupportNeeded();
	}

	private void computeNegSupport(Power power) {
		maxNegSupport = 0;
		mainEnemy = -1;

		for (int i = 0; i < powers.size(); ++i) {
			if (power != powers.get(i) &&  willDefend[i] * nearUnits.get(i).size() > maxNegSupport) {
				maxNegSupport = (int)(willDefend[i] * nearUnits.get(i).size());
				mainEnemy = i; 
			}
		}
	}

	//be sure that determineSharedUnits() is already called.
	public void computeWeight() { 
		computeWeight(power);
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
			
			if (Math.random() >= Math.pow(takeOverRisk, k)) {
				possibleNegSupport--;
				supportNeeded++;
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

	private Power isHomeSCO(Province province) {
		if (!province.isSupplyCenter()) return null;

		for (Power p : powers) { 
			if (p.homeProvinces.contains(province)) {
				return p;
			}
		}

		return null; 
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
						//shared[powerIdx]++;
						unitData.shared++;
					}
				}
			}
			
		}
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
				 	
				 	//remove the unit from the nearUnits
					removeList.add(unitData);
				}
			}
		} 

		nearUnits.get(idx).removeAll(removeList);
	}

	//Is for the belief stuff
	//returns the owner of the unit on this province.
	//otherwise the owner of the SCO
	//otherwise neutral
	public int getProvinceOwner() {
		Unit unit = province.getUnit();
		if (unit != null) return powers.indexOf(unit.owner);

		if (province.isSupplyCenter()) return powers.indexOf(province.getOwner()); 	

		return -1; 	
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

	public void computeAdjProvDatas(ArrayList<ProvinceData> provDatas) {
		for (ProvinceData provData : provDatas) {
			for (Province prov : adjProvinces) {
				if (provData.province == prov) adjProvDatas.add(provData);
			}
		}

		addNearUnits(this);
		for (ProvinceData p : adjProvDatas) addNearUnits(p);
	}



	public String toString() {
		String str = "";
		str += "\tname:           " + province.daide().toString() + "\n";
		str += "\tsupplyGain:     " + supplyGain + "\n";
		str += "\tdefendGain:     " + defendGain + "\n";
		str += "\tgains:          " + gains + "\n";
		str += "\tsmoothed gains: " + smoothedGains + "\n";
		str += "\tweight:         " + weight + "\n";
		str += "\tsupport:        " + support + "\n";
		str += "\tsupportNeeded:  " + supportNeeded + "\n"; 
		str += "\tmaxNegSupport:  " + maxNegSupport + "\n"; 
		str += "\tmainEnemy:      " + mainEnemy + "\n";
		return str; 
	}
}
