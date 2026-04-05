package de.mendelson.comm.as2.preferences;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
import de.mendelson.util.passwordfield.PasswordOverlay;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Panel to define the proxy settings
 *
 * @author S.Heller
 * @version: $Revision: 29 $
 */
public class PreferencesPanelProxy extends PreferencesPanel {

    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }

    private final PreferencesClient preferences;
    private String preferencesStrAtLoadTime = "";

    private final MendelsonMultiResolutionImage IMAGE_PROXY
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/proxy.svg",
                    JDialogPreferences.IMAGE_HEIGHT, JDialogPreferences.IMAGE_HEIGHT * 2);

    /**
     * Creates new form PreferencesPanelDirectories
     */
    public PreferencesPanelProxy(BaseClient baseClient) {
        this.preferences = new PreferencesClient(baseClient);
        this.initComponents();
        TextOverlay.addTo(this.jTextFieldProxyPort, rb.getResourceString("label.proxy.port.hint"));
        TextOverlay.addTo(this.jTextFieldProxyURL, rb.getResourceString("label.proxy.url.hint"));
        TextOverlay.addTo(this.jTextFieldProxyUser, rb.getResourceString("label.proxy.user.hint"));
        PasswordOverlay.addTo(this.jPasswordFieldProxyPass, rb.getResourceString("label.proxy.pass.hint"));
    }

    private void setButtonState() {
        this.jTextFieldProxyURL.setEnabled(this.switchUseProxy.isSelected());
        this.jTextFieldProxyURL.setEditable(this.switchUseProxy.isSelected());
        this.jTextFieldProxyPort.setEnabled(this.switchUseProxy.isSelected());
        this.jTextFieldProxyPort.setEditable(this.switchUseProxy.isSelected());
        this.jTextFieldProxyUser.setEnabled(this.switchUseProxy.isSelected()
                && this.switchUseProxyAuthentification.isSelected());
        this.jTextFieldProxyUser.setEditable(this.switchUseProxy.isSelected()
                && this.switchUseProxyAuthentification.isSelected());
        this.jPasswordFieldProxyPass.setEnabled(this.switchUseProxy.isSelected()
                && this.switchUseProxyAuthentification.isSelected());
        this.jPasswordFieldProxyPass.setEditable(this.switchUseProxy.isSelected()
                && this.switchUseProxyAuthentification.isSelected());
        this.switchUseProxyAuthentification.setEnabled(this.switchUseProxy.isSelected());
    }

    /**
     * Sets new preferences to this panel to changes/modify
     */
    @Override
    public void loadPreferences() {
        this.jTextFieldProxyURL.setText(this.preferences.get(PreferencesAS2.PROXY_HOST));
        this.jTextFieldProxyPort.setText(this.preferences.get(PreferencesAS2.PROXY_PORT));
        this.jTextFieldProxyUser.setText(this.preferences.get(PreferencesAS2.AUTH_PROXY_USER));
        this.jPasswordFieldProxyPass.setText(this.preferences.get(PreferencesAS2.AUTH_PROXY_PASS));
        this.switchUseProxy.setSelected(this.preferences.getBoolean(PreferencesAS2.PROXY_USE));
        this.switchUseProxyAuthentification.setSelected(
                this.preferences.getBoolean(PreferencesAS2.AUTH_PROXY_USE));
        this.setButtonState();
        this.preferencesStrAtLoadTime = this.captureSettingsToStr();
    }

    /**
     * Helper method to find out if there are changes in the GUI before storing
     * them to the server
     */
    private String captureSettingsToStr() {
        StringBuilder builder = new StringBuilder();
        builder.append(PreferencesAS2.PROXY_HOST).append("=")
                .append(this.jTextFieldProxyURL.getText()).append(";");
        builder.append(PreferencesAS2.PROXY_PORT).append("=")
                .append(this.jTextFieldProxyPort.getText()).append(";");
        builder.append(PreferencesAS2.AUTH_PROXY_USER).append("=")
                .append(this.jTextFieldProxyUser.getText()).append(";");
        builder.append(PreferencesAS2.AUTH_PROXY_PASS).append("=")
                .append(new String(this.jPasswordFieldProxyPass.getPassword())).append(";");
        builder.append(PreferencesAS2.PROXY_USE).append("=")
                .append(this.switchUseProxy.isSelected()).append(";");
        builder.append(PreferencesAS2.AUTH_PROXY_USE).append("=")
                .append(this.switchUseProxyAuthentification.isSelected()).append(";");
        return (builder.toString());
    }

    @Override
    public boolean preferencesAreModified() {
        return (!this.preferencesStrAtLoadTime.equals(this.captureSettingsToStr()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelMargin = new javax.swing.JPanel();
        jPanelSpace = new javax.swing.JPanel();
        jPanelSpace7875 = new javax.swing.JPanel();
        jPanelSpace775 = new javax.swing.JPanel();
        jPanelProxyOnOff = new javax.swing.JPanel();
        jLabelUseProxy = new javax.swing.JLabel();
        jLabelProxyURL = new javax.swing.JLabel();
        switchUseProxy = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jTextFieldProxyURL = new javax.swing.JTextField();
        jLabelColon = new javax.swing.JLabel();
        jTextFieldProxyPort = new javax.swing.JTextField();
        jPanel736 = new javax.swing.JPanel();
        jPanelSpace765 = new javax.swing.JPanel();
        jPanelProxyAuth = new javax.swing.JPanel();
        jLabelUseProxyAuthentification = new javax.swing.JLabel();
        jLabelProxyUser = new javax.swing.JLabel();
        jLabelProxyPass = new javax.swing.JLabel();
        jPasswordFieldProxyPass = new javax.swing.JPasswordField();
        jTextFieldProxyUser = new javax.swing.JTextField();
        switchUseProxyAuthentification = new de.mendelson.util.toggleswitch.ToggleSwitch();

        setLayout(new java.awt.GridBagLayout());

        jPanelMargin.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelMargin.add(jPanelSpace, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jPanelSpace7875, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jPanelSpace775, gridBagConstraints);

        jPanelProxyOnOff.setLayout(new java.awt.GridBagLayout());

        jLabelUseProxy.setText(this.rb.getResourceString( "label.proxy.use"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelProxyOnOff.add(jLabelUseProxy, gridBagConstraints);

        jLabelProxyURL.setText(this.rb.getResourceString( "label.proxy.url"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelProxyOnOff.add(jLabelProxyURL, gridBagConstraints);

        switchUseProxy.setDisplayStatusText(true);
        switchUseProxy.setHorizontalTextPosition(SwingConstants.LEFT);
        switchUseProxy.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                switchUseProxyItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 5, 5);
        jPanelProxyOnOff.add(switchUseProxy, gridBagConstraints);

        jTextFieldProxyURL.setPreferredSize(new java.awt.Dimension(200, 22));
        jTextFieldProxyURL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldProxyURLKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelProxyOnOff.add(jTextFieldProxyURL, gridBagConstraints);

        jLabelColon.setText(":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelProxyOnOff.add(jLabelColon, gridBagConstraints);

        jTextFieldProxyPort.setPreferredSize(new java.awt.Dimension(50, 22));
        jTextFieldProxyPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldProxyPortKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelProxyOnOff.add(jTextFieldProxyPort, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelProxyOnOff.add(jPanel736, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelProxyOnOff.add(jPanelSpace765, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelProxyOnOff, gridBagConstraints);

        jPanelProxyAuth.setLayout(new java.awt.GridBagLayout());

        jLabelUseProxyAuthentification.setText(this.rb.getResourceString( "label.proxy.useauthentification"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 5, 5);
        jPanelProxyAuth.add(jLabelUseProxyAuthentification, gridBagConstraints);

        jLabelProxyUser.setText(this.rb.getResourceString( "label.proxy.user"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 5, 5);
        jPanelProxyAuth.add(jLabelProxyUser, gridBagConstraints);

        jLabelProxyPass.setText(this.rb.getResourceString( "label.proxy.pass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 5, 5);
        jPanelProxyAuth.add(jLabelProxyPass, gridBagConstraints);

        jPasswordFieldProxyPass.setPreferredSize(new java.awt.Dimension(200, 22));
        jPasswordFieldProxyPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordFieldProxyPassKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelProxyAuth.add(jPasswordFieldProxyPass, gridBagConstraints);

        jTextFieldProxyUser.setPreferredSize(new java.awt.Dimension(200, 22));
        jTextFieldProxyUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldProxyUserKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelProxyAuth.add(jTextFieldProxyUser, gridBagConstraints);

        switchUseProxyAuthentification.setDisplayStatusText(true);
        switchUseProxyAuthentification.setHorizontalTextPosition(SwingConstants.LEFT);
        switchUseProxyAuthentification.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                switchUseProxyAuthentificationItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 5, 5);
        jPanelProxyAuth.add(switchUseProxyAuthentification, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelProxyAuth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelMargin, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldProxyPortKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldProxyPortKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldProxyPortKeyReleased

    private void jTextFieldProxyURLKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldProxyURLKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldProxyURLKeyReleased

    private void jPasswordFieldProxyPassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldProxyPassKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jPasswordFieldProxyPassKeyReleased

    private void jTextFieldProxyUserKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldProxyUserKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldProxyUserKeyReleased

    private void switchUseProxyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_switchUseProxyItemStateChanged
        this.setButtonState();
    }//GEN-LAST:event_switchUseProxyItemStateChanged

    private void switchUseProxyAuthentificationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_switchUseProxyAuthentificationItemStateChanged
        this.setButtonState();
    }//GEN-LAST:event_switchUseProxyAuthentificationItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelColon;
    private javax.swing.JLabel jLabelProxyPass;
    private javax.swing.JLabel jLabelProxyURL;
    private javax.swing.JLabel jLabelProxyUser;
    private javax.swing.JLabel jLabelUseProxy;
    private javax.swing.JLabel jLabelUseProxyAuthentification;
    private javax.swing.JPanel jPanel736;
    private javax.swing.JPanel jPanelMargin;
    private javax.swing.JPanel jPanelProxyAuth;
    private javax.swing.JPanel jPanelProxyOnOff;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace765;
    private javax.swing.JPanel jPanelSpace775;
    private javax.swing.JPanel jPanelSpace7875;
    private javax.swing.JPasswordField jPasswordFieldProxyPass;
    private javax.swing.JTextField jTextFieldProxyPort;
    private javax.swing.JTextField jTextFieldProxyURL;
    private javax.swing.JTextField jTextFieldProxyUser;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchUseProxy;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchUseProxyAuthentification;
    // End of variables declaration//GEN-END:variables

    @Override
    public void savePreferences() {
        try {
            int proxyPort = Integer.parseInt(this.jTextFieldProxyPort.getText().trim());
            this.preferences.putInt(PreferencesAS2.PROXY_PORT, proxyPort);
        } catch (Exception e) {
            //just ignore this - the formerly value will be kept and the user will see this one he opens the preferences again
        }
        this.preferences.putBoolean(PreferencesAS2.AUTH_PROXY_USE, this.switchUseProxyAuthentification.isSelected());
        this.preferences.put(PreferencesAS2.PROXY_HOST, this.jTextFieldProxyURL.getText());
        this.preferences.putBoolean(PreferencesAS2.PROXY_USE, this.switchUseProxy.isSelected());
        this.preferences.put(PreferencesAS2.AUTH_PROXY_PASS, new String(this.jPasswordFieldProxyPass.getPassword()));
        this.preferences.put(PreferencesAS2.AUTH_PROXY_USER, this.jTextFieldProxyUser.getText());
    }

    @Override
    public ImageIcon getIcon() {
        return (new ImageIcon(IMAGE_PROXY));
    }

    @Override
    public String getTabResource() {
        return ("tab.proxy");
    }

}
