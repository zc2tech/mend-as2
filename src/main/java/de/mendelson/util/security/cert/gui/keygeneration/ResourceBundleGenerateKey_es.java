//$Header: /oftp2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleGenerateKey_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert.gui.keygeneration;

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
public class ResourceBundleGenerateKey_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.size", "Longitud de la llave"},
		{"label.commonname.help", "<HTML><strong>Nombre común</strong><br><br>"
			+"Este es el nombre de su dominio tal y como corresponde a la entrada DNS. Este parámetro es importante para el handshake de una conexión TLS. Es posible (¡pero no recomendable!) introducir aquí una dirección IP. También es posible crear un certificado comodín sustituyendo partes del dominio por *. Sin embargo, esto tampoco se recomienda porque no todos los socios aceptan este tipo de claves.<br>"
			+"Si desea utilizar esta clave como clave TLS y esta entrada hace referencia a un dominio inexistente o no se corresponde con su dominio, la mayoría de los sistemas deberían abortar las conexiones TLS entrantes.</HTML>"},
		{"label.locality.hint", "(Ciudad)"},
		{"label.commonname", "Nombre común"},
		{"title", "Generación de claves"},
		{"label.extension.ski.help", "<HTML><strong>SKI</strong><br><br>"
			+"Hay varias formas de identificar un certificado: mediante el hash del certificado, el emisor, el número de serie y el identificador de clave de sujeto (SKI). El SKI proporciona un identificador único para el solicitante del certificado y se utiliza a menudo cuando se trabaja con la firma digital XML o en el ámbito de la seguridad de los servicios web en general. Por lo tanto, esta extensión con el OID 2.5.29.14 suele ser necesaria para AS4.</HTML>"},
		{"label.mailaddress.help", "<HTML><strong>Dirección de correo</strong><br><br>"
			+"Es la dirección de correo electrónico vinculada a la clave. Técnicamente, este parámetro no tiene ningún interés. Sin embargo, si desea que se autentique la clave, esta dirección de correo electrónico suele utilizarse para la comunicación con la CA. Además, la dirección de correo electrónico también debe estar en el dominio del servidor y corresponder a algo como webmaster@dominio o similar, ya que la mayoría de las CA lo utilizan para comprobar si se está en posesión del dominio asociado.</HTML>"},
		{"label.validity.help", "<HTML><strong>Validez en días</strong><br><br>"
			+"Este valor sólo es interesante para claves autofirmadas. En caso de autenticación, la CA sobrescribirá este valor.</HTML>"},
		{"warning.mail.in.domain", "La dirección de correo electrónico no forma parte del dominio \"{0}\" (por ejemplo, myname@{0}).\nEsto puede ser un problema si la clave se va a autenticar posteriormente."},
		{"label.state", "País"},
		{"button.ignore", "Ignorar las advertencias"},
		{"label.locality", "Ubicación"},
		{"label.subjectalternativenames", "Nombres alternativos de los solicitantes"},
		{"label.mailaddress", "Dirección postal"},
		{"label.namedeccurve.help", "<HTML><strong>Curva</strong><br><br>"
			+"Aquí se selecciona el nombre de la curva EC que se utilizará para la generación de la clave. La longitud de clave deseada suele formar parte del nombre de la curva, por ejemplo la clave de la curva \"BrainpoolP256r1\" tiene una longitud de 256bit. La curva más utilizada a partir de 2022 (aproximadamente el 75% de todos los certificados CE en Internet la utilizan) es la NIST P-256, que puede encontrar aquí con el nombre \"Prime256v1\". A partir de 2022, es la curva estándar de OpenSSL.</HTML>"},
		{"label.namedeccurve", "Curva"},
		{"warning.title", "Posible problema con los parámetros clave"},
		{"warning.nonexisting.domain", "El dominio \"{0}\" no existe."},
		{"label.purpose", "Extensiones clave"},
		{"label.keytype.help", "<HTML><strong>Tipo de clave</strong><br><br>"
			+"Es el algoritmo de creación de la clave. En función del algoritmo, las claves resultantes presentan ventajas e inconvenientes.<br>"
			+"A partir de 2022, recomendaríamos una clave RSA con una longitud de clave de 2048 o 4096 bits.</HTML>"},
		{"label.extension.ski", "Identificador de clave temática (SKI)"},
		{"label.countrycode", "Código del país"},
		{"button.reedit", "Revise"},
		{"label.signature.help", "<HTML><strong>Firma</strong><br><br>"
			+"Es el algoritmo de firma con el que se firma la clave. Es necesario para las pruebas de integridad de la propia clave. Este parámetro no tiene nada que ver con las capacidades de firma de la clave - por ejemplo, también puede crear firmas SHA-2 con una clave firmada SHA-1 o viceversa.<br>"
			+"Recomendamos una clave firmada SHA-2 a partir de 2024.<br><br>"
			+"<strong>Breve descripción general: SHA-1, SHA-2, SHA-3 y RSASSA-PSS</strong><br><br>"
			+"<strong>SHA-1</strong>: Un algoritmo hash antiguo que ahora se considera inseguro.<br>"
			+"<strong>SHA-2</strong>: Una versión más moderna y segura de SHA, que existe en diferentes variantes como SHA-256 y SHA-512.<br>"
			+"<strong>SHA-3</strong>: El algoritmo hash más reciente, que se basa en una estructura diferente a SHA-1 y SHA-2 y es aún más seguro contra ataques.<br>"
			+"<strong>RSASSA-PSS (Probabilistic Signature Scheme)</strong>: se trata de una extensión de RSA. Combina la función hash SHA con el procedimiento de firma PSS, que proporciona seguridad adicional.</HTML>.</HTML>"},
		{"view.expert", "Opinión del experto"},
		{"label.validity", "Validez en días"},
		{"button.cancel", "Demolición"},
		{"label.purpose.ssl", "TLS"},
		{"label.purpose.encsign", "Cifrado y firma digital"},
		{"button.ok", "Ok"},
		{"label.countrycode.hint", "(2 caracteres, ISO 3166)"},
		{"warning.invalid.mail", "La dirección de correo \"{0}\" no es válida."},
		{"label.keytype", "Tipo de llave"},
		{"label.signature", "Firma"},
		{"label.commonname.hint", "(Nombre de dominio del servidor)"},
		{"label.organisationname", "Organización (nombre)"},
		{"view.basic", "Vista estándar"},
		{"label.size.help", "<HTML><strong>Longitud de clave</strong><br><br>"
			+"Es la longitud de la clave. En principio, las operaciones criptográficas con una longitud de clave más larga son más seguras que las operaciones criptográficas con claves de longitud más corta. Sin embargo, la desventaja de las longitudes de clave grandes es que las operaciones criptográficas tardan bastante más, lo que puede ralentizar considerablemente el procesamiento de datos en función de la potencia de cálculo.<br>"
			+"A partir de 2022, recomendaríamos una clave con una longitud de 2048 o 4096 bits.</HTML>"},
		{"label.organisationunit", "Organización (Unidad)"},
	};
}
