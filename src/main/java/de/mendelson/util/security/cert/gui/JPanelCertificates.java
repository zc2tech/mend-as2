package de.mendelson.util.security.cert.gui;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.AllowModificationCallback;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.modulelock.ModuleLock;
import de.mendelson.util.security.cert.CertificateInUseChecker;
import de.mendelson.util.security.cert.CertificateInUseInfo;
import de.mendelson.util.security.cert.CertificateInUseInfo.SingleCertificateInUseInfo;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeyCopyHandler;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.TableModelCertificates;
import de.mendelson.util.security.cert.clientserver.CRLVerificationRequest;
import de.mendelson.util.security.cert.clientserver.CRLVerificationResponse;
import de.mendelson.util.security.cert.clientserver.RefreshKeystoreCertificates;
import de.mendelson.util.security.crl.CRLRevocationInformation;
import de.mendelson.util.tables.JTableColumnResizer;
import de.mendelson.util.tables.PersistentTableRowSorter;
import de.mendelson.util.tables.TableCellRendererDate;
import de.mendelson.util.uinotification.UINotification;
import java.awt.Color;
import java.security.cert.CertPath;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Panel to configure the Certificates
 *
 * @author S.Heller
 * @version $Revision: 80 $
 */
public class JPanelCertificates extends JPanel implements ListSelectionListener, PopupMenuListener {

    private static final int IMAGE_HEIGTH = 18;

    private final Logger logger;
    private JButton editButton = null;
    private JButton deleteButton = null;
    private JMenuItem itemEdit = null;
    private JMenuItem itemDelete = null;
    private CertificateManager manager = null;
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificates.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    public static final ImageIcon ICON_CERTIFICATE_ROOT
            = new ImageIcon(TableModelCertificates.IMAGE_ROOT_MULTIRESOLUTION.toMinResolution(IMAGE_HEIGTH));
    public static final ImageIcon ICON_CERTIFICATE_UNTRUSTED
            = new ImageIcon(TableModelCertificates.IMAGE_UNTRUSTED_MULTIRESOLUTION.toMinResolution(IMAGE_HEIGTH));
    private final List<CertificateInUseChecker> inUseCheckerList
            = Collections.synchronizedList(new ArrayList<CertificateInUseChecker>());
    private final List<AllowModificationCallback> allowModificationCallbackList = new ArrayList<AllowModificationCallback>();
    private KeyCopyHandler keyCopyHandler = null;
    /**
     * Image size for the popup menus
     */
    private int imageSizePopup = JDialogCertificates.IMAGE_SIZE_POPUP;
    private final Color colorOk;
    private final Color colorWarning;
    /**
     * Allows to set an external label to display the trust anchor information
     * in
     */
    private JLabel jLabelTrustAnchorValueAlternate = null;
    private JLabel jLabelWarnings = null;
    private final String moduleName;

    private final GUIClient guiClient;
    private Consumer<String> clientsideOutputConsumer = null;

    /**
     * Creates new form JPanelPartnerConfig
     *
     * @param moduleName The module name of this instance. As this Panel may be
     * used in TLS and ENC/Sign environment and there is a unique key required
     * for persistent settings this String should help
     */
    public JPanelCertificates(Logger logger, ListSelectionListener additionalListener,
            GUIClient guiClient, String moduleName, Color colorOk, Color colorWarning) {
        if (moduleName == null) {
            moduleName = "";
        }
        this.colorOk = colorOk;
        this.colorWarning = colorWarning;
        this.moduleName = moduleName;
        this.logger = logger;
        this.guiClient = guiClient;
        initComponents();
        //add row sorter
        RowSorter<TableModel> sorter = new PersistentTableRowSorter<TableModel>(this.jTable.getModel(),
                this.getClass().getName() + "_" + this.moduleName);
        this.jTable.setRowHeight(TableModelCertificates.ROW_HEIGHT);
        this.jTable.setRowSorter(sorter);
        this.jTable.getTableHeader().setReorderingAllowed(false);
        this.jTable.getColumnModel().getColumn(0).setMaxWidth(TableModelCertificates.ROW_HEIGHT
                + this.jTable.getRowMargin() * 2);
        this.jTable.getColumnModel().getColumn(1).setMaxWidth(TableModelCertificates.ROW_HEIGHT
                + this.jTable.getRowMargin() * 2);
        this.jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jTable.getSelectionModel().addListSelectionListener(additionalListener);
        this.jTable.getSelectionModel().addListSelectionListener(this);
        this.jTable.setDefaultRenderer(Date.class, new TableCellRendererDate(
                DateFormat.getDateInstance(DateFormat.SHORT)));
        this.jPopupMenu.setInvoker(this.jScrollPaneTable);
        this.jPopupMenu.addPopupMenuListener(this);
        this.setMultiresolutionIcons();
    }

