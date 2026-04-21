package de.mendelson.comm.as2.preferences;

import de.mendelson.util.preferences.PreferencesCache;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.server.ServerInstance;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Logger;
import java.util.logging.Level;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Class to manage the preferences of the AS2 server
 *
 * @author S.Heller
 * @version $Revision: 102 $
 */
public class PreferencesAS2 {

    private final static List<String> SUPPORTED_LANGUAGES = Arrays.asList(new String[]{
        "de", "fr", "es", "pt", "it", "en"
    });

    private final static MecResourceBundle rb;

    static {
        //load resource bundle
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferencesAS2.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    private final static PreferencesCache SERVERSIDE_PREFERENCES_CACHE = new PreferencesCache(TimeUnit.SECONDS.toMillis(5));

    /**
     * Position of the client frame X
     */
    public static final String FRAME_X = "frameguix";
    /**
     * Position of the client frame Y
     */
    public static final String FRAME_Y = "frameguiy";
    /**
     * Position of the client frame height
     */
    public static final String FRAME_HEIGHT = "frameguiheight";
    /**
     * Position of the IDE frame WIDTH
     */
    public static final String FRAME_WIDTH = "frameguiwidth";
    /**
     * Language to use for the software localization
     */
    public static final String LANGUAGE = "language";
    public static final String COUNTRY = "country";
    /**
     * Directory the message parts are stored in
     */
    public static final String DIR_MSG = "dirmsg";
    public static final String ASYNC_MDN_TIMEOUT = "asyncmdntimeout";
    public static final String AUTH_PROXY_USER = "proxyuser";
    public static final String AUTH_PROXY_PASS = "proxypass";
    public static final String AUTH_PROXY_USE = "proxyuseauth";
    /**
     * Inbound authentication mode for incoming AS2 messages (0=none, 1=basic, 2=certificate)
     */
    public static final String INBOUND_AUTH_MODE = "inbound.auth.mode";
    public static final String AUTO_MSG_DELETE = "automsgdelete";
    public static final String AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S = "automsgdeleteolderthanmults";
    public static final String AUTO_MSG_DELETE_OLDERTHAN = "automsgdeleteolderthan";
    public static final String AUTO_MSG_DELETE_LOG = "automsgdeletelog";
    public static final String AUTO_STATS_DELETE = "autostatsdelete";
    public static final String AUTO_STATS_DELETE_OLDERTHAN = "autostatsdeleteolderthan";
    public static final String AUTO_LOGDIR_DELETE = "autologdirdelete";
    public static final String AUTO_LOGDIR_DELETE_OLDERTHAN = "autologdirdeleteolderthan";
    public static final String LOG_POLL_PROCESS = "logpollprocess";
    public static final String PROXY_HOST = "proxyhost";
    public static final String PROXY_PORT = "proxyport";
    public static final String PROXY_USE = "proxyuse";
    public static final String RECEIPT_PARTNER_SUBDIR = "receiptpartnersubdir";
    public static final String HTTP_SEND_TIMEOUT = "httpsendtimeout";
    public static final String SHOW_QUOTA_NOTIFICATION_IN_PARTNER_CONFIG = "showquotaconf";
    public static final String SHOW_HTTPHEADER_IN_PARTNER_CONFIG = "showhttpheaderconf";
    public static final String CEM = "cem";
    public static final String COMMUNITY_EDITION = "commed";
    public static final String WRITE_OUTBOUND_STATUS_FILE = "outboundstatusfile";
    public static final String MAX_CONNECTION_RETRY_COUNT = "retrycount";
    public static final String MAX_OUTBOUND_CONNECTIONS = "maxoutboundconnections";
    public static final String CONNECTION_RETRY_WAIT_TIME_IN_S = "retrywaittime";
    public static final String DATASHEET_RECEIPT_URL = "datasheetreceipturl";
    public static final String HIDDENCOLSDEFAULT = "hiddencolsdefault";
    public static final String HIDDENCOLS = "hiddencols";
    public static final String HIDEABLECOLS = "hideablecols";
    public static final String COLOR_BLINDNESS = "colorblindness";
    public static final String LAST_UPDATE_CHECK = "lastupdatecheck";
    public static final String DISPLAY_MODE_CLIENT = "displaymodeclient";
    public static final String TLS_TRUST_ALL_REMOTE_SERVER_CERTIFICATES = "trustallservercerts";
    public static final String TLS_STRICT_HOST_CHECK = "stricthostcheck";
    public static final String EMBEDDED_HTTP_SERVER_STARTED = "embeddedhttpserverstarted";
    public static final String HTTP_LISTEN_PORT = "jetty.http.port";
    public static final String HTTPS_LISTEN_PORT = "jetty.ssl.port";
    public static final String SERVER_HOSTNAME = "server.hostname"; // Optional hostname for email URLs
    public static final String EMBEDDED_HTTP_SERVER_SETTINGS_ACCESSIBLE = "embeddedhttpserversettingsaccessible";
    public static final String EMBEDDED_HTTP_SERVER_REQUESTLOG = "embeddedhttpserverrequestlog";
    public static final String MAX_INBOUND_CONNECTIONS = "jetty.connectionlimit.maxConnections";
    public static final String NOTIFICATION_SMTP_TIMEOUT = "notificationsmtptimeout";
    public static final String NOTIFICATION_SMTP_CONNECTION_TIMEOUT = "notificationsmtpconnectiontimeout";
    public static final String SHOW_OVERWRITE_LOCALSTATION_SECURITY_IN_PARTNER_CONFIG = "showoverwritelocalstationsecurity";
    public static final String CHECK_REVOCATION_LISTS = "checkrevocationlist";
    public static final String AUTO_IMPORT_CHANGED_PARTNER_TLS_CERTIFICATES = "autoimportpartnertlscertificates";
    // Tracker configuration keys
    public static final String TRACKER_ENABLED = "tracker.enabled";
    public static final String TRACKER_AUTH_REQUIRED = "tracker.auth.required";
    public static final String TRACKER_MAX_SIZE_MB = "tracker.maxsize.mb";
    public static final String TRACKER_RATE_LIMIT_FAILURES = "tracker.ratelimit.failures";
    public static final String TRACKER_RATE_LIMIT_WINDOW_HOURS = "tracker.ratelimit.window.hours";
    public static final String TRACKER_RATE_LIMIT_BLOCK_MINUTES = "tracker.ratelimit.block.minutes";
    // IP Whitelist configuration keys
    public static final String IP_WHITELIST_ENABLED_AS2 = "ip.whitelist.enabled.as2";
    public static final String IP_WHITELIST_ENABLED_TRACKER = "ip.whitelist.enabled.tracker";
    public static final String IP_WHITELIST_ENABLED_WEBUI = "ip.whitelist.enabled.webui";
    public static final String IP_WHITELIST_ENABLED_API = "ip.whitelist.enabled.api";
    public static final String IP_WHITELIST_MODE = "ip.whitelist.mode"; // GLOBAL_ONLY, PARTNER_ONLY, USER_ONLY, GLOBAL_AND_SPECIFIC
    public static final String IP_WHITELIST_LOG_RETENTION_DAYS = "ip.whitelist.log.retention.days";
    // Login rate limiting configuration keys
    public static final String LOGIN_RATE_LIMIT_ENABLED = "login.ratelimit.enabled";
    public static final String LOGIN_RATE_LIMIT_FAILURES = "login.ratelimit.failures";
    public static final String LOGIN_RATE_LIMIT_WINDOW_HOURS = "login.ratelimit.window.hours";
    public static final String LOGIN_RATE_LIMIT_BLOCK_MINUTES = "login.ratelimit.block.minutes";

    private IDBDriverManager dbDriverManager = null;

    /**
     * Server side properties are stored in the database - client side
     * properties are stored in the java preferences
     */
    private static final List<String> SERVER_SIDE_KEYS
            = Collections.unmodifiableList(
                    Arrays.asList(
                            DIR_MSG,
                            ASYNC_MDN_TIMEOUT,
                            AUTH_PROXY_USER,
                            AUTH_PROXY_PASS,
                            AUTH_PROXY_USE,
                            INBOUND_AUTH_MODE,
                            AUTO_MSG_DELETE,
                            AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S,
                            AUTO_MSG_DELETE_OLDERTHAN,
                            AUTO_MSG_DELETE_LOG,
                            AUTO_STATS_DELETE,
                            AUTO_STATS_DELETE_OLDERTHAN,
                            AUTO_LOGDIR_DELETE,
                            AUTO_LOGDIR_DELETE_OLDERTHAN,
                            LOG_POLL_PROCESS,
                            PROXY_HOST,
                            PROXY_PORT,
                            PROXY_USE,
                            RECEIPT_PARTNER_SUBDIR,
                            HTTP_SEND_TIMEOUT,
                            CEM,
                            WRITE_OUTBOUND_STATUS_FILE,
                            MAX_CONNECTION_RETRY_COUNT,
                            MAX_OUTBOUND_CONNECTIONS,
                            CONNECTION_RETRY_WAIT_TIME_IN_S,
                            TLS_TRUST_ALL_REMOTE_SERVER_CERTIFICATES,
                            TLS_STRICT_HOST_CHECK,
                            EMBEDDED_HTTP_SERVER_REQUESTLOG,
                            CHECK_REVOCATION_LISTS,
                            AUTO_IMPORT_CHANGED_PARTNER_TLS_CERTIFICATES
                    ));
    private static final HashSet<String> SERVER_SIDE_PROPERTIES
            = new HashSet<String>(SERVER_SIDE_KEYS);

    /**
     * These properties are constant. You could try to change them but they will
     * always return the default value
     */
    private static final HashSet<String> CONSTANT_PROPERTIES
            = new HashSet<String>(
                    Set.of(
                            EMBEDDED_HTTP_SERVER_STARTED,
                            NOTIFICATION_SMTP_CONNECTION_TIMEOUT,
                            NOTIFICATION_SMTP_TIMEOUT
                    ));

    /**
     * These properties are stored in the embedded jetty properties file
     */
    private static final HashSet<String> JETTY_PROPERTIES
            = new HashSet<String>(
                    Set.of(
                            HTTP_LISTEN_PORT,
                            HTTPS_LISTEN_PORT,
                            MAX_INBOUND_CONNECTIONS
                    )
            );

    private static final Map<String, String> DEFAULT_VALUES
            = Map.ofEntries(
                    Map.entry(FRAME_WIDTH, "1024"),
                    Map.entry(FRAME_HEIGHT, "786"),
                    Map.entry(AUTO_MSG_DELETE, "TRUE"),
                    Map.entry(AUTO_MSG_DELETE_OLDERTHAN, "5"),
                    Map.entry(LAST_UPDATE_CHECK, "0"),
                    Map.entry(RECEIPT_PARTNER_SUBDIR, "FALSE"),
                    Map.entry(COLOR_BLINDNESS, "FALSE"),
                    Map.entry(AUTO_LOGDIR_DELETE, "FALSE"),
                    Map.entry(AUTO_LOGDIR_DELETE_OLDERTHAN, "180"),
                    Map.entry(AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S, String.valueOf(TimeUnit.DAYS.toSeconds(1))),
                    Map.entry(NOTIFICATION_SMTP_CONNECTION_TIMEOUT, String.valueOf(TimeUnit.SECONDS.toMillis(15))),
                    Map.entry(NOTIFICATION_SMTP_TIMEOUT, String.valueOf(TimeUnit.SECONDS.toMillis(15))),
                    Map.entry(AUTH_PROXY_PASS, "mypass"),
                    Map.entry(AUTH_PROXY_USER, "myuser"),
                    Map.entry(AUTH_PROXY_USE, "FALSE"),
                    Map.entry(INBOUND_AUTH_MODE, "0"),
                    Map.entry(ASYNC_MDN_TIMEOUT, "30"),
                    Map.entry(AUTO_MSG_DELETE_LOG, "TRUE"),
                    Map.entry(AUTO_STATS_DELETE, "TRUE"),
                    Map.entry(AUTO_STATS_DELETE_OLDERTHAN, "180"),
                    Map.entry(PROXY_HOST, "127.0.0.1"),
                    Map.entry(PROXY_PORT, "8131"),
                    Map.entry(PROXY_USE, "FALSE"),
                    Map.entry(HTTP_SEND_TIMEOUT, "5000"),
                    Map.entry(SHOW_HTTPHEADER_IN_PARTNER_CONFIG, "FALSE"),
                    Map.entry(SHOW_QUOTA_NOTIFICATION_IN_PARTNER_CONFIG, "FALSE"),
                    Map.entry(CEM, "FALSE"),
                    Map.entry(WRITE_OUTBOUND_STATUS_FILE, "FALSE"),
                    Map.entry(MAX_CONNECTION_RETRY_COUNT, "1"),
                    Map.entry(CONNECTION_RETRY_WAIT_TIME_IN_S, "30"),
                    Map.entry(DATASHEET_RECEIPT_URL, "http://localhost:8080/as2/HttpReceiver"),
                    Map.entry(HIDDENCOLSDEFAULT, "11111111111001"),
                    Map.entry(HIDDENCOLS, "11111111110001"),
                    Map.entry(HIDEABLECOLS, "00111111111111"),
                    Map.entry(LOG_POLL_PROCESS, "FALSE"),
                    Map.entry(MAX_OUTBOUND_CONNECTIONS, "9999"),
                    Map.entry(DISPLAY_MODE_CLIENT, "LIGHT"),
                    Map.entry(TLS_TRUST_ALL_REMOTE_SERVER_CERTIFICATES, "FALSE"),
                    Map.entry(TLS_STRICT_HOST_CHECK, "FALSE"),
                    Map.entry(HTTPS_LISTEN_PORT, "8443"),
                    Map.entry(HTTP_LISTEN_PORT, "8080"),
                    Map.entry(SERVER_HOSTNAME, ""),
                    Map.entry(EMBEDDED_HTTP_SERVER_REQUESTLOG, "FALSE"),
                    Map.entry(MAX_INBOUND_CONNECTIONS, "1000"),
                    Map.entry(SHOW_OVERWRITE_LOCALSTATION_SECURITY_IN_PARTNER_CONFIG, "FALSE"),
                    Map.entry(CHECK_REVOCATION_LISTS, "FALSE"),
                    Map.entry(AUTO_IMPORT_CHANGED_PARTNER_TLS_CERTIFICATES, "FALSE"),
                    Map.entry(TRACKER_ENABLED, "true"),
                    Map.entry(TRACKER_AUTH_REQUIRED, "true"),
                    Map.entry(TRACKER_MAX_SIZE_MB, "2"),
                    Map.entry(TRACKER_RATE_LIMIT_FAILURES, "3"),
                    Map.entry(TRACKER_RATE_LIMIT_WINDOW_HOURS, "1"),
                    Map.entry(TRACKER_RATE_LIMIT_BLOCK_MINUTES, "60"),
                    Map.entry(IP_WHITELIST_ENABLED_AS2, "false"),
                    Map.entry(IP_WHITELIST_ENABLED_TRACKER, "false"),
                    Map.entry(IP_WHITELIST_ENABLED_WEBUI, "true"),
                    Map.entry(IP_WHITELIST_ENABLED_API, "true"),
                    Map.entry(IP_WHITELIST_MODE, "GLOBAL_AND_SPECIFIC"),
                    Map.entry(IP_WHITELIST_LOG_RETENTION_DAYS, "30"),
                    Map.entry(LOGIN_RATE_LIMIT_ENABLED, "true"),
                    Map.entry(LOGIN_RATE_LIMIT_FAILURES, "5"),
                    Map.entry(LOGIN_RATE_LIMIT_WINDOW_HOURS, "1"),
                    Map.entry(LOGIN_RATE_LIMIT_BLOCK_MINUTES, "30")
            );

    /**
     * Initialize the preferences
     */
    public PreferencesAS2() {
    }

    /**
     * Initialize the preferences
     */
    public PreferencesAS2(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Returns the localized preference
     */
    public static String getLocalizedName(final String KEY) {
        return (rb.getResourceString(KEY));
    }

    /**
     * Returns the default value for the key
     *
     * @param KEY key to store properties with in the preferences
     */
    public static String getDefaultValue(final String KEY) {
        if (DEFAULT_VALUES.containsKey(KEY)) {
            return (DEFAULT_VALUES.get(KEY));
        }
        if (KEY.equals(EMBEDDED_HTTP_SERVER_STARTED)) {
            return (System.getProperty("mendelson.as2.embeddedhttpserver", "TRUE"));
        }
        if (KEY.equals(FRAME_X)) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension dialogSize = new Dimension(
                    Integer.parseInt(getDefaultValue(FRAME_WIDTH)),
                    Integer.parseInt(getDefaultValue(FRAME_HEIGHT)));
            return (String.valueOf((screenSize.width - dialogSize.width) / 2));
        }
        if (KEY.equals(FRAME_Y)) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension dialogSize = new Dimension(
                    Integer.parseInt(getDefaultValue(FRAME_WIDTH)),
                    Integer.parseInt(getDefaultValue(FRAME_HEIGHT)));
            return (String.valueOf((screenSize.height - dialogSize.height) / 2));
        }
        //language used for the localization
        if (KEY.equals(LANGUAGE)) {
            String defaultLanguage = Locale.getDefault().getLanguage().toLowerCase();
            if (SUPPORTED_LANGUAGES.contains(defaultLanguage)) {
                return (defaultLanguage);
            }
            //if this is not a supported by this mendelson product as client/server language just return english
            return ("en");
        }
        //country used for the localization
        if (KEY.equals(COUNTRY)) {
            return (Locale.getDefault().getCountry());
        }
        //message part directory
        if (KEY.equals(DIR_MSG)) {
            return (Paths.get(
                    Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString(),
                    "messages").toAbsolutePath().toString());
        }
        if (KEY.equals(COMMUNITY_EDITION)) {
            return (ServerInstance.ID.equals(ServerInstance.ID_COMMUNITY_EDITION) ? "TRUE" : "FALSE");
        }
        if (KEY.equals(EMBEDDED_HTTP_SERVER_SETTINGS_ACCESSIBLE)) {
            JettyConfigfileHandler handler = JettyConfigfileHandler.instance();
            return (handler.configFileAccessible() ? "TRUE" : "FALSE");
        }
        throw new IllegalArgumentException("No defaults defined for prefs key " + KEY + " in " + PreferencesAS2.class.getName());
    }

