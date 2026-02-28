//$Header: /as2/de/mendelson/comm/as2/cem/ResourceBundleCEM_es.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.cem;

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
public class ResourceBundleCEM_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"TLS.cert.already.imported", "El certificado CEM transmitido ya existe en el sistema [TLS] (alias {0}), se ha omitido la importación."},
		{"cemtype.response", "El mensaje CEM es del tipo \"Respuesta de certificado"},
		{"category.3", "SSL"},
		{"cemtype.request", "El mensaje CEM es del tipo \"Solicitud de certificado"},
		{"category.2", "Firma"},
		{"state.999", "Errores de procesamiento"},
		{"category.1", "Cifrado"},
		{"cem.response.relatedrequest.found", "El mensaje CEM se refiere a la solicitud \"{0}\""},
		{"ENC_SIGN.cert.imported.success", "El certificado CEM enviado se ha importado correctamente en el sistema [enc/sign] (alias {0})."},
		{"cem.response.prepared", "Mensaje de respuesta CEM creado para la solicitud {0}"},
		{"cem.structure.info", "Número de solicitudes de confianza en la estructura CEM recibidas: {0}"},
		{"transmitted.certificate.info", "El certificado transmitido tiene los parámetros IssuerDN=\"{0}\" y número de serie \"{1}\"."},
		{"cem.validated.schema", "El mensaje CEM entrante se ha validado correctamente."},
		{"state.99", "Proceso anulado"},
		{"TLS.cert.imported.success", "El certificado CEM transmitido se ha importado correctamente en el sistema [TLS] (alias {0})."},
		{"trustrequest.certificates.found", "Número de certificados transferidos: {0}."},
		{"cem.created.request", "Se generó la solicitud CEM para la relación \"{0}\"-\"{1}\". Se incrustó el certificado con los parámetros issuerDN \"{2}\" y número de serie \"{3}\". El uso definido es {4}."},
		{"trustrequest.working.on", "Procese la solicitud de confianza {0}."},
		{"ENC_SIGN.cert.already.imported", "El certificado CEM transmitido ya existe en el sistema [enc/sign] (alias {0}), se ha omitido la importación."},
		{"trustrequest.rejected", "La respuesta a la solicitud de confianza recibida tenía el estado \"Rechazada\"."},
		{"trustrequest.accepted", "La respuesta a la solicitud de confianza recibida tiene el estado \"Aceptada\"."},
		{"state.3", "Aceptado por {0}"},
		{"state.2", "Rechazado por {0}"},
		{"state.1", "Aún no hay respuesta de {0}"},
	};
}
