//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleRenameEntry_pt.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert.gui;

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
public class ResourceBundleRenameEntry_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.newalias", "Novo pseudónimo"},
		{"button.cancel", "Cancelar"},
		{"label.newalias.hint", "O pseudónimo a utilizar no futuro"},
		{"button.ok", "Ok"},
		{"alias.exists.title", "A renomeação do alias falhou"},
		{"alias.exists.message", "O alias \"{0}\" já existe no keystore subjacente."},
		{"title", "Mudar o nome do pseudónimo ({0})"},
	};
}
