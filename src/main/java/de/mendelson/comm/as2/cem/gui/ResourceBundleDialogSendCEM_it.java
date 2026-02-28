//$Header: /as2/de/mendelson/comm/as2/cem/gui/ResourceBundleDialogSendCEM_it.java 3     9/12/24 16:02 Heller $
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
public class ResourceBundleDialogSendCEM_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"label.certificate", "Certificato:"},
		{"purpose.encryption", "Crittografia"},
		{"cem.not.informed", "I seguenti partner non sono stati informati tramite CEM, si prega di effettuare lo scambio di certificati tramite e-mail o simili: {0}"},
		{"purpose.signature", "Firma digitale"},
		{"title", "Scambio di certificati con i partner (CEM)"},
		{"label.receiver", "Destinatario:"},
		{"cem.informed", "Si è tentato di informare i seguenti partner tramite CEM, verificare il successo nell''amministrazione CEM: {0}"},
		{"label.activationdate", "Data di attivazione:"},
		{"cem.request.failed", "Non è stato possibile eseguire la richiesta CEM:\n{0}"},
		{"purpose.ssl", "TLS"},
		{"button.cancel", "Annullamento"},
		{"cem.request.title", "Scambio di certificati tramite CEM"},
		{"button.ok", "Ok"},
		{"label.initiator", "Stazione locale:"},
		{"partner.cem.hint", "I sistemi dei partner devono supportare il CEM per essere inclusi in questa sezione."},
		{"cem.request.success", "La richiesta CEM è stata eseguita con successo."},
		{"partner.all", "--Tutti i partner..."},
	};
}
