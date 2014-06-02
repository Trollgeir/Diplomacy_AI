/*****************************************************************************
 * $Id: Connection.java,v 1.1 2004/09/09 06:32:18 heb Exp $ 
 *
 * Copyright © 2002, 2003, 2004 by Henrik Bylund
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies.
 *****************************************************************************/
package communication.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The <code>Connection</code> class is responsible for
 * communicating with the AIServer. This includes doing handshaking,
 * parsing the C-S Protocol and sending/receiving diplomatic messages
 * to and from the remote server.
 *
 * Communication with the Connection class is done via the string
 * representation of the language syntax. Conversion between the string
 * mnemonics and the 2-octet data representation is handled by the
 * <code>Connection</code> class.
 *
 * @author <a href="mailto:heb@ludd.luth.se">Henrik Bylund</a>
 * @version 1.0
 */
public class Connection {
    // Pseudo-states
    private final static int IDLE           = 0x0000;
    private final static int SENDING        = 0x0100;
    private final static int RECEIVING      = 0x0200;
    private final static int HANDSHAKE      = 0x0400;
    private final static int ERROR          = 0x0800;

    private final static int CONN           = 0x0000;
    private final static int RECV           = 0x0010;
    private final static int SEND           = 0x0020;

    private final static int IM             = 0x0000;
    private final static int RM             = 0x0001;
    private final static int DM             = 0x0002;
    private final static int FM             = 0x0004;
    private final static int EM             = 0x0008;

    /* XXX - Right now, I put us in an error state when an EM has
     * been received. Look into this.
     private final static int EM_SENT        = IDLE    | 0x00;
    */
    private final static int DM_SENT        = IDLE | SEND | DM;
    private final static int RM_RECEIVED    = IDLE | RECV | RM;
    private final static int DM_RECEIVED    = IDLE | RECV | DM;
    
    private final static int SENDING_FM     = SENDING | SEND | FM;
    private final static int SENDING_EM     = SENDING | SEND | EM;
    private final static int SENDING_DM     = SENDING | SEND | DM;

    private final static int RECEIVING_DM   = RECEIVING | RECV | DM;
    private final static int RECEIVING_EM   = RECEIVING | RECV | EM;
    private final static int RECEIVING_FM   = RECEIVING | RECV | FM;
    
    private final static int CONNECTING     = HANDSHAKE | 0x0;
    private final static int CONNECTED      = HANDSHAKE | 0x1;
    private final static int SENDING_IM     = HANDSHAKE | 0x2;
    private final static int IM_SENT        = HANDSHAKE | 0x4;
    private final static int WAITING_FOR_RM = HANDSHAKE | 0x8;

    /* XXX - Can we recover from some EM? */
    private final static int EM_RECEIVED    = ERROR | RECV | EM;
    private final static int EM_SENT        = ERROR | SEND | EM;
    private final static int FM_RECEIVED    = ERROR | RECV | FM;
    private final static int FM_SENT        = ERROR | RECV | FM;
    private final static int DISCONNECTED   = ERROR | CONN;
    
    int state;
    OutputStream output;
    DiplomaticMessageListener listener;

    /**
     * Creates a new connection to a remote server.
     *
     * @param ip       The TCP/IP address of the remote server
     * @param port     The port on which the remote server is listening.
     * @param listener The listener to which to send the diplomatic
     *                 messages.
     * @throws IOException if data could not be sent to the server
     *                     (Possibly because there is no answer at
     *                      the specified address)
     * @throws DisconnectedException if disconnected during the handshake
     *                               process.
     */
    public Connection(InetAddress ip, int port,
                      DiplomaticMessageListener listener)
        throws IOException, DisconnectedException {
        this.listener = listener;
        connect(ip, port);
    }

