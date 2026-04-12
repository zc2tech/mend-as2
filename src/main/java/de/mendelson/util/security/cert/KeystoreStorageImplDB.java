package de.mendelson.util.security.cert;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.keydata.KeydataAccessDB;
import de.mendelson.util.security.keydata.KeystoreData;
import de.mendelson.util.systemevents.SystemEventManager;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Keystore storage implementation that relies on a database storage
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class KeystoreStorageImplDB implements KeystoreStorage {

    public static final int KEYSTORE_USAGE_TLS = 1;
    public static final int KEYSTORE_USAGE_ENC_SIGN = 2;
    public static final String KEYSTORE_STORAGE_TYPE_JKS = BCCryptoHelper.KEYSTORE_JKS;
    public static final String KEYSTORE_STORAGE_TYPE_PKCS12 = BCCryptoHelper.KEYSTORE_PKCS12;

    /**
     * Access to the keystore has to be synchonized - but the keystore is
     * changed..
     */
    private final Object keystoreLock = new Object();

    private KeyStore keystore = null;
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleKeystoreStorage.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    private int keystoreUsage = KEYSTORE_USAGE_ENC_SIGN;
    private final String keystoreStorageType;
    private final SystemEventManager systemEventManager;
    private final IDBDriverManager dbDriverManager;
    private final int userId;  // User ID for user-scoped keystores (0 = admin/system)

    /**
     * @param userId User ID for user-scoped keystores (0 = admin/system, >0 = specific user)
     */
    public KeystoreStorageImplDB(SystemEventManager systemEventManager,
            IDBDriverManager dbDriverManager, final int KEYSTORE_USAGE,
            final String KEYSTORE_STORAGE_TYPE, final int userId) throws Exception {
        this.systemEventManager = systemEventManager;
        this.dbDriverManager = dbDriverManager;
        this.userId = userId;
        KeydataAccessDB dataAccessDB = new KeydataAccessDB(dbDriverManager, systemEventManager);
        KeystoreData keystoreData = dataAccessDB.getKeydata(KEYSTORE_USAGE, userId);
        if (keystoreData == null) {
            throw new Exception("Unable to access keystore data (DB, usage="
                    + this.getKeystoreUsage()
                    + ", storageType=" + this.getKeystoreStorageType()
                    + ", userId=" + userId + ")");
        }
        this.keystoreUsage = KEYSTORE_USAGE;
        this.keystoreStorageType = KEYSTORE_STORAGE_TYPE;
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        this.keystore = cryptoHelper.createKeyStoreInstance(keystoreData.getStorageTypeAsStr(),
                keystoreData.getSecurityProvider());
        KeyStoreUtil.loadKeyStore(this.keystore, keystoreData.getData(), this.getKeystorePass());
    }

    @Override
    public void save() throws Exception {
        synchronized (this.keystoreLock) {
            if (this.keystore == null) {
                //internal error, should not happen
                throw new Exception(rb.getResourceString("error.save.notloaded"));
            }
            byte[] keyData;
            try (ByteArrayOutputStream memOut = new ByteArrayOutputStream()) {
                KeyStoreUtil.saveKeyStore(this.keystore, this.getKeystorePass(), memOut);
                keyData = memOut.toByteArray();
            }
            KeydataAccessDB dataAccessDB = new KeydataAccessDB(this.dbDriverManager, this.systemEventManager);
            dataAccessDB.updateKeydata(keyData, this.keystoreStorageType, this.keystoreUsage,
                    this.keystore.getProvider().getName(), this.userId);
        }
    }

    @Override
    public void loadKeystoreFromServer() throws Exception {
        throw new IllegalAccessException("KeystoreStorageImplDB: loadKeystoreFromServer() is not available for this implementation of storage.");
    }

    @Override
    public void replaceAllEntriesAndSave(List<KeystoreCertificate> oldList, List<KeystoreCertificate> newList) throws Exception {
        synchronized (this.keystoreLock) {
            if (this.keystore == null) {
                //internal error, should not happen
                throw new Exception(rb.getResourceString("error.save.notloaded"));
            }
            Enumeration<String> enumeration = this.keystore.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                this.keystore.deleteEntry(alias);
            }
            //ensure that there are not two same aliases in the new list
            List<String> newAliasList = new ArrayList<String>();
            long uniqueCounter = System.currentTimeMillis();
            for( KeystoreCertificate certificate : newList){
                String newAlias = certificate.getAlias();
                if( newAliasList.contains(newAlias)){
                    //this alias already exists - add a counter to make it unique
                    certificate.setAlias(certificate.getAlias() + "_" + uniqueCounter);
                    uniqueCounter++;
                }
                newAliasList.add(certificate.getAlias());
            }
            //ensure that there are not two same entries with the same fingerprint to import - just take the first one
            List<KeystoreCertificate> newUniqueListToImport = new ArrayList<KeystoreCertificate>();
            List<String> newFingerprintList = new ArrayList<String>();
            for( KeystoreCertificate certificate : newList){
                String fingerprint = certificate.getFingerPrintSHA1();
                if( !newFingerprintList.contains(fingerprint)){
                    newFingerprintList.add(fingerprint);
                    newUniqueListToImport.add( certificate );
                }
            }            
            for (KeystoreCertificate certificate : newUniqueListToImport) {
                if (certificate.getIsKeyPair()) {
                    char[] keyPass = null;
                    if (this.getKeystoreStorageType().equals(KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS)) {
                        keyPass = this.getKeystorePass();
                    }
                    this.keystore.setKeyEntry(certificate.getAlias(), certificate.getPrivateKey(), keyPass,
                            certificate.getCertificateChain());
                } else {
                    this.keystore.setCertificateEntry(certificate.getAlias(), certificate.getX509Certificate());
                }
            }
            this.save();
        }
    }

    @Override
    public Key getKey(String alias) throws Exception {
        synchronized (this.keystoreLock) {
            Key key = this.keystore.getKey(alias, this.getKeystorePass());
            return (key);
        }
    }

    @Override
    public Certificate[] getCertificateChain(String alias) throws Exception {
        synchronized (this.keystoreLock) {
            Certificate[] chain = this.keystore.getCertificateChain(alias);
            return (chain);
        }
    }

    @Override
    public X509Certificate getCertificate(String alias) throws Exception {
        synchronized (this.keystoreLock) {
            X509Certificate certificate = (X509Certificate) this.keystore.getCertificate(alias);
            return (certificate);
        }
    }

    /**
     * This renames an entry in the underlaying storage and saves it
     *
     * @param oldAlias
     * @param newAlias
     * @param keypairPass
     * @throws Exception
     */
    @Override
    public void renameEntry(String oldAlias, String newAlias,
            char[] keypairPass) throws Exception {
        synchronized (this.keystoreLock) {
            KeyStoreUtil.renameEntry(this.keystore, oldAlias, newAlias, keypairPass);
        }
    }

    @Override
    public KeyStore getKeystore() {
        synchronized (this.keystoreLock) {
            return (this.keystore);
        }
    }

    @Override
    public char[] getKeystorePass() {
        return ("test".toCharArray());
    }

    private void deleteEntryAndSave(String alias) throws Exception {
        synchronized (this.keystoreLock) {
            if (this.keystore == null) {
                //internal error, should not happen
                throw new Exception(rb.getResourceString("error.delete.notloaded"));
            }
            this.keystore.deleteEntry(alias);
            this.save();
        }
    }

    @Override
    public void deleteEntry(String alias) throws Exception {
        //because this is the direct file implementation a save is required as a reload from the keystore 
        //file might follow.
        //This is a special behavior for the file based certificate manager.
        this.deleteEntryAndSave(alias);
    }

    @Override
    public Map<String, Certificate> loadCertificatesFromKeystore() throws Exception {
        KeydataAccessDB dataAccessDB = new KeydataAccessDB(dbDriverManager, systemEventManager);
        KeystoreData keystoreData = dataAccessDB.getKeydata(this.getKeystoreUsage(), this.userId);
        if (keystoreData == null) {
            throw new Exception("Unable to access keystore data (DB, usage="
                    + this.getKeystoreUsage()
                    + ", storageType=" + this.getKeystoreStorageType() + ")");
        }
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        KeyStore tempKeystore = cryptoHelper.createKeyStoreInstance(keystoreData.getStorageTypeAsStr(),
                keystoreData.getSecurityProvider());
        KeyStoreUtil.loadKeyStore(tempKeystore, keystoreData.getData(), this.getKeystorePass());
        Map<String, Certificate> certificateMap = KeyStoreUtil.getCertificatesFromKeystore(tempKeystore);
        synchronized (this.keystoreLock) {
            this.keystore = tempKeystore;
        }
        return (certificateMap);
    }

    @Override
    public boolean isKeyEntry(String alias) throws Exception {
        synchronized (this.keystoreLock) {
            return (this.keystore.isKeyEntry(alias));
        }
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
