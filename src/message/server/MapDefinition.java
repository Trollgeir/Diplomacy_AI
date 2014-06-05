package message.server; 

import message.DaideList;
import message.DaideMessage;

public class MapDefinition implements DaideMessage {
	
	public MapDefinition() {
		/* Nothing to do */
	}

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		result.add("MDF"); 
		return result;
	}

}