package de.mendelson.comm.as2.statistic.clientserver;

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
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class QuotaResetRequest extends ClientServerMessage{
    
    private static final long serialVersionUID = 1L;
    private final String localStationId;
    private final String partnerId;
    
    public QuotaResetRequest(String localStationId, String partnerId) {
        this.localStationId = localStationId;
        this.partnerId = partnerId;                
    }

    @Override
    public String toString() {
        return ("Reset quota");
    }

    /**
     * @return the localStationId
     */
    public String getLocalStationId() {
        return localStationId;
    }

    /**
     * @return the partnerId
     */
    public String getPartnerId() {
        return partnerId;
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
}
