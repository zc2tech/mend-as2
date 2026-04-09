
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.server;

import de.mendelson.comm.as2.AS2Config;
import de.mendelson.util.httpconfig.server.HTTPServerConfigInfo;
import de.mendelson.Copyright;
import de.mendelson.activation.AWSRESTAccess;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.AS2ShutdownThread;
import de.mendelson.comm.as2.cem.CertificateCEMController;
import de.mendelson.comm.as2.configurationcheck.ConfigurationCheckController;
import de.mendelson.comm.as2.configurationcheck.ConfigurationIssue;
import de.mendelson.util.database.DBClientInformation;
import de.mendelson.comm.as2.database.DBDriverManagerPostgreSQL;
import de.mendelson.util.database.DBServerInformation;
import de.mendelson.comm.as2.database.DBServerPostgreSQL;
import de.mendelson.comm.as2.log.DBLoggingHandler;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.send.DirPollManager;
import de.mendelson.comm.as2.sendorder.SendOrderAccessDB;
import de.mendelson.comm.as2.sendorder.SendOrderReceiver;
import de.mendelson.comm.as2.timing.CertificateExpireController;
import de.mendelson.comm.as2.timing.FileDeleteController;
import de.mendelson.comm.as2.timing.MDNReceiptController;
import de.mendelson.comm.as2.timing.MessageDeleteController;
import de.mendelson.comm.as2.timing.PostProcessingEventController;
import de.mendelson.comm.as2.timing.StatisticDeleteController;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.clientserver.log.ClientServerLoggingHandler;
import de.mendelson.util.log.DailySubdirFileLoggingHandler;
import de.mendelson.util.log.LogFormatter;
import de.mendelson.util.log.LogFormatterAS2;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import de.mendelson.util.systemevents.notification.SystemEventNotificationControllerImplAS2;
import java.io.IOException;
import java.io.Writer;
import java.net.BindException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import org.eclipse.jetty.server.Server;
import de.mendelson.util.LibVersion;
import de.mendelson.util.ha.ServerInstanceHA;
import de.mendelson.util.clientserver.ClientServerTLSImplDefault;
import de.mendelson.util.clientserver.ServerHelloMessage;
import de.mendelson.util.clientserver.ServerHelloMessageGenerator;
import de.mendelson.util.clientserver.about.ServerInfoRequest;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.database.IDBServer;
import de.mendelson.util.log.ConsoleHandlerStdout;
import de.mendelson.util.modulelock.ModuleLockReleaseController;
import de.mendelson.util.security.BouncyCastleProviderSingleton;
import de.mendelson.util.security.CryptoProvider;
import de.mendelson.util.security.cert.KeystoreStorageImplDB;
import de.mendelson.util.security.keydata.KeydataAccessDB;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class to start the AS2 server
 *
 * @author S.Heller
 * @version $Revision: 201 $
 * @since build 68
 */
public class AS2Server extends AbstractAS2Server implements AS2ServerMBean, ServerHelloMessageGenerator {

    public static final String SERVER_LOGGER_NAME = "de.mendelson.as2.server";
    public static final int CLIENTSERVER_COMM_PORT = 1234;
    public static final int CLIENTSERVER_COMM_PORT_TEST = 41234;
    public static final Path LOG_DIR;

