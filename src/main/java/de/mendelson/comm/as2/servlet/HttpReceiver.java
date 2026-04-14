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

                // Lookup user ID from database
                if ("admin".equals(targetUsername)) {
                    targetUserId = 0;  // Admin user
                } else {
                    // Look up non-admin user
                    try {
                        de.mendelson.comm.as2.usermanagement.UserManagementAccessDB userAccess =
                            new de.mendelson.comm.as2.usermanagement.UserManagementAccessDB(
                                AS2Server.getActivatedDBDriverManager(),
                                Logger.getLogger(AS2Server.SERVER_LOGGER_NAME));
                        de.mendelson.comm.as2.usermanagement.WebUIUser user = userAccess.getUserByUsername(targetUsername);
                        if (user != null) {
                            targetUserId = user.getId();
                        } else {
                            // User not found
                            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                "User not found: " + targetUsername);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Error looking up user: " + e.getMessage());
                        return;
                    }
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
                try {
                    Object attrValue = request.getAttribute(sslSessionAttributeKey);
                    if (attrValue instanceof SSLSession) {
                        SSLSession sslSession = (SSLSession) attrValue;
                        tlsProtocol = sslSession.getProtocol();
                        cipherSuite = sslSession.getCipherSuite();
                    }
                } catch (Exception e) {
                    // Ignore if SSL session cannot be retrieved
                    // This can happen with proxies or certain Jetty configurations
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

        // Get AS2-To header to identify which local station this message is for
        String as2To = headerMap.get("as2-to");
        if (as2To == null || as2To.isEmpty()) {
            logger.warning("No AS2-To header found in request from " + request.getRemoteAddr());
            return false;
        }

        // Unescape AS2-To header value
        as2To = de.mendelson.comm.as2.message.AS2MessageParser.unescapeFromToHeader(as2To);

        // Look up user by username
        de.mendelson.comm.as2.usermanagement.UserManagementAccessDB userAccess =
            new de.mendelson.comm.as2.usermanagement.UserManagementAccessDB(
                AS2Server.getActivatedDBDriverManager(), logger);
        de.mendelson.comm.as2.usermanagement.WebUIUser user = null;
        int userId = 0;

        try {
            user = userAccess.getUserByUsername(targetUsername);
            if (user != null) {
                userId = user.getId();
                // For backward compatibility: treat username "admin" as super-user (userId=0)
                if ("admin".equals(targetUsername)) {
                    userId = 0;
                }
            } else if ("admin".equals(targetUsername)) {
                // Admin user (backward compatibility)
                userId = 0;
            } else {
                logger.warning("User not found: " + targetUsername);
                return false;
            }
        } catch (Exception e) {
            logger.severe("Failed to lookup user: " + e.getMessage());
            return false;
        }

        // Find local station matching both user_id and AS2 ID
        de.mendelson.comm.as2.partner.PartnerAccessDB partnerAccess =
            new de.mendelson.comm.as2.partner.PartnerAccessDB(AS2Server.getActivatedDBDriverManager());

        // Get all local stations owned by this user
        java.util.List<de.mendelson.comm.as2.partner.Partner> userLocalStations =
            partnerAccess.getPartnersOwnedByUser(userId,
                de.mendelson.comm.as2.partner.PartnerAccessDB.DATA_COMPLETENESS_FULL);

        // Find the local station with matching AS2 ID
        de.mendelson.comm.as2.partner.Partner localStation = null;
        for (de.mendelson.comm.as2.partner.Partner partner : userLocalStations) {
            if (partner.isLocalStation() && as2To.equals(partner.getAS2Identification())) {
                localStation = partner;
                break;
            }
        }

        if (localStation == null) {
            logger.warning("No local station found for user '" + targetUsername +
                         "' with AS2-To: " + as2To + " (checked " + userLocalStations.size() + " partners)");
            return false;
        }

        // Get inbound authentication credentials list
        java.util.List<de.mendelson.comm.as2.partner.PartnerInboundAuthCredential> credentials =
            localStation.getInboundAuthCredentialsList();

        // No credentials = no auth required
        if (credentials.isEmpty()) {
            return true;
        }

        boolean anyBasicCredential = false;
        boolean anyCertCredential = false;
        boolean basicAuthPassed = false;
        boolean certAuthPassed = false;

        // Check what types of credentials are configured
        for (de.mendelson.comm.as2.partner.PartnerInboundAuthCredential credential : credentials) {
            if (!credential.isEnabled()) {
                continue;
            }
            if (credential.getAuthType() == de.mendelson.comm.as2.partner.PartnerInboundAuthCredential.AUTH_TYPE_BASIC) {
                anyBasicCredential = true;
            } else if (credential.getAuthType() == de.mendelson.comm.as2.partner.PartnerInboundAuthCredential.AUTH_TYPE_CERTIFICATE) {
                anyCertCredential = true;
            }
        }

        // Validate Basic Authentication (check ALL basic credentials)
        if (anyBasicCredential) {
            String authHeader = headerMap.get("authorization");
            if (authHeader != null && authHeader.startsWith("Basic ")) {
                try {
                    String base64Credentials = authHeader.substring(6);
                    byte[] decoded = Base64.getDecoder().decode(base64Credentials);
                    String credentialsStr = new String(decoded, StandardCharsets.UTF_8);
                    String[] parts = credentialsStr.split(":", 2);

                    if (parts.length == 2) {
                        String username = parts[0];
                        String password = parts[1];

                        // Check against ALL basic auth credentials
                        for (de.mendelson.comm.as2.partner.PartnerInboundAuthCredential credential : credentials) {
                            if (credential.isEnabled() &&
                                credential.getAuthType() == de.mendelson.comm.as2.partner.PartnerInboundAuthCredential.AUTH_TYPE_BASIC &&
                                username.equals(credential.getUsername()) &&
                                password.equals(credential.getPassword())) {
                                basicAuthPassed = true;
                                logger.info("Inbound Basic Auth accepted for user: " + username +
                                           " at local station: " + targetUsername + " from " + request.getRemoteAddr());
                                break;
                            }
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

        // Validate Certificate Authentication (check ALL cert credentials)
        if (anyCertCredential) {
            // Get client certificate from request
            // Method 1: Standard servlet API - javax.servlet.request.X509Certificate attribute
            java.security.cert.X509Certificate[] clientCerts =
                (java.security.cert.X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

            // Method 2: Try Jetty-specific attribute org.eclipse.jetty.server.x509
            if (clientCerts == null || clientCerts.length == 0) {
                logger.info("Client certificates not found via standard attribute, trying Jetty-specific attribute...");
                try {
                    Object jettyX509 = request.getAttribute("org.eclipse.jetty.server.x509");
                    if (jettyX509 instanceof java.security.cert.X509Certificate[]) {
                        clientCerts = (java.security.cert.X509Certificate[]) jettyX509;
                        logger.info("Found " + clientCerts.length + " client certificate(s) via Jetty attribute");
                    } else if (jettyX509 != null) {
                        // Jetty 12 wraps the certificate in org.eclipse.jetty.util.ssl.X509
                        // Try to extract the certificate using reflection
                        logger.info("org.eclipse.jetty.server.x509 is type: " + jettyX509.getClass().getName() + ", attempting to extract certificate...");
                        try {
                            // Call getCertificate() method on org.eclipse.jetty.util.ssl.X509
                            java.lang.reflect.Method getCertMethod = jettyX509.getClass().getMethod("getCertificate");
                            Object cert = getCertMethod.invoke(jettyX509);
                            if (cert instanceof java.security.cert.X509Certificate) {
                                clientCerts = new java.security.cert.X509Certificate[]{(java.security.cert.X509Certificate) cert};
                                logger.info("Extracted 1 certificate from Jetty X509 wrapper");
                            } else {
                                logger.warning("getCertificate() returned unexpected type: " +
                                    (cert != null ? cert.getClass().getName() : "null"));
                            }
                        } catch (NoSuchMethodException e) {
                            logger.warning("Jetty X509 wrapper doesn't have getCertificate() method: " + e.getMessage());
                        } catch (Exception e) {
                            logger.warning("Failed to extract certificate from Jetty wrapper: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Failed to get Jetty x509 attribute: " + e.getMessage());
                }
            }

            // Method 3: If not found, try SSL session attribute (for some servlet containers)
            if (clientCerts == null || clientCerts.length == 0) {
                logger.info("Client certificates not found via standard attribute, checking SSL session...");
                String sslSessionAttributeKey = null;
                java.util.Enumeration<String> attributeEnumeration = request.getAttributeNames();

                // Log all available attributes for debugging
                StringBuilder attrDebug = new StringBuilder("Available request attributes: ");
                java.util.Enumeration<String> debugEnum = request.getAttributeNames();
                while (debugEnum.hasMoreElements()) {
                    String attrName = debugEnum.nextElement();
                    attrDebug.append(attrName).append(", ");
                }
                logger.info(attrDebug.toString());

                while (attributeEnumeration.hasMoreElements()) {
                    String attributeKey = attributeEnumeration.nextElement();
                    if (attributeKey.toLowerCase().contains(".ssl_session") ||
                        attributeKey.toLowerCase().contains("sslsession")) {
                        sslSessionAttributeKey = attributeKey;
                        logger.info("Found SSL session attribute key: " + sslSessionAttributeKey);
                        break;
                    }
                }

                if (sslSessionAttributeKey != null) {
                    try {
                        Object attrValue = request.getAttribute(sslSessionAttributeKey);
                        if (attrValue instanceof javax.net.ssl.SSLSession) {
                            javax.net.ssl.SSLSession sslSession = (javax.net.ssl.SSLSession) attrValue;
                            try {
                                java.security.cert.Certificate[] certs = sslSession.getPeerCertificates();
                                if (certs != null && certs.length > 0) {
                                    clientCerts = new java.security.cert.X509Certificate[certs.length];
                                    for (int i = 0; i < certs.length; i++) {
                                        clientCerts[i] = (java.security.cert.X509Certificate) certs[i];
                                    }
                                }
                            } catch (javax.net.ssl.SSLPeerUnverifiedException e) {
                                logger.warning("No client certificate presented from " + request.getRemoteAddr());
                            } catch (Exception e) {
                                logger.warning("Failed to extract cert from SSL session: " + e.getMessage());
                            }
                        } else {
                            logger.warning("SSL session attribute is not a javax.net.ssl.SSLSession: "
                                + (attrValue != null ? attrValue.getClass().getName() : "null"));
                        }
                    } catch (Exception e) {
                        logger.warning("Failed to retrieve SSL session: " + e.getMessage());
                    }
                } else {
                    logger.warning("No SSL session attribute found in request. SSL client authentication may not be configured.");
                }
            }

            // Now validate the certificate if we found one
            if (clientCerts != null && clientCerts.length > 0) {
                try {
                    java.security.cert.X509Certificate clientCert = clientCerts[0];
                    String certFingerprint = calculateFingerprint(clientCert);
                    logger.info("Client certificate fingerprint: " + certFingerprint);

                    // Check against ALL certificate credentials
                    for (de.mendelson.comm.as2.partner.PartnerInboundAuthCredential credential : credentials) {
                        if (credential.isEnabled() &&
                            credential.getAuthType() == de.mendelson.comm.as2.partner.PartnerInboundAuthCredential.AUTH_TYPE_CERTIFICATE &&
                            credential.getCertFingerprint() != null) {
                            String configuredFingerprint = credential.getCertFingerprint();
                            logger.info("Comparing with configured fingerprint: " + configuredFingerprint);
                            if (certFingerprint.replace(":", "").equalsIgnoreCase(
                                    configuredFingerprint.replace(":", ""))) {
                                certAuthPassed = true;
                                logger.info("Inbound Certificate Auth accepted for local station: " + targetUsername +
                                           " from " + request.getRemoteAddr());
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Certificate validation failed: " + e.getMessage());
                }
            } else {
                logger.warning("No client certificate found in request. Remote client may not be sending a certificate, or SSL mutual authentication is not configured.");
            }

            if (!certAuthPassed) {
                logger.warning("Inbound Certificate Auth failed for local station: " + targetUsername +
                              " from " + request.getRemoteAddr());
            }
        }

        // OR logic: Pass if ANY configured auth method succeeded
        return basicAuthPassed || certAuthPassed;
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
