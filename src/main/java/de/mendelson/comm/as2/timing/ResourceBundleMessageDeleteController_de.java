//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundleMessageDeleteController_de.java 10    9/12/24 16:03 Heller $
package de.mendelson.comm.as2.timing;
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
 * @version $Revision: 10 $
 */
public class ResourceBundleMessageDeleteController_de extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"autodelete", "{0}: Diese Nachricht ist älter als {1} {2} und wurde automatisch vom Systempflegeprozess gelöscht." },    
        {"transaction.deleted.user", "{0} Transaktionen gelöscht durch Benutzerinteraktion" },
        {"transaction.deleted.system", "Transaktionen gelöscht durch Systempflegeprozess" },
        {"transaction.deleted.transactiondate", "Transaktionsdatum: {0}" },
        {"transaction.delete.setting.olderthan", "Der Prozess ist konfiguriert, Transaktionen mit grünem Status zu löschen, die älter sind als {0}." },
        {"delete.ok", "LÖSCHEN ERFOLGREICH" },
        {"delete.failed", "LÖSCHEN FEHLGESCHLAGEN" }, 
        {"delete.skipped", "LÖSCHEN ÜBERSPRUNGEN" },  
    };
    
}