//$Header: /as2/de/mendelson/comm/as2/preferences/PreferencesPanelInterface.java 27    18/07/24 11:05 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.balloontip.BalloonToolTip;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
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
 * Panel to define the interface preferences
 *
 * @author S.Heller
 * @version: $Revision: 27 $
 */
public class PreferencesPanelInterface extends PreferencesPanel {

    private final static MendelsonMultiResolutionImage IMAGE_INTERFACE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/interface.svg",
                    JDialogPreferences.IMAGE_HEIGHT);

    private final static MecResourceBundle rb;
    static{
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferencesInterface.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }
    private final PreferencesClient preferences;
    private String preferencesStrAtLoadTime = "";

    /**
     * Creates new form PreferencesPanelDirectories
     */
    public PreferencesPanelInterface(BaseClient baseClient) {        
        this.preferences = new PreferencesClient(baseClient);
        this.initComponents();
        this.initializeHelp();
    }

    private void initializeHelp() {
    }

    /**
     * Sets new preferences to this panel to changes/modify
     */
    @Override
    public void loadPreferences() {
        this.switchShowQuota.setSelected(
                this.preferences.getBoolean(PreferencesAS2.SHOW_QUOTA_NOTIFICATION_IN_PARTNER_CONFIG));
        this.switchShowHttpHeader.setSelected(
                this.preferences.getBoolean(PreferencesAS2.SHOW_HTTPHEADER_IN_PARTNER_CONFIG));
        this.switchCEM.setSelected(
                this.preferences.getBoolean(PreferencesAS2.CEM));
        this.switchOutboundStatusFiles.setSelected(
                this.preferences.getBoolean(PreferencesAS2.WRITE_OUTBOUND_STATUS_FILE));
        this.switchDisplaySecurityOverwriteLocalstation.setSelected(
                this.preferences.getBoolean(PreferencesAS2.SHOW_OVERWRITE_LOCALSTATION_SECURITY_IN_PARTNER_CONFIG));
        this.switchCheckRevocationLists.setSelected(
                this.preferences.getBoolean(PreferencesAS2.CHECK_REVOCATION_LISTS));
        this.switchCheckPartnerTLSCertificates.setSelected(
                this.preferences.getBoolean(PreferencesAS2.AUTO_IMPORT_CHANGED_PARTNER_TLS_CERTIFICATES));
        this.preferencesStrAtLoadTime = this.captureSettingsToStr();
    }

    /**Helper method to find out if there are changes in the GUI before storing them to the server*/
    private String captureSettingsToStr(){
        StringBuilder builder = new StringBuilder();
        builder.append( PreferencesAS2.SHOW_QUOTA_NOTIFICATION_IN_PARTNER_CONFIG ).append("=")
                .append( this.switchShowQuota.isSelected()).append(";");
        builder.append( PreferencesAS2.SHOW_HTTPHEADER_IN_PARTNER_CONFIG ).append("=")
                .append( this.switchShowHttpHeader.isSelected()).append(";");
        builder.append( PreferencesAS2.CEM ).append("=")
                .append( this.switchCEM.isSelected()).append(";");
        builder.append( PreferencesAS2.WRITE_OUTBOUND_STATUS_FILE ).append("=")
                .append( this.switchOutboundStatusFiles.isSelected()).append(";");
        builder.append( PreferencesAS2.SHOW_OVERWRITE_LOCALSTATION_SECURITY_IN_PARTNER_CONFIG ).append("=")
                .append( this.switchDisplaySecurityOverwriteLocalstation.isSelected()).append(";");
        builder.append( PreferencesAS2.CHECK_REVOCATION_LISTS ).append("=")
                .append( this.switchCheckRevocationLists.isSelected()).append(";");
        builder.append( PreferencesAS2.AUTO_IMPORT_CHANGED_PARTNER_TLS_CERTIFICATES ).append("=")
                .append( this.switchCheckPartnerTLSCertificates.isSelected()).append(";");
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

        jPanelSpace = new javax.swing.JPanel();
        jPanelSpace123 = new javax.swing.JPanel();
        jPanelSpace124 = new javax.swing.JPanel();
        jLabelShowQuota = new javax.swing.JLabel();
        jLabelCEM = new javax.swing.JLabel();
        jPanelUIHelpLabelShowHTTPHeader = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelDisplaySecurityOverwriteLocalstation = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        switchShowQuota = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchCEM = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchShowHttpHeader = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchOutboundStatusFiles = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchDisplaySecurityOverwriteLocalstation = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelSpace7743 = new javax.swing.JPanel();
        jPanelUIHelpLabelOutboundStatusFiles = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelCheckRevocationLists = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        switchCheckRevocationLists = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelUIHelpLabelCheckPartnerTLSCertificates = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        switchCheckPartnerTLSCertificates = new de.mendelson.util.toggleswitch.ToggleSwitch();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(jPanelSpace, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelSpace123, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelSpace124, gridBagConstraints);

        jLabelShowQuota.setText(this.rb.getResourceString( "label.showquota" ));
        jLabelShowQuota.setMaximumSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(jLabelShowQuota, gridBagConstraints);

        jLabelCEM.setText(this.rb.getResourceString( "label.cem" ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(jLabelCEM, gridBagConstraints);

        jPanelUIHelpLabelShowHTTPHeader.setToolTipText(this.rb.getResourceString( "label.showhttpheader.help" ));
        jPanelUIHelpLabelShowHTTPHeader.setText(this.rb.getResourceString( "label.showhttpheader" ));
        jPanelUIHelpLabelShowHTTPHeader.setTooltipWidth(350);
        jPanelUIHelpLabelShowHTTPHeader.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_TOP
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelUIHelpLabelShowHTTPHeader, gridBagConstraints);

        jPanelUIHelpLabelDisplaySecurityOverwriteLocalstation.setToolTipText(this.rb.getResourceString( "label.showsecurityoverwrite.help" ));
        jPanelUIHelpLabelDisplaySecurityOverwriteLocalstation.setText(this.rb.getResourceString( "label.showsecurityoverwrite" ));
        jPanelUIHelpLabelDisplaySecurityOverwriteLocalstation.setTooltipWidth(350);
        jPanelUIHelpLabelDisplaySecurityOverwriteLocalstation.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_TOP
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelUIHelpLabelDisplaySecurityOverwriteLocalstation, gridBagConstraints);

        switchShowQuota.setDisplayStatusText(true);
        switchShowQuota.setHorizontalTextPosition(SwingConstants.LEFT
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(switchShowQuota, gridBagConstraints);

        switchCEM.setDisplayStatusText(true);
        switchCEM.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(switchCEM, gridBagConstraints);

        switchShowHttpHeader.setDisplayStatusText(true);
        switchShowHttpHeader.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(switchShowHttpHeader, gridBagConstraints);

        switchOutboundStatusFiles.setDisplayStatusText(true);
        switchOutboundStatusFiles.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(switchOutboundStatusFiles, gridBagConstraints);

        switchDisplaySecurityOverwriteLocalstation.setDisplayStatusText(true);
        switchDisplaySecurityOverwriteLocalstation.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(switchDisplaySecurityOverwriteLocalstation, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 5, 5);
        add(jPanelSpace7743, gridBagConstraints);

        jPanelUIHelpLabelOutboundStatusFiles.setToolTipText(this.rb.getResourceString( "label.outboundstatusfiles.help" ));
        jPanelUIHelpLabelOutboundStatusFiles.setText(this.rb.getResourceString( "label.outboundstatusfiles" ));
        jPanelUIHelpLabelOutboundStatusFiles.setTooltipWidth(350);
        jPanelUIHelpLabelOutboundStatusFiles.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_BOTTOM
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelUIHelpLabelOutboundStatusFiles, gridBagConstraints);

        jPanelUIHelpLabelCheckRevocationLists.setToolTipText(this.rb.getResourceString( "label.checkrevocationlists.help" ));
        jPanelUIHelpLabelCheckRevocationLists.setText(this.rb.getResourceString( "label.checkrevocationlists" ));
        jPanelUIHelpLabelCheckRevocationLists.setTooltipWidth(350);
        jPanelUIHelpLabelCheckRevocationLists.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_BOTTOM
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelUIHelpLabelCheckRevocationLists, gridBagConstraints);

        switchCheckRevocationLists.setDisplayStatusText(true);
        switchCheckRevocationLists.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(switchCheckRevocationLists, gridBagConstraints);

        jPanelUIHelpLabelCheckPartnerTLSCertificates.setToolTipText(this.rb.getResourceString( "autoimport.tls.help"));
        jPanelUIHelpLabelCheckPartnerTLSCertificates.setText(this.rb.getResourceString( "autoimport.tls"));
        jPanelUIHelpLabelCheckPartnerTLSCertificates.setTooltipWidth(350);
        jPanelUIHelpLabelCheckPartnerTLSCertificates.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_BOTTOM
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelUIHelpLabelCheckPartnerTLSCertificates, gridBagConstraints);

        switchCheckPartnerTLSCertificates.setDisplayStatusText(true);
        switchCheckPartnerTLSCertificates.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(switchCheckPartnerTLSCertificates, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelCEM;
    private javax.swing.JLabel jLabelShowQuota;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace123;
    private javax.swing.JPanel jPanelSpace124;
    private javax.swing.JPanel jPanelSpace7743;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelCheckPartnerTLSCertificates;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelCheckRevocationLists;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelDisplaySecurityOverwriteLocalstation;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelOutboundStatusFiles;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelShowHTTPHeader;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchCEM;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchCheckPartnerTLSCertificates;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchCheckRevocationLists;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchDisplaySecurityOverwriteLocalstation;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchOutboundStatusFiles;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchShowHttpHeader;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchShowQuota;
    // End of variables declaration//GEN-END:variables

    @Override
    public void savePreferences() {
        this.preferences.putBoolean(PreferencesAS2.SHOW_HTTPHEADER_IN_PARTNER_CONFIG, 
                this.switchShowHttpHeader.isSelected());
        this.preferences.putBoolean(PreferencesAS2.SHOW_QUOTA_NOTIFICATION_IN_PARTNER_CONFIG, 
                this.switchShowQuota.isSelected());
        this.preferences.putBoolean(PreferencesAS2.CEM, 
                this.switchCEM.isSelected());
        this.preferences.putBoolean(PreferencesAS2.WRITE_OUTBOUND_STATUS_FILE, 
                this.switchOutboundStatusFiles.isSelected());
        this.preferences.putBoolean(PreferencesAS2.SHOW_OVERWRITE_LOCALSTATION_SECURITY_IN_PARTNER_CONFIG, 
                this.switchDisplaySecurityOverwriteLocalstation.isSelected());
        this.preferences.putBoolean(PreferencesAS2.CHECK_REVOCATION_LISTS, 
                this.switchCheckRevocationLists.isSelected());
        this.preferences.putBoolean(PreferencesAS2.AUTO_IMPORT_CHANGED_PARTNER_TLS_CERTIFICATES, 
                this.switchCheckPartnerTLSCertificates.isSelected());
    }

    @Override
    public ImageIcon getIcon() {
        return (new ImageIcon(IMAGE_INTERFACE.toMinResolution(JDialogPreferences.IMAGE_HEIGHT)));
    }

    @Override
    public String getTabResource() {
        return ("tab.interface");
    }

}
