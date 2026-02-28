//$Header: /as2/de/mendelson/comm/as2/cem/ResourceBundleCEM_it.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.cem;

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
public class ResourceBundleCEM_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"TLS.cert.already.imported", "Il certificato CEM trasmesso esiste già nel sistema [TLS] (alias {0}), l''importazione è stata saltata."},
		{"cemtype.response", "Il messaggio CEM è del tipo \"Risposta certificato\"."},
		{"category.3", "SSL"},
		{"cemtype.request", "Il messaggio CEM è del tipo \"Richiesta di certificato\"."},
		{"category.2", "Firma"},
		{"state.999", "Errori di elaborazione"},
		{"category.1", "Crittografia"},
		{"cem.response.relatedrequest.found", "Il messaggio CEM si riferisce alla richiesta \"{0}\"."},
		{"ENC_SIGN.cert.imported.success", "Il certificato CEM inviato è stato importato con successo nel sistema [enc/sign] (alias {0})."},
		{"cem.response.prepared", "Messaggio di risposta CEM creato per la richiesta {0}"},
		{"cem.structure.info", "Numero di richieste di fiducia nella struttura CEM ricevute: {0}"},
		{"transmitted.certificate.info", "Il certificato trasmesso ha i parametri IssuerDN=\"{0}\" e numero di serie \"{1}\"."},
		{"cem.validated.schema", "Il messaggio CEM in arrivo è stato convalidato con successo."},
		{"state.99", "Processo annullato"},
		{"TLS.cert.imported.success", "Il certificato CEM trasmesso è stato importato con successo nel sistema [TLS] (alias {0})."},
		{"trustrequest.certificates.found", "Numero di certificati trasferiti: {0}."},
		{"cem.created.request", "La richiesta CEM è stata generata per la relazione \"{0}\"-\"{1}\". È stato incorporato il certificato con i parametri issuerDN \"{2}\" e numero di serie \"{3}\". L''uso definito è {4}."},
		{"trustrequest.working.on", "Elaborare la richiesta di fiducia {0}."},
		{"ENC_SIGN.cert.already.imported", "Il certificato CEM trasmesso esiste già nel sistema [enc/sign] (alias {0}), l''importazione è stata saltata."},
		{"trustrequest.rejected", "La risposta alla richiesta di fiducia ricevuta è stata impostata sullo stato \"Rifiutato\"."},
		{"trustrequest.accepted", "La risposta alla richiesta di fiducia ricevuta è stata impostata sullo stato \"Accettato\"."},
		{"state.3", "Accettato da {0}"},
		{"state.2", "Rifiutato da {0}"},
		{"state.1", "Nessuna risposta da {0}"},
	};
}
