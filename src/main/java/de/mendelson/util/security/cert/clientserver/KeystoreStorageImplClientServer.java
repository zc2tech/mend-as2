//$Header: /as2/de/mendelson/util/security/cert/clientserver/KeystoreStorageImplClientServer.java 31    11/02/25 13:40 Heller $
package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.security.cert.KeystoreStorageImplByteArray;
import de.mendelson.util.security.cert.KeystoreStorageImplFile;
import de.mendelson.util.security.cert.ResourceBundleKeystoreStorage;
import de.mendelson.util.security.keygeneration.KeyGenerationResult;
import de.mendelson.util.security.keygeneration.KeyGenerationValues;
import de.mendelson.util.security.keygeneration.KeyGenerator;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Keystore storage implementation that relies on a client-server access
 *
 * @author S.Heller
 * @version $Revision: 31 $
 */
public class KeystoreStorageImplClientServer implements KeystoreStorage {

    public static final int KEYSTORE_USAGE_SSL = KeystoreStorageImplFile.KEYSTORE_USAGE_TLS;
    public static final int KEYSTORE_USAGE_ENC_SIGN = KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN;
    public static final String KEYSTORE_STORAGE_TYPE_JKS = BCCryptoHelper.KEYSTORE_JKS;
    public static final String KEYSTORE_STORAGE_TYPE_PKCS12 = BCCryptoHelper.KEYSTORE_PKCS12;

    private KeyStore keystore = null;
    private final BaseClient baseClient;
    private int keystoreUsage = KEYSTORE_USAGE_ENC_SIGN;
    private String keystoreStorageType = KEYSTORE_STORAGE_TYPE_PKCS12;
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleKeystoreStorage.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    private char[] password = null;
    /**
     * Stores all entries that are key entries
     */
    private final Map<String, KeystoreCertificate> downloadedKeyEntries = new ConcurrentHashMap<String, KeystoreCertificate>();
    private boolean readonly = false;

    /**
     * @param KEYSTORE_USAGE keystore type as defined in the class
     * BCCryptoHelper
     */
    public KeystoreStorageImplClientServer(BaseClient baseClient,
            final int KEYSTORE_USAGE,
            final String KEYSTORE_STORAGE_TYPE) throws Throwable {
        this.baseClient = baseClient;
        this.keystoreUsage = KEYSTORE_USAGE;
        this.keystoreStorageType = KEYSTORE_STORAGE_TYPE;
        this.loadKeystoreFromServer();
    }

