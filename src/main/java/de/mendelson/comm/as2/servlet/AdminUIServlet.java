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

package de.mendelson.comm.as2.servlet;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistService;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Servlet to serve React Admin UI static files from classpath
 */
public class AdminUIServlet extends HttpServlet {

    private static final String RESOURCE_BASE = "/webapp/admin";
    private static final Logger LOGGER = Logger.getLogger("de.mendelson.as2.server");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Check IP whitelist for WebUI access BEFORE serving any content
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing != null) {
                PreferencesAS2 prefs = new PreferencesAS2(processing.getDBDriverManager());
                boolean webUIWhitelistEnabled = "true".equals(prefs.get(PreferencesAS2.IP_WHITELIST_ENABLED_WEBUI));

                if (webUIWhitelistEnabled) {
                    String clientIP = IPWhitelistService.normalizeIP(req.getRemoteAddr());
                    IPWhitelistService whitelistService = IPWhitelistService.getInstance(processing.getDBDriverManager());

                    // Check against global whitelist (userId=-1 for system-wide check)
                    if (!whitelistService.isAllowedForWebUI(clientIP, -1)) {
                        // Log blocked attempt
                        whitelistService.logBlockedAttempt(
                            clientIP,
                            "WEBUI",
                            null,  // No username yet - they're just loading the page
                            null,  // No partner for WebUI
                            req.getHeader("User-Agent"),
                            req.getRequestURI()
                        );

                        LOGGER.warning("IP " + clientIP + " blocked by WebUI whitelist when accessing " + req.getRequestURI());

                        // Return 403 Forbidden with a simple HTML message
                        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        resp.setContentType("text/html");
                        resp.getWriter().write(
                            "<!DOCTYPE html>" +
                            "<html>" +
                            "<head><title>Access Denied</title>" +
                            "<style>body{font-family:Arial,sans-serif;text-align:center;margin-top:50px;}" +
                            "h1{color:#d32f2f;}p{color:#666;}</style></head>" +
                            "<body>" +
                            "<h1>Access Denied</h1>" +
                            "<p>Please contact your system administrator.</p>" +
                            "</body>" +
                            "</html>"
                        );
                        return;
                    }
                }
            }
        } catch (Exception e) {
            // Log error but don't block request (graceful degradation)
            LOGGER.warning("IP whitelist check failed for WebUI: " + e.getMessage());
        }

        String pathInfo = req.getPathInfo();

        // Default to index.html for root path and SPA routes
        if (pathInfo == null || pathInfo.equals("/") || !pathInfo.contains(".")) {
            pathInfo = "/index.html";
        }

        String resourcePath = RESOURCE_BASE + pathInfo;

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                // For SPA routes, serve index.html
                resourcePath = RESOURCE_BASE + "/index.html";
                try (InputStream indexStream = getClass().getResourceAsStream(resourcePath)) {
                    if (indexStream == null) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    serveResource(indexStream, "text/html", resp);
                }
                return;
            }

            String contentType = getContentType(pathInfo);
            serveResource(is, contentType, resp);
        }
    }

    private void serveResource(InputStream is, String contentType, HttpServletResponse resp)
            throws IOException {
        resp.setContentType(contentType);
        resp.setStatus(HttpServletResponse.SC_OK);

        try (OutputStream os = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
