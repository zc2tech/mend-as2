package de.mendelson.comm.as2.servlet.rest.resources;

import de.mendelson.util.security.cert.clientserver.CRLVerificationRequest;
import de.mendelson.util.security.cert.clientserver.CRLVerificationResponse;
import de.mendelson.util.security.cert.clientserver.CSRGenerationRequest;
import de.mendelson.util.security.cert.clientserver.CSRGenerationResponse;
import de.mendelson.util.security.cert.clientserver.CertificateExportRequest;
import de.mendelson.util.security.cert.clientserver.CertificateExportResponse;
import de.mendelson.util.security.cert.clientserver.DownloadRequestKeystore;
import de.mendelson.util.security.cert.clientserver.DownloadResponseKeystore;
import de.mendelson.util.security.cert.clientserver.UploadRequestKeystore;
import de.mendelson.util.security.cert.clientserver.UploadResponseKeystore;
import de.mendelson.util.security.crl.CRLRevocationInformation;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

/**
 * REST resource for certificate management operations
 * Provides certificate listing, import, export, CSR generation, and CRL verification
 *
 * @author S.Heller
 */
@Path("/certificates")
public class CertificateResource {

    /**
     * List certificates by keystore type
     * Query parameters:
     *   keystoreType: sign|tls (default: sign)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCertificates(
            @QueryParam("keystoreType") @DefaultValue("sign") String keystoreType) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            int keystoreUsage = "tls".equals(keystoreType)
                    ? DownloadRequestKeystore.KEYSTORE_TYPE_TLS
                    : DownloadRequestKeystore.KEYSTORE_TYPE_ENC_SIGN;

            DownloadRequestKeystore request = new DownloadRequestKeystore(keystoreUsage);
            DownloadResponseKeystore response = processing.processDownloadRequestKeystore(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            return Response.ok(response.getCertificateList()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Export a certificate by fingerprint
     * Request body: { fingerprintSHA1, keystoreType (sign|tls), format (PEM|PEM_CHAIN|DER|PKCS7|SSH2) }
     */
    @POST
    @Path("/export")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/octet-stream")
    public Response exportCertificate(CertificateExportRequestDTO exportRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            int keystoreUsage = "tls".equals(exportRequest.getKeystoreType())
                    ? CertificateExportRequest.KEYSTORE_USAGE_TLS
                    : CertificateExportRequest.KEYSTORE_USAGE_ENC_SIGN;

            // Map format string to constant
            String format = mapExportFormat(exportRequest.getFormat());

            CertificateExportRequest request = new CertificateExportRequest(
                    keystoreUsage,
                    exportRequest.getFingerprintSHA1(),
                    format
            );

            CertificateExportResponse response = processing.processCertificateExportRequest(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            String filename = "certificate." + getFileExtension(format);
            return Response.ok(response.getExportData())
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Generate a CSR (Certificate Signing Request) for a certificate
     * Request body: { fingerprintSHA1, keystoreType (sign|tls), requestType (PKCS10|CRMF) }
     */
    @POST
    @Path("/generate-csr")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateCSR(CSRGenerationRequestDTO csrRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            int keystoreUsage = "tls".equals(csrRequest.getKeystoreType())
                    ? CSRGenerationRequest.KEYSTORE_USAGE_TLS
                    : CSRGenerationRequest.KEYSTORE_USAGE_ENC_SIGN;

            int requestType = "CRMF".equalsIgnoreCase(csrRequest.getRequestType())
                    ? CSRGenerationRequest.SELECTION_CRMF
                    : CSRGenerationRequest.SELECTION_PKCS10;

            CSRGenerationRequest request = new CSRGenerationRequest(
                    keystoreUsage,
                    csrRequest.getFingerprintSHA1(),
                    requestType
            );

            CSRGenerationResponse response = processing.processCSRGenerationRequest(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            CSRGenerationResponseDTO dto = new CSRGenerationResponseDTO();
            dto.setCsrBase64(response.getCSRBase64());
            dto.setCrmfTLSBase64(response.getCrmfTLSBase64());
            dto.setCrmfSignatureBase64(response.getCrmfSignatureBase64());
            dto.setCrmfEncryptionBase64(response.getCrmfEncryptionBase64());

            return Response.ok(dto).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Verify CRL (Certificate Revocation List) for certificates
     * Request body: { keystoreType (sign|tls), fingerprintSHA1 (optional - if null, verifies all) }
     */
    @POST
    @Path("/verify-crl")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyCRL(CRLVerificationRequestDTO crlRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            String keystoreUsage = "tls".equals(crlRequest.getKeystoreType())
                    ? CRLVerificationRequest.KEYSTORE_USAGE_TLS
                    : CRLVerificationRequest.KEYSTORE_USAGE_ENC_SIGN;

            int process = (crlRequest.getFingerprintSHA1() == null || crlRequest.getFingerprintSHA1().isEmpty())
                    ? CRLVerificationRequest.PROCESS_VERIFY_ALL
                    : CRLVerificationRequest.PROCESS_VERIFY_SINGLE;

            CRLVerificationRequest request = new CRLVerificationRequest(process, keystoreUsage);
            if (process == CRLVerificationRequest.PROCESS_VERIFY_SINGLE) {
                request.setFingerprintSHA1(crlRequest.getFingerprintSHA1());
            }

            CRLVerificationResponse response = processing.processCRLVerificationRequest(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            List<CRLRevocationInformation> results = response.getInformationList();
            return Response.ok(results).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete a certificate from keystore
     * Path parameter: alias - the certificate alias to delete
     * Query parameter: keystoreType (sign|tls)
     *
     * Before deleting, checks if the certificate is in use by any partner
     */
    @DELETE
    @Path("/{alias}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCertificate(
            @PathParam("alias") String alias,
            @QueryParam("keystoreType") @DefaultValue("sign") String keystoreType,
            @QueryParam("force") @DefaultValue("false") boolean force) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            CertificateManager certManager = "tls".equals(keystoreType)
                    ? processing.getCertificateManagerTLS()
                    : processing.getCertificateManagerSignEncrypt();

            // Find the certificate by alias
            KeystoreCertificate certificate = certManager.getKeystoreCertificate(alias);
            if (certificate == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Certificate with alias '" + alias + "' not found"))
                        .build();
            }

            // Check if certificate is in use by partners (only for sign/encrypt keystore)
            if ("sign".equals(keystoreType) && !force) {
                PartnerListRequest partnerRequest = new PartnerListRequest(PartnerListRequest.LIST_ALL);
                PartnerListResponse partnerResponse = processing.processPartnerListRequest(partnerRequest);
                List<Partner> partners = partnerResponse.getList();

                List<String> partnersUsing = new ArrayList<>();
                String certFingerprint = certificate.getFingerPrintSHA1();

                for (Partner partner : partners) {
                    if (certFingerprint.equals(partner.getCryptFingerprintSHA1())) {
                        partnersUsing.add(partner.getName() + " (encryption)");
                    }
                    if (certFingerprint.equals(partner.getSignFingerprintSHA1())) {
                        partnersUsing.add(partner.getName() + " (signing)");
                    }
                    if (certFingerprint.equals(partner.getCryptOverwriteLocalstationFingerprintSHA1())) {
                        partnersUsing.add(partner.getName() + " (encryption - overwrite local)");
                    }
                    if (certFingerprint.equals(partner.getSignOverwriteLocalstationFingerprintSHA1())) {
                        partnersUsing.add(partner.getName() + " (signing - overwrite local)");
                    }
                }

                if (!partnersUsing.isEmpty()) {
                    CertificateInUseResponse inUseResponse = new CertificateInUseResponse();
                    inUseResponse.setInUse(true);
                    inUseResponse.setPartnersUsing(partnersUsing);
                    inUseResponse.setMessage("Certificate is in use by " + partnersUsing.size() + " partner(s)");
                    return Response.status(Response.Status.CONFLICT)
                            .entity(inUseResponse)
                            .build();
                }
            }

            // Delete the certificate
            try {
                certManager.deleteKeystoreEntry(alias);
            } catch (Throwable t) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Failed to delete certificate: " + t.getMessage()))
                        .build();
            }

            return Response.ok(new SuccessResponse("Certificate '" + alias + "' deleted successfully")).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Import/upload certificates to keystore
     * Request body: { keystoreType (sign|tls), certificateList }
     */
    @POST
    @Path("/import")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importCertificates(
            @Context SecurityContext securityContext,
            UploadKeystoreRequestDTO uploadRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            int keystoreUsage = "tls".equals(uploadRequest.getKeystoreType())
                    ? UploadRequestKeystore.KEYSTORE_TYPE_TLS
                    : UploadRequestKeystore.KEYSTORE_TYPE_ENC_SIGN;

            UploadRequestKeystore request = new UploadRequestKeystore(keystoreUsage);
            request.addCertificateList(uploadRequest.getCertificateList());

            String userName = securityContext.getUserPrincipal() != null
                    ? securityContext.getUserPrincipal().getName()
                    : "REST_API";
            String processOriginHost = "REST_API";

            UploadResponseKeystore response = processing.processUploadRequestKeystore(request, userName, processOriginHost);

            if (response.getException() != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            return Response.ok(new SuccessResponse("Certificates imported successfully")).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    // Helper methods

    private String mapExportFormat(String format) {
        if (format == null) {
            return KeystoreCertificate.CERTIFICATE_FORMAT_PEM;
        }
        switch (format.toUpperCase()) {
            case "PEM":
                return KeystoreCertificate.CERTIFICATE_FORMAT_PEM;
            case "PEM_CHAIN":
                return KeystoreCertificate.CERTIFICATE_FORMAT_PEM_CHAIN;
            case "DER":
                return KeystoreCertificate.CERTIFICATE_FORMAT_DER;
            case "PKCS7":
                return KeystoreCertificate.CERTIFICATE_FORMAT_PKCS7;
            case "SSH2":
                return KeystoreCertificate.CERTIFICATE_FORMAT_SSH2;
            default:
                return KeystoreCertificate.CERTIFICATE_FORMAT_PEM;
        }
    }

    private String getFileExtension(String format) {
        if (KeystoreCertificate.CERTIFICATE_FORMAT_PEM.equals(format)
                || KeystoreCertificate.CERTIFICATE_FORMAT_PEM_CHAIN.equals(format)) {
            return "pem";
        } else if (KeystoreCertificate.CERTIFICATE_FORMAT_DER.equals(format)) {
            return "der";
        } else if (KeystoreCertificate.CERTIFICATE_FORMAT_PKCS7.equals(format)) {
            return "p7b";
        } else if (KeystoreCertificate.CERTIFICATE_FORMAT_SSH2.equals(format)) {
            return "pub";
        }
        return "cer";
    }

    // DTOs

    public static class CertificateExportRequestDTO {
        private String fingerprintSHA1;
        private String keystoreType;
        private String format;

        public CertificateExportRequestDTO() {
        }

        public String getFingerprintSHA1() {
            return fingerprintSHA1;
        }

        public void setFingerprintSHA1(String fingerprintSHA1) {
            this.fingerprintSHA1 = fingerprintSHA1;
        }

        public String getKeystoreType() {
            return keystoreType;
        }

        public void setKeystoreType(String keystoreType) {
            this.keystoreType = keystoreType;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    public static class CSRGenerationRequestDTO {
        private String fingerprintSHA1;
        private String keystoreType;
        private String requestType;

        public CSRGenerationRequestDTO() {
        }

        public String getFingerprintSHA1() {
            return fingerprintSHA1;
        }

        public void setFingerprintSHA1(String fingerprintSHA1) {
            this.fingerprintSHA1 = fingerprintSHA1;
        }

        public String getKeystoreType() {
            return keystoreType;
        }

        public void setKeystoreType(String keystoreType) {
            this.keystoreType = keystoreType;
        }

        public String getRequestType() {
            return requestType;
        }

        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }
    }

    public static class CSRGenerationResponseDTO {
        private String csrBase64;
        private String crmfTLSBase64;
        private String crmfSignatureBase64;
        private String crmfEncryptionBase64;

        public CSRGenerationResponseDTO() {
        }

        public String getCsrBase64() {
            return csrBase64;
        }

        public void setCsrBase64(String csrBase64) {
            this.csrBase64 = csrBase64;
        }

        public String getCrmfTLSBase64() {
            return crmfTLSBase64;
        }

        public void setCrmfTLSBase64(String crmfTLSBase64) {
            this.crmfTLSBase64 = crmfTLSBase64;
        }

        public String getCrmfSignatureBase64() {
            return crmfSignatureBase64;
        }

        public void setCrmfSignatureBase64(String crmfSignatureBase64) {
            this.crmfSignatureBase64 = crmfSignatureBase64;
        }

        public String getCrmfEncryptionBase64() {
            return crmfEncryptionBase64;
        }

        public void setCrmfEncryptionBase64(String crmfEncryptionBase64) {
            this.crmfEncryptionBase64 = crmfEncryptionBase64;
        }
    }

    public static class CRLVerificationRequestDTO {
        private String keystoreType;
        private String fingerprintSHA1;

        public CRLVerificationRequestDTO() {
        }

        public String getKeystoreType() {
            return keystoreType;
        }

        public void setKeystoreType(String keystoreType) {
            this.keystoreType = keystoreType;
        }

        public String getFingerprintSHA1() {
            return fingerprintSHA1;
        }

        public void setFingerprintSHA1(String fingerprintSHA1) {
            this.fingerprintSHA1 = fingerprintSHA1;
        }
    }

    public static class UploadKeystoreRequestDTO {
        private String keystoreType;
        private List<KeystoreCertificate> certificateList;

        public UploadKeystoreRequestDTO() {
        }

        public String getKeystoreType() {
            return keystoreType;
        }

        public void setKeystoreType(String keystoreType) {
            this.keystoreType = keystoreType;
        }

        public List<KeystoreCertificate> getCertificateList() {
            return certificateList;
        }

        public void setCertificateList(List<KeystoreCertificate> certificateList) {
            this.certificateList = certificateList;
        }
    }

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

    public static class CertificateInUseResponse {
        private boolean inUse;
        private String message;
        private List<String> partnersUsing;

        public CertificateInUseResponse() {
        }

        public boolean isInUse() {
            return inUse;
        }

        public void setInUse(boolean inUse) {
            this.inUse = inUse;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getPartnersUsing() {
            return partnersUsing;
        }

        public void setPartnersUsing(List<String> partnersUsing) {
            this.partnersUsing = partnersUsing;
        }
    }
}
