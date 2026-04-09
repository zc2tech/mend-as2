//$Header: /as2/de/mendelson/util/clientserver/messages/ServerSideNotification.java 5     2/11/23 15:53 Heller $
package de.mendelson.util.clientserver.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * This marks a client server message as a notification from the server. It should be sent to all client modules and requires no
 * processing notification
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ServerSideNotification extends ClientServerMessage {

    private static final long serialVersionUID = 1L;
    public ServerSideNotification(){
        super();
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
    
}
