package kb.order;

import java.util.ArrayList;

import kb.Node;
import kb.unit.Unit;

/**
 * The order for an army to move with the assistance of a fleet.
 * @author Koen
 *
 */

public class MoveByConvoy implements Order {

	Unit				unit;
	Node				destination;
	ArrayList<Node> 	via;
	
	/**
	 * 
	 * @param unit The unit that is to be moved.
	 * @param destination The final destination of the convoy.
	 * @param via The list of nodes that the fleet will pass through.
	 */
	MoveByConvoy(Unit unit, Node destination, ArrayList<Node> via)
	{
		this.unit = unit;
		this.destination = destination;
		this.via = via;
	}
	
	@Override
	public String daide() {
		String ret = "(" + unit.daide() + ") CTO " + destination.daide() + " VIA (";
		
		for (int i = 0; i < via.size(); i++)
			ret += via.get(i).daide() + " ";
		
		ret += ")";
		
		return ret;
	}

}
