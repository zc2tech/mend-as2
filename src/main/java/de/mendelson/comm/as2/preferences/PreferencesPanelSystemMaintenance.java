package de.mendelson.comm.as2.preferences;

import de.mendelson.util.JTextFieldLimitDocument;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
import de.mendelson.util.uinotification.UINotification;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
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
 * Panel to define the inbox settings
 *
 * @author S.Heller
 * @version: $Revision: 35 $
 */
public class PreferencesPanelSystemMaintenance extends PreferencesPanel {

    private static final  MendelsonMultiResolutionImage ICON_MAINTENANCE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/maintenance.svg",
                    JDialogPreferences.IMAGE_HEIGHT);

    private static final  MecResourceBundle rb;
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
    private String preferencesStrAtLoadTime = "";

    /**
     * Creates new form PreferencesPanelDirectories
     */
    public PreferencesPanelSystemMaintenance(BaseClient baseClient) {
        this.initComponents();
        this.initializeHelp();
        this.preferences = new PreferencesClient(baseClient);
        if (this.preferences.getBoolean(PreferencesAS2.COMMUNITY_EDITION)) {
            this.switchDeleteStatsOlderThan.setVisible(false);
            this.jTextFieldDeleteStatsOlderThan.setVisible(false);
            this.jLabelDays2.setVisible(false);
            this.jLabelDeleteStatsOlderThan.setVisible( false );
            this.jPanelUIHelpDelStatistic.setVisible(false);
        }
        this.jComboBoxTimeUnit.addItem(new TimeUnitMaintenance(TimeUnitMaintenance.MULTIPLIER_DAY));
        this.jComboBoxTimeUnit.addItem(new TimeUnitMaintenance(TimeUnitMaintenance.MULTIPLIER_HOUR));
        this.jComboBoxTimeUnit.addItem(new TimeUnitMaintenance(TimeUnitMaintenance.MULTIPLIER_MINUTE));
        //set the max string length that could be entered to 5 - these are 00000 to 99999
        this.jTextFieldDeleteLogDirOlderThan.setDocument(new JTextFieldLimitDocument(5));
        this.jTextFieldDeleteMsgOlderThan.setDocument(new JTextFieldLimitDocument(5));
        this.jTextFieldDeleteStatsOlderThan.setDocument(new JTextFieldLimitDocument(5));
        this.jTextFieldDeleteTrackerOlderThan.setDocument(new JTextFieldLimitDocument(5));
    }

    private void initializeHelp() {
        this.jPanelUIHelpDelLogDirs.setToolTip(rb, "systemmaintenance.deleteoldlogdirs.help");
        this.jPanelUIHelpDelOldTransactions.setToolTip(rb, "systemmaintenance.deleteoldtransactions.help");
        this.jPanelUIHelpDelStatistic.setToolTip(rb, "systemmaintenance.deleteoldstatistic.help");
    }

    /**
     * Sets new preferences to this panel to changes/modify
     */
    @Override
    public void loadPreferences() {
        this.switchDeleteMsgOlderThan.setSelected(this.preferences.getBoolean(PreferencesAS2.AUTO_MSG_DELETE));
        this.jTextFieldDeleteMsgOlderThan.setText(String.valueOf(this.preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN)));
        this.jComboBoxTimeUnit.setSelectedItem(new TimeUnitMaintenance(this.preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S)));
        if (!this.preferences.getBoolean(PreferencesAS2.COMMUNITY_EDITION)) {
            this.switchDeleteStatsOlderThan.setSelected(this.preferences.getBoolean(PreferencesAS2.AUTO_STATS_DELETE));
            this.jTextFieldDeleteStatsOlderThan.setText(String.valueOf(this.preferences.getInt(PreferencesAS2.AUTO_STATS_DELETE_OLDERTHAN)));
        }
        this.switchDeleteLogDirOlderThan.setSelected(this.preferences.getBoolean(PreferencesAS2.AUTO_LOGDIR_DELETE));
        this.jTextFieldDeleteLogDirOlderThan.setText(String.valueOf(this.preferences.getInt(PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN)));
        this.switchDeleteTrackerOlderThan.setSelected(this.preferences.getBoolean(PreferencesAS2.AUTO_TRACKER_DELETE));
        this.jTextFieldDeleteTrackerOlderThan.setText(String.valueOf(this.preferences.getInt(PreferencesAS2.AUTO_TRACKER_DELETE_OLDERTHAN)));
        this.preferencesStrAtLoadTime = this.captureSettingsToStr();
    }

    /**
     * Helper method to find out if there are changes in the GUI before storing
     * them to the server
     */
    private String captureSettingsToStr() {
        StringBuilder builder = new StringBuilder();
        builder.append(PreferencesAS2.AUTO_MSG_DELETE).append("=")
                .append(this.switchDeleteMsgOlderThan.isSelected()).append(";");
        builder.append(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN).append("=")
                .append(this.jTextFieldDeleteMsgOlderThan.getText()).append(";");
        TimeUnitMaintenance timeUnitMaintenance = (TimeUnitMaintenance) jComboBoxTimeUnit.getSelectedItem();
        builder.append(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S).append("=")
                .append(String.valueOf(timeUnitMaintenance.getMultiplier())).append(";");
        builder.append(PreferencesAS2.AUTO_STATS_DELETE).append("=")
                .append(this.switchDeleteStatsOlderThan.isSelected()).append(";");
        builder.append(PreferencesAS2.AUTO_STATS_DELETE_OLDERTHAN).append("=")
                .append(this.jTextFieldDeleteStatsOlderThan.getText()).append(";");
        builder.append(PreferencesAS2.AUTO_LOGDIR_DELETE).append("=")
                .append(this.switchDeleteLogDirOlderThan.isSelected()).append(";");
        builder.append(PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN).append("=")
                .append(this.jTextFieldDeleteLogDirOlderThan.getText()).append(";");
        builder.append(PreferencesAS2.AUTO_TRACKER_DELETE).append("=")
                .append(this.switchDeleteTrackerOlderThan.isSelected()).append(";");
        builder.append(PreferencesAS2.AUTO_TRACKER_DELETE_OLDERTHAN).append("=")
                .append(this.jTextFieldDeleteTrackerOlderThan.getText()).append(";");
        return (builder.toString());
    }

    @Override
    public boolean preferencesAreModified() {
        return (!this.preferencesStrAtLoadTime.equals(this.captureSettingsToStr()));
    }

    /**
     * Stores the GUI settings in the preferences
     */
    @Override
    public void savePreferences() {
        try {
            int olderThantransactions = Integer.parseInt(this.jTextFieldDeleteMsgOlderThan.getText());
            //do not allow negative values or the 0
            if (olderThantransactions <= 0) {
                olderThantransactions = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN));
            }
            this.preferences.putInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN, olderThantransactions);
            this.preferences.putInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S,
                    (int) ((TimeUnitMaintenance) this.jComboBoxTimeUnit.getSelectedItem()).getMultiplier());
            this.preferences.putBoolean(PreferencesAS2.AUTO_MSG_DELETE, this.switchDeleteMsgOlderThan.isSelected());
            //stats auto delete capabilites
            if (!this.preferences.getBoolean(PreferencesAS2.COMMUNITY_EDITION)) {
                int olderThanStats = Integer.parseInt(this.jTextFieldDeleteStatsOlderThan.getText());
                if (olderThanStats <= 0) {
                    olderThanStats = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.AUTO_STATS_DELETE_OLDERTHAN));
                }
                this.preferences.putInt(PreferencesAS2.AUTO_STATS_DELETE_OLDERTHAN, olderThanStats);
                this.preferences.putBoolean(PreferencesAS2.AUTO_STATS_DELETE, this.switchDeleteStatsOlderThan.isSelected());
            }
            //log dir delete settings
            int olderThanLogDir = Integer.parseInt(this.jTextFieldDeleteLogDirOlderThan.getText());
            //do not allow negative values or the 0
            if (olderThanLogDir <= 0) {
                olderThanLogDir = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN));
            }
            this.preferences.putInt(PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN, olderThanLogDir);
            this.preferences.putBoolean(PreferencesAS2.AUTO_LOGDIR_DELETE, this.switchDeleteLogDirOlderThan.isSelected());
            //tracker delete settings
            int olderThanTracker = Integer.parseInt(this.jTextFieldDeleteTrackerOlderThan.getText());
            //do not allow negative values or the 0
            if (olderThanTracker <= 0) {
                olderThanTracker = Integer.parseInt(this.preferences.getDefaultValue(PreferencesAS2.AUTO_TRACKER_DELETE_OLDERTHAN));
            }
            this.preferences.putInt(PreferencesAS2.AUTO_TRACKER_DELETE_OLDERTHAN, olderThanTracker);
            this.preferences.putBoolean(PreferencesAS2.AUTO_TRACKER_DELETE, this.switchDeleteTrackerOlderThan.isSelected());
        } catch (Exception ex) {
            UINotification.instance().addNotification(ex);
        }
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
        jTextFieldDeleteMsgOlderThan = new javax.swing.JTextField();
        jTextFieldDeleteStatsOlderThan = new javax.swing.JTextField();
        jTextFieldDeleteTrackerOlderThan = new javax.swing.JTextField();
        jLabelDays2 = new javax.swing.JLabel();
        jLabelDays3 = new javax.swing.JLabel();
        jComboBoxTimeUnit = new javax.swing.JComboBox<>();
        jTextFieldDeleteLogDirOlderThan = new javax.swing.JTextField();
        jLabelDays1 = new javax.swing.JLabel();
        jPanelUIHelpDelLogDirs = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelUIHelpDelStatistic = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelUIHelpDelOldTransactions = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelSpaceAbove = new javax.swing.JPanel();
        switchDeleteMsgOlderThan = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchDeleteStatsOlderThan = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchDeleteLogDirOlderThan = new de.mendelson.util.toggleswitch.ToggleSwitch();
        switchDeleteTrackerOlderThan = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelDeleteMsgOlderThan = new javax.swing.JLabel();
        jLabelDeleteStatsOlderThan = new javax.swing.JLabel();
        jLabelDeleteLogDirOlderThan = new javax.swing.JLabel();
        jLabelDeleteTrackerOlderThan = new javax.swing.JLabel();
        jPanelSpace76346 = new javax.swing.JPanel();
        jLabelAutoDelete = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jPanelMargin.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelMargin.add(jPanelSpace, gridBagConstraints);

        jTextFieldDeleteMsgOlderThan.setPreferredSize(new java.awt.Dimension(50, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jTextFieldDeleteMsgOlderThan, gridBagConstraints);

        jTextFieldDeleteStatsOlderThan.setPreferredSize(new java.awt.Dimension(50, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jTextFieldDeleteStatsOlderThan, gridBagConstraints);

        jLabelDays2.setText(PreferencesPanelSystemMaintenance.rb.getResourceString( "label.days" ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelDays2, gridBagConstraints);

        jComboBoxTimeUnit.setPreferredSize(new java.awt.Dimension(135, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jComboBoxTimeUnit, gridBagConstraints);

        jTextFieldDeleteLogDirOlderThan.setPreferredSize(new java.awt.Dimension(50, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jTextFieldDeleteLogDirOlderThan, gridBagConstraints);

        jLabelDays1.setText(PreferencesPanelSystemMaintenance.rb.getResourceString( "label.days" ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelDays1, gridBagConstraints);

        jPanelUIHelpDelLogDirs.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        jPanelMargin.add(jPanelUIHelpDelLogDirs, gridBagConstraints);

        jPanelUIHelpDelStatistic.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        jPanelMargin.add(jPanelUIHelpDelStatistic, gridBagConstraints);

        jPanelUIHelpDelOldTransactions.setToolTipText(rb.getResourceString( "systemmaintenance.deleteoldlogdirs.help"));
        jPanelUIHelpDelOldTransactions.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        jPanelMargin.add(jPanelUIHelpDelOldTransactions, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 0, 10);
        jPanelMargin.add(jPanelSpaceAbove, gridBagConstraints);

        switchDeleteMsgOlderThan.setDisplayStatusText(true);
        switchDeleteMsgOlderThan.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(switchDeleteMsgOlderThan, gridBagConstraints);

        switchDeleteStatsOlderThan.setDisplayStatusText(true);
        switchDeleteStatsOlderThan.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(switchDeleteStatsOlderThan, gridBagConstraints);

        switchDeleteLogDirOlderThan.setDisplayStatusText(true);
        switchDeleteLogDirOlderThan.setHorizontalTextPosition(SwingConstants.LEFT
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(switchDeleteLogDirOlderThan, gridBagConstraints);

        jLabelDeleteMsgOlderThan.setText(PreferencesPanelSystemMaintenance.rb.getResourceString( "label.deletemsgolderthan"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelDeleteMsgOlderThan, gridBagConstraints);

        jLabelDeleteStatsOlderThan.setText(PreferencesPanelSystemMaintenance.rb.getResourceString( "label.deletestatsolderthan"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelDeleteStatsOlderThan, gridBagConstraints);

        jLabelDeleteLogDirOlderThan.setText(PreferencesPanelSystemMaintenance.rb.getResourceString( "label.deletelogdirolderthan"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelDeleteLogDirOlderThan, gridBagConstraints);

        jTextFieldDeleteTrackerOlderThan.setPreferredSize(new java.awt.Dimension(50, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jTextFieldDeleteTrackerOlderThan, gridBagConstraints);

        jLabelDays3.setText(PreferencesPanelSystemMaintenance.rb.getResourceString( "label.days" ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelDays3, gridBagConstraints);

        switchDeleteTrackerOlderThan.setDisplayStatusText(true);
        switchDeleteTrackerOlderThan.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(switchDeleteTrackerOlderThan, gridBagConstraints);

        jLabelDeleteTrackerOlderThan.setText(PreferencesPanelSystemMaintenance.rb.getResourceString( "label.deletetrackerolderthan"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelDeleteTrackerOlderThan, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 50, 10, 10);
        jPanelMargin.add(jPanelSpace76346, gridBagConstraints);

        jLabelAutoDelete.setText(PreferencesPanelSystemMaintenance.rb.getResourceString( "label.autodelete"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelAutoDelete, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelMargin, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<TimeUnitMaintenance> jComboBoxTimeUnit;
    private javax.swing.JLabel jLabelAutoDelete;
    private javax.swing.JLabel jLabelDays1;
    private javax.swing.JLabel jLabelDays2;
    private javax.swing.JLabel jLabelDays3;
    private javax.swing.JLabel jLabelDeleteLogDirOlderThan;
    private javax.swing.JLabel jLabelDeleteMsgOlderThan;
    private javax.swing.JLabel jLabelDeleteStatsOlderThan;
    private javax.swing.JLabel jLabelDeleteTrackerOlderThan;
    private javax.swing.JPanel jPanelMargin;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace76346;
    private javax.swing.JPanel jPanelSpaceAbove;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpDelLogDirs;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpDelOldTransactions;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpDelStatistic;
    private javax.swing.JTextField jTextFieldDeleteLogDirOlderThan;
    private javax.swing.JTextField jTextFieldDeleteMsgOlderThan;
    private javax.swing.JTextField jTextFieldDeleteStatsOlderThan;
    private javax.swing.JTextField jTextFieldDeleteTrackerOlderThan;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchDeleteLogDirOlderThan;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchDeleteMsgOlderThan;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchDeleteStatsOlderThan;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchDeleteTrackerOlderThan;
    // End of variables declaration//GEN-END:variables

    @Override
    public ImageIcon getIcon() {
        return (new ImageIcon(ICON_MAINTENANCE));
    }

    @Override
    public String getTabResource() {
        return ("tab.maintenance");
    }

    public static class TimeUnitMaintenance {

        public static final long MULTIPLIER_DAY = TimeUnit.DAYS.toSeconds(1);
        public static final long MULTIPLIER_HOUR = TimeUnit.HOURS.toSeconds(1);
        public static final long MULTIPLIER_MINUTE = TimeUnit.MINUTES.toSeconds(1);

        private final long MULTIPLIER;

        public TimeUnitMaintenance(final long MULTIPLIER) {
            this.MULTIPLIER = MULTIPLIER;
        }

        @Override
        public String toString() {
            if (this.getMultiplier() == MULTIPLIER_DAY) {
                return (rb.getResourceString("maintenancemultiplier.day"));
            } else if (this.getMultiplier() == MULTIPLIER_HOUR) {
                return (rb.getResourceString("maintenancemultiplier.hour"));
            } else if (this.getMultiplier() == MULTIPLIER_MINUTE) {
                return (rb.getResourceString("maintenancemultiplier.minute"));
            } else {
                return ("Unknown value " + this.getMultiplier());
            }
        }

        /**
         * Overwrite the equal method of object
         *
         * @param anObject object to compare
         */
        @Override
        public boolean equals(Object anObject) {
            if (anObject == this) {
                return (true);
            }
            if (anObject != null && anObject instanceof TimeUnitMaintenance) {
                TimeUnitMaintenance entry = (TimeUnitMaintenance) anObject;
                return (entry.getMultiplier() == this.getMultiplier());
            }
            return (false);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (int) (this.getMultiplier() ^ (this.getMultiplier() >>> 32));
            return hash;
        }

        /**
         * @return the set multiplier
         */
        public long getMultiplier() {
            return MULTIPLIER;
        }

    }

}
