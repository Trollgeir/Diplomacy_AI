package ai;

import game.Receiver;

public abstract class AI extends Receiver {

	private String name;
	private String version;
	private String usage; 

	public AI(String name, String version) {
		this.name = name; 
		this.version = version;
		usage = "";
	}

	public void init(String[] args) throws ArrayIndexOutOfBoundsException {}; 

	public String getName() {
		return name; 
	}

	public String getVersion() {
		return version; 
	}

	public String getUsage() {
		return usage;
	}

}
