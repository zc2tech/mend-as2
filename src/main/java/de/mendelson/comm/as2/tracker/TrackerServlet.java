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
import de.mendelson.comm.as2.tracker.auth.UserTrackerAuthCredential;
import de.mendelson.comm.as2.tracker.auth.UserTrackerAuthDB;
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
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
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

        // Get client IP address for whitelist check (declared here to avoid duplicate later)
        String remoteAddr = request.getRemoteAddr();

        // IP Whitelist Check for Tracker endpoint
        try {
            PreferencesAS2 prefs = new PreferencesAS2(processing.getDBDriverManager());

            if ("true".equals(prefs.get(PreferencesAS2.IP_WHITELIST_ENABLED_TRACKER))) {

                de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistService whitelistService =
                    de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistService.getInstance(
                        processing.getDBDriverManager());

                if (!whitelistService.isAllowedForTracker(remoteAddr)) {
                    // Log blocked attempt
                    whitelistService.logBlockedAttempt(
                        remoteAddr,
                        "TRACKER",
                        null,
                        null,
                        request.getHeader("User-Agent"),
                        request.getRequestURI()
                    );

                    // Return 403 Forbidden
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Access denied: IP address not whitelisted for Tracker endpoint");
                    return;
                }
            }
        } catch (Exception e) {
            // Log error but don't block if whitelist check fails
            LOGGER.warning("IP whitelist check failed for Tracker endpoint: " + e.getMessage());
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

        // 3. Get hostname from Host header
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
        String authenticatedUser = null;
        boolean authRequired = false;

        if (pathUsername != null && !pathUsername.isEmpty()) {
            // ============================================================
            // USER-SPECIFIC URL: /as2/tracker/<username>
            // Use user's personal authentication configuration
            // ============================================================

            try {
                // Get user ID from username
                UserManagementAccessDB userMgmt = new UserManagementAccessDB(
                        processing.getDBDriverManager(), null);
                WebUIUser targetUser = userMgmt.getUserByUsername(pathUsername);

                if (targetUser == null) {
                    LOGGER.warning("User not found for tracker URL: " + pathUsername);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }

                int userId = targetUser.getId();

                // Load user's tracker auth configuration
                Connection configConnection = processing.getDBDriverManager()
                        .getConnectionWithoutErrorHandling(de.mendelson.util.database.IDBDriverManager.DB_CONFIG);
                UserTrackerAuthDB authDB = new UserTrackerAuthDB();

                boolean[] toggles = authDB.loadMasterToggles(userId, configConnection);
                List<UserTrackerAuthCredential> credentials = authDB.loadCredentials(userId, configConnection);
                configConnection.close();

                boolean basicAuthEnabled = toggles[0];
                boolean certAuthEnabled = toggles[1];

            // Check if authentication is required
            if (!basicAuthEnabled && !certAuthEnabled) {
                // No authentication required - allow all
                authRequired = false;
                LOGGER.info("Tracker request for user " + pathUsername + " - no auth required, ip=" + remoteAddr);
            } else {
                // Authentication is required
                authRequired = true;

                // Count enabled credentials by type
                int enabledBasicCount = 0;
                int enabledCertCount = 0;

                if (basicAuthEnabled) {
                    for (UserTrackerAuthCredential cred : credentials) {
                        if (cred.isEnabled() && cred.getAuthType() == UserTrackerAuthCredential.AUTH_TYPE_BASIC) {
                            enabledBasicCount++;
                        }
                    }
                }

                if (certAuthEnabled) {
                    for (UserTrackerAuthCredential cred : credentials) {
                        if (cred.isEnabled() && cred.getAuthType() == UserTrackerAuthCredential.AUTH_TYPE_CERTIFICATE) {
                            enabledCertCount++;
                        }
                    }
                }

                // Validate authentication
                boolean basicAuthPassed = false;
                boolean certAuthPassed = false;

                // Try Basic Auth (only if basic auth is enabled AND has credentials configured)
                if (basicAuthEnabled && enabledBasicCount > 0) {
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Basic ")) {
                        basicAuthPassed = validateBasicAuth(authHeader, credentials);
                        if (basicAuthPassed) {
                            authenticatedUser = extractUsernameFromBasicAuth(authHeader);
                        }
                    }
                }

                // Try Certificate Auth (only if cert auth is enabled AND has credentials configured)
                if (certAuthEnabled && enabledCertCount > 0 && !basicAuthPassed) {
                    X509Certificate[] certs = (X509Certificate[]) request.getAttribute(
                            "jakarta.servlet.request.X509Certificate");
                    if (certs != null && certs.length > 0) {
                        certAuthPassed = validateCertAuth(certs[0], credentials);
                        if (certAuthPassed) {
                            authenticatedUser = "cert:" + pathUsername;
                        }
                    }
                }

                // Check if authentication passed
                if (!basicAuthPassed && !certAuthPassed) {
                    // Authentication failed - neither method passed
                    // Log specific reason for failure
                    if (basicAuthEnabled && enabledBasicCount == 0 && certAuthEnabled && enabledCertCount == 0) {
                        LOGGER.warning("Tracker auth failed for user " + pathUsername +
                                " - both auth types enabled but no credentials configured, ip=" + remoteAddr);
                    } else if (basicAuthEnabled && enabledBasicCount == 0) {
                        LOGGER.warning("Tracker auth failed for user " + pathUsername +
                                " - basic auth enabled but no credentials configured, ip=" + remoteAddr);
                    } else if (certAuthEnabled && enabledCertCount == 0) {
                        LOGGER.warning("Tracker auth failed for user " + pathUsername +
                                " - cert auth enabled but no credentials configured, ip=" + remoteAddr);
                    } else {
                        LOGGER.warning("Tracker auth failed for user " + pathUsername +
                                " - credentials provided but validation failed, ip=" + remoteAddr);
                    }

                    dao.recordAuthFailure(remoteAddr, userAgent, pathUsername);

                    // Check rate limiting
                    if (TrackerRateLimiter.checkAndBlock(remoteAddr, dao, prefs)) {
                        int recentFailures = dao.countRecentFailures(remoteAddr,
                                Integer.parseInt(prefs.get(PreferencesAS2.TRACKER_RATE_LIMIT_WINDOW_HOURS)));

                        sendAttackNotification(remoteAddr, recentFailures,
                                "Rate limit threshold exceeded", processing);

                        LOGGER.warning("IP blocked due to rate limit: " + remoteAddr +
                                " (" + recentFailures + " failures)");
                    }

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "Basic realm=\"Tracker\"");
                    return;
                }

                LOGGER.info("Tracker request authenticated for user " + pathUsername +
                        ", method=" + (basicAuthPassed ? "basic" : "cert") +
                        ", ip=" + remoteAddr);
            }

            } catch (Exception e) {
                LOGGER.severe("Failed to load user tracker auth config: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to load authentication configuration");
                return;
            }

        } else {
            // ============================================================
            // SYSTEM-WIDE URL: /as2/tracker
            // REJECTED - Must use user-specific URLs: /as2/tracker/<username>
            // ============================================================
            LOGGER.warning("Rejected tracker request to deprecated system-wide URL, ip=" + remoteAddr);
            response.sendError(HttpServletResponse.SC_GONE,
                    "System-wide tracker URL is deprecated. Use /as2/tracker/<username> instead.");
            return;
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
        info.setAuthUser(pathUsername);  // Store username from URL path, not from auth credentials
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

    /**
     * Validate Basic Authentication against user credentials.
     *
     * @param authHeader Authorization header value (e.g., "Basic base64...")
     * @param credentials List of user tracker credentials
     * @return true if authentication passed
     */
    private boolean validateBasicAuth(String authHeader, List<UserTrackerAuthCredential> credentials) {
        try {
            // Parse "Basic <base64>" header
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String decodedCredentials = new String(Base64.getDecoder().decode(base64Credentials), "UTF-8");

            // Split on first colon only
            int separatorIndex = decodedCredentials.indexOf(':');
            if (separatorIndex == -1) {
                return false;
            }

            String username = decodedCredentials.substring(0, separatorIndex);
            String password = decodedCredentials.substring(separatorIndex + 1);

            // Check against ALL enabled basic credentials (OR logic)
            for (UserTrackerAuthCredential credential : credentials) {
                if (credential.isEnabled()
                        && credential.getAuthType() == UserTrackerAuthCredential.AUTH_TYPE_BASIC
                        && username.equals(credential.getUsername())
                        && password.equals(credential.getPassword())) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            LOGGER.warning("Error validating basic auth: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate Certificate Authentication against user credentials.
     *
     * @param cert Client certificate
     * @param credentials List of user tracker credentials
     * @return true if authentication passed
     */
    private boolean validateCertAuth(X509Certificate cert, List<UserTrackerAuthCredential> credentials) {
        try {
            // Calculate SHA-1 fingerprint
            String certFingerprint = calculateFingerprint(cert);

            // Check against ALL enabled cert credentials (OR logic)
            for (UserTrackerAuthCredential credential : credentials) {
                if (credential.isEnabled()
                        && credential.getAuthType() == UserTrackerAuthCredential.AUTH_TYPE_CERTIFICATE
                        && certFingerprint.equalsIgnoreCase(credential.getCertFingerprint())) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            LOGGER.warning("Error validating cert auth: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calculate SHA-1 fingerprint of certificate. Returns format: "AB:CD:EF:..."
     *
     * @param cert X509 certificate
     * @return SHA-1 fingerprint with colons
     */
    private String calculateFingerprint(X509Certificate cert) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();

        // Convert to hex with colons
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            String hex = Integer.toHexString(0xFF & digest[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex.toUpperCase());
            if (i < digest.length - 1) {
                hexString.append(':');
            }
        }

        return hexString.toString();
    }

    /**
     * Extract username from Basic Auth header for logging.
     *
     * @param authHeader Authorization header
     * @return Username or null
     */
    private String extractUsernameFromBasicAuth(String authHeader) {
        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String decodedCredentials = new String(Base64.getDecoder().decode(base64Credentials), "UTF-8");

            int separatorIndex = decodedCredentials.indexOf(':');
            if (separatorIndex != -1) {
                return decodedCredentials.substring(0, separatorIndex);
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
