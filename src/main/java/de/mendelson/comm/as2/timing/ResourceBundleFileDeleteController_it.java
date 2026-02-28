//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundleFileDeleteController_it.java 3     9/12/24 16:03 Heller $
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
public class ResourceBundleFileDeleteController_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"delete.title.log", "Eliminazione delle directory di log attraverso la manutenzione del sistema"},
		{"no.entries", "{0}: Nessuna voce trovata"},
		{"delete.title.tempfiles", "File temporanei"},
		{"delete.title._rawincoming", "File in arrivo da _rawincoming"},
		{"success", "SUCCESSO"},
		{"failure", "ERRORE"},
		{"autodelete", "{0}: Il file è stato eliminato automaticamente dal processo di manutenzione del sistema."},
		{"delete.header.logfiles", "Eliminare i file di registro e i file per gli eventi di sistema più vecchi di {0} giorni"},
		{"delete.title", "Eliminazione di file attraverso la manutenzione del sistema"},
	};
}
