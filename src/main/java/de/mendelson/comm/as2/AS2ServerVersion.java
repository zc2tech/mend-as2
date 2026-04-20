package de.mendelson.comm.as2;

import de.mendelson.Copyright;
import de.mendelson.IProductVersion;

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
 * Class to check the version and the build of the AS2 server
 *
 * @author S.Heller
 * @version $Revision: 94 $
 */
public class AS2ServerVersion implements IProductVersion {

    /**
     * Returns the used user agent
     */
    public static String getUserAgent() {
        StringBuilder agent = new StringBuilder();
        agent.append(AS2ServerVersion.getProductName()).append(" ");
        agent.append(AS2ServerVersion.getVersion()).append(" ");
        agent.append(AS2ServerVersion.getBuild());
        return (agent.toString());
    }

    /**
     * Returns this products version number
     */
    public static String getVersion() {
        return ("1.0");
    }

    /**
     * Returns the internal build number
     */
    public static String getBuild() {
        return ("build " + getBuildNo());
    }

    /**
     * Returns the internal build number
     */
    public static int getBuildNo() {
        // version of source
        return (1);
    }

    /**
     * returns the name of this product
     */
    public static String getProductName() {
        return ("mend-as2");
    }

    /**
     * The data base has a version. It is found in the column actualversion of
     * the table version. If the found version does not match the version
     * defined here, an auto update of the database is performed.
     */
    public static int getRequiredDBVersionConfig() {
        return (0);
    }

    /**
     * The data base has a version. It is found in the column actualversion of
     * the table version. If the found version does not match the version
     * defined here, an auto update of the database is performed.
     */
    public static int getRequiredDBVersionRuntime() {
        return (0);
    }

    /**
     * returns the short name of this product, return the full name if not such
     * short name exists!
     */
    public static String getProductNameShortcut() {
        return ("mend-as2");
    }

    /**
     * Returns the date the package was last modified
     */
    public static String getLastModificationDate() {
        String fullDate = "$Date: 20/03/25 14:45 $";
        return (fullDate.substring(fullDate.indexOf(":") + 1, fullDate.lastIndexOf("$")));
    }

    /**
     * Gets the copyright message for this product
     */
    public static String getCopyrightMessage() {
        return (Copyright.getCopyrightMessage());
    }

    /**
     * Gets the company name
     */
    public static String getCompany() {
        return ("mendelson-e-commerce GmbH");
    }

    /**
     * Gets the company address
     */
    public static String getStreet() {
        return ("Kurfürstendamm 30");
    }

    /**
     * Gets the company zip
     */
    public static String getZip() {
        return ("D-10719 Berlin");
    }

    /**
     * Gets the company info email
     */
    public static String getInfoEmail() {
        return ("service@mendelson.de");
    }

    /**
     * Returns the full name with build, version etc
     */
    public static String getFullProductName() {
        StringBuilder builder = new StringBuilder();
        builder.append(AS2ServerVersion.getProductName())
                .append(" ")
                .append(AS2ServerVersion.getVersion())
                .append(" ")
                .append(AS2ServerVersion.getBuild());
        return (builder.toString());
    }

    /**
     * Makes this a IProductVersion
     */
    @Override
    public String getFullName() {
        return (AS2ServerVersion.getFullProductName());
    }
}
