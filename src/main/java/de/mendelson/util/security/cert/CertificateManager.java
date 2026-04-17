package de.mendelson.util.security.cert;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.Base64;
import de.mendelson.util.uinotification.UINotification;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathBuilderResult;
import java.util.logging.Logger;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Helper class to store
 *
 * @author S.Heller
 * @version $Revision: 75 $
 */
public class CertificateManager {

    private Logger logger = null;
    private final List<KeystoreCertificate> keyStoreCertificateList
            = Collections.synchronizedList(new ArrayList<KeystoreCertificate>());
    private final Map<String, KeystoreCertificate> fingerprintCertificateMap
            = Collections.synchronizedMap(new HashMap<String, KeystoreCertificate>());
    private final Map<String, KeystoreCertificate> aliasCertificateMap
            = Collections.synchronizedMap(new HashMap<String, KeystoreCertificate>());
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificateManager.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    private KeystoreStorage storage = null;

    public CertificateManager(Logger logger) {
        this.logger = logger;
    }

    /**
     * Returns the cert alias that is assigned to the cert/key of the passed
     * fingerprint (SHA1)
     */
    public String getAliasByFingerprint(byte[] fingerprintSHA1) {
        KeystoreCertificate cert = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        //fingerprint not found
        if (cert == null) {
            return (null);
        } else {
            return (cert.getAlias());
        }
    }

    /**
     * Returns the cert alias that is assigned to the cert/key of the passed
     * fingerprint (SHA1)
     */
    public String getAliasByFingerprint(String fingerprintSHA1) {
        KeystoreCertificate cert = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        //fingerprint not found
        if (cert == null) {
            return (null);
        } else {
            return (cert.getAlias());
        }
    }

    /**
     * Returns the certificate chain for a special alias
     */
    public Certificate[] getCertificateChain(String alias) throws Exception {
        Certificate[] chain = this.storage.getCertificateChain(alias);
        return (chain);
    }

    /**
     * Returns the X509 certificate assigned to the passed alias
     */
    public X509Certificate getX509Certificate(String alias) throws Exception {
        KeystoreCertificate certificate = null;
        synchronized (this.aliasCertificateMap) {
            certificate = this.aliasCertificateMap.get(alias);
        }
        if (certificate == null) {
            throw new Exception(rb.getResourceString("alias.notfound", alias));
        }
        return (certificate.getX509Certificate());
    }

