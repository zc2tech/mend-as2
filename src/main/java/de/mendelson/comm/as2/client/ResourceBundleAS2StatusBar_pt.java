//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2StatusBar_pt.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleAS2StatusBar_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"count.failure", "Transacções incorrectas"},
		{"configuration.issue", "Verifique a sua configuração"},
		{"count.pending", "Transacções em espera"},
		{"count.ok", "Transacções sem falhas"},
		{"count.selected", "Transacções selecionadas"},
		{"count.all.available", "Total de transacções no sistema"},
		{"count.all.served", "Número de transacções fornecidas"},
		{"no.configuration.issues", "Sem problemas de configuração"},
	};
}
