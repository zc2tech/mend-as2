package de.mendelson.comm.as2.client;

import de.mendelson.comm.as2.client.IconManager;
import de.mendelson.util.httpconfig.gui.JDialogDisplayHTTPConfiguration;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.client.manualsend.JDialogManualSend;
import de.mendelson.comm.as2.client.permissions.SwingUIPermissionManager;
import de.mendelson.comm.as2.clientserver.message.DeleteMessageRequest;
import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.clientserver.message.RefreshTablePartnerData;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MessageOverviewFilter;
import de.mendelson.comm.as2.message.clientserver.MessageOverviewRequest;
import de.mendelson.comm.as2.message.clientserver.MessageOverviewResponse;
import de.mendelson.comm.as2.message.clientserver.MessagePayloadRequest;
import de.mendelson.comm.as2.message.clientserver.MessagePayloadResponse;
import de.mendelson.comm.as2.message.loggui.DialogMessageDetails;
import de.mendelson.comm.as2.message.loggui.TableModelMessageOverview;
import de.mendelson.comm.as2.partner.CertificateUsedByPartnerChecker;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.comm.as2.partner.clientserver.PartnerSystemRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerSystemResponse;
import de.mendelson.comm.as2.partner.gui.JDialogPartnerConfig;
import de.mendelson.comm.as2.partner.gui.ListCellRendererPartner;
import de.mendelson.comm.as2.preferences.JDialogPreferences;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.preferences.PreferencesPanel;
import de.mendelson.comm.as2.preferences.PreferencesPanelConnectivity;
import de.mendelson.comm.as2.preferences.PreferencesPanelDirectories;
import de.mendelson.comm.as2.preferences.PreferencesPanelHttpAuth;
import de.mendelson.comm.as2.preferences.JDialogUserPreferences;
import de.mendelson.comm.as2.preferences.PreferencesPanelInterface;
import de.mendelson.comm.as2.preferences.PreferencesPanelLog;
import de.mendelson.comm.as2.preferences.PreferencesPanelMDN;
import de.mendelson.comm.as2.preferences.PreferencesPanelNotification;
import de.mendelson.comm.as2.preferences.PreferencesPanelProxy;
import de.mendelson.comm.as2.preferences.PreferencesPanelSystemMaintenance;
import de.mendelson.comm.as2.preferences.ResourceBundlePreferencesAS2;
import de.mendelson.comm.as2.server.EventBus;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.ColorUtil;
import de.mendelson.util.DateChooserUI;
import de.mendelson.util.DisplayMode;
import de.mendelson.util.KeyboardShortcutUtil;
import de.mendelson.util.LayoutManagerJToolbar;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.MendelsonMultiResolutionImage.SVGScalingOption;
import de.mendelson.util.Splash;
import de.mendelson.util.WindowTitleUtil;
import de.mendelson.util.clientserver.ClientsideMessageProcessor;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.clientserver.SyncRequestTimeoutException;
import de.mendelson.util.clientserver.about.ServerInfoRequest;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.comm.as2.usermanagement.Permissions;
import de.mendelson.comm.as2.usermanagement.gui.JDialogChangePassword;
import de.mendelson.comm.as2.usermanagement.clientserver.UserListRequest;
import de.mendelson.comm.as2.usermanagement.clientserver.UserListResponse;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadRequestFile;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadResponseFile;
import de.mendelson.util.clientserver.clients.datatransfer.TransferClientWithProgress;
import de.mendelson.util.clientserver.clients.preferences.ConfigurationChangedOnServer;
import de.mendelson.util.clientserver.clients.preferences.ConfigurationChangedOnServerPreferences;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
import de.mendelson.util.clientserver.log.search.gui.JDialogSearchLogfile;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.LoginResponse;
import de.mendelson.util.clientserver.messages.ServerInfo;
import de.mendelson.util.clientserver.user.User;
import de.mendelson.util.log.LogFormatter;
import de.mendelson.util.log.LogFormatterAS2;
import de.mendelson.util.log.panel.LogConsolePanel;
import de.mendelson.util.modulelock.AllowConfigurationModificationCallback;
import de.mendelson.util.modulelock.LockClientInformation;
import de.mendelson.util.modulelock.LockRefreshThread;
import de.mendelson.util.modulelock.ModuleLock;
import de.mendelson.util.modulelock.message.ModuleLockRequest;
import de.mendelson.util.modulelock.message.ModuleLockResponse;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.DefaultKeyCopyHandler;
import de.mendelson.util.security.cert.KeyCopyHandler;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.security.cert.clientserver.KeyCopyRequest;
import de.mendelson.util.security.cert.clientserver.KeystoreStorageImplClientServer;
import de.mendelson.util.security.cert.gui.JDialogCertificates;
import de.mendelson.util.security.cert.gui.ResourceBundleCertificates;
import de.mendelson.util.systemevents.gui.JDialogSystemEvents;
import de.mendelson.util.tables.ColumnFitAdapter;
import de.mendelson.util.tables.JTableColumnResizer;
import de.mendelson.util.tables.TableCellRendererDate;
import de.mendelson.util.tables.hideablecolumns.HideableColumn;
import de.mendelson.util.tables.hideablecolumns.JDialogColumnConfig;
import de.mendelson.util.tables.hideablecolumns.TableColumnHiddenStateListener;
import de.mendelson.util.tables.hideablecolumns.TableColumnModelHideable;
import de.mendelson.util.uinotification.UINotification;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Taskbar;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Main GUI for the control of the mendelson AS2 server
 *
 * @author S.Heller
 * @version $Revision: 48 $
 */
