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
package de.mendelson.comm.as2.tracker.gui;

import com.toedter.calendar.JDateChooser;
import de.mendelson.comm.as2.tracker.TrackerMessageInfo;
import de.mendelson.util.DateChooserUI;
import de.mendelson.util.IStatusBar;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.toggleswitch.ToggleSwitch;
import de.mendelson.util.uinotification.UINotification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Dialog for viewing tracker messages
 *
 * @author Julian Xu
 */
public class JDialogTrackerMessage extends JDialog implements ListSelectionListener {

    private MecResourceBundle rb;
    private BaseClient baseClient;
    private IStatusBar statusBar;

    // UI Components
    private JDateChooser jDateChooserStartDate;
    private JDateChooser jDateChooserEndDate;
    private JTextField jTextFieldTrackerIdFilter;
    private JTextField jTextFieldUserFilter;
    private JComboBox<String> jComboBoxFormatFilter;
    private ToggleSwitch switchAuthNone;
    private ToggleSwitch switchAuthSuccess;
    private ToggleSwitch switchAutoRefresh;
    private JButton jButtonReset;
    private JButton jButtonRefresh;
    private JTable jTableMessages;
    private JTextArea jTextAreaDetails;
    private JScrollPane jScrollPaneDetails;
    private JButton jButtonDownload;
    private JButton jButtonDownloadPayloads;
    private TrackerMessageInfo currentSelectedMessage;
    private javax.swing.Timer autoRefreshTimer;

    public JDialogTrackerMessage(JFrame parent, BaseClient baseClient, IStatusBar statusBar) {
        super(parent, false);  // Non-modal
        this.baseClient = baseClient;
        this.statusBar = statusBar;

        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogTrackerMessage.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Resource bundle not found: " + e.getClassName());
        }

