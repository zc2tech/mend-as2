//$Header: /as2/de/mendelson/util/security/csr/ResourceBundleCSRUtil.java 7     28/08/24 12:39 Heller $
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
 * @version $Revision: 7 $
 */
public class ResourceBundleCSRUtil extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {                
        {"verification.failed", "Verification of the created CSR failed" },
        {"no.certificates.in.reply", "No certificates found in CSR reply, unable to patch the key" },
        {"missing.cert.in.trustchain", "The certificates of the trust chain (root and intermediate certificate) are missing in the system for this operation.\n"
            + "You will receive these certificates from your CA.\n"
            + "Please import the certificate with the issuer\n{0}\ninto the keystore first." },
        {"response.chain.incomplete", "The certificate chain of the response is incomplete" },
        {"response.verification.failed", "Problem verifying the certificate chain of the response: {0}" },
        {"response.public.key.does.not.match", "This is not the CAs answer for this key." },
    };


    

}