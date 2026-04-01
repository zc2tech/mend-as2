package de.mendelson.comm.as2.servlet.rest.resources;

import de.mendelson.comm.as2.cem.CEMEntry;
import de.mendelson.comm.as2.cem.clientserver.*;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.util.security.cert.KeystoreCertificate;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 * REST API for CEM (Certificate Exchange Mechanism) operations
 * Handles CEM requests, responses, and status management
 *
 * @author S.Heller
 */
@Path("/cem")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CemResource {

    /**
     * Get list of all CEM entries
     */
    @GET
    public Response listCemEntries() {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        CEMListRequest request = new CEMListRequest();
        CEMListResponse response = processing.processCEMListRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        return Response.ok(response.getList()).build();
    }

    /**
     * Send a CEM request to exchange certificates with a partner
     * Request body: CEMSendRequestDTO
     */
    @POST
    @Path("/send")
    public Response sendCem(CEMSendRequestDTO dto) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        // Create CEM send request
        CEMSendRequest request = new CEMSendRequest();
        request.setInitiator(dto.getInitiator());
        request.setReceiver(dto.getReceiver());
        request.setCertificate(dto.getCertificate());
        request.setActivationDate(dto.getActivationDate());
        request.setPurposeEncryption(dto.isPurposeEncryption());
        request.setPurposeSignature(dto.isPurposeSignature());
        request.setPurposeSSL(dto.isPurposeSSL());

        CEMSendResponse response = processing.processCEMSendRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        CEMSendResponseDTO responseDto = new CEMSendResponseDTO();
        responseDto.setInformedPartners(response.getInformedPartner());

        return Response.ok(responseDto).build();
    }

    /**
     * Cancel a pending CEM request
     * Request body: CEMCancelRequestDTO
     */
    @POST
    @Path("/cancel")
    public Response cancelCem(CEMCancelRequestDTO dto) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        CEMCancelRequest request = new CEMCancelRequest(dto.getEntry());

        processing.processCEMCancelRequest(request);

        return Response.ok(new SuccessResponse("CEM request canceled successfully")).build();
    }

    /**
     * Delete a CEM entry
     * Request body: CEMDeleteRequestDTO
     */
    @POST
    @Path("/delete")
    public Response deleteCem(CEMDeleteRequestDTO dto) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        CEMDeleteRequest request = new CEMDeleteRequest(dto.getEntry());

        processing.processCEMDeleteRequest(request);

        return Response.ok(new SuccessResponse("CEM entry deleted successfully")).build();
    }

    /**
     * DTO for CEM send request
     */
    public static class CEMSendRequestDTO {
        private Partner initiator;
        private List<Partner> receiver;
        private KeystoreCertificate certificate;
        private Date activationDate;
        private boolean purposeEncryption;
        private boolean purposeSignature;
        private boolean purposeSSL;

        public Partner getInitiator() {
            return initiator;
        }

        public void setInitiator(Partner initiator) {
            this.initiator = initiator;
        }

        public List<Partner> getReceiver() {
            return receiver;
        }

        public void setReceiver(List<Partner> receiver) {
            this.receiver = receiver;
        }

        public KeystoreCertificate getCertificate() {
            return certificate;
        }

        public void setCertificate(KeystoreCertificate certificate) {
            this.certificate = certificate;
        }

        public Date getActivationDate() {
            return activationDate;
        }

        public void setActivationDate(Date activationDate) {
            this.activationDate = activationDate;
        }

        public boolean isPurposeEncryption() {
            return purposeEncryption;
        }

        public void setPurposeEncryption(boolean purposeEncryption) {
            this.purposeEncryption = purposeEncryption;
        }

        public boolean isPurposeSignature() {
            return purposeSignature;
        }

        public void setPurposeSignature(boolean purposeSignature) {
            this.purposeSignature = purposeSignature;
        }

        public boolean isPurposeSSL() {
            return purposeSSL;
        }

        public void setPurposeSSL(boolean purposeSSL) {
            this.purposeSSL = purposeSSL;
        }
    }

    /**
     * DTO for CEM send response
     */
    public static class CEMSendResponseDTO {
        private List<Partner> informedPartners;

        public List<Partner> getInformedPartners() {
            return informedPartners;
        }

        public void setInformedPartners(List<Partner> informedPartners) {
            this.informedPartners = informedPartners;
        }
    }

    /**
     * DTO for CEM cancel request
     */
    public static class CEMCancelRequestDTO {
        private CEMEntry entry;

        public CEMEntry getEntry() {
            return entry;
        }

        public void setEntry(CEMEntry entry) {
            this.entry = entry;
        }
    }

    /**
     * DTO for CEM delete request
     */
    public static class CEMDeleteRequestDTO {
        private CEMEntry entry;

        public CEMEntry getEntry() {
            return entry;
        }

        public void setEntry(CEMEntry entry) {
            this.entry = entry;
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
