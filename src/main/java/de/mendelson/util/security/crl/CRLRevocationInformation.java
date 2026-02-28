//$Header: /as4/de/mendelson/util/security/crl/CRLRevocationInformation.java 1     29/02/24 10:06 Heller $
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
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class CRLRevocationInformation implements Serializable {

    private static final long serialVersionUID = 1L;

    private final CRLRevocationState state;
    private final String fingerprintSHA1;
    private final String logLine;

    public CRLRevocationInformation(CRLRevocationState state, String fingerprintSHA1,
            String logLine) {
        this.state = state;
        this.fingerprintSHA1 = fingerprintSHA1;
        this.logLine = logLine;
    }

    /**
     * @return the state
     */
    public CRLRevocationState getRevocationState() {
        return this.state;
    }

    /**
     * @return the fingerprintSHA1
     */
    public String getFingerprintSHA1() {
        return this.fingerprintSHA1;
    }

    /**
     * @return the logLine
     */
    public String getLogLine() {
        return logLine;
    }

}
