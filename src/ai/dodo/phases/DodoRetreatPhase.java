package ai.dodo.phases;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import message.order.*; 
import message.press.*;
import kb.functions.*;
import kb.province.*; 
import kb.unit.*; 
import kb.*; 
import ai.dodo.*; 

public class DodoRetreatPhase extends DodoPhase {

	public DodoRetreatPhase(ExtendedDodo ai) {
		super(ai); 
	} 
	
	public Node getDestination(MapInfo mapInfo, Unit unit, ArrayList<UnitData> usedUnits, ArrayList<ProvinceData> usedProvinces) {
		ArrayList<ProvinceData> targets = mapInfo.getSortedTargets();
		for (ProvinceData target : targets) {
			for (Node node : unit.retreatTo) { 
				if (target.province == node.province) {
					usedUnits.add(new UnitData(unit, node, target));
					usedProvinces.add(target);
					return node; 
				}	
			}
		}

		return null; 
	}

	public void run(LinkedBlockingQueue<Order> queue) {
		MapInfo mapInfo = new MapInfo(map, power);

		ArrayList<Unit> units = map.getUnitsByOwner(this.getPower());
		ArrayList<Province> home = power.homeProvinces;
		ArrayList<Province> provinces = map.getProvincesByOwner(this.getPower()); 
		ArrayList<Province> occupied = new ArrayList<Province>();
		for (Unit u : units) occupied.add(u.location.province);

		for (Unit u : units) {
				if (u.mustRetreat) {
					// No place to retreat to, so we must disband. 
					if (u.retreatTo.size() == 0) {
						queue.add(new Disband(u)); 
					} else {
						ArrayList<UnitData> usedUnits = new ArrayList<UnitData>();
						ArrayList<ProvinceData> usedProvinces = new ArrayList<ProvinceData>(); 
						Node node = getDestination(mapInfo, u, usedUnits, usedProvinces);
						if (node == null) System.err.println("Error in retreat!");
						queue.add(new Retreat(u, node)); 

						mapInfo.updateByMove(usedUnits, usedProvinces);
					}
				}
			}
		}
	}



/*

//Try to move to a supply center
						Node destination = moveToSupplyCenter(u, u.retreatTo, occupied, false, false);
						if (destination == null) queue.add(new Disband(u)); 
						else {
							occupied.remove(u.location.province); 
							occupied.add(destination.province);
							System.out.println(power.getName() + " : " + "I want to retreat " + u.location.daide() + " to " + destination.daide()); 
							queue.add(new Retreat(u, destination)); 
						}

						*/