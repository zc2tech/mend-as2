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
package de.mendelson.comm.as2.tracker;

import java.io.Serializable;
import java.util.Date;

/**
 * Data model for tracker messages
 *
 * @author Julian Xu
 */
public class TrackerMessageInfo implements Serializable {

    public static final long serialVersionUID = 1L;

    // Authentication status constants
    public static final int AUTH_STATUS_NONE = 0;
    public static final int AUTH_STATUS_SUCCESS = 1;
    public static final int AUTH_STATUS_FAILED = 2;

    private int id;
    private String messageId;
    private String trackerId;
    private String remoteAddr;
    private String userAgent;
    private String contentType;
    private int contentSize;
    private Date initDate;
    private int authStatus = AUTH_STATUS_NONE;
    private String authUser;
    private String rawFilename;
    private String requestHeaders;
    private int payloadCount = 0;
    private String payloadFormat; // cXML, X12, EDIFACT, Unknown
    private String payloadDocType; // Purchase Order, Invoice, 810, DESADV, etc.
    private String payloadDetails; // Additional details

    public TrackerMessageInfo() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getRawFilename() {
        return rawFilename;
    }

    public void setRawFilename(String rawFilename) {
        this.rawFilename = rawFilename;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public int getPayloadCount() {
        return payloadCount;
    }

    public void setPayloadCount(int payloadCount) {
        this.payloadCount = payloadCount;
    }

    public String getPayloadFormat() {
        return payloadFormat;
    }

    public void setPayloadFormat(String payloadFormat) {
        this.payloadFormat = payloadFormat;
    }

    public String getPayloadDocType() {
        return payloadDocType;
    }

    public void setPayloadDocType(String payloadDocType) {
        this.payloadDocType = payloadDocType;
    }

    public String getPayloadDetails() {
        return payloadDetails;
    }

    public void setPayloadDetails(String payloadDetails) {
        this.payloadDetails = payloadDetails;
    }

    /**
     * Get human-readable auth status
     */
    public String getAuthStatusText() {
        switch (authStatus) {
            case AUTH_STATUS_NONE:
                return "No Auth";
            case AUTH_STATUS_SUCCESS:
                return "Success";
            case AUTH_STATUS_FAILED:
                return "Failed";
            default:
                return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "TrackerMessageInfo{" +
                "id=" + id +
                ", trackerId='" + trackerId + '\'' +
                ", remoteAddr='" + remoteAddr + '\'' +
                ", contentSize=" + contentSize +
                ", authStatus=" + authStatus +
                '}';
    }
}
