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
import de.mendelson.util.security.keydata.KeydataAccessDB;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.glassfish.jersey.media.multipart.FormDataParam;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * REST resource for certificate management operations
 * Provides certificate listing, import, export, CSR generation, and CRL verification
 *
 */
@Path("/certificates")
public class CertificateResource {

    private static final Logger logger = Logger.getLogger("de.mendelson.as2.server");

    static {
            }

    public CertificateResource() {
        logger.info("[DEBUG CertificateResource] Constructor called - instance created");
    }

    /**
     * List certificates by keystore type
     * Query parameters:
     *   keystoreType: sign|tls|ssl (default: sign)
     *     - sign: User-specific sign/encrypt certificates
     *     - tls: User-specific TLS certificates (for outbound client auth)
     *     - ssl: System-wide SSL/TLS certificates (for inbound HTTPS server, user_id=0 only)
     *   visibleToUser: User ID to filter certificates visible to specific user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCertificates(
            @Context SecurityContext securityContext,
            @QueryParam("keystoreType") @DefaultValue("sign") String keystoreType,
            @QueryParam("visibleToUser") Integer visibleToUserId) {
        try {

            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get current user's ID from security context
            int currentUserId = getCurrentUserId(securityContext, processing);
            int keystoreUsage;
            int userId; // User ID for keystore operations

            if ("ssl".equals(keystoreType)) {
                // System-wide SSL/TLS keystore (user_id=-1, system-wide)
                keystoreUsage = DownloadRequestKeystore.KEYSTORE_TYPE_TLS;
                userId = KeydataAccessDB.SYSTEM_WIDE_USER_ID;
                            } else if ("tls".equals(keystoreType)) {
                // User-specific TLS keystore
                keystoreUsage = DownloadRequestKeystore.KEYSTORE_TYPE_TLS;
                userId = currentUserId; // Use current user's ID
                            } else {
                // User-specific sign/encrypt keystore
                keystoreUsage = DownloadRequestKeystore.KEYSTORE_TYPE_ENC_SIGN;
                userId = currentUserId; // Use current user's ID
                            }

            DownloadRequestKeystore request = new DownloadRequestKeystore(keystoreUsage, userId);
                        DownloadResponseKeystore response = processing.processDownloadRequestKeystore(request);

            if (response.getException() != null) {
                                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            List<KeystoreCertificate> certificates = response.getCertificateList();

            // Filter certificates by user ownership if requested
            // For user-specific keystores (where userId == currentUserId), certificates are owned by the user directly,
            // not by partners, so we should NOT filter them by partner ownership
            if (visibleToUserId != null && visibleToUserId > 0 && userId != currentUserId) {
                                // Get user's partners to determine which certificates they can see
                de.mendelson.comm.as2.partner.PartnerAccessDB partnerDB =
                    new de.mendelson.comm.as2.partner.PartnerAccessDB(processing.getDBDriverManager());
                List<Partner> userPartners = partnerDB.getPartnersOwnedByUser(
                    visibleToUserId,
                    de.mendelson.comm.as2.partner.PartnerAccessDB.DATA_COMPLETENESS_FULL);

                                // Extract partner IDs
                List<Integer> partnerIds = new ArrayList<>();
                for (Partner partner : userPartners) {
                    partnerIds.add(partner.getDBId());
                                    }

                // Filter certificates to only those belonging to user's partners
                List<KeystoreCertificate> filteredCertificates = new ArrayList<>();
                for (KeystoreCertificate cert : certificates) {
                                        // Get partner ID for this certificate from database
                    Integer certPartnerId = getCertificatePartnerId(
                        processing, cert.getFingerPrintSHA1(), keystoreUsage);

                    if (certPartnerId != null && partnerIds.contains(certPartnerId)) {
                        filteredCertificates.add(cert);
                                            } else {
                                            }
                }

                certificates = filteredCertificates;
                            } else if (userId == currentUserId) {
                            }

            // Convert to DTO format with partner usage information
            List<Map<String, Object>> certificateList = new ArrayList<>();
            for (KeystoreCertificate cert : certificates) {
                Map<String, Object> certData = new HashMap<>();

                // Certificate fields
                certData.put("alias", cert.getAlias());
                certData.put("subjectDN", cert.getSubjectDN());
                certData.put("issuerDN", cert.getIssuerDN());
                certData.put("notAfter", cert.getNotAfter().getTime());
                certData.put("notBefore", cert.getNotBefore().getTime());
                certData.put("fingerprintSHA1", cert.getFingerPrintSHA1());
                certData.put("fingerprintMD5", cert.getFingerPrintMD5());
                certData.put("isKeyPair", cert.getIsKeyPair());
                certData.put("isRootCertificate", cert.isRootCertificate());

                // Owner fields (for single-user mode, all belong to current user)
                certData.put("userId", currentUserId);

                // Check if certificate is used by current user's partners
                // This applies to all keystore types: sign (for encryption/signing), tls (for inbound cert auth)
                logger.info("[DEBUG] Checking certificate usage for alias: " + cert.getAlias() + ", keystoreType: " + keystoreType + ", fingerprint: " + cert.getFingerPrintSHA1());
                List<String> partnersUsing = getPartnersUsingCertificate(processing, cert.getFingerPrintSHA1(), currentUserId);
                certData.put("partnersUsing", partnersUsing);
                boolean canDelete = partnersUsing.isEmpty();
                certData.put("canDelete", canDelete);
                logger.info("[DEBUG] Certificate " + cert.getAlias() + " canDelete=" + canDelete + ", partnersUsing=" + partnersUsing);

                certificateList.add(certData);
            }

            return Response.ok(certificateList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * List ALL users' certificates (admin only)
     * Query parameters:
     *   keystoreType: sign|tls (default: sign)
     * Returns certificates with owner information
     */
    @GET
    @Path("/all-users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllUsersCertificates(
            @Context SecurityContext securityContext,
            @QueryParam("keystoreType") @DefaultValue("sign") String keystoreType) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Check if current user is admin (has USER_MANAGE permission)
            int currentUserId = getCurrentUserId(securityContext, processing);
            de.mendelson.comm.as2.usermanagement.UserManagementAccessDB userAccess =
                new de.mendelson.comm.as2.usermanagement.UserManagementAccessDB(
                    processing.getDBDriverManager(), logger);
            de.mendelson.comm.as2.usermanagement.WebUIUser currentUser =
                userAccess.getUser(currentUserId);

            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("User not found"))
                        .build();
            }

            // Check if user has USER_MANAGE permission (admin)
            java.util.Set<String> permissions =
                userAccess.getUserPermissions(currentUser.getUsername());
            boolean isAdmin = permissions.contains("USER_MANAGE");

            if (!isAdmin) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorResponse("Access denied: Admin permission required"))
                        .build();
            }

            // Determine keystore usage type
            int keystoreUsage;
            if ("tls".equals(keystoreType)) {
                keystoreUsage = de.mendelson.util.security.cert.clientserver.AllUsersCertificatesRequest.KEYSTORE_TYPE_TLS;
            } else {
                keystoreUsage = de.mendelson.util.security.cert.clientserver.AllUsersCertificatesRequest.KEYSTORE_TYPE_ENC_SIGN;
            }

            // Create request and call existing backend handler
            de.mendelson.util.security.cert.clientserver.AllUsersCertificatesRequest request =
                new de.mendelson.util.security.cert.clientserver.AllUsersCertificatesRequest(keystoreUsage);

            de.mendelson.util.security.cert.clientserver.AllUsersCertificatesResponse response =
                processing.processAllUsersCertificatesRequest(request);

            if (response.getException() != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse(response.getException().getMessage()))
                        .build();
            }

            // Convert to DTO format that includes owner information
            List<Map<String, Object>> certificateList = new ArrayList<>();
            for (de.mendelson.util.security.cert.CertificateWithOwner certWithOwner : response.getCertificates()) {
                Map<String, Object> certData = new HashMap<>();

                // Certificate fields
                de.mendelson.util.security.cert.KeystoreCertificate cert = certWithOwner.getCertificate();
                certData.put("alias", cert.getAlias());
                certData.put("subjectDN", cert.getSubjectDN());
                certData.put("issuerDN", cert.getIssuerDN());
                certData.put("notAfter", cert.getNotAfter().getTime());
                certData.put("notBefore", cert.getNotBefore().getTime());
                certData.put("fingerprintSHA1", cert.getFingerPrintSHA1());
                certData.put("fingerprintMD5", cert.getFingerPrintMD5());
                certData.put("isKeyPair", cert.getIsKeyPair());
                certData.put("isRootCertificate", cert.isRootCertificate());

                // Owner fields
                certData.put("userId", certWithOwner.getUserId());
                certData.put("username", certWithOwner.getUsername());

                // Check if certificate is used by current user's partners
                // This applies to all keystore types: sign (for encryption/signing), tls (for inbound cert auth)
                List<String> partnersUsing = getPartnersUsingCertificate(processing, cert.getFingerPrintSHA1(), currentUserId);
                certData.put("partnersUsing", partnersUsing);
                certData.put("canDelete", certWithOwner.getUserId() == currentUserId && partnersUsing.isEmpty());

                certificateList.add(certData);
            }

            return Response.ok(certificateList).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error listing all users' certificates", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * Helper method to get partner ID for a certificate
     */
    private Integer getCertificatePartnerId(AS2ServerProcessing processing,
                                            String fingerprintSHA1, int keystoreUsage) {
        try {
            java.sql.Connection connection = processing.getDBDriverManager()
                .getConnectionWithoutErrorHandling(de.mendelson.util.database.IDBDriverManager.DB_CONFIG);
            String query = "SELECT partnerid FROM certificates WHERE fingerprintsha1 = ? AND category = ?";

            // Map keystore usage to category
            int category = (keystoreUsage == DownloadRequestKeystore.KEYSTORE_TYPE_TLS) ? 3 : 1;

            try (java.sql.PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, fingerprintSHA1);
                stmt.setInt(2, category);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("partnerid");
                    }
                }
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            // Log error but don't fail the request
            System.err.println("Error getting certificate partner ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Helper method to get partners using a certificate (for current user only)
     */
    private List<String> getPartnersUsingCertificate(AS2ServerProcessing processing,
                                                      String fingerprintSHA1,
                                                      int currentUserId) {
        List<String> partnersUsing = new ArrayList<>();
        logger.info("[DEBUG] getPartnersUsingCertificate called for fingerprint: " + fingerprintSHA1 + ", currentUserId: " + currentUserId);
        try {
            // Get ONLY partners owned by the current user
            de.mendelson.comm.as2.partner.PartnerAccessDB partnerDB =
                new de.mendelson.comm.as2.partner.PartnerAccessDB(processing.getDBDriverManager());
            List<Partner> partners = partnerDB.getPartnersOwnedByUser(
                currentUserId,
                de.mendelson.comm.as2.partner.PartnerAccessDB.DATA_COMPLETENESS_FULL);
            logger.info("[DEBUG] Total partners owned by current user: " + partners.size());

            // Check each partner's certificate configuration
            for (Partner partner : partners) {
                logger.info("[DEBUG] Checking partner: " + partner.getName() + " (ID=" + partner.getDBId() + ", isLocalStation=" + partner.isLocalStation() + ")");

                // Check sign/encrypt certificates (for remote partners)
                if (fingerprintSHA1.equals(partner.getCryptFingerprintSHA1())) {
                    logger.info("[DEBUG] MATCH: Partner " + partner.getName() + " uses this cert for ENCRYPTION");
                    partnersUsing.add(partner.getName() + " (encryption)");
                }
                if (fingerprintSHA1.equals(partner.getSignFingerprintSHA1())) {
                    logger.info("[DEBUG] MATCH: Partner " + partner.getName() + " uses this cert for SIGNING");
                    partnersUsing.add(partner.getName() + " (signing)");
                }
                if (fingerprintSHA1.equals(partner.getCryptOverwriteLocalstationFingerprintSHA1())) {
                    logger.info("[DEBUG] MATCH: Partner " + partner.getName() + " uses this cert for ENCRYPTION-OVERWRITE-LOCAL");
                    partnersUsing.add(partner.getName() + " (encryption - overwrite local)");
                }
                if (fingerprintSHA1.equals(partner.getSignOverwriteLocalstationFingerprintSHA1())) {
                    logger.info("[DEBUG] MATCH: Partner " + partner.getName() + " uses this cert for SIGNING-OVERWRITE-LOCAL");
                    partnersUsing.add(partner.getName() + " (signing - overwrite local)");
                }

                // Check TLS certificate (ONLY for local station - inbound cert auth)
                if (partner.isLocalStation()) {
                    // Check inbound auth credentials list for certificate authentication
                    List<de.mendelson.comm.as2.partner.PartnerInboundAuthCredential> inboundCreds =
                        partner.getInboundAuthCredentialsList();
                    logger.info("[DEBUG] Local station " + partner.getName() + " has " + inboundCreds.size() + " inbound auth credentials");

                    for (de.mendelson.comm.as2.partner.PartnerInboundAuthCredential cred : inboundCreds) {
                        if (cred.getAuthType() == de.mendelson.comm.as2.partner.PartnerInboundAuthCredential.AUTH_TYPE_CERTIFICATE) {
                            String credFingerprint = cred.getCertFingerprint();
                            logger.info("[DEBUG] Found certificate auth credential with fingerprint: " + credFingerprint);
                            if (fingerprintSHA1.equals(credFingerprint)) {
                                logger.info("[DEBUG] MATCH: Local station " + partner.getName() + " uses this cert for TLS INBOUND CERT AUTH");
                                partnersUsing.add(partner.getName() + " (TLS inbound cert auth)");
                            }
                        }
                    }
                }
            }
            logger.info("[DEBUG] Partners using this certificate: " + partnersUsing.size() + " - " + partnersUsing);
        } catch (Exception e) {
            logger.warning("Failed to check certificate usage: " + e.getMessage());
            e.printStackTrace();
        }
        return partnersUsing;
    }

    /**
     * Export a certificate by fingerprint
     * Request body: { fingerprintSHA1, keystoreType (sign|tls|ssl), format (PEM|PEM_CHAIN|DER|PKCS7|SSH2) }
     */
    @POST
    @Path("/export")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/octet-stream")
    public Response exportCertificate(
            @Context SecurityContext securityContext,
            CertificateExportRequestDTO exportRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get current user's ID
            int currentUserId = getCurrentUserId(securityContext, processing);

            int keystoreUsage;
            int userId;

            if ("ssl".equals(exportRequest.getKeystoreType())) {
                // System-wide SSL/TLS keystore
                keystoreUsage = CertificateExportRequest.KEYSTORE_USAGE_TLS;
                userId = KeydataAccessDB.SYSTEM_WIDE_USER_ID;
            } else if ("tls".equals(exportRequest.getKeystoreType())) {
                // User-specific TLS keystore
                keystoreUsage = CertificateExportRequest.KEYSTORE_USAGE_TLS;
                userId = currentUserId;
            } else {
                // User-specific sign/encrypt keystore
                keystoreUsage = CertificateExportRequest.KEYSTORE_USAGE_ENC_SIGN;
                userId = currentUserId;
            }

            // Map format string to constant
            String format = mapExportFormat(exportRequest.getFormat());

            CertificateExportRequest request = new CertificateExportRequest(
                    keystoreUsage,
                    exportRequest.getFingerprintSHA1(),
                    format,
                    userId);

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
     * Request body: { keystoreType (sign|tls|ssl), format (PKCS12|JKS) }
     */
    @POST
    @Path("/export-keystore")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/octet-stream")
    public Response exportKeystore(
            @Context SecurityContext securityContext,
            ExportKeystoreRequestDTO exportRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Validate password is provided
            if (exportRequest.getPassword() == null || exportRequest.getPassword().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Password is required"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            // Get current user's ID
            int currentUserId = getCurrentUserId(securityContext, processing);

            // Get the appropriate certificate manager
            CertificateManager manager;
            if ("ssl".equals(exportRequest.getKeystoreType())) {
                // System-wide SSL/TLS keystore
                manager = processing.getCertificateManagerTLS();
            } else if ("tls".equals(exportRequest.getKeystoreType())) {
                // User-specific TLS keystore
                manager = new CertificateManager(logger);
                de.mendelson.util.security.cert.KeystoreStorageImplDB keystoreStorage =
                    new de.mendelson.util.security.cert.KeystoreStorageImplDB(
                        de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                        processing.getDBDriverManager(),
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS,
                        currentUserId);
                manager.loadKeystoreCertificates(keystoreStorage);
            } else {
                // User-specific sign/encrypt keystore
                manager = new CertificateManager(logger);
                de.mendelson.util.security.cert.KeystoreStorageImplDB keystoreStorage =
                    new de.mendelson.util.security.cert.KeystoreStorageImplDB(
                        de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                        processing.getDBDriverManager(),
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN,
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_PKCS12,
                        currentUserId);
                manager.loadKeystoreCertificates(keystoreStorage);
            }

            if (manager == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Certificate manager not available"))
                        .build();
            }

            // Get the keystore and serialize it to bytes with user-provided password
            java.security.KeyStore keystore = manager.getKeystore();
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            char[] password = exportRequest.getPassword().toCharArray();
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
     * Export all public certificates as PEM files in a ZIP archive
     * Request body: { keystoreType (sign|tls) }
     */
    @POST
    @Path("/export-all-public-pem")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/zip")
    public Response exportAllPublicCertificatesPEM(
            @Context SecurityContext securityContext,
            ExportKeystoreRequestDTO exportRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get current user's ID
            int currentUserId = getCurrentUserId(securityContext, processing);

            // Get the appropriate certificate manager
            CertificateManager manager;
            if ("tls".equals(exportRequest.getKeystoreType())) {
                // User-specific TLS keystore
                manager = new CertificateManager(logger);
                de.mendelson.util.security.cert.KeystoreStorageImplDB keystoreStorage =
                    new de.mendelson.util.security.cert.KeystoreStorageImplDB(
                        de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                        processing.getDBDriverManager(),
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS,
                        currentUserId);
                manager.loadKeystoreCertificates(keystoreStorage);
            } else {
                // User-specific sign/encrypt keystore
                manager = processing.getCertificateManagerSignEncrypt();
            }

            if (manager == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Certificate manager not available"))
                        .build();
            }

            // Create a ZIP file containing all public certificates as PEM files
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(baos);

            // Get all certificates from keystore
            java.security.KeyStore keystore = manager.getKeystore();
            java.util.Enumeration<String> aliases = keystore.aliases();

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                java.security.cert.Certificate cert = keystore.getCertificate(alias);

                if (cert != null) {
                    // Convert certificate to PEM format
                    String pemCert = "-----BEGIN CERTIFICATE-----\n" +
                        java.util.Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(cert.getEncoded()) +
                        "\n-----END CERTIFICATE-----\n";

                    // Add to ZIP
                    java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(alias + ".pem");
                    zipOut.putNextEntry(zipEntry);
                    zipOut.write(pemCert.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    zipOut.closeEntry();
                }
            }

            zipOut.close();
            byte[] zipData = baos.toByteArray();

            String filename = exportRequest.getKeystoreType() + "_public_certificates.zip";

            return Response.ok(zipData)
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
    public Response generateCSR(
            @Context SecurityContext securityContext,
            CSRGenerationRequestDTO csrRequest) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get current user's ID
            int currentUserId = getCurrentUserId(securityContext, processing);

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

            // Use user-specific keystore for CSR generation
            CSRGenerationResponse response = processing.processCSRGenerationRequest(request, currentUserId);

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
    public Response generateKey(
            @Context SecurityContext securityContext,
            KeyGenerationRequestDTO keyGenRequest) {
        try {
            logger.info("[DEBUG] Starting key generation process...");
            logger.info("[DEBUG] KeyGenRequest: keystoreType=" + keyGenRequest.getKeystoreType() +
                       ", alias=" + keyGenRequest.getAlias() +
                       ", CN=" + keyGenRequest.getCommonName());

            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                logger.severe("[DEBUG] Server processing not available!");
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Server processing not available"))
                        .build();
            }

            // Get current user's ID
            int currentUserId = getCurrentUserId(securityContext, processing);
            logger.info("[DEBUG] Current user ID: " + currentUserId);

            // Get the appropriate certificate manager
            CertificateManager manager;
            de.mendelson.util.security.cert.KeystoreStorage keystoreStorage = null;

            if ("ssl".equals(keyGenRequest.getKeystoreType())) {
                // System-wide SSL/TLS keystore
                logger.info("[DEBUG] Using system-wide SSL/TLS keystore");
                manager = processing.getCertificateManagerTLS();
            } else if ("tls".equals(keyGenRequest.getKeystoreType())) {
                // User-specific TLS keystore
                logger.info("[DEBUG] Creating user-specific TLS keystore for userId=" + currentUserId);
                manager = new CertificateManager(logger);
                keystoreStorage = new de.mendelson.util.security.cert.KeystoreStorageImplDB(
                    de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                    processing.getDBDriverManager(),
                    de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                    de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS,
                    currentUserId);
                logger.info("[DEBUG] Loading keystore certificates from DB...");
                manager.loadKeystoreCertificates(keystoreStorage);
                logger.info("[DEBUG] Keystore loaded successfully");
            } else {
                // User-specific sign/encrypt keystore
                logger.info("[DEBUG] Creating user-specific sign/encrypt keystore for userId=" + currentUserId);
                manager = new CertificateManager(logger);
                keystoreStorage = new de.mendelson.util.security.cert.KeystoreStorageImplDB(
                    de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                    processing.getDBDriverManager(),
                    de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN,
                    de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_PKCS12,
                    currentUserId);
                logger.info("[DEBUG] Loading keystore certificates from DB...");
                manager.loadKeystoreCertificates(keystoreStorage);
                logger.info("[DEBUG] Keystore loaded successfully");
            }

            if (manager == null) {
                logger.severe("[DEBUG] Certificate manager is null!");
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

            // Expert View: Key Extensions
            if (keyGenRequest.isExtensionSignEncrypt() || keyGenRequest.isExtensionTLS()) {
                values.setKeyExtension(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
            }

            // Expert View: TLS Extended Key Usage
            if (keyGenRequest.isExtensionTLS()) {
                KeyPurposeId[] extKeyUsage = new KeyPurposeId[]{
                    KeyPurposeId.id_kp_serverAuth,
                    KeyPurposeId.id_kp_clientAuth
                };
                values.setExtendedKeyExtension(new ExtendedKeyUsage(extKeyUsage));
            }

            // Expert View: Generate SKI
            if (keyGenRequest.isGenerateSKI()) {
                values.setGenerateSKI(true);
            }

            // Expert View: Subject Alternative Names
            if (keyGenRequest.getSubjectAlternativeNames() != null && !keyGenRequest.getSubjectAlternativeNames().isEmpty()) {
                String[] sanEntries = keyGenRequest.getSubjectAlternativeNames().split(",");
                for (String sanEntry : sanEntries) {
                    sanEntry = sanEntry.trim();
                    if (sanEntry.isEmpty()) {
                        continue;
                    }

                    try {
                        // Parse SAN format: DNS:example.com, IP:192.168.1.1, EMAIL:user@example.com
                        if (sanEntry.toUpperCase().startsWith("DNS:")) {
                            String dnsName = sanEntry.substring(4).trim();
                            values.addSubjectAlternativeName(new GeneralName(GeneralName.dNSName, dnsName));
                        } else if (sanEntry.toUpperCase().startsWith("IP:")) {
                            String ipAddress = sanEntry.substring(3).trim();
                            values.addSubjectAlternativeName(new GeneralName(GeneralName.iPAddress, ipAddress));
                        } else if (sanEntry.toUpperCase().startsWith("EMAIL:")) {
                            String email = sanEntry.substring(6).trim();
                            values.addSubjectAlternativeName(new GeneralName(GeneralName.rfc822Name, email));
                        }
                    } catch (Exception e) {
                        // Skip invalid SAN entries
                        System.err.println("Invalid SAN entry: " + sanEntry + " - " + e.getMessage());
                    }
                }
            }

            // Generate the key
            logger.info("[DEBUG] Generating key pair with KeyGenerator...");
            de.mendelson.util.security.keygeneration.KeyGenerator generator =
                    new de.mendelson.util.security.keygeneration.KeyGenerator();
            de.mendelson.util.security.keygeneration.KeyGenerationResult result =
                    generator.generateKeyPair(values);
            logger.info("[DEBUG] Key pair generated successfully");

            // Store in keystore
            logger.info("[DEBUG] Storing key entry in keystore with alias: " + keyGenRequest.getAlias());
            manager.getKeystore().setKeyEntry(
                    keyGenRequest.getAlias(),
                    result.getKeyPair().getPrivate(),
                    manager.getKeystorePass(),
                    new java.security.cert.Certificate[]{result.getCertificate()}
            );
            logger.info("[DEBUG] Key entry stored, now saving keystore...");
            manager.saveKeystore();
            logger.info("[DEBUG] Keystore saved successfully!");

            // Get certificate info for logging
            X509Certificate cert = (X509Certificate) result.getCertificate();
            String fingerprint = KeystoreCertificate.fingerprintBytesToStr(
                java.security.MessageDigest.getInstance("SHA-1").digest(cert.getEncoded())
            );
            logger.info("[DEBUG] Generated certificate details:");
            logger.info("[DEBUG]   - Alias: " + keyGenRequest.getAlias());
            logger.info("[DEBUG]   - Subject DN: " + cert.getSubjectX500Principal().getName());
            logger.info("[DEBUG]   - Fingerprint SHA-1: " + fingerprint);
            logger.info("[DEBUG]   - Valid from: " + cert.getNotBefore());
            logger.info("[DEBUG]   - Valid until: " + cert.getNotAfter());

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
     * Query parameter: keystoreType (sign|tls|ssl)
     *
     * Before deleting, checks if the certificate is in use by any partner
     */
    @DELETE
    @Path("/{alias}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCertificate(
            @Context SecurityContext securityContext,
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

            // Get current user's ID
            int currentUserId = getCurrentUserId(securityContext, processing);
            logger.info("[DELETE CERT] User ID: " + currentUserId + ", Username: " + securityContext.getUserPrincipal().getName()
                + ", Alias: " + alias + ", KeystoreType: " + keystoreType + ", Force: " + force);

            // Determine which certificate manager to use
            CertificateManager certManager;
            if ("ssl".equals(keystoreType)) {
                // System-wide SSL/TLS keystore
                logger.info("[DELETE CERT] Using system-wide SSL/TLS certificate manager (user_id=-1)");
                certManager = processing.getCertificateManagerTLS();
            } else if ("tls".equals(keystoreType)) {
                // User-specific TLS keystore
                logger.info("[DELETE CERT] Using user-specific TLS certificate manager for user_id=" + currentUserId);
                certManager = new CertificateManager(logger);
                de.mendelson.util.security.cert.KeystoreStorageImplDB keystoreStorage =
                    new de.mendelson.util.security.cert.KeystoreStorageImplDB(
                        de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                        processing.getDBDriverManager(),
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS,
                        currentUserId);
                certManager.loadKeystoreCertificates(keystoreStorage);
            } else {
                // User-specific sign/encrypt keystore - FIX: Create user-specific manager
                logger.info("[DELETE CERT] Creating user-specific sign/encrypt certificate manager for user_id=" + currentUserId);
                certManager = new CertificateManager(logger);
                de.mendelson.util.security.cert.KeystoreStorageImplDB keystoreStorage =
                    new de.mendelson.util.security.cert.KeystoreStorageImplDB(
                        de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                        processing.getDBDriverManager(),
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN,
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_PKCS12,
                        currentUserId);
                certManager.loadKeystoreCertificates(keystoreStorage);
            }

            // Find the certificate by alias
            KeystoreCertificate certificate = certManager.getKeystoreCertificate(alias);
            if (certificate == null) {
                logger.warning("[DELETE CERT] Certificate not found: alias='" + alias + "', keystoreType=" + keystoreType
                    + ", user_id=" + currentUserId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Certificate with alias '" + alias + "' not found"))
                        .build();
            }
            logger.info("[DELETE CERT] Certificate found: alias='" + alias + "', fingerprint=" + certificate.getFingerPrintSHA1()
                + ", isKeyPair=" + certificate.getIsKeyPair());

            // Check if certificate is in use by partners (only for sign/encrypt keystore)
            // Only check partners visible to the current user
            if ("sign".equals(keystoreType) && !force) {
                logger.info("[DELETE CERT] Checking if certificate is in use by current user's partners...");
                PartnerListRequest partnerRequest = new PartnerListRequest(PartnerListRequest.LIST_ALL);
                PartnerListResponse partnerResponse = processing.processPartnerListRequest(partnerRequest);
                List<Partner> allPartners = partnerResponse.getList();

                List<String> partnersUsing = new ArrayList<>();
                String certFingerprint = certificate.getFingerPrintSHA1();
                logger.info("[DELETE CERT] Certificate fingerprint to check: " + certFingerprint);

                // Filter partners to only those created by or visible to the current user
                for (Partner partner : allPartners) {
                    // Skip partners not created by current user (user isolation)
                    if (partner.getCreatedByUserId() != currentUserId) {
                        continue;
                    }

                    logger.fine("[DELETE CERT] Checking partner: " + partner.getName()
                        + " (id=" + partner.getDBId() + ", created_by=" + partner.getCreatedByUserId() + ")");

                    if (certFingerprint.equals(partner.getCryptFingerprintSHA1())) {
                        partnersUsing.add(partner.getName() + " (encryption)");
                        logger.info("[DELETE CERT] Found usage: " + partner.getName() + " (encryption)");
                    }
                    if (certFingerprint.equals(partner.getSignFingerprintSHA1())) {
                        partnersUsing.add(partner.getName() + " (signing)");
                        logger.info("[DELETE CERT] Found usage: " + partner.getName() + " (signing)");
                    }
                    if (certFingerprint.equals(partner.getCryptOverwriteLocalstationFingerprintSHA1())) {
                        partnersUsing.add(partner.getName() + " (encryption - overwrite local)");
                        logger.info("[DELETE CERT] Found usage: " + partner.getName() + " (encryption - overwrite local)");
                    }
                    if (certFingerprint.equals(partner.getSignOverwriteLocalstationFingerprintSHA1())) {
                        partnersUsing.add(partner.getName() + " (signing - overwrite local)");
                        logger.info("[DELETE CERT] Found usage: " + partner.getName() + " (signing - overwrite local)");
                    }
                }

                logger.info("[DELETE CERT] Certificate usage check complete. Found " + partnersUsing.size() + " usage(s)");

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
            logger.info("[DELETE CERT] Attempting to delete certificate: alias='" + alias + "', keystoreType=" + keystoreType
                + ", user_id=" + currentUserId);
            try {
                certManager.deleteKeystoreEntry(alias);
                logger.info("[DELETE CERT] Certificate deleted successfully: alias='" + alias + "'");
            } catch (Throwable t) {
                logger.severe("[DELETE CERT] Failed to delete certificate: alias='" + alias + "', error=" + t.getMessage());
                t.printStackTrace();
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
     * Request body: { keystoreType (sign|tls|ssl), certificateList }
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

            // Get current user's ID
            int currentUserId = getCurrentUserId(securityContext, processing);

            int keystoreUsage;
            int userId;

            if ("ssl".equals(uploadRequest.getKeystoreType())) {
                // System-wide SSL/TLS keystore
                keystoreUsage = UploadRequestKeystore.KEYSTORE_TYPE_TLS;
                userId = KeydataAccessDB.SYSTEM_WIDE_USER_ID;
            } else if ("tls".equals(uploadRequest.getKeystoreType())) {
                // User-specific TLS keystore
                keystoreUsage = UploadRequestKeystore.KEYSTORE_TYPE_TLS;
                userId = currentUserId;
            } else {
                // User-specific sign/encrypt keystore
                keystoreUsage = UploadRequestKeystore.KEYSTORE_TYPE_ENC_SIGN;
                userId = currentUserId;
            }

            UploadRequestKeystore request = new UploadRequestKeystore(keystoreUsage, userId);
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
     *   keystoreType: sign|tls|ssl
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

            // Get current user's ID
            int currentUserId = getCurrentUserId(securityContext, processing);

            // Get the appropriate certificate manager
            CertificateManager certManager;
            if ("ssl".equals(keystoreType)) {
                // System-wide SSL/TLS keystore
                certManager = processing.getCertificateManagerTLS();
            } else if ("tls".equals(keystoreType)) {
                // User-specific TLS keystore
                certManager = new CertificateManager(logger);
                de.mendelson.util.security.cert.KeystoreStorageImplDB keystoreStorage =
                    new de.mendelson.util.security.cert.KeystoreStorageImplDB(
                        de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                        processing.getDBDriverManager(),
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS,
                        currentUserId);
                certManager.loadKeystoreCertificates(keystoreStorage);
            } else {
                // User-specific sign/encrypt keystore
                certManager = new CertificateManager(logger);
                de.mendelson.util.security.cert.KeystoreStorageImplDB keystoreStorage =
                    new de.mendelson.util.security.cert.KeystoreStorageImplDB(
                        de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                        processing.getDBDriverManager(),
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN,
                        de.mendelson.util.security.cert.KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_PKCS12,
                        currentUserId);
                certManager.loadKeystoreCertificates(keystoreStorage);
            }

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

            // Check if certificate already exists by fingerprint (SHA-1)
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            byte[] fingerprintSHA1 = md.digest(x509Cert.getEncoded());
            de.mendelson.util.security.cert.KeystoreCertificate existingCert =
                certManager.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);

            if (existingCert != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorResponse("Certificate already exists with alias: " + existingCert.getAlias()))
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
            List<String> skippedAliases = new ArrayList<>();
            java.util.Enumeration<String> aliases = keystore.aliases();

            while (aliases.hasMoreElements()) {
                String entryAlias = aliases.nextElement();

                if (keystore.isKeyEntry(entryAlias)) {
                    // Import key pair with certificate chain
                    java.security.Key key = keystore.getKey(entryAlias, password.toCharArray());
                    java.security.cert.Certificate[] chain = keystore.getCertificateChain(entryAlias);

                    if (chain != null && chain.length > 0 && chain[0] instanceof java.security.cert.X509Certificate) {
                        java.security.cert.X509Certificate x509Cert = (java.security.cert.X509Certificate) chain[0];

                        // Check if certificate already exists by fingerprint
                        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
                        byte[] fingerprintSHA1 = md.digest(x509Cert.getEncoded());
                        de.mendelson.util.security.cert.KeystoreCertificate existingCert =
                            certManager.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);

                        if (existingCert != null) {
                            skippedAliases.add(entryAlias + " (already exists as " + existingCert.getAlias() + ")");
                            continue;
                        }

                        String targetAlias = (alias != null && !alias.isEmpty()) ? alias : entryAlias;
                        certManager.getKeystore().setKeyEntry(targetAlias, key,
                            certManager.getKeystorePass(), chain);
                        importedAliases.add(targetAlias);
                    }
                } else if (keystore.isCertificateEntry(entryAlias)) {
                    // Import certificate only
                    java.security.cert.Certificate cert = keystore.getCertificate(entryAlias);
                    if (cert instanceof java.security.cert.X509Certificate) {
                        java.security.cert.X509Certificate x509Cert = (java.security.cert.X509Certificate) cert;

                        // Check if certificate already exists by fingerprint
                        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
                        byte[] fingerprintSHA1 = md.digest(x509Cert.getEncoded());
                        de.mendelson.util.security.cert.KeystoreCertificate existingCert =
                            certManager.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);

                        if (existingCert != null) {
                            skippedAliases.add(entryAlias + " (already exists as " + existingCert.getAlias() + ")");
                            continue;
                        }

                        String targetAlias = (alias != null && !alias.isEmpty()) ? alias : entryAlias;
                        certManager.addCertificate(targetAlias, x509Cert);
                        importedAliases.add(targetAlias);
                    }
                }
            }

            certManager.saveKeystore();

            ImportCertificateResponseDTO response = new ImportCertificateResponseDTO();
            String message = "Keystore imported successfully. Imported " + importedAliases.size() + " entries";
            if (!skippedAliases.isEmpty()) {
                message += ". Skipped " + skippedAliases.size() + " duplicate entries";
            }
            response.setMessage(message);
            response.setImportedAliases(importedAliases);
            response.setSkippedAliases(skippedAliases);

            return Response.ok(response).build();

        } catch (Throwable e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Failed to import keystore: " + e.getMessage()))
                    .build();
        }
    }

    // Helper methods

    /**
     * Get current user's ID from security context
     */
    private int getCurrentUserId(SecurityContext securityContext, AS2ServerProcessing processing) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            de.mendelson.comm.as2.usermanagement.UserManagementAccessDB userMgmt =
                new de.mendelson.comm.as2.usermanagement.UserManagementAccessDB(
                    processing.getDBDriverManager(), null);
            de.mendelson.comm.as2.usermanagement.WebUIUser user = userMgmt.getUserByUsername(username);
            if (user != null) {
                return user.getId();
            }
        } catch (Exception e) {
            System.err.println("Error getting current user ID: " + e.getMessage());
        }
        return 0; // Fallback to admin/system
    }

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
        private String password;

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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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
        // Expert view fields
        private boolean extensionTLS = false;
        private boolean extensionSignEncrypt = false;
        private boolean generateSKI = false;
        private String subjectAlternativeNames;

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

        public boolean isExtensionTLS() {
            return extensionTLS;
        }

        public void setExtensionTLS(boolean extensionTLS) {
            this.extensionTLS = extensionTLS;
        }

        public boolean isExtensionSignEncrypt() {
            return extensionSignEncrypt;
        }

        public void setExtensionSignEncrypt(boolean extensionSignEncrypt) {
            this.extensionSignEncrypt = extensionSignEncrypt;
        }

        public boolean isGenerateSKI() {
            return generateSKI;
        }

        public void setGenerateSKI(boolean generateSKI) {
            this.generateSKI = generateSKI;
        }

        public String getSubjectAlternativeNames() {
            return subjectAlternativeNames;
        }

        public void setSubjectAlternativeNames(String subjectAlternativeNames) {
            this.subjectAlternativeNames = subjectAlternativeNames;
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

        private List<String> skippedAliases;

        public List<String> getSkippedAliases() {
            return skippedAliases;
        }

        public void setSkippedAliases(List<String> skippedAliases) {
            this.skippedAliases = skippedAliases;
        }
    }
}