    /**
     * Connects to the server.
     * If the server listens on the specified TCP address,
     * the handshake procedure will be attempted;
     * <OL>
     *   <LI>  an <code>Initial Message</code> will be sent  </LI>
     *   <LI>  a <code>Representation Message</code> will be received  </LI>
     * </OL>
     *
     * @param ip   the IP address to connect to
     * @param port the port number to connect to
     * @throws IOException if data could not be sent to the server
     *                     (Possibly because there is no answer at
     *                      the specified address)
     * @throws DisconnectedException if disconnected during the handshake
     *                               process.
     */
    void connect(InetAddress ip, int port)
        throws IOException, DisconnectedException {
        setState(CONNECTING);
        Socket socket = new Socket(ip, port);
        Consumer consumer = new Consumer(socket.getInputStream());
        consumer.start();
        output = socket.getOutputStream();
        setState(CONNECTED);
 
        // Send the Initial Message
        byte[] im = createInitialMessage();
        send(SENDING_IM, im, IM_SENT);
 
        // Receive the Representation Message
        setState(WAITING_FOR_RM);
        waitForState(RM_RECEIVED);
    }

    /**
     * Disconnects from the AI server.
     * Sends a <code>Final Message</code> to the server, then
     * sets the state to "disconnected".
     *
     * @throws IOException If an error occurs on the underlying layer
     * @throws DisconnectedException If we're already disconnected
     */
    public void disconnect() throws IOException, DisconnectedException {
        byte[] fm = createFinalMessage();
        send(SENDING_FM, fm, FM_SENT);
        setState(DISCONNECTED);
    }
    
    /**
     * Sends a message to the AI server.
     * The array of tokens supplied will be sent as a
     * <code>Diplomatic Message</code> to the server.
     *
     * @param message The tokens to send to the server.
     * @throws UnknownTokenException if the message contains a token
     *                               that is unknown to the local parser.
     *                               Note that if all tokens are deemed to
     *                               be ok, no exception will be thrown
     *                               even if the server labels a token
     *                               as unknown. In this case, an
     *                               <code>Error Message</code> will be
     *                               received from the server instead.
     * @throws DisconnectedException if we're already disconnected from
     *                               the server when attempting to send
     *                               this message.
     */
    public void send(String[] message)
        throws UnknownTokenException, DisconnectedException {
        byte[] dm = createDiplomaticMessage(convert(message));
        send(SENDING_DM, dm, DM_SENT);
    }

    /**
     * Sends the message to the server.
     * This method wraps the sending of the message to the server
     * in a state transition.
     *
     * @param preState The state to set the connection into while
     *                 sending the message.
     * @param message The message to send.
     * @param postState The state to set the connection into
     *                  if the message was sent in good order.
     */
    void send(int preState, byte[] message, int postState)
        throws DisconnectedException {
        synchronized(output){
            if((state & ERROR) > 0){
                throw new DisconnectedException();
            }
            try {
                setState(preState);
//   System.err.println("Sending to server:\n  ");
//   for(int i = 0; i < message.length; i++){
//       System.out.print("0x" + Integer.toHexString(message[i]));
//       if(i % 2 == 0){
//    System.out.print(",");
//       } else {
//    System.out.print(" ");
//       }
//   }
//   System.out.println();
                output.write(message);
                setState(postState);
            } catch (IOException ioe){
                ioe.printStackTrace();
                setState(DISCONNECTED);
            }
        }
    }

    /**
     * Creates an initial message.
     *
     * @return A well-formatted <code>Initial Message</code>
     *         with type=0, version=1, magic number=0xDA,0x10
     */
    byte[] createInitialMessage(){
        return new byte[]
            {(byte)0x00,             // Msg type
             (byte)0x00,             // pad
             (byte)0x00, (byte)0x04, // Remaining length
             (byte)0x00, (byte)0x01, // Version
             (byte)0xDA, (byte)0x10  // Magic number
            };
    }

    /**
     * Creates a diplomatic message.
     *
     * @param data The body of the message
     * @return A well-formatted <code>Diplomatic Message</code>
     */
    byte[] createDiplomaticMessage(byte[] data){
        byte[] bytes = new byte[data.length + 4];
        bytes[0] = 0x02;                         // Msg type
        bytes[1] = 0x00;                         // pad
        bytes[2] = (byte)(data.length >> 8);     // Remaining length
        bytes[3] = (byte)(data.length & 0xff);   // Remaining length
        System.arraycopy(data, 0, bytes,
                         4, data.length);        // Language Message
        return bytes;
    }

