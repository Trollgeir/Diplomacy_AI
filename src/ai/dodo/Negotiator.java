package ai.dodo;

import game.Game;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import message.DaideList;
import message.order.*;
import message.press.*;
import message.server.*;
import ai.Heuristics;
import kb.Map;
import kb.Power;
import kb.province.Province;

// its a bit messy & ugly but it works
public class Negotiator {

	protected LinkedBlockingQueue<String[]> queue = new LinkedBlockingQueue<String[]>();
	protected ExtendedDodo dodoAI;
	protected ArrayList<Order> proposedOrders = new ArrayList<Order>();
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
		
		Power[] allies;
		Power[] enemies;
		
		synchronized(queue) {
			for (String[] s : queue) {
				if (s[0].equals("FRM")){
					from = s[2];
					PowerInfo powerInfo = dodoAI.belief.powerInfo.get(map.getPower(from));
					//ending of 'to'
					end1 = DaideList.unBracket(s, 4);
					if (s[end1+2].equals("PRP")) {

						// end bracket of PRP
						end2 = DaideList.unBracket(s, end1+3);
						String[] prop = new String[(end2)-(end1+1)];
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

							allies = new Power[end2-(end1+1)];

							for (int n = 0;n <  end2 - (end1 + 1);n++){
								allies[n] = map.getPower(s[n + end1 + 1]);
							}

							// first bracket of enemies
							end1 = end2+2;
							// last bracket of enemies
							end2 = DaideList.unBracket(s, end1);

							enemies = new Power[end2-(end1+1)];

							for (int n = 0;n <  end2 - (end1 + 1);n++){
								enemies[n] = map.getPower(s[n + end1 + 1]);
							}
							
							
							if (acceptAlliance(allies, enemies)) {
								for (int n = 0; n < allies.length; n++) {
									if (allies[n] != self)
										setAlliance(allies[n], enemies, true);
								}
								Game.server.send(new Send(new Yes(prop), map.getPower(from)));
							} else {
								for (int n = 0; n < allies.length; n++) {
									if (allies[n] != self)
										setAlliance(allies[n], enemies, false);
								}
								Game.server.send(new Send(new Reject(prop), map.getPower(from)));
							}

						} else if (s[end1+4].equals("PCE")) {

							// first bracket of peace
							end1 = end1+5;
							// last bracket of peace
							end2 = DaideList.unBracket(s, end1);

							Power[] members = new Power[end2-(end1+1)];

							for (int n = 0 ;n < end2 - (end1 + 1); n++){
								members[n] = map.getPower(s[n + end1 + 1]);
							}

							if (acceptPeace(members)) {
								for (int n = 0; n<members.length; n++) {
									if (members[n] != self)
									{
										setPeace(members[n], true);
									}
								}

								Game.server.send(new Send(new Yes(prop), map.getPower(from)));

							} else {

								for (int n = 0; n<members.length;n++) {
									if (members[n] != self)
										setPeace(members[n], false);
								}

								Game.server.send(new Send(new Reject(prop), map.getPower(from)));

							}
						} 
						else if (s[end1+4].equals("XDO")) 
						{

							// start first unit
							end1=end1+6;
							// end first unit
							end2 = DaideList.unBracket(s, end1);

							if (s[end2+1].equals("SUP")) 
							{
								
								Province supporting, supported, target = null;
								
								supporting = map.getProvince(s[end1+3]);

								//start second unit
								end1 = end2+2;
								// end second unit
								end2 = DaideList.unBracket(s, end1);
							
								supported = map.getProvince(s[end1+3]);

								boolean accept;
								
								if (s[end2+1].equals("MTO")) {
									target = map.getProvince(s[end2+2]);
									accept = acceptSupportMoveProposal(supporting, supported, target);
								}
								else
								{
									accept = acceptSupportHoldProposal(supporting, supported);
								}

								if (accept) {
									if (s[end2+1].equals("MTO")) {
										proposedOrders.add(new SupportToMove(supporting.getUnit(), supported.getUnit(), target));
									} else {
										proposedOrders.add(new SupportToHold(supporting.getUnit(), supported.getUnit()));
									}
									Game.server.send(new Send(new Yes(prop), map.getPower(from)));
								} else {
									// TODO: handle XDO
									Game.server.send(new Send(new Reject(prop), map.getPower(from)));
								}

							} else {
								Game.server.send(new Send(new Reject(prop), map.getPower(from)));
							}

						} else if (s[end1+4].equals("DMZ")) {

							Game.server.send(new Send(new Huh(prop), map.getPower(from)));
							System.out.println("Cant handle DMZ, sorry!");

						}

					} 
					else if (s[end1+2].equals("YES")) 
					{
						if (s[end1+4].equals("PRP"))
						{
							if (s[end1+6].equals("ALY")) 
							{
								// first bracket of allies
								end1 = end1+7;
								// last bracket of allies
								end2 = DaideList.unBracket(s, end1);
								
								allies = new Power[end2-(end1+1)];
								
								for (int n = 0;n <  end2 - (end1 + 1);n++){
									allies[n] = map.getPower(s[n + end1 + 1]);
								}

								// first bracket of enemies
								end1 = end2+2;
								// last bracket of enemies
								end2 = DaideList.unBracket(s, end1);
								
								enemies = new Power[end2-(end1+1)];
								
								for (int n = 0;n <  end2 - (end1 + 1);n++){
									enemies[n] = map.getPower(s[n + end1 + 1]);
								}
								
								for (int n = 0; n < allies.length; n++) {
									if (!allies[n].equals(self));
										setAlliance(allies[n], enemies, true);
								}
								
							} else if (s[end1+6].equals("PCE")){

								// first bracket of peace members
								end1 = end1+7;
								// last bracket of peace members
								end2 = DaideList.unBracket(s, end1);

								for (int n = end1 + 1 ;n < end2;n++){
									if (!s[n].equals(self.getName()));
										setPeace(map.getPower(s[n]),true);
								}
							} else if (s[end1+6].equals("XDO")) {
								// TODO: handle accepted order proposal
							} else if (s[end1+6].equals("DMZ")) {
								// TODO: handle accepted DMZ proposal (on hold)
							}
						}

					} else if (s[end1+2].equals("REJ")) {
						if (s[end1+4].equals("PRP")){
							// handle rejected ally proposal:
							if (s[end1+6].equals("ALY")) {
								// first bracket of allies
								end1 = end1+7;
								// last bracket of allies
								end2 = DaideList.unBracket(s, end1);
								
								allies = new Power[end2-(end1+1)];

								for (int n = 0;n <  end2 - (end1 + 1);n++){
									allies[n] = map.getPower(s[n + end1 + 1]);
								}

								// first bracket of enemies
								end1 = end2+2;
								// last bracket of enemies
								end2 = DaideList.unBracket(s, end1);
								
								enemies = new Power[end2-(end1+1)];

								for (int n = 0;n <  end2 - (end1 + 1);n++){
									enemies[n] = map.getPower(s[n + end1 + 1]);
								}
								
								for (int n = 0; n < allies.length; n++) {
									if (!allies[n].equals(self));
										setAlliance(allies[n], enemies, false);
								}
								
							} else if (s[end1+6].equals("PCE")){

								// first bracket of peace members
								end1 = end1+7;
								// last bracket of peace members
								end2 = DaideList.unBracket(s, end1);

								for (int n = end1 + 1 ;n < end2;n++){
									if (!s[n].equals(self.getName()));
										setPeace(map.getPower(s[n]), false);
								}
							} else if (s[end1+6].equals("XDO")) {
								// TODO handle rejected order proposal

							} else if (s[end1+6].equals("DMZ")) {
								// handle rejected DMZ proposal (on hold)
							}
						}
					} else {
						// TODO: HUH
					}
				}
			}
		}

		clear();
	}

	private boolean acceptAlliance(Power[] allies, Power[] enemies) {

		// follow heuristics
		if (map.getYear() == 1901) {
			Power preferred = Heuristics.preferredAlliance(dodoAI.getPower(), map.getStandard(), map);
			Power preferredSecond = Heuristics.secondPreferredAlliance(dodoAI.getPower(), map.getStandard(), map);
			Power preferredEnemy = Heuristics.preferredEnemy(dodoAI.getPower(), map.getStandard(), map);

			boolean amIncluded = false;
			boolean acceptAllies = false;
			boolean acceptEnemies = false;
			for (Power s : allies) {
				if (s.equals(preferred)) {
					acceptAllies = true;
				}
				if (s.equals(dodoAI.belief.self)) {
					amIncluded = true;
				}
			}

			for (Power s : enemies) {
				if (s.equals(preferredEnemy)) {
					acceptEnemies = true;
				}
				if (s.equals(dodoAI.belief.self)) {
					acceptEnemies = false;
				}
			}

			return (amIncluded && acceptAllies && acceptEnemies);
		}

		for (Power ally : allies)
		{
			if (dodoAI.belief.isEnemy(ally))
				return false;
		}
		for (Power enemy : enemies)
		{
			if (dodoAI.belief.isAlly(enemy))
				return false;
		}
		
		// TODO: add stuff on figuring out if we want the alliance

		return true;

	}

	private boolean acceptPeace(Power[] members) {
		for (Power member : members) {
			if (dodoAI.belief.isEnemy(member)){
				return false;
			}
		}
		// TODO: check if this is correct
		return true;
	}
	
	private boolean acceptSupportMoveProposal(Province supporting, Province supported, Province target)
	{
		if (!dodoAI.belief.isAlly(supported.getOwner()))
			return false;
		
		//AllianceInfo alliance = dodoAI.belief.allianceByPower(supported.getOwner());
		
		if (dodoAI.belief.powerInfo.get(supported.getOwner()).supFavor < 0) //We owe them
		{
			if (dodoAI.righteousness >= 1.0) //TODO: should this be righteousness > paranoia or something?
				return true;
		}
		
		//TODO: this.
		
		return false;
	}
	
	private boolean acceptSupportHoldProposal(Province supporting, Province supported)
	{
		if (!dodoAI.belief.isAlly(supported.getOwner()))
			return false;
		
		//AllianceInfo alliance = dodoAI.belief.allianceByPower(supported.getOwner());
		
		if (dodoAI.belief.powerInfo.get(supported.getOwner()).supFavor < 0) //We owe them
		{
			if (dodoAI.righteousness >= 1.0)
				return true;
		}
		
		//TODO: this.
		
		return false;
	}

	private void setPeace(Power member, boolean accepted) {

		PowerInfo powerInfo = dodoAI.belief.powerInfo.get(member);
		if (accepted) {
			powerInfo.peace = true;
			powerInfo.peaceActuality = 1.0;
		} else {
			powerInfo.peace = false;
			powerInfo.peaceActuality = 0.0;
		}
	}

	private void setAlliance(Power ally, Power[] enemies, boolean accepted) {
		
		AllianceInfo newAlliance = new AllianceInfo();
		
		newAlliance.with = ally;
		
		// This is the starting paranoia value of an alliance
		newAlliance.paranoia = 1 - (dodoAI.belief.pUpdate(0)*(dodoAI.belief.powerInfo.get(ally).trust/10));
		
		for (int n = 0; n < enemies.length; n++)
			newAlliance.against.add(enemies[n]);

		if (accepted) {
			boolean addNew = true;
			for (int a = 0; a < dodoAI.belief.allianceInfo.size(); a++)
			{
				AllianceInfo oldAlliance = dodoAI.belief.allianceInfo.get(a);
				if (oldAlliance.with.equals(ally))
				{
					//refresh oldAlliance

					// This is the starting paranoia value of an alliance
					oldAlliance.paranoia = 1 - (dodoAI.belief.pUpdate(0)*(dodoAI.belief.powerInfo.get(ally).trust/10));
					if (oldAlliance.against.containsAll(newAlliance.against))
						addNew = false;
				}
			}
			if (addNew)
				dodoAI.belief.allianceInfo.add(newAlliance);
		} else {
			for (int a = 0; a < dodoAI.belief.allianceInfo.size(); a++)
			{
				AllianceInfo oldAlliance = dodoAI.belief.allianceInfo.get(a);
				if (oldAlliance.with.equals(ally))
				{
					if (oldAlliance.against.containsAll(newAlliance.against) &&
						newAlliance.against.containsAll(oldAlliance.against))
					{
						dodoAI.belief.allianceInfo.remove(a);
						a--;
					}
				}
			}
		}
	}
}