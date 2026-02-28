//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogExportCertificate.java 28    11/02/25 13:40 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.MecFileChooser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.ListCellRendererCertificates;
import de.mendelson.util.security.cert.clientserver.CertificateExportRequest;
import de.mendelson.util.security.cert.clientserver.CertificateExportResponse;
import de.mendelson.util.uinotification.UINotification;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertPath;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Dialog to configure a single partner
 *
 * @author S.Heller
 * @version $Revision: 28 $
 */
public class JDialogExportCertificate extends JDialog {

    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleExportCertificate.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }
    private final CertificateManager manager;
    private final BaseClient baseClient;

    /**
     * @param manager Manages all certificates
     */
    public JDialogExportCertificate(JFrame parent, BaseClient baseClient, CertificateManager manager,
            String selectedAlias, Logger logger) {
        super(parent, true);
        //load resource bundle

        this.baseClient = baseClient;
        this.setTitle(rb.getResourceString("title"));
        initComponents();
        TextOverlay.addTo(jTextFieldExportFile,
                rb.getResourceString("label.exportfile.hint"));
        this.setMultiresolutionIcons();
        this.manager = manager;
        this.getRootPane().setDefaultButton(this.jButtonOk);
        //fill data into comboboxes
        this.jComboBoxExportFormat.addItem(new ExportFormat(KeystoreCertificate.CERTIFICATE_FORMAT_DER));
        this.jComboBoxExportFormat.addItem(new ExportFormat(KeystoreCertificate.CERTIFICATE_FORMAT_PEM));
        this.jComboBoxExportFormat.addItem(new ExportFormat(KeystoreCertificate.CERTIFICATE_FORMAT_PEM_CHAIN));
        this.jComboBoxExportFormat.addItem(new ExportFormat(KeystoreCertificate.CERTIFICATE_FORMAT_PKCS7));
        this.jComboBoxExportFormat.addItem(new ExportFormat(KeystoreCertificate.CERTIFICATE_FORMAT_SSH2));
        KeystoreCertificate selectedCert = this.manager.getKeystoreCertificate(selectedAlias);
        List<KeystoreCertificate> list = this.manager.getKeyStoreCertificateList();
        for (KeystoreCertificate cert : list) {
            this.jComboBoxCertificates.addItem(cert);
        }
        this.jComboBoxCertificates.setRenderer(new ListCellRendererCertificates());
        this.jComboBoxCertificates.setSelectedItem(selectedCert);
        this.setButtonState();
    }

    /**
     * Overwrite the designers icons by multi resolution icons
     */
    private void setMultiresolutionIcons() {
        this.jLabelIcon.setIcon(new ImageIcon(JDialogCertificates.IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(
                JDialogCertificates.IMAGE_SIZE_DIALOG)));
    }

    /**
     * Sets the ok and cancel buttons of this GUI
     */
    private void setButtonState() {
        this.jButtonOk.setEnabled(!this.jTextFieldExportFile.getText().isEmpty());
    }

    /**
     * Finally exports the certificate
     */
    private void performCertificateExport() {
        try {
            KeystoreCertificate selectedCertificate
                    = (KeystoreCertificate) this.jComboBoxCertificates.getSelectedItem();
            ExportFormat exportFormat = (ExportFormat) this.jComboBoxExportFormat.getSelectedItem();
            CertificateExportRequest request = new CertificateExportRequest(
                    this.manager.getStorageUsage(), selectedCertificate.getFingerPrintSHA1(),
                    exportFormat.getType());
            CertificateExportResponse response = (CertificateExportResponse) this.baseClient.sendSync(request);
            if (response.getException() != null) {
                throw response.getException();
            }
            byte[] exportData = response.getExportData();
            String exportFilename = this.jTextFieldExportFile.getText();
            if (exportFormat.getType().equals(KeystoreCertificate.CERTIFICATE_FORMAT_PEM)) {
                if (!exportFilename.toLowerCase().endsWith(".cer")) {
                    exportFilename += ".cer";
                }
            } else if (exportFormat.getType().equals(KeystoreCertificate.CERTIFICATE_FORMAT_PEM_CHAIN)) {
                if (!exportFilename.toLowerCase().endsWith(".pem")) {
                    exportFilename += ".pem";
                }
            } else if (exportFormat.getType().equals(KeystoreCertificate.CERTIFICATE_FORMAT_DER)) {
                if (!exportFilename.toLowerCase().endsWith(".cer")) {
                    exportFilename += ".cer";
                }
            } else if (exportFormat.getType().equals(KeystoreCertificate.CERTIFICATE_FORMAT_PKCS7)) {
                if (!exportFilename.toLowerCase().endsWith(".p7b")) {
                    exportFilename += ".p7b";
                }
            } else if (exportFormat.getType().equals(KeystoreCertificate.CERTIFICATE_FORMAT_SSH2)) {
                if (!exportFilename.toLowerCase().endsWith(".pub")) {
                    exportFilename += ".pub";
                }
            }
            Path file = Paths.get(exportFilename);
            if (exportData != null) {
                try (OutputStream outStream = Files.newOutputStream(file)) {
                    try (ByteArrayInputStream inStream = new ByteArrayInputStream(exportData)) {
                        inStream.transferTo(outStream);
                    }
                }
                String exportFilenameDisplay = Paths.get(exportFilename).toAbsolutePath().toString();
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_SUCCESS,
                        rb.getResourceString("certificate.export.success.title"),
                        rb.getResourceString("certificate.export.success.message",
                                exportFilenameDisplay));
            } else {
                throw new Exception(rb.getResourceString("error.empty.certificate"));
            }
        } catch (Throwable e) {
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    rb.getResourceString("certificate.export.error.title"),
                    rb.getResourceString("certificate.export.error.message",
                            "[" + e.getClass().getSimpleName() + "] "
                            + e.getMessage()));
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

        jPanelEdit = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jLabelExportFile = new javax.swing.JLabel();
        jTextFieldExportFile = new javax.swing.JTextField();
        jLabelExportEncoding = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jComboBoxExportFormat = new javax.swing.JComboBox();
        jLabelAlias = new javax.swing.JLabel();
        jComboBoxCertificates = new javax.swing.JComboBox<>();
        jButtonBrowse = new javax.swing.JButton();
        jPanelSpace1 = new javax.swing.JPanel();
        jPanelSpace2 = new javax.swing.JPanel();
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

        jLabelExportFile.setText(this.rb.getResourceString( "label.exportfile"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelExportFile, gridBagConstraints);

        jTextFieldExportFile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldExportFileKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jTextFieldExportFile, gridBagConstraints);

        jLabelExportEncoding.setText(this.rb.getResourceString( "label.exportformat"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelExportEncoding, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanel3, gridBagConstraints);

        jComboBoxExportFormat.setMinimumSize(new java.awt.Dimension(250, 24));
        jComboBoxExportFormat.setPreferredSize(new java.awt.Dimension(250, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jComboBoxExportFormat, gridBagConstraints);

        jLabelAlias.setText(this.rb.getResourceString( "label.alias"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelAlias, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        jPanelEdit.add(jComboBoxCertificates, gridBagConstraints);

        jButtonBrowse.setText("..");
        jButtonBrowse.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowse.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowse, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelEdit.add(jPanelSpace1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
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

        setSize(new java.awt.Dimension(500, 327));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        MecFileChooser chooser = new MecFileChooser(parent, rb.getResourceString("filechooser.certificate.export"));
        chooser.browseFilename(this.jTextFieldExportFile);
        this.setButtonState();
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    private void jTextFieldExportFileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldExportFileKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldExportFileKeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible(false);
        this.performCertificateExport();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JComboBox<KeystoreCertificate> jComboBoxCertificates;
    private javax.swing.JComboBox jComboBoxExportFormat;
    private javax.swing.JLabel jLabelAlias;
    private javax.swing.JLabel jLabelExportEncoding;
    private javax.swing.JLabel jLabelExportFile;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSpace1;
    private javax.swing.JPanel jPanelSpace2;
    private javax.swing.JTextField jTextFieldExportFile;
    // End of variables declaration//GEN-END:variables

    public static class ExportFormat {

        private final static MecResourceBundle rb;

        static {
            try {
                rb = (MecResourceBundle) ResourceBundle.getBundle(
                        ResourceBundleExportCertificate.class.getName());
            } catch (MissingResourceException e) {
                throw new RuntimeException("Oops..resource bundle "
                        + e.getClassName() + " not found.");
            }
        }

        private final String type;

        public ExportFormat(String type) {
            this.type = type;
            //load resource bundle

        }

        @Override
        public String toString() {
            return (rb.getResourceString(type));
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
            if (anObject != null && anObject instanceof ExportFormat) {
                ExportFormat exportFormat = (ExportFormat) anObject;
                return (exportFormat.getType().equals(this.getType()));
            }
            return (false);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + (this.getType() != null ? this.getType().hashCode() : 0);
            return hash;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }
    }
}
