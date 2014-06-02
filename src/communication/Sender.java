package communication;

import java.net.InetAddress;
import server.Server;

public class Sender
{
	int port;
	String name;
	InetAddress ip;
	Server serv;
	

	public Sender(InetAddress _ip, int _port, String _name)
	{
		this.ip = _ip;
		this.port = _port;
		
		this.name = _name;
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
    		System.out.println("Disconnected");
    		System.exit(0);
    	} catch (UnknownTokenException ute){
    		System.err.println("Unknown token '" + ute.getToken() + "'");
    		System.exit(1);
    	} 
		
	}
	
	public void send(String[] message)
	{
		serv.send(message);
	}
	
}
