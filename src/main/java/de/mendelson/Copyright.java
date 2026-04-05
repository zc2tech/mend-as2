package de.mendelson;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
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
