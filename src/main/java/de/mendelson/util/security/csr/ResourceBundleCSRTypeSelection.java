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
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleCSRTypeSelection extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Select the sign request message type to generate"},
        {"info", "<HTML>There are several different types of sign request messages available. The most common is PKCS#10. "
            + "Please select your sign request messages format.</HTML>" },
        {"label." + JDialogCSRTypeSelection.SELECTION_PKCS10, "<HTML><strong>Certificate Sign Request (CSR) PKCS#10</strong></HTML>" },
        {"label." + JDialogCSRTypeSelection.SELECTION_CRMF, "<HTML>Certificate Request Message Format (CRMF)</HTML>" },
        {"label.selectcsrfile", "Please select the location where to save the CSR to" },
        {"label.exportfile", "Sign request message file" },
        {"label.exportfile.hint", "The filename where to store the generated sign request message" },
        {"button.ok", "Ok"},
        {"button.cancel", "Cancel"},
        
    };
}
