/*****************************************************************************
 * $Id: MessageListener.java,v 1.1 2004/09/09 06:32:18 heb Exp $ 
 *
 * Copyright © 2002, 2003, 2004 by Henrik Bylund
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies.
 *****************************************************************************/
package communication.server;

/**
 * Listens to AI Language level messages.
 * Messages are represented as arrays of tokens, where each token
 * is represented as a <code>String</code> object corresponding to
 * the mnemonic view of the token.
 *
 * @author <a href="mailto:heb@ludd.luth.se">Henrik Bylund</a>
 * @version 1.0
 */
public interface MessageListener {
    /**
     * Receives a message, represented as an array of <code>String</code>
     * tokens.
     * Example: <code>{"YES", "(", "NME", "(", "'My AI'", ")", "(", "'v 1.0'", ")", ")"}</code>
     * which would be the expected response to the message
     * <code>"NME ('My AI') ('v 1.0')"</code> sent to the server.
     *
     * @param message the token array
     */
    public void messageReceived(String[] message);
}
