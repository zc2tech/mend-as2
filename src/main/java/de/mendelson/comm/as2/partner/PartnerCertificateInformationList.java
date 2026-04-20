package de.mendelson.comm.as2.partner;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.MecResourceBundle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
 * Stores a certificate or key used by a partner. Every partner of a
 * communication may use several certificates with several priorities
 *
 * @author S.Heller
 * @version $Revision: 28 $
 */
public class PartnerCertificateInformationList implements Serializable {

    private static final long serialVersionUID = 1L;
    //create empty container
    private final PartnerCertificateInformation infoSSL = new PartnerCertificateInformation(PartnerCertificateInformation.CATEGORY_TLS);
    //create empty container
    private final PartnerCertificateInformation infoCrypt = new PartnerCertificateInformation(PartnerCertificateInformation.CATEGORY_CRYPT);
    //create empty container
    private final PartnerCertificateInformation infoSign = new PartnerCertificateInformation(PartnerCertificateInformation.CATEGORY_SIGN);
    //create empty container
    private final PartnerCertificateInformation infoSignOverwriteLocalstation
            = new PartnerCertificateInformation(PartnerCertificateInformation.CATEGORY_SIGN_OVERWRITE_LOCALSTATION);
    private final PartnerCertificateInformation infoCryptOverwriteLocalstation
            = new PartnerCertificateInformation(PartnerCertificateInformation.CATEGORY_CRYPT_OVERWRITE_LOCALSTATION);
    private final static MecResourceBundle rb;
    static{
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificateInformation.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public PartnerCertificateInformationList() {
    }

    /**
     * Returns the right info container for the passed category
     */
    private PartnerCertificateInformation getContainerByCategory(int category) {
        if (category == 1) {
            return (this.infoCrypt);
        } else if (category == 2) {
            return (this.infoSign);
        } else if (category == 3) {
            return (this.infoSSL);
        } else if (category == PartnerCertificateInformation.CATEGORY_CRYPT_OVERWRITE_LOCALSTATION) {
            return (this.infoCryptOverwriteLocalstation);
        } else if (category == PartnerCertificateInformation.CATEGORY_SIGN_OVERWRITE_LOCALSTATION) {
            return (this.infoSignOverwriteLocalstation);
        } else {
            throw new IllegalArgumentException("PartnerCertificateInformationList.getContainerByCategory: Unsupported category " + category);
        }
    }

    /**
     * Sets a single cert information to the partner, overwriting any existing
     * with the same status, priority and type
     */
    public void setCertificateInformation(PartnerCertificateInformation information) {
        PartnerCertificateInformation container = this.getContainerByCategory(information.getCategory());
        container.setFingerprintSHA1(information.getFingerprintSHA1());
    }

    /**
     * Returns the partner certificate with the passed category, status and
     * priority. If nothing is found, null is returned
     */
    public PartnerCertificateInformation getPartnerCertificate(int category) {
        PartnerCertificateInformation container = this.getContainerByCategory(category);
        return (container);
    }

    /**
     * Sets a new certificate to the partner - of the specific category
     */
    public PartnerCertificateInformation setNewCertificate(String fingerprintSHA1, int category) {
        PartnerCertificateInformation container = this.getContainerByCategory(category);
        container.setFingerprintSHA1(fingerprintSHA1);
        return (container);
    }

    /**
     * Returns a string that contains information about the actual certificate usage
     */
    public String getCertificatePurposeDescription(CertificateManager manager, Partner partner, int category) {
        StringBuilder builder = new StringBuilder();
        PartnerCertificateInformation information = this.getPartnerCertificate(category);
        String alias = manager.getAliasByFingerprint(information.getFingerprintSHA1());
        if (partner.isLocalStation()) {
            if (category == PartnerCertificateInformation.CATEGORY_CRYPT) {
                builder.append(rb.getResourceString("localstation.decrypt",
                        new Object[]{partner.getName(), alias}));
            }
            if (category == PartnerCertificateInformation.CATEGORY_SIGN) {
                builder.append(rb.getResourceString("localstation.sign",
                        new Object[]{partner.getName(), alias}));
            }
        }
        return (builder.toString());
    }

    /**
     * Returns all available certificates as list
     */
    public Collection<PartnerCertificateInformation> asList() {
        int[] categories = new int[]{
            PartnerCertificateInformation.CATEGORY_CRYPT,
            PartnerCertificateInformation.CATEGORY_SIGN,
            PartnerCertificateInformation.CATEGORY_TLS,
            PartnerCertificateInformation.CATEGORY_SIGN_OVERWRITE_LOCALSTATION,
            PartnerCertificateInformation.CATEGORY_CRYPT_OVERWRITE_LOCALSTATION
        };
        List<PartnerCertificateInformation> list = new ArrayList<PartnerCertificateInformation>();
        for (int category : categories) {
            PartnerCertificateInformation container = this.getContainerByCategory(category);
            list.add(container);
        }
        return (list);
    }
}
