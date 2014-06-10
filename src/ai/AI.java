package ai;

import negotiator.Negotiator;
import game.Receiver;

public abstract class AI extends Receiver {

	private String name;
	private String version;
	private String usage; 
	private String power;
	private String passcode;
	private String lvl;
	private boolean canMessage;
	protected Negotiator negotiator;

	public AI(String name, String version) {
		this.name = name; 
		this.version = version;
		usage = "";
		this.negotiator = new Negotiator();
	}

	public void init(String[] args) throws ArrayIndexOutOfBoundsException {}; 

	public String getName() {
		return this.name; 
	}

	public void setName(String name) {
		this.name = name; 
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
	
	public String getLVL(){
		return this.lvl;
	}
	
	public boolean getCanMessage(){
		return this.canMessage;
	}
	
	public void setPower(String power) {
		this.power = power;
	}
	
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
	
	public void setLVL(String lvl){
		this.lvl = lvl;
	}
	
	public void setCanMessage(boolean cm){
		this.canMessage = cm;
	}
	

}
