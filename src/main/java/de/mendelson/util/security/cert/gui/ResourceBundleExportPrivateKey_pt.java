//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportPrivateKey_pt.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleExportPrivateKey_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.exportdir.help", "<HTML><strong>Diretório de exportação</strong><br><br>"
			+"Introduza aqui o diretório de exportação para o qual a chave privada deve ser exportada.<br>"
			+"Por razões de segurança, a chave não é transferida para o cliente, pelo que só pode ser guardada no lado do servidor.<br><br>"
			+"O sistema criará um ficheiro de memória neste diretório que contém um carimbo de data.</HTML>"},
		{"key.export.success.title", "Sucesso"},
		{"label.exportdir.hint", "Diretório no qual o ficheiro keystore/key deve ser criado"},
		{"button.browse", "Navegar"},
		{"label.exportkey", "Nome do ficheiro"},
		{"key.export.error.title", "Erro"},
		{"keystore.contains.nokeys", "Este registo de chaves não contém quaisquer chaves privadas."},
		{"label.exportdir", "Diretório de exportação"},
		{"title", "Exportar chave para ficheiro keystore/key"},
		{"label.exportkey.hint", "Ficheiro de exportação a criar"},
		{"button.cancel", "Cancelar"},
		{"key.exported.to.file", "A chave \"{0}\" foi exportada para o ficheiro \"{1}\"."},
		{"filechooser.key.export", "Selecione o diretório de exportação no servidor"},
		{"label.keypass", "palavra-passe"},
		{"button.ok", "Ok"},
		{"label.alias", "chave"},
		{"label.exportformat", "Formato de exportação"},
		{"key.export.error.message", "Ocorreu um erro durante a exportação.\n{0}"},
		{"label.keypass.hint", "Palavra-passe para o ficheiro keystore/key exportado"},
		{"label.exportformat.help", "<HTML><strong>Formato de exportação</strong><br><br>"
			+"Pode exportar a chave num formato de armazenamento de chaves (PKCS#12) ou num ficheiro de chaves codificado por PEM.<br>"
			+"A forma mais comum é o formato PKCS#12, um ficheiro de chave codificado PEM só é necessário para casos de utilização especiais, como a configuração de proxy invertido.<br><br>"
			+"No caso do ficheiro de chave PEM, a chave é guardada sem uma palavra-passe.</HTML>"},
	};
}
