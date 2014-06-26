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

	public DodoPhase(DodoAI ai) {
		power = ai.getPower(); 
		belief = ai.getBeliefBase(); 
		map = ai.getMap(); 
	}

	public abstract void run(LinkedBlockingQueue<Order> queue); 
}