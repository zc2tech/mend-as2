//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundleFileDeleteController_pt.java 2     9/12/24 16:03 Heller $
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
public class ResourceBundleFileDeleteController_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"delete.title.log", "Eliminação de diretórios de registo através da manutenção do sistema"},
		{"no.entries", "{0}: Não foram encontradas entradas"},
		{"delete.title.tempfiles", "Ficheiros temporários"},
		{"delete.title._rawincoming", "Ficheiros de entrada de _rawincoming"},
		{"success", "SUCESSO"},
		{"failure", "ERRO"},
		{"autodelete", "{0}: O ficheiro foi automaticamente eliminado pelo processo de manutenção do sistema."},
		{"delete.header.logfiles", "Eliminar ficheiros de registo e ficheiros para eventos do sistema com mais de {0} dias"},
		{"delete.title", "Eliminar ficheiros através da manutenção do sistema"},
	};
}
