package de.mendelson.util.security;

import de.mendelson.util.MecResourceBundle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Utility class to handle java keyStore issues
 *
 * @author S.Heller
 * @version $Revision: 81 $
 */
public class KeyStoreUtil {

    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleKeyStoreUtil.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    private KeyStoreUtil() {
    }

    /**
     * Saves the passed keystore
     *
     * @param keystorePass Password for the keystore
     * @param filename Filename where to save the keystore to
     */
    public static void saveKeyStore(KeyStore keystore, char[] keystorePass, String filename) throws Exception {
        try (OutputStream out = Files.newOutputStream(Paths.get(filename),
                StandardOpenOption.SYNC,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            saveKeyStore(keystore, keystorePass, out);
        }
    }

    /**
     * Saves the passed keystore
     *
     * @param keystorePass Password for the keystore
     */
    public static void saveKeyStore(KeyStore keystore, char[] keystorePass, OutputStream outStream) throws Exception {
        keystore.store(outStream, keystorePass);
    }

    /**
     * Saves the passed keystore
     *
     */
    public static void saveKeyStorePKCS11(KeyStore keystore, char[] userPin) throws Exception {
        keystore.store(null, userPin);
    }

    /**
     * Loads a keystore and returns it. The passed keystore has to be created
     * first by the security provider, e.g. using the code
     * KeyStore.getInstance(<keystoretype>, <provider>); If the passed filename
     * does not exist a new, empty keystore will be created
     */
    public static void loadKeyStore(KeyStore keystoreInstance, String filename, char[] keystorePass) throws Exception {
        Path inFile = Paths.get(filename);
        try {
            if (Files.exists(inFile)) {
                try (InputStream inStream = Files.newInputStream(inFile)) {
                    keystoreInstance.load(inStream, keystorePass);
                }
            } else {
                keystoreInstance.load(null, null);
            }
        } catch (Exception e) {
            String message = "[" + e.getClass().getSimpleName() + "]: " + e.getMessage();
            throw new Exception("The system is unable to load the keystore \"" + inFile.toAbsolutePath().toString()
                    + "\" using the keystore and key password \"" + new String(keystorePass) + "\".\nThe following problem occured: " + message);
        }
    }

    /**
     * Loads a keystore from a byte array. The passed keystore has to be created
     * first by the security provider, e.g. using the code
     * KeyStore.getInstance(<keystoretype>, <provider>); If the passed filename
     * does not exist a new, empty keystore will be created
     */
    public static void loadKeyStore(KeyStore keystoreInstance, byte[] keystoreData, char[] keystorePass) throws Exception {
        try (InputStream inStream = new ByteArrayInputStream(keystoreData)) {
            keystoreInstance.load(inStream, keystorePass);
        } catch (Exception e) {
            String message = "KeyStoreUtil.loadKeyStore [" + e.getClass().getSimpleName() + "]: " + e.getMessage();
            throw new Exception(message);
        }
    }

    /**
     * Loads keystore data via PKCS11 and returns it. The passed keystore has to
     * be created first by the security provider, e.g. using the code
     * KeyStore.getInstance(<keystoretype>, <provider>);
     */
    public static void loadKeyStorePKCS11(KeyStore keystoreInstance, char[] userPin) throws Exception {
        try {
            keystoreInstance.load(null, userPin);
        } catch (Exception e) {
            String message = "[" + e.getClass().getSimpleName() + "]: " + e.getMessage();
            throw new Exception("The system is unable to load the keystore via PKCS#11 using the user PIN \""
                    + new String(userPin) + "\".\nThe following problem ocurred: " + message);
        }
    }

    /**
     * Renames an entry in the keystore
     *
     * @param keyStore Keystore to read the keys from
     * @param oldAlias Old alias to rename
     * @param newAlias New alias to rename
     * @param keyPassword Password of the key, not used for keystores of format
     * PKCS#12, for these types of keystores just pass null.
     *
     */
    public static void renameEntry(KeyStore keyStore, String oldAlias, String newAlias,
            char[] keyPassword) throws Exception {
        if (oldAlias != null && newAlias != null && oldAlias.equalsIgnoreCase(newAlias)) {
            throw new Exception(rb.getResourceString("alias.rename.new.equals.old"));
        }
        if (keyPassword == null) {
            keyPassword = "".toCharArray();
        }
        //copy operation
        if (keyStore.isKeyEntry(oldAlias)) {
            Key key = keyStore.getKey(oldAlias, keyPassword);
            Certificate[] certs = keyStore.getCertificateChain(oldAlias);
            keyStore.setKeyEntry(newAlias, key, keyPassword, certs);
        } else {
            Certificate cert = keyStore.getCertificate(oldAlias);
            keyStore.setCertificateEntry(newAlias, cert);
        }
        //delete operation
        keyStore.deleteEntry(oldAlias);
    }

    /**
     * Imports a X509 certificate into the passed keystore using a special
     * provider e.g. for the use of BouncyCastle Provider use the code Provider
     * provBC = Security.getProvider("BC");
     *
     * @param keystore Keystore to import the certificate to
     * @param certStream Stream to access the cert data from
     * @param alias Alias to use in the keystore
     * @param certIndex Its possible that there are more than a single
     * certificate in the passed stream (e.g. p7b). Just pass 0 if you are sure
     * that there is only a single certificate in the stream, else the index to
     * import
     */
    public static void importX509Certificate(KeyStore keystore, InputStream certStream,
            String alias, int certIndex, String securityProvider) throws Exception {
        if (keystore.containsAlias(alias)) {
            throw new Exception(rb.getResourceString("alias.exist", alias));
        }
        List<X509Certificate> certList = readCertificates(certStream, securityProvider);
        keystore.setCertificateEntry(alias, certList.get(certIndex));
    }

    /**
     * Checks if the passed certificate is stored in the keystore and returns
     * its alias. Returns null if the cert is not in the keystore
     */
    public static String getCertificateAlias(KeyStore keystore, X509Certificate cert) throws Exception {
        Enumeration<String> enumeration = keystore.aliases();
        while (enumeration.hasMoreElements()) {
            String certAlias = enumeration.nextElement();
            X509Certificate checkCert = convertToX509Certificate(keystore.getCertificate(certAlias));
            if (checkCert.getSerialNumber().equals(cert.getSerialNumber())
                    && checkCert.getNotAfter().equals(cert.getNotAfter())
                    && checkCert.getNotBefore().equals(cert.getNotBefore())) {
                return (certAlias);
            }
        }
        return (null);
    }

    /**
     * Imports a X509 certificate into the passed keystore using a special
     * provider e.g. for the use of BouncyCastle Provider use the code Provider
     * provBC = Security.getProvider("BC");
     *
     * @param keystore Keystore to import the certificate to
     */
    public static String importX509Certificate(KeyStore keystore, X509Certificate certificate) throws Exception {
        //dont import the certificate if it already exists!
        if (getCertificateAlias(keystore, certificate) != null) {
            return (getCertificateAlias(keystore, certificate));
        }
        String alias = getProposalCertificateAliasForImport(certificate);
        alias = ensureUniqueAliasName(keystore, alias);
        keystore.setCertificateEntry(alias, certificate);
        return (alias);
    }

    /**
     * Checks that an alias for an import is unique in this keystore
     */
    public static String ensureUniqueAliasName(KeyStore keystore, String alias) throws Exception {
        int counter = 1;
        String newAlias = alias;
        //add a number to the alias if it already exists with this name
        while (keystore.containsAlias(newAlias)) {
            newAlias = alias + counter;
            counter++;
        }
        alias = newAlias;
        return (alias);
    }

    /**
     * Checks the principal of a certificate and returns the proposed alias name
     */
    public static String getProposalCertificateAliasForImport(X509Certificate cert) {
        X500Principal principal = cert.getSubjectX500Principal();
        StringTokenizer tokenizer = new StringTokenizer(principal.getName(X500Principal.RFC2253), ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.startsWith("CN=")) {
                return (token.substring(3));
            }
        }
        //fallback: return a common name. Please check if this alias exists before importing the certificate
        return ("certificate");
    }

