//$Header: /as2/de/mendelson/comm/as2/partner/gui/ResourceBundlePartnerPanel_de.java 96    9/12/24 16:02 Heller $
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
 * @version $Revision: 96 $
 */
public class ResourceBundlePartnerPanel_de extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"title", "Partnerkonfiguration"},
        {"label.name", "Name"},
        {"label.name.help", "<HTML><strong>Name</strong><br><br>"
            + "Dies ist der interne Name des Partners, wie er im System verwendet wird. Es handelt sich nicht um einen "
            + "protokollspezifischen Wert, sondern er wird für den Aufbau von Dateinamen oder Verzeichnisstrukturen "
            + "verwendet, die sich auf diesen Partner beziehen."
            + "</HTML>"},
        {"label.name.hint", "Interner Partnernname"},
        {"label.id", "AS2 id"},
        {"label.id.help", "<HTML><strong>AS2 id</strong><br><br>"
            + "Die (in Ihrem Partnernetzwerk) eindeutige Kennung, die im AS2 Protokoll zur Identifizierung "
            + "dieses Partners verwendet wird. Sie können diese frei wählen - stellen Sie nur sicher, dass sie "
            + "eindeutig ist, weltweit."
            + "</HTML>"},
        {"label.id.hint", "Partneridentifikation (AS2 Protokoll)"},
        {"label.partnercomment", "Kommentar"},
        {"label.url", "Empfangs-URL"},
        {"label.url.help", "<HTML><strong>Empfangs-URL</strong><br><br>"
            + "Dies ist die URL Ihres Partners, über die sein AS2 System erreichbar ist.<br>"
            + "Bitte geben Sie diese URL im Format <strong>PROTOKOLL://HOST:PORT/PFAD</strong> an, "
            + "wobei das <strong>PROTOKOLL</strong> eines von \"http\" oder \"https\" sein muß. "
            + "<strong>HOST</strong> bezeichnet den AS2 Server Host Ihres Partners. "
            + "<strong>PORT</strong> ist der Empfangsport Ihres Partners. "
            + "<strong>PFAD</strong> bezeichnet den Empfangspfad, zum Beispiel \"/as2/HttpReceiver\"."
            + "Der gesamte Eintrag wird als ungültig markiert, wenn das Protokoll nicht eines von \"http\" oder \"https\" ist, "
            + "wenn die URL ein falsches Format hat oder wenn der Port nicht in der URL definiert ist.<br><br>"
            + "Bitte geben Sie hier keine URL ein, die über Angaben von \"localhost\" oder \"127.0.0.1\" auf Ihr eigenes "
            + "System verweist - Sie würden damit versuchen, die ausgehenden AS2 Nachrichten an Ihr eigenes System zu senden."
            + "</HTML>"},
        {"label.mdnurl", "MDN URL"},
        {"label.mdnurl.help", "<HTML><strong>MDN</strong> (<strong>M</strong>essage <strong>D</strong>elivery <strong>N</strong>otification) <strong>URL</strong><br><br>"
            + "Dies ist die URL, die Ihr Partner für die eingehende asynchrone MDN zu dieser lokalen Station verwenden wird. Im synchronen Fall "
            + "wird dieser Wert nicht verwendet, da die MDN dann auf dem Rückkanal der ausgehenden Verbindung geschickt wird.<br>"
            + "Bitte geben Sie diese URL im Format <strong>PROTOKOLL://HOST:PORT/PFAD</strong> an.<br><strong>PROTOKOLL</strong> "
            + "muß eines von \"http\" oder \"https\" sein.<br><strong>HOST</strong> bezeichnet Ihren eigenen AS2 Server Host.<br>"
            + "<strong>PORT</strong> ist der Empfangsport Ihres AS2 Systems."
            + "<strong>PFAD</strong> bezeichnet den Empfangspfad, zum Beispiel \"/as2/HttpReceiver\"."
            + "Der gesamte Eintrag wird als ungültig markiert, wenn das Protokoll nicht eines von \"http\" oder \"https\" ist, "
            + "wenn die URL ein falsches Format hat oder wenn der Port nicht in der URL definiert ist.<br><br>"
            + "Bitte geben Sie hier keine URL ein, die über Angaben von \"localhost\" oder \"127.0.0.1\" auf Ihr eigenes "
            + "System verweist - diese Angabe wird nach Empfang der AS2 Nachricht auf der Seite Ihres Partners ausgewertet "
            + "und er würde dann die MDN an sich selber schicken."
            + "</HTML>"},
        {"label.signalias.key", "Privater Schlüssel (Digitale Signatur erstellen)"},
        {"label.signalias.key.help", "<HTML><strong>Privater Schlüssel (Digitale Signatur erstellen)</strong><br><br>"
            + "Bitte wählen Sie hier einen privaten Schlüssel aus, der im Zertifikatmanager (Signatur/Verschlüsselung) des Systems verfügbar ist.<br>"
            + "Mit diesem Schlüssel erstellen Sie eine digitale Signatur für ausgehende Nachrichten an alle entfernten Partner.<br><br>"
            + "Da nur Sie im Besitz des hier eingestellten privaten Schlüssels sind, können auch nur Sie eine Signatur der Daten durchführen.<br>"
            + "Ihre Partner können diese Signatur mit dem Zertifikat prüfen - dadurch wird sicher gestellt, dass die Daten unverändert sind und Sie der"
            + "Absender sind."
            + "</HTML>"},
        {"label.cryptalias.key", "Privater Schlüssel (Datenentschlüsselung)"},
        {"label.cryptalias.key.help", "<HTML><strong>Privater Schlüssel (Datenentschlüsselung)</strong><br><br>"
            + "Bitte wählen Sie hier einen privaten Schlüssel aus, der im Zertifikatmanager (Signatur/Verschlüsselung) des Systems verfügbar ist.<br>"
            + "Wenn eingehende Nachrichten beliebiger Partner für diese lokale Station verschlüsselt sind, wird dieser Schlüssel zur Entschlüsselung verwendet.<br><br>"
            + "Da nur Sie im Besitz des hier eingestellten privaten Schlüssels sind, können auch nur Sie die Daten entschlüsseln, "
            + "die Ihre Partner mit Ihrem Zertifikat verschlüsselt haben.<br>"
            + "Es kann also jeder Partner für Sie Daten verschlüsseln - nur Sie können sie jedoch entschlüsseln."
            + "</HTML>"},
        {"label.signalias.cert", "Partnerzertifikat (Digitale Signatur verifizieren)"},
        {"label.signalias.cert.help", "<HTML><strong>Partnerzertifikat (Digitale Signatur verifizieren)</strong><br><br>"
            + "Bitte wählen Sie hier ein Zertifikat aus, das im Zertifikatmanager (Signatur/Verschlüsselung) des Systems verfügbar ist.<br>"
            + "Wenn eingehende Nachrichten dieses Partners für eine lokale Station digital signiert sind, "
            + "wird dieses Zertifikat verwendet, um diese Signatur zu prüfen."
            + "</HTML>"},
        {"label.cryptalias.cert", "Partnerzertifikat (Datenverschlüsselung)"},
        {"label.cryptalias.cert.help", "<HTML><strong>Partnerzertifikat (Datenverschlüsselung)</strong><br><br>"
            + "Bitte wählen Sie hier ein Zertifikat aus, das im Zertifikatmanager (Signatur/Verschlüsselung) des Systems verfügbar ist.<br>"
            + "Wenn Sie ausgehende Nachrichten an diesen Partner verschlüsseln möchten, wird dieses Zertifikat zum Verschlüsseln der Daten verwendet."
            + "</HTML>"},
        {"label.signtype", "Digitale Signatur"},
        {"label.signtype.help", "<HTML><strong>Digitale Signatur</strong><br><br>"
            + "Hier wählen Sie den Signaturalgorithmus, mit dem ausgehende Nachrichten an diesen Partner signiert werden sollen.<br>"
            + "Wenn Sie hier einen Signaturalgorithmus gewählt haben, wird auch eingehend von diesem Partner eine signierte Nachricht erwartet - der Signaturalgorithmus ist dabei jedoch beliebig.<br><br>"
            + "Die ausgehende Nachricht an diesen Partner wird mit Hilfe des privaten Schlüssels der lokalen "
            + "Station signiert, die der Sender der Transaktion ist."
            + "</HTML>"},
        {"label.encryptiontype", "Nachrichtenverschlüsselung"},
        {"label.encryptiontype.help", "<HTML><strong>Nachrichtenverschlüsselung</strong><br><br>"
            + "Hier wählen Sie den Verschlüsselungsalgorithmus, mit dem ausgehende Nachrichten an diesen Partner verschlüsselt werden sollen.<br>"
            + "Wenn Sie hier einen Verschlüsselungsalgorithmus gewählt haben, wird auch eingehend von diesem Partner "
            + "eine verschlüsselte Nachricht erwartet - der Verschlüsselungsalgorithmus ist dabei jedoch beliebig.<br><br>"
            + "Weitere Informationen über den Verschlüsselungsalgorithmus finden Sie in der Hilfe (Abschnitt Partner) "
            + "- dort werden alle Algorithmen erklärt."
            + "</HTML>"},
        {"label.email", "Mail Adresse"},
        {"label.email.help", "<HTML><strong>Mail Adresse</strong><br><br>"
            + "Dieser Wert ist Teil der AS2 Protokollbeschreibung, wird aber derzeit überhaupt nicht verwendet."
            + "</HTML>"},
        {"label.email.hint", "Im AS2 Protokoll nicht verwendet oder validiert"},
        {"label.localstation", "Lokale Station"},
        {"label.localstation.help", "<HTML><strong>Lokale Station</strong><br><br>"
            + "Eine lokale Station repräsentiert Ihr eigenes System. Sie können beliebig viele lokale Stationen in Ihrem "
            + "System anlegen.<br>"
            + "Sie konfigurieren lokale Stationen und Verbindungspartner getrennt. Die Gesamtkonfiguration"
            + "der Partnerbeziehung wird dann jeweils aus den Konfigurationen der lokalen Station und des entfernten Partners "
            + "automatisch erstellt.<br><br>"
            + "Es gibt zwei Arten von Partnern:<br><br>"
            + "<table border=\"0\">"
            + "<tr>"
            + "<td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/localstation.svg\" height=\"20\" width=\"20\"></td>"
            + "<td>Lokale Stationen</td>" 
            + "</tr>"   
            + "<tr>"
            + "<td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/singlepartner.svg\" height=\"20\" width=\"20\"></td>"
            + "<td>Entfernter Partner</td>" 
            + "</tr>"               
            + "</table>"            
            + "</HTML>"},
        {"label.compression", "Datenkomprimierung"},
        {"label.compression.help", "<HTML><strong>Datenkomprimierung</strong><br><br>"
            + "Wenn diese Option aktiviert ist, werden die ausgehenden Nachrichten mit Hilfe des ZLIB-Algorithmus komprimiert.<br>"
            + "Der Vorteil der Komprimierung besteht darin, dass die Nachrichtengröße in der Regel verringert wird, was zu einer "
            + "schnelleren Übertragung führt. Außerdem wird die Nachrichtenstruktur geändert, was Kompatibilitätsprobleme lösen kann.<br>"
            + "Der Nachteil ist, dass dies ein zusätzlicher Verarbeitungsschritt ist, der auf Kosten des Durchsatzes geht.<br><br>" 
            + "Diese Option erfordert ein AS2-System auf der anderen Seite, das mindestens AS2 1.1 unterstützt."
            + "</HTML>"},
        {"label.usecommandonreceipt", "Empfang"},
        {"label.usecommandonsenderror", "Versand (fehlerhaft)"},
        {"label.usecommandonsendsuccess", "Versand (erfolgreich)"},
        {"label.keepfilenameonreceipt", "Original Dateiname beibehalten"},
        {"label.keepfilenameonreceipt.help", "<HTML><strong>Original Dateiname beibehalten</strong><br><br>"
            + "Ist dies aktiviert, versucht das System, den original Dateinamen aus eingehenden AS2 Nachrichten zu extrahieren und die übertragene "
            + "Datei unter diesem Namen zu speichern, damit sie entsprechend weiterverarbeitet werden kann.<br>"
            + "Diese Option funktioniert nur, wenn der Absender die ursprünglichen Dateinameninformationen hinzugefügt hat. "
            + "Wenn Sie diese Option aktivieren, stellen Sie bitte sicher, dass Ihr Partner eindeutige Dateinamen sendet.<br><br>"
            + "Wenn der extrahierte Dateiname kein gültiger Dateiname ist, wird er durch einen gültigen Dateinamen ersetzt, "
            + "eine POSTPROCESSING-Systemereigniswarnung wird ausgelöst und die Verarbeitung wird fortgesetzt.</HTML>"},
        {"label.address", "Adresse"},
        {"label.notes.help", "<HTML><strong>Notizen</strong><br><br>"
            + "Hier finden Sie die Möglichkeit von Notizen zu diesem Partner für Ihren eigenen Gebrauch."
            + "</HTML>"},
        {"label.contact", "Kontakt"},
        {"tab.misc", "Allgemein"},
        {"tab.security", "Sicherheit"},
        {"tab.send", "Versand"},
        {"tab.mdn", "MDN"},
        {"tab.dirpoll", "Verzeichnisüberwachung"},
        {"tab.receipt", "Empfang"},
        {"tab.httpauth", "HTTP Authentifizierung"},
        {"tab.httpheader", "HTTP Header"},
        {"tab.notification", "Benachrichtigung"},
        {"tab.events", "Nachbearbeitung"},
        {"tab.partnersystem", "Info"},
        {"label.subject", "Nutzdaten Subject"},
        {"label.subject.help", "<HTML><strong>Nutzdaten Subject</strong><br><br>"
            + "$'{'filename} wird durch den Sendedateinamen ersetzt.<br>"
            + "Dieser Wert wird im HTTP Header übertragen, dafür gelten Einschränkungen!<br>"
            + "Bitte verwenden Sie als Zeichenkodierung ISO-8859-1, nur druckbare Zeichen, keine Sonderzeichen.<br>"
            + "CR, LF und TAB werden ersetzt durch \"\\r\", \"\\n\" und \"\\t\".</HTML>"},
        {"label.contenttype", "Nutzdaten Content Type"},
        {"label.contenttype.help", "<HTML><strong>Nutzdaten Content Type</strong><br><br>"
            + "Im AS2 Protokoll werden folgende Content Types sicher unterstützt:<br>"
            + "application/EDI-X12<br>"
            + "application/EDIFACT<br>"
            + "application/edi-consent<br>"
            + "application/XML<br><br>"
            + "Das AS2 RFC sagt aus, dass alle MIME Content Typen im AS2 unterstützt werden sollen.<br>"
            + "Das ist aber keine zwingende Voraussetzung.<br>"
            + "Somit sollten Sie sich nicht darauf verlassen,<br>dass das System Ihres Partners "
            + "oder die unterliegende SMIME Verarbeitung des mendelson AS2 mit anderen Content Typen als den beschriebenen"
            + "umgehen kann."
            + "</HTML>"},
        {"label.syncmdn", "Synchrone Empfangsbestätigung (MDN) anfordern"},
        {"label.syncmdn.help", "<HTML><strong>Synchrone Empfangsbestätigung</strong><br><br>"
            + "Der Partner sendet die Empfangsbestätigung (MDN) auf dem Rückkanal Ihrer ausgehenden Verbindung.<br>"
            + "Die ausgehende Verbindung wird offen gehalten, während der Partner die Daten entschlüsselt "
            + "und die Signatur prüft.<br>"
            + "Aus diesem Grund hat diese Methode einen höheren Ressourcenbedarf "
            + "als die asynchrone MDN-Verarbeitung.</HTML>"},
        {"label.asyncmdn", "Asynchrone Empfangsbestätigung (MDN) anfordern"},
        {"label.asyncmdn.help", "<HTML><strong>Asynchrone Empfangsbestätigung</strong><br><br>"
            + "Der Partner baut eine neue Verbindung zu Ihrem System auf, um die Bestätigung für Ihre "
            + "ausgehende Nachricht zu senden.<br>"
            + "Die Überprüfung der Signatur und die Entschlüsselung der Daten "
            + "auf der Partnerseite erfolgt nach dem Schließen der eingehenden Verbindung.<br>"
            + "Aus diesem Grund benötigt diese Methode weniger Ressourcen als die Methode mit synchroner MDN.</HTML>"},
        {"label.signedmdn", "Signierte Empfangsbestätigung (MDN) anfordern"},
        {"label.signedmdn.help", "<HTML><strong>Signierte Empfangsbestätigung</strong><br><br>"
            + "Mit dieser Einstellung können Sie dem Partnersystem für ausgehende AS2 Nachrichten mitteilen, dass Sie eine signierte Empfangsbestätigung (MDN) wünschen.<br>"
            + "Obwohl das zunächst sinnvoll klingt, ist die Einstellung leider problematisch.<br>"
            + "Denn wenn die MDN des Partners empfangen ist, ist damit die Transaktion beendet.<br>"
            + "Wenn dann die Signaturverifikation der MDN durchgeführt wird und fehl schlägt, gibt es gar keine Möglichkeit mehr, dem Partner dieses Problem mitzuteilen.<br>"
            + "Ein Transaktionsabbruch ist nicht mehr möglich - die Transaktion ist bereits beendet. Somit ist das Verifizieren der Signatur der MDN im automatischen Betrieb sinnlos.<br>"
            + "Das AS2 Protokoll schreibt hier vor, dass die Applikation dieses logische Problem lösen soll, was aber nicht möglich ist.<br>"
            + "Die mendelson AS2 Lösung zeigt im Fall einer fehlgeschlagenen MDN Signaturprüfung eine Warnung an.<br><br>"
            + "Es gibt noch eine Besonderheit dieser Einstellung:<br>"
            + "Wenn es in der Verarbeitung auf Partnerseite zu einem Problem kam, darf die MDN immer unsigniert sein - unabhängig von dieser Einstellung."
            + "</HTML>"},
        {"label.enabledirpoll", "Verzeichnisüberwachung"},
        {"label.enabledirpoll.help", "<HTML><strong>Verzeichnisüberwachung</strong><br><br>"
            + "Wenn Sie diese Option einschalten, wird das System das Ausgangsverzeichnis für diesen Partner automatisch nach "
            + "neuen Dateien durchsuchen.<br>"
            + "Wird eine neue Datei gefunden, wird daraus eine AS2 Nachricht generiert und an den Partner verschickt.<br>"
            + "Bitte beachten Sie, dass diese Methode der Verzeichnisüberwachung nur generelle Parameter für alle Nachrichtenerstellungen verwenden kann.<br>"
            + "Wenn Sie spezielle Parameter für jede Nachricht einzeln einstellen möchten, verwenden Sie bitte den Sendeprozess über die Kommandozeile.<br>"
            + "Im Falle des Clusterbetriebs (HA) müssen Sie alle Verzeichnisüberwachungen ausschalten, da dieser Vorgang nicht synchronisiert werden kann."
            + "</HTML>"},
        {"label.polldir", "Überwachtes Verzeichnis"},
        {"label.pollinterval", "Abholintervall"},
        {"label.pollignore", "Abholen ignorieren für"},
        {"label.pollignore.help", "<HTML><strong>Abholen ignorieren für</strong><br><br>"
            + "Die Verzeichnisüberwachnung wird in regelmässigen Abständen eine definierte Anzahl von Dateien aus dem "
            + "überwachten Verzeichnis abholen und verarbeiten.<br>"
            + "Es muss sichergestellt sein, dass zu diesem Zeitpunkt die Datei "
            + "vollständig vorhanden ist. Wenn Sie regelmässig Dateien in das überwachte Verzeichnis kopieren, kann es hierbei zu "
            + "zeitlichen Überschneidungen kommen, sodass also eine Datei abgeholt wird, die noch gar nicht komplett verfügbar ist.<br>"
            + "Daher sollten Sie, wenn Sie die Dateien mit einer nichtatomaren Operation in das überwachte Verzeichnis kopieren, zum Zeitpunkt "
            + "des Kopierprozesses eine Dateinamenserweiterung wählen, die vom Überwachnungsprozess ignoriert wird.<br>"
            + "Nachdem die gesamte Datei dann im überwachten "
            + "Verzeichnis verfügbar ist, können Sie die Dateinamenserweiterung mit einer atomaren Operation (move, mv, rename) entfernen und die vollständige "
            + "Datei wird abgeholt."
            + "<br>Die Liste der Dateinameserweiterungen ist eine kommagetrennte Liste von Erweiterungen, zum Beispiel "
            + "\"*.tmp, *.upload\"."
            + "</HTML>"},
        {"label.pollignore.hint", "Liste der Dateierweiterungen, die ignoriert werden sollen, kommagetrennt (Wildcards erlaubt)."},
        {"label.maxpollfiles", "Max Dateianzahl/Abholvorgang"},
        {"label.httpauth.none", "Keine"},
        {"label.httpauth.message", "Authentifizierung ausgehender AS2 Nachrichten"},
        {"label.httpauth.credentials.message", "Basis HTTP Authentifizierung"},
        {"label.httpauth.credentials.message.user", "Benutzername"},
        {"label.httpauth.credentials.message.pass", "Passwort"},
        {"label.httpauth.oauth2.authorizationcode.message", "OAuth2 (Authorization code)"},
        {"label.httpauth.oauth2.clientcredentials.message", "OAuth2 (Client credentials)"},
        {"label.httpauth.asyncmdn", "Authentifizierung ausgehender asynchroner MDN"},
        {"label.httpauth.credentials.asyncmdn", "Basis HTTP Authentifizierung"},
        {"label.httpauth.credentials.asyncmdn.user", "Benutzername"},
        {"label.httpauth.credentials.asyncmdn.pass", "Passwort"},
        {"label.httpauth.oauth2.authorizationcode.asyncmdn", "OAuth2 (Authorization code)"},
        {"label.httpauth.oauth2.clientcredentials.asyncmdn", "OAuth2 (Client credentials)"},
        {"label.notify.send", "Benachrichtigen, wenn das Sendekontingent folgenden Wert übersteigt:"},
        {"label.notify.receive", "Benachrichtigen, wenn das Empfangskontingent folgenden Wert übersteigt:"},
        {"label.notify.sendreceive", "Benachrichtigen, wenn das Sende/Empfangskontingent folgenden Wert übersteigt:"},
        {"header.httpheaderkey", "Name"},
        {"header.httpheadervalue", "Wert"},
        {"httpheader.add", "Hinzufügen"},
        {"httpheader.delete", "Entfernen"},
        {"label.as2version", "AS2 Version"},
        {"label.productname", "Produktname"},
        {"label.features", "Funktionen"},
        {"label.features.cem", "Zertifikataustausch über CEM"},
        {"label.features.ma", "Mehrere Anhänge"},
        {"label.features.compression", "Datenkomprimierung"},
        {"partnerinfo", "Ihr Partner übermittelt mit jeder AS2 Nachricht auch Informationen über die Funktionen seines AS2 Systems. Dies ist die Liste dieser Funktionen."},
        {"partnersystem.noinfo", "Keine Information verfügbar - gab es schon eine Transaktion?"},
        {"label.httpversion", "HTTP Protokollversion"},
        {"label.httpversion.help", "<HTML><strong>HTTP Protokollversion</strong><br><br>"
            + "Es gibt die HTTP Protokollversionen"
            + "<ul>"
            + "<li>HTTP/1.0 (RFC 1945)</li>"
            + "<li>HTTP/1.1 (RFC 2616)</li>"
            + "<li>HTTP/2.0 (RFC 9113)</li>"
            + "<li>HTTP/3.0 (RFC 9114)</li>"
            + "</ul>"
            + "Beim AS2 wird in der Regel HTTP/1.1 verwendet.<br><br>"
            + "Hinweis: Dies ist <strong>nicht</strong> die TLS Version!"
            + "</HTML>"},
        {"label.test.connection", "Verbindung prüfen"},
        {"label.mdn.description", "<HTML>Die MDN (Message Delivery Notification) ist die Bestätigung für die AS2 Nachricht. Dieser Abschnitt definiert das Verhalten Ihres Partners für Ihre ausgehenden AS2-Nachrichten.</HTML>"},
        {"label.algorithmidentifierprotection", "Algorithm Identifier Protection Attribute"},
        {"label.algorithmidentifierprotection.help", "<HTML><strong>Algorithm Identifier Protection Attribute</strong><br><br>"
            + "Wenn Sie diese Option einschalten (was empfohlen ist), wird das Algorithm Identifier Protection Attribute in der AS2 Signatur "
            + "verwendet. Dieses Attribut wird im RFC 6211 definiert.<br><br>"
            + "Die verwendete Signatur von AS2 ist anfällig für Algorithmus-Substitutionsangriffe.<br>"
            + "Bei einem Algorithmus-Substitutionsangriff ändert der Angreifer entweder den verwendeten Algorithmus oder die "
            + "Parameter des Algorithmus, um das Ergebnis eines Signaturprüfverfahrens zu verändern.<br>"
            + "Dieses Attribut beinhaltet nun eine Kopie der relevanten Algorithmusbezeichner der Signatur, sodass sie nicht verändert werden können."
            + "Dies verhindert einen Algorithmus-Substitutionsangriff auf die Signatur.<br><br>" 
            + "Es gibt AS2 Systeme, die mit diesem Attribut nicht umgehen können (obwohl das RFC von 2011 ist) und einen Autorisierungsfehler zurückmelden.<br>"
            + "Für diesen Fall kann das Attribut hier abgeschaltete werden."
            + "</HTML>"},
        {"tooltip.button.editevent", "Ereignis bearbeiten"},
        {"tooltip.button.addevent", "Neues Ereignis erstellen"},
        {"label.httpauthentication.credentials.help", "<HTML><strong>HTTP Basis Zugangsauthentifizierung</strong><br><br>"
            + "Bitte richten Sie hier die HTTP Basis-Zugangsauthentifizierung ein, wenn dies auf der Seite "
            + "Ihres Partners aktiviert ist (definiert in RFC 7617). Auf nicht authentifizierte Anfragen "
            + "(falsche Anmeldedaten usw.) sollte das System des entfernten Partners einen <strong>HTTP 401 Unauthorized</strong> "
            + "Status zurückgeben.<br>"
            + "Wenn die Verbindung zu Ihrem Partner TLS-Client-Authentifizierung (über Zertifikate) "
            + "erfordert, ist hier keine Einstellung erforderlich.<br>"
            + "In diesem Fall importieren Sie bitte die Zertifikate "
            + "des Partners über den TLS-Zertifikatsmanager.<br>"
            + "Sas System kümmert sich dann um die TLS-Client-Authentifizierung."
            + "</HTML>"},
        {"label.overwrite.security", "Sicherheitseinstellungen der lokalen Station überschreiben"},
        {"label.keep.security", "Sicherheitseinstellungen der lokalen Station verwenden"},
        {"label.overwrite.crypt", "Eingehende Nachrichten entschlüsseln"},
        {"label.overwrite.crypt.help", "<HTML><strong>Eingehende Nachrichten entschlüsseln</strong><br><br>"
            + "Dieser Schlüssel wird zum Entschlüsseln eingehender Nachrichten dieses Partners verwendet - anstelle des eingestellten "
            + "Schlüssels der jeweiligen lokalen Station."
            + "</HTML>"},
        {"label.overwrite.sign", "Ausgehende Nachrichten signieren"},
        {"label.overwrite.sign.help", "<HTML><strong>Ausgehende Nachrichten signieren</strong><br><br>"
            + "Dieser Schlüssel wird zum Signieren ausgehender Nachrichten an diesen Partners verwendet - anstelle des eingestellten "
            + "Schlüssels der jeweiligen lokalen Station."
            + "</HTML>"},        
    };

}
