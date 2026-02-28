//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleServerPlugins_de.java 3     2/11/23 15:53 Heller $
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
 * @version $Revision: 3 $
 */
public class ResourceBundleServerPlugins_de extends MecResourceBundle {

    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {        
        {"module.name", "[LIZENZ]" },
        {"plugin.active", "Das Plugin [{0}] ist aktiviert." },
        {"plugin.inactive", "Das Plugin [{0}] ist nicht aktiviert." },
    };
}