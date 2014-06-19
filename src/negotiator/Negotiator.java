package negotiator;

import java.util.concurrent.LinkedBlockingQueue;

import message.DaideList;
import message.press.*;
import ai.dodo.DodoAI;
import ai.dodo.DodoBeliefBase;
import kb.Power;

public class Negotiator {
	protected LinkedBlockingQueue<String[]> queue = new LinkedBlockingQueue<String[]>();
	
	// OLD //
	public Negotiator(){
		
	}
	
	public void addProposal(String[] proposal) {
		synchronized(queue) {
			queue.add(proposal);
			handleProposal();
		}
	}
	public void clear() {
		synchronized(queue) {
			queue.clear();
		}
	}

	public void handleProposal() {
		String from;
		String[] to;
		int end1, end2;
		synchronized(queue) {
			for (String[] s : queue) {
				if (s[0].equals("FRM")){
					from = s[2];
					end1 = DaideList.unBracket(s, 4);
					if (s[end1+2].equals("PRP")) {
						if (s[end1+4].equals("ALY")) {
							String[] allies;
							end2 = DaideList.unBracket(s, end1 + 5);
							
							for (int n = end1 + 5 ;n<end2;n++){
								//if (s[n].equals)
							}
						}
						
					}
				} else if (s[0].equals("YES")) {
					// handle confirmed proposal
				} else if (s[0].equals("REJ")) {
					// handle rejected proposal
				} else {
					// HUH
				}
			}
		}
		
		clear();
	}
}
