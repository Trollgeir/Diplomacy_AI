package ai.dodo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import ai.dodo.phases.*;
import kb.Map;
import kb.unit.*;
import kb.Node;
import kb.province.Province;
import kb.unit.Unit;
import message.DaideMessage;
import message.order.*;
import message.press.*;
import message.server.Huh;
import ai.AI;
import ai.Heuristics;
import game.Game;
import kb.Names; 
import kb.Phase; 
import kb.Power;
import kb.functions.MapInfo;
import kb.functions.MapInfo.SCO_type; 

public class DodoAI extends AI {
/* This AI is called Dodo as it has no natural enemies. Also, naive. */
	
	public double initialTrust = 0.5;
	public double decay = 0.05;
	public double righteousness = 0.5;
	public double supIntolerance = 0.5;
	public double incTrust = 0.03;
/*	public String name = "dudo";*/
	public String fileName = "";
	
	protected Negotiator negotiator;
	
	boolean key_to_send = false; 
	Names names = null; 
	// /ArrayList<Province> visitedProvinces = new ArrayList<Province>();
	DodoBeliefBase		belief;
	
	public DodoAI(Map map) {
		super("DodoAI", "0.0.0.0.1", map);
		
		negotiator = new Negotiator(this, map);
	}

	@Override
	public String getUsage() {
		return "\nOptional flags:\n\t-n [name] (Changes name of this AI)\n\t-l [path to log file] (Enables reading names from log file)\n\t-k (Enables require key to send messages)" ;
	}

	public void parseCommandLineArguments(String[] args) {
		// first two argument are always ip and port
		for (int i = 2; i < args.length; i ++) {
			String flag = args[i]; 
			if (flag.equals("-n")) {
				if (!fileName.equals("")) {
					System.out.println("Name already set with -f argument. Ignored."); 
				} else {
					setName(args[++i]);  
				}
			} else if (flag.equals("-l")) {
				names = new Names(args[++i]); 
			} else if (flag.equals("-k")) {
				key_to_send = true; 
			} else if (flag.equals("-f")) {
				//System.out.println("Ddodo"); 
				fileName = args[++i];
			}
				else  {
				//hack to throw outofboundexception
				String error = args[-20]; 
			}
		}
	}

