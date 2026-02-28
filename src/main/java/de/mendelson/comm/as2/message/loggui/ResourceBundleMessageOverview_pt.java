//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageOverview_pt.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleMessageOverview_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"header.timestamp", "Tempo"},
		{"header.partner", "Parceiro"},
		{"header.localstation", "Estação local"},
		{"header.userdefinedid", "Id"},
		{"header.encryption", "Encriptação"},
		{"header.messageid", "Número de referência"},
		{"header.compression", "Compressão"},
		{"number.of.attachments", "* {0} Anexos de ficheiros *"},
		{"header.payload", "Dados do utilizador"},
		{"header.mdn", "MDN"},
		{"header.signature", "Assinatura digital"},
		{"header.subject", "Assunto"},
	};
}
