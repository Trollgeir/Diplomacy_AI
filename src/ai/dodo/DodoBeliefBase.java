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
		paranoia = 0.0f;
		against = new ArrayList<Power>();
		time = 0;
	}
	
	public double 			paranoia;
	public int				time;
	public Power			with;
	public ArrayList<Power>	against;
}

class PowerInfo
{
	public PowerInfo()
	{
		supFavor = 0;
		trust = 0.5;
		peace = false;
		peaceActuality = 1;
	}
	
	public int 		supFavor;
	public boolean 	peace;
	public double 	peaceActuality;
	public double 	trust;
}

public class DodoBeliefBase {
	Map		map;
	Power	self;
	ExtendedDodo ai;
	
	// The initial values which are used for incrementation and decrementation of support and trust
	public double supIntolerance = 0.5; 		//Pick a value between 0-1. 0 means you don't care about support reciprocity
	public double tHalflife = 1.1;			//Treaty half-life. This indicates how fast treaties decay
	public double tTrustInc = 0.03;			//How much trust to increment for every phase as long as the treaty holds.
	
	public ArrayList<AllianceInfo>					allianceInfo;
	public java.util.Map<Power, PowerInfo>			powerInfo;
	
	
	public DodoBeliefBase(Map map, Power self)
	{
		this.map = map;
		this.self = self;
		
		allianceInfo = new ArrayList<AllianceInfo>();
		powerInfo = new java.util.HashMap<Power, PowerInfo>();
		
		for (Power p : map.powers)
		{
			if (!p.equals(self))
				powerInfo.put(p, new PowerInfo());
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
		}
		
		ret += "\n\n";
		ret += " ALLIANCES\n-----------\n";

		for (AllianceInfo info : allianceInfo)
		{
			ret += "\t" + info.with.getName() + " : \n";
			ret += "\t - against : " + info.against + "\n";
			ret += "\t - time : " + info.time + "\n";
			ret += "\t - paranoia : " + info.paranoia + "\n";
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
			
			alliance.time++;
			alliance.paranoia = 1.0 - Math.pow(1.0 - ai.decay, alliance.time) * ai.initialTrust;//TODO: Is this right?
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
		powerInfo.get(p).trust += round((this.supIntolerance * Math.abs(supFavor)) / 100);
	}
	public double pUpdate(double time) {	
		//Function for trust alteration while holding a treaty
		return Math.pow(this.tHalflife,-time);
	}
	public void defectDec(double time, Power p) { 	
		//Function for trust alteration based on defecting a treaty (backstab!)
		powerInfo.get(p).trust -= round(Math.pow(this.tHalflife,(1 - time)));
	}
}
