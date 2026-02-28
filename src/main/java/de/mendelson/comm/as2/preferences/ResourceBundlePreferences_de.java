//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferences_de.java 101   9/12/24 16:03 Heller $
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
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @version $Revision: 101 $
 */
public class ResourceBundlePreferences_de extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        //preferences localized
        {PreferencesAS2.DIR_MSG, "Nachrichtenverzeichnis"},
        {"button.ok", "Ok"},
        {"button.cancel", "Abbrechen"},
        {"button.modify", "Bearbeiten"},
        {"button.browse", "Durchsuchen"},
        {"filechooser.selectdir", "Bitte wählen Sie das zu setzene Verzeichnis"},
        {"title", "Einstellungen"},
        {"tab.language", "Client"},
        {"tab.dir", "Verzeichnisse"},
        {"tab.security", "Sicherheit"},
        {"tab.proxy", "Proxy"},
        {"tab.misc", "Allgemein"},
        {"tab.maintenance", "Systempflege"},
        {"tab.notification", "Benachrichtigung"},
        {"tab.interface", "Module"},
        {"tab.log", "Protokoll"},
        {"tab.connectivity", "Verbindungen"},
        {"header.dirname", "Typ"},
        {"header.dirvalue", "Verzeichnis"},
        {"label.language", "Sprache"},
        {"label.language.help", "<HTML><strong>Sprache</strong><br><br>"
            + "Dies ist die Anzeigesprache des Clients. Wenn Sie Client und Server in verschiedenen Prozessen laufen lassen "
            + "(was empfohlen wird), kann die Serversprache eine andere sein.<br>"
            + "Die Sprache, die im Protokoll verwendet wird, ist immer die Sprache des Servers."
            + "</HTML>"},
        {"label.country", "Land/Region"},
        {"label.country.help", "<HTML><strong>Land/Region</strong><br><br>"
            + "Diese Einstellung steuert im Wesentlichen nur das Datumsformat, das für die Anzeige von Transaktionsdaten usw. im Client verwendet wird."
            + "</HTML>"},
        {"label.displaymode", "Darstellung"},
        {"label.displaymode.help", "<HTML><strong>Darstellung</strong><br><br>"
            + "Hiermit setzen Sie einen der unterstützten Darstellungsmodi des Clients.<br>"
            + "Dies läßt sich ebenfalls per Kommandozeilenparameter beim Aufruf setzen."
            + "</HTML>"},
        {"label.keystore.https.pass", "Keystore Passwort (zum Senden via Https):"},
        {"label.keystore.pass", "Keystore Password (Verschlüsselung/digitale Signatur):"},
        {"label.keystore.https", "Keystore (zum Senden via Https):"},
        {"label.keystore.encryptionsign", "Keystore( Verschl., Signatur):"},
        {"label.proxy.url", "Proxy URL"},
        {"label.proxy.url.hint", "Proxy IP oder Domain"},
        {"label.proxy.port.hint", "Port"},
        {"label.proxy.user", "Benutzer"},
        {"label.proxy.user.hint", "Proxy Login Benutzer"},
        {"label.proxy.pass", "Passwort"},
        {"label.proxy.pass.hint", "Proxy Login Passwort"},
        {"label.proxy.use", "HTTP Proxy für ausgehende HTTP/HTTPs Verbindungen benutzen"},
        {"label.proxy.useauthentification", "Authentifizierung für Proxy benutzen"},
        {"filechooser.keystore", "Bitte wählen Sie die Keystore Datei (JKS Format)."},
        {"label.days", "Tage"},
        {"label.autodelete", "Automatisches Löschen"},
        {"label.deletemsgolderthan", "Von Transaktionseinträgen, die älter sind als"},
        {"label.deletemsglog", "Automatisches Löschen von Dateien und Logeinträgen protokollieren"},
        {"label.deletemsglog.help", "<HTML><strong>Automatisches Löschen von Dateien und Logeinträgen protokollieren</strong><br><br>"
            + "In den Einstellungen haben Sie die Möglichkeit, alte Dateien löschen zu lassen (Systempflege).<br>"
            + "Haben Sie dies eingerichtet "
            + "und schalten diese Option ein, wird jeder Löschvorgang einer alten Datei protokolliert.<br>"
            + "Zudem wird auch für ein "
            + "Systemereignis generiert, was Sie über die Benachrichtigungsfunktion von diesem Vorgang in Kenntnis setzen kann."
            + "</HTML>"},
        {"label.deletestatsolderthan", "Von Statistikdaten, die älter sind als"},
        {"label.deletelogdirolderthan", "Von Protokolldaten, die älter sind als"},
        {"label.asyncmdn.timeout", "Maximale Wartezeit auf asynchrone MDNs"},
        {"label.asyncmdn.timeout.help", "<HTML><strong>Maximale Wartezeit auf asynchrone MDNs</strong>"
            + "<br><br>Die Zeit, die das System auf eine asynchrone MDN (Message Delivery Notification) für eine gesendete AS2 Nachricht wartet, "
            + "bevor es die Transaktion in den Status \"fehlgeschlagen\" versetzt.<br>"
            + "Dieser Wert ist systemweit für alle Partner gültig.<br><br>Der Voreinstellungswert ist 30 min."
            + "</HTML>"},
        {"label.httpsend.timeout", "HTTP/S Sende-Timeout"},
        {"label.httpsend.timeout.help", "<HTML><strong>HTTP/S Sende-Timeout</strong><br><br>"
            + "Dies ist Wert für die Zeitüberschreitung der Netzwerkverbindung für ausgehende Verbindungen.<br>"
            + "Wenn nach dieser Zeit keine Verbindung zu Ihrem Partnersystem zustande gekommen ist, wird der Verbindungsversuch abgebrochen und es werden gegebenenfalls entsprechend "
            + "der Wiederholungseinstellungen später weitere Verbindungsversuche unternommen.<br><br>"
            + "Der Voreinstellungswert ist " + PreferencesAS2.getDefaultValue(PreferencesAS2.HTTP_SEND_TIMEOUT) + "ms."
            + "</HTML>"},
        {"label.min", "min"},
        {"receipt.subdir", "Unterverzeichnisse pro Partner für Nachrichtenempfang anlegen"},
        {"receipt.subdir.help", "<HTML><strong>Unterverzeichnisse für Empfang</strong><br><br>"
            + "Stellt ein, ob Daten im Verzeichnis <strong>&lt;Lokale Station&gt;/inbox</strong>"
            + " oder <strong>&lt;Lokale Station&gt;/inbox/&lt;Partnername&gt;</strong> empfangen werden sollen."
            + "</HTML>"},
        //notification
        {"checkbox.notifycertexpire", "Vor dem Auslaufen von Zertifikaten"},
        {"checkbox.notifytransactionerror", "Nach Fehlern in Transaktionen"},
        {"checkbox.notifycem", "Ereignisse beim Zertifikataustausch (CEM)"},
        {"checkbox.notifyfailure", "Nach Systemproblemen"},
        {"checkbox.notifyresend", "Nach abgewiesenen Resends"},
        {"checkbox.notifyconnectionproblem", "Bei Verbindungsproblemen"},
        {"checkbox.notifypostprocessing", "Probleme bei der Nachbearbeitung"},
        {"checkbox.notifyclientserver", "Probleme der Client-Server Verbindung"},
        {"button.testmail", "Sende Test Mail"},
        {"label.mailhost", "Mailserver (SMTP)"},
        {"label.mailhost.hint", "IP oder Domain des Servers"},
        {"label.mailport", "Port"},
        {"label.mailport.help", "<HTML><strong>SMTP Port</strong><br><br>"
            + "In der Regel ist es einer dieser Werte:<br>"
            + "<strong>25</strong> (Standard Port)<br>"
            + "<strong>465</strong> (TLS Port, überholter Wert)<br>"
            + "<strong>587</strong> (TLS Port, Standardwert)<br>"
            + "<strong>2525</strong> (TLS Port, alternativer Wert, kein Standard)"
            + "</HTML>"},
        {"label.mailport.hint", "SMTP Port"},
        {"label.mailaccount", "Mailserver Konto"},
        {"label.mailpass", "Mailserver Passwort"},
        {"label.notificationmail", "Benachrichtigungsempfänger Mailadresse"},
        {"label.notificationmail.help", "<HTML><strong>Benachrichtigungsempfänger Mailadresse</strong><br><br>"
            + "Die Mail Adresse des Empfängers der Benachrichtigung.<br>"
            + "Wenn die Benachrichtigung an mehrere Empfänger geschickt werden soll, geben Sie hier bitte eine kommaseparierte Liste von Empfangsadressen ein."
            + "</HTML>"},
        {"label.replyto", "Replyto Adresse"},
        {"label.smtpauthorization.header", "SMTP Autorisierung"},
        {"label.smtpauthorization.credentials", "Benutzer/Passwort"},
        {"label.smtpauthorization.none", "Keine"},
        {"label.smtpauthorization.oauth2.authorizationcode", "OAuth2 (Authorization code)"},
        {"label.smtpauthorization.oauth2.clientcredentials", "OAuth2 (Client credentials)"},
        {"label.smtpauthorization.user", "Benutzer"},
        {"label.smtpauthorization.user.hint", "SMTP Server Benutzername"},
        {"label.smtpauthorization.pass", "Passwort"},
        {"label.smtpauthorization.pass.hint", "SMTP Server Passwort"},
        {"label.security", "Verbindungssicherheit"},
        {"testmail.message.success", "Eine Test-eMail wurde erfolgreich an {0} versandt."},
        {"testmail.message.error", "Fehler beim Senden der Test-eMail:\n{0}"},
        {"testmail.title", "Senden einer Test-eMail"},
        {"testmail", "Test Mail"},                
        {"info.restart.client", "Sie müssen den Client neu starten, damit diese Änderungen gültig werden!"},
        {"remotedir.select", "Verzeichnis auf dem Server wählen"},
        //retry
        {"label.retry.max", "Max Anzahl der Versuche zum Verbindungsaufbau"},
        {"label.retry.max.help", "<HTML><strong>Max Anzahl der Versuche zum Verbindungsaufbau</strong>"
            + "<br><br>Dies ist die Anzahl der Wiederholungsversuche, die verwendet werden, um Verbindungen "
            + "zu einem Partner zu wiederholen, wenn eine Verbindung nicht hergestellt werden konnte.<br>"
            + "Die Wartezeit zwischen diesen Wiederholungsversuchen kann in der Eigenschaft <strong>Wartezeit zwischen "
            + "Verbindungswiederholungen</strong> eingestellt werden.<br><br>"
            + "Der Voreinstellungswert ist " + PreferencesAS2.getDefaultValue(PreferencesAS2.MAX_CONNECTION_RETRY_COUNT) + "."
            + "</HTML>"},
        {"label.retry.waittime", "Wartezeit zwischen Verbindungswiederholungen"},
        {"label.retry.waittime.help", "<HTML><strong>Wartezeit zwischen Verbindungswiederholungen</strong>"
            + "<br><br>Dies ist die Zeit in Sekunden, die das System wartet, bevor es erneut eine Verbindung "
            + "zum Partner herstellt.<br>"
            + "Ein erneuter Verbindungsversuch wird nur dann durchgeführt, wenn "
            + "es nicht möglich war, eine Verbindung zu einem Partner herzustellen (z.B. Ausfall des "
            + "Partnersystems oder Infrastrukturproblem).<br>"
            + "Die Anzahl der Verbindungswiederholungen kann "
            + "in der Eigenschaft <strong>Maximale Anzahl von Verbindungswiederholungen</strong> konfiguriert werden.<br><br>"
            + "Der Voreinstellungswert ist " + PreferencesAS2.getDefaultValue(PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S) + "s."
            + "</HTML>"},
        {"label.sec", "s"},
        {"keystore.hint", "<HTML><strong>Achtung:</strong><br>Bitte ändern Sie diese Parameter nur, wenn Sie externe Keystores "
            + "einbinden möchten. Mit veränderten Pfaden kann es zu Problemen beim Update kommen.</HTML>"},
        {"maintenancemultiplier.day", "Tag(e)"},
        {"maintenancemultiplier.hour", "Stunde(n)"},
        {"maintenancemultiplier.minute", "Minute(n)"},
        {"label.logpollprocess", "Informationen zum Pollprozess der Verzeichnisse"},
        {"label.logpollprocess.help", "<HTML><strong>Informationen zum Pollprozess der Verzeichnisse</strong><br><br>"
            + "Wenn Sie diese Option einschalten, wird jeder Pollvorgang eines Ausgangsverzeichnis im Protokoll vermerkt.<br>"
            + "Da dies sehr viele Einträge sein können, verwenden Sie diese Option bitte unter keinen Umständen "
            + "im produktiven Betrieb, sondern nur zu Testzwecken."
            + "</HTML>"},
        {"label.max.outboundconnections", "Max ausgehende parallele Verbindungen"},
        {"label.max.outboundconnections.help", "<HTML><strong>Max ausgehende parallele Verbindungen</strong><br><br>"
            + "Dies ist die Anzahl der maximalen parallelen ausgehenden Verbindungen, die Ihr "
            + "System öffnen wird.<br>"
            + "Dieser Wert dient hauptsächlich dazu, Ihr Partnersystem vor einer Überlastung "
            + "durch eingehende Verbindungen von Ihrer Seite zu schützen.<br><br>"
            + "Der Voreinstellungswert ist " + PreferencesAS2.getDefaultValue(PreferencesAS2.MAX_OUTBOUND_CONNECTIONS) + "."
            + "</HTML>"},
        {"label.max.inboundconnections", "Max eingehende parallele Verbindungen"},
        {"label.max.inboundconnections.help", "<HTML><strong>Max eingehende parallele Verbindungen</strong><br><br>"
            + "Dies ist die Anzahl der maximalen parallelen eingehenden Verbindungen, die von aussen zu Ihrer "
            + "mendelson AS2 Installation geöffnet werden dürfen. Dieser Wert gilt für die gesamte Software und ist nicht "
            + "auf einzelne Partner beschränkt.<br>"
            + "Die Einstellung wird an den eingebetteten HTTP Server weiter gegeben, "
            + "Sie müssen nach einer Änderung den AS2 Server neu starten.<br><br>"
            + "Obwohl es hier die Möglichkeit gibt, die Anzahl parallel eingehender Verbindungen zu beschränken, "
            + "sollten Sie diese Einstellung doch besser an Ihrer Firewall oder in Ihrem vorgeschalteten Proxy vornehmen - "
            + "das gilt dann für Ihr gesamtes System und nicht nur für eine einzelne Software.<br><br>"
            + "Der Voreinstellungswert ist " + PreferencesAS2.getDefaultValue(PreferencesAS2.MAX_INBOUND_CONNECTIONS) + "."
            + "</HTML>"},
        {"event.preferences.modified.subject", "Der Wert {0} der Servereinstellungen wurde modifiziert"},
        {"event.preferences.modified.body", "Alter Wert: {0}\nNeuer Wert: {1}"},
        {"event.notificationdata.modified.subject", "Die Einstellungen zur Benachrichtigung wurden verändert"},
        {"event.notificationdata.modified.body", "Die Benachrichtigungsdaten wurden von\n\n{0}\n\nnach\n\n{1}\n\n verändert."},
        {"label.maxmailspermin", "Max Anzahl von Benachrichtigungen/min"},
        {"label.maxmailspermin.help", "<HTML><strong>Max Anzahl von Benachrichtigungen/min</strong><br><br>"
            + "Um zu viele Mails zu vermeiden, können Sie Benachrichtigungen zusammenfassen, indem Sie die maximale "
            + "Anzahl von Benachrichtigungen pro Minute festlegen.<br>"
            + "Mit dieser Funktion erhalten Sie Mails, die "
            + "mehrere Benachrichtigungen enthalten."
            + "</HTML>"},
        {"systemmaintenance.deleteoldtransactions.help", "<HTML><strong>Löschen alter Transaktionseinträge</strong><br><br>Dies legt den Zeitrahmen fest, in dem die "
            + "Transaktionen und die zugehörigen temporären Daten im System verbleiben und in der Transaktionsübersicht "
            + "angezeigt werden sollen.<br>Diese Einstellungen betreffen <strong>nicht</strong> Ihre empfangenen Daten/Dateien, diese "
            + "bleiben unberührt.<br>"
            + "Für gelöschte Transaktionen ist das Transaktionsprotokoll über die Funktionalität der Logsuche "
            + "weiterhin verfügbar."
            + "</HTML>"},
        {"systemmaintenance.deleteoldstatistic.help", "<HTML><strong>Löschen alter Statistikdaten</strong><br><br>Das System sammelt Kompatibilitätsdaten "
            + "der Partnersysteme und kann diese als Statistik darstellen.<br>"
            + "Dies legt den Zeitrahmen fest, in dem diese Daten vorgehalten werden.</HTML>"},
        {"systemmaintenance.deleteoldlogdirs.help", "<HTML><strong>Löschen alter Protokollverzeichnisse</strong><br><br>"
            + "Auch wenn alte Transaktionen gelöscht wurden, lassen sich die Vorgänge noch über bestehende Protokolldateien nachvollziehen.<br>"
            + "Diese Einstellung löscht diese Protokolldateien und auch alle Dateien für Systemereignisse, die in den gleichen Zeitraum fallen.</HTML>"},
        {"label.colorblindness", "Unterstützung für Farbblindheit"},
        {"warning.clientrestart.required", "Die Client Einstellungen wurden geändert - bitte starten Sie den Client neu, damit sie gültig werden"},
        {"warning.serverrestart.required", "Bitte starten Sie den Server neu, damit diese Änderungen gültig werden."},
        {"warning.changes.canceled", "Der Benutzer hat den Einstellungsdialog abgebrochen - es wurden keine Änderungen an den Einstellungen vorgenommen."},
        {"label.darkmode", "Dunkler Modus"},
        {"label.litemode", "Heller Modus"},
        {"label.hicontrastmode", "Hoher Kontrast Modus"},
        {"label.trustallservercerts", "TLS: Allen Endserverzertifikaten Ihrer AS2 Partner vertrauen"},
        {"label.trustallservercerts.help", "<HTML><strong>TLS: Allen Endserverzertifikaten Ihrer AS2 Partner vertrauen</strong><br><br>"
            + "Normalerweise ist es für TLS erforderlich, alle Zertifikate der Trust Chain des AS2 Systems Ihres Partners in Ihrem TLS Zertifikatsmanager "
            + "zu halten.<br><br>"
            + "Wenn Sie diese Option aktivieren, vertrauen Sie beim ausgehenden Verbindungsaufbau dem Endzertifikat Ihres Partnersystems, "
            + "wenn Sie nur die zugehörigen Stamm- und Zwischenzertifikate im TLS Zertifikatsmanager vorhalten.<br>"
            + "Bitte beachten Sie, dass diese Option nur sinnvoll ist, wenn Ihr Partner ein beglaubigtes Zertifikat verwendet.<br>"
            + "Selbstsignierte Zertifikate werden ohnehin immer akzeptiert."
            + "<br><br><strong>Warnung:</strong>Die Aktivierung dieser Option senkt das Sicherheitsniveau, da Man-in-the-Middle Angriffe möglich sind.</HTML>"},
        {"label.stricthostcheck", "TLS: Strikte Prüfung des Hostnames"},
        {"label.stricthostcheck.help", "<HTML><strong>TLS: Strikte Prüfung des Hostnames</strong><br><br>"
            + "Hiermit stellen Sie ein, ob im Falle einer ausgehenden TLS Verbindung geprüft werden soll, ob der Common Name "
            + "(CN) des entfernten Zertifikats mit dem entfernten Host übereinstimmt.<br>"
            + "Diese Prüfung gilt nur für beglaubigte Zertifikate."
            + "</HTML>"},
        {"label.httpport", "HTTP Eingangsport"},
        {"label.httpport.help", "<HTML><strong>HTTP Eingangsport</strong><br><br>"
            + "Dies ist der Port für eingehende unverschlüsselte Verbindungen. Diese Einstellung wird an den eingebetteten HTTP Server weiter gegeben, "
            + "Sie müssen nach einer Änderung den AS2 Server neu starten.<br>"
            + "Der Port ist Teil der URL, an die Ihr Partner AS2 Nachrichten senden muss. Dies ist http://Host:<strong>Port</strong>/as2/HttpReceiver.<br><br>"
            + "Der Voreinstellungswert ist " + PreferencesAS2.getDefaultValue(PreferencesAS2.HTTP_LISTEN_PORT) + "."
            + "</HTML>"
        },
        {"label.httpsport", "HTTPS Eingangsport"},
        {"label.httpsport.help", "<HTML><strong>HTTPS Eingangsport</strong><br><br>"
            + "Dies ist der Port für eingehende verschlüsselte Verbindungen (TLS). "
            + "Diese Einstellung wird an den eingebetteten HTTP Server weiter gegeben, "
            + "Sie müssen nach einer Änderung den AS2 Server neu starten.<br>"
            + "Der Port ist Teil der URL, an die Ihr Partner AS2 Nachrichten senden muss. Dies ist https://Host:<strong>Port</strong>/as2/HttpReceiver<br><br>"
            + "Der Voreinstellungswert ist " + PreferencesAS2.getDefaultValue(PreferencesAS2.HTTPS_LISTEN_PORT) + "."
            + "</HTML>"
        },
        {"embedded.httpconfig.not.available", "HTTP Server nicht verfügbar oder Zugriffsprobleme auf Konfigurationsdatei"},
        {"button.mailserverdetection", "Mail Server herausfinden"},   
        {"label.loghttprequests", "Protokollierung der HTTP Anfragen des integrierten HTTP Servers"},
        {"label.loghttprequests.help", "<HTML><strong>HTTP Anfrageprotokoll</strong><br><br>"
            + "Wenn aktiviert, schreibt der eingebettete HTTP-Server (Jetty) ein Anfrageprotokoll in "
            + "die Dateien <strong>log/yyyy_MM_dd.jetty.request.log</strong>. Diese Protokolldateien "
            + "werden nicht von der Systemwartung gelöscht - bitte löschen Sie sie manuell.<br><br>"
            + "Bitte starten Sie die Software neu, damit Änderungen dieser Einstellung gültig werden."
            + "</HTML>"
        },
    };

}
