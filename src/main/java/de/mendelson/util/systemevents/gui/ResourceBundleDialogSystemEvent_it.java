//$Header: /as2/de/mendelson/util/systemevents/gui/ResourceBundleDialogSystemEvent_it.java 4     5/12/24 16:40 Heller $
package de.mendelson.util.systemevents.gui;

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
public class ResourceBundleDialogSystemEvent_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.close", "Chiudere"},
		{"header.timestamp", "Timestamp"},
		{"label.id", "Numero dell''evento"},
		{"label.category", "Categoria"},
		{"label.enddate", "Fine"},
		{"label.freetext", "Testo di ricerca"},
		{"category.all", "-- Tutti --"},
		{"label.type", "Tipo"},
		{"title", "Visualizzazione degli eventi di sistema"},
		{"label.search", "Ricerca eventi"},
		{"label.resetfilter", "Reset"},
		{"label.host", "Ospite"},
		{"header.category", "Categoria"},
		{"label.startdate", "Inizio"},
		{"label.freetext.hint", "Ricerca per numero o testo dell''evento"},
		{"label.user", "Proprietario"},
		{"header.type", "Tipo"},
		{"no.data", "Non esiste alcun evento di sistema che corrisponda alla selezione di data/tipo corrente."},
		{"user.server.process", "Processo del server"},
		{"label.date", "data"},
	};
}