    /**
     * Returns the list of available X509 certificates
     */
    public List<X509Certificate> getX509CertificateList() {
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                certList.add(cert.getX509Certificate());
            }
        }
        return (certList);
    }

    /**
     * Returns the private key for an alias. If the assigned certificate does
     * not contain a private key an exception is thrown
     */
    public PrivateKey getPrivateKey(String alias) throws Exception {
        KeystoreCertificate entry = null;
        synchronized (this.aliasCertificateMap) {
            entry = this.aliasCertificateMap.get(alias);
        }
        if (entry == null) {
            throw new Exception(rb.getResourceString("alias.notfound", alias));
        }
        PrivateKey privateKey = (PrivateKey) entry.getPrivateKey();
        if (privateKey == null) {
            throw new Exception(rb.getResourceString("alias.hasno.privatekey", alias));
        }
        return (privateKey);

    }

    /**
     * Returns the public key for an alias.
     */
    public PublicKey getPublicKey(String alias) throws Exception {
        return (this.storage.getCertificate(alias).getPublicKey());
    }

    /**
     * Returns the private key for a passed public key. If the passed public key
     * does not exist an exception is thrown
     *
     * @param publicKey
     * @return
     */
    public PrivateKey getPrivateKeyByPublicKey(PublicKey publicKey) throws Exception {
        try {
            String lookupKeyEncoded = Base64.encode(publicKey.getEncoded());
            synchronized (this.keyStoreCertificateList) {
                for (KeystoreCertificate keystoreCertificate : this.keyStoreCertificateList) {
                    if (keystoreCertificate.getIsKeyPair()) {
                        String foundKeyEncoded = Base64.encode(keystoreCertificate.getX509Certificate().getPublicKey().getEncoded());
                        if (foundKeyEncoded.equals(lookupKeyEncoded)) {
                            return (this.getPrivateKey(keystoreCertificate.getAlias()));
                        }
                    }
                }
            }
            throw new Exception();
        } catch (Exception e) {
            throw new Exception("The private key for the passed public key does not exist.");
        }
    }

    /**
     * Returns the private key for a passed fingerprint (SHA1). If the assigned
     * certificate does not contain a private key an exception is thrown
     */
    public PrivateKey getPrivateKeyByFingerprintSHA1(byte[] fingerprintStrSHA1) throws Exception {
        //this will always return the private key if there is a public and a private key entry with the same serial in
        //the keystore
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintStrSHA1);
        return (this.getPrivateKey(certificate.getAlias()));
    }

    /**
     * Returns the public key for a passed fingerprint (SHA1). If the assigned
     * certificate does not contain a public key an exception is thrown
     */
    public PublicKey getPublicKeyByFingerprintSHA1(byte[] fingerprintStrSHA1) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintStrSHA1);
        return (this.getPublicKey(certificate.getAlias()));
    }

    /**
     * Returns the private key for a passed fingerprint (SHA1). If the assigned
     * certificate does not contain a private key an exception is thrown
     */
    public PrivateKey getPrivateKeyByFingerprintSHA1(String fingerprintStrSHA1) throws Exception {
        //this will always return the private key if there is a public and a private key entry with the same serial in
        //the keystore
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintStrSHA1);
        if (certificate == null) {
            throw new Exception(rb.getResourceString("certificate.not.found.fingerprint",
                    fingerprintStrSHA1));
        }
        return (this.getPrivateKey(certificate.getAlias()));
    }

    /**
     * Returns the public key for a passed fingerprint (SHA1). If the assigned
     * certificate does not contain a public key an exception is thrown
     */
    public PublicKey getPublicKeyByFingerprintSHA1(String fingerprintStrSHA1) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintStrSHA1);
        return (this.getPublicKey(certificate.getAlias()));
    }

    /**
     * Returns the public key or the private key for an alias.
     */
    public Key getKey(String alias) throws Exception {
        KeystoreCertificate certificate = null;
        synchronized (this.aliasCertificateMap) {
            certificate = this.aliasCertificateMap.get(alias);
        }
        if (certificate == null) {
            throw new Exception(rb.getResourceString("alias.notfound", alias));
        }
        Key key = certificate.getPrivateKey();
        if (key == null) {
            throw new Exception(rb.getResourceString("alias.hasno.key", alias));
        } else {
            return (key);
        }
    }

    /**
     * Stores the manages keystore
     */
    public void saveKeystore() throws Throwable {
        this.logger.info("[DEBUG CertificateManager] saveKeystore() called");
        this.storage.save();
        this.logger.info("[DEBUG CertificateManager] Storage saved, now rereading certificates...");
        this.rereadKeystoreCertificates();
        this.logger.info("[DEBUG CertificateManager] Certificates reread successfully");
    }

    /**
     * Deletes all entries in the underlaying storage and replaces them by the
     * list of passed certificates
     *
     * @param newList
     * @throws Exception
     */
    public void replaceAllEntriesAndSave(List<KeystoreCertificate> newList) throws Exception {
        List<KeystoreCertificate> oldList = new ArrayList<KeystoreCertificate>();
        synchronized (this.keyStoreCertificateList) {
            oldList.addAll(this.keyStoreCertificateList);
        }
        this.storage.replaceAllEntriesAndSave(oldList, newList);
        this.rereadKeystoreCertificates();
    }

    /**
     * Deletes an entry from the actual keystore
     */
    public void deleteKeystoreEntry(String alias) throws Throwable {
        this.storage.deleteEntry(alias);
        this.rereadKeystoreCertificates();
    }

    /**
     * Renames an entry in the underlaying keystore. Please remember that
     * PKCS#12 contains no key pair password, pass null in this case
     *
     */
    public void renameAlias(String oldAlias, String newAlias) throws Throwable {
        char[] keypairPass = null;
        if (this.storage.getKeystoreStorageType().equals(BCCryptoHelper.KEYSTORE_JKS)) {
            keypairPass = this.getKeystorePass();
        }
        this.storage.renameEntry(oldAlias, newAlias, keypairPass);
        this.rereadKeystoreCertificates();
    }

    public void loadKeystoreFromServer() throws Exception {
        try {
            this.storage.loadKeystoreFromServer();
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
        this.rereadKeystoreCertificates();
    }

    /**
     * Refreshes the cached certificate data. This is an expensive operation as
     * it reads and analyzes all certificates/keys from the underlaying keystore
     */
    public void rereadKeystoreCertificates() throws Exception {
        Map<String, Certificate> newCertificateMap = this.storage.loadCertificatesFromKeystore();
        synchronized (this.keyStoreCertificateList) {            
            this.keyStoreCertificateList.clear();
            for (String alias : newCertificateMap.keySet()) {
                KeystoreCertificate certificate = new KeystoreCertificate();
                certificate.setAlias(alias);
                X509Certificate foundCertificate = (X509Certificate) newCertificateMap.get(alias);
                certificate.setCertificate(foundCertificate, this.storage.getCertificateChain(alias));
                try {
                    boolean isKeyPair = this.getKeystore().isKeyEntry(alias);
                    certificate.setIsKeyPair(isKeyPair);
                    if (isKeyPair) {
                        certificate.setPrivateKey(this.storage.getKey(alias));
                    }
                } catch (Throwable e) {
                    //no problem, thats what we wanted to know
                    certificate.setIsKeyPair(false);
                    if (this.logger != null) {
                        this.logger.warning(e.getMessage());
                    }
                }
                this.keyStoreCertificateList.add(certificate);
            }
            this.recomputeInternalCaches();
        }
    }

    /**
     * Recomputes the internal caches from the already existing list of
     * certificates
     */
    private void recomputeInternalCaches() {
        synchronized (this.keyStoreCertificateList) {
            synchronized (this.fingerprintCertificateMap) {
                this.fingerprintCertificateMap.clear();
                for (KeystoreCertificate certificate : this.keyStoreCertificateList) {
                    String fingerprint = certificate.getFingerPrintSHA1();
                    //it could happen that a cert and a key with the same fingerprint are in the keystore.
                    //the key has priority in this case
                    KeystoreCertificate existingCertificate = this.fingerprintCertificateMap.get(fingerprint);
                    if (existingCertificate == null) {
                        this.fingerprintCertificateMap.put(fingerprint, certificate);
                    } else if (!existingCertificate.getIsKeyPair()) {
                        this.fingerprintCertificateMap.put(fingerprint, certificate);
                    }
                }
            }
            synchronized (this.aliasCertificateMap) {
                this.aliasCertificateMap.clear();
                for (KeystoreCertificate certificate : this.keyStoreCertificateList) {
                    String alias = certificate.getAlias();
                    this.aliasCertificateMap.put(alias, certificate);
                }
            }
        }
    }

    /**
     * Adds a single certificate and saves the underlaying keystore
     */
    public void addCertificate(String alias, X509Certificate x509Certificate) throws Throwable {
        this.getKeystore().setCertificateEntry(alias, x509Certificate);
        this.saveKeystore();
        this.rereadKeystoreCertificates();
    }

    /**
     * Called from external if the certificate storage has been changed. This
     * method calls the normal rereadkeystore method but logs the step
     */
    public void rereadKeystoreCertificatesLogged() {
        try {
            this.rereadKeystoreCertificates();
            if (this.logger != null) {
                String usageStr;
                if (this.storage.getKeystoreUsage() == KeystoreStorageImplFile.KEYSTORE_USAGE_TLS) {
                    usageStr = "TLS";
                } else if (this.storage.getKeystoreUsage() == KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN) {
                    usageStr = "Enc/Sign";
                } else {
                    usageStr = "Unknown";
                }
                this.logger.fine(rb.getResourceString("keystore.reloaded", usageStr));
            }
        } catch (Exception e) {
            if (this.logger != null) {
                this.logger.warning(rb.getResourceString("keystore.read.failure",
                        new Object[]{e.getMessage()}));
            }
        }
    }

    /**
     * Wrapper function for the underlaying keystore storage implementation
     */
    public boolean canWrite() {
        return (!this.storage.isReadOnly());
    }

    /**
     * Reads the certificates of the actual key store
     *
     */
    public void loadKeystoreCertificates(KeystoreStorage storage) {
        this.storage = storage;
        try {
            this.rereadKeystoreCertificates();
        } catch (Exception e) {
            if (this.logger != null) {
                this.logger.warning(rb.getResourceString("keystore.read.failure",
                        new Object[]{e.getMessage()}));
            }
        }
    }

    /**
     * Reads the certificates of the actual key store
     *
     */
    public void loadKeystoreCertificatesWithException(KeystoreStorage storage) throws Exception {
        this.storage = storage;
        this.rereadKeystoreCertificates();
    }

    /**
     * returns null if the alias does not exist
     */
    public KeystoreCertificate getKeystoreCertificate(String alias) {
        synchronized (this.aliasCertificateMap) {
            KeystoreCertificate certificate = this.aliasCertificateMap.get(alias);
            return (certificate);
        }
    }

    public KeystoreCertificate getKeystoreCertificateBySubjectDNNonNull(String subjectDN, String additionalInfo) throws Exception {
        KeystoreCertificate foundCert = null;
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                if (cert.getSubjectDN().equals(subjectDN)) {
                    //no entry found so far: always store the found one
                    if (foundCert == null) {
                        foundCert = cert;
                    } else {
                        //entry already found: overwrite it only if the found entry is a key
                        if (cert.getIsKeyPair()) {
                            foundCert = cert;
                        }
                    }
                }
            }
            if (foundCert == null) {
                throw new Exception(rb.getResourceString("certificate.not.found.subjectdn.withinfo",
                        new Object[]{subjectDN, additionalInfo}));
            } else {
                return (foundCert);
            }
        }
    }

    public KeystoreCertificate getKeystoreCertificateBySubjectKeyIdentifierNonNull(byte[] skiBytes, String additionalInfo) throws Exception {
        KeystoreCertificate foundCert = this.getKeystoreCertificateBySubjectKeyIdentifier(skiBytes);
        if (foundCert == null) {
            throw new Exception(rb.getResourceString("certificate.not.found.ski.withinfo",
                    new Object[]{KeystoreCertificate.byteArrayToHexStr(skiBytes), additionalInfo}));
        } else {
            return (foundCert);
        }

    }

    public KeystoreCertificate getKeystoreCertificateBySubjectKeyIdentifier(byte[] skiBytes) {
        String skiHex = KeystoreCertificate.byteArrayToHexStr(skiBytes);
        //it could happen that a cert and a key with the same fingerprint are in the keystore.
        //Always return the key in this case.
        KeystoreCertificate foundCert = null;
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                List<String> certificateSKIList = cert.getSubjectKeyIdentifier();
                for (String foundSKI : certificateSKIList) {
                    if (foundSKI.equals(skiHex)) {
                        //no entry found so far: always store the found one
                        if (foundCert == null) {
                            foundCert = cert;
                        } else {
                            //entry already found: overwrite it only if the found entry is a key
                            if (cert.getIsKeyPair()) {
                                foundCert = cert;
                            }
                        }
                    }
                }
            }
        }
        return (foundCert);
    }

    /**
     * returns null if a certificate with the issuerDN and the serial does not
     * exist
     */
    public KeystoreCertificate getKeystoreCertificateByIssuerDNAndSerial(String issuerDN, String serialDEC) {
        if (issuerDN == null || serialDEC == null) {
            return (null);
        }
        return (this.getKeystoreCertificateByIssuerAndSerial(new X500Principal(issuerDN), serialDEC));
    }

    /**
     * returns null if a certificate with the issuerDN and the serial does not
     * exist
     */
    public KeystoreCertificate getKeystoreCertificateByIssuerAndSerial(X500Principal issuer, String serialDEC) {
        //it could happen that a cert and a key with the same fingerprint are in the keystore.
        //Always return the key in this case.
        KeystoreCertificate foundCert = null;
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                if (cert.getSerialNumberDEC().equals(serialDEC) && cert.getX509Certificate().getIssuerX500Principal().equals(issuer)) {
                    //no entry found so far: always store the found one
                    if (foundCert == null) {
                        foundCert = cert;
                    } else {
                        //entry already found: overwrite it only if the found entry is a key
                        if (cert.getIsKeyPair()) {
                            foundCert = cert;
                        }
                    }
                }
            }
        }
        return (foundCert);
    }

    /**
     * Throws an exception if the requested certificate does not exist in the
     * keystore - this contains the additional info Str
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1NonNull(String fingerprintSHA1, String additionalInfo) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        if (certificate == null) {
            throw new Exception(rb.getResourceString("certificate.not.found.fingerprint.withinfo",
                    new Object[]{fingerprintSHA1, additionalInfo}));
        } else {
            return (certificate);
        }
    }

    /**
     * Throws an exception if the requested certificate does not exist in the
     * keystore
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1NonNull(String fingerprintSHA1) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        if (certificate == null) {
            throw new Exception(rb.getResourceString("certificate.not.found.fingerprint", fingerprintSHA1));
        } else {
            return (certificate);
        }
    }

    /**
     * Throws an exception if the requested certificate does not exist in the
     * keystore
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1NonNull(byte[] fingerprintSHA1) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        if (certificate == null) {
            throw new Exception(rb.getResourceString("certificate.not.found.fingerprint",
                    KeystoreCertificate.fingerprintBytesToStr(fingerprintSHA1)));
        } else {
            return (certificate);
        }
    }

    /**
     * Throws an exception if the requested certificate does not exist in the
     * keystore- this contains the additional info Str
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1NonNull(byte[] fingerprintSHA1, String additionalInfo) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        if (certificate == null) {
            throw new Exception(rb.getResourceString("certificate.not.found.fingerprint.withinfo",
                    new Object[]{KeystoreCertificate.fingerprintBytesToStr(fingerprintSHA1), additionalInfo}));
        } else {
            return (certificate);
        }
    }

    private boolean issuerIsEqual(String issuer1, String issuer2) {
        final String[] compareList = new String[]{
            "C", "O", "OU", "CN", "ST", "L", "E"
        };
        try {
            LdapName name1 = new LdapName(issuer1);
            LdapName name2 = new LdapName(issuer2);
            for (Rdn rdn1 : name1.getRdns()) {
                for (Rdn rdn2 : name2.getRdns()) {
                    for (String compareType : compareList) {
                        if (rdn1.getType().equalsIgnoreCase(compareType)
                                && rdn2.getType().equalsIgnoreCase(compareType)) {
                            String value1 = rdn1.getValue().toString();
                            String value2 = rdn2.getValue().toString();
                            if (!value1.equals(value2)) {
                                return (false);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return (false);
        }
        return (true);
    }

    /**
     * Tries to find a certificate with the related issuer/serial and throws an
     * exception if it does not exist in the certificate manager
     *
     * @param issuerStrEscaped This is the issuer as string - in this format commas are escaped, e.g.
     * "O=GoDaddy.com\, Inc."
     * @param serial
     * @param additionalInfo Additional info str if the certificate has not been found
     * @return
     * @throws Exception
     */
    public KeystoreCertificate getKeystoreCertificateByIssuerSerialNonNull(String issuerStrEscaped, BigInteger serial,
            String additionalInfo) throws Exception {
        KeystoreCertificate foundCert = null;
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                String foundIssuerDN = cert.getIssuerDN();
                BigInteger foundSerial = cert.getX509Certificate().getSerialNumber();
                if (foundSerial.equals(serial) && this.issuerIsEqual(issuerStrEscaped, foundIssuerDN)) {
                    //no entry found so far: always store the found one
                    if (foundCert == null) {
                        foundCert = cert;
                    } else {
                        //entry already found: overwrite it only if the found entry is a key
                        if (cert.getIsKeyPair()) {
                            foundCert = cert;
                        }
                    }
                }
            }
        }
        if (foundCert == null) {
            String serialHex = serial.toString(16);
            throw new Exception(rb.getResourceString("certificate.not.found.issuerserial.withinfo",
                    new Object[]{issuerStrEscaped, 
                        serial.toString() + " (dec), " + serialHex + " (hex)", 
                        additionalInfo}));
        } else {
            return (foundCert);
        }
    }

    /**
     * returns null if the fingerprint does not exist
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1(byte[] fingerprintSHA1) {
        if (fingerprintSHA1 == null) {
            return (null);
        }
        String fingerprintSHA1Str = KeystoreCertificate.byteArrayToHexStr(fingerprintSHA1);
        synchronized (this.fingerprintCertificateMap) {
            KeystoreCertificate foundCert = this.fingerprintCertificateMap.get(fingerprintSHA1Str);
            return (foundCert);
        }
    }

    /**
     * returns null if the fingerprint does not exist
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1(String fingerprintSHA1) {
        //just return null if the fingerprint string is invalid in any case
        if (fingerprintSHA1 == null || fingerprintSHA1.trim().isEmpty() || !fingerprintSHA1.contains(":")) {
            return (null);
        }
        synchronized (this.fingerprintCertificateMap) {
            KeystoreCertificate foundCert = this.fingerprintCertificateMap.get(fingerprintSHA1);
            return (foundCert);
        }
    }

    /**
     * Returns a list of certificates, sorted by their name
     */
    public List<KeystoreCertificate> getKeyStoreCertificateList() {
        List<KeystoreCertificate> newList = new ArrayList<KeystoreCertificate>();
        synchronized (this.keyStoreCertificateList) {
            newList.addAll(this.keyStoreCertificateList);
        }
        Collections.sort(newList);
        return (newList);
    }

    /**
     * Passes a logger to the certificate manager. There will be no logging if
     * no logger has been passed
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public char[] getKeystorePass() {
        return this.storage.getKeystorePass();
    }

    public KeyStore getKeystore() {
        return (this.storage.getKeystore());
    }

    /**
     * Returns a map with issuer as key and the available certs as value
     */
    public Map<X500Principal, List<X509Certificate>> getIssuerCertificateMap() throws Exception {
        Map<X500Principal, List<X509Certificate>> map = new HashMap<X500Principal, List<X509Certificate>>();
        synchronized (this.keyStoreCertificateList) {
            List<KeystoreCertificate> certList = this.getKeyStoreCertificateList();
            for (KeystoreCertificate keystoreCertificate : certList) {
                X509Certificate foundCert = (X509Certificate) keystoreCertificate.getX509Certificate();
                if (foundCert != null) {
                    X500Principal subjectDN = foundCert.getSubjectX500Principal();
                    List<X509Certificate> foundCertList = map.get(subjectDN);
                    if (foundCertList == null) {
                        foundCertList = new ArrayList<X509Certificate>();
                        foundCertList.add(foundCert);
                    } else {
                        if (!foundCertList.contains(foundCert)) {
                            foundCertList.add(foundCert);
                        }
                    }
                    map.put(subjectDN, foundCertList);
                }
            }
            return map;
        }
    }

    /**
     * Adds a new Key entry and saves the underlaying keystore
     */
    public void setKeyEntry(String alias, Key key, Certificate[] chain) throws Throwable {
        this.getKeystore().setKeyEntry(alias, key, this.getKeystorePass(), chain);
        this.saveKeystore();
        this.rereadKeystoreCertificates();
    }

    /**
     * Returns the storage type of the underlaying storage. This is one of
     * BCCryptoHelper.KEYSTORE_JKS, BCCryptoHelper.KEYSTORE_PKCS11,
     * BCCryptoHelper.KEYSTORE_PKCS12
     *
     * @return
     */
    public String getStorageType() {
        return (this.storage.getKeystoreStorageType());
    }

    /**
     * Returns the usage of the underlaying storage, e.g.
     * KeystoreStorageImplClientServer.KEYSTORE_USAGE_ENC_SIGN or
     * KeystoreStorageImplClientServer.KEYSTORE_USAGE_TLS
     *
     * @return
     */
    public int getStorageUsage() {
        return (this.storage.getKeystoreUsage());
    }

    /**
     * Returns the userId for user-specific keystores (0 = admin/system, >0 = specific user)
     * Only works if the storage is KeystoreStorageImplClientServer, otherwise returns 0
     */
    public int getUserId() {
        if (this.storage instanceof de.mendelson.util.security.cert.clientserver.KeystoreStorageImplClientServer) {
            return ((de.mendelson.util.security.cert.clientserver.KeystoreStorageImplClientServer) this.storage).getUserId();
        }
        return 0;  // Default to admin/system for non-client-server storages
    }

    /**
     * Compute the whole trust chain of a given alias and returns it as list
     */
    public List<X509Certificate> computeTrustChain(String alias) {
        KeystoreCertificate certificate = this.getKeystoreCertificate(alias);
        PKIXCertPathBuilderResult result = certificate.getPKIXCertPathBuilderResult(this.getKeystore(), this.getX509CertificateList());
        List<X509Certificate> list = new ArrayList<X509Certificate>();
        //self signed?
        if (result == null) {
            //it's a self signed certificate: return it without any CA/intermediate certs
            list.add(certificate.getX509Certificate());
        } else {
            //trusted cert
            CertPath path = result.getCertPath();
            for (Object cert : path.getCertificates()) {
                list.add(0, (X509Certificate) cert);
            }
            X509Certificate anchorCertX509 = list.get(0);
            boolean trustChainComplete = false;
            while (!trustChainComplete) {
                KeystoreCertificate keyCertAnchor = null;
                //find out the keystore cert of the anchor
                for (KeystoreCertificate keyCert : this.getKeyStoreCertificateList()) {
                    if (keyCert.getX509Certificate().equals(anchorCertX509)) {
                        keyCertAnchor = keyCert;
                        break;
                    }
                }
                if (keyCertAnchor != null) {
                    //check if the anchor has another anchor as intermediates certificate may have the attribute "CA:true", too
                    result = keyCertAnchor.getPKIXCertPathBuilderResult(this.getKeystore(), this.getX509CertificateList());
                    anchorCertX509 = result.getTrustAnchor().getTrustedCert();
                    if (!keyCertAnchor.getX509Certificate().equals(anchorCertX509)) {
                        list.add(0, anchorCertX509);
                    } else {
                        trustChainComplete = true;
                    }
                } else {
                    trustChainComplete = true;
                }
            }
        }
        return (list);
    }

    /**
     * Just loads the certificates with their alias from the storage - this does
     * not recompute the internal caches
     *
     * @return
     * @throws Exception
     */
    public Map<String, Certificate> loadCertificatesFromStorage() throws Exception {
        return (this.storage.loadCertificatesFromKeystore());
    }

}
