package ai.dodo;

import kb.unit.Unit; 
import kb.Power; 
import java.util.ArrayList;

public class NeighborData {
	public ArrayList<Unit> units;
	public Power power; 
	public int numSCO; 

	public NeighborData(ArrayList<Unit> units, Power power) {
		this.units = units;
		this.power = power; 
		numSCO =0;
	}

	public int getSize() {
		return units.size();
	}

	public String toString() {
		String str = power.getName() + ":\n";
		str += "\tunits:  " + getSize() + "\n";
		str += "\tnumSCO: " + numSCO + "\n";
		return str;
	}
}