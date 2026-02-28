//$Header: /oftp2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleGenerateKey_it.java 3     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert.gui.keygeneration;

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
public class ResourceBundleGenerateKey_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"label.size", "Lunghezza della chiave"},
		{"label.commonname.help", "<HTML><strong>Nome comune</strong><br><br>Questo è il nome del proprio dominio come corrisponde alla voce DNS. Questo parametro è importante per l''handshake di una connessione TLS. È possibile (ma non consigliato!) inserire un indirizzo IP. È anche possibile creare un certificato wildcard sostituendo parti del dominio con *. Se si desidera utilizzare questa chiave come chiave TLS e questa voce si riferisce a un dominio inesistente o non corrisponde al proprio dominio, la maggior parte dei sistemi dovrebbe interrompere le connessioni TLS in arrivo.</HTML>"},
		{"label.locality.hint", "(Città)"},
		{"label.commonname", "Nome comune"},
		{"title", "Generazione di chiavi"},
		{"label.extension.ski.help", "<HTML><strong>SKI</strong><br><br>Ci sono diversi modi per identificare un certificato: tramite l''hash del certificato, l''emittente, il numero di serie e l''identificatore della chiave del soggetto (SKI). Lo SKI fornisce un identificatore univoco per il richiedente del certificato ed è spesso utilizzato quando si lavora con la firma digitale XML o nell''ambito della sicurezza dei servizi web in generale. Questa estensione con l''OID 2.5.29.14 è quindi spesso richiesta per AS4.</HTML>"},
		{"label.mailaddress.help", "<HTML><strong>Indirizzo e-mail</strong><br><br>Questo è l''indirizzo e-mail collegato alla chiave. Tecnicamente, questo parametro non ha alcun interesse. Tuttavia, se si desidera che la chiave sia autenticata, questo indirizzo e-mail viene solitamente utilizzato per comunicare con la CA. Inoltre, l''indirizzo di posta elettronica dovrebbe essere sul dominio del server e corrispondere a qualcosa come webmaster@dominio o simile, perché la maggior parte delle CA lo utilizza per verificare se si è in possesso del dominio associato.</HTML>"},
		{"label.validity.help", "<HTML><strong>Validità in giorni</strong><br><br>Questo valore è interessante solo per le chiavi autofirmate. In caso di autenticazione, la CA sovrascriverà questo valore.</HTML"},
		{"warning.mail.in.domain", "L''indirizzo e-mail non fa parte del dominio \"{0}\" (ad esempio, myname@{0}).\nQuesto può essere un problema se la chiave deve essere autenticata in seguito."},
		{"label.state", "Paese"},
		{"button.ignore", "Ignorare gli avvisi"},
		{"label.locality", "Posizione"},
		{"label.subjectalternativenames", "Nomi alternativi del richiedente"},
		{"label.mailaddress", "Indirizzo postale"},
		{"label.namedeccurve.help", "<HTML><strong>Curva</strong><br> Qui si seleziona il nome della curva EC da utilizzare per la generazione della chiave. La lunghezza della chiave desiderata è solitamente parte del nome della curva, ad esempio la chiave della curva \"BrainpoolP256r1\" ha una lunghezza di 256 bit. La curva più comunemente utilizzata a partire dal 2022 (circa il 75% di tutti i certificati CE su Internet la utilizza) è la NIST P-256, che si può trovare qui sotto il nome di \"Prime256v1\". A partire dal 2022, questa è la curva standard di OpenSSL.</HTML>"},
		{"label.namedeccurve", "Curva"},
		{"warning.title", "Possibile problema con i parametri chiave"},
		{"warning.nonexisting.domain", "Il dominio \"{0}\" non esiste."},
		{"label.purpose", "Estensioni chiave"},
		{"label.keytype.help", "<HTML><strong>Tipo di chiave</strong><br><br>Questo è l''algoritmo utilizzato per creare la chiave. A seconda dell''algoritmo, vi sono vantaggi e svantaggi per le chiavi risultanti.<br>A partire dal 2022, si consiglia una chiave RSA con una lunghezza di 2048 o 4096 bit.</HTML>"},
		{"label.extension.ski", "Identificatore chiave del soggetto (SKI)"},
		{"label.countrycode", "Codice paese"},
		{"button.reedit", "Rivedere"},
		{"label.signature.help", "<HTML><strong>Firma</strong><br><br>Questo è l''algoritmo di firma con cui viene firmata la chiave. È necessario per i test di integrità della chiave stessa. Questo parametro non ha nulla a che fare con le capacità di firma della chiave: ad esempio, è possibile creare firme SHA-2 con una chiave firmata SHA-1 o viceversa.<br>Si consiglia una chiave firmata SHA-2 a partire dal 2024.<br><br><strong>Breve panoramica: SHA-1, SHA-2, SHA-3 e RSASSA-PSS</strong><br><br><strong>SHA-1</strong>: un vecchio algoritmo di hash che oggi è considerato insicuro.<br><strong>SHA-2</strong>: una versione più moderna e sicura di SHA che esiste in diverse varianti come SHA-256 e SHA-512.<br><strong>SHA-3</strong>: l''algoritmo di hash più recente, basato su una struttura diversa rispetto a SHA-1 e SHA-2 e ancora più sicuro contro gli attacchi.<br><strong>RSASSA-PSS (Probabilistic Signature Scheme)</strong>: si tratta di un''estensione di RSA. Combina la funzione di hash SHA con la procedura di firma PSS, che offre una sicurezza aggiuntiva.</HTML>"},
		{"view.expert", "Il punto di vista dell''esperto"},
		{"label.validity", "Validità in giorni"},
		{"button.cancel", "Demolizione"},
		{"label.purpose.ssl", "TLS"},
		{"label.purpose.encsign", "Crittografia e firma digitale"},
		{"button.ok", "Ok"},
		{"label.countrycode.hint", "(2 caratteri, ISO 3166)"},
		{"warning.invalid.mail", "L''indirizzo di posta \"{0}\" non è valido."},
		{"label.keytype", "Tipo di chiave"},
		{"label.signature", "Firma"},
		{"label.commonname.hint", "(nome di dominio del server)"},
		{"label.organisationname", "Organizzazione (nome)"},
		{"view.basic", "Vista standard"},
		{"label.size.help", "<HTML><strong>Lunghezza della chiave</strong><br><br>Questa è la lunghezza della chiave. In linea di principio, le operazioni crittografiche con chiavi di lunghezza maggiore sono più sicure di quelle con chiavi di lunghezza minore. Tuttavia, lo svantaggio di chiavi di lunghezza elevata è che le operazioni crittografiche richiedono molto più tempo, il che può rallentare significativamente l''elaborazione dei dati a seconda della potenza di calcolo.<br>Si consiglia una chiave con una lunghezza di 2048 o 4096 bit a partire dal 2022.</HTML>"},
		{"label.organisationunit", "Organizzazione (Unità)"},
	};
}
