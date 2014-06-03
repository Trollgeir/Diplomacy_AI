package kb.order;

import java.util.ArrayList;

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
	public ArrayList<String> daide() {
		ArrayList<String> ret = new ArrayList<String>();
		
		ret.add("(");
		ret.addAll(carrier.daide());
		ret.add(")");
		ret.add("CVY");
		ret.add("(");
		ret.addAll(carrying.daide());
		ret.add(")");
		ret.add("CTO");
		ret.addAll(carryTo.daide());
		
		return ret;
	}
	
}
