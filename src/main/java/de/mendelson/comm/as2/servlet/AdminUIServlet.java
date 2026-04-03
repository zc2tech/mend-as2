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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Servlet to serve React Admin UI static files from classpath
 */
public class AdminUIServlet extends HttpServlet {

    private static final String RESOURCE_BASE = "/webapp/admin";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

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
