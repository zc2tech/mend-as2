package de.mendelson.comm.as2.partner.clientserver;

import de.mendelson.comm.as2.partner.Partner;
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
 *
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class PartnerModificationRequest extends ClientServerMessage{

    private static final long serialVersionUID = 1L;
    private final List<Partner> data = new ArrayList<Partner>();

    public PartnerModificationRequest() {
    }

    @Override
    public String toString() {
        return ("Modify partner");
    }
   
    /**
     * @return the data
     */
    public List<Partner> getData() {
        return data;
    }

    /**
     */
    public void setData(List<Partner> newData) {
        this.data.clear();
        this.data.addAll(newData);
    }
    
    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
}
