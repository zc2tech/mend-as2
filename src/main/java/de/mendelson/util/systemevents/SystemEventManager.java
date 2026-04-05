package de.mendelson.util.systemevents;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Performs the notification for an event
 *
 * @author S.Heller
 * @version $Revision: 32 $
 */
public abstract class SystemEventManager {

    private static final DateTimeFormatter EVENT_FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("HH-mm-ss-SSS");
    private static final DateTimeFormatter DAILY_SUBDIR_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final MecResourceBundle rb;
    protected static final String MODULE_NAME;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleSystemEventManager.class.getName());
            MODULE_NAME = rb.getResourceString("module.name");
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    private static final String HOST_NAME;

    static {
        String tempHostName;
        try {
            tempHostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            tempHostName = "Unknown";
        }
        HOST_NAME = tempHostName;
    }

    protected SystemEventManager() {
    }

    public String getHostname() {
        return (HOST_NAME);
    }

    public abstract Path getStorageMainDir();

    /**
     * Stores the system event to a file - to be browsed later
     */
    protected void storeEventToFile(SystemEvent event) throws Exception {
        Path storageDir = Paths.get(this.getStorageMainDir().toString(),
                LocalDateTime.now().format(DAILY_SUBDIR_FORMAT),
                "events");
        String storageFilePrefix
                = LocalDateTime.now().format(EVENT_FILE_DATE_FORMAT)
                + "_" + event.severityToFilename()
                + "_" + event.originToFilename()
                + "_" + event.typeToFilename()
                + "_";
        String storageFileSuffix = ".event";
        event.store(storageDir, storageFilePrefix, storageFileSuffix);
    }

    /**
     * Throws a new system event that a login was successful
     * These methods are kept for WebUI JWT authentication events
     * @deprecated SwingUI no longer uses these methods
     */
    @Deprecated
    public void newEventClientLoginSuccess(String username, SocketAddress remoteAddress, String sessionId,
            String clientInfo, String tlsProtocol, String tlsCipherSuite) {
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO, SystemEvent.ORIGIN_USER,
                SystemEvent.TYPE_CLIENT_LOGIN_SUCCESS);
        StringBuilder builder = new StringBuilder();
        builder.append(rb.getResourceString("label.body.tlsprotocol",
                (tlsProtocol == null ? "--" : tlsProtocol))).append("\n")
                .append(rb.getResourceString("label.body.tlsciphersuite",
                        (tlsCipherSuite == null ? "--" : tlsCipherSuite))).append("\n")
                .append(rb.getResourceString("label.body.clientip",
                        remoteAddress.toString())).append("\n")
                .append(rb.getResourceString("label.body.details",
                        clientInfo)).append("\n");
        event.setBody(builder.toString());
        String subject = rb.getResourceString("label.subject.login.success", username);
        event.setSubject(subject);
        try {
            this.storeEventToFile(event);
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Throws a new system event that a login has failed
     * @deprecated SwingUI no longer uses these methods
     */
    @Deprecated
    public void newEventClientLoginFailure(String username, SocketAddress remoteAddress, String sessionId,
            String clientInfo, String failureReason) {
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_WARNING, SystemEvent.ORIGIN_USER,
                SystemEvent.TYPE_CLIENT_LOGIN_FAILURE);
        StringBuilder builder = new StringBuilder();
        builder.append(rb.getResourceString("label.body.clientip",
                remoteAddress.toString())).append("\n")
                .append(rb.getResourceString("label.body.details",
                        failureReason)).append("\n");
        event.setBody(builder.toString());
        String subject = rb.getResourceString("label.subject.login.failed", username);
        event.setSubject(subject);
        try {
            this.storeEventToFile(event);
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Throws a new system event that a client has disconnected
     */
    public void newEventClientLogoff(String remoteIP, String userName, String processId, String sessionId,
            String message, int clientType) {
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO, SystemEvent.ORIGIN_USER,
                SystemEvent.TYPE_CLIENT_LOGOFF);
        StringBuilder builder = new StringBuilder();
        builder.append(rb.getResourceString("label.body.clientip", remoteIP)).append("\n");
        builder.append(rb.getResourceString("label.body.processid", processId)).append("\n");
        if (message != null && !message.trim().isEmpty()) {
            builder.append(rb.getResourceString("label.body.details", message)).append("\n");
        }
        event.setBody(builder.toString());
        String subject = rb.getResourceString("label.subject.logoff", userName);
        if (clientType != BaseClient.CLIENT_UNSPECIFIED) {
            subject = subject + " ("
                    + BaseClient.clientTypeToStr(clientType)
                    + ")";
        }
        event.setSubject(subject);
        try {
            this.storeEventToFile(event);
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Throws a new system event that a problem occurred in the client-server
     * interface
     */
    public void newEventExceptionInClientServerProcess(String remoteIP, String userName, String processId, String sessionId,
            String message) {
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_ERROR, SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_CLIENT_ANY);
        StringBuilder builder = new StringBuilder();
        builder.append(rb.getResourceString("label.body.clientip", remoteIP)).append("\n");
        builder.append(rb.getResourceString("label.body.processid", processId)).append("\n\n");
        if (message != null && !message.trim().isEmpty()) {
            builder.append(rb.getResourceString("label.body.details", message)).append("\n");
        }
        event.setBody(builder.toString());
        event.setSubject(rb.getResourceString("label.error.clientserver"));
        try {
            this.storeEventToFile(event);
        } catch (Exception e) {
            return;
        }
    }

    /**
     * A problem occurred during a directory creation process
     */
    public void newEventExceptionInDirectoryCreation(Throwable exception, String directory) {
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_ERROR, SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_FILE_MKDIR);
        event.setSubject(rb.getResourceString("error.createdir.subject"));
        event.setBody(rb.getResourceString("error.createdir.body",
                new Object[]{
                    directory,
                    "[" + exception.getClass().getSimpleName() + "] " + exception.getMessage()}
        ));
        this.newEvent(event);
    }

    public abstract void systemFailure(Throwable exception, int eventType, PreparedStatement statement);

    public abstract void systemFailure(Throwable exception, int eventType);

    public abstract void systemFailure(Throwable exception);

    public abstract void newEvent(SystemEvent event);

    public abstract void newEvent(int severity, int origin, int type, String subject, String body);

}
