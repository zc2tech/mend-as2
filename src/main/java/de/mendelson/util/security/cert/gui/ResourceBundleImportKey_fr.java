//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleImportKey_fr.java 5     9/12/24 15:51 Heller $
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
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @author E.Pailleau
 * @version $Revision: 5 $
 */
public class ResourceBundleImportKey_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"button.ok", "Valider"},
        {"button.cancel", "Annuler"},
        {"button.browse", "Parcourir..."},
        {"keystore.contains.nokeys", "Ce porte-clef ne contient aucune clef privée."},
        {"label.importkey", "Nom du fichier"},
        {"label.importkey.hint", "Fichier Keystore à importer (PKCS#12, JKS)"},
        {"label.keypass", "Mot de passe"},
        {"label.keypass.hint", "Mot de passe du Keystore (PKCS#12, JKS)"},
        {"title", "Importer les clefs du porte-clef (PKCS#12, JKS format)"},
        {"filechooser.key.import", "Merci de sélectionner le fichier porte-clef PKCS#12/JKS pour l''import"},
        {"multiple.keys.message", "Merci de sélectionner la clef à importer"},
        {"multiple.keys.title", "Le porte-clef contient plusieurs clefs"},
        {"key.import.success.message", "La clef a été importée avec succès."},
        {"key.import.success.title", "Succès"},
        {"key.import.error.message", "Une erreur a eu lieu lors du processus d''import.\n{0}"},
        {"key.import.error.title", "Erreur"},
        {"enter.keypassword", "Veuillez entrer le mot de passe clé pour \"{0}\"."},
        {"key.import.error.entry.exists", "Importation impossible - une entrée de ce fingerprint existe déjà avec l''alias {0}."},
    };

}
