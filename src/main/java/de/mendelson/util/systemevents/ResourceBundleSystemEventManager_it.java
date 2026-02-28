//$Header: /as4/de/mendelson/util/systemevents/ResourceBundleSystemEventManager_it.java 4     15/01/25 10:18 Heller $
package de.mendelson.util.systemevents;

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
public class ResourceBundleSystemEventManager_it extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"module.name", "[GESTORE EVENTI SISTEMA]"},
        {"label.subject.login.success", "Accesso utente riuscito [{0}]"},
        {"label.subject.logoff", "Logout utente [{0}]"},
        {"error.createdir.subject", "Generazione di directory"},
        {"label.body.clientversion", "Versione del client: {0}"},
        {"label.subject.login.failed", "Accesso utente fallito [{0}]"},
        {"label.error.clientserver", "Problema nella connessione client-server"},
        {"label.body.clientos", "Sistema operativo del client: {0}"},
        {"label.body.clientip", "Indirizzo IP: {0}"},
        {"label.body.processid", "Numero di processo nel sistema operativo del client: {0}"},
        {"label.body.tlsprotocol", "Protocollo TLS: {0}"},
        {"label.body.tlsciphersuite", "Cifrario TLS: {0}"},
        {"error.createdir.body", "Si è verificato un problema durante la creazione di una directory: {0}\nProblema: {1}"},
        {"label.body.details", "Dettagli: {0}"},
        {"error.in.systemevent.registration", "Non è stato possibile registrare un problema di sistema nel gestore degli eventi di sistema: {0}" },
    };
}
