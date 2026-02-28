//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleServerPlugins_fr.java 4     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.server;

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
 * @version $Revision: 4 $
 */
public class ResourceBundleServerPlugins_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {    
        {"module.name", "[LICENCE]" },
        {"plugin.active", "Le plugin [{0}] est activé." },
        {"plugin.inactive", "Le plugin [{0}] est inactif." },
    };
}