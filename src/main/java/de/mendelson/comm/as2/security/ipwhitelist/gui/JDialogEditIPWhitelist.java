/*
 * Copyright (C) 2026 Julian Xu
 */

package de.mendelson.comm.as2.security.ipwhitelist.gui;

import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistAccessDB;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistEntry;
import de.mendelson.util.database.IDBDriverManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;

/**
 * Modal dialog for adding or editing IP whitelist entries
 */
public class JDialogEditIPWhitelist extends JDialog {

    public static final int TYPE_GLOBAL = 1;
    public static final int TYPE_PARTNER = 2;
    public static final int TYPE_USER = 3;

    private final IDBDriverManager dbDriverManager;
    private final IPWhitelistAccessDB accessDB;
    private final int type;
    private final Integer partnerId;
    private final Integer userId;
    private IPWhitelistEntry entry;

    private boolean okPressed = false;

    private JTextField textIPPattern;
    private JTextArea textDescription;
    private JComboBox<String> comboTargetType;
    private JCheckBox checkEnabled;
    private JLabel labelCreated;
    private JButton buttonSave;
    private JButton buttonCancel;
    private ResourceBundleIPWhitelist rb;

    public JDialogEditIPWhitelist(JFrame parent, IDBDriverManager dbDriverManager,
                                   int type, Integer partnerId, Integer userId) {
        this(parent, dbDriverManager, null, type, partnerId, userId);
    }

    public JDialogEditIPWhitelist(JFrame parent, IDBDriverManager dbDriverManager,
                                   IPWhitelistEntry entry, int type,
                                   Integer partnerId, Integer userId) {
        super(parent, entry == null ? "Add IP Whitelist Entry" : "Edit IP Whitelist Entry", true);
        this.dbDriverManager = dbDriverManager;
        this.accessDB = new IPWhitelistAccessDB(dbDriverManager);
        this.entry = entry;
        this.type = type;
        this.partnerId = partnerId;
        this.userId = userId;

        try {
            this.rb = (ResourceBundleIPWhitelist) ResourceBundle.getBundle(
                ResourceBundleIPWhitelist.class.getName());
        } catch (Exception e) {
            this.rb = null;
        }

        initComponents();
        if (entry != null) {
            loadData();
        }
        setupKeyboardShortcuts();
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // IP Pattern
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        mainPanel.add(new JLabel(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.LABEL_IP_PATTERN) : "IP Pattern:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        textIPPattern = new JTextField(20);
        mainPanel.add(textIPPattern, gbc);
        row++;

        // Target Type (only for global)
        if (type == TYPE_GLOBAL) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            mainPanel.add(new JLabel(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.LABEL_TARGET_TYPE) : "Target Type:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            comboTargetType = new JComboBox<>(new String[]{"AS2", "TRACKER", "WEBUI", "API", "ALL"});
            mainPanel.add(comboTargetType, gbc);
            row++;
        }

        // Description
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTH;
        mainPanel.add(new JLabel(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.LABEL_DESCRIPTION) : "Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        textDescription = new JTextArea(4, 20);
        textDescription.setLineWrap(true);
        textDescription.setWrapStyleWord(true);
        JScrollPane scrollDescription = new JScrollPane(textDescription);
        mainPanel.add(scrollDescription, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        row++;

        // Enabled
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        checkEnabled = new JCheckBox(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.LABEL_ENABLED) : "Enabled");
        checkEnabled.setSelected(true);
        mainPanel.add(checkEnabled, gbc);
        gbc.gridwidth = 1;
        row++;

        // Created info (only for edit mode)
        if (entry != null) {
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
            labelCreated = new JLabel("");
            labelCreated.setFont(labelCreated.getFont().deriveFont(Font.ITALIC));
            mainPanel.add(labelCreated, gbc);
            gbc.gridwidth = 1;
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonSave = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_SAVE) : "Save");
        buttonCancel = new JButton(rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.BUTTON_CANCEL) : "Cancel");

        buttonSave.addActionListener(e -> saveEntry());
        buttonCancel.addActionListener(e -> dispose());

        buttonPanel.add(buttonSave);
        buttonPanel.add(buttonCancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        textIPPattern.setText(entry.getIpPattern());
        textDescription.setText(entry.getDescription());
        checkEnabled.setSelected(entry.isEnabled());

        if (type == TYPE_GLOBAL && comboTargetType != null) {
            comboTargetType.setSelectedItem(entry.getTargetType());
        }

        if (labelCreated != null && entry.getCreatedAt() != null) {
            labelCreated.setText("Created: " + entry.getCreatedAt() +
                (entry.getCreatedBy() != null ? " by " + entry.getCreatedBy() : ""));
        }
    }

    private boolean validateInput() {
        String ipPattern = textIPPattern.getText().trim();
        if (ipPattern.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                rb != null ? rb.getResourceString(ResourceBundleIPWhitelist.MSG_VALIDATION_IP_REQUIRED) : "IP Pattern is required",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveEntry() {
        if (!validateInput()) {
            return;
        }

        buttonSave.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Thread saveThread = new Thread(() -> {
            try {
                if (entry == null) {
                    entry = new IPWhitelistEntry();
                }

                entry.setIpPattern(textIPPattern.getText().trim());
                entry.setDescription(textDescription.getText().trim());
                entry.setEnabled(checkEnabled.isSelected());

                if (type == TYPE_GLOBAL && comboTargetType != null) {
                    entry.setTargetType((String) comboTargetType.getSelectedItem());
                }

                if (entry.getId() == 0) {
                    if (type == TYPE_GLOBAL) {
                        accessDB.addGlobalWhitelist(entry);
                    } else if (type == TYPE_PARTNER && partnerId != null) {
                        accessDB.addPartnerWhitelist(partnerId, entry);
                    } else if (type == TYPE_USER && userId != null) {
                        accessDB.addUserWhitelist(userId, entry);
                    }
                } else {
                    if (type == TYPE_GLOBAL) {
                        accessDB.updateGlobalWhitelist(entry);
                    } else if (type == TYPE_PARTNER) {
                        // TODO: Implement updatePartnerWhitelist in IPWhitelistAccessDB
                        // accessDB.updatePartnerWhitelist(entry);
                    } else if (type == TYPE_USER) {
                        // TODO: Implement updateUserWhitelist in IPWhitelistAccessDB
                        // accessDB.updateUserWhitelist(entry);
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    okPressed = true;
                    dispose();
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    buttonSave.setEnabled(true);
                    String msg = "Failed to save: " + e.getMessage();
                    JOptionPane.showMessageDialog(JDialogEditIPWhitelist.this, msg, "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        saveThread.setDaemon(true);
        saveThread.start();
    }

    private void setupKeyboardShortcuts() {
        JRootPane rootPane = getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        actionMap.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        int modifierKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, modifierKey), "save");
        actionMap.put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonSave.isEnabled()) {
                    saveEntry();
                }
            }
        });

        rootPane.setDefaultButton(buttonSave);
    }

    public boolean wasOkPressed() {
        return okPressed;
    }

    public IPWhitelistEntry getEntry() {
        return entry;
    }
}
