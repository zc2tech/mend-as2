//$Header: /oftp2/de/mendelson/util/clientserver/gui/ResourceBundleLogin_es.java 2     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver.gui;

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
public class ResourceBundleLogin_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"button.cancel", "Cancelar"},
		{"button.ok", "Ok"},
		{"label.passwd", "contraseña"},
		{"title.login", "Inscripción"},
		{"label.user", "Usuarios"},
	};
}
