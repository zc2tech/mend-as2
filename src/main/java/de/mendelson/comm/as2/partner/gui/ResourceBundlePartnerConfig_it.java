//$Header: /as2/de/mendelson/comm/as2/partner/gui/ResourceBundlePartnerConfig_it.java 4     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.partner.gui;

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
* @version $Revision: 4 $
*/
public class ResourceBundlePartnerConfig_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"dialog.partner.deletedir.message", "Il partner \"{0}\" è stato eliminato dalla configurazione. Se la directory associata\n\"{1}\"\ndeve essere eliminata dal disco rigido?"},
		{"event.partner.modified.subject", "Il partner {0} è stato modificato dall''utente"},
		{"directory.delete.failure", "Non è stato possibile cancellare la directory \"{0}\": [\"{1}\"]"},
		{"title", "Configurazione del partner"},
		{"localstation.noprivatekey.title", "Nessuna chiave privata"},
		{"text.configurationproblem", "<HTML>Ci sono errori nella configurazione del partner: correggerli prima di salvare.</HTML>"},
		{"dialog.partner.delete.title", "Eliminazione di un partner"},
		{"button.delete", "Cancellare"},
		{"localstation.noprivatekey.message", "Alla stazione locale deve essere assegnata una chiave privata."},
		{"dialog.partner.renamedir.message", "Il partner \"{0}\" è stato rinominato in \"{1}\". Se la directory corrispondente\n\"{2}\"\nsul disco rigido deve essere rinominata?"},
		{"dialog.partner.delete.message", "Si sta per eliminare il partner \"{0}\" dalla configurazione del partner.\nTutti i dati del partner \"{0}\" andranno persi.\n\nSi desidera davvero eliminare il partner \"{0}\"?"},
		{"nolocalstation.message", "Almeno un partner deve essere definito come stazione locale."},
		{"button.clone", "Copia"},
		{"event.partner.modified.body", "Dati precedenti del partner:\n\n{0}\n\nNuovi dati dell''interlocutore:\n\n{1}"},
		{"button.new", "Nuovo"},
		{"directory.rename.failure", "La directory \"{0}\" non può essere rinominata in \"{1}\"."},
		{"event.partner.added.body", "Dati del nuovo partner:\n\n{0}"},
		{"nolocalstation.title", "Nessuna stazione locale"},
		{"module.locked", "L''amministrazione del partner è aperta esclusivamente da un altro cliente, non è possibile salvare le modifiche!"},
		{"event.partner.added.subject", "Il partner {0} è stato aggiunto dall''utente dell''amministrazione del partner"},
		{"button.cancel", "Annullamento"},
		{"dialog.partner.renamedir.title", "Rinominare la directory dei messaggi"},
		{"saving", "Risparmiare..."},
		{"button.globalchange", "Globale"},
		{"button.ok", "Ok"},
		{"directory.delete.success", "La directory \"{0}\" è stata cancellata."},
		{"event.partner.deleted.subject", "Il partner {0} è stato cancellato dall''amministrazione dei partner dall''utente"},
		{"dialog.partner.deletedir.title", "Eliminazione di una directory di messaggi"},
		{"event.partner.deleted.body", "Dati del partner cancellato:\n\n{0}"},
		{"directory.rename.success", "La directory \"{0}\" è stata rinominata in \"{1}\"."},
	};
}
