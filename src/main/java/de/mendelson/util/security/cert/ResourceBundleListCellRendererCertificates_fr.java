//$Header: /oftp2/de/mendelson/util/security/cert/ResourceBundleListCellRendererCertificates_fr.java 6     9/12/24 15:51 Heller $
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
 * @version $Revision: 6 $
 */
public class ResourceBundleListCellRendererCertificates_fr extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {                
        {"certificate.not.assigned", "Aucun/Non trouvé/Non attribué" },
    };
    
}