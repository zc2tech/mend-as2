package de.mendelson.util.security.crl;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPURL;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.cert.KeystoreCertificate;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CRLException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Verifies a CRL of a certificate. Supports HTTP/FTP and LDAP downlaods of CRLs
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class CRLVerification {

    private final CRLCache cache = new CRLCache();
    private final static MecResourceBundle rb;
    private final static String MODULE_NAME;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCRL.class.getName());
            MODULE_NAME = rb.getResourceString("module.name");
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public CRLVerification() {
    }

    public CRLRevocationInformation checkCertificate(KeystoreCertificate certificate) {
        CRLRevocationState revocationState = null;
        StringBuilder builder = new StringBuilder();
        builder.append("[")
                .append(certificate.getAlias())
                .append("] ");
        if (certificate.isSelfSigned() && !certificate.isCACertificate()) {
            revocationState
                    = new CRLRevocationState(CRLRevocationState.STATE_OK,
                            rb.getResourceString("self.signed.skipped"));
        } else {
            try {
                this.checkCertificate(certificate.getX509Certificate());
                revocationState = new CRLRevocationState(CRLRevocationState.STATE_OK,
                        rb.getResourceString("crl.success"));
            } catch (CRLVerificationException ex) {
                revocationState = ex.getRevocationState();
            } catch (Throwable e) {
                revocationState
                        = new CRLRevocationState(CRLRevocationState.STATE_OTHER_PROBLEM,
                                "[" + e.getClass().getSimpleName() + "] " + e.getMessage());
            }
        }
        builder.append(revocationState.getDetails());
        return (new CRLRevocationInformation(revocationState, certificate.getFingerPrintSHA1(),
                MODULE_NAME + " " + builder.toString()));
    }

    public void checkCertificate(X509Certificate certificate) throws CRLVerificationException {
        List<String> crlDistributionPointList = this.getCRLDistributionPoints(certificate);
        for (String crlURL : crlDistributionPointList) {
            X509CRL crl = this.cache.getCRL(crlURL);
            if (crl == null) {
                if (crlURL.startsWith("ldap")) {
                    crl = this.downloadCRLFromLDAP(crlURL);
                } else {
                    crl = this.downloadCRLFromWeb(crlURL);
                }
                this.cache.put(crlURL, crl);
            }
            X509CRLEntry revokedEntry = crl.getRevokedCertificate(certificate);
            if (revokedEntry != null) {
                CRLRevocationState state
                        = new CRLRevocationState(CRLRevocationState.STATE_REVOKED,
                                rb.getResourceString(
                                        "failed.revoked",
                                        revokedEntry.getRevocationReason().toString()));
                throw new CRLVerificationException(state);
            }
        }
    }

    /**
     * Downloads CRL from the crlUrl. Does not support HTTPS - just because CRL
     * URLS using HTTPs is bad practice
     */
    private X509CRL downloadCRLFromWeb(String crlURL) throws CRLVerificationException {
        try {
            URL url = new URL(crlURL);
            try (InputStream crlStream = url.openStream()) {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                X509CRL crl = (X509CRL) certificateFactory.generateCRL(crlStream);
                return (crl);
            }
        } catch (MalformedURLException e) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_MALFORMED_CRL_URL,
                            rb.getResourceString("malformed.crl.url", crlURL));
            throw new CRLVerificationException(state, e);
        } catch (IOException e) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_HTTPS_NOT_SUPPORTED_IN_URL,
                            rb.getResourceString("no.https", crlURL));
            throw new CRLVerificationException(state, e);
        } catch (CRLException e) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_CRL_IN_BAD_FORMAT,
                            rb.getResourceString("bad.crl"));
            throw new CRLVerificationException(state, e);
        } catch (Throwable e) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_OTHER_PROBLEM,
                            "[" + e.getClass().getSimpleName() + "] " + e.getMessage());
            throw new CRLVerificationException(state, e);
        }
    }

    /**
     * Downloads a CRL from given LDAP url, e.g.
     * ldap://ldap.infoldap.de/dc=identity-ca,dc=infonotary,dc=com?certificateRevocationList
     *
     */
    private X509CRL downloadCRLFromLDAP(String ldapURLStr) throws CRLVerificationException {
        LDAPURL ldapURL;
        try {
            ldapURL = new LDAPURL(ldapURLStr);
        } catch (Exception e) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_MALFORMED_CRL_URL,
                            rb.getResourceString("malformed.crl.url", e.getMessage()));
            throw new CRLVerificationException(state);
        }
        if (!ldapURL.hostProvided()) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_MALFORMED_CRL_URL,
                            rb.getResourceString("malformed.crl.url", "No host provided in LDAP URL"));
            throw new CRLVerificationException(state);
        }
        String ldapHost = ldapURL.getHost();
        //389 raw connection or STARTTLS connection
        //636 LDAPS connection, TLS
        int ldapPort = ldapURL.getPort();
        SearchScope searchScope = ldapURL.getScope();
        String baseDN = ldapURL.getBaseDN().toString();
        Filter filter = ldapURL.getFilter();
        String searchAttributeName;
        if (!ldapURL.attributesProvided()) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_MALFORMED_CRL_URL,
                            rb.getResourceString("malformed.crl.url", "No attributes provided in LDAP URL"));
            throw new CRLVerificationException(state);
        } else {
            searchAttributeName = ldapURL.getAttributes()[0];
        }
        try (LDAPConnection connection = new LDAPConnection(ldapHost, ldapPort)) {
            SearchResult searchResult = connection.search(
                    baseDN,
                    searchScope,
                    filter
            );
            if (searchResult.getSearchEntries() != null && !searchResult.getSearchEntries().isEmpty()) {
                //get all possible attributes                
                for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                    Collection<Attribute> attributes = entry.getAttributes();
                    for (Attribute listAttribute : attributes) {
                        if (searchAttributeName.equalsIgnoreCase(listAttribute.getBaseName())) {
                            byte[] crlBytes = entry.getAttributeValueBytes(listAttribute.getName());
                            if (crlBytes == null || crlBytes.length == 0) {
                                CRLRevocationState state
                                        = new CRLRevocationState(CRLRevocationState.STATE_CRL_DOWNLOAD_FAILED,
                                                rb.getResourceString("download.failed.from", ldapURL));
                                throw new CRLVerificationException(state);
                            }
                            try (InputStream inStream = new ByteArrayInputStream(crlBytes)) {
                                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                                return (X509CRL) certificateFactory.generateCRL(inStream);
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_OTHER_PROBLEM,
                            "[" + e.getClass().getSimpleName() + "] " + e.getMessage());
            throw new CRLVerificationException(state);
        }
        CRLRevocationState state
                = new CRLRevocationState(CRLRevocationState.STATE_OTHER_PROBLEM,
                        "Attribute " + searchAttributeName + " not found on LDAP server");
        throw new CRLVerificationException(state);
    }

    /**
     * Extracts all CRL distribution point URLs from the "CRL Distribution
     * Point" extension in a X.509 certificate. Without a CRL entry this returns
     * an empty list
     */
    private List<String> getCRLDistributionPoints(X509Certificate certificate) throws CRLVerificationException {
        //Gets the DER-encoded OCTET string for the extension value for CRLDistributionPoints
        byte[] crlDPExtensionValue = certificate.getExtensionValue("2.5.29.31");
        if (crlDPExtensionValue == null) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_NO_CRL_INFORMATION_IN_CERTIFICATE,
                            rb.getResourceString("no.crl.entry"));
            throw new CRLVerificationException(state);
        }
        //crlDPExtensionValue is encoded in ASN.1 format.        
        //DER (Distinguished Encoding Rules) is one of ASN.1 encoding rules defined in ITU-T X.690, 2002, specification.
        //ASN.1 encoding rules can be used to encode any data object into a binary file. Read the object in octets.
        CRLDistPoint distPoint;
        try {
            try (ASN1InputStream asn1In = new ASN1InputStream(crlDPExtensionValue)) {
                DEROctetString crlDEROctetString = (DEROctetString) asn1In.readObject();
                //Get Input stream in octets
                try (ASN1InputStream asn1InOctets = new ASN1InputStream(crlDEROctetString.getOctets())) {
                    ASN1Primitive crlDERObject = asn1InOctets.readObject();
                    distPoint = CRLDistPoint.getInstance(crlDERObject);
                }
            }
        } catch (IOException e) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_CERTIFICATE_NOT_READABLE,
                            rb.getResourceString("cert.read.error"));
            throw new CRLVerificationException(state, e);
        }
        List<String> crlURLList = new ArrayList<String>();
        //Loop through ASN1Encodable DistributionPoints
        for (DistributionPoint distributionPoint : distPoint.getDistributionPoints()) {
            //get ASN1Encodable DistributionPointName
            DistributionPointName singleDistributionPoint = distributionPoint.getDistributionPoint();
            if (singleDistributionPoint != null && singleDistributionPoint.getType() == DistributionPointName.FULL_NAME) {
                //Create ASN1Encodable General Names
                GeneralName[] generalNames = GeneralNames.getInstance(singleDistributionPoint.getName()).getNames();
                // Look for a URI
                for (GeneralName genName : generalNames) {
                    if (genName.getTagNo() == GeneralName.uniformResourceIdentifier) {
                        //DERIA5String contains an ascii string.
                        //A IA5String is a restricted character string type in the ASN.1 notation
                        String url = DERIA5String.getInstance(genName.getName()).getString().trim();
                        crlURLList.add(url);
                    }
                }
            }
        }
        if (crlURLList.isEmpty()) {
            CRLRevocationState state
                    = new CRLRevocationState(CRLRevocationState.STATE_UNABLE_TO_RETRIEVE_CRL_URL_FROM_CERTIFICATE,
                            rb.getResourceString("error.url.retrieve"));
            throw new CRLVerificationException(state);
        }
        return (crlURLList);
    }

}
