//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeystore_pt.java 2     9/12/24 15:51 Heller $
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
* @version $Revision: 2 $
*/
public class ResourceBundleExportKeystore_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.exportdir.help", "<HTML><strong>Diretório de exportação</strong><br><br>"
			+"Introduza o diretório de exportação para o qual o keystore deve ser exportado.<br>"
			+"Por razões de segurança, as chaves não são transferidas para o cliente,<br>"
			+"para que só seja possível guardar no lado do servidor.<br>"
			+"O sistema cria um ficheiro de memória neste diretório que contém um carimbo de data.</HTML>"},
		{"keystore.export.error.title", "Erro"},
		{"label.exportdir.hint", "Diretório no qual o ficheiro keystore é criado"},
		{"button.browse", "Navegar"},
		{"keystore.export.error.message", "Houve um problema ao exportar:\n{0}"},
		{"label.exportdir", "Diretório de exportação"},
		{"title", "Exportar todas as entradas para o ficheiro keystore"},
		{"button.cancel", "Cancelar"},
		{"filechooser.key.export", "Selecione o diretório de exportação do lado do servidor"},
		{"label.keypass", "Palavra-passe"},
		{"keystore.export.success.title", "Sucesso"},
		{"button.ok", "Ok"},
		{"label.keypass.help", "<HTML><strong>Senha do keystore exportado</strong><br><br>"
			+"Esta é a palavra-passe com a qual o armazenamento de chaves exportado no lado do servidor é protegido.<br>"
			+"Introduza \"teste\" se pretender que este keystore seja importado automaticamente para o produto mendelson mais tarde.</HTML>"},
		{"keystore.exported.to.file", "O ficheiro keystore foi escrito em \"{0}\"."},
		{"label.keypass.hint", "Palavra-passe do armazenamento de chaves exportado"},
	};
}
