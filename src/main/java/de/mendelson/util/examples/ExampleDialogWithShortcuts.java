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

package de.mendelson.util.examples;

import de.mendelson.util.KeyboardShortcutUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Example dialog demonstrating keyboard shortcut usage with Mac support.
 * This shows best practices for implementing keyboard shortcuts in Swing dialogs.
 *
 * Key features:
 * - ESC to close dialog
 * - ENTER to trigger default button (Save)
 * - Cmd+W/Ctrl+W to close dialog (standard Mac behavior)
 * - Cmd+S/Ctrl+S to save (platform-specific)
 * - Custom shortcuts for other buttons
 *
 */
public class ExampleDialogWithShortcuts extends JDialog {

    private JButton jButtonSave;
    private JButton jButtonCancel;
    private JButton jButtonApply;
    private JTextField jTextFieldName;

    public ExampleDialogWithShortcuts(JFrame parent) {
        super(parent, "Example Dialog", true);
        initComponents();
        setupKeyboardShortcuts();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add a text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        jTextFieldName = new JTextField(20);
        contentPanel.add(jTextFieldName, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        jButtonSave = new JButton("Save");
        jButtonSave.addActionListener(this::onSave);

        jButtonApply = new JButton("Apply");
        jButtonApply.addActionListener(this::onApply);

        jButtonCancel = new JButton("Cancel");
        jButtonCancel.addActionListener(this::onCancel);

        buttonPanel.add(jButtonApply);
        buttonPanel.add(jButtonCancel);
        buttonPanel.add(jButtonSave);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Set up all keyboard shortcuts for this dialog.
     * This method demonstrates various shortcut patterns.
     */
    private void setupKeyboardShortcuts() {
        // Method 1: Use the comprehensive setup method
        // This sets up ESC, ENTER, and Cmd/Ctrl+W automatically
        KeyboardShortcutUtil.setupDialogKeyBindings(this, jButtonSave, jButtonCancel);

        // Method 2: Add specific shortcuts to buttons
        // Cmd/Ctrl+S for Save
        KeyboardShortcutUtil.addButtonShortcut(jButtonSave, KeyEvent.VK_S, "SAVE_ACTION");

        // Cmd/Ctrl+A for Apply
        KeyboardShortcutUtil.addButtonShortcut(jButtonApply, KeyEvent.VK_A, "APPLY_ACTION");

        // Optional: Update button text to show shortcuts
        // Uncomment these lines if you want to display shortcuts on buttons
        // KeyboardShortcutUtil.setButtonTextWithShortcut(jButtonSave, "Save", KeyEvent.VK_S);
        // KeyboardShortcutUtil.setButtonTextWithShortcut(jButtonApply, "Apply", KeyEvent.VK_A);
    }

    /**
     * Alternative approach: Manual keyboard shortcut setup.
     * Use this if you need more control over individual shortcuts.
     */
    @SuppressWarnings("unused")
    private void setupKeyboardShortcutsManually() {
        JRootPane rootPane = getRootPane();

        // 1. ESC to close
        KeyboardShortcutUtil.addEscapeKeyBinding(this);

        // 2. ENTER triggers default button
        KeyboardShortcutUtil.addEnterKeyBinding(this, jButtonSave);

        // 3. Platform-specific shortcuts for buttons
        // Cmd+S on Mac, Ctrl+S on Windows/Linux
        KeyStroke saveShortcut = KeyboardShortcutUtil.createMenuShortcut(KeyEvent.VK_S);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(saveShortcut, "SAVE");
        rootPane.getActionMap().put("SAVE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jButtonSave.isEnabled()) {
                    jButtonSave.doClick();
                }
            }
        });

        // 4. Cmd/Ctrl+W to close (standard Mac behavior)
        KeyStroke closeShortcut = KeyboardShortcutUtil.createMenuShortcut(KeyEvent.VK_W);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeShortcut, "CLOSE");
        rootPane.getActionMap().put("CLOSE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void onSave(ActionEvent e) {
        System.out.println("Save clicked - Name: " + jTextFieldName.getText());
        dispose();
    }

    private void onApply(ActionEvent e) {
        System.out.println("Apply clicked - Name: " + jTextFieldName.getText());
        // Don't close the dialog on Apply
    }

    private void onCancel(ActionEvent e) {
        System.out.println("Cancel clicked");
        dispose();
    }

    // Test method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            ExampleDialogWithShortcuts dialog = new ExampleDialogWithShortcuts(frame);
            dialog.setVisible(true);
        });
    }
}
