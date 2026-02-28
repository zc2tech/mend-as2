//$Header: /as2/de/mendelson/util/security/cert/ResourceBundleTableModelCertificates_de.java 10    4/03/25 14:36 Heller $
package de.mendelson.util.security.cert;
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
 * @version $Revision: 10 $
 */
public class ResourceBundleTableModelCertificates_de extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {        
        {"header.alias", "Alias" },
        {"header.expire", "Gültig bis" },
        {"header.length", "Länge" },
        {"header.algorithm", "Algorithmus" },
        {"header.organization", "Organisation" },
        {"header.trust", "Beglaubigung" },
        {"trust.selfsigned", "Selbstsigniert" },
        {"trust.trusted", "Vertrauenswürdig" },
        {"trust.untrusted", "Nicht vertrauenswürdig" },
        {"trust.root", "Stammzertifikat" },
    };
    
}