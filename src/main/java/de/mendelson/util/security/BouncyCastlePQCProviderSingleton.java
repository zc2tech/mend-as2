package de.mendelson.util.security;

import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Single instance of the BCPQC provider to prevent memory leaks
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class BouncyCastlePQCProviderSingleton {

    /**
     * The BouncyCastle crypto provider
     */
    private static BouncyCastlePQCProvider bcProvider;

    /**
     * private: prevents external instantiation
     */
    private BouncyCastlePQCProviderSingleton() {
    }

    /**
     * Returns a BouncyCastle PQC provider instance.
     *
     * @return The BouncyCastle PQC provider instance.
     */
    public static BouncyCastlePQCProvider instance() {
        if (bcProvider != null) {
            return(bcProvider);
        } else {
            bcProvider = new BouncyCastlePQCProvider();
            return(bcProvider);
        }
    }
}
