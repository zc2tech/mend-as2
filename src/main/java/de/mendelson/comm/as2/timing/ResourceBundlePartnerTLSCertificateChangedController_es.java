//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundlePartnerTLSCertificateChangedController_es.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.timing;

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
public class ResourceBundlePartnerTLSCertificateChangedController_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"autoimport.tls.check.started", "Se ha activado la importación automática de certificados TLS de socios modificados."},
		{"import.success.event.header", "Importación automática de un certificado TLS"},
		{"import.success.event.body", "El sistema está configurado para comprobar periódicamente si los interlocutores conectados mediante TLS han modificado su certificado TLS. Si este es el caso y el certificado TLS no existe en su gestor de certificados TLS local, se importa automáticamente.\nEl sistema ha encontrado un nuevo certificado para el interlocutor \"{0}\" en la URL \"{1}\" y lo ha importado correctamente a su gestor de certificados TLS con el alias \"{2}\"."},
		{"import.failed", "No se ha podido importar automáticamente el certificado TLS para el interlocutor {0}: {1}"},
		{"autoimport.tls.check.stopped", "Se ha desactivado la importación automática de certificados TLS asociados modificados."},
		{"module.name", "Examen de certificación TLS"},
		{"import.success", "El certificado TLS \"{0}\" para el interlocutor [{1}] se ha importado automáticamente."},
	};
}