    /**
     * Resets all preferences to the default value if the key is a server side
     * stored key
     *
     * @param key
     */
    public void resetAllServerValuesToDefaultValue(Logger logger) {
        for (String key : SERVER_SIDE_PROPERTIES) {
            this.resetToDefaultValue(logger, key);
        }
    }

    /**
     * Deletes the passed key from the user defined settings - this will result
     * in reading the default value the next time it is requested
     *
     */
    public void resetToDefaultValue(Logger logger, final String KEY) {
        boolean resetPerformed = false;
        if (isServerSideProperty(KEY)) {
            if (this.dbDriverManager == null) {
                this.setDBDriverManagerByPluginCheck();
            }
            try (Connection configConnection = this.dbDriverManager
                    .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
                try (PreparedStatement statement = configConnection.prepareStatement(
                        "DELETE FROM serversettings WHERE vkey=?")) {
                    statement.setString(1, KEY);
                    int rows = statement.executeUpdate();
                    resetPerformed = rows == 1;
                    if (resetPerformed) {
                        SERVERSIDE_PREFERENCES_CACHE.remove(KEY);
                    }
                }
            } catch (Throwable e) {
                SystemEventManagerImplAS2.instance().systemFailure(e);
            }
        } else {
            Preferences preferences = Preferences.userNodeForPackage(AS2ServerVersion.class);
            preferences.remove(KEY);
        }
        if (resetPerformed) {
            String moduleName = rb.getResourceString("module.name");
            String localizedKey = rb.getResourceString(KEY);
            logger.log(Level.WARNING, moduleName + " " + rb.getResourceString("setting.reset", localizedKey));
        }
    }

