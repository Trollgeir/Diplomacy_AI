package kb.unit;

import java.util.ArrayList;

import message.DaideList;
import message.order.*;
import kb.Node;
import kb.Power;

/**
 * A unit on the board
 * @author Koen
 *
 */
public abstract class Unit {

	public Power	owner;
	public Node		location;
	public ArrayList<Node> retreatTo; 
	public boolean mustRetreat; 
	
	
	Unit(Power owner, Node location)
	{
		this.owner = owner;
		moveTo(location);
		mustRetreat = false; 
		retreatTo = new ArrayList<Node>(); 
	}
	
	public abstract boolean isArmy();
	public abstract boolean isFleet();
	
	
	public abstract boolean canMoveOn(Node node);
	
	public abstract DaideList daide();
	
	public void moveTo(Node node)
	{
		location = node;
		node.setUnit(this);
	}
	
	public ArrayList<Node> getAdjNodes() {
		ArrayList<Node> result = new ArrayList<Node>();
		result.add(location);

		if (isFleet()) result.addAll(location.seaNeighbors);
		else result.addAll(location.landNeighbors);
		return result; 
	}

	public ArrayList<Order> possibleOrders()
	{
		//TODO: Convoy, move by convoy
		
		ArrayList<Order> retList = new ArrayList<Order>();
		
		/*
		retList.add(new Hold(this));
		
		//Check all adjacent nodes
		for (int i = 0; i < location.neighbors.size(); i++)
		{
			Node adjacentNode = location.neighbors.get(i);
			
			if (this.canMoveOn(adjacentNode))
			{
				retList.add(new Move(this, adjacentNode));
			}
			
			
			//Support to move
			for (int j = 0; j < adjacentNode.neighbors.size(); j++)
			{
				//Movement support
				if (//A unit cannot support itself
					adjacentNode.neighbors.get(j) != location &&
					adjacentNode.neighbors.get(j).unit != null)
				{
					retList.add(new SupportToMove(this, adjacentNode.unit, adjacentNode));
				}
			}
			
			//Support to hold
			if (adjacentNode.unit != null)
			{
				retList.add(new SupportToHold(this, adjacentNode.unit));
			}
		}
		*/
		
		return retList;
	}
}
