package de.mendelson.comm.as2.client;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Login dialog for SwingUI authentication
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class JDialogLogin extends JDialog {

    private JTextField textUsername;
    private JPasswordField textPassword;
    private JButton buttonLogin;
    private JButton buttonCancel;
    private boolean loginSuccessful = false;
    private String username = null;
    private char[] password = null;

    public JDialogLogin(JFrame parent, String displayMode) {
        super(parent, "Login - " + AS2ServerVersion.getProductName(), true);
        this.initComponents(displayMode);
        this.setLocationRelativeTo(parent);
    }

    private void initComponents(String displayMode) {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Logo panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        try {
            // Load AS2 logo - use the open source logo
            MendelsonMultiResolutionImage logoImage = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/logo_open_source_with_text.svg",
                64,
                MendelsonMultiResolutionImage.SVGScalingOption.KEEP_HEIGHT
            );
            ImageIcon logoIcon = new ImageIcon(logoImage.toMinResolution(64));
            JLabel logoLabel = new JLabel(logoIcon);
            logoPanel.add(logoLabel);
        } catch (Exception e) {
            // If logo fails to load, show text instead
            JLabel logoLabel = new JLabel(AS2ServerVersion.getProductName());
            logoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            logoPanel.add(logoLabel);
        }

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        // Username field
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        textUsername = new JTextField(20);
        textUsername.setText("admin"); // Only admin user allowed for SwingUI
        textUsername.setEditable(false); // Make readonly - only admin allowed
        textUsername.setFocusable(false); // Prevent focus on readonly field
        textUsername.setBackground(UIManager.getColor("TextField.inactiveBackground")); // Visual indication
        formPanel.add(textUsername, gbc);

        // Password label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);

        // Password field
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        textPassword = new JPasswordField(20);
        formPanel.add(textPassword, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonLogin = new JButton("Login");
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginSuccessful = false;
                dispose();
            }
        });

        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonCancel);

        // Add panels to main panel
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.pack();
        this.setSize(400, 250);

        // Enter key triggers login
        textPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Set default button
        this.getRootPane().setDefaultButton(buttonLogin);

        // Focus password field (username is readonly and pre-filled)
        textPassword.requestFocusInWindow();
    }

    private void performLogin() {
        username = textUsername.getText().trim();
        password = textPassword.getPassword();

        // Username validation (should always be "admin" since field is readonly)
        if (username.isEmpty() || !username.equals("admin")) {
            JOptionPane.showMessageDialog(this,
                    "Only 'admin' user is allowed for SwingUI",
                    "Login Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a password",
                    "Login Error",
                    JOptionPane.WARNING_MESSAGE);
            textPassword.requestFocusInWindow();
            return;
        }

        loginSuccessful = true;
        dispose();
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    /**
     * Clear password from memory for security
     */
    public void clearPassword() {
        if (password != null) {
            for (int i = 0; i < password.length; i++) {
                password[i] = 0;
            }
        }
    }
}
