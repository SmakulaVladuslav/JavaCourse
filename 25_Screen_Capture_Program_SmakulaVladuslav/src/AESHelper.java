/* File AESHelper.java
Realization of AESHelper class and internal functions getMACAddress() deriveKeyFromMACAddress(String macAddress)
encrypt(String data, SecretKey key) decrypt(String encryptedData, SecretKey key)
Done by Smakula Vladuslav (group: computer methematics 1)
Date 10.12.2024
*/
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import java.util.Arrays;
import java.net.*;
import java.util.*;

/**
 * Helper class for AES encryption and decryption using a key derived from a MAC address.
 */
public class AESHelper {

    private static final String ALGORITHM = "AES";

    /**
     * Retrieves the MAC address of the first available network interface.
     *
     * @return the MAC address as a string in the format "XX:XX:XX:XX:XX:XX", or null if no MAC address is found.
     */
    public static String getMACAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();

                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X", mac[i]));
                        if (i < mac.length - 1) sb.append(":");
                    }
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if no MAC address is found
    }

    /**
     * Derives a SecretKey from the provided MAC address using SHA-256.
     * The MAC address is hashed, and the result is truncated to 16 bytes for AES-128.
     *
     * @param macAddress the MAC address to derive the key from.
     * @return the derived AES SecretKey.
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available.
     */
    public static SecretKey deriveKeyFromMACAddress(String macAddress) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(macAddress.getBytes());

        byte[] key = Arrays.copyOf(hash, 16); // Truncate to 16 bytes for AES-128

        return new SecretKeySpec(key, ALGORITHM);
    }

    /**
     * Encrypts the given data using AES encryption with the provided key.
     *
     * @param data the plaintext data to encrypt.
     * @param key the AES SecretKey to use for encryption.
     * @return the encrypted data as a Base64 encoded string.
     * @throws Exception if any encryption error occurs.
     */
    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * Decrypts the given encrypted data using AES decryption with the provided key.
     *
     * @param encryptedData the Base64 encoded encrypted data to decrypt.
     * @param key the AES SecretKey to use for decryption.
     * @return the decrypted plaintext data.
     * @throws Exception if any decryption error occurs.
     */
    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }
}
