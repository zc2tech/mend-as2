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

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.tracker.TrackerMessageAccessDB;
import de.mendelson.comm.as2.tracker.TrackerMessageInfo;
import de.mendelson.comm.as2.tracker.TrackerMessageStoreHandler;
import de.mendelson.comm.as2.usermanagement.Role;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.database.IDBDriverManager;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * REST resource for tracker messages
 */
@Path("/tracker-messages")
public class TrackerMessageResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrackerMessages(
            @Context SecurityContext securityContext,
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr,
            @QueryParam("trackerId") String trackerId,
            @QueryParam("user") String userFilter,
            @QueryParam("format") String formatFilter,
            @QueryParam("authNone") @DefaultValue("true") boolean authNone,
            @QueryParam("authSuccess") @DefaultValue("true") boolean authSuccess) {

        try {
            // Get username from security context (set by JWT filter)
            String username = securityContext.getUserPrincipal().getName();

            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
            TrackerMessageAccessDB dao = new TrackerMessageAccessDB(dbDriverManager, prefs);
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            // Get current user
            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            List<TrackerMessageInfo> messages;

            // Check if user is ADMIN
            boolean isAdmin = false;
            List<Role> userRoles = userMgmt.getUserRoles(currentUser.getId());
            for (Role role : userRoles) {
                if ("ADMIN".equals(role.getName())) {
                    isAdmin = true;
                    break;
                }
            }

            // Non-admin users can only see their own tracker messages
            String effectiveUserFilter = userFilter;

            if (!isAdmin) {
                // Non-admin users can only see their own messages
                effectiveUserFilter = currentUser.getUsername();
            }

            // Search by tracker ID if provided
            if (trackerId != null && !trackerId.trim().isEmpty()) {
                messages = dao.getTrackerMessagesByTrackerId(trackerId.trim());

                // Apply user filter if needed
                if (effectiveUserFilter != null && !effectiveUserFilter.trim().isEmpty()) {
                    String finalUserFilter = effectiveUserFilter.trim().toLowerCase();
                    messages = messages.stream()
                            .filter(m -> m.getAuthUser() != null &&
                                   m.getAuthUser().toLowerCase().contains(finalUserFilter))
                            .collect(java.util.stream.Collectors.toList());
                }
            } else {
                // Search by date range
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdf.parse(startDateStr);
                Date endDate = sdf.parse(endDateStr);

                // Set end date to end of day
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(endDate);
                cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
                cal.set(java.util.Calendar.MINUTE, 59);
                cal.set(java.util.Calendar.SECOND, 59);
                cal.set(java.util.Calendar.MILLISECOND, 999);
                endDate = cal.getTime();

                messages = dao.getTrackerMessages(startDate, endDate, authNone, authSuccess, false,
                        effectiveUserFilter, formatFilter);
            }

            // Convert to DTOs
            List<TrackerMessageDTO> dtos = new ArrayList<>();
            for (TrackerMessageInfo info : messages) {
                TrackerMessageDTO dto = new TrackerMessageDTO();
                dto.setTrackerId(info.getTrackerId());
                dto.setTimestamp(info.getInitDate());
                dto.setRemoteAddr(info.getRemoteAddr());
                dto.setUserAgent(info.getUserAgent());
                dto.setContentType(info.getContentType());
                dto.setContentSize(info.getContentSize());
                dto.setAuthStatus(info.getAuthStatusText());
                dto.setAuthUser(info.getAuthUser());
                dto.setPayloadCount(info.getPayloadCount());
                dto.setPayloadFormat(info.getPayloadFormat());
                dto.setPayloadDocType(info.getPayloadDocType());
                dto.setPayloadDetails(info.getPayloadDetails());
                dtos.add(dto);
            }

            return Response.ok(dtos).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{trackerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrackerMessageDetails(
            @Context SecurityContext securityContext,
            @PathParam("trackerId") String trackerId) {
        try {
            // Get username from security context
            String username = securityContext.getUserPrincipal().getName();

            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
            TrackerMessageAccessDB dao = new TrackerMessageAccessDB(dbDriverManager, prefs);
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            // Get current user
            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            TrackerMessageInfo info = dao.getTrackerMessage(trackerId);
            if (info == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Message not found\"}").build();
            }

            // Check permissions - non-admin users can only see their own messages
            boolean isAdmin = false;
            List<Role> userRoles = userMgmt.getUserRoles(currentUser.getId());
            for (Role role : userRoles) {
                if ("ADMIN".equals(role.getName())) {
                    isAdmin = true;
                    break;
                }
            }

            if (!isAdmin) {
                // Non-admin users can only see their own messages
                if (info.getAuthUser() == null || !info.getAuthUser().equals(currentUser.getUsername())) {
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("{\"error\":\"Access denied\"}").build();
                }
            }

            // Load message content
            byte[] content = null;
            if (info.getRawFilename() != null) {
                try {
                    TrackerMessageStoreHandler storeHandler = new TrackerMessageStoreHandler(prefs);
                    content = storeHandler.readTrackerMessage(info.getRawFilename());
                } catch (Exception e) {
                    // Content not available
                }
            }

            TrackerMessageDetailsDTO dto = new TrackerMessageDetailsDTO();
            dto.setTrackerId(info.getTrackerId());
            dto.setTimestamp(info.getInitDate());
            dto.setRemoteAddr(info.getRemoteAddr());
            dto.setUserAgent(info.getUserAgent());
            dto.setContentType(info.getContentType());
            dto.setContentSize(info.getContentSize());
            dto.setAuthStatus(info.getAuthStatusText());
            dto.setAuthUser(info.getAuthUser());
            dto.setRawFilename(info.getRawFilename());
            dto.setRequestHeaders(info.getRequestHeaders());
            dto.setPayloadCount(info.getPayloadCount());
            dto.setPayloadFormat(info.getPayloadFormat());
            dto.setPayloadDocType(info.getPayloadDocType());
            dto.setPayloadDetails(info.getPayloadDetails());

            if (content != null) {
                dto.setContentPreview(new String(content, 0, Math.min(content.length, 1000), "UTF-8"));
            }

            return Response.ok(dto).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{trackerId}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadMessageContent(
            @Context SecurityContext securityContext,
            @PathParam("trackerId") String trackerId) {
        try {
            // Get username from security context
            String username = securityContext.getUserPrincipal().getName();

            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
            TrackerMessageAccessDB dao = new TrackerMessageAccessDB(dbDriverManager, prefs);
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            // Get current user
            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            TrackerMessageInfo info = dao.getTrackerMessage(trackerId);
            if (info == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Message not found\"}").build();
            }

            // Check permissions
            boolean isAdmin = false;
            List<Role> userRoles = userMgmt.getUserRoles(currentUser.getId());
            for (Role role : userRoles) {
                if ("ADMIN".equals(role.getName())) {
                    isAdmin = true;
                    break;
                }
            }

            boolean authRequired = "true".equals(prefs.get(PreferencesAS2.TRACKER_AUTH_REQUIRED));
            if (authRequired && !isAdmin) {
                if (info.getAuthUser() == null || !info.getAuthUser().equals(currentUser.getUsername())) {
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("{\"error\":\"Access denied\"}").build();
                }
            }

            // Load message content
            if (info.getRawFilename() == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Message content not available\"}").build();
            }

            TrackerMessageStoreHandler storeHandler = new TrackerMessageStoreHandler(prefs);
            byte[] content = storeHandler.readTrackerMessage(info.getRawFilename());

            if (content == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Message content not found\"}").build();
            }

            // Generate filename
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = sdf.format(info.getInitDate());
            String filename = "tracker_" + trackerId.substring(0, Math.min(8, trackerId.length())) +
                    "_" + timestamp + ".txt";

            return Response.ok(content)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{trackerId}/download-payloads")
    @Produces("application/zip")
    public Response downloadPayloads(
            @Context SecurityContext securityContext,
            @PathParam("trackerId") String trackerId) {
        try {
            // Get username from security context
            String username = securityContext.getUserPrincipal().getName();

            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
            TrackerMessageAccessDB dao = new TrackerMessageAccessDB(dbDriverManager, prefs);
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            // Get current user
            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            TrackerMessageInfo info = dao.getTrackerMessage(trackerId);
            if (info == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Message not found\"}").build();
            }

            // Check permissions
            boolean isAdmin = false;
            List<Role> userRoles = userMgmt.getUserRoles(currentUser.getId());
            for (Role role : userRoles) {
                if ("ADMIN".equals(role.getName())) {
                    isAdmin = true;
                    break;
                }
            }

            boolean authRequired = "true".equals(prefs.get(PreferencesAS2.TRACKER_AUTH_REQUIRED));
            if (authRequired && !isAdmin) {
                if (info.getAuthUser() == null || !info.getAuthUser().equals(currentUser.getUsername())) {
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("{\"error\":\"Access denied\"}").build();
                }
            }

            // Check if payloads exist
            if (info.getPayloadCount() == 0) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"No payloads available\"}").build();
            }

            // Calculate payload directory path
            if (info.getRawFilename() == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Payload directory not found\"}").build();
            }

            java.nio.file.Path rawPath = Paths.get(info.getRawFilename());
            String dateFolder = rawPath.getParent().getFileName().toString();
            String payloadDirName = "payloads_" + trackerId;

            String msgDir = prefs.get(PreferencesAS2.DIR_MSG);
            java.nio.file.Path payloadDir = Paths.get(msgDir, "tracker", dateFolder, payloadDirName);

            if (!Files.exists(payloadDir)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Payload directory not found: " + payloadDir + "\"}").build();
            }

            // Create ZIP in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                try (Stream<java.nio.file.Path> paths = Files.walk(payloadDir)) {
                    paths.filter(Files::isRegularFile).forEach(file -> {
                        try {
                            String zipEntryName = payloadDir.relativize(file).toString();
                            ZipEntry zipEntry = new ZipEntry(zipEntryName);
                            zos.putNextEntry(zipEntry);
                            Files.copy(file, zos);
                            zos.closeEntry();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }

            byte[] zipData = baos.toByteArray();

            // Generate filename
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = sdf.format(info.getInitDate());
            String filename = "payloads_" + trackerId.substring(0, Math.min(8, trackerId.length())) +
                    "_" + timestamp + ".zip";

            return Response.ok(zipData)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{trackerId}/download-bruno")
    @Produces("application/zip")
    public Response downloadBrunoYaml(
            @Context SecurityContext securityContext,
            @PathParam("trackerId") String trackerId) {
        try {
            // Get username from security context
            String username = securityContext.getUserPrincipal().getName();

            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
            TrackerMessageAccessDB dao = new TrackerMessageAccessDB(dbDriverManager, prefs);
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            // Get current user
            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            TrackerMessageInfo info = dao.getTrackerMessage(trackerId);
            if (info == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Message not found\"}").build();
            }

            // Check permissions
            boolean isAdmin = false;
            List<Role> userRoles = userMgmt.getUserRoles(currentUser.getId());
            for (Role role : userRoles) {
                if ("ADMIN".equals(role.getName())) {
                    isAdmin = true;
                    break;
                }
            }

            boolean authRequired = "true".equals(prefs.get(PreferencesAS2.TRACKER_AUTH_REQUIRED));
            if (authRequired && !isAdmin) {
                if (info.getAuthUser() == null || !info.getAuthUser().equals(currentUser.getUsername())) {
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("{\"error\":\"Access denied\"}").build();
                }
            }

            // Load message content
            byte[] content = null;
            if (info.getRawFilename() != null) {
                TrackerMessageStoreHandler storeHandler = new TrackerMessageStoreHandler(prefs);
                content = storeHandler.readTrackerMessage(info.getRawFilename());
            }

            // Create opencollection.yml content
            StringBuilder openCollectionYaml = new StringBuilder();
            openCollectionYaml.append("opencollection: 1.0.0\n\n");
            openCollectionYaml.append("info:\n");

            // Build collection name from host and context: "localhost-8080-as2-tracker"
            String collectionName = "localhost-8080-as2-tracker";
            if (info.getAuthUser() != null && !info.getAuthUser().isEmpty()) {
                collectionName += "-" + info.getAuthUser();
            }
            openCollectionYaml.append("  name: ").append(collectionName).append("\n");
            openCollectionYaml.append("bundled: false\n");
            openCollectionYaml.append("extensions:\n");
            openCollectionYaml.append("  bruno:\n");
            openCollectionYaml.append("    ignore:\n");
            openCollectionYaml.append("      - node_modules\n");
            openCollectionYaml.append("      - .git\n");

            // Create request.yml content (the actual request)
            StringBuilder requestYaml = new StringBuilder();
            requestYaml.append("info:\n");
            requestYaml.append("  name: Recreate Tracker Message\n");
            requestYaml.append("  type: http\n");
            requestYaml.append("  seq: 1\n\n");

            requestYaml.append("http:\n");
            requestYaml.append("  method: POST\n");

            // Build URL - if auth_user exists, include it in path
            String trackerUrl = "http://localhost:8080/as2/tracker";
            if (info.getAuthUser() != null && !info.getAuthUser().isEmpty()) {
                trackerUrl += "/" + info.getAuthUser();
            }
            requestYaml.append("  url: ").append(trackerUrl).append("\n");

            // Add headers
            if (info.getRequestHeaders() != null && !info.getRequestHeaders().isEmpty()) {
                requestYaml.append("  headers:\n");
                try {
                    // Try to parse as JSON
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    java.util.Map<String, Object> headers = mapper.readValue(info.getRequestHeaders(),
                        new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});

                    for (java.util.Map.Entry<String, Object> entry : headers.entrySet()) {
                        // Skip certain headers that Bruno should regenerate
                        String headerName = entry.getKey();
                        if (headerName.equalsIgnoreCase("Host") ||
                            headerName.equalsIgnoreCase("Content-Length") ||
                            headerName.equalsIgnoreCase("Authorization") ||
                            headerName.toLowerCase().startsWith("request-")) {
                            continue;
                        }
                        requestYaml.append("    ").append(headerName).append(": ").append(entry.getValue()).append("\n");
                    }
                } catch (Exception e) {
                    // Fallback: add as comment if parsing fails
                    requestYaml.append("    # Headers could not be parsed\n");
                }
            }

            // Add body
            requestYaml.append("  body:\n");

            // Determine body type from content-type
            String bodyType = "text";
            if (info.getContentType() != null) {
                String contentType = info.getContentType().toLowerCase();
                if (contentType.contains("xml")) {
                    bodyType = "xml";
                } else if (contentType.contains("json")) {
                    bodyType = "json";
                } else if (contentType.contains("text")) {
                    bodyType = "text";
                }
            }

            requestYaml.append("    type: ").append(bodyType).append("\n");
            requestYaml.append("    data: |-\n");
            if (content != null) {
                String contentStr = new String(content, "UTF-8");
                // Indent each line for YAML
                String[] lines = contentStr.split("\n");
                for (String line : lines) {
                    requestYaml.append("      ").append(line).append("\n");
                }
            } else {
                requestYaml.append("      # Content not available\n");
            }

            // Add auth section if auth_user exists
            if (info.getAuthUser() != null && !info.getAuthUser().isEmpty()) {
                requestYaml.append("  auth:\n");
                requestYaml.append("    type: basic\n");
                requestYaml.append("    username: ").append(info.getAuthUser()).append("\n");
                requestYaml.append("    password: # Enter password here\n");
            }

            // Add settings
            requestYaml.append("\nsettings:\n");
            requestYaml.append("  encodeUrl: true\n");
            requestYaml.append("  timeout: 0\n");
            requestYaml.append("  followRedirects: true\n");
            requestYaml.append("  maxRedirects: 5\n");

            // Create ZIP file with both YAML files
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos);

            // Add opencollection.yml
            java.util.zip.ZipEntry openCollectionEntry = new java.util.zip.ZipEntry("opencollection.yml");
            zos.putNextEntry(openCollectionEntry);
            zos.write(openCollectionYaml.toString().getBytes("UTF-8"));
            zos.closeEntry();

            // Add request.yml
            java.util.zip.ZipEntry requestEntry = new java.util.zip.ZipEntry("Recreate Tracker Message.yml");
            zos.putNextEntry(requestEntry);
            zos.write(requestYaml.toString().getBytes("UTF-8"));
            zos.closeEntry();

            zos.close();
            byte[] zipData = baos.toByteArray();

            // Generate filename with current time in user's timezone
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String timestamp = sdf.format(new Date());
            String filename = "bruno-collect-" + timestamp + ".zip";

            return Response.ok(zipData)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    public static class TrackerMessageDTO {
        private String trackerId;
        private Date timestamp;
        private String remoteAddr;
        private String userAgent;
        private String contentType;
        private int contentSize;
        private String authStatus;
        private String authUser;
        private int payloadCount;
        private String payloadFormat;
        private String payloadDocType;
        private String payloadDetails;

        // Getters and setters
        public String getTrackerId() { return trackerId; }
        public void setTrackerId(String trackerId) { this.trackerId = trackerId; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
        public String getRemoteAddr() { return remoteAddr; }
        public void setRemoteAddr(String remoteAddr) { this.remoteAddr = remoteAddr; }
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public int getContentSize() { return contentSize; }
        public void setContentSize(int contentSize) { this.contentSize = contentSize; }
        public String getAuthStatus() { return authStatus; }
        public void setAuthStatus(String authStatus) { this.authStatus = authStatus; }
        public String getAuthUser() { return authUser; }
        public void setAuthUser(String authUser) { this.authUser = authUser; }
        public int getPayloadCount() { return payloadCount; }
        public void setPayloadCount(int payloadCount) { this.payloadCount = payloadCount; }
        public String getPayloadFormat() { return payloadFormat; }
        public void setPayloadFormat(String payloadFormat) { this.payloadFormat = payloadFormat; }
        public String getPayloadDocType() { return payloadDocType; }
        public void setPayloadDocType(String payloadDocType) { this.payloadDocType = payloadDocType; }
        public String getPayloadDetails() { return payloadDetails; }
        public void setPayloadDetails(String payloadDetails) { this.payloadDetails = payloadDetails; }
    }

    public static class TrackerMessageDetailsDTO extends TrackerMessageDTO {
        private String rawFilename;
        private String requestHeaders;
        private String contentPreview;

        public String getRawFilename() { return rawFilename; }
        public void setRawFilename(String rawFilename) { this.rawFilename = rawFilename; }
        public String getRequestHeaders() { return requestHeaders; }
        public void setRequestHeaders(String requestHeaders) { this.requestHeaders = requestHeaders; }
        public String getContentPreview() { return contentPreview; }
        public void setContentPreview(String contentPreview) { this.contentPreview = contentPreview; }
    }
}
