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

package de.mendelson.comm.as2.servlet.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Jackson mixin to control KeystoreCertificate serialization
 * Ignores fields that contain internal JDK classes or sensitive data
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class KeystoreCertificateMixin {

    @JsonIgnore
    abstract X509Certificate getX509Certificate();

    @JsonIgnore
    abstract PrivateKey getPrivateKey();

    @JsonIgnore
    abstract PublicKey getKey();

    @JsonIgnore
    abstract Certificate[] getCertificateChain();

    @JsonIgnore
    abstract PublicKey getPublicKey();
}
