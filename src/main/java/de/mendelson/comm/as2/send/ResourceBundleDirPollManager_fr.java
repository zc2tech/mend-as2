//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleDirPollManager_fr.java 17    6/02/25 8:23 Heller $
package de.mendelson.comm.as2.send;

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
* @author S.Heller
* @version $Revision: 17 $
*/
public class ResourceBundleDirPollManager_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"warning.ro", "[Surveillance du répertoire] Le fichier source {0} est protégé en écriture, ce fichier est ignoré."},
		{"warning.notcomplete", "[Surveillance du répertoire] Le fichier source {0} n''existe pas encore complètement, le fichier est ignoré."},
		{"title.list.polls.stopped", "Les surveillances suivantes ont été terminées"},
		{"processing.file", "Traite le fichier \"{0}\" pour la relation \"{1}/{2}\"."},
		{"none", "Pas de"},
		{"warning.noread", "[Surveillance du répertoire] Pas d''accès en lecture possible pour le fichier source {0}, le fichier est ignoré."},
		{"poll.started", "La surveillance du répertoire pour la relation \"{0}/{1}\" a été lancée. Ignorer : \"{2}\". Intervalle : {3}s"},
		{"poll.modified", "[Surveillance du répertoire] Les paramètres du partenaire pour la relation \"{0}/{1}\" ont été modifiés."},
		{"title.list.polls.started", "Les surveillances suivantes ont été lancées"},
		{"poll.stopped.notscheduled", "[Surveillance du répertoire] Le système a essayé d''arrêter la surveillance du répertoire pour \"{0}/{1}\" - mais il n''y a pas eu de surveillance."},
		{"processing.file.error", "Erreur de traitement du fichier \"{0}\" pour la relation \"{1}/{2}\" : \"{3}\"."},
		{"title.list.polls.running", "Résumé des répertoires surveillés :"},
		{"poll.log.polling", "[Surveillance du répertoire] {0}->{1} : Vérifier la présence de nouveaux fichiers dans le répertoire \"{2}\"."},
		{"manager.status.modified", "La surveillance des répertoires a modifié les surveillances des répertoires, {0} répertoires sont surveillés"},
		{"poll.stopped", "La surveillance du répertoire pour la relation \"{0}/{1}\" a été arrêtée."},
		{"poll.log.wait", "[Surveillance du répertoire] {0}->{1} : Processus de poll suivante dans {2}s ({3})"},
		{"messagefile.deleted", "Le fichier \"{0}\" a été supprimé et transmis à la file de traitement du serveur."},
	};
}
