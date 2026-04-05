package de.mendelson.util.security.cert;

import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.KeyStoreUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Keystore storage implementation that relies on a byte array
 *
 * @author S.Heller
 * @version $Revision: 15 $
 */
public class KeystoreStorageImplByteArray implements KeystoreStorage {

    public static final int KEYSTORE_USAGE_SSL = KeystoreStorageImplFile.KEYSTORE_USAGE_TLS;
    public static final int KEYSTORE_USAGE_ENC_SIGN = KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN;
    public static final String KEYSTORE_STORAGE_TYPE_JKS = BCCryptoHelper.KEYSTORE_JKS;
    public static final String KEYSTORE_STORAGE_TYPE_PKCS12 = BCCryptoHelper.KEYSTORE_PKCS12;

    private KeyStore keystore = null;
    private char[] keystorePass = null;
    private int keystoreUsage = KEYSTORE_USAGE_ENC_SIGN;
    private String keystoreStorageType = KEYSTORE_STORAGE_TYPE_PKCS12;

    /**
     * @param keystorePass
     */
    public KeystoreStorageImplByteArray(byte[] keystoreBytes, char[] keystorePass, final int KEYSTORE_USAGE,
            final String KEYSTORE_STORAGE_TYPE) throws Exception {
        this.keystorePass = keystorePass;
        this.keystoreUsage = KEYSTORE_USAGE;
        this.keystoreStorageType = KEYSTORE_STORAGE_TYPE;
        if (keystoreBytes == null || keystoreBytes.length == 0) {
            throw new IllegalArgumentException("KeystoreStorageImplByteArray: passed keystore must not have the length 0");
        }
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        this.keystore = cryptoHelper.createKeyStoreInstance(keystoreStorageType);
        //load data into keystore object
        try (InputStream inStream = new ByteArrayInputStream(keystoreBytes)) {
            this.keystore.load(inStream, this.keystorePass);
        }
    }

    @Override
    public void save() throws Exception {
        throw new IllegalAccessException("KeystoreStorageImplByteArray: save() is not available for byte array implementation of storage.");
    }

    @Override
    public void loadKeystoreFromServer() throws Exception {
        throw new IllegalAccessException("KeystoreStorageImplByteArray: loadKeystoreFromServer() is not available for byte array implementation of storage.");
    }

    @Override
    public void replaceAllEntriesAndSave(List<KeystoreCertificate> oldList, List<KeystoreCertificate> newList) throws Exception {
        throw new IllegalAccessException("KeystoreStorageImplByteArray: replaceAllEntriesAndSave() is not available for byte array implementation of storage.");
    }

    @Override
    public Key getKey(String alias) throws Exception {
        Key key = this.keystore.getKey(alias, this.keystorePass);
        return (key);
    }

    @Override
    public Certificate[] getCertificateChain(String alias) throws Exception {
        Certificate[] chain = this.keystore.getCertificateChain(alias);
        return (chain);
    }

    @Override
    public X509Certificate getCertificate(String alias) throws Exception {
        return ((X509Certificate) this.keystore.getCertificate(alias));
    }

    @Override
    public void renameEntry(String oldAlias, String newAlias, char[] keypairPass) throws Exception {
        KeyStoreUtil.renameEntry(this.keystore, oldAlias, newAlias, keypairPass);
    }

    @Override
    public KeyStore getKeystore() {
        return (this.keystore);
    }

    @Override
    public char[] getKeystorePass() {
        return (this.keystorePass);
    }

    @Override
    public void deleteEntry(String alias) throws Exception {
        if (this.keystore == null) {
            //internal error, should not happen
            throw new Exception("CertificateManager.deleteKeystoreEntry: Unable to delete entry, keystore is not loaded.");
        }
        this.keystore.deleteEntry(alias);
    }

    @Override
    public Map<String, Certificate> loadCertificatesFromKeystore() throws Exception {
        Map<String, Certificate> certificateMap = KeyStoreUtil.getCertificatesFromKeystore(this.keystore);
        return (certificateMap);
    }

    @Override
    public boolean isKeyEntry(String alias) throws Exception {
        return (this.keystore.isKeyEntry(alias));
    }

    @Override
    public String getKeystoreStorageType() {
        return (this.keystoreStorageType);
    }

    @Override
    public int getKeystoreUsage() {
        return (this.keystoreUsage);
    }

    @Override
    public boolean isReadOnly() {
        return (false);
    }

}
