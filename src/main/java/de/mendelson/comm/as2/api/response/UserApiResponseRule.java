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

package de.mendelson.comm.as2.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * POJO representing a single user API response rule.
 * Rules define custom HTTP responses based on method and path pattern.
 */
public class UserApiResponseRule implements Serializable {

    public static final String MATCH_TYPE_EXACT = "exact";
    public static final String MATCH_TYPE_PREFIX = "prefix";
    public static final String MATCH_TYPE_WILDCARD = "wildcard";
    public static final String MATCH_TYPE_REGEX = "regex";

    @JsonProperty("id")
    private int id = -1;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("priority")
    private int priority = 0;

    @JsonProperty("enabled")
    private boolean enabled = true;

    @JsonProperty("httpMethod")
    private String httpMethod = "GET";

    @JsonProperty("pathPattern")
    private String pathPattern = "/";

    @JsonProperty("pathMatchType")
    private String pathMatchType = MATCH_TYPE_EXACT;

    @JsonProperty("statusCode")
    private int statusCode = 200;

    @JsonProperty("contentType")
    private String contentType = "application/json";

    @JsonProperty("responseBody")
    private String responseBody = "";

    @JsonProperty("createdAt")
    private Date createdAt;

    @JsonProperty("updatedAt")
    private Date updatedAt;

    public UserApiResponseRule() {
    }

    public UserApiResponseRule(UserApiResponseRule source) {
        if (source != null) {
            this.id = source.id;
            this.userId = source.userId;
            this.priority = source.priority;
            this.enabled = source.enabled;
            this.httpMethod = source.httpMethod;
            this.pathPattern = source.pathPattern;
            this.pathMatchType = source.pathMatchType;
            this.statusCode = source.statusCode;
            this.contentType = source.contentType;
            this.responseBody = source.responseBody;
            this.createdAt = source.createdAt;
            this.updatedAt = source.updatedAt;
        }
    }

    @Override
    public String toString() {
        return String.format("Rule[%d] %s %s (%s) -> %d",
                id, httpMethod, pathPattern, pathMatchType, statusCode);
    }

    // Getters and setters

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public String getPathMatchType() {
        return pathMatchType;
    }

    public void setPathMatchType(String pathMatchType) {
        this.pathMatchType = pathMatchType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
