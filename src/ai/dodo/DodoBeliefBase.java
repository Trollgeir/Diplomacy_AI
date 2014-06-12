package ai.dodo;

import kb.Map;
import kb.province.Province;

public class DodoBeliefBase {
	Map		map;
	
	java.util.Map<Province, Integer>	threat;
	
	
	public DodoBeliefBase(Map map)
	{
		this.map = map;
		//threat = new java.util.Map<Province, Integer>();
	}
	
	
	
	
}