public class AS2Gui extends GUIClient implements ListSelectionListener, RowSorterListener,
        ClientsideMessageProcessor, MouseListener, PopupMenuListener, ModuleStarter,
        TableColumnHiddenStateListener {

    // Icon management is now delegated to IconManager class

    /**
     * Preferences of the application
     */
    private final PreferencesAS2 clientPreferences = new PreferencesAS2();
    private static final Logger logger = Logger.getLogger("de.mendelson.as2.client");
    /**
     * Resourcebundle to localize the GUI
     */
    private final MecResourceBundle rb;
    private final MecResourceBundle rbCertGui;
    private final MecResourceBundle rbPreferences;
    // Oracle JavaHelp fields removed - incompatible with JDK 17+
    /**
     * Flag to show/hide the filter panel
     */
    private boolean showFilterPanel = false;
    // helpHasBeenDisplayed flag removed - no longer needed
    /**
     * Host to connect to
     */
    private final String host;

    /**
     * Current username (can be switched via impersonation)
     */
    private String username;
    /**
     * Original admin username (always "admin" for SwingUI)
     */
    private final String originalUsername = "admin";
    /**
     * Cached user ID for current user (0 = admin, >0 = specific user)
     */
    private int userId = 1;
    /**
     * Permission manager for checking user permissions
     */
    private SwingUIPermissionManager permissionManager;


    public static final String DARK_MODE_CLASSNAME = "com.formdev.flatlaf.FlatDarculaLaf";
    public static final String HIGH_CONSTRAST_MODE_CLASSNAME = "com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme";

    /**
     * Refresh thread for the transaction overview - schedules the refresh
     * requests
     */
    private final RefreshThread refreshThread = new RefreshThread();
    private final LogConsolePanel consolePanel;
    /**
     * EventBus listener for server notifications
     */
    private EventBus.EventListener eventListener;
    /**
     * This dialog is just hidden, never closed
     */
    private JDialogSystemEvents dialogSystemEvents = null;
    private Date filterStartDate = new Date();
    private Date filterEndDate = new Date();
    private Color COLOR_RED = Color.RED.darker();

    /**
     * Creates new form NewJFrame
     */
    public AS2Gui(Splash splash, String host, String username, String password, String displayMode) {
        this.host = host;
        this.username = username;

        // Set username in base client for user-scoped operations
        this.getBaseClient().setUsername(username);

        if (displayMode == null) {
            displayMode = "LIGHT";
        }
        this.setLookAndFeel(displayMode);
        // load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Gui.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        // load resource bundle
        try {
            this.rbCertGui = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificates.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        // load resource bundle
        try {
            this.rbPreferences = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferencesAS2.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        initComponents();
        this.setMultiresolutionIcons();
        this.setupToolbarTooltips();
        this.initializeDesktopIntegration();
        this.setButtonsMultiClickThreshhold();
        // color contrast setup for red
        this.COLOR_RED = ColorUtil.getBestContrastColorAroundForeground(
                this.jLabelRefreshStopWarning.getBackground(),
                COLOR_RED);
        this.jLabelRefreshStopWarning.setForeground(COLOR_RED);
        this.jPanelRefreshWarning.setBorder(javax.swing.BorderFactory.createLineBorder(COLOR_RED));
        this.jPanelRefreshWarning.setVisible(false);
        // set preference values to the GUI
        this.setBounds(
                this.clientPreferences.getInt(PreferencesAS2.FRAME_X),
                this.clientPreferences.getInt(PreferencesAS2.FRAME_Y),
                this.clientPreferences.getInt(PreferencesAS2.FRAME_WIDTH),
                this.clientPreferences.getInt(PreferencesAS2.FRAME_HEIGHT));
        // ensure to display all messages
        this.getLogger().setLevel(Level.ALL);
        this.consolePanel = new LogConsolePanel(this.getLogger(),
                new LogFormatterAS2(LogFormatter.FORMAT_CONSOLE_COLORED),
                new Font(Font.MONOSPACED, Font.PLAIN, 12),
                displayMode);
        // define the colors for the log levels
        consolePanel.setColor(Level.SEVERE, LogConsolePanel.COLOR_DARK_RED);
        consolePanel.setColor(Level.WARNING, LogConsolePanel.COLOR_DARK_BLUE);
        consolePanel.setColor(Level.INFO, LogConsolePanel.COLOR_BLACK);
        consolePanel.setColor(Level.CONFIG, LogConsolePanel.COLOR_DARK_GREEN);
        consolePanel.setColor(Level.FINE, LogConsolePanel.COLOR_LIGHT_GRAY);
        consolePanel.setColor(Level.FINER, LogConsolePanel.COLOR_LIGHT_GRAY);
        consolePanel.setColor(Level.FINEST, LogConsolePanel.COLOR_LIGHT_GRAY);
        this.jPanelServerLog.add(consolePanel);
        String title = AS2ServerVersion.getProductName() + " " + AS2ServerVersion.getVersion();
        if (host != null && !host.equals("localhost")) {
            title = "[" + host + "] " + title;
        }
        // For main window, just add [TEST MODE] suffix if in test mode, don't duplicate
        // product name
        if (WindowTitleUtil.isTestMode()) {
            title = title + WindowTitleUtil.getTestModeSuffix();
        }
        this.setTitle(title);
        // initialize the help system if available
        // this.initializeJavaHelp(displayMode);
        this.jTableMessageOverview.setRowHeight(TableModelMessageOverview.ROW_HEIGHT);
        this.jTableMessageOverview.getSelectionModel().addListSelectionListener(this);
        this.jTableMessageOverview.getTableHeader().setReorderingAllowed(false);
        // icon columns
        TableColumn column = this.jTableMessageOverview.getColumnModel().getColumn(0);
        column.setMaxWidth(TableModelMessageOverview.ROW_HEIGHT + this.jTableMessageOverview.getRowMargin() * 2);
        column.setResizable(false);
        column = this.jTableMessageOverview.getColumnModel().getColumn(1);
        column.setMaxWidth(TableModelMessageOverview.ROW_HEIGHT + this.jTableMessageOverview.getRowMargin() * 2);
        column.setResizable(false);
        this.jTableMessageOverview.setDefaultRenderer(Date.class,
                new TableCellRendererDate(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)));
        // add row sorter
        RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.jTableMessageOverview.getModel());
        this.jTableMessageOverview.setRowSorter(sorter);
        sorter.addRowSorterListener(this);
        this.jPanelFilterOverviewContainer.setVisible(this.showFilterPanel);
        this.jTableMessageOverview.getTableHeader().addMouseListener(new ColumnFitAdapter());
        this.jComboBoxFilterDirection.setRenderer(new ListCellRendererDirection());
        this.jComboBoxFilterLocalStation.setRenderer(new ListCellRendererPartner());
        this.jComboBoxFilterPartner.setRenderer(new ListCellRendererPartner());
        this.setDirectionFilter();
        this.setButtonState();
        // popup menu issues
        this.jPopupMenu.setInvoker(this.jScrollPaneMessageOverview);
        this.jPopupMenu.addPopupMenuListener(this);
        this.jTableMessageOverview.addMouseListener(this);
        super.addMessageProcessor(this);
    
        this.configureHideableColumns();
        this.jToolBar.setLayout(new LayoutManagerJToolbar());
        this.setupDateChooser();
        if (splash != null) {
            splash.destroy();
        }
        this.initializeUINotification();

        // Initialize userId from username
                this.userId = this.getUserIdFromUsername(username);
        
        // Initialize permission manager and load permissions
        this.permissionManager = new SwingUIPermissionManager(this.getBaseClient());
        try {
            this.permissionManager.loadPermissions();
        } catch (Exception e) {
            this.logger.severe("Failed to load user permissions: " + e.getMessage());
            // Continue with empty permissions - user will have no access
        }

        // Apply permission checks to menus
        this.updateMenusForPermissions();

        // Subscribe to EventBus for server events (replaces Mina socket connection)
        this.eventListener = message -> {
            // Call on EDT to ensure thread safety
            SwingUtilities.invokeLater(() -> {
                processMessageFromServer(message);
            });
        };
        EventBus.getInstance().subscribe(this.eventListener);

        // No Mina authentication needed - SwingUI runs in same JVM as server
        // Start the table update thread and initialize status bar
        GUIClient.scheduleWithFixedDelay(this.refreshThread, 3000, 3000, TimeUnit.MILLISECONDS);
        this.as2StatusBar.initialize(this.getBaseClient(), this);
        this.as2StatusBar.startConfigurationChecker();

        // Check and show admin configuration warnings (SwingUI always runs as admin)
        AdminConfigurationWarning.checkAndShowWarnings(this, username, this.getBaseClient());
    }

    /**
     * Perform login with retry capability on failure
     * Shows a login dialog that allows the user to retry if authentication fails
     * @param initialUsername Initial username to try
     * @param initialPassword Initial password to try
     * @return LoginResponse if successful, null if user cancels
     */
    @SuppressWarnings("unused")
    private LoginResponse performLoginWithRetry(String initialUsername, char[] initialPassword) {
        String username = initialUsername;
        char[] password = initialPassword;

        while (true) {
            // Attempt login
            LoginResponse loginResponse = this.performLogin(username, password, "AS2Gui");

            if (loginResponse != null && loginResponse.isSuccess()) {
                // Login successful - start the table update thread and initialize status bar
                GUIClient.scheduleWithFixedDelay(this.refreshThread, 3000, 3000, TimeUnit.MILLISECONDS);
                this.as2StatusBar.initialize(this.getBaseClient(), this);
                this.as2StatusBar.startConfigurationChecker();

                // Check and show admin configuration warnings
                AdminConfigurationWarning.checkAndShowWarnings(this, username, this.getBaseClient());

                return loginResponse;
            }

            // Login failed - show error and prompt for retry
            String errorMsg = loginResponse != null ? loginResponse.getErrorMessage() : "Authentication failed";
            this.getLogger().severe("SwingUI login failed: " + errorMsg);

            // Create a custom dialog with retry/cancel options
            Object[] options = {"Retry", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                null,
                "Authentication failed: " + errorMsg + "\n\nWould you like to retry?",
                "Login Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]
            );

            if (choice != 0) { // User chose "Exit" or closed dialog
                System.exit(1);
            }

            // Show login dialog for retry
            // Detect display mode from current Look and Feel
            String currentLaF = UIManager.getLookAndFeel().getClass().getName();
            String displayMode = DisplayMode.LIGHT; // Default
            if (currentLaF.contains("Darcula")) {
                displayMode = DisplayMode.DARK;
            } else if (currentLaF.contains("HighContrast")) {
                displayMode = DisplayMode.HICONTRAST;
            }

            JDialogLogin loginDialog = new JDialogLogin(this, displayMode);
            loginDialog.setUsername(username); // Pre-fill with previous username
            loginDialog.setVisible(true);

            if (loginDialog.isCanceled()) {
                System.exit(1);
            }

            // Get new credentials from dialog
            username = loginDialog.getUsername();
            password = loginDialog.getPassword();
        }
    }

    /**
     * Sets the look and feel of the client
     */
    private void setLookAndFeel(String displayMode) {
        try {
            // support the command line option -Dswing.defaultlaf=...
            if (System.getProperty("swing.defaultlaf") == null) {
                try {
                    if (displayMode.equalsIgnoreCase(DisplayMode.DARK)) {
                        try {
                            UIManager.setLookAndFeel(DARK_MODE_CLASSNAME);
                            // Button.arc is the corner arc diameter for buttons and toggle buttons (default
                            // is 6)
                            UIManager.put("Button.arc", 4);
                            // Component.arc is used for other components like combo boxes and spinners
                            // (default is 5)
                            UIManager.put("Component.arc", 2);
                            // CheckBox.arc is used for check box icon (default is 4)
                            UIManager.put("CheckBox.arc", 2);
                            // ProgressBar.arc is used for progress bars (default is 4).
                            UIManager.put("ProgressBar.arc", 2);
                            // TextComponent.arc is used for text fields (default is 0)
                            UIManager.put("TextComponent.arc", 0);
                            // Colors
                            UIManager.put("Objects.Green", new ColorUIResource(98, 181, 67));
                            UIManager.put("Objects.Red", new ColorUIResource(242, 101, 34));
                            UIManager.put("Objects.RedStatus", new ColorUIResource(224, 85, 85));
                            UIManager.put("Objects.Blue", new ColorUIResource(64, 182, 224));
                            UIManager.put("Objects.Yellow", new ColorUIResource(244, 175, 61));
                        } catch (Throwable e) {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        }
                    } else if (displayMode.equalsIgnoreCase(DisplayMode.HICONTRAST)) {
                        try {
                            UIManager.setLookAndFeel(HIGH_CONSTRAST_MODE_CLASSNAME);
                            // Button.arc is the corner arc diameter for buttons and toggle buttons (default
                            // is 6)
                            UIManager.put("Button.arc", 4);
                            // Component.arc is used for other components like combo boxes and spinners
                            // (default is 5)
                            UIManager.put("Component.arc", 2);
                            // CheckBox.arc is used for check box icon (default is 4)
                            UIManager.put("CheckBox.arc", 2);
                            // ProgressBar.arc is used for progress bars (default is 4).
                            UIManager.put("ProgressBar.arc", 2);
                            // TextComponent.arc is used for text fields (default is 0)
                            UIManager.put("TextComponent.arc", 0);
                            // Colors
                            UIManager.put("Objects.Green", new ColorUIResource(0, 230, 31));
                            UIManager.put("Objects.Red", new ColorUIResource(255, 67, 64));
                            UIManager.put("Objects.RedStatus", new ColorUIResource(255, 67, 64));
                            UIManager.put("Objects.Blue", new ColorUIResource(0, 234, 255));
                            UIManager.put("Objects.Yellow", new ColorUIResource(255, 211, 51));
                        } catch (Throwable e) {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        }
                    } else {
                        // light mode
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    // fall back to metal l&f if an error occured with any l&f
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                }
            }
        } catch (Exception e) {
            logger.warning("[" + e.getClass().getSimpleName() + "]:" + e.getMessage());
        }
        // L&F changes for the mendelson products
        UIManager.put("TableHeader.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        UIManager.put("Label.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        UIManager.put("CheckBox.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        UIManager.put("RadioButton.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        UIManager.put("List.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        UIManager.put("Button.margin", new InsetsUIResource(4, 14, 4, 14));
        UIManager.put("Button.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 13));
        UIManager.put("ToggleButton.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 13));
        UIManager.put("TabbedPane.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 13));
        UIManager.put("Textfield.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 11));
        UIManager.put("Tree.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 11));
        UIManager.put("Menu.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        UIManager.put("MenuItem.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        UIManager.put("PopupMenu.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        UIManager.put("TextArea.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        UIManager.put("EditorPane.font", new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
    }

    private void setMultiresolutionIcons() {
        // Initialize IconManager
        IconManager.initialize();

        // Toolbar and button icons
        this.jButtonFilter.setIcon(IconManager.getFilterIconToolbar());
        this.jButtonDeleteMessage.setIcon(IconManager.getDeleteIconToolbar());
        this.jButtonMessageDetails.setIcon(IconManager.getMessageDetailsIconToolbar());
        this.jButtonCertificatesSignEncrypt.setIcon(IconManager.getCertificateIconToolbar());
        this.jButtonCertificatesTLS.setIcon(IconManager.getCertificateIconToolbar());
        this.jButtonPartner.setIcon(IconManager.getPartnerIconToolbar());
        this.jButtonUserManagement.setIcon(IconManager.getUserManagementIconToolbar());
        this.jToggleButtonStopRefresh.setIcon(IconManager.getStopIconToolbar());
        this.jButtonConfigureColumns.setIcon(IconManager.getColumnIconToolbar());
        this.jButtonHideFilter.setIcon(IconManager.getHideIconMenuItem());

        // Menu item icons
        this.jMenuItemCertificatesSSL.setIcon(IconManager.getCertificateIconMenuItem());
        this.jMenuItemCertificatesSignCrypt.setIcon(IconManager.getCertificateIconMenuItem());
        this.jMenuItemFileSend.setIcon(IconManager.getManualSendIconMenuItem());
        this.jMenuItemPartner.setIcon(IconManager.getPartnerIconMenuItem());
        this.jMenuItemUserManagement.setIcon(IconManager.getUserManagementIconMenuItem());
        this.jMenuItemSearchInServerLog.setIcon(IconManager.getLogSearchIconMenuItem());
        this.jMenuItemHTTPServerInfo.setIcon(IconManager.getPortsIconMenuItem());
        this.jMenuItemFileExit.setIcon(IconManager.getExitIconMenuItem());
        this.jMenuItemHelpAbout.setIcon(IconManager.getProductLogoIconMenuItem());
        this.jMenuItemHelpSystem.setIcon(IconManager.getProductLogoIconMenuItem());
        this.jMenuItemFilePreferences.setIcon(IconManager.getPreferencesIconMenuItem());
        this.jMenuItemSystemEvents.setIcon(IconManager.getSysinfoIconMenuItem());

        // Popup menu icons
        this.jMenuItemPopupDeleteMessage.setIcon(IconManager.getDeleteIconPopup());
        this.jMenuItemPopupMessageDetails.setIcon(IconManager.getMessageDetailsIconPopup());
        this.jMenuItemPopupSendAgain.setIcon(IconManager.getManualSendIconPopup());

        // Filter status label icons
        this.jLabelFilterShowError.setIcon(IconManager.getStoppedIconMenuItem());
        this.jLabelFilterShowOk.setIcon(IconManager.getFinishedIconMenuItem());
        this.jLabelFilterShowPending.setIcon(IconManager.getPendingIconMenuItem());
    }

    /**
     * Setup tooltips for toolbar buttons showing keyboard shortcuts
     */
    private void setupToolbarTooltips() {
        // Add tooltips to toolbar buttons with their current text
        String partnerText = this.jButtonPartner.getText();
        String userMgmtText = this.jButtonUserManagement.getText();
        String signCryptText = this.jButtonCertificatesSignEncrypt.getText();
        String tlsText = this.jButtonCertificatesTLS.getText();

        // Add keyboard shortcuts to main toolbar buttons
        // Partner button - Cmd/Ctrl+A (A for pArtner, avoiding R which is used for Toggle Refresh)
        KeyboardShortcutUtil.addButtonShortcut(
                this.jButtonPartner,
                java.awt.event.KeyEvent.VK_A,
                "OPEN_PARTNER");
        this.jButtonPartner.setToolTipText(partnerText + " [" +
                KeyboardShortcutUtil.getShortcutDisplayText(java.awt.event.KeyEvent.VK_A) + "]");

        // User Management button - Cmd/Ctrl+U
        KeyboardShortcutUtil.addButtonShortcut(
                this.jButtonUserManagement,
                java.awt.event.KeyEvent.VK_U,
                "OPEN_USER_MANAGEMENT");
        this.jButtonUserManagement.setToolTipText(userMgmtText + " [" +
                KeyboardShortcutUtil.getShortcutDisplayText(java.awt.event.KeyEvent.VK_U) + "]");

        // Sign/Crypt button - Cmd/Ctrl+C (C for Crypt/Certificates)
        KeyboardShortcutUtil.addButtonShortcut(
                this.jButtonCertificatesSignEncrypt,
                java.awt.event.KeyEvent.VK_C,
                "OPEN_SIGNCRYPT");
        this.jButtonCertificatesSignEncrypt.setToolTipText(signCryptText + " [" +
                KeyboardShortcutUtil.getShortcutDisplayText(java.awt.event.KeyEvent.VK_C) + "]");

        // TLS button - Cmd/Ctrl+T (T for TLS)
        KeyboardShortcutUtil.addButtonShortcut(
                this.jButtonCertificatesTLS,
                java.awt.event.KeyEvent.VK_T,
                "OPEN_TLS");
        this.jButtonCertificatesTLS.setToolTipText(tlsText + " [" +
                KeyboardShortcutUtil.getShortcutDisplayText(java.awt.event.KeyEvent.VK_T) + "]");

        // Message Details button - Cmd/Ctrl+D
        KeyboardShortcutUtil.addButtonShortcut(
                this.jButtonMessageDetails,
                java.awt.event.KeyEvent.VK_D,
                "SHOW_MESSAGE_DETAILS");
        this.jButtonMessageDetails.setToolTipText(
                this.rb.getResourceString("details") + " [" +
                        KeyboardShortcutUtil.getShortcutDisplayText(java.awt.event.KeyEvent.VK_D) + "]");

        // Delete Message button - DELETE key (no modifier) and Cmd+Backspace on Mac
        KeyboardShortcutUtil.addButtonShortcut(
                this.jButtonDeleteMessage,
                java.awt.event.KeyEvent.VK_DELETE,
                0, // No modifiers - just DELETE key
                "DELETE_MESSAGE");
        // Add Cmd+Backspace for Mac users (standard macOS delete shortcut)
        KeyboardShortcutUtil.addButtonShortcut(
                this.jButtonDeleteMessage,
                java.awt.event.KeyEvent.VK_BACK_SPACE,
                "DELETE_MESSAGE_BACKSPACE");
        this.jButtonDeleteMessage.setToolTipText(
                this.rb.getResourceString("delete.msg") + " [DELETE or " +
                KeyboardShortcutUtil.getShortcutDisplayText(java.awt.event.KeyEvent.VK_BACK_SPACE) + "]");

        // Filter button - Cmd/Ctrl+F (FIXED: use correct key "filter")
        KeyboardShortcutUtil.addButtonShortcut(
                this.jButtonFilter,
                java.awt.event.KeyEvent.VK_F,
                "TOGGLE_FILTER");
        this.jButtonFilter.setToolTipText(
                this.rb.getResourceString("filter") + " [" +
                        KeyboardShortcutUtil.getShortcutDisplayText(java.awt.event.KeyEvent.VK_F) + "]");

        // Toggle Refresh button - Cmd/Ctrl+R (NEW)
        KeyboardShortcutUtil.addButtonShortcut(
                this.jToggleButtonStopRefresh,
                java.awt.event.KeyEvent.VK_R,
                "TOGGLE_REFRESH");
        this.jToggleButtonStopRefresh.setToolTipText(
                this.rb.getResourceString("stoprefresh.msg") + " [" +
                        KeyboardShortcutUtil.getShortcutDisplayText(java.awt.event.KeyEvent.VK_R) + "]");

        // Configure Columns button - Cmd/Ctrl+L (NEW)
        KeyboardShortcutUtil.addButtonShortcut(
                this.jButtonConfigureColumns,
                java.awt.event.KeyEvent.VK_L,
                "CONFIGURE_COLUMNS");
        this.jButtonConfigureColumns.setToolTipText(
                this.rb.getResourceString("configurecolumns") + " [" +
                        KeyboardShortcutUtil.getShortcutDisplayText(java.awt.event.KeyEvent.VK_L) + "]");
    }

    /**
     * Prevent that double clicks on buttons of the menu bar can open panels
     * multiple times
     */
    private void setButtonsMultiClickThreshhold() {
        long threshhold = TimeUnit.SECONDS.toMillis(1);
        this.jButtonCertificatesTLS.setMultiClickThreshhold(threshhold);
        this.jButtonCertificatesSignEncrypt.setMultiClickThreshhold(threshhold);
        this.jButtonConfigureColumns.setMultiClickThreshhold(threshhold);
        this.jButtonDeleteMessage.setMultiClickThreshhold(threshhold);
        this.jButtonFilter.setMultiClickThreshhold(threshhold);
        this.jButtonMessageDetails.setMultiClickThreshhold(threshhold);
        this.jButtonPartner.setMultiClickThreshhold(threshhold);
    }

    /**
     * Initializes the User Interface notification - also for the dark mode
     */
    private void initializeUINotification() {
        UINotification.instance()
                .setAnchor(this)
                .setStart(UINotification.START_POS_RIGHT_LOWER)
                .setGaps(2, 10, (int) this.as2StatusBar.getPreferredSize().getHeight() + 10)
                .setTiming(
                        UINotification.DEFAULT_NOTIFICATION_DISPLAY_TIME_FADEIN_IN_MS,
                        UINotification.DEFAULT_NOTIFICATION_DISPLAY_TIME_IN_MS,
                        UINotification.DEFAULT_NOTIFICATION_DISPLAY_TIME_FADEOUT_IN_MS)
                .setAllColorsDefaultFromUIManager();
    }

    private void configureHideableColumns() {
        TableColumnModelHideable tableColumnModel = new TableColumnModelHideable(
                this.jTableMessageOverview.getColumnModel());
        this.jTableMessageOverview.setColumnModel(tableColumnModel);
        // configure columns
        HideableColumn[] hideableColumns = tableColumnModel.getColumnsSorted();
        String hiddenColsStr = this.clientPreferences.get(PreferencesAS2.HIDDENCOLS);
        String hideableColsStr = this.clientPreferences.get(PreferencesAS2.HIDEABLECOLS);
        if (hideableColumns.length != hiddenColsStr.length()) {
            hiddenColsStr = this.clientPreferences.get(PreferencesAS2.HIDDENCOLSDEFAULT);
        }
        for (int i = 0; i < hiddenColsStr.length(); i++) {
            hideableColumns[i].setHideable(hideableColsStr.charAt(i) == '1');
            hideableColumns[i].setVisible(hiddenColsStr.charAt(i) == '1');
        }
        tableColumnModel.updateState();
    }

    private void storeColumSettings() {
        TableColumnModelHideable tableColumnModel = (TableColumnModelHideable) this.jTableMessageOverview
                .getColumnModel();
        HideableColumn[] hideableColumns = tableColumnModel.getColumnsSorted();
        StringBuilder builder = new StringBuilder();
        for (HideableColumn col : hideableColumns) {
            if (col.isVisible()) {
                builder.append("1");
            } else {
                builder.append("0");
            }
        }
        this.clientPreferences.put(PreferencesAS2.HIDDENCOLS, builder.toString());
    }

    /**
     * This is mainly for the MAC OS integration and defines handler for the
     * About, preferences and quit dialog entries. Lets the Menu Bar of the main
     * screen move to where the mac OS user expect it. Sets an image in the
     *
     */
    private void initializeDesktopIntegration() {
        // sets the application icons in multiple resolutions
        this.setIconImages(IconManager.getProductLogo().getResolutionVariants());
        // Moves the main Menu Bar to where the Mac OS users expect it - this property
        // is ignored on
        // other platforms
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
                // do nothing
            }
            // Removed APP_PREFERENCES handler - using File menu Preferences instead
            if (desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
                desktop.setQuitHandler(new QuitHandler() {
                    @Override
                    public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
                        AS2Gui.this.exitApplication();
                    }
                });
            }
        }
        // Set taskbar icon
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                try {
                    taskbar.setIconImage(IconManager.getProductLogo());
                } catch (SecurityException e) {
                    // nop
                }
            }
        }
    }

    /**
     * Defines the date chooser and the used colors
     */
    private void setupDateChooser() {
        this.jDateChooserStartDate.setUI(new DateChooserUI());
        this.jDateChooserStartDate.setLocale(Locale.getDefault());
        this.jDateChooserStartDate.setDate(this.filterStartDate);
        this.jDateChooserStartDate.getDateEditor().addPropertyChangeListener(
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        if (e.getPropertyName() != null && e.getPropertyName().startsWith("date")) {
                            filterStartDate = jDateChooserStartDate.getDate();
                        }
                    }
                });
        this.jDateChooserEndDate.setUI(new DateChooserUI());
        this.jDateChooserEndDate.setLocale(Locale.getDefault());
        this.jDateChooserEndDate.setDate(this.filterEndDate);
        this.jDateChooserEndDate.getDateEditor().addPropertyChangeListener(
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        if (e.getPropertyName() != null && e.getPropertyName().startsWith("date")) {
                            filterEndDate = jDateChooserEndDate.getDate();
                        }
                    }
                });
    }

    public void loginRequestedFromServer() {
        // Authentication is now handled by performLoginWithRetry() in constructor
        // Just initialize the UI components here
        this.as2StatusBar.setConnectedHost(this.host);
        // Note: RefreshThread will be started after successful authentication
    }

    /**
     * Override messageReceivedFromServer to detect ServerInfo and initialize the UI
     * Since LoginRequired is no longer sent, we use ServerInfo as the trigger
     */
    public void messageReceivedFromServer(de.mendelson.util.clientserver.messages.ClientServerMessage message) {
        // Detect ServerInfo message (sent when client connects)
        if (message instanceof de.mendelson.util.clientserver.messages.ServerInfo) {
            // Call parent to process ServerInfo
            super.messageReceivedFromServer(message);
            // Trigger the same initialization that loginRequestedFromServer would have done
            SwingUtilities.invokeLater(() -> {
                loginRequestedFromServer();
            });
        } else {
            // Let parent handle other messages
            super.messageReceivedFromServer(message);
        }
    }

    public Logger getLogger() {
        return (AS2Gui.logger);
    }

    /**
     * Shows forced password change dialog if user must change password on first login
     */
    @SuppressWarnings("unused")
    private void showForcedPasswordChangeDialog(User user) {
        try {
            // Get user info from server via client-server messaging
            UserListRequest request = new UserListRequest();
            UserListResponse response = (UserListResponse) this.sendSync(request, 10000);

            if (response == null || response.getUsers() == null) {
                this.getLogger().severe("Cannot load user list for password change");
                return;
            }

            // Find the current user (admin)
            WebUIUser dbUser = null;
            for (WebUIUser u : response.getUsers()) {
                if (u.getUsername().equals(user.getName())) {
                    dbUser = u;
                    break;
                }
            }

            if (dbUser == null) {
                this.getLogger().severe("Cannot find user for password change: " + user.getName());
                return;
            }

            // Show message about password change requirement
            JOptionPane.showMessageDialog(this,
                    "<html><b>Password change required</b><br><br>" +
                    "This is your first login or your password has been reset.<br>" +
                    "You must change your password to continue.</html>",
                    "Password Change Required",
                    JOptionPane.WARNING_MESSAGE);

            // Show password change dialog
            JDialog parentDialog = new JDialog(this, "Change Password", true);
            JDialogChangePassword passwordDialog = new JDialogChangePassword(
                    parentDialog,
                    this,  // GUIClient
                    dbUser
            );
            passwordDialog.setVisible(true);

            // After password changed, verify the flag was cleared
            response = (UserListResponse) this.sendSync(new UserListRequest(), 10000);
            if (response != null && response.getUsers() != null) {
                for (WebUIUser u : response.getUsers()) {
                    if (u.getUsername().equals(user.getName())) {
                        if (u.isMustChangePassword()) {
                            // User closed dialog without changing - force exit
                            JOptionPane.showMessageDialog(this,
                                    "Password change is required. Application will exit.",
                                    "Password Change Required",
                                    JOptionPane.WARNING_MESSAGE);
                            System.exit(0);
                        }
                        break;
                    }
                }
            }

        } catch (Exception e) {
            this.getLogger().severe("Error showing forced password change dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stores the actual GUIs preferences to restore the GUI at the next program
     * start
     */
    private void savePreferences() {
        this.clientPreferences.putInt(PreferencesAS2.FRAME_X,
                (int) this.getBounds().getX());
        this.clientPreferences.putInt(PreferencesAS2.FRAME_Y,
                (int) this.getBounds().getY());
        this.clientPreferences.putInt(PreferencesAS2.FRAME_WIDTH,
                (int) this.getBounds().getWidth());
        this.clientPreferences.putInt(PreferencesAS2.FRAME_HEIGHT,
                (int) this.getBounds().getHeight());
    }

    /**
     * Sets all items to the direction filter
     */
    private void setDirectionFilter() {
        this.jComboBoxFilterDirection.removeAllItems();
        this.jComboBoxFilterDirection.addItem(this.rb.getResourceString("filter.none"));
        this.jComboBoxFilterDirection.addItem(this.rb.getResourceString("filter.direction.inbound"));
        this.jComboBoxFilterDirection.addItem(this.rb.getResourceString("filter.direction.outbound"));
    }

    /**
     * Sets all items in the partner filter combo box
     */
    private void updatePartnerFilter(List<Partner> partner) {
        Partner selectedPartner = null;
        if (this.jComboBoxFilterPartner.getSelectedIndex() > 0) {
            selectedPartner = (Partner) this.jComboBoxFilterPartner.getSelectedItem();
        }
        Collections.sort(partner);
        this.jComboBoxFilterPartner.removeAllItems();
        this.jComboBoxFilterPartner.addItem(this.rb.getResourceString("filter.none"));
        for (Partner singlePartner : partner) {
            if (!singlePartner.isLocalStation()) {
                this.jComboBoxFilterPartner.addItem(singlePartner);
            }
        }
        if (selectedPartner != null) {
            this.jComboBoxFilterPartner.setSelectedItem(selectedPartner);
        }
        if (this.jComboBoxFilterPartner.getSelectedItem() == null) {
            this.jComboBoxFilterPartner.setSelectedIndex(0);
        }
    }

    private void updateLocalStationFilter(List<Partner> partner) {
        Partner selectedLocalStation = null;
        if (this.jComboBoxFilterLocalStation.getSelectedIndex() > 0) {
            selectedLocalStation = (Partner) this.jComboBoxFilterLocalStation.getSelectedItem();
        }
        Collections.sort(partner);
        this.jComboBoxFilterLocalStation.removeAllItems();
        this.jComboBoxFilterLocalStation.addItem(this.rb.getResourceString("filter.none"));
        for (Partner singlePartner : partner) {
            if (singlePartner.isLocalStation()) {
                this.jComboBoxFilterLocalStation.addItem(singlePartner);
            }
        }
        if (selectedLocalStation != null) {
            this.jComboBoxFilterLocalStation.setSelectedItem(selectedLocalStation);
        }
        if (this.jComboBoxFilterLocalStation.getSelectedItem() == null) {
            this.jComboBoxFilterLocalStation.setSelectedIndex(0);
        }
    }

    /**
     * Displays details for the selected msg row
     */
    private void showSelectedRowDetails() {
        final String uniqueId = this.getClass().getName() + ".showSelectedRowDetails." + System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    AS2Gui.this.jButtonMessageDetails.setEnabled(false);
                    AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                            AS2Gui.this.rb.getResourceString("details"), uniqueId);
                    int selectedRow = AS2Gui.this.jTableMessageOverview.getSelectedRow();
                    if (selectedRow >= 0) {
                        AS2Message message = ((TableModelMessageOverview) AS2Gui.this.jTableMessageOverview.getModel())
                                .getRow(selectedRow);
                        AS2MessageInfo info = (AS2MessageInfo) message.getAS2Info();
                        // download the full payload from the server
                        List<AS2Payload> payloads = ((MessagePayloadResponse) AS2Gui.this
                                .sendSync(new MessagePayloadRequest(info.getMessageId()))).getList();
                        message.setPayloads(payloads);
                        DialogMessageDetails dialog = new DialogMessageDetails(AS2Gui.this,
                                AS2Gui.this.getBaseClient(),
                                info,
                                message.getPayloads(),
                                AS2Gui.this.consolePanel.getHandler());
                        AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                        dialog.setVisible(true);
                    }
                } catch (Exception e) {
                    // nop
                } finally {
                    AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                    AS2Gui.this.setButtonState();
                }
            }
        };
        Executors.newSingleThreadExecutor().submit(runnable);
    }

    /**
     * Enables/disables the buttons of this GUI
     */
    private void setButtonState() {
        this.jButtonMessageDetails.setEnabled(this.jTableMessageOverview.getSelectedRow() >= 0
                && this.jTableMessageOverview.getSelectedRowCount() == 1);
        this.jButtonDeleteMessage.setEnabled(this.jTableMessageOverview.getSelectedRow() >= 0);
        // check if min one of the selected rows has the state "stopped" or "finished"
        int[] selectedRows = this.jTableMessageOverview.getSelectedRows();
        AS2Message[] overviewRows = ((TableModelMessageOverview) this.jTableMessageOverview.getModel())
                .getRows(selectedRows);
        boolean deletableRowSelected = false;
        for (int i = 0; i < overviewRows.length; i++) {
            if (overviewRows[i].getAS2Info().getState() == AS2Message.STATE_FINISHED
                    || overviewRows[i].getAS2Info().getState() == AS2Message.STATE_STOPPED) {
                deletableRowSelected = true;
                break;
            }
        }
        this.jButtonDeleteMessage.setEnabled(deletableRowSelected);
        if (this.filterIsSet()) {
            this.jButtonFilter.setIcon(IconManager.getFilterActiveIcon(24));
        } else {
            this.jButtonFilter.setIcon(IconManager.getFilterIcon(24));
        }

    }

    /**
     * Returns if a filter is set on the message overview entries
     */
    private boolean filterIsSet() {
        return (!this.jCheckBoxFilterShowOk.isSelected()
                || !this.jCheckBoxFilterShowPending.isSelected()
                || !this.jCheckBoxFilterShowStopped.isSelected()
                || this.jComboBoxFilterPartner.getSelectedIndex() > 0
                || this.jComboBoxFilterDirection.getSelectedIndex() > 0
                || this.jComboBoxFilterLocalStation.getSelectedIndex() > 0
                || this.jCheckBoxUseTimeFilter.isSelected());
    }

    /**
     * Makes this a ListSelectionListener
     */
    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        int selectedRowCount = this.jTableMessageOverview.getSelectedRowCount();
        this.jButtonMessageDetails.setEnabled(selectedRowCount == 1);
        this.as2StatusBar.setSelectedTransactionCount(selectedRowCount);
        this.setButtonState();
    }

    /**
     * Deletes the actual selected AS2 rows from the database, filesystem etc
     */
    private void deleteSelectedMessages() {
        int requestValue = JOptionPane.showConfirmDialog(
                this, this.rb.getResourceString("dialog.msg.delete.message"),
                this.rb.getResourceString("dialog.msg.delete.title"),
                JOptionPane.YES_NO_OPTION);
        if (requestValue != JOptionPane.YES_OPTION) {
            return;
        }
        int[] selectedRows = this.jTableMessageOverview.getSelectedRows();
        AS2Message[] overviewRows = ((TableModelMessageOverview) this.jTableMessageOverview.getModel())
                .getRows(selectedRows);
        List<AS2MessageInfo> deleteList = new ArrayList<AS2MessageInfo>();
        for (AS2Message message : overviewRows) {
            deleteList.add((AS2MessageInfo) message.getAS2Info());
        }
        DeleteMessageRequest request = new DeleteMessageRequest();
        request.setDeleteList(deleteList);
        this.getBaseClient().sendAsync(request);
    }

    /**
     * Starts a dialog that allows to send files manual to a partner
     */
    private void sendFileManual() {
        try {
            JDialogManualSend dialog = new JDialogManualSend(this,
                    this.getBaseClient(), this.as2StatusBar,
                    this.rb.getResourceString("uploading.to.server"),
                    this.userId);  // Pass current user ID
            dialog.setVisible(true);
        } catch (Exception e) {
            AS2Gui.logger.severe("[" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
    }

    /**
     * Starts a dialog that allows to send files manual to a partner
     */
    private void resendTransactions() {
        final int[] selectedRows = AS2Gui.this.jTableMessageOverview.getSelectedRows();
        int requestValue;
        if (selectedRows.length > 1) {
            requestValue = JOptionPane.showConfirmDialog(
                    this,
                    this.rb.getResourceString("dialog.resend.message.multiple", String.valueOf(selectedRows.length)),
                    this.rb.getResourceString("dialog.resend.title"),
                    JOptionPane.YES_NO_OPTION);
        } else {
            requestValue = JOptionPane.showConfirmDialog(
                    this, this.rb.getResourceString("dialog.resend.message"),
                    this.rb.getResourceString("dialog.resend.title"),
                    JOptionPane.YES_NO_OPTION);
        }
        if (requestValue != JOptionPane.YES_OPTION) {
            return;
        }
        final String uniqueId = this.getClass().getName() + ".sendFileManualFromSelectedTransaction."
                + System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Path tempFile = null;
                try {
                    if (selectedRows.length > 1) {
                        AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                                AS2Gui.this.rb.getResourceString("menu.file.resend.multiple"), uniqueId);
                    } else {
                        AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                                AS2Gui.this.rb.getResourceString("menu.file.resend"), uniqueId);
                    }
                    for (int selectedRow : selectedRows) {
                        if (selectedRow >= 0) {
                            // download the payload for the selected message
                            JDialogManualSend dialog = new JDialogManualSend(AS2Gui.this,
                                    AS2Gui.this.getBaseClient(), AS2Gui.this.as2StatusBar,
                                    AS2Gui.this.rb.getResourceString("uploading.to.server"),
                                    AS2Gui.this.userId);  // Pass current user ID
                            AS2Message message = ((TableModelMessageOverview) AS2Gui.this.jTableMessageOverview
                                    .getModel()).getRow(selectedRow);
                            if (message != null) {
                                AS2MessageInfo info = (AS2MessageInfo) message.getAS2Info();
                                PartnerListRequest listRequest = new PartnerListRequest(
                                        PartnerListRequest.LIST_BY_AS2_ID);
                                listRequest.setAdditionalListOptionStr(info.getSenderId());
                                listRequest.setUserId(AS2Gui.this.userId);  // Set user context
                                Partner sender = ((PartnerListResponse) AS2Gui.this.sendSync(listRequest)).getList()
                                        .get(0);
                                listRequest = new PartnerListRequest(PartnerListRequest.LIST_BY_AS2_ID);
                                listRequest.setAdditionalListOptionStr(info.getReceiverId());
                                listRequest.setUserId(AS2Gui.this.userId);  // Set user context
                                Partner receiver = ((PartnerListResponse) AS2Gui.this.sendSync(listRequest)).getList()
                                        .get(0);
                                List<AS2Payload> payloads = ((MessagePayloadResponse) AS2Gui.this
                                        .sendSync(new MessagePayloadRequest(info.getMessageId()))).getList();
                                for (AS2Payload payload : payloads) {
                                    message.addPayload(payload);
                                }
                                String originalFilename = "as2.bin";
                                if (message.getPayloadCount() > 0) {
                                    AS2Payload payload = message.getPayload(0);
                                    // request the payload file from the server
                                    TransferClientWithProgress transferClient = new TransferClientWithProgress(
                                            AS2Gui.this.getBaseClient(),
                                            AS2Gui.this.as2StatusBar.getProgressPanel());
                                    DownloadRequestFile downloadRequest = new DownloadRequestFile();
                                    downloadRequest.setFilename(payload.getPayloadFilename());
                                    InputStream inStream = null;
                                    OutputStream outStream = null;
                                    try {
                                        DownloadResponseFile response = (DownloadResponseFile) transferClient
                                                .download(downloadRequest);
                                        if (response.getException() != null) {
                                            throw response.getException();
                                        }
                                        if (payload.getOriginalFilename() != null) {
                                            // set the original filename to use
                                            originalFilename = payload.getOriginalFilename();
                                        }
                                        tempFile = AS2Tools.createTempFile(originalFilename, "");
                                        outStream = Files.newOutputStream(tempFile);
                                        inStream = response.getDataStream();
                                        inStream.transferTo(outStream);
                                        outStream.flush();
                                    } catch (Throwable e) {
                                        AS2Gui.logger.severe(e.getMessage());
                                        return;
                                    } finally {
                                        if (inStream != null) {
                                            try {
                                                inStream.close();
                                            } catch (Exception e) {
                                            }
                                        }
                                        if (outStream != null) {
                                            try {
                                                outStream.close();
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                } else {
                                    // weird - no payload found for the selected message?
                                    throw new Exception(
                                            AS2Gui.this.rb.getResourceString(
                                                    "resend.failed.nopayload",
                                                    message.getAS2Info().getMessageId()));
                                }
                                dialog.performResend(info.getMessageId(),
                                        sender, receiver, tempFile, originalFilename, info.getSubject());
                                info.setResendCounter(info.getResendCounter() + 1);
                            }
                        }
                        AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AS2Gui.this, "[" + e.getClass().getSimpleName() + "]:\n"
                            + AS2Tools.fold(e.getMessage(), "\n", 50));
                } finally {
                    AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                    if (tempFile != null) {
                        try {
                            Files.delete(tempFile);
                        } catch (Exception e) {
                            // nop
                        }
                    }
                }

            }
        };
        Executors.newSingleThreadExecutor().submit(runnable);
    }

    /**
     * The client received a message from the server
     */
    @Override
    public boolean processMessageFromServer(ClientServerMessage message) {
        if (message instanceof RefreshClientMessageOverviewList) {
            RefreshClientMessageOverviewList refreshRequest = (RefreshClientMessageOverviewList) message;
            if (refreshRequest.getOperation() == RefreshClientMessageOverviewList.OPERATION_PROCESSING_UPDATE) {
                this.refreshThread.serverRequestsOverviewRefresh();
            } else {
                // always perform update of a delete operation - even if the refresh has been
                // disabled
                this.refreshThread.userRequestsOverviewRefresh();
            }
            return (true);
        } else if (message instanceof RefreshTablePartnerData) {
            this.refreshThread.requestPartnerRefresh();
            return (true);
        } else if (message instanceof ServerInfo) {
            ServerInfo serverInfo = (ServerInfo) message;
            this.getLogger().log(Level.CONFIG, serverInfo.getProductname());
            return (true);
        } else if (message instanceof ConfigurationChangedOnServer) {
            this.preferencesChangedOnServer((ConfigurationChangedOnServer) message);
            return (true);
        }
        // not processed here
        return (false);
    }

    /**
     * Compute a change of a preference on the server side
     */
    private void preferencesChangedOnServer(ConfigurationChangedOnServer message) {
        if (message.getType() == ConfigurationChangedOnServer.TYPE_SERVER_PREFERENCES) {
            ConfigurationChangedOnServerPreferences messagePreferences = (ConfigurationChangedOnServerPreferences) message;
            StringBuilder text = new StringBuilder()
                    .append(this.rbPreferences.getResourceString(messagePreferences.getKey()))
                    .append(" ")
                    .append(this.rbPreferences.getResourceString("set.to"))
                    .append(" ");
            if ((messagePreferences.getOldValue().toUpperCase().equals("TRUE")
                    || messagePreferences.getOldValue().toUpperCase().equals("FALSE"))
                    && (messagePreferences.getNewValue().toUpperCase().equals("TRUE")
                            || messagePreferences.getNewValue().toUpperCase().equals("FALSE"))) {
                text.append("[")
                        .append(this.rbPreferences.getResourceString(messagePreferences.getNewValue().toUpperCase()))
                        .append("]");
            } else {
                text.append("[")
                        .append(messagePreferences.getNewValue())
                        .append("]");
            }
            UINotification.instance().addNotification(
                    IconManager.getPreferences(),
                    UINotification.TYPE_INFORMATION,
                    this.rbPreferences.getResourceString("setting.updated"),
                    text.toString());
        } else if (message.getType() == ConfigurationChangedOnServer.TYPE_NOTIFICATION_SETTINGS) {
            UINotification.instance().addNotification(
                    IconManager.getPreferences(),
                    UINotification.TYPE_INFORMATION,
                    this.rbPreferences.getResourceString("setting.updated"),
                    this.rbPreferences.getResourceString("notification.setting.updated"));
        }
    }

    @Override
    public void displayCertificateManagerTLS(final String selectedAlias) {
        final String uniqueId = this.getClass().getName() + ".displayKeystoreManagerSSL." + System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JDialogCertificates dialog = null;
                // display wait indicator
                AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                        AS2Gui.this.rb.getResourceString("menu.file.certificate"), uniqueId);
                try {
                    AS2Gui.this.jMenuItemCertificatesSSL.setEnabled(false);
                    AS2Gui.this.jButtonCertificatesSignEncrypt.setEnabled(false);
                    AS2Gui.this.jButtonCertificatesTLS.setEnabled(false);

                    // Get current user ID for user-specific keystore
                    int currentUserId = AS2Gui.this.userId;

                    // Check if user has admin privileges (USER_MANAGE permission)
                    boolean isAdmin = (AS2Gui.this.permissionManager != null &&
                                      AS2Gui.this.permissionManager.hasPermission(Permissions.USER_MANAGE));

                    dialog = new JDialogCertificates(AS2Gui.this, AS2Gui.this.getLogger(), AS2Gui.this,
                            AS2Gui.this.rbCertGui.getResourceString("title.ssl"),
                            AS2ServerVersion.getFullProductName(), false,
                            ModuleLock.MODULE_SSL_KEYSTORE, null);
                    dialog.setImageSizePopup(IconManager.IMAGE_SIZE_POPUP);
                    dialog.setSelectionByAlias(selectedAlias);

                    // For TLS certificates, admin users access system-wide keystore (user_id=-1)
                    // Regular users have their own TLS keystores
                    int tlsUserId = isAdmin ? de.mendelson.util.security.keydata.KeydataAccessDB.SYSTEM_WIDE_USER_ID : currentUserId;

                    KeystoreStorage storage = new KeystoreStorageImplClientServer(
                            AS2Gui.this.getBaseClient(),
                            KeystoreStorageImplClientServer.KEYSTORE_USAGE_SSL,
                            KeystoreStorageImplClientServer.KEYSTORE_STORAGE_TYPE_JKS,
                            tlsUserId);  // Use system-wide for admin, user-specific for others
                    dialog.initialize(storage);

                    // No "Show All Users" toggle for TLS - admins see system-wide keystore directly

                    KeyCopyHandler keycopyHandler = new DefaultKeyCopyHandler(
                            AS2Gui.this.getBaseClient(),
                            KeyCopyRequest.KEYSTORE_USAGE_TLS,
                            KeyCopyRequest.KEYSTORE_USAGE_ENC_SIGN,
                            ModuleLock.MODULE_ENCSIGN_KEYSTORE);
                    dialog.setKeyCopyHandler(keycopyHandler);
                } catch (Throwable e) {
                    e.printStackTrace();
                    UINotification.instance().addNotification(e);
                } finally {
                    AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                    if (dialog != null) {
                        dialog.setVisible(true);
                    }
                    AS2Gui.this.jMenuItemCertificatesSignCrypt.setEnabled(true);
                    AS2Gui.this.jButtonCertificatesSignEncrypt.setEnabled(true);
                    AS2Gui.this.jButtonCertificatesTLS.setEnabled(true);
                }
            }
        };
        GUIClient.submit(runnable);
    }

    @Override
    public void displayCertificateManagerEncSign(String selectedAlias) {
        final String uniqueId = this.getClass().getName() + ".displayKeystoreManagerSignEncrypt."
                + System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JDialogCertificates dialog = null;
                // display wait indicator
                AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                        AS2Gui.this.rb.getResourceString("menu.file.certificate"), uniqueId);
                AS2Gui.this.jMenuItemCertificatesSignCrypt.setEnabled(false);
                AS2Gui.this.jButtonCertificatesSignEncrypt.setEnabled(false);
                AS2Gui.this.jButtonCertificatesTLS.setEnabled(false);
                try {
                    // Get current user ID for user-specific keystore
                    int currentUserId = AS2Gui.this.userId;

                    // Check if user has admin privileges (USER_MANAGE permission)
                    boolean isAdmin = (AS2Gui.this.permissionManager != null &&
                                      AS2Gui.this.permissionManager.hasPermission(Permissions.USER_MANAGE));


                    dialog = new JDialogCertificates(AS2Gui.this, AS2Gui.this.getLogger(), AS2Gui.this,
                            AS2Gui.this.rbCertGui.getResourceString("title.signencrypt"),
                            AS2ServerVersion.getFullProductName(), false,
                            ModuleLock.MODULE_ENCSIGN_KEYSTORE, null);
                    dialog.setImageSizePopup(IconManager.IMAGE_SIZE_POPUP);

                    KeystoreStorage storage = new KeystoreStorageImplClientServer(
                            AS2Gui.this.getBaseClient(),
                            KeystoreStorageImplClientServer.KEYSTORE_USAGE_ENC_SIGN,
                            KeystoreStorageImplClientServer.KEYSTORE_STORAGE_TYPE_PKCS12,
                            currentUserId);  // User-specific keystore
                    dialog.initialize(storage);

                    // Enable admin mode to show toggle for viewing all users' certificates
                    // Must be called AFTER initialize() so the panel is fully set up
                    if (isAdmin) {
                        dialog.setAdminMode(AS2Gui.this.getBaseClient(), currentUserId,
                            de.mendelson.util.security.cert.clientserver.AllUsersCertificatesRequest.KEYSTORE_TYPE_ENC_SIGN);
                    }

                    CertificateUsedByPartnerChecker checker = new CertificateUsedByPartnerChecker(
                            AS2Gui.this.getBaseClient(), currentUserId);
                    dialog.addCertificateInUseChecker(checker);
                    dialog.addAllowModificationCallback(new AllowConfigurationModificationCallback((JFrame) AS2Gui.this,
                            AS2Gui.this.getBaseClient(),
                            ModuleLock.MODULE_ENCSIGN_KEYSTORE, true));
                    KeyCopyHandler keycopyHandler = new DefaultKeyCopyHandler(
                            AS2Gui.this.getBaseClient(),
                            KeyCopyRequest.KEYSTORE_USAGE_ENC_SIGN,
                            KeyCopyRequest.KEYSTORE_USAGE_TLS,
                            ModuleLock.MODULE_SSL_KEYSTORE);
                    dialog.setKeyCopyHandler(keycopyHandler);
                } catch (Throwable e) {
                    UINotification.instance().addNotification(e);
                    e.printStackTrace();
                } finally {
                    AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                    if (dialog != null) {
                        dialog.setVisible(true);
                    }
                    AS2Gui.this.jMenuItemCertificatesSignCrypt.setEnabled(true);
                    AS2Gui.this.jButtonCertificatesSignEncrypt.setEnabled(true);
                    AS2Gui.this.jButtonCertificatesTLS.setEnabled(true);
                }
            }
        };
        GUIClient.submit(runnable);
    }

    /**
     * Display user-specific TLS certificate management dialog
     * Allows users to import trusted certificates for inbound authentication
     */
    private void displayMyTLSCertificates() {
        final String uniqueId = this.getClass().getName() + ".displayMyTLSCertificates." + System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JDialogCertificates dialog = null;
                // display wait indicator
                AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                        AS2Gui.this.rb.getResourceString("menu.file.certificates.tls.my"), uniqueId);
                try {
                    // Get current user ID (0 = admin, >0 = other users)
                    int currentUserId = AS2Gui.this.userId;

                                        
                    // Check if user has admin privileges (USER_MANAGE permission)
                    boolean isAdmin = (AS2Gui.this.permissionManager != null &&
                                      AS2Gui.this.permissionManager.hasPermission(Permissions.USER_MANAGE));

                    // Create user-specific keystore storage
                    KeystoreStorage keystoreStorage = new KeystoreStorageImplClientServer(
                        AS2Gui.this.getBaseClient(),
                        KeystoreStorageImplClientServer.KEYSTORE_USAGE_SSL,
                        KeystoreStorageImplClientServer.KEYSTORE_STORAGE_TYPE_JKS,
                        currentUserId);  // User-specific (0=admin, >0=users)

                    
                    // Create and show dialog
                    dialog = new JDialogCertificates(
                        AS2Gui.this,
                        AS2Gui.this.getLogger(),
                        AS2Gui.this,
                        AS2Gui.this.rb.getResourceString("menu.file.certificates.tls.my"),
                        AS2ServerVersion.getFullProductName(),
                        false,
                        ModuleLock.MODULE_SSL_KEYSTORE,
                        null);
                    dialog.setImageSizePopup(IconManager.IMAGE_SIZE_POPUP);
                    dialog.initialize(keystoreStorage);

                    // Enable admin mode to show toggle for viewing all users' certificates
                    // Must be called AFTER initialize() so the panel is fully set up
                    if (isAdmin) {
                        dialog.setAdminMode(AS2Gui.this.getBaseClient(), currentUserId,
                            de.mendelson.util.security.cert.clientserver.AllUsersCertificatesRequest.KEYSTORE_TYPE_TLS);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    UINotification.instance().addNotification(e);
                } finally {
                    AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                    if (dialog != null) {
                        dialog.setVisible(true);
                    }
                }
            }
        };
        GUIClient.submit(runnable);
    }

    private void displayHelpSystem() {
        // Oracle JavaHelp removed - incompatible with JDK 17+
        // Show simple message dialog with GitHub link instead
        javax.swing.JOptionPane.showMessageDialog(
            this,
            "Online documentation available at:\nhttps://github.com/zc2tech/mend-as2",
            "Help",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void displayPreferences(final String selectedTab) {
        final String uniqueId = this.getClass().getName() + ".displayPreferences." + System.currentTimeMillis();
        Runnable prefRunner = new Runnable() {
            @Override
            public void run() {
                JDialogPreferences dialog = null;
                // display wait indicator
                AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                        AS2Gui.this.rb.getResourceString("menu.file.preferences"), uniqueId);
                try {
                    AS2Gui.this.jMenuItemFilePreferences.setEnabled(false);
                    List<PreferencesPanel> panelList = new ArrayList<PreferencesPanel>();
                    panelList.add(new PreferencesPanelMDN(AS2Gui.this.getBaseClient()));
                    panelList.add(new PreferencesPanelConnectivity(AS2Gui.this.getBaseClient()));
                    panelList.add(new PreferencesPanelProxy(AS2Gui.this.getBaseClient()));
                    // Create CertificateManager for inbound auth panel
                    KeystoreStorage storageEncSign = new KeystoreStorageImplClientServer(
                            AS2Gui.this.getBaseClient(),
                            KeystoreStorageImplClientServer.KEYSTORE_USAGE_ENC_SIGN,
                            KeystoreStorageImplClientServer.KEYSTORE_STORAGE_TYPE_PKCS12,
                            AS2Gui.this.userId);  // Load user-specific certificates
                    CertificateManager certificateManagerEncSign = new CertificateManager(AS2Gui.this.getLogger());
                    certificateManagerEncSign.loadKeystoreCertificates(storageEncSign);
                    // Inbound Auth panel removed - now per-partner configuration in Local Station settings
                    // modifying the underlaying keystore settings makes only sense if HA is enabled
                    AS2Gui.this.getBaseClient()
                            .sendSync(new ServerInfoRequest());
                    panelList.add(new PreferencesPanelDirectories(AS2Gui.this.getBaseClient()));
                    panelList.add(new PreferencesPanelSystemMaintenance(AS2Gui.this.getBaseClient()));
                    panelList.add(
                            new PreferencesPanelNotification(AS2Gui.this.getBaseClient(), AS2Gui.this.as2StatusBar));
                    panelList.add(new PreferencesPanelInterface(AS2Gui.this.getBaseClient()));
                    panelList.add(new PreferencesPanelLog(AS2Gui.this.getBaseClient()));
                    // HTTP Auth panel moved to separate My Preferences menu
                    dialog = new JDialogPreferences(AS2Gui.this, panelList, selectedTab, "");
                } catch (Throwable e) {
                    UINotification.instance().addNotification(e);
                    e.printStackTrace();
                } finally {
                    AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                    if (dialog != null) {
                        dialog.setVisible(true);
                    }
                    AS2Gui.this.jMenuItemFilePreferences.setEnabled(true);
                }
            }
        };
        GUIClient.submit(prefRunner);
    }

    @Override
    public void displayPartnerManager(final String partnername) {
        final String uniqueId = this.getClass().getName() + ".displayPartnerManager." + System.currentTimeMillis();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                JDialogPartnerConfig dialog = null;
                // display wait indicator
                AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                        AS2Gui.this.rb.getResourceString("menu.file.partner"), uniqueId);
                // try to set an exclusive lock on this module
                ModuleLockRequest request = new ModuleLockRequest(ModuleLock.MODULE_PARTNER,
                        ModuleLockRequest.TYPE_SET);
                ModuleLockResponse response = (ModuleLockResponse) AS2Gui.this.getBaseClient().sendSync(request);
                boolean hasLock = response.wasSuccessful();
                LockClientInformation lockKeeper = response.getLockKeeper();
                LockRefreshThread lockRefresher = null;
                try {
                    AS2Gui.this.jButtonPartner.setEnabled(false);
                    AS2Gui.this.jMenuItemPartner.setEnabled(false);
                    if (hasLock) {
                        lockRefresher = new LockRefreshThread(AS2Gui.this.getBaseClient(), ModuleLock.MODULE_PARTNER);
                        GUIClient.submit(lockRefresher);
                    }
                    PreferencesClient client = new PreferencesClient(AS2Gui.this.getBaseClient());

                    
                    CertificateManager certificateManagerEncSign = new CertificateManager(logger);
                    KeystoreStorage storage = new KeystoreStorageImplClientServer(
                            AS2Gui.this.getBaseClient(),
                            KeystoreStorageImplClientServer.KEYSTORE_USAGE_ENC_SIGN,
                            KeystoreStorageImplClientServer.KEYSTORE_STORAGE_TYPE_PKCS12,
                            AS2Gui.this.userId);  // Load user-specific certificates
                    certificateManagerEncSign.loadKeystoreCertificates(storage);

                    CertificateManager certificateManagerSLL = new CertificateManager(AS2Gui.logger);
                    storage = new KeystoreStorageImplClientServer(
                            AS2Gui.this.getBaseClient(),
                            KeystoreStorageImplClientServer.KEYSTORE_USAGE_SSL,
                            KeystoreStorageImplClientServer.KEYSTORE_STORAGE_TYPE_JKS,
                            AS2Gui.this.userId);  // Load user-specific TLS certificates
                    certificateManagerSLL.loadKeystoreCertificates(storage);
                    PartnerSystemResponse systemresponse = (PartnerSystemResponse) AS2Gui.this.getBaseClient().sendSync(
                            new PartnerSystemRequest(PartnerSystemRequest.TYPE_LIST_ALL));
                    dialog = new JDialogPartnerConfig(AS2Gui.this,
                            AS2Gui.this,
                            AS2Gui.this.as2StatusBar, hasLock, lockKeeper,
                            certificateManagerEncSign,
                            certificateManagerSLL, systemresponse.getPartnerSystems(),
                            "",
                            AS2Gui.this.userId);
                    if (partnername != null) {
                        dialog.setPreselectedPartner(partnername);
                    }
                    boolean showQuota = client.getBoolean(PreferencesAS2.SHOW_QUOTA_NOTIFICATION_IN_PARTNER_CONFIG);
                    boolean displayHeader = client.getBoolean(PreferencesAS2.SHOW_HTTPHEADER_IN_PARTNER_CONFIG);
                    boolean displayOverwriteLocalStationSecuritySettings = client
                            .getBoolean(PreferencesAS2.SHOW_OVERWRITE_LOCALSTATION_SECURITY_IN_PARTNER_CONFIG);
                    dialog.setDisplayOverwriteLocalstationSecurity(displayOverwriteLocalStationSecuritySettings);
                    dialog.setDisplayNotificationPanel(showQuota);
                    dialog.setDisplayHttpHeaderPanel(displayHeader);
                    dialog.addAllowModificationCallback(new AllowConfigurationModificationCallback((JFrame) AS2Gui.this,
                            AS2Gui.this.getBaseClient(),
                            ModuleLock.MODULE_PARTNER, hasLock));
                } catch (Throwable e) {
                    UINotification.instance().addNotification(e);
                    e.printStackTrace();
                } finally {
                    AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                    if (dialog != null) {
                        try {
                            dialog.setVisible(true);
                        } catch (Throwable e) {
                            UINotification.instance().addNotification(e);
                            e.printStackTrace();
                        }
                    }
                    // we had the lock: stop the refresher thread and release the lock. If this
                    // doesnt work somehow because the connection is lost
                    // there is a watchdog in the server that will kill locks that are not refreshed
                    // for some time
                    if (hasLock) {
                        if (lockRefresher != null) {
                            lockRefresher.pleaseStop();
                        }
                        request = new ModuleLockRequest(ModuleLock.MODULE_PARTNER, ModuleLockRequest.TYPE_RELEASE);
                        response = (ModuleLockResponse) AS2Gui.this.getBaseClient().sendSync(request);
                    }
                    AS2Gui.this.jButtonPartner.setEnabled(true);
                    AS2Gui.this.jMenuItemPartner.setEnabled(true);
                }
            }
        };
        GUIClient.submit(runnable);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPopupMenu = new javax.swing.JPopupMenu();
        jMenuItemPopupMessageDetails = new javax.swing.JMenuItem();
        jMenuItemPopupSendAgain = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPopupDeleteMessage = new javax.swing.JMenuItem();
        jToolBar = new javax.swing.JToolBar();
        // jButtonShop = new javax.swing.JButton();
        jButtonPartner = new javax.swing.JButton();
        jButtonUserManagement = new javax.swing.JButton();
        jButtonCertificatesSignEncrypt = new javax.swing.JButton();
        jButtonCertificatesSignEncrypt.setMnemonic(KeyEvent.VK_S);
        jButtonCertificatesTLS = new javax.swing.JButton();
        jButtonMessageDetails = new javax.swing.JButton();
        jButtonFilter = new javax.swing.JButton();
        jToggleButtonStopRefresh = new javax.swing.JToggleButton();
        jButtonConfigureColumns = new javax.swing.JButton();
        jButtonDeleteMessage = new javax.swing.JButton();
        jPanelMain = new javax.swing.JPanel();
        jSplitPane = new javax.swing.JSplitPane();
        jPanelMessageLog = new javax.swing.JPanel();
        jPanelFilterOverviewContainer = new javax.swing.JPanel();
        jPanelFilterOverview = new javax.swing.JPanel();
        jCheckBoxFilterShowOk = new javax.swing.JCheckBox();
        jCheckBoxFilterShowPending = new javax.swing.JCheckBox();
        jCheckBoxFilterShowStopped = new javax.swing.JCheckBox();
        jLabelFilterShowOk = new javax.swing.JLabel();
        jLabelFilterShowPending = new javax.swing.JLabel();
        jLabelFilterShowError = new javax.swing.JLabel();
        jButtonHideFilter = new javax.swing.JButton();
        jComboBoxFilterPartner = new javax.swing.JComboBox<>();
        jPaneSpace = new javax.swing.JPanel();
        jLabelPartnerFilter = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jComboBoxFilterLocalStation = new javax.swing.JComboBox<>();
        jLabelLocalStationFilter = new javax.swing.JLabel();
        jLabelDirectionFilter = new javax.swing.JLabel();
        jComboBoxFilterDirection = new javax.swing.JComboBox<>();
        jSeparator11 = new javax.swing.JSeparator();
        jCheckBoxUseTimeFilter = new javax.swing.JCheckBox();
        jDateChooserStartDate = new com.toedter.calendar.JDateChooser();
        jDateChooserEndDate = new com.toedter.calendar.JDateChooser();
        jLabelTimefilterFrom = new javax.swing.JLabel();
        jLabelTimefilterTo = new javax.swing.JLabel();
        jScrollPaneMessageOverview = new javax.swing.JScrollPane();
        jTableMessageOverview = new de.mendelson.util.tables.JTableSortable();
        jPanelServerLog = new javax.swing.JPanel();
        jPanelRefreshWarning = new javax.swing.JPanel();
        jLabelRefreshStopWarning = new javax.swing.JLabel();
        as2StatusBar = new de.mendelson.comm.as2.client.AS2StatusBar();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuItemFileSend = new javax.swing.JMenuItem();
        jMenuItemFilePreferences = new javax.swing.JMenuItem();
        jMenuItemPartner = new javax.swing.JMenuItem();
        jMenuItemCertificatesSignCrypt = new javax.swing.JMenuItem();
        jMenuItemCertificatesSSL = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItemHTTPServerInfo = new javax.swing.JMenuItem();
        jMenuItemSystemEvents = new javax.swing.JMenuItem();
        jMenuUserPreference = new javax.swing.JMenu();
        jMenuItemMyTLSCertificates = new javax.swing.JMenuItem();
        jMenuItemSearchInServerLog = new javax.swing.JMenuItem();
        jMenuItemSwitchUser = new javax.swing.JMenuItem();
        jMenuItemFileExit = new javax.swing.JMenuItem();
        jMenuItemHelpAbout = new javax.swing.JMenuItem();
        jMenuItemHelpSystem = new javax.swing.JMenuItem();

        jMenuItemPopupMessageDetails.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupMessageDetails.setText(this.rb.getResourceString("details"));
        jMenuItemPopupMessageDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupMessageDetailsActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupMessageDetails);

        jMenuItemPopupSendAgain.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupSendAgain.setText(this.rb.getResourceString("menu.file.resend"));
        jMenuItemPopupSendAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupSendAgainActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupSendAgain);
        jPopupMenu.add(jSeparator9);

        jMenuItemPopupDeleteMessage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupDeleteMessage.setText(this.rb.getResourceString("delete.msg"));
        jMenuItemPopupDeleteMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupDeleteMessageActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupDeleteMessage);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        // jButtonShop.setIcon(new
        // javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif")));
        // // NOI18N
        // jButtonShop.setText(this.rb.getResourceString( "buy.license"));
        // jButtonShop.setFocusable(false);
        // jButtonShop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        // jButtonShop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        // jButtonShop.addActionListener(new java.awt.event.ActionListener() {
        // public void actionPerformed(java.awt.event.ActionEvent evt) {
        // jButtonShopActionPerformed(evt);
        // }
        // });
        // jToolBar.add(jButtonShop);

        jButtonPartner.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif"))); // NOI18N
        jButtonPartner.setText("My Partner");
        jButtonPartner.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPartner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPartner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPartnerActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonPartner);

        jButtonCertificatesSignEncrypt.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif"))); // NOI18N
        jButtonCertificatesSignEncrypt.setText("My Sign/Crypt");
        jButtonCertificatesSignEncrypt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCertificatesSignEncrypt.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCertificatesSignEncrypt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCertificatesSignEncryptActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonCertificatesSignEncrypt);

        jButtonUserManagement.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif"))); // NOI18N
        jButtonUserManagement.setText("User Management");
        jButtonUserManagement.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonUserManagement.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonUserManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUserManagementActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonUserManagement);

        jButtonCertificatesTLS.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif"))); // NOI18N
        jButtonCertificatesTLS.setText(this.rb.getResourceString("menu.file.certificate.ssl"));
        jButtonCertificatesTLS.setFocusable(false);
        jButtonCertificatesTLS.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCertificatesTLS.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCertificatesTLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCertificatesTLSActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonCertificatesTLS);

        jButtonMessageDetails.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif"))); // NOI18N
        jButtonMessageDetails.setText(this.rb.getResourceString("details"));
        jButtonMessageDetails.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMessageDetails.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMessageDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMessageDetailsActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonMessageDetails);

        jButtonFilter.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif"))); // NOI18N
        jButtonFilter.setText(this.rb.getResourceString("filter"));
        jButtonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFilterActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonFilter);

        jToggleButtonStopRefresh.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif"))); // NOI18N
        jToggleButtonStopRefresh.setText(this.rb.getResourceString("stoprefresh.msg"));
        jToggleButtonStopRefresh.setFocusable(false);
        jToggleButtonStopRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonStopRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonStopRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStopRefreshActionPerformed(evt);
            }
        });
        jToolBar.add(jToggleButtonStopRefresh);

        jButtonConfigureColumns.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif"))); // NOI18N
        jButtonConfigureColumns.setText(this.rb.getResourceString("configurecolumns"));
        jButtonConfigureColumns.setFocusable(false);
        jButtonConfigureColumns.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonConfigureColumns.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonConfigureColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfigureColumnsActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonConfigureColumns);

        jButtonDeleteMessage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image24x24.gif"))); // NOI18N
        jButtonDeleteMessage.setText(this.rb.getResourceString("delete.msg"));
        jButtonDeleteMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDeleteMessage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDeleteMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteMessageActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonDeleteMessage);

        getContentPane().add(jToolBar, java.awt.BorderLayout.NORTH);

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jSplitPane.setBorder(null);
        jSplitPane.setDividerLocation(300);
        jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanelMessageLog.setLayout(new java.awt.GridBagLayout());

        jPanelFilterOverviewContainer
                .setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.activeCaptionBorder));
        jPanelFilterOverviewContainer.setLayout(new java.awt.GridBagLayout());

        jPanelFilterOverview.setLayout(new java.awt.GridBagLayout());

        jCheckBoxFilterShowOk.setSelected(true);
        jCheckBoxFilterShowOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFilterShowOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelFilterOverview.add(jCheckBoxFilterShowOk, gridBagConstraints);

        jCheckBoxFilterShowPending.setSelected(true);
        jCheckBoxFilterShowPending.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFilterShowPendingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelFilterOverview.add(jCheckBoxFilterShowPending, gridBagConstraints);

        jCheckBoxFilterShowStopped.setSelected(true);
        jCheckBoxFilterShowStopped.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFilterShowStoppedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelFilterOverview.add(jCheckBoxFilterShowStopped, gridBagConstraints);

        jLabelFilterShowOk.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelFilterShowOk.setText(this.rb.getResourceString("filter.showfinished"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jLabelFilterShowOk, gridBagConstraints);

        jLabelFilterShowPending.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelFilterShowPending.setText(this.rb.getResourceString("filter.showpending"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jLabelFilterShowPending, gridBagConstraints);

        jLabelFilterShowError.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelFilterShowError.setText(this.rb.getResourceString("filter.showstopped"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jLabelFilterShowError, gridBagConstraints);

        jButtonHideFilter.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jButtonHideFilter.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonHideFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHideFilterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jButtonHideFilter, gridBagConstraints);

        jComboBoxFilterPartner.setMinimumSize(new java.awt.Dimension(150, 20));
        jComboBoxFilterPartner.setPreferredSize(new java.awt.Dimension(150, 22));
        jComboBoxFilterPartner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFilterPartnerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jComboBoxFilterPartner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelFilterOverview.add(jPaneSpace, gridBagConstraints);

        jLabelPartnerFilter.setText(this.rb.getResourceString("filter.partner"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jLabelPartnerFilter, gridBagConstraints);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jSeparator4, gridBagConstraints);

        jComboBoxFilterLocalStation.setMinimumSize(new java.awt.Dimension(150, 20));
        jComboBoxFilterLocalStation.setPreferredSize(new java.awt.Dimension(150, 22));
        jComboBoxFilterLocalStation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFilterLocalStationActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jComboBoxFilterLocalStation, gridBagConstraints);

        jLabelLocalStationFilter.setText(this.rb.getResourceString("filter.localstation"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jLabelLocalStationFilter, gridBagConstraints);

        jLabelDirectionFilter.setText(this.rb.getResourceString("filter.direction"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jLabelDirectionFilter, gridBagConstraints);

        jComboBoxFilterDirection.setMinimumSize(new java.awt.Dimension(150, 20));
        jComboBoxFilterDirection.setPreferredSize(new java.awt.Dimension(150, 22));
        jComboBoxFilterDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFilterDirectionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jComboBoxFilterDirection, gridBagConstraints);

        jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jSeparator11, gridBagConstraints);

        jCheckBoxUseTimeFilter.setText(this.rb.getResourceString("filter.use"));
        jCheckBoxUseTimeFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxUseTimeFilterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jCheckBoxUseTimeFilter, gridBagConstraints);

        jDateChooserStartDate.setMinimumSize(new java.awt.Dimension(130, 20));
        jDateChooserStartDate.setPreferredSize(new java.awt.Dimension(130, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jDateChooserStartDate, gridBagConstraints);

        jDateChooserEndDate.setMinimumSize(new java.awt.Dimension(130, 20));
        jDateChooserEndDate.setPreferredSize(new java.awt.Dimension(130, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterOverview.add(jDateChooserEndDate, gridBagConstraints);

        jLabelTimefilterFrom.setText(this.rb.getResourceString("filter.from"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelFilterOverview.add(jLabelTimefilterFrom, gridBagConstraints);

        jLabelTimefilterTo.setText(this.rb.getResourceString("filter.to"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelFilterOverview.add(jLabelTimefilterTo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelFilterOverviewContainer.add(jPanelFilterOverview, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanelMessageLog.add(jPanelFilterOverviewContainer, gridBagConstraints);

        jTableMessageOverview.setModel(new TableModelMessageOverview());
        jTableMessageOverview.setShowHorizontalLines(false);
        jTableMessageOverview.setShowVerticalLines(false);
        jTableMessageOverview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMessageOverviewMouseClicked(evt);
            }
        });
        jScrollPaneMessageOverview.setViewportView(jTableMessageOverview);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelMessageLog.add(jScrollPaneMessageOverview, gridBagConstraints);

        jSplitPane.setLeftComponent(jPanelMessageLog);

        jPanelServerLog.setLayout(new java.awt.BorderLayout());
        jSplitPane.setRightComponent(jPanelServerLog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanelMain.add(jSplitPane, gridBagConstraints);

        jPanelRefreshWarning.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
        jPanelRefreshWarning.setLayout(new java.awt.GridBagLayout());

        jLabelRefreshStopWarning.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelRefreshStopWarning.setForeground(new java.awt.Color(204, 51, 0));
        jLabelRefreshStopWarning.setText(this.rb.getResourceString("warning.refreshstopped"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelRefreshWarning.add(jLabelRefreshStopWarning, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 3);
        jPanelMain.add(jPanelRefreshWarning, gridBagConstraints);

        as2StatusBar.setMinimumSize(new java.awt.Dimension(565, 26));
        as2StatusBar.setPreferredSize(new java.awt.Dimension(338, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelMain.add(as2StatusBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);

        getContentPane().add(jPanelMain, java.awt.BorderLayout.CENTER);

        // System menu
        jMenuSystem = new javax.swing.JMenu();
        jMenuSystem.setText(this.rb.getResourceString("menu.system"));

        // System Preferences
        jMenuItemFilePreferences.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemFilePreferences.setText(this.rb.getResourceString("menu.file.preferences"));
        jMenuItemFilePreferences
                .setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_COMMA));
        jMenuItemFilePreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFilePreferencesActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemFilePreferences);

        // User Management
        jMenuItemUserManagement = new javax.swing.JMenuItem();
        jMenuItemUserManagement.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif")));
        jMenuItemUserManagement.setText("User Management");
        jMenuItemUserManagement.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_U));
        jMenuItemUserManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUserManagementActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemUserManagement);

        // IP Whitelist Management
        jMenuItemIPWhitelist = new javax.swing.JMenuItem();
        jMenuItemIPWhitelist.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif")));
        jMenuItemIPWhitelist.setText("IP Whitelist Management");
        jMenuItemIPWhitelist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemIPWhitelistActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemIPWhitelist);

        // TLS Certificates
        jMenuItemCertificatesSSL.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemCertificatesSSL.setText(this.rb.getResourceString("menu.file.certificate.ssl"));
        jMenuItemCertificatesSSL.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_2,
                java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        jMenuItemCertificatesSSL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCertificatesSSLActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemCertificatesSSL);
        jMenuSystem.add(jSeparator3);

        jMenuItemHTTPServerInfo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemHTTPServerInfo.setText(this.rb.getResourceString("menu.system.serverinfo"));
        jMenuItemHTTPServerInfo.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_I));
        jMenuItemHTTPServerInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHTTPServerInfoActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemHTTPServerInfo);

        jMenuItemSystemEvents.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemSystemEvents.setText(this.rb.getResourceString("menu.system.events"));
        jMenuItemSystemEvents.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_Y));
        jMenuItemSystemEvents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSystemEventsActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemSystemEvents);

        jMenuItemSearchInServerLog.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemSearchInServerLog.setText(this.rb.getResourceString("menu.system.searchlog"));
        jMenuItemSearchInServerLog.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_F,
                java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        jMenuItemSearchInServerLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSearchInServerLogActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemSearchInServerLog);
        jMenuSystem.add(new javax.swing.JSeparator());

        // Switch User (only for admin role)
        jMenuItemSwitchUser = new javax.swing.JMenuItem();
        jMenuItemSwitchUser.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif")));
        jMenuItemSwitchUser.setText("Switch User");
        jMenuItemSwitchUser.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_W));
        jMenuItemSwitchUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSwitchUserActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemSwitchUser);
        jMenuSystem.add(new javax.swing.JSeparator());

        // Exit
        jMenuItemFileExit.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileExit.setText(this.rb.getResourceString("menu.file.exit"));
        jMenuItemFileExit.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_Q));
        jMenuItemFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileExitActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemFileExit);

        jMenuBar.add(jMenuSystem);

        // Tracker menu
        jMenuTracker = new javax.swing.JMenu();
        jMenuItemTrackerConfig = new javax.swing.JMenuItem();
        jMenuItemTrackerMessage = new javax.swing.JMenuItem();

        jMenuTracker.setText(this.rb.getResourceString("menu.tracker"));

        jMenuItemTrackerConfig.setText(this.rb.getResourceString("menu.tracker.config"));
        jMenuItemTrackerConfig.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_K));
        jMenuItemTrackerConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTrackerConfigActionPerformed(evt);
            }
        });
        jMenuTracker.add(jMenuItemTrackerConfig);

        jMenuItemTrackerMessage.setText(this.rb.getResourceString("menu.tracker.message"));
        jMenuItemTrackerMessage.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_K,
                java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        jMenuItemTrackerMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTrackerMessageActionPerformed(evt);
            }
        });
        jMenuTracker.add(jMenuItemTrackerMessage);

        jMenuBar.add(jMenuTracker);

        // My Preferences menu
        jMenuUserPreference.setText(this.rb.getResourceString("menu.userpreference"));

        // Send file to partner
        jMenuItemFileSend.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileSend.setText(this.rb.getResourceString("menu.file.send"));
        jMenuItemFileSend.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_M));
        jMenuItemFileSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileSendActionPerformed(evt);
            }
        });
        jMenuUserPreference.add(jMenuItemFileSend);

        // Partner configuration
        jMenuItemPartner.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemPartner.setText("My Partner");
        jMenuItemPartner.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(java.awt.event.KeyEvent.VK_P));
        jMenuItemPartner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPartnerActionPerformed(evt);
            }
        });
        jMenuUserPreference.add(jMenuItemPartner);

        // Sign/Crypt Certificates
        jMenuItemCertificatesSignCrypt.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jMenuItemCertificatesSignCrypt.setText("My Sign/Crypt");
        jMenuItemCertificatesSignCrypt.setAccelerator(KeyboardShortcutUtil
                .createMenuShortcut(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        jMenuItemCertificatesSignCrypt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCertificatesSignCryptActionPerformed(evt);
            }
        });
        jMenuUserPreference.add(jMenuItemCertificatesSignCrypt);

        // My TLS Certificates menu item
        jMenuItemMyTLSCertificates.setIcon(IconManager.getCertificateIconMenuItem());
        jMenuItemMyTLSCertificates.setText(this.rb.getResourceString("menu.file.certificates.tls.my"));
        jMenuItemMyTLSCertificates.setAccelerator(KeyboardShortcutUtil
                .createMenuShortcut(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        jMenuItemMyTLSCertificates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayMyTLSCertificates();
            }
        });
        jMenuUserPreference.add(jMenuItemMyTLSCertificates);

        jMenuBar.add(jMenuUserPreference);

        jMenuItemHelpSystem.setText(this.rb.getResourceString("menu.help.helpsystem"));
        jMenuItemHelpSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpSystemActionPerformed(evt);
            }
        });

        setJMenuBar(jMenuBar);

        setSize(new java.awt.Dimension(826, 655));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemFileSendActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemFileSendActionPerformed
        this.sendFileManual();
    }// GEN-LAST:event_jMenuItemFileSendActionPerformed

    private void jButtonDeleteMessageActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonDeleteMessageActionPerformed
        this.deleteSelectedMessages();
    }// GEN-LAST:event_jButtonDeleteMessageActionPerformed

    private void jButtonHideFilterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonHideFilterActionPerformed
        this.showFilterPanel = !this.showFilterPanel;
        this.jPanelFilterOverviewContainer.setVisible(this.showFilterPanel);
    }// GEN-LAST:event_jButtonHideFilterActionPerformed

    private void jCheckBoxFilterShowOkActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxFilterShowOkActionPerformed
        this.refreshThread.userRequestsOverviewRefresh();
    }// GEN-LAST:event_jCheckBoxFilterShowOkActionPerformed

    private void jCheckBoxFilterShowPendingActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxFilterShowPendingActionPerformed
        this.refreshThread.userRequestsOverviewRefresh();
    }// GEN-LAST:event_jCheckBoxFilterShowPendingActionPerformed

    private void jCheckBoxFilterShowStoppedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxFilterShowStoppedActionPerformed
        this.refreshThread.userRequestsOverviewRefresh();
    }// GEN-LAST:event_jCheckBoxFilterShowStoppedActionPerformed

    private void jButtonFilterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonFilterActionPerformed
        this.showFilterPanel = !this.showFilterPanel;
        this.jPanelFilterOverviewContainer.setVisible(this.showFilterPanel);
    }// GEN-LAST:event_jButtonFilterActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
        // Unsubscribe from EventBus
        if (this.eventListener != null) {
            EventBus.getInstance().unsubscribe(this.eventListener);
        }
        this.savePreferences();
    }// GEN-LAST:event_formWindowClosing

    private void jButtonCertificatesSignEncryptActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonCertificatesSignEncryptActionPerformed
        this.displayCertificateManagerEncSign(null);
    }// GEN-LAST:event_jButtonCertificatesSignEncryptActionPerformed

    private void jButtonPartnerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonPartnerActionPerformed
        this.displayPartnerManager(null);
    }// GEN-LAST:event_jButtonPartnerActionPerformed

    private void jButtonUserManagementActionPerformed(java.awt.event.ActionEvent evt) {
        de.mendelson.comm.as2.usermanagement.gui.JDialogUserManagement dialog = new de.mendelson.comm.as2.usermanagement.gui.JDialogUserManagement(
                this, this);
        dialog.setVisible(true);
    }

    private void jButtonMessageDetailsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonMessageDetailsActionPerformed
        this.showSelectedRowDetails();
    }// GEN-LAST:event_jButtonMessageDetailsActionPerformed

    private void jMenuItemCertificatesSignCryptActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemCertificatesSignCryptActionPerformed
        this.displayCertificateManagerEncSign(null);
    }// GEN-LAST:event_jMenuItemCertificatesSignCryptActionPerformed

    private void jTableMessageOverviewMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTableMessageOverviewMouseClicked
        // double click on a row
        if (evt.getClickCount() == 2) {
            this.showSelectedRowDetails();
        }
    }// GEN-LAST:event_jTableMessageOverviewMouseClicked

    private void jMenuItemFilePreferencesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemFilePreferencesActionPerformed
        this.displayPreferences(null);
    }// GEN-LAST:event_jMenuItemFilePreferencesActionPerformed

    private void jMenuItemPartnerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemPartnerActionPerformed
        this.displayPartnerManager(null);
    }// GEN-LAST:event_jMenuItemPartnerActionPerformed

    private void jMenuItemTrackerConfigActionPerformed(java.awt.event.ActionEvent evt) {
        de.mendelson.comm.as2.tracker.gui.JDialogTrackerConfig dialog =
                new de.mendelson.comm.as2.tracker.gui.JDialogTrackerConfig(this, this.getBaseClient());
        dialog.setVisible(true);
    }

    private void jMenuItemTrackerMessageActionPerformed(java.awt.event.ActionEvent evt) {
        de.mendelson.comm.as2.tracker.gui.JDialogTrackerMessage dialog =
                new de.mendelson.comm.as2.tracker.gui.JDialogTrackerMessage(this, this.getBaseClient(), this.as2StatusBar);
        dialog.setVisible(true);
    }

    private void jMenuItemUserManagementActionPerformed(java.awt.event.ActionEvent evt) {
        de.mendelson.comm.as2.usermanagement.gui.JDialogUserManagement dialog = new de.mendelson.comm.as2.usermanagement.gui.JDialogUserManagement(
                this, this);
        dialog.setVisible(true);
    }

    private void jMenuItemIPWhitelistActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO: Fix when IDBDriverManager is available
        // For now, JDialogIPWhitelistManagement needs to be updated to not require IDBDriverManager
        // or we need to provide it somehow
        JOptionPane.showMessageDialog(this,
            "IP Whitelist Management is not yet available in SwingUI.\nPlease use WebUI for now.",
            "Not Available",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void jMenuItemSwitchUserActionPerformed(java.awt.event.ActionEvent evt) {
        this.switchUser();
    }

    private void jMenuItemHelpSystemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemHelpSystemActionPerformed
        this.displayHelpSystem();
    }// GEN-LAST:event_jMenuItemHelpSystemActionPerformed

    private void jMenuItemFileExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemFileExitActionPerformed
        this.exitApplication();
    }// GEN-LAST:event_jMenuItemFileExitActionPerformed

    private void jToggleButtonStopRefreshActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButtonStopRefreshActionPerformed
        this.jPanelRefreshWarning.setVisible(this.jToggleButtonStopRefresh.isSelected());
        if (this.jToggleButtonStopRefresh.isSelected()) {
            this.consolePanel.setDisplayLog(false, this.rb.getResourceString("logputput.disabled"));
        } else {
            this.consolePanel.setDisplayLog(true, this.rb.getResourceString("logputput.enabled"));
        }
    }// GEN-LAST:event_jToggleButtonStopRefreshActionPerformed

    private void jComboBoxFilterPartnerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxFilterPartnerActionPerformed
        this.refreshThread.userRequestsOverviewRefresh();
    }// GEN-LAST:event_jComboBoxFilterPartnerActionPerformed

    private void jMenuItemCertificatesSSLActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemCertificatesTLSActionPerformed
        this.displayCertificateManagerTLS(null);
    }// GEN-LAST:event_jMenuItemCertificatesTLSActionPerformed

    private void jComboBoxFilterLocalStationActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxFilterLocalStationActionPerformed
        this.refreshThread.userRequestsOverviewRefresh();
    }// GEN-LAST:event_jComboBoxFilterLocalStationActionPerformed

    private void jMenuItemHTTPServerInfoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemHTTPServerInfoActionPerformed
        JDialogDisplayHTTPConfiguration dialog = new JDialogDisplayHTTPConfiguration(this, this.getBaseClient(),
                this.as2StatusBar);
        dialog.initialize();
        dialog.setVisible(true);
    }// GEN-LAST:event_jMenuItemHTTPServerInfoActionPerformed

    private void jComboBoxFilterDirectionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxFilterDirectionActionPerformed
        this.refreshThread.userRequestsOverviewRefresh();
    }// GEN-LAST:event_jComboBoxFilterDirectionActionPerformed



    private void jMenuItemPopupMessageDetailsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemPopupMessageDetailsActionPerformed
        this.showSelectedRowDetails();
    }// GEN-LAST:event_jMenuItemPopupMessageDetailsActionPerformed

    private void jMenuItemPopupDeleteMessageActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemPopupDeleteMessageActionPerformed
        this.deleteSelectedMessages();
    }// GEN-LAST:event_jMenuItemPopupDeleteMessageActionPerformed

    private void jMenuItemPopupSendAgainActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemPopupSendAgainActionPerformed
        this.resendTransactions();
    }// GEN-LAST:event_jMenuItemPopupSendAgainActionPerformed

    private void jButtonConfigureColumnsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonConfigureColumnsActionPerformed
        JDialogColumnConfig dialog = new JDialogColumnConfig(this,
                (TableColumnModelHideable) this.jTableMessageOverview.getColumnModel(), this);
        dialog.setVisible(true);
    }// GEN-LAST:event_jButtonConfigureColumnsActionPerformed

    private void jButtonCertificatesTLSActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonCertificatesTLSActionPerformed
        this.displayCertificateManagerTLS(null);
    }// GEN-LAST:event_jButtonCertificatesTLSActionPerformed

    private void jMenuItemSystemEventsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemSystemEventsActionPerformed
        if (this.dialogSystemEvents == null) {
            this.dialogSystemEvents = new JDialogSystemEvents(this, this.getBaseClient(), this.as2StatusBar);
        }
        this.dialogSystemEvents.setVisible(true);
    }// GEN-LAST:event_jMenuItemSystemEventsActionPerformed

    private void jMenuItemSearchInServerLogActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemSearchInServerLogActionPerformed
        JDialogSearchLogfile dialog = new JDialogSearchLogfile(this, this.getBaseClient(), this.as2StatusBar);
        dialog.setVisible(true);
    }// GEN-LAST:event_jMenuItemSearchInServerLogActionPerformed

    private void jCheckBoxUseTimeFilterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxUseTimeFilterActionPerformed
        this.refreshThread.userRequestsOverviewRefresh();
    }// GEN-LAST:event_jCheckBoxUseTimeFilterActionPerformed


    // private void jButtonShopActionPerformed(java.awt.event.ActionEvent evt)
    // {//GEN-FIRST:event_jButtonShopActionPerformed
    // try {
    // URI uri = new URI("https://shop.mendelson-e-c.com/buyas2professional");
    // if (Desktop.isDesktopSupported()) {
    // Desktop.getDesktop().browse(uri);
    // }
    // } catch (Exception e) {
    // this.getLogger().severe(e.getMessage());
    // }
    // }//GEN-LAST:event_jButtonShopActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.mendelson.comm.as2.client.AS2StatusBar as2StatusBar;
    private javax.swing.JButton jButtonCertificatesSignEncrypt;
    private javax.swing.JButton jButtonCertificatesTLS;
    private javax.swing.JButton jButtonConfigureColumns;
    private javax.swing.JButton jButtonDeleteMessage;
    private javax.swing.JButton jButtonFilter;
    private javax.swing.JButton jButtonHideFilter;
    private javax.swing.JButton jButtonMessageDetails;
    private javax.swing.JButton jButtonPartner;
    private javax.swing.JButton jButtonUserManagement;
    private javax.swing.JCheckBox jCheckBoxFilterShowOk;
    private javax.swing.JCheckBox jCheckBoxFilterShowPending;
    private javax.swing.JCheckBox jCheckBoxFilterShowStopped;
    private javax.swing.JCheckBox jCheckBoxUseTimeFilter;
    private javax.swing.JComboBox<String> jComboBoxFilterDirection;
    private javax.swing.JComboBox<Object> jComboBoxFilterLocalStation;
    private javax.swing.JComboBox<Object> jComboBoxFilterPartner;
    private com.toedter.calendar.JDateChooser jDateChooserEndDate;
    private com.toedter.calendar.JDateChooser jDateChooserStartDate;
    private javax.swing.JLabel jLabelDirectionFilter;
    private javax.swing.JLabel jLabelFilterShowError;
    private javax.swing.JLabel jLabelFilterShowOk;
    private javax.swing.JLabel jLabelFilterShowPending;
    private javax.swing.JLabel jLabelLocalStationFilter;
    private javax.swing.JLabel jLabelPartnerFilter;
    private javax.swing.JLabel jLabelRefreshStopWarning;
    private javax.swing.JLabel jLabelTimefilterFrom;
    private javax.swing.JLabel jLabelTimefilterTo;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuSystem;
    private javax.swing.JMenu jMenuUserPreference;
    private javax.swing.JMenuItem jMenuItemMyTLSCertificates;
    private javax.swing.JMenu jMenuTracker;
    private javax.swing.JMenuItem jMenuItemTrackerConfig;
    private javax.swing.JMenuItem jMenuItemTrackerMessage;
    private javax.swing.JMenuItem jMenuItemCertificatesSSL;
    private javax.swing.JMenuItem jMenuItemCertificatesSignCrypt;
    private javax.swing.JMenuItem jMenuItemFileExit;
    private javax.swing.JMenuItem jMenuItemFilePreferences;
    private javax.swing.JMenuItem jMenuItemFileSend;
    private javax.swing.JMenuItem jMenuItemHTTPServerInfo;
    private javax.swing.JMenuItem jMenuItemHelpAbout;
    private javax.swing.JMenuItem jMenuItemHelpSystem;
    private javax.swing.JMenuItem jMenuItemPartner;
    private javax.swing.JMenuItem jMenuItemPopupDeleteMessage;
    private javax.swing.JMenuItem jMenuItemPopupMessageDetails;
    private javax.swing.JMenuItem jMenuItemPopupSendAgain;
    private javax.swing.JMenuItem jMenuItemSearchInServerLog;
    private javax.swing.JMenuItem jMenuItemSystemEvents;
    private javax.swing.JMenuItem jMenuItemSwitchUser;
    private javax.swing.JMenuItem jMenuItemUserManagement;
    private javax.swing.JMenuItem jMenuItemIPWhitelist;
    private javax.swing.JPanel jPaneSpace;
    private javax.swing.JPanel jPanelFilterOverview;
    private javax.swing.JPanel jPanelFilterOverviewContainer;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelMessageLog;
    private javax.swing.JPanel jPanelRefreshWarning;
    private javax.swing.JPanel jPanelServerLog;
    private javax.swing.JPopupMenu jPopupMenu;
    private javax.swing.JScrollPane jScrollPaneMessageOverview;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane;
    private de.mendelson.util.tables.JTableSortable jTableMessageOverview;
    private javax.swing.JToggleButton jToggleButtonStopRefresh;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables

    /**
     * Performs a clean exit
     */
    private void exitApplication() {
        this.savePreferences();
        this.setVisible(false);
        try {
            this.getBaseClient().logout();
            this.getBaseClient().disconnect();
        } catch (Throwable e) {

        } finally {
            System.exit(0);
        }
    }

    /**
     * Makes this a RowSorterListener, workaround for the bug that the selected
     * row will change to a random one after the sort process
     */
    @Override
    public void sorterChanged(RowSorterEvent e) {
        if (e.getType().equals(RowSorterEvent.Type.SORTED)) {
            this.jTableMessageOverview.getSelectionModel().clearSelection();
            this.setButtonState();
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (evt.isPopupTrigger() || evt.isMetaDown()) {
            if (evt.getSource().equals(this.jTableMessageOverview)) {
                this.jPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * PopupMenuListener
     */
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        if (this.jTableMessageOverview.getSelectedRowCount() > 1) {
            this.jMenuItemPopupMessageDetails.setEnabled(false);
            this.jMenuItemPopupSendAgain.setText(this.rb.getResourceString("menu.file.resend.multiple"));
        } else {
            this.jMenuItemPopupMessageDetails.setEnabled(true);
            this.jMenuItemPopupSendAgain.setText(this.rb.getResourceString("menu.file.resend"));
        }
    }

    /**
     * PopupMenuListener
     */
    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    /**
     * PopupMenuListener
     */
    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }

    /**
     * Makes this a ClientSesionHandlerCallback. This is called if a sync
     * request failed
     */
    public void syncRequestFailed(ClientServerMessage request, ClientServerMessage response, Throwable throwable) {
        // the client has a timeout problem either sending data to the server or
        // receiving a sync answer
        // in the defined time . Ignore the payload requests. If there are huge changes
        // on the server these could be
        // really a huge amount of requests (up to 1000) - and so a timeout might
        // happen.
        if (throwable instanceof SyncRequestTimeoutException) {
            if (request != null && !(request instanceof MessagePayloadRequest)) {
                UINotification.instance().addNotification(IconManager.getHourglass(),
                        UINotification.TYPE_WARNING,
                        AS2Gui.this.rb.getResourceString("server.answer.timeout.title"),
                        AS2Gui.this.rb.getResourceString("server.answer.timeout.details"));
            }
        } else {
            AS2Gui.logger.severe(throwable.getMessage());
        }
    }

    @Override
    public void processSyncResponseFromServer(ClientServerResponse response) {
    }

    @Override
    public void tableColumnHiddenStateChanged(ColumnHiddenStateEvent e) {
        // refresh the new settings
        ((TableColumnModelHideable) this.jTableMessageOverview.getColumnModel()).updateState();
        this.storeColumSettings();
    }

    public void clientIsIncompatible(String errorMessage) {
        JOptionPane.showMessageDialog(this,
                AS2Tools.fold(errorMessage, "\n", 80),
                this.rb.getResourceString("fatal.error"), JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    /**
     * Checks at fixed interval if a refresh request is available. This prevents
     * a refresh flooding from the server on heavy load
     */
    private class RefreshThread implements Runnable {

        private boolean overviewRefreshRequested = true;
        private boolean partnerRefreshRequested = true;
        private LazyPayloadLoaderThread lazyLoader = null;
        private boolean firstStart = true;

        @Override
        public void run() {
            try {
                if (this.overviewRefreshRequested) {
                    this.overviewRefreshRequested = false;
                    this.refreshMessageOverviewList();
                }
                if (this.partnerRefreshRequested) {
                    this.partnerRefreshRequested = false;
                    this.refreshTablePartnerData();
                }
                // resize the columns on the first start
                if (this.firstStart) {
                    this.firstStart = false;
                    JTableColumnResizer.adjustColumnWidthByContent(AS2Gui.this.jTableMessageOverview);
                }
            } catch (Throwable e) {
                // Exception in refresh thread - log it but don't crash
            }
        }

        public void userRequestsOverviewRefresh() {
            this.overviewRefreshRequested = true;
        }

        public void serverRequestsOverviewRefresh() {
            boolean refreshEnabled = !AS2Gui.this.jToggleButtonStopRefresh.isSelected();
            if (refreshEnabled) {
                this.overviewRefreshRequested = true;
            }
        }

        public void requestPartnerRefresh() {
            this.partnerRefreshRequested = true;
        }

        /**
         * Reloads the partner ids with their names and passes these information
         * to the overview table. Also refreshes the partner filter.
         *
         */
        public void refreshTablePartnerData() {
            try {
                PartnerListRequest request = new PartnerListRequest(
                        PartnerListRequest.LIST_ALL,
                        PartnerListRequest.DATA_COMPLETENESS_NAME_AS2ID_TYPE);
                request.setUserId(AS2Gui.this.userId);  // Set user context
                List<Partner> partnerList = ((PartnerListResponse) AS2Gui.this.sendSync(request))
                        .getList();
                Map<String, Partner> partnerMap = new HashMap<String, Partner>();
                for (Partner partner : partnerList) {
                    partnerMap.put(partner.getAS2Identification(), partner);
                }
                ((TableModelMessageOverview) AS2Gui.this.jTableMessageOverview.getModel()).passPartner(partnerMap);
                AS2Gui.this.updatePartnerFilter(partnerList);
                AS2Gui.this.updateLocalStationFilter(partnerList);
            } catch (Exception e) {
                // Failed to refresh partner data - will retry on next cycle
            }
        }

        /**
         * Loads the payloads for the passed messages in the background
         */
        private void lazyloadPayloads() {
            this.lazyLoader = new LazyPayloadLoaderThread();
            GUIClient.submit(this.lazyLoader);
        }

        /**
         * Refreshes the message overview list from the database.
         */
        private void refreshMessageOverviewList() {
            // the lazy load process from the last refresh is no longer needed
            if (this.lazyLoader != null) {
                this.lazyLoader.stopLazyLoad();
            }
            final String uniqueId = this.getClass().getName() + ".refreshMessageOverviewList."
                    + System.currentTimeMillis();
            try {
                AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                        AS2Gui.this.rb.getResourceString("refresh.overview"), uniqueId);
                MessageOverviewFilter filter = new MessageOverviewFilter();
                filter.setShowFinished(AS2Gui.this.jCheckBoxFilterShowOk.isSelected());
                filter.setShowPending(AS2Gui.this.jCheckBoxFilterShowPending.isSelected());
                filter.setShowStopped(AS2Gui.this.jCheckBoxFilterShowStopped.isSelected());
                if (AS2Gui.this.jComboBoxFilterPartner.getSelectedIndex() <= 0) {
                    filter.setShowPartner(null);
                } else {
                    filter.setShowPartner((Partner) AS2Gui.this.jComboBoxFilterPartner.getSelectedItem());
                }
                if (AS2Gui.this.jComboBoxFilterLocalStation.getSelectedIndex() <= 0) {
                    filter.setShowLocalStation(null);
                } else {
                    filter.setShowLocalStation((Partner) AS2Gui.this.jComboBoxFilterLocalStation.getSelectedItem());
                }
                if (AS2Gui.this.jComboBoxFilterDirection.getSelectedIndex() == 0) {
                    filter.setShowDirection(MessageOverviewFilter.DIRECTION_ALL);
                } else if (AS2Gui.this.jComboBoxFilterDirection.getSelectedIndex() == 1) {
                    filter.setShowDirection(MessageOverviewFilter.DIRECTION_IN);
                } else if (AS2Gui.this.jComboBoxFilterDirection.getSelectedIndex() == 2) {
                    filter.setShowDirection(MessageOverviewFilter.DIRECTION_OUT);
                }
                if (jCheckBoxUseTimeFilter.isSelected()) {
                    filter.setStartTime(filterStartDate.getTime());
                    filter.setEndTime(filterEndDate.getTime());
                }
                int countServed = 0;
                int countOk = 0;
                int countPending = 0;
                int countFailure = 0;
                int countSelected = 0;
                // The response will be null if the server could not answer in the set timeout -
                // bad connection or system under heavy load?
                MessageOverviewRequest messageRequest = new MessageOverviewRequest(filter);
                messageRequest.setUserId(AS2Gui.this.userId);
                messageRequest.setHasUserManagePermission(
                    AS2Gui.this.permissionManager != null &&
                    AS2Gui.this.permissionManager.hasPermission(Permissions.USER_MANAGE));
                MessageOverviewResponse response = ((MessageOverviewResponse) AS2Gui.this.sendSync(messageRequest));
                if (response != null) {
                    List<AS2MessageInfo> overviewList = response.getList();
                    int countAll = response.getMessageSumOnServer();
                    countServed = overviewList.size();
                    List<AS2Message> messageList = new ArrayList<AS2Message>();
                    for (AS2MessageInfo messageInfo : overviewList) {
                        AS2Message message = new AS2Message(messageInfo);
                        switch (messageInfo.getState()) {
                            case AS2Message.STATE_FINISHED:
                                countOk++;
                                break;
                            case AS2Message.STATE_PENDING:
                                countPending++;
                                break;
                            case AS2Message.STATE_STOPPED:
                                countFailure++;
                                break;
                        }
                        // add the payloads related to this message
                        messageList.add(message);
                    }
                    TableModelMessageOverview tableModel = (TableModelMessageOverview) AS2Gui.this.jTableMessageOverview
                            .getModel();
                    tableModel.passNewData(messageList);
                    // try to jump to latest entry
                    try {
                        int rowCount = AS2Gui.this.jTableMessageOverview.getRowCount();
                        AS2Gui.this.jTableMessageOverview.getSelectionModel().setSelectionInterval(rowCount - 1,
                                rowCount - 1);
                        RefreshThread.this.makeRowVisible(AS2Gui.this.jTableMessageOverview, rowCount - 1);
                    } catch (Throwable ignore) {
                        // nop
                    }
                    countSelected = AS2Gui.this.jTableMessageOverview.getSelectedRowCount();
                    AS2Gui.this.as2StatusBar.setTransactionCount(
                            countAll, countServed, countOk, countPending,
                            countFailure, countSelected);
                    // lazy load the payloads of the underlaying table that have not been loaded so
                    // far
                    this.lazyloadPayloads();
                }
            } catch (Exception e) {
                UINotification.instance().addNotification(e);
                e.printStackTrace();
            } finally {
                AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
            }
        }

        /**
         * Scrolls to an entry of the passed table
         *
         * @param table Table to to scroll in
         * @param row   Row to ensure visibility
         */
        private void makeRowVisible(final JTable table, final int row) {

            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        if (!table.isVisible()) {
                            return;
                        }
                        if (table.getColumnCount() == 0) {
                            return;
                        }
                        if (row < 0 || row >= table.getRowCount()) {
                            return;
                        }
                        try {
                            Rectangle visible = table.getVisibleRect();
                            Rectangle cell = table.getCellRect(row, 0, true);
                            if (cell.y < visible.y) {
                                visible.y = cell.y;
                                table.scrollRectToVisible(visible);
                            } else if (cell.y + cell.height > visible.y + visible.height) {
                                visible.y = cell.y + cell.height - visible.height;
                                table.scrollRectToVisible(visible);
                            }
                        } catch (Throwable e) {
                            // nop
                        }
                    }
                });
            } catch (Exception e) {
                // nop
            }
        }
    }

    /**
     * Helper method to get user ID from username by looking up in database
     * @param username Username to lookup
     * @return User ID from webui_users table, or -1 if lookup fails
     */
    private int getUserIdFromUsername(String username) {
        try {
            UserListResponse response = (UserListResponse) this.sendSync(new UserListRequest(), 10000);
            if (response != null && response.getUsers() != null) {
                for (WebUIUser user : response.getUsers()) {
                    if (user.getUsername().equals(username)) {
                        return user.getId();
                    }
                }
            }
        } catch (Exception e) {
            this.getLogger().severe("Failed to lookup user ID for username: " + username);
        }
        this.getLogger().warning("Could not find user ID for username: " + username + ", returning -1");
        return -1;
    }

    /**
     * Switch to a different user (impersonation)
     * Only available for admin role users with USER_SWITCH permission
     */
    private void switchUser() {
        // Check if currently impersonating another user - allow switch back without permission check
        boolean isImpersonating = !this.username.equals(this.originalUsername);

        if (!isImpersonating) {
            // Not impersonating - check if current user has permission to switch
            if (!this.permissionManager.hasPermission(Permissions.USER_SWITCH)) {
                JOptionPane.showMessageDialog(this,
                        "You do not have permission to switch users",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // If impersonating, allow switch back to original user
        if (isImpersonating) {
            // Switch back to original user directly without confirmation
            this.username = this.originalUsername;
            this.userId = getUserIdFromUsername(this.originalUsername);

            // Update BaseClient username
            this.getBaseClient().setUsername(this.originalUsername);

            // CRITICAL: Refresh permissions for original user
            try {
                this.permissionManager.refreshPermissions();
            } catch (Exception e) {
                this.getLogger().severe("Failed to load permissions for " + this.originalUsername + ": " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Failed to load permissions: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update menus based on new permissions
            updateMenusForPermissions();

            // Update window title to remove user indication
            updateWindowTitle();

            // Update menu item text back to "Switch User"
            jMenuItemSwitchUser.setText("Switch User");

            // Refresh all data for admin user
            refreshAfterUserSwitch();

            JOptionPane.showMessageDialog(this,
                    "Switched back to admin user",
                    "Switch User",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Prevent cascade switching: only allow switching if current user is the original user
        if (!this.username.equals(this.originalUsername)) {
            JOptionPane.showMessageDialog(this,
                    "Cascade switching is not allowed. Please switch back first.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Only allow switching if original user is admin
        if (!this.originalUsername.equals("admin")) {
            JOptionPane.showMessageDialog(this,
                    "User switching is only available for administrators",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Determine if we should include admin users in the list
            // Only the special admin user (user_id=1, username="admin") can switch to other admin users
            // Other ADMIN role users can only switch to normal users
            int originalUserId = getUserIdFromUsername(this.originalUsername);
            boolean includeAdmins = (originalUserId == 1 && "admin".equals(this.originalUsername));

            System.out.println("Switch User: originalUsername=" + this.originalUsername +
                             ", originalUserId=" + originalUserId +
                             ", includeAdmins=" + includeAdmins);

            // Get list of users, optionally including admins, excluding disabled users
            UserListResponse response = (UserListResponse) this.sendSync(
                new UserListRequest(!includeAdmins, true), // excludeAdmins=!includeAdmins, enabledOnly=true
                10000);
            if (response == null || response.getUsers() == null || response.getUsers().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No users available for switching",
                        "Switch User",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Show switch user dialog
            JDialogSwitchUser dialog = new JDialogSwitchUser(this, this.username, response.getUsers());
            dialog.setVisible(true);

            if (dialog.isSwitchSuccessful()) {
                String newUsername = dialog.getSelectedUsername();
                if (newUsername != null && !newUsername.equals(this.username)) {
                    // Switch to new user
                    this.username = newUsername;
                    this.userId = getUserIdFromUsername(newUsername);

                    // Update BaseClient username
                    this.getBaseClient().setUsername(newUsername);

                    // CRITICAL: Refresh permissions for new user
                    try {
                        this.permissionManager.refreshPermissions();
                    } catch (Exception e) {
                        this.getLogger().severe("Failed to load permissions for user " + newUsername + ": " + e.getMessage());
                        JOptionPane.showMessageDialog(this,
                                "Failed to load permissions for user: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Update menus based on new permissions
                    updateMenusForPermissions();

                    // Update window title to show current user
                    updateWindowTitle();

                    // Update menu item text to "Switch Back"
                    jMenuItemSwitchUser.setText("Switch Back");

                    // Refresh all data for new user
                    refreshAfterUserSwitch();

                    // Show success message
                    JOptionPane.showMessageDialog(this,
                            "Switched to user: " + newUsername,
                            "Switch User",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            this.getLogger().severe("Failed to switch user: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to switch user: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Update window title to show current user
     */
    private void updateWindowTitle() {
        String title = WindowTitleUtil.buildTitle(AS2ServerVersion.getFullProductName());
        if (!"admin".equals(this.username)) {
            title += " [User: " + this.username + "]";
        }
        this.setTitle(title);
    }

    /**
     * Refresh all data after user switch
     */
    private void refreshAfterUserSwitch() {
        // Trigger a full refresh of the message overview and partner list
        // This will automatically use the new userId
        this.refreshThread.userRequestsOverviewRefresh();
        this.refreshThread.requestPartnerRefresh();
        // Force immediate refresh instead of waiting for the next scheduled run (3 seconds)
        this.refreshThread.run();
    }

    /**
     * Update menu visibility and enabled state based on current user permissions
     * Called after permission loading or user switching
     */
    private void updateMenusForPermissions() {
        if (this.permissionManager == null || !this.permissionManager.isLoaded()) {
            // No permissions loaded - disable everything except exit
            return;
        }

        // System Menu items
        // Hide System Preferences for users without system config permissions
        boolean hasSystemConfigPermission = this.permissionManager.hasAnyPermission(
            Permissions.SYSTEM_CONFIG_CONNECTIVITY, Permissions.SYSTEM_CONFIG_INBOUND_AUTH,
            Permissions.SYSTEM_CONFIG_DIRECTORIES, Permissions.SYSTEM_CONFIG_MAINTENANCE,
            Permissions.SYSTEM_CONFIG_NOTIFICATIONS, Permissions.SYSTEM_CONFIG_INTERFACE,
            Permissions.SYSTEM_CONFIG_LOGGING
        );
        // Disable (gray out) instead of hiding System > Preferences
        this.jMenuItemFilePreferences.setEnabled(hasSystemConfigPermission);

        this.jMenuItemUserManagement.setVisible(this.permissionManager.hasPermission(Permissions.USER_MANAGE));
        this.jButtonUserManagement.setVisible(this.permissionManager.hasPermission(Permissions.USER_MANAGE));

        this.jMenuItemIPWhitelist.setVisible(this.permissionManager.hasPermission(Permissions.SYSTEM_CONFIG_IP_WHITELIST));

        // Disable (gray out) instead of hiding Sys TLS certificates
        boolean hasTLSPermission = this.permissionManager.hasPermission(Permissions.CERT_TLS_READ);
        this.jMenuItemCertificatesSSL.setEnabled(hasTLSPermission);
        this.jButtonCertificatesTLS.setEnabled(hasTLSPermission);

        boolean hasUserManagePermission = this.permissionManager.hasPermission(Permissions.USER_MANAGE);

        this.jMenuItemHTTPServerInfo.setEnabled(this.permissionManager.hasPermission(Permissions.SYSTEM_INFO_READ));
        this.jMenuItemSystemEvents.setEnabled(this.permissionManager.hasPermission(Permissions.SYSTEM_EVENTS_READ));
        this.jMenuItemSearchInServerLog.setEnabled(this.permissionManager.hasPermission(Permissions.SYSTEM_LOGS_READ));

        // Keep "Switch User/Switch Back" menu visible if:
        // 1. User has USER_SWITCH permission (admin can switch to other users), OR
        // 2. User has already switched (allow switching back even if current user lacks permission)
        boolean hasSwitched = !this.username.equals(this.originalUsername);
        this.jMenuItemSwitchUser.setVisible(
            this.permissionManager.hasPermission(Permissions.USER_SWITCH) || hasSwitched);

        // My Preferences Menu items
        this.jMenuItemFileSend.setEnabled(this.permissionManager.hasPermission(Permissions.MESSAGE_WRITE));

        this.jMenuItemPartner.setEnabled(this.permissionManager.hasPermission(Permissions.PARTNER_READ));
        this.jButtonPartner.setEnabled(this.permissionManager.hasPermission(Permissions.PARTNER_READ));

        this.jMenuItemCertificatesSignCrypt.setEnabled(this.permissionManager.hasPermission(Permissions.CERT_READ));
        this.jButtonCertificatesSignEncrypt.setEnabled(this.permissionManager.hasPermission(Permissions.CERT_READ));

        // Tracker menu (if exists - may not be in all versions)
        // this.jMenuItemTrackerConfig.setEnabled(this.permissionManager.hasPermission(Permissions.TRACKER_CONFIG_READ));
        // this.jMenuItemTrackerMessage.setEnabled(this.permissionManager.hasPermission(Permissions.TRACKER_MESSAGE_READ));

        this.jButtonDeleteMessage.setEnabled(this.permissionManager.hasPermission(Permissions.MESSAGE_WRITE));
        this.jMenuItemPopupDeleteMessage.setEnabled(this.permissionManager.hasPermission(Permissions.MESSAGE_WRITE));
        this.jMenuItemPopupSendAgain.setEnabled(this.permissionManager.hasPermission(Permissions.MESSAGE_WRITE));
    }

    private class LazyPayloadLoaderThread implements Runnable {

        private final List<AS2Message> messageListToUpdate;
        private boolean stopLazyLoad = false;

        /**
         *
         * @param existingMessageList List of messages that have been already
         *                            updated
         * @param newMessageList
         */
        public LazyPayloadLoaderThread() {
            TableModelMessageOverview tableModel = (TableModelMessageOverview) AS2Gui.this.jTableMessageOverview
                    .getModel();
            // most time the list of transactions to upload should be empty - only if there
            // is activity this will change
            this.messageListToUpdate = tableModel.getMessagesWithoutPassedPayloads();
        }

        public void stopLazyLoad() {
            this.stopLazyLoad = true;
        }

        @Override
        public void run() {
            TableModelMessageOverview tableModel = (TableModelMessageOverview) AS2Gui.this.jTableMessageOverview
                    .getModel();
            for (AS2Message message : this.messageListToUpdate) {
                // bail out, lazy load is no longer required because a new overview refresh
                // occured
                if (this.stopLazyLoad) {
                    break;
                } else {
                    List<AS2Payload> payloads = ((MessagePayloadResponse) AS2Gui.this.sendSync(
                            new MessagePayloadRequest(message.getAS2Info().getMessageId()))).getList();
                    tableModel.passPayload(message, payloads);
                }
            }
        }
    }
}
