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
package de.mendelson.comm.as2.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Data model for API request logs
 *
 * @author Julian Xu
 */
public class ApiRequestInfo implements Serializable {

    public static final long serialVersionUID = 1L;

    // Authentication status constants
    public static final int AUTH_STATUS_NONE = 0;
    public static final int AUTH_STATUS_SUCCESS = 1;
    public static final int AUTH_STATUS_FAILED = 2;

    @JsonProperty("id")
    private int id;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("remoteAddr")
    private String remoteAddr;

    @JsonProperty("userAgent")
    private String userAgent;

    @JsonProperty("httpMethod")
    private String httpMethod;

    @JsonProperty("requestPath")
    private String requestPath;

    @JsonProperty("requestHeaders")
    private String requestHeaders;

    @JsonProperty("requestBody")
    private byte[] requestBody;

    @JsonProperty("contentType")
    private String contentType;

    @JsonProperty("contentSize")
    private int contentSize;

    @JsonProperty("responseStatus")
    private int responseStatus;

    @JsonProperty("authStatus")
    private int authStatus = AUTH_STATUS_NONE;

    @JsonProperty("authUser")
    private String authUser;

    @JsonProperty("requestTime")
    private Date requestTime;

    /**
     * Get a preview of the request body content (first 2000 bytes) for text-based content
     * Returns null for binary content
     */
    @JsonProperty("contentPreview")
    public String getContentPreview() {
        if (requestBody == null || requestBody.length == 0) {
            return null;
        }

        // Check if content type indicates text-based content
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            boolean isText = lowerContentType.contains("text") ||
                           lowerContentType.contains("json") ||
                           lowerContentType.contains("xml") ||
                           lowerContentType.contains("javascript") ||
                           lowerContentType.contains("form");

            if (!isText) {
                return null; // Binary content, no preview
            }
        }

        // Generate preview (first 2000 bytes)
        try {
            int previewLength = Math.min(2000, requestBody.length);
            String preview = new String(requestBody, 0, previewLength, "UTF-8");
            if (requestBody.length > 2000) {
                preview += "\n\n... (truncated, total size: " + requestBody.length + " bytes)";
            }
            return preview;
        } catch (Exception e) {
            return null; // If conversion fails, it's likely binary
        }
    }

    public ApiRequestInfo() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
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

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
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

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public String toString() {
        return "ApiRequestInfo{" +
                "id=" + id +
                ", requestId='" + requestId + '\'' +
                ", userId=" + userId +
                ", remoteAddr='" + remoteAddr + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", requestPath='" + requestPath + '\'' +
                ", contentSize=" + contentSize +
                ", responseStatus=" + responseStatus +
                ", authStatus=" + authStatus +
                ", requestTime=" + requestTime +
                '}';
    }
}