    private char[] generatePassword(int length) {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] generatedPassword = new char[length];
        generatedPassword[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        generatedPassword[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        generatedPassword[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        generatedPassword[3] = numbers.charAt(random.nextInt(numbers.length()));
        for (int i = 4; i < length; i++) {
            generatedPassword[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return generatedPassword;
    }

    @Override
    public void loadKeystoreFromServer() throws Throwable {
        //request the keystore from the server
        DownloadRequestKeystore request = new DownloadRequestKeystore(this.keystoreUsage);
        DownloadResponseKeystore response = (DownloadResponseKeystore) baseClient.sendSync(request);
        if (response == null) {
            throw new Exception(rb.getResourceString("error.nodata"));
        }
        if (response.getException() != null) {
            throw response.getException();
        }
        if (response.isReadonlyOnServer()) {
            this.readonly = true;
        } else {
            this.readonly = false;
        }
        this.password = this.generatePassword(64);
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        this.keystore = cryptoHelper.createKeyStoreInstance(BCCryptoHelper.KEYSTORE_PKCS12);
        //initialize keystore in memory
        this.keystore.load(null, this.password);
        List<KeystoreCertificate> list = response.getCertificateList();
        for (KeystoreCertificate downloadedKeyEntry : list) {
            if (downloadedKeyEntry.getIsKeyPair()) {
                KeyGenerationResult dummyResult = this.generateDummyKey();
                this.keystore.setKeyEntry(downloadedKeyEntry.getAlias(),
                        dummyResult.getKeyPair().getPrivate(), null,
                        new Certificate[]{dummyResult.getCertificate()});
                KeystoreCertificate dummyEntry = new KeystoreCertificate();
                dummyEntry.setCertificate(dummyResult.getCertificate(), new Certificate[]{dummyResult.getCertificate()});
                this.downloadedKeyEntries.put(dummyEntry.getFingerPrintSHA1(), downloadedKeyEntry);
            } else {
                this.keystore.setCertificateEntry(downloadedKeyEntry.getAlias(), downloadedKeyEntry.getX509Certificate());
            }
        }
    }

    /**
     * Generates a useless key for the client side - allows the entry to be
     * displayed as key entry in the keystore
     */
    private KeyGenerationResult generateDummyKey() throws Exception {
        KeyGenerator generator = new KeyGenerator();
        KeyGenerationValues parameter = new KeyGenerationValues();
        //generating a longer key takes some time.
        parameter.setKeySize(1024);
        parameter.setKeyAlgorithm(KeyGenerationValues.KEYALGORITHM_RSA);
        parameter.setKeyValidInDays(365);
        parameter.setSignatureAlgorithm(KeyGenerationValues.SIGNATUREALGORITHM_SHA1_WITH_RSA);
        parameter.setOrganisationName("Org");
        parameter.setOrganisationUnit("Server");
        parameter.setCommonName("CN");
        parameter.setEmailAddress("nomail@nomail.to");
        parameter.setLocalityName("DE");
        parameter.setCountryCode("DE");
        parameter.setStateName("Berlin");
        return (generator.generateKeyPair(parameter));
    }

    @Override
    public void save() throws Throwable {
        CertificateManager manager = new CertificateManager(Logger.getAnonymousLogger());
        //write the current keystore object to a byte array
        try (ByteArrayOutputStream memOut = new ByteArrayOutputStream()) {
            this.keystore.store(memOut, this.password);
            memOut.flush();
            KeystoreStorageImplByteArray storage = new KeystoreStorageImplByteArray(
                memOut.toByteArray(),
                this.password, this.keystoreUsage, this.keystoreStorageType);
            manager.loadKeystoreCertificates(storage);
        }        
        List<KeystoreCertificate> certificateList = manager.getKeyStoreCertificateList();
        List<KeystoreCertificate> delEntry = new ArrayList<KeystoreCertificate>();
        List<KeystoreCertificate> addEntry = new ArrayList<KeystoreCertificate>();
        for (KeystoreCertificate entry : certificateList) {
            if (this.downloadedKeyEntries.containsKey(entry.getFingerPrintSHA1())) {
                delEntry.add(entry);
                KeystoreCertificate originalKeyEntry = this.downloadedKeyEntries.get(entry.getFingerPrintSHA1());
                originalKeyEntry.setAlias(entry.getAlias());
                addEntry.add(originalKeyEntry);
            }
        }
        for (KeystoreCertificate entry : delEntry) {
            certificateList.remove(entry);
        }
        certificateList.addAll(addEntry);
        UploadRequestKeystore request = new UploadRequestKeystore(this.keystoreUsage);
        request.addCertificateList(certificateList);
        UploadResponseKeystore response = (UploadResponseKeystore) this.baseClient.sendSync(request);
        if (response == null) {
            throw new Exception(rb.getResourceString("error.save"));
        } else if (response != null && response.getException() != null) {
            throw (response.getException());
        }
    }

    @Override
    public void replaceAllEntriesAndSave(List<KeystoreCertificate> oldList, List<KeystoreCertificate> newList) throws Exception {
        throw new IllegalAccessException("KeystoreStorageImplClientServer: replaceAllEntriesAndSave() is not available for implementation of storage.");
    }

    @Override
    public Key getKey(String alias) throws Exception {
        Key key = this.keystore.getKey(alias, this.password);
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
        return (this.password);
    }

    @Override
    public void deleteEntry(String alias) throws Exception {
        if (this.keystore == null) {
            //internal error, should not happen
            throw new Exception(rb.getResourceString("error.delete.notloaded"));
        }
        this.keystore.deleteEntry(alias);
    }

    /**
     *
     * @return A Map of the the stored certificates where key=alias, value =
     * certificate
     * @throws Exception
     */
    @Override
    public Map<String, Certificate> loadCertificatesFromKeystore() throws Exception {
        Map<String, Certificate> certificateMap = KeyStoreUtil.getCertificatesFromKeystore(this.keystore);
        //replace the loaded certificates by existing key entries if required
        for (String alias : certificateMap.keySet()) {
            Certificate certificate = certificateMap.get(alias);
            KeystoreCertificate ksCertificate = new KeystoreCertificate();
            ksCertificate.setCertificate((X509Certificate) certificate, new Certificate[]{certificate});
            String fingerprint = ksCertificate.getFingerPrintSHA1();
            if (this.downloadedKeyEntries.containsKey(fingerprint)) {
                KeystoreCertificate replacement = this.downloadedKeyEntries.get(fingerprint);
                certificateMap.put(alias, replacement.getX509Certificate());
            }
        }
        return (certificateMap);
    }

    @Override
    public boolean isKeyEntry(String alias) throws Exception {
        throw new IllegalAccessException("KeystoreStorageImplClientServer: isKeyEntry() is not available for implementation of storage.");
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
        return (this.readonly);
    }
}
