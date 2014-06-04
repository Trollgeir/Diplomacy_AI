package message.order;

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
	public ArrayList<String> daide() {
		ArrayList<String> ret = new ArrayList<String>();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add(")");
		ret.add("CTO");
		ret.addAll(destination.daide());
		ret.add("VIA");
		ret.add("(");
		
		
		for (int i = 0; i < via.size(); i++)
			ret.addAll(via.get(i).daide());
		
		ret.add(")");
		
		return ret;
	}

}
