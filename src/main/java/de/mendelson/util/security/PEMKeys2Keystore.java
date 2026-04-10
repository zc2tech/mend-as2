package de.mendelson.util.security;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * This class allows to import keys that exist in PEM encoding (human readable
 * format), e.g. created by openssl, into a PKCS#12 keystore. Please remember
 * that this class does only work with the bountycastle provider as Suns JDK has
 * no idea of how to handle PKCS#12 keystores. To give you an idea of how the
 * PEM encoded key looks like, this is a part of a key -----BEGIN RSA PRIVATE
 * KEY----- Proc-Type: 4,ENCRYPTED DEK-Info: DES-EDE3-CBC,6FAA019A9B61FB51
 *
 * ky5DLG4z7r2op5W/DhPTBg34RdG0eDSKUP4nRNxtHfGYQBMDQwKSYGIu0tztnwij
 * akh3DSRi+r6oZYc7oowjxFUsubZ7JMz6SYgRiDpgN3aVt4SGqqGdFuphuvVsHNhx -----END RSA
 * PRIVATE KEY-----
 *
 * @author S.Heller
 * @version $Revision: 16 $
 */
public class PEMKeys2Keystore {

    private char[] keypass = null;

    /**
     * Keystore to use, if this is not set a new one will be created
     */
    private KeyStore keystore = null;
   
    private final String targetKeystoreType;

    /**
     * Creates a new instance of PEMUtil
     *
     * @param logger Logger to log the information to
     */
    public PEMKeys2Keystore(Logger logger, String targetKeystoreType) {
        this.targetKeystoreType = targetKeystoreType;
        //forget it to work without BC at this point, the SUN JCE provider
        //could not handle pcks12        
        // performs "Security.addProvider(new BouncyCastleProvider());" and adds some BC related system properties
        new BCCryptoHelper().initialize();
    }

    /**
     * @param pemReader Reader that accesses the RSA key in PEM format
     * @param keypassIn Passphrase for the keys stored in the PEM file
     * @param keypassOut Passphrase for the key stored in the keystore
     * @param certificateStream Stream that accesses the certificate for the
     * keys
     * @param alias Alias to use in the new keystore
     *
     */
    public void importKey(Reader pemReader, char[] keypassIn,
            char[] keypassOut,
            InputStream certificateStream, String alias) throws Exception {
        PEMParser pemParser = new PEMParser(pemReader);
        Object readObject = pemParser.readObject();

        PEMDecryptorProvider decryptorProvider = new JcePEMDecryptorProviderBuilder().build(keypassIn);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);

        PrivateKey privateKey = null;
        if (readObject instanceof PEMEncryptedKeyPair) {
            //Encrypted key - use provided password
            KeyPair keyPair = converter.getKeyPair(((PEMEncryptedKeyPair) readObject).decryptKeyPair(decryptorProvider));
            privateKey = keyPair.getPrivate();
        } else if (readObject instanceof PrivateKeyInfo) {
            //PKCS#8 format, the object will be an instanceof PrivateKeyInfo
            privateKey = converter.getPrivateKey((PrivateKeyInfo) readObject);
        } else {
            //Unencrypted key - no password needed
            KeyPair keyPair = converter.getKeyPair((PEMKeyPair) readObject);
            privateKey = keyPair.getPrivate();
        }
        X509Certificate cert = this.readCertificate(certificateStream);
        KeyStore store = this.keystore;
        if (store == null) {
            store = this.generateKeyStore();
        }
        //PKCS12 keys dont have a password, anyway take the given keystore pass as key pass for JKS
        store.setKeyEntry(alias, privateKey, keypassOut, new X509Certificate[]{cert});
        this.keypass = keypassOut;
    }

    /**
     * @param pemKeyFile File that contains the RSA key in PEM format
     * @param keypassIn Passphrase for the key stored in the PEM file
     * @param keypassOut Passphrase for the key stored in the keystore
     * @param certificateFile File that hold the certificate for the keys
     * @param alias Alias to use in the new keystore
     *
     */
    public void importKey(Path pemKeyFile, char[] keypassIn,
            char[] keypassOut,
            Path certificateFile, String alias) throws Exception {
        try (Reader fileReader = Files.newBufferedReader(pemKeyFile); InputStream certStream = Files.newInputStream(certificateFile)) {
            this.importKey(fileReader, keypassIn, keypassOut, certStream, alias);
        }
    }

    /**
     * @param keyDataPEM array that contains the RSA key in PEM format
     * @param keypassIn Passphrase for the keys stored in the PEM file
     * @param keypassOut Passphrase for the key stored in the keystore
     * @param certificateDataPEM array that holds the certificate for the keys
     * @param alias Alias to use in the new keystore
     *
     */
    public void importKey(byte[] keyDataPEM, char[] keypassIn, char[] keypassOut,
            byte[] certificateDataPEM, String alias) throws Exception {
        try (InputStream keyDataPEMIn = new ByteArrayInputStream(keyDataPEM)) {
            try (Reader reader = new InputStreamReader(keyDataPEMIn)) {
                try (ByteArrayInputStream certStream = new ByteArrayInputStream(certificateDataPEM)) {
                    this.importKey(reader, keypassIn, keypassOut, certStream, alias);
                }
            }
        }
    }

    /**
     * Loads ore creates a keystore to import the keys to
     */
    private KeyStore generateKeyStore() throws Exception {
        //do not remove the BC paramter, SUN cannot handle the format proper
        KeyStore newKeystore = null;
        if (this.targetKeystoreType.equals(BCCryptoHelper.KEYSTORE_PKCS12)) {
            newKeystore = KeyStore.getInstance(BCCryptoHelper.KEYSTORE_PKCS12,
                    BouncyCastleProvider.PROVIDER_NAME);
        } else if (this.targetKeystoreType.equals(BCCryptoHelper.KEYSTORE_JKS)) {
            newKeystore = KeyStore.getInstance("JKS");
        } else {
            throw new IllegalArgumentException("PEMKeys2Keystore:generateKeyStore: Unsupported keystore type " + this.targetKeystoreType);
        }
        newKeystore.load(null, null);
        return (newKeystore);
    }

    /**
     * Sets an already existing keystore to this class. Without an existing
     * keystore a new one is created
     */
    public void setTargetKeyStore(KeyStore keystore, char[] keystorePass) {
        this.keystore = keystore;
        // this.keystorePass = keystorePass;
    }

    /**
     * Saves the passed keystore
     *
     * @param keystorePass Password for the keystore
     */
    public void saveKeyStore(KeyStore keystore, char[] keystorePass, Path file) throws Exception {
        // Ensure parent directory exists
        Path parentDir = file.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        try (OutputStream out = Files.newOutputStream(file)) {
            keystore.store(out, keystorePass);
        }
    }

    /**
     * Reads the certificate from the certificate input stream and returns the
     * certificate itself
     */
    private X509Certificate readCertificate(InputStream certificateStream) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) factory.generateCertificate(certificateStream);
        return (cert);
    }

    /**
     * makes this a PasswordFinder
     */
    public char[] getPassword() {
        return this.keypass;
    }

}
