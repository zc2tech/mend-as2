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
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * REST resource for partner management operations
 * Provides CRUD operations for AS2 partners
 *
 * @author S.Heller
 */
@Path("/partners")
public class PartnerResource {

    /**
     * List all partners or filter by type
     * Query parameters:
     *   type: all|local|non-local|cem-supporting (default: all)
     *   completeness: full|names (default: full)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listPartners(
            @QueryParam("type") @DefaultValue("all") String type,
            @QueryParam("completeness") @DefaultValue("full") String completeness) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

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

            return Response.ok(response.getList()).build();
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
    public Response createPartner(Partner partner) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
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
