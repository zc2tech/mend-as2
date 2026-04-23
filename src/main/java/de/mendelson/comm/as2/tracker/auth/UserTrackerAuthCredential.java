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

package de.mendelson.comm.as2.tracker.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * POJO representing a single user tracker authentication credential.
 * Can be either Basic Auth (username/password) or Certificate Auth (fingerprint).
 * Pattern based on PartnerInboundAuthCredential.
 */
public class UserTrackerAuthCredential implements Serializable {

    public static final int AUTH_TYPE_BASIC = 1;
    public static final int AUTH_TYPE_CERTIFICATE = 2;

    @JsonProperty("dbId")
    private int dbId = -1;  // Database primary key, -1 means not yet persisted

    @JsonProperty("authType")
    private int authType = AUTH_TYPE_BASIC;  // 1 or 2

    // For Basic Auth (authType=1)
    @JsonProperty("username")
    private String username = "";

    @JsonProperty("password")
    private String password = "";

    // For Certificate Auth (authType=2)
    @JsonProperty("certFingerprint")
    private String certFingerprint = "";  // SHA-1 format with colons

    @JsonProperty("certAlias")
    private String certAlias = "";  // Display name/alias

    @JsonProperty("enabled")
    private boolean enabled = true;  // Per-credential enable/disable

    /**
     * Default constructor
     */
    public UserTrackerAuthCredential() {
    }

    /**
     * Copy constructor
     */
    public UserTrackerAuthCredential(UserTrackerAuthCredential source) {
        if (source != null) {
            this.dbId = source.dbId;
            this.authType = source.authType;
            this.username = source.username;
            this.password = source.password;
            this.certFingerprint = source.certFingerprint;
            this.certAlias = source.certAlias;
            this.enabled = source.enabled;
        }
    }

    /**
     * Check if credential is empty (has no meaningful data)
     */
    @JsonIgnore
    public boolean isEmpty() {
        if (authType == AUTH_TYPE_BASIC) {
            return username == null || username.trim().isEmpty();
        } else if (authType == AUTH_TYPE_CERTIFICATE) {
            return certFingerprint == null || certFingerprint.trim().isEmpty();
        }
        return true;
    }

    @Override
    public String toString() {
        if (authType == AUTH_TYPE_BASIC) {
            return "Basic[" + username + "]";
        } else {
            String fp = certFingerprint;
            if (fp != null && fp.length() > 20) {
                fp = fp.substring(0, 20) + "...";
            }
            return "Certificate[" + fp + "]";
        }
    }

    // Getters and setters

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
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

    public String getCertFingerprint() {
        return certFingerprint;
    }

    public void setCertFingerprint(String certFingerprint) {
        this.certFingerprint = certFingerprint;
    }

    public String getCertAlias() {
        return certAlias;
    }

    public void setCertAlias(String certAlias) {
        this.certAlias = certAlias;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
