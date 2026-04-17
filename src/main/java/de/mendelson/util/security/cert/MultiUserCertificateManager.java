package de.mendelson.util.security.cert;

import de.mendelson.util.database.IDBDriverManager;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Multi-user certificate manager that provides unified access to both:
 * - System-wide certificates (user_id=-1)
 * - User-specific certificates (user_id=0,1,2...)
 *
 * This manager maintains a cache of CertificateManager instances per user
 * and provides transparent fingerprint-based lookup across all keystores.
 *
 * Thread-safe for concurrent access.
 */
public class MultiUserCertificateManager {

    private final Logger logger;
    private final IDBDriverManager dbDriverManager;

    // System-wide certificate managers (user_id=-1)
    private final CertificateManager systemEncSignManager;
    private final CertificateManager systemTLSManager;

    // Cache of user-specific certificate managers: userId -> (purpose -> CertificateManager)
    // purpose: 1=TLS, 2=ENC_SIGN
    private final Map<Integer, Map<Integer, CertificateManager>> userManagerCache;

    public static final int PURPOSE_TLS = 1;
    public static final int PURPOSE_ENC_SIGN = 2;

    /**
     * Create multi-user certificate manager with system-wide managers
     *
     * @param logger Logger instance
     * @param systemEncSignManager System-wide ENC_SIGN certificate manager (user_id=-1)
     * @param systemTLSManager System-wide TLS certificate manager (user_id=-1)
     * @param dbDriverManager Database driver manager for loading user-specific keystores
     */
    public MultiUserCertificateManager(
            Logger logger,
            CertificateManager systemEncSignManager,
            CertificateManager systemTLSManager,
            IDBDriverManager dbDriverManager) {
        this.logger = logger;
        this.systemEncSignManager = systemEncSignManager;
        this.systemTLSManager = systemTLSManager;
        this.dbDriverManager = dbDriverManager;
        this.userManagerCache = new ConcurrentHashMap<>();
    }

    /**
     * Get certificate alias by fingerprint, searching in order:
     * 1. User-specific keystore (if userId provided)
     * 2. System-wide keystore
     *
     * @param fingerprintSHA1 Certificate fingerprint
     * @param userId User ID (null for system-wide only)
     * @param purpose PURPOSE_TLS or PURPOSE_ENC_SIGN
     * @return Certificate alias, or null if not found
     */
    public String getAliasByFingerprint(String fingerprintSHA1, Integer userId, int purpose) {
        if (fingerprintSHA1 == null) {
            if (logger != null) {
                logger.warning("[CERT-DEBUG] getAliasByFingerprint called with null fingerprint");
            }
            return null;
        }

        String purposeStr = (purpose == PURPOSE_ENC_SIGN) ? "ENC_SIGN" : "TLS";

        System.out.println("[MULTI-USER-CERT-DEBUG] getAliasByFingerprint called:");
        System.out.println("[MULTI-USER-CERT-DEBUG]   Fingerprint: " + fingerprintSHA1);
        System.out.println("[MULTI-USER-CERT-DEBUG]   User ID: " + userId);
        System.out.println("[MULTI-USER-CERT-DEBUG]   Purpose: " + purposeStr);

        if (logger != null) {
            logger.info("[CERT-DEBUG] Looking up certificate:");
            logger.info("[CERT-DEBUG]   Fingerprint: " + fingerprintSHA1);
            logger.info("[CERT-DEBUG]   User ID: " + userId);
            logger.info("[CERT-DEBUG]   Purpose: " + purposeStr);
        }

        // 1. Try user-specific keystore first (if userId provided)
        if (userId != null && userId >= 0) {
            System.out.println("[MULTI-USER-CERT-DEBUG] Trying user-specific keystore...");
            CertificateManager userManager = getUserCertificateManager(userId, purpose);
            System.out.println("[MULTI-USER-CERT-DEBUG]   User manager loaded: " + (userManager != null));
            if (userManager != null) {
                String alias = userManager.getAliasByFingerprint(fingerprintSHA1);
                System.out.println("[MULTI-USER-CERT-DEBUG]   Alias from user keystore: " + alias);
                if (alias != null) {
                    if (logger != null) {
                        logger.info("[CERT-DEBUG] ✓ Certificate FOUND in user " + userId + " keystore");
                        logger.info("[CERT-DEBUG]   Alias: " + alias);
                    }
                    System.out.println("[MULTI-USER-CERT-DEBUG] ✓ Returning alias from user keystore");
                    return alias;
                } else {
                    if (logger != null) {
                        logger.info("[CERT-DEBUG] ✗ Certificate NOT found in user " + userId + " keystore, trying system-wide...");
                    }
                    System.out.println("[MULTI-USER-CERT-DEBUG] ✗ Not in user keystore, trying system-wide...");
                }
            } else {
                if (logger != null) {
                    logger.info("[CERT-DEBUG] ✗ User " + userId + " keystore NOT available (may not exist)");
                }
                System.out.println("[MULTI-USER-CERT-DEBUG] ✗ User keystore not available");
            }
        }

        // 2. Fall back to system-wide keystore
        System.out.println("[MULTI-USER-CERT-DEBUG] Trying system-wide keystore...");
        CertificateManager systemManager = (purpose == PURPOSE_ENC_SIGN)
                ? systemEncSignManager
                : systemTLSManager;

        String alias = systemManager.getAliasByFingerprint(fingerprintSHA1);
        System.out.println("[MULTI-USER-CERT-DEBUG]   Alias from system keystore: " + alias);
        if (alias != null) {
            if (logger != null) {
                logger.info("[CERT-DEBUG] ✓ Certificate FOUND in system-wide keystore");
                logger.info("[CERT-DEBUG]   Alias: " + alias);
            }
        } else {
            if (logger != null) {
                logger.warning("[CERT-DEBUG] ✗ Certificate NOT FOUND in any keystore!");
            }
            System.out.println("[MULTI-USER-CERT-DEBUG] ✗ NOT FOUND in any keystore!");
        }
        return alias;
    }

