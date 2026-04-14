package de.mendelson.comm.as2.partner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

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
 * Data model for inbound authentication credentials for local stations.
 * Supports multiple credentials per local station for flexibility.
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerInboundAuthCredential implements Serializable {

    public static final long serialVersionUID = 1L;

    // Auth types
    public static final int AUTH_TYPE_BASIC = 1;
    public static final int AUTH_TYPE_CERTIFICATE = 2;

    @JsonProperty("dbId")
    private int dbId = -1;

    @JsonProperty("authType")
    private int authType = AUTH_TYPE_BASIC;

    @JsonProperty("username")
    private String username = "";

    @JsonProperty("password")
    private String password = "";

    @JsonProperty("certFingerprint")
    private String certFingerprint = "";

    @JsonProperty("certAlias")
    private String certAlias = "";

    @JsonProperty("enabled")
    private boolean enabled = true;

    /**
     * Default constructor
     */
    public PartnerInboundAuthCredential() {
    }

    /**
     * Copy constructor
     */
    public PartnerInboundAuthCredential(PartnerInboundAuthCredential source) {
        this.dbId = source.dbId;
        this.authType = source.authType;
        this.username = source.username;
        this.password = source.password;
        this.certFingerprint = source.certFingerprint;
        this.certAlias = source.certAlias;
        this.enabled = source.enabled;
    }

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

    /**
     * Check if this credential is empty (no meaningful data)
     * @return true if empty
     */
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
            return "Certificate[" + (certFingerprint != null && certFingerprint.length() > 10
                ? certFingerprint.substring(0, 10) + "..." : certFingerprint) + "]";
        }
    }
}
