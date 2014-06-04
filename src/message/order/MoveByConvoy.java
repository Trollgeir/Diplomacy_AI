package message.order;

import java.util.ArrayList;

import message.DaideList;
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
	public DaideList daide() {
		DaideList ret = new DaideList();
		
		ret.add("(");
		ret.addAll(unit.daide());
		ret.add2(")", "CTO");
		ret.addAll(destination.daide());
		ret.add2("VIA", "(");
		
		
		for (int i = 0; i < via.size(); i++)
			ret.addAll(via.get(i).daide());
		
		ret.add(")");
		
		return ret;
	}

}
