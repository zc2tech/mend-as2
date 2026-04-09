package de.mendelson.comm.as2.client;

import de.mendelson.comm.as2.configurationcheck.gui.JDialogIssuesList;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.clientserver.message.ConfigurationCheckRequest;
import de.mendelson.comm.as2.clientserver.message.ConfigurationCheckResponse;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.util.ColorUtil;
import de.mendelson.util.DisplayMode;
import de.mendelson.util.IStatusBar;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.ProgressPanel;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.GUIClient;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Status bar for the AS2 GUI
 *
 * @author S.Heller
 * @version $Revision: 48 $
 */
public class AS2StatusBar extends JPanel implements IStatusBar {

    private final static int IMAGE_HEIGHT = 22;

    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2StatusBar.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    public static final MendelsonMultiResolutionImage IMAGE_WRENCH
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/wrench.svg", IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_PENDING
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_pending.svg", IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_STOPPED
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_stopped.svg", IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_FINISHED
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_finished.svg", IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_SERVED
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_all.svg", IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_ALL
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_all_sum.svg", IMAGE_HEIGHT);
    public static final MendelsonMultiResolutionImage IMAGE_ALL_SELECTED
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_allselected.svg", IMAGE_HEIGHT);
    private ModuleStarter moduleStarter;
    private BaseClient baseClient = null;
    private ConfigurationCheckThread checkThread = null;

    /**
     * Creates new form AS2StatusBar
     */
    public AS2StatusBar() {
        initComponents();
        this.setMultiresolutionIcons();
        Color circleColor = null;
        if (UIManager.getColor("Objects.RedStatus") != null) {
            circleColor = UIManager.getColor("Objects.RedStatus");
        } else {
            circleColor = ColorUtil.getBestContrastColorAroundForeground(this.jPanelConfigurationIssues.getBackground(),
                    Color.RED.darker());
        }
        Color colorBadgeForeground = ColorUtil.getBestContrastColorAroundForeground(circleColor, Color.WHITE);
        this.notificationBadgeButton.setNotificationBadgeColors(circleColor, colorBadgeForeground);
    }

