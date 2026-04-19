/*
 * Copyright (C) 2026 Julian Xu
 */

package de.mendelson.comm.as2.security.ipwhitelist.gui;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistAccessDB;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistEntry;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistBlockLog;
import de.mendelson.util.database.IDBDriverManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main dialog for IP Whitelist Management
 * Provides tabs for Settings, Global, Partner-Specific, User-Specific, and Block Log
 */
public class JDialogIPWhitelistManagement extends JDialog {

    private final IDBDriverManager dbDriverManager;
    private final IPWhitelistAccessDB accessDB;
    private final PreferencesAS2 preferences;
    private ResourceBundleIPWhitelist rb;

    // Main components
    private JTabbedPane tabbedPane;

    // Settings tab
    private JCheckBox checkAS2;
    private JCheckBox checkTracker;
    private JCheckBox checkWebUI;
    private JCheckBox checkAPI;
    private ButtonGroup modeGroup;
    private JRadioButton radioGlobalOnly;
    private JRadioButton radioPartnerOnly;
    private JRadioButton radioGlobalAndSpecific;
    private JSpinner spinnerRetentionDays;
    private JButton buttonSaveSettings;

    // Global tab
    private JTable tableGlobal;
    private TableModelGlobalWhitelist modelGlobal;
    private JButton buttonGlobalAdd;
    private JButton buttonGlobalEdit;
    private JButton buttonGlobalDelete;
    private JButton buttonGlobalRefresh;

    // Block log tab
    private JTable tableBlockLog;
    private TableModelBlockLog modelBlockLog;
    private JButton buttonBlockLogRefresh;

