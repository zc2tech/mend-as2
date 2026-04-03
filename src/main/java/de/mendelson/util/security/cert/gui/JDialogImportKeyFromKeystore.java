//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogImportKeyFromKeystore.java 11    11/02/25 13:40 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.WindowTitleUtil;
import de.mendelson.util.KeyboardShortcutUtil;
import de.mendelson.util.MecFileChooser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.JKSKeys2JKS;
import de.mendelson.util.security.JKSKeys2PKCS12;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.PKCS122JKS;
import de.mendelson.util.security.PKCS122PKCS12;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.uinotification.UINotification;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


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
 * Dialog to import a key from a keystore (pkcs#12, jks)
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class JDialogImportKeyFromKeystore extends JDialog {

    /**
     * ResourceBundle to localize the GUI
     */
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleImportKey.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }
    private final CertificateManager manager;
    private String newAlias = null;
    private final Logger logger;

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param manager Manager that handles the certificates
     */
    public JDialogImportKeyFromKeystore(JFrame parent, Logger logger, CertificateManager manager) {
        super(parent, true);
        this.setTitle(rb.getResourceString("title"));
        initComponents();
        PasswordOverlay.addTo(this.jPasswordFieldPassphrase,
                rb.getResourceString("label.keypass.hint"));
        TextOverlay.addTo(this.jTextFieldImportKeystoreFile, rb.getResourceString("label.importkey.hint"));
        this.jLabelImage.setIcon(new ImageIcon(JDialogCertificates.IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(
                JDialogCertificates.IMAGE_SIZE_DIALOG)));
        this.manager = manager;
        this.logger = logger;
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.setupKeyboardShortcuts();
        this.setButtonState();
    }

    private void setupKeyboardShortcuts() {
        KeyboardShortcutUtil.setupDialogKeyBindingsWithTooltips(this, this.jButtonOk, this.jButtonCancel);
    }

    public String getNewAlias() {
        return (this.newAlias);
    }

    /**
     * Sets the ok and cancel buttons of this GUI
     */
    private void setButtonState() {
        this.jButtonOk.setEnabled(!this.jTextFieldImportKeystoreFile.getText().isEmpty());
    }

    /**
     * Tries to import a key from a PKCS#12 or JKS formatted keystore file
     *
     */
    private void performImport() {
        try {
            this.performImportPKCS12();
        } catch (Throwable e) {
            try {
                this.performImportJKS();
            } catch (Throwable ex) {
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_ERROR,
                        rb.getResourceString("key.import.error.title"),
                        rb.getResourceString("key.import.error.message",
                                "[" + ex.getClass().getSimpleName() + "]:" + e.getMessage()));
            }
        }
    }

    /**
     * Import the key, jks
     */
    private void performImportJKS() throws Exception {
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        KeyStore sourceKeystore = KeyStore.getInstance("JKS", "SUN");
        KeyStoreUtil.loadKeyStore(sourceKeystore, this.jTextFieldImportKeystoreFile.getText(),
                this.jPasswordFieldPassphrase.getPassword());
        List<String> keyAliasesList = KeyStoreUtil.getKeyAliases(sourceKeystore);
        String selectedAlias = null;
        if (keyAliasesList.isEmpty()) {
            throw new Exception(rb.getResourceString("keystore.contains.nokeys"));
        } else if (keyAliasesList.size() == 1) {
            selectedAlias = keyAliasesList.get(0);
        } else {
            //multiple keys available
            Object[] aliasArray = new Object[keyAliasesList.size()];
            for (int i = 0; i < keyAliasesList.size(); i++) {
                aliasArray[i] = keyAliasesList.get(i);
            }
            Object selectedAliasObject = JOptionPane.showInputDialog(parent,
                    rb.getResourceString("multiple.keys.message"),
                    rb.getResourceString("multiple.keys.title"), JOptionPane.QUESTION_MESSAGE,
                    null, aliasArray, aliasArray[0]);
            //user break
            if (selectedAliasObject == null) {
                return;
            }
            selectedAlias = selectedAliasObject.toString();
        }
        //check if entry with this fingerprint does already exist in the underlaying manager
        X509Certificate foundKeyCertificate = KeyStoreUtil.getCertificate(sourceKeystore, selectedAlias);
        if (foundKeyCertificate != null) {
            KeystoreCertificate foundKeyKeystoreCertificate = new KeystoreCertificate();
            foundKeyKeystoreCertificate.setCertificate(foundKeyCertificate, null);
            String fingerprintToImport = foundKeyKeystoreCertificate.getFingerPrintSHA1();
            KeystoreCertificate existingEntry = this.manager.getKeystoreCertificateByFingerprintSHA1(fingerprintToImport);
            if (existingEntry != null) {
                throw new Exception(rb.getResourceString("key.import.error.entry.exists",
                        existingEntry.getAlias()));
            }
        }
        if (this.manager.getStorageType().equals(BCCryptoHelper.KEYSTORE_PKCS12)) {
            //import JKS key to PKCS#12 keystore
            JKSKeys2PKCS12 importer = new JKSKeys2PKCS12(this.logger);
            importer.setTargetKeyStore(this.manager.getKeystore());
            char[] sourceKeypass = this.requestKeyPass(parent, selectedAlias);
            //user canceled
            if (sourceKeypass == null) {
                return;
            }
            importer.exportKeyFrom(sourceKeystore, sourceKeypass, selectedAlias);
        } else {
            //import JKS key to JKS keystore
            JKSKeys2JKS importer = new JKSKeys2JKS(this.logger);
            importer.setTargetKeyStore(this.manager.getKeystore(), this.manager.getKeystorePass());
            char[] sourceKeypass = this.requestKeyPass(parent, selectedAlias);
            //user canceled
            if (sourceKeypass == null) {
                return;
            }
            importer.exportKey(sourceKeystore, sourceKeypass, selectedAlias);
        }
        this.newAlias = selectedAlias;
        UINotification.instance().addNotification(null,
                UINotification.TYPE_SUCCESS,
                rb.getResourceString("key.import.success.title"),
                rb.getResourceString("key.import.success.message"));
    }

    private char[] requestKeyPass(JFrame parent, String alias) {
        final JPasswordField passwordField = new JPasswordField(30);
        final JOptionPane dialog = new JOptionPane(passwordField,
                JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
            @Override
            public void selectInitialValue() {
                passwordField.requestFocusInWindow();
            }
        };
        dialog.createDialog(parent, rb.getResourceString("enter.keypassword", alias)).setVisible(true);
        Object answer = dialog.getValue();
        if (answer == null || answer == JOptionPane.UNINITIALIZED_VALUE) {
            return (null);
        } else {
            int keyIndex = ((Integer) answer).intValue();
            if (keyIndex == 0) {
                return (passwordField.getPassword());
            }
        }
        return (null);
    }

    /**
     * Import the key, pkcs#12
     */
    private void performImportPKCS12() throws Exception {
        KeyStore sourceKeystore = KeyStore.getInstance(BCCryptoHelper.KEYSTORE_PKCS12,
                BouncyCastleProvider.PROVIDER_NAME);
        KeyStoreUtil.loadKeyStore(sourceKeystore, this.jTextFieldImportKeystoreFile.getText(),
                this.jPasswordFieldPassphrase.getPassword());
        List<String> keyAliasesList = KeyStoreUtil.getKeyAliases(sourceKeystore);
        String selectedAlias = null;
        if (keyAliasesList.isEmpty()) {
            throw new Exception(rb.getResourceString("keystore.contains.nokeys"));
        } else if (keyAliasesList.size() == 1) {
            selectedAlias = keyAliasesList.get(0);
        } else {
            //multiple keys available
            Object[] aliasArray = new Object[keyAliasesList.size()];
            for (int i = 0; i < keyAliasesList.size(); i++) {
                aliasArray[i] = keyAliasesList.get(i);
            }
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
            Object selectedAliasObject = JOptionPane.showInputDialog(parent,
                    rb.getResourceString("multiple.keys.message"),
                    rb.getResourceString("multiple.keys.title"), JOptionPane.QUESTION_MESSAGE,
                    null, aliasArray, aliasArray[0]);
            //user break
            if (selectedAliasObject == null) {
                return;
            }
            selectedAlias = selectedAliasObject.toString();
        }
        //check if entry with this fingerprint does already exist in the underlaying manager
        X509Certificate foundKeyCertificate = KeyStoreUtil.getCertificate(sourceKeystore, selectedAlias);
        if (foundKeyCertificate != null) {
            KeystoreCertificate foundKeyKeystoreCertificate = new KeystoreCertificate();
            foundKeyKeystoreCertificate.setCertificate(foundKeyCertificate, null);
            String fingerprintToImport = foundKeyKeystoreCertificate.getFingerPrintSHA1();
            KeystoreCertificate existingEntry = this.manager.getKeystoreCertificateByFingerprintSHA1(fingerprintToImport);
            if (existingEntry != null) {
                throw new Exception(rb.getResourceString("key.import.error.entry.exists",
                        existingEntry.getAlias()));
            }
        }
        if (this.manager.getStorageType().equals(BCCryptoHelper.KEYSTORE_PKCS12)) {
            PKCS122PKCS12 importer = new PKCS122PKCS12(this.logger);
            importer.setTargetKeyStore(this.manager.getKeystore(), this.manager.getKeystorePass());
            importer.exportKeyFrom(sourceKeystore, selectedAlias);
        } else if (this.manager.getStorageType().equals(BCCryptoHelper.KEYSTORE_JKS)) {
            PKCS122JKS importer = new PKCS122JKS(this.logger);
            importer.setTargetKeyStore(this.manager.getKeystore(), this.manager.getKeystorePass());
            importer.importKey(sourceKeystore, selectedAlias);
        }
        this.newAlias = selectedAlias;
        UINotification.instance().addNotification(null,
                UINotification.TYPE_SUCCESS,
                rb.getResourceString("key.import.success.title"),
                rb.getResourceString("key.import.success.message"));
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
        jLabelImage = new javax.swing.JLabel();
        jLabelImportKeystoreFile = new javax.swing.JLabel();
        jTextFieldImportKeystoreFile = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonBrowseImportFile = new javax.swing.JButton();
        jLabelKeystorePassphrase = new javax.swing.JLabel();
        jPasswordFieldPassphrase = new javax.swing.JPasswordField();
        jPanelSpace2 = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jLabelImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 20, 10);
        jPanelEdit.add(jLabelImage, gridBagConstraints);

        jLabelImportKeystoreFile.setText(this.rb.getResourceString( "label.importkey"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelImportKeystoreFile, gridBagConstraints);

        jTextFieldImportKeystoreFile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldImportKeystoreFileKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldImportKeystoreFile, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanel3, gridBagConstraints);

        jButtonBrowseImportFile.setText("..");
        jButtonBrowseImportFile.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowseImportFile.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowseImportFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseImportFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowseImportFile, gridBagConstraints);

        jLabelKeystorePassphrase.setText(this.rb.getResourceString( "label.keypass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelKeystorePassphrase, gridBagConstraints);

        jPasswordFieldPassphrase.setMinimumSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPassphrase.setPreferredSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPassphrase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordFieldPassphraseKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jPasswordFieldPassphrase, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanelSpace2, gridBagConstraints);

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

        setSize(new java.awt.Dimension(437, 278));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jPasswordFieldPassphraseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldPassphraseKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jPasswordFieldPassphraseKeyReleased

    private void jButtonBrowseImportFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseImportFileActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        MecFileChooser chooser = new MecFileChooser(
                parent,
                rb.getResourceString("filechooser.key.import"));
        chooser.browseFilename(this.jTextFieldImportKeystoreFile);
        this.setButtonState();
    }//GEN-LAST:event_jButtonBrowseImportFileActionPerformed

    private void jTextFieldImportKeystoreFileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldImportKeystoreFileKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldImportKeystoreFileKeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible(false);
        this.performImport();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseImportFile;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JLabel jLabelImportKeystoreFile;
    private javax.swing.JLabel jLabelKeystorePassphrase;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSpace2;
    private javax.swing.JPasswordField jPasswordFieldPassphrase;
    private javax.swing.JTextField jTextFieldImportKeystoreFile;
    // End of variables declaration//GEN-END:variables

}
