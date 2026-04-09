package de.mendelson.util.security.cert;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.CryptoProvider;
import de.mendelson.util.security.KeyStoreUtil;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
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
 * Keystore storage implementation that relies on a HSM via PKCS#11
 *
 * @author S.Heller
 * @version $Revision: 15 $
 */
public class KeystoreStorageImplPKCS11 implements KeystoreStorage {

    public static final int KEYSTORE_USAGE_TLS = KeystoreStorageImplFile.KEYSTORE_USAGE_TLS;
    public static final int KEYSTORE_USAGE_ENC_SIGN = KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN;
    public static final String KEYSTORE_STORAGE_TYPE_PKCS11 = BCCryptoHelper.KEYSTORE_PKCS11;

    private KeyStore keystore = null;
    private final char[] userPin;
    private final MecResourceBundle rb;
    private int keystoreUsage = KEYSTORE_USAGE_ENC_SIGN;
    private final CryptoProvider.ProviderContainer providerContainer;

    /**
     */
    public KeystoreStorageImplPKCS11(final int KEYSTORE_USAGE,
            CryptoProvider cryptoProvider) throws Exception {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleKeystoreStorage.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        if (KEYSTORE_USAGE == KEYSTORE_USAGE_ENC_SIGN) {
           this.providerContainer = cryptoProvider.getProviderEncSign();
        } else if (KEYSTORE_USAGE == KEYSTORE_USAGE_TLS) {
            this.providerContainer = cryptoProvider.getProviderTLSKeystore();
        } else {
            throw new IllegalArgumentException("KeystoreStorageImplPKCS11: Unknown keystore usage " + KEYSTORE_USAGE);
        }
        this.userPin = providerContainer.getUserPin();
        this.keystoreUsage = KEYSTORE_USAGE;
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        this.keystore = cryptoHelper.createKeyStoreInstance(BCCryptoHelper.KEYSTORE_PKCS11, this.providerContainer.getProvider());
        KeyStoreUtil.loadKeyStorePKCS11(this.keystore, this.userPin);
    }

    @Override
    public void loadKeystoreFromServer() throws Exception {
        throw new IllegalAccessException("KeystoreStorageImplPKCS11: loadKeystoreFromServer() is not available for this implementation of storage.");
    }

    @Override
    public void save() throws Exception {
        if (this.keystore == null) {
            //internal error, should not happen
            throw new Exception(this.rb.getResourceString("error.save.notloaded"));
        }
        KeyStoreUtil.saveKeyStorePKCS11(this.keystore, this.userPin);
    }

    @Override
    public void replaceAllEntriesAndSave(List<KeystoreCertificate> oldList,
            List<KeystoreCertificate> newList) throws Exception {
        if (this.keystore == null) {
            //internal error, should not happen
            throw new Exception(this.rb.getResourceString("error.save.notloaded"));
        }
        //delete all certificate entries in the keystore
        synchronized (this.keystore) {
            for (KeystoreCertificate oldEntry : oldList) {
                if (!oldEntry.getIsKeyPair()) {
                    this.keystore.deleteEntry(oldEntry.getAlias());
                }
            }
        }
        //find out PKCS#11 keys to delete in the existing keystore
        for (KeystoreCertificate oldEntry : oldList) {
            boolean oldKeyShouldBeDeleted = true;
            if (oldEntry.getIsKeyPair()) {
                Key oldKey = oldEntry.getPrivateKey();
                for (KeystoreCertificate newEntry : newList) {
                    if (newEntry.getPrivateKey() == oldKey) {
                        oldKeyShouldBeDeleted = false;
                        //is there a rename?
                        if (!oldEntry.getAlias().equals(newEntry.getAlias())) {
                            synchronized (this.keystore) {
                                KeyStoreUtil.renameEntry(
                                        this.keystore,
                                        oldEntry.getAlias(),
                                        newEntry.getAlias(),
                                        null
                                );
                            }
                        }
                    }
                }
                if (oldKeyShouldBeDeleted) {
                    synchronized (this.keystore) {
                        this.keystore.deleteEntry(oldEntry.getAlias());
                    }
                }
            }
        }
        //now set new certificate entries and import the new keys
        synchronized (this.keystore) {
            for (KeystoreCertificate certificate : newList) {
                if (!certificate.getIsKeyPair()) {
                    this.keystore.setCertificateEntry(certificate.getAlias(), certificate.getX509Certificate());
                } else {
                    //do import a new key. All keys are new that are no PKCS#11 keys
                    if (!certificate.getPrivateKey().getClass().getName().contains("P11PrivateKey")) {
                        this.keystore.setKeyEntry(certificate.getAlias(), certificate.getPrivateKey(), null,
                                certificate.getCertificateChain());
                    }
                }
            }
            this.save();
        }
    }

    @Override
    public Key getKey(String alias) throws Exception {
        Key key = this.keystore.getKey(alias, this.userPin);        
        return (key);
    }

    @Override
    public Certificate[] getCertificateChain(String alias) throws Exception {
        Certificate[] chain = this.keystore.getCertificateChain(alias);
        return (chain);
    }

    @Override
    public X509Certificate getCertificate(String alias) throws Exception {
        X509Certificate certificate = (X509Certificate) this.keystore.getCertificate(alias);
        return (certificate);
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
        return (this.userPin);
    }

    @Override
    public void deleteEntry(String alias) throws Exception {
        if (this.keystore == null) {
            //internal error, should not happen
            throw new Exception(this.rb.getResourceString("error.delete.notloaded"));
        }
        this.keystore.deleteEntry(alias);
    }

    @Override
    public Map<String, Certificate> loadCertificatesFromKeystore() throws Exception {
        //recreate keystore object
        KeyStoreUtil.loadKeyStorePKCS11(this.keystore, this.userPin);
        Map<String, Certificate> certificateMap = KeyStoreUtil.getCertificatesFromKeystore(this.keystore);
        return (certificateMap);
    }

    @Override
    public boolean isKeyEntry(String alias) throws Exception {
        return (this.keystore.isKeyEntry(alias));
    }

    @Override
    public String getKeystoreStorageType() {
        return ("PKCS11");
    }

    @Override
    public int getKeystoreUsage() {
        return (this.keystoreUsage);
    }
    
    @Override
    public boolean isReadOnly() {
        return( false );
    }
}
