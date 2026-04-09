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
package de.mendelson.comm.as2.tracker.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;

import java.util.Date;

/**
 * Request to retrieve tracker messages from server
 *
 * @author Julian Xu
 */
public class TrackerMessageRequest extends ClientServerMessage{

    public static final long serialVersionUID = 1L;

    public static final int TYPE_LIST_MESSAGES = 1;
    public static final int TYPE_GET_MESSAGE_DETAILS = 2;

    private int requestType;
    private Date startDate;
    private Date endDate;
    private boolean showAuthNone = true;
    private boolean showAuthSuccess = true;
    private boolean showAuthFailed = true;
    private String trackerId; // For TYPE_GET_MESSAGE_DETAILS
    private String trackerIdFilter; // For filtering by partial tracker ID match
    private String userFilter; // For filtering by user
    private String formatFilter; // For filtering by payload format (cXML, X12, EDIFACT)

    public TrackerMessageRequest(int requestType) {
        this.requestType = requestType;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isShowAuthNone() {
        return showAuthNone;
    }

    public void setShowAuthNone(boolean showAuthNone) {
        this.showAuthNone = showAuthNone;
    }

    public boolean isShowAuthSuccess() {
        return showAuthSuccess;
    }

    public void setShowAuthSuccess(boolean showAuthSuccess) {
        this.showAuthSuccess = showAuthSuccess;
    }

    public boolean isShowAuthFailed() {
        return showAuthFailed;
    }

    public void setShowAuthFailed(boolean showAuthFailed) {
        this.showAuthFailed = showAuthFailed;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    public String getTrackerIdFilter() {
        return trackerIdFilter;
    }

    public void setTrackerIdFilter(String trackerIdFilter) {
        this.trackerIdFilter = trackerIdFilter;
    }

    public String getUserFilter() {
        return userFilter;
    }

    public void setUserFilter(String userFilter) {
        this.userFilter = userFilter;
    }

    public String getFormatFilter() {
        return formatFilter;
    }

    public void setFormatFilter(String formatFilter) {
        this.formatFilter = formatFilter;
    }

    @Override
    public String toString() {
        return "TrackerMessageRequest [requestType=" + requestType + ", trackerId=" + trackerId + "]";
    }
}
