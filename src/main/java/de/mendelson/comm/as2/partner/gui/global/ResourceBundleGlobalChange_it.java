//$Header: /as2/de/mendelson/comm/as2/partner/gui/global/ResourceBundleGlobalChange_it.java 4     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.partner.gui.global;

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
public class ResourceBundleGlobalChange_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"partnersetting.changed", "Le impostazioni sono state modificate per {0} partner."},
		{"label.pollinterval", "Intervallo di polling della directory di tutti i partner"},
		{"button.ok", "Chiudere"},
		{"partnersetting.notchanged", "Le impostazioni non sono state modificate - valore errato"},
		{"label.dirpoll", "Eseguire il sondaggio della directory per tutti i partner"},
		{"button.set", "Set"},
		{"info.text", "<HTML>È possibile utilizzare questa finestra di dialogo per impostare contemporaneamente i parametri di tutti i partner su valori definiti. Se si preme \"Imposta\", il rispettivo valore per <strong>TUTTI</strong> i partner viene sovrascritto.</HTML>"},
		{"title", "Modifiche globali a tutti i partner"},
		{"label.maxpollfiles", "Numero massimo di file da tutti i partner per processo di polling"},
	};
}
