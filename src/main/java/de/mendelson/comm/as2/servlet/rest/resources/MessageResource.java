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

package de.mendelson.comm.as2.servlet.rest.resources;

import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.AS2Info;
import de.mendelson.comm.as2.message.MessageOverviewFilter;
import de.mendelson.comm.as2.message.clientserver.*;
import de.mendelson.comm.as2.log.LogEntry;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.sendorder.SendOrderSender;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.clientserver.message.DeleteMessageRequest;
import de.mendelson.comm.as2.timing.MessageDeleteController;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.comm.as2.usermanagement.Role;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

/**
 * REST API for message operations
 * Handles message listing, details, manual send, payload access
 *
 */
@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {

    /**
     * Get list of messages with optional filtering
     * Query parameters:
     * - startTime: Filter by start time (milliseconds since epoch)
     * - endTime: Filter by end time (milliseconds since epoch)
     * - limit: Maximum number of messages to return (default 1000)
     * - direction: Filter by direction (0=all, 1=inbound, 2=outbound)
     * - showFinished: Show finished messages (true/false, default true)
     * - showPending: Show pending messages (true/false, default true)
     * - showStopped: Show stopped messages (true/false, default true)
     * - partnerId: Filter by partner database ID
     * - localStationId: Filter by local station database ID
     * - userdefinedId: Filter by user-defined ID
     * - messageId: Get specific message by ID
     */
    @GET
    public Response listMessages(
            @Context SecurityContext securityContext,
            @QueryParam("startTime") Long startTime,
            @QueryParam("endTime") Long endTime,
            @QueryParam("limit") Integer limit,
            @QueryParam("direction") Integer direction,
            @QueryParam("showFinished") Boolean showFinished,
            @QueryParam("showPending") Boolean showPending,
            @QueryParam("showStopped") Boolean showStopped,
            @QueryParam("partnerId") Integer partnerId,
            @QueryParam("localStationId") Integer localStationId,
            @QueryParam("userdefinedId") String userdefinedId,
            @QueryParam("messageId") String messageId,
            @QueryParam("format") String format,
            @QueryParam("userId") Integer userFilterId) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        MessageOverviewRequest request;
        if (messageId != null) {
            // Get specific message
            request = new MessageOverviewRequest(messageId);
        } else {
            // Get filtered list
            MessageOverviewFilter filter = new MessageOverviewFilter();

            // Get current user's ID and role for partner visibility filtering
            int currentUserId = -1;
            boolean isAdmin = false;
            try {
                String username = securityContext.getUserPrincipal().getName();
                UserManagementAccessDB userMgmt = new UserManagementAccessDB(
                        processing.getDBDriverManager(), null);
                WebUIUser user = userMgmt.getUserByUsername(username);
                if (user != null) {
                    currentUserId = user.getId();

                    // Check if user has ADMIN role
                    List<Role> roles = userMgmt.getUserRoles(user.getId());
                    isAdmin = roles.stream()
                            .anyMatch(role -> "ADMIN".equalsIgnoreCase(role.getName()));

                    // If userFilterId is provided, use it for filtering (admin selected specific
                    // user)
                    // Otherwise, use currentUserId for non-admin users
                    if (userFilterId != null && userFilterId > 0) {
                        // Admin selected a specific user from dropdown
                        filter.setUserId(userFilterId);
                        filter.setAdmin(false); // Force filtering by this user
                    } else if (isAdmin) {
                        // Admin with no user filter - show all messages
                        filter.setUserId(currentUserId);
                        filter.setAdmin(true);
                    } else {
                        // Non-admin user - only see their own messages
                        filter.setUserId(currentUserId);
                        filter.setAdmin(false);
                    }
                }
            } catch (Exception e) {
                // If we can't get user info, treat as non-admin with no user context
                // This will show all messages (fallback for compatibility)
            }

            if (startTime != null) {
                filter.setStartTime(startTime);
            }

            if (endTime != null) {
                filter.setEndTime(endTime);
            }

            if (limit != null) {
                filter.setLimit(limit);
            }

            if (direction != null) {
                filter.setShowDirection(direction);
            }

            if (showFinished != null) {
                filter.setShowFinished(showFinished);
            }

            if (showPending != null) {
                filter.setShowPending(showPending);
            }

            if (showStopped != null) {
                filter.setShowStopped(showStopped);
            }

            if (userdefinedId != null && !userdefinedId.isEmpty()) {
                filter.setUserdefinedId(userdefinedId);
            }

            if (format != null && !format.isEmpty()) {
                filter.setPayloadFormat(format);
            }

            // Handle partner and local station filters
            if (partnerId != null || localStationId != null) {
                PartnerAccessDB partnerAccess = new PartnerAccessDB(processing.getDBDriverManager());

                if (partnerId != null) {
                    try {
                        Partner partner = partnerAccess.getPartner(partnerId);
                        filter.setShowPartner(partner);
                    } catch (Exception e) {
                        // Partner not found, ignore filter
                    }
                }

                if (localStationId != null) {
                    try {
                        Partner localStation = partnerAccess.getPartner(localStationId);
                        filter.setShowLocalStation(localStation);
                    } catch (Exception e) {
                        // Local station not found, ignore filter
                    }
                }
            }

            request = new MessageOverviewRequest(filter);

            // CRITICAL: Copy user context from filter to request for message ownership
            // filtering
            // The filter is used for partner visibility, but request.userId is checked for
            // message ownership
            if (currentUserId > 0) {
                request.setUserId(currentUserId);
                // ADMIN users can see all messages (similar to USER_MANAGE permission)
                // This allows admin users to monitor all AS2 message traffic
                request.setHasUserManagePermission(isAdmin);
            }
        }

        MessageOverviewResponse response = processing.processMessageOverviewRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        MessageListResponse dto = new MessageListResponse();
        dto.setMessages(response.getList());
        dto.setTotalCount(response.getMessageSumOnServer());

        return Response.ok(dto).build();
    }

    /**
     * Get message transaction log
     */
    @GET
    @Path("/{messageId}/log")
    public Response getMessageLog(@PathParam("messageId") String messageId) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        MessageLogRequest request = new MessageLogRequest(messageId);
        MessageLogResponse response = processing.processMessageLogRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        List<LogEntry> logs = response.getList();

        return Response.ok(logs != null ? logs : new java.util.ArrayList<>()).build();
    }

    /**
     * Get detailed message information
     */
    @GET
    @Path("/{messageId}/details")
    public Response getMessageDetails(@PathParam("messageId") String messageId) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        MessageDetailRequest request = new MessageDetailRequest(messageId);
        MessageDetailResponse response = processing.processMessageDetailRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        List<?> details = response.getList();

        return Response.ok(details != null ? details : new java.util.ArrayList<>()).build();
    }

    /**
     * Download a specific payload from a message
     * 
     * @param messageId      The overview message ID
     * @param entryMessageId Optional - specific entry's message ID (for selecting
     *                       MSG vs MDN)
     * @param payloadIndex   Optional - which payload to download (0-based, default
     *                       0)
     */
    @GET
    @Path("/{messageId}/payload")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getMessagePayload(
            @PathParam("messageId") String messageId,
            @QueryParam("entryMessageId") String entryMessageId,
            @QueryParam("index") Integer payloadIndex) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        // Use entryMessageId if provided, otherwise use messageId
        String targetMessageId = (entryMessageId != null && !entryMessageId.isEmpty()) ? entryMessageId : messageId;

        MessagePayloadRequest request = new MessagePayloadRequest(targetMessageId);
        MessagePayloadResponse response = processing.processMessagePayloadRequest(request);

        if (response.getException() != null) {
            System.err.println("ERROR: Exception in payload response: " + response.getException().getMessage());
            response.getException().printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        List<AS2Payload> payloads = response.getList();

        if (payloads == null || payloads.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("No payload found for message"))
                    .build();
        }

        // Get the requested payload by index (default to 0)
        int index = (payloadIndex != null && payloadIndex >= 0) ? payloadIndex : 0;

        if (index >= payloads.size()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Payload index " + index + " not found (only " + payloads.size()
                            + " payload(s) available)"))
                    .build();
        }

        AS2Payload payload = payloads.get(index);
        byte[] data;
        try {
            data = payload.getData();

            // If getData() returns empty but payloadFilename exists, try reading from file
            if ((data == null || data.length == 0) && payload.getPayloadFilename() != null) {
                try {
                    java.nio.file.Path payloadFile = java.nio.file.Paths.get(payload.getPayloadFilename());
                    if (java.nio.file.Files.exists(payloadFile)) {
                        data = java.nio.file.Files.readAllBytes(payloadFile);
                    }
                } catch (Exception fileEx) {
                    System.err.println("ERROR: Failed to read payload file: " + fileEx.getMessage());
                    fileEx.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to get payload data: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error reading payload: " + e.getMessage()))
                    .build();
        }

        if (data == null || data.length == 0) {
            System.err.println("ERROR: Payload data is null or empty");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Payload data not available"))
                    .build();
        }

        String contentType = payload.getContentType();
        String filename = payload.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            filename = "payload_" + index + ".dat";
        }

        return Response.ok(new ByteArrayInputStream(data))
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .type(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM)
                .build();
    }

    /**
     * Get all message payloads metadata (not the actual data, just info)
     */
    @GET
    @Path("/{messageId}/payloads")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessagePayloads(
            @PathParam("messageId") String messageId,
            @QueryParam("entryMessageId") String entryMessageId) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        // Use entryMessageId if provided, otherwise use messageId
        String targetMessageId = (entryMessageId != null && !entryMessageId.isEmpty()) ? entryMessageId : messageId;
        MessagePayloadRequest request = new MessagePayloadRequest(targetMessageId);
        MessagePayloadResponse response = processing.processMessagePayloadRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        List<AS2Payload> payloads = response.getList();

        if (payloads == null || payloads.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("No payload found for message"))
                    .build();
        }

        // Create simplified payload info without the actual data
        java.util.List<PayloadInfo> payloadInfos = new java.util.ArrayList<>();
        for (int i = 0; i < payloads.size(); i++) {
            AS2Payload payload = payloads.get(i);
            PayloadInfo info = new PayloadInfo();
            info.setIndex(i);
            info.setContentType(payload.getContentType());
            info.setOriginalFilename(payload.getOriginalFilename());

            try {
                byte[] data = payload.getData();
                info.setSize(data != null ? data.length : 0);

                // If getData() returns empty but payloadFilename exists, try reading from file
                if ((data == null || data.length == 0) && payload.getPayloadFilename() != null) {
                    try {
                        java.nio.file.Path payloadFile = java.nio.file.Paths.get(payload.getPayloadFilename());
                        if (java.nio.file.Files.exists(payloadFile)) {
                            data = java.nio.file.Files.readAllBytes(payloadFile);
                            info.setSize(data.length);
                        }
                    } catch (Exception fileEx) {
                        System.err.println("ERROR: Failed to read payload " + i + " from file: " + fileEx.getMessage());
                    }
                }

                // Get preview of data (first 5000 chars for text types)
                if (data != null && data.length > 0 && payload.getContentType() != null &&
                        (payload.getContentType().startsWith("text/") ||
                                payload.getContentType().toLowerCase().contains("xml") ||
                                payload.getContentType().toLowerCase().contains("json") ||
                                payload.getContentType().toLowerCase().contains("edi") ||
                                payload.getContentType().toLowerCase().contains("x12"))) {
                    String preview = new String(data, 0, Math.min(data.length, 5000),
                            java.nio.charset.StandardCharsets.UTF_8);
                    info.setPreview(preview);
                    info.setIsText(true);
                } else {
                    info.setIsText(false);
                    info.setPreview("[Binary data - " + (data != null ? data.length : 0) + " bytes]");
                }
            } catch (Exception e) {
                System.err.println("ERROR: Failed to read payload " + i + ": " + e.getMessage());
                e.printStackTrace();
                info.setSize(0);
                info.setIsText(false);
                info.setPreview("[Error reading payload: " + e.getMessage() + "]");
            }
            payloadInfos.add(info);
        }

        return Response.ok(payloadInfos).build();
    }

    /**
     * Get message header (raw HTTP headers)
     * 
     * @param messageId      The overview message ID
     * @param entryMessageId Optional - specific entry's message ID (for MDN
     *                       selection)
     */
    @GET
    @Path("/{messageId}/header")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessageHeader(
            @PathParam("messageId") String messageId,
            @QueryParam("entryMessageId") String entryMessageId) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        try {
            MessageDetailRequest request = new MessageDetailRequest(messageId);
            MessageDetailResponse response = processing.processMessageDetailRequest(request);

            if (response.getException() != null) {
                RawDataResponse headerResponse = new RawDataResponse();
                headerResponse.setRawData("Error: " + response.getException().getMessage());
                headerResponse.setIsBase64(false);
                return Response.ok(headerResponse).build();
            }

            List<?> details = response.getList();
            if (details == null || details.isEmpty()) {
                RawDataResponse headerResponse = new RawDataResponse();
                headerResponse.setRawData("No header data available");
                headerResponse.setIsBase64(false);
                return Response.ok(headerResponse).build();
            }

            // Find the matching detail entry
            AS2Info targetInfo = null;
            String searchMessageId = (entryMessageId != null && !entryMessageId.isEmpty()) ? entryMessageId : messageId;

            for (Object obj : details) {
                AS2Info info = (AS2Info) obj;
                if (searchMessageId.equals(info.getMessageId())) {
                    targetInfo = info;
                    break;
                }
            }

            if (targetInfo == null) {
                RawDataResponse headerResponse = new RawDataResponse();
                headerResponse.setRawData("No matching detail found for message ID: " + messageId);
                headerResponse.setIsBase64(false);
                return Response.ok(headerResponse).build();
            }

            String headerFilename = targetInfo.getHeaderFilename();

            if (headerFilename == null || headerFilename.isEmpty()) {
                RawDataResponse headerResponse = new RawDataResponse();
                headerResponse.setRawData("No header file available");
                headerResponse.setIsBase64(false);
                return Response.ok(headerResponse).build();
            }

            // Read header file content
            java.nio.file.Path headerFile = java.nio.file.Paths.get(headerFilename);
            if (!java.nio.file.Files.exists(headerFile)) {
                RawDataResponse headerResponse = new RawDataResponse();
                headerResponse.setRawData("Header file not found: " + headerFilename);
                headerResponse.setIsBase64(false);
                return Response.ok(headerResponse).build();
            }

            byte[] headerData = java.nio.file.Files.readAllBytes(headerFile);
            String headerContent = new String(headerData, java.nio.charset.StandardCharsets.UTF_8);

            RawDataResponse headerResponse = new RawDataResponse();
            headerResponse.setRawData(headerContent);
            headerResponse.setIsBase64(false);
            return Response.ok(headerResponse).build();

        } catch (Exception e) {
            System.err.println("ERROR: Failed to get message header: " + e.getMessage());
            e.printStackTrace();
            RawDataResponse headerResponse = new RawDataResponse();
            headerResponse.setRawData("Error: " + e.getMessage());
            headerResponse.setIsBase64(false);
            return Response.ok(headerResponse).build();
        }
    }

    /**
     * Get raw message data (unencrypted)
     * 
     * @param messageId      The overview message ID
     * @param entryMessageId Optional - specific entry's message ID (for MDN
     *                       selection)
     */
    @GET
    @Path("/{messageId}/raw")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessageRawData(
            @PathParam("messageId") String messageId,
            @QueryParam("entryMessageId") String entryMessageId) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            System.err.println("ERROR: Server processing not available");
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        try {
            // Get message/MDN details first to find raw filename
            MessageDetailRequest detailRequest = new MessageDetailRequest(messageId);
            MessageDetailResponse detailResponse = processing.processMessageDetailRequest(detailRequest);

            if (detailResponse.getException() != null) {
                System.err
                        .println("ERROR: Exception in detail response: " + detailResponse.getException().getMessage());
                RawDataResponse rawResponse = new RawDataResponse();
                rawResponse.setRawData("Error: " + detailResponse.getException().getMessage());
                rawResponse.setIsBase64(false);
                return Response.ok(rawResponse).build();
            }

            List<?> details = detailResponse.getList();
            if (details == null || details.isEmpty()) {
                System.err.println("ERROR: No details found for message: " + messageId);
                RawDataResponse rawResponse = new RawDataResponse();
                rawResponse.setRawData("No details found for this message");
                rawResponse.setIsBase64(false);
                return Response.ok(rawResponse).build();
            }

            // Find the matching detail entry
            AS2Info targetInfo = null;
            String searchMessageId = (entryMessageId != null && !entryMessageId.isEmpty()) ? entryMessageId : messageId;

            for (Object obj : details) {
                AS2Info info = (AS2Info) obj;
                if (searchMessageId.equals(info.getMessageId())) {
                    targetInfo = info;
                    break;
                }
            }

            if (targetInfo == null) {
                System.err.println("ERROR: No matching detail found for message ID: " + messageId);
                RawDataResponse rawResponse = new RawDataResponse();
                rawResponse.setRawData("No matching detail found");
                rawResponse.setIsBase64(false);
                return Response.ok(rawResponse).build();
            }

            // Get raw filename - different logic for messages vs MDNs
            String rawFilename = null;
            if (!targetInfo.isMDN()) {
                AS2MessageInfo messageInfo = (AS2MessageInfo) targetInfo;
                if (messageInfo.getRawFilenameDecrypted() != null) {
                    rawFilename = messageInfo.getRawFilenameDecrypted();
                } else if (messageInfo.getRawFilename() != null) {
                    rawFilename = messageInfo.getRawFilename();
                }
            } else {
                // MDN - just use raw filename
                if (targetInfo.getRawFilename() != null) {
                    rawFilename = targetInfo.getRawFilename();
                }
            }

            if (rawFilename == null || rawFilename.isEmpty()) {
                System.err.println("ERROR: No raw filename available");
                RawDataResponse rawResponse = new RawDataResponse();
                rawResponse.setRawData("No raw data file available");
                rawResponse.setIsBase64(false);
                return Response.ok(rawResponse).build();
            }

            // Read raw file content
            java.nio.file.Path rawFile = java.nio.file.Paths.get(rawFilename);
            if (!java.nio.file.Files.exists(rawFile)) {
                System.err.println("ERROR: Raw file not found: " + rawFilename);
                RawDataResponse rawResponse = new RawDataResponse();
                rawResponse.setRawData("Raw file not found: " + rawFilename);
                rawResponse.setIsBase64(false);
                return Response.ok(rawResponse).build();
            }

            byte[] rawData = java.nio.file.Files.readAllBytes(rawFile);
            long fileSize = rawData.length;

            // Limit preview size to 100KB to prevent browser crashes
            int maxPreviewSize = 100 * 1024; // 100KB
            boolean isTruncated = rawData.length > maxPreviewSize;
            byte[] previewData = isTruncated
                    ? java.util.Arrays.copyOf(rawData, maxPreviewSize)
                    : rawData;

            // Check if data is binary or text
            boolean isBinary = false;
            for (int i = 0; i < Math.min(previewData.length, 1000); i++) {
                byte b = previewData[i];
                if (b == 0 || (b < 32 && b != 9 && b != 10 && b != 13)) {
                    isBinary = true;
                    break;
                }
            }

            RawDataResponse rawResponse = new RawDataResponse();
            rawResponse.setFileSize(fileSize);
            rawResponse.setTruncated(isTruncated);

            if (isBinary) {
                // Encode as base64 for binary data
                String base64Data = java.util.Base64.getEncoder().encodeToString(previewData);
                rawResponse.setRawData(base64Data);
                rawResponse.setIsBase64(true);
            } else {
                // Return as text
                String textData = new String(previewData, java.nio.charset.StandardCharsets.UTF_8);
                if (isTruncated) {
                    textData += "\n\n[... Truncated - showing first " + maxPreviewSize + " bytes of " + fileSize
                            + " bytes total. Use 'Save to File' to download complete data ...]";
                }
                rawResponse.setRawData(textData);
                rawResponse.setIsBase64(false);
            }

            return Response.ok(rawResponse).build();

        } catch (Exception e) {
            System.err.println("ERROR: Failed to get raw data: " + e.getMessage());
            e.printStackTrace();
            RawDataResponse rawResponse = new RawDataResponse();
            rawResponse.setRawData("Error: " + e.getMessage());
            rawResponse.setIsBase64(false);
            return Response.ok(rawResponse).build();
        }
    }

    /**
     * Download encrypted raw AS2 message
     * Returns the raw encrypted AS2 message as received/sent (before decryption)
     */
    @GET
    @Path("/{messageId}/download/encrypted")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadEncryptedRaw(
            @PathParam("messageId") String messageId,
            @QueryParam("entryMessageId") String entryMessageId) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        try {
            // Get message details to find raw filename
            MessageDetailRequest detailRequest = new MessageDetailRequest(messageId);
            MessageDetailResponse detailResponse = processing.processMessageDetailRequest(detailRequest);

            if (detailResponse.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(detailResponse.getException().getMessage()))
                        .build();
            }

            List<?> details = detailResponse.getList();
            if (details == null || details.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No details found for this message"))
                        .build();
            }

            // Find the matching detail entry
            AS2Info targetInfo = null;
            String searchMessageId = (entryMessageId != null && !entryMessageId.isEmpty()) ? entryMessageId : messageId;

            for (Object obj : details) {
                AS2Info info = (AS2Info) obj;
                if (searchMessageId.equals(info.getMessageId())) {
                    targetInfo = info;
                    break;
                }
            }

            if (targetInfo == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No matching detail found"))
                        .build();
            }

            // Get raw encrypted filename
            String rawFilename = targetInfo.getRawFilename();
            if (rawFilename == null || rawFilename.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No encrypted raw data available"))
                        .build();
            }

            // Read raw file content
            java.nio.file.Path rawFile = java.nio.file.Paths.get(rawFilename);
            if (!java.nio.file.Files.exists(rawFile)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Encrypted raw file not found"))
                        .build();
            }

            byte[] rawData = java.nio.file.Files.readAllBytes(rawFile);

            // Generate filename for download
            String filename = messageId + "_encrypted_raw.dat";

            return Response.ok(rawData)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .header("Content-Type", "application/octet-stream")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to download encrypted raw data: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Download decrypted raw AS2 message
     * Returns the decrypted AS2 message (after removing encryption layer)
     */
    @GET
    @Path("/{messageId}/download/decrypted")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadDecryptedRaw(
            @PathParam("messageId") String messageId,
            @QueryParam("entryMessageId") String entryMessageId) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        try {
            // Get message details to find raw decrypted filename
            MessageDetailRequest detailRequest = new MessageDetailRequest(messageId);
            MessageDetailResponse detailResponse = processing.processMessageDetailRequest(detailRequest);

            if (detailResponse.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(detailResponse.getException().getMessage()))
                        .build();
            }

            List<?> details = detailResponse.getList();
            if (details == null || details.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No details found for this message"))
                        .build();
            }

            // Find the matching detail entry
            AS2Info targetInfo = null;
            String searchMessageId = (entryMessageId != null && !entryMessageId.isEmpty()) ? entryMessageId : messageId;

            for (Object obj : details) {
                AS2Info info = (AS2Info) obj;
                if (searchMessageId.equals(info.getMessageId())) {
                    targetInfo = info;
                    break;
                }
            }

            if (targetInfo == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No matching detail found"))
                        .build();
            }

            // Get raw decrypted filename (only available for messages, not MDNs)
            String rawDecryptedFilename = null;
            if (!targetInfo.isMDN()) {
                AS2MessageInfo messageInfo = (AS2MessageInfo) targetInfo;
                rawDecryptedFilename = messageInfo.getRawFilenameDecrypted();
            }

            if (rawDecryptedFilename == null || rawDecryptedFilename.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No decrypted raw data available (message may not be encrypted)"))
                        .build();
            }

            // Read raw decrypted file content
            java.nio.file.Path rawFile = java.nio.file.Paths.get(rawDecryptedFilename);
            if (!java.nio.file.Files.exists(rawFile)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Decrypted raw file not found"))
                        .build();
            }

            byte[] rawData = java.nio.file.Files.readAllBytes(rawFile);

            // Generate filename for download
            String filename = messageId + "_decrypted_raw.dat";

            return Response.ok(rawData)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .header("Content-Type", "application/octet-stream")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to download decrypted raw data: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get last message entry for a message
     */
    @GET
    @Path("/{messageId}/last")
    public Response getLastMessage(@PathParam("messageId") String messageId) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        MessageRequestLastMessage request = new MessageRequestLastMessage(messageId);
        MessageResponseLastMessage response = processing.processMessageRequestLastMessage(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        return Response.ok(response.getInfo()).build();
    }

    /**
     * Manual send file(s) to partner
     * Accepts multipart/form-data with:
     * - files: One or more files to send (first file is main payload, others are
     * attachments)
     * - senderId: Database ID of sender partner (local station)
     * - receiverId: Database ID of receiver partner
     * - subject: Optional message subject
     * - contentType: Optional payload content type (applies to first file)
     */
    @POST
    @Path("/send")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response sendMessage(
            @Context HttpServletRequest request,
            @Context SecurityContext securityContext,
            @FormDataParam("files") List<FormDataBodyPart> fileParts,
            @FormDataParam("senderId") String senderId,
            @FormDataParam("receiverId") String receiverId,
            @FormDataParam("subject") String subject,
            @FormDataParam("contentType") String contentType) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        // Get current WebUI user ID for HTTP auth preferences and partner filtering
        int currentUserId = -1;
        boolean isAdmin = false;
        try {
            String username = securityContext.getUserPrincipal().getName();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(
                    processing.getDBDriverManager(),
                    java.util.logging.Logger.getLogger(MessageResource.class.getName()));
            WebUIUser user = userMgmt.getUserByUsername(username);
            if (user != null) {
                currentUserId = user.getId();
                // Check if user has ADMIN role
                List<Role> roles = userMgmt.getUserRoles(user.getId());
                for (Role role : roles) {
                    if ("ADMIN".equalsIgnoreCase(role.getName())) {
                        isAdmin = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not resolve user ID for HTTP auth: " + e.getMessage());
        }

        // Validate input
        if (fileParts == null || fileParts.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("At least one file is required"))
                    .build();
        }

        if (senderId == null || senderId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Sender ID is required"))
                    .build();
        }

        if (receiverId == null || receiverId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Receiver ID is required"))
                    .build();
        }

        try {
            // Get partner list to find AS2 IDs from database IDs
            // IMPORTANT: Admin users see ALL partners (userId=-1), regular users see only
            // their own
            PartnerListRequest partnerRequest = new PartnerListRequest();
            partnerRequest.setUserId(isAdmin ? -1 : currentUserId);
            PartnerListResponse partnerResponse = processing.processPartnerListRequest(partnerRequest);

            if (partnerResponse.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(
                                "Failed to get partner list: " + partnerResponse.getException().getMessage()))
                        .build();
            }

            // Find sender and receiver partners by database ID
            Partner sender = null;
            Partner receiver = null;
            int senderDbId = Integer.parseInt(senderId);
            int receiverDbId = Integer.parseInt(receiverId);

            for (Partner partner : partnerResponse.getList()) {
                if (partner.getDBId() == senderDbId) {
                    sender = partner;
                }
                if (partner.getDBId() == receiverDbId) {
                    receiver = partner;
                }
            }

            if (sender == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Sender partner not found with ID: " + senderId))
                        .build();
            }

            if (receiver == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Receiver partner not found with ID: " + receiverId))
                        .build();
            }

            // Save all uploaded files to persistent temp directory
            // IMPORTANT: Do NOT delete these files - SendOrderReceiver will read them
            // asynchronously
            // and clean them up after processing
            java.nio.file.Path tempDir = java.nio.file.Paths.get("temp");
            if (!java.nio.file.Files.exists(tempDir)) {
                java.nio.file.Files.createDirectories(tempDir);
            }

            java.nio.file.Path[] sendFiles = new java.nio.file.Path[fileParts.size()];
            String[] originalFilenames = new String[fileParts.size()];
            String[] payloadContentTypes = new String[fileParts.size()];

            for (int i = 0; i < fileParts.size(); i++) {
                FormDataBodyPart part = fileParts.get(i);
                FormDataContentDisposition fileDetail = part.getFormDataContentDisposition();
                String originalFilename = fileDetail.getFileName();

                // Get entity as byte array directly
                byte[] fileBytes = part.getValueAs(byte[].class);

                // Save to persistent temp directory (not Java's temp dir which may be cleaned
                // up)
                // Use timestamp + random to ensure uniqueness
                String tempFilename = "webui_upload_" + System.currentTimeMillis() + "_" +
                        (int) (Math.random() * 100000) + "_" + originalFilename;
                java.nio.file.Path tempFile = tempDir.resolve(tempFilename);
                java.nio.file.Files.write(tempFile, fileBytes);

                sendFiles[i] = tempFile;
                originalFilenames[i] = originalFilename;

                // First file uses specified content type, others auto-detect from filename
                if (i == 0 && contentType != null && !contentType.isEmpty()) {
                    payloadContentTypes[i] = contentType;
                } else {
                    // Auto-detect content type from file extension
                    payloadContentTypes[i] = detectContentType(originalFilenames[i]);
                }
            }

            // Send the message using SendOrderSender with userId
            SendOrderSender orderSender = processing.getSendOrderSender();

            de.mendelson.comm.as2.sendorder.SendResult sendResult = orderSender.sendWithResult(
                    processing.getCertificateManagerSignEncrypt(),
                    sender,
                    receiver,
                    sendFiles,
                    originalFilenames,
                    null, // userdefinedId
                    subject,
                    payloadContentTypes,
                    currentUserId // userId for HTTP auth preferences
            );

            if (sendResult.isFailure()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Failed to send message: " + sendResult.getErrorMessage()))
                        .build();
            }

            // Broadcast refresh to connected clients (if ClientServer is available)
            if (processing.getClientserver() != null) {
                processing.getClientserver().broadcastToClients(new RefreshClientMessageOverviewList());
            }

            // Generate response message
            String messageId = sendResult.hasMessage()
                    ? ((AS2MessageInfo) sendResult.getMessage().getAS2Info()).getMessageId()
                    : "order-" + sendResult.getOrderId(); // For IN_MEMORY, use order ID
            String successMsg = fileParts.size() == 1
                    ? "File sent successfully"
                    : fileParts.size() + " files sent successfully (1 main + " + (fileParts.size() - 1) + " attachment"
                            + (fileParts.size() > 2 ? "s" : "") + ")";

            return Response.ok(new SendSuccessResponse(successMsg, messageId)).build();

        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid partner ID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error processing file: " + e.getMessage()))
                    .build();
        }
        // NOTE: Do NOT delete temp files here!
        // The SendOrderReceiver processes send orders asynchronously in IN_MEMORY mode,
        // so files are read AFTER this method returns. The SendOrderReceiver will
        // clean up the temp files after successfully reading them.
    }

    /**
     * DTO for message list response
     */
    public static class MessageListResponse {
        private List<AS2MessageInfo> messages;
        private int totalCount;

        public List<AS2MessageInfo> getMessages() {
            return messages;
        }

        public void setMessages(List<AS2MessageInfo> messages) {
            this.messages = messages;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }

    /**
     * DTO for error responses
     */
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    /**
     * DTO for success responses
     */
    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * DTO for send success responses with message ID
     */
    public static class SendSuccessResponse {
        private String message;
        private String messageId;

        public SendSuccessResponse(String message, String messageId) {
            this.message = message;
            this.messageId = messageId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }
    }

    /**
     * Delete a message by ID
     * 
     * @param messageId The message ID to delete
     */
    @DELETE
    @Path("/{messageId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMessage(@PathParam("messageId") String messageId) {
        System.out.println("Message ID: " + messageId);

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        try {
            // First, get the message info
            MessageDetailRequest detailRequest = new MessageDetailRequest(messageId);
            MessageDetailResponse detailResponse = processing.processMessageDetailRequest(detailRequest);

            if (detailResponse.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(detailResponse.getException().getMessage()))
                        .build();
            }

            List<?> details = detailResponse.getList();
            if (details == null || details.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Message not found"))
                        .build();
            }

            // Build delete list - include all entries (MSG and MDN)
            List<AS2MessageInfo> deleteList = new java.util.ArrayList<>();
            for (Object obj : details) {
                AS2Info info = (AS2Info) obj;
                if (!info.isMDN()) {
                    deleteList.add((AS2MessageInfo) info);
                }
            }

            if (deleteList.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No deletable messages found"))
                        .build();
            }

            // Create and process delete request
            DeleteMessageRequest deleteRequest = new DeleteMessageRequest();
            deleteRequest.setDeleteList(deleteList);

            // Use MessageDeleteController directly
            MessageDeleteController controller = new MessageDeleteController(
                    null, processing.getDBDriverManager());
            StringBuilder deleteLog = new StringBuilder();
            controller.deleteMessagesFromLog(deleteList, true, deleteLog);

            // Broadcast refresh to connected clients (if ClientServer is available)
            if (processing.getClientserver() != null) {
                processing.getClientserver().broadcastToClients(new RefreshClientMessageOverviewList());
            }

            return Response.ok(new DeleteSuccessResponse("Message deleted successfully")).build();

        } catch (Exception e) {
            System.err.println("ERROR: Failed to delete message: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error deleting message: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * DTO for delete success response
     */
    public static class DeleteSuccessResponse {
        private String message;

        public DeleteSuccessResponse() {
        }

        public DeleteSuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * DTO for payload information
     */
    public static class PayloadInfo {
        private int index;
        private String contentType;
        private String originalFilename;
        private long size;
        private String preview;
        private boolean isText;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }

        public void setOriginalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public boolean getIsText() {
            return isText;
        }

        public void setIsText(boolean isText) {
            this.isText = isText;
        }
    }

    /**
     * DTO for raw data response
     */
    public static class RawDataResponse {
        private String rawData;
        private boolean isBase64;
        private long fileSize;
        private boolean truncated;

        public String getRawData() {
            return rawData;
        }

        public void setRawData(String rawData) {
            this.rawData = rawData;
        }

        public boolean getIsBase64() {
            return isBase64;
        }

        public void setIsBase64(boolean isBase64) {
            this.isBase64 = isBase64;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public boolean isTruncated() {
            return truncated;
        }

        public void setTruncated(boolean truncated) {
            this.truncated = truncated;
        }
    }

    /**
     * Auto-detect content type from filename extension
     */
    private String detectContentType(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }

        String lowerFilename = filename.toLowerCase();

        // EDI formats
        if (lowerFilename.endsWith(".edi") || lowerFilename.endsWith(".x12")) {
            return "application/EDI-X12";
        }
        if (lowerFilename.endsWith(".edifact")) {
            return "application/EDIFACT";
        }

        // XML formats
        if (lowerFilename.endsWith(".xml")) {
            return "application/xml";
        }

        // Text formats
        if (lowerFilename.endsWith(".txt") || lowerFilename.endsWith(".text")) {
            return "text/plain";
        }
        if (lowerFilename.endsWith(".csv")) {
            return "text/csv";
        }
        if (lowerFilename.endsWith(".html") || lowerFilename.endsWith(".htm")) {
            return "text/html";
        }

        // JSON
        if (lowerFilename.endsWith(".json")) {
            return "application/json";
        }

        // Document formats
        if (lowerFilename.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (lowerFilename.endsWith(".doc")) {
            return "application/msword";
        }
        if (lowerFilename.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        if (lowerFilename.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        }
        if (lowerFilename.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }

        // Image formats
        if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lowerFilename.endsWith(".png")) {
            return "image/png";
        }
        if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        }
        if (lowerFilename.endsWith(".bmp")) {
            return "image/bmp";
        }

        // Archive formats
        if (lowerFilename.endsWith(".zip")) {
            return "application/zip";
        }
        if (lowerFilename.endsWith(".gz") || lowerFilename.endsWith(".gzip")) {
            return "application/gzip";
        }
        if (lowerFilename.endsWith(".tar")) {
            return "application/x-tar";
        }

        // Default for unknown types
        return "application/octet-stream";
    }
}
