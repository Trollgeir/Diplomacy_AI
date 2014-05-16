package dip.daide.us;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import dip.daide.comm.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.*;

/**
 * Koen was here
 * Thijs is a doofus
 */

/**
 * @author team panda aka some homo's!!!!
 *
 */
public class TheDiplominator implements MessageListener{

	static final String VERSION = "0.2";
    private String name;
    private static Server serv;
    public static LinkedBlockingQueue<String[]> messageQueue = new LinkedBlockingQueue<String[]>();
    static boolean hasStarted = false;
    private Map map;
    private Power me; 
    private int passcode;
    private List<String[]> ordList = new ArrayList<String[]>();
    private int iteration = 0;
    private int pressLevel; 
    
    String year; 
    private int turn = 0; 
    
	private final int SPR = 0; 
	private final int SUM = 1; 
	private final int FAL = 2; 
	private final int AUT = 3; 
	private final int WIN = 4;
    
    public TheDiplominator(InetAddress ip, int port, String name){
    	this.name = name;
    	try {
    	    serv = new Server(ip, port);
    	    serv.addMessageListener(this);
    	    serv.connect();
    	    String[] msg = new String[]{
    	    		"NME",
                    "(", "'" + this.name + "'", ")",
                    "(", "'" + VERSION + "'", ")"
                  };
    	    
    	    serv.send(msg);
    	 
    	    
    	} catch (IOException ioe){
    		ioe.printStackTrace();
    	} catch (DisconnectedException de){
    		System.out.println("Ok, we're disconnected. Exiting...");
    		System.exit(0);
    	} catch (UnknownTokenException ute){
    		System.err.println("Unknown token '" + ute.getToken() + "'");
    		System.exit(1);
    	} 

    }
	
	public static void main(String[] args) {
		try {
		    new TheDiplominator(InetAddress.getByName(args[0]),
			       Integer.parseInt(args[1]),
			       args[2]);

		} catch (ArrayIndexOutOfBoundsException be){
		    usage();
		} catch (UnknownHostException uhe){
		    System.err.println("Unknown host: " + uhe.getMessage());
		} catch (NumberFormatException nfe){
		    usage();
		}
	}
    
	public static void usage(){
    	System.err.println("Usage:\n" +
    			   "  Diplominator <ip> <port> <name>");
    }

