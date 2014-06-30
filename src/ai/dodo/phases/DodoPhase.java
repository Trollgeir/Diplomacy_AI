package ai.dodo.phases; 

import java.util.concurrent.LinkedBlockingQueue;
import message.order.*; 
import message.press.*;
import kb.functions.*;
import kb.province.*; 
import kb.unit.*; 
import kb.*; 
import ai.dodo.*; 

public abstract class DodoPhase {

	protected Power power; 
	protected DodoBeliefBase belief;
	protected Map map; 

	public DodoPhase(ExtendedDodo ai) {
		power = ai.getPower(); 
		belief = ai.getBeliefBase(); 
		map = ai.getMap(); 
	}

	public Power getPower() {
		return power; 
	}

	public DodoBeliefBase getBeliefBase() {
		return belief; 
	}

	public Map getMap() {
		return map; 
	}

	public abstract void run(LinkedBlockingQueue<Order> queue); 
}