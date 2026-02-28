//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportCertificate_it.java 3     9/12/24 15:51 Heller $
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
* @version $Revision: 3 $
*/
public class ResourceBundleExportCertificate_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"error.empty.certificate", "Non sono disponibili dati sui certificati"},
		{"button.browse", "Sfogliare"},
		{"label.exportfile", "Nome del file"},
		{"title", "Esportazione del certificato X.509"},
		{"SSH2", "Formato SSH2 (chiave pubblica, *.pub)"},
		{"certificate.export.success.message", "Il certificato è stato esportato con successo a\n\"{0}\""},
		{"DER", "Formato binario (DER, *.cer)"},
		{"button.cancel", "Annullamento"},
		{"certificate.export.success.title", "Il successo"},
		{"filechooser.certificate.export", "Selezionare il nome del file per l''esportazione."},
		{"button.ok", "Ok"},
		{"label.alias", "Alias"},
		{"label.exportfile.hint", "File di certificato generato"},
		{"label.exportformat", "Formato"},
		{"certificate.export.error.title", "Errore di esportazione"},
		{"PEM", "Formato testo (PEM, *.cer)"},
		{"certificate.export.error.message", "L''esportazione del certificato non è riuscita:\n{0}"},
		{"PEM_CHAIN", "Formato testo (+catena di autenticazione) (PEM, *.pem)"},
		{"PKCS#7", "Con catena di certificazione (PKCS#7, *.p7b)"},
	};
}
