package ai.dodo.phases; 

import java.util.concurrent.LinkedBlockingQueue;
import message.order.*; 
import message.press.*;
import kb.functions.*;
import kb.province.*; 
import kb.unit.*; 
import kb.*; 
import ai.dodo.*; 

public class DodoBuildPhase extends DodoPhase {

	public DodoBuildPhase(DodoAI ai) {
		super(ai); 
	} 

	public void run(LinkedBlockingQueue<Order> queue) {

	}
}