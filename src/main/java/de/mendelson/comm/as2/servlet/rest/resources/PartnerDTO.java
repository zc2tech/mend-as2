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

/**
 * Data Transfer Object for Partner REST API
 * Simplifies JSON serialization/deserialization by including only essential fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerDTO {
    private String name;

    @JsonProperty("as2Identification")
    private String as2Identification;

    private String url;

    @JsonProperty("mdnURL")
    private String mdnURL;

    private String email;
    private String subject;
    private String contentType;
    private boolean localStation;
    private String comment;

    // Default constructor for Jackson
    public PartnerDTO() {
    }

    // Constructor from Partner object
    public PartnerDTO(Partner partner) {
        this.name = partner.getName();
        this.as2Identification = partner.getAS2Identification();
        this.url = partner.getURL();
        this.mdnURL = partner.getMdnURL();
        this.email = partner.getEmail();
        this.subject = partner.getSubject();
        this.contentType = partner.getContentType();
        this.localStation = partner.isLocalStation();
        this.comment = partner.getComment();
    }

    /**
     * Convert DTO to Partner object with all required defaults
     */
    public Partner toPartner() {
        Partner partner = new Partner();
        partner.setName(this.name);
        partner.setAS2Identification(this.as2Identification);
        partner.setLocalStation(this.localStation);

        // Set optional fields if provided, otherwise keep defaults
        if (this.url != null && !this.url.isEmpty()) {
            partner.setURL(this.url);
        }
        if (this.mdnURL != null && !this.mdnURL.isEmpty()) {
            partner.setMdnURL(this.mdnURL);
        }
        if (this.email != null && !this.email.isEmpty()) {
            partner.setEmail(this.email);
        }
        if (this.subject != null && !this.subject.isEmpty()) {
            partner.setSubject(this.subject);
        }
        if (this.contentType != null && !this.contentType.isEmpty()) {
            partner.setContentType(this.contentType);
        }
        if (this.comment != null && !this.comment.isEmpty()) {
            partner.setComment(this.comment);
        }

        return partner;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAs2Identification() {
        return as2Identification;
    }

    public void setAs2Identification(String as2Identification) {
        this.as2Identification = as2Identification;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMdnURL() {
        return mdnURL;
    }

    public void setMdnURL(String mdnURL) {
        this.mdnURL = mdnURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isLocalStation() {
        return localStation;
    }

    public void setLocalStation(boolean localStation) {
        this.localStation = localStation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
