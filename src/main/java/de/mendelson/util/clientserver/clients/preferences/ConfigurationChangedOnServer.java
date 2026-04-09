package de.mendelson.util.clientserver.clients.preferences;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
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
 * Msg for the client server protocol
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ConfigurationChangedOnServer extends  ClientServerMessage{

    private static final long serialVersionUID = 1L;

    public static final int TYPE_SERVER_PREFERENCES = 1;
    public static final int TYPE_NOTIFICATION_SETTINGS = 2;
    public static final int TYPE_TLS_LISTENER_SETTINGS = 3;
    
    private int type = TYPE_SERVER_PREFERENCES;
   
    /**
     * 
     * @param TYPE One of the static class constants TYPE_SERVER_PREFERENCES, TYPE_NOTIFICATION_SETTINGS
     */
    protected ConfigurationChangedOnServer( final int TYPE ){
        this.type = TYPE;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }
    
    @Override
    public String toString(){
        return( "Configuration changed on server" );
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
    
}
