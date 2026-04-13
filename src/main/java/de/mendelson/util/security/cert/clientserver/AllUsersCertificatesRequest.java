package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.Serializable;

/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

/**
 * Request to get all users' certificates (admin only)
 * Returns certificates from all users with owner information
 *
 * @author Julian Xu
 */
public class AllUsersCertificatesRequest extends ClientServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int KEYSTORE_TYPE_TLS = 1;
    public static final int KEYSTORE_TYPE_ENC_SIGN = 2;

    private final int keystoreUsage;  // KEYSTORE_USAGE_ENC_SIGN or KEYSTORE_USAGE_TLS

    /**
     * @param keystoreUsage KEYSTORE_USAGE_ENC_SIGN or KEYSTORE_USAGE_TLS
     */
    public AllUsersCertificatesRequest(int keystoreUsage) {
        this.keystoreUsage = keystoreUsage;
    }

    public int getKeystoreUsage() {
        return keystoreUsage;
    }

    @Override
    public String toString() {
        return "AllUsersCertificatesRequest{keystoreUsage=" + keystoreUsage + "}";
    }
}
