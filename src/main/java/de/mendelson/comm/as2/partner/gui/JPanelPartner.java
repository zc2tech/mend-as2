package de.mendelson.comm.as2.partner.gui;

import de.mendelson.comm.as2.client.IconManager;
import de.mendelson.comm.as2.client.AS2StatusBar;
import de.mendelson.util.security.signature.ListCellRendererSignature;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.partner.HTTPAuthentication;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerCertificateInformation;
import de.mendelson.comm.as2.partner.PartnerEventInformation;
import de.mendelson.comm.as2.partner.PartnerHttpHeader;
import de.mendelson.comm.as2.partner.PartnerInboundAuthCredential;
import de.mendelson.comm.as2.partner.PartnerSystem;
import de.mendelson.comm.as2.partner.gui.event.JDialogConfigureEventMoveToDir;
import de.mendelson.comm.as2.partner.gui.event.JDialogConfigureEventMoveToPartner;
import de.mendelson.comm.as2.partner.gui.event.JDialogConfigureEventShell;
import de.mendelson.comm.as2.partner.gui.event.ResourceBundlePartnerEvent;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.send.HttpConnectionParameter;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.ButtonUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.balloontip.BalloonToolTip;
import de.mendelson.util.balloontip.JPanelUIHelpLabel;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.clientserver.clients.filesystemview.FileSystemViewRequest;
import de.mendelson.util.clientserver.clients.filesystemview.FileSystemViewResponse;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
import de.mendelson.util.clientserver.connectiontest.ConnectionTest;
import de.mendelson.util.clientserver.connectiontest.clientserver.ConnectionTestRequest;
import de.mendelson.util.clientserver.connectiontest.clientserver.ConnectionTestResponse;
import de.mendelson.util.clientserver.connectiontest.gui.JDialogConnectionTestResult;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.ListCellRendererCertificates;
import de.mendelson.util.security.encryption.EncryptionConstantsAS2;
import de.mendelson.util.security.encryption.EncryptionDisplayImplAS2;
import de.mendelson.util.security.encryption.ListCellRendererEncryption;
import de.mendelson.util.security.signature.SignatureConstantsAS2;
import de.mendelson.util.security.signature.SignatureDisplayImplAS2;
import de.mendelson.util.toggleswitch.ToggleSwitch;
import de.mendelson.util.uinotification.UINotification;
import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import de.mendelson.util.wizard.category.Category;
import de.mendelson.util.wizard.category.JDialogCategorySelection;
import de.mendelson.util.wizard.category.Subcategory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Panel to edit a single partner
 *
 * @author S.Heller
 * @version $Revision: 218 $
 */
public class JPanelPartner extends JPanel {

