package de.mendelson.comm.as2.clientserver.message;

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
 * @version $Revision: 5 $
 */
public class ConfigurationCheckRequest extends ClientServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean performClientRelatedTests = false;
    
    public ConfigurationCheckRequest() {
    }

    @Override
    public String toString() {
        return ("Check server configuration");
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
    
    /**
     * @return the performClientRelatedTests
     */
    public boolean getPerformClientRelatedTests() {
        return performClientRelatedTests;
    }

    /**
     * @param performClientRelatedTests the performClientRelatedTests to set
     */
    public void setPerformClientRelatedTests(boolean performClientRelatedTests) {
        this.performClientRelatedTests = performClientRelatedTests;
    }
    
}