    /**
     * Creates an error message
     *
     * @param code The error code to send
     * @return A well-formatted <code>Error Message</code>
     */
    byte[] createErrorMessage(byte code){
        return new byte[]
            {(byte)0x04,             // Msg type
             (byte)0x00,             // pad
             (byte)0x00, (byte)0x02, // Remaining length
             (byte)0x00, code                    // Error code
            };
    }

    /**
     * Creates a final message
     *
     * @return A well-formatted <code>Final Message</code>
     */
    byte[] createFinalMessage(){
        return new byte[]
            {(byte)0x03,             // Msg type
             (byte)0x00,             // pad
             (byte)0x00, (byte)0x00, // Remaining length
            };
    }

    /**
     * Handles an incoming message.
     * This method only delegates to the relevant method for the
     * message type
     *
     * @param type The type of the message
     * @param message The body of the message
     */
    void handleMessage(byte type, byte[] message){
        switch(type){
        case 1:
            handleRepresentationMessage(message);
            break;
        case 2:
            handleDiplomaticMessage(message);
            break;
        case 3:
            handleFinalMessage();
            break;
        case 4:
            handleErrorMessage(message);
            break;
        default:
            unhandledMessage(type, message);
            break;
        }
    }

    /**
     * Handles unknown message types.
     * This would only occur when the server sends badly formatted
     * messages, or the well-formatted message is parsed badly
     * by the receiving side.
     *
     * @param type    The supposed type of the message
     * @param message The body of the message
     */
    void unhandledMessage(int type, byte[] message){
        // XXX - parse this
    }

    /**
     * Handles a <code>Representation Message</code>.
     * Any tokens encoded in the message body will be added
     * to the Token singleton.
     * Upon correct decoding, the state will be set to
     * <code>RM_RECEIVED</code>
     *
     * @param message The body of the message.
     */ 
    void handleRepresentationMessage(byte[] message){
        for(int i = 0; i < message.length; i+=6){
            byte[] bits = new byte[]{message[i], message[i+1]};
            byte[] token  = new byte[]{message[i+2],
                                       message[i+3],
                                       message[i+4]};
            Token.add(bits, new String(token));
     
        }
        setState(RM_RECEIVED);
    }

    /**
     * Handles a <code>Diplomatic Message</code>.
     * The message will be converted to a string array of tokens,
     * and passed on to our listener.
     * Upon reception by our listener, the state will be set to
     * <code>DM_RECEIVED</code>.
     * <br>
     * If the message contains a token that is unknown to us,
     * the token conversion will fail. An <code>Error Message</code>
     * will be sent to the server, and the state will be set to
     * <code>EM_SENT</code>.
     * 
     * @param message The body of the message. This body will be
     *                decoded into a <code>String[]</code> which
     *                is sent to the listener.
     */
    void handleDiplomaticMessage(byte[] message){
            String[] tokens = convert(message);
            listener.diplomaticMessageReceived(tokens);
            setState(DM_RECEIVED);

    }

    /**
     * Handles a <code>Final Message</code>.
     * The state will be set to <code>FM_RECEIVED</code>.
     */
    void handleFinalMessage(){
        setState(FM_RECEIVED);
    }

