package de.mendelson.util.clientserver.clients.preferences;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
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
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class PreferencesResponse extends ClientServerResponse{

    private static final long serialVersionUID = 1L;
    private String value = null;

    public PreferencesResponse( PreferencesRequest request ){
        super( request );
    }

    @Override
    public String toString(){
        return( "Preferences response" );
    }
    
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    
}