    public JDialogIPWhitelistManagement(JFrame parent, IDBDriverManager dbDriverManager) {
        super(parent, "IP Whitelist Management", false);
        this.dbDriverManager = dbDriverManager;
        this.accessDB = new IPWhitelistAccessDB(dbDriverManager);
        this.preferences = new PreferencesAS2(dbDriverManager);

        try {
            this.rb = (ResourceBundleIPWhitelist) ResourceBundle.getBundle(
                ResourceBundleIPWhitelist.class.getName());
        } catch (Exception e) {
            this.rb = null;
        }

        initComponents();
        setupKeyboardShortcuts();
        setSize(900, 700);
        setLocationRelativeTo(parent);

        // Load initial data
        loadSettings();
        loadGlobalEntries();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.TAB_SETTINGS) : "Settings",
            createSettingsTab());
        tabbedPane.addTab(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.TAB_GLOBAL) : "Global Whitelist",
            createGlobalTab());
        tabbedPane.addTab(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.TAB_BLOCKLOG) : "Block Log",
            createBlockLogTab());

        // Tab change listener for auto-refresh
        tabbedPane.addChangeListener(this::onTabChanged);

        // Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Close button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton buttonClose = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_CLOSE) : "Close");
        buttonClose.addActionListener(e -> dispose());
        bottomPanel.add(buttonClose);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    // ========== Settings Tab ==========

    private JPanel createSettingsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Enable checkboxes section
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JLabel labelEnable = new JLabel("Enable Whitelist per Endpoint Type:");
        labelEnable.setFont(labelEnable.getFont().deriveFont(Font.BOLD));
        formPanel.add(labelEnable, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(2, 20, 2, 5);

        gbc.gridx = 0; gbc.gridy = row++;
        checkAS2 = new JCheckBox(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_ENABLE_AS2) : "Enable for AS2 Endpoint");
        formPanel.add(checkAS2, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        checkTracker = new JCheckBox(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_ENABLE_TRACKER) : "Enable for Tracker Endpoint");
        formPanel.add(checkTracker, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        checkWebUI = new JCheckBox(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_ENABLE_WEBUI) : "Enable for WebUI Access");
        formPanel.add(checkWebUI, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        checkAPI = new JCheckBox(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_ENABLE_API) : "Enable for API Access");
        formPanel.add(checkAPI, gbc);

        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JLabel labelMode = new JLabel(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_MODE) : "Whitelist Mode:");
        labelMode.setFont(labelMode.getFont().deriveFont(Font.BOLD));
        formPanel.add(labelMode, gbc);

        modeGroup = new ButtonGroup();
        gbc.gridwidth = 1; gbc.insets = new Insets(2, 20, 2, 5);

        gbc.gridx = 0; gbc.gridy = row++;
        radioGlobalOnly = new JRadioButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_MODE_GLOBAL_ONLY) : "Global Only");
        modeGroup.add(radioGlobalOnly);
        formPanel.add(radioGlobalOnly, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        radioPartnerOnly = new JRadioButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_MODE_PARTNER_ONLY) : "Partner/User Specific Only");
        modeGroup.add(radioPartnerOnly);
        formPanel.add(radioPartnerOnly, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        radioGlobalAndSpecific = new JRadioButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_MODE_GLOBAL_AND_SPECIFIC) : "Global + Specific (Recommended)");
        modeGroup.add(radioGlobalAndSpecific);
        radioGlobalAndSpecific.setSelected(true);
        formPanel.add(radioGlobalAndSpecific, gbc);

        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_LOG_RETENTION) : "Block Log Retention (days):"), gbc);
        gbc.gridx = 1;
        spinnerRetentionDays = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        formPanel.add(spinnerRetentionDays, gbc);

        // Save button
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2; gbc.insets = new Insets(20, 5, 5, 5);
        buttonSaveSettings = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_SAVE) : "Save Settings");
        buttonSaveSettings.addActionListener(e -> saveSettings());
        formPanel.add(buttonSaveSettings, gbc);

        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }

    private void loadSettings() {
        Thread loadThread = new Thread(() -> {
            try {
                boolean enabledAS2 = "true".equals(preferences.get(PreferencesAS2.IP_WHITELIST_ENABLED_AS2));
                boolean enabledTracker = "true".equals(preferences.get(PreferencesAS2.IP_WHITELIST_ENABLED_TRACKER));
                boolean enabledWebUI = "true".equals(preferences.get(PreferencesAS2.IP_WHITELIST_ENABLED_WEBUI));
                boolean enabledAPI = "true".equals(preferences.get(PreferencesAS2.IP_WHITELIST_ENABLED_API));
                String mode = preferences.get(PreferencesAS2.IP_WHITELIST_MODE);
                int retentionDays = preferences.getInt(PreferencesAS2.IP_WHITELIST_LOG_RETENTION_DAYS);

                SwingUtilities.invokeLater(() -> {
                    checkAS2.setSelected(enabledAS2);
                    checkTracker.setSelected(enabledTracker);
                    checkWebUI.setSelected(enabledWebUI);
                    checkAPI.setSelected(enabledAPI);
                    spinnerRetentionDays.setValue(retentionDays);

                    if ("GLOBAL_ONLY".equals(mode)) {
                        radioGlobalOnly.setSelected(true);
                    } else if ("PARTNER_ONLY".equals(mode) || "USER_ONLY".equals(mode)) {
                        radioPartnerOnly.setSelected(true);
                    } else {
                        radioGlobalAndSpecific.setSelected(true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private void saveSettings() {
        buttonSaveSettings.setEnabled(false);

        Thread saveThread = new Thread(() -> {
            try {
                preferences.putBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_AS2, checkAS2.isSelected());
                preferences.putBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_TRACKER, checkTracker.isSelected());
                preferences.putBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_WEBUI, checkWebUI.isSelected());
                preferences.putBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_API, checkAPI.isSelected());
                preferences.putInt(PreferencesAS2.IP_WHITELIST_LOG_RETENTION_DAYS, (Integer) spinnerRetentionDays.getValue());

                String mode = radioGlobalOnly.isSelected() ? "GLOBAL_ONLY" :
                             radioPartnerOnly.isSelected() ? "PARTNER_ONLY" :
                             "GLOBAL_AND_SPECIFIC";
                preferences.put(PreferencesAS2.IP_WHITELIST_MODE, mode);

                SwingUtilities.invokeLater(() -> {
                    buttonSaveSettings.setEnabled(true);
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.MSG_SAVE_SUCCESS) : "Settings saved successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    buttonSaveSettings.setEnabled(true);
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        "Failed to save settings: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        saveThread.setDaemon(true);
        saveThread.start();
    }

    // ========== Global Whitelist Tab ==========

    private JPanel createGlobalTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        modelGlobal = new TableModelGlobalWhitelist();
        tableGlobal = new JTable(modelGlobal);
        tableGlobal.setAutoCreateRowSorter(true);
        tableGlobal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableGlobal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateGlobalButtonStates();
            }
        });

        // Double-click to edit
        tableGlobal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tableGlobal.getSelectedRow() != -1) {
                    editGlobalEntry();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableGlobal);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonGlobalAdd = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_ADD) : "Add");
        buttonGlobalEdit = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_EDIT) : "Edit");
        buttonGlobalDelete = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_DELETE) : "Delete");
        buttonGlobalRefresh = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_REFRESH) : "Refresh");

        buttonGlobalAdd.addActionListener(e -> addGlobalEntry());
        buttonGlobalEdit.addActionListener(e -> editGlobalEntry());
        buttonGlobalDelete.addActionListener(e -> deleteGlobalEntry());
        buttonGlobalRefresh.addActionListener(e -> loadGlobalEntries());

        buttonPanel.add(buttonGlobalAdd);
        buttonPanel.add(buttonGlobalEdit);
        buttonPanel.add(buttonGlobalDelete);
        buttonPanel.add(buttonGlobalRefresh);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        updateGlobalButtonStates();
        return panel;
    }

    private void loadGlobalEntries() {
        buttonGlobalRefresh.setEnabled(false);

        Thread loadThread = new Thread(() -> {
            try {
                List<IPWhitelistEntry> entries = accessDB.getGlobalWhitelist();
                SwingUtilities.invokeLater(() -> {
                    modelGlobal.setData(entries);
                    buttonGlobalRefresh.setEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    buttonGlobalRefresh.setEnabled(true);
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        "Failed to load entries: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private void addGlobalEntry() {
        JDialogEditIPWhitelist dialog = new JDialogEditIPWhitelist(
            (JFrame) getOwner(), dbDriverManager,
            JDialogEditIPWhitelist.TYPE_GLOBAL, null, null);
        dialog.setVisible(true);

        if (dialog.wasOkPressed()) {
            loadGlobalEntries();
        }
    }

    private void editGlobalEntry() {
        int selectedRow = tableGlobal.getSelectedRow();
        if (selectedRow == -1) return;

        int modelRow = tableGlobal.convertRowIndexToModel(selectedRow);
        IPWhitelistEntry entry = modelGlobal.getEntryAt(modelRow);
        if (entry == null) return;

        JDialogEditIPWhitelist dialog = new JDialogEditIPWhitelist(
            (JFrame) getOwner(), dbDriverManager, entry,
            JDialogEditIPWhitelist.TYPE_GLOBAL, null, null);
        dialog.setVisible(true);

        if (dialog.wasOkPressed()) {
            loadGlobalEntries();
        }
    }

    private void deleteGlobalEntry() {
        int selectedRow = tableGlobal.getSelectedRow();
        if (selectedRow == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.MSG_DELETE_CONFIRM) : "Are you sure you want to delete this entry?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int modelRow = tableGlobal.convertRowIndexToModel(selectedRow);
        IPWhitelistEntry entry = modelGlobal.getEntryAt(modelRow);
        if (entry == null) return;

        Thread deleteThread = new Thread(() -> {
            try {
                accessDB.deleteGlobalWhitelist(entry.getId());
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.MSG_DELETE_SUCCESS) : "Entry deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadGlobalEntries();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        "Failed to delete: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        deleteThread.setDaemon(true);
        deleteThread.start();
    }

    private void updateGlobalButtonStates() {
        boolean hasSelection = tableGlobal.getSelectedRow() != -1;
        buttonGlobalEdit.setEnabled(hasSelection);
        buttonGlobalDelete.setEnabled(hasSelection);
    }

    // ========== Block Log Tab ==========

    private JPanel createBlockLogTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        modelBlockLog = new TableModelBlockLog();
        tableBlockLog = new JTable(modelBlockLog);
        tableBlockLog.setAutoCreateRowSorter(true);
        tableBlockLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableBlockLog);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBlockLogRefresh = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_REFRESH) : "Refresh");
        buttonBlockLogRefresh.addActionListener(e -> loadBlockLog());
        buttonPanel.add(buttonBlockLogRefresh);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadBlockLog() {
        buttonBlockLogRefresh.setEnabled(false);

        Thread loadThread = new Thread(() -> {
            try {
                // Get block log for last 30 days
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
                java.util.Date startDate = cal.getTime();
                java.util.Date endDate = new java.util.Date();

                List<IPWhitelistBlockLog> logEntries = accessDB.getBlockLog(startDate, endDate, null);
                SwingUtilities.invokeLater(() -> {
                    modelBlockLog.setData(logEntries);
                    buttonBlockLogRefresh.setEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    buttonBlockLogRefresh.setEnabled(true);
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        "Failed to load block log: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    // ========== Tab Change Handler ==========

    private void onTabChanged(ChangeEvent e) {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == 1) {
            loadGlobalEntries();
        } else if (selectedIndex == 2) {
            loadBlockLog();
        }
    }

    // ========== Keyboard Shortcuts ==========

    private void setupKeyboardShortcuts() {
        JRootPane rootPane = getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // ESC - Close
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        actionMap.put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        int modifierKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        // Cmd/Ctrl+W - Close
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, modifierKey), "close");

        // Cmd/Ctrl+N - Add (only on Global tab)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, modifierKey), "add");
        actionMap.put("add", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabbedPane.getSelectedIndex() == 1 && buttonGlobalAdd.isEnabled()) {
                    addGlobalEntry();
                }
            }
        });

        // Cmd/Ctrl+R - Refresh
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, modifierKey), "refresh");
        actionMap.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tab = tabbedPane.getSelectedIndex();
                if (tab == 1) {
                    loadGlobalEntries();
                } else if (tab == 2) {
                    loadBlockLog();
                }
            }
        });

        // Delete key - Delete entry
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabbedPane.getSelectedIndex() == 1 && buttonGlobalDelete.isEnabled()) {
                    deleteGlobalEntry();
                }
            }
        });
    }
}
