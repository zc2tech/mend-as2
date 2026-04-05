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
 * @version $Revision: 8 $
 */
public class ResourceBundleTableModelCertificates extends MecResourceBundle{
        
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {       
        {"header.alias", "Alias" },
        {"header.expire", "Expire date" },
        {"header.length", "Size" },
        {"header.algorithm", "Algorithm" },
        {"header.organization", "Organization" },
        {"header.trust", "Trust" },
        {"trust.selfsigned", "Self signed" },
        {"trust.trusted", "Trusted" },
        {"trust.untrusted", "Not trusted" },
        {"trust.root", "Root" },
    };
    
}