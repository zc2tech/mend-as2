//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleClientServerSessionHandlerLocalhost_es.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.server;

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
public class ResourceBundleClientServerSessionHandlerLocalhost_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"allowallclients.true", "**El servidor AS22 acepta peticiones de clientes AS2 de otros hosts**"},
		{"allowallclients.false", "**El servidor AS2 sólo acepta peticiones de clientes locales**"},
		{"only.localhost.clients", "Es posible que el servidor remoto sólo acepte conexiones desde localhost. Para cambiar este comportamiento, inícielo con la opción \"-allowallclients\"."},
	};
}
