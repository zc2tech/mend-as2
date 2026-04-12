package de.mendelson.comm.as2.client;

import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.MecResourceBundle;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
 * Dialog for switching between users (impersonation)
 * Only available to users with ADMIN role
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class JDialogSwitchUser extends JDialog {

    private JComboBox<UserComboItem> comboUsers;
    private JButton buttonSwitch;
    private JButton buttonCancel;
    private JLabel labelCurrentUser;
    private boolean switchSuccessful = false;
    private String selectedUsername = null;
    private final MecResourceBundle rb;

    public JDialogSwitchUser(JFrame parent, String currentUsername, List<WebUIUser> availableUsers) {
        super(parent, "Switch User", true);

        // Load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Gui.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Resource bundle not found: " + e.getClassName());
        }

        this.initComponents(currentUsername, availableUsers);
        this.setLocationRelativeTo(parent);
    }

    private void initComponents(String currentUsername, List<WebUIUser> availableUsers) {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Current user display
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Current user:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        labelCurrentUser = new JLabel(currentUsername);
        labelCurrentUser.setFont(labelCurrentUser.getFont().deriveFont(Font.BOLD));
        infoPanel.add(labelCurrentUser, gbc);

        // User selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        infoPanel.add(new JLabel("Switch to user:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        comboUsers = new JComboBox<>();

        // Add "Return to Admin" option if not currently admin
        if (!"admin".equals(currentUsername)) {
            comboUsers.addItem(new UserComboItem("admin", "admin (Return to original)"));
        }

        // Add all other users except current
        for (WebUIUser user : availableUsers) {
            if (!user.getUsername().equals(currentUsername)) {
                comboUsers.addItem(new UserComboItem(user.getUsername(), user.getUsername()));
            }
        }

        infoPanel.add(comboUsers, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonSwitch = new JButton("Switch");
        buttonSwitch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSwitch();
            }
        });

        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchSuccessful = false;
                dispose();
            }
        });

        buttonPanel.add(buttonSwitch);
        buttonPanel.add(buttonCancel);

        // Add panels to main panel
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.pack();
        this.setSize(400, 180);

        // Set default button
        this.getRootPane().setDefaultButton(buttonSwitch);

        // Disable switch button if no users available
        if (comboUsers.getItemCount() == 0) {
            buttonSwitch.setEnabled(false);
        }

        // Add ESC key binding to close dialog
        JRootPane rootPane = this.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        actionMap.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchSuccessful = false;
                dispose();
            }
        });
    }

    private void performSwitch() {
        UserComboItem selected = (UserComboItem) comboUsers.getSelectedItem();
        if (selected != null) {
            selectedUsername = selected.getUsername();
            switchSuccessful = true;
            dispose();
        }
    }

    public boolean isSwitchSuccessful() {
        return switchSuccessful;
    }

    public String getSelectedUsername() {
        return selectedUsername;
    }

    /**
     * Helper class for combo box items
     */
    private static class UserComboItem {
        private final String username;
        private final String displayName;

        public UserComboItem(String username, String displayName) {
            this.username = username;
            this.displayName = displayName;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
