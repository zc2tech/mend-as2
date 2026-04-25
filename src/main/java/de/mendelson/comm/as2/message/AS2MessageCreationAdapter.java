package de.mendelson.comm.as2.message;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.cert.MultiUserCertificateManager;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Wrapper for AS2MessageCreation that bridges between:
 * - MultiUserCertificateManager (new multi-user design)
 * - AS2MessageCreation (existing code that expects single CertificateManager)
 *
 * This adapter creates user-specific CertificateManager views on-demand
 * for message creation, ensuring the correct user's certificates are used.
 */
public class AS2MessageCreationAdapter {

    private final MultiUserCertificateManager multiUserCertManager;
    private final Logger logger;
    private IDBDriverManager dbDriverManager;

    public AS2MessageCreationAdapter(MultiUserCertificateManager multiUserCertManager, Logger logger) {
        this.multiUserCertManager = multiUserCertManager;
        this.logger = logger;
    }

    public void setServerResources(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Create AS2 message for specific user
     *
     * @param sender Sender partner
     * @param receiver Receiver partner
     * @param files Files to send
     * @param originalFilenames Original filenames
     * @param userdefinedId User-defined message ID
     * @param subject Message subject
     * @param payloadContentTypes Content types for payloads
     * @param userId User ID (determines which keystore to use for signing/encryption)
     * @return AS2Message
     * @throws Exception if message creation fails
     */
    public AS2Message createMessage(
            Partner sender,
            Partner receiver,
            Path[] files,
            String[] originalFilenames,
            String userdefinedId,
            String subject,
            String[] payloadContentTypes,
            int userId) throws Exception {

        if (logger != null) {
            logger.info("=================================================");
            logger.info("[AS2-MSG-DEBUG] Creating AS2 message:");
            logger.info("[AS2-MSG-DEBUG]   Sender: " + sender.getName() + " (dbId=" + sender.getDBId() + ")");
            logger.info("[AS2-MSG-DEBUG]   Receiver: " + receiver.getName() + " (dbId=" + receiver.getDBId() + ")");
            logger.info("[AS2-MSG-DEBUG]   User ID: " + userId);
            logger.info("[AS2-MSG-DEBUG]   Sign fingerprint: " + sender.getSignFingerprintSHA1());
            logger.info("[AS2-MSG-DEBUG]   Crypt fingerprint: " + receiver.getCryptFingerprintSHA1());
            logger.info("=================================================");
        }

        // Create user-specific certificate manager view
        UserSpecificCertificateManager userCertManager = new UserSpecificCertificateManager(
            multiUserCertManager,
            userId
        );

        // Create AS2MessageCreation with user-specific certificate manager
        AS2MessageCreation messageCreation = new AS2MessageCreation(
            userCertManager,  // Both sign and encrypt use same user's keystore
            userCertManager
        );

        messageCreation.setLogger(logger);
        if (dbDriverManager != null) {
            messageCreation.setServerResources(dbDriverManager);
        }

        // Create message
        AS2Message message = messageCreation.createMessage(
            sender,
            receiver,
            files,
            originalFilenames,
            userdefinedId,
            subject,
            payloadContentTypes
        );

        if (logger != null) {
            logger.info("[AS2-MSG-DEBUG] ✓ AS2 message created successfully");
            logger.info("[AS2-MSG-DEBUG]   Message ID: " + message.getAS2Info().getMessageId());
            logger.info("=================================================");
        }

        return message;
    }

    /**
     * Adapter that makes MultiUserCertificateManager look like a single-user CertificateManager
     * by binding it to a specific userId context.
     *
     * This allows AS2MessageCreation to use the new multi-user manager without code changes.
     */
    private static class UserSpecificCertificateManager extends de.mendelson.util.security.cert.CertificateManager {

        private final MultiUserCertificateManager multiUserManager;
        private final Integer userId;

        public UserSpecificCertificateManager(MultiUserCertificateManager multiUserManager, int userId) {
            super(null);  // Logger will be set by AS2MessageCreation
            this.multiUserManager = multiUserManager;
            this.userId = userId;
        }

        @Override
        public String getAliasByFingerprint(String fingerprintSHA1) {
           
            String alias = multiUserManager.getAliasByFingerprint(
                fingerprintSHA1,
                userId,
                MultiUserCertificateManager.PURPOSE_ENC_SIGN
            );
            return alias;
        }

        @Override
        public String getAliasByFingerprint(byte[] fingerprintSHA1) {
            return multiUserManager.getAliasByFingerprint(
                fingerprintSHA1,
                userId,
                MultiUserCertificateManager.PURPOSE_ENC_SIGN
            );
        }

        @Override
        public java.security.cert.X509Certificate getX509Certificate(String alias) throws Exception {
            return multiUserManager.getX509Certificate(
                alias,
                userId,
                MultiUserCertificateManager.PURPOSE_ENC_SIGN
            );
        }

        @Override
        public java.security.PrivateKey getPrivateKey(String alias) throws Exception {
            return multiUserManager.getPrivateKey(
                alias,
                userId,
                MultiUserCertificateManager.PURPOSE_ENC_SIGN
            );
        }

        @Override
        public java.security.PrivateKey getPrivateKeyByFingerprintSHA1(String fingerprintSHA1) throws Exception {
           
            // First get the alias
            String alias = getAliasByFingerprint(fingerprintSHA1);

            if (alias == null) {
                throw new Exception("The certificate with the SHA-1 fingerprint \"" + fingerprintSHA1 + "\" does not exist.");
            }

            // Then get the private key
            return getPrivateKey(alias);
        }

        @Override
        public java.security.cert.Certificate[] getCertificateChain(String alias) throws Exception {
            return multiUserManager.getCertificateChain(
                alias,
                userId,
                MultiUserCertificateManager.PURPOSE_ENC_SIGN
            );
        }

        // Delegate other methods to system-wide manager (rare operations)
        // Most operations go through fingerprint lookup above
    }
}
