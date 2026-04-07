package de.mendelson.util.clientserver;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.connectionprogress.JDialogConnectionProgress;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.LoginResponse;
import de.mendelson.util.clientserver.messages.ServerInfo;
import de.mendelson.util.clientserver.messages.ServerSideNotification;
import de.mendelson.util.clientserver.user.User;
import java.awt.Color;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * GUI Client root implementation
 *
 * @author S.Heller
 * @version $Revision: 46 $
 */
public abstract class GUIClient extends JFrame implements ClientSessionHandlerCallback {

    private final BaseClient client;
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleGUIClient.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }
    //The underlaying queue is a DelayedWorkQueue - this is optimized for scheduled
    //execution but submit does also work. The queue is unlimited
    private final static ScheduledThreadPoolExecutor UI_EXECUTOR 
            = new ScheduledThreadPoolExecutor(4, new NamedThreadFactory("ui-client-schedules"));

    private final List<ClientsideMessageProcessor> messageProcessorList = Collections.synchronizedList(new ArrayList<ClientsideMessageProcessor>());
    private String serverProductName = null;

    protected GUIClient() {
        this.client = new BaseClient(this, BaseClient.CLIENT_RICH_CLIENT);
        this.client.setLogger(this.getLogger());
    }

    /**
     * The executor is a scheduled thread pool but its also possible to execute single tasks here
     * as it is inherited from ThreadPool
     * @param runnable 
     */
    public static void submit( Runnable runnable) {
        UI_EXECUTOR.submit(runnable);
    }
    
    public static void scheduleWithFixedDelay( Runnable runnable, int startDelay, int repeatDelay, TimeUnit timeUnit) {
        UI_EXECUTOR.scheduleWithFixedDelay(runnable, startDelay, repeatDelay, timeUnit );
    }

    public void addMessageProcessor(ClientsideMessageProcessor processor) {
        synchronized (this.messageProcessorList) {
            this.messageProcessorList.add(processor);
        }
    }

    public void removeMessageProcessor(ClientsideMessageProcessor processor) {
        synchronized (this.messageProcessorList) {
            this.messageProcessorList.remove(processor);
        }
    }

    /**
     * Logs something to the clients log
     */
    @Override
    public void log(Level logLevel, String message) {
        if (this.getLogger() == null) {
            throw new RuntimeException("GUIClient.log: No logger set.");
        }
        this.getLogger().log(logLevel, message);
    }

    public void connect(final InetSocketAddress address, final long timeout) {
        if (this.getLogger() == null) {
            throw new RuntimeException("GUIClient.connect: No logger set.");
        }
        ProgressRun progress = new ProgressRun(address);
        UI_EXECUTOR.submit(progress);
        boolean connected = false;
        try {
            connected = this.client.connect(address, timeout);
        } finally {
            progress.stopRunning();
        }
        if (!connected) {
            this.log(Level.WARNING, rb.getResourceString("connectionrefused.message", address));
            JOptionPane.showMessageDialog(GUIClient.this,
                    GUIClient.rb.getResourceString("connectionrefused.message", address),
                    GUIClient.rb.getResourceString("connectionrefused.title"), JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    public abstract Logger getLogger();

    /**
     * Authenticates the user with the server
     *
     * @param user the username
     * @param passwd the password
     * @param clientId the client identifier
     * @return LoginResponse containing success status, user info, and mustChangePassword flag, or null if authentication failed
     */
    public LoginResponse performLogin(String user, char[] passwd, String clientId) {
        if (this.getLogger() == null) {
            throw new RuntimeException("GUIClient.performLogin: No logger set.");
        }

        // Call BaseClient.login() which now validates credentials
        LoginResponse response = this.getBaseClient().login(user, passwd, clientId);

        if (response == null || !response.isSuccess()) {
            String errorMsg = response != null ? response.getErrorMessage() : "No response from server";
            this.log(Level.SEVERE, rb.getResourceString("login.failed", user) + ": " + errorMsg);
            return response;
        }

        // Login successful
        // this.log(Level.CONFIG, rb.getResourceString("login.success", user));
        return response;
    }

    /**
     * Overwrite this to change the login dialog color
     */
    public Color getLoginDialogColorBackground() {
        return (Color.decode("#556b4C"));
    }

    /**
     * Overwrite this to change the login dialog color
     */
    public Color getLoginDialogColorForeground() {
        return (Color.WHITE);
    }

    /**
     * Login dialog removed - no longer needed
     * @deprecated Authentication removed for SwingUI
     */
    @Deprecated
    private Object performLogin(String user) {
        // No login dialog needed
        return null;
    }

    /**
     * Sends a message asynchronous to the server
     */
    public void sendAsync(ClientServerMessage message) {
        this.getBaseClient().sendAsync(message);
    }

    /**
     * Sends a message sync to the server and returns a response Will inform the
     * ClientSessionHandler callback (syncRequestFailed) if the sync request
     * fails
     */
    public ClientServerResponse sendSync(ClientServerMessage request, long timeout) {
        return (this.getBaseClient().sendSync(request, timeout));
    }

    /**
     * Sends a message sync to the server and returns a response Will inform the
     * ClientSessionHandler callback (syncRequestFailed) if the sync request
     * fails
     */
    public ClientServerResponse sendSync(ClientServerMessage request) {
        return (this.getBaseClient().sendSync(request));
    }

    @Override
    public void connected(SocketAddress socketAddress) {
        if (this.getLogger() == null) {
            throw new RuntimeException("GUIClient.connected: No logger set.");
        }
        this.log(Level.INFO, rb.getResourceString("connection.success",
                socketAddress.toString()));
    }

    @Override
    public void loggedOut() {
        if (this.getLogger() == null) {
            throw new RuntimeException("GUIClient.loggedOut: No logger set.");
        }
        this.log(Level.INFO, rb.getResourceString("logout.from.server"));
    }

    @Override
    public void disconnected() {
        if (this.getLogger() == null) {
            throw new RuntimeException("GUIClient.diconnected: No logger set.");
        }
        this.log(Level.WARNING, rb.getResourceString("connection.closed"));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, rb.getResourceString("connection.closed.message"),
                        rb.getResourceString("connection.closed.title"), JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    /**
     * Overwrite this in the client implementation for user defined processing
     */
    @Override
    public void messageReceivedFromServer(ClientServerMessage message) {
        //there is no user defined processing for sync responses
        if (message._isSyncRequest() && message instanceof ClientServerResponse) {
            synchronized (this.messageProcessorList) {
                //let the message process by all registered client side processors            
                for (ClientsideMessageProcessor processor : this.messageProcessorList) {
                    processor.processSyncResponseFromServer((ClientServerResponse) message);
                }
            }
            return;
        }
        if (this.getLogger() == null) {
            throw new RuntimeException("GUIClient.messageReceivedFromServer: No logger set.");
        }
        //catch the server information if its available
        if (message instanceof ServerInfo) {
            this.serverProductName = ((ServerInfo) message).getProductname();
        } else {
            boolean processed = false;
            synchronized (this.messageProcessorList) {
                //let the message process by all registered client side processors
                for (ClientsideMessageProcessor processor : this.messageProcessorList) {
                    processed |= processor.processMessageFromServer(message);
                }
            }
            if (!(message instanceof ServerSideNotification) && !processed) {
                this.log(Level.WARNING, rb.getResourceString("client.received.unprocessed.message",
                        message.getClass().getName()));
            }
        }
    }

    @Override
    public void error(String message) {
        if (this.getLogger() == null) {
            throw new RuntimeException("GUIClient.error: No logger set.");
        }
        this.log(Level.SEVERE, rb.getResourceString("error", message));
    }

    /**
     * Performs a logout, closes the actual session
     */
    public void logout() {
        this.getBaseClient().logout();
    }

    /**
     * @return the client
     */
    public BaseClient getBaseClient() {
        return client;
    }

    /**
     * Makes this a ClientSessionCallback
     */
    @Override
    public void syncRequestFailed(ClientServerMessage request, ClientServerMessage response, Throwable throwable) {
        this.getLogger().warning(throwable.getMessage());
    }

    private class ProgressRun implements Runnable {

        private boolean keepRunning = true;
        private final InetSocketAddress address;

        public ProgressRun(InetSocketAddress address) {
            this.address = address;
        }

        @Override
        public void run() {
            JDialogConnectionProgress dialog = new JDialogConnectionProgress(GUIClient.this);
            dialog.setHost(address.toString());
            dialog.setVisible(true);
            while (this.keepRunning) {
                try {
                    TimeUnit.MILLISECONDS.sleep(150);
                } catch (InterruptedException e) {
                }
            }
            dialog.setVisible(false);
            dialog.dispose();
        }

        public void stopRunning() {
            this.keepRunning = false;
        }
    }
}
