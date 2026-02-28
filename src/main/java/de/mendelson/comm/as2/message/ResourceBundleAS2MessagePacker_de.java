//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessagePacker_de.java 30    17/01/25 8:41 Heller $
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
 * @author S.Heller
 * @version $Revision: 30 $
 */
public class ResourceBundleAS2MessagePacker_de extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {
        {"message.signed", "Die ausgehende Nachricht wurde mit dem Algorithmus \"{1}\" digital signiert, benutzt wurde der Schlüssel mit dem Alias \"{0}\" der lokalen Station \"{2}\"." },
        {"message.notsigned", "Die ausgehende Nachricht wurde nicht digital signiert." },
        {"message.encrypted", "Die ausgehende Nachricht wurde mit dem Algorithmus {1} verschlüsselt, benutzt wurde das Zertifikat mit dem Alias \"{0}\" des entfernten Partners \"{2}\"." },
        {"message.notencrypted", "Die ausgehende Nachricht wurde nicht verschlüsselt." },
        {"mdn.created", "Ausgehende MDN erstellt für die AS2 Nachricht \"{0}\", Status auf [{1}] gesetzt." },
        {"mdn.details", "Details der ausgehenden MDN: {0}" },
        {"message.compressed", "Die ausgehenden Nutzdaten wurden von {0} auf {1} komprimiert." },
        {"message.compressed.unknownratio", "Die ausgehenden Nutzdaten wurden komprimiert." },
        {"mdn.signed", "Ausgehende MDN wurde mit dem Algorithmus \"{0}\" signiert, der Schlüsselalias ist \"{1}\" der lokalen Station \"{2}\"." },
        {"mdn.notsigned", "Ausgehende MDN wurde nicht signiert." },
        {"mdn.creation.start", "Erstelle ausgehende MDN, setze Nachrichten Id auf \"{0}\"."},
        {"message.creation.start", "Erstelle ausgehende AS2 Nachricht, setze Nachrichten Id auf \"{0}\"."},
        {"message.creation.error", "Die Nachricht mit der Nachrichten Id \"{0}\" konnte nicht erstellt werden: {1}. "
            + "Dies ist ein Problem, was bereits bei der Erstellung der ausgehenden Nachrichtenstruktur "
            + "auf Ihrem System statt fand - es hat nichts mit dem System Ihres Partners zu tun und es wurde auch nicht versucht, "
            + "eine Verbindung zu Ihrem Partner aufzubauen." },
        {"signature.no.aipa", "Der Signaturprozess verwendet nicht das Algorithm Identifier Protection Attribut in der Signatur (wie in der Konfiguration eingestellt) - das ist unsicher!" },
        
    };
    
}