    /**
     * Returns a single string value from the preferences or the default if it
     * is not found
     *
     */
    public String get(final String KEY) {
        String value = this.readSetting(KEY);
        if (value == null) {
            value = getDefaultValue(KEY);
            if (this.isServerSideProperty(KEY)) {
                SERVERSIDE_PREFERENCES_CACHE.put(KEY, value);
            }
            return (value);
        } else {
            return (value);
        }
    }

    /**
     * Stores a value in the preferences. If the passed value is null or an
     * empty string the key-value pair will be deleted from the storage.
     *
     * @param KEY Key as defined in this class
     * @param value value to set
     */
    public void put(final String KEY, String value) {
        if (value == null || value.isEmpty()) {
            this.deleteSetting(KEY);
        } else {
            this.writeSetting(KEY, value);
        }
    }

    /**
     * Puts a value to the preferences and stores the prefs
     *
     * @param KEY Key as defined in this class
     * @param value value to set
     */
    public void putInt(final String KEY, int value) {
        this.writeSetting(KEY, String.valueOf(value));
    }

    /**
     * Returns the value for the asked key, if none is defined it returns the
     * default value
     */
    public int getInt(final String KEY) {
        String value = this.readSetting(KEY);
        if (value == null) {
            value = getDefaultValue(KEY);
            if (this.isServerSideProperty(KEY)) {
                SERVERSIDE_PREFERENCES_CACHE.put(KEY, value);
            }
        }
        return (Integer.parseInt(value));
    }

