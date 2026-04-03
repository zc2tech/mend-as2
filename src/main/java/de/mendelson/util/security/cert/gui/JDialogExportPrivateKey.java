//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogExportPrivateKey.java 4     11/02/25 13:40 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.WindowTitleUtil;
import de.mendelson.util.KeyboardShortcutUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.filesystemview.RemoteFileBrowser;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.ListCellRendererCertificates;
import de.mendelson.util.security.cert.clientserver.ExportRequestPrivateKey;
import de.mendelson.util.security.cert.clientserver.ExportResponsePrivateKey;
import de.mendelson.util.uinotification.UINotification;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Export a private key into a pkcs#12 keystore or a PEM encoded keyfile
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class JDialogExportPrivateKey extends JDialog {

    private final String EXPORTFORMAT_PKCS12 = ExportRequestPrivateKey.EXPORTFORMAT_PKCS12;
    private final String EXPORTFORMAT_PEM = ExportRequestPrivateKey.EXPORTFORMAT_PEM;

    /**
     * ResourceBundle to localize the GUI
     */
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleExportPrivateKey.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }
    private final CertificateManager manager;
    private final Logger logger;
    private final BaseClient baseClient;
    private final JFrame frameParent;

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param manager Manager that handles the certificates
     */
    public JDialogExportPrivateKey(JFrame frameParent, BaseClient baseClient, Logger logger, CertificateManager manager,
            String selectedAlias) throws Exception {
        super(frameParent, true);
        this.frameParent = frameParent;
        this.baseClient = baseClient;
        this.logger = logger;
        this.setTitle(rb.getResourceString("title"));
        initComponents();
        PasswordOverlay.addTo(this.jPasswordFieldPassphrase, rb.getResourceString("label.keypass.hint"));
        TextOverlay.addTo(this.jTextFieldExportFile, rb.getResourceString("label.exportdir.hint"));
        this.jLabelIcon.setIcon(new ImageIcon(
                JDialogCertificates.IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(
                        JDialogCertificates.IMAGE_SIZE_DIALOG)));
        this.manager = manager;
        ActionListener[] formatListener = this.jComboBoxExportFormat.getActionListeners();
        for (ActionListener listener : formatListener) {
            this.jComboBoxExportFormat.removeActionListener(listener);
        }
        this.jComboBoxExportFormat.removeAllItems();
        this.jComboBoxExportFormat.addItem(EXPORTFORMAT_PKCS12);
        this.jComboBoxExportFormat.addItem(EXPORTFORMAT_PEM);
        this.jComboBoxExportFormat.setSelectedItem(EXPORTFORMAT_PKCS12);
        for (ActionListener listener : formatListener) {
            this.jComboBoxExportFormat.addActionListener(listener);
        }
        this.jComboBoxKeys.setRenderer(new ListCellRendererCertificates());
        this.populateKeyList(selectedAlias);

        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.setupKeyboardShortcuts();
        this.setButtonState();
    }

    private void setupKeyboardShortcuts() {
        KeyboardShortcutUtil.setupDialogKeyBindingsWithTooltips(this, this.jButtonOk, this.jButtonCancel);
    }

    private void populateKeyList(String preselection) throws Exception {
        this.jComboBoxKeys.removeAllItems();
        List<KeystoreCertificate> keyList = new ArrayList<KeystoreCertificate>();
        KeystoreCertificate preselectedKey = null;
        for (KeystoreCertificate key : this.manager.getKeyStoreCertificateList()) {
            if (key.getIsKeyPair()) {
                keyList.add(key);
                if (preselection != null && preselection.equals(key.getAlias())) {
                    preselectedKey = key;
                }
            }
        }
        if (keyList.isEmpty()) {
            throw new Exception(rb.getResourceString("keystore.contains.nokeys"));
        } else {
            for (KeystoreCertificate key : keyList) {
                this.jComboBoxKeys.addItem(key);
            }
            if (preselectedKey != null) {
                this.jComboBoxKeys.setSelectedItem(preselectedKey);
            } else {
                this.jComboBoxKeys.setSelectedItem(0);
            }
        }
    }

    /**
     * Sets the state of the "ok" and "cancel" buttons of this GUI
     */
    private void setButtonState() {
        if (this.jComboBoxExportFormat.getSelectedItem().equals(EXPORTFORMAT_PEM)) {
            this.jLabelPassphrase.setEnabled(false);
            this.jPasswordFieldPassphrase.setEnabled(false);
            this.jPasswordFieldPassphrase.setEditable(false);
            this.jButtonOk.setEnabled(
                    !this.jTextFieldExportFile.getText().isEmpty());
        } else {
            this.jLabelPassphrase.setEnabled(true);
            this.jPasswordFieldPassphrase.setEnabled(true);
            this.jPasswordFieldPassphrase.setEditable(true);
            this.jButtonOk.setEnabled(
                    !this.jTextFieldExportFile.getText().isEmpty()
                    && this.jPasswordFieldPassphrase.getPassword().length > 0);
        }

    }

    /**
     * Finally export the key
     */
    private void performExportPrivateKey() {
        try {
            String serverSideTargetPath = this.jTextFieldExportFile.getText();
            char[] serverSideTargetPass = this.jPasswordFieldPassphrase.getPassword();
            int sourceKeystoreUsage = this.manager.getStorageUsage();
            KeystoreCertificate selectedKey = (KeystoreCertificate) this.jComboBoxKeys.getSelectedItem();
            String selectedAlias = selectedKey.getAlias();
            String selectedFingerPrintSHA1 = selectedKey.getFingerPrintSHA1();
            String saveFileOnServer = null;
            ExportRequestPrivateKey request
                    = new ExportRequestPrivateKey(
                            sourceKeystoreUsage,
                            selectedFingerPrintSHA1,
                            serverSideTargetPath,
                            serverSideTargetPass,
                            (String) this.jComboBoxExportFormat.getSelectedItem());
            ExportResponsePrivateKey response = (ExportResponsePrivateKey) this.baseClient.sendSync(request);
            if (response.getException() != null) {
                throw response.getException();
            }
            saveFileOnServer = response.getSaveFileOnServer();
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_SUCCESS,
                    rb.getResourceString("key.export.success.title"),
                    rb.getResourceString("key.exported.to.file",
                            new Object[]{
                                selectedAlias,
                                saveFileOnServer
                            })
            );
            this.logger.fine(rb.getResourceString("key.exported.to.file",
                    new Object[]{
                        selectedAlias,
                        saveFileOnServer
                    }));
        } catch (Throwable e) {
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    rb.getResourceString("key.export.error.title"),
                    rb.getResourceString("key.export.error.message", e.getMessage()));
        }
    }

    private void browseExportDirectory() {
        String existingPath = this.jTextFieldExportFile.getText();
        RemoteFileBrowser browser = new RemoteFileBrowser(this.frameParent, this.baseClient,
                rb.getResourceString("filechooser.key.export"));
        browser.setDirectoriesOnly(true);
        browser.setSelectedFile(existingPath);
        browser.setVisible(true);
        String selectedPath = browser.getSelectedPath();
        if (selectedPath != null) {
            this.jTextFieldExportFile.setText(selectedPath);
        }
        this.setButtonState();
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
        jLabelIcon = new javax.swing.JLabel();
        jTextFieldExportFile = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonBrowseExportFile = new javax.swing.JButton();
        jLabelPassphrase = new javax.swing.JLabel();
        jPasswordFieldPassphrase = new javax.swing.JPasswordField();
        jComboBoxKeys = new javax.swing.JComboBox();
        jLabelAlias = new javax.swing.JLabel();
        jPanelSpace2 = new javax.swing.JPanel();
        jPanelSpace1 = new javax.swing.JPanel();
        jPanelUIHelpLabelURL = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelSpace3 = new javax.swing.JPanel();
        jComboBoxExportFormat = new javax.swing.JComboBox<>();
        jPanelSpace4 = new javax.swing.JPanel();
        jPanelUIHelpLabelExportFormat = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 20, 10);
        jPanelEdit.add(jLabelIcon, gridBagConstraints);

        jTextFieldExportFile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldExportFileKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldExportFile, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanel3, gridBagConstraints);

        jButtonBrowseExportFile.setText("..");
        jButtonBrowseExportFile.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowseExportFile.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowseExportFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseExportFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowseExportFile, gridBagConstraints);

        jLabelPassphrase.setText(this.rb.getResourceString( "label.keypass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelPassphrase, gridBagConstraints);

        jPasswordFieldPassphrase.setMinimumSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPassphrase.setPreferredSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPassphrase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordFieldPassphraseKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jPasswordFieldPassphrase, gridBagConstraints);

        jComboBoxKeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxKeysActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 5);
        jPanelEdit.add(jComboBoxKeys, gridBagConstraints);

        jLabelAlias.setText(this.rb.getResourceString( "label.alias" ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelAlias, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanelSpace2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanelSpace1, gridBagConstraints);

        jPanelUIHelpLabelURL.setToolTipText(this.rb.getResourceString( "label.exportdir.help"));
        jPanelUIHelpLabelURL.setText(this.rb.getResourceString( "label.exportdir"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelEdit.add(jPanelUIHelpLabelURL, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanelSpace3, gridBagConstraints);

        jComboBoxExportFormat.setMinimumSize(new java.awt.Dimension(100, 24));
        jComboBoxExportFormat.setPreferredSize(new java.awt.Dimension(100, 22));
        jComboBoxExportFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxExportFormatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 5);
        jPanelEdit.add(jComboBoxExportFormat, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanelSpace4, gridBagConstraints);

        jPanelUIHelpLabelExportFormat.setToolTipText(this.rb.getResourceString( "label.exportformat.help"));
        jPanelUIHelpLabelExportFormat.setText(this.rb.getResourceString( "label.exportformat"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelEdit.add(jPanelUIHelpLabelExportFormat, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelEdit, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(this.rb.getResourceString( "button.ok" ));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonOk, gridBagConstraints);

        jButtonCancel.setText(this.rb.getResourceString( "button.cancel" ));
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);

        setSize(new java.awt.Dimension(512, 359));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jPasswordFieldPassphraseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldPassphraseKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jPasswordFieldPassphraseKeyReleased

    private void jButtonBrowseExportFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseExportFileActionPerformed
        this.browseExportDirectory();
    }//GEN-LAST:event_jButtonBrowseExportFileActionPerformed

    private void jTextFieldExportFileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldExportFileKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldExportFileKeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible(false);
        this.performExportPrivateKey();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jComboBoxKeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxKeysActionPerformed
        this.setButtonState();
    }//GEN-LAST:event_jComboBoxKeysActionPerformed

    private void jComboBoxExportFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxExportFormatActionPerformed
        this.setButtonState();
    }//GEN-LAST:event_jComboBoxExportFormatActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseExportFile;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JComboBox<String> jComboBoxExportFormat;
    private javax.swing.JComboBox jComboBoxKeys;
    private javax.swing.JLabel jLabelAlias;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelPassphrase;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSpace1;
    private javax.swing.JPanel jPanelSpace2;
    private javax.swing.JPanel jPanelSpace3;
    private javax.swing.JPanel jPanelSpace4;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelExportFormat;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelURL;
    private javax.swing.JPasswordField jPasswordFieldPassphrase;
    private javax.swing.JTextField jTextFieldExportFile;
    // End of variables declaration//GEN-END:variables
}
