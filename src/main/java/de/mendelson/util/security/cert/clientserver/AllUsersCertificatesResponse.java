package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.security.cert.CertificateWithOwner;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

/**
 * Response containing all users' certificates with owner information
 *
 * @author Julian Xu
 */
public class AllUsersCertificatesResponse extends ClientServerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CertificateWithOwner> certificates = new ArrayList<>();

    public AllUsersCertificatesResponse(AllUsersCertificatesRequest request) {
        super(request);
    }

    public List<CertificateWithOwner> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<CertificateWithOwner> certificates) {
        this.certificates = certificates;
    }

    @Override
    public String toString() {
        return "AllUsersCertificatesResponse{certificateCount=" +
               (certificates != null ? certificates.size() : 0) + "}";
    }
}
