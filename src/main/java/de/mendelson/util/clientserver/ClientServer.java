//$Header: /as2/de/mendelson/util/clientserver/ClientServer.java 46    11/02/25 13:39 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.codec.ClientServerCodecFactory;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.UnorderedThreadPoolExecutor;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Server root for the mendelson client/server architecture
 *
 * @author S.Heller
 * @version $Revision: 46 $
 */
public class ClientServer {

    private long startTime = 0;
    private final Logger logger;
    private ClientServerSessionHandler sessionHandler = null;
    private int port;
    private String productName = "";
    public static final String[] SERVERSIDE_ACCEPTED_TLS_PROTOCOLS
            = new String[]{"TLSv1.2"};
    private final MecResourceBundle rb;
    private final ClientServerTLS clientserverTLS;
    //core pool size = 4
    //max pool size = 16
    private final UnorderedThreadPoolExecutor THREAD_POOL_EXECUTOR
            = new UnorderedThreadPoolExecutor(4, 16, 30, TimeUnit.SECONDS, 
            new NamedThreadFactory("client-server-serverside-exec"));

    /**
     * Creates a new instance of Server
     */
    public ClientServer(Logger logger, int port, ClientServerTLS clientserverTLS) {
        this.port = port;
        this.logger = logger;
        this.clientserverTLS = clientserverTLS;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleClientServer.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }

    public void setSessionHandler(ClientServerSessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    public void setClientServerPort(int port) {
        this.port = port;
    }

    /**
     * Returns the start time of the server
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sends a message object to all connected clients
     */
    public void broadcastToClients(ClientServerMessage message) {
        if (this.sessionHandler != null) {
            sessionHandler.broadcast(message);
        }
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Finally starts the server
     */
    public void start() throws Exception {
        this.logger.log(Level.INFO, this.rb.getResourceString("clientserver.start",
                new Object[]{
                    this.productName,
                    String.valueOf(this.port)
                }));
        if (this.sessionHandler != null) {
            this.sessionHandler.setProductName(this.productName);
        } else {
            this.logger.log(Level.WARNING, "No session handler assigned to the client server!");
        }
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        //add SSL support
        SslFilter tlsFilter = new SslFilter(this.clientserverTLS.createSSLContext());
        //If client authentication is disabled the client certificate must not be in the servers keystore
        tlsFilter.setNeedClientAuth(false);
        // new mina version does not need this
        // tlsFilter.setUseNonBlockingPipeline(true);
        //allow defined TLS protocols only for the client-server connection
        tlsFilter.setEnabledProtocols(SERVERSIDE_ACCEPTED_TLS_PROTOCOLS);
        acceptor.getFilterChain().addFirst("TLS", tlsFilter);
        //add CPU bound tasks first
        acceptor.getFilterChain().addLast("protocol",
                new ProtocolCodecFilter(new ClientServerCodecFactory(null)));
        //see https://issues.apache.org/jira/browse/DIRMINA-682?page=com.atlassian.jira.plugin.system.issuetabpanels:all-tabpanel
        //..and now set up the thread pool
        acceptor.getFilterChain().addLast("executor", new ExecutorFilter(this.THREAD_POOL_EXECUTOR));
        if (this.sessionHandler != null) {
            acceptor.setHandler(this.sessionHandler);
        }
        //finally bind the protocol handler to the port
        acceptor.bind(new InetSocketAddress(this.port));
        this.logger.log(Level.INFO, this.rb.getResourceString("clientserver.started", this.productName));
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Returns the current sessions on this server
     */
    public List<IoSession> getSessions() {
        if (this.sessionHandler != null) {
            return (this.sessionHandler.getSessions());
        } else {
            List<IoSession> emptyList = new ArrayList<IoSession>();
            return (Collections.unmodifiableList(emptyList));
        }
    }

}
