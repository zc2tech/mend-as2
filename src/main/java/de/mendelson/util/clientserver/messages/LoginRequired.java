//$Header: /as2/de/mendelson/util/clientserver/messages/LoginRequired.java 5     2/11/23 15:53 Heller $
package de.mendelson.util.clientserver.messages;

import de.mendelson.util.clientserver.user.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class LoginRequired extends ClientServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private User user;

    @Override
    public String toString() {
        return ("Login required.");
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
}
