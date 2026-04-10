package de.mendelson.comm.as2.server;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
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
 * Direct service client for SwingUI to call server operations without Mina networking.
 * Replaces Mina request-response with direct method calls in same JVM.
 *
 * @author Julian Xu
 * @version 1.0
 */
public class DirectServiceClient {
    private static DirectServiceClient instance;
    private AS2ServerProcessing serverProcessing;

    private DirectServiceClient() {
        // Singleton
    }

    public static synchronized DirectServiceClient getInstance() {
        if (instance == null) {
            instance = new DirectServiceClient();
        }
        return instance;
    }

    public void setServerProcessing(AS2ServerProcessing serverProcessing) {
        this.serverProcessing = serverProcessing;
    }

    /**
     * Process any ClientServerMessage request
     */
    public ClientServerResponse processRequest(ClientServerMessage request) {
        if (serverProcessing == null) {
            throw new IllegalStateException("Server processing not initialized");
        }

        try {
            // Create a capturing IoSession to intercept the response
            CapturingIoSession capturingSession = new CapturingIoSession();

            // Process the request - response will be written to capturing session
            serverProcessing.process(capturingSession, request);

            // Return the captured response
            return capturingSession.getCapturedResponse();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process request: " + request.getClass().getSimpleName(), e);
        }
    }

    /**
     * IoSession implementation that captures written responses
     */
    private static class CapturingIoSession implements org.apache.mina.core.session.IoSession {
        private ClientServerResponse capturedResponse;
        private final java.util.Map<String, Object> attributes = new java.util.HashMap<>();

        public CapturingIoSession() {
            // Set default session attributes for SwingUI client
            // Use consistent values so locks work correctly
            attributes.put("user", "admin");  // SwingUI always runs as admin
            attributes.put("clientPid", java.lang.management.ManagementFactory.getRuntimeMXBean().getName());
        }

        @Override
        public void write(Object message) {
            if (message instanceof ClientServerResponse) {
                this.capturedResponse = (ClientServerResponse) message;
            }
        }

        public ClientServerResponse getCapturedResponse() {
            return capturedResponse;
        }

        @Override
        public Object getAttribute(String key) {
            return attributes.get(key);
        }

        @Override
        public void setAttribute(String key, Object value) {
            attributes.put(key, value);
        }

        @Override
        public Object getRemoteAddress() {
            return new java.net.InetSocketAddress("127.0.0.1", 0);
        }

        @Override
        public Object getLocalAddress() {
            return new java.net.InetSocketAddress("127.0.0.1", 0);
        }

        @Override
        public void closeNow() {
            // No-op
        }

        @Override
        public void closeOnFlush() {
            // No-op
        }

        @Override
        public long getId() {
            // Return a consistent ID for SwingUI session
            return 1;
        }
    }
}
