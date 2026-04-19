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

import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistAccessDB;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistBlockLog;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistEntry;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistMatcher;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistService;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.comm.as2.servlet.rest.auth.AdminOnly;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * REST resource for IP whitelist management operations
 * Accessible only to 'admin' super user
 *
 * @author Julian Xu
 */
@Path("/ipwhitelist")
@AdminOnly
public class IPWhitelistResource {

    private static final Logger LOGGER = Logger.getLogger(IPWhitelistResource.class.getName());

    // ========== Settings Endpoints ==========

    /**
     * Get current IP whitelist settings
     * GET /ipwhitelist/settings
     */
    @GET
    @Path("/settings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSettings() {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            PreferencesAS2 prefs = new PreferencesAS2(processing.getDBDriverManager());

            WhitelistSettingsDTO settings = new WhitelistSettingsDTO();
            settings.setEnabledAS2(prefs.getBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_AS2));
            settings.setEnabledTracker(prefs.getBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_TRACKER));
            settings.setEnabledWebUI(prefs.getBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_WEBUI));
            settings.setEnabledAPI(prefs.getBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_API));
            settings.setMode(prefs.get(PreferencesAS2.IP_WHITELIST_MODE));
            settings.setLogRetentionDays(prefs.getInt(PreferencesAS2.IP_WHITELIST_LOG_RETENTION_DAYS));

            return Response.ok(settings).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to get whitelist settings: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to retrieve whitelist settings: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Update IP whitelist settings
     * POST /ipwhitelist/settings
     */
    @POST
    @Path("/settings")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSettings(WhitelistSettingsDTO settings) {
        try {
            // Validate mode
            String mode = settings.getMode();
            if (mode == null || (!mode.equals("GLOBAL_ONLY") && !mode.equals("PARTNER_ONLY") &&
                !mode.equals("USER_ONLY") && !mode.equals("GLOBAL_AND_SPECIFIC"))) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid mode. Must be one of: GLOBAL_ONLY, PARTNER_ONLY, USER_ONLY, GLOBAL_AND_SPECIFIC"))
                        .build();
            }

            // Validate retention days
            int retentionDays = settings.getLogRetentionDays();
            if (retentionDays < 1 || retentionDays > 365) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Log retention days must be between 1 and 365"))
                        .build();
            }

            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            PreferencesAS2 prefs = new PreferencesAS2(processing.getDBDriverManager());

            // Update settings
            prefs.putBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_AS2, settings.isEnabledAS2());
            prefs.putBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_TRACKER, settings.isEnabledTracker());
            prefs.putBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_WEBUI, settings.isEnabledWebUI());
            prefs.putBoolean(PreferencesAS2.IP_WHITELIST_ENABLED_API, settings.isEnabledAPI());
            prefs.put(PreferencesAS2.IP_WHITELIST_MODE, settings.getMode());
            prefs.putInt(PreferencesAS2.IP_WHITELIST_LOG_RETENTION_DAYS, settings.getLogRetentionDays());

            LOGGER.info("IP whitelist settings updated: AS2=" + settings.isEnabledAS2() +
                       ", TRACKER=" + settings.isEnabledTracker() +
                       ", WEBUI=" + settings.isEnabledWebUI() +
                       ", API=" + settings.isEnabledAPI() +
                       ", MODE=" + settings.getMode());

            return Response.ok(settings).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to update whitelist settings: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to update whitelist settings: " + e.getMessage()))
                    .build();
        }
    }

    // ========== Global Whitelist Endpoints ==========

    /**
     * Get all global whitelist entries, optionally filtered by target type
     * GET /ipwhitelist/global?targetType=AS2
     */
    @GET
    @Path("/global")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGlobalWhitelist(@QueryParam("targetType") String targetType) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            List<IPWhitelistEntry> entries = accessDB.getGlobalWhitelist(targetType);

            return Response.ok(entries).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to get global whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to retrieve global whitelist: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Add a new global whitelist entry
     * POST /ipwhitelist/global
     */
    @POST
    @Path("/global")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGlobalWhitelist(IPWhitelistEntry entry, @Context SecurityContext securityContext) {
        try {
            // Validate IP pattern
            if (!IPWhitelistMatcher.isValidPattern(entry.getIpPattern())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid IP pattern: " + entry.getIpPattern()))
                        .build();
            }

            // Set created_by from authenticated user
            String username = securityContext.getUserPrincipal().getName();
            entry.setCreatedBy(username);

            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            accessDB.addGlobalWhitelist(entry);

            // Refresh cache
            IPWhitelistService.getInstance(processing.getDBDriverManager()).refreshCache();

            return Response.status(Response.Status.CREATED).entity(entry).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to add global whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to add global whitelist entry: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Update a global whitelist entry
     * PUT /ipwhitelist/global/{id}
     */
    @PUT
    @Path("/global/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGlobalWhitelist(@PathParam("id") int id, IPWhitelistEntry entry) {
        try {
            // Validate IP pattern
            if (!IPWhitelistMatcher.isValidPattern(entry.getIpPattern())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid IP pattern: " + entry.getIpPattern()))
                        .build();
            }

            // Set ID from path parameter
            entry.setId(id);

            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            accessDB.updateGlobalWhitelist(entry);

            // Refresh cache
            IPWhitelistService.getInstance(processing.getDBDriverManager()).refreshCache();

            return Response.ok(entry).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to update global whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to update global whitelist entry: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete a global whitelist entry
     * DELETE /ipwhitelist/global/{id}
     */
    @DELETE
    @Path("/global/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGlobalWhitelist(@PathParam("id") int id) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            accessDB.deleteGlobalWhitelist(id);

            // Refresh cache
            IPWhitelistService.getInstance(processing.getDBDriverManager()).refreshCache();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("id", id);
            return Response.ok(result).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to delete global whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to delete global whitelist entry: " + e.getMessage()))
                    .build();
        }
    }

    // ========== Partner Whitelist Endpoints ==========

    /**
     * Get whitelist entries for a specific partner
     * GET /ipwhitelist/partner/{partnerId}
     */
    @GET
    @Path("/partner/{partnerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartnerWhitelist(@PathParam("partnerId") int partnerId) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            List<IPWhitelistEntry> entries = accessDB.getPartnerWhitelist(partnerId);

            return Response.ok(entries).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to get partner whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to retrieve partner whitelist: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Add a partner-specific whitelist entry
     * POST /ipwhitelist/partner/{partnerId}
     */
    @POST
    @Path("/partner/{partnerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPartnerWhitelist(@PathParam("partnerId") int partnerId, IPWhitelistEntry entry) {
        try {
            // Validate IP pattern
            if (!IPWhitelistMatcher.isValidPattern(entry.getIpPattern())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid IP pattern: " + entry.getIpPattern()))
                        .build();
            }

            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            accessDB.addPartnerWhitelist(partnerId, entry);

            // Refresh cache
            IPWhitelistService.getInstance(processing.getDBDriverManager()).refreshCache();

            return Response.status(Response.Status.CREATED).entity(entry).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to add partner whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to add partner whitelist entry: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete a partner whitelist entry
     * DELETE /ipwhitelist/partner/{partnerId}/entry/{entryId}
     */
    @DELETE
    @Path("/partner/{partnerId}/entry/{entryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePartnerWhitelist(@PathParam("partnerId") int partnerId,
                                          @PathParam("entryId") int entryId) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            accessDB.deletePartnerWhitelist(entryId);

            // Refresh cache
            IPWhitelistService.getInstance(processing.getDBDriverManager()).refreshCache();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("id", entryId);
            return Response.ok(result).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to delete partner whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to delete partner whitelist entry: " + e.getMessage()))
                    .build();
        }
    }

    // ========== User Whitelist Endpoints ==========

    /**
     * Get whitelist entries for a specific user
     * GET /ipwhitelist/user/{userId}
     */
    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserWhitelist(@PathParam("userId") int userId) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            List<IPWhitelistEntry> entries = accessDB.getUserWhitelist(userId);

            return Response.ok(entries).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to get user whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to retrieve user whitelist: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Add a user-specific whitelist entry
     * POST /ipwhitelist/user/{userId}
     */
    @POST
    @Path("/user/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserWhitelist(@PathParam("userId") int userId, IPWhitelistEntry entry) {
        try {
            // Validate IP pattern
            if (!IPWhitelistMatcher.isValidPattern(entry.getIpPattern())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid IP pattern: " + entry.getIpPattern()))
                        .build();
            }

            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            accessDB.addUserWhitelist(userId, entry);

            // Refresh cache
            IPWhitelistService.getInstance(processing.getDBDriverManager()).refreshCache();

            return Response.status(Response.Status.CREATED).entity(entry).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to add user whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to add user whitelist entry: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete a user whitelist entry
     * DELETE /ipwhitelist/user/{userId}/entry/{entryId}
     */
    @DELETE
    @Path("/user/{userId}/entry/{entryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserWhitelist(@PathParam("userId") int userId,
                                       @PathParam("entryId") int entryId) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            accessDB.deleteUserWhitelist(entryId);

            // Refresh cache
            IPWhitelistService.getInstance(processing.getDBDriverManager()).refreshCache();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("id", entryId);
            return Response.ok(result).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to delete user whitelist: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to delete user whitelist entry: " + e.getMessage()))
                    .build();
        }
    }

    // ========== Block Log Endpoints ==========

    /**
     * Get block log entries with optional filtering
     * GET /ipwhitelist/blocklog?targetType=AS2&days=7
     */
    @GET
    @Path("/blocklog")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlockLog(
            @QueryParam("targetType") String targetType,
            @QueryParam("days") @DefaultValue("7") int days) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Calculate date range
            Date endDate = new Date();
            Date startDate = new Date(endDate.getTime() - (days * 24L * 60L * 60L * 1000L));

            IPWhitelistAccessDB accessDB = new IPWhitelistAccessDB(processing.getDBDriverManager());
            List<IPWhitelistBlockLog> logs = accessDB.getBlockLog(startDate, endDate, targetType);

            return Response.ok(logs).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to get block log: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to retrieve block log: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Validate an IP pattern
     * POST /ipwhitelist/validate
     */
    @POST
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validatePattern(Map<String, String> request) {
        try {
            String pattern = request.get("pattern");
            if (pattern == null || pattern.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Pattern is required"))
                        .build();
            }

            boolean isValid = IPWhitelistMatcher.isValidPattern(pattern);
            String description = isValid ? IPWhitelistMatcher.getPatternDescription(pattern) : "Invalid pattern";

            Map<String, Object> result = new HashMap<>();
            result.put("valid", isValid);
            result.put("description", description);
            result.put("pattern", pattern);

            return Response.ok(result).build();

        } catch (Exception e) {
            LOGGER.severe("Failed to validate pattern: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to validate pattern: " + e.getMessage()))
                    .build();
        }
    }

    // ========== DTOs ==========

    /**
     * DTO for IP whitelist settings
     */
    public static class WhitelistSettingsDTO {
        private boolean enabledAS2;
        private boolean enabledTracker;
        private boolean enabledWebUI;
        private boolean enabledAPI;
        private String mode;
        private int logRetentionDays;

        public boolean isEnabledAS2() {
            return enabledAS2;
        }

        public void setEnabledAS2(boolean enabledAS2) {
            this.enabledAS2 = enabledAS2;
        }

        public boolean isEnabledTracker() {
            return enabledTracker;
        }

        public void setEnabledTracker(boolean enabledTracker) {
            this.enabledTracker = enabledTracker;
        }

        public boolean isEnabledWebUI() {
            return enabledWebUI;
        }

        public void setEnabledWebUI(boolean enabledWebUI) {
            this.enabledWebUI = enabledWebUI;
        }

        public boolean isEnabledAPI() {
            return enabledAPI;
        }

        public void setEnabledAPI(boolean enabledAPI) {
            this.enabledAPI = enabledAPI;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public int getLogRetentionDays() {
            return logRetentionDays;
        }

        public void setLogRetentionDays(int logRetentionDays) {
            this.logRetentionDays = logRetentionDays;
        }
    }

    // ========== Error Response DTO ==========

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
