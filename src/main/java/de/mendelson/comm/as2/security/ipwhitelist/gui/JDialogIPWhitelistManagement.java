/*
 * Copyright (C) 2026 Julian Xu
 */

package de.mendelson.comm.as2.security.ipwhitelist.gui;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistAccessDB;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistEntry;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistBlockLog;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.util.database.IDBDriverManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Main dialog for IP Whitelist Management
 * Provides tabs for Settings, Global, Partner-Specific, User-Specific, and Block Log
 */
public class JDialogIPWhitelistManagement extends JDialog {

    private final IDBDriverManager dbDriverManager;
    private final IPWhitelistAccessDB accessDB;
    private final PreferencesAS2 preferences;
    private final Logger logger;
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

    // Partner tab
    private JComboBox<String> comboPartner;
    private JTable tablePartner;
    private TableModelPartnerWhitelist modelPartner;
    private JButton buttonPartnerAdd;
    private JButton buttonPartnerEdit;
    private JButton buttonPartnerDelete;
    private JButton buttonPartnerRefresh;

    // User tab
    private JComboBox<String> comboUser;
    private JTable tableUser;
    private TableModelUserWhitelist modelUser;
    private JButton buttonUserAdd;
    private JButton buttonUserEdit;
    private JButton buttonUserDelete;
    private JButton buttonUserRefresh;

    // Block log tab
    private JTable tableBlockLog;
    private TableModelBlockLog modelBlockLog;
    private JButton buttonBlockLogRefresh;

    // Data mappings
    private Map<String, Integer> partnerNameToId = new HashMap<>();
    private Map<String, Integer> userNameToId = new HashMap<>();

