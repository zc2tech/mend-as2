//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleMDNParser_es.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.message;

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
public class ResourceBundleMDNParser_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"invalid.mdn.nocontenttype", "El MDN entrante no es válido: No se ha definido el tipo de contenido."},
		{"structure.failure.mdn", "Se ha reconocido un MDN entrante. Lamentablemente, la estructura del MDN es incorrecta (\"{0}\"), por lo que no se ha podido procesar. La transacción asociada no ha cambiado de estado."},
	};
}