    /**
     * Get certificate alias by fingerprint (byte array version)
     */
    public String getAliasByFingerprint(byte[] fingerprintSHA1, Integer userId, int purpose) {
        if (fingerprintSHA1 == null) {
            return null;
        }

        // Convert to hex string for lookup
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fingerprintSHA1.length; i++) {
            if (i > 0) sb.append(':');
            sb.append(String.format("%02X", fingerprintSHA1[i]));
        }
        return getAliasByFingerprint(sb.toString(), userId, purpose);
    }

    /**
     * Get X509 certificate by alias, searching user-specific then system-wide keystores
     *
     * @param alias Certificate alias
     * @param userId User ID (null for system-wide only)
     * @param purpose PURPOSE_TLS or PURPOSE_ENC_SIGN
     * @return X509Certificate
     * @throws Exception if certificate not found
     */
    public X509Certificate getX509Certificate(String alias, Integer userId, int purpose) throws Exception {
        // Try user-specific keystore first
        if (userId != null && userId >= 0) {
            CertificateManager userManager = getUserCertificateManager(userId, purpose);
            if (userManager != null) {
                try {
                    return userManager.getX509Certificate(alias);
                } catch (Exception e) {
                    // Not found in user keystore, fall through to system
                }
            }
        }

        // Fall back to system-wide keystore
        CertificateManager systemManager = (purpose == PURPOSE_ENC_SIGN)
                ? systemEncSignManager
                : systemTLSManager;
        return systemManager.getX509Certificate(alias);
    }

    /**
     * Get private key by alias, searching user-specific then system-wide keystores
     *
     * @param alias Certificate alias
     * @param userId User ID (null for system-wide only)
     * @param purpose PURPOSE_TLS or PURPOSE_ENC_SIGN
     * @return PrivateKey
     * @throws Exception if key not found
     */
    public PrivateKey getPrivateKey(String alias, Integer userId, int purpose) throws Exception {
        // Try user-specific keystore first
        if (userId != null && userId >= 0) {
            CertificateManager userManager = getUserCertificateManager(userId, purpose);
            if (userManager != null) {
                try {
                    return userManager.getPrivateKey(alias);
                } catch (Exception e) {
                    // Not found in user keystore, fall through to system
                }
            }
        }

        // Fall back to system-wide keystore
        CertificateManager systemManager = (purpose == PURPOSE_ENC_SIGN)
                ? systemEncSignManager
                : systemTLSManager;
        return systemManager.getPrivateKey(alias);
    }

    /**
     * Get certificate chain by alias
     */
    public Certificate[] getCertificateChain(String alias, Integer userId, int purpose) throws Exception {
        // Try user-specific keystore first
        if (userId != null && userId >= 0) {
            CertificateManager userManager = getUserCertificateManager(userId, purpose);
            if (userManager != null) {
                try {
                    return userManager.getCertificateChain(alias);
                } catch (Exception e) {
                    // Not found in user keystore, fall through to system
                }
            }
        }

        // Fall back to system-wide keystore
        CertificateManager systemManager = (purpose == PURPOSE_ENC_SIGN)
                ? systemEncSignManager
                : systemTLSManager;
        return systemManager.getCertificateChain(alias);
    }

    /**
     * Get or create user-specific certificate manager
     *
     * @param userId User ID
     * @param purpose PURPOSE_TLS or PURPOSE_ENC_SIGN
     * @return CertificateManager for this user and purpose, or null if keystore doesn't exist
     */
    private CertificateManager getUserCertificateManager(int userId, int purpose) {
        // Get or create user's manager map
        Map<Integer, CertificateManager> userManagers = userManagerCache.computeIfAbsent(
            userId,
            k -> new ConcurrentHashMap<>()
        );

        // Get or create manager for this purpose
        return userManagers.computeIfAbsent(purpose, p -> {
            try {
                String purposeStr = (purpose == PURPOSE_ENC_SIGN) ? "ENC_SIGN" : "TLS";

                if (logger != null) {
                    logger.info("[CERT-DEBUG] Loading user " + userId + " keystore (purpose=" + purposeStr + ")...");
                }

                CertificateManager manager = new CertificateManager(logger);

                // Determine keystore type based on purpose
                int keystoreUsage = (purpose == PURPOSE_ENC_SIGN)
                    ? KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN
                    : KeystoreStorageImplDB.KEYSTORE_USAGE_TLS;

                String storageType = (purpose == PURPOSE_ENC_SIGN)
                    ? KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_PKCS12
                    : KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS;

                KeystoreStorage storage = new KeystoreStorageImplDB(
                    de.mendelson.util.systemevents.SystemEventManagerImplAS2.instance(),
                    dbDriverManager,
                    keystoreUsage,
                    storageType,
                    userId
                );

                manager.loadKeystoreCertificates(storage);

                // List certificates in this keystore for debugging
                if (logger != null) {
                    logger.info("[CERT-DEBUG] ✓ Successfully loaded user " + userId + " keystore (purpose=" + purposeStr + ")");
                    List<de.mendelson.util.security.cert.KeystoreCertificate> certList = manager.getKeyStoreCertificateList();
                    logger.info("[CERT-DEBUG]   Total certificates: " + certList.size());
                    for (de.mendelson.util.security.cert.KeystoreCertificate cert : certList) {
                        logger.info("[CERT-DEBUG]     - Alias: " + cert.getAlias() +
                                   ", Fingerprint: " + cert.getFingerPrintSHA1() +
                                   ", IsKeyPair: " + cert.getIsKeyPair());
                    }
                }

                return manager;
            } catch (Exception e) {
                if (logger != null) {
                    logger.warning("[CERT-DEBUG] ✗ Failed to load user " + userId + " keystore (purpose=" + purpose + "): " + e.getMessage());
                }
                return null; // Keystore doesn't exist for this user
            }
        });
    }

    /**
     * Clear user-specific keystore cache (call when keystores are modified)
     */
    public void clearUserCache() {
        userManagerCache.clear();
        if (logger != null) {
            logger.info("Cleared user-specific certificate manager cache");
        }
    }

    /**
     * Clear cache for specific user
     */
    public void clearUserCache(int userId) {
        userManagerCache.remove(userId);
        if (logger != null) {
            logger.info("Cleared certificate manager cache for user " + userId);
        }
    }

    /**
     * Get system-wide ENC_SIGN certificate manager
     */
    public CertificateManager getSystemEncSignManager() {
        return systemEncSignManager;
    }

    /**
     * Get system-wide TLS certificate manager
     */
    public CertificateManager getSystemTLSManager() {
        return systemTLSManager;
    }
}
