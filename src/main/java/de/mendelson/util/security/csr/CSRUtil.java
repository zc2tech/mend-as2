package de.mendelson.util.security.csr;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.Base64;
import de.mendelson.util.security.BouncyCastleProviderSingleton;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.EllipticCurve;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.crmf.SubsequentMessage;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;
import org.bouncycastle.cert.crmf.jcajce.JcaCertificateRequestMessageBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Handles csr related activities on a certificate
 *
 * @author S.Heller
 * @version $Revision: 29 $
 */
public class CSRUtil {

    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCSRUtil.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public void displayASN1(byte[] data) throws Exception {
        try (ASN1InputStream dataIn = new ASN1InputStream(data)) {
            ASN1Primitive primitive;
            while ((primitive = dataIn.readObject()) != null) {
                System.out.println(ASN1Dump.dumpAsString(primitive));
            }
        }
    }

    private ASN1Primitive toDERObject(byte[] data) throws IOException {
        try (ByteArrayInputStream inStream = new ByteArrayInputStream(data)) {
            try (ASN1InputStream asnInputStream = new ASN1InputStream(inStream)) {
                return asnInputStream.readObject();
            }
        }
    }

    private List<GeneralName> getSubjectAlternativeNames(X509Certificate certificate) throws Exception {
        List<GeneralName> namesList = new ArrayList<GeneralName>();
        //Each entry is a List whose first entry is an Integer (the name type, 0-8) and whose second entry is a String or a 
        //byte array (the name, in string or ASN.1 DER encoded form, respectively).
        Collection<List<?>> parsedNamesList = certificate.getSubjectAlternativeNames();
        //nothing found -> return empty list
        if (parsedNamesList == null) {
            return (namesList);
        }
        for (List list : parsedNamesList) {
            if (list.size() == 2) {
                int tagNo = ((Integer) list.get(0)).intValue();
                if (list.get(1) instanceof byte[]) {
                    GeneralName newName = new GeneralName(tagNo, this.toDERObject((byte[]) list.get(1)));
                    namesList.add(newName);
                } else if (list.get(1) instanceof String) {
                    GeneralName newName = new GeneralName(tagNo, list.get(1).toString());
                    namesList.add(newName);
                }
            }
        }
        return (namesList);
    }

