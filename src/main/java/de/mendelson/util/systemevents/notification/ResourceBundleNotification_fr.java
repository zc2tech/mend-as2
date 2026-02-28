//$Header: /oftp2/de/mendelson/util/systemevents/notification/ResourceBundleNotification_fr.java 18    9/12/24 15:51 Heller $
package de.mendelson.util.systemevents.notification;

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
 * @author E.Pailleau
 * @version $Revision: 18 $
 */
public class ResourceBundleNotification_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"module.name", "[NOTIFICATION MAIL]" },
        {"test.message.send", "Un e-mail de test a été envoyé à {0}."},
        {"test.message.debug", "\nEnvoyer un processus envoi a échoué.\n"},
        {"misc.message.send", "Un e-mail de notification a été envoyé à {0} ({1}-{2}-{3})."},
        {"misc.message.send.failed", "L''envoi d'un message de notification à {0} a échoué"},
        {"notification.about.event", "Cette notification se réfère à l'événement système de {0}.\nUrgence: {1}\nEnfin: {2}\nTyp: {3}\nId: {4}"},
        {"notification.summary", "Résumé des {0} événements système"},
        {"notification.summary.info", 
            "Ce message récapitulatif s''affiche parce que vous avez défini un nombre limité\n"
            + "d''avis par unité de temps. Pour obtenir les détails de chaque événement,\n"
            + "veuillez démarrer le client et naviguer vers\n"
            + "\"Fichier-Événements système\".\n"
            + "Entrez le numéro unique de l''événement dans le masque de recherche."},
        {"misc.message.summary.send", "Un courriel de notification sommaire a été envoyé à {0}"},
        {"misc.message.summary.failed", "L''envoi d''un message de notification sommaire à {0} a échoué"},
        {"do.not.reply", "Veuillez ne pas répondre à ce mail."},
        {"authorization.none", "AUCUN" },
        {"authorization.oauth2", "OAUTH2" },
        {"authorization.oauth2.authorizationcode", "Authorization code" },
        {"authorization.oauth2.clientcredentials", "Client credentials" },
        {"authorization.credentials", "Utilisateur/mot de passe" },
    };

}
