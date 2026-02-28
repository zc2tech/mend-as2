//$Header: /as2/de/mendelson/comm/as2/partner/clientserver/SinglePartnerDeleteRequest.java 1     31/10/24 7:31 Heller $
package de.mendelson.comm.as2.partner.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
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
 * @version $Revision: 1 $
 */
public class SinglePartnerDeleteRequest extends ClientServerMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String as2id = null;


    public SinglePartnerDeleteRequest(String as2id) {
        this.as2id = as2id;
    }

    @Override
    public String toString() {
        return ("Delete partner");
    }

    /**
     * @return the as2id
     */
    public String getAS2id() {
        return as2id;
    }

    /**
     * Prevent an overwrite of the readObject method for de-serialization
     */
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
        inStream.defaultReadObject();
    }
}