	public void messageReceived(String[] message) {
		
		System.out.print("Server: ");
		printMessage(message);
		
		if (!hasStarted){
			handlePreGameMessage(message);
		} else if (message[0].equals("HLO")){
			handleHLO(message);
		} else if (message[0].equals("ORD")){
				if(!message[7].equals(me.getName())) ordList.add(message);
		} else if (message[0].equals("NOW")){
			
			Enumeration powerIterator = map.listOfPowers.elements();			
		    System.out.println("--------------------------CHECKING IF I HAVE ANY FREINDS----------------------");
			while (powerIterator.hasMoreElements()){
				Power power = (Power) powerIterator.nextElement();
				
				if(power!= me && !power.getName().equals("UNO") && !power.isOut() && power.acceptedPeace()){
					System.out.println(power.getName() + " is our friend");
				}
		    }
			System.out.println("--------------------------_______________________________----------------------");
		    
			if (message[2].equals("SPR")) turn = 0;
			if (message[2].equals("SUM")) turn = 1;
			if (message[2].equals("FAL")) turn = 2;
			if (message[2].equals("AUT")) turn = 3;
			if (message[2].equals("WIN")) turn = 4;
			year = message[3];
			if(turn == FAL){
				map.handleChances(me);
			}

			if(turn == SUM || turn == WIN || turn == AUT || turn == FAL) {
				map.handleORD(ordList, me);
			}
			ordList.removeAll(ordList);

			map.storeSeason(message[2]);
			map.updateUnits(message);
			map.shouldWeStab(me);
			
			if (turn == FAL || turn == SPR){
				messageQueue.clear();
				new Negotiator(map, messageQueue, me, pressLevel).start();
//				System.out.println("After thread declaration");
			} else {
				// do win, sum, aut
				List<String[]> orders = map.processNOW(me);
				for (int i = 0; i < orders.size(); i++){
					sendMessage(orders.get(i));
				}
			}
			
		} else if (message[0].equals("SCO")){
			// Handle SCO
			map.handleChances(me);
			map.handleORD(ordList, me);
			ordList.removeAll(ordList);
			map.updateSCO(message);
		}else if (message[0].equals("YES")){ 
			// DO NOTHING
		}else if (message[0].equals("MIS")){ 
		
		} else if (message[0].equals("OUT")){
			map.listOfPowers.get(message[2]).setOut();
			//SOMEONE HAS BEEN BOOTED. THEY LOSE
		} else if (message[0].equals("HST")){
			// Handle HST
		} else if (message[0].equals("OFF")){
//			System.out.println("The server has disconnected me. Game Over.");
//			System.out.println("                     ,-. ");
//			System.out.println("            ,     ,-.   ,-. ");
//			System.out.println("           / |   (   )-(   ) ");
//			System.out.println("           \\ |  ,.>-(   )-< ");
//			System.out.println("            \\|,' (   )-(   ) ");
//			System.out.println("             Y ___`-'   `-' ");
//			System.out.println("             |/__/   `-' ");
//			System.out.println("             | ");
//			System.out.println("             | ");
//			System.out.println("             | ");
//			System.out.println("          ___|_____________");
			System.exit(0);
		} else if (message[0].equals("THX")){
			// do absolutely nothing
					
		} else if (message[0].equals("CCD")){
			 
		} else if (message.length >= 11 && message[8].equals("TRY") && message[9].equals("(") && message[10].equals(")")){
//			FRM ( ITA ) ( GER ) ( TRY ( ) )
			// This bot is a no-press bot, we should stop trying to talk to it maybe... 
			map.listOfPowers.get(message[2]).setNoPress();
		}
		else if (message[0].equals("SLO")){
			
			if (message[2].equals(me.getName())){
				System.out.println("The game is over. We won.");
				System.exit(0);
			}
			else {
				System.out.println("The game is over. " + message[2] + " won.");
				System.exit(0);
			}
		}
		else {
			messageQueue.add(message);
//			printMessage(messageQueue.peek());
//			System.out.println("Diplomatic message added to queue.");
		}
	}
	
	private void handleHLO(String[] message) {
		me = map.getPower(message[2]);
		passcode = Integer.parseInt(message[5]);
		
		// HLO (ENG) (1234) ((LVL 20) (MTL 1200))
		pressLevel = Integer.parseInt(message[10]);
		
		//------------ LATER NEED TO SET VARIANT SUCH AS PRESS LEVEL
		// CBA to do it now.
	}

	private void handlePreGameMessage(String[] message) {
		
		if(message[0].equalsIgnoreCase("MAP")){
			if(message[2].equalsIgnoreCase("'STANDARD'")){
				sendMessage(new String[] {"MDF"});
			}
		}
		if(message[0].equalsIgnoreCase("MDF")){
			map = new Map(message);
			sendMessage(new String[]{"YES", "(", "MAP", "(", "'STANDARD'", ")", ")"});
//			qProc.start();			
			hasStarted = true; 
			
		}
	}

	static void printMessage(String [] msg){
		for(int i =0; i < msg.length; i++){
			System.out.print(msg[i]+" ");
		}
		System.out.println();
	}
	
	static void sendMessage(String [] msg){
		try {
			serv.send(msg);
			System.out.print("Diplominator: ");
			printMessage(msg);
			
		} catch (DisconnectedException de){
    		System.out.println("No longer connected to server. Exiting.");
    		System.exit(0);
    	} catch (UnknownTokenException ute){
    		System.err.println("Unknown token '" + ute.getToken() + "'");
    		System.exit(1);
    	} 
	}
}
