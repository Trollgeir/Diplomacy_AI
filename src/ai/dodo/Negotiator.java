package ai.dodo;

import game.Game;

import java.util.concurrent.LinkedBlockingQueue;

import message.DaideList;
import message.press.*;
import message.server.Reject;
import ai.dodo.DodoBeliefBase;
import kb.Map;
import kb.Power;

public class Negotiator {

	protected LinkedBlockingQueue<String[]> queue = new LinkedBlockingQueue<String[]>();
	protected DodoAI dodoAI;
	protected Map map;
	
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
		int end1, end2, i = 0;
		Power self = dodoAI.belief.self;
		synchronized(queue) {
			for (String[] s : queue) {
				if (s[0].equals("FRM")){
					from = s[2];
					//ending of 'to'
					end1 = DaideList.unBracket(s, 4);
					if (s[end1+2].equals("PRP")) {
						if (s[end1+4].equals("ALY")) {
							String[] allies = new String[16];
							String[] enemies = new String[16];
							
							// end of allies
							end2 = DaideList.unBracket(s, end1 + 5);
							
							for (int n = end1 + 5 ;n < end2;n++){
								if (!s[n].equals(self.getName())) {
									allies[i] = s[n];
								}
								i++;
							}
							
							// end of enemies
							end1 = end2+2;
							end2 = DaideList.unBracket(s, end1);
							

							i = 0;
							for (int n = end1;n < end2;n++){
								enemies[i] = s[n];
								i++;
							}
							
							handleAlliance(allies, enemies);
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
	
	public void handleAlliance(String[] allies, String[] enemies) {
		
		
	}
}
