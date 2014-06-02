/*****************************************************************************
 * $Id: UnknownTokenException.java,v 1.1 2004/09/09 06:32:18 heb Exp $ 
 *
 * Copyright © 2002, 2003, 2004 by Henrik Bylund
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies.
 *****************************************************************************/
package communication.server;

/**
 * @author <a href="mailto:heb@ludd.luth.se">Henrik Bylund</a>
 * @version 1.0
 */
public class UnknownTokenException extends Exception {
    Object token;

    /**
     * Creates the exception.
     *
     * @param token The offending token encountered in the message.
     */
    public UnknownTokenException(Object token){
	this.token = token;
    }

    /**
     * Gets the token representation that could not be converted.
     * It will be either a <code>String</code> or a <code>Short</code>,
     * depending on what conversion call was made.
     *
     * @return The offending token. Either a <code>Short</code> or a
     *         <code>String</code> object.
     */
    public Object getToken(){
	return token;
    }
}