    /**
     * Generates a PKCS10 CertificationRequest. The passed private key must not
     * be trusted
     */
    public PKCS10CertificationRequest createCSRPKCS10(String dn, PrivateKey privateKey, X509Certificate certificate) throws Exception {
        boolean isECKey = certificate.getPublicKey().getAlgorithm().equals("EC");
        X500Name x500DNName = new X500Name(dn);
        AsymmetricKeyParameter privateKeyAsymKeyParam = PrivateKeyFactory.createKey(privateKey.getEncoded());
        ContentSigner contentSigner = null;
        if (isECKey) {
            contentSigner = new JcaContentSignerBuilder("SHA256withECDSA")
                    .setProvider(BouncyCastleProviderSingleton.instance()).build(privateKey);
        } else {
            AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
            AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
            contentSigner = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(privateKeyAsymKeyParam);
        }
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(certificate.getPublicKey().getEncoded());
        PKCS10CertificationRequestBuilder pkcs10Builder = new PKCS10CertificationRequestBuilder(x500DNName, subPubKeyInfo);
        /*
         * Add SubjectAlternativeNames (SANs) using the ExtensionsGenerator
         */
        List<GeneralName> sanList = this.getSubjectAlternativeNames(certificate);
        if (!sanList.isEmpty()) {
            ExtensionsGenerator extGen = new ExtensionsGenerator();
            GeneralName[] sanArray = new GeneralName[sanList.size()];
            sanList.toArray(sanArray);
            GeneralNames subjectAltNames = new GeneralNames(sanArray);
            extGen.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
            pkcs10Builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());
        }
        //add SKI to the PKCS10 request if this exists in the certificate
        byte[] ski = this.getSubjectKeyIdentifier(certificate);
        if (ski != null && ski.length > 0) {
            ExtensionsGenerator extGen = new ExtensionsGenerator();
            extGen.addExtension(Extension.subjectKeyIdentifier, false, ski);
            pkcs10Builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
                    extGen.generate());
        }
        PKCS10CertificationRequest csr = pkcs10Builder.build(contentSigner);
        ContentVerifierProvider verifier = new JcaContentVerifierProviderBuilder()
                .setProvider(BouncyCastleProviderSingleton.instance()).build(certificate);
        boolean verified = csr.isSignatureValid(verifier);
        if (!verified) {
            throw new Exception(rb.getResourceString("verification.failed"));
        }
        return (csr);
    }

    /**
     * Returns the SKI of the passed certificate or an empty list if there is
     * none
     */
    private byte[] getSubjectKeyIdentifier(X509Certificate certificate) {
        byte[] extensionValue = certificate.getExtensionValue("2.5.29.14");
        if (extensionValue == null) {
            //there is no such extension: return empty list
            return (new byte[0]);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            DEROctetString octetStr = (DEROctetString) ASN1Primitive.fromByteArray(octedBytes);
            byte[] identifier = octetStr.getOctets();
            return (identifier);
        } catch (Throwable e) {
            return (new byte[0]);
        }
    }

    /**
     * Generates a PKCS10 CertificationRequest. The passed private key must not
     * be trusted
     */
    public PKCS10CertificationRequest generateCSRPKCS10(CertificateManager manager, String privateKeyAlias) throws Exception {
        PrivateKey key = manager.getPrivateKey(privateKeyAlias);
        Certificate[] certchain = manager.getCertificateChain(privateKeyAlias);
        X509Certificate[] x509Certchain = new X509Certificate[certchain.length];
        for (int i = 0; i < certchain.length; i++) {
            x509Certchain[i] = (X509Certificate) certchain[i];
        }
        x509Certchain = KeyStoreUtil.orderX509CertChain(x509Certchain);
        //get the subject alternate names
        X509Certificate endCert = x509Certchain[0];
        PKCS10CertificationRequest csr = this.createCSRPKCS10(endCert.getSubjectDN().toString(), key, endCert);
        return (csr);
    }

    /**
     * Generates a Signature Based Proof-of-Possession CSR
     */
    private CertificateRequestMessage generateCertificateRequestMessagePOPSignature(BigInteger certificateRequestId,
            CertificateManager manager, String privateKeyAlias) throws Exception {
        PublicKey publicKey = manager.getPublicKey(privateKeyAlias);
        PrivateKey privateKey = manager.getPrivateKey(privateKeyAlias);
        X509Certificate certificate = manager.getX509Certificate(privateKeyAlias);
        JcaCertificateRequestMessageBuilder certificateRequestBuilder
                = new JcaCertificateRequestMessageBuilder(certificateRequestId);
        X500Name subject = new X500Name("CN=" + this.getX500Name(certificate, BCStyle.CN));
        certificateRequestBuilder
                .setPublicKey(publicKey)
                .setSubject(subject)
                .setProofOfPossessionSigningKeySigner(
                        new JcaContentSignerBuilder(this.getSignatureAlgorithm(publicKey))
                                .setProvider(BouncyCastleProviderSingleton.instance())
                                .build(privateKey)
                );
        /*
         * Add SubjectAlternativeNames (SANs) as extension
         */
        List<GeneralName> sanList = this.getSubjectAlternativeNames(certificate);
        if (!sanList.isEmpty()) {
            GeneralName[] sanArray = new GeneralName[sanList.size()];
            sanList.toArray(sanArray);
            GeneralNames subjectAltNames = new GeneralNames(sanArray);
            certificateRequestBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
        }
        //add SKI to the certificate request message if this exists in the certificate
        byte[] ski = this.getSubjectKeyIdentifier(certificate);
        if (ski != null && ski.length > 0) {
            certificateRequestBuilder.addExtension(Extension.subjectKeyIdentifier, false, ski);
        }
        certificateRequestBuilder.addExtension(Extension.keyUsage, true,
                new KeyUsage(KeyUsage.digitalSignature));
        return (certificateRequestBuilder.build());
    }

    /**
     * Generates a Key Agreement Based Proof-of-Possession CSR
     */
    private CertificateRequestMessage generateCertificateWithPOPKeyAgreement(BigInteger certificateRequestId,
            CertificateManager manager, String privateKeyAlias) throws Exception {
        PublicKey publicKey = manager.getPublicKey(privateKeyAlias);
        X509Certificate certificate = manager.getX509Certificate(privateKeyAlias);
        JcaCertificateRequestMessageBuilder certificateRequestBuilder
                = new JcaCertificateRequestMessageBuilder(certificateRequestId);
        X500Name subject = new X500Name("CN=" + this.getX500Name(certificate, BCStyle.CN));
        certificateRequestBuilder
                .setPublicKey(publicKey)
                .setSubject(subject)
                .setProofOfPossessionSubsequentMessage(
                        ProofOfPossession.TYPE_KEY_AGREEMENT, SubsequentMessage.encrCert
                );
        /*
         * Add SubjectAlternativeNames (SANs) as extension
         */
        List<GeneralName> sanList = this.getSubjectAlternativeNames(certificate);
        if (!sanList.isEmpty()) {
            GeneralName[] sanArray = new GeneralName[sanList.size()];
            sanList.toArray(sanArray);
            GeneralNames subjectAltNames = new GeneralNames(sanArray);
            certificateRequestBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
        }
        //add SKI to the certificate request message if this exists in the certificate
        byte[] ski = this.getSubjectKeyIdentifier(certificate);
        if (ski != null && ski.length > 0) {
            certificateRequestBuilder.addExtension(Extension.subjectKeyIdentifier, false, ski);
        }
        //The key usage for TLS is
        //SSL Client: Digital signature
        //SSL Server: Key encipherment
        certificateRequestBuilder.addExtension(Extension.keyUsage, true,
                new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        // Extended Key Usage für TLS Web Server und Web Client
        ASN1EncodableVector extendedKeyUsageVector = new ASN1EncodableVector();
        // TLS Web Server Authentication (1.3.6.1.5.5.7.3.1)
        extendedKeyUsageVector.add(KeyPurposeId.id_kp_serverAuth);
        // TLS Web Client Authentication (1.3.6.1.5.5.7.3.2)
        extendedKeyUsageVector.add(KeyPurposeId.id_kp_clientAuth);
        // ASN1Sequence erstellen, die beide OIDs enthält
        ASN1Sequence extendedKeyUsage = new DERSequence(extendedKeyUsageVector);
        // Erweiterung zu CertificateRequestMessageBuilder hinzufügen
        certificateRequestBuilder.addExtension(Extension.extendedKeyUsage, true, extendedKeyUsage);
        return (certificateRequestBuilder.build());
    }

    /**
     * Generates a Encryption Based Proof-of-Possession CSR
     */
    private CertificateRequestMessage generateCertificateRequestMessagePOPEncryption(BigInteger certificateRequestId,
            CertificateManager manager, String privateKeyAlias) throws Exception {
        KeystoreCertificate keystoreCert = manager.getKeystoreCertificate(privateKeyAlias);
        PublicKey publicKey = keystoreCert.getPublicKey();
        X509Certificate certificate = manager.getX509Certificate(privateKeyAlias);
        JcaCertificateRequestMessageBuilder certificateRequestBuilder
                = new JcaCertificateRequestMessageBuilder(certificateRequestId);
        X500Name subject = new X500Name("CN=" + this.getX500Name(certificate, BCStyle.CN));
        certificateRequestBuilder
                .setPublicKey(publicKey)
                .setSubject(subject)
                .setProofOfPossessionSubsequentMessage(SubsequentMessage.encrCert);
        /*
         * Add SubjectAlternativeNames (SANs) as extension
         */
        List<GeneralName> sanList = this.getSubjectAlternativeNames(certificate);
        if (!sanList.isEmpty()) {
            GeneralName[] sanArray = new GeneralName[sanList.size()];
            sanList.toArray(sanArray);
            GeneralNames subjectAltNames = new GeneralNames(sanArray);
            certificateRequestBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
        }
        //add SKI to the certificate request message if this exists in the certificate
        byte[] ski = this.getSubjectKeyIdentifier(certificate);
        if (ski != null && ski.length > 0) {
            certificateRequestBuilder.addExtension(Extension.subjectKeyIdentifier, false, ski);
        }
        certificateRequestBuilder.addExtension(Extension.keyUsage, true,
                new KeyUsage(KeyUsage.dataEncipherment));
        return (certificateRequestBuilder.build());
    }

    public CertReqMessages generateCertificateRequestMessagesTLS(BigInteger certificateRequestId,
            CertificateManager manager, String privateKeyAlias) throws Exception {
        CertificateRequestMessage message = this.generateCertificateWithPOPKeyAgreement(
                certificateRequestId, manager, privateKeyAlias);
        CertReqMsg[] certReqMsgArray = new CertReqMsg[]{
            message.toASN1Structure(),};
        CertReqMessages messages = new CertReqMessages(certReqMsgArray);
        return (messages);
    }

    public CertReqMessages generateCertificateRequestMessagesSign(BigInteger certificateRequestId,
            CertificateManager manager, String privateKeyAlias) throws Exception {
        CertificateRequestMessage message = this.generateCertificateRequestMessagePOPSignature(
                certificateRequestId, manager, privateKeyAlias);
        CertReqMsg[] certReqMsgArray = new CertReqMsg[]{
            message.toASN1Structure(),};
        CertReqMessages messages = new CertReqMessages(certReqMsgArray);
        return (messages);
    }

    public CertReqMessages generateCertificateRequestMessagesEnc(BigInteger certificateRequestId,
            CertificateManager manager, String privateKeyAlias) throws Exception {
        CertificateRequestMessage message = this.generateCertificateRequestMessagePOPEncryption(
                certificateRequestId, manager, privateKeyAlias);
        CertReqMsg[] certReqMsgArray = new CertReqMsg[]{
            message.toASN1Structure(),};
        CertReqMessages messages = new CertReqMessages(certReqMsgArray);
        return (messages);
    }

    /**
     * Returns the signature algorithm of a passed public key
     */
    private String getSignatureAlgorithm(PublicKey publicKey) {
        switch (publicKey.getAlgorithm()) {
            case "EC":
                EllipticCurve curve = ((ECPublicKey) publicKey).getParams().getCurve();
                switch (curve.getField().getFieldSize()) {
                    case 224:
                    case 256:
                        return "SHA256withECDSA";
                    case 384:
                        return "SHA384withECDSA";
                    case 521:
                        return "SHA512withECDSA";
                    default:
                        throw new IllegalArgumentException("unknown elliptic curve: " + curve);
                }
            case "RSA":
                return "SHA256WithRSAEncryption";
            default:
                throw new UnsupportedOperationException("CSRUtil.getSignatureAlgorithm: Unsupported private key algorithm: " + publicKey.getAlgorithm());
        }
    }

    /**
     *
     * @param certificate The certificate to analyze
     * @param identifier the name to extract, e.g. BCStyle.CN
     * @return
     */
    private String getX500Name(X509Certificate certificate, ASN1ObjectIdentifier identifier) throws Exception {
        X500Principal principal = certificate.getIssuerX500Principal();
        X500Name x500Name = new X500Name(principal.getName());
        RDN[] rdns = x500Name.getRDNs(identifier);
        List<String> names = new ArrayList<>();
        for (RDN rdn : rdns) {
            String name = IETFUtils.valueToString(rdn.getFirst().getValue());
            names.add(name);
        }
        if (names.isEmpty()) {
            throw new Exception("CSRUtil.getX500Name: Unknown identifier " + identifier.toString() + " requested.");
        }
        return (names.get(0));
    }

    /**
     * Writes the CSR to a string
     */
    public String storeCSRPEMPKCS10ToStr(PKCS10CertificationRequest csr) throws Exception {
        try (StringWriter stringWriter = new StringWriter()) {
            try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
                pemWriter.writeObject(csr);
                pemWriter.flush();
            }
            return (stringWriter.toString());
        }
    }

    /**
     * Writes the CertificateRequestMessage to a string, BASE64 encoded
     */
    public String storeCertificateRequestMessagesToStr(CertReqMessages certReqMessages) throws Exception {
        try (StringWriter stringWriter = new StringWriter()) {
            stringWriter.write(Base64.encode(certReqMessages.getEncoded()));
            stringWriter.flush();
            return (stringWriter.toString());
        }
    }

    /**
     * Writes a csr to a file, PEM encoded
     */
    public void storeCSRPEMPKCS10(PKCS10CertificationRequest csr, Path outFile) throws Exception {
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(Files.newBufferedWriter(outFile))) {
            pemWriter.writeObject(csr);
            pemWriter.flush();
        }
    }

    /**
     * Writes a CSR to a file, PEM encoded
     */
    public void storeRequestToFile(String requestBase64, Path outFile) throws Exception {
        Files.writeString(outFile, requestBase64);
    }

    /**
     * Imports the answer of the CA which looks like a certificate. The patched
     * certificate will be updated with the cert chain that is included in the
     * returned signed certificate.
     *
     * @deprecated The private key operations are done on the server side one -
     * makes this code is moved to the server processing
     *
     */
    @Deprecated(since = "2023")
    public boolean importCSRReply(CertificateManager manager, String alias, Path csrResponseFile) throws Throwable {
        PrivateKey key = manager.getPrivateKey(alias);
        PublicKey publicKey = manager.getPublicKey(alias);
        // Load certificates found in the PEM(!) encoded answer
        List<X509Certificate> responseCertList = new ArrayList<X509Certificate>();
        try (InputStream inputStream = Files.newInputStream(csrResponseFile)) {
            for (Certificate responseCert : CertificateFactory.getInstance("X509").generateCertificates(inputStream)) {
                responseCertList.add((X509Certificate) responseCert);
            }
        }
        if (responseCertList.isEmpty()) {
            throw new Exception(rb.getResourceString("no.certificates.in.reply"));
        }
        PublicKey responsePublicKey = responseCertList.get(responseCertList.size() - 1).getPublicKey();
        if (!publicKey.equals(responsePublicKey)) {
            throw new Exception(rb.getResourceString("response.public.key.does.not.match"));
        }
        List<X509Certificate> newCerts;
        if (responseCertList.size() == 1) {
            // Reply has only one certificate
            newCerts = this.buildNewTrustChain(manager, responseCertList.get(0));
        } else {
            // Reply has a chain of certificates
            newCerts = this.validateReply(responseCertList);
        }
        if (newCerts != null) {
            manager.setKeyEntry(alias, key, newCerts.toArray(new X509Certificate[newCerts.size()]));
            return true;
        } else {
            return false;
        }
    }

    public List<X509Certificate> buildNewTrustChain(CertificateManager manager, X509Certificate certReply)
            throws Exception {
        Map<X500Principal, List<X509Certificate>> knownCerts = manager.getIssuerCertificateMap();
        LinkedList<X509Certificate> newTrustChain = new LinkedList<X509Certificate>();
        this.buildNewTrustChainRecursive(manager, certReply, newTrustChain, knownCerts);
        return (newTrustChain);
    }

    /**
     * Builds a new certificate chain from the answer
     */
    private void buildNewTrustChainRecursive(CertificateManager manager, X509Certificate certificate, LinkedList<X509Certificate> newTrustChain,
            Map<X500Principal, List<X509Certificate>> availableCertificates) throws Exception {
        X500Principal subject = certificate.getSubjectX500Principal();
        X500Principal issuer = certificate.getIssuerX500Principal();
        // Check if the certificate is a root certificate (i.e. was issued by the same Principal that
        // is present in the subject)
        if (subject.equals(issuer)) {
            newTrustChain.addFirst(certificate);
            return;
        }
        // Get the list of known certificates of the certificate's issuer
        List<X509Certificate> issuerCerts = availableCertificates.get(issuer);
        if (issuerCerts == null || issuerCerts.isEmpty()) {
            // A certificate is in the chain that is missing in the available certificates -> has to be imported first
            throw new Exception(rb.getResourceString("missing.cert.in.trustchain", issuer));
        }
        for (X509Certificate issuerCert : issuerCerts) {
            PublicKey publickey = issuerCert.getPublicKey();
            // Verify the certificate with the specified public key
            certificate.verify(publickey);
            this.buildNewTrustChainRecursive(manager, issuerCert, newTrustChain, availableCertificates);
        }
        newTrustChain.addFirst(certificate);
    }

    /**
     * Validates chain in certification reply, and returns the ordered elements
     * of the chain (with user certificate first, and root certificate last in
     * the array).
     *
     * @param replyCerts the chain provided in the reply
     */
    public List<X509Certificate> validateReply(List<X509Certificate> replyCerts) throws Exception {
        // order the certs in the reply (bottom-up).
        X509Certificate tmpCert = null;
        Principal issuer = replyCerts.get(0).getIssuerDN();
        for (int i = 1; i < replyCerts.size(); i++) {
            // find a cert in the reply whose "subject" is the same as the
            // given "issuer"
            int j;
            for (j = i; j < replyCerts.size(); j++) {
                Principal subject = replyCerts.get(j).getSubjectDN();
                if (subject.equals(issuer)) {
                    tmpCert = replyCerts.get(i);
                    replyCerts.set(i, replyCerts.get(j));
                    replyCerts.set(j, tmpCert);
                    issuer = replyCerts.get(i).getIssuerDN();
                    break;
                }
            }
            if (j == replyCerts.size()) {
                throw new Exception(rb.getResourceString("response.chain.incomplete"));
            }
        }
        // now verify each cert in the ordered chain
        for (int i = 0; i < replyCerts.size(); i++) {
            PublicKey pubKey = replyCerts.get(i + 1).getPublicKey();
            try {
                replyCerts.get(i).verify(pubKey);
            } catch (Exception e) {
                throw new Exception(rb.getResourceString("response.verification.failed", e.getMessage()));
            }
        }
        return replyCerts;
    }

}