        initComponents();
        initializeDefaultDates();
        initializeAutoRefreshTimer();
        performSearch();
    }

    private void initializeAutoRefreshTimer() {
        // Create timer for auto-refresh (10 seconds interval)
        autoRefreshTimer = new javax.swing.Timer(10000, evt -> {
            if (switchAutoRefresh != null && switchAutoRefresh.isSelected()) {
                performSearch();
            }
        });
        autoRefreshTimer.setRepeats(true);
    }

    private void toggleAutoRefresh() {
        if (switchAutoRefresh.isSelected()) {
            autoRefreshTimer.start();
        } else {
            autoRefreshTimer.stop();
        }
    }

    @Override
    public void dispose() {
        // Stop timer on dialog close
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
        super.dispose();
    }

    private void initializeDefaultDates() {
        Calendar cal = Calendar.getInstance();
        jDateChooserEndDate.setDate(cal.getTime());

        cal.add(Calendar.DAY_OF_MONTH, -7); // Last 7 days by default
        jDateChooserStartDate.setDate(cal.getTime());
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(rb.getResourceString("title"));
        setPreferredSize(new Dimension(1400, 805));

        JPanel jPanelMain = new JPanel(new BorderLayout(5, 5));
        jPanelMain.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Filter panel at top
        JPanel jPanelFilter = createFilterPanel();
        jPanelMain.add(jPanelFilter, BorderLayout.NORTH);

        // Split pane for table and details - 43.3% list / 56.7% details
        final JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jSplitPane.setResizeWeight(0.433);

        // Table panel
        JPanel jPanelTable = createTablePanel();
        jSplitPane.setTopComponent(jPanelTable);

        // Details panel
        JPanel jPanelDetails = createDetailsPanel();
        jSplitPane.setBottomComponent(jPanelDetails);

        jPanelMain.add(jSplitPane, BorderLayout.CENTER);

        getContentPane().add(jPanelMain);

        // Add keyboard shortcuts
        setupKeyboardShortcuts();

        pack();
        setLocationRelativeTo(getParent());

        // Set divider location AFTER pack() so the actual height is known
        SwingUtilities.invokeLater(() -> {
            jSplitPane.setDividerLocation(0.433);
        });
    }

    private void setupKeyboardShortcuts() {
        // ESC to close dialog
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Cmd+W / Ctrl+W to close
        int modifierKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_W, modifierKey),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // F5 to refresh
        getRootPane().registerKeyboardAction(
            e -> performSearch(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Cmd+R / Ctrl+R to refresh
        getRootPane().registerKeyboardAction(
            e -> performSearch(),
            KeyStroke.getKeyStroke(KeyEvent.VK_R, modifierKey),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(rb.getResourceString("filter.title")));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ===== ROW 1: Date Range and Tracker ID =====
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(rb.getResourceString("label.startdate")), gbc);

        jDateChooserStartDate = new JDateChooser();
        jDateChooserStartDate.setDateFormatString("yyyy-MM-dd");
        jDateChooserStartDate.setUI(new DateChooserUI());
        jDateChooserStartDate.setPreferredSize(new Dimension(140, jDateChooserStartDate.getPreferredSize().height));
        jDateChooserStartDate.addPropertyChangeListener("date", evt -> performSearch());
        gbc.gridx = 1;
        panel.add(jDateChooserStartDate, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(new JLabel(rb.getResourceString("label.enddate")), gbc);

        jDateChooserEndDate = new JDateChooser();
        jDateChooserEndDate.setDateFormatString("yyyy-MM-dd");
        jDateChooserEndDate.setUI(new DateChooserUI());
        jDateChooserEndDate.setPreferredSize(new Dimension(140, jDateChooserEndDate.getPreferredSize().height));
        jDateChooserEndDate.addPropertyChangeListener("date", evt -> performSearch());
        gbc.gridx = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(jDateChooserEndDate, gbc);

        // Tracker ID filter
        gbc.gridx = 4;
        gbc.insets = new Insets(5, 20, 5, 5);
        panel.add(new JLabel(rb.getResourceString("label.trackerid.filter")), gbc);

        jTextFieldTrackerIdFilter = new JTextField();
        jTextFieldTrackerIdFilter.setPreferredSize(new Dimension(200, jTextFieldTrackerIdFilter.getPreferredSize().height));
        jTextFieldTrackerIdFilter.setToolTipText(rb.getResourceString("label.trackerid.filter.tooltip"));
        jTextFieldTrackerIdFilter.addActionListener(evt -> performSearch());
        gbc.gridx = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(jTextFieldTrackerIdFilter, gbc);

        // User filter
        gbc.gridx = 6;
        gbc.insets = new Insets(5, 20, 5, 5);
        panel.add(new JLabel(rb.getResourceString("label.user.filter")), gbc);

        jTextFieldUserFilter = new JTextField();
        jTextFieldUserFilter.setPreferredSize(new Dimension(80, jTextFieldUserFilter.getPreferredSize().height));
        jTextFieldUserFilter.setToolTipText(rb.getResourceString("label.user.filter.tooltip"));
        jTextFieldUserFilter.addActionListener(evt -> performSearch());
        gbc.gridx = 7;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(jTextFieldUserFilter, gbc);

        // Format filter
        gbc.gridx = 8;
        gbc.insets = new Insets(5, 20, 5, 5);
        panel.add(new JLabel(rb.getResourceString("label.format.filter")), gbc);

        jComboBoxFormatFilter = new JComboBox<>(new String[]{
                rb.getResourceString("label.format.all"),
                "cXML",
                "X12",
                "EDIFACT"
        });
        jComboBoxFormatFilter.setPreferredSize(new Dimension(135, jComboBoxFormatFilter.getPreferredSize().height));
        jComboBoxFormatFilter.setToolTipText(rb.getResourceString("label.format.filter.tooltip"));
        jComboBoxFormatFilter.addActionListener(evt -> performSearch());
        gbc.gridx = 9;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(jComboBoxFormatFilter, gbc);

        // ===== ROW 2: Auth Status, Auto-refresh, Action Buttons =====
        row = 1;

        // Auth status filters
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(rb.getResourceString("label.auth.title")), gbc);

        JPanel panelAuthToggles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        switchAuthNone = new ToggleSwitch();
        switchAuthNone.setSelected(true);
        switchAuthNone.addActionListener(evt -> performSearch());
        JLabel labelAuthNone = new JLabel(rb.getResourceString("label.auth.none"));
        labelAuthNone.setForeground(new Color(128, 128, 128));
        panelAuthToggles.add(switchAuthNone);
        panelAuthToggles.add(labelAuthNone);

        switchAuthSuccess = new ToggleSwitch();
        switchAuthSuccess.setSelected(true);
        switchAuthSuccess.addActionListener(evt -> performSearch());
        JLabel labelAuthSuccess = new JLabel(rb.getResourceString("label.auth.success"));
        labelAuthSuccess.setForeground(new Color(34, 139, 34));
        panelAuthToggles.add(switchAuthSuccess);
        panelAuthToggles.add(labelAuthSuccess);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(panelAuthToggles, gbc);

        // Auto-refresh toggle
        gbc.gridx = 4;
        gbc.gridwidth = 2;
        switchAutoRefresh = new ToggleSwitch();
        switchAutoRefresh.setSelected(false);
        switchAutoRefresh.addActionListener(evt -> toggleAutoRefresh());
        JLabel labelAutoRefresh = new JLabel(rb.getResourceString("label.autorefresh"));
        labelAutoRefresh.setForeground(new Color(70, 130, 180));

        JPanel panelAutoRefresh = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelAutoRefresh.add(switchAutoRefresh);
        panelAutoRefresh.add(labelAutoRefresh);
        panel.add(panelAutoRefresh, gbc);

        // Action Buttons
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        jButtonRefresh = new JButton(rb.getResourceString("button.refresh"));
        jButtonRefresh.addActionListener(evt -> performSearch());
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        String shortcutHint = isMac ? "⌘R or F5" : "Ctrl+R or F5";
        jButtonRefresh.setToolTipText(shortcutHint);
        panelButtons.add(jButtonRefresh);

        jButtonReset = new JButton(rb.getResourceString("button.reset"));
        jButtonReset.addActionListener(evt -> resetFilter());
        panelButtons.add(jButtonReset);

        gbc.gridx = 6;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(panelButtons, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        TableModelTrackerMessage tableModel = new TableModelTrackerMessage();
        jTableMessages = new JTable(tableModel);
        jTableMessages.setRowHeight(TableModelTrackerMessage.ROW_HEIGHT);
        jTableMessages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableMessages.getSelectionModel().addListSelectionListener(this);
        jTableMessages.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        jTableMessages.getColumnModel().getColumn(0).setPreferredWidth(250); // Tracker ID
        jTableMessages.getColumnModel().getColumn(1).setPreferredWidth(150); // Timestamp
        jTableMessages.getColumnModel().getColumn(2).setPreferredWidth(120); // IP
        jTableMessages.getColumnModel().getColumn(3).setPreferredWidth(200); // User Agent
        jTableMessages.getColumnModel().getColumn(4).setPreferredWidth(80);  // Size
        jTableMessages.getColumnModel().getColumn(5).setPreferredWidth(100); // Auth Status
        jTableMessages.getColumnModel().getColumn(6).setPreferredWidth(100); // Auth User
        jTableMessages.getColumnModel().getColumn(7).setPreferredWidth(80);  // Payloads
        jTableMessages.getColumnModel().getColumn(8).setPreferredWidth(80);  // Format
        jTableMessages.getColumnModel().getColumn(9).setPreferredWidth(100); // Doc Type

        JScrollPane scrollPane = new JScrollPane(jTableMessages);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create header panel with title and button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        // Title label on the left
        JLabel titleLabel = new JLabel(rb.getResourceString("details.title"));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Buttons panel on the right
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        // Download content button
        jButtonDownload = new JButton("⬇"); // Unicode download arrow
        jButtonDownload.setFont(new Font("Dialog", Font.BOLD, 16));
        jButtonDownload.setPreferredSize(new Dimension(32, 28));
        jButtonDownload.setMargin(new Insets(2, 2, 2, 2));
        jButtonDownload.setToolTipText(rb.getResourceString("button.download.tooltip"));
        jButtonDownload.setEnabled(false);
        jButtonDownload.setFocusPainted(false);
        jButtonDownload.addActionListener(evt -> downloadMessageContent());
        buttonsPanel.add(jButtonDownload);

        // Download payloads button
        jButtonDownloadPayloads = new JButton("📦"); // Unicode package/box icon
        jButtonDownloadPayloads.setFont(new Font("Dialog", Font.BOLD, 14));
        jButtonDownloadPayloads.setPreferredSize(new Dimension(32, 28));
        jButtonDownloadPayloads.setMargin(new Insets(2, 2, 2, 2));
        jButtonDownloadPayloads.setToolTipText(rb.getResourceString("button.download.payloads.tooltip"));
        jButtonDownloadPayloads.setEnabled(false);
        jButtonDownloadPayloads.setFocusPainted(false);
        jButtonDownloadPayloads.addActionListener(evt -> downloadPayloads());
        buttonsPanel.add(jButtonDownloadPayloads);

        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        jTextAreaDetails = new JTextArea();
        jTextAreaDetails.setEditable(false);
        jTextAreaDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        jTextAreaDetails.setText(rb.getResourceString("details.noselection"));

        jScrollPaneDetails = new JScrollPane(jTextAreaDetails);
        jScrollPaneDetails.setBorder(BorderFactory.createTitledBorder(""));
        panel.add(jScrollPaneDetails, BorderLayout.CENTER);

        return panel;
    }

    private void resetFilter() {
        initializeDefaultDates();
        jTextFieldTrackerIdFilter.setText("");
        jTextFieldUserFilter.setText("");
        jComboBoxFormatFilter.setSelectedIndex(0); // Select "All"
        switchAuthNone.setSelected(true);
        switchAuthSuccess.setSelected(true);
        performSearch();
    }

    private void performSearch() {
        // Check if using tracker ID filter
        String trackerIdFilter = jTextFieldTrackerIdFilter.getText();
        if (trackerIdFilter != null && !trackerIdFilter.trim().isEmpty()) {
            performSearchByTrackerId(trackerIdFilter.trim());
            return;
        }

        // Otherwise use date/auth search
        if (jDateChooserStartDate.getDate() == null || jDateChooserEndDate.getDate() == null) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_WARNING,
                    "Date required",
                    rb.getResourceString("error.dates.required"));
            return;
        }

        Date startDate = jDateChooserStartDate.getDate();
        Date endDate = jDateChooserEndDate.getDate();

        // Set end date to end of day (using system timezone for user's local time)
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        endDate = cal.getTime();

        if (startDate.after(endDate)) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_WARNING,
                    "Invalid date range",
                    rb.getResourceString("error.dates.invalid"));
            return;
        }

        // Convert dates to UTC for database query
        // The database stores timestamps in UTC, but the user picks dates in local time
        // We need to adjust the dates so that when interpreted as UTC, they match the local date range
        Calendar calUTC = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));

        // For start date: set to beginning of day in local time, then get the Date object
        cal.setTime(startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Copy to UTC calendar at the same wall-clock time
        calUTC.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        calUTC.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        calUTC.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
        calUTC.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        calUTC.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        calUTC.set(Calendar.SECOND, cal.get(Calendar.SECOND));
        calUTC.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
        Date finalStartDate = calUTC.getTime();

        // For end date: already set to end of day, copy to UTC
        cal.setTime(endDate);
        calUTC.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        calUTC.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        calUTC.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
        calUTC.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        calUTC.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        calUTC.set(Calendar.SECOND, cal.get(Calendar.SECOND));
        calUTC.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
        Date finalEndDate = calUTC.getTime();

        final boolean includeNone = switchAuthNone.isSelected();
        final boolean includeSuccess = switchAuthSuccess.isSelected();
        final String userFilterValue = jTextFieldUserFilter.getText();
        final String formatFilterValue = getSelectedFormat();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String uniqueId = JDialogTrackerMessage.class.getName() + ".search." + System.currentTimeMillis();
                try {
                    statusBar.startProgressIndeterminate(rb.getResourceString("status.searching"), uniqueId);

                    // Use client-server messaging to get tracker messages from server
                    de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest request =
                            new de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest(
                                    de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest.TYPE_LIST_MESSAGES);
                    request.setStartDate(finalStartDate);
                    request.setEndDate(finalEndDate);
                    request.setShowAuthNone(includeNone);
                    request.setShowAuthSuccess(includeSuccess);
                    request.setShowAuthFailed(false); // Always false - failed option removed
                    request.setUserFilter(userFilterValue);
                    request.setFormatFilter(formatFilterValue);

                    de.mendelson.comm.as2.tracker.clientserver.TrackerMessageResponse response =
                            (de.mendelson.comm.as2.tracker.clientserver.TrackerMessageResponse) baseClient.sendSync(request);

                    List<TrackerMessageInfo> results;
                    if (response != null && response.getException() == null) {
                        results = response.getMessages();
                        if (results == null) {
                            results = new ArrayList<>();
                        }
                    } else {
                        throw new Exception(response != null && response.getException() != null
                                ? response.getException().getMessage()
                                : "No response from server");
                    }

                    final List<TrackerMessageInfo> finalResults = results;
                    SwingUtilities.invokeLater(() -> {
                        TableModelTrackerMessage tableModel = (TableModelTrackerMessage) jTableMessages.getModel();
                        tableModel.passNewData(finalResults);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        UINotification.instance().addNotification(
                                null,
                                UINotification.TYPE_ERROR,
                                "Search failed",
                                rb.getResourceString("error.search") + ": " + e.getMessage());
                    });
                } finally {
                    statusBar.stopProgressIfExists(uniqueId);
                }
            }
        };

        GUIClient.submit(runnable);
    }

    private void performSearchByTrackerId(final String trackerIdFilter) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String uniqueId = JDialogTrackerMessage.class.getName() + ".search.tracker." + System.currentTimeMillis();
                try {
                    statusBar.startProgressIndeterminate(rb.getResourceString("status.searching"), uniqueId);

                    // Use client-server messaging to search by tracker ID
                    de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest request =
                            new de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest(
                                    de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest.TYPE_LIST_MESSAGES);
                    request.setTrackerIdFilter(trackerIdFilter);

                    de.mendelson.comm.as2.tracker.clientserver.TrackerMessageResponse response =
                            (de.mendelson.comm.as2.tracker.clientserver.TrackerMessageResponse) baseClient.sendSync(request);

                    List<TrackerMessageInfo> results;
                    if (response != null && response.getException() == null) {
                        results = response.getMessages();
                        if (results == null) {
                            results = new ArrayList<>();
                        }
                    } else {
                        throw new Exception(response != null && response.getException() != null
                                ? response.getException().getMessage()
                                : "No response from server");
                    }

                    final List<TrackerMessageInfo> finalResults = results;
                    SwingUtilities.invokeLater(() -> {
                        TableModelTrackerMessage tableModel = (TableModelTrackerMessage) jTableMessages.getModel();
                        tableModel.passNewData(finalResults);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        UINotification.instance().addNotification(
                                null,
                                UINotification.TYPE_ERROR,
                                "Search failed",
                                rb.getResourceString("error.search") + ": " + e.getMessage());
                    });
                } finally {
                    statusBar.stopProgressIfExists(uniqueId);
                }
            }
        };

        GUIClient.submit(runnable);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        int selectedRow = jTableMessages.getSelectedRow();
        if (selectedRow >= 0) {
            TableModelTrackerMessage tableModel = (TableModelTrackerMessage) jTableMessages.getModel();
            TrackerMessageInfo info = tableModel.getRow(selectedRow);
            if (info != null) {
                currentSelectedMessage = info;
                jButtonDownload.setEnabled(true);
                jButtonDownloadPayloads.setEnabled(info.getPayloadCount() > 0);
                displayMessageDetails(info);
            }
        } else {
            currentSelectedMessage = null;
            jButtonDownload.setEnabled(false);
            jButtonDownloadPayloads.setEnabled(false);
            jTextAreaDetails.setText(rb.getResourceString("details.noselection"));
        }
    }

    private void displayMessageDetails(TrackerMessageInfo info) {
        StringBuilder details = new StringBuilder();

        details.append("═══════════════════════════════════════════════════════\n");
        details.append("  TRACKER MESSAGE DETAILS\n");
        details.append("═══════════════════════════════════════════════════════\n\n");

        details.append("Tracker ID:     ").append(info.getTrackerId()).append("\n");
        details.append("Timestamp:      ").append(info.getInitDate()).append("\n\n");

        details.append("Remote Address: ").append(info.getRemoteAddr()).append("\n");
        details.append("User Agent:     ").append(info.getUserAgent()).append("\n\n");

        details.append("Content Type:   ").append(info.getContentType()).append("\n");
        details.append("Content Size:   ").append(formatSize(info.getContentSize())).append("\n\n");

        details.append("Auth Status:    ").append(info.getAuthStatusText()).append("\n");
        if (info.getAuthUser() != null) {
            details.append("Auth User:      ").append(info.getAuthUser()).append("\n");
        }
        details.append("\n");

        // Display payload analysis if available
        if (info.getPayloadFormat() != null && !info.getPayloadFormat().equals("Unknown")) {
            details.append("───────────────────────────────────────────────────────\n");
            details.append("  PAYLOAD ANALYSIS\n");
            details.append("───────────────────────────────────────────────────────\n");
            details.append("Format:         ").append(info.getPayloadFormat()).append("\n");
            if (info.getPayloadDocType() != null && !info.getPayloadDocType().isEmpty()) {
                details.append("Document Type:  ").append(info.getPayloadDocType()).append("\n");
            }
            if (info.getPayloadDetails() != null && !info.getPayloadDetails().isEmpty()) {
                details.append("Details:        ").append(info.getPayloadDetails()).append("\n");
            }
            details.append("\n");
        }

        details.append("File Location:  ").append(info.getRawFilename()).append("\n\n");

        if (info.getRequestHeaders() != null && !info.getRequestHeaders().isEmpty()) {
            details.append("───────────────────────────────────────────────────────\n");
            details.append("  REQUEST HEADERS\n");
            details.append("───────────────────────────────────────────────────────\n");
            details.append(formatJson(info.getRequestHeaders())).append("\n\n");
        }

        // Try to load and display message content preview
        try {
            // Use client-server messaging to get message content from server
            de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest request =
                    new de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest(
                            de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest.TYPE_GET_MESSAGE_DETAILS);
            request.setTrackerId(info.getTrackerId());

            de.mendelson.comm.as2.tracker.clientserver.TrackerMessageResponse response =
                    (de.mendelson.comm.as2.tracker.clientserver.TrackerMessageResponse) baseClient.sendSync(request);

            if (response != null && response.getException() == null && response.getMessageContent() != null) {
                byte[] content = response.getMessageContent();
                details.append("───────────────────────────────────────────────────────\n");
                details.append("  MESSAGE CONTENT PREVIEW (first 500 bytes)\n");
                details.append("───────────────────────────────────────────────────────\n");

                int previewLength = Math.min(content.length, 500);
                String preview = new String(content, 0, previewLength, "UTF-8");
                details.append(preview);
                if (content.length > 500) {
                    details.append("\n\n... (truncated, ").append(content.length - 500)
                            .append(" more bytes)\n");
                }
            } else {
                System.out.println("  Message content not displayed - one of the conditions failed");
            }
        } catch (Exception e) {
            System.out.println("  Exception loading message content: " + e.getMessage());
            e.printStackTrace();
            details.append("\n[Failed to load message content: ").append(e.getMessage()).append("]\n");
        }

        jTextAreaDetails.setText(details.toString());
        jTextAreaDetails.setCaretPosition(0);
    }

    private String getSelectedFormat() {
        int selectedIndex = jComboBoxFormatFilter.getSelectedIndex();
        if (selectedIndex == 0) {
            return null; // "All" selected
        }
        return (String) jComboBoxFormatFilter.getSelectedItem();
    }

    private String formatSize(int bytes) {
        if (bytes < 1024) {
            return bytes + " bytes";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }

    private String formatJson(String json) {
        if (json == null || json.isEmpty()) {
            return "";
        }

        // Simple JSON formatting - add line breaks after commas
        return json.replace(",\"", ",\n  \"")
                   .replace("{\"", "{\n  \"")
                   .replace("\"}", "\"\n}");
    }

    private void downloadMessageContent() {
        if (currentSelectedMessage == null) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_WARNING,
                    "No selection",
                    rb.getResourceString("error.noselection"));
            return;
        }

        // Show file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Tracker Message Content");

        // Generate default filename: tracker_<trackerId>_<timestamp>.msg
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(currentSelectedMessage.getInitDate());
        String defaultFilename = "tracker_" +
                currentSelectedMessage.getTrackerId().substring(0, 8) +
                "_" + timestamp + ".msg";
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            final java.io.File targetFile = fileChooser.getSelectedFile();
            final TrackerMessageInfo info = currentSelectedMessage;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    final String uniqueId = JDialogTrackerMessage.class.getName() +
                            ".download." + System.currentTimeMillis();
                    try {
                        statusBar.startProgressIndeterminate(
                                rb.getResourceString("status.downloading"), uniqueId);

                        // Get message content from server
                        de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest request =
                                new de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest(
                                        de.mendelson.comm.as2.tracker.clientserver.TrackerMessageRequest.TYPE_GET_MESSAGE_DETAILS);
                        request.setTrackerId(info.getTrackerId());

                        de.mendelson.comm.as2.tracker.clientserver.TrackerMessageResponse response =
                                (de.mendelson.comm.as2.tracker.clientserver.TrackerMessageResponse)
                                baseClient.sendSync(request);

                        if (response != null && response.getException() == null &&
                                response.getMessageContent() != null) {
                            // Write content to file
                            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(targetFile)) {
                                fos.write(response.getMessageContent());
                            }

                            SwingUtilities.invokeLater(() -> {
                                UINotification.instance().addNotification(
                                        null,
                                        UINotification.TYPE_SUCCESS,
                                        "Download complete",
                                        rb.getResourceString("status.download.success") +
                                        ": " + targetFile.getAbsolutePath());
                            });
                        } else {
                            throw new Exception(response != null && response.getException() != null
                                    ? response.getException().getMessage()
                                    : "No content available");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            UINotification.instance().addNotification(
                                    null,
                                    UINotification.TYPE_ERROR,
                                    "Download failed",
                                    rb.getResourceString("error.download") + ": " + e.getMessage());
                        });
                    } finally {
                        statusBar.stopProgressIfExists(uniqueId);
                    }
                }
            };

            GUIClient.submit(runnable);
        }
    }

    private void downloadPayloads() {
        if (currentSelectedMessage == null) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_WARNING,
                    "No selection",
                    rb.getResourceString("error.noselection"));
            return;
        }

        if (currentSelectedMessage.getPayloadCount() == 0) {
            UINotification.instance().addNotification(
                    null,
                    UINotification.TYPE_WARNING,
                    "No payloads",
                    "This message has no extracted payloads");
            return;
        }

        // Show file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Payloads ZIP");

        // Generate default filename: payloads_<trackerId>_<timestamp>.zip
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(currentSelectedMessage.getInitDate());
        String defaultFilename = "payloads_" +
                currentSelectedMessage.getTrackerId().substring(0, 8) +
                "_" + timestamp + ".zip";
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            final java.io.File targetFile = fileChooser.getSelectedFile();
            final TrackerMessageInfo info = currentSelectedMessage;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    final String uniqueId = JDialogTrackerMessage.class.getName() +
                            ".download.payloads." + System.currentTimeMillis();
                    try {
                        statusBar.startProgressIndeterminate(
                                "Downloading payloads...", uniqueId);

                        // Calculate payload directory path
                        String rawFilename = info.getRawFilename();
                        java.nio.file.Path rawPath = java.nio.file.Paths.get(rawFilename);
                        String dateFolder = rawPath.getParent().getFileName().toString();
                        String payloadDirName = "payloads_" + info.getTrackerId();

                        // Create ZIP file
                        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(
                                new java.io.FileOutputStream(targetFile))) {

                            // Get payloads directory from server (via filesystem access)
                            // Note: This assumes we have local filesystem access
                            // In a true client-server setup, we'd need a dedicated request type
                            de.mendelson.comm.as2.preferences.PreferencesAS2 prefs =
                                    new de.mendelson.comm.as2.preferences.PreferencesAS2(null);

                            java.nio.file.Path payloadDir = java.nio.file.Paths.get(
                                    prefs.get(de.mendelson.comm.as2.preferences.PreferencesAS2.DIR_MSG),
                                    "tracker",
                                    dateFolder,
                                    payloadDirName
                            );

                            if (!java.nio.file.Files.exists(payloadDir)) {
                                throw new Exception("Payload directory not found: " + payloadDir);
                            }

                            // Add all files from payload directory to ZIP
                            java.nio.file.Files.walk(payloadDir)
                                    .filter(java.nio.file.Files::isRegularFile)
                                    .forEach(file -> {
                                        try {
                                            String zipEntryName = payloadDir.relativize(file).toString();
                                            java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(zipEntryName);
                                            zos.putNextEntry(zipEntry);
                                            java.nio.file.Files.copy(file, zos);
                                            zos.closeEntry();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    });

                            SwingUtilities.invokeLater(() -> {
                                UINotification.instance().addNotification(
                                        null,
                                        UINotification.TYPE_SUCCESS,
                                        "Download complete",
                                        "Payloads saved to: " + targetFile.getAbsolutePath());
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            UINotification.instance().addNotification(
                                    null,
                                    UINotification.TYPE_ERROR,
                                    "Download failed",
                                    "Failed to download payloads: " + e.getMessage());
                        });
                    } finally {
                        statusBar.stopProgressIfExists(uniqueId);
                    }
                }
            };

            GUIClient.submit(runnable);
        }
    }
}
