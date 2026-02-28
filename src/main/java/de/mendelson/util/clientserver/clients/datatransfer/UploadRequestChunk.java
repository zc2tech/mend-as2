//$Header: /mendelson_business_integration/de/mendelson/util/clientserver/clients/datatransfer/UploadRequestChunk.java 5     5/03/25 17:53 He $
package de.mendelson.util.clientserver.clients.datatransfer;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
 * @version $Revision: 5 $
 */
public class UploadRequestChunk extends ClientServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private byte[] data = null;
    private String targetHash = null;

    public void setData(byte[] data){
        this.data = data;
    }

    @Override
    public String toString() {
        return ("Upload request chunk");
    }
    
    /**
     * @return the data
     */
    public InputStream getDataStream() {
        InputStream inStream = new ByteArrayInputStream(this.getDataBytes());
        return (inStream);
    }

    /**
     * @return the data
     */
    public byte[] getDataBytes() {
        return data;
    }

    /**
     * @return the targetHash
     */
    public String getTargetHash() {
        return targetHash;
    }

    /**
     * @param targetHash the targetHash to set
     */
    public void setTargetHash(String targetHash) {
        this.targetHash = targetHash;
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
    
}
