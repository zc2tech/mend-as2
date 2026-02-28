//$Header: /as4/de/mendelson/util/security/cert/clientserver/CRLVerificationResponse.java 3     29/02/24 10:22 Heller $
package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.security.crl.CRLRevocationInformation;
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
 * @version $Revision: 3 $
 */
public class CRLVerificationResponse extends ClientServerResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private final List<CRLRevocationInformation> informationList = new ArrayList<CRLRevocationInformation>();
    //indicates that the server will not output anything during the check - the client will handle it
    private boolean displayOnClientside = false;

    public CRLVerificationResponse(CRLVerificationRequest request) {
        super(request);
    }

    public void add(CRLRevocationInformation information) {
        this.getInformationList().add(information);
    }

    @Override
    public String toString() {
        return ("Response to a CRL verification process");
    }

    /**
     * Prevent an overwrite of the readObject method for de-serialization
     */
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
        inStream.defaultReadObject();
    }

    /**
     * @return the displayOnClientside
     */
    public boolean isDisplayOnClientside() {
        return displayOnClientside;
    }

    /**
     * @param displayOnClientside the displayOnClientside to set
     */
    public void setDisplayOnClientside(boolean displayOnClientside) {
        this.displayOnClientside = displayOnClientside;
    }

    /**
     * @return the informationList
     */
    public List<CRLRevocationInformation> getInformationList() {
        return informationList;
    }

}
