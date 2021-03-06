package ai;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kb.Map;
import kb.Node;
import kb.Power;
import kb.province.Province;
import kb.unit.Unit;
import communication.LogReader;
import message.DaideList;
import message.order.Hold;
import message.order.Order;
import message.server.Submit;
import ai.dodo.Negotiator;
import game.Game;
import game.Receiver;

public abstract class AI extends Receiver {

	public String name;
	protected String version;
	protected String usage; 
	protected Power power;
	protected String passcode;
	protected String lvl;
	protected boolean canMessage;
	protected Map	map;
	protected ArrayList<ArrayList<Node>> adjacencyList;
	protected Game game;
	protected LinkedBlockingQueue<Order> queue;

	public AI(String name, String version, Map map) {
		this.name = name; 
		this.version = version;
		usage = "";
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

	public void findAdjacent()
	{
		for(Province p : map.getProvincesByOwner(this.power))
		{
			ArrayList<Node> local = new ArrayList<Node>();
			ArrayList<Node> nodes = p.coastLine;
			nodes.add(p.getCentralNode());
			
			for(Node n : nodes)
			{
				if(p.unit() != null)
				{
					for(Node adj : n.landNeighbors)
					{
						if(!local.contains(adj))
							local.add(adj);
					}
					for(Node adj : n.seaNeighbors)
					{
						if(!local.contains(adj))
							local.add(adj);
					}
				}
			}
			adjacencyList.add(local);
		}
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
	
	public Map getMap() {
		return map; 
	}

	@Override
	public void onMessage(String[] message) {
		
		if (!message[0].equals("ORD"))
		{
			String o  = "Message received: ";
			for (String p : message)
				o += p + " ";
			System.out.println(o);
		}
		
		
		if (message[0].equals ("HLO")) {
			handleHLO(message);
		}
		if (message[0].equals("SLO")) {
			handleSLO(message);
		}
		if (message[0].equals("FRM")){
			handleFRM(message);
		}
		if (message[0].equals("SMR")){
			handleSMR(message);
		}
		if (message[0].equals("THX")){
			handleTHX(message);
		}
		if (message[0].equals("YES")){
			handleYES(message);
		}
		if (message[0].equals("REJ")){
			handleREJ(message);
		}
		if (message[0].equals("HUH")){
			handleHUH(message);
		}
		if(message[0].equals("ORD")){
			handleORD(message);
		}
		
		/*TODO*/
	} 
	
	protected int[] shuffle(int size)	
	{
		int[] shuffled = new int[size];
		for(int i = 0; i < size; i++)
		{
			shuffled[i] = i;
		}
		for(int i = 0; i < shuffled.length; i++)
		{
			int j = (int)(Math.random() * shuffled.length);
			int temp = shuffled[i];
			shuffled[i] = shuffled[j];
			shuffled[j] = temp;
		}
		return shuffled;
	}
	
	protected void handleQueue()
	{
		if (queue.size() == 0) return; 
		Order[] oList = new Order[queue.size()];
		queue.toArray(oList);  
		
		Game.server.send(new Submit(oList));
		queue.clear();
	}
	
	protected abstract void handleHLO(String[] message);
	protected abstract void handleSLO(String[] message);
	protected abstract void handleFRM(String[] message);
	protected abstract void handleSMR(String[] message);
	protected abstract void handleTHX(String[] message);
	protected abstract void handleYES(String[] message);
	protected abstract void handleREJ(String[] message);
	protected abstract void handleHUH(String[] message);
	protected abstract void handleORD(String[] message);
	
	public abstract void newTurn();
	
}
