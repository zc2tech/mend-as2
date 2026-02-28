//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportPrivateKey_it.java 3     9/12/24 15:51 Heller $
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
public class ResourceBundleExportPrivateKey_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"label.exportdir.help", "<HTML><strong>Directory di esportazione</strong><br><br>Immettere qui la directory di esportazione in cui esportare la chiave privata.<br>Per motivi di sicurezza, la chiave non viene trasferita al client, quindi è possibile solo il salvataggio sul lato server.<br><br>Il sistema creerà un file di salvataggio in questa directory che contiene un timbro di data.</HTML>"},
		{"key.export.success.title", "Il successo"},
		{"label.exportdir.hint", "Directory in cui deve essere creato il file keystore/key"},
		{"button.browse", "Sfogliare"},
		{"label.exportkey", "Nome del file"},
		{"key.export.error.title", "Errore"},
		{"keystore.contains.nokeys", "Questo keystore non contiene chiavi private."},
		{"label.exportdir", "Esportazione della directory"},
		{"title", "Esportazione della chiave in un file keystore/chiave"},
		{"label.exportkey.hint", "File di esportazione da creare"},
		{"button.cancel", "Annullamento"},
		{"key.exported.to.file", "La chiave \"{0}\" è stata esportata nel file \"{1}\"."},
		{"filechooser.key.export", "Selezionare la directory di esportazione sul server"},
		{"label.keypass", "password"},
		{"button.ok", "Ok"},
		{"label.alias", "chiave"},
		{"label.exportformat", "Formato di esportazione"},
		{"key.export.error.message", "Si è verificato un errore durante l''esportazione.\n{0}"},
		{"label.keypass.hint", "Password per il file keystore/chiave esportato"},
		{"label.exportformat.help", "<HTML><strong>Formato di esportazione</strong><br><br>È possibile esportare la chiave in un formato keystore (PKCS#12) o in un file chiave codificato PEM.<br>Il formato più comune è il PKCS#12, un file chiave codificato PEM è necessario solo per casi d''uso speciali come la configurazione del reverse proxy.<br><br>Nel caso del file chiave PEM, la chiave viene memorizzata senza password.</HTML>"},
	};
}
