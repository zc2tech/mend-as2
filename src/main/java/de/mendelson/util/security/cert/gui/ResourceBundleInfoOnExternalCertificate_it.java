//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleInfoOnExternalCertificate_it.java 3     9/12/24 15:51 Heller $
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
public class ResourceBundleInfoOnExternalCertificate_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"certinfo.certfile", "File di certificato: {0}"},
		{"button.cancel", "Chiudere"},
		{"title.multiple", "Informazioni sui certificati esterni"},
		{"button.ok", "Importazione >>"},
		{"certificate.exists", "Questo certificato esiste già nella gestione dei certificati, l''alias è \"{0}\"."},
		{"certinfo.index", "Certificato {0} da {1}"},
		{"certificate.doesnot.exist", "Questo certificato non esiste ancora nella gestione dei certificati."},
		{"no.certificate", "Il certificato non è stato riconosciuto"},
		{"title.single", "Informazioni su un certificato esterno"},
	};
}
