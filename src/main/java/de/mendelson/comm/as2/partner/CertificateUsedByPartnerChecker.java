package de.mendelson.comm.as2.partner;

import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.security.cert.CertificateInUseChecker;
import de.mendelson.util.security.cert.CertificateInUseInfo;
import de.mendelson.util.security.cert.KeystoreCertificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Checks if a certificate is in use by a partner
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class CertificateUsedByPartnerChecker implements CertificateInUseChecker {

    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificateUsedByPartnerChecker.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    private final BaseClient baseClient;
    private final Map<String, CertificateInUseInfo> infoMap = new ConcurrentHashMap<String, CertificateInUseInfo>();
    private final int userId; // User ID for filtering partners (0 = all users, >0 = specific user)

    public CertificateUsedByPartnerChecker(BaseClient baseClient) {
        this(baseClient, 0); // Default: check all users' partners
    }

    public CertificateUsedByPartnerChecker(BaseClient baseClient, int userId) {
        this.baseClient = baseClient;
        this.userId = userId;
        this.loadCertificateInUseInformation();
    }

    /**
     * Loads all the used certificates and their usage into the info Map
     */
    private void loadCertificateInUseInformation() {
        // DEBUG LOG: Request details
        System.out.println("=== CertificateUsedByPartnerChecker: Loading certificate usage info ===");
        System.out.println("CertificateUsedByPartnerChecker: userId filter = " + this.userId +
                         " (0=all users, >0=specific user)");

        PartnerListRequest request = new PartnerListRequest(PartnerListRequest.LIST_ALL);
        request.setUserId(this.userId);

        PartnerListResponse response = (PartnerListResponse) this.baseClient.sendSync(
                request,
                Partner.TIMEOUT_PARTNER_REQUEST);
        List<Partner> partnerList = response.getList();

        // DEBUG LOG: How many partners returned
        System.out.println("CertificateUsedByPartnerChecker: Received " + partnerList.size() + " partners");

        //build up list of all fingerprints that are in use
        List<String> fingerprintList = new ArrayList<String>();
        for (Partner singlePartner : partnerList) {
            String cryptFingerprint = singlePartner.getCryptFingerprintSHA1();
            String signFingerprint = singlePartner.getSignFingerprintSHA1();

            // DEBUG LOG: Each partner and their certificates
            System.out.println("  Partner: " + singlePartner.getName() +
                             " | isLocal: " + singlePartner.isLocalStation() +
                             " | Sign: " + signFingerprint +
                             " | Crypt: " + cryptFingerprint);

            if (cryptFingerprint != null && !fingerprintList.contains(cryptFingerprint)) {
                fingerprintList.add(cryptFingerprint);
            }
            if (signFingerprint != null && !fingerprintList.contains(signFingerprint)) {
                fingerprintList.add(signFingerprint);
            }
            if (!singlePartner.isLocalStation()
                    && singlePartner.isOverwriteLocalStationSecurity()) {
                String cryptOverwriteLocalFingerprint
                        = singlePartner.getCryptOverwriteLocalstationFingerprintSHA1();
                if (cryptOverwriteLocalFingerprint != null && !fingerprintList.contains(cryptOverwriteLocalFingerprint)) {
                    fingerprintList.add(cryptOverwriteLocalFingerprint);
                }
                String signOverwriteLocalFingerprint
                        = singlePartner.getSignOverwriteLocalstationFingerprintSHA1();
                if (signOverwriteLocalFingerprint != null && !fingerprintList.contains(signOverwriteLocalFingerprint)) {
                    fingerprintList.add(signOverwriteLocalFingerprint);
                }
            }
        }
        // DEBUG LOG: Total unique fingerprints
        System.out.println("CertificateUsedByPartnerChecker: Total unique fingerprints in use: " +
                          fingerprintList.size());
        for (String fp : fingerprintList) {
            System.out.println("  - " + fp);
        }

        for (String fingerPrintSHA1 : fingerprintList) {
            CertificateInUseInfo info = new CertificateInUseInfo(fingerPrintSHA1);
            for (Partner singlePartner : partnerList) {
                String cryptFingerprint = singlePartner.getCryptFingerprintSHA1();
                String signFingerprint = singlePartner.getSignFingerprintSHA1();
                int partnerType = CertificateInUseInfo.PARTNER_REMOTE;
                if (singlePartner.isLocalStation()) {
                    partnerType = CertificateInUseInfo.PARTNER_LOCALSTATION;
                }
                if (fingerPrintSHA1.equals(cryptFingerprint)) {
                    info.addUsage(partnerType,
                            singlePartner.getName(),
                            rb.getResourceString("used.crypt"));
                }
                if (fingerPrintSHA1.equals(signFingerprint)) {
                    info.addUsage(partnerType,
                            singlePartner.getName(),
                            rb.getResourceString("used.sign"));
                }
                if (!singlePartner.isLocalStation()
                        && singlePartner.isOverwriteLocalStationSecurity()) {
                    String cryptOverwriteLocalFingerprint
                            = singlePartner.getCryptOverwriteLocalstationFingerprintSHA1();
                    if (cryptOverwriteLocalFingerprint != null
                            && fingerPrintSHA1.equals(cryptOverwriteLocalFingerprint)) {
                        info.addUsage(partnerType,
                                singlePartner.getName(),
                                rb.getResourceString("used.crypt.overwritelocalsecurity"));
                    }
                    String signOverwriteLocalFingerprint
                            = singlePartner.getSignOverwriteLocalstationFingerprintSHA1();
                    if (signOverwriteLocalFingerprint != null
                            && fingerPrintSHA1.equals(signOverwriteLocalFingerprint)) {
                        info.addUsage(partnerType,
                                singlePartner.getName(),
                                rb.getResourceString("used.sign.overwritelocalsecurity"));
                    }
                }
            }
            this.infoMap.put(fingerPrintSHA1, info);
        }

    }

    /**
     * Checks if and for which cryptographic operation a passed certificated is
     * used in the product. Will return detailed information about the usage
     */
    @Override
    public CertificateInUseInfo checkUsed(KeystoreCertificate certificate) {
        String fingerPrintSHA1 = certificate.getFingerPrintSHA1();

        // DEBUG LOG: Check request
        System.out.println("CertificateUsedByPartnerChecker.checkUsed() called for: " +
                          certificate.getAlias() + " | Fingerprint: " + fingerPrintSHA1);

        if (this.infoMap.containsKey(fingerPrintSHA1)) {
            CertificateInUseInfo info = this.infoMap.get(fingerPrintSHA1);
            System.out.println("  -> FOUND IN USE (usage count: " + info.getUsageList().size() + ")");
            return info;
        } else {
            System.out.println("  -> NOT FOUND IN USE MAP (will be grayed out)");
            return (new CertificateInUseInfo(fingerPrintSHA1));
        }
    }

}
