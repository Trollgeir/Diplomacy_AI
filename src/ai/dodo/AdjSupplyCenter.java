package ai.dodo;

import java.util.ArrayList;
import kb.Node;
import kb.unit.Unit; 
import kb.*;

class AdjSupplyCenter {

	public Node n;
	public ArrayList<Unit> adjUnits; 
	public ArrayList<Power> powers;
	public int[] maxResistance; 
	public boolean isTaken; 
	public int supportNeeded = 0; 

	public AdjSupplyCenter(Node n, ArrayList<Power> powers) {
		this.n = n; 
		this.powers = powers;
		maxResistance = new int[powers.size()];
		adjUnits = new ArrayList<Unit>();
		isTaken = n.province.getOwner() != null; 
	}

	public AdjSupplyCenter(Node n, Unit u, ArrayList<Power> powers) {
		this(n, powers);
		adjUnits.add(u); 
	}

	public boolean isTakable(Power p) {
		if (isTaken && n.province.getOwner() == p) return false; 

		int max = 0; 
		for (int i = 0; i < powers.size(); ++i) {
			if (powers.get(i) == p) continue;
			if (maxResistance[i] > max) max = maxResistance[i];
		}

		supportNeeded = max;
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

	public int getAvailableForce() {
		return adjUnits.size();
	}
}