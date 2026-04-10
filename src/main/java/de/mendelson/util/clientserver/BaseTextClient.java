package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.messages.LoginResponse;
import de.mendelson.util.clientserver.messages.LoginRequest;

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
 * Minimal stub for BaseTextClient compatibility.
 * Mina networking removed.
 *
 * @author Julian Xu
 * @version 1.0
 */
public class BaseTextClient extends BaseClient {
    public BaseTextClient(int clientType) {
        // Stub constructor
    }

    public void addMessageProcessor(Object processor) {
        // No-op stub
    }

    public BaseClient getBaseClient() {
        return this;
    }

    public java.util.logging.Logger getLogger() {
        return java.util.logging.Logger.getLogger(getClass().getName());
    }

    public void log(java.util.logging.Level level, String message) {
        getLogger().log(level, message);
    }

    public void disconnect() {
        // No-op stub
    }

    public void connect(java.net.InetSocketAddress address, long timeout) {
        // No-op stub
    }

    public LoginResponse performLogin(String username, char[] password, String clientId) {
        // No authentication needed - return success
        LoginRequest request = new LoginRequest(username, password, clientId);
        LoginResponse response = new LoginResponse(request);
        response.setSuccess(true);
        return response;
    }
}
