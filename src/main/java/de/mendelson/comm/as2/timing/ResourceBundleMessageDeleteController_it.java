//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundleMessageDeleteController_it.java 3     9/12/24 16:03 Heller $
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
* @version $Revision: 3 $
*/
public class ResourceBundleMessageDeleteController_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"transaction.deleted.user", "{0} Transazioni annullate dall''interazione dell''utente"},
		{"transaction.deleted.system", "Transazioni eliminate dal processo di manutenzione del sistema"},
		{"delete.skipped", "CANCELLARE SCARTATO"},
		{"delete.ok", "ELIMINARE CON SUCCESSO"},
		{"autodelete", "{0}: Questo messaggio è più vecchio di {1} {2} ed è stato eliminato automaticamente dal processo di manutenzione del sistema."},
		{"delete.failed", "CANCELLAZIONE NON RIUSCITA"},
		{"transaction.deleted.transactiondate", "Data della transazione: {0}"},
		{"transaction.delete.setting.olderthan", "Il processo è configurato per eliminare le transazioni con stato verde che sono più vecchie di {0}."},
	};
}
