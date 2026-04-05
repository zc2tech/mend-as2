package de.mendelson.util.clientserver;

import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.BouncyCastleJsseProviderSingleton;
import de.mendelson.util.security.BouncyCastleProviderSingleton;
import de.mendelson.util.security.keygeneration.KeyGenerationResult;
import de.mendelson.util.security.keygeneration.KeyGenerationValues;
import de.mendelson.util.security.keygeneration.KeyGenerator;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Locale;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Full Bonuncycastle implementation of the TLS layer of the client-server connection
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ClientServerTLSImplBC implements ClientServerTLS{

    private final String PRODUCT_NAME;
    
    public ClientServerTLSImplBC( String productName ){
        PRODUCT_NAME = productName;
    }
    
     /**
     * Instanciate a SSL/TLS context. This creates an SSL key on the server and uses
     * it for the SSL secured client-server communication. The TLS
     * between client and server only delivers weak security as the client
     * trusts any key from the server (client and server certificates are not
     * exchanged using an other communication channel, there is no additional
     * shared secret between client and server) - Please be aware that this TSL
     * implementation is not safe against a man in the middle attack. Anyway a
     * man in the middle attack is not an easy attempt in this case.
     */
    @Override
    public SSLContext createSSLContext() throws Exception {
        BCCryptoHelper helper = new BCCryptoHelper();
        SSLContext sslContext = SSLContext.getInstance(ClientServer.SERVERSIDE_ACCEPTED_TLS_PROTOCOLS[0],
                BouncyCastleJsseProviderSingleton.instance());
        KeyManagerFactory keyManagerFactory 
                = KeyManagerFactory.getInstance(
                        "PKIX",
                        BouncyCastleJsseProviderSingleton.instance());
        TrustManagerFactory trustManagerFactory = 
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm(), 
                        BouncyCastleJsseProviderSingleton.instance());
        KeyStore keystore = helper.createKeyStoreInstance(BCCryptoHelper.KEYSTORE_PKCS12,
                BouncyCastleProviderSingleton.instance());
        //initialize keystore
        keystore.load(null, "".toCharArray());
        KeyGenerationResult result = this.generateTLSKey();
        keystore.setKeyEntry("key", result.getKeyPair().getPrivate(), "".toCharArray(), new Certificate[]{result.getCertificate()});
        keyManagerFactory.init(keystore, "".toCharArray());
        trustManagerFactory.init(keystore);
        sslContext.init(keyManagerFactory.getKeyManagers(), 
                trustManagerFactory.getTrustManagers(), 
                SecureRandom.getInstance("DEFAULT", BouncyCastleProviderSingleton.instance()));               
        return sslContext;
    }

    /**
     * Generates the SSL key for the client-server connection
     */
    private KeyGenerationResult generateTLSKey() throws Exception {
        KeyGenerator generator = new KeyGenerator();
        KeyGenerationValues parameter = new KeyGenerationValues();
        //generating a longer key takes some time.
        parameter.setKeySize(2048);
        parameter.setKeyAlgorithm(KeyGenerationValues.KEYALGORITHM_RSA);
        //one shutdown every 10 years should be ok
        parameter.setKeyValidInDays(365 * 10);
        parameter.setSignatureAlgorithm(KeyGenerationValues.SIGNATUREALGORITHM_SHA256_WITH_RSA);
        parameter.setOrganisationName(this.getProductName());
        parameter.setOrganisationUnit("Server");
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            parameter.setCommonName(hostName);
        } catch (Throwable e) {
            //ignore, no entry found in hosts file
        }
        parameter.setEmailAddress("nomail@nomail.to");
        parameter.setLocalityName(Locale.getDefault().getDisplayLanguage());
        parameter.setCountryCode(Locale.getDefault().getCountry());
        parameter.setStateName(Locale.getDefault().getDisplayCountry());
        //add SSL extended key usage
        KeyPurposeId[] extKeyUsage = new KeyPurposeId[2];
        extKeyUsage[0] = KeyPurposeId.id_kp_serverAuth;
        extKeyUsage[1] = KeyPurposeId.id_kp_clientAuth;
        parameter.setExtendedKeyExtension(new ExtendedKeyUsage(extKeyUsage));
        return (generator.generateKeyPair(parameter));
    }

    @Override
    public String getProductName() {
        return( PRODUCT_NAME );
    }
    
}
