//$Header: /as2/de/mendelson/comm/as2/message/store/ResourceBundleMessageStoreHandler_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.message.store;

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
public class ResourceBundleMessageStoreHandler_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"dir.createerror", "O diretório \"{0}\" não pôde ser criado."},
		{"comm.success", "A comunicação AS2 foi bem sucedida, os dados do utilizador {0} foram movidos para \"{1}\". ({2})"},
		{"outboundstatus.written", "O ficheiro de estado da transação de saída foi escrito em \"{0}\"."},
		{"message.error.raw.stored", "Os dados de transmissão foram guardados em \"{0}\"."},
		{"message.error.stored", "A mensagem incorporada foi guardada em \"{0}\"."},
	};
}
