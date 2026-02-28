//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundlePartnerTLSCertificateChangedController_it.java 3     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.timing;

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
public class ResourceBundlePartnerTLSCertificateChangedController_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"autoimport.tls.check.started", "È stata attivata l''importazione automatica dei certificati TLS dei partner modificati."},
		{"import.success.event.header", "Importazione automatica di un certificato TLS"},
		{"import.success.event.body", "Il sistema è configurato in modo da controllare regolarmente i partner connessi via TLS per verificare se hanno cambiato il loro certificato TLS. Se questo è il caso e il certificato TLS non esiste nel gestore di certificati TLS locale, viene importato automaticamente.\nIl sistema ha trovato un nuovo certificato per il partner \"{0}\" sotto l''URL \"{1}\" e lo ha importato con successo nel gestore di certificati TLS con l''alias \"{2}\"."},
		{"import.failed", "Non è stato possibile importare automaticamente il certificato TLS per il partner {0}: {1}"},
		{"autoimport.tls.check.stopped", "L''importazione automatica dei certificati TLS dei partner modificati è stata disattivata."},
		{"module.name", "Esame del certificato TLS"},
		{"import.success", "Il certificato TLS \"{0}\" per il partner [{1}] è stato importato automaticamente."},
	};
}
