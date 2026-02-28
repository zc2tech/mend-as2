//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesInterface_it.java 6     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.preferences;

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
* @version $Revision: 6 $
*/
public class ResourceBundlePreferencesInterface_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.outboundstatusfiles.help", "<HTML><strong>File di stato per le transazioni in uscita</strong><br><br>Se si attiva questa opzione, per ogni transazione in uscita viene scritto un file di stato nella directory \"outboundstatus\".<br>Questo file viene utilizzato a scopo di integrazione e contiene informazioni sulla transazione in questione. Tra queste, ad esempio, lo stato della transazione, il numero del messaggio, l''ID del mittente e del destinatario.<br>Il nome del file di stato contiene il numero del messaggio e termina con \".sent.state\". Dopo l''invio dei dati, è possibile analizzare questo file e vedere lo stato della transazione.</HTML>"},
		{"label.checkrevocationlists.help", "<HTML><strong>Certificati: controllare gli elenchi di revoca</strong><br><br>Un elenco di revoca è un elenco di certificati che sono stati dichiarati non validi a causa di vari problemi di sicurezza. Questi problemi possono essere, ad esempio, la compromissione della chiave privata, la perdita del certificato o il sospetto di attività fraudolente. Gli elenchi di revoca sono gestiti dalle autorità di certificazione o da altre entità fidate autorizzate a emettere certificati. Il controllo degli elenchi di revoca è importante per garantire che i certificati utilizzati in una connessione o per un''operazione crittografica siano validi e affidabili. Un certificato presente in un elenco di revoca non dovrebbe più essere utilizzato per operazioni crittografiche, poiché è potenzialmente insicuro e potrebbe comportare rischi per l''integrità della comunicazione.<br><br>È possibile utilizzare questa impostazione per determinare se il sistema controlla anche gli elenchi di revoca nel controllo della configurazione.</HTML>"},
		{"label.showsecurityoverwrite", "Gestione partner: sovrascrivere le impostazioni di sicurezza della stazione locale"},
		{"label.showquota", "Gestione partner: visualizzazione della configurazione delle notifiche (quota)"},
		{"label.cem", "Consentire lo scambio di certificati (CEM)"},
		{"label.outboundstatusfiles", "File di stato per le transazioni in uscita"},
		{"label.showhttpheader", "Gestione partner: visualizzazione della configurazione dell''intestazione HTTP"},
		{"autoimport.tls.help", "<HTML><strong>Certificati TLS: importazione automatica se modificati</strong><br><br>Se la connessione di un partner è realizzata tramite HTTPS (TLS, l''URL inizia con \"https\"), è possibile verificare regolarmente se il certificato TLS del partner è cambiato. Se è stato modificato e non è ancora presente nel vostro sistema, viene importato automaticamente con l''intera catena di autenticazione.<br>Il sistema controlla i certificati dei partner ogni 15 minuti. Potrebbe quindi essere necessario un certo tempo prima che venga riconosciuta una modifica al certificato TLS di un partner.<br><br>È possibile eseguire questo processo anche manualmente, eseguendo un test di connessione con un partner e quindi importando i certificati TLS mancanti.<br><br>Si noti che questa è un''impostazione problematica a livello di sicurezza, perché ci si fida automaticamente di un certificato che viene trovato, senza chiederlo.</HTML>"},
		{"label.checkrevocationlists", "Certificati: controllare gli elenchi di revoca"},
		{"autoimport.tls", "Certificati TLS: importazione automatica se modificati"},
		{"label.showsecurityoverwrite.help", "<HTML><strong>Sovrascrittura delle impostazioni di sicurezza della stazione locale</strong><br><br>Se si attiva questa opzione, nell''amministrazione dei partner viene visualizzata una scheda aggiuntiva per ogni partner.<br>Ciò consente di definire le chiavi private che vengono utilizzate per questo partner in entrata e in uscita in ogni caso, indipendentemente dalle impostazioni della rispettiva stazione locale.<br>Questa opzione consente di utilizzare chiavi private diverse per ogni partner della stessa stazione locale.<br><br>Questa è un''opzione per la compatibilità con altri prodotti AS2 - alcuni sistemi hanno esattamente questi requisiti, ma richiedono una configurazione di relazioni di partner e non di singoli partner.</HTML>"},
		{"label.showhttpheader.help", "<HTML><strong>Visualizzazione della configurazione dell''intestazione HTTP</strong><br><br>Se si attiva questa opzione, nell''amministrazione del partner viene visualizzata una scheda aggiuntiva per ogni partner, in cui è possibile definire intestazioni HTTP definite dall''utente per l''invio di dati a questo partner.</HTML>"},
	};
}
