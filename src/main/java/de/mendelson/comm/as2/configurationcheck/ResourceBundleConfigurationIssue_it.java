//$Header: /as2/de/mendelson/comm/as2/configurationcheck/ResourceBundleConfigurationIssue_it.java 4     21/02/25 16:04 Heller $
package de.mendelson.comm.as2.configurationcheck;

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
* @version $Revision: 4 $
*/
public class ResourceBundleConfigurationIssue_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"1", "Nessuna chiave trovata nel keystore TLS"},
		{"10", "Certificato di firma mancante di un partner remoto"},
		{"11", "Chiave di crittografia mancante di una stazione locale"},
		{"12", "Chiave di firma mancante di una stazione locale"},
		{"13", "Utilizzo di una chiave di prova disponibile pubblicamente come chiave TLS"},
		{"14", "L''uso di una Java VM a 32 bit non è consigliato per un uso produttivo, poiché la memoria heap massima è limitata a 1,3 GB."},
		{"15", "Servizio Windows avviato con l''account di sistema locale"},
		{"16", "Grande quantità di monitoraggio della directory per unità di tempo"},
		{"17", "Problema dell''elenco di blocco (TLS)"},
		{"18", "Problema dell''elenco di revoca (enc/sign)"},
		{"19", "Client e server vengono eseguiti in un unico processo"},
		{"2", "Più chiavi trovate nel keystore TLS - può essere una sola"},
		{"20", "Maniglie insufficienti per il processo del server"},
		{"3", "Il certificato è scaduto (TLS)"},
		{"4", "Il certificato è scaduto (enc/sign)"},
		{"5", "Attivare la cancellazione automatica - Il sistema contiene un numero elevato di transazioni."},
		{"6", "Assegnazione di almeno 4 core di processore al sistema"},
		{"7", "Riservare almeno 8 GB di memoria principale per il processo del server."},
		{"8", "La quantità di connessioni in uscita è impostata su 0 - il sistema NON invierà"},
		{"9", "Certificato di crittografia mancante di un partner remoto"},
		{"hint.1", "<HTML>Non è stata trovata alcuna chiave nel keystore TLS del sistema.<br>"
			+"Le chiavi si riconoscono dal simbolo della chiave che le precede quando si apre la gestione dei certificati.<br>"
			+"Nel keystore TLS è richiesta esattamente una chiave per eseguire il processo di handshake per la sicurezza della linea.<br>"
			+"Senza questa chiave, non è possibile accedere o lasciare le connessioni protette.</HTML>"},
		{"hint.10", "<HTML>A un partner di connessione non è stato assegnato un certificato di firma nella configurazione.<br>"
			+"In questo caso, non è possibile verificare le firme digitali del partner. Aprire l''amministrazione del partner e assegnargli un certificato di firma.</HTML>"},
		{"hint.11", "<HTML>La stazione locale non ha assegnato una chiave di crittografia.<br>"
			+"In questa configurazione non è possibile decriptare i messaggi in arrivo, indipendentemente dal partner.<br>"
			+"Aprire l''amministrazione del partner e assegnare una chiave privata alla stazione locale.</HTML>"},
		{"hint.12", "<HTML>La vostra stazione locale non ha assegnato una chiave di firma.<br>"
			+"In questa configurazione non è possibile firmare digitalmente i messaggi in uscita, indipendentemente dal partner.<br>"
			+"Aprire l''amministrazione del partner e assegnare una chiave privata alla stazione locale.</HTML>"},
		{"hint.13", "<HTML>Nella consegna mendelson fornisce alcune chiavi di prova.<br>"
			+"Questi sono disponibili pubblicamente sul sito web di mendelson.<br>"
			+"Se queste chiavi vengono utilizzate in modo produttivo per attività crittografiche nell''ambito del trasferimento dei dati, offrono quindi <strong>NESSUNA</strong> sicurezza.<br>"
			+"Qui è possibile inviare anche messaggi non protetti e non criptati.<br>"
			+"Se si desidera una chiave certificata, contattare l''assistenza mendelson.</HTML>"},
		{"hint.14", "<HTML>I processi Java a 32 bit non sono in grado di riservare una quantità di memoria sufficiente a mantenere il sistema stabile durante il funzionamento produttivo. Utilizzare una JVM a 64 bit.</HTML>"},
		{"hint.15", "<HTML>Si è impostato il server AS2 di mendelson come servizio di Windows e lo si avvia tramite un account di sistema locale (\"{0}\").<br>"
			+"Purtroppo, è possibile che questo utente perda i diritti sui suoi file scritti in precedenza dopo un aggiornamento di Windows, il che può portare a vari problemi di sistema.<br><br>"
			+"Si prega di impostare un utente separato per il servizio e di avviare il servizio con questo utente.</HTML>"},
		{"hint.16", "<HTML>Si è definito un gran numero di relazioni tra partner nel sistema e si monitorano le corrispondenti directory in uscita a intervalli troppo brevi.<br>"
			+"Attualmente vengono attivati {0} orologi di directory al minuto, il sistema non riesce a tenere il passo con questo ritmo elevato.<br>"
			+"Si prega di ridurre questo valore aumentando gli intervalli di monitoraggio delle rispettive directory dei partner e disattivando il monitoraggio per i partner in cui non è necessario.Con un numero elevato di partner, si consiglia di disattivare il monitoraggio di tutte le directory e di creare i lavori di invio dal backend utilizzando i comandi <i>AS2Send.exe</i> o <i>as2send.sh</i> a seconda delle necessità.</HTML>"},
		{"hint.17", "<HTML>I certificati autenticati contengono un link a un elenco di revoca che può essere utilizzato per dichiarare il certificato non valido. Ad esempio, se il certificato è stato compromesso.<br>"
			+"Si è verificato un problema nella verifica dell''elenco di revoca del seguente certificato TLS o il certificato è stato revocato:<br>"
			+"<strong>{0}</strong><br><br>"
			+"Ulteriori informazioni su questo certificato<br><br>"
			+"Alias: {1}<br>"
			+"Emittente: {2}<br>"
			+"Impronta digitale (SHA-1): {3}<br><br><br>"
			+"Si noti che il controllo automatico delle CRL può essere disattivato nelle impostazioni.</HTML>"},
		{"hint.18", "<HTML>I certificati autenticati contengono un link a un elenco di revoca dei certificati (CRL), che può essere utilizzato per dichiarare il certificato non valido. Ad esempio, se il certificato è stato compromesso.<br>"
			+"Si è verificato un problema nella verifica dell''elenco di revoca del seguente certificato enc/sign o il certificato è stato revocato:<br>"
			+"<strong>{0}</strong><br><br>"
			+"Ulteriori informazioni su questo certificato<br><br>"
			+"Alias: {1}<br>"
			+"Emittente: {2}<br>"
			+"Impronta digitale (SHA-1): {3}<br><br><br>"
			+"Si noti che il controllo automatico delle CRL può essere disattivato nelle impostazioni.</HTML>"},
		{"hint.19", "<HTML>Si sono avviati il client e il server del prodotto in un unico processo. Non è consigliabile fare questo nel funzionamento produttivo. Poiché le risorse sono assegnate staticamente ai programmi, in questo caso si hanno meno risorse per il funzionamento del server e del client.<br><br>"
			+"Avviare prima il processo del server e poi connettersi al client separatamente.</HTML>"},
		{"hint.2", "<HTML>Il keystore TLS del sistema contiene diverse chiavi. Tuttavia, può essercene solo una, utilizzata come chiave TLS all''avvio del server.<br>"
			+"Eliminare le chiavi dal keystore TLS finché non ne rimane una sola.<br>"
			+"È possibile riconoscere le chiavi nella gestione dei certificati dal simbolo della chiave nella prima colonna.<br>"
			+"Dopo questa modifica, è necessario riavviare il server.</HTML>"},
		{"hint.20", "<HTML>È possibile limitare il numero di porte e file aperti per utente nel sistema operativo.<br>"
			+"L''utente del processo corrente può utilizzare solo {0} handle, che sono troppo pochi per il funzionamento del server. Il processo server utilizza attualmente {1} handle.<br>"
			+"In Linux, è possibile visualizzare questo valore con \"ulimit -n\".<br><br>"
			+"Estendere il valore massimo degli handle disponibili per questo processo ad almeno {2}.</HTML>"},
		{"hint.3", "<HTML>I certificati hanno una durata limitata. Di solito si tratta di uno, tre o cinque anni.<br>"
			+"Un certificato utilizzato nel sistema per la sicurezza della linea TLS non è più valido.<br>"
			+"Non è possibile eseguire operazioni crittografiche con un certificato scaduto, pertanto è necessario provvedere al rinnovo del certificato o alla creazione o autenticazione di un nuovo certificato.<br><br>"
			+"<strong>Informazioni aggiuntive sul certificato:</strong<br><br>"
			+"Alias: {0}<br>"
			+"Emittente: {1}<br>"
			+"Impronta digitale (SHA-1): {2}<br>"
			+"Valido da: {3}<br>"
			+"Valido fino a: {4}<br><br>"
			+"</HTML>"},
		{"hint.4", "<HTML>I certificati hanno una durata limitata. Di solito si tratta di uno, tre o cinque anni.<br>"
			+"Un certificato utilizzato nel sistema per un partner per criptare/decriptare i dati, per la firma digitale o per verificare una firma digitale non è più valido.<br>"
			+"Non è possibile eseguire operazioni crittografiche con un certificato scaduto, pertanto è necessario provvedere al rinnovo del certificato o alla creazione o autenticazione di un nuovo certificato.<br><br>"
			+"<strong>Informazioni aggiuntive sul certificato:</strong<br><br>"
			+"Alias: {0}<br>"
			+"Emittente: {1}<br>"
			+"Impronta digitale (SHA-1): {2}<br>"
			+"Valido da: {3}<br>"
			+"Valido fino a: {4}<br><br>"
			+"</HTML>"},
		{"hint.5", "<HTML>Nelle impostazioni è possibile definire per quanto tempo le transazioni devono rimanere nel sistema.<br>"
			+"Più transazioni rimangono nel sistema, più risorse sono necessarie per l''amministrazione.<br>"
			+"È quindi opportuno utilizzare le impostazioni per assicurarsi di non avere mai più di 30000 transazioni nel sistema.<br>"
			+"Si noti che non si tratta di un sistema di archiviazione, ma di un adattatore di comunicazione.<br>"
			+"È possibile accedere a tutti i registri delle transazioni passate tramite la funzione di ricerca integrata nel registro del server.</HTML>"},
		{"hint.6", "<HTML>Per migliorare la produttività, è necessario che i diversi compiti siano eseguiti in parallelo nel sistema.<br>"
			+"È quindi necessario riservare al processo un numero corrispondente di core della CPU.</HTML>"},
		{"hint.7", "<HTML>Questo programma è scritto in Java.<br>"
			+"Indipendentemente dalla configurazione fisica del computer, è necessario riservare una quantità di memoria adeguata per il processo del server. Nel vostro caso, avete riservato una quantità di memoria troppo bassa.<br>"
			+"Consultare la guida (sezione Installazione): spiega come riservare la memoria corrispondente per quale metodo di avvio.<br><br>"
			+"In ogni caso, assicuratevi di non riservare al processo del server una quantità di memoria superiore alla memoria principale del vostro sistema. In caso contrario, il software diventerà quasi inutilizzabile, perché il sistema scambia continuamente memoria con il disco rigido.</HTML>"},
		{"hint.8", "<HTML>Si sono apportate modifiche alla configurazione per cui le connessioni in uscita non sono attualmente possibili.<br>"
			+"Se si desidera stabilire connessioni in uscita con i partner, il numero di connessioni possibili deve essere impostato ad almeno 1.</HTML>"},
		{"hint.9", "<HTML>A un partner di connessione non è stato assegnato un certificato di crittografia nella configurazione.<br>"
			+"In questo caso, non è possibile crittografare i messaggi al partner. Aprire l''amministrazione del partner e assegnargli un certificato di crittografia.</HTML>"},
	};
}
