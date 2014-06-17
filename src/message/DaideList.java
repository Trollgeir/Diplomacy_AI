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

	
	public static int unBracket(String[] messageIn, int start)
	{
		int end = 0;
		int bracketCount = 0;
		
		//Is the message even bracketed
		if (!messageIn[start].equals("("))
			return -1;
		
		//Iterate over all the words
		for (int i = start; i < messageIn.length; i++)
		{
			String cWord = messageIn[i];
			//Take care of nested brackets
			if (cWord.equals("("))
			{
				bracketCount++;
				continue;
			}
			
			if (cWord.equals(")"))
			{
				bracketCount--;
				if (bracketCount == 0)
				{
					//Stop searching if all of the nested brackets are closed
					end = i;
					break;
				}
			}
		}
		if (bracketCount != 0)
			return -1;
		
		return end;
	}
}