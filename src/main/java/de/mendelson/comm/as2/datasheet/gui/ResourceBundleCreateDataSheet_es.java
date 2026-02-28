//$Header: /as2/de/mendelson/comm/as2/datasheet/gui/ResourceBundleCreateDataSheet_es.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.datasheet.gui;

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
public class ResourceBundleCreateDataSheet_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.usessl", "Utilizar TLS"},
		{"label.newpartner", "Nuevo socio - aún no está en el sistema"},
		{"label.receipturl", "Su URL para la recepción de AS2"},
		{"label.remotepartner", "Socio remoto"},
		{"label.usedatasignature", "Utilizar datos firmados"},
		{"title", "Ficha técnica de la nueva conexión de comunicación"},
		{"label.usedataencryption", "Utilizar el cifrado de datos"},
		{"label.localpartner", "Socio local"},
		{"label.requestsignedeerp", "Esperar un PERE firmado"},
		{"button.cancel", "Cancelar"},
		{"label.comment", "Comentario"},
		{"label.signedmdn", "Firmado MDN"},
		{"label.compression", "Compresión de datos"},
		{"label.usesessionauth", "Utilizar autenticación de sesión"},
		{"button.ok", ">> Crear ficha de datos"},
		{"label.syncmdn", "MDN sincrónico"},
		{"file.written", "La hoja de datos (PDF) se ha escrito después de \"{0}\". Por favor, envíela a su nuevo socio para intercambiar los datos límite de la comunicación."},
		{"progress", "Crear PDF"},
		{"label.signature", "Firma digital"},
		{"label.encryption", "Cifrado"},
		{"label.info", "<HTML><strong>Puede utilizar este diálogo para crear una ficha de datos que facilite la conexión de un nuevo interlocutor.</strong></HTML>"},
	};
}
