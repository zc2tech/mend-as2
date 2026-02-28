//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleRenameEntry_it.java 3     9/12/24 15:51 Heller $
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
* @version $Revision: 3 $
*/
public class ResourceBundleRenameEntry_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"label.newalias", "Nuovo alias"},
		{"button.cancel", "Annullamento"},
		{"label.newalias.hint", "L''alias da utilizzare in futuro"},
		{"button.ok", "Ok"},
		{"alias.exists.title", "Rinominare l''alias non è riuscito"},
		{"alias.exists.message", "L''alias \"{0}\" esiste già nel keystore sottostante."},
		{"title", "Rinominare l''alias ({0})"},
	};
}
