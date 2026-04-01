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
 * @author S.Heller
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
