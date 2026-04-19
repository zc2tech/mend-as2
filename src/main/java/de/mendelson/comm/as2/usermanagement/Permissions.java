package de.mendelson.comm.as2.usermanagement;

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
 * Centralized permission constants for the AS2 system
 * These permissions control access to features in both SwingUI and WebUI
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class Permissions {

    // Partners
    public static final String PARTNER_READ = "PARTNER_READ";
    public static final String PARTNER_WRITE = "PARTNER_WRITE";

    // Certificates - Sign/Crypt
    public static final String CERT_READ = "CERT_READ";
    public static final String CERT_WRITE = "CERT_WRITE";

    // Certificates - TLS
    public static final String CERT_TLS_READ = "CERT_TLS_READ";
    public static final String CERT_TLS_WRITE = "CERT_TLS_WRITE";

    // Messages
    public static final String MESSAGE_READ = "MESSAGE_READ";
    public static final String MESSAGE_WRITE = "MESSAGE_WRITE";

    // System Configuration
    public static final String SYSTEM_CONFIG_CONNECTIVITY = "SYSTEM_CONFIG_CONNECTIVITY";
    public static final String SYSTEM_CONFIG_INBOUND_AUTH = "SYSTEM_CONFIG_INBOUND_AUTH";
    public static final String SYSTEM_CONFIG_DIRECTORIES = "SYSTEM_CONFIG_DIRECTORIES";
    public static final String SYSTEM_CONFIG_MAINTENANCE = "SYSTEM_CONFIG_MAINTENANCE";
    public static final String SYSTEM_CONFIG_NOTIFICATIONS = "SYSTEM_CONFIG_NOTIFICATIONS";
    public static final String SYSTEM_CONFIG_INTERFACE = "SYSTEM_CONFIG_INTERFACE";
    public static final String SYSTEM_CONFIG_LOGGING = "SYSTEM_CONFIG_LOGGING";
    public static final String SYSTEM_CONFIG_IP_WHITELIST = "SYSTEM_CONFIG_IP_WHITELIST";

    // System Monitoring
    public static final String SYSTEM_INFO_READ = "SYSTEM_INFO_READ";
    public static final String SYSTEM_EVENTS_READ = "SYSTEM_EVENTS_READ";
    public static final String SYSTEM_LOGS_READ = "SYSTEM_LOGS_READ";

    // Tracker
    public static final String TRACKER_CONFIG_READ = "TRACKER_CONFIG_READ";
    public static final String TRACKER_CONFIG_WRITE = "TRACKER_CONFIG_WRITE";
    public static final String TRACKER_MESSAGE_READ = "TRACKER_MESSAGE_READ";

    // User Management
    public static final String USER_MANAGE = "USER_MANAGE";
    public static final String USER_SWITCH = "USER_SWITCH";

    /**
     * Private constructor to prevent instantiation
     */
    private Permissions() {
    }
}
