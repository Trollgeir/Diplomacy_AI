package ai.dodo;

public class Trust {

	//need a counter for phases per treaty (time)
	
	public static double supIntolerance = 0.5; 		//Pick a value between 0-1. 0 means you don't care about support reciprocity
	public static double tHalflife = 1.1;			//Treaty half-life. This indicates how fast treaties decay
	public static double tTrustInc = 0.03;			//How much trust to increment for every phase as long as the treaty holds.
	
	
	
	private static double round(double i) { 		//Rounding numbers.
		return Math.round(i*10000) / 10000.0;
	}
	private static double supCalc(int supFavor) {	//Function for trust alteration based on support reciprocity
		return ((supIntolerance * Math.abs(supFavor)) / 100);
	}
	private static double pUpdate(double time) {	//Function for trust alteration while holding a treaty
		return (Math.pow(tHalflife,-time));
	}
	private static double defectDec(double time) { 	//Function for trust alteration based on defecting a treaty (backstab!)
		return Math.pow(tHalflife,(1 - time));
	}
	
		
	public static void main(String[] args) { 
		
		//We do this method for every treaty with every POWER in order to alter said POWER's trust and current paranoia. 
		
		boolean treatyDefect = false;			//import from defectionCheck
		boolean supAccept = false;				//import from Negotiator. Here we check if they accepted our request to support us.
		double powerTrustValue = 5; 			//import from beliefbase of relevant power. 
		int supFavor = -5; 						//import from alliancelist of relevant power. This value should be reset every treaty.
		
	
		for (int phase = 0; phase <= 30; ++phase) { //SIMULATION! This can be used to test a simulation of phases and will be removed once this class is integrated, along with the hack above
				
			//TREATY Calculations & Paranoia update
			System.out.println("----------------- PHASE: " + phase +"---------------------");
			double paranoia = 1 - (pUpdate(phase)*(powerTrustValue/10));
			paranoia = round(paranoia);
			
			if(treatyDefect == false) {
				if (phase > 0) {
					powerTrustValue += tTrustInc;
					System.out.println("+" +tTrustInc+ " : POWER maintains treaty!");
					}
				}
			else{
				powerTrustValue -= defectDec(phase);
				System.out.println("POWER broke treaty! New trust is: " +powerTrustValue);
				break;
				//Remove treaty from list?
				}
		
			
			//SUPPORT calculations
			//I just realized we need to make a list of every single scheduled support and run through them all (for each power).
			if (supIntolerance != 0) {
				if (supAccept == true) {
					if (supFavor >= 0) { // We owe them
						//Increment the trust of relevant power
						powerTrustValue += supCalc(supFavor);
						powerTrustValue = round(powerTrustValue);
						System.out.println("+" + supCalc(supFavor) + " : We owe POWER " + Math.abs(supFavor) + " supports and they continue to support us!");
						++supFavor;
						}
					else{	// They owe us
							//Debugging (no change in trust because it's obligated)
						System.out.println("POWER owe us and they accepted our request to support");
						++supFavor;
						}
					}
				else{
					if(supFavor >= 0) { //We owe them.
							//Debugging (no change in trust because it's permitted)
						System.out.println("We owe POWER " + Math.abs(supFavor) + " supports they refused to help. This is okay!");
						}
					else{ 	//They owe us
							//Decrement the trust of relevant power
						powerTrustValue -= supCalc(supFavor);
						powerTrustValue = round(powerTrustValue);
						System.out.println("-" + +supCalc(supFavor) + " : POWER owes us "+ Math.abs(supFavor) + " and refused to support!");
						}
					}
				}
		System.out.println("Current paranoia with POWER: " +paranoia);
			}
		}
	}