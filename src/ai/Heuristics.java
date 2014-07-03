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
import message.order.*;
import message.server.Connect;
import message.server.MapDefinition;
import message.server.Submit;
import message.server.Yes;
import communication.server.DisconnectedException;
import communication.server.Server;
import communication.server.UnknownTokenException;
import ai.AI;
import ai.dodo.Negotiator;
import game.Game;
import kb.Names; 
import java.util.ArrayList;
import java.util.Set;

public class Heuristics {
	
	public static LinkedBlockingQueue<Order> getOpeningMovesSpring(Power power, boolean isStandard, Map map) {
		LinkedBlockingQueue<Order> orderList = new LinkedBlockingQueue<Order>();
		
		if (isStandard) {
			System.out.println(power.getName() + " game is standard."); 
			Unit fleet1,fleet2,army1,army2;
			if (power.getName().equals("AUS")) {
				System.out.println("Loading AUS opening heuristics!");  
				fleet1 = map.getNode("TRI").unit;
				army1 = map.getNode("BUD").unit;
				army2 = map.getNode("VIE").unit;
				
				orderList.add(new Move(fleet1, map.getNode("ALB")));
				orderList.add(new Move(army1, map.getNode("SER")));
				orderList.add(new Move(army2, map.getNode("TRI")));
				
			} else if (power.getName().equals("ENG")) {
				System.out.println("Loading ENG opening heuristics!");  
				army1 = map.getNode("LVP").unit;
				fleet1 = map.getNode("EDI").unit;
				fleet2 = map.getNode("LON").unit;
				
				orderList.add(new Move(army1, map.getNode("EDI")));
				orderList.add(new Move(fleet1, map.getNode("NWG")));
				orderList.add(new Move(fleet2, map.getNode("NTH")));
				
			} else if (power.getName().equals("FRA")) {
				System.out.println("Loading FRA opening heuristics!");  
				fleet1 = map.getNode("BRE").unit;
				army1 = map.getNode("MAR").unit;
				army2 = map.getNode("PAR").unit;
				
				orderList.add(new Move(fleet1, map.getNode("MAO")));
				orderList.add(new SupportToMove(army1, army2, map.getNode("BUR").province));
				orderList.add(new Move(army2, map.getNode("BUR")));
				
			} else if (power.getName().equals("GER")) {
				System.out.println("Loading GER opening heuristics!");  
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
				
			} else if (power.getName().equals("ITA")) {
				System.out.println("Loading ITA opening heuristics!");  
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
				
			} else if (power.getName().equals("RUS")) {
				System.out.println("Loading RUS opening heuristics!");  
				fleet1 = map.getNode("STP","SCS").unit;
				fleet2 = map.getNode("SEV").unit;
				army1 = map.getNode("MOS").unit;
				army2 = map.getNode("WAR").unit;
				
				// default
				orderList.add(new Move(fleet1, map.getNode("GOB")));
				orderList.add(new Move(army1, map.getNode("UKR")));
				orderList.add(new Move(army2, map.getNode("GAL")));
				
				// Attack on Austria, Allied turkey
				orderList.add(new Move(fleet2, map.getNode("RUM")));
				
				// Attack on Turkey 
				//orderList.add(new Move(fleet2, map.getNode("BLA")));
				
				
			} else if (power.getName().equals("TUR")) {
				System.out.println("Loading TUR opening heuristics!");  
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
			if (power.getName().equals("AUS")) {
				fleet1 = map.getNode("ALB").unit;
				army1 = map.getNode("SER").unit;
				
				orderList.add(new Move(fleet1, map.getNode("GRE")));
				//orderList.add(new SupportToMove(army1, fleet1, map.getNode("GRE")));
				
			} else if (power.getName().equals("ENG")) {
				army1 = map.getNode("EDI").unit;
				fleet1 = map.getNode("NWG").unit;
				fleet2 = map.getNode("NTH").unit;
				
				orderList.add(new MoveByConvoy(army1, map.getNode("NOR"), new Node[] {map.getNode("NTH")}));
				orderList.add(new Move(fleet1, map.getNode("BAR")));
				orderList.add(new Convoy(fleet2, army1, map.getNode("NOR")));
				
			} else if (power.getName().equals("FRA")) {
				fleet1 = map.getNode("MAO").unit;
				army1 = map.getNode("MAR").unit;
				army2 = map.getNode("BUR").unit;
				
				orderList.add(new Move(fleet1, map.getNode("POR")));
				orderList.add(new Move(army1, map.getNode("SPA")));
				orderList.add(new Move(army2, map.getNode("BEL")));
				
			} else if (power.getName().equals("GER")) {
				// openings depend too much on alliances
			
			} else if (power.getName().equals("ITA")) {
				// openings depend too much on alliances
				
			} else if (power.getName().equals("RUS")) {
				// openings depend too much on alliances
				
			} else if (power.getName().equals("TUR")) {
				// openings depend too much on alliances
			} 
		}
		
		return orderList; 
	}
	public static Power preferredAlliance(Power power, boolean isStandard, Map map) {
		Power preferred = null;
		if (isStandard) {
			if (power.getName().equals("AUS")) {
				preferred = map.getPower("ITA"); // confirmed
			} else if (power.getName().equals("ENG")) {
				preferred = map.getPower("FRA"); // confirmed
			} else if (power.getName().equals("FRA")) {
				preferred = map.getPower("ENG"); // confirmed
			} else if (power.getName().equals("GER")) {
				preferred = map.getPower("ENG"); // confirmed
			} else if (power.getName().equals("ITA")) {
				preferred = map.getPower("AUS"); // confirmed
			} else if (power.getName().equals("RUS")) {
				preferred = map.getPower("TUR"); // confirmed
			} else if (power.getName().equals("TUR")) {
				preferred = map.getPower("RUS");
			} 
		}
		return preferred;
	}
	public static Power secondPreferredAlliance(Power power, boolean isStandard, Map map) {
		Power preferred = null;
		if (isStandard) {
			if (power.getName().equals("AUS")) {
				preferred = map.getPower("RUS"); // confirmed
			} else if (power.getName().equals("ENG")) {
				preferred = map.getPower("GER"); // confirmed
			} else if (power.getName().equals("FRA")) {
				preferred = map.getPower("GER"); // confirmed
			} else if (power.getName().equals("GER")) {
				preferred = map.getPower("FRA"); // confirmed
			} else if (power.getName().equals("ITA")) {
				preferred = map.getPower("RUS"); // confirmed
			} else if (power.getName().equals("RUS")) {
				preferred = map.getPower("AUS"); // confirmed
			} else if (power.getName().equals("TUR")) {
				preferred = map.getPower("ITA"); // confirmed
			} 
		}
		return preferred;
	}
	
	// preferred enemy depends on the the first preferred alliances, nothing else
	public static Power preferredEnemy(Power power, boolean isStandard, Map map) {
		Power preferred = null;
		if (isStandard) {
			if (power.getName().equals("AUS")) {
				preferred = map.getPower("TUR");
			} else if (power.getName().equals("ENG")) {
				preferred = map.getPower("GER"); 
			} else if (power.getName().equals("FRA")) {
				preferred = map.getPower("GER"); 
			} else if (power.getName().equals("GER")) {
				preferred = map.getPower("FRA"); 
			} else if (power.getName().equals("ITA")) {
				preferred = map.getPower("TUR"); 
			} else if (power.getName().equals("RUS")) {
				preferred = map.getPower("AUS"); 
			} else if (power.getName().equals("TUR")) {
				preferred = map.getPower("AUS"); 
			} 
		}
		return preferred;
	}
	
	public static java.util.LinkedHashMap<Power, Power> PreferredAlliances(Power power, boolean isStandard, Map map) {
		java.util.LinkedHashMap<Power, Power> result = new java.util.LinkedHashMap<Power, Power>();
		if (isStandard) {
			Power AUS = map.getPower("AUS");
			Power ENG = map.getPower("ENG");
			Power FRA = map.getPower("FRA");
			Power GER = map.getPower("GER");
			Power ITA = map.getPower("ITA");
			Power RUS = map.getPower("RUS");
			Power TUR = map.getPower("TUR");
			
			if (power.getName().equals("AUS")) {
				result.put(ITA, TUR);
				result.put(RUS, TUR);
				result.put(TUR, RUS);
				result.put(GER, RUS);
				result.put(FRA, GER);
				result.put(ENG, RUS);
			} else if (power.getName().equals("ENG")) {
				result.put(FRA, GER);
				result.put(GER, FRA);
				result.put(RUS, GER);
				result.put(ITA, FRA);
				result.put(AUS, RUS);
				result.put(TUR, RUS);
				
			} else if (power.getName().equals("FRA")) {
				result.put(ENG, GER);
				result.put(GER, ITA);
				result.put(ITA, GER);
				result.put(AUS, GER);
				result.put(RUS, GER);
				result.put(TUR, AUS);
				
			} else if (power.getName().equals("GER")) {
				result.put(ENG, FRA);
				result.put(FRA, ENG);
				result.put(RUS, ENG);
				result.put(ITA, FRA);
				result.put(AUS, ITA);
				result.put(TUR, RUS);
				
			} else if (power.getName().equals("ITA")) {
				result.put(AUS, TUR);
				result.put(RUS, AUS);
				result.put(GER, FRA);
				result.put(FRA, GER);
				result.put(ENG, GER);
				result.put(TUR, AUS);
				
			} else if (power.getName().equals("RUS")) {
				result.put(TUR, AUS);
				result.put(GER, ENG);
				result.put(ITA, TUR);
				result.put(AUS, TUR);
				result.put(FRA, GER);
				result.put(ENG, GER);
				
			} else if (power.getName().equals("TUR")) {
				result.put(RUS, AUS);
				result.put(ITA, AUS);
				result.put(GER, RUS);
				result.put(FRA, ITA);
				result.put(FRA, GER);
				result.put(ENG, RUS);
				
			} 
		}
		
		
		
		return result;
		
	}
	
}
