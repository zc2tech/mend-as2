//$Header: /as2/de/mendelson/comm/as2/cem/gui/ResourceBundleCEMOverview_it.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.cem.gui;

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
public class ResourceBundleCEMOverview_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"header.requestdate", "Data della richiesta"},
		{"activity.waitingforanswer", "Attendere la risposta"},
		{"button.responsedetails", "Dettagli della risposta"},
		{"label.certificate", "Certificato:"},
		{"header.receiver", "A"},
		{"header.activity", "Attività del sistema"},
		{"button.sendcem", "Nuovo scambio"},
		{"tab.reasonforrejection", "Motivi del rifiuto"},
		{"header.state", "Risposta"},
		{"title", "Gestione dello scambio di certificati"},
		{"activity.activated", "Nessuno - Attivato su {0}"},
		{"button.refresh", "Aggiornare"},
		{"button.cancel", "Annullamento"},
		{"button.exit", "Chiudere"},
		{"header.category", "Utilizzato per"},
		{"header.alias", "Certificato"},
		{"activity.waitingforprocessing", "Attendere l''elaborazione"},
		{"activity.waitingfordate", "Attendere la data di attivazione ({0})"},
		{"tab.certificate", "Informazioni sul certificato"},
		{"button.remove", "Cancellare"},
		{"button.requestdetails", "Dettagli della richiesta"},
		{"header.initiator", "Da"},
		{"activity.none", "Nessuno"},
	};
}