    /**
     * Tries to read a certificate from a byte array, may return null if reading
     * the data fails
     *
     * @param securityProvider Might be null - then the default security
     * provider is taken as defined in the jre security settings. It's
     * recommended to pass "BC" here for the bouncycastle security provider
     */
    private static List<X509Certificate> readCertificates(byte[] data, String securityProvider) throws Exception {
        CertificateFactory certificateFactory;
        List<X509Certificate> certList = null;
        if (securityProvider != null) {
            certificateFactory = CertificateFactory.getInstance("X.509", securityProvider);
        } else {
            certificateFactory = CertificateFactory.getInstance("X.509");
        }
        //perform the PEM decode process first - this will simply fail if the passed certificate structure is not in PEM
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(data))) {
            PEMParser pemParser = new PEMParser(reader);
            //this will be null if the PEMParser could not successful extract an object
            PemObject pemObject = pemParser.readPemObject();
            if (pemObject != null) {
                data = pemObject.getContent();
            }
        }
        try {
            //try to read pkcs#7 files first - all other read methods will ignore certificates if there is stored more than one
            //cert in the p7b file
            try (InputStream dataIn = new ByteArrayInputStream(data)) {
                Collection<? extends Certificate> tempCertList
                        = certificateFactory.generateCertPath(dataIn, "PKCS7").getCertificates();
                if (tempCertList != null && !tempCertList.isEmpty()) {
                    certList = new ArrayList<X509Certificate>();
                    for (Certificate cert : tempCertList) {
                        certList.add((X509Certificate) cert);
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            if (certList == null) {
                try (InputStream dataIn = new ByteArrayInputStream(data)) {
                    Collection<? extends Certificate> tempCertList
                            = certificateFactory.generateCertificates(dataIn);
                    if (tempCertList != null && !tempCertList.isEmpty()) {
                        certList = new ArrayList<X509Certificate>();
                        for (Certificate cert : tempCertList) {
                            certList.add((X509Certificate) cert);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        //it is ok to return null if the certificate(s) are in unknown format
        return (certList);
    }

    /**
     * Reads a chain of certificates from the passed stream. If the read process
     * fails the method tries to unzip the data - expecting that the user passed
     * a .zip file that contains a certificate which happens quiet often
     */
    public static List<X509Certificate> readCertificates(InputStream certStream, String securityProvider) throws Exception {
        byte[] data = certStream.readAllBytes();
        List<X509Certificate> certList = readCertificates(data, securityProvider);
        if (certList == null) {
            //no success, perhaps base64 encoded data? Decode it and retry the read process
            byte[] decoded = Base64.decode(new String(data));
            certList = readCertificates(decoded, securityProvider);
        }
        if (certList != null) {
            return (certList);
        } else {
            //still no success - check if the user passed a zip archive to the read cert routine
            try (ByteArrayInputStream memIn = new ByteArrayInputStream(data)) {
                try (ZipInputStream zipIn = new ZipInputStream(memIn)) {
                    ZipEntry test = zipIn.getNextEntry();
                    if (test != null) {
                        throw new CertificateException(rb.getResourceString("readerror.zipcert"));
                    }
                } catch (CertificateException e) {
                    throw (e);
                } catch (Exception e) {
                    //ignore, was just a try
                }
            }
            throw new CertificateException(rb.getResourceString("readerror.invalidcert"));
        }
    }

    /**
     * Reads a certificate from a stream and returns it
     *
     * @deprecated Does not support files that contain a cert chain (e.g. *.p7b)
     */
    @Deprecated(since = "2020")
    public static X509Certificate readCertificate(InputStream certStream, Provider provider) throws CertificateException {
        CertificateFactory factory;
        X509Certificate cert = null;
        try {
            if (provider != null) {
                factory = CertificateFactory.getInstance("X.509", provider);
                cert = (X509Certificate) factory.generateCertificate(certStream);
            }
            //Let the default provider parsing the certificate
            if (provider == null || cert == null) {
                factory = CertificateFactory.getInstance("X.509");
                cert = (X509Certificate) factory.generateCertificate(certStream);
            }
            //still no success, perhaps PEM encoding? Start the PEM reader and see if it could read the cert
            if (cert == null) {
                try (Reader certStreamReader = new InputStreamReader(certStream)) {
                    PEMParser pemParser = new PEMParser(certStreamReader);
                    cert = (X509Certificate) pemParser.readObject();
                }
            }
        } catch (Exception e) {
            throw new CertificateException(rb.getResourceString("readerror.invalidcert") + " (" + e.getMessage() + ")");
        }
        if (cert != null) {
            return (cert);
        } else {
            throw new CertificateException(rb.getResourceString("readerror.invalidcert"));
        }
    }

    /**
     * Imports a X509 certificate into the passed keystore using a special
     * provider e.g. for the use of BouncyCastle Provider use the code Provider
     * provBC = Security.getProvider("BC");
     *
     * @param keystore Keystore to import the certificate to
     * @param certificateFilename filename to read the certificate from
     * @param alias Aslias to use in the keystore
     * * @param certIndex Its possible that there are more than a single
     * certificate in the passed stream (e.g. p7b). Just pass 0 if you are sure
     * that there is only a single certificate in the stream, else the index to
     * import
     */
    public static void importX509Certificate(KeyStore keystore, String certificateFilename,
            String alias, int certIndex, String securityProvider) throws Exception {
        try (InputStream inCert = Files.newInputStream(Paths.get(certificateFilename))) {
            importX509Certificate(keystore, inCert, alias, certIndex, securityProvider);
        }
    }

    /**
     * Imports a X509 certificate into the passed keystore
     *
     * @param keystore Keystore to import the certificate to
     * @param certificateFilename filename to read the certificate from
     * @param alias Alias to use in the keystore
     * * @param certIndex Its possible that there are more than a single
     * certificate in the passed stream (e.g. p7b). Just pass 0 if you are sure
     * that there is only a single certificate in the stream, else the index to
     * import
     */
    public static void importX509Certificate(KeyStore keystore, String certificateFilename,
            String alias, int certIndex) throws Exception {
        try (InputStream inStream = Files.newInputStream(Paths.get(certificateFilename))) {
            importX509Certificate(keystore, inStream, alias, certIndex, null);
        }
    }

    /**
     * Attempt to order the supplied array of X.509 certificates in issued to to
     * issued from order.
     *
     * @param certs The X.509 certificates to order
     * @return The ordered X.509 certificates
     */
    public static X509Certificate[] orderX509CertChain(X509Certificate[] certs) {
        int ordered = 0;
        X509Certificate[] tmpCerts = (X509Certificate[]) certs.clone();
        X509Certificate[] orderedCerts = new X509Certificate[certs.length];
        X509Certificate issuerCertificate = null;

        // Find the root issuer (ie certificate where issuer is the same
        // as subject)
        for (X509Certificate singleCertificate : tmpCerts) {
            if (singleCertificate.getIssuerX500Principal().equals(singleCertificate.getSubjectX500Principal())) {
                issuerCertificate = singleCertificate;
                orderedCerts[ordered] = issuerCertificate;
                ordered++;
            }
        }
        // Couldn't find a root issuer so just return the un-ordered array
        if (issuerCertificate == null) {
            return certs;
        }
        // Keep making passes through the array of certificates looking for the
        // next certificate in the chain until the links run out
        while (true) {
            boolean foundNext = false;
            for (X509Certificate singleCertificate : tmpCerts) {
                // Is this certificate the next in the chain?
                if (singleCertificate.getIssuerX500Principal().equals(issuerCertificate.getSubjectX500Principal())
                        && singleCertificate != issuerCertificate) {
                    // Yes
                    issuerCertificate = singleCertificate;
                    orderedCerts[ordered] = issuerCertificate;
                    ordered++;
                    foundNext = true;
                    break;
                }
            }
            if (!foundNext) {
                break;
            }
        }
        // Resize array
        tmpCerts = new X509Certificate[ordered];
        System.arraycopy(orderedCerts, 0, tmpCerts, 0, ordered);
        // Reverse the order of the array
        orderedCerts = new X509Certificate[ordered];
        for (int i = 0; i < ordered; i++) {
            orderedCerts[i] = tmpCerts[tmpCerts.length - 1 - i];
        }
        return orderedCerts;
    }

    /**
     * Exports an X.509 certificate from a passed keystore, encoding is PKCS7
     *
     * @returns the certificate
     */
    public static byte[] exportX509CertificatePKCS7(KeyStore keystore, String alias) throws Exception {
        X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);
        return (exportX509CertificatePKCS7(new X509Certificate[]{certificate}));
    }

    /**
     * Exports an X.509 certificate from a passed keystore, encoding is PKCS7
     *
     * @returns the certificate
     */
    public static byte[] exportX509CertificatePKCS7(X509Certificate[] certificates) throws Exception {
        byte[] certificate = convertX509CertificateToPKCS7(certificates);
        return (certificate);
    }

    /**
     * Converts a x.509 certificate to PEM format which is printable, BASE64
     * encoded.
     */
    public static String convertX509CertificateToPEM(X509Certificate certificate)
            throws CertificateEncodingException, IOException {
        return (convertCertificatesToPEM(List.of(certificate)));
    }

    /**
     * Converts a x.509 certificate to PEM format which is printable, BASE64
     * encoded.
     */
    public static String convertCertificatesToPEM(List<X509Certificate> certificates)
            throws CertificateEncodingException, IOException {
        try (StringWriter stringWriter = new StringWriter()) {
            try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
                for (X509Certificate certificate : certificates) {
                    X500Principal principal = certificate.getSubjectX500Principal();
                    X500Name x500Name = new X500Name(principal.getName());
                    RDN[] rdns = x500Name.getRDNs(BCStyle.CN);
                    List<String> names = new ArrayList<String>();
                    for (RDN rdn : rdns) {
                        String name = IETFUtils.valueToString(rdn.getFirst().getValue());
                        names.add(name);
                    }
                    if (!names.isEmpty()) {
                        pemWriter.write("# Bag Attributes:" + System.lineSeparator());
                        pemWriter.write("#    subject: "
                                + names.get(0)
                                + System.lineSeparator());
                    }
                    pemWriter.writeObject(certificate);
                }
            }
            return (stringWriter.toString());
        }
    }

    /**
     * Converts the passed certificate to an X509 certificate. Mainly it is
     * already in this format.
     */
    public static X509Certificate convertToX509Certificate(Certificate certificate)
            throws CertificateException, IOException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate cert;
        try (ByteArrayInputStream inStream = new ByteArrayInputStream(certificate.getEncoded())) {
            cert = (X509Certificate) factory.generateCertificate(inStream);
        }
        return (cert);
    }

    /**
     * Converts an array x.509 certificate to pkcs#7 format
     */
    public static byte[] convertX509CertificateToPKCS7(X509Certificate[] certificates) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
        List<Certificate> certList = new ArrayList<Certificate>();
        certList.addAll(Arrays.asList(certificates));
        CertPath certPath = factory.generateCertPath(certList);
        return (certPath.getEncoded("PKCS7"));
    }

    /**
     * Exports an X.509 certificate from a passed keystore, encoding is "DER",
     * "PEM", "PKCS7"
     *
     * @returns the certificate
     */
    public static byte[] exportX509Certificate(KeyStore keystore, String alias, String encoding) throws Exception {
        if (keystore.isKeyEntry(alias)) {
            Certificate[] certificates = keystore.getCertificateChain(alias);            
            X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
            for (int i = 0; i < certificates.length; i++) {
                x509Certificates[i] = convertToX509Certificate(certificates[i]);
            }            
            x509Certificates = orderX509CertChain(x509Certificates);
            X509Certificate singleCertificate = x509Certificates[0];
            //write certificate to file
            if (encoding.equals("DER")) {
                byte[] encoded = singleCertificate.getEncoded();
                return (encoded);
            } else if (encoding.equals("PEM")) {
                return (convertX509CertificateToPEM(singleCertificate).getBytes());
            } else if (encoding.equals("PKCS7")) {
                return (convertX509CertificateToPKCS7(x509Certificates));
            } else {
                throw new IllegalArgumentException("exportX509Certificate: Unsupported encoding " + encoding);
            }
        } else if (keystore.isCertificateEntry(alias)) {
            Certificate certificate = keystore.getCertificate(alias);
            X509Certificate x509Certificate = convertToX509Certificate(certificate);
            //write certificate to byte array
            if (encoding.equals("DER")) {
                byte[] encoded = x509Certificate.getEncoded();
                return (encoded);
            } else if (encoding.equals("PEM")) {
                String encoded = convertX509CertificateToPEM(x509Certificate);
                return (encoded.getBytes());
            } else if (encoding.equals("PKCS7")) {
                return (convertX509CertificateToPKCS7(new X509Certificate[]{x509Certificate}));
            } else {
                throw new IllegalArgumentException("exportX509Certificate: Unsupported encoding " + encoding);
            }
        } else {
            throw new IllegalArgumentException("exportX509Certificate: The alias " + alias + " has not been found in the keystore");
        }
    }

    /**
     * Exports an X.509 certificate from a passed keystore, encoding is ASN.1
     * DER
     *
     * @returns the certificate
     */
    public static byte[] exportX509CertificateDER(KeyStore keystore, String alias) throws Exception {
        byte[] certificate = exportX509Certificate(keystore, alias, "DER");
        return (certificate);
    }

    /**
     * Exports a public key as PEM in SSH2 format
     *
     * @returns the certificate
     */
    public static byte[] exportPublicKeySSH2(PublicKey key) throws Exception {
        String certificateEncoded = convertPublicKeyToSSH2(key);
        //no risk of encoding problem, its PEM
        return (certificateEncoded.getBytes());
    }

    /**
     * Exports an X.509 certificate from a passed keystore, encoding is PEM
     *
     * @returns the certificate
     */
    public static byte[] exportX509CertificatePEM(KeyStore keystore, String alias) throws Exception {
        byte[] certificate = exportX509Certificate(keystore, alias, "PEM");
        return (certificate);
    }

    /**
     * Extracts the private key from a passed keystore and stores it in ASN.1
     * encoding as defined in the PKCS#8 standard
     *
     * @param keystore keystore that contains the private key
     * @param keystorePass Password for the keystore
     * @param alias Alias the keystore holds the private key with
     */
    public static void extractPrivateKeyToPKCS8(KeyStore keystore, char[] keystorePass, String alias, Path outFile)
            throws Exception {
        if (!keystore.isKeyEntry(alias)) {
            throw new Exception(rb.getResourceString("privatekey.notfound", alias));
        }
        Key privateKey = keystore.getKey(alias, keystorePass);
        if (privateKey != null) {
            PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            try (OutputStream os = Files.newOutputStream(outFile)) {
                os.write(pkcs8.getEncoded());
            }
        }
    }

    /**
     * Converts the passed public key as PEM file in SSH2 format, this is RFC
     * RFC4251
     */
    public static String convertPublicKeyToSSH2(PublicKey publicKey) throws Exception {
        String publicKeyEncoded;
        if (publicKey.getAlgorithm().equals("RSA")) {
            RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
            try (ByteArrayOutputStream memOutStream = new ByteArrayOutputStream()) {
                try (DataOutputStream dataOutStream = new DataOutputStream(memOutStream)) {
                    dataOutStream.writeInt("ssh-rsa".getBytes().length);
                    dataOutStream.write("ssh-rsa".getBytes());
                    dataOutStream.writeInt(rsaPublicKey.getPublicExponent().toByteArray().length);
                    dataOutStream.write(rsaPublicKey.getPublicExponent().toByteArray());
                    dataOutStream.writeInt(rsaPublicKey.getModulus().toByteArray().length);
                    dataOutStream.write(rsaPublicKey.getModulus().toByteArray());
                    //encode without any line separator!
                    publicKeyEncoded = java.util.Base64.getEncoder().encodeToString(memOutStream.toByteArray());
                    return "ssh-rsa " + publicKeyEncoded;
                }
            }
        } else if (publicKey.getAlgorithm().equals("DSA")) {
            DSAPublicKey dsaPublicKey = (DSAPublicKey) publicKey;
            DSAParams dsaParams = dsaPublicKey.getParams();
            try (ByteArrayOutputStream memOutStream = new ByteArrayOutputStream()) {
                try (DataOutputStream dataOutStream = new DataOutputStream(memOutStream)) {
                    dataOutStream.writeInt("ssh-dss".getBytes().length);
                    dataOutStream.write("ssh-dss".getBytes());
                    dataOutStream.writeInt(dsaParams.getP().toByteArray().length);
                    dataOutStream.write(dsaParams.getP().toByteArray());
                    dataOutStream.writeInt(dsaParams.getQ().toByteArray().length);
                    dataOutStream.write(dsaParams.getQ().toByteArray());
                    dataOutStream.writeInt(dsaParams.getG().toByteArray().length);
                    dataOutStream.write(dsaParams.getG().toByteArray());
                    dataOutStream.writeInt(dsaPublicKey.getY().toByteArray().length);
                    dataOutStream.write(dsaPublicKey.getY().toByteArray());
                    publicKeyEncoded = java.util.Base64.getEncoder().encodeToString(memOutStream.toByteArray());
                    return "ssh-dss " + publicKeyEncoded;
                }
            }
        } else {
            throw new IllegalArgumentException(
                    rb.getResourceString("ssh2.algorithmn.not.supported", publicKey.getAlgorithm()));
        }
    }

    /**
     * Returns a map that contains all certificates of the passed keystore
     */
    public static Map<String, Certificate> getCertificatesFromKeystore(KeyStore keystore) throws GeneralSecurityException {
        Map<String, Certificate> certMap = new HashMap<String, Certificate>();
        Enumeration<String> enumeration = keystore.aliases();
        while (enumeration.hasMoreElements()) {
            String certAlias = enumeration.nextElement();
            certMap.put(certAlias, keystore.getCertificate(certAlias));
        }
        return (certMap);
    }

    /**
     * Returns a single certificate by its alias from a keystore.
     *
     * @param alias The alias to look for
     * @param keystore
     * @return null if the alias does not exist
     * @throws GeneralSecurityException
     */
    public static X509Certificate getCertificate(KeyStore keystore, String alias) throws Exception {
        Certificate foundCertificate = keystore.getCertificate(alias);
        if (foundCertificate != null) {
            return (convertToX509Certificate(foundCertificate));
        } else {
            return (null);
        }
    }

    /**
     * Returns a list of aliases for a specified keystore
     */
    public static List<String> getKeyAliases(KeyStore keystore) throws KeyStoreException {
        Enumeration<String> enumeration = keystore.aliases();
        List<String> keyList = new ArrayList<String>();
        while (enumeration.hasMoreElements()) {
            String alias = (String) enumeration.nextElement();
            if (keystore.isKeyEntry(alias)) {
                keyList.add(alias);
            }
        }
        return (keyList);
    }

    /**
     * Returns a list of aliases for a specified keystore, vector of string
     * because this may be used for GUI lists
     */
    public static List<String> getNonKeyAliases(KeyStore keystore) throws KeyStoreException {
        Enumeration<String> enumeration = keystore.aliases();
        List<String> nonkeyList = new ArrayList<String>();
        while (enumeration.hasMoreElements()) {
            String alias = (String) enumeration.nextElement();
            if (!keystore.isKeyEntry(alias)) {
                nonkeyList.add(alias);
            }
        }
        return (nonkeyList);
    }

    public static String generateFingerprintSHA1(Certificate certificate) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.update(certificate.getEncoded());
        byte[] fingerPrintBytes = digest.digest();
        return (byteArrayToHexStr(fingerPrintBytes));
    }

    private static String byteArrayToHexStr(byte[] byteArray) {
        StringBuilder hextStringBuilder = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if (i > 0) {
                hextStringBuilder.append(":");
            }
            String singleByte = Integer.toHexString(byteArray[i] & 0xFF).toUpperCase();
            if (singleByte.isEmpty()) {
                hextStringBuilder.append("00");
            } else if (singleByte.length() == 1) {
                hextStringBuilder.append("0");
            }
            hextStringBuilder.append(singleByte);
        }
        return hextStringBuilder.toString();
    }

}
