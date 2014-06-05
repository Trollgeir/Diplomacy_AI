package communication;

import java.io.IOException;
import java.net.InetAddress;
import message.DaideList;
import message.DaideMessage;

import communication.server.DisconnectedException;
import communication.server.MessageListener;
import communication.server.Server;
import communication.server.UnknownTokenException;

public class Sender implements MessageListener
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
                    "(", "' v 0.1 '", ")"
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
		try {
			serv.send(message);
		} catch (UnknownTokenException | DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void messageReceived(String[] message) {
		// TODO Auto-generated method stub
		
	}
	
	public static String[] toDaide(DaideMessage message) {
		DaideList l = message.daide();
		String result[] = new String[l.size()];
		for (int i = 0; i < l.size(); ++i) {
			result[i] = l.get(i);
		}
		return result;
	}
}
