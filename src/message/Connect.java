package message; 

public class Connect implements DaideMessage {

	/**
		Message class to request connection to a server
	**/

	private String name; 
	private String version;  

	public Connect(String name, String version) {
		this.name = name; 
		this.version = version; 
	} 

	@Override
	public DaideList daide() {
		DaideList result = new DaideList();
		
        result.add2("NME");
        result.add2("(", "'" + name + "'", ")");
        result.add2("(", "'" + version + "'", ")");

		return result;
	}
}

