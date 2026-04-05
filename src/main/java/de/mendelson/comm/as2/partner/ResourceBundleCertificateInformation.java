package de.mendelson.comm.as2.partner;

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
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ResourceBundleCertificateInformation extends MecResourceBundle {

    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"localstation.decrypt", "Inbound messages for the local station \"{0}\" will be decrypted using the certificate \"{1}\"."},
        {"localstation.sign", "Outbound messages from the local station \"{0}\" will be signed using the certificate \"{1}\"."},
        {"partner.encrypt", "Outbound messages to the partner \"{0}\" will be encrypted using the certificate \"{1}\"."},
        {"partner.sign", "Inbound message signatures from the partner \"{0}\" will be verified using the certificate \"{1}\"."},
    };
}
