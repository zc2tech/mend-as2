//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundleMessageDeleteController_es.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.timing;

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
public class ResourceBundleMessageDeleteController_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"transaction.deleted.user", "{0} Transacciones canceladas por interacción del usuario"},
		{"transaction.deleted.system", "Transacciones eliminadas por el proceso de mantenimiento del sistema"},
		{"delete.skipped", "BORRAR OMITIDO"},
		{"delete.ok", "BORRAR CON ÉXITO"},
		{"autodelete", "{0}: Este mensaje es anterior a {1} {2} y fue borrado automáticamente por el proceso de mantenimiento del sistema."},
		{"delete.failed", "BORRADO FALLIDO"},
		{"transaction.deleted.transactiondate", "Fecha de la transacción: {0}"},
		{"transaction.delete.setting.olderthan", "El proceso está configurado para eliminar transacciones con estado verde que sean anteriores a {0}."},
	};
}
