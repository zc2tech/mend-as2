//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageOverview_it.java 2     5/12/24 11:50 Heller $
package de.mendelson.comm.as2.message.loggui;

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
* @version $Revision: 2 $
*/
public class ResourceBundleMessageOverview_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"header.timestamp", "Tempo"},
		{"header.partner", "Partner"},
		{"header.localstation", "Stazione locale"},
		{"header.userdefinedid", "Id"},
		{"header.encryption", "Crittografia"},
		{"header.messageid", "Numero di riferimento"},
		{"header.compression", "Compressione"},
		{"number.of.attachments", "* {0} File allegati *"},
		{"header.payload", "Dati utente"},
		{"header.mdn", "MDN"},
		{"header.signature", "Firma digitale"},
		{"header.subject", "Oggetto"},
	};
}