    public JDialogIPWhitelistManagement(JFrame parent, IDBDriverManager dbDriverManager) {
        super(parent, "IP Whitelist Management", false);
        this.dbDriverManager = dbDriverManager;
        this.accessDB = new IPWhitelistAccessDB(dbDriverManager);
        this.preferences = new PreferencesAS2(dbDriverManager);
        this.logger = Logger.getLogger("de.mendelson.as2.client");

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
        loadPartnerList();
        loadUserList();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.TAB_SETTINGS) : "Settings",
            createSettingsTab());
        tabbedPane.addTab(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.TAB_GLOBAL) : "Global Whitelist",
            createGlobalTab());
        tabbedPane.addTab(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.TAB_PARTNER) : "Partner-Specific",
            createPartnerTab());
        tabbedPane.addTab(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.TAB_USER) : "User-Specific",
            createUserTab());
        tabbedPane.addTab(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.TAB_BLOCKLOG) : "Block Log",
            createBlockLogTab());

        // Tab change listener for auto-refresh
        tabbedPane.addChangeListener(this::onTabChanged);

        // Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel with Save and Close buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonSaveSettings = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_SAVE) : "Save");
        buttonSaveSettings.addActionListener(e -> saveSettings());
        bottomPanel.add(buttonSaveSettings);

        JButton buttonClose = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_CLOSE) : "Close");
        buttonClose.addActionListener(e -> dispose());
        bottomPanel.add(buttonClose);

        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    // ========== Settings Tab ==========

    private JPanel createSettingsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Enable checkboxes section
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JLabel labelEnable = new JLabel("Enable Whitelist per Endpoint Type:");
        labelEnable.setFont(labelEnable.getFont().deriveFont(Font.BOLD));
        panel.add(labelEnable, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(2, 20, 2, 5);

        gbc.gridx = 0; gbc.gridy = row++;
        checkAS2 = new JCheckBox(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_ENABLE_AS2) : "Enable for AS2 Endpoint");
        panel.add(checkAS2, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        checkTracker = new JCheckBox(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_ENABLE_TRACKER) : "Enable for Tracker Endpoint");
        panel.add(checkTracker, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        checkWebUI = new JCheckBox(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_ENABLE_WEBUI) : "Enable for WebUI Access");
        panel.add(checkWebUI, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        checkAPI = new JCheckBox(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_ENABLE_API) : "Enable for API Access");
        panel.add(checkAPI, gbc);

        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JLabel labelMode = new JLabel(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_MODE) : "Whitelist Mode:");
        labelMode.setFont(labelMode.getFont().deriveFont(Font.BOLD));
        panel.add(labelMode, gbc);

        modeGroup = new ButtonGroup();
        gbc.gridwidth = 1; gbc.insets = new Insets(2, 20, 2, 5);

        gbc.gridx = 0; gbc.gridy = row++;
        radioGlobalOnly = new JRadioButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_MODE_GLOBAL_ONLY) : "Global Only");
        modeGroup.add(radioGlobalOnly);
        panel.add(radioGlobalOnly, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        radioPartnerOnly = new JRadioButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_MODE_PARTNER_ONLY) : "Partner/User Specific Only");
        modeGroup.add(radioPartnerOnly);
        panel.add(radioPartnerOnly, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        radioGlobalAndSpecific = new JRadioButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.SETTINGS_MODE_GLOBAL_AND_SPECIFIC) : "Global + Specific (Recommended)");
        modeGroup.add(radioGlobalAndSpecific);
        radioGlobalAndSpecific.setSelected(true);
        panel.add(radioGlobalAndSpecific, gbc);

        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel("Retention (days):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE; // Don't stretch horizontally
        spinnerRetentionDays = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        spinnerRetentionDays.setPreferredSize(new Dimension(80, spinnerRetentionDays.getPreferredSize().height));
        panel.add(spinnerRetentionDays, gbc);

        // Add vertical spacer to push everything to top (like PreferencesPanel pattern)
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel spacer = new JPanel();
        panel.add(spacer, gbc);

        return panel;
    }

    private void loadSettings() {
        Thread loadThread = new Thread(() -> {
            try {
                boolean enabledAS2 = preferences.getBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_AS2);
                boolean enabledTracker = preferences.getBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_TRACKER);
                boolean enabledWebUI = preferences.getBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_WEBUI);
                boolean enabledAPI = preferences.getBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_API);
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

    // ========== Partner-Specific Tab ==========

    private JPanel createPartnerTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with partner selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.LABEL_SELECT_PARTNER) : "Select Partner:"));
        comboPartner = new JComboBox<>();
        comboPartner.addActionListener(e -> loadPartnerEntries());
        topPanel.add(comboPartner);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        modelPartner = new TableModelPartnerWhitelist();
        tablePartner = new JTable(modelPartner);
        tablePartner.setAutoCreateRowSorter(true);
        tablePartner.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePartner.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePartnerButtonStates();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablePartner);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPartnerAdd = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_ADD) : "Add");
        buttonPartnerAdd.addActionListener(e -> addPartnerEntry());
        buttonPanel.add(buttonPartnerAdd);

        buttonPartnerEdit = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_EDIT) : "Edit");
        buttonPartnerEdit.setEnabled(false);
        buttonPartnerEdit.addActionListener(e -> editPartnerEntry());
        buttonPanel.add(buttonPartnerEdit);

        buttonPartnerDelete = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_DELETE) : "Delete");
        buttonPartnerDelete.setEnabled(false);
        buttonPartnerDelete.addActionListener(e -> deletePartnerEntry());
        buttonPanel.add(buttonPartnerDelete);

        buttonPartnerRefresh = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_REFRESH) : "Refresh");
        buttonPartnerRefresh.addActionListener(e -> loadPartnerEntries());
        buttonPanel.add(buttonPartnerRefresh);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPartnerList() {
        Thread loadThread = new Thread(() -> {
            try {
                // Load users first to create userId -> username mapping
                UserManagementAccessDB userAccess = new UserManagementAccessDB(dbDriverManager, logger);
                List<WebUIUser> users = userAccess.getAllUsers();
                Map<Integer, String> userIdToName = new HashMap<>();
                for (WebUIUser user : users) {
                    userIdToName.put(user.getId(), user.getUsername());
                }

                // Load partners
                PartnerAccessDB partnerAccess = new PartnerAccessDB(dbDriverManager);
                List<Partner> partners = partnerAccess.getAllPartner();

                SwingUtilities.invokeLater(() -> {
                    comboPartner.removeAllItems();
                    partnerNameToId.clear();

                    for (Partner partner : partners) {
                        int userId = partner.getCreatedByUserId();
                        String username = userIdToName.getOrDefault(userId, "unknown");
                        String displayName = username + ":" + partner.getName();

                        comboPartner.addItem(displayName);
                        partnerNameToId.put(displayName, partner.getDBId());
                    }

                    if (comboPartner.getItemCount() > 0) {
                        comboPartner.setSelectedIndex(0);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        "Failed to load partners: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private void loadPartnerEntries() {
        String selectedPartner = (String) comboPartner.getSelectedItem();
        if (selectedPartner == null) return;

        Integer partnerId = partnerNameToId.get(selectedPartner);
        if (partnerId == null) return;

        buttonPartnerRefresh.setEnabled(false);

        Thread loadThread = new Thread(() -> {
            try {
                List<IPWhitelistEntry> entries = accessDB.getPartnerWhitelist(partnerId);
                SwingUtilities.invokeLater(() -> {
                    modelPartner.setData(entries);
                    buttonPartnerRefresh.setEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    buttonPartnerRefresh.setEnabled(true);
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        "Failed to load partner whitelist: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private void addPartnerEntry() {
        String selectedPartner = (String) comboPartner.getSelectedItem();
        if (selectedPartner == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a partner first",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer partnerId = partnerNameToId.get(selectedPartner);
        if (partnerId == null) return;

        // Show input dialog
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("IP Pattern:"), gbc);
        gbc.gridx = 1;
        JTextField textIP = new JTextField(20);
        panel.add(textIP, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextField textDesc = new JTextField(20);
        panel.add(textDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JCheckBox checkEnabled = new JCheckBox("Enabled", true);
        panel.add(checkEnabled, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Partner Whitelist Entry",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String ipPattern = textIP.getText().trim();
            String description = textDesc.getText().trim();

            if (ipPattern.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "IP Pattern is required",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Thread addThread = new Thread(() -> {
                try {
                    IPWhitelistEntry entry = new IPWhitelistEntry();
                    entry.setIpPattern(ipPattern);
                    entry.setDescription(description);
                    entry.setEnabled(checkEnabled.isSelected());

                    accessDB.addPartnerWhitelist(partnerId, entry);

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Entry added successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadPartnerEntries();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Failed to add entry: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
            addThread.setDaemon(true);
            addThread.start();
        }
    }

    private void editPartnerEntry() {
        int selectedRow = tablePartner.getSelectedRow();
        if (selectedRow < 0) return;

        int modelRow = tablePartner.convertRowIndexToModel(selectedRow);
        IPWhitelistEntry entry = modelPartner.getEntryAt(modelRow);
        if (entry == null) return;

        // Show edit dialog with current values
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("IP Pattern:"), gbc);
        gbc.gridx = 1;
        JTextField textIP = new JTextField(entry.getIpPattern(), 20);
        panel.add(textIP, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextField textDesc = new JTextField(entry.getDescription() != null ? entry.getDescription() : "", 20);
        panel.add(textDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JCheckBox checkEnabled = new JCheckBox("Enabled", entry.isEnabled());
        panel.add(checkEnabled, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Partner Whitelist Entry",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String ipPattern = textIP.getText().trim();
            String description = textDesc.getText().trim();

            if (ipPattern.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "IP Pattern is required",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Thread updateThread = new Thread(() -> {
                try {
                    entry.setIpPattern(ipPattern);
                    entry.setDescription(description);
                    entry.setEnabled(checkEnabled.isSelected());

                    accessDB.updatePartnerWhitelist(entry);

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Entry updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadPartnerEntries();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Failed to update entry: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
            updateThread.setDaemon(true);
            updateThread.start();
        }
    }

    private void deletePartnerEntry() {
        int selectedRow = tablePartner.getSelectedRow();
        if (selectedRow < 0) return;

        int modelRow = tablePartner.convertRowIndexToModel(selectedRow);
        IPWhitelistEntry entry = modelPartner.getEntryAt(modelRow);
        if (entry == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this entry?\n\n" +
            "IP Pattern: " + entry.getIpPattern() +
            "\nDescription: " + entry.getDescription(),
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Thread deleteThread = new Thread(() -> {
                try {
                    accessDB.deletePartnerWhitelist(entry.getId());

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Entry deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadPartnerEntries();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Failed to delete entry: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
            deleteThread.setDaemon(true);
            deleteThread.start();
        }
    }

    private void updatePartnerButtonStates() {
        boolean hasSelection = tablePartner.getSelectedRow() != -1;
        buttonPartnerEdit.setEnabled(hasSelection);
        buttonPartnerDelete.setEnabled(hasSelection);
    }

    // ========== User-Specific Tab ==========

    private JPanel createUserTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with user selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.LABEL_SELECT_USER) : "Select User:"));
        comboUser = new JComboBox<>();
        comboUser.addActionListener(e -> loadUserEntries());
        topPanel.add(comboUser);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        modelUser = new TableModelUserWhitelist();
        tableUser = new JTable(modelUser);
        tableUser.setAutoCreateRowSorter(true);
        tableUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUser.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateUserButtonStates();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableUser);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonUserAdd = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_ADD) : "Add");
        buttonUserAdd.addActionListener(e -> addUserEntry());
        buttonPanel.add(buttonUserAdd);

        buttonUserEdit = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_EDIT) : "Edit");
        buttonUserEdit.setEnabled(false);
        buttonUserEdit.addActionListener(e -> editUserEntry());
        buttonPanel.add(buttonUserEdit);

        buttonUserDelete = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_DELETE) : "Delete");
        buttonUserDelete.setEnabled(false);
        buttonUserDelete.addActionListener(e -> deleteUserEntry());
        buttonPanel.add(buttonUserDelete);

        buttonUserRefresh = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_REFRESH) : "Refresh");
        buttonUserRefresh.addActionListener(e -> loadUserEntries());
        buttonPanel.add(buttonUserRefresh);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUserList() {
        Thread loadThread = new Thread(() -> {
            try {
                UserManagementAccessDB userAccess = new UserManagementAccessDB(dbDriverManager, logger);
                List<WebUIUser> users = userAccess.getAllUsers();

                SwingUtilities.invokeLater(() -> {
                    comboUser.removeAllItems();
                    userNameToId.clear();

                    for (WebUIUser user : users) {
                        String username = user.getUsername();
                        comboUser.addItem(username);
                        userNameToId.put(username, user.getId());
                    }

                    if (comboUser.getItemCount() > 0) {
                        comboUser.setSelectedIndex(0);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        "Failed to load users: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private void loadUserEntries() {
        String selectedUser = (String) comboUser.getSelectedItem();
        if (selectedUser == null) return;

        Integer userId = userNameToId.get(selectedUser);
        if (userId == null) return;

        buttonUserRefresh.setEnabled(false);

        Thread loadThread = new Thread(() -> {
            try {
                List<IPWhitelistEntry> entries = accessDB.getUserWhitelist(userId);
                SwingUtilities.invokeLater(() -> {
                    modelUser.setData(entries);
                    buttonUserRefresh.setEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    buttonUserRefresh.setEnabled(true);
                    JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                        "Failed to load user whitelist: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private void addUserEntry() {
        String selectedUser = (String) comboUser.getSelectedItem();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a user first",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer userId = userNameToId.get(selectedUser);
        if (userId == null) return;

        // Show input dialog
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("IP Pattern:"), gbc);
        gbc.gridx = 1;
        JTextField textIP = new JTextField(20);
        panel.add(textIP, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextField textDesc = new JTextField(20);
        panel.add(textDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JCheckBox checkEnabled = new JCheckBox("Enabled", true);
        panel.add(checkEnabled, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add User Whitelist Entry",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String ipPattern = textIP.getText().trim();
            String description = textDesc.getText().trim();

            if (ipPattern.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "IP Pattern is required",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Thread addThread = new Thread(() -> {
                try {
                    IPWhitelistEntry entry = new IPWhitelistEntry();
                    entry.setIpPattern(ipPattern);
                    entry.setDescription(description);
                    entry.setEnabled(checkEnabled.isSelected());

                    accessDB.addUserWhitelist(userId, entry);

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Entry added successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadUserEntries();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Failed to add entry: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
            addThread.setDaemon(true);
            addThread.start();
        }
    }

    private void editUserEntry() {
        int selectedRow = tableUser.getSelectedRow();
        if (selectedRow < 0) return;

        int modelRow = tableUser.convertRowIndexToModel(selectedRow);
        IPWhitelistEntry entry = modelUser.getEntryAt(modelRow);
        if (entry == null) return;

        // Show edit dialog with current values
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("IP Pattern:"), gbc);
        gbc.gridx = 1;
        JTextField textIP = new JTextField(entry.getIpPattern(), 20);
        panel.add(textIP, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextField textDesc = new JTextField(entry.getDescription() != null ? entry.getDescription() : "", 20);
        panel.add(textDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JCheckBox checkEnabled = new JCheckBox("Enabled", entry.isEnabled());
        panel.add(checkEnabled, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit User Whitelist Entry",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String ipPattern = textIP.getText().trim();
            String description = textDesc.getText().trim();

            if (ipPattern.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "IP Pattern is required",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Thread updateThread = new Thread(() -> {
                try {
                    entry.setIpPattern(ipPattern);
                    entry.setDescription(description);
                    entry.setEnabled(checkEnabled.isSelected());

                    accessDB.updateUserWhitelist(entry);

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Entry updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadUserEntries();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Failed to update entry: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
            updateThread.setDaemon(true);
            updateThread.start();
        }
    }

    private void deleteUserEntry() {
        int selectedRow = tableUser.getSelectedRow();
        if (selectedRow < 0) return;

        int modelRow = tableUser.convertRowIndexToModel(selectedRow);
        IPWhitelistEntry entry = modelUser.getEntryAt(modelRow);
        if (entry == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this entry?\n\n" +
            "IP Pattern: " + entry.getIpPattern() +
            "\nDescription: " + entry.getDescription(),
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Thread deleteThread = new Thread(() -> {
                try {
                    accessDB.deleteUserWhitelist(entry.getId());

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Entry deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadUserEntries();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JDialogIPWhitelistManagement.this,
                            "Failed to delete entry: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
            deleteThread.setDaemon(true);
            deleteThread.start();
        }
    }

    private void updateUserButtonStates() {
        boolean hasSelection = tableUser.getSelectedRow() != -1;
        buttonUserEdit.setEnabled(hasSelection);
        buttonUserDelete.setEnabled(hasSelection);
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
            // Global Whitelist tab
            loadGlobalEntries();
        } else if (selectedIndex == 2) {
            // Partner-Specific tab
            loadPartnerList();
            loadPartnerEntries();
        } else if (selectedIndex == 3) {
            // User-Specific tab
            loadUserList();
            loadUserEntries();
        } else if (selectedIndex == 4) {
            // Block Log tab
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
