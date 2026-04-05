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
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 * Dialog to manage WebUI users
 *
 */
public class JDialogUserManagement extends JDialog {

    private final GUIClient guiClient;
    private final Logger logger = Logger.getLogger("de.mendelson.as2.client");
    private JTable userTable;
    private UserTableModel userTableModel;
    private JButton buttonCreate;
    private JButton buttonEdit;
    private JButton buttonDelete;
    private JButton buttonChangePassword;
    private JButton buttonRefresh;
    private JButton buttonClose;

    public JDialogUserManagement(JFrame parent, GUIClient guiClient) {
        super(parent, "User Management", true);
        this.guiClient = guiClient;
        this.initComponents();
        this.setupKeyboardShortcuts();
        this.setSize(900, 600);
        this.setLocationRelativeTo(parent);
        this.loadUsers();
    }

    private void setupKeyboardShortcuts() {
        // Get the root pane's input map and action map
        JRootPane rootPane = this.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // ESC key - Close dialog
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        actionMap.put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Cmd+W / Ctrl+W - Close dialog
        int modifierKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, modifierKey), "close");

        // Cmd+N / Ctrl+N - Create new user
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, modifierKey), "create");
        actionMap.put("create", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonCreate.isEnabled()) {
                    createUser();
                }
            }
        });

        // Cmd+R / Ctrl+R - Refresh
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, modifierKey), "refresh");
        actionMap.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonRefresh.isEnabled()) {
                    loadUsers();
                }
            }
        });

        // Cmd+E / Ctrl+E - Edit selected user
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, modifierKey), "edit");
        actionMap.put("edit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonEdit.isEnabled()) {
                    editUser();
                }
            }
        });

        // Cmd+D / Ctrl+D - Delete selected user
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, modifierKey), "delete");
        // Also support DELETE key and Cmd+Backspace (Mac alternative)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, modifierKey), "delete");
        actionMap.put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonDelete.isEnabled()) {
                    deleteUser();
                }
            }
        });

        // Cmd+P / Ctrl+P - Change password
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, modifierKey), "changePassword");
        actionMap.put("changePassword", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonChangePassword.isEnabled()) {
                    changePassword();
                }
            }
        });

        // ENTER key - Edit selected user (alternative)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editEnter");
        actionMap.put("editEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonEdit.isEnabled() && userTable.getSelectedRow() != -1) {
                    editUser();
                }
            }
        });
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));

        // Create table
        userTableModel = new UserTableModel();
        userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);

        // Enable table sorting
        userTable.setAutoCreateRowSorter(true);

        // Adjust column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Username
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Full Name
        userTable.getColumnModel().getColumn(2).setPreferredWidth(180); // Email
        userTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Roles
        userTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Enabled
        userTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Last Login

        JScrollPane scrollPane = new JScrollPane(userTable);
        this.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        buttonCreate = new JButton("Create User");
        buttonCreate.addActionListener(e -> createUser());
        buttonCreate.setToolTipText("Create User [" + getShortcutText(KeyEvent.VK_N) + "]");

        buttonEdit = new JButton("Edit User");
        buttonEdit.addActionListener(e -> editUser());
        buttonEdit.setEnabled(false);
        buttonEdit.setToolTipText("Edit User [" + getShortcutText(KeyEvent.VK_E) + "]");

        buttonDelete = new JButton("Delete User");
        buttonDelete.addActionListener(e -> deleteUser());
        buttonDelete.setEnabled(false);
        buttonDelete.setToolTipText("Delete User [" + getShortcutText(KeyEvent.VK_D) + "]");

        buttonChangePassword = new JButton("Change Password");
        buttonChangePassword.addActionListener(e -> changePassword());
        buttonChangePassword.setEnabled(false);
        buttonChangePassword.setToolTipText("Change Password [" + getShortcutText(KeyEvent.VK_P) + "]");

        buttonRefresh = new JButton("Refresh");
        buttonRefresh.addActionListener(e -> loadUsers());
        buttonRefresh.setToolTipText("Refresh [" + getShortcutText(KeyEvent.VK_R) + "]");

        buttonClose = new JButton("Close");
        buttonClose.addActionListener(e -> dispose());
        buttonClose.setToolTipText("Close [ESC or " + getShortcutText(KeyEvent.VK_W) + "]");

        buttonPanel.add(buttonCreate);
        buttonPanel.add(buttonEdit);
        buttonPanel.add(buttonDelete);
        buttonPanel.add(buttonChangePassword);
        buttonPanel.add(buttonRefresh);
        buttonPanel.add(buttonClose);

        this.add(buttonPanel, BorderLayout.SOUTH);

        // Add selection listener to enable/disable buttons
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Double-click to edit
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editUser();
                }
            }
        });
    }

    /**
     * Get shortcut text for display (Cmd on Mac, Ctrl on other platforms)
     */
    private String getShortcutText(int keyCode) {
        String modifierText = System.getProperty("os.name").toLowerCase().contains("mac") ? "⌘" : "Ctrl+";
        return modifierText + KeyEvent.getKeyText(keyCode);
    }

    private void loadUsers() {
        // Only disable refresh button during load, keep others enabled
        buttonRefresh.setEnabled(false);

        // Load in background thread to avoid blocking UI
        Thread loadThread = new Thread(() -> {
            try {
                UserListRequest request = new UserListRequest();
                ClientServerResponse response = guiClient.sendSync(request);

                if (response instanceof UserListResponse) {
                    UserListResponse userResponse = (UserListResponse) response;
                    if (userResponse.getException() != null) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this,
                                    "Error loading users: " + userResponse.getException().getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            buttonRefresh.setEnabled(true);
                        });
                    } else {
                        List<WebUIUser> users = userResponse.getUsers();
                        // Fetch roles for each user
                        Map<Integer, String> rolesMap = new HashMap<>();
                        for (WebUIUser user : users) {
                            String roles = getUserRolesAsString(user.getId());
                            rolesMap.put(user.getId(), roles);
                        }

                        SwingUtilities.invokeLater(() -> {
                            userTableModel.setUsers(users);
                            userTableModel.setUserRoles(rolesMap);
                            buttonRefresh.setEnabled(true);
                        });
                    }
                }
            } catch (Exception e) {
                logger.severe("Error loading users: " + e.getMessage());
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error loading users: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    buttonRefresh.setEnabled(true);
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private String getUserRolesAsString(int userId) {
        try {
            UserRolesRequest request = new UserRolesRequest();
            request.setUserId(userId);
            ClientServerResponse response = guiClient.sendSync(request);
            if (response instanceof UserRolesResponse) {
                List<Role> roles = ((UserRolesResponse) response).getRoles();
                return roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", "));
            }
        } catch (Exception e) {
            logger.warning("Could not load roles for user " + userId);
        }
        return "";
    }

    private void setButtonsEnabled(boolean enabled) {
        buttonCreate.setEnabled(enabled);
        buttonRefresh.setEnabled(enabled);
        buttonClose.setEnabled(enabled);
        // Edit, Delete, ChangePassword remain controlled by selection
        if (!enabled) {
            buttonEdit.setEnabled(false);
            buttonDelete.setEnabled(false);
            buttonChangePassword.setEnabled(false);
        }
    }

    private void createUser() {
        JDialogEditUser dialog = new JDialogEditUser(this, guiClient, null);
        dialog.setVisible(true);
        if (dialog.wasSuccessful()) {
            loadUsers(); // Reload in background
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // Convert view row index to model row index (for sorting)
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        WebUIUser user = userTableModel.getUser(modelRow);
        JDialogEditUser dialog = new JDialogEditUser(this, guiClient, user);
        dialog.setVisible(true);
        if (dialog.wasSuccessful()) {
            loadUsers(); // Reload in background
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // Convert view row index to model row index (for sorting)
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        WebUIUser user = userTableModel.getUser(modelRow);

        // Prevent deleting admin user
        if (user.getUsername().equalsIgnoreCase("admin")) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete the admin user.",
                    "Delete Not Allowed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user \"" + user.getUsername() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Disable delete-related buttons during delete
            buttonDelete.setEnabled(false);
            buttonEdit.setEnabled(false);
            buttonChangePassword.setEnabled(false);

            // Delete in background thread
            Thread deleteThread = new Thread(() -> {
                try {
                    UserDeleteRequest request = new UserDeleteRequest();
                    request.setUserId(user.getId());
                    ClientServerResponse response = guiClient.sendSync(request);

                    if (response instanceof UserDeleteResponse) {
                        UserDeleteResponse deleteResponse = (UserDeleteResponse) response;
                        if (deleteResponse.getException() != null) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this,
                                        "Error deleting user: " + deleteResponse.getException().getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                // Re-enable buttons based on selection
                                updateButtonStates();
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this,
                                        "User deleted successfully",
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
                                loadUsers(); // Reload in background
                            });
                        }
                    }
                } catch (Exception e) {
                    logger.severe("Error deleting user: " + e.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Error deleting user: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        // Re-enable buttons based on selection
                        updateButtonStates();
                    });
                }
            });
            deleteThread.setDaemon(true);
            deleteThread.start();
        }
    }

    private void updateButtonStates() {
        int selectedRow = userTable.getSelectedRow();
        boolean hasSelection = selectedRow != -1;

        // Enable edit and change password for any selected user
        buttonEdit.setEnabled(hasSelection);
        buttonChangePassword.setEnabled(hasSelection);

        // Disable delete button for admin user
        if (hasSelection) {
            // Convert view row index to model row index (for sorting)
            int modelRow = userTable.convertRowIndexToModel(selectedRow);
            WebUIUser user = userTableModel.getUser(modelRow);
            buttonDelete.setEnabled(!user.getUsername().equalsIgnoreCase("admin"));
        } else {
            buttonDelete.setEnabled(false);
        }
    }

    private void changePassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // Convert view row index to model row index (for sorting)
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        WebUIUser user = userTableModel.getUser(modelRow);
        JDialogChangePassword dialog = new JDialogChangePassword(this, guiClient, user);
        dialog.setVisible(true);
    }

    // Table Model
    private static class UserTableModel extends AbstractTableModel {

        private List<WebUIUser> users = new ArrayList<>();
        private Map<Integer, String> userRoles = new HashMap<>(); // userId -> comma-separated role names
        private final String[] columnNames = {"Username", "Full Name", "Email", "Roles", "Enabled", "Last Login"};

        public void setUsers(List<WebUIUser> users) {
            this.users = users != null ? users : new ArrayList<>();
            fireTableDataChanged();
        }

        public void setUserRoles(Map<Integer, String> userRoles) {
            this.userRoles = userRoles != null ? userRoles : new HashMap<>();
            fireTableDataChanged();
        }

        public WebUIUser getUser(int row) {
            if (row >= 0 && row < users.size()) {
                return users.get(row);
            }
            return null;
        }

        @Override
        public int getRowCount() {
            return users.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            WebUIUser user = users.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return user.getUsername();
                case 1:
                    return user.getFullName() != null ? user.getFullName() : "";
                case 2:
                    return user.getEmail() != null ? user.getEmail() : "";
                case 3:
                    return userRoles.getOrDefault(user.getId(), "");
                case 4:
                    return user.isEnabled() ? "Active" : "Disabled";
                case 5:
                    return user.getLastLogin() != null ? user.getLastLogin().toString() : "Never";
                default:
                    return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
