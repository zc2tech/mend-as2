//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleMDNParser_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.message;

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
public class ResourceBundleMDNParser_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"invalid.mdn.nocontenttype", "A MDN de entrada é inválida: não foi definido nenhum tipo de conteúdo."},
		{"structure.failure.mdn", "Foi reconhecida uma MDN de entrada. Infelizmente, a estrutura da MDN é incorrecta (\"{0}\"), pelo que não pôde ser processada. A transação associada não alterou o seu estado."},
	};
}
