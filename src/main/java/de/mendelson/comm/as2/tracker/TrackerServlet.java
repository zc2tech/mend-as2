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

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.systemevents.notification.NotificationAccessDBImplAS2;
import de.mendelson.util.systemevents.notification.NotificationData;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Servlet to receive tracker messages via HTTP POST
 *
 * @author Julian Xu
 */
public class TrackerServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger("de.mendelson.as2.server");
    private static final int BUFFER_SIZE = 8192;

    public TrackerServlet() {
    }

    /**
     * GET request returns simple HTML page explaining endpoint usage
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        out.println("<HTML>");
        out.println("    <HEAD>");
        out.println("        <META NAME=\"description\" CONTENT=\"mendelson-e-commerce GmbH: Your EAI partner\">");
        out.println("        <META NAME=\"copyright\" CONTENT=\"mendelson-e-commerce GmbH\">");
        out.println("        <META NAME=\"robots\" CONTENT=\"NOINDEX,NOFOLLOW,NOARCHIVE,NOSNIPPET\">");
        out.println("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("        <title>" + AS2ServerVersion.getProductName() + " - Tracker Endpoint</title>");
        out.println("    </HEAD>");
        out.println("    <BODY>");
        out.println("<H2>" + AS2ServerVersion.getProductName() + " - Tracker Endpoint</H2>");
        out.println("<BR>You have performed an HTTP GET on this URL. <BR>");
        out.println("To submit a tracker message, you must POST the message to this URL <BR>");
        out.println("    </BODY>");
        out.println("</HTML>");
    }

    /**
     * POST request handles tracker message reception
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get server processing instance
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Server processing not available");
            return;
        }

        PreferencesAS2 prefs;
        try {
            prefs = new PreferencesAS2(processing.getDBDriverManager());
        } catch (Exception e) {
            LOGGER.warning("Failed to load preferences: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Server configuration error");
            return;
        }

        // 1. Check if tracker is enabled
        if (!"true".equals(prefs.get(PreferencesAS2.TRACKER_ENABLED))) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Tracker endpoint is disabled");
            return;
        }

        // 2. Extract username from path if present: /as2/tracker/{username}
        String pathInfo = request.getPathInfo();
        String pathUsername = null;
        if (pathInfo != null && pathInfo.length() > 1) {
            // Remove leading slash and get username
            pathUsername = pathInfo.substring(1);
            if (pathUsername.contains("/")) {
                // Invalid path format
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid tracker path format");
                return;
            }
        }

        // 3. Get client IP address and hostname
        String remoteAddr = request.getRemoteAddr();
        String hostHeader = request.getHeader("Host");
        // Use Host header if available, otherwise fall back to IP
        String endpoint = (hostHeader != null && !hostHeader.isEmpty()) ? hostHeader : remoteAddr;
        String userAgent = request.getHeader("User-Agent");

        // 4. Check rate limiting
        TrackerMessageAccessDB dao = new TrackerMessageAccessDB(processing.getDBDriverManager());

        if (TrackerRateLimiter.isBlocked(remoteAddr)) {
            long remainingSeconds = TrackerRateLimiter.getBlockRemainingSeconds(remoteAddr);
            LOGGER.warning("Blocked request from " + remoteAddr +
                    " - " + remainingSeconds + "s remaining");
            response.sendError(429,
                    "Too many failed attempts. Access temporarily blocked for " +
                            remainingSeconds + " seconds.");
            return;
        }

        // 5. Authentication handling
        boolean authRequired = "true".equals(prefs.get(PreferencesAS2.TRACKER_AUTH_REQUIRED));
        String authenticatedUser = null;

        // If path contains username, authentication is REQUIRED
        if (pathUsername != null) {
            authRequired = true;
        }

        if (authRequired) {
            authenticatedUser = authenticateRequest(request, processing);
            if (authenticatedUser == null) {
                // Record auth failure
                dao.recordAuthFailure(remoteAddr, userAgent, extractAttemptedUsername(request));

                // Check if should block after this failure
                if (TrackerRateLimiter.checkAndBlock(remoteAddr, dao, prefs)) {
                    int recentFailures = dao.countRecentFailures(remoteAddr,
                            Integer.parseInt(prefs.get(PreferencesAS2.TRACKER_RATE_LIMIT_WINDOW_HOURS)));

                    // Send attack notification
                    sendAttackNotification(remoteAddr, recentFailures,
                            "Rate limit threshold exceeded", processing);

                    LOGGER.warning("IP blocked due to rate limit: " + remoteAddr +
                            " (" + recentFailures + " failures)");
                }

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "Basic realm=\"Tracker\"");
                return;
            }

            // If path contains username, validate it matches authenticated user
            if (pathUsername != null && !pathUsername.equals(authenticatedUser)) {
                LOGGER.warning("Username mismatch: path=" + pathUsername +
                        ", authenticated=" + authenticatedUser + ", ip=" + remoteAddr);
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Username in path does not match authenticated user");
                return;
            }

            LOGGER.info("Tracker request authenticated: user=" + authenticatedUser +
                    ", ip=" + remoteAddr +
                    (pathUsername != null ? ", path_user=" + pathUsername : ""));
        }

        // 6. Validate message size
        long maxSizeMB = Long.parseLong(prefs.get(PreferencesAS2.TRACKER_MAX_SIZE_MB));
        long maxSizeBytes = maxSizeMB * 1024 * 1024;

        byte[] data;
        try {
            data = readWithSizeLimit(request.getInputStream(), maxSizeBytes);
        } catch (IOException e) {
            LOGGER.warning("Failed to read uploaded data: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to read uploaded data");
            return;
        } catch (FileTooLargeException e) {
            LOGGER.warning("File too large from " + remoteAddr + ": " + e.getMessage());
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                    "Message exceeds maximum size: " + maxSizeMB + "MB");
            return;
        }

        // 6. Generate IDs
        String messageId = UUID.randomUUID().toString();
        String trackerId = UUID.randomUUID().toString();

        // 7. Store to filesystem
        TrackerMessageStoreHandler storeHandler = new TrackerMessageStoreHandler(prefs);
        String rawFilename;
        try {
            rawFilename = storeHandler.storeTrackerMessage(data, trackerId);
        } catch (IOException e) {
            LOGGER.severe("Failed to store tracker message to filesystem: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to store message");
            return;
        }

        // 8. Parse MIME and extract payloads
        int payloadCount = 0;
        PayloadAnalyzer.PayloadAnalysis firstPayloadAnalysis = null;
        try {
            TrackerMimeParser mimeParser = new TrackerMimeParser(prefs);
            payloadCount = mimeParser.parseAndExtractPayloads(
                    data, request.getContentType(), trackerId, rawFilename);
            firstPayloadAnalysis = mimeParser.getFirstPayloadAnalysis();
        } catch (Exception e) {
            LOGGER.warning("Failed to parse MIME payloads for tracker " + trackerId + ": " + e.getMessage());
            // Continue - this is not a critical failure
        }

        // 9. Store to database
        TrackerMessageInfo info = new TrackerMessageInfo();
        info.setMessageId(messageId);
        info.setTrackerId(trackerId);
        info.setRemoteAddr(endpoint);  // Store hostname from Host header instead of IP
        info.setUserAgent(userAgent);
        info.setContentType(request.getContentType());
        info.setContentSize(data.length);
        info.setInitDate(new Date());
        info.setAuthStatus(authRequired ? TrackerMessageInfo.AUTH_STATUS_SUCCESS
                : TrackerMessageInfo.AUTH_STATUS_NONE);
        info.setAuthUser(authenticatedUser);
        info.setRawFilename(rawFilename);
        info.setRequestHeaders(serializeHeaders(request));
        info.setPayloadCount(payloadCount);

        // Store payload analysis if available
        if (firstPayloadAnalysis != null) {
            info.setPayloadFormat(firstPayloadAnalysis.getFormat());
            info.setPayloadDocType(firstPayloadAnalysis.getDocumentType());
            info.setPayloadDetails(firstPayloadAnalysis.getDetails());
        }

        try {
            dao.insertTrackerMessage(info);
        } catch (Exception e) {
            LOGGER.severe("Failed to store tracker message to database: " + e.getMessage());
            // Try to clean up filesystem
            try {
                storeHandler.getFullPath(rawFilename).toFile().delete();
            } catch (Exception cleanupEx) {
                LOGGER.warning("Failed to cleanup file after database error: " +
                        cleanupEx.getMessage());
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to store message metadata");
            return;
        }

        // 9. Return success (plain text response)
        LOGGER.info("Tracker message received: trackerId=" + trackerId +
                ", size=" + data.length + ", ip=" + remoteAddr +
                (authenticatedUser != null ? ", user=" + authenticatedUser : "") +
                ", payloads=" + payloadCount);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("Tracker ID: " + trackerId);
        out.println("Size: " + data.length + " bytes");
        out.println("Timestamp: " + new Date());
        if (payloadCount > 0) {
            out.println("Payloads extracted: " + payloadCount);
        }
    }

    /**
     * Authenticate request using Basic Authentication
     *
     * @param request HTTP request
     * @param processing Server processing instance
     * @return Authenticated username or null if authentication failed
     */
    private String authenticateRequest(HttpServletRequest request, AS2ServerProcessing processing) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), "UTF-8");

            int separatorIndex = credentials.indexOf(':');
            if (separatorIndex == -1) {
                return null;
            }

            String username = credentials.substring(0, separatorIndex);
            String password = credentials.substring(separatorIndex + 1);

            // Validate credentials
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(
                    processing.getDBDriverManager(), null);
            WebUIUser user = userMgmt.getUserByUsername(username);

            if (user == null || !user.isEnabled()) {
                return null;
            }

            // Verify password using PBKDF2
            boolean passwordValid = de.mendelson.util.security.PBKDF2.validatePassword(
                    password,
                    user.getPasswordHash()
            );

            if (passwordValid) {
                return username;
            }

            return null;

        } catch (Exception e) {
            LOGGER.warning("Authentication error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract attempted username from Basic Auth header for logging
     */
    private String extractAttemptedUsername(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), "UTF-8");

            int separatorIndex = credentials.indexOf(':');
            if (separatorIndex == -1) {
                return null;
            }

            return credentials.substring(0, separatorIndex);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Read input stream with size limit
     *
     * @param stream Input stream
     * @param maxSize Maximum size in bytes
     * @return Read data
     * @throws IOException If read fails
     * @throws FileTooLargeException If size exceeds limit
     */
    private byte[] readWithSizeLimit(InputStream stream, long maxSize)
            throws IOException, FileTooLargeException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[BUFFER_SIZE];
        int bytesRead;
        long totalRead = 0;

        while ((bytesRead = stream.read(chunk)) != -1) {
            totalRead += bytesRead;
            if (totalRead > maxSize) {
                throw new FileTooLargeException("File size exceeds limit: " + maxSize + " bytes");
            }
            buffer.write(chunk, 0, bytesRead);
        }

        return buffer.toByteArray();
    }

    /**
     * Serialize HTTP headers to JSON string
     */
    private String serializeHeaders(HttpServletRequest request) {
        StringBuilder json = new StringBuilder("{");
        Enumeration<String> headerNames = request.getHeaderNames();
        boolean first = true;

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(escapeJson(headerName)).append("\":\"")
                    .append(escapeJson(headerValue)).append("\"");
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Escape string for JSON
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Send attack notification email to admin
     */
    private void sendAttackNotification(String remoteAddr, int failedAttempts,
                                        String reason, AS2ServerProcessing processing) {
        try {
            NotificationAccessDBImplAS2 notificationAccessDB =
                    new NotificationAccessDBImplAS2(processing.getDBDriverManager());
            NotificationData notificationData = notificationAccessDB.getNotificationData();

            // Check if notification is configured (has email address)
            if (notificationData != null
                    && notificationData.getNotificationMail() != null
                    && !notificationData.getNotificationMail().trim().isEmpty()) {
                TrackerNotificationMailer.sendAttackNotification(
                        remoteAddr, failedAttempts, reason, notificationData);
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to send attack notification: " + e.getMessage());
        }
    }

    /**
     * Custom exception for file size limit
     */
    private static class FileTooLargeException extends Exception {
        public FileTooLargeException(String message) {
            super(message);
        }
    }
}