    private final String STR_CONTENT_TRANSFER_ENCODING_BINARY = "binary";
    private final String STR_CONTENT_TRANSFER_ENCODING_BASE64 = "base64";
    private final static MecResourceBundle rb;
    private final static MecResourceBundle rbEvents;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePartnerPanel.class.getName());
            rbEvents = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePartnerEvent.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    /**
     * Partner to edit
     */
    private Partner partner = null;
    private DefaultMutableTreeNode partnerNode = null;
    private final JTreePartner tree;
    private final CertificateManager certificateManagerEncSign;
    private final CertificateManager certificateManagerSSL;
    private JButtonPartnerConfigOk buttonOk = null;
    private final PreferencesClient preferences;
    private final JPanel jPanelConfigurationWarning;
    private boolean displayNotificationPanel = false;
    private boolean displayHttpHeaderPanel = false;
    private boolean displayOverwriteLocalstationSecurity = false;
    private final BaseClient baseClient;
    private final AS2StatusBar statusbar;
    private String serverSideFileSeparator = "/";
    private String serverSideMessageDirectoryAbsolute = "messages";
    /**
     * Store all available partner system requests
     */
    private final Map<String, PartnerSystem> partnerSystemMap = new HashMap<String, PartnerSystem>();
    /**
     * Stores the last selection of the tab panels if a new partner is set
     */
    private Component lastSelectedPanel = null;
    /**
     * Visibility panel reference (for remote partners only)
     */

    private final static MendelsonMultiResolutionImage IMAGE_DELETE = MendelsonMultiResolutionImage.fromSVG(
            "/de/mendelson/comm/as2/partner/gui/delete.svg",
            IconManager.IMAGE_SIZE_TOOLBAR);
    private final static MendelsonMultiResolutionImage IMAGE_ADD = MendelsonMultiResolutionImage.fromSVG(
            "/de/mendelson/comm/as2/partner/gui/add.svg",
            IconManager.IMAGE_SIZE_TOOLBAR);
    private final static MendelsonMultiResolutionImage IMAGE_EDIT = MendelsonMultiResolutionImage.fromSVG(
            "/de/mendelson/comm/as2/partner/gui/event/edit.svg",
            IconManager.IMAGE_SIZE_TOOLBAR);
    private final static MendelsonMultiResolutionImage IMAGE_TESTCONNECTION = MendelsonMultiResolutionImage.fromSVG(
            "/de/mendelson/comm/as2/partner/gui/testconnection.svg",
            IconManager.IMAGE_SIZE_TOOLBAR);
    private final static MendelsonMultiResolutionImage IMAGE_SYNC_MDN = MendelsonMultiResolutionImage
            .fromSVG("/de/mendelson/comm/as2/partner/gui/sync_mdn.svg", 90, 130);
    private final static MendelsonMultiResolutionImage IMAGE_ASYNC_MDN = MendelsonMultiResolutionImage
            .fromSVG("/de/mendelson/comm/as2/partner/gui/async_mdn.svg", 90, 130);

    private final String activatedPlugins;

    /**
     * Creates new form JPanelFunctionGraph
     */
    public JPanelPartner(BaseClient baseClient, JTreePartner tree,
            CertificateManager certificateManagerEncSign,
            CertificateManager certificateManagerTLS,
            JButtonPartnerConfigOk buttonOk,
            AS2StatusBar statusbar, boolean changesAllowed,
            List<PartnerSystem> partnerSystemList, String activatedPlugins,
            JPanel jPanelConfigurationWarning) {
        this.statusbar = statusbar;
        this.baseClient = baseClient;
        this.tree = tree;
        this.buttonOk = buttonOk;
        this.activatedPlugins = activatedPlugins;
        this.jPanelConfigurationWarning = jPanelConfigurationWarning;
        this.preferences = new PreferencesClient(baseClient, Partner.TIMEOUT_PARTNER_REQUEST);
        this.initComponents();
        TextOverlay.addTo(this.jTextFieldIgnorePollFilterList,
                rb.getResourceString("label.pollignore.hint"));
        TextOverlay.addTo(this.jTextFieldName,
                rb.getResourceString("label.name.hint"));
        TextOverlay.addTo(this.jTextFieldId,
                rb.getResourceString("label.id.hint"));
        TextOverlay.addTo(this.jTextFieldEMail,
                rb.getResourceString("label.email.hint"));
        PasswordOverlay.addTo(this.jPasswordFieldHttpAuthMessagePass);
        PasswordOverlay.addTo(this.jPasswordFieldHttpPassAsyncMDN);
        this.setMultiresolutionIcons();
        ButtonUtil.reformatButtonText(this.jButtonTestConnection);
        this.initializeHelp();
        this.buttonOk.initialize(tree, this.jTextFieldName,
                this.jTextFieldId, this.jTextFieldReceiptURL, this.jTextFieldMDNURL,
                changesAllowed, this.jPanelConfigurationWarning);
        // some disabled checkboxes should still have black text: wrapp their text in
        // html tags
        this.jCheckBoxEdiintFeaturesCEM.setText("<html>" + this.jCheckBoxEdiintFeaturesCEM.getText() + "</html>");
        this.jCheckBoxEdiintFeaturesCompression
                .setText("<html>" + this.jCheckBoxEdiintFeaturesCompression.getText() + "</html>");
        this.jCheckBoxEdiintFeaturesMA.setText("<html>" + this.jCheckBoxEdiintFeaturesMA.getText() + "</html>");
        this.jTextAreaPartnerSystemInformation.setText(rb.getResourceString("partnerinfo"));
        this.jComboBoxContentTransferEncoding.removeAllItems();
        this.jComboBoxContentTransferEncoding.addItem(STR_CONTENT_TRANSFER_ENCODING_BINARY);
        this.jComboBoxContentTransferEncoding.addItem(STR_CONTENT_TRANSFER_ENCODING_BASE64);
        this.jComboBoxHTTPProtocolVersion.removeAllItems();
        this.jComboBoxHTTPProtocolVersion.addItem(HttpConnectionParameter.HTTP_1_0);
        this.jComboBoxHTTPProtocolVersion.addItem(HttpConnectionParameter.HTTP_1_1);
        this.certificateManagerEncSign = certificateManagerEncSign;
        this.certificateManagerSSL = certificateManagerTLS;
        this.jComboBoxSignType.setRenderer(new ListCellRendererSignature(this.jComboBoxSignType));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_NONE)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA1)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_MD5)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA256)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA384)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA512)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA1_RSASSA_PSS)));
        this.jComboBoxSignType.addItem(
                new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA256_RSASSA_PSS)));
        this.jComboBoxSignType.addItem(
                new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA384_RSASSA_PSS)));
        this.jComboBoxSignType.addItem(
                new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA512_RSASSA_PSS)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA3_224)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA3_256)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA3_384)));
        this.jComboBoxSignType
                .addItem(new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA3_512)));
        this.jComboBoxSignType.addItem(
                new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA3_224_RSASSA_PSS)));
        this.jComboBoxSignType.addItem(
                new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA3_256_RSASSA_PSS)));
        this.jComboBoxSignType.addItem(
                new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA3_384_RSASSA_PSS)));
        this.jComboBoxSignType.addItem(
                new SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SHA3_512_RSASSA_PSS)));
        // this.jComboBoxSignType.addItem(new
        // SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_DILITHIUM)));
        // this.jComboBoxSignType.addItem(new
        // SignatureDisplayImplAS2(Integer.valueOf(SignatureConstantsAS2.SIGNATURE_SPHINCS_PLUS)));

        this.jComboBoxEncryptionType.setRenderer(new ListCellRendererEncryption(this.jComboBoxEncryptionType));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_NONE)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_3DES)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_128_CBC)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_192_CBC)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_256_CBC)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_128_CCM)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_192_CCM)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_256_CCM)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_128_GCM)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_192_GCM)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_256_GCM)));
        this.jComboBoxEncryptionType.addItem(new EncryptionDisplayImplAS2(
                Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_128_CBC_RSAES_AOEP)));
        this.jComboBoxEncryptionType.addItem(new EncryptionDisplayImplAS2(
                Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_192_CBC_RSAES_AOEP)));
        this.jComboBoxEncryptionType.addItem(new EncryptionDisplayImplAS2(
                Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_256_CBC_RSAES_AOEP)));
        this.jComboBoxEncryptionType.addItem(new EncryptionDisplayImplAS2(
                Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_128_GCM_RSAES_AOEP)));
        this.jComboBoxEncryptionType.addItem(new EncryptionDisplayImplAS2(
                Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_192_GCM_RSAES_AOEP)));
        this.jComboBoxEncryptionType.addItem(new EncryptionDisplayImplAS2(
                Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_AES_256_GCM_RSAES_AOEP)));
        this.jComboBoxEncryptionType.addItem(
                new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_CAMELLIA_128_CBC)));
        this.jComboBoxEncryptionType.addItem(
                new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_CAMELLIA_192_CBC)));
        this.jComboBoxEncryptionType.addItem(
                new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_CAMELLIA_256_CBC)));
        this.jComboBoxEncryptionType.addItem(
                new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_CHACHA20_POLY1305)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_DES)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_RC2_40)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_RC2_64)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_RC2_128)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_RC2_196)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_RC4_40)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_RC4_56)));
        this.jComboBoxEncryptionType
                .addItem(new EncryptionDisplayImplAS2(Integer.valueOf(EncryptionConstantsAS2.ENCRYPTION_RC4_128)));
        List<KeystoreCertificate> encSignCertificateList = this.certificateManagerEncSign.getKeyStoreCertificateList();
        // clone the array
        List<KeystoreCertificate> sortedEncSignCertificateList = new ArrayList<KeystoreCertificate>();
        sortedEncSignCertificateList.addAll(encSignCertificateList);
        Collections.sort(sortedEncSignCertificateList);

        // Get TLS certificates for HTTP authentication
        List<KeystoreCertificate> tlsCertificateList = this.certificateManagerSSL.getKeyStoreCertificateList();
        List<KeystoreCertificate> sortedTLSCertificateList = new ArrayList<KeystoreCertificate>();
        sortedTLSCertificateList.addAll(tlsCertificateList);
        Collections.sort(sortedTLSCertificateList);

        this.jComboBoxSignCert.setRenderer(new ListCellRendererCertificates());
        this.jComboBoxCryptCert.setRenderer(new ListCellRendererCertificates());
        this.jComboBoxOverwriteLocalStationCryptKey.setRenderer(new ListCellRendererCertificates());
        this.jComboBoxOverwriteLocalstationSignKey.setRenderer(new ListCellRendererCertificates());
        this.jComboBoxHttpAuthCertMessage.setRenderer(new ListCellRendererCertificates());
        this.jComboBoxHttpAuthCertMDN.setRenderer(new ListCellRendererCertificates());

        // Populate sign/encrypt certificate dropdowns
        for (KeystoreCertificate cert : sortedEncSignCertificateList) {
            this.jComboBoxSignCert.addItem(cert);
            this.jComboBoxCryptCert.addItem(cert);
            if (cert.getIsKeyPair()) {
                this.jComboBoxOverwriteLocalStationCryptKey.addItem(cert);
                this.jComboBoxOverwriteLocalstationSignKey.addItem(cert);
            }
        }

        // Populate HTTP auth certificate dropdowns with BOTH Sign/Crypt AND TLS certificates
        // (need private key for client auth)
        // Add placeholder "-- Select Certificate --" as first item
        KeystoreCertificate placeholderCert = new KeystoreCertificate();
        placeholderCert.setAlias("-- Select Certificate --");
        this.jComboBoxHttpAuthCertMessage.addItem(placeholderCert);
        this.jComboBoxHttpAuthCertMDN.addItem(placeholderCert);

        // Add Sign/Crypt certificates with private keys
        for (KeystoreCertificate cert : sortedEncSignCertificateList) {
            if (cert.getIsKeyPair()) {
                this.jComboBoxHttpAuthCertMessage.addItem(cert);
                this.jComboBoxHttpAuthCertMDN.addItem(cert);
            }
        }

        // Add TLS certificates with private keys
        for (KeystoreCertificate cert : sortedTLSCertificateList) {
            if (cert.getIsKeyPair()) {
                this.jComboBoxHttpAuthCertMessage.addItem(cert);
                this.jComboBoxHttpAuthCertMDN.addItem(cert);
            }
        }
        this.jTableHttpHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                jButtonHttpHeaderRemove.setEnabled(jTableHttpHeader.getSelectedRow() >= 0);
            }
        });
        this.jTableHttpHeader.getTableHeader().setReorderingAllowed(false);
        // figure out the server side file separator
        FileSystemViewRequest request = new FileSystemViewRequest(FileSystemViewRequest.TYPE_GET_FILE_SEPARATOR);
        FileSystemViewResponse response = (FileSystemViewResponse) this.baseClient.sendSync(request,
                Partner.TIMEOUT_PARTNER_REQUEST);
        this.serverSideFileSeparator = response.getParameterString();
        this.serverSideMessageDirectoryAbsolute = this.preferences.get(PreferencesAS2.DIR_MSG);
        String[] dirRequestResult = this.getAbsolutePathOnServerSide(this.serverSideMessageDirectoryAbsolute);
        this.serverSideMessageDirectoryAbsolute = dirRequestResult[0];
        // build cache for the PartnerSystems
        for (PartnerSystem partnerSystem : partnerSystemList) {
            this.partnerSystemMap.put(partnerSystem.getPartner().getAS2Identification(), partnerSystem);
        }
    }

    /**
     * Returns if the passed plugin is activated
     */
    public boolean isPluginActivated(final String PLUGIN) {
        return (this.activatedPlugins != null && this.activatedPlugins.contains(PLUGIN));
    }

    /**
     * Updates the text in a TextPane without triggering a document listener.
     * Its important not to remove _all_ listeners but just the listeners that
     * update the partner stuff. If all listeners are removed the widget will
     * show weird behavior (update problems, freeze on unicode content etc
     *
     * @param textPane    Textpane to set the initial text in and add a new
     *                    document listener afterwards
     * @param initialText Text to set to the passed text pane - without
     *                    triggering a partner update event
     * @param consumer    Consumer that is called for any upcoming change in the
     *                    passed textPane
     */
    private void setUIValueWithoutEvent(JTextPane textPane, String initialText,
            Consumer<String> consumer) {
        if (textPane.getDocument() instanceof AbstractDocument) {
            synchronized (textPane.getDocument()) {
                DocumentListener[] listeners = ((AbstractDocument) textPane.getDocument()).getDocumentListeners();
                for (DocumentListener listener : listeners) {
                    if (listener instanceof DocumentListenerComment) {
                        textPane.getDocument().removeDocumentListener(listener);
                    }
                }
                textPane.setText(initialText);
                textPane.getDocument().addDocumentListener(new DocumentListenerComment(textPane, consumer));
            }
        } else {
            // unable to remove the listeners..
            textPane.setText(initialText);
        }
    }

    /**
     * Sets a switch value without triggering an event
     *
     * @param toggleSwitch to select/deselect
     */
    private void setUIValueWithoutEvent(ToggleSwitch toggleSwitch, boolean state) {
        ActionListener[] actionListener = toggleSwitch.getActionListeners();
        for (ActionListener listener : actionListener) {
            toggleSwitch.removeActionListener(listener);
        }
        toggleSwitch.setSelected(state);
        for (ActionListener listener : actionListener) {
            toggleSwitch.addActionListener(listener);
        }
    }

    /**
     * Sets a checkbox value without triggering an event
     *
     * @param checkbox to select/deselect
     */
    private void setUIValueWithoutEvent(JCheckBox checkbox, boolean state) {
        ActionListener[] actionListener = checkbox.getActionListeners();
        for (ActionListener listener : actionListener) {
            checkbox.removeActionListener(listener);
        }
        checkbox.setSelected(state);
        for (ActionListener listener : actionListener) {
            checkbox.addActionListener(listener);
        }
    }

    /**
     * Sets a combo box value value without triggering an event
     *
     * @param combobox to set the item in
     */
    private void setUIValueWithoutEvent(JComboBox<?> combobox, Object item) {
        ActionListener[] actionListener = combobox.getActionListeners();
        for (ActionListener listener : actionListener) {
            combobox.removeActionListener(listener);
        }
        combobox.setSelectedItem(item);
        // selected item is not in the list - select the first item if this is possible
        if (combobox.getSelectedIndex() == -1 && combobox.getItemCount() > 0) {
            combobox.setSelectedIndex(0);
        }
        for (ActionListener listener : actionListener) {
            combobox.addActionListener(listener);
        }
    }

    /**
     * Sets a radio button value value without triggering an event
     *
     */
    private void setUIValueWithoutEvent(JRadioButton radioButton, boolean state) {
        // Remove both ActionListeners and ItemListeners to prevent events during programmatic changes
        ActionListener[] actionListeners = radioButton.getActionListeners();
        for (ActionListener listener : actionListeners) {
            radioButton.removeActionListener(listener);
        }
        java.awt.event.ItemListener[] itemListeners = radioButton.getItemListeners();
        for (java.awt.event.ItemListener listener : itemListeners) {
            radioButton.removeItemListener(listener);
        }

        // Set the state
        radioButton.setSelected(state);

        // Re-add all listeners
        for (ActionListener listener : actionListeners) {
            radioButton.addActionListener(listener);
        }
        for (java.awt.event.ItemListener listener : itemListeners) {
            radioButton.addItemListener(listener);
        }
    }

    private void setMultiresolutionIcons() {
        this.jButtonHttpHeaderAdd.setIcon(
                new ImageIcon(IMAGE_ADD.toMinResolution(IconManager.IMAGE_SIZE_TOOLBAR)));
        this.jButtonHttpHeaderRemove.setIcon(
                new ImageIcon(IMAGE_DELETE.toMinResolution(IconManager.IMAGE_SIZE_TOOLBAR)));
        this.jButtonTestConnection.setIcon(
                new ImageIcon(IMAGE_TESTCONNECTION.toMinResolution(IconManager.IMAGE_SIZE_TOOLBAR)));
        this.jLabelIconAsyncMDN.setIcon(
                new ImageIcon(IMAGE_ASYNC_MDN.toMinResolution(90)));
        this.jLabelIconSyncMDN.setIcon(
                new ImageIcon(IMAGE_SYNC_MDN.toMinResolution(90)));
        this.jButtonAddEventOnReceipt.setIcon(
                new ImageIcon(IMAGE_ADD.toMinResolution(IconManager.IMAGE_SIZE_MENU_ITEM)));
        this.jButtonEditEventOnReceipt.setIcon(
                new ImageIcon(IMAGE_EDIT.toMinResolution(IconManager.IMAGE_SIZE_MENU_ITEM)));
        this.jButtonAddEventOnSendError.setIcon(
                new ImageIcon(IMAGE_ADD.toMinResolution(IconManager.IMAGE_SIZE_MENU_ITEM)));
        this.jButtonEditEventOnSendError.setIcon(
                new ImageIcon(IMAGE_EDIT.toMinResolution(IconManager.IMAGE_SIZE_MENU_ITEM)));
        this.jButtonAddEventOnSendSuccess.setIcon(
                new ImageIcon(IMAGE_ADD.toMinResolution(IconManager.IMAGE_SIZE_MENU_ITEM)));
        this.jButtonEditEventOnSendSuccess.setIcon(
                new ImageIcon(IMAGE_EDIT.toMinResolution(IconManager.IMAGE_SIZE_MENU_ITEM)));
    }

    private void initializeHelp() {
    }

    private void testConnection() {
        final String uniqueId = this.getClass().getName() + ".testConnection." + System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JDialogPartnerConfig parentDialog = (JDialogPartnerConfig) SwingUtilities
                        .getAncestorOfClass(JDialogPartnerConfig.class, JPanelPartner.this);
                try {
                    parentDialog.lock();
                    // display wait indicator
                    JPanelPartner.this.statusbar.startProgressIndeterminate(
                            JPanelPartner.rb.getResourceString("label.test.connection"), uniqueId);
                    String urlStr = JPanelPartner.this.jTextFieldReceiptURL.getText();
                    URL url = java.net.URI.create(urlStr).toURL();
                    int port = 80;
                    if (url.getPort() > 0) {
                        // will be -1 by default if no specified...
                        port = url.getPort();
                    }
                    // get connection timeout from server preferences
                    long connectionTimeoutInMS = JPanelPartner.this.preferences
                            .getInt(PreferencesAS2.HTTP_SEND_TIMEOUT);
                    ConnectionTestRequest request = new ConnectionTestRequest(url.getHost(),
                            port, url.getProtocol().equalsIgnoreCase("https"),
                            JPanelPartner.this.jTextFieldName.getText(),
                            ConnectionTest.PARTNER_ROLE_REMOTE_PARTNER);
                    request.setTimeout(connectionTimeoutInMS);
                    ConnectionTestResponse response = (ConnectionTestResponse) JPanelPartner.this.baseClient
                            .sendSync(request);
                    if (response.getException() != null) {
                        throw response.getException();
                    }
                    JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, JPanelPartner.this);
                    JDialogConnectionTestResult dialog = new JDialogConnectionTestResult(parent,
                            JDialogConnectionTestResult.CONNECTION_TEST_AS2,
                            response.getLogEntries(),
                            response.getResult(),
                            JPanelPartner.this.certificateManagerEncSign, JPanelPartner.this.certificateManagerSSL,
                            JPanelPartner.this.preferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT));
                    JPanelPartner.this.statusbar.stopProgressIfExists(uniqueId);
                    parentDialog.unlock();
                    dialog.setVisible(true);
                } catch (Throwable e) {
                    JPanelPartner.this.statusbar.stopProgressIfExists(uniqueId);
                    UINotification.instance().addNotification(e);
                } finally {
                    JPanelPartner.this.statusbar.stopProgressIfExists(uniqueId);
                    parentDialog.unlock();
                }
            }
        };
        GUIClient.submit(runnable);
    }

    protected void setDisplayOverwriteLocalstationSecurity(boolean display) {
        this.displayOverwriteLocalstationSecurity = display;
    }

    protected void setDisplayNotificationPanel(boolean display) {
        this.displayNotificationPanel = display;
    }

    protected void setDisplayHttpHeaderPanel(boolean display) {
        this.displayHttpHeaderPanel = display;
    }

    /**
     * Informs the partner tree model that a node value has been changed
     */
    private void informTreeModelNodeChanged() {
        ((DefaultTreeModel) this.tree.getModel()).nodeChanged(this.partnerNode);
    }

    /**
     * Edits a passed partner
     */
    public void setPartner(Partner partner, DefaultMutableTreeNode selectedNode) {
        if (this.lastSelectedPanel == null) {
            this.lastSelectedPanel = this.jPanelMisc;
        } else {
            this.lastSelectedPanel = this.jTabbedPane.getSelectedComponent();
        }
        this.partnerNode = selectedNode;
        this.partner = partner;
        this.buttonOk.setPartner(partner);
        this.buttonOk.computeErrorState();
        this.jTextFieldId.setText(partner.getAS2Identification());
        this.jTextFieldName.setText(partner.getName());

        // For remote partners, only set Receipt URL if it's not the default value
        // This keeps the field empty for new remote partners
        if (partner.isLocalStation()) {
            this.jTextFieldReceiptURL.setText(partner.getURL());
        } else {
            // For remote partners, only populate if URL is not default
            String url = partner.getURL();
            if (url != null && !url.equals(partner.getDefaultURL())) {
                this.jTextFieldReceiptURL.setText(url);
            } else {
                this.jTextFieldReceiptURL.setText(partner.getDefaultURL());
            }
        }

        this.jTextFieldMDNURL.setText(partner.getMdnURL());
        this.jTextFieldEMail.setText(partner.getEmail());
        this.setUIValueWithoutEvent(this.switchLocalStation, partner.isLocalStation());

        // Refresh certificate dropdowns based on local station status (must be before
        // setting cert values)
        this.refreshCertificateDropdowns();
        this.setUIValueWithoutEvent(this.jComboBoxSignCert,
                this.certificateManagerEncSign.getKeystoreCertificateByFingerprintSHA1(
                        partner.getSignFingerprintSHA1()));
        this.setUIValueWithoutEvent(this.jComboBoxCryptCert,
                this.certificateManagerEncSign.getKeystoreCertificateByFingerprintSHA1(
                        partner.getCryptFingerprintSHA1()));
        if (partner.isLocalStation()) {
            this.jPanelOverwriteLocalStationSecurity.setVisible(false);
            this.jPanelUIHelpLabelCryptAlias.setText(rb.getResourceString("label.cryptalias.key"));
            this.jPanelUIHelpLabelCryptAlias.setToolTipText(rb.getResourceString("label.cryptalias.key.help"));
            this.jPanelUIHelpLabelSignAlias.setText(rb.getResourceString("label.signalias.key"));
            this.jPanelUIHelpLabelSignAlias.setToolTipText(rb.getResourceString("label.signalias.key.help"));
        } else {
            this.jPanelOverwriteLocalStationSecurity.setVisible(this.displayOverwriteLocalstationSecurity
                    || partner.isOverwriteLocalStationSecurity());
            this.setUIValueWithoutEvent(this.jRadioButtonKeepLocalstationSecurity,
                    !this.partner.isOverwriteLocalStationSecurity());
            this.setUIValueWithoutEvent(this.jRadioButtonOverwriteLocalstationSecurity,
                    this.partner.isOverwriteLocalStationSecurity());
            this.setUIValueWithoutEvent(this.jComboBoxOverwriteLocalStationCryptKey,
                    this.certificateManagerEncSign.getKeystoreCertificateByFingerprintSHA1(
                            this.partner.getCryptOverwriteLocalstationFingerprintSHA1()));
            this.setUIValueWithoutEvent(this.jComboBoxOverwriteLocalstationSignKey,
                    this.certificateManagerEncSign.getKeystoreCertificateByFingerprintSHA1(
                            this.partner.getSignOverwriteLocalstationFingerprintSHA1()));
            this.jPanelUIHelpLabelCryptAlias.setText(rb.getResourceString("label.cryptalias.cert"));
            this.jPanelUIHelpLabelCryptAlias.setToolTipText(rb.getResourceString("label.cryptalias.cert.help"));
            this.jPanelUIHelpLabelSignAlias.setText(rb.getResourceString("label.signalias.cert"));
            this.jPanelUIHelpLabelSignAlias.setToolTipText(rb.getResourceString("label.signalias.cert.help"));

        }
        this.setUIValueWithoutEvent(this.jComboBoxSignType,
                new SignatureDisplayImplAS2(Integer.valueOf(partner.getSignType())));
        this.setUIValueWithoutEvent(this.jComboBoxEncryptionType,
                new EncryptionDisplayImplAS2(Integer.valueOf(partner.getEncryptionType())));
        this.jTextFieldSubject.setText(partner.getSubject());
        this.jTextFieldContentType.setText(partner.getContentType());
        this.setUIValueWithoutEvent(this.jRadioButtonSyncMDN, partner.isSyncMDN());
        this.setUIValueWithoutEvent(this.jRadioButtonAsyncMDN, !partner.isSyncMDN());
        this.jLabelIconSyncMDN.setEnabled(partner.isSyncMDN());
        this.jLabelIconAsyncMDN.setEnabled(!partner.isSyncMDN());
        this.setUIValueWithoutEvent(this.switchSignedMDN, partner.isSignedMDN());
        this.updatePollDirDisplay(this.partner);
        String pollIgnoreList = this.partner.getPollIgnoreListAsString();
        if (pollIgnoreList == null) {
            this.jTextFieldIgnorePollFilterList.setText("");
        } else {
            this.jTextFieldIgnorePollFilterList.setText(pollIgnoreList);
        }
        this.jTextFieldPollMaxFiles.setText(String.valueOf(this.partner.getMaxPollFiles()));
        this.jTextFieldPollInterval.setText(String.valueOf(this.partner.getPollInterval()));
        this.setUIValueWithoutEvent(this.switchCompress,
                this.partner.getCompressionType() == AS2Message.COMPRESSION_ZLIB);
        // Set HTTP Auth mode for Message
        int authModeMessage = this.partner.getAuthenticationCredentialsMessage().getAuthMode();
        if (authModeMessage == HTTPAuthentication.AUTH_MODE_BASIC) {
            this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthCredentialsMessage, true);
        } else if (authModeMessage == HTTPAuthentication.AUTH_MODE_USER_PREFERENCE) {
            this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthUserPreferenceMessage, true);
        } else if (authModeMessage == HTTPAuthentication.AUTH_MODE_CERTIFICATE) {
            this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthCertificateMessage, true);
            // Set selected certificate (use Sign/Crypt certificate manager for HTTP auth)
            String fingerprint = this.partner.getAuthenticationCredentialsMessage().getCertificateFingerprint();
            if (fingerprint != null && !fingerprint.isEmpty()) {
                KeystoreCertificate cert = this.certificateManagerEncSign.getKeystoreCertificateByFingerprintSHA1(fingerprint);
                if (cert != null) {
                    this.setUIValueWithoutEvent(this.jComboBoxHttpAuthCertMessage, cert);
                } else {
                    // Fingerprint in DB but certificate not found in keystore - select placeholder
                    this.setUIValueWithoutEvent(this.jComboBoxHttpAuthCertMessage, this.jComboBoxHttpAuthCertMessage.getItemAt(0));
                }
            } else {
                // Fingerprint is empty - select placeholder (no auto-selection)
                if (this.jComboBoxHttpAuthCertMessage.getItemCount() > 0) {
                    this.setUIValueWithoutEvent(this.jComboBoxHttpAuthCertMessage, this.jComboBoxHttpAuthCertMessage.getItemAt(0));
                }
            }
        } else {
            this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthNoneMessage, true);
        }
        // Set HTTP Auth mode for Async MDN
        int authModeMDN = this.partner.getAuthenticationCredentialsAsyncMDN().getAuthMode();
        if (authModeMDN == HTTPAuthentication.AUTH_MODE_BASIC) {
            this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthCredentialsMDN, true);
        } else if (authModeMDN == HTTPAuthentication.AUTH_MODE_USER_PREFERENCE) {
            this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthUserPreferenceMDN, true);
        } else if (authModeMDN == HTTPAuthentication.AUTH_MODE_CERTIFICATE) {
            this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthCertificateMDN, true);
            // Set selected certificate (use Sign/Crypt certificate manager for HTTP auth)
            String fingerprintMDN = this.partner.getAuthenticationCredentialsAsyncMDN().getCertificateFingerprint();
            if (fingerprintMDN != null && !fingerprintMDN.isEmpty()) {
                KeystoreCertificate cert = this.certificateManagerEncSign.getKeystoreCertificateByFingerprintSHA1(fingerprintMDN);
                if (cert != null) {
                    this.setUIValueWithoutEvent(this.jComboBoxHttpAuthCertMDN, cert);
                } else {
                    // Fingerprint in DB but certificate not found in keystore - select placeholder
                    this.setUIValueWithoutEvent(this.jComboBoxHttpAuthCertMDN, this.jComboBoxHttpAuthCertMDN.getItemAt(0));
                }
            } else {
                // Fingerprint is empty - select placeholder (no auto-selection)
                if (this.jComboBoxHttpAuthCertMDN.getItemCount() > 0) {
                    this.setUIValueWithoutEvent(this.jComboBoxHttpAuthCertMDN, this.jComboBoxHttpAuthCertMDN.getItemAt(0));
                }
            }
        } else {
            this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthNoneMDN, true);
        }
        this.jTextFieldHttpAuthMessageUser.setText(this.partner.getAuthenticationCredentialsMessage().getUser());
        this.jPasswordFieldHttpAuthMessagePass
                .setText(this.partner.getAuthenticationCredentialsMessage().getPassword());
        this.jTextFieldHttpAuthAsyncMDNUser.setText(this.partner.getAuthenticationCredentialsAsyncMDN().getUser());
        this.jPasswordFieldHttpPassAsyncMDN.setText(this.partner.getAuthenticationCredentialsAsyncMDN().getPassword());

        // Load Inbound Auth (for local stations only)
        if (partner.isLocalStation()) {
            // Load credentials into both tables
            modelInboundAuthBasic.passNewData(partner);
            modelInboundAuthCert.passNewData(partner);

            // Set toggle switches based on saved enabled state
            boolean basicEnabled = partner.isInboundAuthBasicEnabled();
            boolean certEnabled = partner.isInboundAuthCertEnabled();
            this.setUIValueWithoutEvent(this.switchInboundAuthBasic, basicEnabled);
            this.setUIValueWithoutEvent(this.switchInboundAuthCert, certEnabled);

            updateInboundAuthButtonStates();
        }

        this.setUIValueWithoutEvent(this.switchKeepFilenameOnReceipt, this.partner.getKeepOriginalFilenameOnReceipt());
        if (this.partner.getComment() != null && !this.partner.getComment().isEmpty()) {
            this.setUIValueWithoutEvent(this.jTextPanePartnerComment,
                    this.partner.getComment(),
                    this.partner::setComment);
        } else {
            this.setUIValueWithoutEvent(this.jTextPanePartnerComment,
                    "",
                    this.partner::setComment);
        }
        if (this.partner.getContactCompany() != null && !this.partner.getContactCompany().isEmpty()) {
            this.setUIValueWithoutEvent(this.jTextPanePartnerAddress,
                    this.partner.getContactCompany(),
                    this.partner::setContactCompany);
        } else {
            this.setUIValueWithoutEvent(this.jTextPanePartnerAddress,
                    "",
                    this.partner::setContactCompany);
        }
        if (this.partner.getContactAS2() != null && !this.partner.getContactAS2().isEmpty()) {
            this.setUIValueWithoutEvent(this.jTextPanePartnerContact,
                    this.partner.getContactAS2(),
                    this.partner::setContactAS2);
        } else {
            this.setUIValueWithoutEvent(this.jTextPanePartnerContact,
                    "",
                    this.partner::setContactAS2);
        }
        this.setUIValueWithoutEvent(this.jCheckBoxNotifySend, this.partner.isNotifySendEnabled());
        this.setUIValueWithoutEvent(this.jCheckBoxNotifyReceive, this.partner.isNotifyReceiveEnabled());
        this.setUIValueWithoutEvent(this.jCheckBoxNotifySendReceive, this.partner.isNotifySendReceiveEnabled());
        this.jTextFieldNotifySend.setText(String.valueOf(this.partner.getNotifySend()));
        this.jTextFieldNotifyReceive.setText(String.valueOf(this.partner.getNotifyReceive()));
        this.jTextFieldNotifySendReceive.setText(String.valueOf(this.partner.getNotifySendReceive()));
        if (this.partner.getContentTransferEncoding() == AS2Message.CONTENT_TRANSFER_ENCODING_BINARY) {
            this.setUIValueWithoutEvent(this.jComboBoxContentTransferEncoding, STR_CONTENT_TRANSFER_ENCODING_BINARY);
        } else {
            this.setUIValueWithoutEvent(this.jComboBoxContentTransferEncoding, STR_CONTENT_TRANSFER_ENCODING_BASE64);
        }
        this.updatePartnerSystemInformation(this.partner);
        if (this.displayHttpHeaderPanel) {
            ((TableModelHttpHeader) this.jTableHttpHeader.getModel()).passNewData(partner);
        }
        this.setUIValueWithoutEvent(this.jComboBoxHTTPProtocolVersion, partner.getHttpProtocolVersion());
        this.setUIValueWithoutEvent(this.switchUseAlgorithmIdentifierProtectionAttribute,
                partner.getUseAlgorithmIdentifierProtectionAttribute());
        this.setUIValueWithoutEvent(this.switchEnableDirPoll, partner.isEnableDirPoll());
        this.handleVisibilityStateOfWidgets();
        this.disableEnableWidgets();
        // HTTP Auth radio buttons are already set correctly above (lines 596-639)
        // No need to set them again here - this old code was overwriting
        // certificate/user-preference modes
        this.updateHttpAuthState();
        this.setPanelVisiblilityState();
        this.renderEvents();
        try {
            if (this.lastSelectedPanel != null) {
                this.jTabbedPane.setSelectedComponent(this.lastSelectedPanel);
            }
        } catch (Exception e) {
            // ignore, not every panel that was selected for the last partner must be
            // available for this
            // partner
        }
        this.setButtonState();
    }

    /**
     * Sets the visibility state depending if the partner is local station or
     * not. Has to be called every time the local station state changes.
     */
    private void handleVisibilityStateOfWidgets() {
        this.jTextFieldMDNURL.setVisible(this.partner.isLocalStation());
        this.jPanelMDNURLButtons.setVisible(this.partner.isLocalStation());
        this.jLabelMDNDescription.setVisible(!this.partner.isLocalStation());
        this.jPanelUIHelpLabelSignedMDN.setVisible(!this.partner.isLocalStation());
        this.jPanelUIHelpAsyncMDN.setVisible(!this.partner.isLocalStation());
        this.jPanelUIHelpSyncMDN.setVisible(!this.partner.isLocalStation());
        this.jPanelUIHelpLabelMDNURL.setVisible(this.partner.isLocalStation());
        this.jRadioButtonAsyncMDN.setVisible(!partner.isLocalStation());
        this.jLabelIconAsyncMDN.setVisible(!partner.isLocalStation());
        this.jLabelIconSyncMDN.setVisible(!partner.isLocalStation());
        this.jRadioButtonSyncMDN.setVisible(!partner.isLocalStation());
        this.switchSignedMDN.setVisible(!partner.isLocalStation());
        this.switchUseAlgorithmIdentifierProtectionAttribute.setVisible(!partner.isLocalStation());
        this.jPanelUIHelpLabelUseAlgorithmIdentifierProtectionAttribute.setVisible(!partner.isLocalStation());
    }

    private void disableEnableWidgets() {
        this.jTextFieldIgnorePollFilterList.setEnabled(this.partner.isEnableDirPoll());
        this.jTextFieldIgnorePollFilterList.setEditable(this.partner.isEnableDirPoll());
        this.jPanelUIHelpLabelPollIgnoreList.setEnabled(this.partner.isEnableDirPoll());
        this.jTextFieldPollInterval.setEnabled(this.partner.isEnableDirPoll());
        this.jTextFieldPollInterval.setEditable(this.partner.isEnableDirPoll());
        this.jLabelPollInterval.setEnabled(this.partner.isEnableDirPoll());
        this.jLabelPollMaxFiles.setEnabled(this.partner.isEnableDirPoll());
        this.jTextFieldPollMaxFiles.setEnabled(this.partner.isEnableDirPoll());
        this.jTextFieldPollMaxFiles.setEditable(this.partner.isEnableDirPoll());
        this.jLabelPollIntervalSeconds.setEnabled(this.partner.isEnableDirPoll());
        this.jLabelPollDir.setEnabled(this.partner.isEnableDirPoll());
        this.jTextFieldPollDir.setEnabled(this.partner.isEnableDirPoll());
    }

    private void setPanelVisiblilityState() {
        this.jTabbedPane.removeAll();
        this.jTabbedPane.addTab(rb.getResourceString("tab.misc"), this.jPanelMiscMain);
        this.jTabbedPane.addTab(rb.getResourceString("tab.security"), this.jPanelSecurityMain);
        if (!this.partner.isLocalStation()) {
            this.jTabbedPane.addTab(rb.getResourceString("tab.send"), this.jPanelSend);
        }
        this.jTabbedPane.addTab(rb.getResourceString("tab.mdn"), this.jPanelMDN);
        if (this.partner.isLocalStation()) {
            // Inbound Auth tabs only for local stations - split into two tabs
            this.jTabbedPane.addTab(rb.getResourceString("tab.inboundauth.basic"), this.jPanelInboundAuthBasic);
            this.jTabbedPane.addTab(rb.getResourceString("tab.inboundauth.cert"), this.jPanelInboundAuthCert);
        }
        if (!this.partner.isLocalStation()) {
            this.jTabbedPane.addTab(rb.getResourceString("tab.dirpoll"), this.jPanelDirPoll);
            this.jTabbedPane.addTab(rb.getResourceString("tab.receipt"), this.jPanelReceipt);
            this.jTabbedPane.addTab(rb.getResourceString("tab.httpauth"), this.jPanelHTTPAuth);
            if (this.displayHttpHeaderPanel) {
                this.jTabbedPane.addTab(rb.getResourceString("tab.httpheader"), this.jPanelHTTPHeader);
            }
            if (this.displayNotificationPanel) {
                this.jTabbedPane.addTab(rb.getResourceString("tab.notification"), this.jPanelNotification);
            }
            // Visibility tab removed - partners are user-specific
            this.jTabbedPane.addTab(rb.getResourceString("tab.events"), this.jPanelEvents);
            this.jTabbedPane.addTab(rb.getResourceString("tab.partnersystem"), this.jPanelPartnerSystem);
        }
    }

    /**
     * graphically updates the state of the input fields in the HTTP auth panel
     */
    private void updateHttpAuthState() {
        this.jTextFieldHttpAuthMessageUser.setEditable(this.jRadioButtonHttpAuthCredentialsMessage.isSelected());
        this.jTextFieldHttpAuthMessageUser.setEnabled(this.jRadioButtonHttpAuthCredentialsMessage.isSelected());
        this.jPasswordFieldHttpAuthMessagePass.setEditable(this.jRadioButtonHttpAuthCredentialsMessage.isSelected());
        this.jPasswordFieldHttpAuthMessagePass.setEnabled(this.jRadioButtonHttpAuthCredentialsMessage.isSelected());
        this.jTextFieldHttpAuthAsyncMDNUser.setEditable(this.jRadioButtonHttpAuthCredentialsMDN.isSelected());
        this.jTextFieldHttpAuthAsyncMDNUser.setEnabled(this.jRadioButtonHttpAuthCredentialsMDN.isSelected());
        this.jPasswordFieldHttpPassAsyncMDN.setEditable(this.jRadioButtonHttpAuthCredentialsMDN.isSelected());
        this.jPasswordFieldHttpPassAsyncMDN.setEnabled(this.jRadioButtonHttpAuthCredentialsMDN.isSelected());
        // Enable/disable certificate combo boxes
        this.jComboBoxHttpAuthCertMessage.setEnabled(
                this.jRadioButtonHttpAuthCertificateMessage != null &&
                        this.jRadioButtonHttpAuthCertificateMessage.isSelected());
        this.jComboBoxHttpAuthCertMDN.setEnabled(
                this.jRadioButtonHttpAuthCertificateMDN != null &&
                        this.jRadioButtonHttpAuthCertificateMDN.isSelected());
    }

    /**
     * Initialize the Inbound Auth panel for local stations
     */
    private void initializeInboundAuthBasicPanel() {
        jPanelInboundAuthBasic = new javax.swing.JPanel();
        jPanelInboundAuthBasic.setLayout(new java.awt.GridBagLayout());

        // Info label
        jLabelInboundAuthBasicInfo = new javax.swing.JLabel();
        jLabelInboundAuthBasicInfo.setText("<html>" + rb.getResourceString("inboundauth.basic.info") + "</html>");
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 20, 10);
        jPanelInboundAuthBasic.add(jLabelInboundAuthBasicInfo, gridBagConstraints);

        // Basic auth label and toggle switch
        jLabelInboundAuthBasicEnable = new javax.swing.JLabel();
        jLabelInboundAuthBasicEnable.setText(rb.getResourceString("label.inboundauth.basic"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelInboundAuthBasic.add(jLabelInboundAuthBasicEnable, gridBagConstraints);

        switchInboundAuthBasic = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchInboundAuthBasic.setDisplayStatusText(true);
        switchInboundAuthBasic.addActionListener(evt -> switchInboundAuthBasicActionPerformed(evt));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        jPanelInboundAuthBasic.add(switchInboundAuthBasic, gridBagConstraints);

        // Initialize basic auth table model and table
        modelInboundAuthBasic = new TableModelInboundAuthBasic();
        jTableInboundAuthBasic = new javax.swing.JTable(modelInboundAuthBasic);
        jTableInboundAuthBasic.setRowHeight(24);
        jTableInboundAuthBasic.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableInboundAuthBasic.setShowGrid(true);
        jTableInboundAuthBasic.setGridColor(new java.awt.Color(220, 220, 220));
        jTableInboundAuthBasic.setIntercellSpacing(new java.awt.Dimension(1, 1));
        jTableInboundAuthBasic.setFillsViewportHeight(true);

        // Configure column widths for basic auth table
        jTableInboundAuthBasic.getColumnModel().getColumn(TableModelInboundAuthBasic.COL_USERNAME)
                .setPreferredWidth(200);
        jTableInboundAuthBasic.getColumnModel().getColumn(TableModelInboundAuthBasic.COL_PASSWORD)
                .setPreferredWidth(200);
        jTableInboundAuthBasic.getColumnModel().getColumn(TableModelInboundAuthBasic.COL_ENABLED).setPreferredWidth(70);
        jTableInboundAuthBasic.getColumnModel().getColumn(TableModelInboundAuthBasic.COL_ENABLED).setMaxWidth(80);

        // Configure custom renderers and editors for password toggle
        jTableInboundAuthBasic.getColumnModel().getColumn(TableModelInboundAuthBasic.COL_PASSWORD)
                .setCellRenderer(new PasswordCellRenderer());

        PasswordToggleButtonRenderer toggleRenderer = new PasswordToggleButtonRenderer();
        jTableInboundAuthBasic.getColumnModel().getColumn(TableModelInboundAuthBasic.COL_SHOW_PASSWORD)
                .setCellRenderer(toggleRenderer);
        jTableInboundAuthBasic.getColumnModel().getColumn(TableModelInboundAuthBasic.COL_SHOW_PASSWORD)
                .setCellEditor(toggleRenderer);
        jTableInboundAuthBasic.getColumnModel().getColumn(TableModelInboundAuthBasic.COL_SHOW_PASSWORD)
                .setPreferredWidth(50);
        jTableInboundAuthBasic.getColumnModel().getColumn(TableModelInboundAuthBasic.COL_SHOW_PASSWORD)
                .setMaxWidth(60);

        jScrollPaneInboundAuthBasic = new javax.swing.JScrollPane(jTableInboundAuthBasic);
        jScrollPaneInboundAuthBasic.setPreferredSize(new java.awt.Dimension(600, 200));
        jScrollPaneInboundAuthBasic
                .setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        jPanelInboundAuthBasic.add(jScrollPaneInboundAuthBasic, gridBagConstraints);

        // Button panel for Basic Auth Add/Delete
        javax.swing.JPanel jPanelButtonsBasic = new javax.swing.JPanel();
        jPanelButtonsBasic.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jButtonAddInboundAuthBasic = new javax.swing.JButton();
        jButtonAddInboundAuthBasic.setText(rb.getResourceString("inboundauth.button.add"));
        jButtonAddInboundAuthBasic.addActionListener(evt -> jButtonAddInboundAuthBasicActionPerformed(evt));
        jPanelButtonsBasic.add(jButtonAddInboundAuthBasic);

        jButtonDeleteInboundAuthBasic = new javax.swing.JButton();
        jButtonDeleteInboundAuthBasic.setText(rb.getResourceString("inboundauth.button.delete"));
        jButtonDeleteInboundAuthBasic.addActionListener(evt -> jButtonDeleteInboundAuthBasicActionPerformed(evt));
        jPanelButtonsBasic.add(jButtonDeleteInboundAuthBasic);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        jPanelInboundAuthBasic.add(jPanelButtonsBasic, gridBagConstraints);
    }

    private void initializeInboundAuthCertPanel() {
        jPanelInboundAuthCert = new javax.swing.JPanel();
        jPanelInboundAuthCert.setLayout(new java.awt.GridBagLayout());

        // Info label
        jLabelInboundAuthCertInfo = new javax.swing.JLabel();
        jLabelInboundAuthCertInfo.setText("<html>" + rb.getResourceString("inboundauth.cert.info") + "</html>");
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 20, 10);
        jPanelInboundAuthCert.add(jLabelInboundAuthCertInfo, gridBagConstraints);

        // Certificate auth label and toggle switch
        jLabelInboundAuthCertEnable = new javax.swing.JLabel();
        jLabelInboundAuthCertEnable.setText(rb.getResourceString("label.inboundauth.cert"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelInboundAuthCert.add(jLabelInboundAuthCertEnable, gridBagConstraints);

        switchInboundAuthCert = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchInboundAuthCert.setDisplayStatusText(true);
        switchInboundAuthCert.addActionListener(evt -> switchInboundAuthCertActionPerformed(evt));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        jPanelInboundAuthCert.add(switchInboundAuthCert, gridBagConstraints);

        // Initialize certificate auth table model and table
        modelInboundAuthCert = new TableModelInboundAuthCert();
        jTableInboundAuthCert = new javax.swing.JTable(modelInboundAuthCert);
        jTableInboundAuthCert.setRowHeight(24);
        jTableInboundAuthCert.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableInboundAuthCert.setShowGrid(true);
        jTableInboundAuthCert.setGridColor(new java.awt.Color(220, 220, 220));
        jTableInboundAuthCert.setIntercellSpacing(new java.awt.Dimension(1, 1));
        jTableInboundAuthCert.setFillsViewportHeight(true);

        // Configure column widths for cert auth table
        jTableInboundAuthCert.getColumnModel().getColumn(TableModelInboundAuthCert.COL_CERT_ALIAS)
                .setPreferredWidth(150);
        jTableInboundAuthCert.getColumnModel().getColumn(TableModelInboundAuthCert.COL_CERT_FINGERPRINT)
                .setPreferredWidth(300);
        jTableInboundAuthCert.getColumnModel().getColumn(TableModelInboundAuthCert.COL_ENABLED).setPreferredWidth(70);
        jTableInboundAuthCert.getColumnModel().getColumn(TableModelInboundAuthCert.COL_ENABLED).setMaxWidth(80);

        jScrollPaneInboundAuthCert = new javax.swing.JScrollPane(jTableInboundAuthCert);
        jScrollPaneInboundAuthCert.setPreferredSize(new java.awt.Dimension(600, 200));
        jScrollPaneInboundAuthCert
                .setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        jPanelInboundAuthCert.add(jScrollPaneInboundAuthCert, gridBagConstraints);

        // Button panel for Certificate Auth Add/Delete
        javax.swing.JPanel jPanelButtonsCert = new javax.swing.JPanel();
        jPanelButtonsCert.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jButtonAddInboundAuthCert = new javax.swing.JButton();
        jButtonAddInboundAuthCert.setText(rb.getResourceString("inboundauth.button.add"));
        jButtonAddInboundAuthCert.addActionListener(evt -> jButtonAddInboundAuthCertActionPerformed(evt));
        jPanelButtonsCert.add(jButtonAddInboundAuthCert);

        jButtonDeleteInboundAuthCert = new javax.swing.JButton();
        jButtonDeleteInboundAuthCert.setText(rb.getResourceString("inboundauth.button.delete"));
        jButtonDeleteInboundAuthCert.addActionListener(evt -> jButtonDeleteInboundAuthCertActionPerformed(evt));
        jPanelButtonsCert.add(jButtonDeleteInboundAuthCert);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        jPanelInboundAuthCert.add(jPanelButtonsCert, gridBagConstraints);
    }

    /**
     * Updates the partner system information of the selected partner
     */
    private void updatePartnerSystemInformation(final Partner finalPartner) {
        PartnerSystem partnerSystem = this.partnerSystemMap.get(finalPartner.getAS2Identification());
        if (partnerSystem != null) {
            JPanelPartner.this.jTextFieldAS2Version.setText(partnerSystem.getAS2Version());
            JPanelPartner.this.jTextFieldProductName.setText(partnerSystem.getProductName());
            JPanelPartner.this.jCheckBoxEdiintFeaturesCompression.setSelected(partnerSystem.supportsCompression());
            JPanelPartner.this.jCheckBoxEdiintFeaturesCEM.setSelected(partnerSystem.supportsCEM());
            JPanelPartner.this.jCheckBoxEdiintFeaturesMA.setSelected(partnerSystem.supportsMA());
        } else {
            JPanelPartner.this.jTextFieldAS2Version.setText(JPanelPartner.rb.getResourceString("partnersystem.noinfo"));
            JPanelPartner.this.jTextFieldProductName
                    .setText(JPanelPartner.rb.getResourceString("partnersystem.noinfo"));
            JPanelPartner.this.jCheckBoxEdiintFeaturesCompression.setSelected(false);
            JPanelPartner.this.jCheckBoxEdiintFeaturesCEM.setSelected(false);
            JPanelPartner.this.jCheckBoxEdiintFeaturesMA.setSelected(false);
        }
    }

    /**
     * Displays the directory that is assigned with the partner to be polled. It
     * must not be the same because the name may not be a valid filename
     * Shows as relative path from the message directory
     */
    private void updatePollDirDisplay(final Partner finalPartner) {
        StringBuilder pollDirStr = new StringBuilder();

        // Get current username and include it in the path
        String username = this.baseClient.getUsername();
        if (username == null || username.isEmpty()) {
            username = "admin";
        }

        // Build path: {username}-{partnerName}/outbox/{localstation}
        pollDirStr.append(username);
        pollDirStr.append("-");
        pollDirStr.append(AS2Tools.convertToValidFilename(finalPartner.getName()));
        pollDirStr.append(this.serverSideFileSeparator);
        pollDirStr.append("outbox");
        // for single local stations display add the name of the local station, else
        // display <localstation>
        List<Partner> localStations = this.tree.getLocalStations();
        String localStationDir;
        if (localStations.isEmpty()) {
            localStationDir = "<create-local-station-first>";
        } else if (localStations.size() == 1) {
            localStationDir = AS2Tools.convertToValidFilename(localStations.get(0).getName());
        } else {
            localStationDir = "<localstation>";
        }
        this.jTextFieldPollDir.setText(pollDirStr + serverSideFileSeparator + localStationDir);
    }

    /**
     * Asks the server for an absolute path on its side - this is useful if
     * client and server are running on different OS or if the client/server
     * path structure is not the same
     *
     * @param directory
     * @return A string array of the size 2: 0: path, 1: path separator
     */
    private String[] getAbsolutePathOnServerSide(String directory) {
        FileSystemViewRequest request = new FileSystemViewRequest(FileSystemViewRequest.TYPE_GET_ABSOLUTE_PATH_STR);
        request.setRequestFilePath(directory);
        FileSystemViewResponse response = (FileSystemViewResponse) this.baseClient.sendSync(request,
                Partner.TIMEOUT_PARTNER_REQUEST);
        return (new String[] { response.getParameterString(), response.getServerSideFileSeparator() });
    }

    private void setButtonState() {
        if (this.partner != null) {
            this.jTextFieldReceiptURL.setEditable(!this.partner.isLocalStation());
            this.jTextFieldReceiptURL.setEnabled(!this.partner.isLocalStation());
            this.jTextFieldEMail.setEnabled(this.partner.isLocalStation());
            this.jTextFieldEMail.setEditable(this.partner.isLocalStation());
            this.jPanelUIHelpLabelSignType.setVisible(!this.partner.isLocalStation());
            this.jPanelUIHelpLabelEncryptionType.setVisible(!this.partner.isLocalStation());
            this.jComboBoxEncryptionType.setVisible(!this.partner.isLocalStation());
            this.jComboBoxSignType.setVisible(!this.partner.isLocalStation());
            this.jPanelSendMain.setVisible(!this.partner.isLocalStation());
            this.jPanelPollOptions.setVisible(!this.partner.isLocalStation());
            this.jPanelReceiptOptions.setVisible(!this.partner.isLocalStation());
            this.jPanelHttpAuthData.setVisible(!this.partner.isLocalStation());
            this.jTextFieldNotifySend.setEnabled(this.partner.isNotifySendEnabled());
            this.jTextFieldNotifySend.setEditable(this.partner.isNotifySendEnabled());
            this.jTextFieldNotifyReceive.setEnabled(this.partner.isNotifyReceiveEnabled());
            this.jTextFieldNotifyReceive.setEditable(this.partner.isNotifyReceiveEnabled());
            this.jTextFieldNotifySendReceive.setEnabled(this.partner.isNotifySendReceiveEnabled());
            this.jTextFieldNotifySendReceive.setEditable(this.partner.isNotifySendReceiveEnabled());
            this.jComboBoxOverwriteLocalStationCryptKey.setEnabled(
                    this.jRadioButtonOverwriteLocalstationSecurity.isSelected());
            this.jComboBoxOverwriteLocalstationSignKey.setEnabled(
                    this.jRadioButtonOverwriteLocalstationSecurity.isSelected());
            this.renderEvents();
        }
    }

    /**
     * Renders the events for the selected partner - also refreshes the display
     * if there were any changes
     */
    private void renderEvents() {
        int processTypeOnReceipt = this.partner.getPartnerEvents().getProcess(PartnerEventInformation.TYPE_ON_RECEIPT);
        int processTypeOnSendSuccess = this.partner.getPartnerEvents()
                .getProcess(PartnerEventInformation.TYPE_ON_SENDSUCCESS);
        int processTypeOnSendError = this.partner.getPartnerEvents()
                .getProcess(PartnerEventInformation.TYPE_ON_SENDERROR);
        this.jLabelIconProcessTypeOnReceipt.setIcon(
                new ImageIcon(PartnerEventInformation.getImageForProcess(processTypeOnReceipt).toMinResolution(24)));
        this.jLabelIconProcessTypeOnSendError.setIcon(
                new ImageIcon(PartnerEventInformation.getImageForProcess(processTypeOnSendError).toMinResolution(24)));
        this.jLabelIconProcessTypeOnSendSuccess.setIcon(new ImageIcon(
                PartnerEventInformation.getImageForProcess(processTypeOnSendSuccess).toMinResolution(24)));
        List<String> onReceiptParameter = this.partner.getPartnerEvents()
                .getParameter(PartnerEventInformation.TYPE_ON_RECEIPT);
        if (onReceiptParameter.isEmpty()) {
            this.jTextFieldEventInfoOnReceipt.setText("");
        } else {
            if (processTypeOnReceipt == PartnerEventInformation.PROCESS_MOVE_TO_PARTNER) {
                String as2Id = onReceiptParameter.get(0);
                List<Partner> partnerList = this.tree.getAllPartner();
                Partner foundPartner = null;
                for (Partner testPartner : partnerList) {
                    if (testPartner.getAS2Identification().equals(as2Id)) {
                        foundPartner = testPartner;
                        break;
                    }
                }
                if (foundPartner == null) {
                    this.jTextFieldEventInfoOnReceipt.setText("<UNKNOWN>");
                } else {
                    this.jTextFieldEventInfoOnReceipt.setText(foundPartner.toString());
                }
            } else {
                this.jTextFieldEventInfoOnReceipt.setText(onReceiptParameter.get(0));
            }
        }
        this.switchUseEventOnReceipt.setSelected(this.partner.getPartnerEvents().useOnReceipt());
        this.jButtonAddEventOnReceipt.setEnabled(this.switchUseEventOnReceipt.isSelected());
        this.jButtonEditEventOnReceipt.setEnabled(this.switchUseEventOnReceipt.isSelected());
        this.jLabelIconProcessTypeOnReceipt.setEnabled(this.switchUseEventOnReceipt.isSelected());
        List<String> onSendErrorParameter = this.partner.getPartnerEvents()
                .getParameter(PartnerEventInformation.TYPE_ON_SENDERROR);
        if (onSendErrorParameter.isEmpty()) {
            this.jTextFieldEventInfoOnSendError.setText("");
        } else {
            if (processTypeOnSendError == PartnerEventInformation.PROCESS_MOVE_TO_PARTNER) {
                String as2Id = onSendErrorParameter.get(0);
                List<Partner> partnerList = this.tree.getAllPartner();
                Partner foundPartner = null;
                for (Partner testPartner : partnerList) {
                    if (testPartner.getAS2Identification().equals(as2Id)) {
                        foundPartner = testPartner;
                        break;
                    }
                }
                if (foundPartner == null) {
                    this.jTextFieldEventInfoOnSendError.setText("<UNKNOWN>");
                } else {
                    this.jTextFieldEventInfoOnSendError.setText(foundPartner.toString());
                }
            } else {
                this.jTextFieldEventInfoOnSendError.setText(onSendErrorParameter.get(0));
            }
        }
        this.switchUseEventOnSendError.setSelected(this.partner.getPartnerEvents().useOnSenderror());
        this.jButtonAddEventOnSendError.setEnabled(this.switchUseEventOnSendError.isSelected());
        this.jButtonEditEventOnSendError.setEnabled(this.switchUseEventOnSendError.isSelected());
        this.jLabelIconProcessTypeOnSendError.setEnabled(this.switchUseEventOnSendError.isSelected());
        List<String> onSendSuccessParameter = this.partner.getPartnerEvents()
                .getParameter(PartnerEventInformation.TYPE_ON_SENDSUCCESS);
        if (onSendSuccessParameter.isEmpty()) {
            this.jTextFieldEventInfoOnSendSuccess.setText("");
        } else {
            if (processTypeOnSendSuccess == PartnerEventInformation.PROCESS_MOVE_TO_PARTNER) {
                String as2Id = onSendSuccessParameter.get(0);
                List<Partner> partnerList = this.tree.getAllPartner();
                Partner foundPartner = null;
                for (Partner testPartner : partnerList) {
                    if (testPartner.getAS2Identification().equals(as2Id)) {
                        foundPartner = testPartner;
                        break;
                    }
                }
                if (foundPartner == null) {
                    this.jTextFieldEventInfoOnSendSuccess.setText("<UNKNOWN>");
                } else {
                    this.jTextFieldEventInfoOnSendSuccess.setText(foundPartner.toString());
                }
            } else {
                this.jTextFieldEventInfoOnSendSuccess.setText(onSendSuccessParameter.get(0));
            }
        }
        this.switchUseEventOnSendSuccess.setSelected(this.partner.getPartnerEvents().useOnSendsuccess());
        this.jButtonAddEventOnSendSuccess.setEnabled(this.switchUseEventOnSendSuccess.isSelected());
        this.jButtonEditEventOnSendSuccess.setEnabled(this.switchUseEventOnSendSuccess.isSelected());
        this.jLabelIconProcessTypeOnSendSuccess.setEnabled(this.switchUseEventOnSendSuccess.isSelected());
    }

    /**
     * Creates a new process and configures it for the passed event type
     *
     * @param EVENT_TYPE
     */
    private void createProcess(final int EVENT_TYPE) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String actionCommand = evt.getActionCommand();
                try {
                    if (actionCommand.equals(rbEvents.getResourceString("process.executeshell"))) {
                        editEvent(EVENT_TYPE, PartnerEventInformation.PROCESS_EXECUTE_SHELL);
                    } else if (actionCommand.equals(rbEvents.getResourceString("process.movetopartner"))) {
                        editEvent(EVENT_TYPE, PartnerEventInformation.PROCESS_MOVE_TO_PARTNER);
                    } else if (actionCommand.equals(rbEvents.getResourceString("process.movetodirectory"))) {
                        editEvent(EVENT_TYPE, PartnerEventInformation.PROCESS_MOVE_TO_DIR);
                    }
                } catch (Exception e) {
                    UINotification.instance().addNotification(e);
                    e.printStackTrace();
                }
            }
        };
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
                this);
        JDialogCategorySelection dialog = new JDialogCategorySelection(parentFrame);
        dialog.setTitle(rbEvents.getResourceString("title.select.process",
                rbEvents.getResourceString("type." + EVENT_TYPE)));
        Category category = new Category();
        category.setTitle(rbEvents.getResourceString("tab.newprocess"));
        Subcategory subExecuteShell = new Subcategory();
        subExecuteShell.setActionCommand(
                rbEvents.getResourceString("process.executeshell"));
        subExecuteShell.setIcon(new ImageIcon(PartnerEventInformation
                .getImageForProcess(PartnerEventInformation.PROCESS_EXECUTE_SHELL).toMinResolution(36)));
        subExecuteShell.setTitle(rbEvents.getResourceString("process.executeshell"));
        subExecuteShell.setDescription(rbEvents.getResourceString("process.executeshell.description"));
        category.addSubcategory(subExecuteShell);
        Subcategory subMoveToPartner = new Subcategory();
        subMoveToPartner.setActionCommand(
                rbEvents.getResourceString("process.movetopartner"));
        subMoveToPartner.setIcon(new ImageIcon(PartnerEventInformation
                .getImageForProcess(PartnerEventInformation.PROCESS_MOVE_TO_PARTNER).toMinResolution(36)));
        subMoveToPartner.setTitle(rbEvents.getResourceString("process.movetopartner"));
        subMoveToPartner.setDescription(rbEvents.getResourceString("process.movetopartner.description"));
        category.addSubcategory(subMoveToPartner);
        Subcategory subMoveToDirectory = new Subcategory();
        subMoveToDirectory.setActionCommand(
                rbEvents.getResourceString("process.movetodirectory"));
        subMoveToDirectory.setIcon(new ImageIcon(PartnerEventInformation
                .getImageForProcess(PartnerEventInformation.PROCESS_MOVE_TO_DIR).toMinResolution(36)));
        subMoveToDirectory.setTitle(rbEvents.getResourceString("process.movetodirectory"));
        subMoveToDirectory.setDescription(rbEvents.getResourceString("process.movetodirectory.description"));
        category.addSubcategory(subMoveToDirectory);
        dialog.addCategory(category);
        dialog.addActionListener(actionListener);
        dialog.setVisible(true);
    }

    private void editEvent(final int EVENT_TYPE, final int PROCESS_TYPE) {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
                JPanelPartner.this);
        if (PROCESS_TYPE == PartnerEventInformation.PROCESS_EXECUTE_SHELL) {
            JDialogConfigureEventShell dialog = new JDialogConfigureEventShell(
                    parentFrame, JPanelPartner.this.partner,
                    EVENT_TYPE);
            dialog.setVisible(true);
        } else if (PROCESS_TYPE == PartnerEventInformation.PROCESS_MOVE_TO_DIR) {
            JDialogConfigureEventMoveToDir dialog = new JDialogConfigureEventMoveToDir(
                    parentFrame, JPanelPartner.this.baseClient, JPanelPartner.this.partner,
                    EVENT_TYPE);
            dialog.setVisible(true);
        } else if (PROCESS_TYPE == PartnerEventInformation.PROCESS_MOVE_TO_PARTNER) {
            JDialogConfigureEventMoveToPartner dialog = new JDialogConfigureEventMoveToPartner(
                    parentFrame, this.tree.getAllPartner(), JPanelPartner.this.partner,
                    EVENT_TYPE);
            dialog.setVisible(true);
        }
        this.renderEvents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupSyncAsyncMDN = new javax.swing.ButtonGroup();
        buttonGroupAuthenticationMessage = new javax.swing.ButtonGroup();
        buttonGroupAuthenticationMDN = new javax.swing.ButtonGroup();
        buttonGroupOverwriteLocalStationSecurity = new javax.swing.ButtonGroup();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelSend = new javax.swing.JPanel();
        jPanelSendMain = new javax.swing.JPanel();
        jTextFieldReceiptURL = new javax.swing.JTextField();
        jTextFieldSubject = new javax.swing.JTextField();
        jTextFieldContentType = new javax.swing.JTextField();
        jPanelSpace14 = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jComboBoxContentTransferEncoding = new javax.swing.JComboBox<>();
        jLabelContentTransferEncoding = new javax.swing.JLabel();
        jComboBoxHTTPProtocolVersion = new javax.swing.JComboBox<>();
        jButtonTestConnection = new javax.swing.JButton();
        jPanelUIHelpLabelURL = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelSubject = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelContentType = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelSpace42 = new javax.swing.JPanel();
        jPanelUIHelpLabelProtocolVersion = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelSpace43 = new javax.swing.JPanel();
        jPanelSpace44 = new javax.swing.JPanel();
        switchCompress = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelUIHelpLabelCompress = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelSpace77646 = new javax.swing.JPanel();
        jPanelSpace45 = new javax.swing.JPanel();
        jPanelMDN = new javax.swing.JPanel();
        jPanelMDNMain = new javax.swing.JPanel();
        jTextFieldMDNURL = new javax.swing.JTextField();
        jPanelMDNURLButtons = new javax.swing.JPanel();
        jButtonMDNURLHttp = new javax.swing.JButton();
        jButtonMDNURLHttps = new javax.swing.JButton();
        jPanelSpace99 = new javax.swing.JPanel();
        jLabelIconSyncMDN = new javax.swing.JLabel();
        jLabelIconAsyncMDN = new javax.swing.JLabel();
        jLabelMDNDescription = new javax.swing.JLabel();
        jPanelSpacherMDN = new javax.swing.JPanel();
        jPanelUIHelpLabelMDNURL = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelSyncMDN = new javax.swing.JPanel();
        jRadioButtonSyncMDN = new javax.swing.JRadioButton();
        jPanelUIHelpSyncMDN = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelSpacer12 = new javax.swing.JPanel();
        jPanelSpacer13 = new javax.swing.JPanel();
        jPanelAsyncMDN = new javax.swing.JPanel();
        jRadioButtonAsyncMDN = new javax.swing.JRadioButton();
        jPanelUIHelpAsyncMDN = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelSpacer14 = new javax.swing.JPanel();
        jPanelSpace34333 = new javax.swing.JPanel();
        jPanelUIHelpLabelSignedMDN = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        switchSignedMDN = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelDirPoll = new javax.swing.JPanel();
        jPanelPollOptions = new javax.swing.JPanel();
        jLabelPollDir = new javax.swing.JLabel();
        jTextFieldPollDir = new javax.swing.JTextField();
        jLabelPollInterval = new javax.swing.JLabel();
        jTextFieldPollInterval = new javax.swing.JTextField();
        jPanelSpaceX = new javax.swing.JPanel();
        jLabelPollIntervalSeconds = new javax.swing.JLabel();
        jTextFieldIgnorePollFilterList = new javax.swing.JTextField();
        jLabelPollMaxFiles = new javax.swing.JLabel();
        jTextFieldPollMaxFiles = new javax.swing.JTextField();
        jPanelSpace111 = new javax.swing.JPanel();
        jPanelUIHelpLabelPollIgnoreList = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelEnableDirPoll = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        switchEnableDirPoll = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelReceipt = new javax.swing.JPanel();
        jPanelReceiptOptions = new javax.swing.JPanel();
        jPanelSpace456 = new javax.swing.JPanel();
        jPanelUIHelpLabelKeepFilenameOnReceipt = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        switchKeepFilenameOnReceipt = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelHTTPAuth = new javax.swing.JPanel();
        jPanelHttpAuthData = new javax.swing.JPanel();
        jPanelSpace199 = new javax.swing.JPanel();
        jPanelUIHelpAuthCredentialsMessage = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelUIHelpAuthCredentialsMDN = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelSpaceX2 = new javax.swing.JPanel();
        jRadioButtonHttpAuthNoneMessage = new javax.swing.JRadioButton();
        jRadioButtonHttpAuthCredentialsMessage = new javax.swing.JRadioButton();
        jRadioButtonHttpAuthUserPreferenceMessage = new javax.swing.JRadioButton();
        jRadioButtonHttpAuthCertificateMessage = new javax.swing.JRadioButton();
        jComboBoxHttpAuthCertMessage = new javax.swing.JComboBox<>();
        jRadioButtonHttpAuthCredentialsMDN = new javax.swing.JRadioButton();
        jRadioButtonHttpAuthUserPreferenceMDN = new javax.swing.JRadioButton();
        jRadioButtonHttpAuthCertificateMDN = new javax.swing.JRadioButton();
        jComboBoxHttpAuthCertMDN = new javax.swing.JComboBox<>();
        jRadioButtonHttpAuthNoneMDN = new javax.swing.JRadioButton();
        jPanelHTTPAuthCredentialsMessage = new javax.swing.JPanel();
        jLabelHttpAuth = new javax.swing.JLabel();
        jTextFieldHttpAuthMessageUser = new javax.swing.JTextField();
        jLabelHttpPass = new javax.swing.JLabel();
        jPasswordFieldHttpAuthMessagePass = new javax.swing.JPasswordField();
        jPanelHTTPAuthCredentialsMDN = new javax.swing.JPanel();
        jLabelHttpAuthAsyncMDN = new javax.swing.JLabel();
        jTextFieldHttpAuthAsyncMDNUser = new javax.swing.JTextField();
        jLabelHttpPassAsyncMDN = new javax.swing.JLabel();
        jPasswordFieldHttpPassAsyncMDN = new javax.swing.JPasswordField();
        jLabelHttpAuthMessage = new javax.swing.JLabel();
        HttpAuthNoneMDN = new javax.swing.JLabel();
        jPanelSpace345 = new javax.swing.JPanel();
        jPanelSpace485 = new javax.swing.JPanel();
        jPanelHTTPHeader = new javax.swing.JPanel();
        jScrollPaneHttpHeader = new javax.swing.JScrollPane();
        jTableHttpHeader = new javax.swing.JTable();
        jButtonHttpHeaderAdd = new javax.swing.JButton();
        jButtonHttpHeaderRemove = new javax.swing.JButton();
        jPanelNotification = new javax.swing.JPanel();
        jPanelNotificationMain = new javax.swing.JPanel();
        jCheckBoxNotifySend = new javax.swing.JCheckBox();
        jCheckBoxNotifyReceive = new javax.swing.JCheckBox();
        jCheckBoxNotifySendReceive = new javax.swing.JCheckBox();
        jTextFieldNotifyReceive = new javax.swing.JTextField();
        jTextFieldNotifySend = new javax.swing.JTextField();
        jTextFieldNotifySendReceive = new javax.swing.JTextField();
        jPanelSpace23 = new javax.swing.JPanel();
        jPanelEvents = new javax.swing.JPanel();
        jPanelEventsMain = new javax.swing.JPanel();
        jPanelSpace = new javax.swing.JPanel();
        jPanelSpace123 = new javax.swing.JPanel();
        jPanelPostprocessingSend = new javax.swing.JPanel();
        jLabelIconProcessTypeOnReceipt = new javax.swing.JLabel();
        jTextFieldEventInfoOnReceipt = new javax.swing.JTextField();
        jButtonEditEventOnReceipt = new javax.swing.JButton();
        jButtonAddEventOnReceipt = new javax.swing.JButton();
        jPanelPostprocessingReceivedFailure = new javax.swing.JPanel();
        jLabelIconProcessTypeOnSendError = new javax.swing.JLabel();
        jTextFieldEventInfoOnSendError = new javax.swing.JTextField();
        jButtonEditEventOnSendError = new javax.swing.JButton();
        jButtonAddEventOnSendError = new javax.swing.JButton();
        jPanelPostProcessingReceiptSuccess = new javax.swing.JPanel();
        jLabelIconProcessTypeOnSendSuccess = new javax.swing.JLabel();
        jTextFieldEventInfoOnSendSuccess = new javax.swing.JTextField();
        jButtonEditEventOnSendSuccess = new javax.swing.JButton();
        jButtonAddEventOnSendSuccess = new javax.swing.JButton();
        jLabelUseEventOnReceipt = new javax.swing.JLabel();
        switchUseEventOnReceipt = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchUseEventOnSendSuccess = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelUseEventOnSendSuccess = new javax.swing.JLabel();
        jLabelUseEventOnSendError = new javax.swing.JLabel();
        switchUseEventOnSendError = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelSpace126 = new javax.swing.JPanel();
        jPanelSpace127 = new javax.swing.JPanel();
        jPanelSpace129 = new javax.swing.JPanel();
        jPanelSpace128 = new javax.swing.JPanel();
        jPanelSpace875 = new javax.swing.JPanel();
        jPanelPartnerSystem = new javax.swing.JPanel();
        jPanelPartnerSystemMain = new javax.swing.JPanel();
        jLabelAS2Version = new javax.swing.JLabel();
        jLabelProductName = new javax.swing.JLabel();
        jLabelFeatures = new javax.swing.JLabel();
        jCheckBoxEdiintFeaturesCompression = new javax.swing.JCheckBox();
        jCheckBoxEdiintFeaturesMA = new javax.swing.JCheckBox();
        jCheckBoxEdiintFeaturesCEM = new javax.swing.JCheckBox();
        jTextFieldAS2Version = new javax.swing.JTextField();
        jTextFieldProductName = new javax.swing.JTextField();
        jPanelSpaceSpace = new javax.swing.JPanel();
        jScrollPaneTextAreaPartnerSystemInformation = new javax.swing.JScrollPane();
        jTextAreaPartnerSystemInformation = new javax.swing.JTextArea();
        jPanelMisc = new javax.swing.JPanel();
        jPanelMiscMain = new javax.swing.JPanel();
        jTextFieldId = new javax.swing.JTextField();
        jTextFieldName = new javax.swing.JTextField();
        jTextFieldEMail = new javax.swing.JTextField();
        jScrollPanePartnerComment = new javax.swing.JScrollPane();
        jTextPanePartnerComment = new javax.swing.JTextPane();
        jScrollPanePartnerAddress = new javax.swing.JScrollPane();
        jTextPanePartnerContact = new javax.swing.JTextPane();
        jScrollPanePartnerContact = new javax.swing.JScrollPane();
        jTextPanePartnerAddress = new javax.swing.JTextPane();
        jPanelUIHelpLabelName = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelAS2Id = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelEMail = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelAddress = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelComment = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelContact = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelName1 = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        switchLocalStation = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelSpace874 = new javax.swing.JPanel();
        jPanelSecurity = new javax.swing.JPanel();
        jPanelSecurityMain = new javax.swing.JPanel();
        jComboBoxSignCert = new javax.swing.JComboBox<>();
        jPanelSpace2 = new javax.swing.JPanel();
        jComboBoxSignType = new javax.swing.JComboBox<>();
        jComboBoxEncryptionType = new javax.swing.JComboBox<>();
        jComboBoxCryptCert = new javax.swing.JComboBox<>();
        jPanelUIHelpLabelCryptAlias = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelSpaceSecurity = new javax.swing.JPanel();
        jPanelUIHelpLabelSignAlias = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelSignType = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelEncryptionType = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelOverwriteLocalStationSecurity = new javax.swing.JPanel();
        jPanelUIHelpLabelOverwriteCryptAliasLocalStation = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jComboBoxOverwriteLocalStationCryptKey = new javax.swing.JComboBox<>();
        jPanelUIHelpLabelOverwriteLocalstationSignAlias = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jComboBoxOverwriteLocalstationSignKey = new javax.swing.JComboBox<>();
        jRadioButtonOverwriteLocalstationSecurity = new javax.swing.JRadioButton();
        jRadioButtonKeepLocalstationSecurity = new javax.swing.JRadioButton();
        jPanelSpace585 = new javax.swing.JPanel();
        jPanelSpace89582 = new javax.swing.JPanel();
        jPanelSpace455 = new javax.swing.JPanel();
        switchUseAlgorithmIdentifierProtectionAttribute = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelUIHelpLabelUseAlgorithmIdentifierProtectionAttribute = new de.mendelson.util.balloontip.JPanelUIHelpLabel();

        // Initialize Inbound Auth panels
        initializeInboundAuthBasicPanel();
        initializeInboundAuthCertPanel();
        // Update button states after both panels are initialized
        updateInboundAuthButtonStates();

        setLayout(new java.awt.GridBagLayout());

        jTabbedPane.setMinimumSize(new java.awt.Dimension(10, 10));
        jTabbedPane.setPreferredSize(new java.awt.Dimension(10, 10));

        jPanelSend.setLayout(new java.awt.GridBagLayout());

        jPanelSendMain.setMinimumSize(new java.awt.Dimension(20, 20));
        jPanelSendMain.setLayout(new java.awt.GridBagLayout());

        jTextFieldReceiptURL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldReceiptURLKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        jPanelSendMain.add(jTextFieldReceiptURL, gridBagConstraints);

        jTextFieldSubject.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSubjectKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSendMain.add(jTextFieldSubject, gridBagConstraints);

        jTextFieldContentType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldContentTypeKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSendMain.add(jTextFieldContentType, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelSendMain.add(jPanelSpace14, gridBagConstraints);

        jPanelSep.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 0, 5);
        jPanelSendMain.add(jPanelSep, gridBagConstraints);

        jComboBoxContentTransferEncoding.setMinimumSize(new java.awt.Dimension(120, 24));
        jComboBoxContentTransferEncoding.setPreferredSize(new java.awt.Dimension(130, 24));
        jComboBoxContentTransferEncoding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxContentTransferEncodingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSendMain.add(jComboBoxContentTransferEncoding, gridBagConstraints);

        jLabelContentTransferEncoding.setText("Content Transfer Encoding");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSendMain.add(jLabelContentTransferEncoding, gridBagConstraints);

        jComboBoxHTTPProtocolVersion.setPreferredSize(new java.awt.Dimension(111, 24));
        jComboBoxHTTPProtocolVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxHTTPProtocolVersionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSendMain.add(jComboBoxHTTPProtocolVersion, gridBagConstraints);

        jButtonTestConnection.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        jButtonTestConnection.setText(JPanelPartner.rb.getResourceString("label.test.connection"));
        jButtonTestConnection.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTestConnection.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTestConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestConnectionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanelSendMain.add(jButtonTestConnection, gridBagConstraints);

        jPanelUIHelpLabelURL.setToolTipText(JPanelPartner.rb.getResourceString("label.url.help"));
        jPanelUIHelpLabelURL.setText(JPanelPartner.rb.getResourceString("label.url"));
        jPanelUIHelpLabelURL.setTooltipWidth(300);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanelSendMain.add(jPanelUIHelpLabelURL, gridBagConstraints);

        jPanelUIHelpLabelSubject.setToolTipText(JPanelPartner.rb.getResourceString("label.subject.help"));
        jPanelUIHelpLabelSubject.setText(JPanelPartner.rb.getResourceString("label.subject"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelSendMain.add(jPanelUIHelpLabelSubject, gridBagConstraints);

        jPanelUIHelpLabelContentType.setToolTipText(JPanelPartner.rb.getResourceString("label.contenttype.help"));
        jPanelUIHelpLabelContentType.setText(JPanelPartner.rb.getResourceString("label.contenttype"));
        jPanelUIHelpLabelContentType.setTooltipWidth(300);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelSendMain.add(jPanelUIHelpLabelContentType, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanelSendMain.add(jPanelSpace42, gridBagConstraints);

        jPanelUIHelpLabelProtocolVersion.setToolTipText(JPanelPartner.rb.getResourceString("label.httpversion.help"));
        jPanelUIHelpLabelProtocolVersion.setText(JPanelPartner.rb.getResourceString("label.httpversion"));
        jPanelUIHelpLabelProtocolVersion.setTriangleAlignment(JPanelUIHelpLabel.TRIANGLE_ALIGNMENT_CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelSendMain.add(jPanelUIHelpLabelProtocolVersion, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanelSendMain.add(jPanelSpace43, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanelSendMain.add(jPanelSpace44, gridBagConstraints);

        switchCompress.setDisplayStatusText(true);
        switchCompress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchCompressActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSendMain.add(switchCompress, gridBagConstraints);

        jPanelUIHelpLabelCompress.setToolTipText(JPanelPartner.rb.getResourceString("label.compression.help"));
        jPanelUIHelpLabelCompress.setText(JPanelPartner.rb.getResourceString("label.compression"));
        jPanelUIHelpLabelCompress.setTooltipWidth(250);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
        jPanelSendMain.add(jPanelUIHelpLabelCompress, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanelSendMain.add(jPanelSpace77646, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanelSendMain.add(jPanelSpace45, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanelSend.add(jPanelSendMain, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.send"), jPanelSend);

        jPanelMDN.setLayout(new java.awt.GridBagLayout());

        jPanelMDNMain.setLayout(new java.awt.GridBagLayout());

        jTextFieldMDNURL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldMDNURLKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMDNMain.add(jTextFieldMDNURL, gridBagConstraints);

        // Create a container panel for HTTP and HTTPS buttons
        jPanelMDNURLButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jButtonMDNURLHttp.setText("HTTP");
        jButtonMDNURLHttp.setToolTipText("Fill with HTTP endpoint URL");
        jButtonMDNURLHttp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMDNURLHttpActionPerformed(evt);
            }
        });
        jPanelMDNURLButtons.add(jButtonMDNURLHttp);

        jButtonMDNURLHttps.setText("HTTPS");
        jButtonMDNURLHttps.setToolTipText("Fill with HTTPS endpoint URL");
        jButtonMDNURLHttps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMDNURLHttpsActionPerformed(evt);
            }
        });
        jPanelMDNURLButtons.add(jButtonMDNURLHttps);

        // Add the button container panel to the main panel
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMDNMain.add(jPanelMDNURLButtons, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelMDNMain.add(jPanelSpace99, gridBagConstraints);

        jLabelIconSyncMDN.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelMDNMain.add(jLabelIconSyncMDN, gridBagConstraints);

        jLabelIconAsyncMDN.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelMDNMain.add(jLabelIconAsyncMDN, gridBagConstraints);

        jLabelMDNDescription.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabelMDNDescription.setText(JPanelPartner.rb.getResourceString("label.mdn.description"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 20, 5);
        jPanelMDNMain.add(jLabelMDNDescription, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 20, 1, 20);
        jPanelMDNMain.add(jPanelSpacherMDN, gridBagConstraints);

        jPanelUIHelpLabelMDNURL.setToolTipText(JPanelPartner.rb.getResourceString("label.mdnurl.help"));
        jPanelUIHelpLabelMDNURL.setText(JPanelPartner.rb.getResourceString("label.mdnurl"));
        jPanelUIHelpLabelMDNURL.setTooltipWidth(400);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanelMDNMain.add(jPanelUIHelpLabelMDNURL, gridBagConstraints);

        jPanelSyncMDN.setLayout(new java.awt.GridBagLayout());

        buttonGroupSyncAsyncMDN.add(jRadioButtonSyncMDN);
        jRadioButtonSyncMDN.setSelected(true);
        jRadioButtonSyncMDN.setText(JPanelPartner.rb.getResourceString("label.syncmdn"));
        jRadioButtonSyncMDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSyncMDNActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelSyncMDN.add(jRadioButtonSyncMDN, gridBagConstraints);

        jPanelUIHelpSyncMDN.setToolTipText(JPanelPartner.rb.getResourceString("label.syncmdn.help"));
        jPanelUIHelpSyncMDN.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        jPanelSyncMDN.add(jPanelUIHelpSyncMDN, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelMDNMain.add(jPanelSyncMDN, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMDNMain.add(jPanelSpacer12, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMDNMain.add(jPanelSpacer13, gridBagConstraints);

        jPanelAsyncMDN.setLayout(new java.awt.GridBagLayout());

        buttonGroupSyncAsyncMDN.add(jRadioButtonAsyncMDN);
        jRadioButtonAsyncMDN.setText(JPanelPartner.rb.getResourceString("label.asyncmdn"));
        jRadioButtonAsyncMDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonAsyncMDNActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelAsyncMDN.add(jRadioButtonAsyncMDN, gridBagConstraints);

        jPanelUIHelpAsyncMDN.setToolTipText(JPanelPartner.rb.getResourceString("label.asyncmdn.help"));
        jPanelUIHelpAsyncMDN.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        jPanelAsyncMDN.add(jPanelUIHelpAsyncMDN, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMDNMain.add(jPanelAsyncMDN, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMDNMain.add(jPanelSpacer14, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelMDNMain.add(jPanelSpace34333, gridBagConstraints);

        jPanelUIHelpLabelSignedMDN.setToolTipText(JPanelPartner.rb.getResourceString("label.signedmdn.help"));
        jPanelUIHelpLabelSignedMDN.setText(JPanelPartner.rb.getResourceString("label.signedmdn"));
        jPanelUIHelpLabelSignedMDN.setTooltipWidth(350);
        jPanelUIHelpLabelSignedMDN.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMDNMain.add(jPanelUIHelpLabelSignedMDN, gridBagConstraints);

        switchSignedMDN.setDisplayStatusText(true);
        switchSignedMDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchSignedMDNActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelMDNMain.add(switchSignedMDN, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanelMDN.add(jPanelMDNMain, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.mdn"), jPanelMDN);

        jPanelDirPoll.setLayout(new java.awt.GridBagLayout());

        jPanelPollOptions.setLayout(new java.awt.GridBagLayout());

        jLabelPollDir.setText(JPanelPartner.rb.getResourceString("label.polldir"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelPollOptions.add(jLabelPollDir, gridBagConstraints);

        jTextFieldPollDir.setEditable(false);
        jTextFieldPollDir.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelPollOptions.add(jTextFieldPollDir, gridBagConstraints);

        jLabelPollInterval.setText(JPanelPartner.rb.getResourceString("label.pollinterval"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelPollOptions.add(jLabelPollInterval, gridBagConstraints);

        jTextFieldPollInterval.setColumns(5);
        jTextFieldPollInterval.setMinimumSize(new java.awt.Dimension(70, 22));
        jTextFieldPollInterval.setPreferredSize(new java.awt.Dimension(70, 22));
        jTextFieldPollInterval.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPollIntervalKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelPollOptions.add(jTextFieldPollInterval, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelPollOptions.add(jPanelSpaceX, gridBagConstraints);

        jLabelPollIntervalSeconds.setText("s");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelPollOptions.add(jLabelPollIntervalSeconds, gridBagConstraints);

        jTextFieldIgnorePollFilterList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldIgnorePollFilterListKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelPollOptions.add(jTextFieldIgnorePollFilterList, gridBagConstraints);

        jLabelPollMaxFiles.setText(JPanelPartner.rb.getResourceString("label.maxpollfiles"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelPollOptions.add(jLabelPollMaxFiles, gridBagConstraints);

        jTextFieldPollMaxFiles.setColumns(5);
        jTextFieldPollMaxFiles.setMinimumSize(new java.awt.Dimension(70, 22));
        jTextFieldPollMaxFiles.setPreferredSize(new java.awt.Dimension(70, 22));
        jTextFieldPollMaxFiles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPollMaxFilesKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelPollOptions.add(jTextFieldPollMaxFiles, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelPollOptions.add(jPanelSpace111, gridBagConstraints);

        jPanelUIHelpLabelPollIgnoreList.setToolTipText(JPanelPartner.rb.getResourceString("label.pollignore.help"));
        jPanelUIHelpLabelPollIgnoreList.setText(JPanelPartner.rb.getResourceString("label.pollignore"));
        jPanelUIHelpLabelPollIgnoreList.setTooltipWidth(400);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelPollOptions.add(jPanelUIHelpLabelPollIgnoreList, gridBagConstraints);

        jPanelUIHelpLabelEnableDirPoll.setToolTipText(JPanelPartner.rb.getResourceString("label.enabledirpoll.help"));
        jPanelUIHelpLabelEnableDirPoll.setText(JPanelPartner.rb.getResourceString("label.enabledirpoll"));
        jPanelUIHelpLabelEnableDirPoll.setTooltipWidth(400);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelPollOptions.add(jPanelUIHelpLabelEnableDirPoll, gridBagConstraints);

        switchEnableDirPoll.setDisplayStatusText(true);
        switchEnableDirPoll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchEnableDirPollActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelPollOptions.add(switchEnableDirPoll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDirPoll.add(jPanelPollOptions, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.dirpoll"), jPanelDirPoll);

        jPanelReceipt.setLayout(new java.awt.GridBagLayout());

        jPanelReceiptOptions.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanelReceiptOptions.add(jPanelSpace456, gridBagConstraints);

        jPanelUIHelpLabelKeepFilenameOnReceipt
                .setToolTipText(JPanelPartner.rb.getResourceString("label.keepfilenameonreceipt.help"));
        jPanelUIHelpLabelKeepFilenameOnReceipt
                .setText(JPanelPartner.rb.getResourceString("label.keepfilenameonreceipt"));
        jPanelUIHelpLabelKeepFilenameOnReceipt.setTooltipWidth(350);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelReceiptOptions.add(jPanelUIHelpLabelKeepFilenameOnReceipt, gridBagConstraints);

        switchKeepFilenameOnReceipt.setDisplayStatusText(true);
        switchKeepFilenameOnReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchKeepFilenameOnReceiptActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        jPanelReceiptOptions.add(switchKeepFilenameOnReceipt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanelReceipt.add(jPanelReceiptOptions, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.receipt"), jPanelReceipt);

        jPanelHTTPAuth.setLayout(new java.awt.GridBagLayout());

        jPanelHttpAuthData.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 24;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelHttpAuthData.add(jPanelSpace199, gridBagConstraints);

        jPanelUIHelpAuthCredentialsMessage
                .setToolTipText(JPanelPartner.rb.getResourceString("label.httpauthentication.credentials.help"));
        jPanelUIHelpAuthCredentialsMessage.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanelUIHelpAuthCredentialsMessage.setTooltipWidth(350);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelHttpAuthData.add(jPanelUIHelpAuthCredentialsMessage, gridBagConstraints);

        jPanelUIHelpAuthCredentialsMDN
                .setToolTipText(JPanelPartner.rb.getResourceString("label.httpauthentication.credentials.help"));
        jPanelUIHelpAuthCredentialsMDN.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanelUIHelpAuthCredentialsMDN.setTooltipWidth(350);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelHttpAuthData.add(jPanelUIHelpAuthCredentialsMDN, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelHttpAuthData.add(jPanelSpaceX2, gridBagConstraints);

        buttonGroupAuthenticationMessage.add(jRadioButtonHttpAuthNoneMessage);
        jRadioButtonHttpAuthNoneMessage.setSelected(true);
        jRadioButtonHttpAuthNoneMessage.setText(JPanelPartner.rb.getResourceString("label.httpauth.none"));
        jRadioButtonHttpAuthNoneMessage.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonHttpAuthNoneMessageItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(jRadioButtonHttpAuthNoneMessage, gridBagConstraints);

        buttonGroupAuthenticationMessage.add(jRadioButtonHttpAuthCredentialsMessage);
        jRadioButtonHttpAuthCredentialsMessage
                .setText(JPanelPartner.rb.getResourceString("label.httpauth.credentials.message"));
        jRadioButtonHttpAuthCredentialsMessage.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonHttpAuthCredentialsMessageItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(jRadioButtonHttpAuthCredentialsMessage, gridBagConstraints);

        // Certificate authentication option for message
        buttonGroupAuthenticationMessage.add(jRadioButtonHttpAuthCertificateMessage);
        jRadioButtonHttpAuthCertificateMessage
                .setText(JPanelPartner.rb.getResourceString("label.httpauth.certificate.message"));
        jRadioButtonHttpAuthCertificateMessage.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonHttpAuthCredentialsMessageItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(jRadioButtonHttpAuthCertificateMessage, gridBagConstraints);

        // Certificate combo box for message
        jComboBoxHttpAuthCertMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxHttpAuthCertMessageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(jComboBoxHttpAuthCertMessage, gridBagConstraints);

        // Hidden radio button for AUTH_MODE_USER_PREFERENCE (backward compatibility)
        buttonGroupAuthenticationMessage.add(jRadioButtonHttpAuthUserPreferenceMessage);
        jRadioButtonHttpAuthUserPreferenceMessage.setVisible(false);

        buttonGroupAuthenticationMDN.add(jRadioButtonHttpAuthCredentialsMDN);
        jRadioButtonHttpAuthCredentialsMDN
                .setText(JPanelPartner.rb.getResourceString("label.httpauth.credentials.asyncmdn"));
        jRadioButtonHttpAuthCredentialsMDN.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonHttpAuthCredentialsMDNItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(jRadioButtonHttpAuthCredentialsMDN, gridBagConstraints);

        // Certificate authentication option for MDN
        buttonGroupAuthenticationMDN.add(jRadioButtonHttpAuthCertificateMDN);
        jRadioButtonHttpAuthCertificateMDN
                .setText(JPanelPartner.rb.getResourceString("label.httpauth.certificate.asyncmdn"));
        jRadioButtonHttpAuthCertificateMDN.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonHttpAuthCredentialsMDNItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(jRadioButtonHttpAuthCertificateMDN, gridBagConstraints);

        // Certificate combo box for MDN
        jComboBoxHttpAuthCertMDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxHttpAuthCertMDNActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(jComboBoxHttpAuthCertMDN, gridBagConstraints);

        // Hidden radio button for AUTH_MODE_USER_PREFERENCE (backward compatibility)
        buttonGroupAuthenticationMDN.add(jRadioButtonHttpAuthUserPreferenceMDN);
        jRadioButtonHttpAuthUserPreferenceMDN.setVisible(false);

        buttonGroupAuthenticationMDN.add(jRadioButtonHttpAuthNoneMDN);
        jRadioButtonHttpAuthNoneMDN.setSelected(true);
        jRadioButtonHttpAuthNoneMDN.setText(JPanelPartner.rb.getResourceString("label.httpauth.none"));
        jRadioButtonHttpAuthNoneMDN.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonHttpAuthCredentialsMDNItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(jRadioButtonHttpAuthNoneMDN, gridBagConstraints);

        jPanelHTTPAuthCredentialsMessage.setMinimumSize(new java.awt.Dimension(430, 60));
        jPanelHTTPAuthCredentialsMessage.setPreferredSize(new java.awt.Dimension(430, 60));
        jPanelHTTPAuthCredentialsMessage.setLayout(new java.awt.GridBagLayout());

        jLabelHttpAuth.setText(JPanelPartner.rb.getResourceString("label.httpauth.credentials.message.user"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHTTPAuthCredentialsMessage.add(jLabelHttpAuth, gridBagConstraints);

        jTextFieldHttpAuthMessageUser.setColumns(30);
        jTextFieldHttpAuthMessageUser.setMinimumSize(new java.awt.Dimension(150, 22));
        jTextFieldHttpAuthMessageUser.setPreferredSize(new java.awt.Dimension(150, 22));
        jTextFieldHttpAuthMessageUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldHttpAuthMessageUserKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHTTPAuthCredentialsMessage.add(jTextFieldHttpAuthMessageUser, gridBagConstraints);

        jLabelHttpPass.setText(JPanelPartner.rb.getResourceString("label.httpauth.credentials.message.pass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHTTPAuthCredentialsMessage.add(jLabelHttpPass, gridBagConstraints);

        jPasswordFieldHttpAuthMessagePass.setColumns(30);
        jPasswordFieldHttpAuthMessagePass.setText("");
        jPasswordFieldHttpAuthMessagePass.setMinimumSize(new java.awt.Dimension(150, 22));
        jPasswordFieldHttpAuthMessagePass.setPreferredSize(new java.awt.Dimension(150, 22));
        jPasswordFieldHttpAuthMessagePass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordFieldHttpAuthMessagePassKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHTTPAuthCredentialsMessage.add(jPasswordFieldHttpAuthMessagePass, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        jPanelHttpAuthData.add(jPanelHTTPAuthCredentialsMessage, gridBagConstraints);

        jPanelHTTPAuthCredentialsMDN.setMinimumSize(new java.awt.Dimension(430, 60));
        jPanelHTTPAuthCredentialsMDN.setPreferredSize(new java.awt.Dimension(430, 60));
        jPanelHTTPAuthCredentialsMDN.setLayout(new java.awt.GridBagLayout());

        jLabelHttpAuthAsyncMDN.setText(JPanelPartner.rb.getResourceString("label.httpauth.credentials.asyncmdn.user"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHTTPAuthCredentialsMDN.add(jLabelHttpAuthAsyncMDN, gridBagConstraints);

        jTextFieldHttpAuthAsyncMDNUser.setColumns(30);
        jTextFieldHttpAuthAsyncMDNUser.setMinimumSize(new java.awt.Dimension(150, 22));
        jTextFieldHttpAuthAsyncMDNUser.setPreferredSize(new java.awt.Dimension(150, 22));
        jTextFieldHttpAuthAsyncMDNUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldHttpAuthAsyncMDNUserKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHTTPAuthCredentialsMDN.add(jTextFieldHttpAuthAsyncMDNUser, gridBagConstraints);

        jLabelHttpPassAsyncMDN.setText(JPanelPartner.rb.getResourceString("label.httpauth.credentials.asyncmdn.pass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHTTPAuthCredentialsMDN.add(jLabelHttpPassAsyncMDN, gridBagConstraints);

        jPasswordFieldHttpPassAsyncMDN.setColumns(30);
        jPasswordFieldHttpPassAsyncMDN.setText("");
        jPasswordFieldHttpPassAsyncMDN.setMinimumSize(new java.awt.Dimension(150, 22));
        jPasswordFieldHttpPassAsyncMDN.setPreferredSize(new java.awt.Dimension(150, 22));
        jPasswordFieldHttpPassAsyncMDN.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordFieldHttpPassAsyncMDNKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHTTPAuthCredentialsMDN.add(jPasswordFieldHttpPassAsyncMDN, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        jPanelHttpAuthData.add(jPanelHTTPAuthCredentialsMDN, gridBagConstraints);

        jLabelHttpAuthMessage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelHttpAuthMessage.setText(JPanelPartner.rb.getResourceString("label.httpauth.message"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(jLabelHttpAuthMessage, gridBagConstraints);

        HttpAuthNoneMDN.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        HttpAuthNoneMDN.setText(JPanelPartner.rb.getResourceString("label.httpauth.asyncmdn"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHttpAuthData.add(HttpAuthNoneMDN, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelHttpAuthData.add(jPanelSpace345, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelHttpAuthData.add(jPanelSpace485, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        jPanelHTTPAuth.add(jPanelHttpAuthData, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.httpauth"), jPanelHTTPAuth);

        jPanelHTTPHeader.setLayout(new java.awt.GridBagLayout());

        jTableHttpHeader.setModel(new TableModelHttpHeader());
        jScrollPaneHttpHeader.setViewportView(jTableHttpHeader);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        jPanelHTTPHeader.add(jScrollPaneHttpHeader, gridBagConstraints);

        jButtonHttpHeaderAdd.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        jButtonHttpHeaderAdd.setText(JPanelPartner.rb.getResourceString("httpheader.add"));
        jButtonHttpHeaderAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonHttpHeaderAdd.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonHttpHeaderAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonHttpHeaderAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHttpHeaderAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        jPanelHTTPHeader.add(jButtonHttpHeaderAdd, gridBagConstraints);

        jButtonHttpHeaderRemove.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        jButtonHttpHeaderRemove.setText(JPanelPartner.rb.getResourceString("httpheader.delete"));
        jButtonHttpHeaderRemove.setEnabled(false);
        jButtonHttpHeaderRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonHttpHeaderRemove.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonHttpHeaderRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonHttpHeaderRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHttpHeaderRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHTTPHeader.add(jButtonHttpHeaderRemove, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.httpheader"), jPanelHTTPHeader);

        jPanelNotification.setLayout(new java.awt.GridBagLayout());

        jPanelNotificationMain.setLayout(new java.awt.GridBagLayout());

        jCheckBoxNotifySend.setText(JPanelPartner.rb.getResourceString("label.notify.send"));
        jCheckBoxNotifySend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxNotifySendActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationMain.add(jCheckBoxNotifySend, gridBagConstraints);

        jCheckBoxNotifyReceive.setText(JPanelPartner.rb.getResourceString("label.notify.receive"));
        jCheckBoxNotifyReceive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxNotifyReceiveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationMain.add(jCheckBoxNotifyReceive, gridBagConstraints);

        jCheckBoxNotifySendReceive.setText(JPanelPartner.rb.getResourceString("label.notify.sendreceive"));
        jCheckBoxNotifySendReceive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxNotifySendReceiveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationMain.add(jCheckBoxNotifySendReceive, gridBagConstraints);

        jTextFieldNotifyReceive.setText("0");
        jTextFieldNotifyReceive.setPreferredSize(new java.awt.Dimension(50, 22));
        jTextFieldNotifyReceive.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNotifyReceiveKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationMain.add(jTextFieldNotifyReceive, gridBagConstraints);

        jTextFieldNotifySend.setText("0");
        jTextFieldNotifySend.setPreferredSize(new java.awt.Dimension(50, 22));
        jTextFieldNotifySend.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNotifySendKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationMain.add(jTextFieldNotifySend, gridBagConstraints);

        jTextFieldNotifySendReceive.setText("0");
        jTextFieldNotifySendReceive.setPreferredSize(new java.awt.Dimension(50, 22));
        jTextFieldNotifySendReceive.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNotifySendReceiveKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationMain.add(jTextFieldNotifySendReceive, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelNotificationMain.add(jPanelSpace23, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        jPanelNotification.add(jPanelNotificationMain, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.notification"), jPanelNotification);

        jPanelEvents.setLayout(new java.awt.GridBagLayout());

        jPanelEventsMain.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEventsMain.add(jPanelSpace, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        jPanelEventsMain.add(jPanelSpace123, gridBagConstraints);

        jPanelPostprocessingSend.setLayout(new java.awt.GridBagLayout());

        jLabelIconProcessTypeOnReceipt.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 5);
        jPanelPostprocessingSend.add(jLabelIconProcessTypeOnReceipt, gridBagConstraints);

        jTextFieldEventInfoOnReceipt.setEditable(false);
        jTextFieldEventInfoOnReceipt.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        jPanelPostprocessingSend.add(jTextFieldEventInfoOnReceipt, gridBagConstraints);

        jButtonEditEventOnReceipt.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        jButtonEditEventOnReceipt.setToolTipText(JPanelPartner.rb.getResourceString("tooltip.button.editevent"));
        jButtonEditEventOnReceipt.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jButtonEditEventOnReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditEventOnReceiptActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelPostprocessingSend.add(jButtonEditEventOnReceipt, gridBagConstraints);

        jButtonAddEventOnReceipt.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        jButtonAddEventOnReceipt.setToolTipText(JPanelPartner.rb.getResourceString("tooltip.button.addevent"));
        jButtonAddEventOnReceipt.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jButtonAddEventOnReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddEventOnReceiptActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelPostprocessingSend.add(jButtonAddEventOnReceipt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        jPanelEventsMain.add(jPanelPostprocessingSend, gridBagConstraints);

        jPanelPostprocessingReceivedFailure.setLayout(new java.awt.GridBagLayout());

        jLabelIconProcessTypeOnSendError.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 5);
        jPanelPostprocessingReceivedFailure.add(jLabelIconProcessTypeOnSendError, gridBagConstraints);

        jTextFieldEventInfoOnSendError.setEditable(false);
        jTextFieldEventInfoOnSendError.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        jPanelPostprocessingReceivedFailure.add(jTextFieldEventInfoOnSendError, gridBagConstraints);

        jButtonEditEventOnSendError.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        jButtonEditEventOnSendError.setToolTipText(JPanelPartner.rb.getResourceString("tooltip.button.editevent"));
        jButtonEditEventOnSendError.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jButtonEditEventOnSendError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditEventOnSendErrorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelPostprocessingReceivedFailure.add(jButtonEditEventOnSendError, gridBagConstraints);

        jButtonAddEventOnSendError.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        jButtonAddEventOnSendError.setToolTipText(JPanelPartner.rb.getResourceString("tooltip.button.addevent"));
        jButtonAddEventOnSendError.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jButtonAddEventOnSendError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddEventOnSendErrorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelPostprocessingReceivedFailure.add(jButtonAddEventOnSendError, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        jPanelEventsMain.add(jPanelPostprocessingReceivedFailure, gridBagConstraints);

        jPanelPostProcessingReceiptSuccess.setLayout(new java.awt.GridBagLayout());

        jLabelIconProcessTypeOnSendSuccess.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 5);
        jPanelPostProcessingReceiptSuccess.add(jLabelIconProcessTypeOnSendSuccess, gridBagConstraints);

        jTextFieldEventInfoOnSendSuccess.setEditable(false);
        jTextFieldEventInfoOnSendSuccess.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        jPanelPostProcessingReceiptSuccess.add(jTextFieldEventInfoOnSendSuccess, gridBagConstraints);

        jButtonEditEventOnSendSuccess.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        jButtonEditEventOnSendSuccess.setToolTipText(JPanelPartner.rb.getResourceString("tooltip.button.editevent"));
        jButtonEditEventOnSendSuccess.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jButtonEditEventOnSendSuccess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditEventOnSendSuccessActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelPostProcessingReceiptSuccess.add(jButtonEditEventOnSendSuccess, gridBagConstraints);

        jButtonAddEventOnSendSuccess.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image24x24.gif"))); // NOI18N
        jButtonAddEventOnSendSuccess.setToolTipText(JPanelPartner.rb.getResourceString("tooltip.button.addevent"));
        jButtonAddEventOnSendSuccess.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jButtonAddEventOnSendSuccess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddEventOnSendSuccessActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelPostProcessingReceiptSuccess.add(jButtonAddEventOnSendSuccess, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        jPanelEventsMain.add(jPanelPostProcessingReceiptSuccess, gridBagConstraints);

        jLabelUseEventOnReceipt.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabelUseEventOnReceipt.setText(JPanelPartner.rb.getResourceString("label.usecommandonreceipt"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEventsMain.add(jLabelUseEventOnReceipt, gridBagConstraints);

        switchUseEventOnReceipt.setDisplayStatusText(true);
        switchUseEventOnReceipt.setHorizontalTextPosition(SwingConstants.LEFT);
        switchUseEventOnReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchUseEventOnReceiptActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelEventsMain.add(switchUseEventOnReceipt, gridBagConstraints);

        switchUseEventOnSendSuccess.setDisplayStatusText(true);
        switchUseEventOnSendSuccess.setHorizontalTextPosition(SwingConstants.LEFT);
        switchUseEventOnSendSuccess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchUseEventOnSendSuccessActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelEventsMain.add(switchUseEventOnSendSuccess, gridBagConstraints);

        jLabelUseEventOnSendSuccess.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabelUseEventOnSendSuccess.setText(JPanelPartner.rb.getResourceString("label.usecommandonsendsuccess"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEventsMain.add(jLabelUseEventOnSendSuccess, gridBagConstraints);

        jLabelUseEventOnSendError.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabelUseEventOnSendError.setText(JPanelPartner.rb.getResourceString("label.usecommandonsenderror"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEventsMain.add(jLabelUseEventOnSendError, gridBagConstraints);

        switchUseEventOnSendError.setDisplayStatusText(true);
        switchUseEventOnSendError.setHorizontalTextPosition(SwingConstants.LEFT);
        switchUseEventOnSendError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchUseEventOnSendErrorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelEventsMain.add(switchUseEventOnSendError, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        jPanelEventsMain.add(jPanelSpace126, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        jPanelEventsMain.add(jPanelSpace127, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        jPanelEventsMain.add(jPanelSpace129, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        jPanelEventsMain.add(jPanelSpace128, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 5);
        jPanelEventsMain.add(jPanelSpace875, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 10);
        jPanelEvents.add(jPanelEventsMain, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.events"), jPanelEvents);

        jPanelPartnerSystem.setLayout(new java.awt.GridBagLayout());

        jPanelPartnerSystemMain.setLayout(new java.awt.GridBagLayout());

        jLabelAS2Version.setText(JPanelPartner.rb.getResourceString("label.as2version"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelPartnerSystemMain.add(jLabelAS2Version, gridBagConstraints);

        jLabelProductName.setText(JPanelPartner.rb.getResourceString("label.productname"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelPartnerSystemMain.add(jLabelProductName, gridBagConstraints);

        jLabelFeatures.setText(JPanelPartner.rb.getResourceString("label.features"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 10, 5);
        jPanelPartnerSystemMain.add(jLabelFeatures, gridBagConstraints);

        jCheckBoxEdiintFeaturesCompression.setText(JPanelPartner.rb.getResourceString("label.features.compression"));
        jCheckBoxEdiintFeaturesCompression.setEnabled(false);
        jCheckBoxEdiintFeaturesCompression.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelPartnerSystemMain.add(jCheckBoxEdiintFeaturesCompression, gridBagConstraints);

        jCheckBoxEdiintFeaturesMA.setText(JPanelPartner.rb.getResourceString("label.features.ma"));
        jCheckBoxEdiintFeaturesMA.setEnabled(false);
        jCheckBoxEdiintFeaturesMA.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelPartnerSystemMain.add(jCheckBoxEdiintFeaturesMA, gridBagConstraints);

        jCheckBoxEdiintFeaturesCEM.setText(JPanelPartner.rb.getResourceString("label.features.cem"));
        jCheckBoxEdiintFeaturesCEM.setEnabled(false);
        jCheckBoxEdiintFeaturesCEM.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelPartnerSystemMain.add(jCheckBoxEdiintFeaturesCEM, gridBagConstraints);

        jTextFieldAS2Version.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelPartnerSystemMain.add(jTextFieldAS2Version, gridBagConstraints);

        jTextFieldProductName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelPartnerSystemMain.add(jTextFieldProductName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelPartnerSystemMain.add(jPanelSpaceSpace, gridBagConstraints);

        jScrollPaneTextAreaPartnerSystemInformation.setBorder(null);

        jTextAreaPartnerSystemInformation.setEditable(false);
        jTextAreaPartnerSystemInformation
                .setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jTextAreaPartnerSystemInformation.setColumns(20);
        jTextAreaPartnerSystemInformation.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        jTextAreaPartnerSystemInformation.setLineWrap(true);
        jTextAreaPartnerSystemInformation.setRows(5);
        jTextAreaPartnerSystemInformation.setWrapStyleWord(true);
        jTextAreaPartnerSystemInformation.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextAreaPartnerSystemInformation.setFocusable(false);
        jScrollPaneTextAreaPartnerSystemInformation.setViewportView(jTextAreaPartnerSystemInformation);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanelPartnerSystemMain.add(jScrollPaneTextAreaPartnerSystemInformation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanelPartnerSystem.add(jPanelPartnerSystemMain, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.partnersystem"), jPanelPartnerSystem);

        jPanelMisc.setLayout(new java.awt.GridBagLayout());

        jPanelMiscMain.setLayout(new java.awt.GridBagLayout());

        jTextFieldId.setColumns(30);
        jTextFieldId.setMinimumSize(new java.awt.Dimension(300, 22));
        jTextFieldId.setPreferredSize(new java.awt.Dimension(300, 22));
        jTextFieldId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldIdKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMiscMain.add(jTextFieldId, gridBagConstraints);

        jTextFieldName.setColumns(30);
        jTextFieldName.setMinimumSize(new java.awt.Dimension(300, 22));
        jTextFieldName.setPreferredSize(new java.awt.Dimension(300, 22));
        jTextFieldName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNameKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMiscMain.add(jTextFieldName, gridBagConstraints);

        jTextFieldEMail.setColumns(30);
        jTextFieldEMail.setMinimumSize(new java.awt.Dimension(300, 22));
        jTextFieldEMail.setPreferredSize(new java.awt.Dimension(300, 22));
        jTextFieldEMail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldEMailKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMiscMain.add(jTextFieldEMail, gridBagConstraints);

        jScrollPanePartnerComment.setViewportView(jTextPanePartnerComment);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        jPanelMiscMain.add(jScrollPanePartnerComment, gridBagConstraints);

        jScrollPanePartnerAddress.setViewportView(jTextPanePartnerContact);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        jPanelMiscMain.add(jScrollPanePartnerAddress, gridBagConstraints);

        jScrollPanePartnerContact.setViewportView(jTextPanePartnerAddress);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        jPanelMiscMain.add(jScrollPanePartnerContact, gridBagConstraints);

        jPanelUIHelpLabelName.setToolTipText(JPanelPartner.rb.getResourceString("label.name.help"));
        jPanelUIHelpLabelName.setText(JPanelPartner.rb.getResourceString("label.name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMiscMain.add(jPanelUIHelpLabelName, gridBagConstraints);

        jPanelUIHelpLabelAS2Id.setToolTipText(JPanelPartner.rb.getResourceString("label.id.help"));
        jPanelUIHelpLabelAS2Id.setText(JPanelPartner.rb.getResourceString("label.id"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMiscMain.add(jPanelUIHelpLabelAS2Id, gridBagConstraints);

        jPanelUIHelpLabelEMail.setToolTipText(JPanelPartner.rb.getResourceString("label.email.help"));
        jPanelUIHelpLabelEMail.setText(JPanelPartner.rb.getResourceString("label.email"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMiscMain.add(jPanelUIHelpLabelEMail, gridBagConstraints);

        jPanelUIHelpLabelAddress.setToolTipText(JPanelPartner.rb.getResourceString("label.notes.help"));
        jPanelUIHelpLabelAddress.setHelpEnabled(false);
        jPanelUIHelpLabelAddress.setText(JPanelPartner.rb.getResourceString("label.address"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMiscMain.add(jPanelUIHelpLabelAddress, gridBagConstraints);

        jPanelUIHelpLabelComment.setToolTipText(JPanelPartner.rb.getResourceString("label.notes.help"));
        jPanelUIHelpLabelComment.setHelpEnabled(false);
        jPanelUIHelpLabelComment.setText(JPanelPartner.rb.getResourceString("label.partnercomment"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMiscMain.add(jPanelUIHelpLabelComment, gridBagConstraints);

        jPanelUIHelpLabelContact.setToolTipText(JPanelPartner.rb.getResourceString("label.notes.help"));
        jPanelUIHelpLabelContact.setHelpEnabled(false);
        jPanelUIHelpLabelContact.setText(JPanelPartner.rb.getResourceString("label.contact"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMiscMain.add(jPanelUIHelpLabelContact, gridBagConstraints);

        jPanelUIHelpLabelName1.setToolTipText(JPanelPartner.rb.getResourceString("label.localstation.help"));
        jPanelUIHelpLabelName1.setText(JPanelPartner.rb.getResourceString("label.localstation"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        jPanelMiscMain.add(jPanelUIHelpLabelName1, gridBagConstraints);

        switchLocalStation.setDisplayStatusText(true);
        switchLocalStation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                switchLocalStationItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelMiscMain.add(switchLocalStation, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMiscMain.add(jPanelSpace874, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMisc.add(jPanelMiscMain, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.misc"), jPanelMisc);

        jPanelSecurity.setLayout(new java.awt.GridBagLayout());

        jPanelSecurityMain.setLayout(new java.awt.GridBagLayout());

        jComboBoxSignCert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSignCertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelSecurityMain.add(jComboBoxSignCert, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelSecurityMain.add(jPanelSpace2, gridBagConstraints);

        jComboBoxSignType.setMinimumSize(new java.awt.Dimension(190, 24));
        jComboBoxSignType.setPreferredSize(new java.awt.Dimension(190, 24));
        jComboBoxSignType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSignTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelSecurityMain.add(jComboBoxSignType, gridBagConstraints);

        jComboBoxEncryptionType.setMinimumSize(new java.awt.Dimension(190, 24));
        jComboBoxEncryptionType.setPreferredSize(new java.awt.Dimension(190, 24));
        jComboBoxEncryptionType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxEncryptionTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelSecurityMain.add(jComboBoxEncryptionType, gridBagConstraints);

        jComboBoxCryptCert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCryptCertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelSecurityMain.add(jComboBoxCryptCert, gridBagConstraints);

        jPanelUIHelpLabelCryptAlias.setToolTipText(JPanelPartner.rb.getResourceString("label.mdnurl.help"));
        jPanelUIHelpLabelCryptAlias.setText(JPanelPartner.rb.getResourceString("label.mdnurl"));
        jPanelUIHelpLabelCryptAlias.setTooltipWidth(250);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelSecurityMain.add(jPanelUIHelpLabelCryptAlias, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSecurityMain.add(jPanelSpaceSecurity, gridBagConstraints);

        jPanelUIHelpLabelSignAlias.setToolTipText(JPanelPartner.rb.getResourceString("label.mdnurl.help"));
        jPanelUIHelpLabelSignAlias.setText(JPanelPartner.rb.getResourceString("label.mdnurl"));
        jPanelUIHelpLabelSignAlias.setTooltipWidth(250);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelSecurityMain.add(jPanelUIHelpLabelSignAlias, gridBagConstraints);

        jPanelUIHelpLabelSignType.setToolTipText(JPanelPartner.rb.getResourceString("label.signtype.help"));
        jPanelUIHelpLabelSignType.setText(JPanelPartner.rb.getResourceString("label.signtype"));
        jPanelUIHelpLabelSignType.setTooltipWidth(250);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelSecurityMain.add(jPanelUIHelpLabelSignType, gridBagConstraints);

        jPanelUIHelpLabelEncryptionType.setToolTipText(JPanelPartner.rb.getResourceString("label.encryptiontype.help"));
        jPanelUIHelpLabelEncryptionType.setText(JPanelPartner.rb.getResourceString("label.encryptiontype"));
        jPanelUIHelpLabelEncryptionType.setTooltipWidth(250);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelSecurityMain.add(jPanelUIHelpLabelEncryptionType, gridBagConstraints);

        jPanelOverwriteLocalStationSecurity.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelOverwriteLocalStationSecurity.setLayout(new java.awt.GridBagLayout());

        jPanelUIHelpLabelOverwriteCryptAliasLocalStation
                .setToolTipText(JPanelPartner.rb.getResourceString("label.overwrite.crypt.help"));
        jPanelUIHelpLabelOverwriteCryptAliasLocalStation
                .setText(JPanelPartner.rb.getResourceString("label.overwrite.crypt"));
        jPanelUIHelpLabelOverwriteCryptAliasLocalStation.setTooltipWidth(250);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        jPanelOverwriteLocalStationSecurity.add(jPanelUIHelpLabelOverwriteCryptAliasLocalStation, gridBagConstraints);

        jComboBoxOverwriteLocalStationCryptKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxOverwriteLocalStationCryptKeyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelOverwriteLocalStationSecurity.add(jComboBoxOverwriteLocalStationCryptKey, gridBagConstraints);

        jPanelUIHelpLabelOverwriteLocalstationSignAlias
                .setToolTipText(JPanelPartner.rb.getResourceString("label.overwrite.sign.help"));
        jPanelUIHelpLabelOverwriteLocalstationSignAlias
                .setText(JPanelPartner.rb.getResourceString("label.overwrite.sign"));
        jPanelUIHelpLabelOverwriteLocalstationSignAlias.setTooltipWidth(250);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        jPanelOverwriteLocalStationSecurity.add(jPanelUIHelpLabelOverwriteLocalstationSignAlias, gridBagConstraints);

        jComboBoxOverwriteLocalstationSignKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxOverwriteLocalstationSignKeyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelOverwriteLocalStationSecurity.add(jComboBoxOverwriteLocalstationSignKey, gridBagConstraints);

        buttonGroupOverwriteLocalStationSecurity.add(jRadioButtonOverwriteLocalstationSecurity);
        jRadioButtonOverwriteLocalstationSecurity
                .setText(JPanelPartner.rb.getResourceString("label.overwrite.security"));
        jRadioButtonOverwriteLocalstationSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonOverwriteLocalstationSecurityActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelOverwriteLocalStationSecurity.add(jRadioButtonOverwriteLocalstationSecurity, gridBagConstraints);

        buttonGroupOverwriteLocalStationSecurity.add(jRadioButtonKeepLocalstationSecurity);
        jRadioButtonKeepLocalstationSecurity.setSelected(true);
        jRadioButtonKeepLocalstationSecurity.setText(JPanelPartner.rb.getResourceString("label.keep.security"));
        jRadioButtonKeepLocalstationSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonKeepLocalstationSecurityActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelOverwriteLocalStationSecurity.add(jRadioButtonKeepLocalstationSecurity, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanelOverwriteLocalStationSecurity.add(jPanelSpace585, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 3, 1, 3);
        jPanelOverwriteLocalStationSecurity.add(jPanelSpace89582, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 10);
        jPanelSecurityMain.add(jPanelOverwriteLocalStationSecurity, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSecurityMain.add(jPanelSpace455, gridBagConstraints);

        switchUseAlgorithmIdentifierProtectionAttribute.setDisplayStatusText(true);
        switchUseAlgorithmIdentifierProtectionAttribute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchUseAlgorithmIdentifierProtectionAttributeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSecurityMain.add(switchUseAlgorithmIdentifierProtectionAttribute, gridBagConstraints);

        jPanelUIHelpLabelUseAlgorithmIdentifierProtectionAttribute
                .setToolTipText(JPanelPartner.rb.getResourceString("label.algorithmidentifierprotection.help"));
        jPanelUIHelpLabelUseAlgorithmIdentifierProtectionAttribute
                .setText(JPanelPartner.rb.getResourceString("label.algorithmidentifierprotection"));
        jPanelUIHelpLabelUseAlgorithmIdentifierProtectionAttribute.setTooltipWidth(350);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelSecurityMain.add(jPanelUIHelpLabelUseAlgorithmIdentifierProtectionAttribute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        jPanelSecurity.add(jPanelSecurityMain, gridBagConstraints);

        jTabbedPane.addTab(JPanelPartner.rb.getResourceString("tab.security"), jPanelSecurity);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jTabbedPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxCryptCertActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxCryptCertActionPerformed
        if (this.partner != null && this.jComboBoxCryptCert.getSelectedItem() != null) {
            KeystoreCertificate certificate = (KeystoreCertificate) this.jComboBoxCryptCert.getSelectedItem();
            PartnerCertificateInformation cryptInfo = new PartnerCertificateInformation(
                    certificate.getFingerPrintSHA1(),
                    PartnerCertificateInformation.CATEGORY_CRYPT);
            partner.setCertificateInformation(cryptInfo);
            partner.getCryptFingerprintSHA1();
            this.informTreeModelNodeChanged();
        }
        this.setButtonState();
    }// GEN-LAST:event_jComboBoxCryptCertActionPerformed

    private void jPasswordFieldHttpPassAsyncMDNKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jPasswordFieldHttpPassAsyncMDNKeyReleased
        if (this.jRadioButtonHttpAuthCredentialsMDN.isSelected()) {
            if (this.partner != null && this.partner.getAuthenticationCredentialsAsyncMDN() != null) {
                this.partner.getAuthenticationCredentialsAsyncMDN()
                        .setPassword(new String(this.jPasswordFieldHttpPassAsyncMDN.getPassword()));
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            }
        }
    }// GEN-LAST:event_jPasswordFieldHttpPassAsyncMDNKeyReleased

    private void jTextFieldHttpAuthAsyncMDNUserKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldHttpAuthAsyncMDNUserKeyReleased
        if (this.jRadioButtonHttpAuthCredentialsMDN.isSelected()) {
            if (this.partner != null && this.partner.getAuthenticationCredentialsAsyncMDN() != null) {
                this.partner.getAuthenticationCredentialsAsyncMDN()
                        .setUser(this.jTextFieldHttpAuthAsyncMDNUser.getText());
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            }
        }
    }// GEN-LAST:event_jTextFieldHttpAuthAsyncMDNUserKeyReleased

    private void jPasswordFieldHttpAuthMessagePassKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jPasswordFieldHttpAuthMessagePassKeyReleased
        if (this.jRadioButtonHttpAuthCredentialsMessage.isSelected()) {
            if (this.partner != null && this.partner.getAuthenticationCredentialsMessage() != null) {
                this.partner.getAuthenticationCredentialsMessage()
                        .setPassword(new String(this.jPasswordFieldHttpAuthMessagePass.getPassword()));
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            }
        }
    }// GEN-LAST:event_jPasswordFieldHttpAuthMessagePassKeyReleased

    private void jTextFieldHttpAuthMessageUserKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldHttpAuthMessageUserKeyReleased
        if (this.jRadioButtonHttpAuthCredentialsMessage.isSelected()) {
            if (this.partner != null && this.partner.getAuthenticationCredentialsMessage() != null) {
                this.partner.getAuthenticationCredentialsMessage()
                        .setUser(this.jTextFieldHttpAuthMessageUser.getText());
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            }
        }
    }// GEN-LAST:event_jTextFieldHttpAuthMessageUserKeyReleased

    private void jTextFieldIgnorePollFilterListKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldIgnorePollFilterListKeyReleased
        if (this.partner != null) {
            this.partner.setPollIgnoreListString(this.jTextFieldIgnorePollFilterList.getText());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jTextFieldIgnorePollFilterListKeyReleased

    private void jTextFieldPollIntervalKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldPollIntervalKeyReleased
        if (this.partner != null) {
            try {
                int pollInterval = Integer.parseInt(this.jTextFieldPollInterval.getText().trim());
                this.partner.setPollInterval(pollInterval);
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            } catch (Exception e) {
                // nop
            }
        }
    }// GEN-LAST:event_jTextFieldPollIntervalKeyReleased

    private void jRadioButtonSyncMDNActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButtonSyncMDNActionPerformed
        if (this.partner != null) {
            this.partner.setSyncMDN(true);
            this.jLabelIconSyncMDN.setEnabled(true);
            this.jLabelIconAsyncMDN.setEnabled(false);
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jRadioButtonSyncMDNActionPerformed

    private void jRadioButtonAsyncMDNActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButtonAsyncMDNActionPerformed
        if (this.partner != null) {
            this.partner.setSyncMDN(false);
            this.jLabelIconSyncMDN.setEnabled(false);
            this.jLabelIconAsyncMDN.setEnabled(true);
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jRadioButtonAsyncMDNActionPerformed

    private void jTextFieldContentTypeKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldContentTypeKeyReleased
        if (this.partner != null) {
            if (this.jTextFieldContentType.getText().trim().isEmpty()) {
                this.partner.setContentType("application/EDI-Consent");
            } else {
                this.partner.setContentType(this.jTextFieldContentType.getText());
            }
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jTextFieldContentTypeKeyReleased

    private void jTextFieldSubjectKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldSubjectKeyReleased
        if (this.partner != null) {
            if (this.jTextFieldSubject.getText().trim().isEmpty()) {
                this.partner.setSubject("AS2 message");
            } else {
                this.partner.setSubject(this.jTextFieldSubject.getText());
            }
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jTextFieldSubjectKeyReleased

    private void jTextFieldEMailKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldEMailKeyReleased
        if (this.partner != null) {
            if (this.jTextFieldEMail.getText().trim().isEmpty()) {
                this.partner.setEmail("sender@as2server.com");
            } else {
                this.partner.setEmail(this.jTextFieldEMail.getText());
            }
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jTextFieldEMailKeyReleased

    private void jTextFieldMDNURLKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldMDNURLKeyReleased
        if (this.partner != null) {
            if (this.jTextFieldMDNURL.getText().trim().isEmpty()) {
                this.partner.setMdnURL(this.partner.getDefaultURL());
            } else {
                this.partner.setMdnURL(this.jTextFieldMDNURL.getText());
            }
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jTextFieldMDNURLKeyReleased

    private void jButtonMDNURLHttpActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.partner != null) {
            try {
                // Get username from baseClient
                String username = this.baseClient.getUsername();

                // Get actual HTTP port - use AS2Config which handles test mode automatically
                de.mendelson.comm.as2.AS2Config config = new de.mendelson.comm.as2.AS2Config();
                int httpPort = config.getHttpPort();

                // Get hostname - use InetAddress to get actual hostname, fallback to localhost
                String hostname = "localhost";
                try {
                    hostname = java.net.InetAddress.getLocalHost().getHostName();
                } catch (Exception e) {
                    // Fallback to localhost if unable to get hostname
                    hostname = "localhost";
                }

                // Build HTTP MDN URL: http://{hostname}:{port}/as2/HttpReceiver/{username}
                String mdnUrl = "http://" + hostname + ":" + httpPort + "/as2/HttpReceiver/" + username;

                this.jTextFieldMDNURL.setText(mdnUrl);
                this.partner.setMdnURL(mdnUrl);
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            } catch (Exception e) {
                // If any error occurs, show error message
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Failed to generate HTTP URL: " + e.getMessage(),
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jButtonMDNURLHttpsActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.partner != null) {
            try {
                // Get username from baseClient
                String username = this.baseClient.getUsername();

                // Get actual HTTPS port - use AS2Config which handles test mode automatically
                de.mendelson.comm.as2.AS2Config config = new de.mendelson.comm.as2.AS2Config();
                int httpsPort = config.getHttpsPort();

                // Get hostname - use InetAddress to get actual hostname, fallback to localhost
                String hostname = "localhost";
                try {
                    hostname = java.net.InetAddress.getLocalHost().getHostName();
                } catch (Exception e) {
                    // Fallback to localhost if unable to get hostname
                    hostname = "localhost";
                }

                // Build HTTPS MDN URL: https://{hostname}:{port}/as2/HttpReceiver/{username}
                String mdnUrl = "https://" + hostname + ":" + httpsPort + "/as2/HttpReceiver/" + username;

                this.jTextFieldMDNURL.setText(mdnUrl);
                this.partner.setMdnURL(mdnUrl);
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            } catch (Exception e) {
                // If any error occurs, show error message
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Failed to generate HTTPS URL: " + e.getMessage(),
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jComboBoxEncryptionTypeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxEncryptionTypeActionPerformed
        if (this.partner != null) {
            if (this.jComboBoxEncryptionType.getSelectedItem() != null) {
                EncryptionDisplayImplAS2 selectedItem = (EncryptionDisplayImplAS2) this.jComboBoxEncryptionType
                        .getSelectedItem();
                this.partner.setEncryptionType(((Integer) selectedItem.getWrappedValue()).intValue());
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            }
        }
    }// GEN-LAST:event_jComboBoxEncryptionTypeActionPerformed

    private void jComboBoxSignCertActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxSignCertActionPerformed
        if (this.partner != null && this.jComboBoxSignCert.getSelectedItem() != null) {
            KeystoreCertificate certificate = (KeystoreCertificate) this.jComboBoxSignCert.getSelectedItem();
            PartnerCertificateInformation signInfo = new PartnerCertificateInformation(
                    certificate.getFingerPrintSHA1(),
                    PartnerCertificateInformation.CATEGORY_SIGN);
            partner.setCertificateInformation(signInfo);
            partner.getSignFingerprintSHA1();
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
        this.setButtonState();
    }// GEN-LAST:event_jComboBoxSignCertActionPerformed

    private void jComboBoxSignTypeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxSignTypeActionPerformed
        if (this.partner != null && this.jComboBoxSignType.getSelectedItem() != null) {
            SignatureDisplayImplAS2 signatureDisplay = (SignatureDisplayImplAS2) this.jComboBoxSignType
                    .getSelectedItem();
            this.partner.setSignType(((Integer) signatureDisplay.getWrappedValue()).intValue());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
        this.setButtonState();
    }// GEN-LAST:event_jComboBoxSignTypeActionPerformed

    private void jTextFieldReceiptURLKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldReceiptURLKeyReleased
        if (this.partner != null) {
            this.partner.setURL(this.jTextFieldReceiptURL.getText());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jTextFieldReceiptURLKeyReleased

    private void jTextFieldIdKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldIdKeyReleased
        if (this.partner != null) {
            this.partner.setAS2Identification(this.jTextFieldId.getText());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jTextFieldIdKeyReleased

    private void jTextFieldNameKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldNameKeyReleased
        if (this.partner != null) {
            this.partner.setName(this.jTextFieldName.getText());
            this.updatePollDirDisplay(this.partner);
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jTextFieldNameKeyReleased

    private void jTextFieldNotifySendKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldNotifySendKeyReleased
        if (this.jCheckBoxNotifySend.isSelected()) {
            if (this.partner != null) {
                try {
                    this.partner.setNotifySend(Integer.parseInt(this.jTextFieldNotifySend.getText()));
                    this.buttonOk.computeErrorState();
                    this.informTreeModelNodeChanged();
                } catch (NumberFormatException e) {
                    // nop
                }
            }
        }
    }// GEN-LAST:event_jTextFieldNotifySendKeyReleased

    private void jTextFieldNotifyReceiveKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldNotifyReceiveKeyReleased
        if (this.jCheckBoxNotifyReceive.isSelected()) {
            if (this.partner != null) {
                try {
                    this.partner.setNotifyReceive(Integer.parseInt(this.jTextFieldNotifyReceive.getText()));
                    this.buttonOk.computeErrorState();
                    this.informTreeModelNodeChanged();
                } catch (NumberFormatException e) {
                    // nop
                }
            }
        }
    }// GEN-LAST:event_jTextFieldNotifyReceiveKeyReleased

    private void jTextFieldNotifySendReceiveKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldNotifySendReceiveKeyReleased
        if (this.jCheckBoxNotifySendReceive.isSelected()) {
            if (this.partner != null) {
                try {
                    this.partner.setNotifySendReceive(Integer.parseInt(this.jTextFieldNotifySendReceive.getText()));
                    this.buttonOk.computeErrorState();
                    this.informTreeModelNodeChanged();
                } catch (NumberFormatException e) {
                    // nop
                }
            }
        }
    }// GEN-LAST:event_jTextFieldNotifySendReceiveKeyReleased

    private void jCheckBoxNotifySendActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxNotifySendActionPerformed
        if (this.partner != null) {
            this.partner.setNotifySendEnabled(this.jCheckBoxNotifySend.isSelected());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
            this.setButtonState();
        }
    }// GEN-LAST:event_jCheckBoxNotifySendActionPerformed

    private void jCheckBoxNotifyReceiveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxNotifyReceiveActionPerformed
        if (this.partner != null) {
            this.partner.setNotifyReceiveEnabled(this.jCheckBoxNotifyReceive.isSelected());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
            this.setButtonState();
        }
    }// GEN-LAST:event_jCheckBoxNotifyReceiveActionPerformed

    private void jCheckBoxNotifySendReceiveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxNotifySendReceiveActionPerformed
        if (this.partner != null) {
            this.partner.setNotifySendReceiveEnabled(this.jCheckBoxNotifySendReceive.isSelected());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
            this.setButtonState();
        }
    }// GEN-LAST:event_jCheckBoxNotifySendReceiveActionPerformed

    private void jComboBoxContentTransferEncodingActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxContentTransferEncodingActionPerformed
        if (this.partner != null) {
            int newTransferEncoding = -1;
            if (this.jComboBoxContentTransferEncoding.getSelectedItem().equals(STR_CONTENT_TRANSFER_ENCODING_BINARY)) {
                newTransferEncoding = AS2Message.CONTENT_TRANSFER_ENCODING_BINARY;
            } else {
                newTransferEncoding = AS2Message.CONTENT_TRANSFER_ENCODING_BASE64;
            }
            if (this.partner.getContentTransferEncoding() != newTransferEncoding) {
                this.partner.setContentTransferEncoding(newTransferEncoding);
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            }
        }
    }// GEN-LAST:event_jComboBoxContentTransferEncodingActionPerformed

    private void jButtonHttpHeaderAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonHttpHeaderAddActionPerformed
        PartnerHttpHeader header = new PartnerHttpHeader();
        header.setKey("");
        header.setValue("");
        ((TableModelHttpHeader) this.jTableHttpHeader.getModel()).addRow(header);
    }// GEN-LAST:event_jButtonHttpHeaderAddActionPerformed

    private void jButtonHttpHeaderRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonHttpHeaderRemoveActionPerformed
        int selectedRow = this.jTableHttpHeader.getSelectedRow();
        ((TableModelHttpHeader) this.jTableHttpHeader.getModel()).deleteRow(selectedRow);
        if (selectedRow > this.jTableHttpHeader.getRowCount() - 1) {
            selectedRow = this.jTableHttpHeader.getRowCount() - 1;
        }
        this.jTableHttpHeader.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
    }// GEN-LAST:event_jButtonHttpHeaderRemoveActionPerformed

    private void jComboBoxHTTPProtocolVersionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxHTTPProtocolVersionActionPerformed
        if (this.partner != null) {
            this.partner.setHttpProtocolVersion((String) this.jComboBoxHTTPProtocolVersion.getSelectedItem());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_jComboBoxHTTPProtocolVersionActionPerformed

    private void jTextFieldPollMaxFilesKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextFieldPollMaxFilesKeyReleased
        if (this.partner != null) {
            try {
                int maxPollFiles = Integer.parseInt(this.jTextFieldPollMaxFiles.getText().trim());
                this.partner.setMaxPollFiles(maxPollFiles);
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            } catch (Exception e) {
                // nop
            }
        }
    }// GEN-LAST:event_jTextFieldPollMaxFilesKeyReleased

    private void jButtonTestConnectionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonTestConnectionActionPerformed
        this.testConnection();
    }// GEN-LAST:event_jButtonTestConnectionActionPerformed

    private void jButtonAddEventOnReceiptActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAddEventOnReceiptActionPerformed
        this.createProcess(PartnerEventInformation.TYPE_ON_RECEIPT);
    }// GEN-LAST:event_jButtonAddEventOnReceiptActionPerformed

    private void jButtonEditEventOnReceiptActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonEditEventOnReceiptActionPerformed
        this.editEvent(PartnerEventInformation.TYPE_ON_RECEIPT,
                this.partner.getPartnerEvents().getProcess(PartnerEventInformation.TYPE_ON_RECEIPT));
    }// GEN-LAST:event_jButtonEditEventOnReceiptActionPerformed

    private void jButtonEditEventOnSendErrorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonEditEventOnSendErrorActionPerformed
        this.editEvent(PartnerEventInformation.TYPE_ON_SENDERROR,
                this.partner.getPartnerEvents().getProcess(PartnerEventInformation.TYPE_ON_SENDERROR));
    }// GEN-LAST:event_jButtonEditEventOnSendErrorActionPerformed

    private void jButtonAddEventOnSendErrorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAddEventOnSendErrorActionPerformed
        this.createProcess(PartnerEventInformation.TYPE_ON_SENDERROR);
    }// GEN-LAST:event_jButtonAddEventOnSendErrorActionPerformed

    private void jButtonEditEventOnSendSuccessActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonEditEventOnSendSuccessActionPerformed
        this.editEvent(PartnerEventInformation.TYPE_ON_SENDSUCCESS,
                this.partner.getPartnerEvents().getProcess(PartnerEventInformation.TYPE_ON_SENDSUCCESS));
    }// GEN-LAST:event_jButtonEditEventOnSendSuccessActionPerformed

    private void jButtonAddEventOnSendSuccessActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAddEventOnSendSuccessActionPerformed
        this.createProcess(PartnerEventInformation.TYPE_ON_SENDSUCCESS);
    }// GEN-LAST:event_jButtonAddEventOnSendSuccessActionPerformed

    private void jRadioButtonHttpAuthCredentialsMessageItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_jRadioButtonHttpAuthCredentialsMessageItemStateChanged
        if (this.partner != null) {

            if (this.jRadioButtonHttpAuthNoneMessage.isSelected()) {
                this.partner.getAuthenticationCredentialsMessage().setAuthMode(HTTPAuthentication.AUTH_MODE_NONE);
            } else if (this.jRadioButtonHttpAuthCredentialsMessage.isSelected()) {
                this.partner.getAuthenticationCredentialsMessage().setAuthMode(HTTPAuthentication.AUTH_MODE_BASIC);
            } else if (this.jRadioButtonHttpAuthUserPreferenceMessage.isSelected()) {
                this.partner.getAuthenticationCredentialsMessage()
                        .setAuthMode(HTTPAuthentication.AUTH_MODE_USER_PREFERENCE);
            } else if (this.jRadioButtonHttpAuthCertificateMessage.isSelected()) {
                this.partner.getAuthenticationCredentialsMessage()
                        .setAuthMode(HTTPAuthentication.AUTH_MODE_CERTIFICATE);
            }
            this.informTreeModelNodeChanged();
        }
        this.updateHttpAuthState();
    }// GEN-LAST:event_jRadioButtonHttpAuthCredentialsMessageItemStateChanged

    private void jRadioButtonHttpAuthCredentialsMDNItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_jRadioButtonHttpAuthCredentialsMDNItemStateChanged
        if (this.partner != null) {
         
            if (this.jRadioButtonHttpAuthNoneMDN.isSelected()) {
                this.partner.getAuthenticationCredentialsAsyncMDN().setAuthMode(HTTPAuthentication.AUTH_MODE_NONE);
            } else if (this.jRadioButtonHttpAuthCredentialsMDN.isSelected()) {
                this.partner.getAuthenticationCredentialsAsyncMDN().setAuthMode(HTTPAuthentication.AUTH_MODE_BASIC);
            } else if (this.jRadioButtonHttpAuthUserPreferenceMDN.isSelected()) {
                this.partner.getAuthenticationCredentialsAsyncMDN()
                        .setAuthMode(HTTPAuthentication.AUTH_MODE_USER_PREFERENCE);
            } else if (this.jRadioButtonHttpAuthCertificateMDN.isSelected()) {
                this.partner.getAuthenticationCredentialsAsyncMDN()
                        .setAuthMode(HTTPAuthentication.AUTH_MODE_CERTIFICATE);
            }
            this.informTreeModelNodeChanged();
        }
        this.updateHttpAuthState();
    }// GEN-LAST:event_jRadioButtonHttpAuthCredentialsMDNItemStateChanged

    private void jRadioButtonHttpAuthNoneMessageItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_jRadioButtonHttpAuthNoneMessageItemStateChanged
        if (this.partner != null && this.jRadioButtonHttpAuthNoneMessage.isSelected()) {
         
            this.partner.getAuthenticationCredentialsMessage().setAuthMode(HTTPAuthentication.AUTH_MODE_NONE);
            this.informTreeModelNodeChanged();
        }
        this.updateHttpAuthState();
    }// GEN-LAST:event_jRadioButtonHttpAuthNoneMessageItemStateChanged

    private void jComboBoxOverwriteLocalStationCryptKeyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxOverwriteLocalStationCryptKeyActionPerformed
        if (this.partner != null && this.jComboBoxOverwriteLocalStationCryptKey.getSelectedItem() != null) {
            KeystoreCertificate certificate = (KeystoreCertificate) this.jComboBoxOverwriteLocalStationCryptKey
                    .getSelectedItem();
            PartnerCertificateInformation cryptInfo = new PartnerCertificateInformation(
                    certificate.getFingerPrintSHA1(),
                    PartnerCertificateInformation.CATEGORY_CRYPT_OVERWRITE_LOCALSTATION);
            partner.setCertificateInformation(cryptInfo);
            this.informTreeModelNodeChanged();
        }
        this.setButtonState();
    }// GEN-LAST:event_jComboBoxOverwriteLocalStationCryptKeyActionPerformed

    private void jComboBoxHttpAuthCertMessageActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxHttpAuthCertMessageActionPerformed
        if (this.partner != null && this.jComboBoxHttpAuthCertMessage.getSelectedItem() != null) {
            KeystoreCertificate certificate = (KeystoreCertificate) this.jComboBoxHttpAuthCertMessage.getSelectedItem();
            String fingerprint = certificate.getFingerPrintSHA1();
            this.partner.getAuthenticationCredentialsMessage()
                    .setCertificateFingerprint(fingerprint);
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        } else {
        }
    }// GEN-LAST:event_jComboBoxHttpAuthCertMessageActionPerformed

    private void jComboBoxHttpAuthCertMDNActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxHttpAuthCertMDNActionPerformed
        if (this.partner != null && this.jComboBoxHttpAuthCertMDN.getSelectedItem() != null) {
            KeystoreCertificate certificate = (KeystoreCertificate) this.jComboBoxHttpAuthCertMDN.getSelectedItem();
            String fingerprint = certificate.getFingerPrintSHA1();
            this.partner.getAuthenticationCredentialsAsyncMDN()
                    .setCertificateFingerprint(fingerprint);
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        } else {
        }
    }// GEN-LAST:event_jComboBoxHttpAuthCertMDNActionPerformed

    private void jComboBoxOverwriteLocalstationSignKeyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxOverwriteLocalstationSignKeyActionPerformed
        if (this.partner != null && this.jComboBoxOverwriteLocalstationSignKey.getSelectedItem() != null) {
            KeystoreCertificate certificate = (KeystoreCertificate) this.jComboBoxOverwriteLocalstationSignKey
                    .getSelectedItem();
            PartnerCertificateInformation cryptInfo = new PartnerCertificateInformation(
                    certificate.getFingerPrintSHA1(),
                    PartnerCertificateInformation.CATEGORY_SIGN_OVERWRITE_LOCALSTATION);
            partner.setCertificateInformation(cryptInfo);
            this.informTreeModelNodeChanged();
        }
        this.setButtonState();
    }// GEN-LAST:event_jComboBoxOverwriteLocalstationSignKeyActionPerformed

    private void jRadioButtonKeepLocalstationSecurityActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButtonKeepLocalstationSecurityActionPerformed
        if (this.partner != null) {
            this.partner.setOverwriteLocalStationSecurity(false);
            this.informTreeModelNodeChanged();
        }
        this.setButtonState();
    }// GEN-LAST:event_jRadioButtonKeepLocalstationSecurityActionPerformed

    // Inbound Auth event handlers
    private void switchInboundAuthBasicActionPerformed(java.awt.event.ActionEvent evt) {
        updateInboundAuthButtonStates();
    }

    private void switchInboundAuthCertActionPerformed(java.awt.event.ActionEvent evt) {
        updateInboundAuthButtonStates();
    }

    private void jButtonAddInboundAuthBasicActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.partner == null || !this.partner.isLocalStation()) {
            return;
        }

        PartnerInboundAuthCredential credential = new PartnerInboundAuthCredential();
        credential.setAuthType(PartnerInboundAuthCredential.AUTH_TYPE_BASIC);
        // Empty credentials - user will fill in username and password
        credential.setUsername("");
        credential.setPassword("");

        modelInboundAuthBasic.addRow(credential);

        // Select the newly added row (last row)
        int newRowIndex = jTableInboundAuthBasic.getRowCount() - 1;
        jTableInboundAuthBasic.setRowSelectionInterval(newRowIndex, newRowIndex);
        jTableInboundAuthBasic.scrollRectToVisible(jTableInboundAuthBasic.getCellRect(newRowIndex, 0, true));

        this.buttonOk.computeErrorState();
        this.informTreeModelNodeChanged();
    }

    private void jButtonDeleteInboundAuthBasicActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTableInboundAuthBasic.getSelectedRow();
        if (selectedRow >= 0) {
            modelInboundAuthBasic.deleteRow(selectedRow);
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }

    private void jButtonAddInboundAuthCertActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.partner == null || !this.partner.isLocalStation()) {
            return;
        }

        // Show certificate selection dialog - use Sign/Crypt certificates only
        JDialogSelectCertificate dialog = new JDialogSelectCertificate(
                SwingUtilities.getWindowAncestor(this),
                this.certificateManagerEncSign,
                rb.getResourceString("inboundauth.cert.select.title"),
                rb.getResourceString("inboundauth.cert.select.message"));
        dialog.setVisible(true);

        KeystoreCertificate selectedCert = dialog.getSelectedCertificate();
        if (selectedCert != null) {
            PartnerInboundAuthCredential credential = new PartnerInboundAuthCredential();
            credential.setAuthType(PartnerInboundAuthCredential.AUTH_TYPE_CERTIFICATE);
            credential.setCertFingerprint(selectedCert.getFingerPrintSHA1());
            credential.setCertAlias(selectedCert.getAlias());

            modelInboundAuthCert.addRow(credential);
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }

    private void jButtonDeleteInboundAuthCertActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTableInboundAuthCert.getSelectedRow();
        if (selectedRow >= 0) {
            modelInboundAuthCert.deleteRow(selectedRow);
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
    }

    private void updateInboundAuthButtonStates() {
        boolean basicEnabled = switchInboundAuthBasic.isSelected();
        boolean certEnabled = switchInboundAuthCert.isSelected();

        jButtonAddInboundAuthBasic.setEnabled(basicEnabled);
        jButtonDeleteInboundAuthBasic.setEnabled(basicEnabled);
        jTableInboundAuthBasic.setEnabled(basicEnabled);
        jScrollPaneInboundAuthBasic.setEnabled(basicEnabled);

        jButtonAddInboundAuthCert.setEnabled(certEnabled);
        jButtonDeleteInboundAuthCert.setEnabled(certEnabled);
        jTableInboundAuthCert.setEnabled(certEnabled);
        jScrollPaneInboundAuthCert.setEnabled(certEnabled);
    }

    /**
     * Force synchronization of inbound auth credentials from tables to partner
     * object.
     * Call this before saving the partner to ensure all table changes are
     * persisted.
     */
    public void syncInboundAuthCredentials() {
        if (this.partner != null && this.partner.isLocalStation()) {
            // Save the toggle switch states (whether each auth type is enabled)
            boolean basicEnabled = switchInboundAuthBasic.isSelected();
            boolean certEnabled = switchInboundAuthCert.isSelected();
            this.partner.setInboundAuthBasicEnabled(basicEnabled);
            this.partner.setInboundAuthCertEnabled(certEnabled);

            // Collect all credentials from both tables (regardless of toggle state)
            // The toggles control whether the auth is USED, but we still save the
            // credentials
            List<PartnerInboundAuthCredential> allCredentials = new ArrayList<>();

            // Collect basic auth credentials from table
            synchronized (modelInboundAuthBasic) {
                int basicRowCount = modelInboundAuthBasic.getRowCount();
                for (int i = 0; i < basicRowCount; i++) {
                    PartnerInboundAuthCredential cred = modelInboundAuthBasic.getRow(i);
                    if (cred != null && !cred.isEmpty()) {
                        allCredentials.add(cred);
                    }
                }
            }

            // Collect certificate credentials from table
            synchronized (modelInboundAuthCert) {
                int certRowCount = modelInboundAuthCert.getRowCount();
                for (int i = 0; i < certRowCount; i++) {
                    PartnerInboundAuthCredential cred = modelInboundAuthCert.getRow(i);
                    if (cred != null && !cred.isEmpty()) {
                        allCredentials.add(cred);
                    }
                }
            }

            // Update partner with all credentials
            this.partner.setInboundAuthCredentialsList(allCredentials);
        }
    }

    private void jRadioButtonOverwriteLocalstationSecurityActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButtonOverwriteLocalstationSecurityActionPerformed
        if (this.partner != null) {
            this.partner.setOverwriteLocalStationSecurity(true);
            this.informTreeModelNodeChanged();
        }
        this.setButtonState();
    }// GEN-LAST:event_jRadioButtonOverwriteLocalstationSecurityActionPerformed

    private void switchCompressActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_switchCompressActionPerformed
        if (this.partner != null) {
            this.partner.setCompressionType(
                    this.switchCompress.isSelected() ? AS2Message.COMPRESSION_ZLIB : AS2Message.COMPRESSION_NONE);
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_switchCompressActionPerformed

    /**
     * Refresh certificate dropdowns based on whether this is a local station or
     * remote partner.
     * Local stations need private keys (key pairs) for signing/decryption.
     * Remote partners only need public certificates for verification/encryption.
     */
    private void refreshCertificateDropdowns() {
        if (this.partner == null) {
            return;
        }

        boolean isLocalStation = this.partner.isLocalStation();

        // Remember current selections
        KeystoreCertificate currentSignCert = (KeystoreCertificate) this.jComboBoxSignCert.getSelectedItem();
        KeystoreCertificate currentCryptCert = (KeystoreCertificate) this.jComboBoxCryptCert.getSelectedItem();

        // Temporarily disable action listeners to prevent unwanted certificate changes
        ActionListener[] signListeners = this.jComboBoxSignCert.getActionListeners();
        ActionListener[] cryptListeners = this.jComboBoxCryptCert.getActionListeners();
        for (ActionListener listener : signListeners) {
            this.jComboBoxSignCert.removeActionListener(listener);
        }
        for (ActionListener listener : cryptListeners) {
            this.jComboBoxCryptCert.removeActionListener(listener);
        }

        // Clear the dropdowns
        this.jComboBoxSignCert.removeAllItems();
        this.jComboBoxCryptCert.removeAllItems();

        // Get all certificates and sort them
        List<KeystoreCertificate> encSignCertificateList = this.certificateManagerEncSign.getKeyStoreCertificateList();
        List<KeystoreCertificate> sortedList = new ArrayList<>(encSignCertificateList);
        Collections.sort(sortedList);

        // Populate dropdowns
        for (KeystoreCertificate cert : sortedList) {
            if (isLocalStation) {
                // Local Station: only add key pairs (certificates with private keys)
                if (cert.getIsKeyPair()) {
                    this.jComboBoxSignCert.addItem(cert);
                    this.jComboBoxCryptCert.addItem(cert);
                }
            } else {
                // Remote Partner: add all certificates (public keys are OK)
                this.jComboBoxSignCert.addItem(cert);
                this.jComboBoxCryptCert.addItem(cert);
            }
        }

        // Try to restore previous selections if they're still available
        // Use setUIValueWithoutEvent to avoid triggering ActionListeners
        if (currentSignCert != null) {
            for (int i = 0; i < this.jComboBoxSignCert.getItemCount(); i++) {
                KeystoreCertificate cert = this.jComboBoxSignCert.getItemAt(i);
                if (cert.getFingerPrintSHA1().equals(currentSignCert.getFingerPrintSHA1())) {
                    // Set directly without triggering events (listeners already removed)
                    this.jComboBoxSignCert.setSelectedItem(cert);
                    break;
                }
            }
        }
        if (currentCryptCert != null) {
            for (int i = 0; i < this.jComboBoxCryptCert.getItemCount(); i++) {
                KeystoreCertificate cert = this.jComboBoxCryptCert.getItemAt(i);
                if (cert.getFingerPrintSHA1().equals(currentCryptCert.getFingerPrintSHA1())) {
                    // Set directly without triggering events (listeners already removed)
                    this.jComboBoxCryptCert.setSelectedItem(cert);
                    break;
                }
            }
        }

        // Re-enable action listeners
        for (ActionListener listener : signListeners) {
            this.jComboBoxSignCert.addActionListener(listener);
        }
        for (ActionListener listener : cryptListeners) {
            this.jComboBoxCryptCert.addActionListener(listener);
        }
    }

    private void switchLocalStationItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_switchLocalStationItemStateChanged
        if (this.partner != null) {
            this.partner.setLocalStation(this.switchLocalStation.isSelected());
            if (this.partner.isLocalStation()) {
                this.tree.setToLocalStation(this.partner);
            }
            this.informTreeModelNodeChanged();
            this.setPanelVisiblilityState();
            this.handleVisibilityStateOfWidgets();
            this.disableEnableWidgets();
            this.buttonOk.computeErrorState();
            this.updatePollDirDisplay(this.partner);
            // Refresh certificate dropdowns based on local station status
            this.refreshCertificateDropdowns();
        }
        this.setButtonState();
    }// GEN-LAST:event_switchLocalStationItemStateChanged

    private void switchSignedMDNActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_switchSignedMDNActionPerformed
        if (this.partner != null) {
            this.partner.setSignedMDN(this.switchSignedMDN.isSelected());
            this.informTreeModelNodeChanged();
        }
    }// GEN-LAST:event_switchSignedMDNActionPerformed

    private void switchEnableDirPollActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_switchEnableDirPollActionPerformed
        if (this.partner != null) {
            try {
                this.partner.setEnableDirPoll(this.switchEnableDirPoll.isSelected());
                this.disableEnableWidgets();
                this.informTreeModelNodeChanged();
            } catch (Exception e) {
                UINotification.instance().addNotification(e);
            }
        }
    }// GEN-LAST:event_switchEnableDirPollActionPerformed

    private void switchKeepFilenameOnReceiptActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_switchKeepFilenameOnReceiptActionPerformed
        if (this.partner != null) {
            this.partner.setKeepOriginalFilenameOnReceipt(this.switchKeepFilenameOnReceipt.isSelected());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
        this.setButtonState();
    }// GEN-LAST:event_switchKeepFilenameOnReceiptActionPerformed

    private void switchUseEventOnReceiptActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_switchUseEventOnReceiptActionPerformed
        if (this.partner != null) {
            this.partner.getPartnerEvents().setUseOnReceipt(this.switchUseEventOnReceipt.isSelected());
            this.informTreeModelNodeChanged();
        }
        this.renderEvents();
    }// GEN-LAST:event_switchUseEventOnReceiptActionPerformed

    private void switchUseEventOnSendSuccessActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_switchUseEventOnSendSuccessActionPerformed
        if (this.partner != null) {
            this.partner.getPartnerEvents().setUseOnSendsuccess(this.switchUseEventOnSendSuccess.isSelected());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
        this.renderEvents();
    }// GEN-LAST:event_switchUseEventOnSendSuccessActionPerformed

    private void switchUseEventOnSendErrorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_switchUseEventOnSendErrorActionPerformed
        if (this.partner != null) {
            this.partner.getPartnerEvents().setUse(PartnerEventInformation.TYPE_ON_SENDERROR,
                    this.switchUseEventOnSendError.isSelected());
            this.buttonOk.computeErrorState();
            this.informTreeModelNodeChanged();
        }
        this.renderEvents();
    }// GEN-LAST:event_switchUseEventOnSendErrorActionPerformed

    private void switchUseAlgorithmIdentifierProtectionAttributeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_switchUseAlgorithmIdentifierProtectionAttributeActionPerformed
        if (this.partner != null) {
            try {
                this.partner.setUseAlgorithmIdentifierProtectionAttribute(
                        this.switchUseAlgorithmIdentifierProtectionAttribute.isSelected());
                this.buttonOk.computeErrorState();
                this.informTreeModelNodeChanged();
            } catch (Exception e) {
                // nop
            }
        }
    }// GEN-LAST:event_switchUseAlgorithmIdentifierProtectionAttributeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel HttpAuthNoneMDN;
    private javax.swing.ButtonGroup buttonGroupAuthenticationMDN;
    private javax.swing.ButtonGroup buttonGroupAuthenticationMessage;
    private javax.swing.ButtonGroup buttonGroupOverwriteLocalStationSecurity;
    private javax.swing.ButtonGroup buttonGroupSyncAsyncMDN;
    private javax.swing.JButton jButtonAddEventOnReceipt;
    private javax.swing.JButton jButtonAddEventOnSendError;
    private javax.swing.JButton jButtonAddEventOnSendSuccess;
    private javax.swing.JButton jButtonEditEventOnReceipt;
    private javax.swing.JButton jButtonEditEventOnSendError;
    private javax.swing.JButton jButtonEditEventOnSendSuccess;
    private javax.swing.JButton jButtonHttpHeaderAdd;
    private javax.swing.JButton jButtonHttpHeaderRemove;
    private javax.swing.JButton jButtonMDNURLHttp;
    private javax.swing.JButton jButtonMDNURLHttps;
    private javax.swing.JButton jButtonTestConnection;
    private javax.swing.JCheckBox jCheckBoxEdiintFeaturesCEM;
    private javax.swing.JCheckBox jCheckBoxEdiintFeaturesCompression;
    private javax.swing.JCheckBox jCheckBoxEdiintFeaturesMA;
    private javax.swing.JCheckBox jCheckBoxNotifyReceive;
    private javax.swing.JCheckBox jCheckBoxNotifySend;
    private javax.swing.JCheckBox jCheckBoxNotifySendReceive;
    private javax.swing.JComboBox<String> jComboBoxContentTransferEncoding;
    private javax.swing.JComboBox<KeystoreCertificate> jComboBoxCryptCert;
    private javax.swing.JComboBox<EncryptionDisplayImplAS2> jComboBoxEncryptionType;
    private javax.swing.JComboBox<String> jComboBoxHTTPProtocolVersion;
    private javax.swing.JComboBox<KeystoreCertificate> jComboBoxHttpAuthCertMessage;
    private javax.swing.JComboBox<KeystoreCertificate> jComboBoxHttpAuthCertMDN;
    private javax.swing.JComboBox<KeystoreCertificate> jComboBoxOverwriteLocalStationCryptKey;
    private javax.swing.JComboBox<KeystoreCertificate> jComboBoxOverwriteLocalstationSignKey;
    private javax.swing.JComboBox<KeystoreCertificate> jComboBoxSignCert;
    private javax.swing.JComboBox<SignatureDisplayImplAS2> jComboBoxSignType;
    private javax.swing.JLabel jLabelAS2Version;
    private javax.swing.JLabel jLabelContentTransferEncoding;
    private javax.swing.JLabel jLabelFeatures;
    private javax.swing.JLabel jLabelHttpAuth;
    private javax.swing.JLabel jLabelHttpAuthAsyncMDN;
    private javax.swing.JLabel jLabelHttpAuthMessage;
    private javax.swing.JLabel jLabelHttpPass;
    private javax.swing.JLabel jLabelHttpPassAsyncMDN;
    private javax.swing.JLabel jLabelIconAsyncMDN;
    private javax.swing.JLabel jLabelIconProcessTypeOnReceipt;
    private javax.swing.JLabel jLabelIconProcessTypeOnSendError;
    private javax.swing.JLabel jLabelIconProcessTypeOnSendSuccess;
    private javax.swing.JLabel jLabelIconSyncMDN;
    private javax.swing.JLabel jLabelMDNDescription;
    private javax.swing.JLabel jLabelPollDir;
    private javax.swing.JLabel jLabelPollInterval;
    private javax.swing.JLabel jLabelPollIntervalSeconds;
    private javax.swing.JLabel jLabelPollMaxFiles;
    private javax.swing.JLabel jLabelProductName;
    private javax.swing.JLabel jLabelUseEventOnReceipt;
    private javax.swing.JLabel jLabelUseEventOnSendError;
    private javax.swing.JLabel jLabelUseEventOnSendSuccess;
    private javax.swing.JPanel jPanelAsyncMDN;
    private javax.swing.JPanel jPanelDirPoll;
    private javax.swing.JPanel jPanelEvents;
    private javax.swing.JPanel jPanelEventsMain;
    private javax.swing.JPanel jPanelHTTPAuth;
    private javax.swing.JPanel jPanelHTTPAuthCredentialsMDN;
    private javax.swing.JPanel jPanelHTTPAuthCredentialsMessage;
    private javax.swing.JPanel jPanelHTTPHeader;
    private javax.swing.JPanel jPanelHttpAuthData;
    // Basic Auth Tab
    private javax.swing.JPanel jPanelInboundAuthBasic;
    private javax.swing.JLabel jLabelInboundAuthBasicEnable;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchInboundAuthBasic;
    private javax.swing.JLabel jLabelInboundAuthBasicInfo;
    private TableModelInboundAuthBasic modelInboundAuthBasic;
    private javax.swing.JTable jTableInboundAuthBasic;
    private javax.swing.JScrollPane jScrollPaneInboundAuthBasic;
    private javax.swing.JButton jButtonAddInboundAuthBasic;
    private javax.swing.JButton jButtonDeleteInboundAuthBasic;
    // Certificate Auth Tab
    private javax.swing.JPanel jPanelInboundAuthCert;
    private javax.swing.JLabel jLabelInboundAuthCertEnable;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchInboundAuthCert;
    private javax.swing.JLabel jLabelInboundAuthCertInfo;
    private TableModelInboundAuthCert modelInboundAuthCert;
    private javax.swing.JTable jTableInboundAuthCert;
    private javax.swing.JScrollPane jScrollPaneInboundAuthCert;
    private javax.swing.JButton jButtonAddInboundAuthCert;
    private javax.swing.JButton jButtonDeleteInboundAuthCert;
    private javax.swing.JPanel jPanelMDN;
    private javax.swing.JPanel jPanelMDNMain;
    private javax.swing.JPanel jPanelMDNURLButtons;
    private javax.swing.JPanel jPanelMisc;
    private javax.swing.JPanel jPanelMiscMain;
    private javax.swing.JPanel jPanelNotification;
    private javax.swing.JPanel jPanelNotificationMain;
    private javax.swing.JPanel jPanelOverwriteLocalStationSecurity;
    private javax.swing.JPanel jPanelPartnerSystem;
    private javax.swing.JPanel jPanelPartnerSystemMain;
    private javax.swing.JPanel jPanelPollOptions;
    private javax.swing.JPanel jPanelPostProcessingReceiptSuccess;
    private javax.swing.JPanel jPanelPostprocessingReceivedFailure;
    private javax.swing.JPanel jPanelPostprocessingSend;
    private javax.swing.JPanel jPanelReceipt;
    private javax.swing.JPanel jPanelReceiptOptions;
    private javax.swing.JPanel jPanelSecurity;
    private javax.swing.JPanel jPanelSecurityMain;
    private javax.swing.JPanel jPanelSend;
    private javax.swing.JPanel jPanelSendMain;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace111;
    private javax.swing.JPanel jPanelSpace123;
    private javax.swing.JPanel jPanelSpace126;
    private javax.swing.JPanel jPanelSpace127;
    private javax.swing.JPanel jPanelSpace128;
    private javax.swing.JPanel jPanelSpace129;
    private javax.swing.JPanel jPanelSpace14;
    private javax.swing.JPanel jPanelSpace199;
    private javax.swing.JPanel jPanelSpace2;
    private javax.swing.JPanel jPanelSpace23;
    private javax.swing.JPanel jPanelSpace34333;
    private javax.swing.JPanel jPanelSpace345;
    private javax.swing.JPanel jPanelSpace42;
    private javax.swing.JPanel jPanelSpace43;
    private javax.swing.JPanel jPanelSpace44;
    private javax.swing.JPanel jPanelSpace45;
    private javax.swing.JPanel jPanelSpace455;
    private javax.swing.JPanel jPanelSpace456;
    private javax.swing.JPanel jPanelSpace485;
    private javax.swing.JPanel jPanelSpace585;
    private javax.swing.JPanel jPanelSpace77646;
    private javax.swing.JPanel jPanelSpace874;
    private javax.swing.JPanel jPanelSpace875;
    private javax.swing.JPanel jPanelSpace89582;
    private javax.swing.JPanel jPanelSpace99;
    private javax.swing.JPanel jPanelSpaceSecurity;
    private javax.swing.JPanel jPanelSpaceSpace;
    private javax.swing.JPanel jPanelSpaceX;
    private javax.swing.JPanel jPanelSpaceX2;
    private javax.swing.JPanel jPanelSpacer12;
    private javax.swing.JPanel jPanelSpacer13;
    private javax.swing.JPanel jPanelSpacer14;
    private javax.swing.JPanel jPanelSpacherMDN;
    private javax.swing.JPanel jPanelSyncMDN;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpAsyncMDN;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpAuthCredentialsMDN;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpAuthCredentialsMessage;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelAS2Id;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelAddress;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelComment;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelCompress;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelContact;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelContentType;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelCryptAlias;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelEMail;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelEnableDirPoll;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelEncryptionType;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelKeepFilenameOnReceipt;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelMDNURL;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelName;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelName1;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelOverwriteCryptAliasLocalStation;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelOverwriteLocalstationSignAlias;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelPollIgnoreList;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelProtocolVersion;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelSignAlias;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelSignType;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelSignedMDN;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelSubject;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelURL;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelUseAlgorithmIdentifierProtectionAttribute;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpSyncMDN;
    private javax.swing.JPasswordField jPasswordFieldHttpAuthMessagePass;
    private javax.swing.JPasswordField jPasswordFieldHttpPassAsyncMDN;
    private javax.swing.JRadioButton jRadioButtonAsyncMDN;
    private javax.swing.JRadioButton jRadioButtonHttpAuthCredentialsMDN;
    private javax.swing.JRadioButton jRadioButtonHttpAuthCredentialsMessage;
    private javax.swing.JRadioButton jRadioButtonHttpAuthCertificateMDN;
    private javax.swing.JRadioButton jRadioButtonHttpAuthCertificateMessage;
    private javax.swing.JRadioButton jRadioButtonHttpAuthNoneMDN;
    private javax.swing.JRadioButton jRadioButtonHttpAuthNoneMessage;
    private javax.swing.JRadioButton jRadioButtonHttpAuthUserPreferenceMDN;
    private javax.swing.JRadioButton jRadioButtonHttpAuthUserPreferenceMessage;
    private javax.swing.JRadioButton jRadioButtonKeepLocalstationSecurity;
    private javax.swing.JRadioButton jRadioButtonOverwriteLocalstationSecurity;
    private javax.swing.JRadioButton jRadioButtonSyncMDN;
    private javax.swing.JScrollPane jScrollPaneHttpHeader;
    private javax.swing.JScrollPane jScrollPanePartnerAddress;
    private javax.swing.JScrollPane jScrollPanePartnerComment;
    private javax.swing.JScrollPane jScrollPanePartnerContact;
    private javax.swing.JScrollPane jScrollPaneTextAreaPartnerSystemInformation;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTableHttpHeader;
    private javax.swing.JTextArea jTextAreaPartnerSystemInformation;
    private javax.swing.JTextField jTextFieldAS2Version;
    private javax.swing.JTextField jTextFieldContentType;
    private javax.swing.JTextField jTextFieldEMail;
    private javax.swing.JTextField jTextFieldEventInfoOnReceipt;
    private javax.swing.JTextField jTextFieldEventInfoOnSendError;
    private javax.swing.JTextField jTextFieldEventInfoOnSendSuccess;
    private javax.swing.JTextField jTextFieldHttpAuthAsyncMDNUser;
    private javax.swing.JTextField jTextFieldHttpAuthMessageUser;
    private javax.swing.JTextField jTextFieldId;
    private javax.swing.JTextField jTextFieldIgnorePollFilterList;
    private javax.swing.JTextField jTextFieldMDNURL;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldNotifyReceive;
    private javax.swing.JTextField jTextFieldNotifySend;
    private javax.swing.JTextField jTextFieldNotifySendReceive;
    private javax.swing.JTextField jTextFieldPollDir;
    private javax.swing.JTextField jTextFieldPollInterval;
    private javax.swing.JTextField jTextFieldPollMaxFiles;
    private javax.swing.JTextField jTextFieldProductName;
    private javax.swing.JTextField jTextFieldReceiptURL;
    private javax.swing.JTextField jTextFieldSubject;
    private javax.swing.JTextPane jTextPanePartnerAddress;
    private javax.swing.JTextPane jTextPanePartnerComment;
    private javax.swing.JTextPane jTextPanePartnerContact;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchCompress;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchEnableDirPoll;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchKeepFilenameOnReceipt;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchLocalStation;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchSignedMDN;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchUseAlgorithmIdentifierProtectionAttribute;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchUseEventOnReceipt;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchUseEventOnSendError;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchUseEventOnSendSuccess;
    // End of variables declaration//GEN-END:variables

    private final class DocumentListenerComment implements DocumentListener {

        private final JTextPane textPane;
        private final Consumer<String> consumer;

        public DocumentListenerComment(JTextPane textPane, Consumer<String> consumer) {
            this.textPane = textPane;
            this.consumer = consumer;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            this.consumer.accept(this.textPane.getText());
            informTreeModelNodeChanged();
            setButtonState();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            this.consumer.accept(this.textPane.getText());
            informTreeModelNodeChanged();
            setButtonState();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            this.consumer.accept(this.textPane.getText());
            informTreeModelNodeChanged();
            setButtonState();
        }

    }

}
