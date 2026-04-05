
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.util.security.cert.gui;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.WindowTitleUtil;
import de.mendelson.util.KeyboardShortcutUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.gui.keygeneration.JDialogGenerateKey;
import de.mendelson.util.LayoutManagerJToolbar;
import de.mendelson.util.MecFileChooser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.AllowModificationCallback;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.modulelock.LockClientInformation;
import de.mendelson.util.modulelock.ModuleLock;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateInUseChecker;
import de.mendelson.util.security.cert.KeyCopyHandler;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.security.cert.clientserver.CSRAnswerImportRequest;
import de.mendelson.util.security.cert.clientserver.CSRAnswerImportResponse;
import de.mendelson.util.security.cert.clientserver.CSRGenerationRequest;
import de.mendelson.util.security.cert.clientserver.CSRGenerationResponse;
import de.mendelson.util.security.cert.clientserver.RefreshKeystoreCertificates;
import de.mendelson.util.security.csr.CSRUtil;
import de.mendelson.util.security.csr.JDialogCSRTypeSelection;
import de.mendelson.util.security.csr.ResourceBundleCSR;
import de.mendelson.util.security.keygeneration.KeyGenerationResult;
import de.mendelson.util.security.keygeneration.KeyGenerationValues;
import de.mendelson.util.security.keygeneration.KeyGenerator;
import de.mendelson.util.uinotification.UINotification;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;
import javax.swing.UIManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Certificate manager UI
 *
 * @author S.Heller
 * @version $Revision: 137 $
 */
public class JDialogCertificates extends JDialog implements ListSelectionListener {

    public static final int IMAGE_SIZE_DIALOG = 32;
    public static final int IMAGE_SIZE_POPUP = 20;
    public static final int IMAGE_SIZE_MENUITEM = 20;
    public static final int IMAGE_SIZE_TOOLBAR = 24;
    public static final int IMAGE_SIZE_TREENODE = 18;
    public static final int IMAGE_SIZE_LIST = 18;
    public static final int IMAGE_SIZE_TABLE = 18;

