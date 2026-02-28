//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleClientServerSessionHandlerLocalhost_fr.java 2     9/12/24 16:03 Heller $
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
public class ResourceBundleClientServerSessionHandlerLocalhost_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"allowallclients.true", "**Le serveur AS22 accepte les requêtes client AS2 d''autres hôtes**."},
		{"allowallclients.false", "**Le serveur AS2 n''accepte que les requêtes client locales**."},
		{"only.localhost.clients", "Le serveur distant ne doit accepter que les connexions de localhost. Pour modifier ce comportement, veuillez le démarrer avec l''option \"-allowallclients\"."},
	};
}
