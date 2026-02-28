//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessagePacker_it.java 5     17/01/25 8:41 Heller $
package de.mendelson.comm.as2.message;

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
* @version $Revision: 5 $
*/
public class ResourceBundleAS2MessagePacker_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"mdn.signed", "L''MDN in uscita è stato firmato con l''algoritmo \"{0}\", l''alias della chiave è \"{1}\" della stazione locale \"{2}\"."},
		{"message.compressed.unknownratio", "I dati utente in uscita sono stati compressi."},
		{"message.creation.error", "Non è stato possibile creare il messaggio con ID messaggio \"{0}\": {1}. Si tratta di un problema che si è già verificato quando la struttura dei messaggi in uscita è stata creata sul vostro sistema; non ha nulla a che fare con il sistema del vostro partner e non è stato fatto alcun tentativo di stabilire una connessione con il vostro partner."},
		{"signature.no.aipa", "Il processo di firma non utilizza l''attributo Algorithm Identifier Protection nella firma (come impostato nella configurazione): questo non è sicuro!"},
		{"mdn.creation.start", "Creare MDN in uscita, impostare l''Id del messaggio su \"{0}\"."},
		{"message.compressed", "I dati utente in uscita sono stati compressi da {0} a {1}."},
		{"mdn.details", "Dettagli del MDN in uscita: {0}"},
		{"mdn.created", "MDN in uscita creato per il messaggio AS2 \"{0}\", stato impostato su [{1}]."},
		{"mdn.notsigned", "L''MDN in uscita non è stato firmato."},
		{"message.signed", "Il messaggio in uscita è stato firmato digitalmente con l''algoritmo \"{1}\"; è stata utilizzata la chiave con l''alias \"{0}\" della stazione locale \"{2}\"."},
		{"message.encrypted", "Il messaggio in uscita è stato crittografato con l''algoritmo {1}, è stato utilizzato il certificato con l''alias \"{0}\" del partner remoto \"{2}\"."},
		{"message.creation.start", "Creare un messaggio AS2 in uscita, impostare l''Id del messaggio su \"{0}\"."},
		{"message.notsigned", "Il messaggio in uscita non è stato firmato digitalmente."},
		{"message.notencrypted", "Il messaggio in uscita non era criptato."},
	};
}
