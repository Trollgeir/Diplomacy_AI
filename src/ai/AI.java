package ai;

public abstract class AI {

	private String name;
	private String version;

	public AI(String name, String version) {
		this.name = name; 
		this.version = version; 
	}

	public abstract void onMessage(String[] message);

	public String getName() {
		return name; 
	}

	public String getVersion() {
		return version; 
	}

}
