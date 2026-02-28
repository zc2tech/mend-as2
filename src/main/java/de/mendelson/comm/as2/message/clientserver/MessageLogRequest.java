//$Header: /as2/de/mendelson/comm/as2/message/clientserver/MessageLogRequest.java 4     2/11/23 15:52 Heller $
package de.mendelson.comm.as2.message.clientserver;

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
 * @version $Revision: 4 $
 */
public class MessageLogRequest extends ClientServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String messageId = null;

    public MessageLogRequest(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return ("Message log request");
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Prevent an overwrite of the readObject method for de-serialization
     */
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
        inStream.defaultReadObject();
    }

}
