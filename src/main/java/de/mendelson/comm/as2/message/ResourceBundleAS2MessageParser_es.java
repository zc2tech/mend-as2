//$Header: /mec_as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessageParser_es.java 4     21/03/25 9:12 Heller $
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
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ResourceBundleAS2MessageParser_es extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"inbound.connection.syncmdn", "Se ha recibido MDN síncrono en el canal de retorno de su conexión saliente."},
        {"original.filename.found", "El nombre original del archivo fue transmitido por el remitente como \"{0}\"."},
        {"msg.incoming.identproblem", "La transmisión entrante es un mensaje AS2. No se ha procesado porque había un problema con la identificación del interlocutor."},
        {"mdn.incoming.relationship", "La transmisión entrante es un acuse de recibo (MDN) [{0}]"},
        {"msg.already.processed", "Ya se ha procesado un mensaje con el número [{0}]."},
        {"message.signature.using.alias", "Utilice el certificado \"{0}\" del interlocutor remoto \"{1}\" para verificar la firma digital del mensaje AS2 entrante."},
        {"found.attachments", "Se han encontrado archivos adjuntos con datos de usuario {0} en el mensaje AS2."},
        {"contentmic.match", "El código de integridad del mensaje (MIC) coincide con el mensaje AS2 enviado."},
        {"mdn.details", "Detalles del acuse de recibo (MDN) recibido de {0}: \"{1}\""},
        {"msg.incoming.ha", "La transmisión entrante es un mensaje AS2 [{0}], tamaño de datos brutos: {1}, procesado por [{2}]."},
        {"mdn.signed.error", "El acuse de recibo entrante (MDN) está firmado digitalmente en contra de la configuración de interlocutor \"{0}\"."},
        {"mdn.notsigned", "El acuse de recibo (MDN) no está firmado digitalmente."},
        {"mdn.signature.using.alias", "Utilice el certificado \"{0}\" del interlocutor remoto \"{1}\" para verificar la firma digital del MDN entrante."},
        {"signature.analyzed.digest", "El remitente utilizó el algoritmo \"{0}\" para la firma digital."},
        {"mdn.signature.ok", "Se ha verificado correctamente la firma digital del MDN recibido."},
        {"mdn.signature.failure", "Fallo en la verificación de la firma digital del MDN recibido - {0}"},
        {"data.compressed.expanded", "Los datos de usuario comprimidos del mensaje AS2 entrante se expandieron de {0} a {1}."},
        {"mdn.state", "El estado del acuse de recibo recibido (MDN) es [{0}]."},
        {"msg.signed", "El mensaje AS2 entrante está firmado digitalmente."},
        {"inbound.connection.tls", "Conexión TLS entrante desde [{0}] al puerto {1} [{2}, {3}]"},
        {"mdn.answerto", "El acuse de recibo entrante (MDN) con el número de mensaje \"{0}\" es la respuesta al mensaje AS2 saliente \"{1}\"."},
        {"msg.notsigned", "El mensaje AS2 entrante no está firmado digitalmente."},
        {"decryption.inforequired", "Para descifrar el mensaje AS2 entrante se necesita una clave con los siguientes parámetros:\n{0}"},
        {"invalid.original.filename.log", "Se ha detectado un nombre de archivo original no válido en la transacción. \"{0}\" se sustituye por \"{1}\" y el procesamiento continúa."},
        {"filename.extraction.error", "No es posible extraer el nombre de archivo original del mensaje AS2 entrante: se ignora \"{0}\"."},
        {"mdn.incoming.ha", "La transmisión entrante es un acuse de recibo (MDN), procesado por [{0}]."},
        {"mdn.incoming", "La transmisión recibida es un acuse de recibo (MDN)."},
        {"mdn.incoming.relationship.ha", "La transmisión entrante es un acuse de recibo (MDN) [{0}], procesado por [{1}]."},
        {"mdn.signed", "El acuse de recibo (MDN) está firmado digitalmente ({0})."},
        {"signature.analyzed.digest.failed", "El sistema no pudo averiguar el algoritmo de firma del mensaje AS2 entrante."},
        {"msg.encrypted", "El mensaje AS2 entrante está encriptado."},
        {"contentmic.failure", "El código de integridad del mensaje (MIC) no coincide con el mensaje AS2 enviado (esperado: {0}, recibido: {1})."},
        {"data.unable.to.process.content.transfer.encoding", "Se han recibido datos que no se han podido procesar porque contienen errores. La codificación de transferencia de contenido \"{0}\" es desconocida."},
        {"original.filename.undefined", "No se ha transferido el nombre original del archivo."},
        {"decryption.infoassigned", "Se utilizó una clave con los siguientes parámetros (alias \"{0}\") para desencriptar el mensaje AS2 entrante:\n{1}"},
        {"data.not.compressed", "Los datos AS2 recibidos no están comprimidos."},
        {"inbound.connection.raw", "Conexión entrante desde [{0}] al puerto {1}"},
        {"mdn.unsigned.error", "El acuse de recibo entrante (MDN) NO está firmado digitalmente, contrariamente a la configuración de interlocutor \"{0}\"."},
        {"msg.incoming", "La transmisión entrante es un mensaje AS2 [{0}], tamaño de datos brutos: {1}"},
        {"found.cem", "El mensaje recibido es una solicitud de intercambio de certificados (CEM)."},
        {"msg.notencrypted", "El mensaje AS2 entrante no está cifrado."},
        {"message.signature.ok", "La firma digital del mensaje AS2 entrante se ha verificado correctamente."},
        {"mdn.unexpected.messageid", "El acuse de recibo recibido (MDN) hace referencia al mensaje AS2 del número de referencia \"{0}\", que no espera un MDN."},
        {"message.signature.failure", "Fallo en la verificación de la firma digital del mensaje AS2 entrante - {0}"},
        {"decryption.done.alias", "Los datos del mensaje AS2 entrante se descifraron utilizando la clave \"{0}\" de la estación local \"{3}\", el algoritmo de cifrado fue \"{1}\", el algoritmo de cifrado de la clave fue \"{2}\"."},
        {"invalid.original.filename.title", "Se ha detectado un nombre de archivo original no válido en la transacción"},
        {"invalid.original.filename.body", "El sistema extrajo un nombre de archivo original no válido en la transacción {0} de {1} a {2}.\nEl nombre de archivo \"{3}\" encontrado se sustituyó por \"{4}\" y el procesamiento continuó con este nuevo nombre de archivo. Esto puede tener un impacto en su flujo de procesamiento, ya que los nombres de archivo a veces contienen metadatos."},};
}
