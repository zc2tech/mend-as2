//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleInfoOnExternalCertificate.java 7     3/07/24 14:19 Heller $ 
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.MecResourceBundle;

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
 * @version $Revision: 7 $
 */
public class ResourceBundleInfoOnExternalCertificate extends MecResourceBundle {

    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"button.ok", "Import >>"},
        {"button.cancel", "Close"},
        {"title.single", "Info on external certificate"},
        {"title.multiple", "Info on external certificates"},
        {"certinfo.certfile", "Certificate file: {0}"},
        {"certinfo.index", "Certificate {0} of {1}"},
        {"certificate.exists", "This certificate does already exist in the certificate manager, alias is \"{0}\""},
        {"certificate.doesnot.exist", "This certificate does not exist in the certificate manager so far"},
        {"no.certificate", "Unable to identify certificate" },
    };    
}
