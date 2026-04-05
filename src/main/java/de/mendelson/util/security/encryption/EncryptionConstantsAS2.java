package de.mendelson.util.security.encryption;

import java.io.Serializable;

/**
 * Keeps the constant values of the signatures
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class EncryptionConstantsAS2 implements Serializable {

    private static final long serialVersionUID = 1L;
        
    public static final int ENCRYPTION_UNKNOWN = 0;
    public static final int ENCRYPTION_NONE = 1;
    public static final int ENCRYPTION_3DES = 2;
    public static final int ENCRYPTION_RC2_40 = 3;
    public static final int ENCRYPTION_RC2_64 = 4;
    public static final int ENCRYPTION_RC2_128 = 5;
    public static final int ENCRYPTION_RC2_196 = 6;
    public static final int ENCRYPTION_RC2_UNKNOWN = 7;
    public static final int ENCRYPTION_AES_128_CBC = 8;    
    public static final int ENCRYPTION_AES_192_CBC = 9;    
    public static final int ENCRYPTION_AES_256_CBC = 10;    
    /**
     * @deprecated by CBC
     */
    @Deprecated (since="08/2024")
    public static final int ENCRYPTION_AES_128 = 8;    
    /**
     * @deprecated by CBC
     */
    @Deprecated (since="08/2024")
    public static final int ENCRYPTION_AES_192 = 9;  
    /**
     * @deprecated by CBC
     */
    @Deprecated (since="08/2024")
    public static final int ENCRYPTION_AES_256 = 10;    
    public static final int ENCRYPTION_RC4_40 = 11;
    public static final int ENCRYPTION_RC4_56 = 12;
    public static final int ENCRYPTION_RC4_128 = 13;
    public static final int ENCRYPTION_RC4_UNKNOWN = 14;
    public static final int ENCRYPTION_DES = 15;
    @Deprecated (since="09/2024")
    public static final int ENCRYPTION_AES_128_RSAES_AOEP = 16;
    @Deprecated (since="09/2024")
    public static final int ENCRYPTION_AES_192_RSAES_AOEP = 17;
    @Deprecated (since="09/2024")
    public static final int ENCRYPTION_AES_256_RSAES_AOEP = 18;    
    public static final int ENCRYPTION_AES_128_CBC_RSAES_AOEP = 16;
    public static final int ENCRYPTION_AES_192_CBC_RSAES_AOEP = 17;
    public static final int ENCRYPTION_AES_256_CBC_RSAES_AOEP = 18;       
    public static final int ENCRYPTION_AES_128_GCM = 19;
    public static final int ENCRYPTION_AES_192_GCM = 20;
    public static final int ENCRYPTION_AES_256_GCM = 21;
    public static final int ENCRYPTION_AES_128_CCM = 22;
    public static final int ENCRYPTION_AES_192_CCM = 23;
    public static final int ENCRYPTION_AES_256_CCM = 24;
    public static final int ENCRYPTION_CHACHA20_POLY1305 = 25;
    public static final int ENCRYPTION_CAMELLIA_128_CBC = 26;
    public static final int ENCRYPTION_CAMELLIA_192_CBC = 27;
    public static final int ENCRYPTION_CAMELLIA_256_CBC = 28;
    public static final int ENCRYPTION_AES_128_GCM_RSAES_AOEP = 29;
    public static final int ENCRYPTION_AES_192_GCM_RSAES_AOEP = 30;
    public static final int ENCRYPTION_AES_256_GCM_RSAES_AOEP = 31;   
    
    public static final int ENCRYPTION_UNKNOWN_ALGORITHM = 99;
    
}
