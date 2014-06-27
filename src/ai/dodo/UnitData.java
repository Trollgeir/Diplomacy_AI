package ai.dodo; 

import java.util.Comparator;
import java.lang.Comparable;
import kb.Node;
import kb.unit.Unit; 
import kb.*;

public class UnitData {

	public Unit unit;
	public int shared;
	public Node destNode;
	public ProvinceData provData; 

	public UnitData(Unit unit, Node destNode, ProvinceData provData) {
		this.unit = unit;
		this.destNode = destNode; 
		this.provData = provData;
		shared = 0;
	}

	@Override
	public boolean equals(Object o) {
		UnitData u = (UnitData)o; 
		return u.unit == unit; 
	}
}