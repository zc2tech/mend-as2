package de.mendelson.util.mailautoconfig;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.mailautoconfig.gui.ResourceBundleMailAutoConfigurationDetection;
import de.mendelson.util.systemevents.notification.NotificationData;
import java.io.Serializable;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Container object to transport a single mail service configuration
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class MailServiceConfiguration implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public static final String SERVICE_POP3 = "pop3";
    public static final String SERVICE_SMTP = "smtp";
    public static final String SERVICE_IMAP = "imap";
    public static final String SERVICE_EXCHANGE = "exchange";

    public static final int SECURITY_PLAIN = NotificationData.SECURITY_PLAIN;
    public static final int SECURITY_START_TLS = NotificationData.SECURITY_START_TLS;
    public static final int SECURITY_TLS = NotificationData.SECURITY_TLS;

    private final int port;
    private final String service;
    private final int security;
    private final String mailProviderLongName;
    private final String serverhost;
    

    public MailServiceConfiguration(String service, String serverhost, int port, int security, String mailProviderLongName) {
        this.service = service;
        this.port = port;
        this.security = security;
        this.serverhost = serverhost;
        this.mailProviderLongName = mailProviderLongName;
    }

    /**
     * Overwrite the equal method of object
     *
     * @param anObject object to compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof MailServiceConfiguration) {
            MailServiceConfiguration entry = (MailServiceConfiguration) anObject;
            return (entry.getService().equals(this.getService())
                    && entry.getSecurity() == this.getSecurity()
                    && entry.getPort() == this.getPort());
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.getPort();
        hash = 97 * hash + Objects.hashCode(this.getService());
        hash = 97 * hash + this.getSecurity();
        return hash;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @return the security
     */
    public int getSecurity() {
        return security;
    }

    public String getSecurityAsString(){
        //load resource bundle
        MecResourceBundle rb;
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMailAutoConfigurationDetection.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        return( rb.getResourceString( "security." + this.security));
    }
    
    
    /**
     * @return the mailProviderLongName
     */
    public String getMailProviderLongName() {
        return mailProviderLongName;
    }

    /**
     * @return the serverhost
     */
    public String getServerHost() {
        return serverhost;
    }
    
    public String toDebugDisplay(){
        return( "Provider=" + this.mailProviderLongName 
                + ";Service=" + this.service 
                + "; Host=" + this.serverhost
                + "; Port=" + this.port
                + "; Security=" + this.security);
    }

}
