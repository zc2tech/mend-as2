//$Header: /oftp2/de/mendelson/util/security/csr/ResourceBundleCSR_it.java 3     9/12/24 15:51 Heller $
package de.mendelson.util.security.csr;

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
public class ResourceBundleCSR_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"csr.generation.failure.message", "{0}"},
		{"cancel", "Annullamento"},
		{"label.selectcsrfile", "Selezionare il file per salvare il CSR"},
		{"csr.message.storequestion.renew", "Desidera che la chiave venga rinnovata presso il mendelson CA\no salvare la richiesta in un file?"},
		{"csr.option.1.renew", "Rinnovare a mendelson CA"},
		{"csr.title.renew", "Rinnovo del certificato: Richiesta di firma del certificato (CSR)"},
		{"csrresponse.import.failure.title", "Problema durante la patch della chiave"},
		{"ca.connection.problem", "HTTP {0}: La CA mendelson non è attualmente disponibile. Riprovare più tardi."},
		{"csr.message.storequestion", "Si desidera che la chiave sia autenticata dalla CA di mendelson\no salvare la richiesta in un file?"},
		{"csr.generation.success.title", "Il CSR è stato creato con successo"},
		{"csr.generation.failure.title", "Errori durante la creazione del CSR"},
		{"csr.generation.success.message", "La richiesta di autenticazione generata è stata salvata nel file\n\"{0}\".\nSi prega di inviare questi dati alla propria CA.\nSi consiglia la CA mendelson (http://ca.mendelson-e-c.com)."},
		{"label.selectcsrrepsonsefile", "Selezionare il file di risposta della CA"},
		{"csrresponse.import.success.title", "Il successo"},
		{"csrresponse.import.failure.message", "{0}"},
		{"csr.title", "Autenticare il certificato: Richiesta di firma del certificato (CSR)"},
		{"csr.option.2", "Salva su file"},
		{"csr.option.1", "Notarizzazione a mendelson CA"},
		{"csrresponse.import.success.message", "La chiave è stata patchata con successo con la risposta della CA."},
	};
}