    private void setMultiresolutionIcons() {
        PreferencesAS2 preferences = new PreferencesAS2();
        final String displayMode = preferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelTransactionsFailure.setIcon(new ImageIcon(IMAGE_STOPPED.toMinResolution(IMAGE_HEIGHT)));
                jLabelTransactionsOk.setIcon(new ImageIcon(IMAGE_FINISHED.toMinResolution(IMAGE_HEIGHT)));
                jLabelTransactionsPending.setIcon(new ImageIcon(IMAGE_PENDING.toMinResolution(IMAGE_HEIGHT)));
                jLabelTransactionsServed.setIcon(new ImageIcon(IMAGE_SERVED.toMinResolution(IMAGE_HEIGHT)));
                jLabelTransactionsAll.setIcon(new ImageIcon(IMAGE_ALL.toMinResolution(IMAGE_HEIGHT)));
                jLabelTransactionsSelected.setIcon(new ImageIcon(IMAGE_ALL_SELECTED.toMinResolution(IMAGE_HEIGHT)));
                notificationBadgeButton.setIcon(
                        new ImageIcon(IMAGE_WRENCH.toMinResolution(IMAGE_HEIGHT)),
                        !displayMode.equals(DisplayMode.LIGHT));
            }
        });

    }

    public void initialize(BaseClient baseClient, ModuleStarter moduleStarter) {
        this.baseClient = baseClient;
        this.moduleStarter = moduleStarter;
    }

    public void startConfigurationChecker() {
        if (this.baseClient == null) {
            throw new IllegalArgumentException("Status bar: Please pass the base client to the status bar before starting the config checker.");
        }
        this.checkThread = new ConfigurationCheckThread();
        GUIClient.scheduleWithFixedDelay(this.checkThread, 1, 30, TimeUnit.SECONDS);
    }

    public void setTransactionCount(int countAll, int countServed, int countOk, int countPending, int countFailed, int countSelected) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelTransactionsAll.setText(String.valueOf(countAll));
                jLabelTransactionsServed.setText(String.valueOf(countServed));
                jLabelTransactionsOk.setText(String.valueOf(countOk));
                jLabelTransactionsPending.setText(String.valueOf(countPending));
                jLabelTransactionsFailure.setText(String.valueOf(countFailed));
                jLabelTransactionsSelected.setText(String.valueOf(countSelected));
            }
        });

    }

    public void setConnectedHost(String host) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelHost.setText(AS2ServerVersion.getProductName() + "@" + host);
            }
        });
    }

    public void setSelectedTransactionCount(int countSelected) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelTransactionsSelected.setText(String.valueOf(countSelected));
            }
        });
    }

    @Override
    public void startProgressIndeterminate(String progressDetails, String uniqueId) {
        this.progressPanel.startProgressIndeterminate(progressDetails, uniqueId);
    }

    @Override
    public void stopProgressIfExists(String uniqueId) {
        this.progressPanel.stopProgressIfExists(uniqueId);
    }

    public ProgressPanel getProgressPanel() {
        return (this.progressPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelMain = new javax.swing.JPanel();
        jPanelTransactionCount = new javax.swing.JPanel();
        jLabelTransactionsAll = new javax.swing.JLabel();
        jLabelTransactionsServed = new javax.swing.JLabel();
        jLabelTransactionsOk = new javax.swing.JLabel();
        jLabelTransactionsPending = new javax.swing.JLabel();
        jLabelTransactionsFailure = new javax.swing.JLabel();
        jLabelTransactionsSelected = new javax.swing.JLabel();
        jPanelSep1 = new javax.swing.JPanel();
        jSeparator6 = new javax.swing.JSeparator();
        jPanelSep2 = new javax.swing.JPanel();
        jSeparator7 = new javax.swing.JSeparator();
        jPanelSep3 = new javax.swing.JPanel();
        jSeparator8 = new javax.swing.JSeparator();
        jPanelSep4 = new javax.swing.JPanel();
        jPanelEmpty123 = new javax.swing.JPanel();
        jPanelSep5 = new javax.swing.JPanel();
        jSeparator10 = new javax.swing.JSeparator();
        jPanelSep8 = new javax.swing.JPanel();
        jSeparator13 = new javax.swing.JSeparator();
        jPanelEmpty = new javax.swing.JPanel();
        jPanelConfigurationIssues = new javax.swing.JPanel();
        jLabelConfigurationIssue = new javax.swing.JLabel();
        notificationBadgeButton = new de.mendelson.util.NotificationBadgeButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelHost = new javax.swing.JLabel();
        progressPanel = new de.mendelson.util.ProgressPanel();
        jPanelSep6 = new javax.swing.JPanel();
        jSeparator11 = new javax.swing.JSeparator();
        jPanelSep7 = new javax.swing.JPanel();
        jSeparator12 = new javax.swing.JSeparator();

        setFocusable(false);
        setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        setRequestFocusEnabled(false);
        setVerifyInputWhenFocusTarget(false);
        setLayout(new java.awt.GridBagLayout());

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jPanelTransactionCount.setMinimumSize(new java.awt.Dimension(350, 24));
        jPanelTransactionCount.setPreferredSize(new java.awt.Dimension(350, 24));
        jPanelTransactionCount.setLayout(new java.awt.GridBagLayout());

        jLabelTransactionsAll.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        jLabelTransactionsAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsAll.setText("0");
        jLabelTransactionsAll.setToolTipText(AS2StatusBar.rb.getResourceString( "count.all.available"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsAll, gridBagConstraints);

        jLabelTransactionsServed.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        jLabelTransactionsServed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsServed.setText("0");
        jLabelTransactionsServed.setToolTipText(AS2StatusBar.rb.getResourceString( "count.all.served"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsServed, gridBagConstraints);

        jLabelTransactionsOk.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        jLabelTransactionsOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsOk.setText("0");
        jLabelTransactionsOk.setToolTipText(AS2StatusBar.rb.getResourceString( "count.ok"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsOk, gridBagConstraints);

        jLabelTransactionsPending.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        jLabelTransactionsPending.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsPending.setText("0");
        jLabelTransactionsPending.setToolTipText(AS2StatusBar.rb.getResourceString( "count.pending"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsPending, gridBagConstraints);

        jLabelTransactionsFailure.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        jLabelTransactionsFailure.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsFailure.setText("0");
        jLabelTransactionsFailure.setToolTipText(AS2StatusBar.rb.getResourceString( "count.failure"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsFailure, gridBagConstraints);

        jLabelTransactionsSelected.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        jLabelTransactionsSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsSelected.setText("0");
        jLabelTransactionsSelected.setToolTipText(AS2StatusBar.rb.getResourceString( "count.selected"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsSelected, gridBagConstraints);

        jPanelSep1.setLayout(new java.awt.GridBagLayout());

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanelSep1.add(jSeparator6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep1, gridBagConstraints);

        jPanelSep2.setLayout(new java.awt.GridBagLayout());

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanelSep2.add(jSeparator7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep2, gridBagConstraints);

        jPanelSep3.setLayout(new java.awt.GridBagLayout());

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanelSep3.add(jSeparator8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep3, gridBagConstraints);

        jPanelSep4.setLayout(new java.awt.GridBagLayout());
        jPanelSep4.add(jPanelEmpty123, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanelTransactionCount.add(jPanelSep4, gridBagConstraints);

        jPanelSep5.setLayout(new java.awt.GridBagLayout());

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanelSep5.add(jSeparator10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep5, gridBagConstraints);

        jPanelSep8.setLayout(new java.awt.GridBagLayout());

        jSeparator13.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanelSep8.add(jSeparator13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        jPanelMain.add(jPanelTransactionCount, gridBagConstraints);

        jPanelEmpty.setMinimumSize(new java.awt.Dimension(446, 24));
        jPanelEmpty.setPreferredSize(new java.awt.Dimension(125, 24));
        jPanelEmpty.setLayout(new java.awt.GridBagLayout());

        jPanelConfigurationIssues.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelConfigurationIssuesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanelConfigurationIssuesMouseEntered(evt);
            }
        });
        jPanelConfigurationIssues.setLayout(new java.awt.GridBagLayout());

        jLabelConfigurationIssue.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        jLabelConfigurationIssue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        // jLabelConfigurationIssue.setText("Not connected");
        jLabelConfigurationIssue.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanelConfigurationIssues.add(jLabelConfigurationIssue, gridBagConstraints);

        notificationBadgeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                notificationBadgeButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                notificationBadgeButtonMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanelConfigurationIssues.add(notificationBadgeButton, gridBagConstraints);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanelConfigurationIssues.add(jSeparator1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanelEmpty.add(jPanelConfigurationIssues, gridBagConstraints);

        jLabelHost.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        // jLabelHost.setText("Not connected");
        jLabelHost.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabelHost.setMaximumSize(new java.awt.Dimension(1000, 16));
        jLabelHost.setMinimumSize(new java.awt.Dimension(100, 16));
        jLabelHost.setPreferredSize(new java.awt.Dimension(200, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanelEmpty.add(jLabelHost, gridBagConstraints);

        progressPanel.setMaximumSize(new java.awt.Dimension(2147483647, 16));
        progressPanel.setMinimumSize(new java.awt.Dimension(200, 12));
        progressPanel.setPreferredSize(new java.awt.Dimension(200, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelEmpty.add(progressPanel, gridBagConstraints);

        jPanelSep6.setLayout(new java.awt.GridBagLayout());

        jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanelSep6.add(jSeparator11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelEmpty.add(jPanelSep6, gridBagConstraints);

        jPanelSep7.setLayout(new java.awt.GridBagLayout());

        jSeparator12.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanelSep7.add(jSeparator12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelEmpty.add(jPanelSep7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMain.add(jPanelEmpty, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        add(jPanelMain, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jPanelConfigurationIssuesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelConfigurationIssuesMouseClicked
        if (evt.getClickCount() == 2) {
            //double clicked on the issue panel
        }
    }//GEN-LAST:event_jPanelConfigurationIssuesMouseClicked

    private void jPanelConfigurationIssuesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelConfigurationIssuesMouseEntered
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        int x = (int) this.jPanelConfigurationIssues.getLocationOnScreen().getX();
        int y = (int) this.getLocationOnScreen().getY();
        JDialogIssuesList dialog = new JDialogIssuesList(parent, this.baseClient,
                new Point(x, y), this.moduleStarter);
        dialog.setVisible(true);
    }//GEN-LAST:event_jPanelConfigurationIssuesMouseEntered

    private void notificationBadgeButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_notificationBadgeButtonMouseEntered
        //buttons eat up mouse events
        JComponent source = (JComponent) evt.getSource();
        source.getParent().dispatchEvent(evt);
    }//GEN-LAST:event_notificationBadgeButtonMouseEntered

    private void notificationBadgeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_notificationBadgeButtonMouseClicked
        //buttons eat up mouse events
        JComponent source = (JComponent) evt.getSource();
        source.getParent().dispatchEvent(evt);
    }//GEN-LAST:event_notificationBadgeButtonMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelConfigurationIssue;
    private javax.swing.JLabel jLabelHost;
    private javax.swing.JLabel jLabelTransactionsAll;
    private javax.swing.JLabel jLabelTransactionsFailure;
    private javax.swing.JLabel jLabelTransactionsOk;
    private javax.swing.JLabel jLabelTransactionsPending;
    private javax.swing.JLabel jLabelTransactionsSelected;
    private javax.swing.JLabel jLabelTransactionsServed;
    private javax.swing.JPanel jPanelConfigurationIssues;
    private javax.swing.JPanel jPanelEmpty;
    private javax.swing.JPanel jPanelEmpty123;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSep4;
    private javax.swing.JPanel jPanelSep5;
    private javax.swing.JPanel jPanelSep6;
    private javax.swing.JPanel jPanelSep7;
    private javax.swing.JPanel jPanelSep8;
    private javax.swing.JPanel jPanelTransactionCount;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private de.mendelson.util.NotificationBadgeButton notificationBadgeButton;
    private de.mendelson.util.ProgressPanel progressPanel;
    // End of variables declaration//GEN-END:variables

    public class ConfigurationCheckThread implements Runnable {

        public ConfigurationCheckThread() {
        }

        @Override
        public void run() {
            try {
                ConfigurationCheckRequest checkRequest = new ConfigurationCheckRequest();
                checkRequest.setPerformClientRelatedTests(true);
                ConfigurationCheckResponse response = (ConfigurationCheckResponse) baseClient.sendSync(checkRequest);
                final int issueCount = response.getIssues().size();
                if (issueCount == 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            notificationBadgeButton.setText("");
                            String text = rb.getResourceString("no.configuration.issues");
                            jLabelConfigurationIssue.setText(text);
                            int labelWidth = computeStringWidth(jLabelConfigurationIssue.getFont(), text) + 10;
                            jLabelConfigurationIssue.setPreferredSize(new Dimension(labelWidth, IMAGE_HEIGHT));
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String text = rb.getResourceString("configuration.issue");
                            jLabelConfigurationIssue.setText(text);
                            notificationBadgeButton.setText(String.valueOf(issueCount));
                            //contents with some gap result in the label width
                            final int labelWidth = computeStringWidth(jLabelConfigurationIssue.getFont(), text)
                                    + new ImageIcon(IMAGE_WRENCH.toMinResolution(IMAGE_HEIGHT)).getIconWidth() + 10;
                            jPanelConfigurationIssues.setPreferredSize(new Dimension(
                                    labelWidth
                                    + 10
                                    + (int) notificationBadgeButton.getPreferredSize().getWidth(), IMAGE_HEIGHT));
                        }
                    });
                }
            } catch (Throwable e) {
                //nop
            }
        }

        private int computeStringWidth(Font font, String text) {
            AffineTransform affinetransform = new AffineTransform();
            FontRenderContext fontRenderContext = new FontRenderContext(affinetransform, true, true);
            int width = (int) (font.getStringBounds(text, fontRenderContext).getWidth());
            return (width);
        }

    }

}
