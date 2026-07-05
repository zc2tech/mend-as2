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
import de.mendelson.comm.as2.api.response.UserApiResponseRule;
import de.mendelson.comm.as2.api.response.UserApiResponseRuleDB;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.database.IDBDriverManager;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.sql.Connection;
import java.util.List;

/**
 * REST API for user-specific API response rules configuration.
 * All endpoints require authentication and operate on the current user's data.
 */
@Path("/user/api-response")
public class UserApiResponseResource {

    @Context
    private SecurityContext securityContext;

    /**
     * Get all response rules for current user.
     * GET /user/api-response/rules
     */
    @GET
    @Path("/rules")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRules() {
        try {
            String username = securityContext.getUserPrincipal().getName();
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            int userId = currentUser.getId();

            Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            UserApiResponseRuleDB ruleDB = new UserApiResponseRuleDB();

            List<UserApiResponseRule> rules = ruleDB.loadRules(userId, configConnection);
            configConnection.close();

            return Response.ok(rules).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Create a new response rule.
     * POST /user/api-response/rules
     */
    @POST
    @Path("/rules")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRule(UserApiResponseRule rule) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            int userId = currentUser.getId();
            rule.setUserId(userId);

            Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnection.setAutoCommit(false);

            try {
                UserApiResponseRuleDB ruleDB = new UserApiResponseRuleDB();
                int newId = ruleDB.insertRule(rule, configConnection);
                configConnection.commit();

                rule.setId(newId);
                return Response.status(Response.Status.CREATED).entity(rule).build();

            } catch (Exception e) {
                configConnection.rollback();
                throw e;
            } finally {
                configConnection.close();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Update an existing response rule.
     * PUT /user/api-response/rules/{id}
     */
    @PUT
    @Path("/rules/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRule(@PathParam("id") int ruleId, UserApiResponseRule rule) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            rule.setId(ruleId);
            rule.setUserId(currentUser.getId());

            Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnection.setAutoCommit(false);

            try {
                UserApiResponseRuleDB ruleDB = new UserApiResponseRuleDB();
                ruleDB.updateRule(rule, configConnection);
                configConnection.commit();

                return Response.ok(rule).build();

            } catch (Exception e) {
                configConnection.rollback();
                throw e;
            } finally {
                configConnection.close();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Delete a response rule.
     * DELETE /user/api-response/rules/{id}
     */
    @DELETE
    @Path("/rules/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRule(@PathParam("id") int ruleId) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnection.setAutoCommit(false);

            try {
                UserApiResponseRuleDB ruleDB = new UserApiResponseRuleDB();
                ruleDB.deleteRule(ruleId, configConnection);
                configConnection.commit();

                return Response.ok("{\"success\":true}").build();

            } catch (Exception e) {
                configConnection.rollback();
                throw e;
            } finally {
                configConnection.close();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Update priorities for multiple rules (reorder).
     * PUT /user/api-response/rules/reorder
     */
    @PUT
    @Path("/rules/reorder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reorderRules(ReorderRequest request) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            AS2Server server = AS2Server.getStaticServerReference();
            if (server == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            IDBDriverManager dbDriverManager = server.getServerProcessing().getDBDriverManager();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(dbDriverManager, null);

            WebUIUser currentUser = userMgmt.getUserByUsername(username);
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnection.setAutoCommit(false);

            try {
                UserApiResponseRuleDB ruleDB = new UserApiResponseRuleDB();
                ruleDB.updatePriorities(request.getRules(), configConnection);
                configConnection.commit();

                return Response.ok("{\"success\":true}").build();

            } catch (Exception e) {
                configConnection.rollback();
                throw e;
            } finally {
                configConnection.close();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Request payload for reorder operation
     */
    public static class ReorderRequest {
        @JsonProperty("rules")
        private List<UserApiResponseRule> rules;

        public List<UserApiResponseRule> getRules() {
            return rules;
        }

        public void setRules(List<UserApiResponseRule> rules) {
            this.rules = rules;
        }
    }
}
