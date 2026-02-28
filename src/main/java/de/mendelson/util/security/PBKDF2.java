//$Header: /as2/de/mendelson/util/security/PBKDF2.java 4     11/02/25 13:40 Heller $
package de.mendelson.util.security;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Allows to generate hash values and validating passwords against these hashes,
 * hashes are stored using the PBKDF2 algorithm
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class PBKDF2 {

    private final static int KEY_LENGTH = 128;
    private final static String ALGORITHM = "PBKDF2WithHmacSHA1";
    
    private static final String DELIMITER = "#";
    /**
     * Increase this value in the future if the computers are faster
     */
    private static final int GENERATION_ITERATIONS = 75000;

    private PBKDF2(){        
    }
    
    /**
     * Validates a passed raw password against a stored has
     *
     * @param transmittedPassword The password that is transmitted - it should be checked if this matches
     */
    public static boolean validatePassword(String transmittedPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] storedParts = storedPassword.split(DELIMITER);
        int iterations = Integer.parseInt(storedParts[0]);
        byte[] storedSalt = hexToByteArray(storedParts[1]);
        byte[] storedHash = hexToByteArray(storedParts[2]);
        PBEKeySpec spec = new PBEKeySpec(transmittedPassword.toCharArray(), storedSalt, iterations, storedHash.length * 8);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] transmittedHash = keyFactory.generateSecret(spec).getEncoded();
        boolean match = Arrays.equals(storedHash, transmittedHash);
        return( match );
    }

    private static byte[] hexToByteArray(String hexStr) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexStr.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    /**
     * Generates a string that contains the password hash with some additional
     * information in the following format: iterations#salt#hash
     *
     *
     * @param password Password to generate the password hash from
     * @return String that contains the hash
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] chars = password.toCharArray();
        byte[] salt = generateSalt().getBytes();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, GENERATION_ITERATIONS, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = keyFactory.generateSecret(spec).getEncoded();
        return (GENERATION_ITERATIONS + DELIMITER + byteArrayToHex(salt) + DELIMITER + byteArrayToHex(hash));
    }

    private static String generateSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return (new String(salt));
    }

    private static String byteArrayToHex(byte[] byteArray) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, byteArray);
        String hex = bi.toString(16);
        int paddingLength = (byteArray.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

}
