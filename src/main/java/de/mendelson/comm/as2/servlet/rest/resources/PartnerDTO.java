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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.HTTPAuthentication;
import de.mendelson.comm.as2.partner.PartnerInboundAuthCredential;
import java.util.List;
import java.util.ArrayList;

/**
 * Data Transfer Object for Partner REST API
 * Includes all partner configuration fields for complete CRUD operations
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerDTO {
    // Database ID (for updates)
    private int dbid;

    // General tab
    private String name;

    @JsonProperty("as2Identification")
    private String as2Identification;

    private boolean localStation;
    private String comment;

    // Send tab
    private String url;
    private String subject;
    private String contentType;
    private String email;
    private int encryptionType;
    private int signType;
    private int compressionType;

    // Receive/MDN tab
    @JsonProperty("mdnURL")
    private String mdnURL;

    private boolean syncMDN;
    private boolean signedMDN;

    // Security tab
    private String signFingerprintSHA1;
    private String cryptFingerprintSHA1;
    private boolean overwriteLocalStationSecurity;
    private String signOverwriteLocalstationFingerprintSHA1;
    private String cryptOverwriteLocalstationFingerprintSHA1;
    private boolean useAlgorithmIdentifierProtectionAttribute;

    // Directory Poll tab
    private boolean enableDirPoll;
    private int pollInterval;
    private int maxPollFiles;
    private String pollIgnoreListAsString;
    private boolean keepFilenameOnReceipt;

    // HTTP tab
    private String httpProtocolVersion;
    private int contentTransferEncoding;

    // HTTP Authentication tab
    @JsonProperty("authenticationCredentialsMessage")
    private HTTPAuthentication authenticationCredentialsMessage;

    @JsonProperty("authenticationCredentialsAsyncMDN")
    private HTTPAuthentication authenticationCredentialsAsyncMDN;

    // Inbound Authentication (for local stations only) - supports multiple credentials
    @JsonProperty("inboundAuthCredentialsList")
    private List<PartnerInboundAuthCredential> inboundAuthCredentialsList = new ArrayList<>();

    @JsonProperty("inboundAuthBasicEnabled")
    private boolean inboundAuthBasicEnabled = false;

    @JsonProperty("inboundAuthCertEnabled")
    private boolean inboundAuthCertEnabled = false;

    // Contact tab
    private String contactAS2;
    private String contactCompany;

    // Notification settings
    private int notifySend;
    private int notifyReceive;
    private int notifySendReceive;
    private boolean notifySendEnabled;
    private boolean notifyReceiveEnabled;
    private boolean notifySendReceiveEnabled;

    // Ownership tracking
    private int createdByUserId;
    private String createdByUsername;

    // Default constructor for Jackson
    public PartnerDTO() {
    }

    // Constructor from Partner object
    public PartnerDTO(Partner partner) {
        this.dbid = partner.getDBId();
        this.name = partner.getName();
        this.as2Identification = partner.getAS2Identification();
        this.localStation = partner.isLocalStation();
        this.comment = partner.getComment();

        this.url = partner.getURL();
        this.subject = partner.getSubject();
        this.contentType = partner.getContentType();
        this.email = partner.getEmail();
        this.encryptionType = partner.getEncryptionType();
        this.signType = partner.getSignType();
        this.compressionType = partner.getCompressionType();

        this.mdnURL = partner.getMdnURL();
        this.syncMDN = partner.isSyncMDN();
        this.signedMDN = partner.isSignedMDN();

        this.signFingerprintSHA1 = partner.getSignFingerprintSHA1();
        this.cryptFingerprintSHA1 = partner.getCryptFingerprintSHA1();
        this.overwriteLocalStationSecurity = partner.isOverwriteLocalStationSecurity();
        this.signOverwriteLocalstationFingerprintSHA1 = partner.getSignOverwriteLocalstationFingerprintSHA1();
        this.cryptOverwriteLocalstationFingerprintSHA1 = partner.getCryptOverwriteLocalstationFingerprintSHA1();
        this.useAlgorithmIdentifierProtectionAttribute = partner.getUseAlgorithmIdentifierProtectionAttribute();

        this.enableDirPoll = partner.isEnableDirPoll();
        this.pollInterval = partner.getPollInterval();
        this.maxPollFiles = partner.getMaxPollFiles();
        this.pollIgnoreListAsString = partner.getPollIgnoreListAsString();
        this.keepFilenameOnReceipt = partner.getKeepOriginalFilenameOnReceipt();

        this.httpProtocolVersion = partner.getHttpProtocolVersion();
        this.contentTransferEncoding = partner.getContentTransferEncoding();

        this.authenticationCredentialsMessage = partner.getAuthenticationCredentialsMessage();
        this.authenticationCredentialsAsyncMDN = partner.getAuthenticationCredentialsAsyncMDN();

        // Only include inbound auth credentials list for local stations
        if (partner.isLocalStation()) {
            this.inboundAuthCredentialsList = partner.getInboundAuthCredentialsList();
            this.inboundAuthBasicEnabled = partner.isInboundAuthBasicEnabled();
            this.inboundAuthCertEnabled = partner.isInboundAuthCertEnabled();
        }

        this.contactAS2 = partner.getContactAS2();
        this.contactCompany = partner.getContactCompany();

        this.createdByUserId = partner.getCreatedByUserId();
        // createdByUsername will be set by PartnerResource when needed
    }

    /**
     * Convert DTO to Partner object with all fields
     */
    public Partner toPartner() {
        Partner partner = new Partner();

        // General tab
        partner.setName(this.name);
        partner.setAS2Identification(this.as2Identification);
        partner.setLocalStation(this.localStation);
        if (this.comment != null) {
            partner.setComment(this.comment);
        }

        // Send tab
        if (this.url != null && !this.url.isEmpty()) {
            partner.setURL(this.url);
        }
        if (this.subject != null) {
            partner.setSubject(this.subject);
        }
        if (this.contentType != null) {
            partner.setContentType(this.contentType);
        }
        if (this.email != null) {
            partner.setEmail(this.email);
        }
        partner.setEncryptionType(this.encryptionType);
        partner.setSignType(this.signType);
        partner.setCompressionType(this.compressionType);

        // Receive/MDN tab
        if (this.mdnURL != null && !this.mdnURL.isEmpty()) {
            partner.setMdnURL(this.mdnURL);
        }
        partner.setSyncMDN(this.syncMDN);
        partner.setSignedMDN(this.signedMDN);

        // Security tab
        if (this.signFingerprintSHA1 != null) {
            partner.setSignFingerprintSHA1(this.signFingerprintSHA1);
        }
        if (this.cryptFingerprintSHA1 != null) {
            partner.setCryptFingerprintSHA1(this.cryptFingerprintSHA1);
        }
        partner.setOverwriteLocalStationSecurity(this.overwriteLocalStationSecurity);
        if (this.signOverwriteLocalstationFingerprintSHA1 != null) {
            partner.setSignOverwriteLocalstationFingerprintSHA1(this.signOverwriteLocalstationFingerprintSHA1);
        }
        if (this.cryptOverwriteLocalstationFingerprintSHA1 != null) {
            partner.setCryptOverwriteLocalstationFingerprintSHA1(this.cryptOverwriteLocalstationFingerprintSHA1);
        }
        partner.setUseAlgorithmIdentifierProtectionAttribute(this.useAlgorithmIdentifierProtectionAttribute);

        // Directory Poll tab
        partner.setEnableDirPoll(this.enableDirPoll);
        partner.setPollInterval(this.pollInterval);
        partner.setMaxPollFiles(this.maxPollFiles);
        if (this.pollIgnoreListAsString != null) {
            partner.setPollIgnoreListString(this.pollIgnoreListAsString);
        }
        partner.setKeepOriginalFilenameOnReceipt(this.keepFilenameOnReceipt);

        // HTTP tab
        if (this.httpProtocolVersion != null) {
            partner.setHttpProtocolVersion(this.httpProtocolVersion);
        }
        partner.setContentTransferEncoding(this.contentTransferEncoding);

        // HTTP Authentication tab
        if (this.authenticationCredentialsMessage != null) {
            partner.setAuthentication(this.authenticationCredentialsMessage);
        }
        if (this.authenticationCredentialsAsyncMDN != null) {
            partner.setAuthenticationAsyncMDN(this.authenticationCredentialsAsyncMDN);
        }

        // Inbound Auth credentials list (for local stations only)
        if (partner.isLocalStation() && this.inboundAuthCredentialsList != null && !this.inboundAuthCredentialsList.isEmpty()) {
            partner.setInboundAuthCredentialsList(this.inboundAuthCredentialsList);
        }

        // Inbound Auth enable flags (for local stations only)
        if (partner.isLocalStation()) {
            partner.setInboundAuthBasicEnabled(this.inboundAuthBasicEnabled);
            partner.setInboundAuthCertEnabled(this.inboundAuthCertEnabled);
        }

        // Contact tab
        if (this.contactAS2 != null) {
            partner.setContactAS2(this.contactAS2);
        }
        if (this.contactCompany != null) {
            partner.setContactCompany(this.contactCompany);
        }

        return partner;
    }

    // Getters and setters
    public int getDbid() { return dbid; }
    public void setDbid(int dbid) { this.dbid = dbid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAs2Identification() { return as2Identification; }
    public void setAs2Identification(String as2Identification) { this.as2Identification = as2Identification; }

    public boolean isLocalStation() { return localStation; }
    public void setLocalStation(boolean localStation) { this.localStation = localStation; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getEncryptionType() { return encryptionType; }
    public void setEncryptionType(int encryptionType) { this.encryptionType = encryptionType; }

    public int getSignType() { return signType; }
    public void setSignType(int signType) { this.signType = signType; }

    public int getCompressionType() { return compressionType; }
    public void setCompressionType(int compressionType) { this.compressionType = compressionType; }

    public String getMdnURL() { return mdnURL; }
    public void setMdnURL(String mdnURL) { this.mdnURL = mdnURL; }

    public boolean isSyncMDN() { return syncMDN; }
    public void setSyncMDN(boolean syncMDN) { this.syncMDN = syncMDN; }

    public boolean isSignedMDN() { return signedMDN; }
    public void setSignedMDN(boolean signedMDN) { this.signedMDN = signedMDN; }

    public String getSignFingerprintSHA1() { return signFingerprintSHA1; }
    public void setSignFingerprintSHA1(String signFingerprintSHA1) { this.signFingerprintSHA1 = signFingerprintSHA1; }

    public String getCryptFingerprintSHA1() { return cryptFingerprintSHA1; }
    public void setCryptFingerprintSHA1(String cryptFingerprintSHA1) { this.cryptFingerprintSHA1 = cryptFingerprintSHA1; }

    public boolean isOverwriteLocalStationSecurity() { return overwriteLocalStationSecurity; }
    public void setOverwriteLocalStationSecurity(boolean overwriteLocalStationSecurity) { this.overwriteLocalStationSecurity = overwriteLocalStationSecurity; }

    public String getSignOverwriteLocalstationFingerprintSHA1() { return signOverwriteLocalstationFingerprintSHA1; }
    public void setSignOverwriteLocalstationFingerprintSHA1(String signOverwriteLocalstationFingerprintSHA1) { this.signOverwriteLocalstationFingerprintSHA1 = signOverwriteLocalstationFingerprintSHA1; }

    public String getCryptOverwriteLocalstationFingerprintSHA1() { return cryptOverwriteLocalstationFingerprintSHA1; }
    public void setCryptOverwriteLocalstationFingerprintSHA1(String cryptOverwriteLocalstationFingerprintSHA1) { this.cryptOverwriteLocalstationFingerprintSHA1 = cryptOverwriteLocalstationFingerprintSHA1; }

    public boolean isUseAlgorithmIdentifierProtectionAttribute() { return useAlgorithmIdentifierProtectionAttribute; }
    public void setUseAlgorithmIdentifierProtectionAttribute(boolean useAlgorithmIdentifierProtectionAttribute) { this.useAlgorithmIdentifierProtectionAttribute = useAlgorithmIdentifierProtectionAttribute; }

    public boolean isEnableDirPoll() { return enableDirPoll; }
    public void setEnableDirPoll(boolean enableDirPoll) { this.enableDirPoll = enableDirPoll; }

    public int getPollInterval() { return pollInterval; }
    public void setPollInterval(int pollInterval) { this.pollInterval = pollInterval; }

    public int getMaxPollFiles() { return maxPollFiles; }
    public void setMaxPollFiles(int maxPollFiles) { this.maxPollFiles = maxPollFiles; }

    public String getPollIgnoreListAsString() { return pollIgnoreListAsString; }
    public void setPollIgnoreListAsString(String pollIgnoreListAsString) { this.pollIgnoreListAsString = pollIgnoreListAsString; }

    public boolean isKeepFilenameOnReceipt() { return keepFilenameOnReceipt; }
    public void setKeepFilenameOnReceipt(boolean keepFilenameOnReceipt) { this.keepFilenameOnReceipt = keepFilenameOnReceipt; }

    public String getHttpProtocolVersion() { return httpProtocolVersion; }
    public void setHttpProtocolVersion(String httpProtocolVersion) { this.httpProtocolVersion = httpProtocolVersion; }

    public int getContentTransferEncoding() { return contentTransferEncoding; }
    public void setContentTransferEncoding(int contentTransferEncoding) { this.contentTransferEncoding = contentTransferEncoding; }

    public HTTPAuthentication getAuthenticationCredentialsMessage() { return authenticationCredentialsMessage; }
    public void setAuthenticationCredentialsMessage(HTTPAuthentication authenticationCredentialsMessage) { this.authenticationCredentialsMessage = authenticationCredentialsMessage; }

    public HTTPAuthentication getAuthenticationCredentialsAsyncMDN() { return authenticationCredentialsAsyncMDN; }
    public void setAuthenticationCredentialsAsyncMDN(HTTPAuthentication authenticationCredentialsAsyncMDN) { this.authenticationCredentialsAsyncMDN = authenticationCredentialsAsyncMDN; }

    public String getContactAS2() { return contactAS2; }
    public void setContactAS2(String contactAS2) { this.contactAS2 = contactAS2; }

    public String getContactCompany() { return contactCompany; }
    public void setContactCompany(String contactCompany) { this.contactCompany = contactCompany; }

    public int getNotifySend() { return notifySend; }
    public void setNotifySend(int notifySend) { this.notifySend = notifySend; }

    public int getNotifyReceive() { return notifyReceive; }
    public void setNotifyReceive(int notifyReceive) { this.notifyReceive = notifyReceive; }

    public int getNotifySendReceive() { return notifySendReceive; }
    public void setNotifySendReceive(int notifySendReceive) { this.notifySendReceive = notifySendReceive; }

    public boolean isNotifySendEnabled() { return notifySendEnabled; }
    public void setNotifySendEnabled(boolean notifySendEnabled) { this.notifySendEnabled = notifySendEnabled; }

    public boolean isNotifyReceiveEnabled() { return notifyReceiveEnabled; }
    public void setNotifyReceiveEnabled(boolean notifyReceiveEnabled) { this.notifyReceiveEnabled = notifyReceiveEnabled; }

    public boolean isNotifySendReceiveEnabled() { return notifySendReceiveEnabled; }
    public void setNotifySendReceiveEnabled(boolean notifySendReceiveEnabled) { this.notifySendReceiveEnabled = notifySendReceiveEnabled; }

    public List<PartnerInboundAuthCredential> getInboundAuthCredentialsList() { return inboundAuthCredentialsList; }
    public void setInboundAuthCredentialsList(List<PartnerInboundAuthCredential> inboundAuthCredentialsList) { this.inboundAuthCredentialsList = inboundAuthCredentialsList; }

    public boolean isInboundAuthBasicEnabled() { return inboundAuthBasicEnabled; }
    public void setInboundAuthBasicEnabled(boolean inboundAuthBasicEnabled) { this.inboundAuthBasicEnabled = inboundAuthBasicEnabled; }

    public boolean isInboundAuthCertEnabled() { return inboundAuthCertEnabled; }
    public void setInboundAuthCertEnabled(boolean inboundAuthCertEnabled) { this.inboundAuthCertEnabled = inboundAuthCertEnabled; }

    public int getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(int createdByUserId) { this.createdByUserId = createdByUserId; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
}
