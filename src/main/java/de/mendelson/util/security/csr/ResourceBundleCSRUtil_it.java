//$Header: /oftp2/de/mendelson/util/security/csr/ResourceBundleCSRUtil_it.java 3     9/12/24 15:51 Heller $
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
public class ResourceBundleCSRUtil_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"response.verification.failed", "Non è stato possibile verificare la catena di fiducia della risposta CSR: {0}"},
		{"verification.failed", "La verifica della richiesta di firma del certificato (CSR) creata non è riuscita."},
		{"missing.cert.in.trustchain", "Per questa operazione mancano i certificati della catena di autenticazione del sistema (certificato radice e intermedio).\nQuesti certificati vengono forniti dalla propria CA.\nImportare innanzitutto il certificato con i dati della chiave (issuer)\n{0}."},
		{"no.certificates.in.reply", "La chiave non può essere patchata, non sono stati trovati certificati nella risposta della CA."},
		{"response.chain.incomplete", "La catena di fiducia della risposta CSR è incompleta."},
		{"response.public.key.does.not.match", "La risposta della CA non corrisponde a questa chiave."},
	};
}
