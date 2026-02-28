//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2StatusBar_es.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.client;

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
public class ResourceBundleAS2StatusBar_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"count.failure", "Transacciones incorrectas"},
		{"configuration.issue", "Compruebe su configuración"},
		{"count.pending", "Transacciones en espera"},
		{"count.ok", "Transacciones impecables"},
		{"count.selected", "Transacciones seleccionadas"},
		{"count.all.available", "Total de transacciones en el sistema"},
		{"count.all.served", "Número de transacciones realizadas"},
		{"no.configuration.issues", "Sin problemas de configuración"},
	};
}
