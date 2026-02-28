//$Header: /oftp2/de/mendelson/util/security/cert/ResourceBundleKeystoreStorage_fr.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert;

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
* @version $Revision: 2 $
*/
public class ResourceBundleKeystoreStorage_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"error.delete.notloaded", "L''entrée n''a pas pu être supprimée, le keystore sous-jacent n''a pas encore été chargé."},
		{"error.nodata", "Le keystore n''a pas pu être lu : Aucune donnée disponible"},
		{"moved.keystore.to.db.title", "Importation d''un fichier keystore ({0})"},
		{"error.readaccess", "Le keystore n''a pas pu être lu : Impossible d''accéder en lecture à \"{0}\"."},
		{"moved.keystore.reason.commandline", "L''importation a été déclenchée par un paramètre de ligne de commande au démarrage du serveur."},
		{"error.save.notloaded", "Le keystore ne peut pas être enregistré, il n''a pas encore été chargé."},
		{"moved.keystore.to.db", "Importez les données du keystore de \"{0}\" dans le système - l''utilisation prévue est {1}. Les clés/certificats éventuellement existants ont été supprimés."},
		{"error.save", "Les données du keystore n''ont pas pu être enregistrées."},
		{"error.empty", "Le keystore n''a pas pu être lu : Les données du keystore doivent être plus longues que 0."},
		{"keystore.read.failure", "Le système n''a pas pu lire les certificats sous-jacents. Message d''erreur : \"{0}\". Veuillez vérifier que vous utilisez le bon mot de passe pour le keystore."},
		{"moved.keystore.reason.initial", "L''importation a été effectuée parce qu''il n''y a actuellement pas de magasin de clés interne au système. Il s''agit d''une procédure initiale."},
		{"error.filexists", "Le keystore n''a pas pu être lu : Le fichier keystore \"{0}\" n''existe pas."},
		{"error.notafile", "Le keystore n''a pas pu être lu : Le fichier keystore \"{0}\" n''est pas un fichier."},
	};
}
