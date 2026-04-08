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

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.tracker.TrackerMessageAccessDB;
import de.mendelson.comm.as2.tracker.TrackerMessageInfo;
import de.mendelson.comm.as2.tracker.TrackerMessageStoreHandler;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server-side handler for tracker message requests
 *
 * @author Julian Xu
 */
public class TrackerMessageHandler {

    private final IDBDriverManager dbDriverManager;
    private final Logger logger = Logger.getLogger("de.mendelson.as2.server");

    public TrackerMessageHandler(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Process tracker message requests from client
     */
    public List<ClientServerMessage> processRequest(ClientServerMessage request) {
        List<ClientServerMessage> responseList = new ArrayList<>();

        if (request instanceof TrackerMessageRequest) {
            TrackerMessageRequest trackerRequest = (TrackerMessageRequest) request;
            TrackerMessageResponse response = new TrackerMessageResponse(trackerRequest);

            try {
                if (trackerRequest.getRequestType() == TrackerMessageRequest.TYPE_LIST_MESSAGES) {
                    // List messages with filters
                    TrackerMessageAccessDB dao = new TrackerMessageAccessDB(dbDriverManager);

                    List<TrackerMessageInfo> messages;
                    if (trackerRequest.getTrackerIdFilter() != null
                            && !trackerRequest.getTrackerIdFilter().trim().isEmpty()) {
                        // Use tracker ID filter
                        messages = dao.getTrackerMessagesByTrackerId(
                                trackerRequest.getTrackerIdFilter().trim());
                    } else {
                        // Use date/auth filters
                        messages = dao.getTrackerMessages(
                                trackerRequest.getStartDate(),
                                trackerRequest.getEndDate(),
                                trackerRequest.isShowAuthNone(),
                                trackerRequest.isShowAuthSuccess(),
                                trackerRequest.isShowAuthFailed()
                        );
                    }

                    response.setMessages(messages);

                } else if (trackerRequest.getRequestType() == TrackerMessageRequest.TYPE_GET_MESSAGE_DETAILS) {
                    // Get single message details
                    TrackerMessageAccessDB dao = new TrackerMessageAccessDB(dbDriverManager);
                    TrackerMessageInfo info = dao.getTrackerMessage(trackerRequest.getTrackerId());

                    response.setMessageDetails(info);

                    // Read message content from filesystem
                    if (info != null && info.getRawFilename() != null) {
                        try {
                            PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
                            TrackerMessageStoreHandler storeHandler = new TrackerMessageStoreHandler(prefs);
                            byte[] content = storeHandler.readTrackerMessage(info.getRawFilename());
                            response.setMessageContent(content);
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Failed to read tracker message content: " + e.getMessage(), e);
                            response.setException(new Exception("Failed to read message content: " + e.getMessage()));
                        }
                    }
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing tracker message request", e);
                response.setException(e);
            }

            responseList.add(response);
        }

        return responseList;
    }
}
