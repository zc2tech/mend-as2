//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeyPKCS12_it.java 3     9/12/24 15:51 Heller $
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
public class ResourceBundleExportKeyPKCS12_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"label.exportdir.help", "<HTML><strong>Directory di esportazione</strong><br><br>Si prega di specificare qui la directory di esportazione in cui esportare la chiave privata.<br>Per motivi di sicurezza, la chiave non viene trasferita al client, quindi è possibile solo il salvataggio sul lato server.<br><br>Il sistema creerà un file di salvataggio in questa directory che contiene un timbro con la data.</HTML>"},
		{"key.export.success.title", "Il successo"},
		{"label.exportdir.hint", "Directory in cui deve essere creato il keystore (PKCS#12)"},
		{"button.browse", "Sfogliare"},
		{"label.exportkey", "Nome del file"},
		{"key.export.error.title", "Errore"},
		{"keystore.contains.nokeys", "Questo keystore non contiene chiavi private."},
		{"label.exportdir", "Esportazione della directory"},
		{"title", "Esportazione della chiave nel keystore (formato PKCS#12)"},
		{"label.exportkey.hint", "Esportazione del file keystore da creare (PKCS#12)"},
		{"button.cancel", "Annullamento"},
		{"key.exported.to.file", "La chiave \"{0}\" è stata esportata nel file PKCS#12 \"{1}\"."},
		{"filechooser.key.export", "Selezionare la directory di esportazione sul server"},
		{"label.keypass", "password"},
		{"button.ok", "Ok"},
		{"label.alias", "chiave"},
		{"key.export.error.message", "Si è verificato un errore durante l''esportazione.\n{0}"},
		{"label.keypass.hint", "Password per il keystore esportato"},
	};
}
