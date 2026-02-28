//$Header: /oftp2/de/mendelson/util/clientserver/ResourceBundleGUIClient_fr.java 3     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * ResourceBundle to localize the mendelson products - if you want to localize
 * eagle to your language, please contact us: localize@mendelson.de
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleGUIClient_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        //dialog
        {"password.required", "Erreur lors de la connexion, un mot de passe est nécessaire pour l''utilisateur {0}."},
        {"connectionrefused.message", "{0}: Connexion impossible. Veuillez vous assurer que le serveur est en cours d''exécution."},
        {"connectionrefused.title", "Problème de connexion"},
        {"login.success", "Connecté en tant qu''utilisateur \"{0}\""},
        {"login.failure", "Echec de la connexion en tant qu''utilisateur \"{0}\""},
        {"connection.success", "Client connecté à {0}"},
        {"logout.from.server", "Une déconnexion du serveur a été effectuée"},
        {"connection.closed", "La connexion locale client-serveur a été interrompue par le serveur"},
        {"connection.closed.title", "Interruption de la connexion locale"},
        {"connection.closed.message", "La connexion locale client-serveur a été interrompue par le serveur"},
        {"client.received.unprocessed.message", "Le serveur a envoyé un message qui n''a pas été traité par le client: {0}"},
        {"error", "Problem: {0}"},
        {"login.failed.client.incompatible.message", "Le serveur signale que ce client n''a pas la bonne version.\n"
            + "Veuillez utiliser le client qui correspond au serveur."
        },
        {"login.failed.client.incompatible.title", "Login rejeté"},};
}
