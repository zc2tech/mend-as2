
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.util.clientserver;

import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.codec.ClientServerCodecFactory;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.QuitRequest;
import de.mendelson.util.clientserver.user.User;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.UnorderedThreadPoolExecutor;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Abstract client for a user
 *
 * @author S.Heller
 * @version $Revision: 73 $
 */
public class BaseClient {

    public static final int CLIENT_UNSPECIFIED = 0;
    public static final int CLIENT_RICH_CLIENT = 1;
    public static final int CLIENT_REST = 2;
    public static final int CLIENT_XML = 3;
    public static final int CLIENT_SENDORDER = 4;
    public static final int CLIENT_WEBINTERFACE = 5;
    public static final int CLIENT_COMMANDLINE_SHUTDOWN = 6;
    public static final int CLIENT_WEB = 7;

    private int clientType = CLIENT_UNSPECIFIED;

    private Logger logger = Logger.getAnonymousLogger();
    private final ClientSessionHandler clientSessionHandler;
    private IoSession session = null;
    private final NioSocketConnector connector = new NioSocketConnector();
    private ExecutorFilter executorFilter = null;
    private final static UnorderedThreadPoolExecutor THREAD_POOL_EXECUTOR
            = new UnorderedThreadPoolExecutor(2, 16, 30, TimeUnit.SECONDS,
                    new NamedThreadFactory("client-server-clientside-exec"));

    /**
     * SSLContext is thread safe and could be used over all client connections
     */
    private final static SSLContext SSL_CONTEXT_CLIENT;

