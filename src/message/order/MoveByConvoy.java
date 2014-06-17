package message.order;

import message.DaideList;
import kb.Node;
import kb.unit.Unit;

/**
 * The order for an army to move with the assistance of a fleet.
 * @author Koen
 *
 */

public class MoveByConvoy extends Order {

	Unit		unit;
	Node		destination;
	Node[] 		via;
	
	/**
	 * 
	 * @param unit The unit that is to be moved.
	 * @param destination The final destination of the convoy.
	 * @param via The list of nodes that the fleet will pass through.
	 */
	public MoveByConvoy(Unit unit, Node destination, Node[] via)
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
		
		
		for (int i = 0; i < via.length; i++)
			ret.addAll(via[i].daide());
		
		ret.add(")");
		
		return ret;
	}

}
