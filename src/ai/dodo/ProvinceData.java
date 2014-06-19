package ai.dodo;

import java.util.ArrayList;
import java.lang.Comparable;
import kb.Node;
import kb.unit.Unit; 
import kb.*;
import kb.province.Province;

public class ProvinceData {

	public static float c_supply;
	public static float c_adjSupply;
	public static float c_shared;
	public static float c_enemy;
	public static float risk; 
	public float sharedRisk;
	public static float defence; 

	public int supplyType; 
	public int neighborSupply; 

	public Province province;
	public ArrayList<Power> powers;

	public ArrayList<Province> adjProvinces; 
	public ArrayList<ArrayList<Unit>> nearUnits;
	public ArrayList<ArrayList<Node>> unitNodes;
	public int[] shared;  

	public int support; 
	public int maxNegSupport; 
	public int mainEnemy; 

	public float weight; 
	public float gains; 

	public ProvinceData(Province province, ArrayList<Power> powers) {
		//TODO set supply type

		this.province = province;
		this.powers = powers; 

		adjProvinces = new ArrayList<Province>();

		nearUnits = new ArrayList<ArrayList<Unit>>();
		for (int i = 0; i < powers.size(); ++i) nearUnits.add(new ArrayList<Unit>());	

		shared = new int[powers.size()];		
	}

	public void computeAdjProvinces() {
		//TODO
	}

	public void computeNeighborSupply() {
		neighborSupply =  0;
		for (Province p : adjProvinces) {
			if (p.isSupplyCenter()) neighborSupply++; 
		}
	}

	public void computeNegSupport(Power power) {
		maxNegSupport = 0;
		mainEnemy = -1;

		for (int i = 0; i < powers.size(); ++i) {
			if (power != powers.get(i) && nearUnits.get(i).size() > maxNegSupport) {
				maxNegSupport = nearUnits.get(i).size();
				mainEnemy = i; 
			}
		}
	}

	public void computeSupport(Power power) {
		for (int i = 0; i < powers.size(); ++i) {
			if (power == powers.get(i)) {
				support = nearUnits.get(i).size();
			}
		}
	}

	public void addAdjUnit(Unit unit, Node node) {
		Power p = unit.owner; 
		for (int i = 0; i < powers.size(); ++i) {
			if (p == powers.get(i) && !nearUnits.get(i).contains(unit)) {
				nearUnits.get(i).add(unit);
				unitNodes.get(i).add(node);
				return;
			}
		}
	}

	public void computeGains(Power power) {
		gains = c_supply * getSupplyWeight(province) * isOwnCenter(province, power) * defence;
		
		for (Province p : adjProvinces) {
			gains += c_adjSupply  * getSupplyWeight(p) * isOwnCenter(province, power) * defence; 
		}
	}

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

	public void computeWeight(Power power) {
		int idx = powers.indexOf(power);

		//init weight on the gains.
		weight = gains; 
	
		//expected negative support
		float expectedNeg =  risk * (maxNegSupport - shared[mainEnemy]) + sharedRisk * shared[mainEnemy];

		//penalty for each unit that needs to support while it is shared.
		weight -= c_shared * Math.max(expectedNeg - shared[idx], 0);			
	}

	public boolean isTakeable() {
		return support > risk * (maxNegSupport - shared[mainEnemy]) + sharedRisk * shared[mainEnemy]; 
	}

	public static void determineSharedUnits(ArrayList<ProvinceData> provinces) {
		for (ProvinceData p0 : provinces) {
			if (p0.gains == 0) continue;
			
			for (ProvinceData p1 : provinces) {
				if (p0 == p1 || p1.gains == 0) continue;

				for (ArrayList<Unit> subList :  p0.nearUnits) {
					for (Unit u : subList) {
						int powerIdx = p0.powers.indexOf(u.owner);

						if (p1.nearUnits.get(powerIdx).contains(u)) {
							p0.shared[powerIdx]++;
						}
					}
				}
			}
		}
	}
}