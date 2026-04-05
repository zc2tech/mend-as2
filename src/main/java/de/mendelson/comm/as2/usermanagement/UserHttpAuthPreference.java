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
 * GNU General Public License for details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.mendelson.comm.as2.usermanagement;

import java.io.Serializable;

/**
 * Represents a user's HTTP authentication preference for a specific partner
 */
public class UserHttpAuthPreference implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int partnerId;
    private String partnerName; // For display purposes
    private String partnerAs2Id; // For display purposes
    private boolean useMessageAuth;
    private String messageUsername;
    private String messagePassword;
    private boolean useMdnAuth;
    private String mdnUsername;
    private String mdnPassword;

    public UserHttpAuthPreference() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerAs2Id() {
        return partnerAs2Id;
    }

    public void setPartnerAs2Id(String partnerAs2Id) {
        this.partnerAs2Id = partnerAs2Id;
    }

    public boolean isUseMessageAuth() {
        return useMessageAuth;
    }

    public void setUseMessageAuth(boolean useMessageAuth) {
        this.useMessageAuth = useMessageAuth;
    }

    public String getMessageUsername() {
        return messageUsername;
    }

    public void setMessageUsername(String messageUsername) {
        this.messageUsername = messageUsername;
    }

    public String getMessagePassword() {
        return messagePassword;
    }

    public void setMessagePassword(String messagePassword) {
        this.messagePassword = messagePassword;
    }

    public boolean isUseMdnAuth() {
        return useMdnAuth;
    }

    public void setUseMdnAuth(boolean useMdnAuth) {
        this.useMdnAuth = useMdnAuth;
    }

    public String getMdnUsername() {
        return mdnUsername;
    }

    public void setMdnUsername(String mdnUsername) {
        this.mdnUsername = mdnUsername;
    }

    public String getMdnPassword() {
        return mdnPassword;
    }

    public void setMdnPassword(String mdnPassword) {
        this.mdnPassword = mdnPassword;
    }
}
