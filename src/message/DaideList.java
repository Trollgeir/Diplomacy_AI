package message; 

import java.util.ArrayList;

public class DaideList extends ArrayList<String> {

	public void add2(String ... strings) {
		for (String s : strings) 
			add(s); 
	};

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < size(); ++i) {
			result += get(i); 
			result += i < size() - 1 ? " " : "";
		}
		return result; 
	} 

}