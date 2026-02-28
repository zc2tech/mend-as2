//$Header: /as2/de/mendelson/util/security/BouncyCastleProviderSingleton.java 2     20/04/23 15:00 Heller $
package de.mendelson.util.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Single instance of the BC provider to prevent memory leaks
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class BouncyCastleProviderSingleton {

    /**
     * The BouncyCastle crypto provider
     */
    private static BouncyCastleProvider bcProvider;

    /**
     * private: prevents external instantiation
     */
    private BouncyCastleProviderSingleton() {
    }

    /**
     * Returns a BouncyCastle provider instance.
     *
     * @return The BouncyCastle provider instance.
     */
    public static BouncyCastleProvider instance() {
        if (bcProvider != null) {
            return(bcProvider);
        } else {
            bcProvider = new BouncyCastleProvider();
            return(bcProvider);
        }
    }
}
