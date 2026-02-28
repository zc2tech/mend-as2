//$Header: /as2/de/mendelson/comm/as2/partner/gui/ResourceBundlePartnerConfig_es.java 2     9/12/24 16:02 Heller $
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
* @version $Revision: 2 $
*/
public class ResourceBundlePartnerConfig_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"dialog.partner.deletedir.message", "El interlocutor \"{0}\" ha sido eliminado de la configuración. Si el directorio asociado\n\"{1}\"\ndebe ser borrado del disco duro?"},
		{"event.partner.modified.subject", "El interlocutor {0} ha sido modificado por el usuario"},
		{"directory.delete.failure", "No se ha podido eliminar el directorio \"{0}\": [\"{1}\"]"},
		{"title", "Configuración de socios"},
		{"localstation.noprivatekey.title", "Sin clave privada"},
		{"text.configurationproblem", "<HTML>Hay errores en la configuración del socio - por favor, corríjalos antes de guardar.</HTML>"},
		{"dialog.partner.delete.title", "Suprimir un socio"},
		{"button.delete", "Borrar"},
		{"localstation.noprivatekey.message", "La estación local debe tener asignada una clave privada."},
		{"dialog.partner.renamedir.message", "El interlocutor \"{0}\" ha sido renombrado a \"{1}\". Si el directorio correspondiente\n\"{2}\"\nen el disco duro debe ser renombrado?"},
		{"dialog.partner.delete.message", "Está a punto de eliminar el interlocutor \"{0}\" de la configuración de interlocutores.\nSe perderán todos los datos de interlocutor de \"{0}\".\n\n¿Realmente desea eliminar el interlocutor \"{0}\"?"},
		{"nolocalstation.message", "Al menos uno de los socios debe definirse como estación local."},
		{"button.clone", "Copia"},
		{"event.partner.modified.body", "Datos anteriores del socio:\n\n{0}\n\nNuevos datos del interlocutor:\n\n{1}"},
		{"button.new", "Nuevo"},
		{"directory.rename.failure", "El directorio \"{0}\" no puede renombrarse a \"{1}\"."},
		{"event.partner.added.body", "Datos del nuevo socio:\n\n{0}"},
		{"nolocalstation.title", "Ninguna emisora local"},
		{"module.locked", "La administración de socios está abierta exclusivamente por otro cliente, ¡no puede guardar sus cambios!"},
		{"event.partner.added.subject", "El interlocutor {0} ha sido añadido por el usuario de la administración de interlocutores"},
		{"button.cancel", "Cancelar"},
		{"dialog.partner.renamedir.title", "Cambiar el nombre del directorio de mensajes"},
		{"saving", "Guardar..."},
		{"button.globalchange", "Global"},
		{"button.ok", "Ok"},
		{"directory.delete.success", "El directorio \"{0}\" ha sido borrado."},
		{"event.partner.deleted.subject", "El interlocutor {0} ha sido eliminado de la administración de interlocutores por el usuario"},
		{"dialog.partner.deletedir.title", "Borrar un directorio de mensajes"},
		{"event.partner.deleted.body", "Datos del socio eliminado:\n\n{0}"},
		{"directory.rename.success", "El directorio \"{0}\" ha sido renombrado a \"{1}\"."},
	};
}
