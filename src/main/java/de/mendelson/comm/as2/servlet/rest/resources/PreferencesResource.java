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

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.util.clientserver.clients.preferences.PreferencesRequest;
import de.mendelson.util.clientserver.clients.preferences.PreferencesResponse;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * REST API for server preferences and configuration
 * Handles getting and setting server configuration values
 *
 */
@Path("/preferences")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PreferencesResource {

    /**
     * Get a preference value by key
     */
    @GET
    @Path("/{key}")
    public Response getPreference(@PathParam("key") String key) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        PreferencesRequest request = new PreferencesRequest();
        request.setType(PreferencesRequest.TYPE_GET);
        request.setKey(key);

        PreferencesResponse response = processing.processPreferencesGetRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        PreferenceDTO dto = new PreferenceDTO();
        dto.setKey(key);
        dto.setValue(response.getValue());

        return Response.ok(dto).build();
    }

    /**
     * Get the default value for a preference key
     */
    @GET
    @Path("/{key}/default")
    public Response getDefaultPreference(@PathParam("key") String key) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        PreferencesRequest request = new PreferencesRequest();
        request.setType(PreferencesRequest.TYPE_GET_DEFAULT);
        request.setKey(key);

        PreferencesResponse response = processing.processPreferencesGetDefaultRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        PreferenceDTO dto = new PreferenceDTO();
        dto.setKey(key);
        dto.setValue(response.getValue());

        return Response.ok(dto).build();
    }

    /**
     * Set a preference value
     */
    @PUT
    @Path("/{key}")
    public Response setPreference(
            @PathParam("key") String key,
            @Context SecurityContext securityContext,
            PreferenceDTO dto) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        PreferencesRequest request = new PreferencesRequest();
        request.setType(PreferencesRequest.TYPE_SET);
        request.setKey(key);
        request.setValue(dto.getValue());

        String userName = securityContext.getUserPrincipal() != null
                ? securityContext.getUserPrincipal().getName()
                : "api-user";
        String processOriginHost = "REST-API";

        processing.processPreferencesSetRequest(request, userName, processOriginHost);

        return Response.ok(new SuccessResponse("Preference updated successfully")).build();
    }

    /**
     * DTO for preference value
     */
    public static class PreferenceDTO {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * DTO for error responses
     */
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

    /**
     * DTO for success responses
     */
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
