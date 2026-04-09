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

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.glassfish.jersey.media.multipart.FormDataParam;
import java.util.ArrayList;
import java.util.List;

/**
 * REST resource for certificate management operations
 * Provides certificate listing, import, export, CSR generation, and CRL verification
 *
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
     * Export entire keystore
     * Request body: { keystoreType (sign|tls), format (PKCS12|JKS) }
     */
    @POST
    @Path("/export-keystore")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/octet-stream")
    public Response exportKeystore(ExportKeystoreRequestDTO exportRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get the appropriate certificate manager
            CertificateManager manager = "tls".equals(exportRequest.getKeystoreType())
                    ? processing.getCertificateManagerTLS()
                    : processing.getCertificateManagerSignEncrypt();

            if (manager == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Certificate manager not available"))
                        .build();
            }

            // Get the keystore and serialize it to bytes
            java.security.KeyStore keystore = manager.getKeystore();
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            char[] password = manager.getKeystorePass();
            keystore.store(baos, password);
            byte[] keystoreData = baos.toByteArray();

            String filename = exportRequest.getKeystoreType() + "_keystore." +
                             ("JKS".equalsIgnoreCase(exportRequest.getFormat()) ? "jks" : "p12");

            return Response.ok(keystoreData)
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
     * Generate a new key pair and certificate
     * Request body: KeyGenerationRequestDTO with all certificate details
     */
    @POST
    @Path("/generate-key")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateKey(KeyGenerationRequestDTO keyGenRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get the appropriate certificate manager
            CertificateManager manager = "tls".equals(keyGenRequest.getKeystoreType())
                    ? processing.getCertificateManagerTLS()
                    : processing.getCertificateManagerSignEncrypt();

            if (manager == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Certificate manager not available"))
                        .build();
            }

            // Build KeyGenerationValues from request
            de.mendelson.util.security.keygeneration.KeyGenerationValues values =
                    new de.mendelson.util.security.keygeneration.KeyGenerationValues();
            values.setKeyAlgorithm(keyGenRequest.getKeyAlgorithm());
            values.setKeySize(keyGenRequest.getKeySize());
            values.setCommonName(keyGenRequest.getCommonName());
            values.setOrganisationUnit(keyGenRequest.getOrganisationUnit());
            values.setOrganisationName(keyGenRequest.getOrganisationName());
            values.setLocalityName(keyGenRequest.getLocalityName());
            values.setStateName(keyGenRequest.getStateName());
            values.setCountryCode(keyGenRequest.getCountryCode());
            values.setEmailAddress(keyGenRequest.getEmailAddress());
            values.setKeyValidInDays(keyGenRequest.getKeyValidInDays());
            values.setSignatureAlgorithm(keyGenRequest.getSignatureAlgorithm());

            // Generate the key
            de.mendelson.util.security.keygeneration.KeyGenerator generator =
                    new de.mendelson.util.security.keygeneration.KeyGenerator();
            de.mendelson.util.security.keygeneration.KeyGenerationResult result =
                    generator.generateKeyPair(values);

            // Store in keystore
            manager.getKeystore().setKeyEntry(
                    keyGenRequest.getAlias(),
                    result.getKeyPair().getPrivate(),
                    manager.getKeystorePass(),
                    new java.security.cert.Certificate[]{result.getCertificate()}
            );
            manager.saveKeystore();

            KeyGenerationResponseDTO response = new KeyGenerationResponseDTO();
            response.setAlias(keyGenRequest.getAlias());
            response.setSubjectDN(result.getCertificate().getSubjectX500Principal().getName());
            return Response.ok(response).build();

        } catch (Throwable e) {
            e.printStackTrace();
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

    /**
     * Import certificate or keystore file via multipart form-data upload
     * Form parameters:
     *   file: Certificate/keystore file
     *   keystoreType: sign|tls
     *   importType: certificate|keystore
     *   password: (optional, required for keystore import)
     *   alias: (optional) Alias for the imported certificate/key
     */
    @POST
    @Path("/import-file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importCertificateFile(
            @Context SecurityContext securityContext,
            @FormDataParam("file") java.io.InputStream fileInputStream,
            @FormDataParam("file") org.glassfish.jersey.media.multipart.FormDataContentDisposition fileDetail,
            @FormDataParam("keystoreType") String keystoreType,
            @FormDataParam("importType") @DefaultValue("keystore") String importType,
            @FormDataParam("password") String password,
            @FormDataParam("alias") String alias) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            if (fileInputStream == null || fileDetail == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("File is required"))
                        .build();
            }

            CertificateManager certManager = "tls".equals(keystoreType)
                    ? processing.getCertificateManagerTLS()
                    : processing.getCertificateManagerSignEncrypt();

            String fileName = fileDetail.getFileName();

            if ("certificate".equalsIgnoreCase(importType)) {
                // Import standalone certificate (from trading partner)
                return importStandaloneCertificate(fileInputStream, fileName, alias, certManager);
            } else {
                // Import keystore with private key
                return importKeystoreFile(fileInputStream, fileName, password, alias, certManager);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to import: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Import a standalone certificate file (.cer, .crt, .pem) without private key
     */
    private Response importStandaloneCertificate(
            java.io.InputStream fileInputStream,
            String fileName,
            String alias,
            CertificateManager certManager) {
        try {
            // Read certificate file
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] certBytes = baos.toByteArray();

            // Parse certificate
            java.security.cert.CertificateFactory certFactory =
                java.security.cert.CertificateFactory.getInstance("X.509");
            java.security.cert.X509Certificate x509Cert;

            try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(certBytes)) {
                x509Cert = (java.security.cert.X509Certificate) certFactory.generateCertificate(bis);
            }

            if (x509Cert == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Unable to parse certificate file"))
                        .build();
            }

            // Generate alias if not provided
            if (alias == null || alias.trim().isEmpty()) {
                alias = x509Cert.getSubjectX500Principal().getName();
                // Simplify alias - extract CN if present
                if (alias.contains("CN=")) {
                    String cn = alias.substring(alias.indexOf("CN=") + 3);
                    if (cn.contains(",")) {
                        cn = cn.substring(0, cn.indexOf(","));
                    }
                    alias = cn.trim();
                }
            }

            // Add certificate to keystore
            certManager.addCertificate(alias, x509Cert);

            ImportCertificateResponseDTO response = new ImportCertificateResponseDTO();
            response.setAlias(alias);
            response.setSubjectDN(x509Cert.getSubjectX500Principal().getName());
            response.setIssuerDN(x509Cert.getIssuerX500Principal().getName());
            response.setMessage("Certificate imported successfully");

            return Response.ok(response).build();

        } catch (Throwable e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Failed to import certificate: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Import a keystore file (.p12, .pfx, .jks) with private key
     */
    private Response importKeystoreFile(
            java.io.InputStream fileInputStream,
            String fileName,
            String password,
            String alias,
            CertificateManager certManager) {
        try {
            if (password == null || password.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Password is required for keystore import"))
                        .build();
            }

            // Read keystore file
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] keystoreBytes = baos.toByteArray();

            // Determine keystore type from file extension
            String keystoreType = "PKCS12"; // default
            if (fileName.toLowerCase().endsWith(".jks")) {
                keystoreType = "JKS";
            }

            // Load keystore
            java.security.KeyStore keystore = java.security.KeyStore.getInstance(keystoreType);
            try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(keystoreBytes)) {
                keystore.load(bis, password.toCharArray());
            }

            // Import all entries from keystore
            List<String> importedAliases = new ArrayList<>();
            java.util.Enumeration<String> aliases = keystore.aliases();

            while (aliases.hasMoreElements()) {
                String entryAlias = aliases.nextElement();

                if (keystore.isKeyEntry(entryAlias)) {
                    // Import key pair with certificate chain
                    java.security.Key key = keystore.getKey(entryAlias, password.toCharArray());
                    java.security.cert.Certificate[] chain = keystore.getCertificateChain(entryAlias);

                    String targetAlias = (alias != null && !alias.isEmpty()) ? alias : entryAlias;
                    certManager.getKeystore().setKeyEntry(targetAlias, key,
                        certManager.getKeystorePass(), chain);
                    importedAliases.add(targetAlias);
                } else if (keystore.isCertificateEntry(entryAlias)) {
                    // Import certificate only
                    java.security.cert.Certificate cert = keystore.getCertificate(entryAlias);
                    if (cert instanceof java.security.cert.X509Certificate) {
                        String targetAlias = (alias != null && !alias.isEmpty()) ? alias : entryAlias;
                        certManager.addCertificate(targetAlias, (java.security.cert.X509Certificate) cert);
                        importedAliases.add(targetAlias);
                    }
                }
            }

            certManager.saveKeystore();

            ImportCertificateResponseDTO response = new ImportCertificateResponseDTO();
            response.setMessage("Keystore imported successfully. Imported " + importedAliases.size() + " entries");
            response.setImportedAliases(importedAliases);

            return Response.ok(response).build();

        } catch (Throwable e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Failed to import keystore: " + e.getMessage()))
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

    public static class ExportKeystoreRequestDTO {
        private String keystoreType;
        private String format;

        public ExportKeystoreRequestDTO() {
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

    public static class KeyGenerationRequestDTO {
        private String keystoreType;
        private String alias;
        private String keyAlgorithm = "RSA";
        private int keySize = 2048;
        private String commonName;
        private String organisationUnit;
        private String organisationName;
        private String localityName;
        private String stateName;
        private String countryCode;
        private String emailAddress;
        private int keyValidInDays = 365;
        private String signatureAlgorithm = "SHA256WithRSA";

        public KeyGenerationRequestDTO() {
        }

        public String getKeystoreType() {
            return keystoreType;
        }

        public void setKeystoreType(String keystoreType) {
            this.keystoreType = keystoreType;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getKeyAlgorithm() {
            return keyAlgorithm;
        }

        public void setKeyAlgorithm(String keyAlgorithm) {
            this.keyAlgorithm = keyAlgorithm;
        }

        public int getKeySize() {
            return keySize;
        }

        public void setKeySize(int keySize) {
            this.keySize = keySize;
        }

        public String getCommonName() {
            return commonName;
        }

        public void setCommonName(String commonName) {
            this.commonName = commonName;
        }

        public String getOrganisationUnit() {
            return organisationUnit;
        }

        public void setOrganisationUnit(String organisationUnit) {
            this.organisationUnit = organisationUnit;
        }

        public String getOrganisationName() {
            return organisationName;
        }

        public void setOrganisationName(String organisationName) {
            this.organisationName = organisationName;
        }

        public String getLocalityName() {
            return localityName;
        }

        public void setLocalityName(String localityName) {
            this.localityName = localityName;
        }

        public String getStateName() {
            return stateName;
        }

        public void setStateName(String stateName) {
            this.stateName = stateName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public int getKeyValidInDays() {
            return keyValidInDays;
        }

        public void setKeyValidInDays(int keyValidInDays) {
            this.keyValidInDays = keyValidInDays;
        }

        public String getSignatureAlgorithm() {
            return signatureAlgorithm;
        }

        public void setSignatureAlgorithm(String signatureAlgorithm) {
            this.signatureAlgorithm = signatureAlgorithm;
        }
    }

    public static class KeyGenerationResponseDTO {
        private String alias;
        private String subjectDN;

        public KeyGenerationResponseDTO() {
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getSubjectDN() {
            return subjectDN;
        }

        public void setSubjectDN(String subjectDN) {
            this.subjectDN = subjectDN;
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

    public static class ImportCertificateResponseDTO {
        private String message;
        private String alias;
        private String subjectDN;
        private String issuerDN;
        private List<String> importedAliases;

        public ImportCertificateResponseDTO() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getSubjectDN() {
            return subjectDN;
        }

        public void setSubjectDN(String subjectDN) {
            this.subjectDN = subjectDN;
        }

        public String getIssuerDN() {
            return issuerDN;
        }

        public void setIssuerDN(String issuerDN) {
            this.issuerDN = issuerDN;
        }

        public List<String> getImportedAliases() {
            return importedAliases;
        }

        public void setImportedAliases(List<String> importedAliases) {
            this.importedAliases = importedAliases;
        }
    }
}
