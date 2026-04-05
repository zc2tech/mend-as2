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
 * GNU General Public License for details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.mendelson.comm.as2.servlet.rest.resources;

import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.comm.as2.usermanagement.UserHttpAuthPreference;
import de.mendelson.comm.as2.usermanagement.UserHttpAuthPreferenceAccessDB;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for user HTTP authentication preferences
 */
@Path("/user-preferences/http-auth")
public class UserHttpAuthPreferenceResource {

    private static final Logger logger = Logger.getLogger(UserHttpAuthPreferenceResource.class.getName());

    /**
     * Get all HTTP auth preferences for the current user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyPreferences(@jakarta.ws.rs.core.Context SecurityContext securityContext) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get current user
            String username = securityContext.getUserPrincipal().getName();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(processing.getDBDriverManager(), logger);
            WebUIUser user = userMgmt.getUserByUsername(username);

            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("User not found"))
                        .build();
            }

            // Get user's visible partners
            PartnerAccessDB partnerDB = new PartnerAccessDB(processing.getDBDriverManager());
            List<Partner> visiblePartners = partnerDB.getPartnersVisibleToUser(
                    user.getId(),
                    PartnerAccessDB.DATA_COMPLETENESS_NAMES_AS2ID_TYPE
            );

            // Get existing preferences
            UserHttpAuthPreferenceAccessDB prefDB = new UserHttpAuthPreferenceAccessDB(processing.getDBDriverManager(), logger);
            List<UserHttpAuthPreference> existingPrefs = prefDB.getPreferencesForUser(user.getId());

            // Create a list with all visible partners (fill in missing ones)
            List<UserHttpAuthPreference> allPrefs = new ArrayList<>();
            for (Partner partner : visiblePartners) {
                if (partner.isLocalStation()) {
                    continue; // Skip local stations
                }

                // Find existing preference or create new one
                UserHttpAuthPreference pref = existingPrefs.stream()
                        .filter(p -> p.getPartnerId() == partner.getDBId())
                        .findFirst()
                        .orElseGet(() -> {
                            UserHttpAuthPreference newPref = new UserHttpAuthPreference();
                            newPref.setUserId(user.getId());
                            newPref.setPartnerId(partner.getDBId());
                            newPref.setPartnerName(partner.getName());
                            newPref.setPartnerAs2Id(partner.getAS2Identification());
                            return newPref;
                        });

                allPrefs.add(pref);
            }

            return Response.ok(allPrefs).build();
        } catch (Exception e) {
            logger.severe("Error loading HTTP auth preferences: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Save HTTP auth preference
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response savePreference(
            UserHttpAuthPreference preference,
            @jakarta.ws.rs.core.Context SecurityContext securityContext) {

        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get current user
            String username = securityContext.getUserPrincipal().getName();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(processing.getDBDriverManager(), logger);
            WebUIUser user = userMgmt.getUserByUsername(username);

            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("User not found"))
                        .build();
            }

            // Ensure preference is for the current user
            preference.setUserId(user.getId());

            // Save preference
            UserHttpAuthPreferenceAccessDB prefDB = new UserHttpAuthPreferenceAccessDB(processing.getDBDriverManager(), logger);
            prefDB.savePreference(preference);

            return Response.ok(new SuccessResponse("Preference saved successfully")).build();
        } catch (Exception e) {
            logger.severe("Error saving HTTP auth preference: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete HTTP auth preference
     */
    @DELETE
    @Path("/{partnerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePreference(
            @PathParam("partnerId") int partnerId,
            @jakarta.ws.rs.core.Context SecurityContext securityContext) {

        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get current user
            String username = securityContext.getUserPrincipal().getName();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(processing.getDBDriverManager(), logger);
            WebUIUser user = userMgmt.getUserByUsername(username);

            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("User not found"))
                        .build();
            }

            // Delete preference
            UserHttpAuthPreferenceAccessDB prefDB = new UserHttpAuthPreferenceAccessDB(processing.getDBDriverManager(), logger);
            prefDB.deletePreference(user.getId(), partnerId);

            return Response.ok(new SuccessResponse("Preference deleted successfully")).build();
        } catch (Exception e) {
            logger.severe("Error deleting HTTP auth preference: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    // DTOs
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

    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
