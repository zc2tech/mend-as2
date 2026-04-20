package de.mendelson.comm.as2.partner.gui;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;

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
 * Simple dialog for selecting a certificate from the TLS keystore
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class JDialogSelectCertificate extends JDialog {

    private KeystoreCertificate selectedCertificate = null;
    private JList<KeystoreCertificate> certificateList;
    private DefaultListModel<KeystoreCertificate> listModel;

    public JDialogSelectCertificate(Window parent, CertificateManager certificateManager,
                                    String title, String message) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        initComponents(certificateManager, message);
        setLocationRelativeTo(parent);
    }

    private void initComponents(CertificateManager certificateManager, String message) {
        setLayout(new BorderLayout(10, 10));

        // Message panel at top
        JPanel messagePanel = new JPanel(new BorderLayout());
        JLabel messageLabel = new JLabel("<html>" + message + "</html>");
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.NORTH);

        // Certificate list in center
        listModel = new DefaultListModel<>();
        List<KeystoreCertificate> certList = certificateManager.getKeyStoreCertificateList();
        for (KeystoreCertificate cert : certList) {
            listModel.addElement(cert);
        }

        certificateList = new JList<>(listModel);
        certificateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        certificateList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value,
                                                                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof KeystoreCertificate) {
                    KeystoreCertificate cert = (KeystoreCertificate) value;
                    setText(cert.getAlias() + " (" + cert.getFingerPrintSHA1() + ")");
                }
                return this;
            }
        });

        // Double-click to select
        certificateList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectAndClose();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(certificateList);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent e) -> selectAndClose());
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            selectedCertificate = null;
            dispose();
        });
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
        pack();
    }

    private void selectAndClose() {
        selectedCertificate = certificateList.getSelectedValue();
        dispose();
    }

    public KeystoreCertificate getSelectedCertificate() {
        return selectedCertificate;
    }
}
