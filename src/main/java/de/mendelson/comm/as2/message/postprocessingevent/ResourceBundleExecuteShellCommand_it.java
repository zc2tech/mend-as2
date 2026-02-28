//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteShellCommand_it.java 3     9/12/24 16:02 Heller $
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
* @version $Revision: 3 $
*/
public class ResourceBundleExecuteShellCommand_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"executing.receipt", "({0} --> {1}) Eseguire l''evento dopo la ricezione dei dati."},
		{"executing.send", "({0} --> {1}) Eseguire l''evento di invio dati."},
		{"executing.command", "[Comando shell: \"{0}\"."},
		{"messageid.nolonger.exist", "[Un evento di post-elaborazione per il messaggio \"{0}\" non può essere eseguito - questo messaggio non esiste più. Saltare l''elaborazione..."},
		{"executed.command", "[Postprocessing] Il comando di shell è stato eseguito, valore di ritorno={0}."},
	};
}