    static {
        try {
            X509TrustManager trustManagerTrustAll = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates,
                        String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates,
                        String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{trustManagerTrustAll}, null);
            SSL_CONTEXT_CLIENT = context;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set default timeout for sync requests to 30s
     */
    public static final long TIMEOUT_SYNC_RECEIVE = TimeUnit.SECONDS.toMillis(60);
    public static final long TIMEOUT_SYNC_SEND = TimeUnit.SECONDS.toMillis(60);

    /**
     * User that is connected
     */
    private User user = null;

    public BaseClient(ClientSessionHandlerCallback callback, final int CLIENT_TYPE) {
        this.clientType = CLIENT_TYPE;
        this.clientSessionHandler = new ClientSessionHandler(callback);
    }

    /**
     * Indicates if server log messages should be displayed in the client or
     * simply ignored
     */
    public void setDisplayServerLogMessages(boolean flag) {
        this.clientSessionHandler.setDisplayServerLogMessages(flag);
    }

    public boolean getDisplayServerLogMessages() {
        return (this.clientSessionHandler.getDisplayServerLogMessages());
    }

    /**
     * Logs something to the clients log
     */
    public void log(Level logLevel, String message) {
        this.logger.log(logLevel, message);
    }

    public void setLogger(Logger logger) {
        if (logger != null) {
            this.logger = logger;
        }
    }

    /**
     * Are client and server running n the same machine?
     */
    public boolean serverRunsLocal() {
        if (!this.isConnected()) {
            return (false);
        }
        InetAddress serviceAddress = ((InetSocketAddress) this.session.getServiceAddress()).getAddress();
        InetAddress localAddress = ((InetSocketAddress) this.session.getLocalAddress()).getAddress();
        return (serviceAddress.getHostAddress().equals(localAddress.getHostAddress()));
    }

    /**
     * Login method removed - SwingUI clients no longer require authentication
     * WebUI uses JWT tokens via REST API instead
     * @deprecated No longer used - authentication removed for client-server protocol
     */
    @Deprecated
    public Object login(String user, char[] passwd, String clientId) {
        // SwingUI clients connect without authentication
        // This method is kept for backward compatibility but does nothing
        return null;
    }

    /**
     * checks if the client is connected
     */
    public boolean isConnected() {
        return (this.session != null && this.session.isConnected());
    }

    /**
     * Checks if the client is connected
     * Login is no longer required, so this is equivalent to isConnected()
     * @deprecated Use isConnected() instead
     */
    @Deprecated
    public boolean isConnectedAndLoggedIn() {
        return isConnected();
    }

    public synchronized boolean connect(InetSocketAddress hostAddress, long timeout) {
        if (this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] BaseClient.connect: "
                    + "Already connected to " + hostAddress + ". Please disconnect first.");
        }
        try {
            this.connector.setConnectTimeoutMillis(timeout);
            this.connector.setHandler(this.clientSessionHandler);
            //add SSL support
            SslFilter sslFilter = new SslFilter(SSL_CONTEXT_CLIENT);
            sslFilter.setEnabledProtocols(ClientServer.SERVERSIDE_ACCEPTED_TLS_PROTOCOLS[0]);
            sslFilter.setNeedClientAuth(false);
            // new mina version does not need this
            // sslFilter.setUseNonBlockingPipeline(true);
            this.connector.getFilterChain().addFirst("TLS", sslFilter);
            //add CPU bound tasks first
            this.connector.getFilterChain().addLast("protocol", new ProtocolCodecFilter(
                    new ClientServerCodecFactory(this.clientSessionHandler.getCallback())));
            //multi threaded model: allow and receive simulanously            
            this.executorFilter = new ExecutorFilter(THREAD_POOL_EXECUTOR);
            this.connector.getFilterChain().addLast("executor", this.executorFilter);
            ConnectFuture connectFuture = null;
            boolean connected = false;
            try {
                connectFuture = this.connector.connect(hostAddress).awaitUninterruptibly();
            } finally {
                if (connectFuture != null) {
                    if (connectFuture.isConnected()) {
                        this.session = connectFuture.getSession();                        
                        connected = true;
                    } else {
                        this.connector.dispose();
                        connected = false;
                    }
                }
            }
            return (connected);
        } catch (Exception e) {
            this.log(Level.WARNING, "[Client-Server communication] "
                    + "BaseClient.connect: " + e.getMessage());
            return false;
        }
    }

    public void broadcast(ClientServerMessage message) {
        if (!this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] "
                    + "BaseClient.broadcast: Not connected to a server. Please connect first.");
        }
        this.session.write(message);
    }

    /**
     * Sends an async message to the server and does not care for an answer
     */
    public void sendAsync(ClientServerMessage message) {
        if (!this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] BaseClient.sendAsync: "
                    + "Not connected to a server. Please connect first.");
        }
        WriteFuture future = this.session.write(message);
    }

    /**
     * Sends a sync message and throws a timeout exception if the client does
     * not answer in a proper time
     *
     */
    public ClientServerResponse sendSync(ClientServerMessage request) {
        return (this.sendSync(request, TIMEOUT_SYNC_RECEIVE));
    }

    /**
     * Sends a sync message and waits infinite for an answer. A connection
     * timeout will occur if the request could not be sent to the server anyway.
     * Once the request has been sent this will wait for an answer without any
     * timeout.
     *
     */
    public ClientServerResponse sendSyncWaitInfinite(ClientServerMessage request) {
        if (!this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] BaseClient.sendSyncWaitInfinite: "
                    + "Not connected to a server. Please connect first.");
        }
        ClientServerResponse response = null;
        request._setSyncRequest(true);
        try {
            this.clientSessionHandler.addSyncRequest(request);
            WriteFuture writeFuture = this.session.write(request);
            boolean isSent = writeFuture.await(TIMEOUT_SYNC_SEND, TimeUnit.MILLISECONDS);
            if (!isSent) {
                StringBuilder message = new StringBuilder("[Client-Server communication]")
                        .append("(").append(request.toString()).append(") ");
                message.append("Send timeout - Could not send sync request to server after " + TIMEOUT_SYNC_SEND + "ms");
                throw new TimeoutException(message.toString());
            }
            if (writeFuture.getException() != null) {
                throw (writeFuture.getException());
            }
            response = this.clientSessionHandler.waitForSyncAnswerInfinite(this.session, request.getReferenceId());
        } catch (Throwable throwable) {
            this.clientSessionHandler.syncRequestFailed(request, response, throwable);
        } finally {
            //remove the request from the map
            this.clientSessionHandler.removeSyncRequest(request);
        }
        return (response);
    }

    /**
     * Sends a sync message and throws a timeout exception if the client does
     * not answer in a proper time. The passed timeout is not the connection
     * timeout (this is set to TIMEOUT_SYNC_SEND [in ms] by default - it is the
     * sync wait timeout)
     *
     */
    public ClientServerResponse sendSync(ClientServerMessage request, long timeout) {
        if (!this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] "
                    + "BaseClient.sendSync: Not connected to a server. Please connect first.");
        }
        ClientServerResponse response = null;
        request._setSyncRequest(true);
        try {
            this.clientSessionHandler.addSyncRequest(request);
            WriteFuture writeFuture = this.session.write(request);
            boolean isSent = writeFuture.await(TIMEOUT_SYNC_SEND, TimeUnit.MILLISECONDS);
            if (!isSent) {
                StringBuilder message = new StringBuilder("[Client-Server communication]")
                        .append("(").append(request.toString()).append(") ")
                        .append("Send timeout - Could not send sync request to server after " + TIMEOUT_SYNC_SEND + "ms");
                SyncRequestTimeoutException exception = new SyncRequestTimeoutException(
                        SyncRequestTimeoutException.TIMEOUT_TYPE_SEND,
                        message.toString(), request, TIMEOUT_SYNC_SEND);
                throw exception;
            }
            if (writeFuture.getException() != null) {
                throw (writeFuture.getException());
            }
            response = this.clientSessionHandler.waitForSyncAnswer(request.getReferenceId(), timeout);
            if (response == null) {
                StringBuilder message = new StringBuilder("[Client-Server communication]");
                message.append("(").append(request.toString()).append(") ")
                        .append("Receipt timeout - Could not receive the sync response after " + timeout + "ms [");
                message.append(request.getClass().getSimpleName()).append("]");
                SyncRequestTimeoutException exception = new SyncRequestTimeoutException(
                        SyncRequestTimeoutException.TIMEOUT_TYPE_RECEIVE,
                        message.toString(), request, timeout);
                throw exception;
            }
        } catch (Throwable throwable) {
            this.clientSessionHandler.syncRequestFailed(request, response, throwable);
        } finally {
            //remove the request from the map
            this.clientSessionHandler.removeSyncRequest(request);
        }
        return (response);
    }

    /**
     * Performs a logout, closes the session
     */
    public void logout() {
        if (this.session != null) {
            if (this.session.isConnected()) {
                if (this.user == null) {
                    this.log(Level.WARNING, "[Client-Server communication] Logout failed, you are not logged in so far!");
                } else {
                    QuitRequest quitRequest = new QuitRequest();
                    quitRequest.setUser(this.user.getName());
                    this.session.write(quitRequest);
                    this.user = null;
                }
            }
            this.session.close(false);
        }
    }

    /**
     * Disconnects the connector
     */
    public void disconnect() {
        try {
            if (this.executorFilter != null) {
                this.executorFilter.destroy();
            }
            if (this.connector != null && !this.connector.isDisposed()) {
                this.connector.dispose(true);
            }
        } catch (Exception e) {
            //nop
        }
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the current user or null if there is nobody logged in
     */
    public User getUser() {
        return (this.user);
    }

    /**
     * Returns the host address if the server is connected and the client is
     * logged in- else null
     *
     * @return null if the client is not connected or not logged in to the
     * server
     */
    public InetAddress getHostAddress() {
        if (this.isConnectedAndLoggedIn()) {
            InetAddress serviceAddress = ((InetSocketAddress) this.session.getServiceAddress()).getAddress();
            return (serviceAddress);
        } else {
            return (null);
        }
    }

    /**
     * @return the clientType
     */
    public int getClientType() {
        return clientType;
    }

    public static String clientTypeToStr(int clientType) {
        if (clientType == CLIENT_SENDORDER) {
            return ("COMMAND SEND");
        } else if (clientType == CLIENT_REST) {
            return ("REST");
        } else if (clientType == CLIENT_RICH_CLIENT) {
            return ("RICH CLIENT");
        } else if (clientType == CLIENT_WEBINTERFACE) {
            return ("WEB INTERFACE");
        } else if (clientType == CLIENT_XML) {
            return ("XML");
        } else if (clientType == CLIENT_COMMANDLINE_SHUTDOWN) {
            return ("COMMANDLINE SHUTDOWN");
        } else if (clientType == CLIENT_WEB) {
            return ("WEB");
        }
        return ("UNSPECIFIED");
    }

}
