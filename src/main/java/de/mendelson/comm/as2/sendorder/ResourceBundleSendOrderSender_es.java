//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderSender_es.java 3     17/01/25 8:41 Heller $
package de.mendelson.comm.as2.sendorder;

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
public class ResourceBundleSendOrderSender_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"sendoder.sendfailed", "Se ha producido un problema al procesar una solicitud de envío: [{0}] \"{1}\" - los datos no se transmitieron al interlocutor."},
		{"message.packed", "Mensaje AS2 saliente de \"{0}\" para destinatario \"{1}\" creado en {3}, tamaño de datos brutos: {2}, id definida por el usuario: \"{4}\""},
	};
}
