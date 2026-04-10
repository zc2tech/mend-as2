package de.mendelson.util.clientserver;

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
 * Minimal stub for ClientServer compatibility.
 * Mina networking removed - server communication now uses EventBus.
 *
 * @author Julian Xu
 * @version 1.0
 */
public class ClientServer {
    // Constants for compatibility
    public static final int CLIENTSERVER_COMM_PORT = 1234;
    public static final int CLIENTSERVER_COMM_PORT_TEST = 41234;
    public static final String SESSION_ATTRIB_USER = "user";
    public static final String SESSION_ATTRIB_CLIENT_PID = "clientPid";
    public static final long TIMEOUT_SYNC_RECEIVE = 30000L;
    public static final String[] SERVERSIDE_ACCEPTED_TLS_PROTOCOLS = new String[]{"TLSv1.2", "TLSv1.3"};

    /**
     * No-arg constructor stub
     */
    public ClientServer() {
        // Stub
    }

    /**
     * Multi-arg constructor stub for compatibility
     */
    public ClientServer(Object logger, int port, Object tls) {
        // Stub
    }

    /**
     * Stub method - no longer broadcasts via Mina
     * EventBus is used instead
     */
    public void broadcastToClients(Object message) {
        // No-op: All broadcasts now go through EventBus
    }

    /**
     * Stub method for compatibility
     */
    public void setProductName(String productName) {
        // No-op
    }

    /**
     * Stub method for compatibility
     */
    public void start() throws Exception {
        // No-op: Server starts without Mina socket
    }

    /**
     * Stub method for compatibility
     */
    public java.util.Collection<?> getSessions() {
        return java.util.Collections.emptyList();
    }

    /**
     * Stub method for compatibility
     */
    public void setSessionHandler(Object handler) {
        // No-op
    }

    /**
     * Stub method for compatibility
     */
    public static String clientTypeToStr(int clientType) {
        switch (clientType) {
            case 1: return "WEB";
            case 2: return "RICH_CLIENT";
            case 3: return "REST";
            case 4: return "SENDORDER";
            case 5: return "SHUTDOWN";
            default: return "UNSPECIFIED";
        }
    }
}
