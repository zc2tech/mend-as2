//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleCertificates_it.java 4     11/03/25 16:42 Heller $
package de.mendelson.util.security.cert.gui;

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
public class ResourceBundleCertificates_it extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"cert.delete.impossible", "La voce non può essere cancellata, è in uso.\nPer ulteriori informazioni, utilizzare \"Mostra utilizzo\"."},
        {"label.keystore.export", "Esportazione di tutte le voci in un file keystore (solo per scopi di backup!)"},
        {"certificate.import.alias", "Alias per questo certificato:"},
        {"tab.info.basic", "dettagli"},
        {"warning.deleteallexpired.expired.but.used.text", "{0} Le chiavi/certificati scaduti sono utilizzati nella configurazione e quindi non vengono eliminati."},
        {"filechooser.certificate.import", "Selezionare il file di certificato per l''importazione"},
        {"button.import", "Importazione"},
        {"menu.export", "Esportazione"},
        {"certificate.ca.import.success.message", "Il certificato CA è stato importato con successo con l''alias \"{0}\"."},
        {"button.keycopy", "Copia nella gestione {0}"},
        {"button.delete", "Cancellare la chiave/certificato"},
        {"label.cert.valid", "Questo certificato è valido"},
        {"display.ca.certs", "Mostra i certificati CA ({0})"},
        {"keystore.readonly.message", "Protetto da scrittura. Non è possibile modificarlo."},
        {"keycopy.success.text", "La voce [{0}] è stata copiata con successo"},
        {"menu.tools.generatekey", "Generare una nuova chiave (autofirmata)"},
        {"label.key.export.pkcs12", "Chiave di esportazione (PKCS#12, PEM) (solo per scopi di backup!)"},
        {"button.keycopy.signencrypt", "Crittografia/firma"},
        {"label.key.invalid", "Questa chiave non è valida"},
        {"dialog.cert.delete.title", "Cancellare il certificato"},
        {"success.deleteallexpired.title", "Eliminare i certificati/chiavi scaduti e non utilizzati"},
        {"success.deleteallexpired.text", "{0} le chiavi/certificati scaduti e non utilizzati sono stati eliminati"},
        {"module.locked", "Questa gestione dei certificati è attualmente aperta esclusivamente da un altro cliente, non è possibile apportare modifiche!"},
        {"button.edit", "Rinominare l''alias"},
        {"label.keystore", "Posizione di stoccaggio"},
        {"warning.deleteallexpired.noneavailable.title", "Nessuno disponibile"},
        {"dialog.cert.delete.message", "Si vuole davvero eliminare il certificato con l''alias \"{0}\"?"},
        {"button.cancel", "Annullamento"},
        {"title.cert.in.use", "Il certificato viene utilizzato"},
        {"label.cert.invalid", "Questo certificato non è valido"},
        {"certificate.import.error.message", "Si è verificato un errore durante l''importazione:\n{0}"},
        {"button.delete.all.expired", "Cancellare tutte le chiavi/certificati scaduti"},
        {"button.keycopy.tls", "TLS"},
        {"button.ok", "Ok"},
        {"generatekey.error.message", "{0}"},
        {"menu.file.close", "Uscita"},
        {"label.selectcsrfile", "Selezionare il file in cui salvare la richiesta di autenticazione"},
        {"warning.deleteallexpired.noneavailable.text", "Non ci sono voci scadute e non utilizzate"},
        {"button.export", "Esportazione"},
        {"label.key.valid", "Questa chiave è valida"},
        {"certificate.import.success.message", "Il certificato è stato importato con successo con l''alias \"{0}\"."},
        {"title.signencrypt", "Chiavi e certificati (crittografia, firme)"},
        {"keycopy.target.ro.text", "Operazione fallita - il file chiave della destinazione è protetto da scrittura."},
        {"label.cert.export", "Certificato di esportazione (per il partner)"},
        {"warning.deleteallexpired.expired.but.used.title", "Chiavi/certificati utilizzati"},
        {"tab.info.trustchain", "Percorso di certificazione"},
        {"warning.deleteallexpired.text", "Si vuole davvero eliminare {0} voci scadute e inutilizzate?"},
        {"module.locked.title", "Il modulo è in uso"},
        {"title.ssl", "Chiavi e certificati (TLS)"},
        {"tab.info.extension", "Estensioni"},
        {"menu.tools", "Esteso"},
        {"button.reference", "Mostra l''utilizzo"},
        {"menu.tools.importcsr", "Certificato di autenticazione: Importa la risposta della CA alla richiesta di autenticazione"},
        {"module.locked.text", "Il modulo {0} è utilizzato esclusivamente da un altro client ({1})."},
        {"modifications.notalllowed.message", "Le modifiche non sono possibili"},
        {"keycopy.target.ro.title", "Il target è di sola lettura"},
        {"label.key.import", "Importare la propria chiave privata (da Keystore PKCS#12, JKS)"},
        {"menu.tools.importcsr.renew", "Rinnovare il certificato: Importazione della risposta della CA alla richiesta di autenticazione"},
        {"keycopy.target.exists.title", "La voce esiste già nella destinazione"},
        {"generatekey.error.title", "Errore durante la generazione della chiave"},
        {"label.trustanchor", "Ancora di fiducia"},
        {"menu.file", "File"},
        {"button.newkey", "Chiave di importazione"},
        {"certificate.import.error.title", "Errore"},
        {"certificate.import.success.title", "Il successo"},
        {"keystore.readonly.title", "Keystore protetto da scrittura - modifica non possibile"},
        {"label.cert.import", "Certificato di importazione (dal partner)"},
        {"warning.deleteallexpired.title", "Eliminare le chiavi/certificati scaduti e non utilizzati"},
        {"menu.tools.generatecsr.renew", "Rinnovare il certificato: Generare richiesta di autenticazione (alla CA)"},
        {"menu.tools.verifyall", "Controllare gli elenchi di revoca di tutti i certificati (CRL)"},
        {"warning.testkey", "Chiave del test di Mendelson disponibile pubblicamente - non utilizzare in operazioni produttive!"},
        {"keycopy.target.exists.text", "Questa voce esiste già nell''amministrazione del certificato di destinazione (alias {0})."},
        {"menu.tools.generatecsr", "Autenticare il certificato: Genera richiesta di autenticazione (alla CA)"},
        {"menu.import", "Importazione"},};
}