    static {
        LOG_DIR = Paths.get(System.getProperty("user.dir"), "log");
    }
    private final static AtomicInteger transactionCounter = new AtomicInteger(0);
    private final static AtomicLong rawDataSent = new AtomicLong(0);
    private final static AtomicLong rawDataReceived = new AtomicLong(0);
    /**
     * Server start time in ms
     */
    private long startTime = 0;
    private final Logger logger = Logger.getLogger(SERVER_LOGGER_NAME);
    /**
     * Product preferences
     */
    private final PreferencesAS2 preferences = new PreferencesAS2();
    /**
     * DB server that is used
     */
    private IDBServer dbServer = null;
    /**
     * Localize the output
     */
    private final MecResourceBundle rb;
    private DirPollManager pollManager = null;
    private ConfigurationCheckController configCheckController = null;
    private CertificateManager certificateManagerEncSign = null;
    private boolean skipStartupConfigCheck = false;
    private CertificateManager certificateManagerTLS = null;
    private AS2ServerProcessing serverProcessing = null;
    private final ClientServer clientserver;
    private ClientServerSessionHandlerLocalhost clientServerSessionHandler = null;
    /**
     * Sets if all clients may connect to this server or only clients from the
     * servers host
     */
    private boolean allowAllClients = false;
    private HTTPServerConfigInfo httpServerConfigInfo = null;
    private final DBServerInformation dbServerInformation = new DBServerInformation();
    private final DBClientInformation dbClientInformation = new DBClientInformation();
    /**
     * Indicates that the system is in shutdown process. No Notification etc
     * will be sent anymore in that state
     */
    public static boolean inShutdownProcess = false;
    private final IDBDriverManager dbDriverManager;
    public static final ServerPlugins PLUGINS = new ServerPlugins();
    private SendOrderReceiver sendOrderReceiver = null;
    private final ServerInstanceHA serverInstanceHA = new ServerInstanceHA();
    private final ServerStartupSequence serverStartupSequence = new ServerStartupSequence(this.logger);
    private CertificateCEMController cemController = null;
    private ModuleLockReleaseController lockReleaseController = null;
    private CertificateExpireController expireController = null;
    private PostProcessingEventController eventController = null;
    private FileDeleteController fileDeleteController = null;
    private MessageDeleteController logDeleteController = null;
    private MDNReceiptController receiptController = null;
    private StatisticDeleteController statsDeleteController = null;
    private final Handler loggingHandlerSystemOut = new ConsoleHandlerStdout();
    public final static CryptoProvider CRYPTO_PROVIDER = new CryptoProvider();
    private final AS2Config config;

    // Static reference for REST API access
    private static AS2Server staticServerReference = null;

