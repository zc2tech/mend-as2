package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.QuitRequest;
import de.mendelson.util.clientserver.messages.ServerInfo;
import de.mendelson.util.clientserver.messages.ServerLogMessage;
import de.mendelson.util.systemevents.SystemEventManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.filter.FilterEvent;
import org.apache.mina.filter.ssl.SslEvent;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Session handler for the server implementation
 *
 * @author S.Heller
 * @version $Revision: 54 $
 */
public class ClientServerSessionHandler extends IoHandlerAdapter {

    public static final String SESSION_ATTRIB_USER = "user";
    public static final String SESSION_ATTRIB_CLIENT_PID = "pid";
    public static final String SESSION_ATTRIB_CLIENT_IP = "ip";
    public static final String SESSION_ATTRIB_CLIENT_TYPE = "clienttype";

    private Logger logger = Logger.getAnonymousLogger();
    /**
     * Synchronized structure to perform user defined processing on the server
     * depending on the incoming message object type
     */
    private final List<ClientServerProcessing> processingList = Collections.synchronizedList(new ArrayList<ClientServerProcessing>());
    /**
     * Stores the product name, this is displayed on login requests
     */
    private String productName = "";
    /**
     * Stores all sessions
     */
    private final List<IoSession> sessions = Collections.synchronizedList(new ArrayList<IoSession>());
    /**
     * Allows to access the server for special messages without a required login
     */
    private AnonymousProcessing anonymousProcessing = null;
    private ClientServerSessionHandlerCallback callback = null;
    private final int maxClients;
    private final SystemEventManager eventManager;

    public ClientServerSessionHandler(Logger logger, String[] validClientIds, int maxClients, SystemEventManager eventManager) {
        if (logger != null) {
            this.logger = logger;
        }
        this.eventManager = eventManager;
        this.maxClients = maxClients;
    }

    public void setCallback(ClientServerSessionHandlerCallback callback) {
        this.callback = callback;
    }

    /**
     * Get all available sessions
     */
    public List<IoSession> getSessions() {
        List<IoSession> sessionList = new ArrayList<IoSession>();
        synchronized (this.sessions) {
            sessionList.addAll(this.sessions);
        }
        return (Collections.unmodifiableList(sessionList));
    }

    /**
     * Allows to process messages without login, e.g. server state
     */
    public void setAnonymousProcessing(AnonymousProcessing anonymousProcessing) {
        this.anonymousProcessing = anonymousProcessing;
    }

    /**
     * Logs something to the clients log - but only if the level is higher than
     * the defined loglevelThreshold
     */
    public void log(Level logLevel, String message) {
        this.logger.log(logLevel, message);
    }

