/*****************************************************************************
 * $Id: DiplomaticMessageListener.java,v 1.1 2004/09/09 06:32:18 heb Exp $ 
 *
 * Copyright © 2002, 2003, 2004 by Henrik Bylund
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies.
 *****************************************************************************/
package communication.server;

/**
 * Listens to Diplomatic Messages.
 *
 * @author <a href="mailto:heb@ludd.luth.se">Henrik Bylund</a>
 * @version 1.0
 */
public interface DiplomaticMessageListener {
    /**
     * Receives a Diplomatic Message, in its string mnemonic
     * representation.
     */
    public void diplomaticMessageReceived(String[] message);
}
