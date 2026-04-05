package de.mendelson.util.security;

import java.security.Provider;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Storage for security provider
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class CryptoProvider {

    private ProviderContainer providerTLSKeystore = null;
    private ProviderContainer providerEncSign = null;
    private ProviderContainer providerKeyManagerFactory = null;
    private ProviderContainer providerTrustManagerFactory = null;
    private ProviderContainer providerSSLContext = null;
    
    public CryptoProvider() {
        providerTLSKeystore = new ProviderContainer(BouncyCastleProviderSingleton.instance(), "".toCharArray(),
                BouncyCastleProviderSingleton.instance().getName());
        providerEncSign = new ProviderContainer(BouncyCastleProviderSingleton.instance(), "".toCharArray(),
                BouncyCastleProviderSingleton.instance().getName());
    }

    public ProviderContainer getProviderTLSKeystore() {
        return (this.providerTLSKeystore);
    }

    public ProviderContainer getProviderEncSign() {
        return (this.providerEncSign);
    }

    public ProviderContainer getProviderKeyManagerFactory() {
        return (this.providerKeyManagerFactory);
    }
    
    public ProviderContainer getProviderTrustManagerFactory() {
        return (this.providerTrustManagerFactory);
    }
    
    public ProviderContainer getProviderSSLContext() {
        return (this.providerSSLContext);
    }
    
    public void setProviderTLSKeystore(ProviderContainer providerTLSKeyStore) {
        this.providerTLSKeystore = providerTLSKeyStore;
    }

    public void setProviderEncSign(ProviderContainer providerEncSign) {
        this.providerEncSign = providerEncSign;
    }
    
    public void setProviderKeyManagerFactory(ProviderContainer providerKeyManagerFactory) {
        this.providerKeyManagerFactory = providerKeyManagerFactory;
    }
    
    public void setProviderTrustManagerFactory(ProviderContainer providerTrustManagerFactory) {
        this.providerTrustManagerFactory = providerTrustManagerFactory;
    }
    
    public void setProviderSSLContext(ProviderContainer providerSSLContext) {
        this.providerSSLContext = providerSSLContext;
    }
        
    public static class ProviderContainer {

        private final Provider provider;
        private final char[] userPin;
        private final String hsmName;

        public ProviderContainer(Provider provider, char[] userPin, String hsmName) {
            this.provider = provider;
            this.userPin = userPin;
            this.hsmName = hsmName;
        }

        /**
         * @return the provider
         */
        public Provider getProvider() {
            return this.provider;
        }

        /**
         * @return the userPin
         */
        public char[] getUserPin() {
            return this.userPin;
        }

        /**
         * @return the name
         */
        public String getHSMName() {
            return hsmName;
        }

    }

}
