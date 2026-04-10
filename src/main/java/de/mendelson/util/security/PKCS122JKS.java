package de.mendelson.util.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * This class allows to import a key that exist in pkcs#12 keystore into an
 * other JKS keystore
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class PKCS122JKS {

    /**
     * Keystore to use, if this is not set a new one will be created
     */
    private KeyStore keystore = null;
    /**
     * Default pass for a new created keystore, overwrite this by using the
     * setKeyStore() method
     */
    private char[] keystorePass = "test".toCharArray();

    /**
     * Creates a new instance of PEMUtil
     *
     * @param logger Logger to log the information to
     */
    public PKCS122JKS(Logger logger) {
        //forget it to work without BC at this point, the SUN JCE provider
        //could not handle pcks12
        // performs "Security.addProvider(new BouncyCastleProvider());" and adds some BC related system properties
        new BCCryptoHelper().initialize();
    }

    /**
     * Import a key and all trust chain certificates
     */
    public void importKey(KeyStore sourceKeyStore, String alias) throws Exception {
        if (sourceKeyStore.isKeyEntry(alias)) {
            Key importKey = sourceKeyStore.getKey(alias, new char[]{});
            Certificate[] certchain = sourceKeyStore.getCertificateChain(alias);
            if (certchain == null || certchain.length == 0) {
                throw new Exception("JKS import: private key with alias " + alias + " does not contain a certificate.");
            }
            KeyStore targetStore = this.keystore;
            if (targetStore == null) {
                targetStore = this.generateKeyStore();
            }
            targetStore.setKeyEntry(alias, importKey, this.keystorePass, certchain);
            //add certificates of the trust chain if they do not exist so far in the target keystore
            List<String> targetFingerprintList = new ArrayList<String>();
            Enumeration<String> targetAliasEnumeration = targetStore.aliases();
            while (targetAliasEnumeration.hasMoreElements()) {
                String targetAlias = targetAliasEnumeration.nextElement();
                Certificate certificate = targetStore.getCertificate(targetAlias);
                targetFingerprintList.add(KeyStoreUtil.generateFingerprintSHA1(certificate));
            }
            for (Certificate newCertificate : certchain) {
                if (newCertificate instanceof X509Certificate) {
                    X509Certificate newCertificateX509 = (X509Certificate) newCertificate;
                    String newCertFingerprint = KeyStoreUtil.generateFingerprintSHA1(newCertificateX509);
                    if (!targetFingerprintList.contains(newCertFingerprint)) {
                        String proposedAlias = KeyStoreUtil.getProposalCertificateAliasForImport(newCertificateX509);
                        while (targetStore.containsAlias(proposedAlias)) {
                            proposedAlias = proposedAlias + "0";
                        }
                        targetStore.setCertificateEntry(proposedAlias, newCertificateX509);
                    }
                }
            }
        } else {
            throw new Exception("JKS import: keystore doesn't contain a private key with alias " + alias);
        }
    }

    /**
     */
    public void importKey(InputStream sourceKeystoreStream, char[] sourceKeypass,
            String alias) throws Exception {
        //open keystore
        KeyStore sourceKeystore = KeyStore.getInstance(BCCryptoHelper.KEYSTORE_PKCS12,
                BouncyCastleProvider.PROVIDER_NAME);
        sourceKeystore.load(sourceKeystoreStream, sourceKeypass);
        this.importKey(sourceKeystore, alias);
    }

    /**
     * Loads ore creates a keystore to import the keys to
     */
    private KeyStore generateKeyStore() throws Exception {
        //do not remove the BC paramter, SUN cannot handle the format proper
        KeyStore localKeystore = KeyStore.getInstance(BCCryptoHelper.KEYSTORE_JKS,
                BouncyCastleProvider.PROVIDER_NAME);
        localKeystore.load(null, null);
        return (localKeystore);
    }

    /**
     * Sets an already existing keystore to this class. Without an existing
     * keystore a new one is created
     */
    public void setTargetKeyStore(KeyStore keystore, char[] keystorePass) {
        this.keystore = keystore;
        this.keystorePass = keystorePass;
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
}
