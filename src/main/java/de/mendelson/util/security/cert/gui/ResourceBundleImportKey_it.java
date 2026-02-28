//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleImportKey_it.java 3     9/12/24 15:51 Heller $
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
public class ResourceBundleImportKey_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"key.import.error.entry.exists", "Importazione non possibile: esiste già una voce per questa impronta digitale con l''alias {0}."},
		{"button.browse", "Sfogliare"},
		{"key.import.success.title", "Il successo"},
		{"key.import.error.message", "Si è verificato un errore durante il processo di importazione.\n{0}"},
		{"keystore.contains.nokeys", "Questo file di chiavi non contiene chiavi private."},
		{"multiple.keys.title", "Diverse chiavi incluse"},
		{"title", "Importazione della chiave da un file chiave (formato PKCS#12, JKS)"},
		{"button.cancel", "Demolizione"},
		{"key.import.error.title", "Errore"},
		{"enter.keypassword", "Inserire la password della chiave per \"{0}\"."},
		{"label.keypass", "password"},
		{"button.ok", "Ok"},
		{"multiple.keys.message", "Selezionare la chiave da importare"},
		{"label.importkey", "Nome del file"},
		{"filechooser.key.import", "Selezionare un file PKCS#12/JKS Electronic-Key per l''importazione."},
		{"key.import.success.message", "La chiave è stata importata con successo."},
		{"label.importkey.hint", "File chiave da importare (PKCS#12, JKS)"},
		{"label.keypass.hint", "Password del keystore (PKCS#12, JKS)"},
	};
}
