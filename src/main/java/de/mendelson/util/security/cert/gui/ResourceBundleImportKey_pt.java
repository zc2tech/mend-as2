//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleImportKey_pt.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleImportKey_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"key.import.error.entry.exists", "Importação não possível - já existe uma entrada para esta impressão digital com o pseudónimo {0}."},
		{"button.browse", "Navegar"},
		{"key.import.success.title", "Sucesso"},
		{"key.import.error.message", "Ocorreu um erro durante o processo de importação.\n{0}"},
		{"keystore.contains.nokeys", "Este ficheiro de chaves não contém quaisquer chaves privadas."},
		{"multiple.keys.title", "Várias chaves incluídas"},
		{"title", "Importar chave do ficheiro de chaves (PKCS#12, formato JKS)"},
		{"button.cancel", "Demolição"},
		{"key.import.error.title", "Erro"},
		{"enter.keypassword", "Introduzir a palavra-passe chave para \"{0}\""},
		{"label.keypass", "palavra-passe"},
		{"button.ok", "Ok"},
		{"multiple.keys.message", "Selecione a chave a importar"},
		{"label.importkey", "Nome do ficheiro"},
		{"filechooser.key.import", "Selecione um ficheiro de chave eletrónica PKCS#12/JKS para a importação"},
		{"key.import.success.message", "A chave foi importada com sucesso."},
		{"label.importkey.hint", "Ficheiro de chaves a importar (PKCS#12, JKS)"},
		{"label.keypass.hint", "Palavra-passe do armazenamento de chaves (PKCS#12, JKS)"},
	};
}
