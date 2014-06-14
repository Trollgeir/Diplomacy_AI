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

public class Heuristics {
	
	public static LinkedBlockingQueue<Order> getOpeningMovesSpring(Power power, boolean isStandard, Map map) {
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
				fleet1 = map.getNode("BRE").unit;
				army1 = map.getNode("MAR").unit;
				army2 = map.getNode("PAR").unit;
				
				orderList.add(new Move(fleet1, map.getNode("MAO")));
				orderList.add(new SupportToMove(army1, army2, map.getNode("BUR")));
				orderList.add(new Move(army2, map.getNode("BUR")));
				
			} else if (power.getName() == "GER") {
				fleet1 = map.getNode("KIE").unit;
				army1 = map.getNode("BER").unit;
				army2 = map.getNode("MUN").unit;
				
				// default
				orderList.add(new Move(army1, map.getNode("KIE")));
				orderList.add(new Move(army2, map.getNode("RUH")));
				
				// england is 'objective', france ally, russia neutral, most popular (very non-offensive)
				orderList.add(new Move(fleet1, map.getNode("DEN")));
				
				// france is 'objective', england ally, russia neutral, second most popular (offensive to france)
				//orderList.add(new Move(fleet1, map.getNode("HOL")));
				
			} else if (power.getName() == "ITA") {
				fleet1 = map.getNode("NAP").unit;
				army1 = map.getNode("ROM").unit;
				army2 = map.getNode("VEN").unit;				
				
				// default
				orderList.add(new Move(fleet1, map.getNode("ION")));
				
				// alliance with austria, anti turkey
				orderList.add(new Move(army1, map.getNode("APU")));
				orderList.add(new Hold(army2));
				
				// anti austria (almost never happens)
				//orderList.add(new Move(army1, map.getNode("VEN")));
				//orderList.add(new Move(army2, map.getNode("TYR")));			
				
			} else if (power.getName() == "RUS") {
				fleet1 = map.getNode("STP","SCS").unit;
				fleet2 = map.getNode("SEV").unit;
				army1 = map.getNode("MOS").unit;
				army2 = map.getNode("WAR").unit;
				
				// default
				orderList.add(new Move(fleet1, map.getNode("BOT")));
				orderList.add(new Move(army1, map.getNode("UKR")));
				orderList.add(new Move(army2, map.getNode("GAL")));
				
				// Attack on Austria, Allied turkey
				orderList.add(new Move(fleet2, map.getNode("RUM")));
				
				// Attack on Turkey 
				//orderList.add(new Move(fleet2, map.getNode("BLA")));
				
				
			} else if (power.getName() == "TUR") {
				fleet1 = map.getNode("ANK").unit;
				army1 = map.getNode("CON").unit;
				army2 = map.getNode("SMY").unit;
				
				// default
				orderList.add(new Move(army1, map.getNode("BUL")));
				
				// western push
				orderList.add(new Move(fleet1, map.getNode("CON")));
				orderList.add(new Hold(army2));
				
				// unsure of Russia but weak
				//orderList.add(new Move(fleet1, map.getNode("BLA")));
				//orderList.add(new Move(army2, map.getNode("CON")));
			} 
		}
		
		return orderList; 
	}
	public static LinkedBlockingQueue<Order> getOpeningMovesFall(Power power, boolean isStandard, Map map) {
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
				
				orderList.add(new MoveByConvoy(army1, map.getNode("NOR"), new Node[] {map.getNode("NTH")}));
				orderList.add(new Move(fleet1, map.getNode("BAR")));
				orderList.add(new Convoy(fleet2, army1, map.getNode("NOR")));
				
			} else if (power.getName() == "FRA") {
				fleet1 = map.getNode("MAO").unit;
				army1 = map.getNode("MAR").unit;
				army2 = map.getNode("BUR").unit;
				
				orderList.add(new Move(fleet1, map.getNode("POR")));
				orderList.add(new Move(army1, map.getNode("SPA")));
				orderList.add(new Move(army2, map.getNode("BEL")));
				
			} else if (power.getName() == "GER") {
				// openings depend too much on alliances
			
			} else if (power.getName() == "ITA") {
				// openings depend too much on alliances
				
			} else if (power.getName() == "RUS") {
				// openings depend too much on alliances
				
			} else if (power.getName() == "TUR") {
				// openings depend too much on alliances
			} 
		}
		
		return orderList; 
	}
	
}
