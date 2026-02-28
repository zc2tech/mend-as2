//$Header: /as2/de/mendelson/comm/as2/partner/gui/ResourceBundlePartnerPanel_es.java 4     9/12/24 16:02 Heller $
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
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ResourceBundlePartnerPanel_es extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"label.httpauth.oauth2.authorizationcode.asyncmdn", "OAuth2 (Código de autorización)"},
        {"tab.httpheader", "Cabecera HTTP"},
        {"label.as2version", "Versión AS2"},
        {"label.overwrite.security", "Sobrescribir la configuración de seguridad de la estación local"},
        {"label.id.help", "<HTML><strong>AS2 id</strong><br><br>"
            + "El identificador único (en su red de socios) que se utiliza en el protocolo AS2 para identificar a este socio. Puede elegirlo libremente, pero asegúrese de que es único en todo el mundo.</HTML>"},
        {"label.httpauth.credentials.message.pass", "contraseña"},
        {"label.contact", "Póngase en contacto con nosotros"},
        {"label.keepfilenameonreceipt.help", "<HTML><strong>Mantener nombre de archivo original</strong><br><br>"
            + "Si está activada, el sistema intenta extraer el nombre original del archivo de los mensajes AS2 entrantes y guardar el archivo transferido con este nombre para poder procesarlo en consecuencia.<br>"
            + "Esta opción sólo funciona si el remitente ha añadido la información original del nombre del archivo. Si activa esta opción, asegúrese de que su socio envía nombres de archivo únicos.<br><br>"
            + "Si el nombre de archivo extraído no es un nombre de archivo válido, se sustituirá por un nombre de archivo válido, se activará una advertencia de evento de sistema POSTPROCESSING y continuará el procesamiento.</HTML>"},
        {"label.overwrite.crypt.help", "<HTML><strong>Desencriptar mensajes entrantes</strong><br><br>"
            + "Esta clave se utiliza para descifrar los mensajes entrantes de este socio - en lugar de la clave establecida de la estación local respectiva.</HTML>"},
        {"label.name.help", "<HTML><strong>Nombre</strong><br><br>"
            + "Es el nombre interno del interlocutor tal y como se utiliza en el sistema. No es un valor específico del protocolo, pero se utiliza para construir nombres de archivo o estructuras de directorio que hagan referencia a este interlocutor.</HTML>.</HTML>"},
        {"label.signedmdn", "Solicitar acuse de recibo firmado (MDN)"},
        {"label.pollinterval", "Intervalo de recogida"},
        {"label.compression.help", "<HTML><strong>Compresión de datos</strong><br><br>"
            + "Si esta opción está activada, los mensajes salientes se comprimen utilizando el algoritmo ZLIB.<br>"
            + "La ventaja de la compresión es que suele reducirse el tamaño del mensaje, lo que permite una transmisión más rápida. También se modifica la estructura del mensaje, lo que puede resolver problemas de compatibilidad.<br>"
            + "La desventaja es que se trata de un paso de procesamiento adicional que va en detrimento del rendimiento.<br><br>"
            + "Esta opción requiere un sistema AS2 en el otro lado que soporte al menos AS2 1.1.</HTML>"},
        {"label.keepfilenameonreceipt", "Conservar el nombre original del archivo"},
        {"label.pollignore.help", "<HTML><strong>Ignorar recogida para</strong><br><br>"
            + "La supervisión de directorios recuperará y procesará un número definido de archivos del directorio supervisado a intervalos regulares.<br>"
            + "Hay que asegurarse de que el fichero está completamente disponible en ese momento. Si copia regularmente archivos en el directorio supervisado, pueden producirse solapamientos temporales de modo que se recupere un archivo que aún no está completamente disponible.<br>"
            + "Por lo tanto, si copia los archivos al directorio supervisado utilizando una operación no atómica, deberá seleccionar una extensión de nombre de archivo en el momento del proceso de copia que sea ignorada por el proceso de supervisión.<br>"
            + "Una vez que el archivo completo esté disponible en el directorio supervisado, puede eliminar la extensión del nombre del archivo con una operación atómica (mover, mv, renombrar) y se recuperará el archivo completo.<br>"
            + "La lista de extensiones de nombres de archivo es una lista de extensiones separadas por comas, por ejemplo \"*.tmp, *.upload\".</HTML>.</HTML>"},
        {"label.maxpollfiles", "Número máximo de archivos/proceso de recogida"},
        {"label.httpauth.credentials.asyncmdn.user", "Nombre de usuario"},
        {"label.notify.send", "Notificar si la cuota de transmisión supera el siguiente valor:"},
        {"label.httpversion", "Versión del protocolo HTTP"},
        {"label.localstation.help", "<HTML><strong>Emisora local</strong><br><br>"
            + "Una estación local representa su propio sistema. Puedes crear cualquier número de estaciones locales en tu sistema.<br>"
            + "Las estaciones locales y los interlocutores de conexión se configuran por separado. A continuación, la configuración global de la relación de interlocutor se crea automáticamente a partir de las configuraciones de la estación local y del interlocutor remoto.<br><br>"
            + "Hay dos tipos de socios:<br><br>"
            + "<table border=\"0\"><tr><td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/localstation.svg\" height=\"20\" width=\"20\"></td><td>Emisoras locales</td></tr><tr><td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/singlepartner.svg\" height=\"20\" width=\"20\"></td><td>Socio eliminado</td></tr></table></HTML>"},
        {"label.httpauthentication.credentials.help", "<HTML><strong>Autenticación de acceso básico HTTP</strong><br><br>"
            + "Configure aquí la autenticación básica de acceso HTTP si está activada en el sistema de su socio (definida en RFC 7617). El sistema del socio remoto debe devolver un estado <strong>HTTP 401 No autorizado</strong> para las solicitudes no autenticadas (datos de inicio de sesión incorrectos, etc.).<br>"
            + "Si la conexión con su interlocutor requiere autenticación de cliente TLS (mediante certificados), no es necesario realizar ningún ajuste aquí.<br>"
            + "En este caso, importe los certificados del interlocutor a través del gestor de certificados TLS.<br>"
            + "A continuación, el sistema se encarga de la autenticación del cliente TLS.</HTML>"},
        {"label.subject", "Datos del usuario Asunto"},
        {"label.cryptalias.key", "Clave privada (descifrado de datos)"},
        {"label.url.help", "<HTML><strong>Red de recepción</strong><br><br>"
            + "Es la URL de su socio a través de la cual se puede acceder a su sistema AS2.<br>"
            + "Introduzca esta URL en el formato <strong>PROTOCOL://HOST:PORT/PFAD</strong>, en el que <strong>PROTOCOL</strong> debe ser \"http\" o \"https\". <strong>HOST</strong> indica el host del servidor AS2 de su socio. <strong>PORT</strong> es el puerto de recepción de su interlocutor. <strong>PFAD</strong> denota la ruta de recepción, por ejemplo \"/as2/HttpReceiver\" La entrada completa se marca como no válida si el protocolo no es uno de \"http\" o \"https\", si la URL tiene un formato incorrecto o si el puerto no está definido en la URL.<br><br>"
            + "Por favor, no introduzca aquí una URL que haga referencia a su propio sistema a través de \"localhost\" o \"127.0.0.1\" - estaría intentando enviar los mensajes AS2 salientes a su propio sistema.</HTML>"},
        {"label.keep.security", "Utilizar la configuración de seguridad de la estación local"},
        {"label.cryptalias.cert.help", "<HTML><strong>Certificado de socio (cifrado de datos)</strong<br><br>"
            + "Seleccione aquí un certificado que esté disponible en el gestor de certificados del sistema (firma/cifrado).<br>"
            + "Si desea cifrar los mensajes salientes a este interlocutor, este certificado se utiliza para cifrar los datos.</HTML>"},
        {"label.httpauth.oauth2.clientcredentials.asyncmdn", "OAuth2 (credenciales del cliente)"},
        {"tab.receipt", "Recepción"},
        {"label.contenttype", "Datos del usuario Tipo de contenido"},
        {"label.httpauth.credentials.message.user", "Nombre de usuario"},
        {"label.enabledirpoll", "Control de directorios"},
        {"label.cryptalias.cert", "Certificado de socio (cifrado de datos)"},
        {"label.algorithmidentifierprotection", "Identificador del algoritmo Atributo de protección"},
        {"label.httpauth.oauth2.authorizationcode.message", "OAuth2 (Código de autorización)"},
        {"partnerinfo", "Con cada mensaje AS2, su interlocutor también envía información sobre las funciones de su sistema AS2. Esta es la lista de estas funciones."},
        {"label.asyncmdn", "Solicitar acuse de recibo asíncrono (MDN)"},
        {"label.mdnurl", "URL MDN"},
        {"tab.security", "Seguridad"},
        {"label.partnercomment", "Comentario"},
        {"tab.events", "Tratamiento posterior"},
        {"label.httpauth.credentials.asyncmdn.pass", "contraseña"},
        {"label.id", "AS2 id"},
        {"label.contenttype.help", "<HTML><strong>Tipo de contenido de datos de usuario</strong><br><br>"
            + "El protocolo AS2 admite con seguridad los siguientes tipos de contenido:<br>"
            + "application/EDI-X12<br>"
            + "application/EDIFACT<br>"
            + "application/edi-consent<br>"
            + "application/XML<br><br>"
            + "La RFC AS2 establece que todos los tipos de contenido MIME deben ser soportados en AS2.<br>"
            + "Sin embargo, no es un requisito obligatorio.<br>"
            + "Por lo tanto, no debe fiarse de ello,<br>"
            + "que el sistema de su interlocutor o el procesamiento SMIME subyacente del mendelson AS2 puedan manejar tipos de contenido distintos de los descritos.</HTML>"},
        {"tooltip.button.addevent", "Crear un nuevo evento"},
        {"label.test.connection", "Comprobar conexión"},
        {"label.signtype", "Firma digital"},
        {"label.httpauth.credentials.asyncmdn", "Autenticación HTTP básica"},
        {"label.httpauth.message", "Autenticación de mensajes AS2 salientes"},
        {"label.notify.sendreceive", "Notificar si la cuota de envío/recepción supera el siguiente valor:"},
        {"label.signtype.help", "<HTML><strong>Firma digital</strong><br><br>"
            + "Aquí se selecciona el algoritmo de firma con el que se firmarán los mensajes salientes a este interlocutor.<br>"
            + "Si ha seleccionado un algoritmo de firma aquí, también se espera un mensaje firmado entrante de este interlocutor - sin embargo, el algoritmo de firma es arbitrario.<br><br>"
            + "El mensaje saliente a este interlocutor se firma utilizando la clave privada de la estación local remitente de la transacción.</HTML>"},
        {"label.httpauth.oauth2.clientcredentials.message", "OAuth2 (credenciales del cliente)"},
        {"tab.send", "Envío"},
        {"tooltip.button.editevent", "Editar evento"},
        {"tab.mdn", "MDN"},
        {"label.httpauth.asyncmdn", "Autenticación de MDN asíncronos salientes"},
        {"tab.notification", "Notificación"},
        {"label.mdnurl.help", "<HTML><strong>MDN</strong> (<strong>Mensaje <strong>D</strong>entrega <strong>N</strong>notificación) <strong>URL</strong><br><br>"
            + "Esta es la URL que su interlocutor utilizará para el MDN asíncrono entrante a esta estación local. En el caso síncrono, este valor no se utiliza, ya que el MDN se envía entonces por el canal de retorno de la conexión saliente.<br>"
            + "Por favor, introduzca esta URL en el formato <strong>PROTOCOL://HOST:PORT/PFAD</strong>.<br>"
            + "<strong>PROTOCOL</strong> debe ser uno de \"http\" o \"https\".<br>"
            + "<strong>HOST</strong> se refiere a su propio host del servidor AS2.<br>"
            + "<strong>PORT</strong> es el puerto de recepción de su sistema AS2.<strong>PFAD</strong> denota la ruta de recepción, por ejemplo \"/as2/HttpReceiver\".<strong>La entrada completa se marca como no válida si el protocolo no es uno de \"http\" o \"https\", si la URL tiene un formato incorrecto o si el puerto no está definido en la URL.<br><br>"
            + "Por favor, no introduzca aquí una URL que haga referencia a su propio sistema a través de \"localhost\" o \"127.0.0.1\" - esta información será evaluada en el lado de su interlocutor después de recibir el mensaje AS2 y entonces él se enviaría el MDN a sí mismo.</HTML>"},
        {"label.features", "Funciones"},
        {"label.httpversion.help", "<HTML><strong>Versión del protocolo HTTP</strong><br><br>"
            + "Existen versiones del protocolo HTTP<ul><li>HTTP/1.0 (RFC 1945)</li><li>HTTP/1.1 (RFC 2616)</li><li>HTTP/2.0 (RFC 9113)</li><li>HTTP/3.0 (RFC 9114)</li></ul>HTTP/1.1 se utiliza generalmente para AS2.<br><br>"
            + "Nota: ¡Ésta no es la versión TLS!</HTML>"},
        {"label.signalias.key", "Clave privada (crear firma digital)"},
        {"label.encryptiontype.help", "<HTML><strong>Encriptación de mensajes</strong><br><br>"
            + "Aquí se selecciona el algoritmo de encriptación con el que deben encriptarse los mensajes salientes a este interlocutor.<br>"
            + "Si ha seleccionado un algoritmo de encriptación aquí, también se espera un mensaje encriptado de este interlocutor - sin embargo, el algoritmo de encriptación es arbitrario.<br><br>"
            + "Puede encontrar más información sobre el algoritmo de encriptación en la Ayuda (sección Socio): allí se explican todos los algoritmos.</HTML>"},
        {"label.compression", "Compresión de datos"},
        {"label.overwrite.crypt", "Descifrar los mensajes entrantes"},
        {"label.email", "Dirección postal"},
        {"header.httpheadervalue", "Valor"},
        {"httpheader.add", "Añadir"},
        {"tab.misc", "General"},
        {"partnersystem.noinfo", "No hay información disponible - ¿ha habido ya una transacción?"},
        {"label.httpauth.credentials.message", "Autenticación HTTP básica"},
        {"label.usecommandonreceipt", "Recepción"},
        {"label.features.cem", "Intercambio de certificados a través de CEM"},
        {"label.usecommandonsendsuccess", "Envío (con éxito)"},
        {"label.signalias.cert.help", "<HTML><strong>Certificado de socio (verificar firma digital)</strong<br><br>"
            + "Seleccione aquí un certificado que esté disponible en el gestor de certificados del sistema (firma/cifrado).<br>"
            + "Si los mensajes entrantes de este interlocutor están firmados digitalmente para una estación local, este certificado se utiliza para verificar esta firma.</HTML>"},
        {"label.mdn.description", "<HTML>El MDN (Message Delivery Notification) es la confirmación para el mensaje AS2. Esta sección define el comportamiento de su interlocutor para sus mensajes AS2 salientes.</HTML</HTML>"},
        {"label.encryptiontype", "Cifrado de mensajes"},
        {"label.cryptalias.key.help", "<HTML><strong>Clave privada (desencriptación de datos)</strong<br><br>"
            + "Seleccione aquí una clave privada disponible en el gestor de certificados del sistema (firma/cifrado).<br>"
            + "Si los mensajes entrantes de cualquier interlocutor están cifrados para esta estación local, esta clave se utiliza para el descifrado.<br><br>"
            + "Como sólo usted está en posesión de la clave privada establecida aquí, sólo usted puede descifrar los datos que sus socios han cifrado con su certificado.<br>"
            + "Esto significa que cualquier socio puede cifrar datos para usted, pero sólo usted puede descifrarlos.</HTML>"},
        {"label.name", "Nombre"},
        {"label.signalias.cert", "Certificado de socio (verificar firma digital)"},
        {"label.email.help", "<HTML><strong>Dirección de correo</strong><br><br>"
            + "Este valor forma parte de la descripción del protocolo AS2, pero actualmente no se utiliza en absoluto.</HTML>"},
        {"label.httpauth.none", "Ninguno"},
        {"label.id.hint", "Identificación del socio (protocolo AS2)"},
        {"label.url", "Recibir URL"},
        {"label.subject.help", "<HTML><strong>Datos del usuario objeto</strong><br><br>"
            + "$'{'nombrearchivo} se sustituye por el nombre del archivo de envío.<br>"
            + "Este valor se transmite en la cabecera HTTP, ¡se aplican restricciones!<br>"
            + "Por favor, utilice ISO-8859-1 como codificación de caracteres, sólo caracteres imprimibles, sin caracteres especiales.<br>"
            + "CR, LF y TAB se sustituyen por \"\r\", \"\n\" y \"\t\".</HTML>"},
        {"label.pollignore", "Ignorar la recogida para"},
        {"label.productname", "Nombre del producto"},
        {"label.usecommandonsenderror", "Envío (defectuoso)"},
        {"tab.dirpoll", "Control de directorios"},
        {"label.name.hint", "Nombre del socio interno"},
        {"label.overwrite.sign.help", "<HTML><strong>Firmar mensajes salientes</strong><br><br>"
            + "Esta clave se utiliza para firmar los mensajes salientes a este interlocutor - en lugar de la clave establecida de la estación local respectiva.</HTML>"},
        {"title", "Configuración de socios"},
        {"label.polldir", "Directorio supervisado"},
        {"tab.httpauth", "Autenticación HTTP"},
        {"label.algorithmidentifierprotection.help", "<HTML><strong>Atributo de protección del identificador del algoritmo</strong><br><br>"
            + "Si activa esta opción (lo cual es recomendable), se utilizará el atributo Algorithm Identifier Protection en la firma AS2. Este atributo está definido en RFC 6211.<br><br>"
            + "La firma AS2 utilizada es susceptible de sufrir ataques de sustitución de algoritmos.<br>"
            + "En un ataque de sustitución de algoritmo, el atacante cambia el algoritmo utilizado o los parámetros del algoritmo para modificar el resultado de un procedimiento de verificación de firma.<br>"
            + "Este atributo contiene ahora una copia de los identificadores de algoritmo relevantes de la firma para que no puedan cambiarse, evitando así un ataque de sustitución de algoritmo en la firma.<br><br>"
            + "Hay sistemas AS2 que no pueden manejar este atributo (aunque la RFC es de 2011) e informan de un error de autorización.<br>"
            + "En este caso, el atributo puede desactivarse aquí.</HTML>"},
        {"label.pollignore.hint", "Lista de extensiones de archivo que deben ignorarse, separadas por comas (se permiten comodines)."},
        {"label.signalias.key.help", "<HTML><strong>Clave privada (crear firma digital)</strong<br><br>"
            + "Seleccione aquí una clave privada disponible en el gestor de certificados del sistema (firma/cifrado).<br>"
            + "Esta clave se utiliza para crear una firma digital para los mensajes salientes a todos los interlocutores remotos.<br><br>"
            + "Como sólo tú estás en posesión de la clave privada establecida aquí, sólo tú puedes firmar los datos.<br>"
            + "Sus socios pueden comprobar esta firma con el certificado - esto garantiza que los datos no se han modificado y que usted es el remitente.</HTML>"},
        {"label.notes.help", "<HTML><strong>Notas</strong><br><br>"
            + "Aquí encontrará la posibilidad de tomar notas sobre este socio para su propio uso.</HTML>"},
        {"label.syncmdn", "Solicitar acuse de recibo síncrono (MDN)"},
        {"label.signedmdn.help", "<HTML><strong>Firma de acuse de recibo</strong><br><br>"
            + "Con este ajuste puede informar al sistema asociado para los mensajes AS2 salientes de que desea un acuse de recibo firmado (MDN).<br>"
            + "Aunque esto suena sensato en un principio, la configuración es lamentablemente problemática.<br>"
            + "Esto se debe a que una vez que se ha recibido el MDN del socio, la transacción se ha completado.<br>"
            + "Si a continuación se realiza la verificación de la firma del MDN y falla, ya no hay forma de informar al interlocutor de este problema.<br>"
            + "Ya no es posible anular una transacción: la transacción ya se ha completado. Esto significa que verificar la firma del MDN en modo automático no tiene sentido.<br>"
            + "El protocolo AS2 estipula que la aplicación debe resolver este problema lógico, pero esto no es posible.<br>"
            + "La solución mendelson AS2 muestra una advertencia en caso de que falle la comprobación de la firma MDN.<br><br>"
            + "Hay otra característica especial de este entorno:<br>"
            + "Si hubo un problema durante el procesamiento en el lado del socio, el MDN siempre puede ser sin firmar - independientemente de esta configuración.</HTML>"},
        {"label.notify.receive", "Notificar si la cuota de recepción supera el siguiente valor:"},
        {"label.features.ma", "Múltiples anexos"},
        {"label.email.hint", "No utilizado ni validado en el protocolo AS2"},
        {"label.localstation", "Estación local"},
        {"httpheader.delete", "Eliminar"},
        {"tab.partnersystem", "Información"},
        {"label.features.compression", "Compresión de datos"},
        {"label.asyncmdn.help", "<HTML><strong>Acuse de recibo asíncrono</strong><br><br>"
            + "El interlocutor establece una nueva conexión con su sistema para enviar la confirmación del mensaje saliente.<br>"
            + "La firma se verifica y los datos se descifran en el lado del interlocutor una vez cerrada la conexión entrante.<br>"
            + "Por esta razón, este método requiere menos recursos que el método con MDN síncrono.</HTML>"},
        {"label.address", "Dirección"},
        {"label.enabledirpoll.help", "<HTML><strong>Monitorización de directorios</strong><br><br>"
            + "Si activa esta opción, el sistema buscará automáticamente en el directorio de origen nuevos archivos para este interlocutor.<br>"
            + "Si se encuentra un nuevo archivo, se genera un mensaje AS2 y se envía al interlocutor.<br>"
            + "Tenga en cuenta que este método de supervisión de directorios sólo puede utilizar parámetros generales para todas las creaciones de mensajes.<br>"
            + "Si desea establecer parámetros especiales para cada mensaje individualmente, utilice el proceso de envío a través de la línea de comandos.<br>"
            + "En caso de funcionamiento en clúster (HA), debe desactivar toda la supervisión de directorios, ya que este proceso no se puede sincronizar.</HTML>"},
        {"header.httpheaderkey", "Nombre"},
        {"label.overwrite.sign", "Firmar los mensajes salientes"},
        {"label.syncmdn.help", "<HTML><strong>Acuse de recibo síncrono</strong><br><br>"
            + "El interlocutor envía el acuse de recibo (MDN) en el canal de retorno de su conexión saliente.<br>"
            + "La conexión saliente se mantiene abierta mientras el interlocutor descifra los datos y comprueba la firma.<br>"
            + "Por este motivo, este método requiere más recursos que el procesamiento MDN asíncrono.</HTML>"},};
}
