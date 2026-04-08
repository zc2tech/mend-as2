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

import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.comm.as2.usermanagement.clientserver.UserPasswordChangeRequest;
import de.mendelson.comm.as2.usermanagement.clientserver.UserPasswordChangeResponse;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.WindowTitleUtil;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Dialog to change a user's password
 * Note: Password changing is implemented via UserModifyRequest
 * by creating a new user object with updated password hash
 *
 */
public class JDialogChangePassword extends JDialog {

    private final GUIClient guiClient;
    private final WebUIUser user;

    private JPasswordField textNewPassword;
    private JPasswordField textConfirmPassword;
    private JButton buttonSave;
    private JButton buttonCancel;

    public JDialogChangePassword(JDialog parent, GUIClient guiClient, WebUIUser user) {
        super(parent, WindowTitleUtil.buildTitle("Change Password - " + user.getUsername()), true);
        this.guiClient = guiClient;
        this.user = user;
        this.initComponents();
        this.setupKeyboardShortcuts();
        this.setSize(400, 250);  // Increased height to accommodate logo
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
                    changePassword();
                }
            }
        });
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));

        // Logo panel at the top (similar to login dialog)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        logoPanel.setBackground(Color.WHITE);

        String logoPath = WindowTitleUtil.isTestMode()
            ? "/de/mendelson/comm/as2/client/logo_open_source_with_text_test.svg"
            : "/de/mendelson/comm/as2/client/logo_open_source_with_text.svg";

        try {
            MendelsonMultiResolutionImage logoImage = MendelsonMultiResolutionImage.fromSVG(
                logoPath,
                64,
                MendelsonMultiResolutionImage.SVGScalingOption.KEEP_HEIGHT
            );
            ImageIcon icon = new ImageIcon(logoImage.toMinResolution(64));
            JLabel logoLabel = new JLabel(icon);
            logoPanel.add(logoLabel);
        } catch (Exception e) {
            // If logo can't be loaded, continue without it
            System.err.println("Could not load logo: " + e.getMessage());
        }

        this.add(logoPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // New Password
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("New Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        textNewPassword = new JPasswordField(20);
        formPanel.add(textNewPassword, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        textConfirmPassword = new JPasswordField(20);
        formPanel.add(textConfirmPassword, gbc);

        this.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        buttonSave = new JButton("Change Password");
        buttonSave.addActionListener(e -> changePassword());

        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(e -> dispose());

        buttonPanel.add(buttonSave);
        buttonPanel.add(buttonCancel);

        this.add(buttonPanel, BorderLayout.SOUTH);

        // Make Save button default
        getRootPane().setDefaultButton(buttonSave);
    }

    private void changePassword() {
        String newPassword = new String(textNewPassword.getPassword());
        String confirmPassword = new String(textConfirmPassword.getPassword());

        // Validate
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Password is required",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable save button to prevent double-click
        buttonSave.setEnabled(false);

        // Send password change request to server in background thread
        Thread changeThread = new Thread(() -> {
            try {
                UserPasswordChangeRequest request = new UserPasswordChangeRequest();
                request.setUserId(user.getId());
                request.setPlainPassword(newPassword);

                ClientServerResponse response = guiClient.sendSync(request);

                if (response instanceof UserPasswordChangeResponse) {
                    UserPasswordChangeResponse changeResponse = (UserPasswordChangeResponse) response;
                    SwingUtilities.invokeLater(() -> {
                        if (changeResponse.getException() != null) {
                            JOptionPane.showMessageDialog(this,
                                    "Error changing password: " + changeResponse.getException().getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            buttonSave.setEnabled(true);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Password changed successfully",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        }
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error changing password: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    buttonSave.setEnabled(true);
                });
            }
        });
        changeThread.setDaemon(true);
        changeThread.start();
    }
}
