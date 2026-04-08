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

import de.mendelson.comm.as2.tracker.TrackerMessageInfo;
import de.mendelson.util.clientserver.messages.ClientServerResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Response containing tracker messages from server
 *
 * @author Julian Xu
 */
public class TrackerMessageResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;

    private List<TrackerMessageInfo> messages;
    private TrackerMessageInfo messageDetails; // For single message request
    private byte[] messageContent; // Raw message content

    public TrackerMessageResponse(TrackerMessageRequest request) {
        super(request);
    }

    public List<TrackerMessageInfo> getMessages() {
        return messages;
    }

    public void setMessages(List<TrackerMessageInfo> messages) {
        this.messages = messages;
    }

    public TrackerMessageInfo getMessageDetails() {
        return messageDetails;
    }

    public void setMessageDetails(TrackerMessageInfo messageDetails) {
        this.messageDetails = messageDetails;
    }

    public byte[] getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(byte[] messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public String toString() {
        return "TrackerMessageResponse [messages=" + (messages != null ? messages.size() : 0) + " items]";
    }
}