    /**
     * Handles an <code>Error Message</code>.
     * A textual description of the error code will be
     * printed to <code>System.out</code>, after which the state
     * will be set to <code>EM_RECEIVED</code>.
     *
     * @param message The body of the error message. The error code will
     *                be read from <code>message[0],message[1]</code>.
     */
    void handleErrorMessage(byte[] message){
        String detail = "Server reports: ";
        switch(message[1]){
        case 0x01:
            detail += "IM timer popped";
            break;
        case 0x02:
            detail += "IM was not the first message sent by the client";
            break;
        case 0x03:
            detail += "IM indicated the wrong endian";
            break;
        case 0x04:
            detail += "IM had an incorrect magic number";
            break;
        case 0x05:
            detail += "Version incompatibility";
            break;
        case 0x06:
            detail += "More than 1 IM sent";
            break;
        case 0x07: // Huh?
            detail += "IM sent by server";
            break;
        case 0x08:
            detail += "Unknown message received";
            break;
        case 0x09:
            detail += "Message shorter than expected";
            break;
        case 0x0A:
            detail += "DM sent before RM";
            break;
        case 0x0B: // Huh?
            detail += "RM was not the first message sent by the server";
            break;
        case 0x0C: // Huh?
            detail += "More than 1 RM sent";
            break;
        case 0x0D:
            detail += "RM sent by client";
            break;
        case 0x0E:
            detail += "Invalid token in DM";
            break;
        default:
            detail = "[Unknown error code " + message[0] + message[1] + "]";
        }
        System.out.println(detail);
        setState(EM_RECEIVED);
    }
 

    /**
     * Sets the state of the connection.
     * Prints the new state on <code>System.err</code> and then
     * awakes all threads waiting on this object's monitor.
     *
     * @param newState The state to set the connection into.
     */
    synchronized void setState(int newState){
        state = newState;
        if((state & ERROR) != 0){
            System.err.println("Entering state 0x" +
                               Integer.toHexString(newState) +
                               " which is an error state.");
        }
        this.notifyAll();
    }

    /**
     * Waits for a specific state.
     *
     * @param targetState The state bits that must be set for
     *                    this method to return.
     */
    synchronized void waitForState(int targetState){
        while((state & targetState) != targetState){
            try {
                this.wait();
            } catch (InterruptedException ie){}
        }
    }

    /**
     * Converts a message into a <code>String[]</code> token representation.
     *
     * @param bits The data to convert.
     * @return the tokens in their mnemonic representation.
     * @throws UnknownTokenException if a token encountered in the data
     *                               stream is not known by the
     *                               <code>Token</code> singleton.
     */
    String[] convert(byte[] bits) {
        StringBuffer str = null;

        boolean inString = false;
        ArrayList list = new ArrayList(bits.length / 2);
 
        for(int i = 0; i < bits.length; i += 2){
            String token;
            if(bits[i] == Token.TEXT){
                if(!inString){
                    str = new StringBuffer();
                    str.append('\'');
                    inString = true;
                }
                str.append((char)bits[i+1]);
                continue;
            } else {
                /*
                 * Text tokens will never be the last token in the stream,
                 * therefore we close the buffer and add it to the list
                 * here.
                 */
                if(inString){
                    str.append('\'');
                    list.add(str.toString());
                    str = null;
                    inString = false;
                }
                try {
                    list.add(Token.convert(new byte[]{bits[i], bits[i+1]}));
                } catch (UnknownTokenException ute){
                    if((list.size() > 0) &&
                       ((String)list.get(0)).equals("HUH")){
                        list.add(Integer.toHexString(bits[i]) +
                                 "," +
                                 Integer.toHexString(bits[i+1]));
                    } else {
                        System.err.println("Unknown token encountered after " +
                                           "these tokens:");
                        Iterator iter = list.iterator();
                        while(iter.hasNext()){
                            System.err.print(iter.next() + " ");
                        }
                        System.err.println();
                        //Send a HUH message to the server.  Note that the official server
                        //does not very well handle having a HUH message sent to it.  
                        //David has promised to fix this.
                    	try {
	                        byte errMessage [] = new byte [bits.length + 8];
	                    	byte [] HUH = Token.convert("HUH");
	                    	errMessage[0] = HUH[0];
	                    	errMessage[1] = HUH[1];
	                    	byte [] PAR = Token.convert("(");
	                    	errMessage[2] = PAR[0];
	                    	errMessage[3] = PAR[1];
	                    	System.arraycopy(bits, 0, errMessage, 4, i);
	                    	byte ERR [] = Token.convert("ERR");
	                    	errMessage[i + 4]  = ERR[0];
	                    	errMessage[i + 5] = ERR[1];
	                    	System.arraycopy(bits, i, errMessage, i + 6, bits.length - i);
	                    	PAR = Token.convert(")");
	                    	errMessage[bits.length + 6] = PAR[0];
	                    	errMessage[bits.length + 7] = PAR[1];
	                    	
	                    	 byte[] dm = createDiplomaticMessage(errMessage);
	                         try
							{
								send(SENDING_DM, dm, DM_SENT);
							} catch (DisconnectedException e)
							{
								e.printStackTrace();
							}
                    	}
                    	catch (UnknownTokenException e) {}
						list.add("UNKNOWN");
                    }
                }
            }
        }
        return (String[])list.toArray(new String[]{});
    }

