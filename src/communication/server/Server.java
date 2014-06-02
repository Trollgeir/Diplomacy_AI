/*****************************************************************************
 * $Id: Server.java,v 1.1 2004/09/09 06:32:18 heb Exp $ 
 *
 * Copyright © 2002, 2003, 2004 by Henrik Bylund
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies.
 *****************************************************************************/
package communication.server;

import java.net.InetAddress;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * The <code>Server</code> class wraps a <code>Connection</code>
 * with a more user-friendly interface.
 *
 * As of the current version, the extra user-friendliness is minimal.
 * The <code>Server</code> class allows for more than one message
 * listener, but that's all. In future versions, the Server will
 * deal with real Message classes instead of token arrays.
 * Responses (acknowledgements and rejections) of messages will also
 * be handled in a synchrone way by the <code>Server</code> class.
 *
 * @author <a href="mailto:heb@ludd.luth.se">Henrik Bylund</a>
 * @version 1.0
 */
public class Server implements DiplomaticMessageListener {
    Connection conn;
    int port;
    InetAddress ip;

    LinkedList listeners;

    /**
     * Creates a peer to the server.
     * A connection will not actually be created until
     * <code>connect()</code> is called.
     *
     * @param ip The IP address of the server.
     * @param port The TCP port on which to contact the server.
     * @see #connect
     */
    public Server(InetAddress ip, int port){
	this.ip = ip;
	this.port = port;
	listeners = new LinkedList();
    }

    /**
     * Connect to the server and perform neccessary handshaking.
     *
     * @throws IOException if an I/O exception occured.
     * @throws DisconnectedException if we were disconnected during
     *                               the handshake process.
     */
    public void connect() throws IOException, DisconnectedException {
	conn = new Connection(ip, port, this);
    }

    /**
     * Listens to messages from the server.
     *
     * This method may possibly be broken down to allow
     * for different listeners for different types of
     * messages.
     */
    public void addMessageListener(MessageListener listener){
	synchronized(listeners){
	    listeners.add(listener);
	}
    }

    /**
     * Receive a diplomatic message.
     * The message will be passed on to all <code>MessageListener</code>s
     * that are registered.
     */
    public void diplomaticMessageReceived(String[] message){
	synchronized(listeners){
	    Iterator iter = listeners.iterator();
	    while(iter.hasNext()){
		MessageListener ml = (MessageListener)iter.next();
		ml.messageReceived(message);
	    }
	}
    }

    /**
     * Send a message to the server.
     *
     * @param msg The tokens to send to the server.
     * @throws UnknownTokenException if any of the tokens was not recognized.
     * @throws DisconnectedException if we are already disconnected from
     *                               the server.
     */
    public void send(String[] msg)
	throws UnknownTokenException, DisconnectedException {
	conn.send(msg);
    }
}
