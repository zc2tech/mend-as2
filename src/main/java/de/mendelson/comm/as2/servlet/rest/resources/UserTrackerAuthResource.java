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

import com.fasterxml.jackson.annotation.JsonProperty;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.tracker.auth.UserTrackerAuthCredential;
import de.mendelson.comm.as2.tracker.auth.UserTrackerAuthDB;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.database.IDBDriverManager;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * REST API for user-specific tracker authentication configuration.
 * All endpoints require authentication and operate on the current user's data.
 */
@Path("/user/tracker-auth")
public class UserTrackerAuthResource {

    @Context
    private SecurityContext securityContext;

    /**
     * Get current user's tracker auth configuration (toggles + all credentials).
     * GET /user/tracker-auth/config
     */
    @GET
    @Path("/config")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig() {
        try {
            // Get current username from security context
            String username = securityContext.getUserPrincipal().getName();

            // Get database connection
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            // Get user ID
            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }
            int userId = currentUser.getId();

            // Load tracker auth configuration
            Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            UserTrackerAuthDB authDB = new UserTrackerAuthDB();

            // Load master toggles
            boolean[] toggles = authDB.loadMasterToggles(userId, configConnection);

            // Load all credentials
            List<UserTrackerAuthCredential> credentials = authDB.loadCredentials(userId, configConnection);

            configConnection.close();

            // Build response object
            UserTrackerAuthConfig config = new UserTrackerAuthConfig();
            config.setUserId(userId);
            config.setBasicAuthEnabled(toggles[0]);
            config.setCertAuthEnabled(toggles[1]);
            config.setCredentialsList(credentials);

            return Response.ok(config).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Save current user's tracker auth configuration.
     * POST /user/tracker-auth/config
     */
    @POST
    @Path("/config")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveConfig(UserTrackerAuthConfig config) {
        Connection configConnection = null;
        try {
            // Get current username from security context
            String username = securityContext.getUserPrincipal().getName();

            // Get database connection
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            // Get user ID
            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }
            int userId = currentUser.getId();

            // Get database connection without auto-commit
            configConnection = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnection.setAutoCommit(false);

            UserTrackerAuthDB authDB = new UserTrackerAuthDB();

            // Save credentials and toggles
            authDB.saveCredentials(
                    userId,
                    config.getCredentialsList(),
                    config.isBasicAuthEnabled(),
                    config.isCertAuthEnabled(),
                    configConnection
            );

            configConnection.commit();

            return Response.ok("{\"success\":true}").build();

        } catch (Exception e) {
            e.printStackTrace();
            if (configConnection != null) {
                try {
                    configConnection.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } finally {
            if (configConnection != null) {
                try {
                    configConnection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * DTO for tracker auth configuration (used in JSON serialization).
     */
    public static class UserTrackerAuthConfig {

        @JsonProperty("userId")
        private int userId;

        @JsonProperty("basicAuthEnabled")
        private boolean basicAuthEnabled;

        @JsonProperty("certAuthEnabled")
        private boolean certAuthEnabled;

        @JsonProperty("credentialsList")
        private List<UserTrackerAuthCredential> credentialsList = new ArrayList<>();

        // Getters and setters
        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public boolean isBasicAuthEnabled() {
            return basicAuthEnabled;
        }

        public void setBasicAuthEnabled(boolean basicAuthEnabled) {
            this.basicAuthEnabled = basicAuthEnabled;
        }

        public boolean isCertAuthEnabled() {
            return certAuthEnabled;
        }

        public void setCertAuthEnabled(boolean certAuthEnabled) {
            this.certAuthEnabled = certAuthEnabled;
        }

        public List<UserTrackerAuthCredential> getCredentialsList() {
            return credentialsList;
        }

        public void setCredentialsList(List<UserTrackerAuthCredential> credentialsList) {
            this.credentialsList = credentialsList;
        }
    }
}
