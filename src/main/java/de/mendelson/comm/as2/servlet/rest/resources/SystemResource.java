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

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.preferences.InboundAuthCredential;
import de.mendelson.comm.as2.preferences.InboundAuthCredentialAccessDB;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.httpconfig.server.HTTPServerConfigInfo;
import de.mendelson.util.systemevents.SystemEvent;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST resource for system information
 *
 */
@Path("/system")
public class SystemResource {

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystemInfo() {
        SystemInfo info = new SystemInfo();
        info.setProductName(AS2ServerVersion.getFullProductName());
        info.setVersion(AS2ServerVersion.getVersion());
        info.setBuild(AS2ServerVersion.getBuild());

        return Response.ok(info).build();
    }

    @GET
    @Path("/http-config")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHTTPServerConfig() {
        try {
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            HTTPServerConfigInfo configInfo = server.getHTTPServerConfigInfo();
            if (configInfo == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"HTTP server config not available\"}").build();
            }

            HTTPServerConfig config = new HTTPServerConfig();
            config.setJettyVersion(configInfo.getJettyHTTPServerVersion());
            config.setJavaVersion(configInfo.getJavaVersion());
            config.setTlsEnabled(configInfo.isSSLEnabled());
            config.setTlsSecurityProvider(configInfo.getTLSSecurityProviderName());
            config.setNeedClientAuth(configInfo.needsClientAuthentication());
            config.setReceiptURLPath(configInfo.getReceiptURLPath());
            config.setServerStatePath(configInfo.getServerStatePath());
            config.setConfigFile(configInfo.getHTTPServerConfigFile().toString());
            config.setUserConfigFile(configInfo.getHTTPServerUserConfigFile().toString());

            List<ListenerInfo> listeners = new ArrayList<>();
            for (HTTPServerConfigInfo.Listener listener : configInfo.getListener()) {
                ListenerInfo info = new ListenerInfo();
                info.setProtocol(listener.getProtocol());
                info.setPort(listener.getPort());
                info.setAdapter(listener.getAdapter());
                listeners.add(info);
            }
            config.setListeners(listeners);

            config.setExcludedProtocols(configInfo.getExcludedProtocols());
            config.setPossibleProtocols(configInfo.getPossibleProtocols());
            config.setExcludedCiphers(configInfo.getExcludedCipher());
            config.setPossibleCiphers(configInfo.getPossibleCipher());
            config.setDeployedWars(configInfo.getDeployedWars());

            return Response.ok(config).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/events")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystemEvents(
            @QueryParam("limit") @jakarta.ws.rs.DefaultValue("100") int limit,
            @QueryParam("severityError") @jakarta.ws.rs.DefaultValue("true") boolean severityError,
            @QueryParam("severityWarning") @jakarta.ws.rs.DefaultValue("true") boolean severityWarning,
            @QueryParam("severityInfo") @jakarta.ws.rs.DefaultValue("true") boolean severityInfo,
            @QueryParam("originSystem") @jakarta.ws.rs.DefaultValue("true") boolean originSystem,
            @QueryParam("originUser") @jakarta.ws.rs.DefaultValue("true") boolean originUser,
            @QueryParam("originTransaction") @jakarta.ws.rs.DefaultValue("true") boolean originTransaction,
            @QueryParam("category") @jakarta.ws.rs.DefaultValue("-1") int category,
            @QueryParam("searchText") String searchText) {
        try {
            List<SystemEventInfo> eventInfos = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat datePathFormat = new SimpleDateFormat("yyyyMMdd");

            // Build filter lists
            List<Integer> acceptedSeverities = new ArrayList<>();
            if (severityError) acceptedSeverities.add(SystemEvent.SEVERITY_ERROR);
            if (severityWarning) acceptedSeverities.add(SystemEvent.SEVERITY_WARNING);
            if (severityInfo) acceptedSeverities.add(SystemEvent.SEVERITY_INFO);

            List<Integer> acceptedOrigins = new ArrayList<>();
            if (originSystem) acceptedOrigins.add(SystemEvent.ORIGIN_SYSTEM);
            if (originUser) acceptedOrigins.add(SystemEvent.ORIGIN_USER);
            if (originTransaction) acceptedOrigins.add(SystemEvent.ORIGIN_TRANSACTION);

            // Get today's and yesterday's event directories
            java.util.Date now = new java.util.Date();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(now);

            List<java.nio.file.Path> eventDirs = new ArrayList<>();
            for (int i = 0; i < 2; i++) {  // Today and yesterday
                String dateStr = datePathFormat.format(cal.getTime());
                java.nio.file.Path eventsDir = Paths.get("log", dateStr, "events");
                if (Files.exists(eventsDir)) {
                    eventDirs.add(eventsDir);
                }
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1);
            }

            // Read event files
            List<SystemEvent> events = new ArrayList<>();
            for (java.nio.file.Path dir : eventDirs) {
                try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(dir, "*.event")) {
                    for (java.nio.file.Path eventFile : stream) {
                        try {
                            SystemEvent event = SystemEvent.parse(eventFile);
                            if (event != null) {
                                // Apply filters
                                boolean severityAccepted = acceptedSeverities.contains(event.getSeverity());
                                boolean originAccepted = acceptedOrigins.contains(event.getOrigin());
                                boolean categoryAccepted = (category == -1) || (event.getCategory() == category);

                                boolean textAccepted = true;
                                if (searchText != null && !searchText.trim().isEmpty()) {
                                    String searchLower = searchText.toLowerCase().trim();
                                    String subject = (event.getSubject() != null ? event.getSubject() : "").toLowerCase();
                                    String body = (event.getBody() != null ? event.getBody() : "").toLowerCase();
                                    String id = (event.getId() != null ? event.getId() : "").toLowerCase();
                                    textAccepted = subject.contains(searchLower) ||
                                                 body.contains(searchLower) ||
                                                 id.contains(searchLower);
                                }

                                if (severityAccepted && originAccepted && categoryAccepted && textAccepted) {
                                    events.add(event);
                                }
                            }
                        } catch (Exception e) {
                            // Skip invalid event files
                        }
                    }
                }
            }

