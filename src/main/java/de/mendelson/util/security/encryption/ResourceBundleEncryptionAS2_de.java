//$Header: /as2/de/mendelson/util/security/encryption/ResourceBundleEncryptionAS2_de.java 11    9/12/24 16:03 Heller $
package de.mendelson.util.security.encryption;

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
 * @version $Revision: 11 $
 */
public class ResourceBundleEncryptionAS2_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {        
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_UNKNOWN, "Unbekannt"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_NONE, "Unverschlüsselt"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_3DES, "3DES"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_40, "RC2-40"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_64, "RC2-64"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_128, "RC2-128"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_196, "RC2-196"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_UNKNOWN, "RC2"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_128_CBC, "AES-128 (CBC)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_192_CBC, "AES-192 (CBC)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_256_CBC, "AES-256 (CBC)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_128_CBC_RSAES_AOEP, "AES-128 (CBC, RSAES-OAEP)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_192_CBC_RSAES_AOEP, "AES-192 (CBC, RSAES-OAEP)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_256_CBC_RSAES_AOEP, "AES-256 (CBC, RSAES-OAEP)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_128_GCM_RSAES_AOEP, "AES-128 (GCM, RSAES-OAEP)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_192_GCM_RSAES_AOEP, "AES-192 (GCM, RSAES-OAEP)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_256_GCM_RSAES_AOEP, "AES-256 (GCM, RSAES-OAEP)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_128_GCM, "AES-128 (GCM)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_192_GCM, "AES-192 (GCM)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_256_GCM, "AES-256 (GCM)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_128_CCM, "AES-128 (CCM)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_192_CCM, "AES-192 (CCM)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_256_CCM, "AES-256 (CCM)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_CHACHA20_POLY1305, "CHACHA20-POLY1305"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_40, "RC4-40"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_56, "RC4-56"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_128, "RC4-128"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_UNKNOWN, "RC4"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_DES, "DES"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_CAMELLIA_128_CBC, "CAMELLIA-128 (CBC)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_CAMELLIA_192_CBC, "CAMELLIA-192 (CBC)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_CAMELLIA_256_CBC, "CAMELLIA-256 (CBC)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_UNKNOWN_ALGORITHM, "Unbekannt"},
    };
}