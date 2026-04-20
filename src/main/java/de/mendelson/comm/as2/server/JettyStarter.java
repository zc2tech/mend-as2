package de.mendelson.comm.as2.server;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.httpconfig.server.HTTPServerConfigInfo;
import de.mendelson.util.httpconfig.server.HTTPServerConfigInfoProcessor;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.RequestLogWriter;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;


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
 * Helper class that starts up the internal jetty web server
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
public class JettyStarter {

    private final String MODULE_NAME;
    private final MecResourceBundle rb;
    private HTTPServerConfigInfo httpServerConfigInfo = null;
    private final Logger logger;
    private final KeystoreStorage tlsStorage;
    private final JettyCertificateRefreshController certificateRefreshController;
    private final PreferencesAS2 preferences;
    private final CertificateManager certificateManagerTLS;

    public JettyStarter(Logger logger, KeystoreStorage tlsStorage, IDBDriverManager dbDriverManager, CertificateManager certificateManagerTLS) {
        this.logger = logger;
        this.tlsStorage = tlsStorage;
        this.certificateManagerTLS = certificateManagerTLS;
        this.certificateRefreshController = new JettyCertificateRefreshController(logger, dbDriverManager);
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleJettyStarter.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.MODULE_NAME = this.rb.getResourceString("module.name");
        this.preferences = new PreferencesAS2(dbDriverManager);
    }

