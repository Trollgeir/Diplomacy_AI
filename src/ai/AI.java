package ai;

import game.Receiver;

public abstract class AI extends Receiver {

	private String name;
	private String version;
	private String usage; 
	private String power;
	private String passcode;
	private String[] variant;

	public AI(String name, String version) {
		this.name = name; 
		this.version = version;
		usage = "";
	}

	public void init(String[] args) throws ArrayIndexOutOfBoundsException {}; 

	public String getName() {
		return this.name; 
	}

	public String getVersion() {
		return this.version; 
	}

	public String getUsage() {
		return this.usage;
	}
	
	public String getPower() {
		return this.power;
	}
	
	public String getPasscode() {
		return this.passcode;
	}
	
	public String[] getVariant() {
		return this.variant;
	}
	
	public void setPower(String power) {
		this.power = power;
	}
	
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
	
	public void setVariant(String[] variant) {
		this.variant = variant.clone();
	}
	

}
