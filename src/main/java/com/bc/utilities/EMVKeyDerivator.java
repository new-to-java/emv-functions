package com.bc.utilities;

import com.bc.enumeration.KeyType;
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
    @Pattern(regexp = "^(CRYPTOGRAM_MASTER_KEY)$")
    private String inputKeyType;
    @NotEmpty
    @Pattern(regexp = "^[\\d]{16}$")
    private String pan;
    @NotEmpty
    @Pattern(regexp = "^[\\d]{1,2}$")
    private String panSequenceNumber;
    @NotEmpty
    @Pattern(regexp = "^(MASTERCARD|VISA|PRIVATELABEL)$")
    private String paymentScheme;
    @NotEmpty
    @Pattern(regexp = "^(VISA_CVN18|MASTERCARD_CVN14)$")
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
            return deriveRequestedKey();
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
    private String deriveRequestedKey(){
        switch (KeyType.valueOf(keyToGenerate)){
            case UNIQUE_DERIVATION_KEY:
                return deriveUniqueDerivationKey();
            case SESSION_KEY:
                deriveSessionKey();
        }
        return null;
    }
    /**
     * This method derives a card specific Unique Derivation Key from a master key.
     */
    private String deriveUniqueDerivationKey() {
        String udkLeftComponent, udkRightComponent;
        String udkLeft, udkRight;
        Xor xor = new Xor();
        if (panSequenceNumber.length() == 2) {
            udkLeftComponent = (pan + panSequenceNumber).substring(2);
        } else {
            udkLeftComponent = (pan + "0" + panSequenceNumber).substring(2);
        }
        xor.setLeftOperand(udkLeftComponent);
        xor.setRightOperand("F".repeat(DEFAULT_PAN_LENGTH));
        udkRightComponent = xor.doXor();
        TripleDES tripleDES = new TripleDES();
        tripleDES.setInputData(udkLeftComponent);
        tripleDES.setKey(workTdeaInputKey);
        udkLeft = tripleDES.encrypt();
        tripleDES = new TripleDES();
        tripleDES.setInputData(udkRightComponent);
        tripleDES.setKey(workTdeaInputKey);
        udkRight = tripleDES.encrypt();
        return udkLeft + udkRight;
    }
    /**
     * This method derives a Session Key from a master key.
     */
    private void deriveSessionKey() {

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