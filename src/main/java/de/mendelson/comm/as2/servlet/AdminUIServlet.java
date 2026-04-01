package de.mendelson.comm.as2.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
