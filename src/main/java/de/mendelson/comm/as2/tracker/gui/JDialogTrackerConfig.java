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

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.toggleswitch.ToggleSwitch;
import de.mendelson.util.uinotification.UINotification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Dialog for configuring tracker endpoint settings
 *
 * @author Julian Xu
 */
public class JDialogTrackerConfig extends JDialog {

    private MecResourceBundle rb;
    private PreferencesAS2 preferences;
    private BaseClient baseClient;
    private boolean isAdmin;

    // UI Components
    private ToggleSwitch toggleEnabled;
    private ToggleSwitch toggleAuthRequired;
    private JTextField jTextFieldMaxSize;
    private JTextField jTextFieldRateLimitFailures;
    private JTextField jTextFieldRateLimitWindow;
    private JTextField jTextFieldRateLimitBlock;
    private JButton jButtonOk;
    private JButton jButtonCancel;

    public JDialogTrackerConfig(JFrame parent, BaseClient baseClient) {
        super(parent, true);
        this.baseClient = baseClient;

                this.isAdmin = checkUserIsAdmin();
        
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogTrackerConfig.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Resource bundle not found: " + e.getClassName());
        }

        try {
            this.preferences = new PreferencesAS2(null);
        } catch (Exception e) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_ERROR,
                    "Failed to load preferences",
                    e.getMessage());
            this.dispose();
            return;
        }

                initComponents();
                loadSettings();
                this.getRootPane().setDefaultButton(this.jButtonOk);
    }

    /**
     * Check if current user has USER_MANAGE permission (admin role)
     */
    private boolean checkUserIsAdmin() {
        try {
            String username = this.baseClient.getUsername();
            
            if (username == null || username.isEmpty()) {
                                return false;
            }

            // Get AS2ServerProcessing from DirectServiceClient
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

            // Create UserManagementAccessDB with proper DBDriverManager
            de.mendelson.comm.as2.usermanagement.UserManagementAccessDB userDB =
                new de.mendelson.comm.as2.usermanagement.UserManagementAccessDB(
                    dbDriverManager,
                    java.util.logging.Logger.getLogger("TrackerConfig"));

            // Get user permissions (returns Set<String>)
            java.util.Set<String> permissions = userDB.getUserPermissions(username);
            
            // Check if user has USER_MANAGE permission (admin role)
            boolean isAdmin = permissions != null && permissions.contains("USER_MANAGE");
                        return isAdmin;
        } catch (Exception e) {
            // If error checking permissions, assume not admin
                        e.printStackTrace();
            return false;
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(rb.getResourceString("title"));
        setResizable(false);

        JPanel jPanelMain = new JPanel();
        jPanelMain.setLayout(new GridBagLayout());
        jPanelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Title label
        JLabel jLabelTitle = new JLabel(rb.getResourceString("title"));
        jLabelTitle.setFont(jLabelTitle.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 10, 5);
        jPanelMain.add(jLabelTitle, gbc);

        // Tracker URL info panel (always visible)
        JPanel urlInfoPanel = createTrackerUrlInfoPanel();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 5, 15, 5);
        gbc.fill = GridBagConstraints.BOTH;
        jPanelMain.add(urlInfoPanel, gbc);

        
        // Only show configuration fields for admin users
        if (this.isAdmin) {
                        gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Enable tracker
            JLabel jLabelEnabled = new JLabel(rb.getResourceString("label.enabled"));
            gbc.gridx = 0;
            gbc.gridy = row;
            jPanelMain.add(jLabelEnabled, gbc);

            toggleEnabled = new ToggleSwitch();
            gbc.gridx = 1;
            gbc.gridy = row++;
            jPanelMain.add(toggleEnabled, gbc);

            // Require authentication
            JLabel jLabelAuthRequired = new JLabel(rb.getResourceString("label.auth.required"));
            gbc.gridx = 0;
            gbc.gridy = row;
            jPanelMain.add(jLabelAuthRequired, gbc);

            toggleAuthRequired = new ToggleSwitch();
            gbc.gridx = 1;
            gbc.gridy = row++;
            jPanelMain.add(toggleAuthRequired, gbc);

            // Separator
            JSeparator separator1 = new JSeparator();
            gbc.gridx = 0;
            gbc.gridy = row++;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(15, 5, 15, 5);
            jPanelMain.add(separator1, gbc);

            gbc.gridwidth = 1;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Max message size
            JLabel jLabelMaxSize = new JLabel(rb.getResourceString("label.maxsize"));
            gbc.gridx = 0;
            gbc.gridy = row;
            jPanelMain.add(jLabelMaxSize, gbc);

            jTextFieldMaxSize = new JTextField(10);
            gbc.gridx = 1;
            gbc.gridy = row++;
            jPanelMain.add(jTextFieldMaxSize, gbc);

            // Rate limit section label
            JLabel jLabelRateLimit = new JLabel(rb.getResourceString("label.ratelimit.title"));
            jLabelRateLimit.setFont(jLabelRateLimit.getFont().deriveFont(Font.BOLD));
            gbc.gridx = 0;
            gbc.gridy = row++;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(15, 5, 5, 5);
            jPanelMain.add(jLabelRateLimit, gbc);

            gbc.gridwidth = 1;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Max failures
            JLabel jLabelRateLimitFailures = new JLabel(rb.getResourceString("label.ratelimit.failures"));
            gbc.gridx = 0;
            gbc.gridy = row;
            jPanelMain.add(jLabelRateLimitFailures, gbc);

            jTextFieldRateLimitFailures = new JTextField(10);
            gbc.gridx = 1;
            gbc.gridy = row++;
            jPanelMain.add(jTextFieldRateLimitFailures, gbc);

            // Time window
            JLabel jLabelRateLimitWindow = new JLabel(rb.getResourceString("label.ratelimit.window"));
            gbc.gridx = 0;
            gbc.gridy = row;
            jPanelMain.add(jLabelRateLimitWindow, gbc);

            jTextFieldRateLimitWindow = new JTextField(10);
            gbc.gridx = 1;
            gbc.gridy = row++;
            jPanelMain.add(jTextFieldRateLimitWindow, gbc);

            // Block duration
            JLabel jLabelRateLimitBlock = new JLabel(rb.getResourceString("label.ratelimit.block"));
            gbc.gridx = 0;
            gbc.gridy = row;
            jPanelMain.add(jLabelRateLimitBlock, gbc);

            jTextFieldRateLimitBlock = new JTextField(10);
            gbc.gridx = 1;
            gbc.gridy = row++;
            jPanelMain.add(jTextFieldRateLimitBlock, gbc);

                    } else {
                    }

        // Button panel
        JPanel jPanelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        jButtonOk = new JButton(rb.getResourceString("button.ok"));
        jButtonOk.addActionListener(evt -> jButtonOkActionPerformed());
        jPanelButtons.add(jButtonOk);

        // Only show Cancel button for admin users (non-admins just have OK to close)
        if (this.isAdmin) {
            jButtonCancel = new JButton(rb.getResourceString("button.cancel"));
            jButtonCancel.addActionListener(evt -> dispose());
            jPanelButtons.add(jButtonCancel);
        }

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        jPanelMain.add(jPanelButtons, gbc);

        getContentPane().add(jPanelMain);

        // Add keyboard shortcuts
        setupKeyboardShortcuts();

        pack();
        setLocationRelativeTo(getParent());
    }

    private void setupKeyboardShortcuts() {
        // ESC to cancel/close
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

    private JPanel createTrackerUrlInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(245, 245, 245));

        // Get username from BaseClient
        String username = this.baseClient.getUsername();
        if (username == null || username.isEmpty()) {
            username = "admin";
        }

        // Get HTTP server info to build tracker URL
        String trackerUrl = getTrackerUrl(username);

        // Info icon and text
        JLabel infoIcon = new JLabel("ℹ️");
        infoIcon.setFont(infoIcon.getFont().deriveFont(16f));
        infoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel(rb.getResourceString("label.tracker.url.info"));
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

    private String getTrackerUrl(String username) {
        // Build tracker URL using actual server configuration (handles test mode)
        // Format: {protocol}://{host}:{port}/as2/tracker/{username}

        // Get hostname using helper
        String host = de.mendelson.comm.as2.server.ServerConfigurationHelper.getHostname();

        String protocol = "https";
        int port = 8443;

        // Get actual listening ports from HTTP server config
        try {
            de.mendelson.comm.as2.server.AS2Server server =
                de.mendelson.comm.as2.server.AS2Server.getStaticServerReference();

            if (server != null) {
                de.mendelson.util.httpconfig.server.HTTPServerConfigInfo configInfo =
                    server.getHTTPServerConfigInfo();

                if (configInfo != null) {
                    // Get HTTPS port first using helper
                    Integer httpsPort = de.mendelson.comm.as2.server.ServerConfigurationHelper.getHttpsPort(configInfo);
                    if (httpsPort != null) {
                        port = httpsPort;
                        protocol = "https";
                    } else {
                        // Try HTTP port using helper
                        Integer httpPort = de.mendelson.comm.as2.server.ServerConfigurationHelper.getHttpPort(configInfo);
                        if (httpPort != null) {
                            port = httpPort;
                            protocol = "http";
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Use defaults if we can't get actual config
            System.err.println("Failed to get actual HTTP server config: " + e.getMessage());
        }

        return protocol + "://" + host + ":" + port + "/as2/tracker/" + username;
    }

    private void loadSettings() {
        // Only load settings into fields for admin users
        if (this.isAdmin) {
            toggleEnabled.setSelected("true".equals(preferences.get(PreferencesAS2.TRACKER_ENABLED)));
            toggleAuthRequired.setSelected("true".equals(preferences.get(PreferencesAS2.TRACKER_AUTH_REQUIRED)));
            jTextFieldMaxSize.setText(preferences.get(PreferencesAS2.TRACKER_MAX_SIZE_MB));
            jTextFieldRateLimitFailures.setText(preferences.get(PreferencesAS2.TRACKER_RATE_LIMIT_FAILURES));
            jTextFieldRateLimitWindow.setText(preferences.get(PreferencesAS2.TRACKER_RATE_LIMIT_WINDOW_HOURS));
            jTextFieldRateLimitBlock.setText(preferences.get(PreferencesAS2.TRACKER_RATE_LIMIT_BLOCK_MINUTES));
        }
        // Non-admin users only see the URL panel, no settings to load
    }

    private void jButtonOkActionPerformed() {
        // For non-admin users, OK button just closes the dialog
        if (!this.isAdmin) {
            dispose();
            return;
        }

        // For admin users, validate and save settings
        // Validate inputs
        try {
            int maxSize = Integer.parseInt(jTextFieldMaxSize.getText().trim());
            if (maxSize <= 0 || maxSize > 100) {
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        "Invalid max size",
                        rb.getResourceString("error.maxsize"));
                return;
            }

            int failures = Integer.parseInt(jTextFieldRateLimitFailures.getText().trim());
            if (failures <= 0 || failures > 100) {
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        "Invalid failure count",
                        rb.getResourceString("error.failures"));
                return;
            }

            int window = Integer.parseInt(jTextFieldRateLimitWindow.getText().trim());
            if (window <= 0 || window > 24) {
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        "Invalid time window",
                        rb.getResourceString("error.window"));
                return;
            }

            int block = Integer.parseInt(jTextFieldRateLimitBlock.getText().trim());
            if (block <= 0 || block > 1440) {
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        "Invalid block duration",
                        rb.getResourceString("error.block"));
                return;
            }
        } catch (NumberFormatException e) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_ERROR,
                    "Invalid number",
                    rb.getResourceString("error.invalid.number"));
            return;
        }

        // Save settings
        preferences.put(PreferencesAS2.TRACKER_ENABLED,
                String.valueOf(toggleEnabled.isSelected()));
        preferences.put(PreferencesAS2.TRACKER_AUTH_REQUIRED,
                String.valueOf(toggleAuthRequired.isSelected()));
        preferences.put(PreferencesAS2.TRACKER_MAX_SIZE_MB,
                jTextFieldMaxSize.getText().trim());
        preferences.put(PreferencesAS2.TRACKER_RATE_LIMIT_FAILURES,
                jTextFieldRateLimitFailures.getText().trim());
        preferences.put(PreferencesAS2.TRACKER_RATE_LIMIT_WINDOW_HOURS,
                jTextFieldRateLimitWindow.getText().trim());
        preferences.put(PreferencesAS2.TRACKER_RATE_LIMIT_BLOCK_MINUTES,
                jTextFieldRateLimitBlock.getText().trim());

        UINotification.instance().addNotification(
                null,
                UINotification.TYPE_SUCCESS,
                "Configuration saved",
                rb.getResourceString("success.saved"));
        dispose();
    }
}
