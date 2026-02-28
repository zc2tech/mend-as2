//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesInterface_de.java 10    9/12/24 16:03 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.comm.as2.timing.PartnerTLSCertificateChangedController;
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
 * @version $Revision: 10 $
 */
public class ResourceBundlePreferencesInterface_de extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"label.cem", "Zertifikataustausch erlauben (CEM)"},
        {"label.outboundstatusfiles", "Statusdateien für ausgehende Transaktionen"},
        {"label.outboundstatusfiles.help", "<HTML><strong>Statusdateien für ausgehende Transaktionen</strong><br><br>"
            + "Wenn Sie diese Option einschalten, wird für jede ausgehende Transaktion eine Statusdatei im Verzeichnis \"outboundstatus\" "
            + "geschrieben."
            + "<br>Diese Datei dient Integrationszwecken und beinhaltet Informationen zu der jeweiligen Transaktion. Dies sind"
            + "zum Beispiel Transaktionsstatus, Nachrichtennummer, Sender- und Empfängerkennung.<br>"
            + "Der Dateiname der Statusdatei beinhaltet die Nachrichtenummer und endet mit \".sent.state\". Sie können nach dem Versand "
            + "von Daten diese Datei parsen und schauen, welchen Status die Transaktion hat."
            + "</HTML>"},
        {"label.showsecurityoverwrite", "Partnerverwaltung: Sicherheitseinstellungen der lokalen Station überschreiben"},
        {"label.showsecurityoverwrite.help", "<HTML><strong>Sicherheitseinstellungen der lokalen Station überschreiben</strong><br><br>"
            + "Wenn Sie diese Option einschalten, wird in der Partnerverwaltung pro Partner ein zusätzlicher "
            + "Tab angezeigt.<br>Darin können Sie die privaten Schlüssel definieren, die für diesen Partner ein- und ausgehend "
            + "auf jeden Fall verwendet werden - unabhängig von den Einstellungen der jeweiligen lokalen Station.<br>"
            + "Diese Option ermöglicht es Ihnen, für jeden Partner bei gleicher lokaler Station unterschiedliche private "
            + "Schlüssel zu verwenden.<br><br>Dies ist eine Option für die Kompatibilität mit anderen AS2 Produkten - manche Systeme "
            + "haben genau diese Anforderungen, die jedoch eine Konfiguration von Beziehungen von Partnern und "
            + "nicht Einzelpartnern voraussetzen."
            + "</HTML>"},
        {"label.showhttpheader", "Partnerverwaltung: Anzeige der HTTP Header Konfiguration"},
        {"label.showhttpheader.help", "<HTML><strong>Anzeige der HTTP Header Konfiguration</strong><br><br>"
            + "Wenn Sie diese Option einschalten, wird in der Partnerverwaltung pro Partner ein zusätzlicher "
            + "Tab angezeigt, in dem Sie benutzerdefinierte HTTP Header für den Datenversand an diesen Partner "
            + "definieren können."
            + "</HTML>"},
        {"label.showquota", "Partnerverwaltung: Anzeige der Benachrichtigungskonfiguration (Quota)"},
        {"label.checkrevocationlists", "Zertifikate: Sperrlisten prüfen"},
        {"label.checkrevocationlists.help", "<HTML><strong>Zertifikate: Sperrlisten prüfen</strong><br><br>"
            + "Eine Sperrliste ist eine Liste von Zertifikaten, die aufgrund verschiedener Sicherheitsbedenken oder "
            + "Probleme für ungültig erklärt wurden. Diese Probleme können beispielsweise die Kompromittierung "
            + "des privaten Schlüssels, der Verlust des Zertifikats oder der Verdacht auf betrügerische "
            + "Aktivitäten sein. Die Sperrlisten werden von Zertifizierungsstellen oder anderen vertrauenswürdigen "
            + "Entitäten verwaltet, die autorisiert sind, Zertifikate auszustellen. "
            + "Das Prüfen der Sperrlisten ist wichtig, um sicherzustellen, dass die in einer "
            + "Verbindung oder für eine kryptographische Operation verwendeten Zertifikate gültig "
            + "und vertrauenswürdig sind. Ein Zertifikat, das auf einer Sperrliste steht, "
            + "sollte nicht mehr für kryptographische Operationen verwendet werden, da es potenziell unsicher ist "
            + "und Risiken für die Integrität der Kommunikation darstellen könnte.<br><br>"
            + "Mit Hilfe dieser Einstellung können Sie bestimmen, ob das System in der Konfigurationsprüfung auch die "
            + "Sperrlisten überprüft."
            + "</HTML>"},
        {"autoimport.tls", "TLS Zertifikate: Automatischer Import wenn verändert"},
        {"autoimport.tls.help", "<HTML><strong>TLS Zertifikate: Automatischer Import wenn verändert</strong><br><br>"
            + "Wenn eine Partneranbindung über HTTPS realisiert ist (TLS, die URL beginnt mit \"https\"), kann regelmässig geprüft werden, "
            + "ob sich das TLS Zertifikat auf Partnerseite verändert hat. Wurde es verändert und ist noch nicht in Ihrem System, wird es dann automatisch mit "
            + "der gesamten Beglaubigungskette importiert.<br>"
            + "Das System überprüft die Partnerzertifikate alle " 
            + PartnerTLSCertificateChangedController.CHECK_DELAY_IN_MIN + " Minuten. Es kann also etwas dauern, bevor eine "
            + "Änderung des TLS Zertifikats eines Partner erkannt wird.<br><br>"            
            + "Sie können diesen Vorgang auch manuell durchführen, indem Sie einen Verbindungstest zu einem Partner "
            + "vornehmen und dann fehlende TLS Zertifikate importieren.<br><br>"
            + "Bitte beachten Sie, dass dies auf Sicherheitsebene eine problematische Einstellung ist, weil Sie damit automatisch einem "
            + "gefundenen Zertifikat vertrauen - ohne Nachfrage."
            + "</HTML>"
        },};

}
