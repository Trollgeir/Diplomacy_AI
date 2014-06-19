package ai.dodo;

import java.util.ArrayList;
import java.lang.Comparable;
import kb.Node;
import kb.unit.Unit; 
import kb.*;

class AdjSupplyCenter implements Comparable {

	public Node n;
	public ArrayList<Unit> adjUnits; 
	public ArrayList<Power> powers;
	
	public int[] maxResistance; 
	public ArrayList<ArrayList<Unit>> adjEnemyUnits; 
	public int mainEnemy; 

	public boolean isTaken; 
	public int supportNeeded = 0; 

	public float weight = 0; 

	public AdjSupplyCenter(Node n, ArrayList<Power> powers) {
		this.n = n; 
		this.powers = powers;
		adjUnits = new ArrayList<Unit>();		

		maxResistance = new int[powers.size()];
		adjEnemyUnits = new ArrayList<ArrayList<Unit>>();
		for (int i = 0; i < powers.size(); ++i) adjEnemyUnits.add(new ArrayList<Unit>());

		isTaken = n.province.getOwner() != null; 
	}

	public AdjSupplyCenter(Node n, Unit u, ArrayList<Power> powers) {
		this(n, powers);
		adjUnits.add(u); 
	}


	public int compareTo(Object o) {
        AdjSupplyCenter c = (AdjSupplyCenter) o;
        return (int)(1000 * weight - 1000 * c.weight);
    }

	public boolean isTakable(Power p) {
		if (isTaken && n.province.getOwner() == p) return false; 

		int max = 0; 
		int enemy = -1; 
		for (int i = 0; i < powers.size(); ++i) {
			if (powers.get(i) == p) continue;
			if (maxResistance[i] > max) {
				max = maxResistance[i];
				enemy = i; 
			}
		}

		supportNeeded = max;
		mainEnemy = enemy;
		return supportNeeded < adjUnits.size() || (!isTaken && supportNeeded == adjUnits.size());
	}

	public void addPower(Power p) {
		for (int i = 0; i < powers.size(); ++i) {
			if (p == powers.get(i)) {
				maxResistance[i]++;
				return;
			}
		}
	}

	public void addEnemyUnit(Unit unit) {
		Power p = unit.owner; 
		for (int i = 0; i < powers.size(); ++i) {
			if (p == powers.get(i)) {
				adjEnemyUnits.get(i).add(unit);
				maxResistance[i]++;
				return;
			}
		}
	}

	public int getAvailableForce() {
		return adjUnits.size();
	}
}