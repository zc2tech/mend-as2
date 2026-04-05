package de.mendelson.comm.as2.clientserver.message;

import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class DeleteMessageRequest extends ClientServerMessage implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private final List<AS2MessageInfo> deleteList = new ArrayList<AS2MessageInfo>();

    @Override
    public String toString(){
        return( "Delete messages" );
    }

    /**
     * @return the deleteList
     */
    public List<AS2MessageInfo> getDeleteList() {
        return deleteList;
    }

    /**
     */
    public void setDeleteList(List<AS2MessageInfo> newDeleteList) {
        this.deleteList.clear();
        this.deleteList.addAll( newDeleteList );
    }
    
    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
    
}
