//$Header: /as2/de/mendelson/Copyright.java 25    11/02/25 13:39 Heller $
package de.mendelson;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Show information about the copyright message for all products of
 * mendelson-e-commerce GmbH
 * 
 * @author S.Heller
 * @version $Revision: 25 $
 */
public class Copyright {

    private Copyright() {
    }

    /** Original upstream copyright */
    public static String getUpstreamCopyright() {
        return "(c) 2000-2025 mendelson-e-commerce GmbH Berlin, Germany";
    }

    /** Your fork's notice (use current year dynamically if you want) */
    public static String getForkCopyright() {
        return "(c) 2026 Xu, Julian (julian.xu@aliyun.com)";
    }

    /** What you actually display in About/CLI banners */
    public static String getCopyrightMessage() {
        return getUpstreamCopyright() + " | " + getForkCopyright();
    }

}
