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
public class TripleDES
        implements LoggerUtility {
    private String inputData;
    private String key;
    private final String DES_EDE = "DESede";
    private final String MODE = "ECB";
    private final String PADDING = "NoPadding";
    private final boolean ENCRYPT = true;
    private final boolean DECRYPT = false;
    @Setter(AccessLevel.NONE)
    private String workTdeaInputKey;
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
        byte [] decodedInputData = decodeInputDataTextToByteArray();
        byte [] desEdeOutputData = runDESede(decodedInputData);
        outputData = Hex.encodeHexString(desEdeOutputData);
        logInfo(log, "TDEA Encryption request processed!");
        logDebug(log, "Encrypted data: {}.", outputData);
        return outputData;
    }
    /**
     * Method to perform a Triple DES decryption of a cipher text using a key passed
     * @return Clear text generated from cipher text after decryption.
     */
    public String decrypt(){
        initialize(DECRYPT);
        byte [] decodedInputData = decodeInputDataTextToByteArray();
        byte [] desEdeOutputData = runDESede(decodedInputData);
        outputData = Hex.encodeHexString(desEdeOutputData);
        logInfo(log, "TDEA Decryption request processed!");
        logDebug(log, "Decrypted data: {}.", outputData);
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
        algorithmWithModeAndPadding.append(DES_EDE)
                .append("/")
                .append(MODE)
                .append("/")
                .append(PADDING);

        try {
            des = Cipher.getInstance(algorithmWithModeAndPadding.toString());
        } catch (NoSuchPaddingException |
                 NoSuchAlgorithmException cipherException) {
            throwExceptionAndTerminate("Cipher object algorithm initialization failed",
                    cipherException
            );
        }
    }
    /**
     * Decode the plaintext key to byte array and initialize the Key object.
     */
    private void initializeDESedeKey() {
        try {
            convertToTripleLengthTDEAKey();
            secretKey = new SecretKeySpec(Hex.decodeHex(
                    workTdeaInputKey),
                    DES_EDE
            );
        } catch (DecoderException decoderException){
            throwExceptionAndTerminate("Key decoding to byte array failed - ",
                    decoderException
            );
        }
    }
    /**
     * Initialize Cipher object for encryption.
     */
    private void initializeForDesEncryption(){
        try {
            des.init(Cipher.ENCRYPT_MODE,
                    secretKey
            );
        } catch (InvalidKeyException invalidKeyException){
            throwExceptionAndTerminate("Cipher object initialization failed due to invalid key",
                    invalidKeyException);
        }
    }
    /**
     * Initialize Cipher object for decryption.
     */
    private void initializeForDesDecryption(){
        try {
            des.init(Cipher.DECRYPT_MODE,
                    secretKey
            );
        } catch (InvalidKeyException invalidKeyException) {
            throwExceptionAndTerminate("Cipher object initialization failed due to invalid key",
                    invalidKeyException);
        }
    }
    /**
     * Transform the input data to byte array.
     */
    private byte [] decodeInputDataTextToByteArray(){
        byte [] decodedHexData = new byte[0];
        try {
            decodedHexData = Hex.decodeHex(inputData);
        } catch (DecoderException decoderException){
            throwExceptionAndTerminate("Clear text data decoding to byte array failed",
                    decoderException
            );
        }
        return decodedHexData;
    }
    /**
     * Run the DESede algorithm.
     */
    private byte [] runDESede(byte [] decodedInputData) {
        byte [] cryptoOutputData = new byte[0];
        try
        {
            cryptoOutputData = des.doFinal(decodedInputData);
        } catch (IllegalBlockSizeException |
                 BadPaddingException cryptoException){
            throwExceptionAndTerminate("Encrypt/Decrypt operation failed",
                    cryptoException);
        }
        return cryptoOutputData;
    }
    /**
     * Method to convert a single or double length TDEA key to a triple length TDEA key.
     * Note: Parity check is not implemented and will be done in a future release.
     */
    private void convertToTripleLengthTDEAKey(){
        final int KEY_LENGTH_TDEA_SINGLE = 16;
        final int KEY_LENGTH_TDEA_DOUBLE = 32;
        if (key.length() == KEY_LENGTH_TDEA_SINGLE) {
            workTdeaInputKey = key + key + key;
            logDebug(log,
                    "Single length TDEA Key received {}, Key expanded to triple length TDEA Key: {}.",
                    key,
                    workTdeaInputKey
            );
        } else if (key.length() == KEY_LENGTH_TDEA_DOUBLE) {
            workTdeaInputKey = key + key.substring(0,
                    KEY_LENGTH_TDEA_SINGLE
            );
            logDebug(log,
                    "Double length TDEA Key received {}, Key expanded to triple length TDEA Key: {}.",
                    key,
                    workTdeaInputKey
            );
        } else {
            workTdeaInputKey = key;
            logInfo(log,
                    "Triple length TDEA Key input: {}.",
                    workTdeaInputKey
            );
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
     * Method to raise an exception and terminate processing.
     * @param exception Generic exception object.
     * @param message Message to be included in the exception.
     */
    private void throwExceptionAndTerminate(String message,
                                            Exception exception) {
        throw new RuntimeException(this.getClass() +
                " --> " +
                message +
                " Cause: " +
                exception.getCause() +
                " Message: " +
                exception.getMessage()
        );
    }
}
