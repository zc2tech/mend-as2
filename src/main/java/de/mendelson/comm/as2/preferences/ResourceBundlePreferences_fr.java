//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferences_fr.java 89    9/12/24 16:03 Heller $
package de.mendelson.comm.as2.preferences;

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
 * @version $Revision: 89 $
 */
public class ResourceBundlePreferences_fr extends MecResourceBundle {

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
        {PreferencesAS2.DIR_MSG, "Archivage message"},
        {"button.ok", "Valider"},
        {"button.cancel", "Annuler"},
        {"button.modify", "Modifier"},
        {"button.browse", "Parcourir..."},
        {"filechooser.selectdir", "Sélectionner un répertoire"},
        {"title", "Préférences"},
        {"tab.language", "Client"},
        {"tab.dir", "Répertoires"},
        {"tab.security", "Sécurité"},
        {"tab.proxy", "Proxy"},
        {"tab.misc", "Divers"},
        {"tab.maintenance", "Maintenance"},
        {"tab.notification", "Notification"},
        {"tab.interface", "Modules"},
        {"tab.log", "Journal"},
        {"tab.connectivity", "Connectivité"},
        {"header.dirname", "Type"},
        {"header.dirvalue", "Rép."},
        {"label.language", "Langue"},
        {"label.language.help", "<HTML><strong>Langue</strong><br><br>"
            + "Il s''agit de la langue d''affichage du client. Si vous exécutez le client "
            + "et le serveur dans des processus différents (ce qui est recommandé), "
            + "la langue du serveur peut être différente. La langue utilisée dans "
            + "le journal sera toujours la langue du serveur."
            + "</HTML>"},
        {"label.country", "Pays/Région"},
        {"label.country.help", "<HTML><strong>Pays/Région</strong><br><br>"
            + "Ce paramètre contrôle principalement le format de date qui est utilisé pour afficher les dates de transaction, etc. dans le client."
            + "</HTML>"},
        {"label.displaymode", "Affichage"},
        {"label.displaymode.help", "<HTML><strong>Affichage</strong><br><br>"
            + "Cette fonction permet de définir l''un des modes d'affichage pris en charge par l''interface utilisateur.<br>"
            + "Il peut également être défini via des paramètres de ligne de commande lors de l''appel de l''exécutable ou du script de démarrage correspondant."
            + "</HTML>"},
        {"label.keystore.https.pass", "Mot de passe du porte-clef (envoi https):"},
        {"label.keystore.pass", "Mot de passe du porte-clef (encryption/signature):"},
        {"label.keystore.https", "Porte-clef (envoi https):"},
        {"label.keystore.encryptionsign", "Porte-clef (enc, sign):"},
        {"label.proxy.url", "URL du proxy"},
        {"label.proxy.url.hint", "IP ou domaine du proxy"},
        {"label.proxy.port.hint", "Port"},
        {"label.proxy.user", "Utilisateur"},
        {"label.proxy.user.hint", "Login utilisateur du proxy"},
        {"label.proxy.pass", "Mot de passe"},
        {"label.proxy.pass.hint", "Mot de passe utilisateur du proxy"},
        {"label.proxy.use", "Utiliser un proxy HTTP pour les connexions sortante HTTP/HTTPs"},
        {"label.proxy.useauthentification", "Utiliser l''authentification auprès du proxy"},
        {"filechooser.keystore", "Merci de sélectionner le fichier porte-clef (format jks)."},
        {"label.days", "jours"},
        {"label.autodelete", "Supprimer automatiquement"},
        {"label.deletemsgolderthan", "Les entrées de transactions plus vieux que"},
        {"label.deletemsglog", "Tenir informer dans le log à propos des messages automatiquement supprimés"},
        {"label.deletemsglog.help", "<HTML><strong>Tenir informer dans le log à propos des messages automatiquement supprimés</strong><br><br>"
            + "Dans les paramètres, vous avez la possibilité de faire supprimer les anciens fichiers (Maintenance).<br>"
            + "Si vous "
            + "avez configuré et activé cette option, chaque processus de suppression d''un ancien fichier est consigné. "
            + "De plus, un événement système est généré, ce qui vous permet d''être informé de ce processus via la "
            + "fonction de notification."
            + "</HTML>"},
        {"label.deletestatsolderthan", "Les statistiques qui sont plus vieux que"},
        {"label.deletelogdirolderthan", "Des données de journal plus anciennes que"},
        {"label.asyncmdn.timeout", "Temps d''attente maximal pour un MDN asynchrone"},
        {"label.asyncmdn.timeout.help", "<HTML><strong>Temps d''attente maximal pour un MDN asynchrone</strong>"
            + "<br><br>Le temps que le système attendra un MDN (message delivery notification) asynchrone pour un message AS2 envoyé avant de mettre la transaction en état d'échec.<br>"
            + "Cette valeur est valable dans tout le système pour tous les partenaires.<br><br>La valeur par défaut est de 30 min."
            + "</HTML>"},
        {"label.httpsend.timeout", "Timeout sur envoi HTTP/S"},
        {"label.httpsend.timeout.help", "<HTML><strong>Timeout sur envoi HTTP/S</strong><br><br>"
            + "Il s''agit du délai de connexion au réseau pour les connexions sortantes.<br>"
            + "Si, après ce délai, aucune connexion n''a été établie avec votre système partenaire, la tentative de connexion "
            + "est annulée et d''autres tentatives de connexion seront effectuées ultérieurement, le cas échéant, en fonction "
            + "des paramètres de répétition.<br><br>"
            + "La valeur par défaut est " + PreferencesAS2.getDefaultValue(PreferencesAS2.HTTP_SEND_TIMEOUT) + "."
            + "</HTML>"},
        {"label.min", "min"},
        {"receipt.subdir", "Créer des sous-répertoires par partenaires pour les messages reçus"},
        {"receipt.subdir.help", "<HTML><strong>Sous-répertoires pour l''accueil</strong><br><br>"
            + "Définit si les données doivent être reçues dans le répertoire <strong>&lt;Station locale&gt;/inbox</strong>"
            + " ou <strong>&lt;Station locale&gt;/inbox/&lt;Nom du partenaire&gt;</strong>."
            + "</HTML>"},
        //notification
        {"checkbox.notifycertexpire", "Expiration de certificats"},
        {"checkbox.notifytransactionerror", "Erreurs de transaction"},
        {"checkbox.notifycem", "Echange certificats (CEM)"},
        {"checkbox.notifyfailure", "Problems système"},
        {"checkbox.notifyresend", "Renvoie rejetés"},
        {"checkbox.notifyconnectionproblem", "Problèmes de connexion"},
        {"checkbox.notifypostprocessing", "Problèmes lors du post-traitement"},
        {"checkbox.notifyclientserver", "Problèmes client-serveur"},
        {"button.testmail", "Envoyer un e-mail de test"},
        {"label.mailhost", "Hôte du serveur de mail (SMTP)"},
        {"label.mailhost.hint", "IP ou domaine du serveur"},
        {"label.mailport", "Port"},
        {"label.mailport.hint", "Port"},
        {"label.mailport.help", "<HTML><strong>SMTP Port</strong><br><br>"
            + "En général, il s''agit de l'une de ces valeurs:<br>"
            + "<strong>25</strong> (Port standard)<br>"
            + "<strong>465</strong> (Port TLS, valeur obsolète)<br>"
            + "<strong>587</strong> (Port TLS, valeur par défaut)<br>"
            + "<strong>2525</strong> (Port TLS, valeur alternative, pas de standard)"
            + "</HTML>"},
        {"label.mailaccount", "Compte sur le serveur de mail"},
        {"label.mailpass", "Mot de passe sur le serveur de mail"},
        {"label.notificationmail", "Adresse de notification du destinataire"},
        {"label.notificationmail.help", "<HTML><strong>Adresse de notification du destinataire</strong><br><br>"
            + "L''adresse e-mail du destinataire de la notification.<br>"
            + "Si la notification doit être envoyée à plusieurs destinataires, veuillez saisir ici une liste séparée par des virgules d''adresses de réception."
            + "</HTML>"},
        {"label.replyto", "Adresse de réponse (Replyto)"},
        {"label.smtpauthorization.header", "Autorisation SMTP"},
        {"label.smtpauthorization.credentials", "Utilisateur/Mot de passe"},
        {"label.smtpauthorization.none", "Aucun"},
        {"label.smtpauthorization.oauth2.authorizationcode", "OAuth2 (Authorization code)"},
        {"label.smtpauthorization.oauth2.clientcredentials", "OAuth2 (Client credentials)"},
        {"label.smtpauthorization.user", "Utilisateur"},
        {"label.smtpauthorization.user.hint", "Nom d''utilisateur du serveur SMTP"},
        {"label.smtpauthorization.pass", "Mot de passe"},
        {"label.smtpauthorization.pass.hint", "Mot de passe du serveur SMTP"},
        {"label.security", "Sécurité de connexion"},
        {"testmail.message.success", "E-mail de test envoyé avec succés, a {0}"},
        {"testmail.message.error", "Erreur lors de l''envoi de l''e-mail de test:\n{0}"},
        {"testmail.title", "Résultat de l''envoi de l''email de test"},
        {"testmail", "L''email de test"},
        {"info.restart.client", "Un redémarrage du client est requise pour effectuer ces modifications valide!"},
        {"remotedir.select", "Sélectionnez le répertoire sur le serveur"},
        //retry
        {"label.retry.max", "Le nombre maximum de tentatives de connexion"},
        {"label.retry.max.help", "<HTML><strong>Le nombre maximum de tentatives de connexion</strong>"
            + "<br><br>Il s'agit du nombre de tentatives utilisées pour relancer les connexions à "
            + "un partenaire si une connexion n'a pas pu être établie. Le temps d''attente entre ces "
            + "tentatives peut être configuré dans la propriété <strong>Le temps d''attente entre deux tentatives de connexion</strong>.<br><br>"
            + "La valeur par défaut est " + PreferencesAS2.getDefaultValue(PreferencesAS2.MAX_CONNECTION_RETRY_COUNT) + "."
            + "</HTML>"},
        {"label.retry.waittime", "Le temps d''attente entre deux tentatives de connexion"},
        {"label.retry.waittime.help", "<HTML><strong>Le temps d''attente entre deux tentatives de connexion</strong>"
            + "<br><br>Il s''agit du temps en secondes que le système attendra avant de se reconnecter "
            + "au partenaire. Une nouvelle tentative de connexion n''est effectuée que s''il a été "
            + "impossible d'établir une connexion avec un partenaire (par exemple, système du partenaire "
            + "hors service ou problème d''infrastructure). Le nombre de tentatives de connexion peut être "
            + "configuré dans la propriété <strong>Le nombre maximum de tentatives de connexion</strong>.<br><br>"
            + "La valeur par défaut est " + PreferencesAS2.getDefaultValue(PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S) + "s."
            + "</HTML>"},
        {"label.sec", "s"},
        {"keystore.hint", "<HTML><strong>Attention:</strong><br>Veuillez modifier ces paramètres uniquement si vous souhaitez "
            + "utiliser des keystores externes pour les intégrer. Avec des chemins modifiés, des problèmes peuvent survenir lors de la mise à jour.</HTML>"},
        {"maintenancemultiplier.day", "jour(s)"},
        {"maintenancemultiplier.hour", "heure(s)"},
        {"maintenancemultiplier.minute", "minute(s)"},
        {"label.logpollprocess", "Informations sur le processus de vote"},
        {"label.logpollprocess.help", "<HTML><strong>Informations sur le processus de vote</strong><br><br>"
            + "Si vous activez cette option, chaque opération d'interrogation d''un répertoire de "
            + "départ est notée dans le journal. Comme il peut s'agir d'un grand nombre d'entrées, "
            + "n''utilisez en aucun cas cette option en mode productif, mais uniquement à des "
            + "fins de test."
            + "</HTML>"},
        {"label.max.outboundconnections", "Connexions sortantes parallèles (max)"},
        {"label.max.outboundconnections.help", "<HTML><strong>Connexions sortantes parallèles (max)</strong><br><br>"
            + "Il s''agit du nombre maximal de connexions sortantes parallèles que votre système ouvrira. "
            + "Cette valeur est principalement disponible pour éviter que le système de votre partenaire ne "
            + "soit inondé de connexions entrantes de votre côté.<br><br>"
            + "La valeur par défaut est " + PreferencesAS2.getDefaultValue(PreferencesAS2.MAX_OUTBOUND_CONNECTIONS) + "."
            + "</HTML>"},
        {"label.max.inboundconnections", "Max connexions parallèles entrantes"},
        {"label.max.inboundconnections.help", "<HTML><strong>Max connexions parallèles entrantes</strong><br><br>"
            + "Il s''agit du nombre maximal de connexions entrantes parallèles qui peuvent être ouvertes depuis l''extérieur vers votre installation mendelson AS2. "
            + "Cette valeur s''applique à l''ensemble du logiciel et n''est pas limitée à des partenaires individuels.<br>"
            + "Ce paramètre est transmis au serveur HTTP embarqué, vous devez redémarrer le serveur AS2 après une modification.<br><br>"
            + "Bien qu''il soit possible de limiter le nombre de connexions entrantes parallèles, il est préférable de régler "
            + "ce paramètre dans votre pare-feu ou dans votre proxy en amont - cela s''applique alors à l''ensemble de votre système "
            + "et pas seulement à un seul logiciel.<br><br>"
            + "La valeur par défaut est " + PreferencesAS2.getDefaultValue(PreferencesAS2.MAX_INBOUND_CONNECTIONS) + "."
            + "</HTML>"},
        {"event.preferences.modified.subject", "La valeur {0} du paramètre serveur a été modifiée"},
        {"event.preferences.modified.body", "Valeur précédente: {0}\n\nNouvelle valeur: {1}"},
        {"event.notificationdata.modified.subject", "Les paramètres de notification ont été modifiés."},
        {"event.notificationdata.modified.body", "Les données d''avis sont passées de\n\n{0}\n\nà\n\n{1}"},
        {"label.maxmailspermin", "Nombre maximum de notifications/min"},
        {"label.maxmailspermin.help", "<HTML><strong>Nombre maximum de notifications/min</strong><br><br>"
            + "Pour éviter un trop grand nombre de courriers, vous pouvez résumer toutes les notifications en "
            + "définissant le nombre maximum de notifications qui seront envoyées par minute. En utilisant "
            + "cette fonctionnalité, vous recevrez des courriers contenant plusieurs notifications."
            + "</HTML>"},
        {"systemmaintenance.deleteoldtransactions.help", "<HTML><strong>Supprimer les anciennes entrées de transactions</strong><br><br>"
            + "Ce paramètre définit la période pendant laquelle les entrées de transactions et les données associées "
            + "(par exemple, les fichiers temporaires) restent dans le système et doivent être affichées dans l''aperçu "
            + "des transactions.<br>Ces paramètres n''affectent pas vos données/fichiers reçus, ils ne sont pas affectés."
            + "<br>Même pour les transactions supprimées, le journal des transactions est toujours disponible via la "
            + "fonction recherche log.</HTML>"},
        {"systemmaintenance.deleteoldstatistic.help", "<HTML><strong>Suppression des anciennes données statistiques</strong><br><br>"
            + "Le système collecte les données de compatibilité des systèmes partenaires et peut les afficher sous forme de "
            + "statistiques. Cela détermine la période pendant laquelle ces données sont conservées.</HTML>"},
        {"systemmaintenance.deleteoldlogdirs.help", "<HTML><strong>Suppression des anciens répertoires de logs</strong><br><br>"
            + "Même si les anciennes transactions ont été supprimées, les opérations peuvent toujours "
            + "être retracées grâce aux fichiers journaux existants. Ce paramètre permet de supprimer ces "
            + "fichiers journaux ainsi que tous les fichiers relatifs aux événements du système qui tombent dans la même période."
            + "</HTML>"},
        {"label.colorblindness", "Support pour le daltonisme"},
        {"warning.clientrestart.required", "Les paramètres du client ont été modifiés - veuillez redémarrer le client pour les rendre valides"},
        {"warning.serverrestart.required", "Veuillez redémarrer le serveur pour que ces modifications soient valables."},
        {"warning.changes.canceled", "L''utilisateur a annulé la boîte de dialogue des paramètres - aucune modification n''a été apportée aux paramètres."},
        {"label.darkmode", "Mode sombre"},
        {"label.litemode", "Mode allégé"},
        {"label.hicontrastmode", "Mode contraste élevé"},
        {"label.trustallservercerts", "TLS: Faire confiance à tous les certificats de serveur final de vos partenaires AS2"},
        {"label.trustallservercerts.help", "<HTML><strong>TLS: Faire confiance à tous les certificats de serveur final de vos partenaires AS2</strong><br><br>"
            + "Normalement, TLS exige que tous les certificats de la chaîne de confiance du système AS2 de votre partenaire soient conservés dans votre gestionnaire de certificats TLS. "
            + "Si vous activez cette option, vous faites confiance au certificat final de votre système partenaire lors de l''établissement de la connexion sortante, "
            + "si vous ne conservez que les certificats racine et intermédiaires correspondants dans le gestionnaire de certificats TLS. "
            + "Veuillez noter que cette option n''est utile que si votre partenaire utilise un certificat certifié - les certificats auto-signés sont de toute façon acceptés."
            + "<br><br><strong>Avertissement:</strong> l''activation de cette option diminue le niveau de sécurité, car des attaques man-in-the-middle sont possibles!"
            + "</HTML>"},
        {"label.stricthostcheck", "TLS: Faire confiance à tous les hôtes"},
        {"label.stricthostcheck.help", "<HTML><strong>TLS: Faire confiance à tous les hôtes</strong><br><br>"
            + "Vous indiquez ici si, dans le cas d''une connexion TLS sortante, il faut vérifier si le nom commun (CN) "
            + "du certificat distant correspond à l''hôte distant. Cette vérification ne s''applique qu'aux certificats "
            + "authentifiés."
            + "</HTML>"},
        {"label.httpport", "Port d''entrée HTTP"},
        {"label.httpport.help", "<HTML><strong>Port d''entrée HTTP</strong><br><br>"
            + "Il s''agit du port pour les connexions entrantes non cryptées. Ce paramètre est transmis au serveur "
            + "HTTP embarqué, vous devez redémarrer le serveur AS2 après une modification.<br>"
            + "Le port fait partie de l''URL à laquelle votre partenaire doit envoyer les messages AS2. Il s''agit de http://host:<strong>port</strong>/as2/HttpReceiver<br><br>"
            + "La valeur par défaut est " + PreferencesAS2.getDefaultValue(PreferencesAS2.HTTP_LISTEN_PORT) + "."
            + "</HTML>"
        },
        {"label.httpsport", "Port d''entrée HTTPS"},
        {"label.httpsport.help", "<HTML><strong>Port d''entrée HTTPS</strong><br><br>"
            + "Il s''agit du port pour les connexions entrantes cryptées. "
            + "Ce paramètre est transmis au serveur "
            + "HTTP embarqué, vous devez redémarrer le serveur AS2 après une modification.<br>"
            + "Le port fait partie de l''URL à laquelle votre partenaire doit envoyer les messages AS2. Il s''agit de https://host:<strong>port</strong>/as2/HttpReceiver<br><br>"
            + "La valeur par défaut est " + PreferencesAS2.getDefaultValue(PreferencesAS2.HTTPS_LISTEN_PORT) + "."
            + "</HTML>"
        },
        {"embedded.httpconfig.not.available", "Serveur HTTP non disponible ou problèmes d''accès au fichier de configuration"},
        {"button.mailserverdetection", "Détecter serveur"},
        {"label.loghttprequests", "Journal des requêtes HTTP du serveur HTTP intégré"},
        {"label.loghttprequests.help", "<HTML><strong>HTTP Request Log</strong><br><br>"
            + "Si une connexion partenaire est réalisée via HTTPS "
            + "(TLS, l''URL commence par https), il est possible de vérifier régulièrement si "
            + "le certificat TLS a été modifié. S''il a été modifié et n'est pas encore dans le système, "
            + "il est alors automatiquement importé avec toute la chaîne d''authentification.<br><br>"
            + "Veuillez noter qu''il s''agit d''un réglage problématique au niveau de la sécurité, "
            + "car vous faites ainsi automatiquement confiance à un certificat trouvé - sans demande."
            + "</HTML>"
        },};
}
