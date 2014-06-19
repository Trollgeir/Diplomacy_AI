package ai.dodo;

import java.util.ArrayList;
import java.lang.Comparable;
import kb.Node;
import kb.unit.Unit; 
import kb.*;
import kb.province.Province;

public class ProvinceData {

	public static float c_supply = 2.0f;
	public static float c_adjSupply = 1.0f;
	public static float c_shared = 0.75f;
	public static float risk = 0.75f; 
	public static float sharedRisk = 0.5f;
	public static float defence = 0.0f; 

	public int supplyType; 
	public int neighborSupply; 

	public Power power; 
	public Province province;
	public ArrayList<Power> powers;

	public ArrayList<Province> adjProvinces; 
	public ArrayList<ArrayList<Unit>> nearUnits;
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
		nearUnits = new ArrayList<ArrayList<Unit>>();
		for (int i = 0; i < powers.size(); ++i) nearUnits.add(new ArrayList<Unit>());	
		shared = new int[powers.size()];

		computeAdjProvinces();	
		addNearUnit(province);
		for (Province p : adjProvinces) addNearUnit(p);

		computeGains(power);
	}

	//be sure that determineSharedUnits() is already called.
	public void computeWeight() {
		computeSupport(power);
		computeNegSupport(power);
		computeWeight(power);
	}

	public boolean isTakeable() {
		return support > risk * (maxNegSupport - shared[mainEnemy]) + sharedRisk * shared[mainEnemy]; 
	}


	private void computeAdjProvinces() {
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.add(province.getCentralNode());
		nodes.addAll(province.coastLine);

		for (Node node : nodes) {
			computeAdjProvinces(node);
		}
	}

	private void computeAdjProvinces(Node node) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.addAll(node.landNeighbors);
		nodes.addAll(node.seaNeighbors);

	 	for (Node n : nodes) {
			Province adjProv = n.province;
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

	private void addNearUnit(Province province) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.add(province.getCentralNode());
		nodes.addAll(province.coastLine);
		
		for (Node node : nodes) {
			if (node.unit != null) {
				Unit unit = node.unit;
				Power p = unit.owner; 
				
				for (int i = 0; i < powers.size(); ++i) {
					if (p == powers.get(i) && !nearUnits.get(i).contains(unit)) {
						nearUnits.get(i).add(unit);
						return;
					}
				}

				return; 
			}
		}		
	}

	private void computeGains(Power power) {
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

	private void computeWeight(Power power) {
		int idx = powers.indexOf(power);

		//init weight on the gains.
		weight = gains; 
	
		if (mainEnemy != -1) {
			//expected negative support
			float expectedNeg =  risk * (maxNegSupport - shared[mainEnemy]) + sharedRisk * shared[mainEnemy];

			//penalty for each unit that needs to support while it is shared.
			weight -= c_shared * Math.max(expectedNeg - shared[idx], 0);		
		}	
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