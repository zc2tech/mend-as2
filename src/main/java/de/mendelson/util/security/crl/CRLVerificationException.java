package de.mendelson.util.security.crl;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Exception that is throw during the process of a CRL verification
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class CRLVerificationException extends Exception {

    private final CRLRevocationState revocationState;

    public CRLVerificationException(CRLRevocationState revocationState, Throwable cause) {
        super(revocationState.getDetails(), cause);
        this.revocationState = revocationState;
    }

    public CRLVerificationException(CRLRevocationState revocationState) {
        super(revocationState.getDetails());
        this.revocationState = revocationState;
    }
    
    /**
     * @return the revocationState
     */
    public CRLRevocationState getRevocationState() {
        return revocationState;
    }

}
