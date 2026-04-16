
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.util.security.cert.gui.keygeneration;

import de.mendelson.util.KeyboardShortcutUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.balloontip.BalloonToolTip;
import de.mendelson.util.security.cert.KeystoreStorageImplFile;
import de.mendelson.util.security.keygeneration.KeyGenerationValues;
import de.mendelson.util.security.keygeneration.KeyGenerator;
import de.mendelson.util.security.keylength.KeyLengthDisplay;
import de.mendelson.util.security.keylength.ListCellRendererKeyLength;
import de.mendelson.util.security.signature.ListCellRendererSignature;
import de.mendelson.util.security.signature.SignatureDisplay;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Dialog to work with certificates
 *
 * @author S.Heller
 * @version $Revision: 46 $
 */
public class JDialogGenerateKey extends JDialog {

    private static final String KEY_SIZE_1024 = "1024";
    private static final String KEY_SIZE_2048 = "2048";
    private static final String KEY_SIZE_4096 = "4096";

    private final static MendelsonMultiResolutionImage IMAGE_EDIT
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/security/cert/gui/keygeneration/edit.svg", 20);
    private final static MendelsonMultiResolutionImage IMAGE_KEY
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/security/cert/key.svg", 32);

    /**
     * ResourceBundle to localize the GUI
     */
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleGenerateKey.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }
    private KeyGenerationValues values = new KeyGenerationValues();
    private final String alias = "myalias";
    private final List<GeneralName> namesList = new ArrayList<GeneralName>();

    /**
     * Creates new form JDialogPartnerConfig
     *
     */
    public JDialogGenerateKey(JFrame parent) {
        this(parent, KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN);
    }

    /**
     * Creates new form JDialogPartnerConfig with keystore usage
     *
     * @param parent Parent frame
     * @param keystoreUsage KEYSTORE_USAGE_TLS or KEYSTORE_USAGE_ENC_SIGN
     */
    public JDialogGenerateKey(JFrame parent, int keystoreUsage) {
        super(parent, true);
        this.setTitle(rb.getResourceString("title"));
        initComponents();

        // Set default checkboxes based on keystore usage
        if (keystoreUsage == KeystoreStorageImplFile.KEYSTORE_USAGE_TLS) {
            this.jCheckBoxExtensionTLS.setSelected(true);
        } else if (keystoreUsage == KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN) {
            this.jCheckBoxExtensionSignEncrypt.setSelected(true);
        }

        this.jComboBoxKeySignature.setRenderer(new ListCellRendererSignature(this.jComboBoxKeySignature));
        this.jComboBoxKeySize.setRenderer(new ListCellRendererKeyLength(this.jComboBoxKeySize));
        this.setMultiresolutionIcons();
        //populate combo boxes
        this.jComboBoxKeyType.removeAllItems();
        this.jComboBoxKeyType.addItem(KeyGenerator.KEYALGORITHM_DSA);
        this.jComboBoxKeyType.addItem(KeyGenerator.KEYALGORITHM_RSA);
        this.jComboBoxKeyType.addItem(KeyGenerator.KEYALGORITHM_ECDSA);
        this.jComboBoxKeyType.addItem(KeyGenerator.KEYALGORITHM_DILITHIUM);
        this.jComboBoxKeyType.addItem(KeyGenerator.KEYALGORITHM_SPHINCSPLUS);
        this.jComboBoxKeyType.setSelectedItem(KeyGenerator.KEYALGORITHM_RSA);
        this.displayValues();
        Enumeration<?> enumeration = ECNamedCurveTable.getNames();
        List<String> curveNames = new ArrayList<String>();
        while (enumeration.hasMoreElements()) {
            String curveName = enumeration.nextElement().toString();
            if (!curveName.isEmpty()) {
                curveName = curveName.substring(0, 1).toUpperCase() + curveName.substring(1);
                curveNames.add(curveName);
            }
        }
        curveNames.add(KeyGenerator.CURVE_NAME_ED25519);
        Collections.sort(curveNames);
        this.jComboBoxECCurve.removeAllItems();
        for (String curveName : curveNames) {
            this.jComboBoxECCurve.addItem(curveName);
        }
        this.jComboBoxECCurve.setSelectedItem("Prime256v1");
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.setupKeyboardShortcuts();
        this.setViewMode();
        this.addWindowListener(
                new WindowAdapter() {
            //Windows closing is called when the user closes the window using a OS button, e.g. "X"
            @Override
            public void windowClosing(WindowEvent e) {
                values = null;
            }
        });
    }

    private void setupKeyboardShortcuts() {
        KeyboardShortcutUtil.setupDialogKeyBindingsWithTooltips(this, this.jButtonOk, this.jButtonCancel);
    }

    /**
     * Overwrite the designers icons by multi resolution icons
     */
    private void setMultiresolutionIcons() {
        this.jLabelIcon.setIcon(new ImageIcon(IMAGE_KEY));
        this.jButtonSubjectAlternativeNames.setIcon(new ImageIcon(IMAGE_EDIT));
    }

    private void setKeyRelatedValuesToCombobox() {
        String keyType = (String) this.jComboBoxKeyType.getSelectedItem();
        KeyLengthDisplay keySizePreselection = (KeyLengthDisplay) this.jComboBoxKeySize.getSelectedItem();
        SignatureDisplayValue signatureAlgorithmPreselection
                = (SignatureDisplayValue) this.jComboBoxKeySignature.getSelectedItem();
        if (keyType == null) {
            return;
        }
        this.jComboBoxKeySignature.removeAllItems();
        if (keyType.equals(KeyGenerator.KEYALGORITHM_ECDSA)) {
            // 10/2019: a key size of 512 will result in an invalid key size error
            // - this is the same as an asymmetric key size of 15360 bit which seems
            // not to be supported now
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jComboBoxKeySignature.removeAllItems();
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA256_WITH_ECDSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA384_WITH_ECDSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA512_WITH_ECDSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA3_256_WITH_ECDSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA3_384_WITH_ECDSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA3_512_WITH_ECDSA));
                    jPanelUIHelpLabelECCurve.setEnabled(true);
                    jComboBoxECCurve.setEnabled(true);
                    jComboBoxKeySize.setEnabled(false);
                    jPanelUIHelpLabelKeySize.setEnabled(false);
                    jComboBoxKeySize.setSelectedItem(keySizePreselection);
                    jComboBoxKeySignature.setSelectedItem(signatureAlgorithmPreselection);
                    if (jComboBoxKeySignature.getSelectedItem() == null) {
                        jComboBoxKeySignature.setSelectedItem(
                                new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA256_WITH_ECDSA));
                    }
                }
            });
        } else if (keyType.equals(KeyGenerator.KEYALGORITHM_DILITHIUM)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jComboBoxKeySignature.setEnabled(false);
                    jPanelUIHelpLabelECCurve.setEnabled(false);
                    jPanelUIHelpLabelKeySignature.setEnabled(false);
                    jComboBoxECCurve.setEnabled(false);
                    jComboBoxKeySize.setEnabled(false);
                    jPanelUIHelpLabelKeySize.setEnabled(false);
                }
            });
        } else if (keyType.equals(KeyGenerator.KEYALGORITHM_SPHINCSPLUS)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jComboBoxKeySignature.removeAllItems();
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA2_128S));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA2_128F));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA2_192S));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA2_192F));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA2_256S));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA2_256F));
                    jComboBoxKeySignature.setEnabled(true);
                    jPanelUIHelpLabelECCurve.setEnabled(false);
                    jPanelUIHelpLabelKeySignature.setEnabled(true);
                    jComboBoxECCurve.setEnabled(false);
                    jComboBoxKeySize.setEnabled(false);
                    jPanelUIHelpLabelKeySize.setEnabled(false);
                    jComboBoxKeySignature.setSelectedItem(signatureAlgorithmPreselection);
                    if (jComboBoxKeySignature.getSelectedItem() == null) {
                        jComboBoxKeySignature.setSelectedItem(
                                new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA2_128F));
                    }
                }
            });
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jComboBoxKeySize.removeAllItems();
                    jComboBoxKeySize.addItem(new KeyLengthDisplay(KEY_SIZE_1024));
                    jComboBoxKeySize.addItem(new KeyLengthDisplay(KEY_SIZE_2048));
                    jComboBoxKeySize.addItem(new KeyLengthDisplay(KEY_SIZE_4096));
                    jComboBoxKeySignature.removeAllItems();
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_MD5_WITH_RSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA1_WITH_RSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA256_WITH_RSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA256_WITH_RSA_RSASSA_PSS));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA512_WITH_RSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA512_WITH_RSA_RSASSA_PSS));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA3_256_WITH_RSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA3_256_WITH_RSA_RSASSA_PSS));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA3_512_WITH_RSA));
                    jComboBoxKeySignature.addItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA3_512_WITH_RSA_RSASSA_PSS));
                    jPanelUIHelpLabelECCurve.setEnabled(false);
                    jComboBoxECCurve.setEnabled(false);
                    jComboBoxKeySize.setEnabled(true);
                    jPanelUIHelpLabelKeySize.setEnabled(true);
                    jComboBoxKeySize.setSelectedItem(keySizePreselection);
                    if (jComboBoxKeySize.getSelectedItem() == null) {
                        jComboBoxKeySize.setSelectedItem(new KeyLengthDisplay(KEY_SIZE_2048));
                    }
                    jComboBoxKeySignature.setSelectedItem(signatureAlgorithmPreselection);
                    if (jComboBoxKeySignature.getSelectedItem() == null) {
                        jComboBoxKeySignature.setSelectedItem(new SignatureDisplayValue(KeyGenerator.SIGNATUREALGORITHM_SHA256_WITH_RSA));
                    }
                }
            });
        }

    }

    /**
     * Stores the actual GUI values in an object that could be accessed from
     * outside
     */
    private void captureGUIValues() {
        this.values.setCommonName(this.jTextFieldCommonName.getText().trim());
        this.values.setCountryCode(this.jTextFieldCountryCode.getText().trim());
        this.values.setEmailAddress(this.jTextFieldMailAddress.getText().trim());
        if (!this.jComboBoxECCurve.isEnabled()) {
            KeyLengthDisplay display = (KeyLengthDisplay) this.jComboBoxKeySize.getSelectedItem();
            this.values.setKeySize(Integer.parseInt(display.getWrappedValue()));
        } else {
            this.values.setKeySize(-1);
        }
        this.values.setKeyAlgorithm(this.jComboBoxKeyType.getSelectedItem().toString());
        this.values.setKeyValidInDays(Integer.parseInt(this.jTextFieldValidity.getText().trim()));
        this.values.setLocalityName(this.jTextFieldLocality.getText().trim());
        this.values.setOrganisationName(this.jTextFieldOrganisationName.getText().trim());
        this.values.setOrganisationUnit(this.jTextFieldOrganisationUnit.getText().trim());
        SignatureDisplayValue selectedsignature = (SignatureDisplayValue) this.jComboBoxKeySignature.getSelectedItem();
        if (selectedsignature != null) {
            this.values.setSignatureAlgorithm(selectedsignature.getSignatureAlgorithm());
        } else {
            this.values.setSignatureAlgorithm(null);
        }
        this.values.setStateName(this.jTextFieldState.getText().trim());
        if (this.jComboBoxECCurve.isEnabled()) {
            this.values.setECNamedCurve(this.jComboBoxECCurve.getSelectedItem().toString());
        }
        if (this.jCheckBoxExtensionSignEncrypt.isSelected() || this.jCheckBoxExtensionTLS.isSelected()) {
            this.values.setKeyExtension(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        }
        if (this.jCheckBoxExtensionTLS.isSelected()) {
            KeyPurposeId[] extKeyUsage = new KeyPurposeId[]{
                KeyPurposeId.id_kp_serverAuth,
                KeyPurposeId.id_kp_clientAuth
            };
            this.values.setExtendedKeyExtension(new ExtendedKeyUsage(extKeyUsage));
        }
        if (this.jCheckBoxExtensionSKI.isSelected()) {
            this.values.setGenerateSKI(true);
        }
        //Subject Alternative Name (German: Alternativer Antragstellername)
        for (GeneralName generalName : this.namesList) {
            this.values.addSubjectAlternativeName(generalName);
        }
    }

    private void displayValues() {
        this.jTextFieldCommonName.setText(this.getValues().getCommonName());
        this.jTextFieldCountryCode.setText(this.getValues().getCountryCode());
        this.jTextFieldLocality.setText(this.getValues().getLocalityName());
        this.jTextFieldMailAddress.setText(this.getValues().getEmailAddress());
        this.jTextFieldOrganisationName.setText(this.getValues().getOrganisationName());
        this.jTextFieldOrganisationUnit.setText(this.getValues().getOrganisationUnit());
        this.jTextFieldState.setText(this.getValues().getStateName());
        this.jTextFieldValidity.setText(String.valueOf(this.getValues().getKeyValidInDays()));
        this.jComboBoxKeySize.setSelectedItem(String.valueOf(this.getValues().getKeySize()));
        this.jComboBoxKeyType.setSelectedItem(this.getValues().getKeyAlgorithm());
        this.jComboBoxKeySignature.setSelectedItem(this.getValues().getSignatureAlgorithm());
    }

    public String getAlias() {
        return (this.alias);
    }

    /**
     * Checks the settings of the key generation
     */
    private boolean ignoreSettingProblems() {
        StringBuilder warning = new StringBuilder();
        String domain = this.jTextFieldCommonName.getText().trim();
        //if the CN is a wildcard entry it may have the structure "*.domain.com". In this case
        //the domain check for existence has to be performed on the main domain
        if (domain.startsWith("*.")) {
            domain = domain.substring(2);
        }
        String mail = this.jTextFieldMailAddress.getText();
        //check if domain exists
        try {
            @SuppressWarnings("unused")
            InetAddress address = InetAddress.getByName(domain);
        } catch (UnknownHostException e) {
            if (warning.length() > 0) {
                warning.append("\n\n");
            }
            warning.append(rb.getResourceString("warning.nonexisting.domain", domain));
        }
        //get the mail domain        
        int atIndex = mail.indexOf("@");
        if (atIndex < 0 || atIndex == mail.length() - 1) {
            if (warning.length() > 0) {
                warning.append("\n\n");
            }
            warning.append(rb.getResourceString("warning.invalid.mail", mail));
        } else {
            String mailDomain = mail.substring(atIndex + 1);
            if (!domain.endsWith(mailDomain)) {
                if (warning.length() > 0) {
                    warning.append("\n\n");
                }
                warning.append(rb.getResourceString("warning.mail.in.domain", domain));
            }
        }
        if (warning.length() > 0) {
            return (this.askUserToIgnoreSettingProblem(warning.toString()));
        } else {
            return (true);
        }
    }

    private boolean askUserToIgnoreSettingProblem(String warning) {
        String[] options = new String[]{
            rb.getResourceString("button.reedit"),
            rb.getResourceString("button.ignore"),};
        int requestValue = JOptionPane.showOptionDialog(this,
                warning,
                rb.getResourceString("warning.title"), JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null,
                options, options[0]);
        return (requestValue == 1);
    }

    private void setViewMode() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jTextFieldValidity.setEnabled(jToggleButtonExpert.isSelected());
                jTextFieldValidity.setEditable(jToggleButtonExpert.isSelected());
                jComboBoxKeyType.setEnabled(jToggleButtonExpert.isSelected());
                jComboBoxKeySignature.setEnabled(jToggleButtonExpert.isSelected());
                jComboBoxKeySize.setEnabled(jToggleButtonExpert.isSelected());
                jCheckBoxExtensionTLS.setEnabled(jToggleButtonExpert.isSelected());
                jCheckBoxExtensionSignEncrypt.setEnabled(jToggleButtonExpert.isSelected());
                jCheckBoxExtensionSKI.setEnabled(jToggleButtonExpert.isSelected());
                jLabelPurpose.setEnabled(jToggleButtonExpert.isSelected());
                jLabelSubjectAlternativeNames.setEnabled(jToggleButtonExpert.isSelected());
                jTextFieldSubjectAlternativeNames.setEnabled(jToggleButtonExpert.isSelected());
                jButtonSubjectAlternativeNames.setEnabled(jToggleButtonExpert.isSelected());
                setKeyRelatedValuesToCombobox();
            }
        });
    }

    private void editSubjectAlternativeNames() {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        JDialogEditSubjectAlternativeNames dialog = new JDialogEditSubjectAlternativeNames(parentFrame, this.namesList);
        dialog.setVisible(true);
        //display names list in text field
        StringBuilder namesSerialized = new StringBuilder();
        for (GeneralName generalName : this.namesList) {
            if (namesSerialized.length() > 0) {
                namesSerialized.append("; ");
            }
            int tagNo = generalName.getTagNo();
            namesSerialized.append(TagNo.intValueToString(tagNo));
            namesSerialized.append("=");
            //IP addresses are sometimes stored as DEROctetString which would result in a single hex value on display
            // - this has to be decoded for the display
            if (generalName.getName() instanceof DEROctetString && tagNo == GeneralName.iPAddress) {
                DEROctetString str = (DEROctetString) generalName.getName();
                StringBuilder decStr = new StringBuilder();
                byte[] octets = str.getOctets();
                for (byte octet : octets) {
                    if (decStr.length() > 0) {
                        decStr.append(".");
                    }
                    decStr.append((int) (octet & 0xFF));
                }
                namesSerialized.append(decStr);
            } else {
                namesSerialized.append(generalName.getName().toString());
            }
        }
        this.jTextFieldSubjectAlternativeNames.setText(namesSerialized.toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelEdit = new javax.swing.JPanel();
        jPanelEditInner = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jLabelOrganisationUnit = new javax.swing.JLabel();
        jComboBoxKeySize = new javax.swing.JComboBox<>();
        jComboBoxKeySignature = new javax.swing.JComboBox<>();
        jComboBoxKeyType = new javax.swing.JComboBox<>();
        jTextFieldValidity = new javax.swing.JTextField();
        jTextFieldMailAddress = new javax.swing.JTextField();
        jTextFieldState = new javax.swing.JTextField();
        jTextFieldOrganisationName = new javax.swing.JTextField();
        jTextFieldOrganisationUnit = new javax.swing.JTextField();
        jLabelOrganisationName = new javax.swing.JLabel();
        jLabelLocality = new javax.swing.JLabel();
        jLabelState = new javax.swing.JLabel();
        jLabelCountryCode = new javax.swing.JLabel();
        jCheckBoxExtensionSignEncrypt = new javax.swing.JCheckBox();
        jCheckBoxExtensionTLS = new javax.swing.JCheckBox();
        jLabelPurpose = new javax.swing.JLabel();
        jPanelLocality = new javax.swing.JPanel();
        jTextFieldLocality = new javax.swing.JTextField();
        jLabelLocalityHint = new javax.swing.JLabel();
        jPanelCommonName = new javax.swing.JPanel();
        jTextFieldCommonName = new javax.swing.JTextField();
        jLabelCommonNameHint = new javax.swing.JLabel();
        jPanelCountryCode = new javax.swing.JPanel();
        jLabelCountryCodeHint = new javax.swing.JLabel();
        jTextFieldCountryCode = new javax.swing.JTextField();
        jPanelSAN = new javax.swing.JPanel();
        jTextFieldSubjectAlternativeNames = new javax.swing.JTextField();
        jButtonSubjectAlternativeNames = new javax.swing.JButton();
        jLabelSubjectAlternativeNames = new javax.swing.JLabel();
        jPanelUIHelpLabelKeyType = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelKeySignature = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelKeySize = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelCommonName = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelMailaddress = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelValidity = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jComboBoxECCurve = new javax.swing.JComboBox<>();
        jPanelUIHelpLabelECCurve = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelSKI = new javax.swing.JPanel();
        jCheckBoxExtensionSKI = new javax.swing.JCheckBox();
        jPanelUIHelpExtensionSKI = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jToolBar = new javax.swing.JToolBar();
        jToggleButtonBasic = new javax.swing.JToggleButton();
        jToggleButtonExpert = new javax.swing.JToggleButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jPanelEditInner.setLayout(new java.awt.GridBagLayout());

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/keygeneration/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        jPanelEditInner.add(jLabelIcon, gridBagConstraints);

        jLabelOrganisationUnit.setText(JDialogGenerateKey.rb.getResourceString( "label.organisationunit"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jLabelOrganisationUnit, gridBagConstraints);

        jComboBoxKeySize.setMinimumSize(new java.awt.Dimension(170, 24));
        jComboBoxKeySize.setPreferredSize(new java.awt.Dimension(170, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jComboBoxKeySize, gridBagConstraints);

        jComboBoxKeySignature.setMinimumSize(new java.awt.Dimension(170, 24));
        jComboBoxKeySignature.setPreferredSize(new java.awt.Dimension(170, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jComboBoxKeySignature, gridBagConstraints);

        jComboBoxKeyType.setMinimumSize(new java.awt.Dimension(170, 24));
        jComboBoxKeyType.setPreferredSize(new java.awt.Dimension(170, 24));
        jComboBoxKeyType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxKeyTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jComboBoxKeyType, gridBagConstraints);

        jTextFieldValidity.setMaximumSize(new java.awt.Dimension(50, 22));
        jTextFieldValidity.setPreferredSize(new java.awt.Dimension(50, 22));
        jTextFieldValidity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldValidityKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jTextFieldValidity, gridBagConstraints);

        jTextFieldMailAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldMailAddressKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jTextFieldMailAddress, gridBagConstraints);

        jTextFieldState.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldStateKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jTextFieldState, gridBagConstraints);

        jTextFieldOrganisationName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldOrganisationNameKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jTextFieldOrganisationName, gridBagConstraints);

        jTextFieldOrganisationUnit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldOrganisationUnitKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jTextFieldOrganisationUnit, gridBagConstraints);

        jLabelOrganisationName.setText(JDialogGenerateKey.rb.getResourceString( "label.organisationname"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jLabelOrganisationName, gridBagConstraints);

        jLabelLocality.setText(JDialogGenerateKey.rb.getResourceString( "label.locality"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jLabelLocality, gridBagConstraints);

        jLabelState.setText(JDialogGenerateKey.rb.getResourceString( "label.state"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jLabelState, gridBagConstraints);

        jLabelCountryCode.setText(JDialogGenerateKey.rb.getResourceString( "label.countrycode"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jLabelCountryCode, gridBagConstraints);

        jCheckBoxExtensionSignEncrypt.setText(JDialogGenerateKey.rb.getResourceString( "label.purpose.encsign"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelEditInner.add(jCheckBoxExtensionSignEncrypt, gridBagConstraints);

        jCheckBoxExtensionTLS.setText(JDialogGenerateKey.rb.getResourceString( "label.purpose.ssl"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelEditInner.add(jCheckBoxExtensionTLS, gridBagConstraints);

        jLabelPurpose.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPurpose.setText(JDialogGenerateKey.rb.getResourceString( "label.purpose"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(30, 5, 5, 5);
        jPanelEditInner.add(jLabelPurpose, gridBagConstraints);

        jPanelLocality.setLayout(new java.awt.GridBagLayout());

        jTextFieldLocality.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldLocalityKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelLocality.add(jTextFieldLocality, gridBagConstraints);

        jLabelLocalityHint.setText(JDialogGenerateKey.rb.getResourceString("label.locality.hint"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelLocality.add(jLabelLocalityHint, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelEditInner.add(jPanelLocality, gridBagConstraints);

        jPanelCommonName.setLayout(new java.awt.GridBagLayout());

        jTextFieldCommonName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldCommonNameKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelCommonName.add(jTextFieldCommonName, gridBagConstraints);

        jLabelCommonNameHint.setText(JDialogGenerateKey.rb.getResourceString("label.commonname.hint"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelCommonName.add(jLabelCommonNameHint, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelEditInner.add(jPanelCommonName, gridBagConstraints);

        jPanelCountryCode.setLayout(new java.awt.GridBagLayout());

        jLabelCountryCodeHint.setText(JDialogGenerateKey.rb.getResourceString("label.countrycode.hint"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelCountryCode.add(jLabelCountryCodeHint, gridBagConstraints);

        jTextFieldCountryCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldCountryCodeKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelCountryCode.add(jTextFieldCountryCode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelEditInner.add(jPanelCountryCode, gridBagConstraints);

        jPanelSAN.setLayout(new java.awt.GridBagLayout());

        jTextFieldSubjectAlternativeNames.setEditable(false);
        jTextFieldSubjectAlternativeNames.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSubjectAlternativeNamesKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSAN.add(jTextFieldSubjectAlternativeNames, gridBagConstraints);

        jButtonSubjectAlternativeNames.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/keygeneration/missing_image24x24.gif"))); // NOI18N
        jButtonSubjectAlternativeNames.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonSubjectAlternativeNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubjectAlternativeNamesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSAN.add(jButtonSubjectAlternativeNames, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelEditInner.add(jPanelSAN, gridBagConstraints);

        jLabelSubjectAlternativeNames.setText(JDialogGenerateKey.rb.getResourceString( "label.subjectalternativenames"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jLabelSubjectAlternativeNames, gridBagConstraints);

        jPanelUIHelpLabelKeyType.setToolTipText(JDialogGenerateKey.rb.getResourceString( "label.keytype.help"));
        jPanelUIHelpLabelKeyType.setText(JDialogGenerateKey.rb.getResourceString( "label.keytype"));
        jPanelUIHelpLabelKeyType.setTooltipWidth(300);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanelEditInner.add(jPanelUIHelpLabelKeyType, gridBagConstraints);

        jPanelUIHelpLabelKeySignature.setToolTipText(JDialogGenerateKey.rb.getResourceString( "label.signature.help"));
        jPanelUIHelpLabelKeySignature.setText(JDialogGenerateKey.rb.getResourceString( "label.signature"));
        jPanelUIHelpLabelKeySignature.setTooltipWidth(300);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanelEditInner.add(jPanelUIHelpLabelKeySignature, gridBagConstraints);

        jPanelUIHelpLabelKeySize.setToolTipText(JDialogGenerateKey.rb.getResourceString( "label.size.help"));
        jPanelUIHelpLabelKeySize.setText(JDialogGenerateKey.rb.getResourceString( "label.size"));
        jPanelUIHelpLabelKeySize.setTooltipWidth(300);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanelEditInner.add(jPanelUIHelpLabelKeySize, gridBagConstraints);

        jPanelUIHelpLabelCommonName.setToolTipText(JDialogGenerateKey.rb.getResourceString( "label.commonname.help"));
        jPanelUIHelpLabelCommonName.setText(JDialogGenerateKey.rb.getResourceString( "label.commonname"));
        jPanelUIHelpLabelCommonName.setTooltipWidth(300);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanelEditInner.add(jPanelUIHelpLabelCommonName, gridBagConstraints);

        jPanelUIHelpLabelMailaddress.setToolTipText(JDialogGenerateKey.rb.getResourceString( "label.mailaddress.help"));
        jPanelUIHelpLabelMailaddress.setText(JDialogGenerateKey.rb.getResourceString( "label.mailaddress"));
        jPanelUIHelpLabelMailaddress.setTooltipWidth(300);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanelEditInner.add(jPanelUIHelpLabelMailaddress, gridBagConstraints);

        jPanelUIHelpLabelValidity.setToolTipText(JDialogGenerateKey.rb.getResourceString( "label.validity.help"));
        jPanelUIHelpLabelValidity.setText(JDialogGenerateKey.rb.getResourceString( "label.validity"));
        jPanelUIHelpLabelValidity.setTooltipWidth(300);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanelEditInner.add(jPanelUIHelpLabelValidity, gridBagConstraints);

        jComboBoxECCurve.setMinimumSize(new java.awt.Dimension(180, 24));
        jComboBoxECCurve.setPreferredSize(new java.awt.Dimension(180, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEditInner.add(jComboBoxECCurve, gridBagConstraints);

        jPanelUIHelpLabelECCurve.setToolTipText(JDialogGenerateKey.rb.getResourceString( "label.namedeccurve.help"));
        jPanelUIHelpLabelECCurve.setText(JDialogGenerateKey.rb.getResourceString( "label.namedeccurve"));
        jPanelUIHelpLabelECCurve.setTooltipWidth(175);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelEditInner.add(jPanelUIHelpLabelECCurve, gridBagConstraints);

        jPanelSKI.setLayout(new java.awt.GridBagLayout());

        jCheckBoxExtensionSKI.setText(JDialogGenerateKey.rb.getResourceString( "label.extension.ski"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelSKI.add(jCheckBoxExtensionSKI, gridBagConstraints);

        jPanelUIHelpExtensionSKI.setToolTipText(JDialogGenerateKey.rb.getResourceString( "label.extension.ski.help" ));
        jPanelUIHelpExtensionSKI.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanelUIHelpExtensionSKI.setTooltipWidth(250);
        jPanelUIHelpExtensionSKI.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 14;
        jPanelSKI.add(jPanelUIHelpExtensionSKI, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelEditInner.add(jPanelSKI, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelEdit.add(jPanelEditInner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelEdit, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(JDialogGenerateKey.rb.getResourceString( "button.ok" ));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonOk, gridBagConstraints);

        jButtonCancel.setText(JDialogGenerateKey.rb.getResourceString( "button.cancel" ));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        jToggleButtonBasic.setSelected(true);
        jToggleButtonBasic.setText(JDialogGenerateKey.rb.getResourceString( "view.basic"));
        jToggleButtonBasic.setFocusable(false);
        jToggleButtonBasic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonBasic.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonBasic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonBasicActionPerformed(evt);
            }
        });
        jToolBar.add(jToggleButtonBasic);

        jToggleButtonExpert.setText(JDialogGenerateKey.rb.getResourceString( "view.expert"));
        jToggleButtonExpert.setFocusable(false);
        jToggleButtonExpert.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonExpert.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonExpert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonExpertActionPerformed(evt);
            }
        });
        jToolBar.add(jToggleButtonExpert);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(jToolBar, gridBagConstraints);

        setSize(new java.awt.Dimension(634, 778));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.values = null;
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        if (!this.ignoreSettingProblems()) {
            return;
        }
        this.captureGUIValues();
        this.setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jTextFieldValidityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldValidityKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldValidityKeyReleased

    private void jTextFieldMailAddressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldMailAddressKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldMailAddressKeyReleased

    private void jTextFieldCountryCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCountryCodeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCountryCodeKeyReleased

    private void jTextFieldStateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldStateKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldStateKeyReleased

    private void jTextFieldLocalityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldLocalityKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldLocalityKeyReleased

    private void jTextFieldOrganisationNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldOrganisationNameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldOrganisationNameKeyReleased

    private void jTextFieldOrganisationUnitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldOrganisationUnitKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldOrganisationUnitKeyReleased

    private void jTextFieldCommonNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCommonNameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCommonNameKeyReleased

    private void jToggleButtonBasicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonBasicActionPerformed
        this.jToggleButtonExpert.setSelected(!this.jToggleButtonBasic.isSelected());
        this.setViewMode();
    }//GEN-LAST:event_jToggleButtonBasicActionPerformed

    private void jToggleButtonExpertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonExpertActionPerformed
        this.jToggleButtonBasic.setSelected(!this.jToggleButtonExpert.isSelected());
        this.setViewMode();
    }//GEN-LAST:event_jToggleButtonExpertActionPerformed

    private void jComboBoxKeyTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxKeyTypeActionPerformed
        this.setKeyRelatedValuesToCombobox();
    }//GEN-LAST:event_jComboBoxKeyTypeActionPerformed

    private void jTextFieldSubjectAlternativeNamesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSubjectAlternativeNamesKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSubjectAlternativeNamesKeyReleased

    private void jButtonSubjectAlternativeNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubjectAlternativeNamesActionPerformed
        this.editSubjectAlternativeNames();
    }//GEN-LAST:event_jButtonSubjectAlternativeNamesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonSubjectAlternativeNames;
    private javax.swing.JCheckBox jCheckBoxExtensionSKI;
    private javax.swing.JCheckBox jCheckBoxExtensionSignEncrypt;
    private javax.swing.JCheckBox jCheckBoxExtensionTLS;
    private javax.swing.JComboBox<String> jComboBoxECCurve;
    private javax.swing.JComboBox<SignatureDisplayValue> jComboBoxKeySignature;
    private javax.swing.JComboBox<KeyLengthDisplay> jComboBoxKeySize;
    private javax.swing.JComboBox<String> jComboBoxKeyType;
    private javax.swing.JLabel jLabelCommonNameHint;
    private javax.swing.JLabel jLabelCountryCode;
    private javax.swing.JLabel jLabelCountryCodeHint;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelLocality;
    private javax.swing.JLabel jLabelLocalityHint;
    private javax.swing.JLabel jLabelOrganisationName;
    private javax.swing.JLabel jLabelOrganisationUnit;
    private javax.swing.JLabel jLabelPurpose;
    private javax.swing.JLabel jLabelState;
    private javax.swing.JLabel jLabelSubjectAlternativeNames;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCommonName;
    private javax.swing.JPanel jPanelCountryCode;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelEditInner;
    private javax.swing.JPanel jPanelLocality;
    private javax.swing.JPanel jPanelSAN;
    private javax.swing.JPanel jPanelSKI;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpExtensionSKI;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelCommonName;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelECCurve;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelKeySignature;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelKeySize;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelKeyType;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelMailaddress;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelValidity;
    private javax.swing.JTextField jTextFieldCommonName;
    private javax.swing.JTextField jTextFieldCountryCode;
    private javax.swing.JTextField jTextFieldLocality;
    private javax.swing.JTextField jTextFieldMailAddress;
    private javax.swing.JTextField jTextFieldOrganisationName;
    private javax.swing.JTextField jTextFieldOrganisationUnit;
    private javax.swing.JTextField jTextFieldState;
    private javax.swing.JTextField jTextFieldSubjectAlternativeNames;
    private javax.swing.JTextField jTextFieldValidity;
    private javax.swing.JToggleButton jToggleButtonBasic;
    private javax.swing.JToggleButton jToggleButtonExpert;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the values
     */
    public KeyGenerationValues getValues() {
        return values;
    }

    private static class SignatureDisplayValue extends SignatureDisplay {

        /**
         * Icons, multi resolution
         */
        public final static MendelsonMultiResolutionImage IMAGE_SIGNATURE_STRONG
                = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/signature/signature_strong.svg",
                        ListCellRendererSignature.IMAGE_HEIGHT);
        public final static MendelsonMultiResolutionImage IMAGE_SIGNATURE_WEAK
                = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/signature/signature_weak.svg",
                        ListCellRendererSignature.IMAGE_HEIGHT);
        public final static MendelsonMultiResolutionImage IMAGE_SIGNATURE_BROKEN
                = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/signature/signature_broken.svg",
                        ListCellRendererSignature.IMAGE_HEIGHT);

        private String signatureAlgorithm = null;

        public SignatureDisplayValue(String signatureAlgorithm) {
            super(signatureAlgorithm);
            this.signatureAlgorithm = signatureAlgorithm;
        }

        /**
         * Overwrite the equal method of object
         *
         * @param anObject object to compare
         */
        @Override
        public boolean equals(Object anObject) {
            if (anObject == this) {
                return (true);
            }
            if (anObject != null && anObject instanceof SignatureDisplayValue) {
                SignatureDisplayValue value = (SignatureDisplayValue) anObject;
                return (value.signatureAlgorithm.equalsIgnoreCase(this.signatureAlgorithm));
            }
            return (false);
        }

        @Override
        public String toString() {
            return (KeyGenerator.signatureAlgorithmToDisplay(this.signatureAlgorithm));
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + Objects.hashCode(this.signatureAlgorithm);
            return hash;
        }

        /**
         * @return the signatureAlgorithm
         */
        public String getSignatureAlgorithm() {
            return signatureAlgorithm;
        }

        @Override
        public ImageIcon getIcon() {
            String signAlgorithm = (String) this.getWrappedValue();
            if (signAlgorithm.equals(KeyGenerator.SIGNATUREALGORITHM_MD5_WITH_RSA)) {
                return (new ImageIcon(IMAGE_SIGNATURE_BROKEN.toMinResolution(ListCellRendererSignature.IMAGE_HEIGHT)));
            } else if (signAlgorithm.equals(KeyGenerator.SIGNATUREALGORITHM_SHA1_WITH_RSA)) {
                return (new ImageIcon(IMAGE_SIGNATURE_WEAK.toMinResolution(ListCellRendererSignature.IMAGE_HEIGHT)));
            } else {
                return (new ImageIcon(IMAGE_SIGNATURE_STRONG.toMinResolution(ListCellRendererSignature.IMAGE_HEIGHT)));
            }
        }

        @Override
        public String getText() {
            return (this.toString());
        }
    }
}
