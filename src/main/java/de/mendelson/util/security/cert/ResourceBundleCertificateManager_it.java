//$Header: /oftp2/de/mendelson/util/security/cert/ResourceBundleCertificateManager_it.java 3     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert;

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
public class ResourceBundleCertificateManager_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"certificate.not.found.fingerprint.withinfo", "Il certificato con l''impronta SHA-1 \"{0}\" non esiste nel sistema. ({1})"},
		{"keystore.PKCS12", "Portachiavi di crittografia/firma"},
		{"event.certificate.deleted.body", "Il seguente certificato è stato eliminato dal sistema:\n\n{0}"},
		{"certificate.not.found.subjectdn.withinfo", "Il certificato con il subjectDN \"{0}\" non esiste nel sistema. ({1})"},
		{"certificate.not.found.ski.withinfo", "Il certificato con l''identificatore della chiave oggetto \"{0}\" non esiste nel sistema. ({1})"},
		{"keystore.PKCS11", "HSM/PKCS#11"},
		{"alias.hasno.key", "Il file del certificato non contiene una chiave con l''alias \"{0}\"."},
		{"keystore.reloaded", "({0}) Il file del certificato è stato ricaricato, tutte le chiavi e i certificati sono stati aggiornati."},
		{"event.certificate.added.subject", "{0}: è stato aggiunto un nuovo certificato (alias \"{1}\")"},
		{"event.certificate.modified.subject", "{0}: L''alias di un certificato è stato modificato"},
		{"access.problem", "Problemi di accesso a {0}"},
		{"keystore.read.failure", "Il sistema non è in grado di leggere i certificati/le chiavi memorizzati. Messaggio di errore: \"{0}\". Assicurarsi di aver impostato la password corretta del keystore."},
		{"event.certificate.deleted.subject", "{0}: Un certificato è stato cancellato (alias \"{1}\")"},
		{"alias.notfound", "Il file di certificato non contiene un certificato con l''alias \"{0}\"."},
		{"certificate.not.found.fingerprint", "Il certificato con l''impronta SHA-1 \"{0}\" non esiste."},
		{"event.certificate.added.body", "È stato aggiunto al sistema un nuovo certificato con i seguenti dati:\n\n{0}"},
		{"event.certificate.modified.body", "L''alias del certificato \"{0}\" verrebbe modificato in \"{1}\".\n\n\nQuesti sono i dati del certificato:\n\n{2}"},
		{"keystore.JKS", "Deposito chiavi TLS"},
		{"alias.hasno.privatekey", "Il file del certificato non contiene una chiave privata con l''alias \"{0}\"."},
		{"certificate.not.found.issuerserial.withinfo", "È richiesto il certificato con l''emittente \"{0}\" e il numero di serie \"{1}\", ma non esiste nel sistema ({2})."},
	};
}
