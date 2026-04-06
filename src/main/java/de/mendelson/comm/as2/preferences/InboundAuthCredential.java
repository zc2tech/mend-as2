/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.mendelson.comm.as2.preferences;

import java.io.Serializable;

/**
 * Entity representing a single inbound authentication credential.
 * Used for system-wide authentication of incoming AS2 messages.
 *
 * @author Julian Xu
 */
public class InboundAuthCredential implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int TYPE_BASIC = 1;
    public static final int TYPE_CERTIFICATE = 2;

    private int id;
    private int authType;
    private String username;  // For basic auth
    private String password;  // For basic auth
    private String certAlias; // For certificate auth

    public InboundAuthCredential() {
    }

    public InboundAuthCredential(int authType) {
        this.authType = authType;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCertAlias() {
        return certAlias;
    }

    public void setCertAlias(String certAlias) {
        this.certAlias = certAlias;
    }

    @Override
    public String toString() {
        if (authType == TYPE_BASIC) {
            return "Basic: " + username;
        } else if (authType == TYPE_CERTIFICATE) {
            return "Certificate: " + certAlias;
        }
        return "Unknown";
    }
}