    private void throwEventLogoff(IoSession session, String message) {
        try {
            String remoteProcessId = (String) session.getAttribute(SESSION_ATTRIB_CLIENT_PID);
            String userName = (String) session.getAttribute(SESSION_ATTRIB_USER);
            int clientType = BaseClient.CLIENT_UNSPECIFIED;
            if (session.containsAttribute(SESSION_ATTRIB_CLIENT_TYPE)) {
                clientType = ((Integer) session.getAttribute(SESSION_ATTRIB_CLIENT_TYPE)).intValue();
            }
            //this is tricky - if the session is closed it has no longer a remote IP - that is why it is stored
            //as session parameter
            String clientIP = (String) session.getAttribute(SESSION_ATTRIB_CLIENT_IP);
            this.eventManager.newEventClientLogoff(clientIP, userName, remoteProcessId,
                    String.valueOf(session.getId()), message, clientType);
        } catch (Exception e) {
            System.out.println("ClientServerSessionHandler.logoff: ["
                    + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
    }

    private void throwEventExceptionInClientServerProcess(IoSession session, String message) {
        try {
            String remoteProcessId = (String) session.getAttribute(SESSION_ATTRIB_CLIENT_PID);
            String userName = (String) session.getAttribute(SESSION_ATTRIB_USER);
            //this is tricky - if the session is closed it has no longer a remote IP - that is why it is stored
            //as session parameter
            String clientIP = (String) session.getAttribute(SESSION_ATTRIB_CLIENT_IP);
            this.eventManager.newEventExceptionInClientServerProcess(clientIP, userName, remoteProcessId,
                    String.valueOf(session.getId()), message);
        } catch (Exception e) {
            System.out.println("ClientServerSessionHandler.clientserver: ["
                    + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
    }

    @Override
    /**
     * The session has been opened: send a server info object This is an
     * incoming connection from a client
     */
    public void sessionOpened(IoSession session) {
        //store immediatly the remote IP address in the session - if the session is closed it is no longer
        //available and it might be required later even if the session goes into the closed state
        session.setAttribute(SESSION_ATTRIB_CLIENT_IP, session.getRemoteAddress().toString());
    }

    /**
     * Incoming message on the server site
     */
    @Override
    public void messageReceived(IoSession session, Object messageObj) {
        if (!(messageObj instanceof ClientServerMessage)) {
            return;
        }
        ClientServerMessage message = (ClientServerMessage) messageObj;
        if (message instanceof QuitRequest) {
            session.closeOnFlush();
            return;
        }
        if (this.anonymousProcessing != null && this.anonymousProcessing.processMessageWithoutLogin(session, message)) {
            this.performUserDefinedProcessing(session, message);
        } else {
            // SwingUI clients no longer require authentication
            // Add session tracking for monitoring purposes
            boolean sessionTracked = session.containsAttribute(SESSION_ATTRIB_USER);
            if (!sessionTracked) {
                // Track session with a generic identifier
                session.setAttribute(SESSION_ATTRIB_USER, "swing_client");
                session.setAttribute(SESSION_ATTRIB_CLIENT_TYPE, Integer.valueOf(1)); // RICH_CLIENT type

                // Add session to the list
                synchronized (this.sessions) {
                    // Check max clients limit
                    if (this.maxClients > 0 && this.sessions.size() >= this.maxClients) {
                        this.log(Level.WARNING, "Maximum number of clients reached, rejecting connection");
                        session.closeOnFlush();
                        return;
                    }
                    this.sessions.add(session);
                }
            }

            //here starts the user defined processing to extend the server functionality
            this.performUserDefinedProcessing(session, message);
        }
    }

    /**
     * User defined extensions for the server processing
     */
    private void performUserDefinedProcessing(IoSession session, ClientServerMessage message) {
        boolean processed = false;
        synchronized (this.processingList) {
            for (int i = 0; i < this.processingList.size(); i++) {
                processed |= this.processingList.get(i).process(session, message);
            }
        }
        if (!processed) {
            this.log(Level.WARNING, "performUserDefinedProcessing: inbound message of class ["
                    + message.getClass().getName() + "] has not been processed.");
        }
    }

    /**
     * User defined actions for messages sent by any client. The user may extend
     * the framework by implementing a ServerProcessing interface
     */
    public void addServerProcessing(ClientServerProcessing serverProcessing) {
        synchronized (this.processingList) {
            this.processingList.add(serverProcessing);
        }
    }

    /**
     * Sends a message object to all connected clients
     */
    public void broadcast(Object data) {
        synchronized (this.sessions) {
            for (IoSession session : this.sessions) {
                if (session.isConnected()) {
                    session.write(data);
                }
            }
        }
    }

    /**
     * Sends a log message to all connected clients
     */
    public void broadcastLogMessage(Level level, String message, Object[] parameter) {
        ServerLogMessage serverMessage = new ServerLogMessage();
        serverMessage.setLevel(level);
        serverMessage.setMessage(message);
        serverMessage.setParameter(parameter);
        this.broadcast(serverMessage);
    }

    /**
     * Sends a log message to all connected clients
     */
    public void broadcastLogMessage(Level level, String message) {
        this.broadcastLogMessage(level, message, null);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        String user = (String) session.getAttribute(SESSION_ATTRIB_USER);
        if (user != null) {
            synchronized (this.sessions) {
                this.sessions.remove(session);
            }
            this.throwEventLogoff(session, "");
            if (this.callback != null) {
                this.callback.clientDisconnected(session);
            }
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        this.throwEventLogoff(session, "");
        // disconnect an idle client
        session.closeOnFlush();
        if (this.callback != null) {
            this.callback.clientDisconnected(session);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        //The method org.apache.mina.core.polling.AbstractPollingIoProcessor  seems to have a problem
        //in the method clearWriteRequestQueue(AbstractPollingIoProcessor.java:1200) in MINA 2.2.1 - this will be ignored here
        if (cause instanceof WriteToClosedSessionException) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Exception caught in client-server interface.").append("\n");
        builder.append("[" + cause.getClass().getSimpleName() + "]: " + cause.getMessage()).append("\n\n");
        StackTraceElement[] stackTrace = cause.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            builder.append(element.getClassName() + ": " + element.getMethodName() + "\n");
        }
        this.throwEventExceptionInClientServerProcess(session, builder.toString());
        // Close connection when unexpected exception is caught.
        session.closeNow();
        if (this.callback != null) {
            this.callback.clientDisconnected(session);
        }
    }

    public int getConnectedClients() {
        synchronized (this.sessions) {
            return (this.sessions.size());
        }
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Wait for the TLS handshake to be complete - then send the serverinfo
     * Login is no longer required for client-server protocol
     * @param session
     * @param event
     * @throws Exception
     */
    @Override
    public void event(IoSession session, FilterEvent event) throws Exception {
        if (event == SslEvent.SECURED) {
            ServerInfo info = new ServerInfo();
            info.setProductname(this.productName);
            session.write(info);
            // No longer sending LoginRequired - clients connect without authentication
        }
    }

}
