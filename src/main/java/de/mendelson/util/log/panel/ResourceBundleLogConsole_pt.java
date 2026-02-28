//$Header: /oftp2/de/mendelson/util/log/panel/ResourceBundleLogConsole_pt.java 2     9/12/24 15:50 Heller $
package de.mendelson.util.log.panel;

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
public class ResourceBundleLogConsole_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"write.failure", "Erro ao escrever o registo: {0}."},
		{"label.tofile", "Escrever o registo num ficheiro"},
		{"label.toclipboard", "Copiar registo para a área de transferência"},
		{"filechooser.logfile", "Selecione o ficheiro no qual o registo deve ser escrito."},
		{"write.success", "O registo foi guardado com sucesso no ficheiro \"{0}\"."},
		{"title", "Despesas"},
		{"label.clear", "Eliminar"},
	};
}