    /**
     * Puts a value to the preferences and stores the setting
     *
     * @param KEY Key as defined in this class
     * @param value value to set
     */
    public void putBoolean(final String KEY, boolean value) {
        this.writeSetting(KEY, value ? "TRUE" : "FALSE");
    }

    /**
     * Returns the value for the asked key, if non is defined it returns the
     * default value
     */
    public boolean getBoolean(final String KEY) {
        String value = this.readSetting(KEY);
        if (value == null) {
            value = getDefaultValue(KEY);
            if (this.isServerSideProperty(KEY)) {
                SERVERSIDE_PREFERENCES_CACHE.put(KEY, value);
            }
        }
        return (Boolean.parseBoolean(value));
    }

    /**
     * Returns the value for the asked key, if noen is defined it returns the
     * second parameters value
     */
    public boolean getBoolean(final String KEY, boolean defaultValue) {
        String value = this.readSetting(KEY);
        if (value == null) {
            value = String.valueOf(defaultValue);
            if (this.isServerSideProperty(KEY)) {
                SERVERSIDE_PREFERENCES_CACHE.put(KEY, value);
            }
        }
        return (Boolean.parseBoolean(value));
    }

    /**
     * Indicates if this is a client- or a server setting and defines hereby the
     * storage place (db or preferences)
     *
     * @param KEY
     * @return
     */
    private boolean isServerSideProperty(String KEY) {
        return (SERVER_SIDE_PROPERTIES.contains(KEY));
    }

