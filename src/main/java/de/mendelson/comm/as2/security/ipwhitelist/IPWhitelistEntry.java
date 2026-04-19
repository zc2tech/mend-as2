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
package de.mendelson.comm.as2.security.ipwhitelist;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an IP whitelist entry
 * Can be global, partner-specific, or user-specific
 *
 * @author Julian Xu
 */
public class IPWhitelistEntry implements Serializable {

    public static final long serialVersionUID = 1L;

    // Target types for global whitelist
    public static final String TARGET_AS2 = "AS2";
    public static final String TARGET_TRACKER = "TRACKER";
    public static final String TARGET_WEBUI = "WEBUI";
    public static final String TARGET_API = "API";
    public static final String TARGET_ALL = "ALL";

    private int id;
    private String ipPattern;           // e.g., "192.168.1.100", "10.0.0.0/24", "172.16.*"
    private String description;
    private String targetType;          // Only for global whitelist
    private boolean enabled;
    private Date createdAt;
    private String createdBy;
    private Integer partnerId;          // Only for partner-specific whitelist
    private Integer userId;             // Only for user-specific whitelist

    public IPWhitelistEntry() {
        this.enabled = true;
        this.createdAt = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpPattern() {
        return ipPattern;
    }

    public void setIpPattern(String ipPattern) {
        this.ipPattern = ipPattern;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IPWhitelistEntry{");
        sb.append("id=").append(id);
        sb.append(", ipPattern='").append(ipPattern).append('\'');
        if (targetType != null) {
            sb.append(", targetType='").append(targetType).append('\'');
        }
        if (partnerId != null) {
            sb.append(", partnerId=").append(partnerId);
        }
        if (userId != null) {
            sb.append(", userId=").append(userId);
        }
        sb.append(", enabled=").append(enabled);
        sb.append('}');
        return sb.toString();
    }
}
