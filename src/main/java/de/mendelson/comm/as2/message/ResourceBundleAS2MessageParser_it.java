//$Header: /mec_as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessageParser_it.java 5     21/03/25 9:12 Heller $
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
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ResourceBundleAS2MessageParser_it extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"inbound.connection.syncmdn", "È stato ricevuto un MDN sincrono sul canale posteriore della connessione in uscita."},
        {"original.filename.found", "Il nome originale del file è stato trasmesso dal mittente come \"{0}\"."},
        {"msg.incoming.identproblem", "La trasmissione in arrivo è un messaggio AS2. Non è stato elaborato perché c''è stato un problema con l''identificazione del partner."},
        {"mdn.incoming.relationship", "La trasmissione in arrivo è una conferma di ricezione (MDN) [{0}]"},
        {"msg.already.processed", "È già stato elaborato un messaggio con il numero di messaggio [{0}]."},
        {"message.signature.using.alias", "Utilizzare il certificato \"{0}\" del partner remoto \"{1}\" per verificare la firma digitale del messaggio AS2 in arrivo."},
        {"found.attachments", "Nel messaggio AS2 sono stati trovati allegati con dati utente {0}."},
        {"contentmic.match", "Il Message Integrity Code (MIC) corrisponde al messaggio AS2 inviato."},
        {"mdn.details", "Dettagli dell''avviso di ricevimento (MDN) ricevuto da {0}: \"{1}\"."},
        {"msg.incoming.ha", "La trasmissione in arrivo è un messaggio AS2 [{0}], dimensione dati grezzi: {1}, elaborato da [{2}]."},
        {"mdn.signed.error", "La conferma di ricezione in arrivo (MDN) è firmata digitalmente contrariamente alla configurazione del partner \"{0}\"."},
        {"mdn.notsigned", "La ricevuta di ritorno (MDN) non è firmata digitalmente."},
        {"mdn.signature.using.alias", "Utilizzare il certificato \"{0}\" del partner remoto \"{1}\" per verificare la firma digitale dell''MDN in arrivo."},
        {"signature.analyzed.digest", "Il mittente ha utilizzato l''algoritmo \"{0}\" per la firma digitale."},
        {"mdn.signature.ok", "La firma digitale dell''MDN ricevuto è stata verificata con successo."},
        {"mdn.signature.failure", "La verifica della firma digitale dell''MDN ricevuto non è riuscita - {0}"},
        {"data.compressed.expanded", "I dati utente compressi del messaggio AS2 in arrivo sono stati espansi da {0} a {1}."},
        {"mdn.state", "Lo stato della ricevuta di ritorno (MDN) è [{0}]."},
        {"msg.signed", "Il messaggio AS2 in arrivo è firmato digitalmente."},
        {"inbound.connection.tls", "Connessione TLS in arrivo da [{0}] alla porta {1} [{2}, {3}]"},
        {"mdn.answerto", "L''avviso di ricevimento in arrivo (MDN) con il numero di messaggio \"{0}\" è la risposta al messaggio AS2 in uscita \"{1}\"."},
        {"msg.notsigned", "Il messaggio AS2 in arrivo non è firmato digitalmente."},
        {"decryption.inforequired", "Per decifrare il messaggio AS2 in arrivo è necessaria una chiave con i seguenti parametri:\n{0}"},
        {"invalid.original.filename.log", "Rilevato un nome di file originale non valido nella transazione. \"{0}\" viene sostituito da \"{1}\" e l''elaborazione continua."},
        {"filename.extraction.error", "Non è possibile estrarre il nome del file originale del messaggio AS2 in arrivo: \"{0}\" viene ignorato."},
        {"mdn.incoming.ha", "La trasmissione in arrivo è una conferma di ricezione (MDN), elaborata da [{0}]."},
        {"mdn.incoming", "La trasmissione ricevuta è una conferma di ricezione (MDN)."},
        {"mdn.incoming.relationship.ha", "La trasmissione in arrivo è una conferma di ricezione (MDN) [{0}], elaborata da [{1}]."},
        {"mdn.signed", "La conferma di ricezione (MDN) è firmata digitalmente ({0})."},
        {"signature.analyzed.digest.failed", "Il sistema non è riuscito a scoprire l''algoritmo di firma del messaggio AS2 in arrivo."},
        {"msg.encrypted", "Il messaggio AS2 in arrivo è criptato."},
        {"contentmic.failure", "Il Message Integrity Code (MIC) non corrisponde al messaggio AS2 inviato (atteso: {0}, ricevuto: {1})."},
        {"data.unable.to.process.content.transfer.encoding", "Sono stati ricevuti dati che non possono essere elaborati perché contengono errori. La codifica di trasferimento del contenuto \"{0}\" è sconosciuta."},
        {"original.filename.undefined", "Il nome del file originale non è stato trasferito."},
        {"decryption.infoassigned", "Per decifrare il messaggio AS2 in arrivo è stata utilizzata una chiave con i seguenti parametri (alias \"{0}\"):\n{1}"},
        {"data.not.compressed", "I dati AS2 ricevuti non sono compressi."},
        {"inbound.connection.raw", "Connessione in arrivo da [{0}] alla porta {1}"},
        {"mdn.unsigned.error", "L''avviso di ricevimento in arrivo (MDN) NON è firmato digitalmente, contrariamente alla configurazione del partner \"{0}\"."},
        {"msg.incoming", "La trasmissione in arrivo è un messaggio AS2 [{0}], dimensione dati grezzi: {1}"},
        {"found.cem", "Il messaggio ricevuto è una richiesta di scambio di certificati (CEM)."},
        {"msg.notencrypted", "Il messaggio AS2 in arrivo non è criptato."},
        {"message.signature.ok", "La firma digitale del messaggio AS2 in arrivo è stata verificata con successo."},
        {"mdn.unexpected.messageid", "L''avviso di ricevimento (MDN) ricevuto fa riferimento al messaggio AS2 del numero di riferimento \"{0}\", che non si aspetta un MDN."},
        {"message.signature.failure", "Verifica della firma digitale del messaggio AS2 in arrivo fallita - {0}"},
        {"decryption.done.alias", "I dati del messaggio AS2 in arrivo sono stati decifrati utilizzando la chiave \"{0}\" della stazione locale \"{3}\", l''algoritmo di cifratura era \"{1}\", l''algoritmo di cifratura della chiave era \"{2}\"."},
        {"invalid.original.filename.title", "Nome del file originale non valido rilevato nella transazione"},
        {"invalid.original.filename.body", "Il sistema ha estratto un nome di file originale non valido nella transazione {0} da {1} a {2}.\nIl nome di file \"{3}\" trovato è stato sostituito con \"{4}\" e l''elaborazione è proseguita con questo nuovo nome di file. Questo può avere un impatto sul flusso di elaborazione, poiché i nomi dei file a volte contengono metadati."},};
}
