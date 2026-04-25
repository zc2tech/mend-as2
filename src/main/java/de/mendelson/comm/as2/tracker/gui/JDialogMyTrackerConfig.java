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
package de.mendelson.comm.as2.tracker.gui;

import de.mendelson.comm.as2.tracker.auth.UserTrackerAuthCredential;
import de.mendelson.comm.as2.tracker.auth.UserTrackerAuthDB;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.toggleswitch.ToggleSwitch;
import de.mendelson.util.uinotification.UINotification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Dialog for user-specific tracker authentication configuration
 *
 * @author Julian Xu
 */
public class JDialogMyTrackerConfig extends JDialog {

    private MecResourceBundle rb;
    private BaseClient baseClient;
    private String username;
    private int userId = -1;
    private List<KeystoreCertificate> availableCerts = new ArrayList<>();

    // Authentication settings
    private boolean basicAuthEnabled = false;
    private boolean certAuthEnabled = false;
    private List<UserTrackerAuthCredential> basicAuthList = new ArrayList<>();
    private List<UserTrackerAuthCredential> certAuthList = new ArrayList<>();

    // UI Components
    private JTabbedPane jTabbedPane;
    private ToggleSwitch toggleBasicAuthEnabled;
    private ToggleSwitch toggleCertAuthEnabled;
    private JTable jTableBasicAuth;
    private DefaultTableModel tableModelBasicAuth;
    private JTable jTableCertAuth;
    private DefaultTableModel tableModelCertAuth;
    private JButton jButtonOk;
    private JButton jButtonCancel;

    public JDialogMyTrackerConfig(JFrame parent, BaseClient baseClient) {
        super(parent, true);
        this.baseClient = baseClient;
        this.username = baseClient.getUsername();

        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogMyTrackerConfig.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Resource bundle not found: " + e.getClassName());
        }

