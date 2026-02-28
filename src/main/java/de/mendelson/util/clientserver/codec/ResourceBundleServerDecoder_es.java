//$Header: /oftp2/de/mendelson/util/clientserver/codec/ResourceBundleServerDecoder_es.java 2     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver.codec;

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
public class ResourceBundleServerDecoder_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"client.incompatible", "No se ha podido establecer una conexión cliente-servidor. El servidor no puede deserializar los datos entrantes del cliente. La causa principal son versiones diferentes del cliente y del servidor."},
	};
}
