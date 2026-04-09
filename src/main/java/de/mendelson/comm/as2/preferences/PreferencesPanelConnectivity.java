package de.mendelson.comm.as2.preferences;

import de.mendelson.comm.as2.client.AS2Gui;
import de.mendelson.util.JTextFieldLimitDocument;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.balloontip.BalloonToolTip;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
import de.mendelson.util.uinotification.UINotification;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Panel to define the interface preferences
 *
 * @author S.Heller
 * @version: $Revision: 9 $
 */
public class PreferencesPanelConnectivity extends PreferencesPanel {

    private final static MendelsonMultiResolutionImage IMAGE_CONNECTIVITY
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/ports.svg",
                    JDialogPreferences.IMAGE_HEIGHT);
    private final static MendelsonMultiResolutionImage IMAGE_WARNING
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/warning_sign.svg",
                    AS2Gui.IMAGE_SIZE_MENU_ITEM);
    private String oldHTTPPort = "";
    private String oldHTTPSPort = "";
    private String oldConnectionCountInbound = "";
    private String preferencesStrAtLoadTime = "";

    /**
     * Localize the GUI
     */
    private final static MecResourceBundle rb;
    static{
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }

    /**
     * GUI prefs
     */
    private final PreferencesClient preferences;

    /**
     * Creates new form PreferencesPanelDirectories
     */
    public PreferencesPanelConnectivity(BaseClient baseClient) {
        this.preferences = new PreferencesClient(baseClient);
        this.initComponents();
        this.initializeHelp();
        this.jTextFieldSendHttpTimeout.setDocument(new JTextFieldLimitDocument(5));
        this.jTexFieldRetryCount.setDocument(new JTextFieldLimitDocument(5));
        this.jTextFieldRetryWaittime.setDocument(new JTextFieldLimitDocument(5));
        this.jTextFieldOutboundConnections.setDocument(new JTextFieldLimitDocument(5));
        this.jTextFieldInboundConnections.setDocument(new JTextFieldLimitDocument(5));
        //max port is 65535 because its a unsigned 16 bit value in TCP/IP
        this.jTextFieldHTTPPort.setDocument(new JTextFieldLimitDocument(5));
        //max port is 65535 because its a unsigned 16 bit value in TCP/IP
        this.jTextFieldHTTPSPort.setDocument(new JTextFieldLimitDocument(5));
        this.jLabelWarningJettyConfigAccess1.setIcon(new ImageIcon(IMAGE_WARNING.toMinResolution(AS2Gui.IMAGE_SIZE_MENU_ITEM)));
        this.jLabelWarningJettyConfigAccess2.setIcon(new ImageIcon(IMAGE_WARNING.toMinResolution(AS2Gui.IMAGE_SIZE_MENU_ITEM)));
        this.jLabelWarningJettyConfigAccess3.setIcon(new ImageIcon(IMAGE_WARNING.toMinResolution(AS2Gui.IMAGE_SIZE_MENU_ITEM)));
    }

    private void initializeHelp() {
        this.jPanelUIHelpLabelSendTimeout.setTooltipWidth(350);
        this.jPanelUIHelpLabelConnectionRetryCount.setTooltipWidth(350);
        this.jPanelUIHelpLabelConnectionRetryTime.setTooltipWidth(350);
        this.jPanelUIHelpLabelMaxOutboundConnections.setTooltipWidth(350);
        this.jPanelUIHelpLabelHTTPPort.setTooltipWidth(350);
        this.jPanelUIHelpLabelHTTPSPort.setTooltipWidth(350);
        this.jPanelUIHelpLabelMaxInboundConnections.setTooltipWidth(350);
    }

    /**
     * Sets new preferences to this panel to changes/modify
     */
    @Override
    public void loadPreferences() {
        this.switchTrustAllServerCerts.setSelected(this.preferences.getBoolean(PreferencesAS2.TLS_TRUST_ALL_REMOTE_SERVER_CERTIFICATES));
        this.switchStrictHostnameCheck.setSelected(this.preferences.getBoolean(PreferencesAS2.TLS_STRICT_HOST_CHECK));
        this.jTextFieldSendHttpTimeout.setText(this.preferences.get(PreferencesAS2.HTTP_SEND_TIMEOUT));
        this.jTexFieldRetryCount.setText(this.preferences.get(PreferencesAS2.MAX_CONNECTION_RETRY_COUNT));
        this.jTextFieldRetryWaittime.setText(this.preferences.get(PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S));
        this.jTextFieldOutboundConnections.setText(this.preferences.get(PreferencesAS2.MAX_OUTBOUND_CONNECTIONS));
        this.oldHTTPPort = this.preferences.get(PreferencesAS2.HTTP_LISTEN_PORT);
        this.oldHTTPSPort = this.preferences.get(PreferencesAS2.HTTPS_LISTEN_PORT);
        this.oldConnectionCountInbound = this.preferences.get(PreferencesAS2.MAX_INBOUND_CONNECTIONS);
        this.jTextFieldHTTPPort.setText(oldHTTPPort);
        this.jTextFieldHTTPSPort.setText(oldHTTPSPort);       
        this.jTextFieldInboundConnections.setText(oldConnectionCountInbound);  
        boolean jettySettingsCouldBeChanged
                = this.preferences.getBoolean(PreferencesAS2.EMBEDDED_HTTP_SERVER_SETTINGS_ACCESSIBLE)
                && this.preferences.getBoolean(PreferencesAS2.EMBEDDED_HTTP_SERVER_STARTED);
        this.jTextFieldHTTPPort.setEnabled(jettySettingsCouldBeChanged);
        this.jTextFieldHTTPSPort.setEnabled(jettySettingsCouldBeChanged);
        this.jTextFieldHTTPPort.setEditable(jettySettingsCouldBeChanged);
        this.jTextFieldHTTPPort.setEditable(jettySettingsCouldBeChanged);
        this.jTextFieldInboundConnections.setEditable(jettySettingsCouldBeChanged);
        this.jLabelWarningJettyConfigAccess1.setVisible(!jettySettingsCouldBeChanged);
        this.jLabelWarningJettyConfigAccess2.setVisible(!jettySettingsCouldBeChanged);
        this.jLabelWarningJettyConfigAccess3.setVisible(!jettySettingsCouldBeChanged);
        this.preferencesStrAtLoadTime = this.captureSettingsToStr();
    }

    /**Helper method to find out if there are changes in the GUI before storing them to the server*/
    private String captureSettingsToStr(){
        StringBuilder builder = new StringBuilder();
        builder.append( PreferencesAS2.TLS_TRUST_ALL_REMOTE_SERVER_CERTIFICATES ).append("=")
                .append( this.switchTrustAllServerCerts.isSelected()).append(";");                
        builder.append( PreferencesAS2.TLS_STRICT_HOST_CHECK ).append("=")
                .append( this.switchStrictHostnameCheck.isSelected()).append(";");
        builder.append( PreferencesAS2.HTTP_SEND_TIMEOUT ).append("=")
                .append( this.jTextFieldSendHttpTimeout.getText()).append(";");
        builder.append( PreferencesAS2.MAX_CONNECTION_RETRY_COUNT ).append("=")
                .append( this.jTexFieldRetryCount.getText()).append(";");
        builder.append( PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S ).append("=")
                .append( this.jTextFieldRetryWaittime.getText()).append(";");
        builder.append( PreferencesAS2.MAX_OUTBOUND_CONNECTIONS ).append("=")
                .append( this.jTextFieldOutboundConnections.getText()).append(";");
        builder.append( PreferencesAS2.HTTP_LISTEN_PORT ).append("=")
                .append( this.jTextFieldHTTPPort.getText()).append(";");
        builder.append( PreferencesAS2.HTTPS_LISTEN_PORT ).append("=")
                .append( this.jTextFieldHTTPSPort.getText()).append(";");
        builder.append( PreferencesAS2.MAX_INBOUND_CONNECTIONS ).append("=")
                .append( this.jTextFieldInboundConnections.getText()).append(";");
        return( builder.toString() );
    }
    
    
    @Override
    public boolean preferencesAreModified() {
        return( !this.preferencesStrAtLoadTime.equals(this.captureSettingsToStr()) );
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
        jPanelTrustAllHostNames = new javax.swing.JPanel();
        jPanelSpaceStrictHostCheck = new javax.swing.JPanel();
        switchStrictHostnameCheck = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelUIHelpLabelConnectionRetryTime = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelConnectionRetryCount = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelMaxOutboundConnections = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelSendTimeout = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jTextFieldRetryWaittime = new javax.swing.JTextField();
        jLabelSeconds = new javax.swing.JLabel();
        jTexFieldRetryCount = new javax.swing.JTextField();
        jTextFieldOutboundConnections = new javax.swing.JTextField();
        jTextFieldSendHttpTimeout = new javax.swing.JTextField();
        jLabelMilliseconds = new javax.swing.JLabel();
        jPanelUIHelpLabelHTTPPort = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jTextFieldHTTPSPort = new javax.swing.JTextField();
        jPanelUIHelpLabelHTTPSPort = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jTextFieldHTTPPort = new javax.swing.JTextField();
        jLabelWarningJettyConfigAccess1 = new javax.swing.JLabel();
        jLabelWarningJettyConfigAccess2 = new javax.swing.JLabel();
        jPanelUIHelpLabelMaxInboundConnections = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jTextFieldInboundConnections = new javax.swing.JTextField();
        jLabelWarningJettyConfigAccess3 = new javax.swing.JLabel();
        switchTrustAllServerCerts = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelUIHelpLabelTrustAllServerCerts = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelStrictHostnameCheck = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelSpace875 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jPanelMargin.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelMargin.add(jPanelSpace, gridBagConstraints);

        jPanelTrustAllHostNames.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelTrustAllHostNames.add(jPanelSpaceStrictHostCheck, gridBagConstraints);

        switchStrictHostnameCheck.setDisplayStatusText(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelTrustAllHostNames.add(switchStrictHostnameCheck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jPanelTrustAllHostNames, gridBagConstraints);

        jPanelUIHelpLabelConnectionRetryTime.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString( "label.retry.waittime.help"));
        jPanelUIHelpLabelConnectionRetryTime.setText(PreferencesPanelConnectivity.rb.getResourceString( "label.retry.waittime"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelUIHelpLabelConnectionRetryTime, gridBagConstraints);

        jPanelUIHelpLabelConnectionRetryCount.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString( "label.retry.max.help"));
        jPanelUIHelpLabelConnectionRetryCount.setText(PreferencesPanelConnectivity.rb.getResourceString( "label.retry.max"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelUIHelpLabelConnectionRetryCount, gridBagConstraints);

        jPanelUIHelpLabelMaxOutboundConnections.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString("label.max.outboundconnections.help"));
        jPanelUIHelpLabelMaxOutboundConnections.setText(PreferencesPanelConnectivity.rb.getResourceString("label.max.outboundconnections"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelUIHelpLabelMaxOutboundConnections, gridBagConstraints);

        jPanelUIHelpLabelSendTimeout.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString( "label.httpsend.timeout.help"));
        jPanelUIHelpLabelSendTimeout.setText(PreferencesPanelConnectivity.rb.getResourceString( "label.httpsend.timeout"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelUIHelpLabelSendTimeout, gridBagConstraints);

        jTextFieldRetryWaittime.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldRetryWaittime.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jTextFieldRetryWaittime, gridBagConstraints);

        jLabelSeconds.setText(PreferencesPanelConnectivity.rb.getResourceString( "label.sec"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelMargin.add(jLabelSeconds, gridBagConstraints);

        jTexFieldRetryCount.setMinimumSize(new java.awt.Dimension(50, 20));
        jTexFieldRetryCount.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jTexFieldRetryCount, gridBagConstraints);

        jTextFieldOutboundConnections.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldOutboundConnections.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jTextFieldOutboundConnections, gridBagConstraints);

        jTextFieldSendHttpTimeout.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldSendHttpTimeout.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jTextFieldSendHttpTimeout, gridBagConstraints);

        jLabelMilliseconds.setText("ms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelMargin.add(jLabelMilliseconds, gridBagConstraints);

        jPanelUIHelpLabelHTTPPort.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString("label.httpport.help"));
        jPanelUIHelpLabelHTTPPort.setText(PreferencesPanelConnectivity.rb.getResourceString("label.httpport"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelUIHelpLabelHTTPPort, gridBagConstraints);

        jTextFieldHTTPSPort.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldHTTPSPort.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jTextFieldHTTPSPort, gridBagConstraints);

        jPanelUIHelpLabelHTTPSPort.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString("label.httpsport.help"));
        jPanelUIHelpLabelHTTPSPort.setText(PreferencesPanelConnectivity.rb.getResourceString("label.httpsport"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelUIHelpLabelHTTPSPort, gridBagConstraints);

        jTextFieldHTTPPort.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldHTTPPort.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jTextFieldHTTPPort, gridBagConstraints);

        jLabelWarningJettyConfigAccess1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image16x16.gif"))); // NOI18N
        jLabelWarningJettyConfigAccess1.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString("embedded.httpconfig.not.available"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jLabelWarningJettyConfigAccess1, gridBagConstraints);

        jLabelWarningJettyConfigAccess2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image16x16.gif"))); // NOI18N
        jLabelWarningJettyConfigAccess2.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString("embedded.httpconfig.not.available"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jLabelWarningJettyConfigAccess2, gridBagConstraints);

        jPanelUIHelpLabelMaxInboundConnections.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString("label.max.inboundconnections.help"));
        jPanelUIHelpLabelMaxInboundConnections.setText(PreferencesPanelConnectivity.rb.getResourceString("label.max.inboundconnections"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelUIHelpLabelMaxInboundConnections, gridBagConstraints);

        jTextFieldInboundConnections.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldInboundConnections.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jTextFieldInboundConnections, gridBagConstraints);

        jLabelWarningJettyConfigAccess3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image16x16.gif"))); // NOI18N
        jLabelWarningJettyConfigAccess3.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString("embedded.httpconfig.not.available"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jLabelWarningJettyConfigAccess3, gridBagConstraints);

        switchTrustAllServerCerts.setDisplayStatusText(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(switchTrustAllServerCerts, gridBagConstraints);

        jPanelUIHelpLabelTrustAllServerCerts.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString( "label.trustallservercerts.help" ));
        jPanelUIHelpLabelTrustAllServerCerts.setText(PreferencesPanelConnectivity.rb.getResourceString( "label.trustallservercerts" ));
        jPanelUIHelpLabelTrustAllServerCerts.setTooltipWidth(350);
        jPanelUIHelpLabelTrustAllServerCerts.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_BOTTOM
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelUIHelpLabelTrustAllServerCerts, gridBagConstraints);

        jPanelUIHelpLabelStrictHostnameCheck.setToolTipText(PreferencesPanelConnectivity.rb.getResourceString( "label.stricthostcheck.help" ));
        jPanelUIHelpLabelStrictHostnameCheck.setText(PreferencesPanelConnectivity.rb.getResourceString( "label.stricthostcheck" ));
        jPanelUIHelpLabelStrictHostnameCheck.setTooltipWidth(350);
        jPanelUIHelpLabelStrictHostnameCheck.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_BOTTOM
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelMargin.add(jPanelUIHelpLabelStrictHostnameCheck, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        jPanelMargin.add(jPanelSpace875, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 0, 0);
        add(jPanelMargin, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelMilliseconds;
    private javax.swing.JLabel jLabelSeconds;
    private javax.swing.JLabel jLabelWarningJettyConfigAccess1;
    private javax.swing.JLabel jLabelWarningJettyConfigAccess2;
    private javax.swing.JLabel jLabelWarningJettyConfigAccess3;
    private javax.swing.JPanel jPanelMargin;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace875;
    private javax.swing.JPanel jPanelSpaceStrictHostCheck;
    private javax.swing.JPanel jPanelTrustAllHostNames;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelConnectionRetryCount;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelConnectionRetryTime;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelHTTPPort;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelHTTPSPort;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelMaxInboundConnections;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelMaxOutboundConnections;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelSendTimeout;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelStrictHostnameCheck;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelTrustAllServerCerts;
    private javax.swing.JTextField jTexFieldRetryCount;
    private javax.swing.JTextField jTextFieldHTTPPort;
    private javax.swing.JTextField jTextFieldHTTPSPort;
    private javax.swing.JTextField jTextFieldInboundConnections;
    private javax.swing.JTextField jTextFieldOutboundConnections;
    private javax.swing.JTextField jTextFieldRetryWaittime;
    private javax.swing.JTextField jTextFieldSendHttpTimeout;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchStrictHostnameCheck;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchTrustAllServerCerts;
    // End of variables declaration//GEN-END:variables

    @Override
    public void savePreferences() {
        boolean serverRestartRequired = false;
        this.preferences.putBoolean(PreferencesAS2.TLS_TRUST_ALL_REMOTE_SERVER_CERTIFICATES, this.switchTrustAllServerCerts.isSelected());
        this.preferences.putBoolean(PreferencesAS2.TLS_STRICT_HOST_CHECK, this.switchStrictHostnameCheck.isSelected());
        try {
            int value = Integer.parseInt(this.jTextFieldSendHttpTimeout.getText().trim());
            if (value < 0) {
                value = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.HTTP_SEND_TIMEOUT));
            }
            this.preferences.putInt(PreferencesAS2.HTTP_SEND_TIMEOUT, value);
        } catch (NumberFormatException e) {
            //nop
        }
        try {
            int value = Integer.parseInt(this.jTexFieldRetryCount.getText().trim());
            if (value < 0) {
                value = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.MAX_CONNECTION_RETRY_COUNT));
            }
            this.preferences.putInt(PreferencesAS2.MAX_CONNECTION_RETRY_COUNT, value);
        } catch (NumberFormatException e) {
            //nop
        }
        try {
            int value = Integer.parseInt(this.jTextFieldRetryWaittime.getText().trim());
            if (value < 0) {
                value = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S));
            }
            this.preferences.putInt(PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S, value);
        } catch (NumberFormatException e) {
            //nop
        }
        try {
            int value = Integer.parseInt(this.jTextFieldOutboundConnections.getText().trim());
            if (value < 0) {
                value = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.MAX_OUTBOUND_CONNECTIONS));
            }
            this.preferences.putInt(PreferencesAS2.MAX_OUTBOUND_CONNECTIONS, value);
        } catch (NumberFormatException e) {
            //nop
        }
        //is there a change in the HTTP port?
        if (!this.oldHTTPPort.equals(this.jTextFieldHTTPPort.getText().trim())) {
            try {
                int value = Integer.parseInt(this.jTextFieldHTTPPort.getText().trim());
                if (value < 0) {
                    value = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.HTTP_LISTEN_PORT));
                }
                this.preferences.putInt(PreferencesAS2.HTTP_LISTEN_PORT, value);
                serverRestartRequired = true;
            } catch (NumberFormatException e) {
                //nop
            }
        }
        //is there a change in the HTTPS port?
        if (!this.oldHTTPSPort.equals(this.jTextFieldHTTPSPort.getText().trim())) {
            try {
                int value = Integer.parseInt(this.jTextFieldHTTPSPort.getText().trim());
                if (value < 0) {
                    value = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.HTTPS_LISTEN_PORT));
                }
                this.preferences.putInt(PreferencesAS2.HTTPS_LISTEN_PORT, value);
                serverRestartRequired = true;
            } catch (NumberFormatException e) {
                //nop
            }
        }
        //is there a change in the max inbound connections?
        if (!this.oldConnectionCountInbound.equals(this.jTextFieldInboundConnections.getText().trim())) {
            try {
                int value = Integer.parseInt(this.jTextFieldInboundConnections.getText().trim());
                if (value < 0) {
                    value = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.MAX_INBOUND_CONNECTIONS));
                }
                this.preferences.putInt(PreferencesAS2.MAX_INBOUND_CONNECTIONS, value);
                serverRestartRequired = true;
            } catch (NumberFormatException e) {
                //nop
            }
        }
        if (serverRestartRequired) {
            UINotification.instance().addNotification(
                    IMAGE_CONNECTIVITY,
                    UINotification.TYPE_INFORMATION,
                    rb.getResourceString("title"),
                    rb.getResourceString("warning.serverrestart.required"));
        }
    }

    @Override
    public ImageIcon getIcon() {
        return (new ImageIcon(IMAGE_CONNECTIVITY));
    }

    @Override
    public String getTabResource() {
        return ("tab.connectivity");
    }

}
