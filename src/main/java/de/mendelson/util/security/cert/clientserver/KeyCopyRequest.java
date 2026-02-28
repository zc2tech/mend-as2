//$Header: /as4/de/mendelson/util/security/cert/clientserver/KeyCopyRequest.java 4     9/11/23 9:52 Heller $
package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.security.cert.KeystoreStorageImplFile;
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
 * @version $Revision: 4 $
 */
public class KeyCopyRequest extends ClientServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int KEYSTORE_USAGE_TLS = KeystoreStorageImplFile.KEYSTORE_USAGE_TLS;
    public static final int KEYSTORE_USAGE_ENC_SIGN = KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN;
    private final int keystoreUsageSource;
    private final int keystoreUsageTarget;
    private final String fingerprintSHA1;

    public KeyCopyRequest(
            final int KEYSTORE_USAGE_SOURCE,
            final int KEYSTORE_USAGE_TARGET,
            String fingerprintSHA1) {
        this.keystoreUsageSource = KEYSTORE_USAGE_SOURCE;
        this.keystoreUsageTarget = KEYSTORE_USAGE_TARGET;
        this.fingerprintSHA1 = fingerprintSHA1;
    }

    @Override
    public String toString() {
        return ("Key copy request");
    }

    /**
     * Prevent an overwrite of the readObject method for de-serialization
     */
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
        inStream.defaultReadObject();
    }

    /**
     * @return the keystoreType, one of ExportRequestPrivateKeyPKCS12.KEYSTORE_USAGE_TLS 
     * or ExportRequestPrivateKeyPKCS12.KEYSTORE_USAGE_ENC_SIGN
     */
    public int getKeystoreUsageSource() {
        return keystoreUsageSource;
    }

    /**
     * @return the keystoreType, one of ExportRequestPrivateKeyPKCS12.KEYSTORE_USAGE_TLS 
     * or ExportRequestPrivateKeyPKCS12.KEYSTORE_USAGE_ENC_SIGN
     */
    public int getKeystoreUsageTarget() {
        return keystoreUsageTarget;
    }
    
    /**
     * @return the fingerprintSHA1
     */
    public String getFingerprintSHA1() {
        return fingerprintSHA1;
    }
}
