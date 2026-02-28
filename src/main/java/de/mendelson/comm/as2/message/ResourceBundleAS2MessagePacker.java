//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessagePacker.java 28    17/01/25 8:41 Heller $
package de.mendelson.comm.as2.message;

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
 * @version $Revision: 28 $
 */
public class ResourceBundleAS2MessagePacker extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"message.signed", "Outbound message signed with the algorithm \"{1}\", using the private key with the alias \"{0}\" of the local station \"{2}\"."},
        {"message.notsigned", "Outbound message is not signed."},
        {"message.encrypted", "Outbound message encrypted with the algorithm \"{1}\", using the certificate with the alias \"{0}\" of the remote partner \"{2}\"."},
        {"message.notencrypted", "Outbound message has not been encrypted."},
        {"mdn.created", "Outbound MDN created for AS2 message \"{0}\", state set to [{1}]."},
        {"mdn.details", "Outbound MDN details: {0}"},
        {"message.compressed", "Outbound payload compressed from {0} to {1}."},
        {"message.compressed.unknownratio", "Outbound payload compressed."},
        {"mdn.signed", "Outbound MDN has been signed with the algorithm \"{0}\", the key alias is \"{1}\" of the local station \"{2}\"."},
        {"mdn.notsigned", "Outbound MDN has not been signed."},
        {"mdn.creation.start", "Generating outbound MDN, setting message id to \"{0}\"."},
        {"message.creation.start", "Generating outbound AS2 message, setting message id to \"{0}\"."},
        {"message.creation.error", "The message with the message id \"{0}\" could not be generated: {1}. "
            + "This is a problem that already took place when the outgoing message structure was created on your system - "
            + "it has nothing to do with your partner''s system and no attempt was made to establish a "
            + "connection to your partner."},
        {"signature.no.aipa",
             "The signing process does not use the Algorithm Identifier Protection Attribute as defined in the configuration - this is insecure!"},};

}
