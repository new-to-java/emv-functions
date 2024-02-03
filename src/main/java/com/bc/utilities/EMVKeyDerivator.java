package com.bc.utilities;

import com.bc.enumeration.CryptogramVersionNumber;
import com.bc.enumeration.KeyType;
import com.bc.enumeration.PaymentScheme;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static com.bc.constants.PaymentSchemeConstants.DEFAULT_PAN_LENGTH;
/**
 * This class implements the methods for deriving various cryptographic keys used in the EMV functions.
 * Note: All attributes except the key are mandatory input for the proper functioning of the key derivation methods.
 */
@Setter
@Getter
@Slf4j
public class EMVKeyDerivator {
    //Input attributes
    @NotEmpty
    @Pattern(regexp = "^[\\da-fA-F]{16,48}$")
    private String inputKey;
    @NotEmpty
    @Pattern(regexp = "^(CRYPTOGRAM_MASTER_KEY|UNIQUE_DERIVATION_KEY)$")
    private String inputKeyType;
    @NotEmpty
    @Pattern(regexp = "^[\\d]{16}$")
    private String pan;
    @NotEmpty
    @Pattern(regexp = "^[\\d]{1,2}$")
    private String panSequenceNumber;
    @NotEmpty
    @Pattern(regexp = "^[\\da-fA-F]{1,4}$")
    private String applicationTransactionCounter;
    @NotEmpty
    @Pattern(regexp = "^(MASTERCARD|VISA|PRIVATELABEL)$")
    private String paymentScheme;
    @NotEmpty
    @Pattern(regexp = "^(VISA_CVN10|VISA_CVN18|VISA_CVN22|MASTERCARD_CVN10|MASTERCARD_CVN14)$")
    private String cryptogramVersionNumber;
    @NotEmpty
    @Pattern(regexp = "^(UNIQUE_DERIVATION_KEY|SESSION_KEY)$")
    private String keyToGenerate;
    //Work attributes
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String workTdeaInputKey;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private static int KEY_LENGTH_TDEA_SINGLE = 16;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private static int KEY_LENGTH_TDEA_DOUBLE = 32;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private static int KEY_LENGTH_TDEA_TRIPLE = 48;

