//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeyPKCS12_fr.java 2     9/12/24 15:51 Heller $
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
 * @version $Revision: 2 $
 */
public class ResourceBundleExportKeyPKCS12_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"button.ok", "Valider"},
        {"button.cancel", "Annuler"},
        {"button.browse", "Parcourir..."},
        {"keystore.contains.nokeys", "Ce porte-clef ne contient aucune clef privée."},
        {"label.exportdir", "Répertoire d''exportation"},
        {"label.exportdir.hint", "Répertoire dans lequel la liste de clés doit être créée (PKCS#12)"},
        {"label.exportdir.help", "<HTML><strong>Répertoire d''exportation</strong><br><br>"
            + "Veuillez indiquer ici le répertoire d''exportation dans lequel la clé privée doit être exportée.<br>"
            + "Pour des raisons de sécurité, la clé n''est pas transférée sur le client, "
            + "c''est pourquoi seule une sauvegarde côté serveur est possible.<br><br>"
            + "Le système créera dans ce répertoire un fichier mémoire contenant un horodateur."
            + "</HTML>"},
        {"label.keypass", "Mot de passe"},
        {"label.keypass.hint", "Mot de passe pour keystore exporté"},
        {"title", "Export de la clef vers le porte-clef (PKCS#12 format)"},
        {"filechooser.key.export", "Veuillez sélectionner le répertoire d''exportation sur le serveur"},
        {"key.export.success.title", "Succès"},
        {"key.export.error.message", "Une erreur a eu lieu lors du processus d''export.\n{0}"},
        {"key.export.error.title", "Erreur"},
        {"label.alias", "Clef"},
        {"key.exported.to.file", "La clef \"{0}\" a été insérée dans le porte-clef \"{1}\" (PKCS#12)"},};

}
