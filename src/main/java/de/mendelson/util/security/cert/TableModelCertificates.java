package de.mendelson.util.security.cert;

import de.mendelson.util.ImageUtil;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.security.cert.gui.JDialogCertificates;
import de.mendelson.util.security.keygeneration.KeyGenerator;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * table model to display a configuration grid
 *
 * @author S.Heller
 * @version $Revision: 28 $
 */
public class TableModelCertificates extends AbstractTableModel {

    public static final int ROW_HEIGHT = JDialogCertificates.IMAGE_SIZE_TABLE + 3;
    protected static final int IMAGE_HEIGHT = JDialogCertificates.IMAGE_SIZE_TABLE;

    /**
     * Icons, multi resolution
     */
    public static final MendelsonMultiResolutionImage IMAGE_CERTIFICATE_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/certificate.svg",
                    IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_KEY_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/key.svg",
                    IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_INVALID_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/cert_invalid.svg",
                    IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_VALID_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/cert_valid.svg",
                    IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_ROOT_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/cert_root.svg",
                    IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_UNTRUSTED_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/cert_untrusted.svg",
                    IMAGE_HEIGHT);

    public static final ImageIcon ICON_CERTIFICATE
            = new ImageIcon(IMAGE_CERTIFICATE_MULTIRESOLUTION.toMinResolution(IMAGE_HEIGHT));
    public static final ImageIcon ICON_KEY
            = new ImageIcon(IMAGE_KEY_MULTIRESOLUTION.toMinResolution(IMAGE_HEIGHT));
    public static final ImageIcon ICON_VALID
            = new ImageIcon(IMAGE_VALID_MULTIRESOLUTION.toMinResolution(IMAGE_HEIGHT));
    public static final ImageIcon ICON_INVALID
            = new ImageIcon(IMAGE_INVALID_MULTIRESOLUTION.toMinResolution(IMAGE_HEIGHT));
    public static final ImageIcon ICON_CERTIFICATE_ROOT
            = new ImageIcon(IMAGE_ROOT_MULTIRESOLUTION.toMinResolution(IMAGE_HEIGHT));
    public static final ImageIcon ICON_CERTIFICATE_MISSING
            = new ImageIcon(IMAGE_UNTRUSTED_MULTIRESOLUTION.toMinResolution(IMAGE_HEIGHT));
    /*ResourceBundle to localize the headers*/
    private static final MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleTableModelCertificates.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    private final List<KeystoreCertificate> listData = Collections.synchronizedList(new ArrayList<KeystoreCertificate>());
    private final List<CertificateInUseChecker> inUseCheckerList
            = Collections.synchronizedList(new ArrayList<CertificateInUseChecker>());

    private static final Class<?>[] COLUMN_CLASSES = new Class<?>[]{
        ImageIcon.class,
        ImageIcon.class,
        String.class,
        Date.class,
        String.class,
        String.class,
        String.class,
        String.class};

    private CertificateManager certificateManager = null;

    /**
     * Creates new table model
     */
    public TableModelCertificates() {
    }

    public void setCertificateManager(CertificateManager certificateManager) {
        this.certificateManager = certificateManager;
    }

    /**
     * Adds an (optional) checker that will check if a certificate is in use. If
     * this is not set the checker functionality will simply be disabled and the
     * unused certificates will not be greyed out
     *
     * @param certificateInUseChecker The check that checks the configuration
     * for a used certificates
     */
    public void addCertificateInUseChecker(CertificateInUseChecker certificateInUseChecker) {
        synchronized (this.inUseCheckerList) {
            this.inUseCheckerList.add(certificateInUseChecker);
        }
    }

    /**
     * Passes data to the model and fires a table data update
     *
     */
    public void setNewData(List<KeystoreCertificate> data) {
        synchronized (this.listData) {
            this.listData.clear();
            this.listData.addAll(data);
        }
        ((AbstractTableModel) this).fireTableDataChanged();
    }

    public List<KeystoreCertificate> getCurrentCertificateList() {
        List<KeystoreCertificate> copyList = new ArrayList<KeystoreCertificate>();
        synchronized (this.listData) {
            copyList.addAll(this.listData);
        }
        return (copyList);
    }

    /**
     * returns the number of rows in the table
     */
    @Override
    public int getRowCount() {
        synchronized (this.listData) {
            return (this.listData.size());
        }
    }

    /**
     * returns the number of columns in the table. should be const for a table
     */
    @Override
    public int getColumnCount() {
        return (8);
    }

    /**
     * Returns the name of every column
     *
     * @param col Column to get the header name of
     */
    @Override
    public String getColumnName(int col) {
        if (col == 0) {
            return (" ");
        }
        if (col == 1) {
            return ("  ");
        }
        if (col == 2) {
            return (rb.getResourceString("header.alias"));
        }
        if (col == 3) {
            return (rb.getResourceString("header.expire"));
        }
        if (col == 4) {
            return (rb.getResourceString("header.algorithm"));
        }
        if (col == 5) {
            return (rb.getResourceString("header.length"));
        }
        if (col == 6) {
            return (rb.getResourceString("header.organization"));
        }
        if (col == 7) {
            return (rb.getResourceString("header.trust"));
        }
        //should not happen
        return ("");
    }

    /**
     * Returns the certificate/key image without any usage checker modifications
     * (grayed out)
     */
    public ImageIcon getUnmodifiedIconForCertificate(KeystoreCertificate certificate, int size) {
        ImageIcon icon;
        if (certificate.getIsKeyPair()) {
            icon = new ImageIcon(IMAGE_KEY_MULTIRESOLUTION.toMinResolution(size));
        } else if (certificate.isRootCertificate()) {
            icon = new ImageIcon(IMAGE_ROOT_MULTIRESOLUTION.toMinResolution(size));
        } else {
            icon = new ImageIcon(IMAGE_CERTIFICATE_MULTIRESOLUTION.toMinResolution(size));
        }
        return (icon);
    }

    private ImageIcon getIconForCertificate(KeystoreCertificate certificate) {
        ImageIcon icon = this.getUnmodifiedIconForCertificate(certificate, IMAGE_HEIGHT);

        synchronized (this.inUseCheckerList) {
            for (CertificateInUseChecker checker : this.inUseCheckerList) {
                CertificateInUseInfo info = checker.checkUsed(certificate);
                if (info.getUsageList().isEmpty()) {
                    icon = ImageUtil.grayImage(icon);
                    break;
                } 
            }
        }
        return (icon);
    }

    /**
     * Returns the grid value
     */
    @Override
    public Object getValueAt(int row, int col) {
        KeystoreCertificate certificate = null;
        synchronized (this.listData) {
            certificate = this.listData.get(row);
        }
        if (col == 0) {
            return (this.getIconForCertificate(certificate));
        }
        if (col == 1) {
            try {
                certificate.getX509Certificate().checkValidity();
                return (ICON_VALID);
            } catch (Exception e) {
                return (ICON_INVALID);
            }
        }
        if (col == 2) {
            return (certificate.getAlias());
        }
        if (col == 3) {
            return (certificate.getNotAfter());
        }
        if (col == 4) {
            StringBuilder builder = new StringBuilder();
            builder.append(certificate.getPublicKeyAlgorithm())
                    .append("/")
                    .append(KeyGenerator.signatureAlgorithmToDisplay(certificate.getSigAlgName()));
            if (certificate.getPublicKeyAlgorithm().equals("EC")) {
                ECPublicKey publicKey = (ECPublicKey) certificate.getX509Certificate().getPublicKey();
                try {
                    String curveName = certificate.getCurveName(publicKey);
                    if (!curveName.isEmpty()) {
                        curveName = curveName.substring(0, 1).toUpperCase() + curveName.substring(1);
                        builder.append("/")
                                .append(curveName);
                    }
                } catch (Throwable e) {
                }
            }
            return (builder.toString());
        }
        if (col == 5) {
            return (String.valueOf(certificate.getPublicKeyLength()));
        }
        if (col == 6) {
            String organization = certificate.getSubjectOrganization();
            if (organization == null || organization.trim().length() == 0) {
                organization = certificate.getSubjectCN();
            }
            return (organization);
        }
        if (col == 7) {
            if (certificate.isRootCertificate()) {
                return (rb.getResourceString("trust.root"));
            } else if (certificate.isSelfSigned()) {
                return (rb.getResourceString("trust.selfsigned"));
            } else {
                KeystoreCertificate trustAnchor = this.getTrustAnchor(certificate);
                if (trustAnchor == null) {
                    return (rb.getResourceString("trust.untrusted"));
                } else {
                    return (rb.getResourceString("trust.trusted"));
                }
            }
        }
        return ("");
    }

    /**
     * Swing GUI checks which cols are editable.
     */
    @Override
    public boolean isCellEditable(int row, int col
    ) {
        return (false);
    }

    /**
     * Set how to display the grid elements
     *
     * @param col requested column
     */
    @Override
    public Class<?> getColumnClass(int col) {
        return (COLUMN_CLASSES[col]);
    }

    /**
     * Returns the certificate at the passed row
     */
    public KeystoreCertificate getParameter(int row) {
        synchronized (this.listData) {
            return (this.listData.get(row));
        }
    }

    public KeystoreCertificate[] getCertificatesAsArray() {
        synchronized (this.listData) {
            KeystoreCertificate[] certArray = new KeystoreCertificate[this.listData.size()];
            certArray = this.listData.toArray(certArray);
            return (certArray);
        }
    }

    /**
     * Returns the trust anchor if a keystore manager exists - else null. If the
     * trust chain is not complete: returns null
     *
     * @return
     */
    private KeystoreCertificate getTrustAnchor(KeystoreCertificate certificate) {
        if (this.certificateManager == null) {
            return (null);
        }
        PKIXCertPathBuilderResult result = certificate.getPKIXCertPathBuilderResult(
                this.certificateManager.getKeystore(),
                this.certificateManager.getX509CertificateList());
        if (result != null) {
            Certificate trustAnchor = result.getTrustAnchor().getTrustedCert();
            for (KeystoreCertificate availableKeystoreCert : this.certificateManager.getKeyStoreCertificateList()) {
                if (trustAnchor.equals(availableKeystoreCert.getX509Certificate())) {
                    return( availableKeystoreCert);                    
                }
            }
        }
        return (null);
    }
}
