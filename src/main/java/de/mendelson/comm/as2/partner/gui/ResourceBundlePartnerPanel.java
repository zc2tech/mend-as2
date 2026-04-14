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
 * @version $Revision: 92 $
 */
public class ResourceBundlePartnerPanel extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"title", "Partner configuration"},
        {"label.name", "Name"},
        {"label.name.hint", "Internal partner name"},
        {"label.name.help", "<HTML><strong>Name</strong><br><br>"
            + "This is the internal name of the partner as used in the system. This is no protocol specific "
            + "value but it is used to build up any filename or directory structure that is related to this partner."
            + "</HTML>"},
        {"label.id", "AS2 id"},
        {"label.id.help", "<HTML><strong>AS2 id</strong><br><br>"
            + "The (in your partner network) unique identification used in the AS2 protocol to identify this partner. "
            + "You can chose this freely - just ensure it''s world wide unique."
            + "</HTML>"},
        {"label.id.hint", "Partner identification (AS2 protocol)"},
        {"label.partnercomment", "Comment"},
        {"label.url", "Receipt URL"},
        {"label.url.help", "<HTML><strong>Receipt URL</strong><br><br>"
            + "This is the URL of your partner through which his AS2 system can be reached.<br>"
            + "Please specify this URL in the format <strong>PROTOCOL://HOST:PORT/PATH</strong>, where the "
            + "<strong>PROTOCOL</strong> must be one of \"http\" or \"https\". <strong>HOST</strong> denotes the AS2 server host of your "
            + "partner. <strong>PORT</strong> is the receive port of your partner. <strong>PATH</strong> denotes the "
            + "receipt path for this partner, for example \"/as2/HttpReceiver\".<br><br>"
            + "The whole entry will be marked as invalid if the protocol is not one of \"http\" or \"https\", if the URL is in bad format "
            + "or if the port is not defined in the URL.<br><br>"
            + "Please do not enter a URL here that refers to your own system via \"localhost\" or \"127.0.0.1\" - "
            + "you would be trying to send the outgoing AS2 messages to your own system."
            + "</HTML>"},
        {"label.mdnurl", "MDN URL"},
        {"label.mdnurl.help", "<HTML><strong>MDN</strong> (<strong>M</strong>essage <strong>D</strong>elivery <strong>N</strong>otification) <strong>URL</strong><br><br>"
            + "This is the URL that your partner will use for the incoming asynchronous MDN to this local station. In the synchronous case this value is not "
            + "used because the MDN is sent on the backchannel of the outgoing connection.<br>"
            + "Please specify this URL in the format <strong>PROTOCOL://HOST:PORT/PATH</strong>.<br>"
            + "<strong>PROTOCOL</strong> must be one of \"http\" or \"https\".<br><strong>HOST</strong> "
            + "denotes your own AS2 server host.<br><strong>PORT</strong> is the receive port of your AS2 system. "
            + "<br><strong>PATH</strong> denotes the receive "
            + "path, for example \"/as2/HttpReceiver\".<br><br>"
            + "The whole entry will be marked as invalid if the protocol is not one of \"http\" or \"https\", if the URL is in bad format "
            + "or if the port is not defined in the URL.<br><br>"
            + "Please do not enter a URL here that refers to your own system via \"localhost\" or \"127.0.0.1\" - "
            + "this information will be evaluated on your partner''s side after receiving the AS2 "
            + "message and he would then send the MDN to himself."
            + "</HTML>"},
        {"label.signalias.key", "Private key (Outbound signature generation)"},
        {"label.signalias.key.help", "<HTML><strong>Private key (Outbound signature generation)</strong><br><br>"
            + "Please select here a private key which is available in the certificate manager (signature/encryption) of the system.<br>"
            + "This key is used to create a digital signature for outgoing messages to all remote partners.<br><br>"
            + "Since only you are in possession of the private key set here, only you can perform a signature on the data. Your partners "
            + "can check this signature with the certificate - this ensures that the data is unchanged and that you are the "
            + "sender."
            + "</HTML>"},
        {"label.cryptalias.key", "Private key (Inbound data decryption)"},
        {"label.cryptalias.key.help", "<HTML><strong>Private key (Inbound data decryption)</strong><br><br>"
            + "Please select here a private key which is available in the certificate manager (signature/encryption) of the system.<br>"
            + "If incoming messages from any partner are encrypted for this local station, this key is used for decryption.<br><br>"
            + "Since only you are in possession of the private key set here, only you can decrypt the data that your partners "
            + "have encrypted with your certificate. So any partner can encrypt data for you - but only you can decrypt it."
            + "</HTML>"},
        {"label.signalias.cert", "Partner certificate (Inbound signature verification)"},
        {"label.signalias.cert.help", "<HTML><strong>Partner certificate (Inbound signature verification)</strong><br><br>"
            + "Please select a certificate here that is available in the certificate manager (signature/encryption) of the system.<br>"
            + "If incoming messages from this partner are digitally signed for a local station, this certificate is used to verify this signature."
            + "</HTML>"},
        {"label.cryptalias.cert", "Partner certificate (Outbound data encryption)"},
        {"label.cryptalias.cert.help", "<HTML><strong>Partner certificate (Outbound data encryption)</strong><br><br>"
            + "Please select a certificate here that is available in the certificate manager (signature/encryption) of the system.<br>"
            + "If you want to encrypt outgoing messages to this partner, this certificate is used to encrypt the data."
            + "</HTML>"},
        {"label.signtype", "Digital signature algorithm"},
        {"label.signtype.help", "<HTML><strong>Digital signature algorithm</strong><br><br>"
            + "Here you select the signature algorithm to be used to sign outgoing messages to this partner.<br>"
            + "If you have selected a signature algorithm here, an incoming signed message is also expected from this partner "
            + "- however, the signature algorithm is arbitrary.<br><br>"
            + "The outgoing message to this partner is signed using the private key of the local station "
            + "that is the sender of the transaction."
            + "</HTML>"},
        {"label.encryptiontype", "Message encryption algorithm"},
        {"label.encryptiontype.help", "<HTML><strong>Message encryption algorithm</strong><br><br>"
            + "Here you select the encryption algorithm to be used to encrypt outgoing messages to this partner.<br>"
            + "If you have selected an encryption algorithm here, an encrypted message is also expected inbound from this partner "
            + "- but the encryption algorithm is arbitrary.<br><br>"
            + "For further information regarding the encrytpion algorithm please have a look at the help (section partner) - all "
            + "the algorithms are explained there."
            + "</HTML>"},
        {"label.email", "EMail address"},
        {"label.email.help", "<HTML><strong>EMail address</strong><br><br>"
            + "This value is part of the AS2 protocol description but is in fact currently not used at all."
            + "</HTML>"},
        {"label.email.hint", "Transmitted in the AS2 protocol but not used or validated."},
        {"label.localstation", "Local station"},
        {"label.localstation.help", "<HTML><strong>Local station</strong><br><br>"
            + "A local station represents your own system. You can create any number of local stations in your system.<br>"
            + "You configure local stations and connection partners separately. The overall configuration of the partner "
            + "relationship is then created automatically from the configurations of the local station and the remote partner.<br><br>"
            + "There are two types of partners:<br><br>"
            + "<table border=\"0\">"
            + "<tr>"
            + "<td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/localstation.svg\" height=\"20\" width=\"20\"></td>"
            + "<td>Local stations</td>" 
            + "</tr>"   
            + "<tr>"
            + "<td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/singlepartner.svg\" height=\"20\" width=\"20\"></td>"
            + "<td>Remote partners</td>" 
            + "</tr>"               
            + "</table>"            
            + "</HTML>"}, 
        {"label.compression", "Data compression"},
        {"label.compression.help", "<HTML><strong>Data compression</strong><br><br>"
            + "If this option is enable the outbound messages will be compressed using the ZLIB algorithm.<br><br>"
            + "The advantage of compression is that the message size is usually reduced, which leads to faster transfer. "
            + "In addition, the message structure is changed, which can solve compatibility problems.<br>" 
            + "The disadvantage is that this is an additional processing step that comes at the expense of performance.<br><br>" 
            + "This option requires an AS2 system on the other side that supports at least AS2 1.1."
            + "</HTML>"},
        {"label.usecommandonreceipt", "Receipt"},
        {"label.usecommandonsenderror", "Sent (error)"},
        {"label.usecommandonsendsuccess", "Sent (success)"},
        {"label.keepfilenameonreceipt", "Keep original file name on receipt"},
        {"label.keepfilenameonreceipt.help", "<HTML><strong>Keep original file name on receipt</strong><br><br>"
            + "If this is enabled, the system tries to extract the original file name from incoming AS2 messages and "
            + "save the transferred file under this name so that it can be processed accordingly.<br>"
            + "This option will only work if the sender added the original filename information. "
            + "If you enable this please ensure your partner sends unique file names.<br><br>"
            + "If the extracted filename is not a valid filename it will replaced by a valid filename, a POSTPROCESSING system event warning "
            + "will be thrown and the processing will continue.</HTML>"},
        {"label.address", "Address"},
        {"label.contact", "Contact"},
        {"label.notes.help", "<HTML><strong>Notes</strong><br><br>"
            + "These are some notes regarding this partner, just for your own use."
            + "</HTML>"},
        {"tab.misc", "Misc"},
        {"tab.security", "Security"},
        {"tab.send", "Send"},
        {"tab.mdn", "MDN"},
        {"tab.dirpoll", "Directory polling"},
        {"tab.receipt", "Receipt"},
        {"tab.httpauth", "HTTP authentication"},
        {"tab.inboundauth.basic", "Inbound Auth Basic"},
        {"tab.inboundauth.cert", "Inbound Auth Cert"},
        {"tab.httpheader", "HTTP header"},
        {"tab.notification", "Notification"},
        {"tab.visibility", "Visibility"},
        {"tab.events", "Postprocessing"},
        {"tab.partnersystem", "Info"},
        {"label.subject", "Payload subject"},
        {"label.subject.help", "<HTML><strong>Payload subject</strong><br><br>"
            + "$'{'filename} will be replaced by the send filename.<br>This value will be transferred in the HTTP header, there are restrictions! Please use ISO-8859-1 as character encoding, only printable characters, no special characters. CR, LF and TAB are replaced by \"\\r\", \"\\\n\" and \"\\t\"."
            + "</HTML>"},
        {"label.contenttype", "Payload content type"},
        {"label.contenttype.help", "<HTML><strong>Payload content type</strong><br><br>"
            + "The following content types are safely supported in the AS2 protocol:<br><br>"
            + "application/EDI-X12<br>"
            + "application/EDIFACT<br>"
            + "application/edi-consent<br>"
            + "application/XML<br><br>"
            + "The AS2 RFC states that all MIME content types should be supported in AS2 - "
            + "but this is not a mandatory requirement. Thus, you should not rely on your "
            + "partners system or the underlying SMIME processing of the mendelson AS2 to "
            + "handle content types other than those described."
            + "</HTML>"},
        {"label.syncmdn", "Request sync MDN"},
        {"label.syncmdn.help", "<HTML><strong>Request sync MDN</strong><br><br>"
            + "The partner sends the acknowledgement (MDN) on the "
            + "backchannel of your outbound connection. "
            + "The outbound connection is kept open while the partner decrypts the data and verifies "
            + "the signature - this is the reason this method has higher resource requirements than "
            + "the async MDN processing.</HTML>"},
        {"label.asyncmdn", "Request async MDN"},
        {"label.asyncmdn.help", "<HTML><strong>Async MDN</strong><br><br>"
            + "The partner establishs a new connection to your system to send the acknowledgement for your outbound message (MDN). "
            + "The signature verification and data decryption on your partner side is done after the inbound connection has been closed - the is the reason this method requires less resources thatn the synchronous MDN.</HTML>"},
        {"label.signedmdn", "Request signed MDN"},
        {"label.signedmdn.help", "<HTML><strong>Signed MDN</strong><br><br>"
            + "With this setting you can tell the partner system for outgoing AS2 messages that you want a signed acknowledgement of receipt (MDN).<br>"
            + "Although this sounds reasonable at first, it is unfortunately problematic. Because when the partner''s MDN is received, this ends the transaction. "
            + "If the signature verification of the MDN is then performed and fails, there is no way at all to notify the partner of this problem. "
            + "It is no longer possible to abort the transaction - the transaction has already ended. Thus, verifying the signature of the MDN in automatic mode is pointless. "
            + "The AS2 protocol defines here that the application should solve this logical problem, but this is not possible.<br>"
            + "The mendelson AS2 solution displays a warning in case of a failed MDN signature verification.<br><br>"
            + "There is one more special feature of this setting: If there was a problem in the processing on partner side, the MDN may always be unsigned - independent of this setting."
            + "</HTML>"},
        {"label.enabledirpoll", "Directory poll"},
        {"label.enabledirpoll.help", "<HTML><strong>Enable directory poll</strong><br><br>"
            + "If you enable this option, the system will automatically search the outbound directory for this partner for new files. "
            + "If a new file is found, an AS2 message is generated from it and sent to the partner.<br>Please note that this method of "
            + "directory monitoring can only use general parameters for all message creation. If you want to set special parameters "
            + "for each message individually, please use the send process via the command line.<br>"
            + "In case of cluster operation (HA) you must turn off all directory monitoring, as this process cannot be synchronized."
            + "</HTML>"},
        {"label.polldir", "Poll directory"},
        {"label.pollinterval", "Poll interval"},
        {"label.pollignore", "Poll ignore files"},
        {"label.pollignore.help", "<HTML><strong>Poll ignore files</strong><br><br>"
            + "The directory monitoring will fetch and process a defined number of files from the monitored directory at regular intervals. It must be ensured that at this time the file "
            + "is completely available. If you regularly copy files into the monitored directory, this can lead to time overlaps - that is, that a file is fetched that is not yet completely available. "
            + "Therefore, if you copy files to the watched directory using a non-atomic operation, you should choose a file name extension at the time of the copying process that will be ignored by the watching process. "
            + "After the entire file is available in the monitored "
            + "directory, you can remove the extension with an atomic operation (move, mv, rename) and the full file will be fetched. "
            + "<br>The list of filename extensions is a comma-separated list of extensions, for example \"*.tmp, *.upload\"."
            + "</HTML>"},
        {"label.pollignore.hint", "List of files to be ignored by the directory poll, comma separated. Wildcards are allowed. "},
        {"label.maxpollfiles", "Max files per poll"},
        {"label.httpauth.message", "Authentication for outbound AS messages"},
        {"label.httpauth.none", "None"},
        {"label.httpauth.credentials.message", "Basic authentication"},
        {"label.httpauth.credentials.message.user", "Username"},
        {"label.httpauth.credentials.message.pass", "Password"},
        {"label.httpauth.oauth2.authorizationcode.message", "OAuth2 (Authorization code)"},
        {"label.httpauth.oauth2.clientcredentials.message", "OAuth2 (Client credentials)"},
        {"label.httpauth.certificate.message", "Certificate authentication"},
        {"label.httpauth.asyncmdn", "Authentication for outbound async MDN"},
        {"label.httpauth.credentials.asyncmdn", "Basic authentication"},
        {"label.httpauth.credentials.asyncmdn.user", "Username"},
        {"label.httpauth.credentials.asyncmdn.pass", "Password"},
        {"label.httpauth.oauth2.authorizationcode.asyncmdn", "OAuth2 (Authorization code)"},
        {"label.httpauth.oauth2.clientcredentials.asyncmdn", "OAuth2 (Client credentials)"},
        {"label.httpauth.certificate.asyncmdn", "Certificate authentication"},
        {"label.notify.send", "Notify if send message quota exceeds"},
        {"label.notify.receive", "Notify if receive message quota exceeds"},
        {"label.notify.sendreceive", "Notify if receive and send message quota exceeds"},
        {"header.httpheaderkey", "Name"},
        {"header.httpheadervalue", "Value"},
        {"httpheader.add", "Add"},
        {"httpheader.delete", "Remove"},
        {"label.as2version", "AS2 version"},
        {"label.productname", "Product name"},
        {"label.features", "Features"},
        {"label.features.cem", "Certificate exchange via CEM"},
        {"label.features.ma", "Multiple attachments"},
        {"label.features.compression", "Compression"},
        {"partnerinfo", "Your trading partner transmits with every AS2 message some informations about his AS2 system capabilities. This is a list of features that has been transmitted by your partner."},
        {"partnersystem.noinfo", "No info available - has there been already a transaction?"},
        {"label.httpversion", "HTTP protocol version"},
        {"label.httpversion.help", "<HTML><strong>HTTP protocol version</strong><br><br>"
            + "The following HTTP protocol versions are defined:"
            + "<ul>"
            + "<li>HTTP/1.0 (RFC 1945)</li>"
            + "<li>HTTP/1.1 (RFC 2616)</li>"
            + "<li>HTTP/2.0 (RFC 9113)</li>"
            + "<li>HTTP/3.0 (RFC 9114)</li>"
            + "</ul>"
            + "AS2 does mainly use HTTP 1.1.<br><br>"
            + "Hint: This is <strong>not</strong> the TLS version!"
            + "</HTML>"},
        {"label.test.connection", "Test connection"},
        {"label.mdn.description", "<HTML>The MDN (message delivery notification) is the acknowledgement message for the AS2 message. This section defines the behavior your partner has to follow for your outbound AS2 messages.</HTML>"},
        {"label.algorithmidentifierprotection", "Algorithm Identifier Protection Attribute"},
        {"label.algorithmidentifierprotection.help", "<HTML><strong>Algorithm Identifier Protection Attribute</strong><br><br>"
            + "If you enable this option (which is recommended), the Algorithm Identifier Protection attribute "
            + "is used in the AS2 signature. This attribute is defined in RFC 6211.<br><br>"
            + "The AS2 signature used is vulnerable to algorithm substitution attacks. In an algorithm "
            + "substitution attack, the attacker changes either the algorithm used or the parameters of "
            + "the algorithm in order to change the result of the signature check. This attribute now "
            + "contains a copy of the relevant algorithm identifiers of the signature so that they "
            + "cannot be changed. This prevents an algorithm substitution attack on the signature.<br><br>"
            + "There are AS2 systems that cannot handle this attribute (although the RFC is from 2011) and "
            + "report an authorization error. In this case, the attribute can be switched off here."
            + "</HTML>"},
        {"tooltip.button.editevent", "Edit event"},
        {"tooltip.button.addevent", "Create a new event"},
        {"label.httpauthentication.credentials.help", "<HTML><strong>Basic HTTP authentication</strong><br><br>"
            + "Please setup the HTTP basic access authentication here if this is enabled on your partners side "
            + "(as defined in RFC 7617). To unauthenticated requests (wrong credentials etc), the remote partners "
            + "system should return a <strong>HTTP 401 Unauthorized</strong> status.<br>If the connection to "
            + "your partner requires TLS client authentication (via certificates), there is no setting required here. "
            + "In this case just import the partners certificates via the TLS certificate manager and you are done."
            + "</HTML>"},
        {"label.overwrite.security", "Overwrite security settings of the local station"},
        {"label.keep.security", "Use security settings of the local station"},
        {"label.overwrite.crypt", "Decrypt incoming messages"},
        {"label.overwrite.crypt.help", "<HTML><strong>Decrypt incoming messages</strong><br><br>"
            + "This key is used to decrypt incoming messages from this partner - "
            + "instead of the set key of the respective local station."
            + "</HTML>"},
        {"label.overwrite.sign", "Sign outgoing messages"},
        {"label.overwrite.sign.help", "<HTML><strong>Sign outgoing messages</strong><br><br>"
            + "This key is used to sign outgoing messages to this partner - instead of the set key of the respective local station."
            + "</HTML>"},
        // Inbound Auth tab
        {"label.inboundauth.none", "None"},
        {"label.inboundauth.basic", "Basic Authentication"},
        {"label.inboundauth.basic.enable", "Enable Basic Authentication"},
        {"label.inboundauth.basic.username", "Username:"},
        {"label.inboundauth.basic.password", "Password:"},
        {"label.inboundauth.cert", "Certificate Authentication"},
        {"label.inboundauth.cert.enable", "Enable Certificate Authentication"},
        {"label.inboundauth.cert.select", "Client Certificate:"},
        {"inboundauth.basic.info", "Configure basic authentication credentials (username/password) required for incoming AS2 messages to this local station."},
        {"inboundauth.cert.info", "Configure client certificate authentication required for incoming AS2 messages to this local station."},
        {"inboundauth.info", "Configure authentication required for incoming AS2 messages to this local station."},
        // Inbound Auth table columns
        {"inboundauth.table.col.status", "Status"},
        {"inboundauth.table.col.type", "Type"},
        {"inboundauth.table.col.username", "Username"},
        {"inboundauth.table.col.password", "Password"},
        {"inboundauth.table.col.certalias", "Certificate Name"},
        {"inboundauth.table.col.certificate", "Certificate Fingerprint"},
        {"inboundauth.table.col.enabled", "Enabled"},
        {"inboundauth.button.add", "Add"},
        {"inboundauth.button.delete", "Delete"},
        {"inboundauth.type.basic", "Basic"},
        {"inboundauth.type.certificate", "Certificate"},
        // Certificate selection dialog
        {"inboundauth.cert.select.title", "Select Client Certificate"},
        {"inboundauth.cert.select.message", "Select a certificate to authenticate incoming connections from remote partners:"},
    };
}
