//$Header: /as2/de/mendelson/util/clientserver/connectionprogress/ResourceBundleConnectionProgress_pt.java 1     6/12/24 8:50 Heller $
package de.mendelson.util.clientserver.connectionprogress;

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
* @version $Revision: 1 $
*/
public class ResourceBundleConnectionProgress_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"connecting.to", "Ligar a {0}..."},
	};
}
