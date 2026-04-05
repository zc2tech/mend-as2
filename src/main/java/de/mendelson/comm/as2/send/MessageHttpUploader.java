package de.mendelson.comm.as2.send;

import de.mendelson.comm.as2.clientserver.message.IncomingMessageRequest;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageResponse;
import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.message.AS2Info;
import de.mendelson.comm.as2.message.AS2MDNInfo;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.MDNAccessDB;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.message.store.MessageStoreHandler;
import de.mendelson.comm.as2.partner.HTTPAuthentication;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerHttpHeader;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.statistic.QuotaAccessDB;
import de.mendelson.comm.as2.usermanagement.UserHttpAuthPreference;
import de.mendelson.comm.as2.usermanagement.UserHttpAuthPreferenceAccessDB;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.AnonymousTextClient;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.oauth2.OAuth2Config;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.security.cert.KeystoreStorageImplDB;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.mail.internet.MimeUtility;
import jakarta.servlet.http.HttpServletResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.io.ManagedHttpClientConnection;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;


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
 * Class to allow HTTP multipart uploads
 *
 * @author S.Heller
 * @version $Revision: 224 $
 */
public class MessageHttpUploader {

    private Logger logger = null;
    private final PreferencesAS2 preferences = new PreferencesAS2();
    /**
     * localize the GUI
     */
    private final static MecResourceBundle rb;

