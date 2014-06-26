package communication; 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.lang.Exception;

public class LogReader {

	public String[] names; 
	public String[] powers;
	int numObservers = 0;

	public void readLog(String path, String[] powers, String[] names) {
		this.names = names;
		this.powers = powers;
		names[0] = "Server"; 
		powers[0] = "NON";
		String line = ""; 
		try {
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
	   			processLine(line);
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Error reading log file: " + path);
			System.out.println(" at line: " + line); 

			e.printStackTrace();
		}

		for (int i = 0; i < 8; ++i) {
			System.out.println(i + ": " + names[i] + " - " + powers[i]); 
		}
	} 


	public void processLine(String line) {
		if (line.charAt(0) == ' ') return; 
		int i = 0; 
		while (line.charAt(i++) != ' ');
		i--;
		while (line.charAt(i++) == ' ');
		i--;
		char x = line.charAt(i); 
		int num = x - '0';
		if (num == 0) return;
		if(line.indexOf(">> OBS") != -1)
		{
			numObservers++;
			return;
		}
		int idx = line.indexOf("NME");
		if (idx != -1) { 
			names[num-numObservers] = processName(line, idx + 3 + 4);
		}
		idx = line.indexOf("HLO");
		if (idx != -1) {
			powers[num-numObservers] = processPower(line, idx + 3 + 3);
		}  			
	}

	public String processName(String line, int start) {
		int end = start;
		while (line.charAt(end) != '\'') end++;
		return line.substring(start, end); 
	}

	public String processPower(String line, int start) {
		int end = start;
		while (line.charAt(end) != ' ') end++;
		return line.substring(start, end);
	}

	public static void main(String args[]) {
		String a[] = new String[8];
		String b[] = new String[8];
		new LogReader().readLog(args[0], a, b);
	}

}

