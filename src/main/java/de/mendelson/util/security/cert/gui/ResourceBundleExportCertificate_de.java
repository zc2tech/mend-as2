//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportCertificate_de.java 16    9/12/24 15:51 Heller $ 
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.cert.KeystoreCertificate;

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
 * @version $Revision: 16 $
 */
public class ResourceBundleExportCertificate_de extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"button.ok", "Ok"},
        {"button.cancel", "Abbrechen"},
        {"button.browse", "Durchsuchen"},
        {"title", "X.509 Zertifikat exportieren"},
        {"label.exportfile", "Dateiname"},
        {"label.exportfile.hint", "Zertifikatsdatei, die generiert wird"},
        {"label.alias", "Alias"},
        {"label.exportformat", "Format"},
        {"error.empty.certificate", "Keine Zertifikatdaten verfügbar" },
        {"filechooser.certificate.export", "Bitte wählen Sie den Dateinamen für den Export."},
        {"certificate.export.error.title", "Fehler beim Export"},
        {"certificate.export.error.message", "Der Export des Zertifikates schlug fehl:\n{0}"},
        {"certificate.export.success.title", "Erfolg"},
        {"certificate.export.success.message", "Das Zertifikat konnte erfolgreich exportiert werden nach\n\"{0}\""},
        {KeystoreCertificate.CERTIFICATE_FORMAT_PEM, "Textformat (PEM, *.cer)"},
        {KeystoreCertificate.CERTIFICATE_FORMAT_PEM_CHAIN, "Textformat (+Beglaubigungskette) (PEM, *.pem)"},
        {KeystoreCertificate.CERTIFICATE_FORMAT_DER, "Binärformat (DER, *.cer)"},
        {KeystoreCertificate.CERTIFICATE_FORMAT_PKCS7, "Mit Zertifizierungskette (PKCS#7, *.p7b)"},
        {KeystoreCertificate.CERTIFICATE_FORMAT_SSH2, "SSH2 Format (öffentlicher Schlüssel, *.pub)"},
    };

}