    /**
     * Indicates if this is a constant property. In this case it is not possible
     * to change the default value
     *
     * @param KEY
     * @return
     */
    private boolean isConstantProperty(String KEY) {
        return (CONSTANT_PROPERTIES.contains(KEY));
    }

    /**
     * Indicates if this is a property that is stored in the jetty config file
     *
     * @param KEY
     * @return
     */
    private boolean isJettyProperty(String KEY) {
        return (JETTY_PROPERTIES.contains(KEY));
    }

    /**
     * Will read a setting from the storage and return null if there is no
     * storage entry - then the default value should be returned
     *
     * @param KEY
     * @return
     */
    private String readSetting(String KEY) {
        if (isConstantProperty(KEY)) {
            return (getDefaultValue(KEY));
        }
        if (isJettyProperty(KEY)) {
            JettyConfigfileHandler handler = JettyConfigfileHandler.instance();
            return (handler.getValue(KEY, getDefaultValue(KEY)));
        }
        if (isServerSideProperty(KEY)) {
            String cachedValue = SERVERSIDE_PREFERENCES_CACHE.get(KEY);
            if (cachedValue != null) {
                return (cachedValue);
            } else {
                if (this.dbDriverManager == null) {
                    this.setDBDriverManagerByPluginCheck();
                }
                try (Connection configConnection = this.dbDriverManager
                        .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
                    try (PreparedStatement statement = configConnection.prepareStatement(
                            "SELECT vvalue FROM serversettings WHERE vkey=?")) {
                        statement.setString(1, KEY);
                        try (ResultSet result = statement.executeQuery()) {
                            if (result.next()) {
                                String value = result.getString("vvalue");
                                SERVERSIDE_PREFERENCES_CACHE.put(KEY, value);
                                return (value);
                            }
                        }
                    }
                } catch (Throwable e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e);
                }
            }
            return (null);
        } else {
            Preferences preferences = Preferences.userNodeForPackage(AS2ServerVersion.class);
            return (preferences.get(KEY, null));
        }
    }

    /**
     * Will write a setting to the storage
     *
     * @param KEY
     * @return
     */
    private void writeSetting(String KEY, String value) {
        if (isJettyProperty(KEY)) {
            //write to jetty config file if this is a jetty config setting
            JettyConfigfileHandler handler = JettyConfigfileHandler.instance();
            handler.setValue(KEY, value);
        } else if (!isConstantProperty(KEY)) {
            if (isServerSideProperty(KEY)) {
                if (this.dbDriverManager == null) {
                    this.setDBDriverManagerByPluginCheck();
                }
                try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                        .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
                    configConnectionNoAutoCommit.setAutoCommit(false);
                    String transactionName = "PreferencesAS2_writeSetting";
                    try (Statement statementTransaction = configConnectionNoAutoCommit.createStatement()) {
                        dbDriverManager.startTransaction(statementTransaction, transactionName);
                        dbDriverManager.setTableLockINSERTAndUPDATE(statementTransaction,
                                new String[]{"serversettings"});
                        //try to update existing row
                        try (PreparedStatement statementUpdate = configConnectionNoAutoCommit.prepareStatement(
                                "UPDATE serversettings SET vvalue=? WHERE vkey=?")) {
                            statementUpdate.setString(1, value);
                            statementUpdate.setString(2, KEY);
                            int updatedRows = statementUpdate.executeUpdate();
                            if (updatedRows == 0) {
                                //nothing updated - this was a new entry
                                try (PreparedStatement statementInsert
                                        = configConnectionNoAutoCommit.prepareStatement(
                                                "INSERT INTO serversettings(vkey,vvalue)VALUES(?,?)")) {
                                    statementInsert.setString(1, KEY);
                                    statementInsert.setString(2, value);
                                    statementInsert.executeUpdate();
                                }
                            }
                            this.dbDriverManager.commitTransaction(statementTransaction, transactionName);
                            SERVERSIDE_PREFERENCES_CACHE.put(KEY, value);
                        } catch (Throwable e) {
                            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                            this.dbDriverManager.rollbackTransaction(statementTransaction);
                        }
                    }
                } catch (Throwable e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            } else {
                //its a client value - just write it to the preferences
                Preferences preferences = Preferences.userNodeForPackage(AS2ServerVersion.class);
                preferences.put(KEY, value);
                try {
                    preferences.flush();
                } catch (BackingStoreException ignore) {
                }
            }
        }
    }

    private synchronized void setDBDriverManagerByPluginCheck() {
        if (this.dbDriverManager == null) {
            this.dbDriverManager = AS2Server.getActivatedDBDriverManager();
        }
    }

    /**
     * Will delete a setting in the storage
     *
     * @param KEY
     * @return
     */
    private void deleteSetting(String KEY) {
        if (isServerSideProperty(KEY)) {
            if (this.dbDriverManager == null) {
                this.setDBDriverManagerByPluginCheck();
            }
            try (Connection configConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
                configConnection.setAutoCommit(false);
                String transactionName = "PreferencesAS2_deleteSetting";
                try (Statement statementTransactionControl = configConnection.createStatement()) {
                    this.dbDriverManager.startTransaction(statementTransactionControl, transactionName);
                    this.dbDriverManager.setTableLockDELETE(statementTransactionControl,
                            new String[]{"serversettings"});
                    try (PreparedStatement statementDelete = configConnection.prepareStatement(
                            "DELETE FROM serversettings WHERE vkey=?")) {
                        statementDelete.setString(1, KEY);
                        statementDelete.executeUpdate();
                        this.dbDriverManager.commitTransaction(statementTransactionControl, transactionName);
                        SERVERSIDE_PREFERENCES_CACHE.remove(KEY);
                    } catch (Throwable e) {
                        SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                        this.dbDriverManager.rollbackTransaction(statementTransactionControl);
                    }
                }
            } catch (Throwable e) {
                SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } else {
            Preferences preferences = Preferences.userNodeForPackage(AS2ServerVersion.class);
            preferences.remove(KEY);
            try {
                preferences.flush();
            } catch (BackingStoreException ignore) {
            }
        }
    }

    /**
     * Clears the server side preferences cache. This might be required in HA
     * mode if there are multiple nodes working on the same preferences and a
     * request needs to get the current stored value in the database
     */
    public void clearCache() {
        SERVERSIDE_PREFERENCES_CACHE.clear();
    }

    public static String getSupportedLanguagesAsUsageList() {
        StringJoiner joiner = new StringJoiner(", ");
        for (String language : SUPPORTED_LANGUAGES) {
            joiner.add(language);
        }
        return (joiner.toString());

    }

}