    static {
        //Load default resourcebundle
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleHttpUploader.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    /**
     * The header that has been built fro the request
     */
    private final Properties requestHeader = new Properties();
    /**
     * remote answer
     */
    private byte[] responseData = null;
    /**
     * remote answer
     */
    private Header[] responseHeader = null;
    /**
     * remote answer - status code and reason phrase
     */
    private int responseStatusCode = -1;
    private String responseReasonPhrase = null;
    private ClientServer clientserver = null;
    private IDBDriverManager dbDriverManager = null;
    //keystore data
    private KeystoreStorage certStore = null;
    private KeystoreStorage trustStore = null;
    //EDIINT faetures
    private String ediintFeatures = "multiple-attachments, CEM";
    //WebUI user ID for HTTP auth preference resolution
    private int userId = -1;

    /**
     * Creates new message uploader instance
     *
     */
    public MessageHttpUploader() {
    }

    /**
     * Sets the capabilities of the system, defaults to "multiple-attachments,
     * CEM"
     */
    public void setEDIINTFeatures(String ediintFeatures) {
        this.ediintFeatures = ediintFeatures;
    }

    /**
     * Sets keystore parameter for TLS sending. This is only necessary if HTTPS
     * is the protocol used for the message POST
     *
     */
    public void setSSLParameter(KeystoreStorage certStore, KeystoreStorage trustStore) {
        this.certStore = certStore;
        this.trustStore = trustStore;
    }

    /**
     * Passes a logger to this class for logging purpose
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Passes a server instance to this class to refresh messages automatically
     * for logging purpose
     */
    public void setAbstractServer(ClientServer clientserver) {
        this.clientserver = clientserver;
    }

    /**
     * Pass a DB access to this class for logging purpose
     */
    public void setDBConnection(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Set WebUI user ID for HTTP auth preference resolution
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the created header for the sent data
     */
    public Properties upload(HttpConnectionParameter connectionParameter, AS2Message message,
            Partner sender, Partner receiver) throws Exception {
        NumberFormat formatter = new DecimalFormat("0.00");
        AS2Info as2Info = message.getAS2Info();
        MessageAccessDB messageAccess = null;
        MDNAccessDB mdnAccess = null;
        if (this.dbDriverManager != null && messageAccess == null && !as2Info.isMDN()) {
            messageAccess = new MessageAccessDB(this.dbDriverManager);
            messageAccess.initializeOrUpdateMessage((AS2MessageInfo) as2Info);
        } else if (this.dbDriverManager != null && as2Info.isMDN()) {
            mdnAccess = new MDNAccessDB(this.dbDriverManager);
            mdnAccess.initializeOrUpdateMDN((AS2MDNInfo) as2Info);
        }
        if (this.clientserver != null) {
            this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
        }
        long startTime = System.currentTimeMillis();
        //sets the global requestHeader
        int returnCode = this.performUpload(connectionParameter, message, sender, receiver);
        long size = message.getRawDataSize();
        long transferTime = System.currentTimeMillis() - startTime;
        float bytePerSec = (float) ((float) size * 1000f / (float) transferTime);
        float kbPerSec = (float) (bytePerSec / 1024f);
        if (returnCode == HttpServletResponse.SC_OK) {
            if (this.logger != null) {
                this.logger.log(Level.INFO,
                        rb.getResourceString("returncode.ok",
                                new Object[]{
                                    String.valueOf(returnCode),
                                    AS2Tools.getDataSizeDisplay(size),
                                    AS2Tools.getTimeDisplay(transferTime),
                                    formatter.format(kbPerSec),}), as2Info);
            }
        } else if (returnCode == HttpServletResponse.SC_ACCEPTED || returnCode == HttpServletResponse.SC_CREATED || returnCode == HttpServletResponse.SC_NO_CONTENT || returnCode == HttpServletResponse.SC_RESET_CONTENT || returnCode == HttpServletResponse.SC_PARTIAL_CONTENT) {
            if (this.logger != null) {
                this.logger.log(Level.INFO,
                        rb.getResourceString("returncode.accepted",
                                new Object[]{
                                    String.valueOf(returnCode),
                                    AS2Tools.getDataSizeDisplay(size),
                                    AS2Tools.getTimeDisplay(transferTime),
                                    formatter.format(kbPerSec),}), as2Info);
            }
        } else {
            //If the returncode is -1 here, this has been already handled by the upload routine 
            //- its a TLS Problem or a timeout problem
            if (returnCode > 0) {
                //no connection
                SystemEventManagerImplAS2.instance().newEventConnectionProblem(receiver, message.getAS2Info(),
                        rb.getResourceString("error.noconnection"),
                        rb.getResourceString("hint.httpcode.signals.problem",
                                String.valueOf(returnCode)));
                if (this.logger != null) {
                    this.logger.log(Level.SEVERE, rb.getResourceString("hint.httpcode.signals.problem",
                            String.valueOf(returnCode)), message.getAS2Info());
                }
                throw new NoConnectionException("[" + receiver.getURL() + "]: HTTP " + returnCode);
            } else {
                throw new NoConnectionException("");
            }
        }
        //store the sent data and assign the payload to the message
        if (this.dbDriverManager != null) {
            MessageStoreHandler messageStoreHandler = new MessageStoreHandler(this.dbDriverManager);
            messageStoreHandler.storeSentMessage(message, sender, receiver, this.getRequestHeader());
        }
        //perform some statistic entries
        if (this.dbDriverManager != null) {
            //inc the sent data size, this is for new connections (as2 messages, async mdn)
            AS2Server.incRawSentData(size);
            if (message.getAS2Info().isMDN()) {
                AS2MDNInfo mdnInfo = (AS2MDNInfo) message.getAS2Info();
                //ASYNC MDN sent: insert an entry into the statistic table
                QuotaAccessDB.incReceivedMessages(this.dbDriverManager, mdnInfo.getSenderId(),
                        mdnInfo.getReceiverId(), mdnInfo.getState(), mdnInfo.getRelatedMessageId());
            }
        }
        //inform the server of the result if a sync MDN has been requested
        if (!message.isMDN()) {
            AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
            if (messageInfo.requestsSyncMDN()) {
                //check if the received MDN is just empty
                if ((this.getResponseHeader() == null || this.getResponseHeader().length == 0)
                        && (this.getResponseData() == null || this.getResponseData().length == 0)) {
                    throw new Exception(rb.getResourceString("answer.no.sync.mdn.empty"));
                }
                //perform a check if the answer really contains a MDN or is just an empty HTTP 200 with some header data
                //this check looks for the existance of some key header values
                boolean as2FromExists = false;
                boolean as2ToExists = false;
                for (int i = 0; i < this.getResponseHeader().length; i++) {
                    String key = this.getResponseHeader()[i].getName();
                    if (key.equalsIgnoreCase("as2-to")) {
                        as2ToExists = true;
                    } else if (key.equalsIgnoreCase("as2-from")) {
                        as2FromExists = true;
                    }
                }
                if (!as2ToExists) {
                    StringBuilder missingHeaderList = new StringBuilder("\"AS2-TO\"");
                    if (!as2FromExists) {
                        missingHeaderList.append(", \"AS2-FROM\"");
                    }
                    String responseDataStr = null;
                    byte[] uploadResponseData = this.getResponseData();
                    if (uploadResponseData.length < 1024) {
                        responseDataStr = new String(uploadResponseData);
                    } else {
                        responseDataStr = new String(uploadResponseData, 0, 1024);
                    }
                    throw new Exception(rb.getResourceString("answer.no.sync.mdn",
                            new Object[]{
                                missingHeaderList.toString(),
                                responseDataStr}));
                }
                //send the data to the as2 server. It does not care if the MDN has been sync or async anymore
                Path tempFile = null;
                try( AnonymousTextClient client = new AnonymousTextClient(BaseClient.CLIENT_SENDORDER)){
                    client.setDisplayServerLogMessages(false);
                    // Use test mode port if enabled
                    boolean isTestMode = Boolean.parseBoolean(System.getProperty("mendelson.as2.testmode", "false"));
                    int port = isTestMode ? AS2Server.CLIENTSERVER_COMM_PORT_TEST : AS2Server.CLIENTSERVER_COMM_PORT;
                    client.connect("localhost", port, 30000);
                    IncomingMessageRequest messageRequest = new IncomingMessageRequest();
                    messageRequest.setSyncMDN( true );
                    //create temporary file to store the data
                    tempFile = AS2Tools.createTempFile("SYNCMDN_received", ".bin");
                    Files.write(tempFile, this.responseData, StandardOpenOption.SYNC,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE);
                    messageRequest.setMessageDataFilename(tempFile.toAbsolutePath().toString());
                    for (Header singleResponseHeader : this.getResponseHeader()) {
                        String key = singleResponseHeader.getName();
                        String value = singleResponseHeader.getValue();
                        messageRequest.addHeader(key.toLowerCase(), value);
                        if (key.equalsIgnoreCase("content-type")) {
                            messageRequest.setContentType(value);
                        }
                    }
                    //compatibility issue: some AS2 systems do not send a as2-from in the sync case, even if
                    //this if _NOT_ RFC conform
                    //see RFC 4130, section 6.2: The AS2-To and AS2-From header fields MUST be
                    //present in all AS2 messages and AS2 MDNs whether asynchronous or synchronous in nature,
                    //except for asynchronous MDNs, which are sent using SMTP.
                    if (!as2FromExists) {
                        messageRequest.addHeader("as2-from", AS2Message.escapeFromToHeader(receiver.getAS2Identification()));
                    }
                    IncomingMessageResponse response = (IncomingMessageResponse) client.sendSyncWaitInfinite(messageRequest);
                    if (response.getException() != null) {
                        throw (response.getException());
                    }
                } catch (Throwable e) {
                    if (this.logger != null) {
                        this.logger.log(Level.SEVERE, e.getMessage(), as2Info);
                    }
                    messageAccess.setMessageState(as2Info.getMessageId(), AS2Message.STATE_STOPPED);
                }
                if (tempFile != null) {
                    try {
                        Files.delete(tempFile);
                    } catch (Exception e) {
                        SystemEvent event = new SystemEvent(
                                SystemEvent.SEVERITY_WARNING,
                                SystemEvent.ORIGIN_SYSTEM,
                                SystemEvent.TYPE_FILE_DELETE);
                        event.setSubject(event.typeToTextLocalized());
                        event.setBody("[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
                        SystemEventManagerImplAS2.instance().newEvent(event);
                    }
                }
            }
        }
        return (this.getRequestHeader());
    }

    /**
     * Builds a proxy object from the actual preferences, returns null if no
     * proxy is requested
     */
    public ProxyObject createProxyObjectFromPreferences() {
        if (!this.preferences.getBoolean(PreferencesAS2.PROXY_USE)) {
            //return empty proxy object, is not used
            return (null);
        }
        ProxyObject proxy = new ProxyObject();
        proxy.setHost(this.preferences.get(PreferencesAS2.PROXY_HOST));
        proxy.setPort(this.preferences.getInt(PreferencesAS2.PROXY_PORT));
        if (this.preferences.getBoolean(PreferencesAS2.AUTH_PROXY_USE)) {
            proxy.setUser(this.preferences.get(PreferencesAS2.AUTH_PROXY_USER));
            proxy.setPassword(this.preferences.get(PreferencesAS2.AUTH_PROXY_PASS).toCharArray());
        }
        return (proxy);
    }

    /**
     * Uploads the data, returns the HTTP result code
     */
    public int performUpload(HttpConnectionParameter connectionParameter, AS2Message message, Partner sender, Partner receiver) {
        return (this.performUpload(connectionParameter, message, sender, receiver, null));
    }

    /**
     * Creates a request config to be passed to the HttpPost - it contains basic
     * HTTP settings
     */
    private RequestConfig generateRequestConfig(HttpConnectionParameter connectionParameter) {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        if (connectionParameter.getConnectionTimeoutMillis() != -1) {
            requestConfigBuilder.setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionParameter.getConnectionTimeoutMillis()));
        }
        if (connectionParameter.getSoTimeoutMillis() != -1) {
            requestConfigBuilder.setResponseTimeout(Timeout.ofMilliseconds(connectionParameter.getConnectionTimeoutMillis()));
        }
        requestConfigBuilder.setExpectContinueEnabled(connectionParameter.isUseExpectContinue());
        requestConfigBuilder.setContentCompressionEnabled(false);
        return (requestConfigBuilder.build());
    }

    /**
     * Adds a proxy route to the client builder
     *
     * @param proxy
     * @param clientBuilder
     * @param credentialsProvider
     * @throws Exception
     */
    private void addProxy(ProxyObject proxy, HttpClientBuilder clientBuilder, CredentialsProvider credentialsProvider,
            AS2Message message) throws Exception {
        HttpHost proxyHost = new HttpHost("http", proxy.getHost(), proxy.getPort());
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);
        //proxy authentication
        if (proxy.getUser() != null) {
            AuthScope authScope = new AuthScope(proxy.getHost(), proxy.getPort());
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(proxy.getUser(), proxy.getPassword());
            if (credentialsProvider instanceof BasicCredentialsProvider basicProvider) {
                basicProvider.setCredentials(authScope, credentials);
            }
            BasicScheme basicAuth = new BasicScheme();
            AuthCache authCache = new BasicAuthCache();
            authCache.put(proxyHost, basicAuth);
            HttpClientContext context = HttpClientContext.create();
            context.setCredentialsProvider(credentialsProvider);
            context.setAuthCache(authCache);
            clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            this.logger.log(Level.INFO,
                    rb.getResourceString("using.proxy.auth",
                            new Object[]{
                                proxy.getHost(),
                                String.valueOf(proxy.getPort()),
                                proxy.getUser()
                            }), message.getAS2Info());
        } else {
            this.logger.log(Level.INFO,
                    rb.getResourceString("using.proxy",
                            new Object[]{
                                proxy.getHost(),
                                String.valueOf(proxy.getPort())
                            }), message.getAS2Info());
        }
        clientBuilder.setRoutePlanner(routePlanner);
    }

