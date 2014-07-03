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


public class DodoMovementPhase extends DodoPhase {

	public DodoMovementPhase(ExtendedDodo ai) {
		super(ai); 
	} 

	public void attack(ProvinceData target, ArrayList<UnitData> units, ArrayList<UnitData> usedUnits, ArrayList<Province> usedProvinces, LinkedBlockingQueue<Order> queue) {
		UnitData holdingUnit = null;
		UnitData movingUnit = null;
		for (UnitData u : units) {
			if (u.unit.location.province == target.province) {
				holdingUnit = u;
				usedUnits.add(u);
				break;
			}
		}

		if (holdingUnit != null) {
			units.remove(holdingUnit);
			usedUnits.add(holdingUnit);
			queue.add(new Hold(holdingUnit.unit));
		} else {
			movingUnit = units.get(0);
			usedUnits.add(movingUnit);
			units.remove(movingUnit);
			queue.add(new Move(movingUnit.unit, movingUnit.destNode));
		}
		usedProvinces.add(target.province);

		int used = 0;
		int needed = target.supportNeeded;
		while (used < needed - 1) {
			UnitData supportUnit = units.get(used); 
			if (holdingUnit == null) {
				queue.add(new SupportToMove(supportUnit.unit, movingUnit.unit, movingUnit.destNode.province));
			} else {
				queue.add(new SupportToHold(supportUnit.unit, holdingUnit.unit));
			}

			usedUnits.add(supportUnit);
			usedProvinces.add(supportUnit.unit.location.province);
			used++;
		}
	}

	public void run(LinkedBlockingQueue<Order> queue) {
		float[] willAttack = belief.allAttackAgainstWeights(); 
		float[] willDefend = belief.allDefendAgainstWeights(); 

		MapInfo mapInfo = new MapInfo(map, power, willAttack, willDefend);

		System.out.println("Neighbor Count\n");
		ArrayList<NeighborData> neighborCount = mapInfo.getNeighborCount();
		for (NeighborData neighbor : neighborCount) System.out.println(neighbor);
		
		ArrayList<Unit> units = map.getUnitsByOwner(this.getPower());
		ArrayList<Province> home = power.homeProvinces;
		ArrayList<Province> provinces = map.getProvincesByOwner(this.getPower()); 
		ArrayList<Province> occupied = new ArrayList<Province>();
		
		for (Unit u : units) occupied.add(u.location.province);

		System.out.println("Init MapInfo");
			System.out.println("power: " + power);
			
			ArrayList<Unit> availableUnits = new ArrayList<Unit>(); 
			availableUnits.addAll(units);
			
			while (availableUnits.size() > 0) {
				ArrayList<ProvinceData> targets = mapInfo.getSortedTargets(); 

				targets = mapInfo.filterTakeable(targets);

				/*
				System.out.println("Sorted targets:" + targets.size());
				for (ProvinceData p : targets) {
					System.out.println(p.toString()); 
				}
				*/
				
				if (targets.size() == 0) break;

				float totalWeight = 0; 
				ProvinceData mainTarget = targets.get(0);


				for (ProvinceData target : targets) {
					if (target.weight > 0.5f * mainTarget.weight) {
						totalWeight += target.weight;
					} else {
						break;
					}
				}

				if (totalWeight == 0) break; 
				System.out.println("Total weight: " + totalWeight); 
				int targetIdx = 0;
				float val = 0;
				float targetVal = (float)Math.random() * totalWeight;
				while ((val += targets.get(targetIdx).weight) < targetVal && targetIdx++ < targets.size());

				//System.out.println("attacking: " + targets.get(targetIdx).province.name);
				ArrayList<UnitData> sortedUnits = mapInfo.getSortedUnits(targets.get(targetIdx));
				System.out.println("targetIdx: " + targetIdx + " / " + targets.size());

				//Be sure that the unit standing on the target is always used (or do something intelligent...)
				
				ArrayList<UnitData> usedUnits = new ArrayList<UnitData>();
				ArrayList<Province> usedProvinces = new ArrayList<Province>(); 
				
				attack(targets.get(targetIdx), sortedUnits, usedUnits, usedProvinces, queue);
	
				for (UnitData ud : usedUnits) {
					availableUnits.remove(ud.unit); 
				}

				ArrayList<ProvinceData> usedProvinceData =  new ArrayList<ProvinceData>();

				for (Province p0 : usedProvinces) {
					for (ProvinceData p1 : targets) {
						if (p0 == p1.province) {
							usedProvinceData.add(p1);
							break;
						}
					}
				}


				mapInfo.updateByMove(usedUnits, usedProvinceData);

			}
		
			for (Unit u : availableUnits) {
				queue.add(new Hold(u));
			}

			System.out.println("------------- ORDERS -------------");
			for (Order order : queue) {
				System.out.println(order.daide());
			}

	} 

}