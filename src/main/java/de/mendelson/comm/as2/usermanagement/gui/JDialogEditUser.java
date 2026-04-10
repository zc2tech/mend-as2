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

package de.mendelson.comm.as2.usermanagement.gui;

import de.mendelson.comm.as2.usermanagement.Role;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.comm.as2.usermanagement.clientserver.*;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.util.logging.Logger;

/**
 * Dialog to create or edit a user
 *
 */
public class JDialogEditUser extends JDialog {

    private final GUIClient guiClient;
    private final Logger logger = Logger.getLogger("de.mendelson.as2.client");
    private final WebUIUser editingUser;
    private boolean successful = false;

    private JTextField textUsername;
    private JPasswordField textPassword;
    private JPasswordField textConfirmPassword;
    private JLabel labelPassword;
    private JLabel labelConfirmPassword;
    private JTextField textEmail;
    private JTextField textFullName;
    private JCheckBox checkEnabled;
    private JCheckBox checkGeneratePassword;
    private JPanel rolePanel;
    private Map<Integer, JCheckBox> roleCheckboxes;
    private List<Role> availableRoles;
    private JButton buttonSave;
    private JButton buttonCancel;

    public JDialogEditUser(JDialog parent, GUIClient guiClient, WebUIUser user) {
        super(parent, user == null ? "Create User" : "Edit User", true);
        this.guiClient = guiClient;
        this.editingUser = user;

        this.loadAvailableRoles();

        this.initComponents();
        this.setupKeyboardShortcuts();
        this.setSize(600, 650); // Increased width and height for role section
        this.setLocationRelativeTo(parent);
    }

