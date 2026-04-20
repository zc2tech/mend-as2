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
 * Represents a blocked IP attempt in the audit log
 *
 * @author Julian Xu
 */
public class IPWhitelistBlockLog implements Serializable {

    public static final long serialVersionUID = 1L;

    private int id;
    private String blockedIp;
    private String targetType;         // AS2, TRACKER, WEBUI, API
    private String attemptedUser;
    private String attemptedPartner;
    private Date blockTime;
    private String userAgent;
    private String requestPath;

    public IPWhitelistBlockLog() {
        this.blockTime = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBlockedIp() {
        return blockedIp;
    }

    public void setBlockedIp(String blockedIp) {
        this.blockedIp = blockedIp;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getAttemptedUser() {
        return attemptedUser;
    }

    public void setAttemptedUser(String attemptedUser) {
        this.attemptedUser = attemptedUser;
    }

    public String getAttemptedPartner() {
        return attemptedPartner;
    }

    public void setAttemptedPartner(String attemptedPartner) {
        this.attemptedPartner = attemptedPartner;
    }

    public Date getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Date blockTime) {
        this.blockTime = blockTime;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    @Override
    public String toString() {
        return "IPWhitelistBlockLog{" +
                "id=" + id +
                ", blockedIp='" + blockedIp + '\'' +
                ", targetType='" + targetType + '\'' +
                ", attemptedUser='" + attemptedUser + '\'' +
                ", attemptedPartner='" + attemptedPartner + '\'' +
                ", blockTime=" + blockTime +
                '}';
    }
}
