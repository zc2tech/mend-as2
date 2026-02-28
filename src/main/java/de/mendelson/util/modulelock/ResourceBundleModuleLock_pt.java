//$Header: /oftp2/de/mendelson/util/modulelock/ResourceBundleModuleLock_pt.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.modulelock;

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
public class ResourceBundleModuleLock_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"Partner management", "Gestão de parceiros"},
		{"TLS keystore", "Gestão de certificados (TLS)"},
		{"configuration.locked.otherclient", "O módulo {0} está aberto exclusivamente por outro cliente,\nDe momento, não é possível efetuar quaisquer alterações.\nDetalhes do outro cliente:\nIP: {1}\nUtilizador: {2}\nID do processo: {3}"},
		{"ENC/SIGN keystore", "Gestão de certificados (encriptação/assinatura)"},
		{"configuration.changed.otherclient", "Outro cliente pode ter efectuado alterações ao módulo {0}.\nPor favor, abra novamente esta interface de configuração para recarregar a configuração atual."},
		{"Server settings", "Definições do servidor"},
		{"modifications.notallowed.message", "De momento, não é possível efetuar alterações"},
	};
}
