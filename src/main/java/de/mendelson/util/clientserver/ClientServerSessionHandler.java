package de.mendelson.util.clientserver;

import org.apache.mina.core.session.IoSession;

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
 * Minimal stub for ClientServerSessionHandler compatibility.
 * Mina networking removed.
 *
 * @author Julian Xu
 * @version 1.0
 */
public class ClientServerSessionHandler {
    // Constants for session attributes
    public static final String SESSION_ATTRIB_USER = "user";
    public static final String SESSION_ATTRIB_CLIENT_PID = "clientPid";
    public static final long TIMEOUT_SYNC_RECEIVE = 30000L;

    public void addServerProcessing(ClientServerProcessing processing) {
        // No-op stub
    }

    public void broadcast(Object message) {
        // No-op stub - EventBus is used instead
    }

    /**
     * Stub method for compatibility
     */
    public void broadcastLogMessage(java.util.logging.Level level, String message, Object[] parameter) {
        // No-op stub - logging broadcast no longer used
    }

    /**
     * Stub method for compatibility
     */
    public void setAnonymousProcessing(Object processing) {
        // No-op
    }

    /**
     * Stub method for compatibility
     */
    public void setProductName(String productName) {
        // No-op
    }
}
