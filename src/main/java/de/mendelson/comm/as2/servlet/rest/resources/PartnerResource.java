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

import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.comm.as2.partner.clientserver.SinglePartnerAddRequest;
import de.mendelson.comm.as2.partner.clientserver.SinglePartnerAddResponse;
import de.mendelson.comm.as2.partner.clientserver.SinglePartnerDeleteRequest;
import de.mendelson.comm.as2.partner.clientserver.SinglePartnerDeleteResponse;
import de.mendelson.comm.as2.partner.clientserver.SinglePartnerModificationRequest;
import de.mendelson.comm.as2.partner.clientserver.SinglePartnerModificationResponse;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * REST resource for partner management operations
 * Provides CRUD operations for AS2 partners
 *
 */
@Path("/partners")
public class PartnerResource {

    private static final Logger logger = Logger.getLogger(PartnerResource.class.getName());

    /**
     * List all partners or filter by type
     * Query parameters:
     *   type: all|local|non-local|cem-supporting (default: all)
     *   completeness: full|names (default: full)
     *   visibleToUser: User ID to filter partners visible to specific user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listPartners(
            @QueryParam("type") @DefaultValue("all") String type,
            @QueryParam("completeness") @DefaultValue("full") String completeness,
            @QueryParam("visibleToUser") Integer visibleToUserId) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            List<Partner> partners;

            // If userId specified, filter by visibility
            if (visibleToUserId != null && visibleToUserId > 0) {
                int dataCompleteness = "names".equals(completeness)
                        ? PartnerAccessDB.DATA_COMPLETENESS_NAMES_AS2ID_TYPE
                        : PartnerAccessDB.DATA_COMPLETENESS_FULL;

                PartnerAccessDB partnerDB = new PartnerAccessDB(processing.getDBDriverManager());
                partners = partnerDB.getPartnersVisibleToUser(visibleToUserId, dataCompleteness);
            } else {
                // Use existing logic
                int listOption = PartnerListRequest.LIST_ALL;
                if ("local".equals(type)) {
                    listOption = PartnerListRequest.LIST_LOCALSTATION;
                } else if ("non-local".equals(type)) {
                    listOption = PartnerListRequest.LIST_NON_LOCALSTATIONS;
                } else if ("cem-supporting".equals(type)) {
                    listOption = PartnerListRequest.LIST_NON_LOCALSTATIONS_SUPPORTING_CEM;
                }

                int dataCompleteness = "names".equals(completeness)
                        ? PartnerListRequest.DATA_COMPLETENESS_NAME_AS2ID_TYPE
                        : PartnerListRequest.DATA_COMPLETENESS_FULL;

                PartnerListRequest request = new PartnerListRequest(listOption, dataCompleteness);
                PartnerListResponse response = processing.processPartnerListRequest(request);

                if (response.getException() != null) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ErrorResponse(response.getException().getMessage()))
                            .build();
                }

                partners = response.getList();
            }

            return Response.ok(partners).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Get partner by AS2 ID
     */
    @GET
    @Path("/{as2id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartner(@PathParam("as2id") String as2id) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            PartnerListRequest request = new PartnerListRequest(PartnerListRequest.LIST_BY_AS2_ID, PartnerListRequest.DATA_COMPLETENESS_FULL);
            request.setAdditionalListOptionStr(as2id);
            PartnerListResponse response = processing.processPartnerListRequest(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            List<Partner> partners = response.getList();
            if (partners.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Partner not found: " + as2id))
                        .build();
            }

            return Response.ok(partners.get(0)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Create a new partner
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPartner(Partner partner, @jakarta.ws.rs.core.Context jakarta.ws.rs.core.SecurityContext securityContext) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Set the creator user ID from the security context
            String username = securityContext.getUserPrincipal().getName();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(processing.getDBDriverManager(), logger);
            WebUIUser user = userMgmt.getUserByUsername(username);
            if (user != null) {
                partner.setCreatedByUserId(user.getId());
            }

            SinglePartnerAddRequest request = new SinglePartnerAddRequest(partner);
            SinglePartnerAddResponse response = processing.processPartnerAddRequest(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse("Partner created successfully"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Update an existing partner
     */
    @PUT
    @Path("/{as2id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePartner(@PathParam("as2id") String as2id, Partner partner) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Ensure the AS2 ID in the path matches the partner object
            partner.setAS2Identification(as2id);

            SinglePartnerModificationRequest request = new SinglePartnerModificationRequest(partner);
            SinglePartnerModificationResponse response = processing.processPartnerModificationRequest(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            return Response.ok(new SuccessResponse("Partner updated successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete a partner by AS2 ID
     */
    @DELETE
    @Path("/{as2id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePartner(@PathParam("as2id") String as2id) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            SinglePartnerDeleteRequest request = new SinglePartnerDeleteRequest(as2id);
            SinglePartnerDeleteResponse response = processing.processPartnerDeleteRequest(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            return Response.ok(new SuccessResponse("Partner deleted successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Get visibility settings for a partner
     * GET /partners/{id}/visibility
     */
    @GET
    @Path("/{id}/visibility")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartnerVisibility(@PathParam("id") int partnerId) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            PartnerAccessDB partnerDB = new PartnerAccessDB(processing.getDBDriverManager());
            Partner partner = partnerDB.getPartner(partnerId);

            if (partner == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Partner not found"))
                        .build();
            }

            Map<String, Object> visibility = new HashMap<>();
            visibility.put("partnerId", partnerId);
            visibility.put("localStation", partner.isLocalStation());
            visibility.put("visibleToAll", partner.isVisibleToAllUsers());
            visibility.put("visibleToUserIds", partner.getVisibleToUserIds());

            return Response.ok(visibility).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Update visibility settings for a partner
     * PUT /partners/{id}/visibility
     * Body: { "visibleToAll": true|false, "userIds": [1,2,3] }
     */
    @PUT
    @Path("/{id}/visibility")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePartnerVisibility(
            @PathParam("id") int partnerId,
            Map<String, Object> visibilityData) {

        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            PartnerAccessDB partnerDB = new PartnerAccessDB(processing.getDBDriverManager());
            Partner partner = partnerDB.getPartner(partnerId);

            if (partner == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Partner not found"))
                        .build();
            }

            if (partner.isLocalStation()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Cannot set visibility for local stations"))
                        .build();
            }

            Boolean visibleToAll = (Boolean) visibilityData.get("visibleToAll");
            List<Integer> userIds = new java.util.ArrayList<>();

            if (Boolean.FALSE.equals(visibleToAll)) {
                // Extract user IDs
                Object userIdsObj = visibilityData.get("userIds");
                if (userIdsObj instanceof List) {
                    for (Object id : (List) userIdsObj) {
                        if (id instanceof Number) {
                            userIds.add(((Number) id).intValue());
                        }
                    }
                }
            }

            // Update visibility
            partner.setVisibleToUserIds(userIds);
            partnerDB.updatePartner(partner);

            return Response.ok(new SuccessResponse("Visibility updated successfully")).build();
        } catch (Exception e) {
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
