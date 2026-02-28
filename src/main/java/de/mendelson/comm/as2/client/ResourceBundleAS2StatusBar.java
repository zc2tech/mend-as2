//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2StatusBar.java 7     2/11/23 15:52 Heller $ 
package de.mendelson.comm.as2.client;

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
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class ResourceBundleAS2StatusBar extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"count.ok", "Transaction without failure"},
        {"count.all.served", "All served transactions"},
        {"count.all.available", "All available transactions at the server"},
        {"count.pending", "Pending transactions"},
        {"count.failure", "Transactions with failure"},
        {"count.selected", "Selected transaction"},
        {"configuration.issue", "Please check your configuration"},
        {"no.configuration.issues", "No configuration issues"},};

}
