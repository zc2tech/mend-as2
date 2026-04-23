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
        if (issuers != null && issuers.length > 0) {
            for (int i = 0; i < Math.min(issuers.length, 3); i++) {
            }
        }

        // Verify the certificate chain is available
        X509Certificate[] chain = getCertificateChain(alias);
        if (chain != null) {
        }

        // Verify the private key is available
        PrivateKey privateKey = getPrivateKey(alias);
        if (privateKey != null) {
        }

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
        X509Certificate[] chain = delegate.getCertificateChain(alias);
        if (chain != null && chain.length > 0) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
                byte[] der = chain[0].getEncoded();
                md.update(der);
                byte[] digest = md.digest();
                StringBuilder hexString = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    String hex = Integer.toHexString(0xFF & digest[i]);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex.toUpperCase());
                    if (i < digest.length - 1) {
                        hexString.append(':');
                    }
                }
            } catch (Exception e) {
            }
        }
        return chain;
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        PrivateKey key = delegate.getPrivateKey(alias);
        return key;
    }
}
