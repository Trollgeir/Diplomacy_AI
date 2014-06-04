package message.order;

import message.DaideList;
import kb.Node;
import kb.unit.Unit;

/**
 * The order to make a unit carry another unit.
 * @author Koen
 *
 */

public class Convoy implements Order {

	Unit		carrier, carrying;
	Node		carryTo;
	
	/**
	 * 
	 * @param carrier The unit that will carry the unit
	 * @param carrying The unit that is to be carried
	 * @param carryTo The destination
	 */
	Convoy(Unit carrier, Unit carrying, Node carryTo)
	{
		this.carrier = carrier;
		this.carrying = carrying;
		this.carryTo = carryTo;
	}
	
	@Override
	public DaideList daide() {
		DaideList ret = new DaideList();
		
		ret.add("(");
		ret.addAll(carrier.daide());
		ret.add2(")", "CVY", "(");
		ret.addAll(carrying.daide());
		ret.add2(")", "CTO");
		ret.addAll(carryTo.daide());
		
		return ret;
	}
	
}
