//$Header: /oftp2/de/mendelson/util/log/panel/ResourceBundleLogConsole_it.java 3     9/12/24 15:50 Heller $
package de.mendelson.util.log.panel;

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
public class ResourceBundleLogConsole_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"write.failure", "Errore durante la scrittura del log: {0}."},
		{"label.tofile", "Scrivere il log su file"},
		{"label.toclipboard", "Copia del registro negli appunti"},
		{"filechooser.logfile", "Selezionare il file in cui scrivere il log."},
		{"write.success", "Il registro è stato salvato con successo nel file \"{0}\"."},
		{"title", "Spese"},
		{"label.clear", "Cancellare"},
	};
}
