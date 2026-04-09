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

package de.mendelson.comm.as2.preferences;

import de.mendelson.util.ImageButtonBar;
import de.mendelson.util.ImageButtonBarUI;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.uinotification.UINotification;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Dialog to configure user-level preferences (not system-level)
 * Unlike JDialogPreferences which handles system-level settings,
 * this dialog handles user-specific settings stored per-user in database
 *
 * @author Julian Xu
 */
public class JDialogUserPreferences extends JDialog {

    public static final int IMAGE_HEIGHT = ImageButtonBarUI.DEFAULT_IMAGE_HEIGHT;

    /**
     * ResourceBundle to localize the GUI
     */
    private static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * stores all available panels
     */
    private final List<PreferencesPanel> panelList = new ArrayList<PreferencesPanel>();
    private ImageButtonBar buttonBar; // May be null if only one tab
    private boolean okPressed = false;

    /**
     * Creates new user preferences dialog
     *
     * @param parent Parent frame
     * @param panelList List of user preference panels to display
     * @param selectedTab The tab to select initially (panel's tab resource string)
     * @param activatedPlugins String containing all activated plugins of the system
     */
    public JDialogUserPreferences(JFrame parent, List<PreferencesPanel> panelList,
            String selectedTab, String activatedPlugins) {
        super(parent, true);
        this.panelList.addAll(panelList);
        initComponents();
        this.setMultiresolutionIcons();

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        for (PreferencesPanel preferencePanel : this.panelList) {
            //initialize the panels
            preferencePanel.setActivatedPlugins(activatedPlugins);
            preferencePanel.loadPreferences();
            //add the panels to the layout
            this.jPanelEdit.add(preferencePanel, gridBagConstraints);
        }

        // Only show tab bar if there's more than one tab
        if (this.panelList.size() > 1) {
            this.buttonBar = new ImageButtonBar(ImageButtonBar.HORIZONTAL)
                    .setPreferredButtonSize(85, 84);
            boolean selected = selectedTab == null;
            for (PreferencesPanel preferencePanel : this.panelList) {
                if (selectedTab != null && preferencePanel.getTabResource().equals(selectedTab)) {
                    selected = true;
                }
                String tabText = rb.getResourceString(preferencePanel.getTabResource());
                if (tabText.length() > 15) {
                    int blankIndex = tabText.indexOf(" ");
                    if (blankIndex > 0) {
                        tabText = tabText.substring(0, blankIndex);
                    }
                }
                buttonBar.addButton(
                        preferencePanel.getIcon(),
                        tabText,
                        new JComponent[]{preferencePanel},
                        selected);
                selected = false;
            }
            buttonBar.build();

            //add button bar
            this.jPanelButtonBar.setLayout(new BorderLayout());
            this.jPanelButtonBar.add(buttonBar, BorderLayout.CENTER);
        } else {
            // Single tab - make the first panel visible directly
            if (!this.panelList.isEmpty()) {
                this.panelList.get(0).setVisible(true);
            }
            // Hide the button bar panel
            this.jPanelButtonBar.setVisible(false);
        }

        this.getRootPane().setDefaultButton(this.jButtonOk);
    }

 

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
        jPanelEdit = new javax.swing.JPanel();
        jPanelButton = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanelButtonBar = new javax.swing.JPanel();
        jPanelSpace = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(rb.getResourceString("title.userpreferences"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelEdit, gridBagConstraints);

        jPanelButton.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(rb.getResourceString("button.ok"));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        jPanelButton.add(jButtonOk, gridBagConstraints);

        jButtonCancel.setText(rb.getResourceString("button.cancel"));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        jPanelButton.add(jButtonCancel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButton, gridBagConstraints);

        jPanelButtonBar.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelButtonBar, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        getContentPane().add(jPanelSpace, gridBagConstraints);

        setSize(900, 650);
    }

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            for (PreferencesPanel panel : this.panelList) {
                panel.savePreferences();
            }
            this.okPressed = true;
            this.setVisible(false);
            this.dispose();
        } catch (Exception e) {
            UINotification.instance().addNotification(e);
            e.printStackTrace();
        }
    }

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
        this.dispose();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        this.setVisible(false);
        this.dispose();
    }

    private void setMultiresolutionIcons() {
        // Use the same icon as preferences dialog
        try {
            if (this.getClass().getClassLoader().getResource(
                    "de/mendelson/comm/as2/client/preferences32x32.gif") != null) {
                java.awt.Image icon = javax.imageio.ImageIO.read(
                    this.getClass().getClassLoader().getResourceAsStream(
                        "de/mendelson/comm/as2/client/preferences32x32.gif"));
                this.setIconImage(icon);
            }
        } catch (Exception e) {
            // Ignore icon loading errors
        }
    }

    /**
     * Returns if the user pressed ok
     */
    public boolean isOkPressed() {
        return this.okPressed;
    }

    // Variables declaration
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelButtonBar;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSpace;
    // End of variables declaration
}
