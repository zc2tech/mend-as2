//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleFileDisplay_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.message.loggui;

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
public class ResourceBundleFileDisplay_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"no.file", "** NÃO EXISTEM DADOS DISPONÍVEIS **"},
		{"file.notfound", "** O FICHEIRO {0} JÁ NÃO ESTÁ DISPONÍVEL **"},
		{"file.tolarge", "** {0}: DADOS DESTE TAMANHO NÃO PODEM SER VISUALIZADOS **"},
	};
}
