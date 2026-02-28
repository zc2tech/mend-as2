//$Header: /oftp2/de/mendelson/util/security/crl/ResourceBundleCRL_it.java 3     9/12/24 15:51 Heller $
package de.mendelson.util.security.crl;

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
public class ResourceBundleCRL_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"malformed.crl.url", "URL CRL errato ({0})"},
		{"failed.revoked", "Il certificato è stato revocato: {0}"},
		{"bad.crl", "I dati CRL scaricati non possono essere elaborati"},
		{"no.crl.entry", "Il certificato non ha un''estensione che rimanda a un URL CRL."},
		{"self.signed.skipped", "Autofirmato - verifica saltata"},
		{"cert.read.error", "Il certificato per l''URL dell''elenco di revoca non può essere letto"},
		{"crl.success", "Ok - Il certificato non è stato revocato."},
		{"no.https", "Problema di connessione con URI {0} - HTTPS non è supportato"},
		{"module.name", "[lista nera]"},
		{"error.url.retrieve", "L''URL dell''elenco di revoca non può essere letto dal certificato"},
		{"download.failed.from", "Il download della lista nera è fallito ({0})"},
	};
}
