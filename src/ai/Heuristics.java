package ai;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kb.Map;
import kb.Node;
import kb.Power;
import kb.unit.Unit;
import negotiator.Negotiator;
import message.order.*;
import message.server.Connect;
import message.server.MapDefinition;
import message.server.Submit;
import message.server.Yes;
import communication.server.DisconnectedException;
import communication.server.Server;
import communication.server.UnknownTokenException;
import ai.AI;
import game.Game;
import kb.Names; 

public abstract class Heuristics {
	
	public LinkedBlockingQueue<Order> getOpeningMovesSpring(Power power, boolean isStandard, Map map) {
		LinkedBlockingQueue<Order> orderList = new LinkedBlockingQueue<Order>();
		
		if (isStandard) {
			Unit fleet1,fleet2,army1,army2;
			if (power.getName() == "AUS") {
				fleet1 = map.getNode("TRI").unit;
				army1 = map.getNode("BUD").unit;
				army2 = map.getNode("VIE").unit;
				
				orderList.add(new Move(fleet1, map.getNode("ALB")));
				orderList.add(new Move(army1, map.getNode("SER")));
				orderList.add(new Move(army2, map.getNode("TRI")));
				
			} else if (power.getName() == "ENG") {
				army1 = map.getNode("LVP").unit;
				fleet1 = map.getNode("EDI").unit;
				fleet2 = map.getNode("LON").unit;
				
				orderList.add(new Move(army1, map.getNode("EDI")));
				orderList.add(new Move(fleet1, map.getNode("NWG")));
				orderList.add(new Move(fleet2, map.getNode("NTH")));
				
			} else if (power.getName() == "FRA") {
				
			} else if (power.getName() == "GER") {
				
			} else if (power.getName() == "ITA") {
				
			} else if (power.getName() == "RUS") {
				
			} else if (power.getName() == "TUR") {
				
			} 
		}
		
		return orderList; 
	}
	public LinkedBlockingQueue<Order> getOpeningMovesFall(Power power, boolean isStandard, Map map) {
		LinkedBlockingQueue<Order> orderList = new LinkedBlockingQueue<Order>();
		
		if (isStandard) {
			Unit fleet1,fleet2,army1,army2;
			if (power.getName() == "AUS") {
				fleet1 = map.getNode("ALB").unit;
				army1 = map.getNode("SER").unit;
				
				orderList.add(new Move(fleet1, map.getNode("GRE")));
				orderList.add(new SupportToMove(army1, fleet1, map.getNode("GRE")));
				
			} else if (power.getName() == "ENG") {
				army1 = map.getNode("EDI").unit;
				fleet1 = map.getNode("NWG").unit;
				fleet2 = map.getNode("NTH").unit;
				
				//orderList.add(new MoveByConvoy(army1, map.getNode("NOR"), new Node[] {map.getNode("NTH")}));
				//orderList.add(new (fleet1, map.getNode("NWG")));
				orderList.add(new Move(fleet2, map.getNode("NTH")));
				
			} else if (power.getName() == "FRA") {
				
			} else if (power.getName() == "GER") {
				
			} else if (power.getName() == "ITA") {
				
			} else if (power.getName() == "RUS") {
				
			} else if (power.getName() == "TUR") {
				
			} 
		}
		
		return orderList; 
	}
	
}