            // Sort by timestamp descending
            events.sort((e1, e2) -> Long.compare(e2.getTimestamp(), e1.getTimestamp()));

            // Limit results
            int maxEvents = Math.min(limit, 1000);
            if (events.size() > maxEvents) {
                events = events.subList(0, maxEvents);
            }

            // Convert to DTO
            for (SystemEvent event : events) {
                SystemEventInfo info = new SystemEventInfo();
                info.setId(event.getId());
                info.setTimestamp(dateFormat.format(new java.util.Date(event.getTimestamp())));
                info.setSeverity(getSeverityString(event.getSeverity()));
                info.setOrigin(getOriginString(event.getOrigin()));
                info.setCategory(event.getCategory());
                info.setType(event.getType());
                info.setSubject(event.getSubject());
                info.setBody(event.getBody());
                info.setUserId(event.getUser());
                info.setProcessOriginHost(event.getProcessOriginHost());
                eventInfos.add(info);
            }

            return Response.ok(eventInfos).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/event-categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventCategories() {
        List<CategoryInfo> categories = new ArrayList<>();
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_SERVER_COMPONENTS, "Server Components"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_CONNECTIVITY, "Connectivity"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_TRANSACTION, "Transaction"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_CERTIFICATE, "Certificate"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_DATABASE, "Database"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_CONFIGURATION, "Configuration"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_QUOTA, "Quota"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_NOTIFICATION, "Notification"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_PROCESSING, "Processing"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_LICENSE, "License"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_FILE_OPERATION, "File Operation"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_CLIENT_OPERATION, "Client Operation"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_XML_INTERFACE, "XML Interface"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_REST_INTERFACE, "REST Interface"));
        categories.add(new CategoryInfo(SystemEvent.CATEGORY_OTHER, "Other"));
        return Response.ok(categories).build();
    }

    @GET
    @Path("/serverlog")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchServerLog(
            @QueryParam("searchText") String searchText,
            @QueryParam("limit") @jakarta.ws.rs.DefaultValue("500") int limit) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String today = dateFormat.format(new java.util.Date());
            java.nio.file.Path logPath = Paths.get("log", today, "as2.log");

            if (!Files.exists(logPath)) {
                return Response.ok(new ServerLogResponse()).build();
            }

            List<String> allLines = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(logPath, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    allLines.add(line);
                }
            }

            List<String> matchedLines;
            if (searchText != null && !searchText.trim().isEmpty()) {
                String searchLower = searchText.toLowerCase();
                matchedLines = allLines.stream()
                        .filter(line -> line.toLowerCase().contains(searchLower))
                        .collect(Collectors.toList());
            } else {
                matchedLines = allLines;
            }

            int totalMatches = matchedLines.size();
            int maxLines = Math.min(limit, 1000);

            if (matchedLines.size() > maxLines) {
                matchedLines = matchedLines.subList(Math.max(0, matchedLines.size() - maxLines), matchedLines.size());
            }

            ServerLogResponse response = new ServerLogResponse();
            response.setLogFile(logPath.toString());
            response.setTotalLines(allLines.size());
            response.setMatchedLines(totalMatches);
            response.setLines(matchedLines);
            response.setTruncated(totalMatches > maxLines);

            return Response.ok(response).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    private String getSeverityString(int severity) {
        switch (severity) {
            case SystemEvent.SEVERITY_INFO:
                return "INFO";
            case SystemEvent.SEVERITY_WARNING:
                return "WARNING";
            case SystemEvent.SEVERITY_ERROR:
                return "ERROR";
            default:
                return "UNKNOWN";
        }
    }

    private String getOriginString(int origin) {
        switch (origin) {
            case SystemEvent.ORIGIN_SYSTEM:
                return "SYSTEM";
            case SystemEvent.ORIGIN_USER:
                return "USER";
            case SystemEvent.ORIGIN_TRANSACTION:
                return "TRANSACTION";
            default:
                return "UNKNOWN";
        }
    }

    // Inbound Authentication endpoints
    @GET
    @Path("/inbound-auth/config")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInboundAuthConfig() {
        try {
            PreferencesAS2 preferences = new PreferencesAS2();
            int authMode = preferences.getInt(PreferencesAS2.INBOUND_AUTH_MODE);

            InboundAuthConfig config = new InboundAuthConfig();
            config.setAuthMode(authMode);

            return Response.ok(config).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/inbound-auth/config")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveInboundAuthConfig(InboundAuthConfig config) {
        try {
            PreferencesAS2 preferences = new PreferencesAS2();
            preferences.putInt(PreferencesAS2.INBOUND_AUTH_MODE, config.getAuthMode());

            return Response.ok("{\"success\":true}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/inbound-auth/credentials/basic")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBasicAuthCredentials() {
        try {
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            InboundAuthCredentialAccessDB credentialDB =
                new InboundAuthCredentialAccessDB(dbDriverManager, null);

            List<InboundAuthCredential> credentials =
                credentialDB.getCredentials(InboundAuthCredential.TYPE_BASIC);

            return Response.ok(credentials).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/inbound-auth/credentials/basic")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveBasicAuthCredentials(List<InboundAuthCredential> credentials) {
        try {
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            InboundAuthCredentialAccessDB credentialDB =
                new InboundAuthCredentialAccessDB(dbDriverManager, null);

            // Delete existing and add new
            credentialDB.deleteAllCredentials(InboundAuthCredential.TYPE_BASIC);
            for (InboundAuthCredential cred : credentials) {
                if (cred.getUsername() != null && !cred.getUsername().trim().isEmpty()) {
                    cred.setAuthType(InboundAuthCredential.TYPE_BASIC);
                    credentialDB.addCredential(cred);
                }
            }

            return Response.ok("{\"success\":true}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/inbound-auth/credentials/cert")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCertAuthCredentials() {
        try {
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            InboundAuthCredentialAccessDB credentialDB =
                new InboundAuthCredentialAccessDB(dbDriverManager, null);

            List<InboundAuthCredential> credentials =
                credentialDB.getCredentials(InboundAuthCredential.TYPE_CERTIFICATE);

            return Response.ok(credentials).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/inbound-auth/credentials/cert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveCertAuthCredentials(List<InboundAuthCredential> credentials) {
        try {
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            InboundAuthCredentialAccessDB credentialDB =
                new InboundAuthCredentialAccessDB(dbDriverManager, null);

            // Delete existing and add new
            credentialDB.deleteAllCredentials(InboundAuthCredential.TYPE_CERTIFICATE);
            for (InboundAuthCredential cred : credentials) {
                if (cred.getCertAlias() != null && !cred.getCertAlias().trim().isEmpty()) {
                    cred.setAuthType(InboundAuthCredential.TYPE_CERTIFICATE);
                    credentialDB.addCredential(cred);
                }
            }

            return Response.ok("{\"success\":true}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/tracker/config")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrackerConfig() {
        try {
            PreferencesAS2 preferences = new PreferencesAS2();

            TrackerConfig config = new TrackerConfig();
            config.setEnabled("true".equals(preferences.get(PreferencesAS2.TRACKER_ENABLED)));
            config.setAuthRequired("true".equals(preferences.get(PreferencesAS2.TRACKER_AUTH_REQUIRED)));
            config.setMaxSizeMB(preferences.getInt(PreferencesAS2.TRACKER_MAX_SIZE_MB));
            config.setRateLimitFailures(preferences.getInt(PreferencesAS2.TRACKER_RATE_LIMIT_FAILURES));
            config.setRateLimitWindowHours(preferences.getInt(PreferencesAS2.TRACKER_RATE_LIMIT_WINDOW_HOURS));
            config.setRateLimitBlockMinutes(preferences.getInt(PreferencesAS2.TRACKER_RATE_LIMIT_BLOCK_MINUTES));

            return Response.ok(config).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/tracker/config")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveTrackerConfig(TrackerConfig config) {
        try {
            PreferencesAS2 preferences = new PreferencesAS2();

            preferences.put(PreferencesAS2.TRACKER_ENABLED, config.isEnabled() ? "true" : "false");
            preferences.put(PreferencesAS2.TRACKER_AUTH_REQUIRED, config.isAuthRequired() ? "true" : "false");
            preferences.putInt(PreferencesAS2.TRACKER_MAX_SIZE_MB, config.getMaxSizeMB());
            preferences.putInt(PreferencesAS2.TRACKER_RATE_LIMIT_FAILURES, config.getRateLimitFailures());
            preferences.putInt(PreferencesAS2.TRACKER_RATE_LIMIT_WINDOW_HOURS, config.getRateLimitWindowHours());
            preferences.putInt(PreferencesAS2.TRACKER_RATE_LIMIT_BLOCK_MINUTES, config.getRateLimitBlockMinutes());

            return Response.ok("{\"success\":true}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    public static class SystemInfo {
        private String productName;
        private String version;
        private String build;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getBuild() {
            return build;
        }

        public void setBuild(String build) {
            this.build = build;
        }
    }

    public static class HTTPServerConfig {
        private String jettyVersion;
        private String javaVersion;
        private boolean tlsEnabled;
        private String tlsSecurityProvider;
        private boolean needClientAuth;
        private String receiptURLPath;
        private String serverStatePath;
        private String configFile;
        private String userConfigFile;
        private List<ListenerInfo> listeners;
        private List<String> excludedProtocols;
        private List<String> possibleProtocols;
        private List<String> excludedCiphers;
        private List<String> possibleCiphers;
        private List<String> deployedWars;

        // Getters and setters
        public String getJettyVersion() { return jettyVersion; }
        public void setJettyVersion(String jettyVersion) { this.jettyVersion = jettyVersion; }
        public String getJavaVersion() { return javaVersion; }
        public void setJavaVersion(String javaVersion) { this.javaVersion = javaVersion; }
        public boolean isTlsEnabled() { return tlsEnabled; }
        public void setTlsEnabled(boolean tlsEnabled) { this.tlsEnabled = tlsEnabled; }
        public String getTlsSecurityProvider() { return tlsSecurityProvider; }
        public void setTlsSecurityProvider(String tlsSecurityProvider) { this.tlsSecurityProvider = tlsSecurityProvider; }
        public boolean isNeedClientAuth() { return needClientAuth; }
        public void setNeedClientAuth(boolean needClientAuth) { this.needClientAuth = needClientAuth; }
        public String getReceiptURLPath() { return receiptURLPath; }
        public void setReceiptURLPath(String receiptURLPath) { this.receiptURLPath = receiptURLPath; }
        public String getServerStatePath() { return serverStatePath; }
        public void setServerStatePath(String serverStatePath) { this.serverStatePath = serverStatePath; }
        public String getConfigFile() { return configFile; }
        public void setConfigFile(String configFile) { this.configFile = configFile; }
        public String getUserConfigFile() { return userConfigFile; }
        public void setUserConfigFile(String userConfigFile) { this.userConfigFile = userConfigFile; }
        public List<ListenerInfo> getListeners() { return listeners; }
        public void setListeners(List<ListenerInfo> listeners) { this.listeners = listeners; }
        public List<String> getExcludedProtocols() { return excludedProtocols; }
        public void setExcludedProtocols(List<String> excludedProtocols) { this.excludedProtocols = excludedProtocols; }
        public List<String> getPossibleProtocols() { return possibleProtocols; }
        public void setPossibleProtocols(List<String> possibleProtocols) { this.possibleProtocols = possibleProtocols; }
        public List<String> getExcludedCiphers() { return excludedCiphers; }
        public void setExcludedCiphers(List<String> excludedCiphers) { this.excludedCiphers = excludedCiphers; }
        public List<String> getPossibleCiphers() { return possibleCiphers; }
        public void setPossibleCiphers(List<String> possibleCiphers) { this.possibleCiphers = possibleCiphers; }
        public List<String> getDeployedWars() { return deployedWars; }
        public void setDeployedWars(List<String> deployedWars) { this.deployedWars = deployedWars; }
    }

    public static class ListenerInfo {
        private String protocol;
        private int port;
        private String adapter;

        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public String getAdapter() { return adapter; }
        public void setAdapter(String adapter) { this.adapter = adapter; }
    }

    public static class SystemEventInfo {
        private String id;
        private String timestamp;
        private String severity;
        private String origin;
        private int category;
        private int type;
        private String subject;
        private String body;
        private String userId;
        private String processOriginHost;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public int getCategory() { return category; }
        public void setCategory(int category) { this.category = category; }
        public int getType() { return type; }
        public void setType(int type) { this.type = type; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getProcessOriginHost() { return processOriginHost; }
        public void setProcessOriginHost(String processOriginHost) { this.processOriginHost = processOriginHost; }
    }

    public static class ServerLogResponse {
        private String logFile;
        private int totalLines;
        private int matchedLines;
        private List<String> lines = new ArrayList<>();
        private boolean truncated;

        public String getLogFile() { return logFile; }
        public void setLogFile(String logFile) { this.logFile = logFile; }
        public int getTotalLines() { return totalLines; }
        public void setTotalLines(int totalLines) { this.totalLines = totalLines; }
        public int getMatchedLines() { return matchedLines; }
        public void setMatchedLines(int matchedLines) { this.matchedLines = matchedLines; }
        public List<String> getLines() { return lines; }
        public void setLines(List<String> lines) { this.lines = lines; }
        public boolean isTruncated() { return truncated; }
        public void setTruncated(boolean truncated) { this.truncated = truncated; }
    }

    public static class CategoryInfo {
        private int value;
        private String name;

        public CategoryInfo(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class InboundAuthConfig {
        private int authMode;

        public int getAuthMode() { return authMode; }
        public void setAuthMode(int authMode) { this.authMode = authMode; }
    }

    public static class TrackerConfig {
        private boolean enabled;
        private boolean authRequired;
        private int maxSizeMB;
        private int rateLimitFailures;
        private int rateLimitWindowHours;
        private int rateLimitBlockMinutes;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isAuthRequired() { return authRequired; }
        public void setAuthRequired(boolean authRequired) { this.authRequired = authRequired; }
        public int getMaxSizeMB() { return maxSizeMB; }
        public void setMaxSizeMB(int maxSizeMB) { this.maxSizeMB = maxSizeMB; }
        public int getRateLimitFailures() { return rateLimitFailures; }
        public void setRateLimitFailures(int rateLimitFailures) { this.rateLimitFailures = rateLimitFailures; }
        public int getRateLimitWindowHours() { return rateLimitWindowHours; }
        public void setRateLimitWindowHours(int rateLimitWindowHours) { this.rateLimitWindowHours = rateLimitWindowHours; }
        public int getRateLimitBlockMinutes() { return rateLimitBlockMinutes; }
        public void setRateLimitBlockMinutes(int rateLimitBlockMinutes) { this.rateLimitBlockMinutes = rateLimitBlockMinutes; }
    }
}