	public void parseTrustFile()
	{		
		if(names != null){
			BufferedWriter bw = null;
			BufferedReader br = null;
			try{
				bw = new BufferedWriter(new FileWriter(name +"Trust.txt", true));
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try{
				br = new BufferedReader(new FileReader(name +"Trust.txt"));
				try{
					String line = br.readLine();
					while(line != null)
					{
						String[] splitted = line.split(" ");
						for(Power p : map.powers)
						{
							PowerInfo pi = belief.powerInfo.get(p);
							if(splitted[0].equals(pi.name)) // found the player inside our trust file
							{
								pi.trust = Double.parseDouble(splitted[1]);
								pi.seenBefore = true;
							}
						}
						line = br.readLine();
					}
					br.close();
					try{
						bw = new BufferedWriter(new FileWriter(name +"Trust.txt", true));
						for(Power p: map.powers)
						{
							if(!p.getName().equals(name))
							{
								PowerInfo pi = belief.powerInfo.get(p);
								if(!pi.seenBefore) // not seen before
								{// thus add it to the text file
									pi.trust = initialTrust;
									bw.append(pi.name + " " + pi.trust);
									bw.newLine();
								}
							}
						}
						bw.close();
					}
					catch(IOException e){
						e.printStackTrace();
					}
				} catch (IOException e){
					e.printStackTrace();
				}
			} catch (FileNotFoundException e){
				e.printStackTrace();
			}
		}
	}
	
	public void writeToFile()
	{
		if(names != null)
		{
			BufferedReader br = null; BufferedWriter bw = null;
			ArrayList<String> output = new ArrayList<String>();
			try{
				br = new BufferedReader(new FileReader(name +"Trust.txt"));
				try{
					String line = br.readLine();
					while(line != null)
					{
						output.add(line);
						br.readLine();
					}
					br.close();
				}
				catch (IOException e){
					e.printStackTrace();
				}
			} catch (FileNotFoundException e){
				e.printStackTrace();
			}
			try{
				bw = new BufferedWriter(new FileWriter(name +"Trust.txt", false));
				for(int i = 0; i < output.size(); i++)
				{
					String[] splitted = output.get(i).split(" ");
					if(names != null){
						for(Power p : map.powers)
						{
							if(!p.getName().equals(name))
							{
								PowerInfo pi = belief.powerInfo.get(p);
								if(splitted[0].equals(pi.name)) // found a power we know, update the values
								{
									splitted[1] = Double.toString(pi.trust);
									output.set(i, (splitted[0] + " " + splitted[1]));
									break;
								}
							}
						}
					}
				}
				for(int i = 0; i < output.size(); i++)
				{
					bw.append(output.get(i));
					bw.newLine();
				}
				bw.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
			System.out.println("DONE WRITING!");
		}
	}
	
	public void parseTextFile(String fileName)
	{
		System.out.println("u,aua,"); 
		try{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			try{
				String line = br.readLine();
				int index = 0;
				String result = "";
				while (line != null)
				{
					index = line.lastIndexOf("=");
					result = line.substring(index + 1);
					if(result.startsWith(" ")){
						result = result.substring(1);
					}
					if(line.startsWith("name")) {
						name = result;
						System.out.println(this.name); 
					} else if (line.startsWith("initialTrust")){
						initialTrust = Double.parseDouble(result);
					}
					else if (line.startsWith("decay")){
						decay = Double.parseDouble(result);
					}
					else if (line.startsWith("righteousness"))
					{
						righteousness = Double.parseDouble(result);
					}
					else if (line.startsWith("supIntolerance"))
					{
						supIntolerance = Double.parseDouble(result);
					}
					line = br.readLine();
				}			
			} catch (IOException e){
				e.printStackTrace();
			}
		} catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public void findGains()
	{}
		
	
	
	@Override
	protected void handleHLO(String[] message)
	{
		this.power =  map.getPower(message[2]);		
		this.passcode = message[5];
		this.lvl = message[10];
		if (names != null) {
			names.init(map);
		}
		
		belief = new DodoBeliefBase(map, power, this);
		parseTrustFile();
	}
	@Override
	protected void handleSLO(String[] message)
	{
		if (message[2].equals(getPower().getName())) {
			System.out.println("I won!"); 
		} else {
			System.out.println("I lost."); 
		}
	}
	@Override
	protected void handleFRM(String[] message)
	{
		this.negotiator.addProposal(message);
	}
	@Override
	protected void handleSMR(String[] message)
	{

		System.out.println("\n endgame info: \n");
		for (String m : message) {
			if (m == null) break; 
			System.out.println("" + m);
		
		}
		writeToFile();
		System.out.println("TO BE CONTINUED?"); 
		System.exit(0); 
	}
	@Override
	protected void handleTHX(String[] message)
	{
		//System.out.println("Server thanks " + this.getPower().getName() + " for his order."); 
		this.setCanMessage(true);
	}
	@Override
	protected void handleYES(String[] message)
	{
		/*TODO, need to know all about sent messages..*/
	}
	@Override
	protected void handleREJ(String[] message)
	{
		/*TODO, need to know all about sent messages..*/
		System.out.println("Rejected order muh."); 
	}
	@Override
	protected void handleHUH(String[] message)
	{
		/*TODO, need to know all about sent messages..*/
	}
	
	@Override
	public void handleORD(String[] message)
	{
		Power from = map.getPower(message[7]);
		String orderType = message[11];
		String target = "";
		Power supportPower = null;
		
		if (orderType.equals("MTO")) {
			target = message[12];
			//System.out.println("" + from.getName() + " " + orderType + " " + Target);
		} else if (orderType.equals("SUP")) {
			supportPower = map.getPower(message[13]);
			target = message[18];
			//System.out.println("" + from.getName() + " " + orderType + " " + supportPower.getName() + " in " + Target );
		}
		
		ArrayList<Province> myProvinces = map.getProvincesByOwner(getPower());
		if(target != "" && myProvinces.contains(target))
		{
			isDefected(from, supportPower);
		}
	
		if (map.getPhase() == Phase.SUM || map.getPhase() == Phase.AUT){
			//We're only interested in processing results after a movement phase. Parser needed!
		}
		
	}
	
	@Override
	public void init(String[] args) throws ArrayIndexOutOfBoundsException {
		parseCommandLineArguments(args);
		if(!fileName.equals(""))
			parseTextFile(fileName);
		System.out.println(name); 
	}

	public static void main(String[] args) {
		Map map = new Map();
		AI ai = new DodoAI(map);
		new Game(ai, map, args);
	}
	
	public void isDefected(Power mover, Power supporter)
	{
		if(supporter != null) // There is a supporter; check if he is in alliance or peace with us
		{
			if(belief.isAlly(mover) || belief.powerInfo.get(mover).peace) // The other power was trying to invade our province while being allied with us
			{
				handleBackstab(mover);
			}
			else if (belief.isAlly(supporter) || belief.powerInfo.get(supporter).peace)
			{
				handleBackstab(supporter);
			}
		}
		else // No supporter; check if the mover has an alliance or peace with us
			if(belief.isAlly(mover) || belief.powerInfo.get(mover).peace) // The other power was trying to invade our province while being allied with us
			{
				handleBackstab(mover);
			}
	}
	
	public void handleBackstab(Power p)
	{
		AllianceInfo allianceInfo = belief.allianceByPower(p);
		// Decrement trust of the power
		belief.defectDec(allianceInfo.actuality, p);
		// Disband the alliance ??
		belief.deleteAllAlliancesWith(p);
	}
	
	
	
	public DodoBeliefBase getBeliefBase() {
		return belief;
	}

	public void newTurn()
	{
		if (!power.alive) return;
		
		if(belief != null)
		{
			belief.incrementPeaceTime();
			belief.incrementAllianceTime();
		}
		
		System.out.println();
		System.out.println("The new Dodo lives!");
		System.out.println("New turn for " + power.getName());
		System.out.println("Year: " + map.getYear() + " -----------  Phase: " + map.getPhase()); 

		if (power.getName().equals("ITA") && map.getYear() == 1901) {
			System.out.println("Hey, I am Italy and I want peace with Austria! :D"); 
			Power[] peace = new Power[2];
			peace[0] = power; 
			peace[1] = map.getPower("AUS");
			Game.server.send(new Send(new Proposal(new Peace(peace)), peace[1]));  
		}

		//Suggesting alliances to EVERYONE!
		/*Power[] me = {power};
		
		for ( Power ally : map.powers )
		{
			for ( Power enemy : map.powers )
			{
				Power[] against = {enemy};
				DaideMessage msg = new Send(new Proposal(new Alliance(me, against)), ally);
				
				if (ally != enemy && ally != power && enemy != power)
				{
					Game.server.send(msg);
					System.out.println(msg);
				}
			}
		}*/
		
		
		if (map.getPhase() == Phase.SPR || map.getPhase() == Phase.FAL) {
			DodoMovementPhase movementPhase = new DodoMovementPhase(this); 
			movementPhase.run(queue);
			
		} else if(map.getPhase() == Phase.SUM || map.getPhase() == Phase.AUT){
			
			DodoRetreatPhase retreatPhase = new DodoRetreatPhase(this);
			retreatPhase.run(queue);
		} else if (map.getPhase() == Phase.WIN) { 
			
			DodoBuildPhase buildPhase = new DodoBuildPhase(this);
			buildPhase.run(queue);
		}

		if (key_to_send) {
			try {
				System.out.println("Press enter to continue.");
				System.in.read(); 
			} catch (IOException e) {
				//Should never happen though...
				e.printStackTrace(); 
			}
		}
		
		handleQueue();
		
		System.out.println("");
		System.out.println(belief);
		System.out.println("");
		System.out.println("-----------END OF TURN -----------");
		System.out.println("");
		//System.out.println(this.getPower().getName() + " sent his order!"); 
	}
	
}
