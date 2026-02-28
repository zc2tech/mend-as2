//$Header: /as2/de/mendelson/util/mailautoconfig/gui/ResourceBundleMailAutoConfigurationDetection.java 5     2/11/23 15:53 Heller $ 
package de.mendelson.util.mailautoconfig.gui;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.mailautoconfig.MailServiceConfiguration;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ResourceBundleMailAutoConfigurationDetection extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"button.ok", "Use selected configuration"},
        {"button.cancel", "Cancel"},
        {"title", "Auto detect mail server settings"},
        {"label.mailaddress", "Mail address" },
        {"button.start.detection", "Detect" },
        {"header.service", "Service" },
        {"header.host", "Host" },
        {"header.port", "Port" },
        {"header.security", "Security" },
        {"progress.detection", "Detecting mail server settings" },
        {"security." + MailServiceConfiguration.SECURITY_PLAIN, "None" },
        {"security." + MailServiceConfiguration.SECURITY_START_TLS, "StartTLS" },
        {"security." + MailServiceConfiguration.SECURITY_TLS, "TLS" },
        {"label.detectedprovider", "<HTML>The detected mail provider is <strong>{0}</strong></HTML>"},
        {"detection.failed.title", "Server detection failed" },
        {"detection.failed.text", "The system was unable to detect the mail server configuration for the mail address {0}." },
        {"label.email.hint", "Enter a valid mail address to detect the related mailservers settings" },
        {"email.invalid.title", "Invalid address"},
        {"email.invalid.text", "No detection performed - the entered email address {0} is invalid."},
    };
}
