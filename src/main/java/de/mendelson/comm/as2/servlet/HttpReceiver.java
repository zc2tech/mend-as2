 package de.mendelson.comm.as2.servlet;

import de.mendelson.Copyright;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageRequest;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageResponse;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2MessageProcessor;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.clientserver.AnonymousTextClient;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Logger;
import javax.net.ssl.SSLSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Servlet to receive AS2 messages via HTTP
 *
 * @author S.Heller
 * @version $Revision: 64 $
 */
public class HttpReceiver extends HttpServlet {

    public HttpReceiver() {
    }

    /**
     * A GET request should be rejected
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        res.setContentType("text/html");
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        out.println("<HTML>");
        out.println("    <HEAD>");
        out.println("        <META NAME=\"description\" CONTENT=\"mendelson-e-commerce GmbH: Your EAI partner\">");
        out.println("        <META NAME=\"copyright\" CONTENT=\"mendelson-e-commerce GmbH\">");
        out.println("        <META NAME=\"robots\" CONTENT=\"NOINDEX,NOFOLLOW,NOARCHIVE,NOSNIPPET\">");
        out.println("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("        <title>" + AS2ServerVersion.getProductName() + "</title>");
        out.println("        <link rel=\"shortcut icon\" href=\"images/mendelson_favicon.png\" type=\"image/x-icon\" />");
        out.println("    </HEAD>");
        out.println("    <BODY>");
        out.println("<H2>" + AS2ServerVersion.getProductName() + " " + AS2ServerVersion.getVersion() + " " + AS2ServerVersion.getBuild() + "</H2>");
        out.println("<BR> " + Copyright.getCopyrightMessage());
        out.println("<BR><br>You have performed an HTTP GET on this URL. <BR>");
        out.println("To submit an AS2 message, you must POST the message to this URL <BR>");
        out.println("    </BODY>");
        out.println("</HTML>");
    }

    /**
     * POST by the HTTP client: receive the message and work on it
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //stores if the commit already occured. Do not send an additional error in this case
        boolean committed = false;
        Path dataFile = null;
        try {
            // Extract username from URL path for user-specific endpoints
            String pathInfo = request.getPathInfo();  // e.g., "/john" or null
            String targetUsername = null;
            int targetUserId = 0;

            if (pathInfo != null && pathInfo.length() > 1) {
                // Remove leading slash
                targetUsername = pathInfo.substring(1);

                // Lookup user ID (would need UserManagementAccessDB here)
                // For now, set to 0 (admin) - will be enhanced later
                targetUserId = 0;  // TODO: Implement user lookup

                if (targetUserId == 0 && !"admin".equals(targetUsername)) {
                    // User not found
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "User not found: " + targetUsername);
                    return;
                }
            } else {
                // No path info - use system/admin (backward compatibility)
                targetUsername = "admin";
                targetUserId = 0;
            }

            // Store user context for processing
            request.setAttribute("targetUserId", targetUserId);
            request.setAttribute("targetUsername", targetUsername);

            String tlsProtocol = "-";
            String cipherSuite = "-";
            int localPort = request.getLocalPort();
            String remoteAddress = request.getRemoteAddr();
            //might be one of
            //javax.servlet.request.ssl_session
            //org.eclipse.jetty.servlet.request.ssl_session
            //[]...
            String sslSessionAttributeKey = null;
            Enumeration<String> attributeEnumeration = request.getAttributeNames();
            while (attributeEnumeration.hasMoreElements()) {
                String attributeKey = attributeEnumeration.nextElement();
                if (attributeKey.toLowerCase().contains(".ssl_session")) {
                    sslSessionAttributeKey = attributeKey;
                    break;
                }
            }
            //get SSL information
            if (sslSessionAttributeKey != null) {
                SSLSession sslSession = (SSLSession) request.getAttribute(sslSessionAttributeKey);
                if (sslSession != null) {
                    tlsProtocol = sslSession.getProtocol();
                    cipherSuite = sslSession.getCipherSuite();
                }
            }
            InputStream inStream = request.getInputStream();
            //store the data in a file to process it later. This may be useful
            //for a huge data request that may lead to a out of memory fairly easy.
            dataFile = AS2Tools.createTempFile("as2", "request");
            Files.copy(inStream, dataFile, StandardCopyOption.REPLACE_EXISTING);
            //extract header
            LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
            Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                headerMap.put(key.toLowerCase(), request.getHeader(key));
            }

            // Validate inbound authentication before processing message
            if (!this.validateInboundAuthentication(headerMap, request)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "Basic realm=\"AS2 Server\"");
                committed = true;
                return;
            }

            //check if this is a AS2 message that requests async MDN. In this case return the ok code
            //before processing the message, there is no need to keep the connection alive.
            boolean isAS2MessageRequestingAsyncMDN = headerMap.containsKey("receipt-delivery-option") 
                    && headerMap.get("receipt-delivery-option") != null 
                    && !headerMap.get("receipt-delivery-option").trim().isEmpty();
            if (isAS2MessageRequestingAsyncMDN) {
                this.informAS2ServerIncomingMessage(dataFile, headerMap, request, null, tlsProtocol, cipherSuite, localPort,
                        remoteAddress);
                committed = true;
                response.setStatus(HttpServletResponse.SC_OK);
                //close the connection
                response.getWriter().flush();
                response.getWriter().close();
            } else {
                this.informAS2ServerIncomingMessage(dataFile, headerMap, request, response, tlsProtocol, cipherSuite, localPort,
                        remoteAddress);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (!committed) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } finally {
            if (dataFile != null) {
                try {
                    Files.delete(dataFile);
                } catch (IOException e) {
                    SystemEvent event = new SystemEvent(
                            SystemEvent.SEVERITY_WARNING,
                            SystemEvent.ORIGIN_SYSTEM,
                            SystemEvent.TYPE_FILE_DELETE);
                    event.setSubject(event.typeToTextLocalized());
                    event.setBody("[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
                    SystemEventManagerImplAS2.instance().newEvent(event);
                }
            }
        }
    }//end of doPost

    /**
     * Informs the AS2 server that a new message arrived and returns the HTTP
     * returncode that has been set by the processing server
     */
    private void informAS2ServerIncomingMessage(Path dataFile,
            LinkedHashMap<String, String> headerMap, HttpServletRequest request,
            HttpServletResponse response, String tlsProtocol,
            String cipherSuite, int localPort, String remoteAddress) throws Throwable {
        IncomingMessageRequest messageRequest = new IncomingMessageRequest();
        messageRequest.setMessageDataFilename(dataFile.toAbsolutePath().toString());
        messageRequest.setContentType(request.getContentType());
        messageRequest.setUsesTLS(request.isSecure());
        messageRequest.setLocalPort(localPort);
        messageRequest.setTLSProtocol(tlsProtocol);
        messageRequest.setCipherSuite(cipherSuite);
        messageRequest.setRemoteAddress(remoteAddress);

        // Set target user ID from request attributes
        Integer targetUserId = (Integer) request.getAttribute("targetUserId");
        if (targetUserId != null) {
            messageRequest.setTargetUserId(targetUserId);
        }

        String remoteHost = request.getRemoteHost();
        if (remoteHost == null) {
            remoteHost = request.getRemoteAddr();
        }
        messageRequest.setRemoteHost(remoteHost);
        Iterator<String> headerIterator = headerMap.keySet().iterator();
        while (headerIterator.hasNext()) {
            String key = headerIterator.next();
            if (key != null) {
                String value = headerMap.get(key);
                if (value != null) {
                    messageRequest.addHeader(key, value);
                }
            }
        }
        // Direct method call instead of Mina socket communication
        AS2MessageProcessor processor = AS2MessageProcessor.getInstance();
        IncomingMessageResponse messageResponse = processor.processIncomingMessage(messageRequest);

        if (messageResponse == null) {
            throw new Exception("Failed to process AS2 message: no response received");
        }

        if (messageResponse.getException() != null) {
            throw messageResponse.getException();
        }

        //build up response, this is the sync MDN
        if (response != null) {
            if (messageResponse.getHttpReturnCode() != HttpServletResponse.SC_OK) {
                response.setStatus(messageResponse.getHttpReturnCode());
            }
            //add MDN data
            if (messageResponse.getMDNData() != null) {
                Properties header = messageResponse.getHeader();
                Iterator<Object> iterator = header.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    response.setHeader(key, header.getProperty(key));
                }
                ServletOutputStream outStream;
                try (ByteArrayInputStream inStream = new ByteArrayInputStream(messageResponse.getMDNData())) {
                    outStream = response.getOutputStream();
                    inStream.transferTo(outStream);
                }
                outStream.flush();
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Receive AS2 messages via HTTP/S";
    }

    /**
     * Validate inbound authentication based on per-partner configuration.
     * Returns true if authentication passes or is disabled, false otherwise.
     * Implements OR logic: message accepted if it matches ANY enabled auth method.
     *
     * This validates EXTERNAL partner authentication, not internal client-server communication.
     */
    private boolean validateInboundAuthentication(LinkedHashMap<String, String> headerMap, HttpServletRequest request) {
        Logger logger = Logger.getLogger("de.mendelson.as2.server");

        // Get target username from request attribute (set in doPost)
        String targetUsername = (String) request.getAttribute("targetUsername");
        if (targetUsername == null) {
            logger.severe("No username in request path - endpoint requires /as2/HttpReceiver/{username}");
            return false;
        }

        // Load local station partner by username/AS2 ID
        // Convention: local station AS2 ID should match username
        de.mendelson.comm.as2.partner.PartnerAccessDB partnerAccess =
            new de.mendelson.comm.as2.partner.PartnerAccessDB(AS2Server.getActivatedDBDriverManager());
        de.mendelson.comm.as2.partner.Partner localStation =
            partnerAccess.getPartnerByAS2Id(targetUsername,
                de.mendelson.comm.as2.partner.PartnerAccessDB.DATA_COMPLETENESS_FULL);

        if (localStation == null || !localStation.isLocalStation()) {
            logger.warning("No local station found for username: " + targetUsername);
            return false;
        }

        de.mendelson.comm.as2.partner.HTTPAuthentication inboundAuth = localStation.getInboundAuthCredentials();
        int authMode = inboundAuth.getAuthMode();

        // Mode 0: No authentication required
        if (authMode == 0) {
            return true;
        }

        boolean basicAuthRequired = (authMode & 1) != 0;
        boolean certAuthRequired = (authMode & 2) != 0;

        boolean basicAuthPassed = false;
        boolean certAuthPassed = false;

        // Check basic authentication
        if (basicAuthRequired) {
            String authHeader = headerMap.get("authorization");
            if (authHeader != null && authHeader.startsWith("Basic ")) {
                try {
                    String base64Credentials = authHeader.substring(6);
                    byte[] decoded = Base64.getDecoder().decode(base64Credentials);
                    String credentials = new String(decoded, StandardCharsets.UTF_8);
                    String[] parts = credentials.split(":", 2);

                    if (parts.length == 2) {
                        String username = parts[0];
                        String password = parts[1];

                        // Match against configured credentials
                        if (username.equals(inboundAuth.getUser()) &&
                            password.equals(inboundAuth.getPassword())) {
                            basicAuthPassed = true;
                            logger.info("Inbound Basic Auth accepted for local station: " + targetUsername +
                                       " from " + request.getRemoteAddr());
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Inbound Basic Auth parsing failed: " + e.getMessage());
                }
            }

            if (!basicAuthPassed) {
                logger.warning("Inbound Basic Auth failed for local station: " + targetUsername +
                              " from " + request.getRemoteAddr());
            }
        }

        // Check certificate authentication
        if (certAuthRequired) {
            // Get client certificate from SSL session
            String sslSessionAttributeKey = null;
            java.util.Enumeration<String> attributeEnumeration = request.getAttributeNames();
            while (attributeEnumeration.hasMoreElements()) {
                String attributeKey = attributeEnumeration.nextElement();
                if (attributeKey.toLowerCase().contains(".ssl_session")) {
                    sslSessionAttributeKey = attributeKey;
                    break;
                }
            }

            if (sslSessionAttributeKey != null) {
                javax.net.ssl.SSLSession sslSession = (javax.net.ssl.SSLSession) request.getAttribute(sslSessionAttributeKey);
                if (sslSession != null) {
                    try {
                        java.security.cert.Certificate[] certs = sslSession.getPeerCertificates();
                        if (certs != null && certs.length > 0) {
                            java.security.cert.X509Certificate clientCert = (java.security.cert.X509Certificate) certs[0];
                            String certFingerprint = calculateFingerprint(clientCert);

                            // Match against configured certificate
                            String configuredFingerprint = inboundAuth.getCertificateFingerprint();
                            if (configuredFingerprint != null &&
                                certFingerprint.replace(":", "").equalsIgnoreCase(
                                    configuredFingerprint.replace(":", ""))) {
                                certAuthPassed = true;
                                logger.info("Inbound Certificate Auth accepted for local station: " + targetUsername +
                                           " from " + request.getRemoteAddr());
                            }
                        }
                    } catch (javax.net.ssl.SSLPeerUnverifiedException e) {
                        // No client certificate presented
                        logger.warning("No client certificate presented from " + request.getRemoteAddr());
                    } catch (Exception e) {
                        logger.warning("Certificate validation failed: " + e.getMessage());
                    }
                }
            }

            if (!certAuthPassed) {
                logger.warning("Inbound Certificate Auth failed for local station: " + targetUsername +
                              " from " + request.getRemoteAddr());
            }
        }

        // OR logic: pass if ANY required auth method succeeds
        if (basicAuthRequired && !basicAuthPassed) {
            return false;
        }
        if (certAuthRequired && !certAuthPassed) {
            return false;
        }

        return true;
    }

    /**
     * Helper method to calculate SHA-1 fingerprint of a certificate
     */
    private String calculateFingerprint(java.security.cert.X509Certificate cert) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            byte[] der = cert.getEncoded();
            md.update(der);
            byte[] digest = md.digest();

            // Convert to hex string with colons
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
        } catch (Exception e) {
            return "";
        }
    }
}
