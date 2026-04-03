//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogExportKeystore.java 5     11/02/25 13:40 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.WindowTitleUtil;
import de.mendelson.util.KeyboardShortcutUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.filesystemview.RemoteFileBrowser;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.security.cert.clientserver.ExportRequestKeystore;
import de.mendelson.util.security.cert.clientserver.ExportResponseKeystore;
import de.mendelson.util.uinotification.UINotification;
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
 * Export a private key into a keystore with a selectable password
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class JDialogExportKeystore extends JDialog {

    /**
     * ResourceBundle to localize the GUI
     */
    private final MecResourceBundle rb;
    private final CertificateManager manager;
    private final Logger logger;
    private final BaseClient baseClient;
    private final JFrame frameParent;

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param manager Manager that handles the certificates
     */
    public JDialogExportKeystore(JFrame frameParent, BaseClient baseClient, Logger logger, CertificateManager manager) throws Exception {
        super(frameParent, true);
        this.frameParent = frameParent;
        this.baseClient = baseClient;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleExportKeystore.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.logger = logger;
        this.setTitle(this.rb.getResourceString("title"));
        initComponents();
        PasswordOverlay.addTo(this.jPasswordFieldPassphrase, this.rb.getResourceString("label.keypass.hint"));
        TextOverlay.addTo(this.jTextFieldExportKeystoreFile, this.rb.getResourceString("label.exportdir.hint"));
        this.jLabelIcon.setIcon(new ImageIcon(JDialogCertificates.IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(
                JDialogCertificates.IMAGE_SIZE_DIALOG)));
        this.manager = manager;
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.setupKeyboardShortcuts();
        this.setButtonState();
    }

    private void setupKeyboardShortcuts() {
        KeyboardShortcutUtil.setupDialogKeyBindingsWithTooltips(this, this.jButtonOk, this.jButtonCancel);
    }

    /**
     * Sets the ok and cancel buttons of this GUI
     */
    private void setButtonState() {
        this.jButtonOk.setEnabled(!this.jTextFieldExportKeystoreFile.getText().isEmpty()
                && this.jPasswordFieldPassphrase.getPassword().length > 0);
    }

    /**
     * Finally export the key
     */
    private void performExportToKeystore() {
        try {
            String serverSideTargetPath = this.jTextFieldExportKeystoreFile.getText();
            char[] serverSideTargetPass = this.jPasswordFieldPassphrase.getPassword();
            int sourceKeystoreUsage = this.manager.getStorageUsage();
            ExportRequestKeystore request
                    = new ExportRequestKeystore(
                            sourceKeystoreUsage,
                            serverSideTargetPath,
                            serverSideTargetPass);
            ExportResponseKeystore response = (ExportResponseKeystore) this.baseClient.sendSync(request);
            if (response.getException() != null) {
                throw response.getException();
            }
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_SUCCESS,
                    this.rb.getResourceString("keystore.export.success.title"),
                    this.rb.getResourceString("keystore.exported.to.file",
                            new Object[]{
                                response.getSaveFileOnServer()
                            })
            );
            this.logger.fine(this.rb.getResourceString("keystore.exported.to.file",
                    new Object[]{
                        response.getSaveFileOnServer()
                    }));
        } catch (Throwable e) {
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("keystore.export.error.title"),
                    this.rb.getResourceString("keystore.export.error.message", e.getMessage()));
        }
    }

    private void browseExportDirectory() {
        String existingPath = this.jTextFieldExportKeystoreFile.getText();
        RemoteFileBrowser browser = new RemoteFileBrowser(this.frameParent, this.baseClient,
                this.rb.getResourceString("filechooser.key.export"));
        browser.setDirectoriesOnly(true);
        browser.setSelectedFile(existingPath);
        browser.setVisible(true);
        String selectedPath = browser.getSelectedPath();
        if (selectedPath != null) {
            this.jTextFieldExportKeystoreFile.setText(selectedPath);
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
        jTextFieldExportKeystoreFile = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonBrowseExportFile = new javax.swing.JButton();
        jPasswordFieldPassphrase = new javax.swing.JPasswordField();
        jPanelSpace2 = new javax.swing.JPanel();
        jPanelSpace1 = new javax.swing.JPanel();
        jPanelUIHelpLabelExportfile = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelPass = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
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

        jTextFieldExportKeystoreFile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldExportKeystoreFileKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldExportKeystoreFile, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
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

        jPasswordFieldPassphrase.setText("test");
        jPasswordFieldPassphrase.setMinimumSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPassphrase.setPreferredSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPassphrase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordFieldPassphraseKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jPasswordFieldPassphrase, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanelSpace2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanelSpace1, gridBagConstraints);

        jPanelUIHelpLabelExportfile.setToolTipText(this.rb.getResourceString( "label.exportdir.help"));
        jPanelUIHelpLabelExportfile.setText(this.rb.getResourceString( "label.exportdir"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelEdit.add(jPanelUIHelpLabelExportfile, gridBagConstraints);

        jPanelUIHelpLabelPass.setToolTipText(this.rb.getResourceString( "label.keypass.help"));
        jPanelUIHelpLabelPass.setText(this.rb.getResourceString( "label.keypass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelEdit.add(jPanelUIHelpLabelPass, gridBagConstraints);

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

        setSize(new java.awt.Dimension(500, 302));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jPasswordFieldPassphraseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldPassphraseKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jPasswordFieldPassphraseKeyReleased

    private void jButtonBrowseExportFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseExportFileActionPerformed
        this.browseExportDirectory();
    }//GEN-LAST:event_jButtonBrowseExportFileActionPerformed

    private void jTextFieldExportKeystoreFileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldExportKeystoreFileKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldExportKeystoreFileKeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible(false);
        this.performExportToKeystore();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseExportFile;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSpace1;
    private javax.swing.JPanel jPanelSpace2;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelExportfile;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelPass;
    private javax.swing.JPasswordField jPasswordFieldPassphrase;
    private javax.swing.JTextField jTextFieldExportKeystoreFile;
    // End of variables declaration//GEN-END:variables
}
