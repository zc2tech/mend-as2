//$Header: /as4/de/mendelson/util/security/cert/ResourceBundleListCellRendererCertificates.java 5     13/09/24 12:23 Heller $
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
 * @version $Revision: 5 $
 */
public class ResourceBundleListCellRendererCertificates extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {                
        {"certificate.not.assigned", "None/Not found/Unassigned" },
    };
    
}