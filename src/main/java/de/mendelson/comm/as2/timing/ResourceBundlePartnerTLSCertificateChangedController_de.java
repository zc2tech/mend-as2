//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundlePartnerTLSCertificateChangedController_de.java 6     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.timing;
import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class ResourceBundlePartnerTLSCertificateChangedController_de extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {
        {"module.name", "TLS Zertifikat Prüfung" },
        {"import.success", "Das TLS Zertifikat \"{0}\" für den Partner [{1}] wurde automatisch importiert." },
        {"import.success.event.header", "Automatischer Import eines TLS Zertifikats" },
        {"import.success.event.body", "Das System ist so konfiguriert, dass es regelmässig über TLS angebundene Partner kontrolliert, "
            + "ob sie ihr TLS Zertifikat verändert haben. Ist dies der Fall und das TLS Zertifikat exisitert nicht in Ihrem lokalen "
            + "TLS Zertifikatmanager, wird es automatisch importiert.\n"
            + "Das System hat für den Partner \"{0}\" unter der URL \"{1}\" ein neues Zertifikat gefunden und "
            + "es mit dem Alias \"{2}\" erfolgreich "
            + "in Ihren TLS Zertifikatmanager importiert." },
        {"import.failed", "Das TLS Zertifikat für den Partner {0} konnte nicht automatisch importiert werden: {1}" },        
        {"autoimport.tls.check.started", "Der automatische Import von veränderten Partner TLS Zertifikaten wurde aktiviert."},
        {"autoimport.tls.check.stopped", "Der automatische Import von veränderten Partner TLS Zertifikaten wurde deaktiviert."},
    };
    
}