    public EMVKeyDerivator(){
        inputKey = null;
        inputKeyType = null;
        pan = null;
        panSequenceNumber = null;
        paymentScheme = null;
        cryptogramVersionNumber = null;
        keyToGenerate = null;
    }
    /**
     * Driver method for generating EMV Keys
     */
    public String generateKey() {
        if (objectIsValid()) {
            convertToTripleLengthTDEAKey();
            debugLog();
            return getRequestedKey();
        } else {
            debugLog();
            return null;
        }
    }
    /**
     * Method for invoking the self validator bean validator class' isAValidObject method for validating
     * the input attributes and ensure all the input attributes are set as required.
     */
    private boolean objectIsValid(){
        SelfValidator<EMVKeyDerivator> emvKeyDerivatorSelfValidator = new SelfValidator<>();
        return emvKeyDerivatorSelfValidator.isAValidObject(this);
    }
    /**
     * Method to convert a single or double length TDEA key to a triple length TDEA key.
     * Note: Parity check is not implemented and will be done in a future release.
     */
    private void convertToTripleLengthTDEAKey(){
        if (inputKey.length() == KEY_LENGTH_TDEA_SINGLE) {
            workTdeaInputKey = inputKey + inputKey + inputKey;
            log.info("Single length TDEA Key received: " + inputKey);
            log.info("Expanded triple length TDEA Key: " + workTdeaInputKey);
        } else if (inputKey.length() == KEY_LENGTH_TDEA_DOUBLE) {
            workTdeaInputKey = inputKey + inputKey.substring(0, KEY_LENGTH_TDEA_SINGLE);
            log.info("Double length TDEA Key received: " + inputKey);
            log.info("Expanded triple length TDEA Key: " + workTdeaInputKey);
        } else {
            workTdeaInputKey = inputKey;
            log.info("Triple length TDEA Key received: " + inputKey + ". No key expansion performed!");
        }
    }
    /**
     * Driver method for deriving the requested TDEA key from the Master key.
     */
    private String getRequestedKey(){
        switch (KeyType.valueOf(keyToGenerate)){
            case UNIQUE_DERIVATION_KEY:
                return getUniqueDerivationKey();
            case SESSION_KEY:
                return getSessionKey();
        }
        return null;
    }
    /**
     * This method derives a card specific Unique Derivation Key from a master key using the Option A,
     * as described in EMV Book 2 - A1.4.1 (reference version: v4.1).
     */
    private String getUniqueDerivationKey() {
        String udkKeyAComponent, udkKeyBComponent;
        String udkKeyA, udkKeyB;
        // Build UDK A component
        udkKeyAComponent = getUdkKeyAComponent();
        // Build UDK B component
        udkKeyBComponent = getUdkKeyBComponent(udkKeyAComponent);
        // Build UDK Key A and UDK Key B
        udkKeyA = tripleDESEncypt(udkKeyAComponent, workTdeaInputKey);
        udkKeyB = tripleDESEncypt(udkKeyBComponent, workTdeaInputKey);
        return udkKeyA + udkKeyB;
    }
    /**
     * Method used to build the UDK Key A component.
     * @return UDK Key A component.
     */
    private String getUdkKeyAComponent(){
        if (panSequenceNumber.length() == 2) {
            return  (pan + panSequenceNumber).substring(2);
        } else {
            return (pan + "0" + panSequenceNumber).substring(2);
        }
    }
    /**
     * Method used to build the UDK Key B component.
     * @return UDK Key B component.
     */
    private String getUdkKeyBComponent(String udkLeftComponent){

        Xor xor = new Xor();
        xor.setLeftOperand(udkLeftComponent);
        xor.setRightOperand("F".repeat(DEFAULT_PAN_LENGTH));
        return xor.doXor();

    }
    /**
     * Perform Triple DES Encryption.
     * @param inputData Input data to be encrypted.
     * @param inputKey Triple DES Key to encrypt the data.
     * @return Encrypted data String.
     */
    private String tripleDESEncypt(String inputData, String inputKey) {

        TripleDES tripleDES = new TripleDES();
        tripleDES.setInputData(inputData);
        tripleDES.setKey(inputKey);
        return tripleDES.encrypt();

    }
    /**
     * Driver method used to derive a Session Key from a Master Key based on Payment Scheme.
     * @return Generated Session Key
     */
    private String getSessionKey() {

        switch (PaymentScheme.valueOf(paymentScheme)){
            case VISA:
            case PRIVATELABEL:
                return getSessionKeyDerivationMethodForVisa();
            case MASTERCARD:
                return null;
            default:
                return null;
        }

    }
    /**
     * Method used to determine the Session Key Derivation method to be used for Visa payment scheme.
     * @return Session Key Derivation method to use.
     */
    private String getSessionKeyDerivationMethodForVisa() {

        switch (CryptogramVersionNumber.valueOf(cryptogramVersionNumber)) {
            // For Visa CVN10 cards, use UDK itself as Session Key.
            case VISA_CVN10:
                return inputKey;
            // For Visa CVN18 and CVN22 cards, use EMV CSK method to derive a Session Key.
            case VISA_CVN18:
            case VISA_CVN22:
                return getEMVCommonSessionKeyDerivationMethodBasedKey();
            default:
                return null;
        }

    }
    /**
     * Generate a Session Key using EMV Common Session Key derivation method, this implementation is based on the
     * details provided in EMV Book 2 and VIS 1.6 - D.7.2.
     * @return Session Key generated using EMV CSK method.
     */
    private String getEMVCommonSessionKeyDerivationMethodBasedKey(){

        String emvCskKeyAComponent, emvCskKeyBComponent;
        String emvCskKeyA, emvCskKeyB;
        // Get UDK Key A and build EMV CS Key A Component
        emvCskKeyAComponent = getEMVCommonSessionKeyAComponent();
        emvCskKeyA = tripleDESEncypt(emvCskKeyAComponent, workTdeaInputKey);
        // Get UDK Key B and build EMV CS Key B Component
        emvCskKeyBComponent = getEMVCommonSessionKeyBComponent();
        emvCskKeyB = tripleDESEncypt(emvCskKeyBComponent, workTdeaInputKey);
        // Return generated EMV CSK method session key
        log.info(this.getClass() + " Session Key components generated: Component A {} / Component B {}.", emvCskKeyAComponent, emvCskKeyBComponent);
        log.info(this.getClass() + " Session Key generated: Key A {} / Key B {}.", emvCskKeyA, emvCskKeyB);
        return emvCskKeyA + emvCskKeyB;

    }
    /**
     * Method used to build the EMV Common Session Key Derivation Key A component.
     * @return EMV CSK A component.
     */
    private String getEMVCommonSessionKeyAComponent(){

        String paddedAtc = Padding.padString(applicationTransactionCounter, "0", 4, true);
        return paddedAtc + "F00000000000";

    }
    /**
     * Method used to build the EMV Common Session Key Derivation Key B component.
     * @return EMV CSK B component.
     */
    private String getEMVCommonSessionKeyBComponent(){

        String paddedAtc = Padding.padString(applicationTransactionCounter, "0", 4, true);
        return paddedAtc + "0F0000000000";

    }
    /**
     * Override method for the object's default toString method.
     * @return String representing object's attribute values.
     */
    @Override
    public String toString() {
        return "{" +
                "inputKey='" + inputKey + '\'' +
                ", inputKeyType='" + inputKeyType + '\'' +
                ", pan='" + pan + '\'' +
                ", panSequenceNumber='" + panSequenceNumber + '\'' +
                ", paymentScheme='" + paymentScheme + '\'' +
                ", cryptogramVersionNumber='" + cryptogramVersionNumber + '\'' +
                ", keyToGenerate='" + keyToGenerate + '\'' +
                ", workTdeaInputKey='" + workTdeaInputKey + '\'' +
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