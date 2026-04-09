/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.mendelson.comm.as2.preferences;

import de.mendelson.comm.as2.clientserver.message.InboundAuthCredentialRequest;
import de.mendelson.comm.as2.clientserver.message.InboundAuthCredentialResponse;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Preferences panel for system-wide inbound authentication settings.
 * Allows configuration of authentication required for incoming AS2 messages.
 *
 * @author Julian Xu
 */
public class PreferencesPanelInboundAuth extends PreferencesPanel {

    private static final MendelsonMultiResolutionImage IMAGE_INBOUND_AUTH =
            MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/preferences/inbound_auth.svg",
                    JDialogPreferences.IMAGE_HEIGHT);

    private static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    private PreferencesClient preferences;
    private BaseClient baseClient;
    private CertificateManager certificateManager;
    private Logger logger;
    private InboundAuthCredentialAccessDB credentialDB;
    private String preferencesStrAtLoadTime = "";

    // UI Components
    private JCheckBox jCheckBoxEnableBasicAuth;
    private JCheckBox jCheckBoxEnableCertAuth;

    // Basic Auth Table
    private JTable jTableBasicAuth;
    private DefaultTableModel tableModelBasicAuth;
    private JButton jButtonAddBasic;
    private JButton jButtonRemoveBasic;
    private JScrollPane jScrollPaneBasicAuth;

    // Certificate Auth Table
    private JTable jTableCertAuth;
    private DefaultTableModel tableModelCertAuth;
    private JButton jButtonAddCert;
    private JButton jButtonRemoveCert;
    private JScrollPane jScrollPaneCertAuth;

    private JPanel jPanelBasicAuthTable;
    private JPanel jPanelCertAuthTable;

    public PreferencesPanelInboundAuth(BaseClient baseClient, CertificateManager certManager,
                                        IDBDriverManager dbDriverManager, Logger logger) {
        super();
        this.baseClient = baseClient;
        this.preferences = new PreferencesClient(baseClient);
        this.certificateManager = certManager;
        this.logger = logger;
        // Only create DB access if dbDriverManager is available (server-side)
        if (dbDriverManager != null) {
            this.credentialDB = new InboundAuthCredentialAccessDB(dbDriverManager, logger);
        }

        this.initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        // Title
        JLabel titleLabel = new JLabel(rb.getResourceString("inboundauth.title"));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        add(titleLabel, gbc);

        gbc.gridy++;
        JLabel infoLabel = new JLabel(
            "<html>" + rb.getResourceString("inboundauth.info") + "</html>"
        );
        add(infoLabel, gbc);

        // Checkboxes panel
        gbc.gridy++;
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jCheckBoxEnableBasicAuth = new JCheckBox(rb.getResourceString("inboundauth.enable.basic"));
        jCheckBoxEnableCertAuth = new JCheckBox(rb.getResourceString("inboundauth.enable.cert"));

        jCheckBoxEnableBasicAuth.addActionListener(e -> updateFieldsVisibility());
        jCheckBoxEnableCertAuth.addActionListener(e -> updateFieldsVisibility());

        checkboxPanel.add(jCheckBoxEnableBasicAuth);
        checkboxPanel.add(jCheckBoxEnableCertAuth);
        add(checkboxPanel, gbc);

        // Basic Auth Table Panel - Left side
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        jPanelBasicAuthTable = createBasicAuthTablePanel();
        add(jPanelBasicAuthTable, gbc);

        // Certificate Auth Table Panel - Right side
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        jPanelCertAuthTable = createCertAuthTablePanel();
        add(jPanelCertAuthTable, gbc);

        // Initially update visibility
        updateFieldsVisibility();
    }

    private JPanel createBasicAuthTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(rb.getResourceString("inboundauth.basic.title")));

        // Table with row number column
        String[] columns = {
            "#",
            rb.getResourceString("inboundauth.basic.col.username"),
            rb.getResourceString("inboundauth.basic.col.password")
        };
        tableModelBasicAuth = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Row number column is not editable
            }
        };
        jTableBasicAuth = new JTable(tableModelBasicAuth);
        jTableBasicAuth.setRowHeight(25);

        // Set row number column width and center alignment
        jTableBasicAuth.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTableBasicAuth.getColumnModel().getColumn(0).setMaxWidth(50);
        jTableBasicAuth.getColumnModel().getColumn(0).setCellRenderer(new RowNumberRenderer());

        jScrollPaneBasicAuth = new JScrollPane(jTableBasicAuth);
        jScrollPaneBasicAuth.setPreferredSize(new Dimension(400, 120));

        panel.add(jScrollPaneBasicAuth, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jButtonAddBasic = new JButton(rb.getResourceString("inboundauth.button.add"));
        jButtonRemoveBasic = new JButton(rb.getResourceString("inboundauth.button.remove"));

        jButtonAddBasic.addActionListener(e -> addBasicAuthRow());
        jButtonRemoveBasic.addActionListener(e -> removeBasicAuthRow());

        buttonPanel.add(jButtonAddBasic);
        buttonPanel.add(jButtonRemoveBasic);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCertAuthTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(rb.getResourceString("inboundauth.cert.title")));

        // Table with row number column and custom renderer/editor for certificate selection
        String[] columns = {"#", rb.getResourceString("inboundauth.cert.col.alias")};
        tableModelCertAuth = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // Row number
                return KeystoreCertificate.class; // Certificate column
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Row number column is not editable
            }
        };

        jTableCertAuth = new JTable(tableModelCertAuth);
        jTableCertAuth.setRowHeight(30);

        // Set row number column width and center alignment
        jTableCertAuth.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTableCertAuth.getColumnModel().getColumn(0).setMaxWidth(50);
        jTableCertAuth.getColumnModel().getColumn(0).setCellRenderer(new RowNumberRenderer());

        // Set cell renderer and editor for certificate column (column 1)
        jTableCertAuth.getColumnModel().getColumn(1).setCellRenderer(new CertificateCellRenderer());
        jTableCertAuth.getColumnModel().getColumn(1).setCellEditor(new CertificateCellEditor());

        jScrollPaneCertAuth = new JScrollPane(jTableCertAuth);
        jScrollPaneCertAuth.setPreferredSize(new Dimension(400, 120));

        panel.add(jScrollPaneCertAuth, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jButtonAddCert = new JButton(rb.getResourceString("inboundauth.button.add"));
        jButtonRemoveCert = new JButton(rb.getResourceString("inboundauth.button.remove"));

        jButtonAddCert.addActionListener(e -> addCertAuthRow());
        jButtonRemoveCert.addActionListener(e -> removeCertAuthRow());

        buttonPanel.add(jButtonAddCert);
        buttonPanel.add(jButtonRemoveCert);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addBasicAuthRow() {
        int rowNum = tableModelBasicAuth.getRowCount() + 1;
        tableModelBasicAuth.addRow(new Object[]{rowNum, "", ""});
    }

    private void removeBasicAuthRow() {
        int selectedRow = jTableBasicAuth.getSelectedRow();
        if (selectedRow >= 0) {
            tableModelBasicAuth.removeRow(selectedRow);
            // Update row numbers after deletion
            for (int i = 0; i < tableModelBasicAuth.getRowCount(); i++) {
                tableModelBasicAuth.setValueAt(i + 1, i, 0);
            }
        }
    }

    private void addCertAuthRow() {
        List<KeystoreCertificate> certs = certificateManager.getKeyStoreCertificateList();
        if (!certs.isEmpty()) {
            // Filter to only get certificates (not partner-related info)
            List<KeystoreCertificate> filteredCerts = new ArrayList<>();
            for (KeystoreCertificate cert : certs) {
                filteredCerts.add(cert);
            }

            if (!filteredCerts.isEmpty()) {
                int rowNum = tableModelCertAuth.getRowCount() + 1;
                tableModelCertAuth.addRow(new Object[]{rowNum, filteredCerts.get(0)});
            }
        }
    }

    private void removeCertAuthRow() {
        int selectedRow = jTableCertAuth.getSelectedRow();
        if (selectedRow >= 0) {
            tableModelCertAuth.removeRow(selectedRow);
            // Update row numbers after deletion
            for (int i = 0; i < tableModelCertAuth.getRowCount(); i++) {
                tableModelCertAuth.setValueAt(i + 1, i, 0);
            }
        }
    }

    private void updateFieldsVisibility() {
        boolean basicAuthEnabled = jCheckBoxEnableBasicAuth.isSelected();
        boolean certAuthEnabled = jCheckBoxEnableCertAuth.isSelected();

        // Enable/disable Basic Auth tab components
        jTableBasicAuth.setEnabled(basicAuthEnabled);
        jButtonAddBasic.setEnabled(basicAuthEnabled);
        jButtonRemoveBasic.setEnabled(basicAuthEnabled);

        // Enable/disable Certificate Auth tab components
        jTableCertAuth.setEnabled(certAuthEnabled);
        jButtonAddCert.setEnabled(certAuthEnabled);
        jButtonRemoveCert.setEnabled(certAuthEnabled);

        revalidate();
        repaint();
    }

    @Override
    public void loadPreferences() {
        int authMode = this.preferences.getInt(PreferencesAS2.INBOUND_AUTH_MODE);

        // authMode is now a bitmask:
        // 0 = no auth
        // 1 = basic auth only
        // 2 = cert auth only
        // 3 = both (1 + 2)
        jCheckBoxEnableBasicAuth.setSelected((authMode & 1) != 0);
        jCheckBoxEnableCertAuth.setSelected((authMode & 2) != 0);

        // Load credentials from database or via client-server message
        // Load basic auth credentials
        tableModelBasicAuth.setRowCount(0);
        List<InboundAuthCredential> basicCreds = loadCredentials(InboundAuthCredential.TYPE_BASIC);
        int rowNum = 1;
        for (InboundAuthCredential cred : basicCreds) {
            tableModelBasicAuth.addRow(new Object[]{rowNum++, cred.getUsername(), cred.getPassword()});
        }

        // Load certificate credentials
        tableModelCertAuth.setRowCount(0);
        List<InboundAuthCredential> certCreds = loadCredentials(InboundAuthCredential.TYPE_CERTIFICATE);
        rowNum = 1;
        for (InboundAuthCredential cred : certCreds) {
            KeystoreCertificate cert = certificateManager.getKeystoreCertificate(cred.getCertAlias());
            if (cert != null) {
                tableModelCertAuth.addRow(new Object[]{rowNum++, cert});
            }
        }

        updateFieldsVisibility();
        this.preferencesStrAtLoadTime = this.captureSettingsToStr();
    }

    /**
     * Load credentials either from local database (if available) or via client-server message
     */
    private List<InboundAuthCredential> loadCredentials(int authType) {
        if (credentialDB != null) {
            // Direct database access (server-side)
            return credentialDB.getCredentials(authType);
        } else {
            // Use client-server message (client-side)
            try {
                InboundAuthCredentialRequest request = new InboundAuthCredentialRequest(
                    InboundAuthCredentialRequest.OPERATION_GET, authType);
                InboundAuthCredentialResponse response =
                    (InboundAuthCredentialResponse) baseClient.sendSync(request);
                if (response != null && response.getCredentials() != null) {
                    return response.getCredentials();
                }
            } catch (Exception e) {
                if (logger != null) {
                    logger.warning("Failed to load inbound auth credentials: " + e.getMessage());
                }
            }
            return new ArrayList<>();
        }
    }

    @Override
    public void savePreferences() {
        // Calculate authMode as bitmask:
        // 0 = no auth (neither checked)
        // 1 = basic auth only
        // 2 = cert auth only
        // 3 = both (1 + 2)
        int authMode = 0;
        if (jCheckBoxEnableBasicAuth.isSelected()) authMode |= 1;
        if (jCheckBoxEnableCertAuth.isSelected()) authMode |= 2;

        this.preferences.putInt(PreferencesAS2.INBOUND_AUTH_MODE, authMode);

        // Collect basic auth credentials from table (skip row number column at index 0)
        List<InboundAuthCredential> basicCreds = new ArrayList<>();
        for (int i = 0; i < tableModelBasicAuth.getRowCount(); i++) {
            String username = (String) tableModelBasicAuth.getValueAt(i, 1); // Column 1: username
            String password = (String) tableModelBasicAuth.getValueAt(i, 2); // Column 2: password

            if (username != null && !username.trim().isEmpty()) {
                InboundAuthCredential cred = new InboundAuthCredential(InboundAuthCredential.TYPE_BASIC);
                cred.setUsername(username.trim());
                cred.setPassword(password != null ? password : "");
                basicCreds.add(cred);
            }
        }

        // Collect certificate credentials from table (skip row number column at index 0)
        List<InboundAuthCredential> certCreds = new ArrayList<>();
        for (int i = 0; i < tableModelCertAuth.getRowCount(); i++) {
            KeystoreCertificate cert = (KeystoreCertificate) tableModelCertAuth.getValueAt(i, 1); // Column 1: certificate

            if (cert != null) {
                InboundAuthCredential cred = new InboundAuthCredential(InboundAuthCredential.TYPE_CERTIFICATE);
                cred.setCertAlias(cert.getAlias());
                certCreds.add(cred);
            }
        }

        // Save credentials either via database or client-server message
        saveCredentials(InboundAuthCredential.TYPE_BASIC, basicCreds);
        saveCredentials(InboundAuthCredential.TYPE_CERTIFICATE, certCreds);
    }

    /**
     * Save credentials either to local database (if available) or via client-server message
     */
    private void saveCredentials(int authType, List<InboundAuthCredential> credentials) {
        if (credentialDB != null) {
            // Direct database access (server-side)
            credentialDB.deleteAllCredentials(authType);
            for (InboundAuthCredential cred : credentials) {
                credentialDB.addCredential(cred);
            }
        } else {
            // Use client-server message (client-side)
            try {
                InboundAuthCredentialRequest request = new InboundAuthCredentialRequest(
                    InboundAuthCredentialRequest.OPERATION_SAVE, authType);
                request.setCredentials(credentials);
                InboundAuthCredentialResponse response =
                    (InboundAuthCredentialResponse) baseClient.sendSync(request);
                if (response != null && !response.isSuccess()) {
                    if (logger != null) {
                        logger.warning("Failed to save inbound auth credentials");
                    }
                }
            } catch (Exception e) {
                if (logger != null) {
                    logger.warning("Failed to save inbound auth credentials: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean preferencesAreModified() {
        return !this.preferencesStrAtLoadTime.equals(this.captureSettingsToStr());
    }

    private String captureSettingsToStr() {
        StringBuilder builder = new StringBuilder();
        builder.append(jCheckBoxEnableBasicAuth.isSelected()).append(";");
        builder.append(jCheckBoxEnableCertAuth.isSelected()).append(";");

        // Capture basic auth table
        for (int i = 0; i < tableModelBasicAuth.getRowCount(); i++) {
            builder.append(tableModelBasicAuth.getValueAt(i, 1)).append(":");
            builder.append(tableModelBasicAuth.getValueAt(i, 2)).append(";");
        }

        // Capture cert table
        for (int i = 0; i < tableModelCertAuth.getRowCount(); i++) {
            KeystoreCertificate cert = (KeystoreCertificate) tableModelCertAuth.getValueAt(i, 1);
            if (cert != null) {
                builder.append(cert.getAlias()).append(";");
            }
        }

        return builder.toString();
    }

    @Override
    public ImageIcon getIcon() {
        return new ImageIcon(IMAGE_INBOUND_AUTH.toMinResolution(JDialogPreferences.IMAGE_HEIGHT));
    }

    @Override
    public String getTabResource() {
        return "tab.inboundauth";
    }

    // Inner class for certificate cell renderer
    private class CertificateCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            if (value instanceof KeystoreCertificate) {
                KeystoreCertificate cert = (KeystoreCertificate) value;
                setText(cert.getAlias());
            } else {
                setText("");
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            setOpaque(true);

            return this;
        }
    }

    // Inner class for certificate cell editor
    private class CertificateCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JComboBox<String> comboBox;  // Changed to String type
        private List<KeystoreCertificate> certificateList;

        public CertificateCellEditor() {
            comboBox = new JComboBox<>();
            certificateList = new ArrayList<>();
        }

        // Update the combo box items when editor is opened
        private void refreshCertificateList() {
            comboBox.removeAllItems();
            certificateList.clear();
            List<KeystoreCertificate> certs = certificateManager.getKeyStoreCertificateList();
            Collections.sort(certs);
            for (KeystoreCertificate cert : certs) {
                // Store certificate object separately
                certificateList.add(cert);
                // Display only the alias in the combo box
                comboBox.addItem(cert.getAlias());
            }
        }

        @Override
        public Object getCellEditorValue() {
            // Return the KeystoreCertificate object, not the string
            int selectedIndex = comboBox.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < certificateList.size()) {
                return certificateList.get(selectedIndex);
            }
            return null;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {

            // Refresh the certificate list each time editor is opened
            refreshCertificateList();

            if (value instanceof KeystoreCertificate) {
                KeystoreCertificate cert = (KeystoreCertificate) value;
                // Select the matching alias in the combo box
                comboBox.setSelectedItem(cert.getAlias());
            }

            return comboBox;
        }
    }

    // Inner class for row number cell renderer
    private class RowNumberRenderer extends JLabel implements TableCellRenderer {
        public RowNumberRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            setText(value != null ? value.toString() : String.valueOf(row + 1));

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            return this;
        }
    }
}
