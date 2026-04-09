package de.mendelson.util.security.cert;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mendelson.util.security.Base64;
import de.mendelson.util.security.BouncyCastleProviderSingleton;
import de.mendelson.util.security.keygeneration.KeyGenerator;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.pqc.jcajce.provider.dilithium.BCDilithiumPublicKey;
import org.bouncycastle.pqc.jcajce.provider.sphincsplus.BCSPHINCSPlusPublicKey;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Object that stores a single configuration certificate/key
 *
 * @author S.Heller
 * @version $Revision: 65 $
 */
public class KeystoreCertificate implements Comparable<KeystoreCertificate>, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    public static final String CERTIFICATE_FORMAT_PEM = "PEM";
    public static final String CERTIFICATE_FORMAT_PEM_CHAIN = "PEM_CHAIN";
    public static final String CERTIFICATE_FORMAT_DER = "DER";
    public static final String CERTIFICATE_FORMAT_PKCS7 = "PKCS#7";
    public static final String CERTIFICATE_FORMAT_SSH2 = "SSH2";
    private String alias = "";
    private X509Certificate certificate = null;
    /**
     * Private of public key
     */
    private Key privateKey = null;
    private boolean isKeyPair = false;
    private String infoText = "";
    //cache some data
    private byte[] fingerprintSHA1Bytes = null;
    private String fingerprintSHA1 = null;
    private Certificate[] certificateChain = null;

    private final static String[] KEY_USAGE_NAMES = {
        "Digital signature",
        "Non repudiation",
        "Key encipherment",
        "Data encipherment",
        "Key agreement",
        "Key certificate signing",
        "CRL signing",
        "Encipher",
        "Decipher"
    };

    private static final Map<String, String> EXTENSION_OID_MAP = new HashMap<String, String>();

    static {
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.1", "Webserver authentication");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.2", "Client authentication");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.3", "Code signing");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.4", "Email protection");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.5", "IPSec end system");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.6", "IPSec tunnel");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.7", "IPSec user");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.8", "Timestamping");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.9", "OCSP Signing");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.10", "Microsoft Smart Card Logon");
        EXTENSION_OID_MAP.put("1.3.6.1.5.5.7.3.11", "Key Recovery");
        EXTENSION_OID_MAP.put("2.16.840.1.113733.1.8.1", "Verisign Server Gated Crypto");
        //Netscape extended key usages
        EXTENSION_OID_MAP.put("2.16.840.1.113730.4.1", "Netscape Server Gated Crypto");
        EXTENSION_OID_MAP.put("2.16.840.1.113730.1.2", "Netscape base URL");
        EXTENSION_OID_MAP.put("2.16.840.1.113730.1.8", "Netscape CA policy URL");
        EXTENSION_OID_MAP.put("2.16.840.1.113730.1.4", "Netscape CA revocation URL");
        EXTENSION_OID_MAP.put("2.16.840.1.113730.1.7", "Netscape cert renewal URL");
        EXTENSION_OID_MAP.put("2.16.840.1.113730.2.5", "Netscape cert sequence");
        EXTENSION_OID_MAP.put("2.16.840.1.113730.1.1", "Netscape cert type");
        EXTENSION_OID_MAP.put("2.16.840.1.113730.1.13", "Netscape comment");
        EXTENSION_OID_MAP.put("2.16.840.1.113730.1.3", "Netscape revocation URL");
        EXTENSION_OID_MAP.put("2.16.840.1.113730.1.12", "Netscape SSL server name");
        //MS extended key usages
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.10.3.3", "Microsoft Server Gated Crypto");
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.20.2.2", "Smart card logon");
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.10.3.4", "Encrypting filesystem");
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.10.3.12", "Document signing");
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.21.5", "CA encryption certificate");
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.10.3.1", "Microsoft trust list signing");
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.10.3.4.1", "File recovery");
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.10.3.11", "Key recovery");
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.10.3.10", "Qualified subordination");
        EXTENSION_OID_MAP.put("1.3.6.1.4.1.311.10.3.9", "Root list signer");
    }

    /**
     * The SHA-1 fingerprints of the public available mendelson test keys
     */
    public static final String[] TEST_KEYS_FINGERPRINTS_SHA1 = new String[]{
        "6D:9A:2C:79:02:0B:F1:6B:20:78:E4:A3:BE:DF:93:DD:2A:AD:B7:40", //key2
        "3D:A0:27:42:4D:92:6D:04:BB:74:66:1D:48:3E:61:6A:46:2A:05:B7", //key1
        "08:FF:33:83:DF:8B:2F:9F:40:BB:F7:88:FE:FD:9C:15:40:E4:FE:C6", //key4
        "DC:99:5A:83:60:A4:37:C4:30:3B:10:AC:31:4E:D9:21:16:61:36:77" //key3  
    };

    public KeystoreCertificate() {
    }

    /**
     * Clone this object
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            KeystoreCertificate clonedEntry = (KeystoreCertificate) super.clone();
            return (clonedEntry);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return (null);
        }
    }

    /**
     * Returns the extension value "extended key usage", OID 2.5.29.37
     *
     */
    public List<String> getExtendedKeyUsage() {
        List<String> extendedKeyUsage = new ArrayList<String>();
        try {
            List<String> oidList = this.certificate.getExtendedKeyUsage();
            for (String oid : oidList) {
                if (EXTENSION_OID_MAP.containsKey(oid)) {
                    extendedKeyUsage.add(EXTENSION_OID_MAP.get(oid));
                } else {
                    extendedKeyUsage.add(oid);
                }
            }
        } catch (Exception e) {
        }
        return (extendedKeyUsage);
    }

    /**
     * OID 2.5.29.35 - Authority Key Identifier This extension may be used
     * either as a certificate or CRL extension. It identifies the public key to
     * be used to verify the signature on this certificate or CRL. It enables
     * distinct keys used by the same CA to be distinguished (e.g., as key
     * updating occurs).
     *
     * @return
     */
    public List<String> getAuthorityKeyIdentifier() {
        List<String> authorityKeyIdentifierList = new ArrayList<String>();
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.35");
        if (extensionValue == null) {
            //there is no such extension: return empty list
            return (authorityKeyIdentifierList);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            ASN1Sequence asn1Sequence = (ASN1Sequence) ASN1Primitive.fromByteArray(octedBytes);
            for (int i = 0, len = asn1Sequence.size(); i < len; i++) {
                if (asn1Sequence.getObjectAt(i) instanceof DERTaggedObject) {
                    DERTaggedObject derTagObj = (DERTaggedObject) asn1Sequence.getObjectAt(i);
                    if (derTagObj.getTagNo() == 0) {
                        DEROctetString octetStr = (DEROctetString) derTagObj.getLoadedObject();
                        byte[] identifier = octetStr.getOctets();
                        authorityKeyIdentifierList.add("[Key identifier] " + byteArrayToHexStr(identifier));
                    } else if (derTagObj.getTagNo() == 2) {
                        DEROctetString octetStr = (DEROctetString) derTagObj.getLoadedObject();
                        byte[] identifier = octetStr.getOctets();
                        authorityKeyIdentifierList.add("[Serial] " + byteArrayToHexStr(identifier));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (authorityKeyIdentifierList);
    }

    /**
     * OID 2.5.29.14 - Subject Key Identifier This extension identifies the
     * public key being certified. It enables distinct keys used by the same
     * subject to be differentiated (e.g., as key updating occurs).
     *
     * @return
     */
    public List<String> getSubjectKeyIdentifier() {
        List<String> subjectKeyIdentifierList = new ArrayList<String>();
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.14");
        if (extensionValue == null) {
            //there is no such extension: return empty list
            return (subjectKeyIdentifierList);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            DEROctetString octetStr = (DEROctetString) ASN1Primitive.fromByteArray(octedBytes);
            byte[] identifier = octetStr.getOctets();
            subjectKeyIdentifierList.add(byteArrayToHexStr(identifier));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (subjectKeyIdentifierList);
    }

    public List<String> getPolicy() {
        List<String> policyList = new ArrayList<String>();
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.32");
        if (extensionValue == null) {
            //there is no such extension: return empty list
            return (policyList);
        }
        try {
            byte[] octetBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            ASN1Primitive parsedData = ASN1Primitive.fromByteArray(octetBytes);
            CertificatePolicies policies = CertificatePolicies.getInstance(parsedData);
            for (PolicyInformation policyInfo : policies.getPolicyInformation()) {
                if (policyInfo.getPolicyQualifiers() != null) {
                    ASN1Sequence policyQualifier = (ASN1Sequence) policyInfo.getPolicyQualifiers().getObjectAt(0);
                    policyList.add(policyQualifier.getObjectAt(1).toString());
                }
            }
        } catch (Exception e) {
        }
        return (policyList);
    }

    /**
     * Returns the key usages of this cert, OID 2.5.29.15
     */
    public List<String> getKeyUsages() {
        List<String> keyUsages = new ArrayList<String>();
        boolean[] keyUsage = this.certificate.getKeyUsage();
        try {
            if (keyUsage != null) {
                for (int i = 0; i < keyUsage.length; i++) {
                    if (keyUsage[i]) {
                        keyUsages.add(KEY_USAGE_NAMES[i]);
                    }
                }
            }
        } catch (Exception e) {
        }
        return (keyUsages);
    }

    /**
     * In fact whenever we say key we mean a pair of numbers comprising the key;
     * a key number to use in the raising of powers and another number that is
     * the modulus of the arithmetic to be used for the work.
     *
     * @return
     */
    public BigInteger getModulus() {
        PublicKey publicKey = this.certificate.getPublicKey();
        if (publicKey instanceof RSAPublicKey) {
            RSAPublicKey rsaKey = (RSAPublicKey) publicKey;
            return (rsaKey.getModulus());
        }
        return (BigInteger.ZERO);
    }

    /**
     * In fact whenever we say key we mean a pair of numbers comprising the key;
     * a key number to use in the raising of powers and another number that is
     * the modulus of the arithmetic to be used for the work.
     *
     * @return
     */
    public BigInteger getPublicExponent() {
        PublicKey publicKey = this.certificate.getPublicKey();
        if (publicKey instanceof RSAPublicKey) {
            RSAPublicKey rsaKey = (RSAPublicKey) publicKey;
            return (rsaKey.getPublicExponent());
        }
        return (BigInteger.ZERO);
    }

    /**
     * Returns the subject alternative name of this cert, OID 2.5.29.17
     */
    public List<String> getSubjectAlternativeNames() {
        List<String> alternativeNames = new ArrayList<String>();
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.17");
        if (extensionValue == null) {
            return (alternativeNames);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            GeneralName[] names = (GeneralNames.getInstance(ASN1Primitive.fromByteArray(octedBytes))).getNames();
            for (GeneralName name : names) {
                ASN1Encodable encodable = name.getName();

                //IP addresses are sometimes stored as DEROctetString which would result in a single hex value on display
                // - this has to be decoded for the display
                if (encodable instanceof DEROctetString && name.getTagNo() == GeneralName.iPAddress) {
                    DEROctetString str = (DEROctetString) encodable;
                    StringBuilder decStr = new StringBuilder();
                    byte[] octets = str.getOctets();
                    for (byte octet : octets) {
                        if (decStr.length() > 0) {
                            decStr.append(".");
                        }
                        decStr.append((int) (octet & 0xFF));
                    }
                    alternativeNames.add(decStr + " (" + generalNameTagNoToString(name) + ")");
                } else {
                    alternativeNames.add(((ASN1Encodable) name.getName()).toString()
                            + " (" + generalNameTagNoToString(name) + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (alternativeNames);
    }

    /**
     * Converts the tag no of a general name to a human readable value
     */
    public static final String generalNameTagNoToString(int tagNo) {
        if (tagNo == GeneralName.dNSName) {
            return ("DNS");
        }
        if (tagNo == GeneralName.directoryName) {
            return ("Directory");
        }
        if (tagNo == GeneralName.ediPartyName) {
            return ("EDI party");
        }
        if (tagNo == GeneralName.iPAddress) {
            return ("IP");
        }
        if (tagNo == GeneralName.otherName) {
            return ("Other name");
        }
        if (tagNo == GeneralName.registeredID) {
            return ("Registered ID");
        }
        if (tagNo == GeneralName.rfc822Name) {
            return ("Mail (RFC822)");
        }
        if (tagNo == GeneralName.uniformResourceIdentifier) {
            return ("URI");
        }
        if (tagNo == GeneralName.x400Address) {
            return ("x.400");
        }
        return ("");
    }

    /**
     * Converts the tag no of a general name to a human readable value
     */
    public static final String generalNameTagNoToString(GeneralName name) {
        return (generalNameTagNoToString(name.getTagNo()));
    }

    /**
     * Get extension values for CRL Distribution Points as a string list or an
     * empty list if an exception occured or the extension doesnt exist OID
     * 2.5.29.31
     */
    public List<String> getCrlDistributionURLs() {
        List<String> ulrList = new ArrayList<String>();
        //CRL destribution points has OID 2.5.29.31
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.31");
        if (extensionValue == null) {
            return (ulrList);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            CRLDistPoint distPoint = CRLDistPoint.getInstance(ASN1Primitive.fromByteArray(octedBytes));
            DistributionPoint[] points = distPoint.getDistributionPoints();
            for (DistributionPoint point : points) {
                DistributionPointName distributionPointName = point.getDistributionPoint();
                if (distributionPointName != null) {
                    if (distributionPointName.getType() == DistributionPointName.FULL_NAME) {
                        GeneralNames generalNames = (GeneralNames) distributionPointName.getName();
                        for (GeneralName generalName : generalNames.getNames()) {
                            //generalName.getTagNo() is GeneralName.uniformResourceIdentifier in this case
                            ulrList.add(((ASN1String) generalName.getName()).getString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            //nop
        }
        return (ulrList);
    }

    /**
     * Returns the enwrapped certificate version
     */
    public int getVersion() {
        return (this.certificate.getVersion());
    }

    public String getSigAlgName() {
        return (this.certificate.getSigAlgName());
    }

    public String getSigAlgOID() {
        return (this.certificate.getSigAlgOID());
    }

    public String getPublicKeyAlgorithm() {
        PublicKey publicKey = this.certificate.getPublicKey();
        return (publicKey.getAlgorithm());
    }

    /**
     * Valid date start
     */
    public Date getNotBefore() {
        return (this.certificate.getNotBefore());
    }

    /**
     * Valid date end
     */
    public Date getNotAfter() {
        return (this.certificate.getNotAfter());
    }

    /**
     * Returns a String that contains just the parts "CN", "O", "OU", "C", "ST",
     * "L", "E" of the subject - in this order
     *
     * @return
     */
    public String getSubjectDN() {
        X500Principal subjectPrincipal = certificate.getSubjectX500Principal();
        String fullSubject = subjectPrincipal.getName();
        final String[] displayList = new String[]{
            "CN", "O", "OU", "C", "ST", "L", "E"
        };
        StringBuilder subjectBuilder = new StringBuilder();
        try {
            LdapName subjectLdapName = new LdapName(fullSubject);
            for (String displayType : displayList) {
                for (Rdn rdn : subjectLdapName.getRdns()) {
                    if (rdn.getType().equalsIgnoreCase(displayType)) {
                        if (subjectBuilder.length() > 0) {
                            subjectBuilder.append(",");
                        }
                        subjectBuilder.append(displayType)
                                .append("=")
                                .append(this.escapeRDNValue(rdn.getValue().toString()));
                    }
                }
            }
        } catch (Exception e) {
            return (fullSubject);
        }
        return (subjectBuilder.toString());
    }

    public X500Principal getSubjectX500Principal() {
        return (this.certificate.getSubjectX500Principal());
    }

    public X500Principal getIssuerX500Principal() {
        return (this.certificate.getIssuerX500Principal());
    }

    /**
     * Returns a String that contains just the parts "CN", "O", "OU", "C", "ST",
     * "L", "E" of the issuer - in this order
     *
     * @return The issuer as String - escaped if this is required by the content
     */
    public String getIssuerDN() {
        X500Principal issuerPrincipal = certificate.getIssuerX500Principal();
        String fullIssuer = issuerPrincipal.getName();
        final String[] displayList = new String[]{
            "CN", "O", "OU", "C", "ST", "L", "E"
        };
        StringBuilder issuerBuilder = new StringBuilder();
        try {
            LdapName issuerLdapName = new LdapName(fullIssuer);
            for (String displayType : displayList) {
                for (Rdn rdn : issuerLdapName.getRdns()) {
                    if (rdn.getType().equalsIgnoreCase(displayType)) {
                        if (issuerBuilder.length() > 0) {
                            issuerBuilder.append(",");
                        }
                        issuerBuilder.append(displayType)
                                .append("=")
                                .append(this.escapeRDNValue(rdn.getValue().toString()));
                    }
                }
            }
        } catch (Exception e) {
            return (fullIssuer);
        }
        return (issuerBuilder.toString());
    }

    /**
     * If an issuer is requested as String there are several characters that
     * need to be escaped, e.g. ","
     *
     * @param rdnValue
     * @return
     */
    private String escapeRDNValue(String rdnValue) {
        if (rdnValue == null || rdnValue.isEmpty()) {
            return rdnValue;
        }
        final String SPECIAL_X500_CHARACTERS = ",=+<>#;\"\\";
        StringBuilder escapedValue = new StringBuilder();
        for (int i = 0; i < rdnValue.length(); i++) {
            char foundChar = rdnValue.charAt(i);
            if (SPECIAL_X500_CHARACTERS.contains(String.valueOf(foundChar))) {
                escapedValue.append("\\");
            }
            escapedValue.append(foundChar);
        }
        return (escapedValue.toString());
    }

    /**
     * Returns the serial number as decimal
     */
    public String getSerialNumberDEC() {
        return (this.certificate.getSerialNumber().toString());
    }

    /**
     * Returns the serial number as decimal
     */
    public String getSerialNumberHEX() {
        return (this.certificate.getSerialNumber().toString(16).toUpperCase());
    }

    public void setAlias(String alias) {
        if (alias == null) {
            alias = "";
        }
        this.alias = alias;
    }
    
    public void setCertificate(X509Certificate certificate, Certificate[] certificateChain) {
        this.certificate = certificate;
        this.certificateChain = certificateChain;
        this.computeInfoText();
    }

    /**
     * @deprecated (This ment the private key - please use setPrivateKey
     * instead)
     */
    @Deprecated(since = "2024")
    public void setKey(Key privateKey) {
        this.setPrivateKey(privateKey);
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }

    public void setIsKeyPair(boolean isKeyPair) {
        this.isKeyPair = isKeyPair;
    }

    public X509Certificate getX509Certificate() {
        return (this.certificate);
    }

    public boolean getIsKeyPair() {
        return (this.isKeyPair);
    }

    /**
     * Returns the private key of the entry - or null if it is not set
     *
     * @deprecated (This ment the private key - please use getPrivateKey
     * instead)
     */
    @Deprecated(since = "2024")
    public Key getKey() {
        return (this.getPrivateKey());
    }

    /**
     * Returns the private key of the entry - or null if it is not set
     */
    public Key getPrivateKey() {
        return (this.privateKey);
    }

    /**
     * Returns the public key of the entry
     */
    public PublicKey getPublicKey() {
        return (this.certificate.getPublicKey());
    }

    /**
     * KeyUsage extension, (OID = 2.5.29.15). The key usage extension defines
     * the purpose (e.g., encipherment, signature, certificate signing) of the
     * key contained in the certificate. The ASN.1 definition for this is:
     *
     * KeyUsage ::= BIT STRING { digitalSignature (0), nonRepudiation (1),
     * keyEncipherment (2), dataEncipherment (3), keyAgreement (4), keyCertSign
     * (5), --> true ONLY for CAs cRLSign (6), encipherOnly (7), decipherOnly
     * (8) }
     *
     * @return
     */
    public boolean isCACertificate() {
        boolean[] keyUsage = this.certificate.getKeyUsage();
        if (keyUsage != null) {
            return (keyUsage[5]);
        } else {
            return (false);
        }
    }

    public boolean isEndUserCertificate() {
        return (!this.isCACertificate());
    }

    /**
     * This method seems not to be reliable for all certificates - sometimes the
     * Root certificates could not be identified using it. The method
     * isCACertificate works for sure - but does only identify if a certificate
     * is an end user certificate or not.
     *
     * @return
     */
    public boolean isRootCertificate() {
        return (this.isSelfSigned() && this.certificate.getBasicConstraints() != -1);
    }

    public boolean isSelfSigned() {
        X500Principal subject = this.certificate.getSubjectX500Principal();
        X500Principal issuer = this.certificate.getIssuerX500Principal();
        return (subject.equals(issuer));
    }

    public String getAlias() {
        return (this.alias);
    }

    /**
     * Returns the key length of the passed public key. It is first analyzed if
     * this is a EC key, RSA etc..
     *
     * @param publicKey
     * @return
     */
    private int getPublicKeyLength(PublicKey publicKey) {
        if (publicKey instanceof RSAPublicKey) {
            RSAPublicKey rsaKey = (RSAPublicKey) publicKey;
            return (rsaKey.getModulus().bitLength());
        } else if (publicKey instanceof DSAPublicKey) {
            DSAPublicKey dsaKey = (DSAPublicKey) publicKey;
            return (dsaKey.getParams().getP().bitLength());
        } else if (publicKey instanceof ECPublicKey) {
            ECPublicKey ecKey = (ECPublicKey) publicKey;
            return (ecKey.getParams().getOrder().bitLength());
        } else if (publicKey instanceof BCEdDSAPublicKey) {
            BCEdDSAPublicKey edDSAPublicKey = (BCEdDSAPublicKey) publicKey;
            if (edDSAPublicKey.getAlgorithm().equals(KeyGenerator.CURVE_NAME_ED25519)) {
                return (Ed25519.PUBLIC_KEY_SIZE * 8);
            } else {
                return (0);
            }
        } else if (publicKey instanceof BCDilithiumPublicKey) {
            BCDilithiumPublicKey dilithiumPublicKey = (BCDilithiumPublicKey) publicKey;
            String specName = dilithiumPublicKey.getParameterSpec().getName();
            if (specName.equals(DilithiumParameterSpec.dilithium2.getName())) {
                return (1312);
            } else if (specName.equals(DilithiumParameterSpec.dilithium3.getName())) {
                return (1952);
            } else if (specName.equals(DilithiumParameterSpec.dilithium5.getName())) {
                return (2592);
            }
        } else if (publicKey instanceof BCSPHINCSPlusPublicKey) {
            BCSPHINCSPlusPublicKey sphincsplusPublicKey = (BCSPHINCSPlusPublicKey) publicKey;
            String specName = sphincsplusPublicKey.getParameterSpec().getName();
            if (specName.contains("128")) {
                return (32);
            } else if (specName.contains("192")) {
                return (48);
            } else if (specName.contains("256")) {
                return (64);
            }
        }
        return (0);
    }

    /**
     * If the public key could not be obtained by unknown reason this will
     * return 0
     *
     * @return
     */
    public int getPublicKeyLength() {
        PublicKey publicKey = this.certificate.getPublicKey();
        return (this.getPublicKeyLength(publicKey));
    }

    public byte[] getFingerPrintBytesSHA1() {
        if (this.fingerprintSHA1Bytes == null) {
            this.fingerprintSHA1Bytes = this.getFingerPrintBytes("SHA1");
        }
        return (this.fingerprintSHA1Bytes);
    }

    public byte[] getFingerPrintBytesMD5() {
        return (this.getFingerPrintBytes("MD5"));
    }

    public byte[] getFingerPrintBytesSHA256() {
        return (this.getFingerPrintBytes("SHA-256"));
    }

    public String getFingerPrintSHA1() {
        if (this.fingerprintSHA1 == null) {
            this.fingerprintSHA1 = this.getFingerPrint("SHA1");
        }
        return (this.fingerprintSHA1);
    }

    public String getFingerPrintMD5() {
        return (this.getFingerPrint("MD5"));
    }

    public String getFingerPrintSHA256() {
        return (this.getFingerPrint("SHA-256"));
    }

    /**
     * Deserializes a fingerprint string to a byte array It is assumed that the
     * fingerprint string has the format hex:hex:hex
     */
    public static byte[] fingerprintStrToBytes(String fingerprintStr) {
        if (fingerprintStr == null || !fingerprintStr.contains(":")) {
            throw new IllegalArgumentException("KeystoreCertificate.fingerprintStrToBytes: The certificate fingerprint \"" + fingerprintStr + "\" is not a valid fingerprint");
        }
        String[] token = fingerprintStr.split(":");
        byte[] bytes = new byte[token.length];
        for (int i = 0; i < token.length; i++) {
            while (token[i].length() < 2) {
                token[i] = "0" + token[i];
            }
            bytes[i] = fromHexString(token[i])[0];
        }
        return (bytes);
    }

    private static byte[] fromHexString(final String encoded) {
        if ((encoded.length() % 2) != 0) {
            throw new IllegalArgumentException("KeystoreCertificate.fromHexString: Input string must contain an even number of characters");
        }
        final byte[] result = new byte[encoded.length() / 2];
        final char[] enc = encoded.toCharArray();
        try {
            for (int i = 0; i < enc.length; i += 2) {
                StringBuilder curr = new StringBuilder(2);
                curr.append(enc[i]).append(enc[i + 1]);
                result[i / 2] = (byte) Integer.parseInt(curr.toString(), 16);
            }
            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("KeystoreCertificate.fromHexString: Input string must contain hex values, found: \"" + encoded + "\"");
        }
    }

    public static String byteArrayToHexStr(byte[] byteArray) {
        StringBuilder hextStringBuffer = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if (i > 0) {
                hextStringBuffer.append(":");
            }
            String singleByte = Integer.toHexString(byteArray[i] & 0xFF).toUpperCase();
            if (singleByte.isEmpty()) {
                hextStringBuffer.append("00");
            } else if (singleByte.length() == 1) {
                hextStringBuffer.append("0");
            }
            hextStringBuffer.append(singleByte);
        }
        return hextStringBuffer.toString();
    }

    /**
     * Serializes a fingerprint string from a byte array to a String It is
     * assumed that the fingerprint string has the format hex:hex:hex
     */
    public static String fingerprintBytesToStr(byte[] fingerprintBytes) {
        return (byteArrayToHexStr(fingerprintBytes));
    }

    /**
     * @param digest to create the hash value, please use SHA1 or MD5 only
     *
     */
    private byte[] getFingerPrintBytes(String digest) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(digest);
            return (messageDigest.digest(this.certificate.getEncoded()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns a fingerprint string that returns the fingerprint using the
     * format n:n:n
     *
     * @param digest to create the hash value, e.g. "SHA1", "MD5", "SHA-256"
     *
     */
    private String getFingerPrint(String digest) {
        return (fingerprintBytesToStr(this.getFingerPrintBytes(digest)));
    }

    /**
     * Returns the cert path for this certificate as it exists in the keystore.
     *
     * @return null if no cert path could be found All used methods are not
     * thread safe
     */
    public synchronized PKIXCertPathBuilderResult
            getPKIXCertPathBuilderResult(KeyStore keystore, List<X509Certificate> certificateList) {
        try {
            X509CertSelector certSelector = new X509CertSelector();
            certSelector.setCertificate(this.certificate);
            CertPathBuilder pathBuilder = CertPathBuilder.getInstance("PKIX",
                    BouncyCastleProviderSingleton.instance().getName());
            PKIXBuilderParameters pkixParameter = new PKIXBuilderParameters(keystore, certSelector);
            pkixParameter.setRevocationEnabled(false);
            //this is necessary for brainpool certificates, else the signature check will always fail under 
            //java 17 and above
            pkixParameter.setSigProvider(BouncyCastleProviderSingleton.instance().getName());
            //a value of 5 does not work for some certificates in Bouncycastle. 3 means Anchor + 3 certificate 
            //which should be fine
            pkixParameter.setMaxPathLength(3);
            CertStoreParameters storeParameter = new CollectionCertStoreParameters(certificateList);
            CertStore certStore = CertStore.getInstance("Collection", storeParameter,
                    BouncyCastleProviderSingleton.instance().getName());
            pkixParameter.addCertStore(certStore);
            PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) pathBuilder.build(pkixParameter);
            return (result);
        } catch (Throwable e) {
        }
        return (null);
    }

    /**
     * Validates the certificate and returns the trust anchor certificate if the
     * cert path is valid and the full path could be validated
     *
     * @return null if the certificate could not be trusted or an other failure
     * like nosuchalg exception etc occurs
     */
    public X509Certificate validateCertPath(KeyStore keystore, List<X509Certificate> certificateList) {
        CertPath certPath = this.getPKIXCertPathBuilderResult(keystore, certificateList).getCertPath();
        if (certPath == null) {
            return (null);
        }
        try {
            // Validator params
            PKIXParameters params = new PKIXParameters(keystore);
            //this is necessary for brainpool certificates, else the signature check will always fail under 
            //java 17 and above
            params.setSigProvider(BouncyCastleProviderSingleton.instance().getName());
            // Disable CRL checking since we are not supplying any CRLs
            params.setRevocationEnabled(false);
            CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX",
                    BouncyCastleProviderSingleton.instance().getName());
            CertPathValidatorResult result = certPathValidator.validate(certPath, params);
            // Get the CA used to validate this path
            PKIXCertPathValidatorResult pkixResult = (PKIXCertPathValidatorResult) result;
            TrustAnchor trustAnchor = pkixResult.getTrustAnchor();
            X509Certificate taCert = trustAnchor.getTrustedCert();
            return (taCert);
        } catch (Throwable e) {
        }
        return (null);
    }

    /**
     * Returns the CN entry of the subject or null if this is not set
     */
    public String getSubjectCN() {
        return (this.getSubjectEntryUnescaped("2.5.4.3"));
    }

    /**
     * Returns the CN entry of the issuer or null if this is not set
     */
    public String getIssuerCN() {
        return (this.getIssuerEntryUnescaped("2.5.4.3"));
    }

    /**
     * Returns the CN entry of the issuer or null if this is not set
     */
    public String getIssuerOrganization() {
        return (this.getIssuerEntryUnescaped("2.5.4.10"));
    }

    /**
     * Returns the CN entry of the subject or null if this is not set
     */
    public String getSubjectOrganization() {
        return (this.getSubjectEntryUnescaped("2.5.4.10"));
    }

    /**
     * Returns the CN entry of the subject or null if this is not set
     */
    public String getSubjectOU() {
        return (this.getSubjectEntryUnescaped("2.5.4.11"));
    }

    /**
     * This returns the unescaped(!) value of the subject entry
     *
     * @param oidStr
     * @return
     */
    private String getSubjectEntryUnescaped(String oidStr) {
        X500Name x500Name = X500Name.getInstance(this.certificate.getSubjectX500Principal().getEncoded());
        for (RDN rdn : x500Name.getRDNs()) {
            for (AttributeTypeAndValue attributeAndValue : rdn.getTypesAndValues()) {
                ASN1ObjectIdentifier oid = attributeAndValue.getType();
                if (oid.equals(new ASN1ObjectIdentifier(oidStr))) {
                    return (attributeAndValue.getValue().toString());
                }
            }
        }
        return null;
    }

    /**
     * This returns the unescaped(!) value of the issuer entry
     *
     * @param oidStr
     * @return
     */
    private String getIssuerEntryUnescaped(String oidStr) {
        X500Name x500Name = X500Name.getInstance(this.certificate.getIssuerX500Principal().getEncoded());
        for (RDN rdn : x500Name.getRDNs()) {
            for (AttributeTypeAndValue attributeAndValue : rdn.getTypesAndValues()) {
                ASN1ObjectIdentifier oid = attributeAndValue.getType();
                if (oid.equals(new ASN1ObjectIdentifier(oidStr))) {
                    return (attributeAndValue.getValue().toString());
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return (this.alias);
    }

    /**
     * Sort a list of KeystoreCertificate You MUST not use the equals method in
     * this method - this will result in a "Comparison method violates its
     * general contract" IllegalArgument Exception The reason ist that the
     * implementor must also ensure that the relation is transitive:
     * (x.compareTo(y)>0 && y.compareTo(z)>0) implies x.compareTo(z)>0.
     */
    @Override
    public int compareTo(KeystoreCertificate otherCert) {
        return (this.alias.toUpperCase().compareTo(otherCert.alias.toUpperCase()));
    }

    private void computeInfoText() {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        StringBuilder infoTextBuilder = new StringBuilder();
        infoTextBuilder.append("Version: ").append(this.getVersion());
        if (this.isRootCertificate()) {
            infoTextBuilder.append(" (Root certificate)");
        }
        infoTextBuilder.append("\n")
                .append("Subject: ").append(this.getSubjectDN()).append("\n")
                .append("Issuer: ").append(this.getIssuerDN()).append("\n")
                .append("Serial (dec): ").append(this.getSerialNumberDEC()).append("\n")
                .append("Serial (hex): ").append(this.getSerialNumberHEX()).append("\n")
                .append("Valid from: ").append(format.format(this.getNotBefore())).append("\n")
                .append("Valid until: ").append(format.format(this.getNotAfter())).append("\n")
                .append("Public key: ");
        int publicKeyLength = this.getPublicKeyLength();
        infoTextBuilder.append(String.valueOf(publicKeyLength))
                .append(" ").append(this.getPublicKeyAlgorithm()).append("\n")
                .append("Signature algorithm: ").append(this.getSigAlgName()).append(" (OID ")
                .append(this.getSigAlgOID()).append(")\n");
        if (this.getPublicKeyAlgorithm().startsWith("EC")) {
            try {
                ECPublicKey publicKey = (ECPublicKey) this.getX509Certificate().getPublicKey();
                String oid = this.getCurveOID(publicKey);
                String curveName = this.getCurveName(publicKey);
                infoTextBuilder
                        .append("Named Curve: ")
                        .append(curveName).append(" [OID ")
                        .append(oid).append("]\n");
            } catch (Throwable ignore) {
                infoTextBuilder
                        .append("Named Curve:  Unknown\n");
            }
        }
        try {
            infoTextBuilder.append("Fingerprint (MD5): ").append(this.getFingerPrintMD5()).append("\n")
                    .append("Fingerprint (SHA-1): ").append(this.getFingerPrintSHA1()).append("\n")
                    .append("Fingerprint (SHA-256): ").append(this.getFingerPrintSHA256()).append("\n");
        } catch (Exception e) {
            infoTextBuilder.append("Fingerprint processing failed: ").append(e.getMessage());
        }
        this.infoText = infoTextBuilder.toString();
    }

    /**
     * Returns the curve OID if this is a EC key/certificate
     */
    private String getCurveOID(ECPublicKey publicKey) throws Throwable {
        AlgorithmParameters params = AlgorithmParameters.getInstance("EC");
        params.init(publicKey.getParams());
        return (params.getParameterSpec(ECGenParameterSpec.class).getName());
    }

    /**
     * Returns the curve OID if this is a EC key/certificate
     */
    public String getCurveName(ECPublicKey publicKey) throws Throwable {
        ECParameterSpec params = publicKey.getParams();
        //convert to BC spec
        org.bouncycastle.jce.spec.ECParameterSpec spec = EC5Util.convertSpec(params);
        Enumeration<?> ecNamedCurveTable = ECNamedCurveTable.getNames();
        while (ecNamedCurveTable.hasMoreElements()) {
            String name = ecNamedCurveTable.nextElement().toString();
            X9ECParameters possibleMatch = ECNamedCurveTable.getByName(name);
            if (possibleMatch != null) {
                if (spec.getN().equals(possibleMatch.getN())
                        && spec.getH().equals(possibleMatch.getH())
                        && spec.getCurve().equals(possibleMatch.getCurve())
                        && spec.getG().equals(possibleMatch.getG())) {
                    return name;
                }
            }
        }
        Enumeration<?> ecCustomNamedCurveTable = CustomNamedCurves.getNames();
        while (ecCustomNamedCurveTable.hasMoreElements()) {
            String name = ecCustomNamedCurveTable.nextElement().toString();
            X9ECParameters possibleMatch = CustomNamedCurves.getByName(name);
            if (possibleMatch != null) {
                if (spec.getN().equals(possibleMatch.getN())
                        && spec.getH().equals(possibleMatch.getH())
                        && spec.getCurve().equals(possibleMatch.getCurve())
                        && spec.getG().equals(possibleMatch.getG())) {
                    return name;
                }
            }
        }
        return ("");
    }

    /**
     * Returns a string that contains information about the certificate
     */
    public String getInfo() {
        return (this.infoText);
    }

    /**
     * Returns some information about the certificate extensions
     */
    public String getInfoExtension() {
        StringBuilder extensionText = new StringBuilder();
        List<String> crl = this.getCrlDistributionURLs();
        for (int i = 0; i < crl.size(); i++) {
            extensionText.append("CRL distribution[").append(String.valueOf(i + 1)).append("]: ").append(crl.get(i)).append("\n");
        }
        List<String> alternativeNames = this.getSubjectAlternativeNames();
        if (!alternativeNames.isEmpty()) {
            extensionText.append("Subject alternative name: ").append(this.convertListToString(alternativeNames)).append("\n");
        }
        List<String> keyUsages = this.getKeyUsages();
        if (!keyUsages.isEmpty()) {
            extensionText.append("Key usage: ").append(this.convertListToString(keyUsages)).append("\n");
        }
        List<String> extkeyUsages = this.getExtendedKeyUsage();
        if (!extkeyUsages.isEmpty()) {
            extensionText.append("Extended key usage: ").append(this.convertListToString(extkeyUsages)).append("\n");
        }
        List<String> authorityKeyIdentifier = this.getAuthorityKeyIdentifier();
        if (!authorityKeyIdentifier.isEmpty()) {
            extensionText.append("Authority key identifier: ").append(this.convertListToString(authorityKeyIdentifier)).append("\n");
        }
        List<String> subjectKeyIdentifier = this.getSubjectKeyIdentifier();
        if (!subjectKeyIdentifier.isEmpty()) {
            extensionText.append("Subject key identifier: ").append(this.convertListToString(subjectKeyIdentifier)).append("\n");
        }
        List<String> policyList = this.getPolicy();
        if (!policyList.isEmpty()) {
            extensionText.append("Certificate policies: ").append(this.convertListToString(policyList)).append("\n");
        }
        return (extensionText.toString());
    }

    /**
     * Converts the arraylist content to a comma separated string
     */
    private String convertListToString(Collection<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String value : list) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
        return (builder.toString());
    }

    /**
     * Overwrite the equal method of object
     *
     * @param anObject object ot compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof KeystoreCertificate) {
            KeystoreCertificate cert = (KeystoreCertificate) anObject;
            String otherFingerPrint;
            String ownFingerPrint;
            try {
                otherFingerPrint = cert.getFingerPrintSHA1();
                ownFingerPrint = this.getFingerPrintSHA1();
                return (otherFingerPrint.equals(ownFingerPrint));
            } catch (Exception e) {
                //unable to obtain the finger print. Use the serial number and the dates.
                return (cert.getIssuerDN().equals(this.getIssuerDN())
                        && cert.getNotAfter().equals(this.getNotAfter())
                        && cert.getNotBefore().equals(this.getNotBefore()));
            }
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.alias != null ? this.alias.hashCode() : 0);
        hash = 97 * hash + (this.certificate != null ? this.certificate.hashCode() : 0);
        hash = 97 * hash + (this.isKeyPair ? 1 : 0);
        return hash;
    }

    /**
     * @return the CertificateChain
     */
    public Certificate[] getCertificateChain() {
        return (this.certificateChain);
    }

    /**
     * Sets the entry to display mode. This allows to send it to a client
     * wihtout sending private key information
     */
    public void setToDisplayMode() {
        //makes only sense for key entries - this will generate a dummy key for the display side.
        //The key itself will not be transported to the client
        if (this.isKeyPair) {
            this.privateKey = null;
        }
    }

    /**
     * Adds this entry to the passed parent JSON node
     */
    public void addToJSON(ArrayNode parent) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS' UTC'");
        ObjectNode certificateNode = parent.addObject();
        certificateNode.put("alias", this.alias);
        certificateNode.put("subject", this.getSubjectDN());
        certificateNode.put("issuer", this.getIssuerDN());
        certificateNode.put("notbefore", dateFormat.format(this.getNotBefore()));
        certificateNode.put("notafter", dateFormat.format(this.getNotAfter()));
        certificateNode.put("length", this.getPublicKeyLength());
        certificateNode.put("algorithm", this.getPublicKeyAlgorithm());
        if (this.getX509Certificate().getPublicKey() instanceof ECPublicKey) {
            try {
                certificateNode.put("curve", this.getCurveName((ECPublicKey) this.getX509Certificate().getPublicKey()));
            } catch (Throwable e) {
            }
        }
        certificateNode.put("fingerprintsha1", this.getFingerPrintSHA1());
        certificateNode.put("fingerprintsha256", this.getFingerPrintSHA256());
        certificateNode.put("serialhex", this.getSerialNumberHEX());
        certificateNode.put("serialdec", this.getSerialNumberDEC());
        if (!this.getKeyUsages().isEmpty()) {
            ArrayNode usageNode = certificateNode.putArray("usages");
            ObjectNode extNode = usageNode.addObject();
            for (String ext : this.getKeyUsages()) {
                extNode.put("usage", ext);
            }
        }
        if (!this.getExtendedKeyUsage().isEmpty()) {
            ArrayNode extArrayNode = certificateNode.putArray("extusages");
            ObjectNode extNode = extArrayNode.addObject();
            for (String ext : this.getExtendedKeyUsage()) {
                extNode.put("extusage", ext);
            }
        }
        if (!this.getSubjectAlternativeNames().isEmpty()) {
            ArrayNode sanArrayNode = certificateNode.putArray("sanlist");
            ObjectNode sanNode = sanArrayNode.addObject();
            for (String san : this.getSubjectAlternativeNames()) {
                sanNode.put("san", san);
            }
        }
        String certificateBase64 = "ENCODING_ERROR";
        try {
            certificateBase64 = Base64.encode(this.certificate.getEncoded());
        } catch (Throwable e) {
        }
        certificateNode.put("certencoded", certificateBase64);
    }

}
