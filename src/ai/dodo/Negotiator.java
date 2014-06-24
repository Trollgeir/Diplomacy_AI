package ai.dodo;

import game.Game;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;

import message.DaideList;
import message.press.*;
import message.server.*;
import ai.Heuristics;
import ai.dodo.DodoBeliefBase;
import kb.Map;
import kb.Phase;
import kb.Power;

// its a bit messy & ugly but it works
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
		int end1, end2, i;
		Power self = dodoAI.belief.self;
		synchronized(queue) {
			for (String[] s : queue) {
				if (s[0].equals("FRM")){
					from = s[2];
					//ending of 'to'
					end1 = DaideList.unBracket(s, 4);
					if (s[end1+2].equals("PRP")) {
						
						// end bracket of PRP
						end2 = DaideList.unBracket(s, end1+3);
						String[] prop = new String[(end2+1)-(end1+1)];
						i = 0;
						for (int n = end1+2;n<=end2;n++) {
							prop[i] = s[n];
							i++;
						}
						if (s[end1+4].equals("ALY")) {
							
							// first bracket of allies
							end1 = end1+5;
							// last bracket of allies
							end2 = DaideList.unBracket(s, end1);
							
							String[] allies = new String[end2-(end1+1)];
							
							i=0;
							for (int n = end1 + 1 ;n < end2;n++){
								allies[i] = s[n];
								i++;
							}
							
							// first bracket of enemies
							end1 = end2+2;
							// last bracket of enemies
							end2 = DaideList.unBracket(s, end1);
							
							String[] enemies = new String[end2-(end1+1)];
							
							i = 0;
							for (int n = end1 + 1;n < end2;n++){
								enemies[i] = s[n];
								i++;
							}
							
							if (acceptAlliance(allies, enemies)) {
								Game.server.send(new Send(new Yes(prop), map.getPower(from)));
							} else {
								Game.server.send(new Send(new Reject(prop), map.getPower(from)));
							}
							
						} else if (s[end1+4].equals("PCE")) {
							
							// first bracket of peace
							end1 = end1+5;
							// last bracket of peace
							end2 = DaideList.unBracket(s, end1);

							String[] members = new String[end2-(end1+1)];
							
							i=0;
							for (int n = end1 + 1 ;n < end2;n++){
								members[i] = s[n];
								i++;
							}
							
							if (acceptPeace(members)) {
								Game.server.send(new Send(new Yes(prop), map.getPower(from)));
							} else {
								Game.server.send(new Send(new Reject(prop), map.getPower(from)));
							}
						}
						
					}
				} else if (s[0].equals("YES")) {
					if (s[2].equals("PRP")){
						if (s[4].equals("ALY")) {
							// first bracket of allies
							end1 = 5;
							// last bracket of allies
							end2 = DaideList.unBracket(s, end1);
							
							for (int n = end1 + 1 ;n < end2;n++){
								// TODO: fill in belief base that the power is now your ally
							}
							
							// first bracket of enemies
							end1 = end2+2;
							// last bracket of enemies
							end2 = DaideList.unBracket(s, end1);
							
							for (int n = end1 + 1;n < end2;n++){
								// TODO: fill in belief base that the power is now your enemy
							}
						} else if (s[4].equals("PCE")){
							// TODO: handle accepted peace offer
						}
					}
					
				} else if (s[0].equals("REJ")) {
					if (s[2].equals("PRP")){
						if (s[4].equals("ALY")) {
							// first bracket of allies
							end1 = 5;
							// last bracket of allies
							end2 = DaideList.unBracket(s, end1);
							
							for (int n = end1 + 1 ;n < end2;n++){
								// TODO: fill in belief base that the power is now your enemy
							}
							
							// first bracket of enemies
							end1 = end2+2;
							// last bracket of enemies
							end2 = DaideList.unBracket(s, end1);
							
							for (int n = end1 + 1;n < end2;n++){
								// TODO: fill in belief base that the power is now.... your friend? :P
							}
						} else if (s[4].equals("PCE")){
							//TODO: handle rejected peace offer
						}
					}
				} else {
					// HUH
				}
			}
		}
		
		clear();
	}
	
	private boolean acceptAlliance(String[] allies, String[] enemies) {
		
		// follow heuristics
		if (map.getYear() == 1901) {
			Power preffered = Heuristics.preferredAlliance(dodoAI.getPower(), map.getStandard(), map);
			Power preferredSecond = Heuristics.secondPreferredAlliance(dodoAI.getPower(), map.getStandard(), map);
			Power preferredEnemy = Heuristics.preferredEnemy(dodoAI.getPower(), map.getStandard(), map);
			
			boolean amIncluded = false;
			boolean acceptAllies = false;
			boolean acceptEnemies = false;
			for (String s : allies) {
				if (s.equals(preffered.getName())) {
					acceptAllies = true;
				}
				if (s.equals(dodoAI.belief.self.getName())) {
					amIncluded = true;
				}
			}
			
			for (String s : enemies) {
				if (s.equals(preferredEnemy.getName())) {
					acceptEnemies = true;
				}
				if (s.equals(dodoAI.belief.self.getName())) {
					acceptEnemies = false;
				}
			}
			
			if (amIncluded && acceptAllies && acceptEnemies) {
				return true;
			}
		}
		
		// add stuff on figuring out if we want the alliance
		
		return false;
		
	}
	
	private boolean acceptPeace(String[] members) {
		
		// TODO: add stuff on figuring out if we want the peace
		return false;
	}
	
}
