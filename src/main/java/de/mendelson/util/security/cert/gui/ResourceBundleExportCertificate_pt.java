//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportCertificate_pt.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleExportCertificate_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"error.empty.certificate", "Não existem dados de certificado disponíveis"},
		{"button.browse", "Navegar"},
		{"label.exportfile", "Nome do ficheiro"},
		{"title", "Exportar certificado X.509"},
		{"SSH2", "Formato SSH2 (chave pública, *.pub)"},
		{"certificate.export.success.message", "O certificado pôde ser exportado com êxito para\n\"{0}\""},
		{"DER", "Formato binário (DER, *.cer)"},
		{"button.cancel", "Cancelar"},
		{"certificate.export.success.title", "Sucesso"},
		{"filechooser.certificate.export", "Selecione o nome do ficheiro para a exportação."},
		{"button.ok", "Ok"},
		{"label.alias", "Apelido"},
		{"label.exportfile.hint", "Ficheiro de certificado que é gerado"},
		{"label.exportformat", "Formato"},
		{"certificate.export.error.title", "Erro de exportação"},
		{"PEM", "Formato de texto (PEM, *.cer)"},
		{"certificate.export.error.message", "A exportação do certificado falhou:\n{0}"},
		{"PEM_CHAIN", "Formato de texto (+cadeia de autenticação) (PEM, *.pem)"},
		{"PKCS#7", "Com cadeia de certificação (PKCS#7, *.p7b)"},
	};
}
