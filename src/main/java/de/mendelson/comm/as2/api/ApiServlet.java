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

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.api.auth.UserApiAuthCredential;
import de.mendelson.comm.as2.api.auth.UserApiAuthDB;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.comm.as2.tracker.TrackerRateLimiter;
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
 * Servlet to receive API requests via HTTP GET/POST/PUT/DELETE
 * URL pattern: /as2/api/{username}/*
 *
 * @author Julian Xu
 */
public class ApiServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger("de.mendelson.as2.server");
    private static final int BUFFER_SIZE = 8192;

    public ApiServlet() {
    }

    /**
     * GET request returns simple HTML page explaining endpoint usage or processes API request
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response, "GET");
    }

    /**
     * POST request handles API requests
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response, "POST");
    }

    /**
     * PUT request handles API requests
     */
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response, "PUT");
    }

    /**
     * DELETE request handles API requests
     */
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response, "DELETE");
    }

    /**
     * Common request handler for all HTTP methods
     */
    private void handleRequest(HttpServletRequest request, HttpServletResponse response, String httpMethod)
            throws ServletException, IOException {

        // Get server processing instance
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Server processing not available");
            return;
        }

        // Get client IP address
        String remoteAddr = request.getRemoteAddr();

        // IP Whitelist Check for API endpoint
        try {
            PreferencesAS2 prefs = new PreferencesAS2(processing.getDBDriverManager());

            if ("true".equals(prefs.get(PreferencesAS2.IP_WHITELIST_ENABLED_API))) {
                de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistService whitelistService =
                    de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistService.getInstance(
                        processing.getDBDriverManager());

                if (!whitelistService.isAllowedForApi(remoteAddr)) {
                    whitelistService.logBlockedAttempt(
                        remoteAddr,
                        "API",
                        null,
                        null,
                        request.getHeader("User-Agent"),
                        request.getRequestURI()
                    );

                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Access denied: IP address not whitelisted for API endpoint");
                    return;
                }
            }
        } catch (Exception e) {
            LOGGER.warning("IP whitelist check failed for API endpoint: " + e.getMessage());
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

        // 1. Check if API is enabled
        if (!"true".equals(prefs.get(PreferencesAS2.API_ENABLED))) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "API endpoint is disabled");
            return;
        }

        // 2. Extract username from path: /as2/api/{username}/*
        String pathInfo = request.getPathInfo();
        String pathUsername = null;
        String customPath = "/";

        if (pathInfo != null && pathInfo.length() > 1) {
            // Remove leading slash
            String[] pathParts = pathInfo.substring(1).split("/", 2);
            pathUsername = pathParts[0];
            if (pathParts.length > 1) {
                customPath = "/" + pathParts[1];
            }
        }

        // If no username provided, return info page
        if (pathUsername == null || pathUsername.isEmpty()) {
            returnInfoPage(response);
            return;
        }

        // 3. Get hostname from Host header
        String hostHeader = request.getHeader("Host");
        String endpoint = (hostHeader != null && !hostHeader.isEmpty()) ? hostHeader : remoteAddr;
        String userAgent = request.getHeader("User-Agent");

        // 4. Check rate limiting
        ApiRequestAccessDB dao = new ApiRequestAccessDB(processing.getDBDriverManager());

        if (TrackerRateLimiter.isBlocked(remoteAddr)) {
            long remainingSeconds = TrackerRateLimiter.getBlockRemainingSeconds(remoteAddr);
            LOGGER.warning("Blocked API request from " + remoteAddr +
                    " - " + remainingSeconds + "s remaining");
            response.sendError(429,
                    "Too many failed attempts. Access temporarily blocked for " +
                            remainingSeconds + " seconds.");
            return;
        }

        // 5. Get user from username
        WebUIUser targetUser;
        try {
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(
                    processing.getDBDriverManager(), null);
            targetUser = userMgmt.getUserByUsername(pathUsername);

            if (targetUser == null) {
                LOGGER.warning("User not found for API URL: " + pathUsername);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to load user: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load user configuration");
            return;
        }

        int userId = targetUser.getId();

        // 6. Authentication handling
        String authenticatedUser = null;
        boolean authRequired = false;

        try {
            // Load user's API auth configuration
            Connection configConnection = processing.getDBDriverManager()
                    .getConnectionWithoutErrorHandling(de.mendelson.util.database.IDBDriverManager.DB_CONFIG);
            UserApiAuthDB authDB = new UserApiAuthDB();

            boolean[] toggles = authDB.loadMasterToggles(userId, configConnection);
            List<UserApiAuthCredential> credentials = authDB.loadCredentials(userId, configConnection);
            configConnection.close();

            boolean basicAuthEnabled = toggles[0];
            boolean certAuthEnabled = toggles[1];

            // Check if authentication is required
            if (!basicAuthEnabled && !certAuthEnabled) {
                authRequired = false;
                LOGGER.info("API request for user " + pathUsername + " - no auth required, ip=" + remoteAddr);
            } else {
                authRequired = true;

                // Count enabled credentials by type
                int enabledBasicCount = 0;
                int enabledCertCount = 0;

                if (basicAuthEnabled) {
                    for (UserApiAuthCredential cred : credentials) {
                        if (cred.isEnabled() && cred.getAuthType() == UserApiAuthCredential.AUTH_TYPE_BASIC) {
                            enabledBasicCount++;
                        }
                    }
                }

                if (certAuthEnabled) {
                    for (UserApiAuthCredential cred : credentials) {
                        if (cred.isEnabled() && cred.getAuthType() == UserApiAuthCredential.AUTH_TYPE_CERTIFICATE) {
                            enabledCertCount++;
                        }
                    }
                }

                // Validate authentication
                boolean basicAuthPassed = false;
                boolean certAuthPassed = false;

                // Try Basic Auth
                if (basicAuthEnabled && enabledBasicCount > 0) {
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Basic ")) {
                        basicAuthPassed = validateBasicAuth(authHeader, credentials);
                        if (basicAuthPassed) {
                            authenticatedUser = extractUsernameFromBasicAuth(authHeader);
                        }
                    }
                }

                // Try Certificate Auth
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
                    // Authentication failed
                    LOGGER.warning("API auth failed for user " + pathUsername +
                            " - credentials validation failed, ip=" + remoteAddr);

                    dao.recordAuthFailure(remoteAddr, userAgent, pathUsername);

                    // Check rate limiting - count recent failures and block if threshold exceeded
                    int windowHours = Integer.parseInt(prefs.get(PreferencesAS2.API_RATE_LIMIT_WINDOW_HOURS));
                    int threshold = Integer.parseInt(prefs.get(PreferencesAS2.API_RATE_LIMIT_FAILURES));
                    int recentFailures = dao.countRecentFailures(remoteAddr, windowHours);

                    if (recentFailures >= threshold) {
                        // Block this IP
                        int blockMinutes = Integer.parseInt(prefs.get(PreferencesAS2.API_RATE_LIMIT_BLOCK_MINUTES));
                        TrackerRateLimiter.blockIP(remoteAddr, blockMinutes);

                        sendAttackNotification(remoteAddr, recentFailures,
                                "Rate limit threshold exceeded", processing);

                        LOGGER.warning("IP blocked due to rate limit: " + remoteAddr +
                                " (" + recentFailures + " failures)");
                    }

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "Basic realm=\"API\"");
                    return;
                }

                LOGGER.info("API request authenticated for user " + pathUsername +
                        ", method=" + (basicAuthPassed ? "basic" : "cert") +
                        ", ip=" + remoteAddr);
            }

        } catch (Exception e) {
            LOGGER.severe("Failed to load user API auth config: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load authentication configuration");
            return;
        }

        // 7. Validate request size
        long maxSizeMB = Long.parseLong(prefs.get(PreferencesAS2.API_MAX_SIZE_MB));
        long maxSizeBytes = maxSizeMB * 1024 * 1024;

        byte[] data = new byte[0];
        String contentType = request.getContentType();

        // Read request body if present (for POST/PUT)
        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
            try {
                data = readWithSizeLimit(request.getInputStream(), maxSizeBytes);
            } catch (IOException e) {
                LOGGER.warning("Failed to read request data: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to read request data");
                return;
            } catch (FileTooLargeException e) {
                LOGGER.warning("Request too large from " + remoteAddr + ": " + e.getMessage());
                response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                        "Request exceeds maximum size: " + maxSizeMB + "MB");
                return;
            }
        }

        // 8. Generate request ID
        String requestId = UUID.randomUUID().toString();

        // 9. Store to database
        ApiRequestInfo info = new ApiRequestInfo();
        info.setRequestId(requestId);
        info.setUserId(userId);
        info.setRemoteAddr(toIPv4Format(remoteAddr));
        info.setUserAgent(userAgent);
        info.setHttpMethod(httpMethod);
        info.setRequestPath(customPath);
        info.setRequestHeaders(serializeHeaders(request));
        info.setRequestBody(data);
        info.setContentType(contentType);
        info.setContentSize(data.length);
        info.setResponseStatus(HttpServletResponse.SC_OK);
        info.setAuthStatus(authRequired ? ApiRequestInfo.AUTH_STATUS_SUCCESS
                : ApiRequestInfo.AUTH_STATUS_NONE);
        info.setAuthUser(authenticatedUser != null ? authenticatedUser : pathUsername);
        info.setRequestTime(new Date());

        try {
            dao.insertApiRequest(info);
        } catch (Exception e) {
            LOGGER.severe("Failed to store API request to database: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to store request metadata");
            return;
        }

        // 10. Return success response (JSON)
        LOGGER.info("API request received: requestId=" + requestId +
                ", method=" + httpMethod +
                ", path=" + customPath +
                ", size=" + data.length +
                ", ip=" + remoteAddr +
                (authenticatedUser != null ? ", user=" + authenticatedUser : ""));

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println("{");
        out.println("  \"requestId\": \"" + requestId + "\",");
        out.println("  \"timestamp\": \"" + new Date() + "\",");
        out.println("  \"status\": \"success\",");
        out.println("  \"method\": \"" + httpMethod + "\",");
        out.println("  \"path\": \"" + escapeJson(customPath) + "\"");
        out.println("}");
    }

    /**
     * Return HTML info page when no username provided
     */
    private void returnInfoPage(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<!DOCTYPE HTML>");
        out.println("<HTML>");
        out.println("    <HEAD>");
        out.println("        <META NAME=\"description\" CONTENT=\"mendelson-e-commerce GmbH: Your EAI partner\">");
        out.println("        <META NAME=\"copyright\" CONTENT=\"mendelson-e-commerce GmbH\">");
        out.println("        <META NAME=\"robots\" CONTENT=\"NOINDEX,NOFOLLOW,NOARCHIVE,NOSNIPPET\">");
        out.println("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("        <title>" + AS2ServerVersion.getProductName() + " - REST API Endpoint</title>");
        out.println("    </HEAD>");
        out.println("    <BODY>");
        out.println("<H2>" + AS2ServerVersion.getProductName() + " - REST API Endpoint</H2>");
        out.println("<P>User-specific REST API endpoints.</P>");
        out.println("<P>URL pattern: <code>/as2/api/{username}/your-custom-path</code></P>");
        out.println("<P>Supported methods: GET, POST, PUT, DELETE</P>");
        out.println("<P>Configure authentication in the WebUI under \"My REST API Config\"</P>");
        out.println("    </BODY>");
        out.println("</HTML>");
    }

    /**
     * Read input stream with size limit
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
                throw new FileTooLargeException("Request size exceeds limit: " + maxSize + " bytes");
            }
            buffer.write(chunk, 0, bytesRead);
        }

        return buffer.toByteArray();
    }

    /**
     * Convert IPv6 address to IPv4 format where possible
     */
    private String toIPv4Format(String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }

        String cleanAddr = address;
        if (address.startsWith("[") && address.endsWith("]")) {
            cleanAddr = address.substring(1, address.length() - 1);
        }

        if ("0:0:0:0:0:0:0:1".equals(cleanAddr) || "::1".equals(cleanAddr)) {
            return "127.0.0.1";
        }

        if (cleanAddr.startsWith("::ffff:") && cleanAddr.length() > 7) {
            String ipv4Part = cleanAddr.substring(7);
            if (ipv4Part.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                return ipv4Part;
            }
        }

        return cleanAddr;
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

            if (notificationData != null
                    && notificationData.getNotificationMail() != null
                    && !notificationData.getNotificationMail().trim().isEmpty()) {
                // Could reuse TrackerNotificationMailer or create ApiNotificationMailer
                // For now, just log it
                LOGGER.warning("API attack notification: " + remoteAddr + " - " + failedAttempts + " failures");
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
     * Validate Basic Authentication
     */
    private boolean validateBasicAuth(String authHeader, List<UserApiAuthCredential> credentials) {
        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String decodedCredentials = new String(Base64.getDecoder().decode(base64Credentials), "UTF-8");

            int separatorIndex = decodedCredentials.indexOf(':');
            if (separatorIndex == -1) {
                return false;
            }

            String username = decodedCredentials.substring(0, separatorIndex);
            String password = decodedCredentials.substring(separatorIndex + 1);

            for (UserApiAuthCredential credential : credentials) {
                if (credential.isEnabled()
                        && credential.getAuthType() == UserApiAuthCredential.AUTH_TYPE_BASIC
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
     * Validate Certificate Authentication
     */
    private boolean validateCertAuth(X509Certificate cert, List<UserApiAuthCredential> credentials) {
        try {
            String certFingerprint = calculateFingerprint(cert);

            for (UserApiAuthCredential credential : credentials) {
                if (credential.getAuthType() == UserApiAuthCredential.AUTH_TYPE_CERTIFICATE) {
                    if (credential.isEnabled()
                            && certFingerprint.equalsIgnoreCase(credential.getCertFingerprint())) {
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            LOGGER.warning("Error validating cert auth: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calculate SHA-1 fingerprint of certificate
     */
    private String calculateFingerprint(X509Certificate cert) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();

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
     * Extract username from Basic Auth header
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
