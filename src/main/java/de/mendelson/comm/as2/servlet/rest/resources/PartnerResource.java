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
import java.util.ArrayList;
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
                partners = partnerDB.getPartnersOwnedByUser(visibleToUserId, dataCompleteness);
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
                // Set userId=-1 to return ALL partners (no user filtering)
                request.setUserId(-1);
                PartnerListResponse response = processing.processPartnerListRequest(request);

                if (response.getException() != null) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ErrorResponse(response.getException().getMessage()))
                            .build();
                }

                partners = response.getList();
            }

            // Convert to DTOs and populate creator usernames
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(processing.getDBDriverManager(), logger);
            List<PartnerDTO> partnerDTOs = new ArrayList<>();
            for (Partner partner : partners) {
                PartnerDTO dto = new PartnerDTO(partner);

                // Look up and set creator username
                int creatorUserId = partner.getCreatedByUserId();
                try {
                    if (creatorUserId > 0) {
                        WebUIUser creator = userMgmt.getUser(creatorUserId);
                        if (creator != null) {
                            dto.setCreatedByUsername(creator.getUsername());
                        }
                    }
                } catch (Exception e) {
                    // If user lookup fails, leave username as null (will fall back to "User X" in UI)
                    logger.warning("Failed to lookup user " + creatorUserId + ": " + e.getMessage());
                }

                partnerDTOs.add(dto);
            }

            return Response.ok(partnerDTOs).build();
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
    public Response createPartner(PartnerDTO partnerDTO, @jakarta.ws.rs.core.Context jakarta.ws.rs.core.SecurityContext securityContext) {
        try {
            logger.info("Received createPartner request");

            // Validate DTO
            if (partnerDTO == null) {
                logger.warning("PartnerDTO is null");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Partner data is required"))
                        .build();
            }

            logger.info("PartnerDTO: name=" + partnerDTO.getName() +
                       ", as2Id=" + partnerDTO.getAs2Identification() +
                       ", isLocal=" + partnerDTO.isLocalStation());

            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Convert DTO to Partner object with proper defaults
            Partner partner = partnerDTO.toPartner();

            // Log the received partner data
            logger.info("Creating partner: name=" + partner.getName() + ", as2Id=" + partner.getAS2Identification() + ", isLocal=" + partner.isLocalStation());

            // Set the creator user ID from the security context
            String username = securityContext.getUserPrincipal().getName();
            UserManagementAccessDB userMgmt = new UserManagementAccessDB(processing.getDBDriverManager(), logger);
            WebUIUser user = userMgmt.getUserByUsername(username);
            if (user != null) {
                // Use the actual user ID (admin user has ID 1)
                partner.setCreatedByUserId(user.getId());
                logger.info("Setting created_by_user_id=" + user.getId() + " for username=" + username);
            }

            SinglePartnerAddRequest request = new SinglePartnerAddRequest(partner);
            SinglePartnerAddResponse response = processing.processPartnerAddRequest(request);

            if (response.getException() != null) {
                logger.warning("Partner creation failed: " + response.getException().getMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse("Partner created successfully"))
                    .build();
        } catch (Exception e) {
            logger.severe("Partner creation error: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage() != null ? e.getMessage() : e.getClass().getName()))
                    .build();
        }
    }

    /**
     * Update an existing partner by database ID
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePartner(@PathParam("id") int dbId, PartnerDTO partnerDTO) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get existing partner by database ID to preserve internal state
            PartnerAccessDB partnerAccess = new PartnerAccessDB(processing.getDBDriverManager());
            Partner existingPartner = partnerAccess.getPartner(dbId);

            if (existingPartner == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Partner with ID " + dbId + " not found"))
                        .build();
            }

            // Note: AS2 ID duplication is allowed because users have separate endpoints /as2/HttpReceiver/{username}

            // Update existing partner with values from DTO (preserves fields not in DTO)
            updatePartnerFromDTO(existingPartner, partnerDTO);

            SinglePartnerModificationRequest request = new SinglePartnerModificationRequest(existingPartner);
            SinglePartnerModificationResponse response = processing.processPartnerModificationRequest(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            return Response.ok(new SuccessResponse("Partner updated successfully")).build();
        } catch (Exception e) {
            logger.severe("Partner update error: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
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
     * Update an existing Partner object with values from PartnerDTO
     * This preserves fields not included in the DTO
     */
    private void updatePartnerFromDTO(Partner partner, PartnerDTO dto) {
        // General tab
        if (dto.getName() != null) {
            partner.setName(dto.getName());
        }
        if (dto.getAs2Identification() != null) {
            partner.setAS2Identification(dto.getAs2Identification());
        }
        partner.setLocalStation(dto.isLocalStation());
        partner.setComment(dto.getComment());

        // Send tab
        if (dto.getUrl() != null && !dto.getUrl().isEmpty()) {
            partner.setURL(dto.getUrl());
        }
        if (dto.getSubject() != null) {
            partner.setSubject(dto.getSubject());
        }
        if (dto.getContentType() != null) {
            partner.setContentType(dto.getContentType());
        }
        if (dto.getEmail() != null) {
            partner.setEmail(dto.getEmail());
        }
        partner.setEncryptionType(dto.getEncryptionType());
        partner.setSignType(dto.getSignType());
        partner.setCompressionType(dto.getCompressionType());

        // Receive/MDN tab
        if (dto.getMdnURL() != null && !dto.getMdnURL().isEmpty()) {
            partner.setMdnURL(dto.getMdnURL());
        }
        partner.setSyncMDN(dto.isSyncMDN());
        partner.setSignedMDN(dto.isSignedMDN());

        // Security tab
        if (dto.getSignFingerprintSHA1() != null) {
            partner.setSignFingerprintSHA1(dto.getSignFingerprintSHA1());
        }
        if (dto.getCryptFingerprintSHA1() != null) {
            partner.setCryptFingerprintSHA1(dto.getCryptFingerprintSHA1());
        }
        partner.setOverwriteLocalStationSecurity(dto.isOverwriteLocalStationSecurity());
        if (dto.getSignOverwriteLocalstationFingerprintSHA1() != null) {
            partner.setSignOverwriteLocalstationFingerprintSHA1(dto.getSignOverwriteLocalstationFingerprintSHA1());
        }
        if (dto.getCryptOverwriteLocalstationFingerprintSHA1() != null) {
            partner.setCryptOverwriteLocalstationFingerprintSHA1(dto.getCryptOverwriteLocalstationFingerprintSHA1());
        }
        partner.setUseAlgorithmIdentifierProtectionAttribute(dto.isUseAlgorithmIdentifierProtectionAttribute());

        // Directory Poll tab
        partner.setEnableDirPoll(dto.isEnableDirPoll());
        partner.setPollInterval(dto.getPollInterval());
        partner.setMaxPollFiles(dto.getMaxPollFiles());
        if (dto.getPollIgnoreListAsString() != null) {
            partner.setPollIgnoreListString(dto.getPollIgnoreListAsString());
        }
        partner.setKeepOriginalFilenameOnReceipt(dto.isKeepFilenameOnReceipt());

        // HTTP tab
        if (dto.getHttpProtocolVersion() != null) {
            partner.setHttpProtocolVersion(dto.getHttpProtocolVersion());
        }
        partner.setContentTransferEncoding(dto.getContentTransferEncoding());

        // HTTP Authentication tab
        if (dto.getAuthenticationCredentialsMessage() != null) {
            partner.setAuthentication(dto.getAuthenticationCredentialsMessage());
        }
        if (dto.getAuthenticationCredentialsAsyncMDN() != null) {
            partner.setAuthenticationAsyncMDN(dto.getAuthenticationCredentialsAsyncMDN());
        }

        // Inbound Authentication (for local stations only)
        if (partner.isLocalStation() && dto.getInboundAuthCredentialsList() != null) {
            partner.setInboundAuthCredentialsList(dto.getInboundAuthCredentialsList());
        }

        // Inbound Auth enable flags (for local stations only)
        if (partner.isLocalStation()) {
            partner.setInboundAuthBasicEnabled(dto.isInboundAuthBasicEnabled());
            partner.setInboundAuthCertEnabled(dto.isInboundAuthCertEnabled());
        }

        // Contact tab
        if (dto.getContactAS2() != null) {
            partner.setContactAS2(dto.getContactAS2());
        }
        if (dto.getContactCompany() != null) {
            partner.setContactCompany(dto.getContactCompany());
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
