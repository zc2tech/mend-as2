package de.mendelson.util.clientserver;

import javax.swing.JFrame;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import de.mendelson.util.clientserver.messages.LoginResponse;
import de.mendelson.util.clientserver.messages.LoginRequest;
import de.mendelson.util.clientserver.messages.ClientServerResponse;

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
 * Minimal stub for GUIClient compatibility.
 * Mina networking removed - SwingUI now uses EventBus for server communication.
 *
 * @author Julian Xu
 * @version 1.0
 */
public class GUIClient extends JFrame {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private final BaseClient baseClient = new BaseClient();

    /**
     * Schedule task with fixed delay - delegates to BaseClient
     */
    public static void scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        BaseClient.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    /**
     * Submit task to executor (stub method for compatibility)
     */
    public static Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    /**
     * Get base client stub for compatibility
     */
    public BaseClient getBaseClient() {
        return baseClient;
    }

    /**
     * Send sync request - stub method delegates to BaseClient
     */
    public ClientServerResponse sendSync(Object request) {
        return baseClient.sendSync(request);
    }

    /**
     * Send sync request with timeout - stub method delegates to BaseClient
     */
    public ClientServerResponse sendSync(Object request, long timeout) {
        return baseClient.sendSync(request, timeout);
    }

    /**
     * Logout - stub method delegates to BaseClient
     */
    public void logout() {
        baseClient.logout();
    }

    /**
     * Disconnect - stub method delegates to BaseClient
     */
    public void disconnect() {
        baseClient.disconnect();
    }

    /**
     * Send async message - stub method delegates to BaseClient
     */
    public void sendAsync(Object message) {
        baseClient.sendAsync(message);
    }

    /**
     * Add message processor - stub method (no-op)
     */
    public void addMessageProcessor(Object processor) {
        // No-op: Message processing now handled by EventBus
    }

    /**
     * Remove message processor - stub method (no-op)
     */
    public void removeMessageProcessor(Object processor) {
        // No-op: Message processing now handled by EventBus
    }

    /**
     * Perform login - stub method (no authentication needed, same JVM)
     */
    public LoginResponse performLogin(String username, char[] password, String clientId) {
        // No authentication needed when running in same JVM - return success
        LoginRequest request = new LoginRequest(username, password, clientId);
        LoginResponse response = new LoginResponse(request);
        response.setSuccess(true);
        return response;
    }

    /**
     * Message received from server - stub method for compatibility
     */
    public void messageReceivedFromServer(Object message) {
        // No-op: Messages now handled through EventBus
    }
}
