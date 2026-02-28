//$Header: /as2/de/mendelson/comm/as2/partner/gui/ResourceBundlePartnerPanel_it.java 5     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.partner.gui;

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
public class ResourceBundlePartnerPanel_it extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"label.httpauth.oauth2.authorizationcode.asyncmdn", "OAuth2 (Codice di autorizzazione)"},
        {"tab.httpheader", "Intestazione HTTP"},
        {"label.as2version", "Versione AS2"},
        {"label.overwrite.security", "Sovrascrivere le impostazioni di sicurezza della stazione locale"},
        {"label.id.help", "<HTML><strong>AS2 id</strong><br><br>"
            + "L''identificatore univoco (nella vostra rete di partner) che viene utilizzato nel protocollo AS2 per identificare questo partner. Potete sceglierlo liberamente, ma assicuratevi che sia univoco, in tutto il mondo.</HTML>"},
        {"label.httpauth.credentials.message.pass", "password"},
        {"label.contact", "Contattateci"},
        {"label.keepfilenameonreceipt.help", "<HTML><strong>Mantieni il nome originale del file</strong><br><br>"
            + "Se questa opzione è attivata, il sistema cerca di estrarre il nome originale del file dai messaggi AS2 in arrivo e di salvare il file trasferito con questo nome, in modo che possa essere elaborato di conseguenza.<br>"
            + "Questa opzione funziona solo se il mittente ha aggiunto le informazioni sul nome del file originale. Se si attiva questa opzione, assicurarsi che il partner invii nomi di file univoci.<br><br>"
            + "Se il nome del file estratto non è un nome di file valido, verrà sostituito con un nome di file valido, verrà emesso un avviso di evento di sistema POSTPROCESSING e l''elaborazione continuerà.</HTML>"},
        {"label.overwrite.crypt.help", "<HTML><strong>Decifrare i messaggi in arrivo</strong><br><br>"
            + "Questa chiave viene utilizzata per decifrare i messaggi in arrivo da questo partner, invece della chiave impostata della rispettiva stazione locale.</HTML>"},
        {"label.name.help", "<HTML><strong>Nome</strong><br><br>"
            + "È il nome interno del partner così come viene utilizzato nel sistema. Non è un valore specifico del protocollo, ma viene usato per costruire nomi di file o strutture di directory che si riferiscono a questo partner.</HTML>"},
        {"label.signedmdn", "Richiesta di conferma di ricezione firmata (MDN)"},
        {"label.pollinterval", "Intervallo di raccolta"},
        {"label.compression.help", "<HTML><strong>Compressione dei dati</strong><br><br>"
            + "Se questa opzione è attivata, i messaggi in uscita vengono compressi con l''algoritmo ZLIB.<br>"
            + "Il vantaggio della compressione è che la dimensione del messaggio viene solitamente ridotta, il che porta a una trasmissione più veloce. Anche la struttura del messaggio viene modificata, il che può risolvere i problemi di compatibilità.<br>"
            + "Lo svantaggio è che si tratta di una fase di elaborazione aggiuntiva che va a scapito del throughput.<br><br>"
            + "Questa opzione richiede un sistema AS2 dall''altra parte che supporti almeno AS2 1.1.</HTML>"},
        {"label.keepfilenameonreceipt", "Mantenere il nome originale del file"},
        {"label.pollignore.help", "<HTML><strong>Ignora il ritiro per</strong><br><br>"
            + "Il monitoraggio della directory recupera ed elabora un numero definito di file dalla directory monitorata a intervalli regolari.<br>"
            + "È necessario assicurarsi che il file sia completamente disponibile in questo momento. Se si copiano regolarmente i file nella directory monitorata, è possibile che si verifichino sovrapposizioni temporali e che venga recuperato un file non ancora completamente disponibile.<br>"
            + "Pertanto, se si copiano i file nella directory monitorata utilizzando un''operazione non atomica, al momento del processo di copia è necessario selezionare un''estensione del nome del file che venga ignorata dal processo di monitoraggio.<br>"
            + "Una volta che l''intero file è disponibile nella directory monitorata, è possibile rimuovere l''estensione del nome del file con un''operazione atomica (move, mv, rename) e il file completo verrà recuperato.<br>"
            + "L''elenco delle estensioni dei nomi dei file è un elenco separato da virgole, ad esempio \"*.tmp, *.upload\".</HTML>"},
        {"label.maxpollfiles", "Numero massimo di file/processo di prelievo"},
        {"label.httpauth.credentials.asyncmdn.user", "Nome utente"},
        {"label.notify.send", "Notifica se la quota di trasmissione supera il valore seguente:"},
        {"label.httpversion", "Versione del protocollo HTTP"},
        {"label.localstation.help", "<HTML><strong>Stazione locale</strong><br><br>"
            + "Una stazione locale rappresenta il proprio sistema. È possibile creare un numero qualsiasi di stazioni locali nel proprio sistema.<br>"
            + "Le stazioni locali e i partner di connessione vengono configurati separatamente. La configurazione complessiva della relazione con il partner viene quindi creata automaticamente dalle configurazioni della stazione locale e del partner remoto.<br><br>"
            + "Esistono due tipi di partner:<br><br>"
            + "<table border=\"0\"><tr><td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/localstation.svg\" height=\"20\" width=\"20\"></td><td>Stazioni locali</td></tr><tr><td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/singlepartner.svg\" height=\"20\" width=\"20\"></td><td>Partner rimosso</td></tr></table></HTML>"},
        {"label.httpauthentication.credentials.help", "<HTML><strong>Autenticazione di accesso di base HTTP</strong><br><br>"
            + "Impostare qui l''autenticazione di base HTTP per l''accesso, se questa è abilitata sul lato del partner (definita in RFC 7617). Il sistema del partner remoto deve restituire uno stato <strong>HTTP 401 Unauthorised</strong> per le richieste non autenticate (dati di login errati, ecc.).<br>"
            + "Se la connessione al partner richiede l''autenticazione del client TLS (tramite certificati), non è necessaria alcuna impostazione.<br>"
            + "In questo caso, importare i certificati del partner tramite il gestore di certificati TLS.<br>"
            + "Il sistema si occupa quindi dell''autenticazione TLS del cliente.</HTML>"},
        {"label.subject", "Dati utente Soggetto"},
        {"label.cryptalias.key", "Chiave privata (decodifica dei dati)"},
        {"label.url.help", "<HTML><strong>L''URL di ricezione</strong><br><br>"
            + "Si tratta dell''URL del partner attraverso il quale è possibile raggiungere il suo sistema AS2.<br>"
            + "Inserire questo URL nel formato <strong>PROTOCOLLO://HOST:PORT/PFAD</strong>, dove il <strong>PROTOCOLLO</strong> deve essere uno tra \"http\" o \"https\". <strong>HOST</strong> indica l''host del server AS2 del partner. <strong>PORT</strong> è la porta di ricezione del partner. <strong>PFAD</strong> indica il percorso di ricezione, ad esempio \"/as2/HttpReceiver\" L''intera voce viene contrassegnata come non valida se il protocollo non è uno di \"http\" o \"https\", se l''URL ha un formato errato o se la porta non è definita nell''URL.<br><br>"
            + "Non inserire qui un URL che faccia riferimento al proprio sistema tramite \"localhost\" o \"127.0.0.1\": si cercherebbe di inviare i messaggi AS2 in uscita al proprio sistema.</HTML>"},
        {"label.keep.security", "Utilizzare le impostazioni di sicurezza della stazione locale"},
        {"label.cryptalias.cert.help", "<HTML><strong>Certificato del partner (crittografia dei dati)</strong<br><br>"
            + "Selezionare qui un certificato disponibile nel gestore dei certificati del sistema (firma/crittografia).<br>"
            + "Se si desidera criptare i messaggi in uscita verso questo partner, questo certificato viene usato per criptare i dati.</HTML>"},
        {"label.httpauth.oauth2.clientcredentials.asyncmdn", "OAuth2 (credenziali del cliente)"},
        {"tab.receipt", "Ricezione"},
        {"label.contenttype", "Dati utente Tipo di contenuto"},
        {"label.httpauth.credentials.message.user", "Nome utente"},
        {"label.enabledirpoll", "Monitoraggio della directory"},
        {"label.cryptalias.cert", "Certificato del partner (crittografia dei dati)"},
        {"label.algorithmidentifierprotection", "Identificatore di algoritmo Attributo di protezione"},
        {"label.httpauth.oauth2.authorizationcode.message", "OAuth2 (Codice di autorizzazione)"},
        {"partnerinfo", "Con ogni messaggio AS2, il partner invia anche informazioni sulle funzioni del proprio sistema AS2. Questo è l''elenco di tali funzioni."},
        {"label.asyncmdn", "Richiesta di conferma di ricezione asincrona (MDN)"},
        {"label.mdnurl", "URL MDN"},
        {"tab.security", "Sicurezza"},
        {"label.partnercomment", "Commento"},
        {"tab.events", "Post-elaborazione"},
        {"label.httpauth.credentials.asyncmdn.pass", "password"},
        {"label.id", "AS2 id"},
        {"label.contenttype.help", "<HTML><strong>Tipo di contenuto dei dati dell''utente</strong><br><br>"
            + "I seguenti tipi di contenuto sono supportati in modo sicuro dal protocollo AS2:<br>"
            + "application/EDI-X12<br>"
            + "application/EDIFACT<br>"
            + "application/edi-consent<br>"
            + "application/XML<br><br>"
            + "La RFC AS2 afferma che tutti i tipi di contenuto MIME dovrebbero essere supportati in AS2.<br>"
            + "Tuttavia, questo non è un requisito obbligatorio.<br>"
            + "Non si deve quindi fare affidamento su questo dato,<br>"
            + "che il sistema del vostro partner o l''elaborazione SMIME di mendelson AS2 sia in grado di gestire tipi di contenuto diversi da quelli descritti.</HTML>"},
        {"tooltip.button.addevent", "Creare un nuovo evento"},
        {"label.test.connection", "Controllare il collegamento"},
        {"label.signtype", "Firma digitale"},
        {"label.httpauth.credentials.asyncmdn", "Autenticazione HTTP di base"},
        {"label.httpauth.message", "Autenticazione dei messaggi AS2 in uscita"},
        {"label.notify.sendreceive", "Notifica se la quota di invio/ricezione supera il valore seguente:"},
        {"label.signtype.help", "<HTML><strong>Firma digitale</strong><br><br>"
            + "Qui si seleziona l''algoritmo di firma con cui devono essere firmati i messaggi in uscita verso questo partner.<br>"
            + "Se si è selezionato un algoritmo di firma, ci si aspetta un messaggio firmato in arrivo anche da questo partner.<br><br>"
            + "Il messaggio in uscita verso questo partner viene firmato utilizzando la chiave privata della stazione locale che è il mittente della transazione.</HTML>"},
        {"label.httpauth.oauth2.clientcredentials.message", "OAuth2 (credenziali del cliente)"},
        {"tab.send", "Spedizione"},
        {"tooltip.button.editevent", "Modifica evento"},
        {"tab.mdn", "MDN"},
        {"label.httpauth.asyncmdn", "Autenticazione di MDN asincrono in uscita"},
        {"tab.notification", "Notifica"},
        {"label.mdnurl.help", "<HTML><strong>MDN</strong> (<strong>M</strong> messaggio <strong>D</strong> consegna <strong>N</strong> notifica) <strong>URL</strong><br><br>"
            + "È l''URL che il partner utilizzerà per l''MDN asincrono in entrata verso questa stazione locale. Nel caso sincrono, questo valore non viene utilizzato poiché l''MDN viene inviato sul canale di ritorno della connessione in uscita.<br>"
            + "Inserire questo URL nel formato <strong>PROTOCOLLO://HOST:PORT/PFAD</strong>.<br>"
            + "<strong>PROTOCOLLO</strong> deve essere uno dei seguenti: \"http\" o \"https\".<br>"
            + "<strong>HOST</strong> si riferisce all''host del proprio server AS2.<br>"
            + "<strong>PORT</strong> è la porta di ricezione del sistema AS2.<strong>PFAD</strong> indica il percorso di ricezione, ad esempio \"/as2/HttpReceiver\".<strong>L''intera voce viene contrassegnata come non valida se il protocollo non è uno di \"http\" o \"https\", se l''URL ha un formato errato o se la porta non è definita nell''URL.<br><br>"
            + "Si prega di non inserire qui un URL che faccia riferimento al proprio sistema tramite \"localhost\" o \"127.0.0.1\": questa informazione verrà valutata dal partner dopo aver ricevuto il messaggio AS2 ed egli invierà quindi l''MDN a se stesso.</HTML>"},
        {"label.features", "Funzioni"},
        {"label.httpversion.help", "<HTML><strong>Versione del protocollo HTTP</strong><br><br>"
            + "Esistono versioni del protocollo HTTP<ul><li>HTTP/1.0 (RFC 1945)</li><li>HTTP/1.1 (RFC 2616)</li><li>HTTP/2.0 (RFC 9113)</li><li>HTTP/3.0 (RFC 9114)</li></ul>HTTP/1.1 è generalmente utilizzato per AS2.<br><br>"
            + "Nota: questa non è la versione TLS!</HTML>"},
        {"label.signalias.key", "Chiave privata (crea la firma digitale)"},
        {"label.encryptiontype.help", "<HTML><strong>Cifratura dei messaggi</strong><br><br>"
            + "Qui si seleziona l''algoritmo di crittografia con cui devono essere crittografati i messaggi in uscita verso questo partner.<br>"
            + "Se si è selezionato un algoritmo di crittografia, ci si aspetta un messaggio crittografato anche da questo partner, ma l''algoritmo di crittografia è arbitrario.<br><br>"
            + "Ulteriori informazioni sull''algoritmo di crittografia si trovano nella Guida (sezione Partner) - tutti gli algoritmi sono spiegati lì.</HTML>"},
        {"label.compression", "Compressione dei dati"},
        {"label.overwrite.crypt", "Decifrare i messaggi in arrivo"},
        {"label.email", "Indirizzo postale"},
        {"header.httpheadervalue", "Valore"},
        {"httpheader.add", "Aggiungi"},
        {"tab.misc", "Generale"},
        {"partnersystem.noinfo", "Non sono disponibili informazioni - c''è già stata una transazione?"},
        {"label.httpauth.credentials.message", "Autenticazione HTTP di base"},
        {"label.usecommandonreceipt", "Ricezione"},
        {"label.features.cem", "Scambio di certificati tramite CEM"},
        {"label.usecommandonsendsuccess", "Spedizione (riuscita)"},
        {"label.signalias.cert.help", "<HTML><strong>Certificato del partner (verifica della firma digitale)</strong<br><br>"
            + "Selezionare qui un certificato disponibile nel gestore dei certificati del sistema (firma/crittografia).<br>"
            + "Se i messaggi in arrivo da questo partner sono firmati digitalmente per una stazione locale, questo certificato viene utilizzato per verificare la firma.</HTML>"},
        {"label.mdn.description", "<HTML>La MDN (Message Delivery Notification) è la conferma del messaggio AS2. Questa sezione definisce il comportamento del partner per i messaggi AS2 in uscita.</HTML</HTML>"},
        {"label.encryptiontype", "Crittografia dei messaggi"},
        {"label.cryptalias.key.help", "<HTML><strong>Chiave privata (decodifica dei dati)</strong<br><br>"
            + "Selezionare qui una chiave privata disponibile nel gestore dei certificati del sistema (firma/crittografia).<br>"
            + "Se i messaggi in arrivo da qualsiasi partner sono criptati per questa stazione locale, questa chiave viene utilizzata per la decodifica.<br><br>"
            + "Poiché solo voi siete in possesso della chiave privata qui impostata, solo voi potete decifrare i dati che i vostri partner hanno criptato con il vostro certificato.<br>"
            + "Questo significa che qualsiasi partner può criptare i dati per voi, ma solo voi potete decriptarli.</HTML>"},
        {"label.name", "Nome"},
        {"label.signalias.cert", "Certificato del partner (verifica della firma digitale)"},
        {"label.email.help", "<HTML><strong>Indirizzo di posta elettronica</strong><br><br>"
            + "Questo valore fa parte della descrizione del protocollo AS2, ma al momento non viene utilizzato affatto.</HTML>"},
        {"label.httpauth.none", "Nessuno"},
        {"label.id.hint", "Identificazione del partner (protocollo AS2)"},
        {"label.url", "Ricezione dell''URL"},
        {"label.subject.help", "<HTML><strong>Dati dell''utente</strong><br><br>"
            + "$'{'filename} è sostituito dal nome del file inviato.<br>"
            + "Questo valore viene trasmesso nell''intestazione HTTP; si applicano le restrizioni!<br>"
            + "Utilizzare la codifica dei caratteri ISO-8859-1, solo caratteri stampabili, nessun carattere speciale.<br>"
            + "CR, LF e TAB sono sostituiti da \"\r\", \"\n\" e \"\t\".</HTML>"},
        {"label.pollignore", "Ignorare il ritiro per"},
        {"label.productname", "Nome del prodotto"},
        {"label.usecommandonsenderror", "Spedizione (difettosa)"},
        {"tab.dirpoll", "Monitoraggio della directory"},
        {"label.name.hint", "Nome del partner interno"},
        {"label.overwrite.sign.help", "<HTML><strong>Firmare i messaggi in uscita</strong><br><br>"
            + "Questa chiave viene utilizzata per firmare i messaggi in uscita verso questo partner, al posto della chiave impostata della rispettiva stazione locale.</HTML>"},
        {"title", "Configurazione del partner"},
        {"label.polldir", "Elenco monitorato"},
        {"tab.httpauth", "Autenticazione HTTP"},
        {"label.algorithmidentifierprotection.help", "<HTML><strong>Attributo di protezione dell''identificatore di algoritmo</strong><br><br>"
            + "Se si attiva questa opzione (cosa consigliata), l''attributo Algorithm Identifier Protection viene utilizzato nella firma AS2. Questo attributo è definito nella RFC 6211.<br><br>"
            + "La firma AS2 utilizzata è suscettibile di attacchi di sostituzione dell''algoritmo.<br>"
            + "In un attacco di sostituzione di algoritmo, l''attaccante modifica l''algoritmo utilizzato o i parametri dell''algoritmo per cambiare il risultato di una procedura di verifica della firma.<br>"
            + "Questo attributo contiene ora una copia degli identificatori degli algoritmi rilevanti della firma, in modo che non possano essere modificati, impedendo così un attacco di sostituzione dell''algoritmo alla firma.<br><br>"
            + "Ci sono sistemi AS2 che non riescono a gestire questo attributo (anche se l''RFC è del 2011) e segnalano un errore di autorizzazione.<br>"
            + "In questo caso, l''attributo può essere disattivato qui.</HTML>"},
        {"label.pollignore.hint", "Elenco di estensioni di file da ignorare, separate da virgole (sono ammessi i caratteri jolly)."},
        {"label.signalias.key.help", "<HTML><strong>Chiave privata (crea la firma digitale)</strong<br><br>"
            + "Selezionare qui una chiave privata disponibile nel gestore dei certificati del sistema (firma/crittografia).<br>"
            + "Questa chiave viene utilizzata per creare una firma digitale per i messaggi in uscita verso tutti i partner remoti.<br><br>"
            + "Poiché solo voi siete in possesso della chiave privata impostata qui, solo voi potete firmare i dati.<br>"
            + "I vostri partner possono verificare questa firma con il certificato - questo garantisce che i dati sono invariati e che voi siete il mittente.</HTML>"},
        {"label.notes.help", "<HTML><strong>Note</strong><br><br>"
            + "Qui troverete la possibilità di prendere appunti su questo partner per il vostro uso personale.</HTML>"},
        {"label.syncmdn", "Richiesta di conferma di ricezione sincrona (MDN)"},
        {"label.signedmdn.help", "<HTML><strong>Conferma di ricezione firmata</strong><br><br>"
            + "Con questa impostazione è possibile comunicare al sistema partner per i messaggi AS2 in uscita che si desidera una conferma di ricezione firmata (MDN).<br>"
            + "Anche se all''inizio sembra sensato, l''impostazione è purtroppo problematica.<br>"
            + "Infatti, una volta ricevuto l''MDN del partner, la transazione è completa.<br>"
            + "Se la verifica della firma dell''MDN viene poi effettuata e fallisce, non c''è più modo di informare il partner di questo problema.<br>"
            + "L''annullamento della transazione non è più possibile: la transazione è già stata completata. Ciò significa che la verifica della firma dell''MDN in modalità automatica è inutile.<br>"
            + "Il protocollo AS2 prevede che l''applicazione risolva questo problema logico, ma ciò non è possibile.<br>"
            + "La soluzione mendelson AS2 visualizza un avviso in caso di mancato controllo della firma MDN.<br><br>"
            + "C''è un''altra particolarità di questa ambientazione:<br>"
            + "Se si è verificato un problema durante l''elaborazione da parte del partner, l''MDN potrebbe essere sempre non firmato, indipendentemente da questa impostazione.</HTML>"},
        {"label.notify.receive", "Notifica se la quota di ricezione supera il valore seguente:"},
        {"label.features.ma", "Allegati multipli"},
        {"label.email.hint", "Non utilizzato o convalidato nel protocollo AS2"},
        {"label.localstation", "Stazione locale"},
        {"httpheader.delete", "Rimuovere"},
        {"tab.partnersystem", "Info"},
        {"label.features.compression", "Compressione dei dati"},
        {"label.asyncmdn.help", "<HTML><strong>Ricezione asincrona di ricezione</strong><br><br>"
            + "Il partner stabilisce una nuova connessione al sistema dell''utente per inviare la conferma del messaggio in uscita.<br>"
            + "La firma viene verificata e i dati decifrati sul lato del partner dopo la chiusura della connessione in entrata.<br>"
            + "Per questo motivo, questo metodo richiede meno risorse rispetto al metodo con MDN sincrono.</HTML>"},
        {"label.address", "Indirizzo"},
        {"label.enabledirpoll.help", "<HTML><strong>Monitoraggio della directory</strong><br><br>"
            + "Se si attiva questa opzione, il sistema cercherà automaticamente nella directory di origine i nuovi file per questo partner.<br>"
            + "Se viene trovato un nuovo file, viene generato un messaggio AS2 e inviato al partner.<br>"
            + "Si noti che questo metodo di monitoraggio delle directory può utilizzare solo parametri generali per tutte le creazioni di messaggi.<br>"
            + "Se si desidera impostare parametri speciali per ogni singolo messaggio, utilizzare il processo di invio tramite la riga di comando.<br>"
            + "In caso di funzionamento in cluster (HA), è necessario disattivare il monitoraggio delle directory, poiché questo processo non può essere sincronizzato.</HTML>"},
        {"header.httpheaderkey", "Nome"},
        {"label.overwrite.sign", "Firmare i messaggi in uscita"},
        {"label.syncmdn.help", "<HTML><strong>Ricezione sincrona di ricezione</strong><br><br>"
            + "Il partner invia la conferma di ricezione (MDN) sul canale di ritorno della connessione in uscita.<br>"
            + "La connessione in uscita viene mantenuta aperta mentre il partner decifra i dati e verifica la firma.<br>"
            + "Per questo motivo, questo metodo richiede più risorse rispetto all''elaborazione MDN asincrona.</HTML>"},};
}