    /**
     * Creates a new AS2 server and starts it
     *
     * @param startHTTPServer Start the integrated HTTP server. Could be
     *                        disabled to use the receiver servlet in other servlet
     *                        containers
     * @param allowAllClients Allow client-server connections from other than
     *                        localhost
     * @param startPlugins    Starts the plugins if there are any in the system
     * @param startMinaServer Start the Mina client-server for SwingUI. Should be
     *                        disabled in headless mode for security
     * @param config          AS2 configuration object for test mode and other
     *                        settings
     *
     */
    public AS2Server(boolean startHTTPServer, boolean allowAllClients, boolean startPlugins,
            boolean importTLS, boolean importSignEnc, boolean skipStartupConfigCheck,
            boolean startMinaServer, AS2Config config) throws Exception {
        // Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Server.class.getName());
        } // load up resourcebundle
        catch (MissingResourceException e) {
            throw new Exception("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.startTime = Instant.now().toEpochMilli();
        this.skipStartupConfigCheck = skipStartupConfigCheck;
        this.config = config;
        staticServerReference = this; // Set static reference for REST API access
        this.initializeLogger();
        this.logger.info(rb.getResourceString("server.willstart", AS2ServerVersion.getFullProductName()));
        this.logger.info(Copyright.getCopyrightMessage());

        // Log test mode status
        int clientServerPort = config.getClientServerPort();
        if (config.isTestMode()) {
            this.logger.info("*** TEST MODE ENABLED - Using alternative port " + clientServerPort
                    + " for client-server communication ***");
        }

        System.setProperty("mendelson.as2.embeddedhttpserver", startHTTPServer ? "TRUE" : "FALSE");
        // Store test mode in system property for JettyStarter to access
        System.setProperty("mend.as2.testmode", String.valueOf(config.isTestMode()));
        this.fireSystemEventServerStartupBegins();
        this.allowAllClients = allowAllClients;
        PLUGINS.setStartPlugins(startPlugins);
        this.performStartupChecks();
        this.serverStartupSequence.performWork();
        dbDriverManager = getActivatedDBDriverManager();

        // Conditionally start Mina client-server for SwingUI
        if (startMinaServer) {
            this.logger.info("Starting Mina client-server for SwingUI on port " + clientServerPort);
            this.clientserver = new ClientServer(this.logger, clientServerPort,
                    new ClientServerTLSImplDefault(AS2ServerVersion.getFullProductName()));
            this.clientserver.setProductName(AS2ServerVersion.getFullProductName());
            this.initializeServerInstanceHA();
            this.setupClientServerSessionHandler();
        } else {
            this.logger.info("Mina client-server DISABLED (headless mode) - SwingUI access not available");
            this.logger.info("Management available via WebUI at http://localhost:8080/as2/webui/");
            // Set clientserver to null so we can detect headless mode
            this.clientserver = null;
            this.clientServerSessionHandler = null;
        }

        this.start(importTLS, importSignEnc);
        // stop logging to the console here
        this.logger.removeHandler(this.loggingHandlerSystemOut);
        // start the partner poll threads
        this.pollManager.start();
    }

    /**
     * If there is any other value in the internal keystore settings than the
     * default value and the HA plugin is not active this will reset all
     * settings back to the default settings. This is also required for any
     * update from mendelson AS2 older than build 580 where the keystore
     * settings have been changed by the user
     */
    private void handleKeystoreSettings(boolean importTLS, boolean importSignEnc) throws Exception {
        KeydataAccessDB keydataAccessDB = new KeydataAccessDB(this.dbDriverManager,
                SystemEventManagerImplAS2.instance());
        Path keystoreFileEncSign = Paths.get("certificates.p12");
        if (importSignEnc) {
            byte[] keystoreData = Files.readAllBytes(keystoreFileEncSign);
            keydataAccessDB.updateKeydata(keystoreData,
                    KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_PKCS12,
                    KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN,
                    BouncyCastleProviderSingleton.instance().getName());
            keydataAccessDB.logKeystoreImport(this.logger,
                    keystoreFileEncSign,
                    KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN,
                    KeydataAccessDB.REASON_IMPORT_COMMAND_LINE_SETTINGS);
        } else {
            keydataAccessDB.insertKeydataFromFileIfItDoesNotExistInDB(
                    this.logger,
                    keystoreFileEncSign,
                    KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_PKCS12,
                    KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN,
                    BouncyCastleProviderSingleton.instance().getName());
        }
        Path keystoreFileTLS = Paths.get("jetty12/etc/keystore");
        if (importTLS) {
            byte[] keystoreData = Files.readAllBytes(keystoreFileTLS);
            keydataAccessDB.updateKeydata(keystoreData,
                    KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS,
                    KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                    BouncyCastleProviderSingleton.instance().getName());
            keydataAccessDB.logKeystoreImport(this.logger,
                    keystoreFileTLS,
                    KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                    KeydataAccessDB.REASON_IMPORT_COMMAND_LINE_SETTINGS);
        } else {
            keydataAccessDB.insertKeydataFromFileIfItDoesNotExistInDB(
                    this.logger,
                    keystoreFileTLS,
                    KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS,
                    KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                    BouncyCastleProviderSingleton.instance().getName());
        }
    }

    private void fireSystemEventServerStartupBegins() {
        String subject = this.rb.getResourceString("server.willstart", AS2ServerVersion.getFullProductName());
        String body = this.rb.getResourceString("server.start.details",
                new Object[] {
                        AS2ServerVersion.getFullProductName(),
                        Boolean.toString(System.getProperty("mendelson.as2.embeddedhttpserver", "TRUE")
                                .equalsIgnoreCase("TRUE")),
                        Boolean.toString(this.allowAllClients),
                        AS2Tools.getDataSizeDisplay(Runtime.getRuntime().maxMemory()),
                        System.getProperty("java.version"),
                        System.getProperty("user.name"),
                        ServerInstance.ID
                });
        SystemEventManagerImplAS2.instance().newEvent(
                SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                subject, body);
    }

    /**
     * Returns the currently activated DB driver manager
     */
    public static final IDBDriverManager getActivatedDBDriverManager() {
        // if (PLUGINS.isActivated(ServerPlugins.PLUGIN_POSTGRESQL)) {
        // return (DBDriverManagerPostgreSQL.instance());
        // } else if (PLUGINS.isActivated(ServerPlugins.PLUGIN_MYSQL)) {
        // return (DBDriverManagerMySQL.instance());
        // } else if (PLUGINS.isActivated(ServerPlugins.PLUGIN_ORACLE_DB)) {
        // return (DBDriverManagerOracleDB.instance());
        // } else {
        // return (DBDriverManagerHSQL.instance());
        // }
        return (DBDriverManagerPostgreSQL.instance());
    }

    /**
     * Informs the event manager that the server is finally running - with or
     * without found configuration issues
     */
    private void fireSystemEventServerRunning(List<ConfigurationIssue> configurationIssues) {
        String subject = this.rb.getResourceString("server.started",
                String.valueOf(System.currentTimeMillis() - this.startTime));
        String body = this.rb.getResourceString("server.start.details",
                new Object[] {
                        AS2ServerVersion.getFullProductName(),
                        Boolean.toString(
                                System.getProperty("mendelson.as2.embeddedhttpserver").equalsIgnoreCase("TRUE")),
                        Boolean.toString(this.allowAllClients),
                        AS2Tools.getDataSizeDisplay(Runtime.getRuntime().maxMemory()),
                        System.getProperty("java.version"),
                        System.getProperty("user.name"),
                        ServerInstance.ID
                });
        int severity = SystemEvent.SEVERITY_INFO;
        if (!configurationIssues.isEmpty()) {
            StringBuilder issueListStr = new StringBuilder();
            for (ConfigurationIssue issue : configurationIssues) {
                issueListStr.append("*").append(issue.getSubject());
                if (issue.getDetails() != null && !issue.getDetails().trim().isEmpty()) {
                    issueListStr.append(" (").append(issue.getDetails()).append(")");
                }
                issueListStr.append("\n");
            }
            severity = SystemEvent.SEVERITY_WARNING;
            if (configurationIssues.size() > 1) {
                body = this.rb.getResourceString("server.started.issues", configurationIssues.size())
                        + "\n"
                        + issueListStr
                        + "\n\n"
                        + body;
            } else {
                body = this.rb.getResourceString("server.started.issue")
                        + "\n"
                        + issueListStr
                        + "\n\n"
                        + body;
            }
        }
        // display the used libs
        List<String> usedLibsList = LibVersion.getLibVersions();
        if (!usedLibsList.isEmpty()) {
            body = body + "\n\n";
            body = body + this.rb.getResourceString("server.started.usedlibs") + ":\n";
            for (String usedLibsListStr : usedLibsList) {
                body = body + usedLibsListStr + "\n";
            }
        }
        SystemEventManagerImplAS2.instance().newEvent(severity,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_MAIN_SERVER_RUNNING,
                subject, body);
    }

    private void performStartupChecks() throws Exception {
        this.checkLock();
        // check if all ports are available - use the correct port based on test mode
        AS2ServerResourceCheck resourceCheck = new AS2ServerResourceCheck();
        int clientServerPort = config.getClientServerPort();
        resourceCheck.performPortCheck(clientServerPort);
        resourceCheck.checkCPUCores(this.logger);
        resourceCheck.checkHeap(this.logger);
        BCCryptoHelper helper = new BCCryptoHelper();
        // check if the jurisdiction policy strength package has been installed
        boolean unlimitedStrengthInstalled = helper.performUnlimitedStrengthJurisdictionPolicyTest();
        if (!unlimitedStrengthInstalled) {
            this.logger.severe(this.rb.getResourceString("fatal.limited.strength"));
            String subject = this.rb.getResourceString("fatal.limited.strength");
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    subject, "");
            System.exit(1);
        }
    }

