//$Header: /mec_oftp2/de/mendelson/util/mailautoconfig/gui/JDialogMailAutoConfigurationDetection.java 8     14/03/25 11:33 Heller $
package de.mendelson.util.mailautoconfig.gui;

import de.mendelson.util.IStatusBar;
import de.mendelson.util.WindowTitleUtil;
import de.mendelson.util.LockingGlassPane;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.mailautoconfig.MailServiceConfiguration;
import de.mendelson.util.mailautoconfig.clientserver.MailAutoConfigDetectRequest;
import de.mendelson.util.mailautoconfig.clientserver.MailAutoConfigDetectResponse;
import de.mendelson.util.uinotification.UINotification;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


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
 * Detect mail server settings by a give mail address
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class JDialogMailAutoConfigurationDetection extends JDialog implements ListSelectionListener {

    /**
     * ResourceBundle to localize the GUI
     */
    private MecResourceBundle rb = null;

    private final static MendelsonMultiResolutionImage IMAGE_MAILSERVERDETECTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/mailautoconfig/gui/detect.svg", 32);
    private final BaseClient baseClient;
    private final IStatusBar statusbar;
    private boolean useConfiguration = false;

    public JDialogMailAutoConfigurationDetection(JFrame parent,
            String mailAddress, BaseClient baseClient, IStatusBar statusbar) {
        super(parent, true);
        this.baseClient = baseClient;
        this.statusbar = statusbar;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMailAutoConfigurationDetection.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.setTitle(this.rb.getResourceString("title"));
        initComponents();
        this.jTableConfiguration.setModel(new TableModelMailAutoDetection());
        this.jTableConfiguration.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jTableConfiguration.getSelectionModel().addListSelectionListener(this);
        this.jTableConfiguration.setRowHeight(TableModelMailAutoDetection.ROW_HEIGHT);
        this.jLabelIcon.setIcon(new ImageIcon(IMAGE_MAILSERVERDETECTION.toMinResolution(32)));
        this.jTextFieldMailAddress.setText(mailAddress);
        this.jTextFieldMailAddress.selectAll();
        TextOverlay.addTo(this.jTextFieldMailAddress,
                this.rb.getResourceString("label.email.hint"));
        this.getRootPane().setDefaultButton(this.jButtonStartDetection);
        this.setButtonState();
    }

    private boolean emailAddressIsValid(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return (false);
        }
        return (true);
    }

    public boolean useConfiguration() {
        return (this.useConfiguration);
    }

    public String getMailAddress() {
        return (this.jTextFieldMailAddress.getText());
    }

    public MailServiceConfiguration getSelectedConfiguration() {
        int selectedRow = this.jTableConfiguration.getSelectedRow();
        return (((TableModelMailAutoDetection) this.jTableConfiguration.getModel()).getConfigurationAt(selectedRow));
    }

    /**
     * Sets the ok and cancel buttons of this GUI
     */
    private void setButtonState() {
        this.jButtonOk.setEnabled(this.jTableConfiguration.getSelectedRow() >= 0);
    }

    /**
     * Lock the component: Add a glasspane that prevents any action on the UI
     */
    private void lock() {
        //init glasspane for first use
        if (!(this.getGlassPane() instanceof LockingGlassPane)) {
            this.setGlassPane(new LockingGlassPane());
        }
        this.getGlassPane().setVisible(true);
        this.getGlassPane().requestFocusInWindow();
    }

    /**
     * Unlock the component: remove the glass pane that prevents any action on
     * the UI
     */
    private void unlock() {
        getGlassPane().setVisible(false);
    }

    /**
     * Sends a request to the server to perform a mail configuration detection
     */
    private void performDetection() {
        if (!this.emailAddressIsValid(this.jTextFieldMailAddress.getText())) {
            UINotification.instance().addNotification(
                    JDialogMailAutoConfigurationDetection.IMAGE_MAILSERVERDETECTION,
                    UINotification.TYPE_ERROR,
                    JDialogMailAutoConfigurationDetection.this.rb.getResourceString("email.invalid.title"),
                    JDialogMailAutoConfigurationDetection.this.rb.getResourceString("email.invalid.text",
                            JDialogMailAutoConfigurationDetection.this.jTextFieldMailAddress.getText()));
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JDialogMailAutoConfigurationDetection.this.lock();
                final String uniqueId = this.getClass().getName() + ".performDetection." + System.currentTimeMillis();
                try {
                    JDialogMailAutoConfigurationDetection.this.statusbar.startProgressIndeterminate(
                            JDialogMailAutoConfigurationDetection.this.rb.getResourceString("progress.detection"), uniqueId);
                    MailAutoConfigDetectRequest request
                            = new MailAutoConfigDetectRequest(
                                    JDialogMailAutoConfigurationDetection.this.jTextFieldMailAddress.getText());
                    MailAutoConfigDetectResponse response = (MailAutoConfigDetectResponse) baseClient.sendSync(request);
                    if (response.getException() != null) {
                        throw (response.getException());
                    }
                    ((TableModelMailAutoDetection) JDialogMailAutoConfigurationDetection.this.jTableConfiguration.getModel())
                            .passNewData(response.getConfiguration());
                    if (!response.getConfiguration().isEmpty()) {
                        JDialogMailAutoConfigurationDetection.this.jLabelProvider.setText(
                                JDialogMailAutoConfigurationDetection.this.rb.getResourceString("label.detectedprovider",
                                        response.getConfiguration().get(0).getMailProviderLongName()));
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JDialogMailAutoConfigurationDetection.this.jTableConfiguration.getSelectionModel()
                                        .setSelectionInterval(0, 0);
                            }
                        });
                    } else {
                        JDialogMailAutoConfigurationDetection.this.jLabelProvider.setText(
                                JDialogMailAutoConfigurationDetection.this.rb.getResourceString("label.detectedprovider",
                                        "--"));
                        UINotification.instance().addNotification(
                                JDialogMailAutoConfigurationDetection.IMAGE_MAILSERVERDETECTION,
                                UINotification.TYPE_ERROR,
                                JDialogMailAutoConfigurationDetection.this.rb.getResourceString("detection.failed.title"),
                                JDialogMailAutoConfigurationDetection.this.rb.getResourceString("detection.failed.text",
                                        JDialogMailAutoConfigurationDetection.this.jTextFieldMailAddress.getText()));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    UINotification.instance().addNotification(e);
                } finally {
                    JDialogMailAutoConfigurationDetection.this.unlock();
                    JDialogMailAutoConfigurationDetection.this.statusbar.stopProgressIfExists(uniqueId);
                }
            }
        };
        GUIClient.submit( runnable );
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
        jLabelMailAddress = new javax.swing.JLabel();
        jTextFieldMailAddress = new javax.swing.JTextField();
        jButtonStartDetection = new javax.swing.JButton();
        jScrollPaneConfig = new javax.swing.JScrollPane();
        jTableConfiguration = new javax.swing.JTable();
        jLabelProvider = new javax.swing.JLabel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/mailautoconfig/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 20, 10);
        jPanelEdit.add(jLabelIcon, gridBagConstraints);

        jLabelMailAddress.setText(this.rb.getResourceString( "label.mailaddress"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelMailAddress, gridBagConstraints);

        jTextFieldMailAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldMailAddressKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldMailAddress, gridBagConstraints);

        jButtonStartDetection.setText(this.rb.getResourceString("button.start.detection"));
        jButtonStartDetection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartDetectionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonStartDetection, gridBagConstraints);

        jTableConfiguration.setShowHorizontalLines(false);
        jTableConfiguration.setShowVerticalLines(false);
        jTableConfiguration.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableConfigurationMouseClicked(evt);
            }
        });
        jScrollPaneConfig.setViewportView(jTableConfiguration);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        jPanelEdit.add(jScrollPaneConfig, gridBagConstraints);

        jLabelProvider.setText(this.rb.getResourceString( "label.detectedprovider", "--")
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        jPanelEdit.add(jLabelProvider, gridBagConstraints);

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

        setSize(new java.awt.Dimension(559, 456));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldMailAddressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldMailAddressKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldMailAddressKeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.useConfiguration = true;
        this.setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonStartDetectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartDetectionActionPerformed
        this.performDetection();
    }//GEN-LAST:event_jButtonStartDetectionActionPerformed

    private void jTableConfigurationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableConfigurationMouseClicked
        if (evt.getClickCount() == 2) {
            this.useConfiguration = true;
            this.setVisible(false);
        }
    }//GEN-LAST:event_jTableConfigurationMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonStartDetection;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelMailAddress;
    private javax.swing.JLabel jLabelProvider;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JScrollPane jScrollPaneConfig;
    private javax.swing.JTable jTableConfiguration;
    private javax.swing.JTextField jTextFieldMailAddress;
    // End of variables declaration//GEN-END:variables

    /**
     * Makes this a ListSelectionListener
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.setButtonState();
    }
}