        // Get user ID
        if (!loadUserId()) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_ERROR,
                    "Failed to load user",
                    "Could not find user information");
            this.dispose();
            return;
        }

        // Load certificates
        loadCertificates();

        // Load existing configuration
        loadConfiguration();

        initComponents();
        this.getRootPane().setDefaultButton(this.jButtonOk);
    }

    private boolean loadUserId() {
        try {
            de.mendelson.comm.as2.server.DirectServiceClient serviceClient =
                    de.mendelson.comm.as2.server.DirectServiceClient.getInstance();
            if (serviceClient == null) {
                return false;
            }

            de.mendelson.comm.as2.server.AS2ServerProcessing processing = serviceClient.getServerProcessing();
            if (processing == null) {
                return false;
            }

            de.mendelson.util.database.IDBDriverManager dbDriverManager = processing.getDBDriverManager();
            if (dbDriverManager == null) {
                return false;
            }

            Connection connection = null;
            try {
                connection = dbDriverManager.getConnectionWithoutErrorHandling(
                        de.mendelson.util.database.IDBDriverManager.DB_CONFIG);

                UserManagementAccessDB userDB = new UserManagementAccessDB(
                        dbDriverManager,
                        java.util.logging.Logger.getLogger("MyTrackerConfig"));

                WebUIUser user = userDB.getUserByUsername(this.username);
                if (user != null) {
                    this.userId = user.getId();
                    return true;
                }
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadCertificates() {
        try {
            de.mendelson.comm.as2.server.DirectServiceClient serviceClient =
                    de.mendelson.comm.as2.server.DirectServiceClient.getInstance();
            if (serviceClient == null) {
                return;
            }

            de.mendelson.comm.as2.server.AS2ServerProcessing processing = serviceClient.getServerProcessing();
            if (processing == null) {
                return;
            }

            CertificateManager certManager = processing.getCertificateManagerSignEncrypt();
            if (certManager != null) {
                List<KeystoreCertificate> certs = certManager.getKeyStoreCertificateList();
                // Filter for public certificates only (not key pairs)
                for (KeystoreCertificate cert : certs) {
                    if (!cert.getIsKeyPair()) {
                        availableCerts.add(cert);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfiguration() {
        try {
            de.mendelson.comm.as2.server.DirectServiceClient serviceClient =
                    de.mendelson.comm.as2.server.DirectServiceClient.getInstance();
            if (serviceClient == null) {
                return;
            }

            de.mendelson.comm.as2.server.AS2ServerProcessing processing = serviceClient.getServerProcessing();
            if (processing == null) {
                return;
            }

            de.mendelson.util.database.IDBDriverManager dbDriverManager = processing.getDBDriverManager();
            if (dbDriverManager == null) {
                return;
            }

            Connection connection = null;
            try {
                connection = dbDriverManager.getConnectionWithoutErrorHandling(
                        de.mendelson.util.database.IDBDriverManager.DB_CONFIG);

                UserTrackerAuthDB authDB = new UserTrackerAuthDB();

                // Load master toggles
                boolean[] toggles = authDB.loadMasterToggles(this.userId, connection);
                this.basicAuthEnabled = toggles[0];
                this.certAuthEnabled = toggles[1];

                // Load credentials
                List<UserTrackerAuthCredential> allCredentials = authDB.loadCredentials(this.userId, connection);

                // Separate by type
                for (UserTrackerAuthCredential cred : allCredentials) {
                    if (cred.getAuthType() == UserTrackerAuthCredential.AUTH_TYPE_BASIC) {
                        basicAuthList.add(cred);
                    } else if (cred.getAuthType() == UserTrackerAuthCredential.AUTH_TYPE_CERTIFICATE) {
                        certAuthList.add(cred);
                    }
                }
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_WARNING,
                    "Failed to load configuration",
                    "Starting with default settings");
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(rb.getResourceString("title"));
        setResizable(true);

        JPanel jPanelMain = new JPanel();
        jPanelMain.setLayout(new BorderLayout(10, 10));
        jPanelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info panel at top
        JPanel infoPanel = createInfoPanel();
        jPanelMain.add(infoPanel, BorderLayout.NORTH);

        // Tabbed pane
        jTabbedPane = new JTabbedPane();

        // Basic Auth tab
        JPanel basicAuthPanel = createBasicAuthPanel();
        jTabbedPane.addTab(rb.getResourceString("tab.basicauth"), basicAuthPanel);

        // Certificate Auth tab
        JPanel certAuthPanel = createCertAuthPanel();
        jTabbedPane.addTab(rb.getResourceString("tab.certauth"), certAuthPanel);

        jPanelMain.add(jTabbedPane, BorderLayout.CENTER);

        // Button panel
        JPanel jPanelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        jButtonOk = new JButton(rb.getResourceString("button.ok"));
        jButtonOk.addActionListener(evt -> jButtonOkActionPerformed());
        jPanelButtons.add(jButtonOk);

        jButtonCancel = new JButton(rb.getResourceString("button.cancel"));
        jButtonCancel.addActionListener(evt -> dispose());
        jPanelButtons.add(jButtonCancel);

        jPanelMain.add(jPanelButtons, BorderLayout.SOUTH);

        getContentPane().add(jPanelMain);

        // Add keyboard shortcuts
        setupKeyboardShortcuts();

        setSize(700, 500);
        setLocationRelativeTo(getParent());
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(245, 245, 245));

        // Get tracker URL
        String trackerUrl = getTrackerUrl();

        JLabel infoLabel = new JLabel(rb.getResourceString("label.endpoint"));
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField urlField = new JTextField(trackerUrl);
        urlField.setEditable(false);
        urlField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        urlField.setBackground(Color.WHITE);
        urlField.setAlignmentX(Component.LEFT_ALIGNMENT);
        urlField.setMaximumSize(new Dimension(Integer.MAX_VALUE, urlField.getPreferredSize().height));

        panel.add(infoLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(urlField);

        return panel;
    }

    private String getTrackerUrl() {
        String host = de.mendelson.comm.as2.server.ServerConfigurationHelper.getHostname();
        String protocol = "https";
        int port = 8443;

        try {
            de.mendelson.comm.as2.server.AS2Server server =
                    de.mendelson.comm.as2.server.AS2Server.getStaticServerReference();

            if (server != null) {
                de.mendelson.util.httpconfig.server.HTTPServerConfigInfo configInfo =
                        server.getHTTPServerConfigInfo();

                if (configInfo != null) {
                    Integer httpsPort = de.mendelson.comm.as2.server.ServerConfigurationHelper.getHttpsPort(configInfo);
                    if (httpsPort != null) {
                        port = httpsPort;
                        protocol = "https";
                    } else {
                        Integer httpPort = de.mendelson.comm.as2.server.ServerConfigurationHelper.getHttpPort(configInfo);
                        if (httpPort != null) {
                            port = httpPort;
                            protocol = "http";
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Use defaults
        }

        return protocol + "://" + host + ":" + port + "/as2/tracker/" + this.username;
    }

    private JPanel createBasicAuthPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Enable toggle
        JLabel labelEnable = new JLabel(rb.getResourceString("label.basicauth.enable"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(labelEnable, gbc);

        toggleBasicAuthEnabled = new ToggleSwitch();
        toggleBasicAuthEnabled.setSelected(this.basicAuthEnabled);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(toggleBasicAuthEnabled, gbc);

        // Table
        String[] columnNames = {
                rb.getResourceString("column.enabled"),
                rb.getResourceString("column.username"),
                rb.getResourceString("column.password")
        };
        tableModelBasicAuth = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        jTableBasicAuth = new JTable(tableModelBasicAuth);
        jTableBasicAuth.setRowHeight(25);

        // Set column widths
        TableColumn enabledCol = jTableBasicAuth.getColumnModel().getColumn(0);
        enabledCol.setPreferredWidth(80);
        enabledCol.setMaxWidth(80);

        JScrollPane scrollPane = new JScrollPane(jTableBasicAuth);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd = new JButton(rb.getResourceString("button.add"));
        btnAdd.addActionListener(e -> addBasicAuthRow());
        buttonPanel.add(btnAdd);

        JButton btnDelete = new JButton(rb.getResourceString("button.delete"));
        btnDelete.addActionListener(e -> deleteBasicAuthRow());
        buttonPanel.add(btnDelete);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        // Load data into table
        refreshBasicAuthTable();

        return panel;
    }

    private JPanel createCertAuthPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Enable toggle
        JLabel labelEnable = new JLabel(rb.getResourceString("label.certauth.enable"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(labelEnable, gbc);

        toggleCertAuthEnabled = new ToggleSwitch();
        toggleCertAuthEnabled.setSelected(this.certAuthEnabled);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(toggleCertAuthEnabled, gbc);

        // Table
        String[] columnNames = {
                rb.getResourceString("column.enabled"),
                rb.getResourceString("column.certificate")
        };
        tableModelCertAuth = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Only enabled column is editable, certificate selection via combo box
                return column == 0;
            }
        };

        jTableCertAuth = new JTable(tableModelCertAuth);
        jTableCertAuth.setRowHeight(25);

        // Set column widths
        TableColumn enabledCol = jTableCertAuth.getColumnModel().getColumn(0);
        enabledCol.setPreferredWidth(80);
        enabledCol.setMaxWidth(80);

        // Set up certificate column with combo box editor
        TableColumn certCol = jTableCertAuth.getColumnModel().getColumn(1);
        JComboBox<String> certCombo = new JComboBox<>();
        for (KeystoreCertificate cert : availableCerts) {
            certCombo.addItem(cert.getAlias());
        }
        certCol.setCellEditor(new DefaultCellEditor(certCombo));

        JScrollPane scrollPane = new JScrollPane(jTableCertAuth);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd = new JButton(rb.getResourceString("button.add"));
        btnAdd.setEnabled(!availableCerts.isEmpty());
        btnAdd.addActionListener(e -> addCertAuthRow());
        buttonPanel.add(btnAdd);

        JButton btnDelete = new JButton(rb.getResourceString("button.delete"));
        btnDelete.addActionListener(e -> deleteCertAuthRow());
        buttonPanel.add(btnDelete);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        // Load data into table
        refreshCertAuthTable();

        return panel;
    }

    private void refreshBasicAuthTable() {
        tableModelBasicAuth.setRowCount(0);
        for (UserTrackerAuthCredential cred : basicAuthList) {
            tableModelBasicAuth.addRow(new Object[]{
                    cred.isEnabled(),
                    cred.getUsername(),
                    cred.getPassword()
            });
        }
    }

    private void refreshCertAuthTable() {
        tableModelCertAuth.setRowCount(0);
        for (UserTrackerAuthCredential cred : certAuthList) {
            tableModelCertAuth.addRow(new Object[]{
                    cred.isEnabled(),
                    cred.getCertAlias()
            });
        }
    }

    private void addBasicAuthRow() {
        UserTrackerAuthCredential newCred = new UserTrackerAuthCredential();
        newCred.setDbId(-1);
        newCred.setAuthType(UserTrackerAuthCredential.AUTH_TYPE_BASIC);
        newCred.setUsername("");
        newCred.setPassword("");
        newCred.setEnabled(true);
        basicAuthList.add(newCred);
        refreshBasicAuthTable();
    }

    private void deleteBasicAuthRow() {
        int selectedRow = jTableBasicAuth.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < basicAuthList.size()) {
            basicAuthList.remove(selectedRow);
            refreshBasicAuthTable();
        }
    }

    private void addCertAuthRow() {
        if (availableCerts.isEmpty()) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_WARNING,
                    "No certificates available",
                    "Please import certificates first");
            return;
        }

        UserTrackerAuthCredential newCred = new UserTrackerAuthCredential();
        newCred.setDbId(-1);
        newCred.setAuthType(UserTrackerAuthCredential.AUTH_TYPE_CERTIFICATE);
        newCred.setCertAlias(availableCerts.get(0).getAlias());
        newCred.setEnabled(true);
        certAuthList.add(newCred);
        refreshCertAuthTable();
    }

    private void deleteCertAuthRow() {
        int selectedRow = jTableCertAuth.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < certAuthList.size()) {
            certAuthList.remove(selectedRow);
            refreshCertAuthTable();
        }
    }

    private void setupKeyboardShortcuts() {
        // ESC to cancel
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Cmd+W / Ctrl+W to close
        int modifierKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_W, modifierKey),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void jButtonOkActionPerformed() {
        // Update lists from table data
        updateBasicAuthListFromTable();
        updateCertAuthListFromTable();

        // Validate
        if (toggleBasicAuthEnabled.isSelected() && basicAuthList.isEmpty()) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_WARNING,
                    "No basic auth credentials",
                    "Please add at least one username/password or disable basic authentication");
            return;
        }

        if (toggleCertAuthEnabled.isSelected() && certAuthList.isEmpty()) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_WARNING,
                    "No certificate credentials",
                    "Please add at least one certificate or disable certificate authentication");
            return;
        }

        // Save configuration
        if (saveConfiguration()) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_SUCCESS,
                    "Configuration saved",
                    rb.getResourceString("success.saved"));
            dispose();
        }
    }

    private void updateBasicAuthListFromTable() {
        for (int i = 0; i < tableModelBasicAuth.getRowCount() && i < basicAuthList.size(); i++) {
            UserTrackerAuthCredential cred = basicAuthList.get(i);
            cred.setEnabled((Boolean) tableModelBasicAuth.getValueAt(i, 0));
            cred.setUsername((String) tableModelBasicAuth.getValueAt(i, 1));
            cred.setPassword((String) tableModelBasicAuth.getValueAt(i, 2));
        }
    }

    private void updateCertAuthListFromTable() {
        for (int i = 0; i < tableModelCertAuth.getRowCount() && i < certAuthList.size(); i++) {
            UserTrackerAuthCredential cred = certAuthList.get(i);
            cred.setEnabled((Boolean) tableModelCertAuth.getValueAt(i, 0));
            String certAlias = (String) tableModelCertAuth.getValueAt(i, 1);
            cred.setCertAlias(certAlias);

            // Find and set certificate fingerprint
            for (KeystoreCertificate cert : availableCerts) {
                if (cert.getAlias().equals(certAlias)) {
                    try {
                        cred.setCertFingerprint(cert.getFingerPrintSHA1());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private boolean saveConfiguration() {
        try {
            de.mendelson.comm.as2.server.DirectServiceClient serviceClient =
                    de.mendelson.comm.as2.server.DirectServiceClient.getInstance();
            if (serviceClient == null) {
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        "Service not available",
                        "Could not connect to server");
                return false;
            }

            de.mendelson.comm.as2.server.AS2ServerProcessing processing = serviceClient.getServerProcessing();
            if (processing == null) {
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        "Service not available",
                        "Server processing not initialized");
                return false;
            }

            de.mendelson.util.database.IDBDriverManager dbDriverManager = processing.getDBDriverManager();
            if (dbDriverManager == null) {
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        "Database not available",
                        "Could not access database");
                return false;
            }

            Connection connection = null;
            try {
                connection = dbDriverManager.getConnectionWithoutErrorHandling(
                        de.mendelson.util.database.IDBDriverManager.DB_CONFIG);
                connection.setAutoCommit(false);

                UserTrackerAuthDB authDB = new UserTrackerAuthDB();

                // Combine all credentials
                List<UserTrackerAuthCredential> allCredentials = new ArrayList<>();
                allCredentials.addAll(basicAuthList);
                allCredentials.addAll(certAuthList);

                // Save
                authDB.saveCredentials(
                        this.userId,
                        allCredentials,
                        toggleBasicAuthEnabled.isSelected(),
                        toggleCertAuthEnabled.isSelected(),
                        connection
                );

                connection.commit();
                return true;

            } catch (Exception e) {
                if (connection != null) {
                    try {
                        connection.rollback();
                    } catch (Exception ex) {
                        // Ignore rollback error
                    }
                }
                e.printStackTrace();
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        "Failed to save",
                        e.getMessage());
                return false;
            } finally {
                if (connection != null) {
                    try {
                        connection.setAutoCommit(true);
                        connection.close();
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_ERROR,
                    "Failed to save",
                    e.getMessage());
            return false;
        }
    }
}