    /**
     * Converts a message from its <code>String[]</code> token
     * representation.
     *
     * @param tokens The mnemonic representation of the tokens
     * @return the protocol representation of the tokens
     * @throws UnknownTokenException if a token encountered in the array
     *                               is not known by the <code>Token</code>
     *                               singleton.
     */
    byte[] convert(String[] tokens) throws UnknownTokenException {
        int numTokens = tokens.length;
        byte[] bytes = new byte[numTokens * 2];
        int t = 0;
        for(int i = 0; i < numTokens; i++){
            String token = tokens[i];
            byte[] bits;
            if(token.startsWith("'")){
                bits = Token.convertText(token);
                if(bits.length != 1){ // Grow/shrink the target array
                    int newSize = bytes.length + bits.length - 2;
                    byte[] newArray = new byte[newSize];
                    System.arraycopy(bytes, 0, newArray, 0, t);
                    bytes = newArray;
                }
            } else {
                bits = Token.convert(token);
            }
            System.arraycopy(bits, 0, bytes, t, bits.length);
            t += bits.length;
        }
        return bytes;
    }
    

    /**
     * The consumer of incoming messages.
     * This thread will run while the connection's state is not an
     * error state.
     * 
     */
    public class Consumer extends Thread {
        InputStream input;

        /**
         * Creates a consumer.
         * The specified <code>InputStream</code> will be used for
         * reading incoming messages until the <code>Connection</code>
         * enters an error state.
         *
         * @param in The stream to read messages from
         */
        public Consumer(InputStream in){
            this.input = in;
        }

        /**
         * Continously read messages from the <code>InputStream</code>
         * until the <code>Connection</code> enters an error state.
         * When a complete message has been read, it will be passed
         * on to <code>handleMessage</code>.
         * If an I/O error occurs, the JVM will exit.
         *
         * @see Connection#handleMessage
         */
        public void run(){
     
            while((Connection.this.state & ERROR) == 0){
                try {
                    byte[] msgType   = new byte[1];
                    byte[] length_hi = new byte[1];
                    byte[] length_lo = new byte[1];
//                    System.out.println("Consumer ready to read a message");
                    while(input.read(msgType) == 0); // Read the message type
//                    System.out.println("Got message of type " + msgType[0]);
                    while(input.skip(1) == 0);       // Skip the pad
                    while(input.read(length_hi) == 0); // High octet of length
                    while(input.read(length_lo) == 0); // Low octet of length

                    int length = (int)length_hi[0] & 0xff;
                    length <<= 8;
                    length += length_lo[0] & 0xff;

                    byte[] msg = new byte[length];
                    int num = 0;

                    while(num != length){
						int rd = input.read(msg, num, length - num);
						if(rd == -1){
						    System.err.println("EOF reading from server, " +
			                                               "disconnecting");
			                            setState(DISCONNECTED);
						    break;
						} else {
						    num += rd;
						}
                    }
		    if((Connection.this.state & ERROR) == 0){
			handleMessage(msgType[0], msg);
		    }
                } catch (IOException ioe){
                    ioe.printStackTrace();
                    setState(DISCONNECTED);
                }
            }
	    // Notify the user?
	    // listener.disconnect();
	    System.err.println("Disconnected from server, exiting");
        }
    }
}
