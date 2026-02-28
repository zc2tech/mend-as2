//$Header: /as2/de/mendelson/util/systemevents/notification/NotificationData.java 12    2/11/23 15:53 Heller $
package de.mendelson.util.systemevents.notification;

import de.mendelson.util.oauth2.OAuth2Config;
import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores the notification data for the mendelson products
 * @author S.Heller
 * @version $Revision: 12 $
 */
public abstract class NotificationData implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public static final int SECURITY_PLAIN = 0;
    public static final int SECURITY_START_TLS = 1;
    public static final int SECURITY_TLS = 2;
    
    /**The mail server host*/
    public abstract String getMailServer();
    /**The mail server port, depends on the security level*/
    public abstract int getMailServerPort();
    /**The connection security, one of SECURITY_PLAIN, SECURITY_START_TLS, SECURITY_TLS*/
    public abstract int getConnectionSecurity();
    /**Returns if this is a user/password login*/
    public abstract boolean usesSMTPAuthCredentials();
    /**Returns if this is a OAuth2 authorization*/
    public abstract boolean usesSMTPAuthOAuth2();
    /**The user name for a SMTP login using credentials*/
    public abstract String getSMTPUser();
    /**The password for a SMTP login using credentials*/
    public abstract char[] getSMTPPass();
    /**The replyTo address for the mail - mainly this is a mail where a reply makes no sense*/
    public abstract String getReplyTo();
    /**The receiver mail address or the list of receivers, comma separated*/
    public abstract String getNotificationMail();
    /**Allows to bundle multiple mails in a single one - this prevents mail flooding*/
    public abstract int getMaxNotificationsPerMin();
    /**Contains the OAuth2 authorization data for this notification*/
    public abstract OAuth2Config getOAuth2Config();
    
}
