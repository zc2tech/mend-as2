//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteShellCommand_es.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleExecuteShellCommand_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"executing.receipt", "[Post-proceso] ({0} --> {1}) Ejecutar evento tras recepción de datos."},
		{"executing.send", "[Post-proceso] ({0} --> {1}) Ejecutar evento de envío de datos."},
		{"executing.command", "[Post-proceso] Comando Shell: \"{0}\"."},
		{"messageid.nolonger.exist", "[Postproceso] No se puede ejecutar un evento de postproceso para el mensaje \"{0}\" - este mensaje ya no existe. Omitir procesamiento..."},
		{"executed.command", "[Postproceso] Se ha ejecutado el comando shell, valor de retorno={0}."},
	};
}
