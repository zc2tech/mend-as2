//$Header: /as2/de/mendelson/comm/as2/database/ResourceBundleDBDriverManager_it.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.database;

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
public class ResourceBundleDBDriverManager_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"database.creation.failed.1", "Si è verificato un errore durante la creazione del database di configurazione"},
		{"database.creation.failed.2", "Si è verificato un errore durante la creazione del database di runtime"},
		{"creating.database.1", "Creare un database di configurazione"},
		{"creating.database.2", "Creare il database di runtime"},
		{"creating.database.details", "Host: {0}, Porta: {1}, Utente: {2}, Nome DB: {3}"},
		{"database.creation.success.1", "Il database di configurazione è stato creato con successo"},
		{"database.creation.success.2", "Il database di runtime è stato creato con successo"},
		{"module.name", "[DATABASE]"},
	};
}