    @Override
    public void start(boolean importTLS, boolean importSignEnc) throws Exception {
        try {
            this.ensureRunningDBServer();
            this.handleKeystoreSettings(importTLS, importSignEnc);
            this.certificateManagerEncSign = new CertificateManager(this.logger);
            KeystoreStorage signEncStorage = new KeystoreStorageImplDB(
                    SystemEventManagerImplAS2.instance(),
                    this.dbDriverManager,
                    KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN,
                    KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_PKCS12);
            this.certificateManagerEncSign.loadKeystoreCertificates(signEncStorage);
            this.certificateManagerTLS = new CertificateManager(this.logger);
            KeystoreStorage tlsStorage = new KeystoreStorageImplDB(
                    SystemEventManagerImplAS2.instance(),
                    this.dbDriverManager,
                    KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                    KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS);
            this.certificateManagerTLS.loadKeystoreCertificates(tlsStorage);
            this.startHTTPServer(tlsStorage);
            this.startSendOrderReceiver();
            this.initializeAdditionalLogger();
            // start control threads
            this.receiptController = new MDNReceiptController(this.clientserver, this.dbDriverManager);
            this.receiptController.startMDNCheck();
            this.logDeleteController = new MessageDeleteController(
                    this.clientserver, this.dbDriverManager);
            this.logDeleteController.startAutoDeleteControl();
            this.fileDeleteController = new FileDeleteController(this.dbDriverManager);
            this.fileDeleteController.startAutoDeleteControl();
            this.statsDeleteController = new StatisticDeleteController(this.dbDriverManager);
            this.statsDeleteController.startAutoDeleteControl();
            this.eventController = new PostProcessingEventController(this.clientserver,
                    this.certificateManagerEncSign,
                    this.dbDriverManager);
            this.eventController.startEventExecution();
            this.pollManager = new DirPollManager(this.certificateManagerEncSign,
                    this.clientserver, this.dbDriverManager);
            this.configCheckController = new ConfigurationCheckController(
                    this.certificateManagerEncSign,
                    this.certificateManagerTLS,
                    this.httpServerConfigInfo, this.pollManager,
                    this.dbDriverManager);
            this.serverProcessing = new AS2ServerProcessing(this.clientserver, this.pollManager,
                    this.certificateManagerEncSign, this.certificateManagerTLS, this.dbDriverManager,
                    this.configCheckController, this.httpServerConfigInfo, this.dbServerInformation,
                    this.dbClientInformation);
            // Only set session handler if Mina server is running
            if (this.clientServerSessionHandler != null) {
                this.clientServerSessionHandler.addServerProcessing(this.serverProcessing);
            }
            // Make serverProcessing available to REST API
            de.mendelson.comm.as2.servlet.rest.RestApplication.ServerProcessingHolder
                    .setInstance(this.serverProcessing);
            this.expireController = new CertificateExpireController(this.certificateManagerEncSign,
                    this.certificateManagerTLS);
            this.expireController.startCertExpireControl();
            // Start configuration check controller unless skipped
            if (!skipStartupConfigCheck) {
                this.configCheckController.start();
            } else {
                this.logger.info("Configuration check controller disabled (as2.startup.skip.configcheck=true)");
            }
            this.lockReleaseController = new ModuleLockReleaseController(
                    this.dbDriverManager, SystemEventManagerImplAS2.instance(),
                    AS2Server.SERVER_LOGGER_NAME);
            this.lockReleaseController.startLockReleaseControl();
            this.cemController = new CertificateCEMController(
                    this.clientserver, this.dbDriverManager, this.certificateManagerEncSign);
            this.cemController.start();
            new SystemEventNotificationControllerImplAS2(
                    this.getLogger(),
                    this.dbDriverManager);
            Runtime.getRuntime().addShutdownHook(new AS2ShutdownThread(this.dbServer));
            // listen for inbound client connects (only if Mina server is enabled)
            if (this.clientserver != null) {
                this.clientserver.start();
            }
            // run the configuration check unless skipped
            List<ConfigurationIssue> configurationIssues;
            if (skipStartupConfigCheck) {
                this.logger.info("Skipping startup configuration check (as2.startup.skip.configcheck=true)");
                configurationIssues = new ArrayList<>();
            } else {
                configurationIssues = this.configCheckController.runOnce();
            }
            for (ConfigurationIssue issue : configurationIssues) {
                StringBuilder issueDetails = new StringBuilder();
                issueDetails.append("(" + issue.getDetails() + ")");
                issueDetails.append("\n\n");
                issueDetails.append(this.html2txt(issue.getHintAsHTML()));
                SystemEventManagerImplAS2.instance().newEvent(
                        SystemEvent.SEVERITY_WARNING,
                        SystemEvent.ORIGIN_SYSTEM,
                        SystemEvent.TYPE_SERVER_CONFIGURATION_CHECK,
                        issue.getSubject(), issueDetails.toString());
            }
            this.fireSystemEventServerRunning(configurationIssues);
            this.logger.info(rb.getResourceString("server.started",
                    String.valueOf(System.currentTimeMillis() - this.startTime)));
        } catch (BindException e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    this.rb.getResourceString("server.startup.failed"),
                    this.rb.getResourceString("bind.exception",
                            new Object[] { e.getMessage(),
                                    AS2ServerVersion.getProductName()
                            }));
            // populate the bind exception with some more information for the user
            BindException bindException = new BindException(this.rb.getResourceString("bind.exception",
                    new Object[] { e.getMessage(),
                            AS2ServerVersion.getProductName()
                    }));
            throw bindException;
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    this.rb.getResourceString("server.startup.failed"),
                    e.getMessage());
            throw e;
        }
    }

    private String html2txt(String htmlStr) {
        htmlStr = AS2Tools.replace(htmlStr, "<HTML>", "");
        htmlStr = AS2Tools.replace(htmlStr, "<br>", "\n");
        htmlStr = AS2Tools.replace(htmlStr, "</HTML>", "");
        htmlStr = AS2Tools.replace(htmlStr, "<strong>", "");
        htmlStr = AS2Tools.replace(htmlStr, "</strong>", "");
        return (htmlStr);
    }

    /**
     * Initialize the main logging interface
     */
    private void initializeLogger() {
        this.logger.setLevel(Level.ALL);
        if (this.logger.getParent() != null) {
            Handler[] handlerList = this.logger.getParent().getHandlers();
            for (Handler handler : handlerList) {
                handler.setLevel(Level.ALL);
                if (handler instanceof ConsoleHandler) {
                    handler.setFormatter(new LogFormatter(LogFormatter.FORMAT_CONSOLE));
                }
            }
        }
        Handler[] handlerList = this.logger.getHandlers();
        for (Handler handler : handlerList) {
            handler.setLevel(Level.ALL);
            if (handler instanceof ConsoleHandler) {
                handler.setFormatter(new LogFormatter(LogFormatter.FORMAT_CONSOLE));
            }
        }
        LogFormatter logformatterSystemOut = new LogFormatter(LogFormatter.FORMAT_CONSOLE);
        this.loggingHandlerSystemOut.setFormatter(logformatterSystemOut);
        this.loggingHandlerSystemOut.setLevel(Level.ALL);
        this.logger.addHandler(this.loggingHandlerSystemOut);
        this.logger.setUseParentHandlers(false);
    }

    /**
     * Adds additional logging targets, e.g. DB, Client-Server interface, daily
     * log to the existing logger
     */
    private void initializeAdditionalLogger() {
        // send the log info to the attached clients of the client-server framework
        // (only if Mina enabled)
        if (this.clientServerSessionHandler != null) {
            logger.addHandler(new ClientServerLoggingHandler(this.clientServerSessionHandler));
        }
        logger.addHandler(new DBLoggingHandler(dbDriverManager));
        // add file logger that logs in a daily subdir
        logger.addHandler(new DailySubdirFileLoggingHandler(
                AS2Server.LOG_DIR,
                "as2.log", new LogFormatterAS2(LogFormatter.FORMAT_LOGFILE,
                        this.dbDriverManager),
                SystemEventManagerImplAS2.instance()));
    }

    private void setupClientServerSessionHandler() {
        // Only called if Mina server is enabled (startMinaServer=true)
        // set up session handler for incoming client requests
        this.clientServerSessionHandler = new ClientServerSessionHandlerLocalhost(this.logger,
                new String[] { AS2ServerVersion.getFullProductName() }, this.allowAllClients,
                this.preferences.getBoolean(PreferencesAS2.COMMUNITY_EDITION) ? 1 : -1,
                SystemEventManagerImplAS2.instance(), this.dbDriverManager);
        this.clientServerSessionHandler.setAnonymousProcessing(new AnonymousProcessingAS2());
        this.clientserver.setSessionHandler(this.clientServerSessionHandler);
        this.clientServerSessionHandler.setProductName(AS2ServerVersion.getProductName());
    }

    @Override
    public Logger getLogger() {
        return (this.logger);
    }

    /**
     * Checks for a lock to prevent starting the server several times on the
     * same machine
     *
     */
    private void checkLock() {
        // check if lock file exists, if it exists cancel!
        Path lockFile = Paths.get(AS2ServerVersion.getProductName().replace(' ', '_') + ".lock");
        if (Files.exists(lockFile)) {
            long lastModificationTime = System.currentTimeMillis();
            try {
                lastModificationTime = Files.getLastModifiedTime(lockFile).toMillis();
            } catch (IOException e) {
                // nop
            }
            DateFormat format = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            this.logger.severe(rb.getResourceString("server.already.running",
                    new Object[] {
                            lockFile.toAbsolutePath().toString(),
                            format.format(new java.util.Date(lastModificationTime))
                    }));
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    this.rb.getResourceString("server.startup.failed"),
                    rb.getResourceString("server.already.running",
                            new Object[] {
                                    lockFile.toAbsolutePath().toString(),
                                    format.format(new java.util.Date(lastModificationTime))
                            }));
            throw new ServerAlreadyRunningException(rb.getResourceString("server.already.running",
                    new Object[] {
                            lockFile.toAbsolutePath().toString(),
                            format.format(new java.util.Date(lastModificationTime))
                    }));
        } else {
            // write the lock file
            try (Writer writer = Files.newBufferedWriter(lockFile)) {
                writer.write("");
            } catch (Exception e) {
                this.logger
                        .severe("Problem writing the lock file: [" + e.getClass().getName() + "]: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * This starts the poll process that listens to the database queue for new
     * data to send
     *
     * @throws Exception
     */
    private void startSendOrderReceiver() throws Exception {
        // reset the send order state of available send orders back to waiting
        SendOrderAccessDB sendOrderAccess = new SendOrderAccessDB(
                this.dbDriverManager);
        sendOrderAccess.resetAllToWaiting();
        this.sendOrderReceiver = new SendOrderReceiver(this.clientserver, this.dbDriverManager);
        this.sendOrderReceiver.execute();
    }

    /**
     * Starts the embedded web server if requested
     */
    private Server startHTTPServer(KeystoreStorage tlsStorage) throws Exception {
        // start the HTTP server if this is requested
        if (System.getProperty("mendelson.as2.embeddedhttpserver", "TRUE").equalsIgnoreCase("TRUE")) {
            JettyStarter starter = new JettyStarter(this.logger, tlsStorage, this.dbDriverManager,
                    this.certificateManagerTLS);
            starter.startWebserver();
            this.httpServerConfigInfo = starter.getHttpServerConfigInfo();
        } else {
            this.logger.info(rb.getResourceString("server.nohttp"));
        }
        return (null);
    }

    /**
     * Starts the database server
     */
    private void ensureRunningDBServer() throws Exception {
        // start the database server and ensure it is running
        // I only want postgres currently

        // if (PLUGINS.isActivated(ServerPlugins.PLUGIN_POSTGRESQL)) {
        // this.dbServer = new DBServerPostgreSQL(this.dbDriverManager,
        // this.dbServerInformation,
        // this.dbClientInformation);
        // } else if (PLUGINS.isActivated(ServerPlugins.PLUGIN_MYSQL)) {
        // this.dbServer = new DBServerMySQL(this.dbDriverManager,
        // this.dbServerInformation,
        // this.dbClientInformation);
        // } else if (PLUGINS.isActivated(ServerPlugins.PLUGIN_ORACLE_DB)) {
        // this.dbServer = new DBServerOracle(this.dbDriverManager,
        // this.dbServerInformation,
        // this.dbClientInformation);
        // } else {
        // this.dbServer = new DBServerHSQL(this.dbDriverManager,
        // this.dbServerInformation,
        // this.dbClientInformation);
        // }

        this.dbServer = new DBServerPostgreSQL(this.dbDriverManager, this.dbServerInformation,
                this.dbClientInformation);
        this.dbServer.ensureServerIsRunning();
    }

    @Override
    public int getPort() {
        return (CLIENTSERVER_COMM_PORT);
    }

    /**
     * Get static server reference for REST API access
     */
    public static AS2Server getStaticServerReference() {
        return staticServerReference;
    }

    /**
     * Get HTTP server configuration info for REST API access
     */
    public HTTPServerConfigInfo getHTTPServerConfigInfo() {
        return this.httpServerConfigInfo;
    }

    /**
     * MBean interface
     */
    @Override
    public long getUsedMemoryInBytes() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    /**
     * MBean interface
     */
    @Override
    public long getTotalMemoryInBytes() {
        return (Runtime.getRuntime().totalMemory());
    }

    /**
     * MBean interface
     */
    @Override
    public String getServerVersion() {
        return (AS2ServerVersion.getProductName() + " " + AS2ServerVersion.getVersion() + " "
                + AS2ServerVersion.getBuild());
    }

    /**
     * Returns the server processing instance for REST API access
     */
    public AS2ServerProcessing getServerProcessing() {
        return this.serverProcessing;
    }

    /**
     * MBean interface
     */
    @Override
    public long getUptimeInMS() {
        return (System.currentTimeMillis() - this.startTime);
    }

    @Override
    public long getRawDataSentInBytesInUptime() {
        return (rawDataSent.get());
    }

    @Override
    public long getRawDataReceivedInBytesInUptime() {
        return (rawDataReceived.get());
    }

    @Override
    public long getTransactionCountInUptime() {
        return (transactionCounter.get());
    }

    public static void incTransactionCounter() {
        transactionCounter.incrementAndGet();
    }

    public static void incRawSentData(long size) {
        rawDataSent.addAndGet(size);
    }

    public static void incRawReceivedData(long size) {
        rawDataReceived.addAndGet(size);
    }

    /**
     * Deletes the lock file
     */
    public static void deleteLockFile() {
        Path lockFile = Paths.get(AS2ServerVersion.getProductName().replace(' ', '_') + ".lock");
        try {
            Files.delete(lockFile);
        } catch (Exception e) {
            // nop
        }
    }

    /**
     * Returns the new calculated server HA instance. The main data should just
     * performed once as this is expensive
     */
    private void initializeServerInstanceHA() {
        this.serverInstanceHA.setProductVersion(AS2ServerVersion.getFullProductName());
        this.serverInstanceHA.setUniqueId(ServerInstance.ID);
        this.serverInstanceHA.setStartTime(this.startTime);
        this.serverInstanceHA.setOS(System.getProperty("os.name")
                + " " + System.getProperty("os.version")
                + " " + System.getProperty("os.arch"));
        // Only count clients if Mina server is running
        if (this.clientserver != null) {
            this.serverInstanceHA.setNumberOfClients(this.clientserver.getSessions().size());
        } else {
            this.serverInstanceHA.setNumberOfClients(0);
        }
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.serverInstanceHA.setLocalIP(inetAddress.getHostAddress());
        } catch (Exception e) {
            // nop
        }
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        this.serverInstanceHA.setHost(runtimeBean.getName());
        // try to figure out if this instance runs on aws - then add some additional
        // values that are conditional
        String publicIP = AWSRESTAccess.retrieveIP4Address();
        this.serverInstanceHA.setPublicIP(publicIP);
        String cloudInstanceId = AWSRESTAccess.retrieveInstanceId();
        this.serverInstanceHA.setCloudInstanceId(cloudInstanceId);
    }

    /**
     * Returns the new calculated server HA instance. The main data should just
     * performed once as this is expensive
     */
    public ServerInstanceHA getServerInstanceHA() {
        // Only count clients if Mina server is running
        if (this.clientserver != null) {
            this.serverInstanceHA.setNumberOfClients(this.clientserver.getSessions().size());
        } else {
            this.serverInstanceHA.setNumberOfClients(0);
        }
        return (this.serverInstanceHA);
    }

    public static String getLicenseType() {
        // figure out the license type
        if (AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_HA)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_ORACLE_DB)) {
            return (ServerInfoRequest.VALUE_LICENSE_TYPE_ENTERPRISE_HA);
        } else if (AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_MYSQL)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_POSTGRESQL)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_REST_API)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_JAVA_API)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_XML_API)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_OAUTH2)) {
            return (ServerInfoRequest.VALUE_LICENSE_TYPE_PROFESSIONAL);
        } else {
            return (ServerInfoRequest.VALUE_LICENSE_TYPE_ENTRY);
        }
    }

    /**
     * Makes this a ServerHelloMessageGenerator
     */
    @Override
    public List<ServerHelloMessage> generateServerHelloMessages() {
        List<ServerHelloMessage> serverHelloList = new ArrayList<ServerHelloMessage>();
        String productName = AS2ServerVersion.getFullProductName();
        String licenseType = AS2Server.getLicenseType();
        if (!licenseType.equals(ServerInfoRequest.VALUE_LICENSE_TYPE_ENTRY)) {
            productName = productName + " " + licenseType;
        }
        serverHelloList.add(
                new ServerHelloMessage(this.rb.getResourceString("server.hello",
                        productName)));
        if (AS2Server.PLUGINS.licenseWillExpire()) {
            if (AS2Server.PLUGINS.getLicenseExpiresInDays() == 1) {
                serverHelloList.add(
                        new ServerHelloMessage(this.rb.getResourceString("server.hello.licenseexpire.single",
                                new Object[] {
                                        "1",
                                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                                .format(AS2Server.PLUGINS.getLicenseExpireDate())
                                }), ServerHelloMessage.LEVEL_SEVERE));
            } else {
                serverHelloList.add(new ServerHelloMessage(this.rb.getResourceString("server.hello.licenseexpire",
                        new Object[] {
                                String.valueOf(AS2Server.PLUGINS.getLicenseExpiresInDays()),
                                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                        .format(AS2Server.PLUGINS.getLicenseExpireDate())
                        }), ServerHelloMessage.LEVEL_SEVERE));
            }
        }
        return (serverHelloList);
    }
}
