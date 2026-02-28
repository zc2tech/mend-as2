//$Header: /as2/de/mendelson/util/security/cert/clientserver/KeyCopyResponse.java 2     2/11/23 15:53 Heller $
package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
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
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class KeyCopyResponse extends ClientServerResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String usedTargetAlias = null;
    
    public KeyCopyResponse(KeyCopyRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Key copy from one keystore to another");
    }
    
    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }

    /**
     * @return the usedTargetAlias
     */
    public String getUsedTargetAlias() {
        return usedTargetAlias;
    }

    /**
     * @param usedTargetAlias the usedTargetAlias to set
     */
    public void setUsedTargetAlias(String usedTargetAlias) {
        this.usedTargetAlias = usedTargetAlias;
    }
    
}
