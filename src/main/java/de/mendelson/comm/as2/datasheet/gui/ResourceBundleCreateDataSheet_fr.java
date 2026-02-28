//$Header: /as2/de/mendelson/comm/as2/datasheet/gui/ResourceBundleCreateDataSheet_fr.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.datasheet.gui;

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
public class ResourceBundleCreateDataSheet_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.usessl", "Utiliser TLS"},
		{"label.newpartner", "Nouveau partenaire - pas encore dans le système"},
		{"label.receipturl", "Votre URL pour la réception AS2"},
		{"label.remotepartner", "Partenaire distant"},
		{"label.usedatasignature", "Utilise des données signées"},
		{"title", "Fiche technique pour la nouvelle connexion de communication"},
		{"label.usedataencryption", "Utilise le cryptage des données"},
		{"label.localpartner", "Partenaire local"},
		{"label.requestsignedeerp", "Attente de l''EERP signé"},
		{"button.cancel", "Annuler"},
		{"label.comment", "Commentaire"},
		{"label.signedmdn", "MDN signés"},
		{"label.compression", "Compression des données"},
		{"label.usesessionauth", "Utiliser Session Auth"},
		{"button.ok", ">> Créer une fiche technique"},
		{"label.syncmdn", "MDN synchrone"},
		{"file.written", "La fiche technique (PDF) a été écrite après \"{0}\". Veuillez l''envoyer à votre nouveau partenaire afin d''échanger les données marginales de la communication."},
		{"progress", "Créer un PDF"},
		{"label.signature", "Signature numérique"},
		{"label.encryption", "Cryptage"},
		{"label.info", "<HTML><strong>Ce dialogue vous permet de créer une fiche technique qui facilite la connexion d''un nouveau partenaire.</strong></HTML>.</HTML>"},
	};
}
