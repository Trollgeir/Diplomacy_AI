package ai.dodo;

public class Trust {

	//need a counter for phases = time
	
	public static double supIntolerance = 0.5; 		//pick a value between 0-1. 0 means you don't care about support reciprocity
	public static double tHalflife = 1.05;			//treaty half-life
	public static double tTrustInc = 0.03;			//How much trust to increment for every phase as long as the treaty holds.
	
	
	
	private static double round(double i) { 		//Rounding numbers.
		return Math.round(i*10000) / 10000.0;
	}
	private static double supCalc(int supFavor) {	//Function for updating 
		return ((supIntolerance * Math.abs(supFavor)) / 100);
	}
	private static double pUpdate(double time) {
		return (Math.pow(tHalflife,(1- time)));
	}
	
		
	public static void main(String[] args) { 
		
		boolean treatyDefect = false;			//import from defectionCheck
		boolean expectingSupport = true;		//import from Negotiator buffer?
		boolean supAccept = true;				//import from Negotiator
		double powerTrustValue = 5; 			//import from beliefbase
		int supFavor = -5; 						//import from alliancelist of relevant power
		
	
		for (int phase = 0; phase < 15; ++phase) { //SIMULATION!
				
			//TREATY Calculations & Paranoia update
			double paranoia = 1 - (pUpdate(phase)*powerTrustValue/10);
			paranoia = round(paranoia);
			
			if(treatyDefect == false) {
				powerTrustValue += tTrustInc;
				System.out.println("+" +tTrustInc+ " trust to POWER for maintaining treaty!");
				}
			else {
				powerTrustValue -= pUpdate(phase);
				System.out.println("POWER broke treaty! New trust is: " +powerTrustValue);
				break;
				//Remove treaty from list?
				}
		
			
			//SUPPORT calculations
			if (supIntolerance != 0 && expectingSupport == true) {
				if (supAccept == true) {
					if (supFavor >= 0) { // We owe them
						//Increment the trust of relevant power
						powerTrustValue += supCalc(supFavor);
						powerTrustValue = round(powerTrustValue);
						System.out.println("We owe POWER " + Math.abs(supFavor) + " supports and they continue to support us! New trust for POWER:" + powerTrustValue);
						System.out.println("New paranoia for POWER : " + paranoia);
						++supFavor;
						}
					else {	// They owe us
							//Debugging (no change in trust)
						System.out.println("POWER owe us and they accepted our request to support");
						++supFavor;
					}
				}
				else {
					if(supFavor >= 0) { //We owe them.
							//Debugging (no change in trust)
						System.out.println("We owe POWER " + Math.abs(supFavor) + " supports they refused to help. This is okay!");
						}
					else { 	//They owe us
							//Decrement the trust of relevant power
						powerTrustValue -= supCalc(supFavor);
						powerTrustValue = round(powerTrustValue);
						System.out.println("POWER owes us "+ supFavor + " and refused to support! New trust for POWER:" + powerTrustValue);
					}
				}
			}
		}
	}
}