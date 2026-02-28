//$Header: /oftp2/de/mendelson/util/mailautoconfig/gui/ResourceBundleMailAutoConfigurationDetection_de.java 5     9/12/24 15:50 Heller $ 
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
public class ResourceBundleMailAutoConfigurationDetection_de extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"button.ok", "Ausgewählte Konfiguration verwenden"},
        {"button.cancel", "Abbruch"},
        {"title", "Mail Server Einstellungen herausfinden"},
        {"label.mailaddress", "Mail Addresse" },
        {"button.start.detection", "Herausfinden" },
        {"header.service", "Service" },
        {"header.host", "Host" },
        {"header.port", "Port" },
        {"header.security", "Sicherheit" },
        {"progress.detection", "Finde Mailservereinstellungen heraus" },
        {"security." + MailServiceConfiguration.SECURITY_PLAIN, "Keine" },
        {"security." + MailServiceConfiguration.SECURITY_START_TLS, "StartTLS" },
        {"security." + MailServiceConfiguration.SECURITY_TLS, "TLS" },
        {"label.detectedprovider", "<HTML>Der erkannte Mail Anbieter ist <strong>{0}</strong></HTML>"},
        {"detection.failed.title", "Erkennung fehlgeschlagen" },
        {"detection.failed.text", "Das System konnte nicht die Mailservereinstellungen für die Mailaddresse {0} herausfinden." },
        {"label.email.hint", "Gültige Mailaddresse zum Hrausfinden der Servereinstellungen" },
        {"email.invalid.title", "Ungültige Addresse"},
        {"email.invalid.text", "Die Untersuchung wurde nicht durchgeführt - die Mailaddresse {0} ist ungültig."},
    };
}
