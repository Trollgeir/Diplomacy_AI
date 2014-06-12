package ai;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kb.Map;
import kb.Power;
import kb.unit.Unit;
import communication.LogReader;
import message.order.Hold;
import message.order.Order;
import message.server.Submit;
import negotiator.Negotiator;
import game.Game;
import game.Receiver;

public abstract class AI extends Receiver {

	private String name;
	private String version;
	private String usage; 
	private Power power;
	private String passcode;
	private String lvl;
	private boolean canMessage;
	protected Negotiator negotiator;
	protected Map	map;
	protected Game game;
	protected LinkedBlockingQueue<Order> queue;

	public AI(String name, String version, Map map) {
		this.name = name; 
		this.version = version;
		usage = "";
		this.negotiator = new Negotiator();
		this.queue = new LinkedBlockingQueue<Order>();
		this.map = map;
		map.setAI(this);
	}

	public void init(String[] args) throws ArrayIndexOutOfBoundsException {}; 

	public void setGame(Game g)
	{
		game = g;
	}
	
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
	
	public Power getPower() {
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
	
	public void setPower(Power power) {
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
	
	@Override
	public void onMessage(String[] message) {
		
		String[] newMessage = new String[1024];
		int j = 0;
		for(int i = 0; i < message.length; i++)
		{
			if(!message[i].equals(")") && !message[i].equals("("))
			{
				newMessage[j] = message[i];
				j++;
			}
		}		
		
		if (message[0].equals ("HLO")) {
			handleHLO(newMessage);
		}
		if (message[0].equals("SLO")) {
			handleSLO(newMessage);
		}
		if(message[0].equals("FRM")){
			handleFRM(newMessage);
		}
		if (message[0].equals("SMR")){
			handleSMR(newMessage);
		}
		if(message[0].equals("THX")){
			handleTHX(newMessage);
		}
		if(message[0].equals("YES")){
			handleYES(newMessage);
		}
		if(message[0].equals("REJ")){
			handleREJ(newMessage);
		}
		if(message[0].equals("HUH")){
			handleHUH(newMessage);
		}
		
		/*TODO*/
	} 
	
	protected void handleQueue()
	{
		Order[] oList = new Order[queue.size()];  
		
		int i = 0;
		while (queue.size() > 0)
		{
			oList[i] = queue.poll();
			queue.poll();
		}
		
		Game.server.send(new Submit(oList));
	}
	
	protected abstract void handleHLO(String[] message);
	protected abstract void handleSLO(String[] message);
	protected abstract void handleFRM(String[] message);
	protected abstract void handleSMR(String[] message);
	protected abstract void handleTHX(String[] message);
	protected abstract void handleYES(String[] message);
	protected abstract void handleREJ(String[] message);
	protected abstract void handleHUH(String[] message);
	
	public abstract void newTurn();
}
