package de.mendelson.util.clientserver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.comm.as2.server.DirectServiceClient;

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
 * Minimal stub for BaseClient compatibility.
 * Mina networking removed - SwingUI now uses EventBus for server communication.
 *
 * @author Julian Xu
 * @version 1.0
 */
public class BaseClient {
    public static final int CLIENT_WEB = 1;
    public static final int CLIENT_RICH_CLIENT = 2;
    public static final int CLIENT_REST = 3;
    public static final int CLIENT_SENDORDER = 4;
    public static final int CLIENT_UNSPECIFIED = 0;
    public static final int CLIENT_COMMANDLINE_SHUTDOWN = 5;
    public static final long TIMEOUT_SYNC_RECEIVE = 30000L;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * Username of the logged-in user (for user-scoped operations)
     */
    private String username = "admin";  // Default to admin for backward compatibility

    /**
     * Schedule task with fixed delay (used by refresh threads)
     */
    public static void scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        scheduler.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    /**
     * Set the username for this client session
     */
    public void setUsername(String username) {
        this.username = username != null ? username : "admin";
    }

    /**
     * Get the username for this client session
     */
    public String getUsername() {
        return username;
    }

    /**
     * Async send - executes in background thread via DirectServiceClient
     */
    public void sendAsync(Object message) {
        if (message instanceof ClientServerMessage) {
            // Execute in background thread to not block UI
            final String currentUsername = this.username;
            scheduler.execute(() -> {
                try {
                    DirectServiceClient.getInstance().processRequest((ClientServerMessage) message, currentUsername);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Stub method for compatibility
     */
    public ClientServerResponse sendSync(Object request, long timeout) {
        // Use DirectServiceClient to get real data from server
        if (request instanceof ClientServerMessage) {
            return DirectServiceClient.getInstance().processRequest((ClientServerMessage) request, this.username);
        }
        return null;
    }

    /**
     * Stub method for compatibility - single parameter version
     */
    public ClientServerResponse sendSync(Object request) {
        return sendSync(request, TIMEOUT_SYNC_RECEIVE);
    }

    /**
     * Stub method for compatibility
     */
    public ClientServerResponse sendSyncWaitInfinite(Object request) {
        return sendSync(request, Long.MAX_VALUE);
    }

    /**
     * Stub method for compatibility
     */
    public void logout() {
        // No-op: No authentication needed
    }

    /**
     * Stub method for compatibility
     */
    public void disconnect() {
        // No-op: No connection to disconnect
    }

    /**
     * Stub method for compatibility
     */
    public boolean getDisplayServerLogMessages() {
        return false;
    }

    /**
     * Stub method for compatibility
     */
    public void setUser(Object user) {
        // No-op
    }

    /**
     * Stub method for compatibility - convert client type to string
     */
    public static String clientTypeToStr(int clientType) {
        switch (clientType) {
            case CLIENT_WEB: return "WEB";
            case CLIENT_RICH_CLIENT: return "RICH_CLIENT";
            case CLIENT_REST: return "REST";
            case CLIENT_SENDORDER: return "SENDORDER";
            case CLIENT_COMMANDLINE_SHUTDOWN: return "SHUTDOWN";
            default: return "UNSPECIFIED";
        }
    }
}
