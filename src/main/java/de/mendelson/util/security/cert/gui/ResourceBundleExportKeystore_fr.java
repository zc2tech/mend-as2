//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeystore_fr.java 4     9/12/24 15:51 Heller $ 
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
 * @version $Revision: 4 $
 */
public class ResourceBundleExportKeystore_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"button.ok", "Ok"},
        {"button.cancel", "Annuler"},
        {"button.browse", "Parcourir"},
        {"label.exportdir", "Répertoire d''exportation"},
        {"label.exportdir.hint", "Répertoire dans lequel le fichier keystore est créé"},
        {"label.exportdir.help", "<HTML><strong>Répertoire d''exportation</strong><br><br>"
            + "Veuillez indiquer le répertoire d''exportation vers lequel la base de données<br>"
            + "de clés doit être exportée.<br>"
            + "Pour des raisons de sécurité, les clés ne sont pas transférées au client,<br>"
            + "de sorte que seule la sauvegarde côté serveur est possible.<br>"
            + "Le système créera un fichier de sauvegarde dans ce répertoire, qui contiendra un horodatage."
            + "</HTML>"},
        {"label.keypass", "Mot de passe"},
        {"label.keypass.hint", "Mot de passe exporté du keystore"},
        {"label.keypass.help", "<HTML><strong>Mot de passe</strong><br><br>"
            + "Il s''agit du mot de passe avec lequel la base de données exportée côté serveur est "
            + "sécurisée.<br>Veuillez saisir \"test\" si cette base de données doit "
            + "être automatiquement importée ultérieurement dans le produit mendelson."
            + "</HTML>"},
        {"title", "Exporter toutes les entrées dans la base de données des clés"},
        {"filechooser.key.export", "Veuillez sélectionner le répertoire d'exportation sur le serveur"},
        {"keystore.export.success.title", "Succès"},
        {"keystore.export.error.message", "Une erreur s''est produite au cours du processus d''exportation.\n{0}"},
        {"keystore.export.error.title", "Erreur"},
        {"keystore.exported.to.file", "Le fichier keystore a été écrit dans le fichier keystore \"{0}\"."},};

}
