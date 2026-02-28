//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleClientServerSessionHandlerLocalhost_pt.java 2     9/12/24 16:03 Heller $
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
public class ResourceBundleClientServerSessionHandlerLocalhost_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"allowallclients.true", "**O servidor AS22 aceita pedidos de clientes AS2 de outros hosts**"},
		{"allowallclients.false", "**O servidor AS2 só aceita pedidos de clientes locais"},
		{"only.localhost.clients", "O servidor remoto só pode aceitar ligações a partir do localhost. Para alterar este comportamento, inicie-o com a opção \"-allowallclients\"."},
	};
}
