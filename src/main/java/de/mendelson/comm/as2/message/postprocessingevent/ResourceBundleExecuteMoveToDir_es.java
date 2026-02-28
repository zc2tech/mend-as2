//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteMoveToDir_es.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;

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
public class ResourceBundleExecuteMoveToDir_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"executing.receipt", "[Post-procesamiento] ({0} --> {1}) Ejecutar evento tras su recepción."},
		{"executing.send", "[Post-proceso] ({0} --> {1}) Ejecutar evento después del envío."},
		{"messageid.nolonger.exist", "[Post-proceso] El evento para el mensaje \"{0}\" no pudo ser ejecutado - ya no existe. Se omite el proceso..."},
		{"executing.movetodir", "[Post-proceso] Mover \"{0}\" a \"{1}\"."},
		{"executing.targetdir", "[Post-proceso] Directorio de destino: \"{0}\"."},
		{"executing.movetodir.success", "[Post-procesamiento] Archivo movido con éxito"},
	};
}
