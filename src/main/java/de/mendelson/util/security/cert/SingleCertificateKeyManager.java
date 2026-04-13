package de.mendelson.util.security.cert;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509KeyManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Custom X509KeyManager that restricts certificate selection to a single alias.
 * Used for HTTP client certificate authentication when a specific certificate
 * must be presented to the server during TLS handshake.
 *
 * This wraps an existing KeyManager and filters certificate selection to return
 * only the specified alias, enabling precise control over which client certificate
 * is presented during mutual TLS authentication.
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class SingleCertificateKeyManager implements X509KeyManager {

    private final X509KeyManager delegate;
    private final String alias;

    /**
     * Constructor
     * @param delegate The underlying KeyManager that has access to all certificates
     * @param alias The specific alias to use for client certificate authentication
     */
    public SingleCertificateKeyManager(X509KeyManager delegate, String alias) {
        this.delegate = delegate;
        this.alias = alias;
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        // Only return our specific alias if it matches the key type
        String[] allAliases = delegate.getClientAliases(keyType, issuers);
        if (allAliases == null) {
            return null;
        }
        for (String a : allAliases) {
            if (a.equals(alias)) {
                return new String[]{alias};
            }
        }
        return null;
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        // Always choose our specific alias
        return alias;
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return delegate.getServerAliases(keyType, issuers);
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return delegate.chooseServerAlias(keyType, issuers, socket);
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return delegate.getCertificateChain(alias);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return delegate.getPrivateKey(alias);
    }
}
