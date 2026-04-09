package de.mendelson.util.mailautoconfig.clientserver;

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
 * @version $Revision: 3 $
 */
public class MailAutoConfigDetectRequest extends ClientServerMessage{
    
    private static final long serialVersionUID = 1L;
    private final String mailAddress;
    
    public MailAutoConfigDetectRequest(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    @Override
    public String toString() {
        return ("Request a mail server configuration");
    }
    
    /**
     * @return the mailAddress
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
 
}
