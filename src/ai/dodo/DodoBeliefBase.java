package ai.dodo;

import java.util.ArrayList;
import java.util.Set;

import kb.Map;
import kb.Node;
import kb.Power;
import kb.province.Province;
import kb.unit.Unit;

class AllianceInfo
{
	public AllianceInfo()
	{
		//TODO: Just some default values, may need tweaking.
		against = new ArrayList<Power>();
		actuality = 1.0;
		time = 0;
	}
	
	public double			actuality;
	public int 				time;
	public Power			with;
	public ArrayList<Power>	against;
}

class PowerInfo
{
	public PowerInfo(String name, DodoAI ai)
	{
		supFavor = 0;
		peace = false;
		peaceActuality = 1;
		trust = ai.initialTrust;
		peaceTime = 0;
		paranoia = 1 - trust;
		this.name = name;
		seenBefore = false;
	}
	
	public int 		supFavor;
	public boolean 	peace;
	public int 		peaceTime;
	public double 	peaceActuality;
	public double	paranoia;
	public double 	trust;
	public boolean 	seenBefore;
	public String 	name;
}

public class DodoBeliefBase {
	Map		map;
	Power	self;
	DodoAI ai;
	
	public ArrayList<AllianceInfo>					allianceInfo;
	public java.util.Map<Power, PowerInfo>			powerInfo;
	
	
	public DodoBeliefBase(Map map, Power self, DodoAI ai)
	{
		this.map = map;
		this.self = self;
		//This one is for the wizards:
		this.ai = ai; 


		allianceInfo = new ArrayList<AllianceInfo>();
		powerInfo = new java.util.HashMap<Power, PowerInfo>();
		
		for (Power p : map.powers)
		{
			if(ai.names != null)
				powerInfo.put(p, new PowerInfo(ai.names.getNameByPower(p), ai));
		}
	}
	
	public String toString()
	{
		String ret = "=====BELIEFS OF " + self.getName() + "=====\n";
		
		ret += " POWERS\n--------\n";
		Set<Power> powerKeys = powerInfo.keySet();
		
		for (Power p : powerKeys)
		{
			PowerInfo info = powerInfo.get(p);

			ret += "\t" + p.getName() + " : \n";
			ret += "\t - supFavor : " + info.supFavor + "\n";
			ret += "\t - peace : " + info.peace + "\n";
			ret += "\t - peaceActuality : " + info.peaceActuality + "\n";
			ret += "\t - trust : " + info.trust + "\n";
			ret += "\t - paranoia : " + info.paranoia + "\n";
		}
		
		ret += "\n\n";
		ret += " ALLIANCES\n-----------\n";

		for (AllianceInfo info : allianceInfo)
		{
			ret += "\t" + info.with.getName() + " : \n";
			ret += "\t - against : " + info.against + "\n";
			ret += "\t - time : " + info.actuality + "\n";
		}
		
		return ret + "\n========================";
	}
	
	public AllianceInfo allianceByPower(Power power)
	{
		for (AllianceInfo alliance : allianceInfo)
		{
			if (alliance.with.equals(power))
				return alliance;
		}
		return null;
	}
	
	public boolean isAlly(Power power)
	{
		for (AllianceInfo alliance : allianceInfo)
		{
			if (alliance.with.equals(power))
				return true;
		}
		return false;
	}
	
	public boolean isEnemy(Power power)
	{
		for (AllianceInfo alliance : allianceInfo)
		{
			if (alliance.against.contains(power)) {
				return true;
			}
		}
		return false;
	}
	
	public void incrementAllianceTime()
	{
		for (int i = 0; i < allianceInfo.size(); i++)
		{
			AllianceInfo alliance = allianceInfo.get(i);
			
			alliance.actuality = Math.pow(ai.decay, (-alliance.time));
			PowerInfo pi = powerInfo.get(alliance.with);
			pi.trust += ai.incTrust;
			pi.trust = pi.trust < 0 ? 0 : pi.trust > 1 ? 1 : pi.trust;
			pi.paranoia += 1 - (Math.pow(ai.decay, alliance.time) * pi.trust);
			alliance.time++;
		}
	}
	
	public void incrementPeaceTime()
	{
		for(int i = 0; i < map.powers.size(); i++)
		{
			PowerInfo pi = powerInfo.get(map.powers.get(i));
			if(!pi.name.equals(ai.name) && pi.peace) 
			{
				pi.trust += ai.incTrust;
				pi.trust = pi.trust < 0 ? 0 : pi.trust > 1 ? 1 : pi.trust;
				pi.peaceActuality = Math.pow(ai.decay, (-pi.peaceTime));
				pi.paranoia += 1 - (Math.pow(ai.decay, pi.peaceTime) * pi.trust);
				pi.peaceTime++;
			}
		}
	}

	public void deleteAllAlliancesWith(Power p)
	{
		for(int i = 0; i < allianceInfo.size(); i++)
		{
			if(allianceInfo.get(i).with == p)
				allianceInfo.remove(i);
		}
	}
	
	public double round(double i) { 		//Rounding numbers.
		return Math.round(i*10000) / 10000.0;
	}
	public void supCalc(int supFavor, Power p) {
		//Function for trust alteration based on support reciprocity
		powerInfo.get(p).trust += round(Math.pow(ai.supIntolerance * Math.abs(supFavor), 2) / 1000);
	}
	public double pUpdate(double time) {	
		//Function for trust alteration while holding a treaty
		return Math.pow(ai.decay,-time);
	}
	public void defectDec(double time, Power p) { 	
		//Function for trust alteration based on defecting a treaty (backstab!)
		powerInfo.get(p).trust -= round(Math.pow(ai.decay,(1 - time)));
	}

	public float defendAgainstWeight(Power p) {
		PowerInfo info = powerInfo.get(p); 
		if(info != null)
		{
			if(!info.peace)
				return 1.0f;
			return (float)info.paranoia;
		}
		return 0.0f;
	}

	public float attackAgainstWeight(Power p) {
		PowerInfo info = powerInfo.get(p);
		if (info == null) return 0;
		{
			if(!info.peace)
				return 1.0f;
		}
		return (float)Math.pow((float)1.1, (float)info.peaceActuality)*((float)1-(float)ai.righteousness); 
	}

	public float[] allDefendAgainstWeights() {
		float[] result = new float[map.powers.size()];
		for (int i = 0; i < map.powers.size(); ++i) {
			result[i] = defendAgainstWeight(map.powers.get(i)); 
		}
		return result; 
	}

	public float[] allAttackAgainstWeights() {
		float[] result = new float[map.powers.size()];
		System.out.println("willAttack: ");
		for (int i = 0; i < map.powers.size(); ++i) {
			result[i] = attackAgainstWeight(map.powers.get(i)); 
			System.out.println("\tpower: " + map.powers.get(i) + ": " + result[i]);
		}
		return result; 
	}


}
