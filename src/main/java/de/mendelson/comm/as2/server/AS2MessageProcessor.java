package de.mendelson.comm.as2.server;

import de.mendelson.comm.as2.clientserver.message.IncomingMessageRequest;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageResponse;

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
 * Service to process incoming AS2 messages without Apache Mina client-server.
 * Replaces the Mina socket-based communication between HttpReceiver and
 * AS2ServerProcessing with direct method calls in the same JVM.
 *
 * This eliminates the need for TCP sockets, serialization, and network
 * communication for internal message passing.
 *
 * @author Julian Xu
 * @version 1.0
 */
public class AS2MessageProcessor {
    private static AS2MessageProcessor instance;
    private AS2ServerProcessing serverProcessing;

    private AS2MessageProcessor() {
        // Singleton - private constructor
    }

    /**
     * Get the singleton AS2MessageProcessor instance.
     *
     * @return the AS2MessageProcessor instance
     */
    public static synchronized AS2MessageProcessor getInstance() {
        if (instance == null) {
            instance = new AS2MessageProcessor();
        }
        return instance;
    }

    /**
     * Set the server processing instance.
     * Must be called during server startup after AS2ServerProcessing is created.
     *
     * @param serverProcessing the server processing instance
     */
    public void setServerProcessing(AS2ServerProcessing serverProcessing) {
        this.serverProcessing = serverProcessing;
    }

    /**
     * Process incoming AS2 message from HttpReceiver servlet.
     * This replaces the Mina socket-based communication with a direct method call.
     *
     * @param request the incoming message request containing HTTP data and headers
     * @return the response with MDN data and HTTP status code
     * @throws Exception if processing fails
     * @throws IllegalStateException if server processing not initialized
     */
    public IncomingMessageResponse processIncomingMessage(IncomingMessageRequest request)
            throws Exception {
        if (serverProcessing == null) {
            throw new IllegalStateException("Server processing not initialized - call setServerProcessing() first");
        }
        return serverProcessing.handleIncomingMessage(request);
    }
}
