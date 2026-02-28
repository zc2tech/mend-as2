//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessagePacker_es.java 4     17/01/25 8:41 Heller $
package de.mendelson.comm.as2.message;

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
public class ResourceBundleAS2MessagePacker_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"mdn.signed", "El MDN saliente se firmó con el algoritmo \"{0}\", el alias de la clave es \"{1}\" de la estación local \"{2}\"."},
		{"message.compressed.unknownratio", "Los datos salientes del usuario se han comprimido."},
		{"message.creation.error", "No se ha podido crear el mensaje con el ID de mensaje \"{0}\": {1}. Se trata de un problema que ya se produjo cuando se creó la estructura de mensajes salientes en su sistema - no tiene nada que ver con el sistema de su interlocutor y no se intentó establecer una conexión con su interlocutor."},
		{"signature.no.aipa", "El proceso de firma no utiliza el atributo Algorithm Identifier Protection en la firma (tal y como se ha establecido en la configuración) - ¡esto es inseguro!"},
		{"mdn.creation.start", "Crear MDN saliente, establecer Id de mensaje en \"{0}\"."},
		{"message.compressed", "Los datos salientes del usuario se comprimieron de {0} a {1}."},
		{"mdn.details", "Detalles del MDN saliente: {0}"},
		{"mdn.created", "MDN saliente creado para el mensaje AS2 \"{0}\", estado establecido en [{1}]."},
		{"mdn.notsigned", "El MDN saliente no estaba firmado."},
		{"message.signed", "El mensaje saliente se firmó digitalmente con el algoritmo \"{1}\", se utilizó la clave con el alias \"{0}\" de la estación local \"{2}\"."},
		{"message.encrypted", "El mensaje saliente se cifró con el algoritmo {1}, se utilizó el certificado con el alias \"{0}\" del interlocutor remoto \"{2}\"."},
		{"message.creation.start", "Crear mensaje AS2 saliente, fijar Id de mensaje en \"{0}\"."},
		{"message.notsigned", "El mensaje saliente no estaba firmado digitalmente."},
		{"message.notencrypted", "El mensaje saliente no estaba cifrado."},
	};
}