    /**
     * starts the web server
     */
    public Server startWebserver() throws Exception {
        this.logger.info(MODULE_NAME + " " + this.rb.getResourceString("httpserver.willstart"));

        // Check if test mode is enabled
        boolean isTestMode = Boolean.parseBoolean(System.getProperty("mend.as2.testmode", "false"));

        SystemEventManagerImplAS2.instance().newEvent(
                SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_HTTP_SERVER_STARTUP_BEGIN,
                rb.getResourceString("httpserver.willstart"),
                "");
        try {
            //Read the user defined properties file to overwrite the default settings of the
            //jetty.xml file
            Properties userConfiguration = new Properties();
            Path userConfigurationFile = Paths.get("jetty12", "jetty.config");
            this.logger.info(MODULE_NAME + " " + this.rb.getResourceString("userconfiguration.reading",
                    userConfigurationFile.toAbsolutePath().toString()));
            try (InputStream userConfigurationStream = Files.newInputStream(userConfigurationFile)) {
                userConfiguration.load(userConfigurationStream);
            } catch (Exception e) {
                this.logger.warning(MODULE_NAME + " " + this.rb.getResourceString("userconfiguration.readerror",
                        new Object[]{
                            userConfigurationFile.toAbsolutePath().toString(),
                            "[" + e.getClass().getSimpleName() + "] " + e.getMessage()
                        }));
            }

            // Override ports if in test mode
            if (isTestMode) {
                // Use fixed test mode ports: HTTP=11080, HTTPS=11443
                userConfiguration.setProperty("jetty.http.port", "11080");
                userConfiguration.setProperty("jetty.ssl.port", "11443");

                this.logger.info(MODULE_NAME + " *** TEST MODE: Using HTTP port 11080 and HTTPS port 11443 ***");
            }

            Map<String, String> userConfigurationMap = new HashMap<String, String>();
            for (Object key : userConfiguration.keySet()) {
                String keyStr = key.toString();
                String valueStr = userConfiguration.getProperty(key.toString());
                userConfigurationMap.put(keyStr, valueStr);
                this.logger.info(MODULE_NAME + " " + this.rb.getResourceString("userconfiguration.setvar",
                        new Object[]{
                            keyStr,
                            valueStr
                        }));
            }
            Path jettyXMLConfigurationPath = Paths.get("jetty12", "etc", "jetty.xml");
            Resource jettyConfigResource = ResourceFactory.root().newResource(jettyXMLConfigurationPath);
            XmlConfiguration jettyXMLConfiguration = new XmlConfiguration(jettyConfigResource);
            jettyXMLConfiguration.getProperties().putAll(userConfigurationMap);

            // Use regular thread pool for HTTP request handling (Java 17 compatible)
            QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setStopTimeout(5000); // Set thread pool stop timeout (5 seconds)
            // Virtual threads require Java 21+, using regular threads for Java 17 compatibility
            // threadPool.setVirtualThreadsExecutor(Executors.newVirtualThreadPerTaskExecutor());
            org.eclipse.jetty.server.Server tempHTTPServer = new org.eclipse.jetty.server.Server(threadPool);

            // Set stop timeout to avoid long waits during shutdown (5 seconds)
            tempHTTPServer.setStopTimeout(5000);

            jettyXMLConfiguration.configure(tempHTTPServer);

            // Programmatically deploy webapps from jetty12/webapps directory
            Path webappsDir = Paths.get("jetty12", "webapps");
            if (Files.exists(webappsDir) && Files.isDirectory(webappsDir)) {
                org.eclipse.jetty.server.handler.ContextHandlerCollection contexts =
                    tempHTTPServer.getDescendant(org.eclipse.jetty.server.handler.ContextHandlerCollection.class);
                if (contexts != null) {
                    try (var stream = Files.list(webappsDir)) {
                        stream.filter(Files::isDirectory).forEach(webappPath -> {
                            try {
                                String contextPath = "/" + webappPath.getFileName().toString();
                                WebAppContext webapp = new WebAppContext();
                                webapp.setContextPath(contextPath);
                                webapp.setBaseResourceAsPath(webappPath);

                                // Explicitly set web.xml descriptor path
                                Path webXmlPath = webappPath.resolve("WEB-INF/web.xml");
                                if (Files.exists(webXmlPath)) {
                                    webapp.setDescriptor(webXmlPath.toString());
                                }

                                webapp.setParentLoaderPriority(true);
                                contexts.addHandler(webapp);
                                this.logger.info(MODULE_NAME + " Deploying webapp: " + contextPath + " from " + webappPath);
                            } catch (Exception e) {
                                this.logger.warning(MODULE_NAME + " Failed to deploy webapp from " + webappPath + ": " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }

            //add life cycle listener to jetty
            tempHTTPServer.addEventListener(new LifeCycle.Listener() {
                @Override
                public void lifeCycleStarted(LifeCycle lifeCycle) {
                    logger.info(MODULE_NAME + " " + rb.getResourceString("httpserver.running", "Jetty " + Server.getVersion()));
                }

                @Override
                public void lifeCycleStarting(LifeCycle lifeCycle) {
                }

                @Override
                public void lifeCycleFailure(LifeCycle lifeCycle, Throwable failure) {
                    logger.info(MODULE_NAME + " " + rb.getResourceString("httpserver.startup.problem",
                            "[" + failure.getClass().getSimpleName() + "] " + failure.getMessage()));
                    failure.printStackTrace();
                }

                @Override
                public void lifeCycleStopped(LifeCycle lifeCycle) {
                    logger.info(MODULE_NAME + " " + rb.getResourceString("httpserver.stopped"));
                }
            });
            //add bean listener to jetty
            tempHTTPServer.addEventListener(new Container.InheritedListener() {
                @Override
                public void beanAdded(Container parent, Object child) {
                    //Acceptor is a private class in AbstractConnector...
                    if (parent instanceof ServerConnector
                            && child.getClass().getName().equals("org.eclipse.jetty.server.AbstractConnector$Acceptor")) {
                        ServerConnector connector = (ServerConnector) parent;
                        String connectorStr = String.format("(%s{%s:%d})",
                                connector.getDefaultProtocol(),
                                connector.getHost() == null ? "0.0.0.0" : connector.getHost(),
                                connector.getLocalPort() <= 0 ? connector.getPort() : connector.getLocalPort());
                        logger.info(MODULE_NAME + " " + rb.getResourceString("listener.started", connectorStr));
                    }
                }

                @Override
                public void beanRemoved(Container parent, Object child) {
                }
            }
            );
            //define the TLS system keystore for the jetty access
            Connector[] connector = tempHTTPServer.getConnectors();
            for (Connector conn : connector) {
                if (conn.getConnectionFactory("ssl") != null) {
                    SslConnectionFactory sslConnectionFactory = (SslConnectionFactory) conn.getConnectionFactory("ssl");
                    SslContextFactory.Server sslContextFactory = (SslContextFactory.Server) sslConnectionFactory.getSslContextFactory();
                    sslContextFactory.setKeyStore(this.tlsStorage.getKeystore());
                    sslContextFactory.setKeyStorePassword(new String(tlsStorage.getKeystorePass()));

                    // Configure trust-all trust manager for client certificates by creating custom SSLContext
                    // This accepts ANY client certificate during TLS handshake without validation
                    // The actual certificate validation happens at application layer by checking fingerprints
                    try {
                        // Create trust manager that accepts all client certificates
                        javax.net.ssl.X509TrustManager trustAllManager = new javax.net.ssl.X509TrustManager() {
                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[0];
                            }
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                // Accept all client certificates without validation
                                // Application layer validates fingerprints against partner_inbound_auth_credentials
                            }
                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                                    throws java.security.cert.CertificateException {
                                // For server cert validation, use default validation
                                throw new java.security.cert.CertificateException("Server cert validation not supported in this TrustManager");
                            }
                        };

                        // Create SSL context with custom trust manager
                        javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");

                        // Initialize KeyManager with the TLS keystore
                        javax.net.ssl.KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance(
                            javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
                        kmf.init(this.tlsStorage.getKeystore(), tlsStorage.getKeystorePass());

                        // Initialize SSL context with KeyManager and trust-all TrustManager
                        sslContext.init(kmf.getKeyManagers(), new javax.net.ssl.TrustManager[] { trustAllManager }, null);

                        // Set the custom SSL context
                        sslContextFactory.setSslContext(sslContext);

                        this.logger.info(MODULE_NAME + " TrustManager configured to accept all client certificates (validation at application layer)");
                    } catch (Exception e) {
                        this.logger.severe(MODULE_NAME + " Failed to configure trust-all TrustManager: " + e.getMessage());
                        // Fallback: use default truststore behavior
                        sslContextFactory.setTrustStore(this.tlsStorage.getKeystore());
                        sslContextFactory.setTrustStorePassword(new String(tlsStorage.getKeystorePass()));
                    }

                    // Request client certificates (but don't require them)
                    // This allows certificate authentication to work while still allowing
                    // connections without client certs (for basic auth or no auth)
                    sslContextFactory.setWantClientAuth(true);
                    this.logger.info(MODULE_NAME + " Configured HTTPS connector to request client certificates (WantClientAuth=true)");
                    certificateRefreshController.addRefreshControl(sslContextFactory);
                }
            }
            // Add a request logger to jetty
            if (this.preferences.getBoolean(PreferencesAS2.EMBEDDED_HTTP_SERVER_REQUESTLOG)) {
                // Use a file name with the pattern 'yyyy_MM_dd'
                RequestLogWriter logWriter = new RequestLogWriter("log/yyyy_MM_dd.jetty.request.log");
                // Log times are in the current time zone.
                logWriter.setTimeZone(TimeZone.getDefault().getID());
                // Set the RequestLog to log to the given file, rolling over at midnight.
                tempHTTPServer.setRequestLog(new CustomRequestLog(logWriter, CustomRequestLog.EXTENDED_NCSA_FORMAT));
            }
            //finally start the embedded HTTP server
            tempHTTPServer.start();
            //ensure the wars have been deployed
            for (Handler handler : tempHTTPServer.getDescendants(WebAppContext.class)) {
                WebAppContext context = (WebAppContext) handler;
                //see if wars had any exceptions that would cause it to be unavailable
                if (context.getUnavailableException()
                        != null) {
                    this.logger.warning(MODULE_NAME + " " + this.rb.getResourceString("deployment.failed",
                            new Object[]{
                                context.getDisplayName(),
                                "["
                                + context.getUnavailableException().getClass().getSimpleName()
                                + "] " + context.getUnavailableException().getMessage()
                            }));
                    SystemEventManagerImplAS2.instance().newEvent(
                            SystemEvent.SEVERITY_WARNING,
                            SystemEvent.ORIGIN_SYSTEM,
                            SystemEvent.TYPE_HTTP_SERVER_STARTUP_BEGIN,
                            context.getDisplayName(),
                            this.rb.getResourceString("deployment.failed",
                                    new Object[]{
                                        context.getDisplayName(),
                                        "["
                                        + context.getUnavailableException().getClass().getSimpleName()
                                        + "] " + context.getUnavailableException().getMessage()
                                    }));
                } else {
                    this.logger.info(MODULE_NAME + " " + this.rb.getResourceString("deployment.success",
                            context.getDisplayName()));
                }
            }
            this.httpServerConfigInfo = HTTPServerConfigInfo.computeHTTPServerConfigInfo(tempHTTPServer, true,
                    "/as2/HttpReceiver", "/as2/ServerState");
            HTTPServerConfigInfoProcessor infoProcessor = new HTTPServerConfigInfoProcessor(
                    this.getHttpServerConfigInfo(), this.certificateManagerTLS);
            StringBuilder body = new StringBuilder();
            body.append(infoProcessor.getMiscConfigurationText());
            SystemEventManagerImplAS2.instance().newEvent(SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_HTTP_SERVER_RUNNING,
                    rb.getResourceString("httpserver.running",
                            "jetty " + this.getHttpServerConfigInfo().getJettyHTTPServerVersion()),
                    body.toString());
            return (tempHTTPServer);
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_HTTP_SERVER_STARTUP_BEGIN,
                    rb.getResourceString("httpserver.willstart"),
                    "[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
            throw e;
        }
    }

    /**
     * @return the httpServerConfigInfo
     */
    public HTTPServerConfigInfo getHttpServerConfigInfo() {
        return httpServerConfigInfo;
    }

}