    /**
     * Overwrite the designers icons by multi resolution icons
     */
    private void setMultiresolutionIcons() {
        this.jMenuItemPopupDeleteEntry.setIcon(
                new ImageIcon(JDialogCertificates.IMAGE_DELETE_MULTIRESOLUTION
                        .toMinResolution(this.imageSizePopup)));
        this.jMenuItemPopupExport.setIcon(
                new ImageIcon(JDialogCertificates.IMAGE_EXPORT_MULTIRESOLUTION
                        .toMinResolution(this.imageSizePopup)));
        this.jMenuItemPopupRenameAlias.setIcon(
                new ImageIcon(JDialogCertificates.IMAGE_EDIT_MULTIRESOLUTION
                        .toMinResolution(this.imageSizePopup)));
        this.jMenuItemPopupReference.setIcon(
                new ImageIcon(JDialogCertificates.IMAGE_REFERENCE
                        .toMinResolution(this.imageSizePopup)));
        this.jMenuItemPopupKeyCopy.setIcon(
                new ImageIcon(JDialogCertificates.IMAGE_KEYCOPY
                        .toMinResolution(this.imageSizePopup)));
    }

    /**
     * Will delete all entries that are red in the current certificate list
     */
    protected void deleteAllUnusedExpiredEntries() {
        List<KeystoreCertificate> certificateList = ((TableModelCertificates) this.jTable.getModel()).getCurrentCertificateList();
        List<KeystoreCertificate> expiredList = new ArrayList<KeystoreCertificate>();
        List<KeystoreCertificate> expiredButUsedList = new ArrayList<KeystoreCertificate>();
        Date currentDate = new Date();
        for (KeystoreCertificate certificate : certificateList) {
            if (certificate.getNotAfter().before(currentDate)) {
                //this is an expired certificate but perhaps it is in use - then it should not be deleted
                synchronized (this.inUseCheckerList) {
                    if (this.inUseCheckerList.isEmpty()) {
                        expiredList.add(certificate);
                    } else {
                        for (CertificateInUseChecker checker : this.inUseCheckerList) {
                            CertificateInUseInfo info = checker.checkUsed(certificate);
                            if (info.isEmpty()) {
                                //unused expired certificate
                                expiredList.add(certificate);
                            } else {
                                //used expired certificate - has to have the same state as an unexpired certificate 
                                //for this process
                                expiredButUsedList.add(certificate);
                            }
                        }
                    }
                }
            }
        }
        if (!expiredButUsedList.isEmpty()) {
            UINotification.instance().addNotification(JDialogCertificates.IMAGE_CERTIFICATE,
                    UINotification.TYPE_WARNING,
                    rb.getResourceString("warning.deleteallexpired.expired.but.used.title"),
                    rb.getResourceString("warning.deleteallexpired.expired.but.used.text",
                            String.valueOf(expiredButUsedList.size()))
            );
        }
        if (!expiredList.isEmpty()) {
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
            int requestValue = JOptionPane.showConfirmDialog(parent,
                    rb.getResourceString("warning.deleteallexpired.text", String.valueOf(expiredList.size())),
                    rb.getResourceString("warning.deleteallexpired.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (requestValue == JOptionPane.YES_OPTION) {
                for (KeystoreCertificate certToDelete : expiredList) {
                    try {
                        this.manager.deleteKeystoreEntry(certToDelete.getAlias());
                    } catch (Throwable e) {
                        UINotification.instance().addNotification(e);
                    }
                }
                try {
                    this.refreshData();
                    UINotification.instance().addNotification(JDialogCertificates.IMAGE_CERTIFICATE,
                            UINotification.TYPE_SUCCESS,
                            rb.getResourceString("success.deleteallexpired.title"),
                            rb.getResourceString("success.deleteallexpired.text", String.valueOf(expiredList.size())));
                } catch (Throwable e) {
                    UINotification.instance().addNotification(e);
                }
            }
        } else {
            UINotification.instance().addNotification(JDialogCertificates.IMAGE_CERTIFICATE,
                    UINotification.TYPE_ERROR,
                    rb.getResourceString("warning.deleteallexpired.noneavailable.title"),
                    rb.getResourceString("warning.deleteallexpired.noneavailable.text"));
        }
    }

    /**
     * Allows to setup an external alternate label where the trust label
     * information is displayed in. Calling this will invisible the local label
     *
     * @param jLabelTrustAnchorValueAlternate
     */
    protected void setExternalDisplayComponents(JLabel jLabelTrustAnchorValueAlternate, JLabel jLabelWarnings) {
        this.jLabelTrustAnchor.setVisible(false);
        this.jLabelTrustAnchorValue.setVisible(false);
        this.jLabelTrustAnchorValueAlternate = jLabelTrustAnchorValueAlternate;
        this.jLabelWarnings = jLabelWarnings;
    }

    /**
     * Adds a callback that is called if a user tries to modify the
     * configuration A modification will be prevented if one of the callbacks
     * does not allow it
     */
    protected void addAllowModificationCallback(AllowModificationCallback callback) {
        this.allowModificationCallbackList.add(callback);
    }

    /**
     * Checks if the operation is possible because the key store is R/O and
     * displays a message if not It's also possible to set the module into a
     * mode where modifications are not allowed - this will be displayed, too
     */
    private boolean isOperationAllowed(boolean silent) {
        for (AllowModificationCallback callback : this.allowModificationCallbackList) {
            boolean modificationAllowed = callback.allowModification(silent);
            if (!modificationAllowed) {
                return (false);
            }
        }
        boolean readWrite = true;
        readWrite = readWrite && this.manager.canWrite();
        return (readWrite);
    }

    public void addKeystore(CertificateManager manager) {
        this.manager = manager;
        ((TableModelCertificates) this.jTable.getModel()).setCertificateManager(manager);
        if (this.moduleName.equals(ModuleLock.MODULE_ENCSIGN_KEYSTORE)) {
            this.jMenuItemPopupKeyCopy.setText(rb.getResourceString("button.keycopy",
                    rb.getResourceString("button.keycopy.tls")));
        } else {
            this.jMenuItemPopupKeyCopy.setText(rb.getResourceString("button.keycopy",
                    rb.getResourceString("button.keycopy.signencrypt")));
        }
        this.refreshData();
        JTableColumnResizer.adjustColumnWidthByContent(this.jTable);
        if (this.jTable.getRowCount() > 0) {
            this.jTable.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    private boolean keystoreIsReadonly() {
        return (!this.manager.canWrite());
    }

    /**
     * Adds the functionality to check if a certificate is in use and should not
     * be deleted
     *
     * @param checker
     */
    protected void addCertificateInUseChecker(CertificateInUseChecker checker) {
        synchronized (this.inUseCheckerList) {
            this.inUseCheckerList.add(checker);
            ((TableModelCertificates) this.jTable.getModel()).addCertificateInUseChecker(checker);
        }
    }

    protected void setSelectionByAlias(String selectedAlias) {
        if (selectedAlias != null) {
            for (int i = 0; i < ((TableModelCertificates) this.jTable.getModel()).getRowCount(); i++) {
                KeystoreCertificate certificate = ((TableModelCertificates) this.jTable.getModel()).getParameter(i);
                if (certificate.getAlias().equals(selectedAlias)) {
                    this.jTable.getSelectionModel().setSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    /**
     * Returns a single certificate of a row of the embedded table
     */
    protected KeystoreCertificate getSelectedCertificate() {
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow < 0) {
            return (null);
        }
        return (((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow));
    }

    /**
     * Returns the actual selected row
     */
    protected int getSelectedRow() {
        return (this.jTable.getSelectedRow());
    }

    protected void refreshData() {
        try {
            this.manager.rereadKeystoreCertificates();
        } catch (Exception e) {
            UINotification.instance().addNotification(e);
        }
        //try to keep the mark
        int selectedRow = this.jTable.getSelectedRow();
        String selectedAlias = null;
        if (selectedRow >= 0) {
            selectedAlias = (((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow)).getAlias();
        }
        List<KeystoreCertificate> managersKeystoreCertificateList = this.manager.getKeyStoreCertificateList();
        List<KeystoreCertificate> keystoreCertListWithoutCA = new ArrayList<KeystoreCertificate>();
        //do not show the CA certificates
        for (KeystoreCertificate cert : managersKeystoreCertificateList) {
            if (!cert.isCACertificate()) {
                keystoreCertListWithoutCA.add(cert);
            }
        }
        this.jLabelShowCACertificates.setText(rb.getResourceString( "display.ca.certs", 
                String.valueOf(managersKeystoreCertificateList.size()-keystoreCertListWithoutCA.size())));
        if (this.switchShowCACertificates.isSelected()) {
            //show all certificates
            ((TableModelCertificates) this.jTable.getModel()).setNewData(managersKeystoreCertificateList);
        } else {           
            ((TableModelCertificates) this.jTable.getModel()).setNewData(keystoreCertListWithoutCA);
        }
        for (int i = 0, rowCount = this.jTable.getRowCount(); i < rowCount; i++) {
            KeystoreCertificate cert = ((TableModelCertificates) this.jTable.getModel()).getParameter(i);
            if (cert.getAlias().equals(selectedAlias)) {
                this.jTable.getSelectionModel().setSelectionInterval(i, i);
                break;
            }
        }
    }

    /**
     * Lets this gui refresh the table
     */
    protected void certificateDeleted(int lastRow) {
        //last row? dec
        if (lastRow > this.jTable.getRowCount() - 1 && lastRow != 0) {
            lastRow--;
        }
        if (this.jTable.getRowCount() > 0) {
            this.jTable.getSelectionModel().setSelectionInterval(lastRow, lastRow);
        }
    }

    /**
     * Lets this gui refresh the table
     */
    protected void certificateAdded(String newAlias) {
        if (newAlias == null) {
            return;
        }
        for (int i = 0, rowCount = this.jTable.getRowCount(); i < rowCount; i++) {
            KeystoreCertificate cert = ((TableModelCertificates) this.jTable.getModel()).getParameter(i);
            if (cert.getAlias().equals(newAlias)) {
                this.jTable.getSelectionModel().setSelectionInterval(i, i);
                break;
            }
        }
    }

    /**
     * Lets this gui refresh the table: Select the new changed certicate row to
     * be selected
     */
    protected void certificateRenamedTo(String newAlias) {
        if (newAlias == null) {
            return;
        }
        for (int i = 0, rowCount = this.jTable.getRowCount(); i < rowCount; i++) {
            KeystoreCertificate cert = ((TableModelCertificates) this.jTable.getModel()).getParameter(i);
            if (cert.getAlias().equals(newAlias)) {
                this.jTable.getSelectionModel().setSelectionInterval(i, i);
                break;
            }
        }
    }

    /**
     * Allows the GUI to control the passed buttons
     */
    protected void setButtons(JButton editButton, JButton deleteButton) {
        this.editButton = editButton;
        this.deleteButton = deleteButton;
        this.setButtonState();
    }

    protected void setMenuItems(JMenuItem itemEdit, JMenuItem itemDelete) {
        this.itemEdit = itemEdit;
        this.itemDelete = itemDelete;
        this.setButtonState();
    }

    /**
     * Control the state of the panels buttons
     */
    private void setButtonState() {
        if (this.deleteButton != null) {
            this.deleteButton.setEnabled(this.jTable.getSelectedRow() >= 0);
        }
        if (this.editButton != null) {
            this.editButton.setEnabled(this.jTable.getSelectedRow() >= 0);
        }
        if (this.itemEdit != null) {
            this.itemEdit.setEnabled(this.jTable.getSelectedRow() >= 0);
        }
        if (this.itemDelete != null) {
            this.itemDelete.setEnabled(this.jTable.getSelectedRow() >= 0);
        }
    }

    /**
     * Makes this a ListSelectionListener
     */
    @Override
    public synchronized void valueChanged(ListSelectionEvent listSelectionEvent) {
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow >= 0) {
            final KeystoreCertificate certificate
                    = ((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow);
            String infoText = certificate.getInfo();
            String extensionText = certificate.getInfoExtension();
            this.jEditorPaneInfo.setText(infoText);
            this.jTextAreaInfoExtension.setText(extensionText);
            List<KeystoreCertificate> trustChain
                    = JPanelCertificates.this.computeTrustChain(certificate.getAlias());
            this.jTreeTrustChain.buildTree(trustChain);
            this.displayTrustAnchor();
            this.displayWarnings(certificate);
        }
        this.setButtonState();
    }

    /**
     * Displays warnings if a related label is set
     */
    private void displayWarnings(KeystoreCertificate certificate) {
        if (this.jLabelWarnings == null) {
            return;
        }
        this.jLabelWarnings.setText("");
        this.jLabelWarnings.setForeground(this.colorOk);
        boolean problem = false;
        for (String fingerprint : KeystoreCertificate.TEST_KEYS_FINGERPRINTS_SHA1) {
            if (fingerprint.equalsIgnoreCase(certificate.getFingerPrintSHA1())) {
                this.jLabelWarnings.setForeground(colorWarning);
                this.jLabelWarnings.setText(rb.getResourceString("warning.testkey"));
                problem = true;
                break;
            }
        }
        if (!problem) {
            try {
                certificate.getX509Certificate().checkValidity();
                if (certificate.getIsKeyPair()) {
                    this.jLabelWarnings.setText(rb.getResourceString("label.key.valid"));
                } else {
                    this.jLabelWarnings.setText(rb.getResourceString("label.cert.valid"));
                }
            } catch (Exception e) {
                //there is a problem...turn label color to red
                this.jLabelWarnings.setForeground(this.colorWarning);
                if (certificate.getIsKeyPair()) {
                    this.jLabelWarnings.setText(rb.getResourceString("label.key.invalid"));
                } else {
                    this.jLabelWarnings.setText(rb.getResourceString("label.cert.invalid"));
                }
            }
        }
    }

    /**
     * Compute the whole trust chain (e.g. for pkcs#7 export)
     */
    private List<KeystoreCertificate> computeTrustChain(String alias) {
        KeystoreCertificate certificate = this.manager.getKeystoreCertificate(alias);
        PKIXCertPathBuilderResult result = certificate.getPKIXCertPathBuilderResult(this.manager.getKeystore(),
                this.manager.getX509CertificateList());
        List<KeystoreCertificate> list = new ArrayList<KeystoreCertificate>();
        //self signed?
        if (result == null) {
            //it's a self signed certificate: return it without any CA/intermediate certs
            list.add(certificate);
        } else {
            //trusted cert
            CertPath certPath = result.getCertPath();
            for (Object cert : certPath.getCertificates()) {
                X509Certificate workingCert = (X509Certificate) cert;
                for (KeystoreCertificate availableKeystoreCert : this.manager.getKeyStoreCertificateList()) {
                    if (workingCert.equals(availableKeystoreCert.getX509Certificate())) {
                        list.add(0, availableKeystoreCert);
                    }
                }
            }
            X509Certificate anchorCertificateX509 = null;
            boolean trustChainComplete = false;
            if (list.isEmpty()) {
                anchorCertificateX509 = result.getTrustAnchor().getTrustedCert();
                KeystoreCertificate anchorKeystoreCertificate = new KeystoreCertificate();
                anchorKeystoreCertificate.setCertificate(anchorCertificateX509, null);
                list.add(anchorKeystoreCertificate);
                trustChainComplete = true;
            } else {
                anchorCertificateX509 = list.get(0).getX509Certificate();
            }
            while (!trustChainComplete) {
                KeystoreCertificate keyCertAnchor = null;
                //find out the keystore cert of the anchor
                for (KeystoreCertificate keyCert : this.manager.getKeyStoreCertificateList()) {
                    if (keyCert.getX509Certificate().equals(anchorCertificateX509)) {
                        keyCertAnchor = keyCert;
                        break;
                    }
                }
                if (keyCertAnchor != null) {
                    //check if the anchor has another anchor as intermediates certificate may have the attribute "CA:true", too
                    result = keyCertAnchor.getPKIXCertPathBuilderResult(this.manager.getKeystore(),
                            this.manager.getX509CertificateList());
                    if (result != null) {
                        anchorCertificateX509 = result.getTrustAnchor().getTrustedCert();
                        if (!keyCertAnchor.getX509Certificate().equals(anchorCertificateX509)) {
                            for (KeystoreCertificate availableKeystoreCert : this.manager.getKeyStoreCertificateList()) {
                                if (anchorCertificateX509.equals(availableKeystoreCert.getX509Certificate())) {
                                    list.add(0, availableKeystoreCert);
                                }
                            }
                        } else {
                            trustChainComplete = true;
                        }
                    } else {
                        trustChainComplete = true;
                    }
                } else {
                    trustChainComplete = true;
                }
            }
            //if a certificate is imported two times into the keystore it will occure two or more times in a row in this list and
            //this will confuse the cert path display
            // - the following code will remove the certificates if they are two times in a row in the list
            KeystoreCertificate selectedCertificate = null;
            for (KeystoreCertificate cert : list) {
                if (cert.getAlias().equals(alias) && cert.getFingerPrintSHA1().equals(list.get(list.size() - 1).getFingerPrintSHA1())) {
                    selectedCertificate = cert;
                }
            }
            KeystoreCertificate lastCheckedCert = null;
            boolean repeatLoop = true;
            while (repeatLoop && list.size() > 1) {
                int deleteIndex = -1;
                for (int i = 0; i < list.size(); i++) {
                    KeystoreCertificate singleCert = list.get(i);
                    if (lastCheckedCert == null) {
                        lastCheckedCert = singleCert;
                    } else {
                        if (lastCheckedCert.getFingerPrintSHA1().equals(singleCert.getFingerPrintSHA1())) {
                            deleteIndex = i;
                            lastCheckedCert = null;
                            break;
                        }
                        lastCheckedCert = singleCert;
                    }
                }
                if (deleteIndex != -1) {
                    list.remove(deleteIndex);
                } else {
                    repeatLoop = false;
                }
            }
            //ensure that the selectedCertificate is always the last one in the path - it might be the same cert with an other
            //alias, too after this delete algorithm
            if (selectedCertificate != null) {
                list.remove(list.size() - 1);
                list.add(selectedCertificate);
            }
        }
        return (list);
    }

    protected void displayTrustAnchor() {
        JLabel usedDisplayLabel = this.jLabelTrustAnchorValue;
        if (this.jLabelTrustAnchorValueAlternate != null) {
            usedDisplayLabel = this.jLabelTrustAnchorValueAlternate;
        }
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                KeystoreCertificate certificate = ((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow);
                if (certificate.isRootCertificate()) {
                    usedDisplayLabel.setIcon(ICON_CERTIFICATE_ROOT);
                    usedDisplayLabel.setText("Root certificate");
                } else if (certificate.isSelfSigned()) {
                    //figure out the icon used to render the cert entry in the table
                    usedDisplayLabel.setIcon(((TableModelCertificates) this.jTable.getModel())
                            .getUnmodifiedIconForCertificate(certificate, IMAGE_HEIGTH));
                    usedDisplayLabel.setText("Self signed");
                } else {
                    PKIXCertPathBuilderResult result = certificate.getPKIXCertPathBuilderResult(this.manager.getKeystore(), this.manager.getX509CertificateList());
                    if (result == null) {
                        usedDisplayLabel.setIcon(ICON_CERTIFICATE_UNTRUSTED);
                        usedDisplayLabel.setText("Untrusted");
                    } else {
                        TrustAnchor anchor = result.getTrustAnchor();
                        if (anchor == null) {
                            usedDisplayLabel.setIcon(ICON_CERTIFICATE_UNTRUSTED);
                            usedDisplayLabel.setText("Untrusted");
                        } else {
                            List<KeystoreCertificate> trustPath = this.computeTrustChain(certificate.getAlias());
                            //found a root in the cert path
                            usedDisplayLabel.setIcon(ICON_CERTIFICATE_ROOT);
                            usedDisplayLabel.setText(trustPath.get(0).getAlias());
                        }
                    }
                }
            } catch (Exception e) {
                usedDisplayLabel.setIcon(null);
                usedDisplayLabel.setText("--");
            }
        } else {
            usedDisplayLabel.setIcon(null);
            usedDisplayLabel.setText("--");
        }
    }

    protected void performDeleteParameter() {
        try {
            KeystoreCertificate certificate = ((TableModelCertificates) this.jTable.getModel()).getParameter(this.jTable.getSelectedRow());
            this.manager.deleteKeystoreEntry(certificate.getAlias());
            this.manager.saveKeystore();
            int selectedRow = this.jTable.getSelectedRow();
            this.refreshData();
            if (this.jTable.getRowCount() - 1 >= selectedRow) {
                this.jTable.getSelectionModel().setSelectionInterval(
                        selectedRow, selectedRow);
            } else if (this.jTable.getRowCount() > 0) {
                this.jTable.getSelectionModel().setSelectionInterval(
                        this.jTable.getRowCount() - 1, this.jTable.getRowCount() - 1);
            }
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
    }

    protected void renameSelectedAlias() {
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        String oldAlias = selectedCertificate.getAlias();
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        JDialogRenameEntry dialog = new JDialogRenameEntry(parent, this.manager, oldAlias);
        dialog.setVisible(true);
        String newAlias = dialog.getNewAlias();
        dialog.dispose();
        this.refreshData();
        this.certificateRenamedTo(newAlias);
    }

    protected void deleteSelectedCertificate() {
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        boolean inUse = false;
        synchronized (this.inUseCheckerList) {
            for (CertificateInUseChecker checker : this.inUseCheckerList) {
                CertificateInUseInfo singleInfo = checker.checkUsed(selectedCertificate);
                if (!singleInfo.isEmpty()) {
                    inUse = true;
                }
            }
        }
        if (inUse) {
            UINotification.instance().addNotification(JDialogCertificates.IMAGE_DELETE_MULTIRESOLUTION,
                    UINotification.TYPE_WARNING,
                    rb.getResourceString("title.cert.in.use"),
                    rb.getResourceString("cert.delete.impossible"));
            return;
        }
        //ask the user if the cert should be really deleted, all data is lost
        int requestValue = JOptionPane.showConfirmDialog(
                this, rb.getResourceString("dialog.cert.delete.message", selectedCertificate.getAlias()),
                rb.getResourceString("dialog.cert.delete.title"),
                JOptionPane.YES_NO_OPTION);
        if (requestValue != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int selectedRow = this.getSelectedRow();
            this.manager.deleteKeystoreEntry(selectedCertificate.getAlias());
            this.refreshData();
            this.certificateDeleted(selectedRow);
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
    }

    /**
     * Exports a selected certificate
     */
    protected void exportSelectedCertificate() {
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        if (selectedCertificate != null) {
            try {
                //it is possible that the selected certificate does not exist on the server so far - means it
                //is required to save the changes here and reload the keystore data from the server
                this.manager.saveKeystore();
                this.manager.loadKeystoreFromServer();
                this.refreshData();
                JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
                JDialogExportCertificate dialog = new JDialogExportCertificate(parent,
                        this.guiClient.getBaseClient(), this.manager,
                        selectedCertificate.getAlias(), this.logger);
                dialog.setVisible(true);
            } catch (Throwable e) {
                UINotification.instance().addNotification(e);
            }
        }
    }

    /**
     * Exports full data to a keystore on the server side
     */
    protected void exportKeystore() {
        try {
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
            JDialogExportKeystore dialog = new JDialogExportKeystore(parent,
                    this.guiClient.getBaseClient(),
                    this.logger, this.manager);
            dialog.setVisible(true);
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
    }

    /**
     * Exports a key to a pkcs12 keystore
     */
    protected void exportPKCS12Key() {
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        String preselectionAlias = selectedCertificate == null ? null : selectedCertificate.getAlias();
        try {
            //it is possible that the selected key does not exist on the server so far - means it
            //is required to save the changes here and reload the keystore data from the server
            this.manager.saveKeystore();
            this.manager.loadKeystoreFromServer();
            this.refreshData();
            //signal the server that there are changes in the keystore
            RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
            this.guiClient.sendAsync(signal);
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
            JDialogExportPrivateKey dialog = new JDialogExportPrivateKey(parent,
                    this.guiClient.getBaseClient(),
                    this.logger, this.manager,
                    preselectionAlias);
            dialog.setVisible(true);
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
    }

    /**
     * Displays a dialog that shows information about the usage of the selected
     * certificate
     */
    protected void displayReference() {
        List<SingleCertificateInUseInfo> infoList = new ArrayList<SingleCertificateInUseInfo>();
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        synchronized (this.inUseCheckerList) {
            for (CertificateInUseChecker checker : this.inUseCheckerList) {
                CertificateInUseInfo info = checker.checkUsed(selectedCertificate);
                if (!info.isEmpty()) {
                    infoList.addAll(info.getUsageList());
                }
            }
        }
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        JDialogCertificateReference dialog = new JDialogCertificateReference(parent, infoList, selectedCertificate);
        dialog.setVisible(true);
    }

    /**
     * Checks if a reference functionality has been attached to the certificate
     * manager. This is only the case if a CertificateInUseChecker has been
     * attached. This should be the case for enc/signature manager only as in
     * the TLS manager there is no partner assigned to a certificate/key
     */
    protected boolean isReferenceFunctionAvailable() {
        synchronized (this.inUseCheckerList) {
            return (!this.inUseCheckerList.isEmpty());
        }
    }

    public void keycopy() {
        if (this.keyCopyHandler != null) {
            try {
                int selectedRow = this.jTable.getSelectedRow();
                if (selectedRow >= 0) {
                    KeystoreCertificate selectedEntry
                            = ((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow);
                    this.keyCopyHandler.copyEntry(selectedEntry);
                }
            } catch (Throwable e) {
                UINotification.instance().addNotification(e);
            }
        }
    }

    protected void performGenericExport() {
        if (!isOperationAllowed(false)) {
            return;
        }
        KeystoreCertificate entry = this.getSelectedCertificate();
        if (entry == null) {
            return;
        }
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        JDialogExport dialog = new JDialogExport(parent);
        dialog.setVisible(true);
        int selection = dialog.getSelection();
        if (selection == JDialogExport.SELECTION_EXPORT_CERTIFICATE) {
            this.exportSelectedCertificate();
        } else if (selection == JDialogExport.SELECTION_EXPORT_KEY) {
            this.exportPKCS12Key();
        } else if (selection == JDialogExport.SELECTION_EXPORT_KEYSTORE) {
            this.exportKeystore();
        }
    }

    /**
     * Verifies the selected certificate
     */
    protected void checkRevocationLists() {
        try {
            CRLVerificationRequest request = new CRLVerificationRequest(
                    CRLVerificationRequest.PROCESS_VERIFY_ALL,
                    this.moduleName);
            CRLVerificationResponse response
                    = (CRLVerificationResponse) this.guiClient.getBaseClient().sendSync(request);
            if (response != null && response.getException() != null) {
                throw response.getException();
            }
            //If the response is not already processed on server side but returned to the client
            //and the program has set a consumer to deal with this this is passed to the consumer
            if (response != null && response.isDisplayOnClientside() && this.clientsideOutputConsumer != null) {
                StringBuilder builder = new StringBuilder();
                for (CRLRevocationInformation information : response.getInformationList()) {
                    builder.append(information.getLogLine()).append("\n");
                }
                this.clientsideOutputConsumer.accept(builder.toString());
            }
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPopupMenu = new javax.swing.JPopupMenu();
        jMenuItemPopupExport = new javax.swing.JMenuItem();
        jMenuItemPopupRenameAlias = new javax.swing.JMenuItem();
        jMenuItemPopupReference = new javax.swing.JMenuItem();
        jMenuItemPopupKeyCopy = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPopupDeleteEntry = new javax.swing.JMenuItem();
        jSplitPane = new javax.swing.JSplitPane();
        jScrollPaneTable = new javax.swing.JScrollPane();
        jTable = new de.mendelson.util.tables.JTableSortable();
        jTabbedPaneInfo = new javax.swing.JTabbedPane();
        jScrollPaneInfo = new javax.swing.JScrollPane();
        jEditorPaneInfo = new javax.swing.JEditorPane();
        jScrollPaneInfoExtension = new javax.swing.JScrollPane();
        jTextAreaInfoExtension = new javax.swing.JTextArea();
        jScrollPaneTrustchain = new javax.swing.JScrollPane();
        jTreeTrustChain = new de.mendelson.util.security.cert.gui.JTreeTrustChain();
        jLabelTrustAnchor = new javax.swing.JLabel();
        jLabelTrustAnchorValue = new javax.swing.JLabel();
        jLabelShowCACertificates = new javax.swing.JLabel();
        switchShowCACertificates = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelSpace775 = new javax.swing.JPanel();
        jPanelSpace776 = new javax.swing.JPanel();

        jMenuItemPopupExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupExport.setText(JPanelCertificates.rb.getResourceString("button.export"));
        jMenuItemPopupExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupExportActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupExport);

        jMenuItemPopupRenameAlias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupRenameAlias.setText(JPanelCertificates.rb.getResourceString("button.edit"));
        jMenuItemPopupRenameAlias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupRenameAliasActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupRenameAlias);

        jMenuItemPopupReference.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupReference.setText(JPanelCertificates.rb.getResourceString("button.reference"));
        jMenuItemPopupReference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupReferenceActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupReference);

        jMenuItemPopupKeyCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupKeyCopy.setText(JPanelCertificates.rb.getResourceString("button.keycopy"));
        jMenuItemPopupKeyCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupKeyCopyActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupKeyCopy);
        jPopupMenu.add(jSeparator1);

        jMenuItemPopupDeleteEntry.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupDeleteEntry.setText(JPanelCertificates.rb.getResourceString( "button.delete"));
        jMenuItemPopupDeleteEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupDeleteEntryActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupDeleteEntry);

        setLayout(new java.awt.GridBagLayout());

        jSplitPane.setDividerLocation(200);
        jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTable.setModel(new TableModelCertificates());
        jTable.setDoubleBuffered(true);
        jTable.setShowHorizontalLines(false);
        jTable.setShowVerticalLines(false);
        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMouseClicked(evt);
            }
        });
        jScrollPaneTable.setViewportView(jTable);

        jSplitPane.setLeftComponent(jScrollPaneTable);

        jEditorPaneInfo.setEditable(false);
        jEditorPaneInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jEditorPaneInfo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jEditorPaneInfo.setDoubleBuffered(true);
        jScrollPaneInfo.setViewportView(jEditorPaneInfo);

        jTabbedPaneInfo.addTab(JPanelCertificates.rb.getResourceString( "tab.info.basic" ), jScrollPaneInfo);

        jTextAreaInfoExtension.setEditable(false);
        jTextAreaInfoExtension.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextAreaInfoExtension.setLineWrap(true);
        jTextAreaInfoExtension.setWrapStyleWord(true);
        jTextAreaInfoExtension.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextAreaInfoExtension.setDoubleBuffered(true);
        jScrollPaneInfoExtension.setViewportView(jTextAreaInfoExtension);

        jTabbedPaneInfo.addTab(JPanelCertificates.rb.getResourceString( "tab.info.extension" ), jScrollPaneInfoExtension);

        jTreeTrustChain.setDoubleBuffered(true);
        jScrollPaneTrustchain.setViewportView(jTreeTrustChain);

        jTabbedPaneInfo.addTab(JPanelCertificates.rb.getResourceString( "tab.info.trustchain" ), jScrollPaneTrustchain);

        jSplitPane.setRightComponent(jTabbedPaneInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane, gridBagConstraints);

        jLabelTrustAnchor.setText(JPanelCertificates.rb.getResourceString( "label.trustanchor"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(jLabelTrustAnchor, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabelTrustAnchorValue, gridBagConstraints);

        jLabelShowCACertificates.setText(rb.getResourceString( "display.ca.certs", String.valueOf(0)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabelShowCACertificates, gridBagConstraints);

        switchShowCACertificates.setDisplayStatusText(true);
        switchShowCACertificates.setHorizontalTextPosition(SwingConstants.LEFT);
        switchShowCACertificates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchShowCACertificatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 35, 5, 5);
        add(switchShowCACertificates, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jPanelSpace775, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jPanelSpace776, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
    if (evt.isPopupTrigger() || evt.isMetaDown()) {
        if (this.jTable.getSelectedRowCount() == 0) {
            return;
        }
        this.jPopupMenu.show(evt.getComponent(), evt.getX(),
                evt.getY());
    }
}//GEN-LAST:event_jTableMouseClicked

private void jMenuItemPopupDeleteEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupDeleteEntryActionPerformed
    if (!this.isOperationAllowed(false)) {
        return;
    }
    this.deleteSelectedCertificate();
}//GEN-LAST:event_jMenuItemPopupDeleteEntryActionPerformed

private void jMenuItemPopupRenameAliasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupRenameAliasActionPerformed
    if (!this.isOperationAllowed(false)) {
        return;
    }
    this.renameSelectedAlias();
}//GEN-LAST:event_jMenuItemPopupRenameAliasActionPerformed

private void jMenuItemPopupExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupExportActionPerformed
    this.performGenericExport();
}//GEN-LAST:event_jMenuItemPopupExportActionPerformed

    private void jMenuItemPopupReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupReferenceActionPerformed
        if (!this.isOperationAllowed(false)) {
            return;
        }
        this.displayReference();
    }//GEN-LAST:event_jMenuItemPopupReferenceActionPerformed

    private void jMenuItemPopupKeyCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupKeyCopyActionPerformed
        this.keycopy();
    }//GEN-LAST:event_jMenuItemPopupKeyCopyActionPerformed

    private void switchShowCACertificatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchShowCACertificatesActionPerformed
        this.refreshData();
    }//GEN-LAST:event_switchShowCACertificatesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPaneInfo;
    private javax.swing.JLabel jLabelShowCACertificates;
    private javax.swing.JLabel jLabelTrustAnchor;
    private javax.swing.JLabel jLabelTrustAnchorValue;
    private javax.swing.JMenuItem jMenuItemPopupDeleteEntry;
    private javax.swing.JMenuItem jMenuItemPopupExport;
    private javax.swing.JMenuItem jMenuItemPopupKeyCopy;
    private javax.swing.JMenuItem jMenuItemPopupReference;
    private javax.swing.JMenuItem jMenuItemPopupRenameAlias;
    private javax.swing.JPanel jPanelSpace775;
    private javax.swing.JPanel jPanelSpace776;
    private javax.swing.JPopupMenu jPopupMenu;
    private javax.swing.JScrollPane jScrollPaneInfo;
    private javax.swing.JScrollPane jScrollPaneInfoExtension;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JScrollPane jScrollPaneTrustchain;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JTabbedPane jTabbedPaneInfo;
    private de.mendelson.util.tables.JTableSortable jTable;
    private javax.swing.JTextArea jTextAreaInfoExtension;
    private de.mendelson.util.security.cert.gui.JTreeTrustChain jTreeTrustChain;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchShowCACertificates;
    // End of variables declaration//GEN-END:variables

    /**
     * Makes this a popup menu listener
     */
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        if (e.getSource() == this.jPopupMenu) {
            boolean operationAllowed = this.isOperationAllowed(true);
            KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
            this.jMenuItemPopupExport.setEnabled(selectedCertificate != null);
            this.jMenuItemPopupDeleteEntry.setEnabled(operationAllowed && !this.keystoreIsReadonly());
            this.jMenuItemPopupRenameAlias.setEnabled(operationAllowed && !this.keystoreIsReadonly());
            synchronized (this.inUseCheckerList) {
                this.jMenuItemPopupReference.setEnabled(selectedCertificate != null
                        && !this.inUseCheckerList.isEmpty()
                );
            }
            this.jMenuItemPopupKeyCopy.setEnabled(this.keyCopyHandler != null);
        }
    }

    /**
     * Makes this a popup menu listener
     */
    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    /**
     * Makes this a popup menu listener
     */
    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }

    /**
     * Sets the image size for the images of every popup menu of the certificate
     * manager
     *
     * @param imageSizePopup the imageSizePopup to set
     */
    public void setImageSizePopup(int imageSizePopup) {
        this.imageSizePopup = imageSizePopup;
        this.setMultiresolutionIcons();
    }

    /**
     * Allows to copy a selected entry of the keystore manager to another
     * keystore manager of the system
     *
     * @return the keyCopyHandler
     */
    public KeyCopyHandler getKeyCopyHandler() {
        return keyCopyHandler;
    }

    /**
     * Allows to copy a selected entry of the keystore manager to another
     * keystore manager of the system
     *
     * @param keyCopyHandler the keyCopyHandler to set
     */
    public void setKeyCopyHandler(KeyCopyHandler keyCopyHandler) {
        this.keyCopyHandler = keyCopyHandler;
    }

    /**
     * Allows to set a consumer for clientside output. Some client-server
     * message come back with log information from the server for some products.
     * Once this consumer is set it will deal with the client side output -
     * product related
     */
    public void setClientsideOutputConsumer(Consumer<String> clientsideOutputConsumer) {
        this.clientsideOutputConsumer = clientsideOutputConsumer;
    }
}
