//$Header: /oftp2/de/mendelson/util/security/csr/ResourceBundleCSRUtil_de.java 9     9/12/24 15:51 Heller $
package de.mendelson.util.security.csr;
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
 * @version $Revision: 9 $
 */
public class ResourceBundleCSRUtil_de extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {                
        {"verification.failed", "Die Überprüfung des erstellten Certificate Sign Requests (CSR) ist fehlgeschlagen." },
        {"no.certificates.in.reply", "Der Schlüssel konnte nicht gepatcht werden, es wurden in der CA Antwort keine Zertifikate gefunden." },
        {"missing.cert.in.trustchain", "Es fehlen für diese Operation die Zertifikate der Beglaubigungskette im System (Root und Intermediate Zertifikat).\n"
            + "Sie erhalten diese Zertifikate von Ihrer CA.\n"
            + "Bitte importieren Sie zunächst das Zertifikat mit den Eckdaten (issuer)\n{0}." },
        {"response.chain.incomplete", "Der Trust Chain der CSR Antwort ist unvollständig." },
        {"response.verification.failed", "Der Trust Chain der CSR Antwort konnte nicht verifiziert werden: {0}" },
        {"response.public.key.does.not.match", "Diese Antwort der CA passt nicht zu diesem Schlüssel." },
    };


    

}