package kb;

import communication.LogReader;

	/**
		This class uses the server log file to obtain knowledge about which name corresponds with which 
		power. This knowledge is not allowed by DAIDE rules, but can be used to apply learning.  
	**/

public class Names {


	// Path to log file
	String path; 

	// 
	public Power[] powers; 
	public String[] names;

	public Names(String path) {
		this.path = path; 
		powers = new Power[8];
		names = new String[8];
	}

 	public void init(Map map) {
 		try {
    		Thread.sleep(1000);
		} catch(InterruptedException ex) {
    		Thread.currentThread().interrupt();
		}
		String[] s_powers = new String[8];
 		new LogReader().readLog(path, s_powers, names);
 		for (int i = 1; i < 8; ++i) {
 			powers[i] = map.getPower(s_powers[i]); 
 		}

 		for (int i = 1; i < 8; ++i) {
 			System.out.println(names[i] + ", " + powers[i].getName()); 
 		}
 	}
 	
 	public String getNameByPower(Power p)
 	{
 		for(int i = 1; i < 8; i++)
 		{
 			if(powers[i].getName().equals(p.getName()))
 			{
 				return names[i];
 			}
 		}
 		return "";
 	}
}