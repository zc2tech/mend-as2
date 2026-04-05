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
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ResourceBundlePartnerTLSCertificateChangedController extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"module.name", "TLS certificate check"},
        {"import.success", "The TLS certificate \"{0}\" of the partner [{1}] has been imported automatically."},
        {"import.success.event.header", "Automatic import of a TLS certificate"},
        {"import.success.event.body", "The system is configured so that it regularly checks "
            + "partners connected via TLS to see whether they have changed their TLS certificate. "
            + "If this is the case and the TLS certificate does not exist in your local "
            + "TLS certificate manager, it is automatically imported.\n"
            + "The system has found a new certificate for the partner \"{0}\" under the URL \"{1}\" "
            + "and successfully imported it into your TLS certificate manager with the alias \"{2}\"."},
        {"import.failed", "The TLS certificate of the partner {0} has not been imported automatically: {1}"},
        {"autoimport.tls.check.started", "The automatic import of modified partner TLS certificates has been activated."},
        {"autoimport.tls.check.stopped", "The automatic import of modified partner TLS certificates has been deactivated."},};

}
