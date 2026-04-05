package de.mendelson.util.security;

import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Single instance of the BC Jsse provider to prevent memory leaks
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class BouncyCastleJsseProviderSingleton {

    /**
     * The BouncyCastle JSSE provider
     */
    private static BouncyCastleJsseProvider bcProvider;

    /**
     * private: prevents external instantiation
     */
    private BouncyCastleJsseProviderSingleton() {
    }

    /**
     * Returns a BouncyCastle provider instance.
     *
     * @return The BouncyCastle provider instance.
     */
    public static BouncyCastleJsseProvider instance() {
        if (bcProvider != null) {
            return(bcProvider);
        } else {
            bcProvider = new BouncyCastleJsseProvider();
            return(bcProvider);
        }
    }
}
