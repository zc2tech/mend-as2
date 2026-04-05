package de.mendelson.comm.as2.preferences;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.filesystemview.RemoteFileBrowser;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
import de.mendelson.util.tables.JTableColumnResizer;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 *Panel to define the directory preferences
 * @author S.Heller
 * @version: $Revision: 28 $
 */
public class PreferencesPanelDirectories extends PreferencesPanel {

    private final static MendelsonMultiResolutionImage ICON_FOLDER
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/folder.svg", 
                    JDialogPreferences.IMAGE_HEIGHT);
    
    /**Localize the GUI*/
    private MecResourceBundle rb = null;
    /**GUI prefs*/
    private final PreferencesClient preferences;
    private final BaseClient baseClient;
    private String preferencesStrAtLoadTime = "";

    /** Creates new form PreferencesPanelDirectories */
    public PreferencesPanelDirectories(BaseClient baseClient) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.preferences = new PreferencesClient(baseClient);
        this.baseClient = baseClient;
        this.initComponents();
        this.initializeHelp();
        this.jTable.setRowHeight(TableModelPreferencesDir.ROW_HEIGHT);
        this.setButtonState();
    }

    private void initializeHelp(){
        this.jPanelUIHelpPartnerDir.setToolTip( this.rb, "receipt.subdir.help");
    }
    
    /**checks the state of the buttons, it depends on the selection in
     *the table
     */
    private void setButtonState() {
        this.jButtonChange.setEnabled(this.jTable.getSelectedRow() >= 0);
    }

    /**Sets new preferences to this panel to changes/modify
     */
    @Override
    public void loadPreferences() {
        String[] keys = new String[]{
            PreferencesAS2.DIR_MSG,};
        //fill in data
        List<PreferencesObjectKeyValue> list = new ArrayList<PreferencesObjectKeyValue>();
        for (String key : keys) {
            list.add(new PreferencesObjectKeyValue(key,
                    this.preferences.get(key)));
        }
        ((TableModelPreferencesDir) this.jTable.getModel()).passNewData(list);
        JTableColumnResizer.adjustColumnWidthByContent(this.jTable);
        this.switchButtonReceiverSubdirectory.setSelected(this.preferences.getBoolean(PreferencesAS2.RECEIPT_PARTNER_SUBDIR));
        this.preferencesStrAtLoadTime = this.captureSettingsToStr();
    }

    /**Helper method to find out if there are changes in the GUI before storing them to the server*/
    private String captureSettingsToStr(){
        StringBuilder builder = new StringBuilder();
        PreferencesObjectKeyValue directory = ((TableModelPreferencesDir) this.jTable.getModel()).getPreference(0);
        String currentPath = directory.getValue();
        builder.append( PreferencesAS2.DIR_MSG ).append("=")
                .append( currentPath).append(";");
        builder.append( PreferencesAS2.RECEIPT_PARTNER_SUBDIR ).append("=")
                .append( this.switchButtonReceiverSubdirectory.isSelected()).append(";");
        return( builder.toString() );
    }
    
    @Override
    public boolean preferencesAreModified() {
        return( !this.preferencesStrAtLoadTime.equals(this.captureSettingsToStr()) );
    }
    
    
    /**Modifies the selected row in the table*/
    private void modifySelection() {
        PreferencesObjectKeyValue directory = ((TableModelPreferencesDir) this.jTable.getModel()).getPreference(this.jTable.getSelectedRow());
        String existingPath = directory.getValue();
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        RemoteFileBrowser browser = new RemoteFileBrowser(parent, this.baseClient,
                this.rb.getResourceString("remotedir.select"));
        browser.setDirectoriesOnly(true);
        browser.setSelectedFile(existingPath);
        browser.setVisible(true);
        String selectedPath = browser.getSelectedPath();
        if (selectedPath != null) {
            directory.setValue(selectedPath);
            ((TableModelPreferencesDir) this.jTable.getModel()).fireTableDataChanged();
            this.preferences.put(directory.getKey(), selectedPath);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelMargin = new javax.swing.JPanel();
        jPanelDirSelection = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jButtonChange = new javax.swing.JButton();
        jPanelUIHelpPartnerDir = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelSpacer = new javax.swing.JPanel();
        jPanelSpaceAbove = new javax.swing.JPanel();
        switchButtonReceiverSubdirectory = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelReceiverSubdirectory = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jPanelMargin.setLayout(new java.awt.GridBagLayout());

        jPanelDirSelection.setLayout(new java.awt.GridBagLayout());

        jTable.setModel(new TableModelPreferencesDir());
        jTable.setShowHorizontalLines(false);
        jTable.setShowVerticalLines(false);
        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMouseClicked(evt);
            }
        });
        jScrollPane.setViewportView(jTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDirSelection.add(jScrollPane, gridBagConstraints);

        jButtonChange.setText(this.rb.getResourceString( "button.modify" ));
        jButtonChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDirSelection.add(jButtonChange, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanelMargin.add(jPanelDirSelection, gridBagConstraints);

        jPanelUIHelpPartnerDir.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanelMargin.add(jPanelUIHelpPartnerDir, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelMargin.add(jPanelSpacer, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMargin.add(jPanelSpaceAbove, gridBagConstraints);

        switchButtonReceiverSubdirectory.setDisplayStatusText(true);
        switchButtonReceiverSubdirectory.setHorizontalTextPosition(SwingConstants.LEFT);
        switchButtonReceiverSubdirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchButtonReceiverSubdirectoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 5, 5);
        jPanelMargin.add(switchButtonReceiverSubdirectory, gridBagConstraints);

        jLabelReceiverSubdirectory.setText(this.rb.getResourceString( "receipt.subdir" ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMargin.add(jLabelReceiverSubdirectory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelMargin, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
        this.setButtonState();
        if (evt.getClickCount() == 2) {
            this.modifySelection();
        }
    }//GEN-LAST:event_jTableMouseClicked

    private void jButtonChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeActionPerformed
        this.modifySelection();
    }//GEN-LAST:event_jButtonChangeActionPerformed

    private void switchButtonReceiverSubdirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchButtonReceiverSubdirectoryActionPerformed
        this.preferences.putBoolean(PreferencesAS2.RECEIPT_PARTNER_SUBDIR, this.switchButtonReceiverSubdirectory.isSelected());
    }//GEN-LAST:event_switchButtonReceiverSubdirectoryActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonChange;
    private javax.swing.JLabel jLabelReceiverSubdirectory;
    private javax.swing.JPanel jPanelDirSelection;
    private javax.swing.JPanel jPanelMargin;
    private javax.swing.JPanel jPanelSpaceAbove;
    private javax.swing.JPanel jPanelSpacer;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpPartnerDir;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchButtonReceiverSubdirectory;
    // End of variables declaration//GEN-END:variables

    @Override
    public void savePreferences() {
        //NOP
    }

    @Override
    public ImageIcon getIcon() {
        return (new ImageIcon( ICON_FOLDER));
    }

    @Override
    public String getTabResource() {
        return ("tab.dir");
    }
}
