package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.ServerInfo;
import de.mendelson.util.clientserver.user.User;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Text Client to connect to a mendelson product
 *
 * @author S.Heller
 * @version $Revision: 32 $
 */
public class TextClient extends BaseTextClient implements ClientsideMessageProcessor, AutoCloseable {

    private String user = null;
    private ConnectThread connectionThread = null;

    /**
     *
     * @param CLIENT_TYPE Client Type as defined in the BaseClient
     */
    public TextClient(final int CLIENT_TYPE) {
        super(CLIENT_TYPE);
        super.addMessageProcessor(this);
    }

    /**
     * Connects to the server and performs a login
     */
    public void connectAndLogin(String host,
            int clientServerCommPort, String clientId,
            String user, char[] password, long timeout,
            String connectionThreadNamePrefix) throws Throwable {
        this.user = user;
        this.connectionThread = new ConnectThread(host, clientServerCommPort, timeout);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(this.connectionThread);
        executor.shutdown();
        this.connectionThread.getDoneSignal().await(timeout, TimeUnit.MILLISECONDS);
        if (this.connectionThread.getState() == ConnectThread.STATE_FAILURE) {
            throw (this.connectionThread.getException());
        } else if (this.connectionThread.getState() == ConnectThread.STATE_WORKING) {
            throw new Exception("Connection timeout");
        }
    }

    /**
     * The client received a message from the server. Overwrite this in your
     * text client implementation to process inbound messages from the server on
     * the client
     */
    @Override
    public boolean processMessageFromServer(ClientServerMessage message) {
        if (message instanceof ServerInfo) {
            if (this.getBaseClient().getDisplayServerLogMessages()) {
                ServerInfo serverInfo = (ServerInfo) message;
                this.getLogger().log(Level.CONFIG, "Remote server identified as " + serverInfo.getProductname());
            }
        }
        return (true);
    }

    @Override
    public void loginRequestedFromServer() {
        // Simplified - no authentication needed
        // Create a dummy user for compatibility
        User dummyUser = new User();
        dummyUser.setName(this.user != null ? this.user : "text_client");
        this.getBaseClient().setUser(dummyUser);

        if (this.getBaseClient().getDisplayServerLogMessages()) {
            this.log(Level.INFO, "Connected to server");
        }
        this.connectionThread.signalSuccess();
        this.connectionThread.signalLoginProcessFinished();
    }

    /**
     * Performs a logout, closes the actual session
     */
    @Override
    public void logout() {
        if (this.getBaseClient().getDisplayServerLogMessages()) {
            this.log(Level.INFO, "Logging out");
        }
        super.logout();
    }

    /**
     * Returns the version of this class
     */
    public static String getVersion() {
        String revision = "$Revision: 32 $";
        return (revision.substring(revision.indexOf(":") + 1,
                revision.lastIndexOf("$")).trim());
    }

    /**
     * Allows a manual login, not supported so far
     */
    @Override
    public void performLogin() {
        throw new IllegalArgumentException("PerformLogin: manual login not implemented so far.");
    }

    @Override
    public void processSyncResponseFromServer(ClientServerResponse response) {
    }

    /**
     * Makes this class AutoCloseable: automatically logout and disconnect the text client
     */
    @Override
    public void close() throws Exception {
        this.logout();
        this.disconnect();
    }

    private class ConnectThread implements Runnable {

        public static final int STATE_WORKING = 0;
        public static final int STATE_FAILURE = 1;
        public static final int STATE_SUCCESS = 2;
        private final int clientServerCommPort;
        private final String host;
        private int state = STATE_WORKING;
        private Throwable exception = null;
        private final CountDownLatch doneSignal = new CountDownLatch(2);
        private final long timeout;

        public ConnectThread(String host, int clientServerCommPort, long timeout) {
            this.host = host;
            this.timeout = timeout;
            this.clientServerCommPort = clientServerCommPort;
        }

        @Override
        public void run() {
            try {
                long connectionStartTime = System.currentTimeMillis();
                connect(new InetSocketAddress(this.host, this.clientServerCommPort), this.timeout);
                while (this.state == STATE_WORKING) {
                    Thread.sleep(50);
                    //The whole login process takes some message to go back and forward - its not
                    //the one time timeout the thread should wait for the login to be done
                    if (connectionStartTime + (3 * this.timeout) < System.currentTimeMillis()) {
                        throw new Exception("Connection timeout in client-server connection");
                    }
                }
            } catch (Throwable e) {
                this.signalFailure(e);
            }
            this.doneSignal.countDown();
        }

        public void signalLoginProcessFinished() {
            this.doneSignal.countDown();
        }

        public void signalSuccess() {
            this.state = STATE_SUCCESS;
        }

        public void signalFailure(Throwable exception) {
            this.exception = exception;
            this.state = STATE_FAILURE;
        }

        /**
         * @return the state
         */
        public int getState() {
            return state;
        }

        /**
         * @return the exception
         */
        public Throwable getException() {
            return exception;
        }

        /**
         * @return the doneSignal
         */
        public CountDownLatch getDoneSignal() {
            return doneSignal;
        }
    }
}
