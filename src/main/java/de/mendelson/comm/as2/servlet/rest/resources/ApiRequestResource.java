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

import de.mendelson.comm.as2.api.ApiRequestAccessDB;
import de.mendelson.comm.as2.api.ApiRequestInfo;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.database.IDBDriverManager;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * REST API for user API request history.
 * All endpoints require authentication and operate on the current user's data.
 */
@Path("/user/api-requests")
public class ApiRequestResource {

    @Context
    private SecurityContext securityContext;

    /**
     * Get current user's API requests with filters.
     * GET /user/api-requests?startDate=...&endDate=...&method=...&path=...
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRequests(
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr,
            @QueryParam("method") String methodFilter,
            @QueryParam("path") String pathFilter) {
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

            // Parse date range (default to last 7 days)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate;
            Date startDate;

            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = dateFormat.parse(endDateStr);
                // Set to end of day
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                endDate = cal.getTime();
            } else {
                endDate = new Date(); // Now
            }

            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = dateFormat.parse(startDateStr);
                // Set to start of day
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startDate = cal.getTime();
            } else {
                // Default: 7 days ago
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -7);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startDate = cal.getTime();
            }

            // Load API requests
            ApiRequestAccessDB apiRequestDAO = new ApiRequestAccessDB(dbDriverManager);
            List<ApiRequestInfo> requests = apiRequestDAO.getApiRequests(
                    userId, startDate, endDate, methodFilter, pathFilter);

            return Response.ok(requests).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Get single API request by ID.
     * GET /user/api-requests/{requestId}
     */
    @GET
    @Path("/{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRequestById(@PathParam("requestId") String requestId) {
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

            // Load API request
            ApiRequestAccessDB apiRequestDAO = new ApiRequestAccessDB(dbDriverManager);
            ApiRequestInfo request = apiRequestDAO.getApiRequestById(requestId);

            if (request == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Request not found\"}").build();
            }

            // Verify ownership (security check)
            if (request.getUserId() != userId) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\":\"Access denied\"}").build();
            }

            return Response.ok(request).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Download API request body.
     * GET /user/api-requests/{requestId}/body
     */
    @GET
    @Path("/{requestId}/body")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadRequestBody(@PathParam("requestId") String requestId) {
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

            // Load API request
            ApiRequestAccessDB apiRequestDAO = new ApiRequestAccessDB(dbDriverManager);
            ApiRequestInfo request = apiRequestDAO.getApiRequestById(requestId);

            if (request == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Request not found\"}").build();
            }

            // Verify ownership
            if (request.getUserId() != userId) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\":\"Access denied\"}").build();
            }

            byte[] body = request.getRequestBody();
            if (body == null || body.length == 0) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(body)
                    .header("Content-Disposition", "attachment; filename=\"" + requestId + ".bin\"")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
