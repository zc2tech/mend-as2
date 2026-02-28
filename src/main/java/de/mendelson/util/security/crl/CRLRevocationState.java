//$Header: /as4/de/mendelson/util/security/crl/CRLRevocationState.java 1     29/02/24 10:06 Heller $
package de.mendelson.util.security.crl;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores information regarding a single revocation request
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class CRLRevocationState implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    public static final int STATE_OK = 0;
    public static final int STATE_REVOKED = 1;    
    public static final int STATE_NO_CRL_INFORMATION_IN_CERTIFICATE = 2;
    public static final int STATE_UNABLE_TO_RETRIEVE_CRL_URL_FROM_CERTIFICATE = 3;
    public static final int STATE_CRL_NOT_REACHABLE = 4;
    public static final int STATE_MALFORMED_CRL_URL = 5;
    public static final int STATE_CERTIFICATE_NOT_READABLE = 6;
    public static final int STATE_CRL_DOWNLOAD_FAILED = 7;
    public static final int STATE_OTHER_PROBLEM = 8;
    public static final int STATE_HTTPS_NOT_SUPPORTED_IN_URL = 9;
    public static final int STATE_CRL_IN_BAD_FORMAT = 10;

   
    private final int state;
    private final String details;
  
    
    public CRLRevocationState( int state, String details){
        this.state = state;
        this.details = details;
    }

    /**
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * @return the details
     */
    public String getDetails() {
        return details;
    }
    
    
}
