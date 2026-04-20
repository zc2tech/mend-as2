package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.security.cert.KeystoreStorageImplFile;
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
 * @version $Revision: 4 $
 */
public class DownloadRequestKeystore extends ClientServerMessage{

    public static final int KEYSTORE_TYPE_TLS = KeystoreStorageImplFile.KEYSTORE_USAGE_TLS;
    public static final int KEYSTORE_TYPE_ENC_SIGN = KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN;

    private static final long serialVersionUID = 1L;

    private final int keystoreUsage;
    private final int userId;  // User ID for user-specific keystores (0 = admin/system)

    public DownloadRequestKeystore(final int KEYSTORE_USAGE) {
        this(KEYSTORE_USAGE, 0);  // Default to admin/system keystore
    }

    public DownloadRequestKeystore(final int KEYSTORE_USAGE, final int userId) {
        this.keystoreUsage = KEYSTORE_USAGE;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return ("Download request keystore (usage=" + keystoreUsage + ", userId=" + userId + ")");
    }

    public int getKeystoreUsage(){
        return( this.keystoreUsage);
    }

    public int getUserId(){
        return( this.userId);
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }


}
