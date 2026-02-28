//$Header: /as2/de/mendelson/comm/as2/configurationcheck/ResourceBundleConfigurationIssue_de.java 36    21/02/25 16:04 Heller $
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
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @version $Revision: 36 $
 */
public class ResourceBundleConfigurationIssue_de extends MecResourceBundle {

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
        {String.valueOf(ConfigurationIssue.CERTIFICATE_EXPIRED_ENC_SIGN), "Zertifikat ist abgelaufen (enc/sign)"},
        {"hint." + String.valueOf(ConfigurationIssue.CERTIFICATE_EXPIRED_ENC_SIGN),
            "<HTML>Zertifikate haben nur eine begrenzte Laufzeit. In der Regel sind das ein, drei oder fünf Jahre.<br>"
            + "Ein Zertifikat, was Sie in Ihrem System für einen Partner zum Verschlüsseln/Entschlüsseln von Daten, zum digitalen Signieren oder zum Prüfen einer digitalen Signatur verwenden, ist nicht mehr gültig.<br>"
            + "Es ist nicht möglich, mit einem abgelaufenen Zertifikat kryptographischer Operationen auszuführen - "
            + "daher sollten Sie sich bitte darum kümmern, das Zertifikat zu erneuern oder ein neues Zertifikat erstellen "
            + "bzw beglaubigen lassen.<br><br>"
            + "<strong>Zusatzinformationen zum Zertifikat:</strong><br><br>"
            + "Alias: {0}<br>"
            + "Issuer: {1}<br>"
            + "Fingerprint (SHA-1): {2}<br>"
            + "Gültig von: {3}<br>"
            + "Gültig bis: {4}<br>"
            + "<br></HTML>"},
        {String.valueOf(ConfigurationIssue.CERTIFICATE_EXPIRED_TLS), "Zertifikat ist abgelaufen (TLS)"},
        {"hint." + String.valueOf(ConfigurationIssue.CERTIFICATE_EXPIRED_TLS),
            "<HTML>Zertifikate haben nur eine begrenzte Laufzeit. In der Regel sind das ein, drei oder fünf Jahre.<br>"
            + "Ein Zertifikat, was Sie in Ihrem System für die TLS Leitungssicherung verwenden, ist nicht mehr gültig.<br>"
            + "Es ist nicht möglich, mit einem abgelaufenen Zertifikat kryptographischer Operationen auszuführen - "
            + "daher sollten Sie sich bitte darum kümmern, das Zertifikat zu erneuern oder ein neues Zertifikat erstellen "
            + "bzw beglaubigen lassen.<br><br>"
            + "<strong>Zusatzinformationen zum Zertifikat:</strong><br><br>"
            + "Alias: {0}<br>"
            + "Issuer: {1}<br>"
            + "Fingerprint (SHA-1): {2}<br>"
            + "Gültig von: {3}<br>"
            + "Gültig bis: {4}<br>"
            + "<br></HTML>"},
        {String.valueOf(ConfigurationIssue.MULTIPLE_KEYS_IN_TLS_KEYSTORE), "Mehrere Schlüssel im TLS Keystore gefunden - darf nur einer sein"},
        {"hint." + String.valueOf(ConfigurationIssue.MULTIPLE_KEYS_IN_TLS_KEYSTORE),
            "<HTML>In dem TLS Keystore Ihres Systems befinden sich mehrere Schlüssel. Es darf sich allerdings nur einer darin befinden - dieser wird beim Start des Servers als TLS Schlüssel verwendet.<br>"
            + "Bitte löschen Sie so lange Schüssel aus dem TLS Keystore, bis nur noch ein Schlüssel darin vorhanden ist.<br>"
            + "Sie erkennen die Schlüssel in der Zertifikatverwaltung am Schlüsselsymbol in der ersten Spalte.<br>"
            + "Nach dieser Änderung ist es notwendig, den Server neu zu starten.</HTML>"},
        {String.valueOf(ConfigurationIssue.NO_KEY_IN_TLS_KEYSTORE), "Kein Schlüssel im TLS Keystore gefunden"},
        {"hint." + String.valueOf(ConfigurationIssue.NO_KEY_IN_TLS_KEYSTORE),
            "<HTML>Es wurde kein Schlüssel im TLS Keystore Ihres Systems gefunden.<br>"
            + "Sie erkennen Schlüssel am vorangestellten Schlüsselsymbol, wenn Sie die Zertifikatverwaltung öffnen.<br>"
            + "Es wird genau ein Schlüssel im TLS Keystore benötigt, um den Handshakeprozess der Leitungssicherung durchzuführen.<br>"
            + "Ohne diesen Schlüssel kommen Sie also weder ein- noch ausgehend zu gesicherten Verbindungen.</HTML>"},
        {String.valueOf(ConfigurationIssue.HUGE_AMOUNT_OF_TRANSACTIONS_NO_AUTO_DELETE), "Aktivieren Sie automatisches Löschen - Im System ist eine grosse Menge von Transaktionen"},
        {"hint." + String.valueOf(ConfigurationIssue.HUGE_AMOUNT_OF_TRANSACTIONS_NO_AUTO_DELETE),
            "<HTML>In den Einstellungen können Sie definieren, wie lange Transaktionen im System verbleiben sollen.<br>"
            + "Je mehr Transaktionen im System verbleiben, desto mehr Resourcen werden für die Verwaltung benötigt.<br>"
            + "Daher sollten Sie mit Hilfe der Einstellungen dafür sorgen, dass Sie nie mehr als maximal 30000 Transaktionen im System haben.<br>"
            + "Bitte beachten Sie, dass dies kein Archivsystem ist, sondern ein Kommunikationsadapter.<br>"
            + "Sie haben über die integrierte Suchfunktion des Serverlogs Zugriff auf alle Transaktionslogs der Vergangenheit.</HTML>"},
        {String.valueOf(ConfigurationIssue.FEW_CPU_CORES), "Weisen Sie dem System mindestens 4 Prozessorkerne zu"},
        {"hint." + String.valueOf(ConfigurationIssue.FEW_CPU_CORES),
            "<HTML>Für besseren Durchsatz ist es notwendig, dass unterschiedliche Aufgaben im System parallel durchgeführt werden.<br>"
            + "Daher ist es notwendig, eine entsprechende Anzahl von CPU Kernen für den Prozess zu reservieren.</HTML>"},
        {String.valueOf(ConfigurationIssue.LOW_MAX_HEAP_MEMORY), "Reservieren Sie mindestens 8GB Hauptspeicher für den Serverprozess"},
        {"hint." + String.valueOf(ConfigurationIssue.LOW_MAX_HEAP_MEMORY),
            "<HTML>Dieses Programm ist in Java geschrieben.<br>"
            + "Unabhängig von der physikalischen Ausstattung Ihres Rechners müssen Sie dem Serverprozess eine entsprechende Menge an Speicher reservieren. In Ihrem Fall haben Sie zu wenig Speicher reserviert.<br>"
            + "Bitte schauen Sie in die Hilfe (Abschnitt Installation) - dort steht, wie Sie für welche Startmethode den entsprechenden Speicher reservieren.<br><br>"
            + "Bitte stellen Sie auf jeden Fall sicher, nicht mehr Speicher für den Serverprozess zu reservieren, als Ihr System Hauptspeicher hat. "
            + "Ansonsten wird die Software nahezu unbedienbar, weil das System ständig Speicher auf die Festplatte auslagert.</HTML>"},
        {String.valueOf(ConfigurationIssue.NO_OUTBOUND_CONNECTIONS_ALLOWED), "Menge ausgehender Verbindungen ist auf 0 gesetzt - das System wird NICHT senden"},
        {"hint." + String.valueOf(ConfigurationIssue.NO_OUTBOUND_CONNECTIONS_ALLOWED),
            "<HTML>Sie haben Konfigurationsänderungen vorgenommen, sodass aktuell keine ausgehenden Verbindungen möglich sind.<br>"
            + "Wenn Sie ausgehende Verbindungen zu Partnern aufnehmen möchten, müsste die Anzahl der möglichen Verbindungen mindestens auf den Wert 1 gesetzt werden.</HTML>"},
        {String.valueOf(ConfigurationIssue.CERTIFICATE_MISSING_ENC_REMOTE_PARTNER), "Fehlendes Verschlüsselungszertifikat eines entfernten Partners"},
        {"hint." + String.valueOf(ConfigurationIssue.CERTIFICATE_MISSING_ENC_REMOTE_PARTNER),
            "<HTML>Ein Verbindungpartner hat in Ihrer Konfiguration kein Verschlüsselungszertifikat zugewiesen.<br>"
            + "Sie können in diesem Fall keine Nachrichten an ihn verschlüsseln. Bitte öffnen Sie die Partnerverwaltung und weisen Sie dem Partner ein Verschlüsselungszertifikat zu.</HTML>"},
        {String.valueOf(ConfigurationIssue.CERTIFICATE_MISSING_SIGN_REMOTE_PARTNER), "Fehlendes Signaturzertifikat eines entfernten Partners"},
        {"hint." + String.valueOf(ConfigurationIssue.CERTIFICATE_MISSING_SIGN_REMOTE_PARTNER),
            "<HTML>Ein Verbindungpartner hat in Ihrer Konfiguration kein Signaturzertifikat zugewiesen.<br>"
            + "Sie können in diesem Fall keine digitalen Signaturen Ihres Partners verifizieren. Bitte öffnen Sie die Partnerverwaltung und weisen Sie dem Partner ein Signaturzertifikat zu.</HTML>"},
        {String.valueOf(ConfigurationIssue.KEY_MISSING_ENC_LOCAL_STATION), "Fehlender Verschlüsselungsschlüssel einer lokalen Station"},
        {"hint." + String.valueOf(ConfigurationIssue.KEY_MISSING_ENC_LOCAL_STATION),
            "<HTML>Ihre lokale Station hat keinen Verschlüsselungsschlüssel zugewiesen.<br>"
            + "Sie können in dieser Konfiguration keine eingehenden Nachrichten entschlüsseln - egal von welchem Partner.<br>"
            + "Bitte öffnen Sie die Partnerverwaltung und weisen Sie der lokalen Station einen privaten Schlüssel zu.</HTML>"},
        {String.valueOf(ConfigurationIssue.KEY_MISSING_SIGN_LOCAL_STATION), "Fehlender Signaturschlüssel einer lokalen Station"},
        {"hint." + String.valueOf(ConfigurationIssue.KEY_MISSING_SIGN_LOCAL_STATION),
            "<HTML>Ihre lokale Station hat keinen Signaturschlüssel zugewiesen.<br>"
            + "Sie können in dieser Konfiguration keine ausgehenden Nachrichten digital signieren - egal für welchem Partner.<br>"
            + "Bitte öffnen Sie die Partnerverwaltung und weisen Sie der lokalen Station einen privaten Schlüssel zu.</HTML>"},
        {String.valueOf(ConfigurationIssue.USE_OF_TEST_KEYS_IN_TLS), "Verwendung eines öffentlich verfügbaren Testschlüssels als TLS Schlüssel"},
        {"hint." + String.valueOf(ConfigurationIssue.USE_OF_TEST_KEYS_IN_TLS),
            "<HTML>In der Auslieferung stellt mendelson einige Testschlüssel zur Verfügung.<br>"
            + "Diese sind auf der mendelson Webseite öffentlich verfügbar.<br>"
            + "Wenn Sie diese Schlüssel für kryptographische Aufgaben innerhalb Ihres Datentransfers produktiv verwenden, bieten sie daher <strong>KEINE</strong> Sicherheit.<br>"
            + "Hier können Sie dann auch gleich ungesichert und unverschlüsselt senden.<br>"
            + "Wenn Sie einen beglaubigten Schlüssel benötigen, wenden Sie sich bitte an den mendelson Support.</HTML>"},
        {String.valueOf(ConfigurationIssue.JVM_32_BIT), "Die Verwendung einer 32 Bit Java VM wird nicht für den produktiven Einsatz empfohlen, da dann der maximale Heap-Speicher auf 1,3GB begrenzt ist."},
        {"hint." + String.valueOf(ConfigurationIssue.JVM_32_BIT),
            "<HTML>Java 32bit Prozesse können nicht genug Speicher reservieren, um das System im Produktivbetrieb stabil zu halten. Bitte verwenden Sie eine 64bit JVM.</HTML>"},
        {String.valueOf(ConfigurationIssue.WINDOWS_SERVICE_LOCAL_SYSTEM_ACCOUNT), "Windows Service mit lokalem Systemkonto gestartet"},
        {"hint." + String.valueOf(ConfigurationIssue.WINDOWS_SERVICE_LOCAL_SYSTEM_ACCOUNT),
            "<HTML>Sie haben den mendelson AS2 Server als Windows Service eingerichtet und starten ihn über ein lokales Systemkonto (\"{0}\").<br>"
            + "Leider ist es möglich, dass dieser Benutzer nach einem Windows Update die Rechte an seinen vormals geschriebenen Dateien verliert, "
            + "das kann zu vielfältigen Systemproblemen führen.<br><br>"
            + "Bitte richten Sie einen eigenen Benutzer für den Service ein und starten den Service mit diesem Benutzer.</HTML>"},
        {String.valueOf(ConfigurationIssue.TOO_MANY_DIR_POLLS), "Große Menge von Verzeichnisüberwachungen pro Zeiteinheit"},
        {"hint." + String.valueOf(ConfigurationIssue.TOO_MANY_DIR_POLLS),
            "<HTML>Sie haben in Ihrem System eine große Menge von Partnerbeziehnungen definiert und überwachen die entsprechenden "
            + "Ausgangsverzeichnisse in zu kurzen Zeitintervallen.<br>Aktuell werden pro Minute {0} Verzeichnisüberwachungen aktiviert, "
            + "das System kann diese hohe Rate nicht einhalten.<br>"
            + "Bitte reduzieren Sie diesen Wert, indem Sie die Überwachungsintervalle der jeweiligen Partnerverzeichnisse vergrößern und"
            + " auch Überwachungen für Partner deaktivieren, wo dies nicht benötigt wird."
            + "Bei einer großen Anzahl von Partnern wird empfohlen, alle Verzeichnisüberwachungen deaktivieren und die Sendeaufträge von "
            + "Ihrem Backend aus mit den Befehlen <i>AS2Send.exe</i> oder <i>as2send.sh</i> nach Bedarf zu erstellen.</HTML>"},
        {String.valueOf(ConfigurationIssue.CRL_CERTIFICATE_REVOCATION_ENC_SIGN), "Sperrlistenproblem (enc/sign)"},
        {"hint." + String.valueOf(ConfigurationIssue.CRL_CERTIFICATE_REVOCATION_ENC_SIGN),
            "<HTML>Beglaubigte Zertifikate enthalten einen Link zu einer Sperrliste (CRL, certificate revocation list), mit der dieses Zertifikat für ungültig "
            + "erklärt werden kann. Zum Beispiel, wenn das Zertifikat kompromittiert wurde.<br>"
            + "Es gab ein Problem bei der "
            + "Überprüfung der Sperrliste des folgenden enc/sign-Zertifikats oder das Zertifikat wurde widerrufen:<br><strong>{0}</strong><br><br>"
            + "Zusätzliche Information zu diesem Zertifikat<br><br>"
            + "Alias: {1}<br>"
            + "Issuer: {2}<br>"
            + "Fingerprint (SHA-1): {3}<br>"
            + "<br>"
            + "<br>Bitte beachten Sie, dass die automatische CRL Prüfung in den Einstellungen abgeschaltet werden kann."
            + "</HTML>"},
        {String.valueOf(ConfigurationIssue.CRL_CERTIFICATE_REVOCATION_TLS), "Sperrlistenproblem (TLS)"},
        {"hint." + String.valueOf(ConfigurationIssue.CRL_CERTIFICATE_REVOCATION_TLS),
            "<HTML>Beglaubigte Zertifikate enthalten einen Link zu einer Sperrliste, mit der dieses Zertifikat für ungültig "
            + "erklärt werden kann. Zum Beispiel, wenn das Zertifikat kompromittiert wurde.<br>"
            + "Es gab ein Problem bei der "
            + "Überprüfung der Sperrliste des folgenden TLS-Zertifikats oder das Zertifikat wurde widerrufen:<br><strong>{0}</strong><br><br>"
            + "Zusätzliche Information zu diesem Zertifikat<br><br>"
            + "Alias: {1}<br>"
            + "Issuer: {2}<br>"
            + "Fingerprint (SHA-1): {3}<br>"
            + "<br>"
            + "<br>Bitte beachten Sie, dass die automatische CRL Prüfung in den Einstellungen abgeschaltet werden kann."
            + "</HTML>"},
        {String.valueOf(ConfigurationIssue.CLIENT_SERVER_IN_ONE_PROCESS), "Client und Server laufen in einem Prozess"},
        {"hint." + String.valueOf(ConfigurationIssue.CLIENT_SERVER_IN_ONE_PROCESS),
            "<HTML>Sie haben Client und Server des Produktes in einem Prozess gestartet. Es ist nicht empfehlenswert, das "
            + "im produktiven Betrieb zu tun. Da die Resourcen den Programmen statisch zugewiesen sind, haben Sie in diesem Fall "
            + "geringere Resourcen für den Server- und Clientbetrieb.<br><br>"
            + "Bitte starten Sie zunächst den Serverprozess und verbinden sich dann separat "
            + "mit dem Client."
            + "</HTML>"},
        {String.valueOf(ConfigurationIssue.NOT_ENOUGH_HANDLES), "Nicht genug Handles für Serverprozess"},
        {"hint." + String.valueOf(ConfigurationIssue.NOT_ENOUGH_HANDLES),
            "<HTML>Sie können in Ihrem Betriebssystem die Anzahl der offenen Ports und Dateien pro Benutzer limitieren.<br>"
            + "Ihr aktueller Prozessbenutzer darf nur {0} Handles verwenden, was zu wenig für den Serverbetrieb ist. "
            + "Aktuell verwendet der Serverprozess {1} Handles.<br>"
            + "Unter Linux können Sie diesen Wert mit \"ulimit -n\" einsehen.<br><br>"
            + "Bitte erweitern Sie den Maximalwert der verfügbaren Handles für diesen Prozess auf mindestens {2}."
            + "</HTML>"},
    };
}
