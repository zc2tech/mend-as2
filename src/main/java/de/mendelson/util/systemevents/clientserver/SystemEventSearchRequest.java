package de.mendelson.util.systemevents.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.systemevents.search.ServerSideEventFilter;
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
 * @version $Revision: 4 $
 */
public class SystemEventSearchRequest extends ClientServerMessage{

    private static final long serialVersionUID = 1L;
    private final ServerSideEventFilter filter;
    
    public SystemEventSearchRequest(ServerSideEventFilter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return ("Search for system events");
    }

    /**
     * @return the search filter
     */
    public ServerSideEventFilter getFilter() {
        return( this.filter );
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
    
}
