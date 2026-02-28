//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeystore_it.java 3     9/12/24 15:51 Heller $
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
public class ResourceBundleExportKeystore_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"label.exportdir.help", "<HTML><strong>Directory di esportazione</strong><br><br>Si prega di specificare la directory di esportazione in cui esportare il keystore.<br>Per motivi di sicurezza, le chiavi non vengono trasferite al client,<br>in modo che sia possibile solo il salvataggio sul lato server.<br>Il sistema crea un file di salvataggio in questa directory che contiene un timbro con la data.</HTML>"},
		{"keystore.export.error.title", "Errore"},
		{"label.exportdir.hint", "Directory in cui viene creato il file keystore"},
		{"button.browse", "Sfogliare"},
		{"keystore.export.error.message", "Si è verificato un problema durante l''esportazione:\n{0}"},
		{"label.exportdir", "Esportazione della directory"},
		{"title", "Esportazione di tutte le voci in un file keystore"},
		{"button.cancel", "Annullamento"},
		{"filechooser.key.export", "Selezionare la directory di esportazione lato server"},
		{"label.keypass", "Password"},
		{"keystore.export.success.title", "Il successo"},
		{"button.ok", "Ok"},
		{"label.keypass.help", "<HTML><strong>Password del keystore esportato</strong><br><br>Questa è la password con cui viene protetto il keystore esportato lato server.<br>Si prega di inserire \"test\" se questo keystore deve essere importato automaticamente nel prodotto mendelson in seguito.</HTML>"},
		{"keystore.exported.to.file", "Il file del keystore è stato scritto in \"{0}\"."},
		{"label.keypass.hint", "Password del keystore esportato"},
	};
}