    protected final static MendelsonMultiResolutionImage IMAGE_DELETE_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/delete.svg",
                    IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_DELETE_EXPIRED_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/delete_expired.svg",
                    IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_IMPORT_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/import.svg",
                    IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_EXPORT_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/export.svg",
                    IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_EDIT_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/edit.svg",
                    IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_ADD_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/add.svg",
                    IMAGE_SIZE_MENUITEM);
    protected final static MendelsonMultiResolutionImage IMAGE_CA_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/ca.svg",
                    IMAGE_SIZE_MENUITEM);
    protected final static MendelsonMultiResolutionImage IMAGE_CERTIFICATE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/certificate.svg",
                    IMAGE_SIZE_MENUITEM);
    protected final static MendelsonMultiResolutionImage IMAGE_KEY
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/key.svg",
                    IMAGE_SIZE_MENUITEM);
    protected final static MendelsonMultiResolutionImage IMAGE_REFERENCE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/reference.svg",
                    IMAGE_SIZE_MENUITEM);
    protected final static MendelsonMultiResolutionImage IMAGE_KEYCOPY
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/keycopy.svg",
                    IMAGE_SIZE_MENUITEM);
    protected final static MendelsonMultiResolutionImage IMAGE_CRL
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/crl.svg",
                    IMAGE_SIZE_MENUITEM);

    /**
     * Resource to localize the GUI
     */
    private final static MecResourceBundle rb;
    private final static MecResourceBundle rbCSR;

    static {
        //load resource bundle
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificates.class.getName());
            rbCSR = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCSR.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    private JPanelCertificates panelCertificates = null;
    private CertificateManager manager;
    private final Logger logger;
    private final GUIClient guiClient;
    private final String productName;
    private final List<AllowModificationCallback> allowModificationCallbackList = new ArrayList<AllowModificationCallback>();
    private final LockClientInformation lockKeeper;
    private final String moduleName;
    private Color colorOk = Color.green.darker().darker();
    private Color colorWarning = Color.red.darker();

    private final static boolean ALLOW_CSR_TYPE_SELECTION = true;

    /**
     * Creates new form JDialogMessageMapping
     */
    public JDialogCertificates(JFrame parent, Logger logger, GUIClient guiClient,
            String title, String productName, boolean moduleLockedByAnotherClient,
            String moduleName, LockClientInformation lockKeeper) {
        super(parent, WindowTitleUtil.buildTitle(title), true);
        this.guiClient = guiClient;
        this.logger = logger;
        this.productName = productName;
        this.lockKeeper = lockKeeper;
        this.moduleName = moduleName;
        this.initComponents();
        if (UIManager.getColor("Objects.Green") != null) {
            this.colorOk = UIManager.getColor("Objects.Green");
        } else {
            this.colorOk = ColorUtil.getBestContrastColorAroundForeground(this.jLabelWarnings.getBackground(),
                    this.colorOk);
        }
        if (UIManager.getColor("Objects.RedStatus") != null) {
            this.colorWarning = UIManager.getColor("Objects.RedStatus");
        } else {
            this.colorWarning = ColorUtil.getBestContrastColorAroundForeground(this.jLabelWarnings.getBackground(),
                    this.colorWarning);
        }
        this.jLabelWarnings.setForeground(colorWarning);
        this.jLabelWarningRO.setForeground(colorWarning);
        this.setJMenuBar(this.jMenuBar);
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.panelCertificates = new JPanelCertificates(this.logger, this, this.guiClient, moduleName,
                this.colorOk, this.colorWarning);
        this.panelCertificates.setButtons(this.jButtonEditCertificate, this.jButtonDeleteCertificate);
        this.panelCertificates.setMenuItems(this.jMenuItemFileRenameAlias, this.jMenuItemFileDelete);
        //if no certificate is in the keystore a value should be displayed that shows this..
        this.jLabelTrustAnchorValue.setText("--");
        this.panelCertificates.setExternalDisplayComponents(this.jLabelTrustAnchorValue, this.jLabelWarnings);
        this.jPanelCertificatesMain.add(this.panelCertificates);
        this.jPanelModuleLockWarning.setVisible(moduleLockedByAnotherClient);
        this.jToolBar.setLayout(new LayoutManagerJToolbar());
        if (this.moduleName.equals(ModuleLock.MODULE_ENCSIGN_KEYSTORE)) {
            this.jMenuItemFileKeyCopy.setText(rb.getResourceString("button.keycopy",
                    rb.getResourceString("button.keycopy.tls")));
        } else {
            this.jMenuItemFileKeyCopy.setText(rb.getResourceString("button.keycopy",
                    rb.getResourceString("button.keycopy.signencrypt")));
        }
        this.jMenuItemFileKeyCopy.setEnabled(false);
        //bind del key to delete certificate
        ActionListener actionListenerDEL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!isOperationAllowed(false)) {
                    return;
                }
                panelCertificates.deleteSelectedCertificate();
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        this.getRootPane().registerKeyboardAction(actionListenerDEL, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        // Setup keyboard shortcuts
        this.setupKeyboardShortcuts();
    }

    /**
     * Setup keyboard shortcuts for this dialog
     */
    private void setupKeyboardShortcuts() {
        // ESC to close, ENTER for OK button, Cmd/Ctrl+W to close
        KeyboardShortcutUtil.setupDialogKeyBindingsWithTooltips(this, this.jButtonOk, null);
    }

    /**
     * Overwrite the designers icons by multi resolution icons
     */
    private void setMultiresolutionIcons() {
        this.jButtonImport.setIcon(new ImageIcon(IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_TOOLBAR)));
        this.jButtonExport.setIcon(new ImageIcon(IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_TOOLBAR)));
        this.jMenuItemImportCertificate.setIcon(new ImageIcon(IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemImportKeyFromKeystore.setIcon(new ImageIcon(IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemExportCertificate.setIcon(new ImageIcon(IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemExportKeyPKCS12.setIcon(new ImageIcon(IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemExportKeystore.setIcon(new ImageIcon(IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jButtonDeleteCertificate.setIcon(new ImageIcon(IMAGE_DELETE_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_TOOLBAR)));
        this.jButtonEditCertificate.setIcon(new ImageIcon(IMAGE_EDIT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_TOOLBAR)));
        this.jMenuItemFileDelete.setIcon(new ImageIcon(IMAGE_DELETE_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemFileDeleteAllExpired.setIcon(new ImageIcon(IMAGE_DELETE_EXPIRED_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemFileRenameAlias.setIcon(new ImageIcon(IMAGE_EDIT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemGenerateKey.setIcon(new ImageIcon(IMAGE_ADD_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemGenerateSignRequestInitial.setIcon(new ImageIcon(IMAGE_CA_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemGenerateSignRequestRenew.setIcon(new ImageIcon(IMAGE_CA_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemImportSignRequestResponseInitial.setIcon(new ImageIcon(IMAGE_CA_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemImportSignRequestResponseRenew.setIcon(new ImageIcon(IMAGE_CA_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemFileReference.setIcon(new ImageIcon(IMAGE_REFERENCE.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemFileKeyCopy.setIcon(new ImageIcon(IMAGE_KEYCOPY.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemVerifyCertificates.setIcon(new ImageIcon(IMAGE_CRL.toMinResolution(IMAGE_SIZE_MENUITEM)));
    }

    @Override
    public void setVisible(boolean flag) {
        if (flag) {
            this.setMultiresolutionIcons();
            this.setButtonState();
        }
        super.setVisible(flag);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JMenu getMenuImport() {
        return (this.jMenuImport);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JMenu getMenuExport() {
        //ensure to set the HiDPI icons before exporting the toolbar
        this.setMultiresolutionIcons();
        return (this.jMenuExport);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JMenu getMenuTools() {
        //ensure to set the HiDPI icons before exporting the toolbar
        this.setMultiresolutionIcons();
        return (this.jMenuTools);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JPanel getMainPanel() {
        //ensure to set the HiDPI icons before exporting the toolbar
        this.setMultiresolutionIcons();
        return (this.jPanelMain);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JToolBar getToolbar() {
        //ensure to set the HiDPI icons before exporting the toolbar
        this.setMultiresolutionIcons();
        return (this.jToolBar);
    }

    /**
     * Adds a callback that is called if a user tries to modify the
     * configuration A modification will be prevented if one of the callbacks
     * does not allow it
     */
    public void addAllowModificationCallback(AllowModificationCallback callback) {
        this.allowModificationCallbackList.add(callback);
        this.panelCertificates.addAllowModificationCallback(callback);
    }

    /**
     * Set the handler to copy entries from one keystore manager to another. If
     * this is not passed the entry "key copy" in the UI will be greyed out
     *
     * @param handler
     */
    public void setKeyCopyHandler(KeyCopyHandler handler) {
        if (this.manager == null) {
            throw new IllegalArgumentException("JDialogCertificates: Please call initialize() before "
                    + "passing a key copy handler");
        }
        this.panelCertificates.setKeyCopyHandler(handler);
        this.jMenuItemFileKeyCopy.setEnabled(handler != null);
    }

    /**
     * Initializes the keystore gui
     */
    public void initialize(KeystoreStorage keystoreStorage) {
        this.manager = new CertificateManager(this.logger);
        this.setTitle(this.getTitle());
        this.manager.loadKeystoreCertificates(keystoreStorage);
        if (!this.manager.canWrite()) {
            this.jLabelWarningRO.setText(rb.getResourceString("keystore.readonly.message"));
        }
        this.panelCertificates.addKeystore(manager);
        this.setButtonState();
    }

    public void setSelectionByAlias(String selectedAlias) {
        this.panelCertificates.setSelectionByAlias(selectedAlias);
    }

    public void addCertificateInUseChecker(CertificateInUseChecker checker) {
        this.panelCertificates.addCertificateInUseChecker(checker);
    }

    /**
     * Checks if the operation is possible because the keystore is R/O and
     * displayes a message if not It's also possible to set the module into a
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

    /**
     * Imports a certificate into the keystore
     */
    private void importCertificate() {
        if (!this.isOperationAllowed(false)) {
            return;
        }
        //take the main panel as anchor because it might be integrated in another swing program
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.jPanelMain);
        MecFileChooser chooser = new MecFileChooser(
                parent,
                rb.getResourceString("filechooser.certificate.import"));
        String importFilename = chooser.browseFilename();
        if (importFilename == null) {
            return;
        }
        JDialogInfoOnExternalCertificate infoDialog
                = new JDialogInfoOnExternalCertificate(parent, Paths.get(importFilename),
                        this.manager);
        infoDialog.setVisible(true);
        while (infoDialog.importPressed()) {
            //it is possible that there are more than a single certificate in the passed file (e.g. p7b). Get the index
            int selectedCertificateIndex = infoDialog.getCertificateIndex();
            try {
                List<X509Certificate> certList;
                try (InputStream inStream = Files.newInputStream(Paths.get(importFilename))) {
                    certList = KeyStoreUtil.readCertificates(inStream,
                            BouncyCastleProvider.PROVIDER_NAME);
                }
                X509Certificate importCertificate = certList.get(selectedCertificateIndex);
                String proposedAlias = KeyStoreUtil.getProposalCertificateAliasForImport(importCertificate);
                String alias = JOptionPane.showInputDialog(this,
                        rb.getResourceString("certificate.import.alias"), proposedAlias);
                if (alias == null || alias.trim().isEmpty()) {
                    return;
                }
                KeyStoreUtil.importX509Certificate(this.manager.getKeystore(), importFilename, alias, selectedCertificateIndex,
                        BouncyCastleProvider.PROVIDER_NAME);
                this.panelCertificates.refreshData();
                this.panelCertificates.certificateAdded(alias);
                KeystoreCertificate keystoreCertificate = this.manager.getKeystoreCertificate(alias);
                String messageKey = "certificate.import.success.message";
                if (keystoreCertificate.isCACertificate()) {
                    messageKey = "certificate.ca.import.success.message";
                }
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_SUCCESS,
                        rb.getResourceString("certificate.import.success.title"),
                        rb.getResourceString(messageKey, alias));
                //multiple certificates: show the import dialog again
                if (certList.size() > 1) {
                    infoDialog.setVisible(true);
                } else {
                    break;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_ERROR,
                        rb.getResourceString("certificate.import.error.title"),
                        rb.getResourceString("certificate.import.error.message", e.getMessage()));
            }
        }
    }

    /**
     * Imports a key in PKCS12/JKS format to the keystore - with the full trust
     * chain
     */
    private void importPrivateKey() {
        if (!isOperationAllowed(false)) {
            return;
        }
        //take the main panel as anchor because it might be integrated in another swing program
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.jPanelMain);
        JDialogImportKeyFromKeystore dialog = new JDialogImportKeyFromKeystore(parent, this.logger, this.manager);
        dialog.setVisible(true);
        try {
            this.panelCertificates.refreshData();
            this.panelCertificates.certificateAdded(dialog.getNewAlias());
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
            this.logger.severe("[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
        }
    }

    private void generateCSR(boolean initial) {
        CSRUtil util = new CSRUtil();
        KeystoreCertificate selectedPrivateKey = this.panelCertificates.getSelectedCertificate();
        try {
            //save the keystore on the server - it is possible that the key does not exist so far
            //on the server and this means a key import to the server is required
            this.manager.saveKeystore();
            this.manager.loadKeystoreFromServer();
            this.panelCertificates.refreshData();
            String title = rbCSR.getResourceString("csr.title.renew");
            String storequestion = rbCSR.getResourceString("csr.message.storequestion.renew");
            String[] options = new String[]{
                rbCSR.getResourceString("csr.option.1.renew"),
                rbCSR.getResourceString("csr.option.2"),
                rbCSR.getResourceString("cancel"),};
            if (initial) {
                title = rbCSR.getResourceString("csr.title");
                storequestion = rbCSR.getResourceString("csr.message.storequestion");
                options = new String[]{
                    rbCSR.getResourceString("csr.option.1"),
                    rbCSR.getResourceString("csr.option.2"),
                    rbCSR.getResourceString("cancel"),};
            }
            int requestValue = JOptionPane.showOptionDialog(this,
                    storequestion,
                    title, JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, options[2]);
            if (requestValue == 0) {
                CSRGenerationRequest request = new CSRGenerationRequest(this.manager.getStorageUsage(),
                        selectedPrivateKey.getFingerPrintSHA1(), CSRGenerationRequest.SELECTION_PKCS10);
                CSRGenerationResponse response = (CSRGenerationResponse) this.guiClient.getBaseClient().sendSync(request);
                if (response.getException() != null) {
                    throw (response.getException());
                }
                String csrStrPEMPKCS10 = response.getCSRBase64();
                this.buyKeyAtMendelson(csrStrPEMPKCS10);
            } else if (requestValue == 1) {
                //take the main panel as anchor because it might be integrated in another swing program
                JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
                        this.jPanelMain);
                if (ALLOW_CSR_TYPE_SELECTION) {
                    JDialogCSRTypeSelection dialogCSRType = new JDialogCSRTypeSelection(parentFrame);
                    dialogCSRType.setVisible(true);
                    int selectedCSRType = dialogCSRType.getSelection();
                    if (selectedCSRType != JDialogCSRTypeSelection.SELECTION_CANCEL) {
                        String outFilename = dialogCSRType.getSelectedFilename();
                        CSRGenerationRequest request = new CSRGenerationRequest(this.manager.getStorageUsage(),
                                selectedPrivateKey.getFingerPrintSHA1(), selectedCSRType);
                        CSRGenerationResponse response = (CSRGenerationResponse) this.guiClient.getBaseClient().sendSync(request);
                        if (response.getException() != null) {
                            throw response.getException();
                        }
                        String csrStrPEM = response.getCSRBase64();
                        if (csrStrPEM != null) {
                            Path outFile = Paths.get(outFilename);
                            util.storeRequestToFile(csrStrPEM, outFile);
                        }
                        String crmfSignature = response.getCrmfSignatureBase64();
                        if (crmfSignature != null) {
                            Path outFile = Paths.get(outFilename + ".signaturerequest");
                            util.storeRequestToFile(crmfSignature, outFile);
                        }
                        String crmfEncryption = response.getCrmfEncryptionBase64();
                        if (crmfEncryption != null) {
                            Path outFile = Paths.get(outFilename + ".encryptionrequest");
                            util.storeRequestToFile(crmfEncryption, outFile);
                        }
                        String crmfTLS = response.getCrmfTLSBase64();
                        if (crmfTLS != null) {
                            Path outFile = Paths.get(outFilename + ".TLSrequest");
                            util.storeRequestToFile(crmfTLS, outFile);
                        }
                        UINotification.instance().addNotification(null,
                                UINotification.TYPE_SUCCESS,
                                rbCSR.getResourceString("csr.generation.success.title"),
                                rbCSR.getResourceString("csr.generation.success.message",
                                        Paths.get(outFilename).toAbsolutePath().toString()));
                    }
                } else {
                    MecFileChooser chooser = new MecFileChooser(parentFrame,
                            rb.getResourceString("label.selectcsrfile"));
                    String outFilename = chooser.browseFilename();
                    if (outFilename != null) {
                        CSRGenerationRequest request = new CSRGenerationRequest(this.manager.getStorageUsage(),
                                selectedPrivateKey.getFingerPrintSHA1(), CSRGenerationRequest.SELECTION_PKCS10);
                        CSRGenerationResponse response = (CSRGenerationResponse) this.guiClient.getBaseClient().sendSync(request);
                        if (response.getException() != null) {
                            throw response.getException();
                        }
                        Path outFile = Paths.get(outFilename);
                        String csrStrPEM = response.getCSRBase64();
                        util.storeRequestToFile(csrStrPEM, outFile);
                        UINotification.instance().addNotification(null,
                                UINotification.TYPE_SUCCESS,
                                rbCSR.getResourceString("csr.generation.success.title"),
                                rbCSR.getResourceString("csr.generation.success.message",
                                        outFile.toAbsolutePath().toString()));
                    }
                }
            }
        } catch (Throwable e) {
            this.logger.severe(e.getMessage());
            String errorDetails = "[" + e.getClass().getSimpleName() + "] " + e.getMessage();
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    rbCSR.getResourceString("csr.generation.failure.title"),
                    rbCSR.getResourceString("csr.generation.failure.message",
                            errorDetails));
        }
    }

    /**
     * Generates a body publisher for the java.net.http client that contains the
     * form data that should be transferred
     *
     * @param formDataMap A map that contains all form variables as key/value
     * pair
     * @return
     */
    private HttpRequest.BodyPublisher generateFormData(Map<Object, Object> formDataMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : formDataMap.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    private void buyKeyAtMendelson(String csrStr) throws Exception {
        Map<Object, Object> formDataMap = new HashMap<>();
        formDataMap.put("csrpem", csrStr);
        formDataMap.put("source", this.productName);
        HttpClient client = HttpClient.newBuilder().
                followRedirects(Redirect.ALWAYS).build();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(this.generateFormData(formDataMap))
                .uri(URI.create("http://ca.mendelson-e-c.com/csr2session.php"))
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpURLConnection.HTTP_OK
                && statusCode != HttpURLConnection.HTTP_ACCEPTED) {
            throw new Exception(rbCSR.getResourceString("ca.connection.problem", String.valueOf(statusCode)));
        }
        String sessionId = response.body();
        URI uri = new URI("http://ca.mendelson-e-c.com?area=buy&stage=checkcsr&sid=" + sessionId);
        Desktop.getDesktop().browse(uri);
    }

    private void generateKeypair() {
        KeyGenerator generator = new KeyGenerator();

        try {
            //take the main panel as anchor because it might be integrated in another swing program
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
                    this.jPanelMain);
            JDialogGenerateKey dialog = new JDialogGenerateKey(parent);
            dialog.setVisible(true);
            KeyGenerationValues values = dialog.getValues();
            if (values == null) {
                //user break
                return;
            }
            KeyGenerationResult result = generator.generateKeyPair(values);
            String alias = KeyStoreUtil.getProposalCertificateAliasForImport(result.getCertificate());
            alias = KeyStoreUtil.ensureUniqueAliasName(this.manager.getKeystore(), alias);
            this.manager.getKeystore().setKeyEntry(alias, result.getKeyPair().getPrivate(),
                    null, new X509Certificate[]{result.getCertificate()});
            this.panelCertificates.refreshData();
            this.panelCertificates.certificateAdded(alias);
        } catch (Throwable e) {
            String message = e.getClass().getName() + ": " + e.getMessage();
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    rb.getResourceString("generatekey.error.title"),
                    rb.getResourceString("generatekey.error.message", message));
            e.printStackTrace();
        }
    }

    /**
     * Saves the keystore on the server, patches the key (clones it if renew)
     * and reloads the new keystore from the server
     *
     * @param renew
     */
    private void importCSRResponseOnServer(boolean renew) {
        KeystoreCertificate selectedCert = this.panelCertificates.getSelectedCertificate();

        try {
            //take the main panel as anchor because it might be integrated in another swing program
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
                    this.jPanelMain);
            MecFileChooser chooser = new MecFileChooser(parent,
                    rbCSR.getResourceString("label.selectcsrrepsonsefile"));
            String inFilename = chooser.browseFilename();
            if (inFilename != null) {
                byte[] csrAnswer = Files.readAllBytes(Paths.get(inFilename));
                //save the keystore on the server
                this.manager.saveKeystore();
                this.manager.loadKeystoreFromServer();
                this.panelCertificates.refreshData();
                //perform the patch operation on the server
                CSRAnswerImportRequest request = new CSRAnswerImportRequest(
                        this.manager.getStorageUsage(),
                        selectedCert.getFingerPrintSHA1(),
                        renew, csrAnswer);
                CSRAnswerImportResponse response = (CSRAnswerImportResponse) this.guiClient.getBaseClient().sendSync(request);
                if (response.getException() != null) {
                    throw (response.getException());
                }
                this.manager.loadKeystoreFromServer();
                this.panelCertificates.refreshData();
                //signal the server that there are changes in the keystore
                RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
                this.guiClient.sendAsync(signal);
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_SUCCESS,
                        rbCSR.getResourceString("csrresponse.import.success.title"),
                        rbCSR.getResourceString("csrresponse.import.success.message"));
            }
        } catch (Throwable e) {
            this.logger.severe(e.getMessage());
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    rbCSR.getResourceString("csrresponse.import.failure.title"),
                    rbCSR.getResourceString("csrresponse.import.failure.message", e.getMessage()));
        }
    }

    /**
     * Saves the internal certificate manager
     *
     * @throws Throwable
     */
    public void saveCertificateManager() throws Throwable {
        if (this.manager != null) {
            this.manager.saveKeystore();
            //signal the server that there are changes in the keystore
            RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
            this.guiClient.sendAsync(signal);
        }
    }

    private void saveAndClose() {
        if (this.manager != null) {
            try {
                this.manager.saveKeystore();
                //signal the server that there are changes in the keystore
                RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
                this.guiClient.sendAsync(signal);
            } catch (Throwable e) {
                e.printStackTrace();
                UINotification.instance().addNotification(e);
                return;
            }

        }
        this.setVisible(false);
        this.dispose();
    }

    public void setOkButtonVisible(boolean visible) {
        this.jPanelButton.setVisible(visible);
    }

    /**
     * Refreshes the menus etc
     */
    private void setButtonState() {
        if (this.panelCertificates != null) {
            //disable everything?
            boolean operationAllowed = this.isOperationAllowed(true);
            this.jButtonImport.setEnabled(operationAllowed);
            this.jButtonDeleteCertificate.setEnabled(operationAllowed);
            this.jButtonEditCertificate.setEnabled(operationAllowed);
            this.jMenuItemGenerateKey.setEnabled(operationAllowed);
            this.jMenuItemImportSignRequestResponseInitial.setEnabled(operationAllowed);
            this.jMenuItemImportSignRequestResponseRenew.setEnabled(operationAllowed);
            this.jMenuItemImportCertificate.setEnabled(operationAllowed);
            this.jMenuItemImportKeyFromKeystore.setEnabled(operationAllowed);
            KeystoreCertificate selectedCert = this.panelCertificates.getSelectedCertificate();
            this.jMenuItemGenerateSignRequestInitial.setEnabled(selectedCert != null && selectedCert.getIsKeyPair()
                    && selectedCert.isSelfSigned());
            this.jMenuItemImportSignRequestResponseInitial.setEnabled(operationAllowed && selectedCert != null && selectedCert.getIsKeyPair()
                    && selectedCert.isSelfSigned());
            this.jMenuItemGenerateSignRequestRenew.setEnabled(selectedCert != null && selectedCert.getIsKeyPair()
                    && !selectedCert.isSelfSigned());
            this.jMenuItemImportSignRequestResponseRenew.setEnabled(operationAllowed && selectedCert != null && selectedCert.getIsKeyPair()
                    && !selectedCert.isSelfSigned());
            this.jMenuItemFileReference.setEnabled(selectedCert != null
                    && this.panelCertificates.isReferenceFunctionAvailable());
            this.jMenuItemFileDelete.setEnabled(operationAllowed && selectedCert != null);
            this.jMenuItemFileDeleteAllExpired.setEnabled(operationAllowed);
            this.jMenuItemFileRenameAlias.setEnabled(operationAllowed && selectedCert != null);

        }
    }

    private void performGenericImport() {
        //take the main panel as anchor because it might be integrated in another swing program
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
                this.jPanelMain);
        JDialogImport dialog = new JDialogImport(parent);
        dialog.setVisible(true);
        int selection = dialog.getSelection();
        if (selection == JDialogImport.SELECTION_IMPORT_CERTIFICATE) {
            this.importCertificate();
        } else if (selection == JDialogImport.SELECTION_IMPORT_KEY) {
            this.importPrivateKey();
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

        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemFileRenameAlias = new javax.swing.JMenuItem();
        jMenuItemFileReference = new javax.swing.JMenuItem();
        jMenuItemFileKeyCopy = new javax.swing.JMenuItem();
        jMenuItemFileDelete = new javax.swing.JMenuItem();
        jMenuItemFileDeleteAllExpired = new javax.swing.JMenuItem();
        jMenuImport = new javax.swing.JMenu();
        jMenuItemImportCertificate = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItemImportKeyFromKeystore = new javax.swing.JMenuItem();
        jMenuExport = new javax.swing.JMenu();
        jMenuItemExportCertificate = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExportKeyPKCS12 = new javax.swing.JMenuItem();
        jMenuItemExportKeystore = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemGenerateKey = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemGenerateSignRequestInitial = new javax.swing.JMenuItem();
        jMenuItemImportSignRequestResponseInitial = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemGenerateSignRequestRenew = new javax.swing.JMenuItem();
        jMenuItemImportSignRequestResponseRenew = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemVerifyCertificates = new javax.swing.JMenuItem();
        jToolBar = new javax.swing.JToolBar();
        jButtonImport = new javax.swing.JButton();
        jButtonExport = new javax.swing.JButton();
        jButtonEditCertificate = new javax.swing.JButton();
        jButtonDeleteCertificate = new javax.swing.JButton();
        jPanelMain = new javax.swing.JPanel();
        jPanelModuleLockWarning = new javax.swing.JPanel();
        jLabelModuleLockedWarning = new javax.swing.JLabel();
        jButtonModuleLockInfo = new javax.swing.JButton();
        jPanelCertificatesMain = new javax.swing.JPanel();
        jPanelButton = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jPanelStatusBar = new javax.swing.JPanel();
        jLabelTrustAnchor = new javax.swing.JLabel();
        jLabelTrustAnchorValue = new javax.swing.JLabel();
        jLabelWarnings = new javax.swing.JLabel();
        jPanelSep1 = new javax.swing.JPanel();
        jSeparator6 = new javax.swing.JSeparator();
        jPanelSep2 = new javax.swing.JPanel();
        jLabelWarningRO = new javax.swing.JLabel();
        jPanelSep3 = new javax.swing.JPanel();

        jMenuFile.setText(this.rb.getResourceString( "menu.file"));

        jMenuItemFileRenameAlias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileRenameAlias.setText(this.rb.getResourceString( "button.edit"));
        jMenuItemFileRenameAlias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileRenameAliasActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileRenameAlias);

        jMenuItemFileReference.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileReference.setText(this.rb.getResourceString( "button.reference"));
        jMenuItemFileReference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileReferenceActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileReference);

        jMenuItemFileKeyCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileKeyCopy.setText(this.rb.getResourceString( "button.keycopy"));
        jMenuItemFileKeyCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileKeyCopyActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileKeyCopy);

        jMenuItemFileDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileDelete.setText(this.rb.getResourceString( "button.delete"));
        jMenuItemFileDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileDeleteActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileDelete);

        jMenuItemFileDeleteAllExpired.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileDeleteAllExpired.setText(this.rb.getResourceString( "button.delete.all.expired"));
        jMenuItemFileDeleteAllExpired.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileDeleteAllExpiredActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileDeleteAllExpired);

        jMenuBar.add(jMenuFile);

        jMenuImport.setText(this.rb.getResourceString( "menu.import" ));

        jMenuItemImportCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportCertificate.setText(this.rb.getResourceString( "label.cert.import" ));
        jMenuItemImportCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportCertificateActionPerformed(evt);
            }
        });
        jMenuImport.add(jMenuItemImportCertificate);
        jMenuImport.add(jSeparator8);

        jMenuItemImportKeyFromKeystore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportKeyFromKeystore.setText(this.rb.getResourceString( "label.key.import" ));
        jMenuItemImportKeyFromKeystore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportKeyFromKeystoreActionPerformed(evt);
            }
        });
        jMenuImport.add(jMenuItemImportKeyFromKeystore);

        jMenuBar.add(jMenuImport);

        jMenuExport.setText(this.rb.getResourceString( "menu.export" ));

        jMenuItemExportCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemExportCertificate.setText(this.rb.getResourceString( "label.cert.export" ));
        jMenuItemExportCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportCertificateActionPerformed(evt);
            }
        });
        jMenuExport.add(jMenuItemExportCertificate);
        jMenuExport.add(jSeparator7);

        jMenuItemExportKeyPKCS12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemExportKeyPKCS12.setText(this.rb.getResourceString( "label.key.export.pkcs12" ));
        jMenuItemExportKeyPKCS12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportKeyPKCS12ActionPerformed(evt);
            }
        });
        jMenuExport.add(jMenuItemExportKeyPKCS12);

        jMenuItemExportKeystore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemExportKeystore.setText(this.rb.getResourceString( "label.keystore.export" ));
        jMenuItemExportKeystore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportKeystoreActionPerformed(evt);
            }
        });
        jMenuExport.add(jMenuItemExportKeystore);

        jMenuBar.add(jMenuExport);

        jMenuTools.setText(this.rb.getResourceString( "menu.tools"));

        jMenuItemGenerateKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemGenerateKey.setText(this.rb.getResourceString( "menu.tools.generatekey"));
        jMenuItemGenerateKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateKeyActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemGenerateKey);
        jMenuTools.add(jSeparator4);

        jMenuItemGenerateSignRequestInitial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemGenerateSignRequestInitial.setText(this.rb.getResourceString( "menu.tools.generatecsr"));
        jMenuItemGenerateSignRequestInitial.setEnabled(false);
        jMenuItemGenerateSignRequestInitial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateSignRequestInitialActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemGenerateSignRequestInitial);

        jMenuItemImportSignRequestResponseInitial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportSignRequestResponseInitial.setText(this.rb.getResourceString( "menu.tools.importcsr"));
        jMenuItemImportSignRequestResponseInitial.setEnabled(false);
        jMenuItemImportSignRequestResponseInitial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportSignRequestResponseInitialActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemImportSignRequestResponseInitial);
        jMenuTools.add(jSeparator5);

        jMenuItemGenerateSignRequestRenew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemGenerateSignRequestRenew.setText(this.rb.getResourceString( "menu.tools.generatecsr.renew"));
        jMenuItemGenerateSignRequestRenew.setEnabled(false);
        jMenuItemGenerateSignRequestRenew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateSignRequestRenewActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemGenerateSignRequestRenew);

        jMenuItemImportSignRequestResponseRenew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportSignRequestResponseRenew.setText(this.rb.getResourceString( "menu.tools.importcsr.renew"));
        jMenuItemImportSignRequestResponseRenew.setEnabled(false);
        jMenuItemImportSignRequestResponseRenew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportSignRequestResponseRenewActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemImportSignRequestResponseRenew);
        jMenuTools.add(jSeparator1);

        jMenuItemVerifyCertificates.setText(this.rb.getResourceString("menu.tools.verifyall"));
        jMenuItemVerifyCertificates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemVerifyCertificatesActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemVerifyCertificates);

        jMenuBar.add(jMenuTools);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        jButtonImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image24x24.gif"))); // NOI18N
        jButtonImport.setText(this.rb.getResourceString( "button.import"));
        jButtonImport.setFocusable(false);
        jButtonImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonImport.setMaximumSize(new java.awt.Dimension(99, 41));
        jButtonImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonImport);

        jButtonExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image24x24.gif"))); // NOI18N
        jButtonExport.setText(this.rb.getResourceString( "button.export"));
        jButtonExport.setFocusable(false);
        jButtonExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExport.setMaximumSize(new java.awt.Dimension(99, 41));
        jButtonExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonExport);

        jButtonEditCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image24x24.gif"))); // NOI18N
        jButtonEditCertificate.setText(this.rb.getResourceString( "button.edit"));
        jButtonEditCertificate.setFocusable(false);
        jButtonEditCertificate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEditCertificate.setMaximumSize(new java.awt.Dimension(99, 41));
        jButtonEditCertificate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonEditCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditCertificateActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonEditCertificate);

        jButtonDeleteCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image24x24.gif"))); // NOI18N
        jButtonDeleteCertificate.setText(this.rb.getResourceString( "button.delete"));
        jButtonDeleteCertificate.setFocusable(false);
        jButtonDeleteCertificate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDeleteCertificate.setMaximumSize(new java.awt.Dimension(99, 41));
        jButtonDeleteCertificate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDeleteCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteCertificateActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonDeleteCertificate);

        getContentPane().add(jToolBar, java.awt.BorderLayout.NORTH);

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jPanelModuleLockWarning.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 51, 0)));
        jPanelModuleLockWarning.setLayout(new java.awt.GridBagLayout());

        jLabelModuleLockedWarning.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelModuleLockedWarning.setForeground(new java.awt.Color(204, 51, 0));
        jLabelModuleLockedWarning.setText(this.rb.getResourceString( "module.locked"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelModuleLockWarning.add(jLabelModuleLockedWarning, gridBagConstraints);

        jButtonModuleLockInfo.setText("...");
        jButtonModuleLockInfo.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonModuleLockInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModuleLockInfoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelModuleLockWarning.add(jButtonModuleLockInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 9.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMain.add(jPanelModuleLockWarning, gridBagConstraints);

        jPanelCertificatesMain.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMain.add(jPanelCertificatesMain, gridBagConstraints);

        jPanelButton.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(this.rb.getResourceString( "button.ok" ));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButton.add(jButtonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanelMain.add(jPanelButton, gridBagConstraints);

        getContentPane().add(jPanelMain, java.awt.BorderLayout.CENTER);

        jPanelStatusBar.setLayout(new java.awt.GridBagLayout());

        jLabelTrustAnchor.setText(this.rb.getResourceString( "label.trustanchor"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        jPanelStatusBar.add(jLabelTrustAnchor, gridBagConstraints);

        jLabelTrustAnchorValue.setText("jLabelTrustAnchorValue");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        jPanelStatusBar.add(jLabelTrustAnchorValue, gridBagConstraints);

        jLabelWarnings.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelWarnings.setForeground(new java.awt.Color(0, 153, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        jPanelStatusBar.add(jLabelWarnings, gridBagConstraints);

        jPanelSep1.setLayout(new java.awt.GridBagLayout());

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
        jPanelSep1.add(jSeparator6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelStatusBar.add(jPanelSep1, gridBagConstraints);

        jPanelSep2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelStatusBar.add(jPanelSep2, gridBagConstraints);

        jLabelWarningRO.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelWarningRO.setForeground(new java.awt.Color(0, 153, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 5);
        jPanelStatusBar.add(jLabelWarningRO, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelStatusBar.add(jPanelSep3, gridBagConstraints);

        getContentPane().add(jPanelStatusBar, java.awt.BorderLayout.SOUTH);

        setSize(new java.awt.Dimension(1035, 777));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemExportKeyPKCS12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportKeyPKCS12ActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.exportPKCS12Key();
    }//GEN-LAST:event_jMenuItemExportKeyPKCS12ActionPerformed

    private void jMenuItemExportCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.exportSelectedCertificate();
    }//GEN-LAST:event_jMenuItemExportCertificateActionPerformed

    private void jMenuItemImportKeyFromKeystoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportKeyFromKeystoreActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importPrivateKey();
    }//GEN-LAST:event_jMenuItemImportKeyFromKeystoreActionPerformed

    private void jMenuItemImportCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importCertificate();
    }//GEN-LAST:event_jMenuItemImportCertificateActionPerformed

    private void jButtonDeleteCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.deleteSelectedCertificate();
    }//GEN-LAST:event_jButtonDeleteCertificateActionPerformed

    private void jButtonEditCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.renameSelectedAlias();
    }//GEN-LAST:event_jButtonEditCertificateActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.saveAndClose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        //this is special here: Because there are many operations in the certificate
        //manager that perform saves on the server side the close will also save it
        this.saveAndClose();
    }//GEN-LAST:event_closeDialog

    private void jMenuItemGenerateKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateKeyActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.generateKeypair();
    }//GEN-LAST:event_jMenuItemGenerateKeyActionPerformed

    private void jMenuItemGenerateSignRequestInitialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateSignRequestInitialActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.generateCSR(true);
    }//GEN-LAST:event_jMenuItemGenerateSignRequestInitialActionPerformed

    private void jMenuItemImportSignRequestResponseInitialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportSignRequestResponseInitialActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importCSRResponseOnServer(false);
        this.panelCertificates.refreshData();
        this.panelCertificates.displayTrustAnchor();
    }//GEN-LAST:event_jMenuItemImportSignRequestResponseInitialActionPerformed

    private void jMenuItemFileRenameAliasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileRenameAliasActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.renameSelectedAlias();
    }//GEN-LAST:event_jMenuItemFileRenameAliasActionPerformed

    private void jMenuItemFileDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileDeleteActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.deleteSelectedCertificate();
    }//GEN-LAST:event_jMenuItemFileDeleteActionPerformed

    private void jMenuItemGenerateSignRequestRenewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateSignRequestRenewActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.generateCSR(false);
    }//GEN-LAST:event_jMenuItemGenerateSignRequestRenewActionPerformed

    private void jMenuItemImportSignRequestResponseRenewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportSignRequestResponseRenewActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importCSRResponseOnServer(true);
        this.panelCertificates.refreshData();
        this.panelCertificates.displayTrustAnchor();
    }//GEN-LAST:event_jMenuItemImportSignRequestResponseRenewActionPerformed

    private void jButtonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImportActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.performGenericImport();
    }//GEN-LAST:event_jButtonImportActionPerformed

    private void jButtonModuleLockInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModuleLockInfoActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
                this);
        ModuleLock.displayDialogModuleLocked(parent, this.lockKeeper, this.moduleName);
    }//GEN-LAST:event_jButtonModuleLockInfoActionPerformed

    private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportActionPerformed
        this.panelCertificates.performGenericExport();
    }//GEN-LAST:event_jButtonExportActionPerformed

    private void jMenuItemFileDeleteAllExpiredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileDeleteAllExpiredActionPerformed
        this.panelCertificates.deleteAllUnusedExpiredEntries();
    }//GEN-LAST:event_jMenuItemFileDeleteAllExpiredActionPerformed

    private void jMenuItemFileReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileReferenceActionPerformed
        this.panelCertificates.displayReference();
    }//GEN-LAST:event_jMenuItemFileReferenceActionPerformed

    private void jMenuItemFileKeyCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileKeyCopyActionPerformed
        this.panelCertificates.keycopy();
    }//GEN-LAST:event_jMenuItemFileKeyCopyActionPerformed

    private void jMenuItemExportKeystoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportKeystoreActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.exportKeystore();
    }//GEN-LAST:event_jMenuItemExportKeystoreActionPerformed

    private void jMenuItemVerifyCertificatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerifyCertificatesActionPerformed
        this.panelCertificates.checkRevocationLists();
    }//GEN-LAST:event_jMenuItemVerifyCertificatesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDeleteCertificate;
    private javax.swing.JButton jButtonEditCertificate;
    private javax.swing.JButton jButtonExport;
    private javax.swing.JButton jButtonImport;
    private javax.swing.JButton jButtonModuleLockInfo;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelModuleLockedWarning;
    private javax.swing.JLabel jLabelTrustAnchor;
    private javax.swing.JLabel jLabelTrustAnchorValue;
    private javax.swing.JLabel jLabelWarningRO;
    private javax.swing.JLabel jLabelWarnings;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuExport;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuImport;
    private javax.swing.JMenuItem jMenuItemExportCertificate;
    private javax.swing.JMenuItem jMenuItemExportKeyPKCS12;
    private javax.swing.JMenuItem jMenuItemExportKeystore;
    private javax.swing.JMenuItem jMenuItemFileDelete;
    private javax.swing.JMenuItem jMenuItemFileDeleteAllExpired;
    private javax.swing.JMenuItem jMenuItemFileKeyCopy;
    private javax.swing.JMenuItem jMenuItemFileReference;
    private javax.swing.JMenuItem jMenuItemFileRenameAlias;
    private javax.swing.JMenuItem jMenuItemGenerateKey;
    private javax.swing.JMenuItem jMenuItemGenerateSignRequestInitial;
    private javax.swing.JMenuItem jMenuItemGenerateSignRequestRenew;
    private javax.swing.JMenuItem jMenuItemImportCertificate;
    private javax.swing.JMenuItem jMenuItemImportKeyFromKeystore;
    private javax.swing.JMenuItem jMenuItemImportSignRequestResponseInitial;
    private javax.swing.JMenuItem jMenuItemImportSignRequestResponseRenew;
    private javax.swing.JMenuItem jMenuItemVerifyCertificates;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelCertificatesMain;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelModuleLockWarning;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelStatusBar;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables

    /**
     * Let this class listen to the underlaying table liste events, makes it a
     * ListSelectionListener
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.setButtonState();
    }

    /**
     * Sets the image size for the images of every popup menu of the certificate
     * manager
     *
     * @param imageSizePopup the imageSizePopup to set
     */
    public void setImageSizePopup(int imageSizePopup) {
        this.panelCertificates.setImageSizePopup(imageSizePopup);
    }

    /**
     * Allows to set a consumer for clientside output. Some client-server
     * message come back with log information from the server for some products.
     * Once this consumer is set it will deal with the client side output -
     * product related
     */
    public void setClientsideOutputConsumer(Consumer clientsideOutputConsumer) {
        this.panelCertificates.setClientsideOutputConsumer(clientsideOutputConsumer);
    }
}
