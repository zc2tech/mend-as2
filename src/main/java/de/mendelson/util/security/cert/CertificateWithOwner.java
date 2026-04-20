package de.mendelson.util.security.cert;

import java.io.Serializable;

/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

/**
 * Holds certificate information along with owner information
 * Used for admin view to display all users' certificates
 *
 * @author Julian Xu
 */
public class CertificateWithOwner implements Serializable {

    private static final long serialVersionUID = 1L;

    private KeystoreCertificate certificate;
    private int userId;
    private String username;

    public CertificateWithOwner(KeystoreCertificate certificate, int userId, String username) {
        this.certificate = certificate;
        this.userId = userId;
        this.username = username;
    }

    public KeystoreCertificate getCertificate() {
        return certificate;
    }

    public void setCertificate(KeystoreCertificate certificate) {
        this.certificate = certificate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "CertificateWithOwner{" +
                "alias=" + (certificate != null ? certificate.getAlias() : "null") +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                '}';
    }
}
