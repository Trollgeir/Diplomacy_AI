package kb;

import java.util.ArrayList;

import kb.order.Order;
import kb.province.Land;
import kb.province.Province;
import kb.unit.Army;

/**
 * The map/knowledge base.
 * @author Koen
 *
 */

public class Map {

	ArrayList<Province>		provinces;
	ArrayList<Power>		powers;
	
	public static void main(String[] args) {
		Power power = new Power("POW");
		Province p1, p2, p3;
		Node n1, n2, n3;
		p1 = new Land("NON", false);
		n1 = new Node(p1);
		
		p2 = new Land("NTW", false);
		n2 = new Node(p2);
		
		p3 = new Land("NTH", false);
		n3 = new Node(p3);
		
		
		n1.neighbors.add(n2);
		n2.neighbors.add(n1);
		
		n2.neighbors.add(n3);
		n3.neighbors.add(n2);
		
		n2.unit = new Army(power, n2);
		ArrayList<Order> order = n2.unit.possibleOrders();
		
		for (int i = 0; i < order.size(); i++)
		{
			System.out.println(order.get(i).daide());
		}
	}

	public Node getNode(String name)
	{
		for (int i = 0; i < provinces.size(); i++)
		{
			Province ret = provinces.get(i);
			if (ret.name() == name)
			{
				return ret.getNode();
			}
		}
		
		return null;
	}
	
}