    private void setupKeyboardShortcuts() {
        JRootPane rootPane = this.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();
        int modifierKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        // ESC key - Close dialog
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        actionMap.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Cmd+W / Ctrl+W - Close dialog
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, modifierKey), "cancel");

        // Cmd+S / Ctrl+S - Save
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, modifierKey), "save");
        actionMap.put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonSave.isEnabled()) {
                    saveUser();
                }
            }
        });
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Username
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        textUsername = new JTextField(20);
        if (editingUser != null) {
            textUsername.setText(editingUser.getUsername());
            textUsername.setEnabled(false); // Cannot change username
        }
        formPanel.add(textUsername, gbc);

        row++;

        // Password (only for new users or leave empty for no change)
        if (editingUser == null) {
            // Generate and email password checkbox (only for new users)
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            checkGeneratePassword = new JCheckBox("Generate and email password to user");
            checkGeneratePassword.setToolTipText("<html>A secure random password will be generated and sent to the user's email address.<br>" +
                "User must change password on first login.</html>");
            checkGeneratePassword.addItemListener(e -> {
                boolean generatePassword = checkGeneratePassword.isSelected();
                labelPassword.setVisible(!generatePassword);
                textPassword.setVisible(!generatePassword);
                labelConfirmPassword.setVisible(!generatePassword);
                textConfirmPassword.setVisible(!generatePassword);
                if (generatePassword) {
                    textPassword.setText("");
                    textConfirmPassword.setText("");
                }
            });
            formPanel.add(checkGeneratePassword, gbc);
            gbc.gridwidth = 1; // Reset gridwidth

            row++;

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            labelPassword = new JLabel("Password:");
            formPanel.add(labelPassword, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            textPassword = new JPasswordField(20);
            formPanel.add(textPassword, gbc);

            row++;

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            labelConfirmPassword = new JLabel("Confirm Password:");
            formPanel.add(labelConfirmPassword, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            textConfirmPassword = new JPasswordField(20);
            formPanel.add(textConfirmPassword, gbc);

            row++;
        }

        // Email
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        textEmail = new JTextField(20);
        if (editingUser != null && editingUser.getEmail() != null) {
            textEmail.setText(editingUser.getEmail());
        }
        formPanel.add(textEmail, gbc);

        row++;

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        textFullName = new JTextField(20);
        if (editingUser != null && editingUser.getFullName() != null) {
            textFullName.setText(editingUser.getFullName());
        }
        formPanel.add(textFullName, gbc);

        row++;

        // Enabled
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Enabled:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        checkEnabled = new JCheckBox();
        checkEnabled.setSelected(editingUser == null || editingUser.isEnabled());

        // Disable the checkbox for admin user
        boolean isAdminUser = editingUser != null && editingUser.getUsername().equalsIgnoreCase("admin");
        if (isAdminUser) {
            checkEnabled.setEnabled(false);
            checkEnabled.setToolTipText("Cannot disable admin user");
        }

        formPanel.add(checkEnabled, gbc);

        row++;

        // Roles section (if roles are available)
        if (availableRoles != null && !availableRoles.isEmpty()) {
            boolean isAdmin = editingUser != null && editingUser.getUsername().equalsIgnoreCase("admin");

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            JLabel rolesLabel = new JLabel(isAdmin ? "Assigned Roles: (Cannot modify admin roles)" : "Assigned Roles:");
            if (isAdmin) {
                rolesLabel.setForeground(new Color(108, 117, 125)); // Gray color
            }
            formPanel.add(rolesLabel, gbc);

            row++;

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            rolePanel = new JPanel(new GridLayout(0, 1, 5, 5));
            rolePanel.setBorder(BorderFactory.createEtchedBorder());
            if (isAdmin) {
                rolePanel.setBackground(new Color(248, 249, 250)); // Light gray background
            }

            roleCheckboxes = new HashMap<>();

            // Get current user roles if editing
            Set<Integer> currentRoleIds = new HashSet<>();
            if (editingUser != null) {
                List<Role> userRoles = getUserRoles(editingUser.getId());
                for (Role role : userRoles) {
                    currentRoleIds.add(role.getId());
                }
            }

            // Create checkbox for each role
            for (Role role : availableRoles) {
                JCheckBox checkbox = new JCheckBox(role.getName() + " - " + role.getDescription());
                checkbox.setSelected(currentRoleIds.contains(role.getId()));
                checkbox.setEnabled(!isAdmin); // Disable checkboxes for admin user
                roleCheckboxes.put(role.getId(), checkbox);
                rolePanel.add(checkbox);
            }

            JScrollPane roleScrollPane = new JScrollPane(rolePanel);
            roleScrollPane.setPreferredSize(new Dimension(550, 180));
            roleScrollPane.setMinimumSize(new Dimension(550, 180));
            formPanel.add(roleScrollPane, gbc);

            gbc.gridwidth = 1; // Reset
        }

        this.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        buttonSave = new JButton(editingUser == null ? "Create" : "Update");
        buttonSave.addActionListener(e -> saveUser());

        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(e -> dispose());

        buttonPanel.add(buttonSave);
        buttonPanel.add(buttonCancel);

        this.add(buttonPanel, BorderLayout.SOUTH);

        // Make Save button default
        getRootPane().setDefaultButton(buttonSave);
    }

    private void saveUser() {
        // Validate input
        String username = textUsername.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username is required",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (editingUser == null) {
            // Creating new user
            boolean generatePassword = checkGeneratePassword != null && checkGeneratePassword.isSelected();

            if (generatePassword) {
                // Validate email is provided when generating password
                String email = textEmail.getText().trim();
                if (email.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Email address is required when generating password",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                createUser(username, null, true);
            } else {
                // Manual password entry - validate password fields
                String password = new String(textPassword.getPassword());
                String confirmPassword = new String(textConfirmPassword.getPassword());

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Password is required",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this,
                            "Passwords do not match",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                createUser(username, password, false);
            }
        } else {
            // Updating existing user
            updateUser();
        }
    }

    private void createUser(String username, String password, boolean generateAndEmailPassword) {
        // Disable save button to prevent double-click
        buttonSave.setEnabled(false);

        // Execute in background thread
        Thread createThread = new Thread(() -> {
            try {
                WebUIUser user = new WebUIUser();
                user.setUsername(username);
                user.setEmail(textEmail.getText().trim());
                user.setFullName(textFullName.getText().trim());
                user.setEnabled(checkEnabled.isSelected());

                UserCreateRequest request = new UserCreateRequest();
                request.setUser(user);
                request.setPlainPassword(password);
                request.setGenerateAndEmailPassword(generateAndEmailPassword);

                ClientServerResponse response = guiClient.sendSync(request);

                if (response instanceof UserCreateResponse) {
                    UserCreateResponse createResponse = (UserCreateResponse) response;
                    SwingUtilities.invokeLater(() -> {
                        if (createResponse.getException() != null) {
                            String errorMsg = createResponse.getException().getMessage();
                            if (errorMsg.contains("email notification failed")) {
                                JOptionPane.showMessageDialog(this,
                                        errorMsg,
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                                successful = true;
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "Error creating user: " + errorMsg,
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                buttonSave.setEnabled(true);
                            }
                        } else {
                            int userId = createResponse.getUserId();
                            // Save role assignments
                            saveRoleAssignments(userId);

                            successful = true;
                            String successMsg = generateAndEmailPassword ?
                                "User created successfully. Password has been sent to " + textEmail.getText().trim() :
                                "User created successfully";
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this,
                                        successMsg,
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
                                dispose();
                            });
                        }
                    });
                }
            } catch (Exception e) {
                logger.severe("Error creating user: " + e.getMessage());
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error creating user: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    buttonSave.setEnabled(true);
                });
            }
        });
        createThread.setDaemon(true);
        createThread.start();
    }

    private void updateUser() {
        // Disable save button to prevent double-click
        buttonSave.setEnabled(false);

        // Execute in background thread
        Thread updateThread = new Thread(() -> {
            try {
                editingUser.setEmail(textEmail.getText().trim());
                editingUser.setFullName(textFullName.getText().trim());
                editingUser.setEnabled(checkEnabled.isSelected());

                UserModifyRequest request = new UserModifyRequest();
                request.setUser(editingUser);

                ClientServerResponse response = guiClient.sendSync(request);

                if (response instanceof UserModifyResponse) {
                    UserModifyResponse modifyResponse = (UserModifyResponse) response;
                    SwingUtilities.invokeLater(() -> {
                        if (modifyResponse.getException() != null) {
                            JOptionPane.showMessageDialog(this,
                                    "Error updating user: " + modifyResponse.getException().getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            buttonSave.setEnabled(true);
                        } else {
                            // Save role assignments
                            saveRoleAssignments(editingUser.getId());

                            successful = true;
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this,
                                        "User updated successfully",
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
                                dispose();
                            });
                        }
                    });
                }
            } catch (Exception e) {
                logger.severe("Error updating user: " + e.getMessage());
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error updating user: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    buttonSave.setEnabled(true);
                });
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void loadAvailableRoles() {
        Thread loadThread = new Thread(() -> {
            try {
                RoleListRequest request = new RoleListRequest();
                ClientServerResponse response = guiClient.sendSync(request);
                if (response instanceof RoleListResponse) {
                    availableRoles = ((RoleListResponse) response).getRoles();
                } else {
                    logger.warning("Unexpected response type for RoleListRequest: " +
                        (response != null ? response.getClass().getName() : "null"));
                }
            } catch (Exception e) {
                logger.severe("Error loading roles: " + e.getMessage());
                e.printStackTrace();
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
        try {
            loadThread.join(5000); // Wait up to 5 seconds for roles to load
        } catch (InterruptedException e) {
            logger.warning("Timeout loading roles");
        }

        // Log final state for debugging
        if (availableRoles == null || availableRoles.isEmpty()) {
            logger.warning("No roles available - role UI will not be shown. availableRoles=" + availableRoles);
        }
    }

    private List<Role> getUserRoles(int userId) {
        try {
            UserRolesRequest request = new UserRolesRequest();
            request.setUserId(userId);
            ClientServerResponse response = guiClient.sendSync(request);
            if (response instanceof UserRolesResponse) {
                return ((UserRolesResponse) response).getRoles();
            }
        } catch (Exception e) {
            logger.severe("Error loading user roles: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private void saveRoleAssignments(int userId) {
        if (roleCheckboxes == null) {
            return;
        }

        // Don't save role changes for admin user
        if (editingUser != null && editingUser.getUsername().equalsIgnoreCase("admin")) {
            logger.info("Skipping role assignment for admin user - roles cannot be modified");
            return;
        }

        // First, get current roles to determine what changed
        Set<Integer> currentRoleIds = new HashSet<>();
        List<Role> currentRoles = getUserRoles(userId);
        for (Role role : currentRoles) {
            currentRoleIds.add(role.getId());
        }

        // Process each role checkbox
        for (Map.Entry<Integer, JCheckBox> entry : roleCheckboxes.entrySet()) {
            int roleId = entry.getKey();
            boolean isSelected = entry.getValue().isSelected();
            boolean wasSelected = currentRoleIds.contains(roleId);

            try {
                if (isSelected && !wasSelected) {
                    // Add role
                    UserRoleAssignRequest request = new UserRoleAssignRequest();
                    request.setUserId(userId);
                    request.setRoleId(roleId);
                    guiClient.sendSync(request);
                } else if (!isSelected && wasSelected) {
                    // Remove role
                    UserRoleRemoveRequest request = new UserRoleRemoveRequest();
                    request.setUserId(userId);
                    request.setRoleId(roleId);
                    guiClient.sendSync(request);
                }
            } catch (Exception e) {
                logger.severe("Error saving role assignment: " + e.getMessage());
            }
        }
    }

    public boolean wasSuccessful() {
        return successful;
    }
}
