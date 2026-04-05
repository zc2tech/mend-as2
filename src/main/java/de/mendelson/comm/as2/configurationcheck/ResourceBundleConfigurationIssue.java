package de.mendelson.comm.as2.configurationcheck;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @version $Revision: 33 $
 */
public class ResourceBundleConfigurationIssue extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        //preferences localized
        {String.valueOf(ConfigurationIssue.CERTIFICATE_EXPIRED_ENC_SIGN), "Certificate expired (enc/sign)"},
        {"hint." + String.valueOf(ConfigurationIssue.CERTIFICATE_EXPIRED_ENC_SIGN),
            "<HTML>Certificates have only a limited term. This is usually one, three or five years.<br>"
            + "A certificate that you use in your system for a partner to encrypt/decrypt data, digitally sign, or verify a digital signature is no longer valid.<br>"
            + "It is not possible to perform cryptographic operations with an expired certificate - "
            + "so please make sure you renew the certificate or create or authenticate a new certificate.<br><br>"
            + "<strong>Additional information on the certificate:</strong><br><br>"
            + "Alias: {0}<br>"
            + "Issuer: {1}<br>"
            + "Fingerprint (SHA-1): {2}<br>"
            + "Valid from: {3}<br>"
            + "Valid to: {4}<br>"
            + "<br></HTML>"},
        {String.valueOf(ConfigurationIssue.CERTIFICATE_EXPIRED_TLS), "Certificate expired (TLS)"},
        {"hint." + String.valueOf(ConfigurationIssue.CERTIFICATE_EXPIRED_TLS),
            "<HTML>Certificates have only a limited term. This is usually one, three or five years.<br>"
            + "A certificate that you use in your system for the TLS handshake is no longer valid.<br>"
            + "It is not possible to perform cryptographic operations with an expired certificate - "
            + "so please make sure you renew the certificate or create or authenticate a new certificate.<br><br>"
            + "<strong>Additional information on the certificate:</strong><br><br>"
            + "Alias: {0}<br>"
            + "Issuer: {1}<br>"
            + "Fingerprint (SHA-1): {2}<br>"
            + "Valid from: {3}<br>"
            + "Valid to: {4}<br>"
            + "<br></HTML>"},
        {String.valueOf(ConfigurationIssue.MULTIPLE_KEYS_IN_TLS_KEYSTORE), "Multiple keys found in TLS keystore - must be single key"},
        {"hint." + String.valueOf(ConfigurationIssue.MULTIPLE_KEYS_IN_TLS_KEYSTORE),
            "<HTML>There are several keys in the TLS keystore of your system.<br>"
            + "However, only one key may be in it - this is used as the TLS key when the server is started.<br>"
            + "Please delete the key from the TLS keystore until there is only one key left. You can recognize the keys in the certificate manager by the key symbol in the first column. After this change it is necessary to restart the server.</HTML>"},
        {String.valueOf(ConfigurationIssue.NO_KEY_IN_TLS_KEYSTORE), "No key found in TLS keystore"},
        {"hint." + String.valueOf(ConfigurationIssue.NO_KEY_IN_TLS_KEYSTORE),
            "<HTML>No key was found in the TLS keystore of your system.<br>"
            + "You can recognize a key by the key symbol in front of it when you open the certificate manager.<br>"
            + "Exactly one key is required in the TLS keystore to execute the handshake process of the TLS line security.<br>"
            + "Without this key, you will not be able to establish secure connections either inbound or outbound.</HTML>"},
        {String.valueOf(ConfigurationIssue.HUGE_AMOUNT_OF_TRANSACTIONS_NO_AUTO_DELETE), "Setup auto delete process - Huge amount of transactions in the system"},
        {"hint." + String.valueOf(ConfigurationIssue.HUGE_AMOUNT_OF_TRANSACTIONS_NO_AUTO_DELETE),
            "<HTML>In the settings, you can define how long transactions should remain in the system.<br>"
            + "The more transactions remain in the system, the more resources are required for administration.<br>"
            + "You should therefore use the settings to ensure that you never have more than 30000 transactions in the system.<br>"
            + "Please note that this is not an archive system, but a communication adapter. You have access to all past transaction logs via the integrated search function of the server log.</HTML>"},
        {String.valueOf(ConfigurationIssue.FEW_CPU_CORES), "Assign min 4 CPU cores to the server process"},
        {"hint." + String.valueOf(ConfigurationIssue.FEW_CPU_CORES),
            "<HTML>For better throughput it is necessary that different tasks in the system are performed in parallel.<br>"
            + "Therefore it is necessary to reserve a corresponding number of CPU cores for the process.</HTML>"},
        {String.valueOf(ConfigurationIssue.LOW_MAX_HEAP_MEMORY), "Setup min 8GB heap memory for the server process"},
        {"hint." + String.valueOf(ConfigurationIssue.LOW_MAX_HEAP_MEMORY),
            "<HTML>This program is written in Java.<br>"
            + "Regardless of the physical equipment of your computer, you must reserve an appropriate amount of memory for the server process. In your case, you have reserved too little memory.<br>"
            + "Please have a look at the help (section Installation) - there you can see how to reserve the corresponding memory for which start method.<br><br>"
            + "In any case, please ensure that you do not reserve more memory for the server process than your system has main memory. "
            + "Otherwise the software will become almost unusable because the system is constantly swapping memory to the hard disk.</HTML>"},
        {String.valueOf(ConfigurationIssue.NO_OUTBOUND_CONNECTIONS_ALLOWED), "Outbound connections is set to 0 - system will NOT send"},
        {"hint." + String.valueOf(ConfigurationIssue.NO_OUTBOUND_CONNECTIONS_ALLOWED),
            "<HTML>You have made configuration changes so that no outgoing connections are currently possible.<br>"
            + "If you want to establish outgoing connections to partners, the number of possible connections must be set to at least 1.</HTML>"},
        {String.valueOf(ConfigurationIssue.CERTIFICATE_MISSING_ENC_REMOTE_PARTNER), "Missing enc certificate of remote partner"},
        {"hint." + String.valueOf(ConfigurationIssue.CERTIFICATE_MISSING_ENC_REMOTE_PARTNER),
            "<HTML>A connection partner has not assigned an encryption certificate in your configuration.<br>"
            + "In this case, you cannot encrypt any messages for him. Please open the partner manager and assign an encryption certificate to the partner.</HTML>"},
        {String.valueOf(ConfigurationIssue.CERTIFICATE_MISSING_SIGN_REMOTE_PARTNER), "Missing sign certificate of remote partner"},
        {"hint." + String.valueOf(ConfigurationIssue.CERTIFICATE_MISSING_SIGN_REMOTE_PARTNER),
            "<HTML>A connection partner has not assigned a signature verification certificate in your configuration.<br>"
            + "In this case, you cannot verify any digital signature of messages sent from him. Please open the partner manager and assign a signture certificate to the partner.</HTML>"},
        {String.valueOf(ConfigurationIssue.KEY_MISSING_ENC_LOCAL_STATION), "Missing enc key of local station"},
        {"hint." + String.valueOf(ConfigurationIssue.KEY_MISSING_ENC_LOCAL_STATION),
            "<HTML>Your local station hasn''t assigned an encryption key.<br>"
            + "You cannot decrypt incoming messages in this configuration - regardless of which partner.<br>"
            + "Please open the partner management and assign a private key to the local station.</HTML>"},
        {String.valueOf(ConfigurationIssue.KEY_MISSING_SIGN_LOCAL_STATION), "Missing sign key of local station"},
        {"hint." + String.valueOf(ConfigurationIssue.KEY_MISSING_SIGN_LOCAL_STATION),
            "<HTML>Your local station hasn''t assigned a sign key.<br>"
            + "You cannot sign any outbound message messages in this configuration - regardless to which partner.<br>"
            + "Please open the partner management and assign a private key to the local station.</HTML>"},
        {String.valueOf(ConfigurationIssue.USE_OF_TEST_KEYS_IN_TLS), "Use of a public available test key as TLS key"},
        {"hint." + String.valueOf(ConfigurationIssue.USE_OF_TEST_KEYS_IN_TLS),
            "<HTML>mendelson provides test keys in the delivery.<br>"
            + "These are publicly available on the mendelson website.<br>"
            + "If you use these keys productively for cryptographic tasks within your data transfer, they therefore offer NO security.<br>"
            + "Here you can also send unsecured and unencrypted.<br>"
            + "If you need a trusted key, please contact mendelson support.</HTML>"},
        {String.valueOf(ConfigurationIssue.JVM_32_BIT), "Using a 32bit Java VM is not recommended for production use as the max heap memory is limited there to 1.3GB"},
        {"hint." + String.valueOf(ConfigurationIssue.JVM_32_BIT),
            "<HTML>Java 32bit processes cannot reserve enough memory to keep the system stable in productive operation. Please use a 64bit JVM.</HTML>"},
        {String.valueOf(ConfigurationIssue.WINDOWS_SERVICE_LOCAL_SYSTEM_ACCOUNT), "Windows service started using a local system account"},
        {"hint." + String.valueOf(ConfigurationIssue.WINDOWS_SERVICE_LOCAL_SYSTEM_ACCOUNT),
            "<HTML>You have set up the mendelson AS2 server as Windows service and start it from a local system account (\"{0}\").<br>"
            + "Unfortunately it is possible that this user loses the rights to his previously written files after a Windows update, "
            + "this can lead to various system problems.<br><br>"
            + "Please set up a separate user for the service and start the service with this user.</HTML>"},
        {String.valueOf(ConfigurationIssue.TOO_MANY_DIR_POLLS), "Large amount of directory monitoring activities per time interval"},
        {"hint." + String.valueOf(ConfigurationIssue.TOO_MANY_DIR_POLLS),
            "<HTML>You have defined a large number of partner relationships in your system and monitor the corresponding "
            + "outbound directories in too short time intervals.<br>Currently you defined {0} monitoring activities per minute, "
            + "the system cannot maintain this high rate.<br>"
            + "Please decrease this value by increasing the monitoring intervals of the respective partner directories and "
            + "also disable outbound directory monitoring for partners where this is not required.<br><br>"
            + "For a huge amount of partners it is recommended to disable all directory monitoring processes and create the send orders "
            + "from your backend using the commands <i>AS2Send.exe</i> or <i>as2send.sh</i> on demand.</HTML>"},
        {String.valueOf(ConfigurationIssue.CRL_CERTIFICATE_REVOCATION_ENC_SIGN), "Certificate revocation problem (enc/sign)"},
        {"hint." + String.valueOf(ConfigurationIssue.CRL_CERTIFICATE_REVOCATION_ENC_SIGN),
            "<HTML>Trusted certificates contain a link to a revocation list that can be used to declare "
            + "this certificate invalid. For example, if the certificate has been compromised.<br>"
            + "There was a problem checking the revocation list of the following enc/sign certificate or the certificate has "
            + "been revoked:<br><strong>{0}</strong><br><br>"
            + "Additional information on the certificate<br><br>"
            + "Alias: {1}<br>"
            + "Issuer: {2}<br>"
            + "Fingerprint (SHA-1): {3}<br>"
            + "<br>"
            + "<br>Please remember that you could disable the automatically CRL check in the server settings."
            + "</HTML>"},
        {String.valueOf(ConfigurationIssue.CRL_CERTIFICATE_REVOCATION_TLS), "Certificate revocation problem (TLS)"},
        {"hint." + String.valueOf(ConfigurationIssue.CRL_CERTIFICATE_REVOCATION_TLS),
            "<HTML>Trusted certificates contain a link to a revocation list that can be used to declare "
            + "this certificate invalid. For example, if the certificate has been compromised.<br>"
            + "There was a problem checking the revocation list of the following TLS certificate or the certificate has "
            + "been revoked:<br><strong>{0}</strong><br><br>"
            + "Additional information on the certificate<br><br>"
            + "Alias: {1}<br>"
            + "Issuer: {2}<br>"
            + "Fingerprint (SHA-1): {3}<br>"
            + "<br>"
            + "<br>Please remember that you could disable the automatically CRL check in the server settings."
            + "</HTML>"},
        {String.valueOf(ConfigurationIssue.CLIENT_SERVER_IN_ONE_PROCESS), "Client and server run in one process"},
        {"hint." + String.valueOf(ConfigurationIssue.CLIENT_SERVER_IN_ONE_PROCESS),
            "<HTML>You have started the client and server of the product in one process. It is not recommended "
            + "to do this in production. As the resources are statically assigned to the programs, you will "
            + "have fewer resources for server and client operation in this case.<br><br>"
            + "Please start the server process first and then connect with the client separately."
            + "</HTML>"},
        {String.valueOf(ConfigurationIssue.NOT_ENOUGH_HANDLES), "Not enough handles for server process"},
        {"hint." + String.valueOf(ConfigurationIssue.NOT_ENOUGH_HANDLES),
            "<HTML>You can limit the number of open ports and files per user in your operating system.<br>" 
            + "Your current process user may only use {0} handles, which is too few for the process to run in "
            + "server operation. The server process currently uses {1} handles.<br>"
            + "Under Linux, you can view this value with \"ulimit -n\".<br><br>"
            + "Please extend the maximum value of available handles for this process to at least {2}."
            + "</HTML>"},
    };
}
