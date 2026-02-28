//$Header: /as2/de/mendelson/comm/as2/message/clientserver/MessageDetailResponse.java 3     2/11/23 15:52 Heller $
package de.mendelson.comm.as2.message.clientserver;

import de.mendelson.comm.as2.message.AS2Info;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.Serializable;
import java.util.List;
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
public class MessageDetailResponse extends ClientServerResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<AS2Info> list = null;

    public MessageDetailResponse(MessageDetailRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Message detail response");
    }

    /**
     * @return the list
     */
    public List<AS2Info> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<AS2Info> list) {
        this.list = list;
    }

    
}
