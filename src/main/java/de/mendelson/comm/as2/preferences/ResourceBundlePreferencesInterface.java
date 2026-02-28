//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesInterface.java 9     19/07/24 11:21 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.comm.as2.timing.PartnerTLSCertificateChangedController;
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
 * @version $Revision: 9 $
 */
public class ResourceBundlePreferencesInterface extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"label.showsecurityoverwrite", "Partner management: Overwrite security settings of the local station"},
        {"label.showsecurityoverwrite.help", "<HTML><strong>Overwrite security settings of the local station</strong><br><br>"
            + "If you switch this option on, an additional tab is displayed for each partner in the partner "
            + "administration. Here you can define the private keys that are always used for incoming "
            + "and outgoing calls for this partner - regardless of the settings of the respective local station.<br>"
            + "This option allows you to use different private keys for each partner at the same local station.<br><br>"
            + "This is an option for compatibility with other AS2 products - some systems have exactly these "
            + "requirements, but they require the configuration of relationships between partners "
            + "and not individual partners."
            + "</HTML>"},
        {"label.showhttpheader", "Partner management: Allow to configure the HTTP headers"},
        {"label.showhttpheader.help", "<HTML><strong>Allow to configure the HTTP headers</strong><br><br>"
            + "If you activate this option, an additional tab is displayed in the partner administration "
            + "for each partner, in which you can define user-defined HTTP headers "
            + "for sending data to this partner."
            + "</HTML>"},
        {"label.showquota", "Partner management: Allow to configure quota notification"},
        {"label.outboundstatusfiles", "Write outbound transaction status files"},
        {"label.outboundstatusfiles.help", "<HTML><strong>Write outbound transaction status files</strong><br><br>"
            + "If you activate this option, a status file is written to the \"outboundstatus\" directory "
            + "for each outbound transaction. This file is used for integration purposes and contains "
            + "information on the respective transaction. This includes, for example, the transaction "
            + "status, message number, sender and recipient ID.<br><br>"
            + "The file name of the status file contains the message number and ends with \".sent.state\". "
            + "After sending data, you can parse this file and check the status of the transaction."
            + "</HTML>"},
        {"label.cem", "Allow certificate exchange (CEM)"},
        {"label.checkrevocationlists", "Certificates: Check revocation lists"},
        {"label.checkrevocationlists.help", "<HTML><strong>Certificates: Checking Revocation Lists</strong><br><br>"
            + "A revocation list is a list of certificates that have been invalidated due to various security concerns or "
            + "issues. These concerns may include compromise of the private key, loss of the certificate, or suspicion "
            + "of fraudulent activity. Revocation lists are managed by certification authorities or other trusted entities "
            + "authorized to issue certificates. Checking the revocation lists is important to ensure that the certificates "
            + "used in a connection or for a cryptographic operation are valid and trustworthy. A certificate listed on a "
            + "revocation list should no longer be used for cryptographic operations, as it may pose potential security risks "
            + "and compromise the integrity of communication.<br><br>"
            + "With the help of this setting, you can determine whether the system also checks the revocation lists "
            + "during configuration validation."
            + "</HTML>"
        },
        {"autoimport.tls", "TLS Certificates: Auto import if changed"},
        {"autoimport.tls.help", "<HTML><strong>TLS Certificates: Auto import if changed</strong><br><br>"
            + "If a partner connection is realised via HTTPS (TLS, the URL starts with https), you can regularly check "
            + "whether the TLS certificate has changed on the partners side. If it has been changed and is not yet in your system, "
            + "it is then automatically imported with the entire trust chain.<br>"
            + "The system will check the partners TLS certificates all " 
            + PartnerTLSCertificateChangedController.CHECK_DELAY_IN_MIN + " minutes. Means it could take some time before "
            + "the change of a partners TLS certificate is detected.<br><br>"
            + "You can also carry out this process manually by performing a connection test to a partner "
            + "and then importing missing TLS certificates.<br><br>"
            + "Please note that this is a problematic setting at security level because it means "
            + "that you automatically trust a certificate that has been found - without being asked."
            + "</HTML>"
        },};
}