    /**
     * Uploads the data, returns the HTTP result code
     *
     * @param receiptURL Receivers URL, might be NULL
     */
    public int performUpload(HttpConnectionParameter connectionParameter, AS2Message message,
            Partner sender, Partner receiver, URL receiptURL) {
        int statusCode = -1;
        HttpPost filePost = null;
        CloseableHttpClient httpClient = null;
        try {
            //determine the receipt URL if it is not set
            if (receiptURL == null) {
                //async MDN requested?
                if (message.isMDN()) {
                    if (this.dbDriverManager == null) {
                        throw new IllegalArgumentException("MessageHTTPUploader.performUpload(): A MDN receipt URL is not set, unable to determine where to send the MDN");
                    }
                    MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
                    AS2MessageInfo relatedMessageInfo = messageAccess.getLastMessageEntry(
                            ((AS2MDNInfo) message.getAS2Info()).getRelatedMessageId());
                    receiptURL = new URL(relatedMessageInfo.getAsyncMDNURL());
                } else {
                    receiptURL = new URL(receiver.getURL());
                }
            }
            //create the http client
            HttpClientBuilder clientBuilder = HttpClients.custom();
            if (receiptURL.getProtocol().equalsIgnoreCase("https")) {
                SSLConnectionSocketFactory sslConnectionSocketFactory
                        = this.generateSSLFactory(connectionParameter, message.getAS2Info());
                clientBuilder.setConnectionManager(
                    PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(sslConnectionSocketFactory)
                        .build()
                );
            }
            // In HttpClient 5.x, connection reuse strategy is replaced with a lambda
            clientBuilder.setConnectionReuseStrategy((request, response, context) -> false);
            HttpHost targetHost = new HttpHost(receiptURL.getProtocol(), receiptURL.getHost(), receiptURL.getPort());
            ProxyObject proxy = connectionParameter.getProxy();
            if (proxy != null && proxy.getHost() != null) {
                CredentialsProvider proxyCredentialsProvider = new BasicCredentialsProvider();
                this.addProxy(proxy, clientBuilder, proxyCredentialsProvider, message);
            }
            httpClient = clientBuilder.build();
            filePost = new HttpPost(receiptURL.toExternalForm());
            filePost.setConfig(this.generateRequestConfig(connectionParameter));
            if (connectionParameter.getHttpProtocolVersion() == null) {
                //default settings: HTTP 1.1
                filePost.setVersion(HttpVersion.HTTP_1_1);
            } else if (connectionParameter.getHttpProtocolVersion().equals(HttpConnectionParameter.HTTP_1_0)) {
                filePost.setVersion(HttpVersion.HTTP_1_0);
            } else if (connectionParameter.getHttpProtocolVersion().equals(HttpConnectionParameter.HTTP_1_1)) {
                filePost.setVersion(HttpVersion.HTTP_1_1);
            }
            //add basic authentication
            HTTPAuthentication basicAuthentication = null;
            if (message.isMDN()) {
                basicAuthentication = receiver.getAuthenticationCredentialsAsyncMDN();
            } else {
                basicAuthentication = receiver.getAuthenticationCredentialsMessage();
            }

            // Resolve HTTP authentication based on authMode
            String authUsername = null;
            String authPassword = null;

            if (basicAuthentication.getAuthMode() == HTTPAuthentication.AUTH_MODE_BASIC) {
                // Use credentials from partner configuration
                authUsername = basicAuthentication.getUser();
                authPassword = basicAuthentication.getPassword();
            } else if (basicAuthentication.getAuthMode() == HTTPAuthentication.AUTH_MODE_USER_PREFERENCE) {
                // Retrieve credentials from user preferences
                if (this.userId > 0 && this.dbDriverManager != null) {
                    try {
                        UserHttpAuthPreferenceAccessDB prefDB = new UserHttpAuthPreferenceAccessDB(
                            this.dbDriverManager, this.logger
                        );
                        UserHttpAuthPreference pref = prefDB.getPreference(this.userId, receiver.getDBId());

                        if (pref != null) {
                            if (message.isMDN()) {
                                if (pref.isUseMdnAuth()) {
                                    authUsername = pref.getMdnUsername();
                                    authPassword = pref.getMdnPassword();
                                }
                            } else {
                                if (pref.isUseMessageAuth()) {
                                    authUsername = pref.getMessageUsername();
                                    authPassword = pref.getMessagePassword();
                                }
                            }
                        } else {
                            if (this.logger != null) {
                                this.logger.log(Level.WARNING,
                                    "HTTP auth user preference not found for userId=" + this.userId +
                                    ", partnerId=" + receiver.getDBId() + ". Sending without authentication.",
                                    message.getAS2Info());
                            }
                        }
                    } catch (Exception e) {
                        if (this.logger != null) {
                            this.logger.log(Level.SEVERE,
                                "Failed to retrieve HTTP auth user preference: " + e.getMessage(),
                                message.getAS2Info());
                        }
                    }
                } else {
                    if (this.logger != null) {
                        this.logger.log(Level.WARNING,
                            "HTTP auth user preference mode enabled but userId not set. Sending without authentication.",
                            message.getAS2Info());
                    }
                }
            }

            // Add Authorization header if credentials were resolved
            if (authUsername != null && authPassword != null) {
                filePost.addHeader("Authorization", this.generateBasicAuth(authUsername, authPassword));
            }
            filePost.addHeader("as2-version", "1.2");
            filePost.addHeader("ediint-features", ediintFeatures);
            filePost.addHeader("mime-version", "1.0");
            filePost.addHeader("recipient-address", receiptURL.toExternalForm());
            filePost.addHeader("message-id", "<" + message.getAS2Info().getMessageId() + ">");
            filePost.addHeader("as2-from", AS2Message.escapeFromToHeader(sender.getAS2Identification()));
            filePost.addHeader("as2-to", AS2Message.escapeFromToHeader(receiver.getAS2Identification()));
            String originalFilename = null;
            if (message.getPayloads() != null && !message.getPayloads().isEmpty()) {
                originalFilename = message.getPayloads().get(0).getOriginalFilename();
            }
            if (originalFilename != null) {
                String subject = this.replaceSubject(message.getAS2Info().getSubject(), originalFilename);
                filePost.addHeader("subject", subject);
                //update the message infos subject with the actual content
                if (!message.isMDN()) {
                    ((AS2MessageInfo) message.getAS2Info()).setSubject(subject);
                    //refresh this in the database if it is requested
                    if (this.dbDriverManager != null) {
                        MessageAccessDB access = new MessageAccessDB(this.dbDriverManager);
                        access.updateSubject((AS2MessageInfo) message.getAS2Info());
                    }
                }
            } else {
                filePost.addHeader("subject", message.getAS2Info().getSubject());
            }
            filePost.addHeader("from", sender.getEmail());
            filePost.addHeader("connection", "close, TE");
            //the data header must be always in english locale else there would be special
            //french characters (e.g. 13 dec. 2011 16:28:56 CET) which is not allowed after 
            //RFC 4130           
            DateFormat format = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz", Locale.US);
            filePost.addHeader("date", format.format(new Date()));
            String contentType;
            if (message.getAS2Info().getEncryptionType() != AS2Message.ENCRYPTION_NONE) {
                contentType = "application/pkcs7-mime; smime-type=enveloped-data; name=smime.p7m";
            } else {
                contentType = message.getContentType();
            }
            filePost.addHeader("content-type", contentType);
            //MDN header, this is always the way for async MDNs
            if (message.isMDN()) {
                if (this.logger != null) {
                    this.logger.log(Level.INFO,
                            rb.getResourceString("sending.mdn.async",
                                    new Object[]{
                                        receiptURL
                                    }), message.getAS2Info());
                    //its a TLS connection and the system should trust all server certificates
                    if (receiptURL.getProtocol().equalsIgnoreCase("https")) {
                        if (connectionParameter.getTrustAllRemoteServerCertificates()) {
                            this.logger.log(Level.INFO,
                                    rb.getResourceString("trust.all.server.certificates"),
                                    message.getAS2Info());
                        }
                        if (connectionParameter.getStrictHostCheck()) {
                            this.logger.log(Level.INFO,
                                    rb.getResourceString("strict.hostname.check"),
                                    message.getAS2Info());
                        }
                    }
                }
                filePost.addHeader("server", message.getAS2Info().getUserAgent());
            } else {
                AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
                //outbound AS2/CEM message
                if (messageInfo.requestsSyncMDN()) {
                    if (this.logger != null) {
                        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
                            this.logger.log(Level.INFO,
                                    rb.getResourceString("sending.cem.sync",
                                            new Object[]{
                                                receiver.getURL()
                                            }), messageInfo);
                        } else if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_AS2) {
                            this.logger.log(Level.INFO,
                                    rb.getResourceString("sending.msg.sync",
                                            new Object[]{
                                                receiver.getURL()
                                            }), messageInfo);
                        }
                        //its a TLS connection and the system should trust all server certificates
                        if (receiptURL.getProtocol().equalsIgnoreCase("https")) {
                            if (connectionParameter.getTrustAllRemoteServerCertificates()) {
                                this.logger.log(Level.INFO,
                                        rb.getResourceString("trust.all.server.certificates"),
                                        messageInfo);
                            }
                            if (connectionParameter.getStrictHostCheck()) {
                                this.logger.log(Level.INFO,
                                        rb.getResourceString("strict.hostname.check"),
                                        messageInfo);
                            }
                        }
                    }
                } else {
                    //Message with ASYNC MDN request
                    if (this.logger != null) {
                        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
                            this.logger.log(Level.INFO,
                                    rb.getResourceString("sending.cem.async",
                                            new Object[]{
                                                receiver.getURL(),
                                                sender.getMdnURL()
                                            }), messageInfo);
                        } else if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_AS2) {
                            this.logger.log(Level.INFO,
                                    rb.getResourceString("sending.msg.async",
                                            new Object[]{
                                                receiver.getURL(),
                                                sender.getMdnURL()
                                            }), messageInfo);
                        }
                        //its a TLS connection and the system should trust all server certificates
                        if (receiptURL.getProtocol().equalsIgnoreCase("https")) {
                            if (connectionParameter.getTrustAllRemoteServerCertificates()) {
                                this.logger.log(Level.INFO,
                                        rb.getResourceString("trust.all.server.certificates"),
                                        messageInfo);
                            }
                            if (connectionParameter.getStrictHostCheck()) {
                                this.logger.log(Level.INFO,
                                        rb.getResourceString("strict.hostname.check"),
                                        messageInfo);
                            }
                        }
                    }
                    //The following header indicates that this requests an asnc MDN.
                    //When the header "receipt-delivery-option" is present,
                    //the header "disposition-notification-to" serves as a request
                    //for an asynchronous MDN.
                    //The header "receipt-delivery-option" must always be accompanied by
                    //the header "disposition-notification-to".
                    //When the header "receipt-delivery-option" is not present and the header
                    //"disposition-notification-to" is present, the header "disposition-notification-to"
                    //serves as a request for a synchronous MDN.
                    filePost.addHeader("receipt-delivery-option", sender.getMdnURL());
                }
                filePost.addHeader("disposition-notification-to", sender.getMdnURL());
                //request a signed MDN if this is set up in the partner configuration
                if (receiver.isSignedMDN()) {
                    filePost.addHeader("disposition-notification-options",
                            messageInfo.getDispositionNotificationOptions().getHeaderValue());
                }
                if (messageInfo.getSignType() != AS2Message.SIGNATURE_NONE) {
                    filePost.addHeader("content-disposition", "attachment; filename=\"smime.p7m\"");
                } else if (messageInfo.getSignType() == AS2Message.SIGNATURE_NONE && message.getAS2Info().getSignType() == AS2Message.ENCRYPTION_NONE) {
                    //RFC 822 mail headers must contain only US-ASCII characters. Headers that contain non US-ASCII 
                    //characters must be encoded so that they contain only US-ASCII characters. Basically, 
                    //this process involves using either BASE64 or QP to encode certain characters. 
                    //RFC 2047 describes this in detail. 
                    //test if an encoding is required
                    try {
                        String filename = message.getPayload(0).getOriginalFilename();
                        boolean filenameEncodingRequired = !MimeUtility.encodeText(filename).equals(filename);
                        if (!filenameEncodingRequired) {
                            filePost.addHeader("content-disposition", "attachment; filename=\"" + filename + "\"");
                        } else {
                            filePost.addHeader("content-disposition", "attachment; filename=\""
                                    + MimeUtility.encodeText(filename,
                                            StandardCharsets.UTF_8.displayName(), "B")
                                    + "\"");
                        }
                    } catch (UnsupportedEncodingException willnothappen) {
                        //NOP
                    }
                }
            }
            int port = receiptURL.getPort();
            if (port == -1) {
                port = receiptURL.getDefaultPort();
            }
            filePost.addHeader("host", receiptURL.getHost() + ":" + port);
            filePost.addHeader("user-agent", connectionParameter.getUserAgent());
            byte[] transferData = message.getRawData();
            //using a ByteArrayEntity because this is repeatable
            ByteArrayEntity postEntity = new ByteArrayEntity(transferData, ContentType.parse(contentType));
            filePost.setEntity(postEntity);
            //setup oauth2 header
            if (message.isMDN()) {
                this.setOAuth2Header(filePost, receiver.usesOAuth2MDN(), receiver.getOAuth2MDN());
            } else {
                this.setOAuth2Header(filePost, receiver.usesOAuth2Message(), receiver.getOAuth2Message());
            }
            this.updateUploadHTTPHeaderWithUserDefinedHeaders(filePost, receiver);
            //Execute request
            try (CloseableHttpResponse httpResponse = httpClient.execute(filePost)) {
                if (httpResponse != null) {
                    this.responseData = this.readEntityData(httpResponse);
                    this.responseStatusCode = httpResponse.getCode();
                    this.responseReasonPhrase = httpResponse.getReasonPhrase();
                    statusCode = this.responseStatusCode;
                    this.responseHeader = httpResponse.getHeaders();
                }
            }
            for (Header singleHeader : filePost.getHeaders()) {
                if (singleHeader.getValue() != null) {
                    this.requestHeader.setProperty(singleHeader.getName(), singleHeader.getValue());
                }
            }
            //accept all 2xx answers
            //SC_ACCEPTED Status code (202) indicating that a request was accepted for processing, but was not completed.
            //SC_CREATED  Status code (201) indicating the request succeeded and created a new resource on the server.
            //SC_NO_CONTENT Status code (204) indicating that the request succeeded but that there was no new information to return.
            //SC_NON_AUTHORITATIVE_INFORMATION Status code (203) indicating that the meta information presented by the client did not originate from the server.
            //SC_OK Status code (200) indicating the request succeeded normally.
            //SC_RESET_CONTENT Status code (205) indicating that the agent SHOULD reset the document view which caused the request to be sent.
            //SC_PARTIAL_CONTENT Status code (206) indicating that the server has fulfilled the partial GET request for the resource.
            if (statusCode != HttpServletResponse.SC_OK
                    && statusCode != HttpServletResponse.SC_ACCEPTED
                    && statusCode != HttpServletResponse.SC_CREATED
                    && statusCode != HttpServletResponse.SC_NO_CONTENT
                    && statusCode != HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION
                    && statusCode != HttpServletResponse.SC_RESET_CONTENT
                    && statusCode != HttpServletResponse.SC_PARTIAL_CONTENT) {
                if (this.logger != null) {
                    this.logger.log(Level.SEVERE,
                            rb.getResourceString("error.httpupload",
                                    new Object[]{
                                        String.valueOf(statusCode) + " "
                                        + URLDecoder.decode(this.responseReasonPhrase == null ? ""
                                                : this.responseReasonPhrase, StandardCharsets.UTF_8)
                                    }), message.getAS2Info());
                }
                //store the sent data and assign the payload to the message - it should be available even if the 
                //message has been rejected
                if (this.dbDriverManager != null) {
                    MessageStoreHandler messageStoreHandler = new MessageStoreHandler(this.dbDriverManager);
                    messageStoreHandler.storeSentMessage(message, sender, receiver, this.getRequestHeader());
                }
            }
        } catch (Exception ex) {
            if (this.logger != null) {
                StringBuilder errorMessage = new StringBuilder();
                errorMessage.append("MessageHTTPUploader.performUpload: [");
                errorMessage.append(ex.getClass().getSimpleName());
                errorMessage.append("]");
                if (ex.getMessage() != null) {
                    errorMessage.append(": ").append(ex.getMessage());
                }
                //missing certificate on one of the sides
                if (ex instanceof SSLPeerUnverifiedException) {
                    if (ex.getCause() != null) {
                        Throwable causeEx = ex.getCause();
                        errorMessage.append("[");
                        errorMessage.append(causeEx.getClass().getSimpleName());
                        if (causeEx.getMessage() != null) {
                            errorMessage.append("/").append(causeEx.getMessage());
                        }
                        errorMessage.append("]");
                    }
                    SystemEventManagerImplAS2.instance().newEventConnectionProblem(receiver, message.getAS2Info(),
                            errorMessage.toString(), rb.getResourceString("hint.SSLPeerUnverifiedException"));
                    errorMessage.append("\n").append(rb.getResourceString("hint.SSLPeerUnverifiedException"));
                }
                //Remote server does not answer or is not reachable, java.net exception. Same reason for both expections
                //no idea why one of them is thrown sometimes instead of the other. 
                //Perhaps it depends on the java version or the apache client version.
                if (ex instanceof ConnectTimeoutException || ex instanceof ConnectException) {
                    if (ex.getCause() != null) {
                        Throwable causeEx = ex.getCause();
                        errorMessage.append("[");
                        errorMessage.append(causeEx.getClass().getSimpleName());
                        if (causeEx.getMessage() != null) {
                            errorMessage.append("/").append(causeEx.getMessage());
                        }
                        errorMessage.append("]");
                    }
                    SystemEventManagerImplAS2.instance().newEventConnectionProblem(receiver, message.getAS2Info(),
                            errorMessage.toString(), rb.getResourceString("hint.ConnectTimeoutException"));
                    errorMessage.append("\n").append(rb.getResourceString("hint.ConnectTimeoutException"));
                }

                //any other generic SSL problem - no idea why both may be thrown
                if (ex instanceof SSLException || ex instanceof IOException) {
                    if (ex.getCause() != null) {
                        Throwable causeEx = ex.getCause();
                        errorMessage.append("[");
                        errorMessage.append(causeEx.getClass().getSimpleName());
                        if (causeEx.getMessage() != null) {
                            errorMessage.append("/").append(causeEx.getMessage());
                        }
                        errorMessage.append("]");
                    }
                    SystemEventManagerImplAS2.instance().newEventConnectionProblem(receiver, message.getAS2Info(),
                            errorMessage.toString(), rb.getResourceString("hint.SSLException"));
                    errorMessage.append("\n").append(rb.getResourceString("hint.SSLException"));
                }
                this.logger.log(Level.SEVERE, errorMessage.toString(), message.getAS2Info());
            }
        } finally {
            if (httpClient != null) {
                //shutdown the HTTPClient to release the resources
                try {
                    httpClient.close();
                } catch (IOException e) {
                    //nop
                }
            }
        }
        return (statusCode);
    }

    private String generateBasicAuth(String username, String password) {
        return ("Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
    }

    private SSLConnectionSocketFactory generateSSLFactory(HttpConnectionParameter connectionParameter,
            AS2Info as2Info) throws Exception {

        //TLS key stores not set so far: take the trust store from the system
        if (this.certStore == null) {
            this.trustStore = new KeystoreStorageImplDB(
                    SystemEventManagerImplAS2.instance(),
                    this.dbDriverManager,
                    KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                    KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS
            );
            this.certStore = this.trustStore;
        }
        SSLContext sslcontext;
        // TrustStrategy for trusting self-signed certificates
        TrustStrategy trustSelfSignedStrategy = (chain, authType) -> true;

        if (connectionParameter.getTrustAllRemoteServerCertificates()) {
            SSLContextBuilder builder = SSLContexts.custom()
                    .loadTrustMaterial(this.trustStore.getKeystore(), trustSelfSignedStrategy);
            sslcontext = builder.build();
        } else {
            sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(this.trustStore.getKeystore(), trustSelfSignedStrategy)
                    .loadKeyMaterial(this.certStore.getKeystore(), this.certStore.getKeystorePass())
                    .build();
        }
        // Allowed SSL/TLS protocols as client
        String[] allowedProtocols
                = new String[]{
                    "SSLv3",
                    "TLSv1",
                    "TLSv1.1",
                    "TLSv1.2",
                    "TLSv1.3"};
        SSLConnectionSocketFactory sslConnectionFactory;
        if (!connectionParameter.getStrictHostCheck()) {
            sslConnectionFactory = new SSLConnectionSocketFactory(sslcontext,
                    allowedProtocols,
                    null,
                    //this is the AllowAllHostnameVerifier, another verifier is the StrictHostnameVerifier
                    new NoopHostnameVerifier());
        } else {
            sslConnectionFactory = new SSLConnectionSocketFactory(sslcontext,
                    allowedProtocols,
                    null,
                    //this is the StrictHostnameVerifier with self signed exception
                    new DefaultHostnameVerifierTrustedOnly(as2Info));
        }
        return (sslConnectionFactory);
    }

    /**
     * Replaces the subject set for the partner. The encoding must be
     * ISO-8859-1, only printable characters, CR, LF and TAB are replaced
     */
    private String replaceSubject(String definedSubject, String originalFilename) {
        String subjectStr = this.replace(definedSubject, "${filename}", originalFilename);
        StringBuilder subjectBuilder = new StringBuilder();
        for (int i = 0; i < subjectStr.length(); i++) {
            char testChar = subjectStr.charAt(i);
            if (testChar == '\n') {
                subjectBuilder.append("\\").append("n");
            } else if (testChar == '\r') {
                subjectBuilder.append("\\").append("r");
            } else if (testChar == '\t') {
                subjectBuilder.append("\\").append("t");
            } else {
                subjectBuilder.append(testChar);
            }
        }
        return (subjectBuilder.toString());
    }

    /**
     * Generates a authorization header for OAuth2 if this is required
     */
    private void setOAuth2Header(HttpPost post, boolean useOAuth2, OAuth2Config config) {
        if (useOAuth2 && config != null) {
            post.setHeader("Authorization", "Bearer " + config.getAccessTokenStr());
        }
    }

    /**
     * Updates the passed post HTTP headers with the headers defined for the
     * receiver
     */
    private void updateUploadHTTPHeaderWithUserDefinedHeaders(HttpPost post, Partner receiver) {
        List<String> usedHeaderKeys = new ArrayList<String>();
        for (Header singleHeader : post.getHeaders()) {
            PartnerHttpHeader headerReplacement = receiver.getHttpHeader(singleHeader.getName());
            if (headerReplacement != null) {
                //a value to replace is set
                if (headerReplacement.getValue() != null && !headerReplacement.getValue().isEmpty()) {
                    post.setHeader(singleHeader.getName(), headerReplacement.getValue());
                } else {
                    //no value to replace is set: delete the header
                    post.removeHeader(singleHeader);
                }
                usedHeaderKeys.add(singleHeader.getName());
            }
        }
        //add additional user defined headers
        List<PartnerHttpHeader> additionalHeaders = receiver.getAllNonListedHttpHeader(usedHeaderKeys);
        for (PartnerHttpHeader additionalHeader : additionalHeaders) {
            //add the header if a value is set
            if (additionalHeader.getValue() != null && !additionalHeader.getValue().isEmpty()) {
                post.setHeader(additionalHeader.getKey(), additionalHeader.getValue());
            }
        }
    }

    /**
     * Replaces the string tag by the string replacement in the sourceString
     *
     * @param source Source string
     * @param tag	String that will be replaced
     * @param replacement String that will replace the tag
     * @return String that contains the replaced values
     */
    private String replace(String source, String tag, String replacement) {
        if (source == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        while (true) {
            int index = source.indexOf(tag);
            if (index == -1) {
                buffer.append(source);
                return (buffer.toString());
            }
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + tag.length());
        }
    }

    /**
     * Returns the version of this class
     */
    public static String getVersion() {
        String revision = "$Revision: 224 $";
        return (revision.substring(revision.indexOf(":") + 1,
                revision.lastIndexOf("$")).trim());
    }

    /**
     * Returns the response data as byte array
     */
    public byte[] getResponseData() {
        return (this.responseData);
    }

    /**
     * Reads the data of a HTTP response entity
     */
    public byte[] readEntityData(ClassicHttpResponse httpResponse) throws Exception {
        if (httpResponse == null) {
            return (null);
        }
        if (httpResponse.getEntity() == null) {
            return (null);
        }
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            httpResponse.getEntity().writeTo(outStream);
            outStream.flush();
            return (outStream.toByteArray());
        }
    }

    /**
     * Returns the array of response headers after the upload process has been
     * performed
     *
     * @return the responseHeader
     */
    public Header[] getResponseHeader() {
        if (this.responseHeader == null) {
            return (new Header[0]);
        }
        return (this.responseHeader);
    }

    /**
     * @return the requestHeader, this is is only valid if the send process has
     * been already performed
     */
    public Properties getRequestHeader() {
        return requestHeader;
    }

    private class DefaultHostnameVerifierTrustedOnly implements HostnameVerifier {

        private final AS2Info as2Info;

        public DefaultHostnameVerifierTrustedOnly(AS2Info as2Info) {
            this.as2Info = as2Info;
        }

        @Override
        public boolean verify(String hostname, SSLSession session) {
            try {
                Certificate[] certificates = session.getPeerCertificates();
                if (certificates != null && certificates.length > 0) {
                    X509Certificate certificate = (X509Certificate) certificates[0];
                    if (this.isSelfSigned(certificate)) {
                        if (logger != null) {
                            logger.log(Level.INFO,
                                    rb.getResourceString("strict.hostname.check.skipped.selfsigned"),
                                    this.as2Info);
                        }
                        return (true);
                    }
                }
            } catch (Exception e) {
            }
            return (new DefaultHostnameVerifier().verify(hostname, session));
        }

        /**
         * Checks if the passed certificate is self signed. A certificate is
         * self-signed if the subject and issuer match
         */
        private boolean isSelfSigned(X509Certificate certificate) {
            try {
                X500Principal issuer = certificate.getIssuerX500Principal();
                X500Principal subject = certificate.getSubjectX500Principal();
                certificate.verify(certificate.getPublicKey());
                return issuer.equals(subject);
            } catch (Exception e) {
                return false;
            }
        }

    }

}
