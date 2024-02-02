package com.bc.utilities;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Class implementing wrapper functions for Triple Data Encryption Standard (TDES) Algorithm functions
 * using java standard Crypto libraries.
 */
@Slf4j
@Setter
public class TripleDES {
    private String inputData;
    private String key;
    private final String DESEDE = "DESede";
    private final String MODE = "ECB";
    private final String PADDING = "NoPadding";
    private final boolean ENCRYPT = true;
    private final boolean DECRYPT = false;
    @Setter(AccessLevel.NONE)
    private Cipher des;
    @Setter(AccessLevel.NONE)
    private Key secretKey;
    @Setter(AccessLevel.NONE)
    private String outputData;
    /**
     * Method to perform a Triple DES encryption of a clear text using a key passed
     * @return Cipher text generated from clear text after encryption.
     */
    public String encrypt(){

        initialize(ENCRYPT);

        byte [] decodedInputData = decodeinputDataTextToByteArray();
        byte [] desEdeOutputData = runDESede(decodedInputData);
        outputData = Hex.encodeHexString(desEdeOutputData);
        debugLog();

        return outputData;

    }
    /**
     * Method to perform a Triple DES decryption of a cipher text using a key passed
     * @return Clear text generated from cipher text after decryption.
     */
    public String decrypt(){

        initialize(DECRYPT);

        byte [] decodedInputData = decodeinputDataTextToByteArray();
        byte [] desEdeOutputData = runDESede(decodedInputData);
        outputData = Hex.encodeHexString(desEdeOutputData);
        debugLog();

        return outputData;

    }
    /**
     * Method to initialize the Cipher object to set algorithm to DESede and perform encryption or decryption.
     * @param encrypt When set to true, will initialise the Cipher object to DESede encrypt,
     *                else the Cipher object will be set to DESede decrypt.
     */
    private void initialize(boolean encrypt) {

        initializeDESAlgorithm();
        initializeDESedeKey();
        if (encrypt) {
            initializeForDesEncryption();
        } else {
            initializeForDesDecryption();
        }

    }
    /**
     * Initialize Cipher object des with the DESede algorithm with desired mode and padding.
     */
    private void initializeDESAlgorithm(){

        StringBuilder algorithmWithModeAndPadding = new StringBuilder();

        algorithmWithModeAndPadding.append(DESEDE)
                .append("/")
                .append(MODE)
                .append("/")
                .append(PADDING);

        try {
            des = Cipher.getInstance(String.valueOf(algorithmWithModeAndPadding));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException cipherException) {
            debugLog();
            throw new RuntimeException(this.getClass() + " - Cipher object algorithm initialization failed - "
                    + cipherException.getCause() + " - " + cipherException.getMessage());
        }

    }
    /**
     * Decode the plaintext key to byte array and initialize the Key object.
     */
    private void initializeDESedeKey() {
        try {
            secretKey = new SecretKeySpec(Hex.decodeHex(key), DESEDE);
        } catch (DecoderException decoderException){
            debugLog();
            throw new RuntimeException(this.getClass() + " - Key decoding to byte array failed - "
                    + decoderException.getCause() + " - " + decoderException.getMessage());
        }

    }
    /**
     * Initialize Cipher object for encryption.
     */
    private void initializeForDesEncryption(){
        try {
            des.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException invalidKeyException){
            debugLog();
            throw new RuntimeException(this.getClass() + " - Cipher object initialization failed due to invalid key - "
                    + invalidKeyException.getCause() + " - " + invalidKeyException.getMessage());
        }

    }
    /**
     * Initialize Cipher object for decryption.
     */
    private void initializeForDesDecryption(){
        try {
            des.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException invalidKeyException) {
            debugLog();
            throw new RuntimeException(this.getClass() + " - Cipher object initialization failed due to invalid key - "
                    + invalidKeyException.getCause() + " - " + invalidKeyException.getMessage());
        }

    }
    /**
     * Transform the input data to byte array.
     */
    private byte [] decodeinputDataTextToByteArray(){
        try {
            return Hex.decodeHex(inputData);
        } catch (DecoderException decoderException){
            debugLog();
            throw new RuntimeException(this.getClass() + " - Clear text data decoding to byte array failed - "
                    + decoderException.getCause() + " - " + decoderException.getMessage());
        }

    }
    /**
     * Run the DESede algorithm.
     */
    private byte [] runDESede(byte [] decodedInputData) {
        try {
            return des.doFinal(decodedInputData);
        } catch (IllegalBlockSizeException | BadPaddingException cipherException){
            debugLog();
            throw new RuntimeException(this.getClass() + " - Encrypt/Decrypt operation failed - "
                    + cipherException.getCause() + " - " + cipherException.getMessage());
        }

    }
    /**
     * Override method for the object's default toString method.
     * @return String representing object's attribute values.
     */
    @Override
    public String toString() {

        return "{" +
                "inputData='" + inputData + '\'' +
                ", key='" + key + '\'' +
                ", outputData='" + outputData + '\'' +
                '}';

    }
    /**
     * Method for logging the input data and output data for the EMVKeyDerivator function, when the debug log level is enabled.
     */
    private void debugLog(){

        if (log.isDebugEnabled()) {
            log.debug(" Debug log : {}", this);
        }

    }

}
