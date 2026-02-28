//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2Server_it.java 4     18/02/25 14:39 Heller $
package de.mendelson.comm.as2.server;

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
 * @version $Revision: 4 $
 */
public class ResourceBundleAS2Server_it extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"server.willstart", "{0} inizia"},
        {"server.started.issue", "Attenzione: all''avvio del server è stato rilevato un problema di configurazione."},
        {"server.hello", "Questo è {0}"},
        {"server.shutdown", "{0} si spegne."},
        {"bind.exception", "{0}\nÈ stata definita una porta attualmente utilizzata da un altro processo nel sistema.\nPuò trattarsi della porta client-server o della porta HTTP/S definita nella configurazione HTTP.\nModificare la configurazione o interrompere l''altro processo prima di utilizzare il pulsante {1}."},
        {"server.already.running", "Sembra che un''istanza di mendelson AS2 sia già in esecuzione.\nTuttavia, potrebbe anche essere che un''istanza precedente non sia stata terminata correttamente. Se si è sicuri che nessun''altra istanza è in esecuzione,\ncancellare il file di blocco \"{0}\"\n(data di inizio {1}) e riavviare il server."},
        {"server.start.details", "{0} Parametro:\n\nAvvia il server HTTP integrato: {1}\nConsente connessioni client-server da altri host: {2}\nMemoria heap: {3}\nVersione di Java: {4}\nUtente del sistema: {5}\nIdentificazione del sistema: {6}"},
        {"server.hello.licenseexpire.single", "La licenza scade tra {0} giorni ({1}). È necessario rinnovare la licenza tramite il supporto mendelson (service@mendelson.de) se si desidera continuare a utilizzarla in seguito."},
        {"server.hello.licenseexpire", "La licenza scade tra {0} giorni ({1}). È necessario rinnovare la licenza tramite il supporto mendelson (service@mendelson.de) se si desidera continuare a usarla dopo questo periodo."},
        {"server.started", "mendelson AS2 2024 build 613 avviato in {0} ms."},
        {"server.startup.failed", "Si è verificato un problema nell''avvio del server: l''avvio è stato annullato."},
        {"server.started.issues", "Attenzione: all''avvio del server sono stati rilevati {0} problemi di configurazione."},
        {"fatal.limited.strength", "Questa VM Java non supporta la lunghezza della chiave richiesta. Prima di avviare il server AS2 di mendelson, installare i file \"Unlimited jurisdiction key strength policy\"."},
        {"server.nohttp", "Il server HTTP integrato non è stato avviato."},
        {"server.started.usedlibs", "Libretti usati"},};
}
