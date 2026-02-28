//$Header: /as2/de/mendelson/comm/as2/message/store/ResourceBundleMessageStoreHandler_es.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleMessageStoreHandler_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"dir.createerror", "No se ha podido crear el directorio \"{0}\"."},
		{"comm.success", "La comunicación AS2 se ha realizado correctamente, los datos de usuario {0} se han movido a \"{1}\". ({2})"},
		{"outboundstatus.written", "El archivo de estado de la transacción saliente se escribió en \"{0}\"."},
		{"message.error.raw.stored", "Los datos de transmisión se han guardado en \"{0}\"."},
		{"message.error.stored", "El mensaje incrustado se guardó en \"{0}\"."},
	};
}
