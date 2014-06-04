package message; 

import java.util.ArrayList;

public class DaideList extends ArrayList<String> {

	public void add2(String ... strings) {
		for (String s : strings) 
			add(s); 
	};

}