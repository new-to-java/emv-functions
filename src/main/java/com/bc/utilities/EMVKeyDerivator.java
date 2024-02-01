package com.bc.utilities;

import com.bc.enumeration.KeyType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * This class implements the methods for deriving various cryptographic keys used in the EMV functions.
 * Note: All attributes except the key are mandatory input for the proper functioning of the key derivation methods.
 */
@Setter
@Getter
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
    @Pattern(regexp = "^(MASTERCARD|VISA)$")
    private String paymentScheme;
    @NotEmpty
    @Pattern(regexp = "^(VISA_CVN18|MASTERCARD_CVN14)$")
    private String cryptogramVersionNumber;
    @NotEmpty
    @Pattern(regexp = "^(UNIQUE_DERIVATION_KEY|SESSION_KEY)$")
    private String keyToGenerate;
    //Output attributes
    @Setter(AccessLevel.NONE)
    private String derivedKey;
    @Setter(AccessLevel.NONE)
    private int derivedKeyLength;
    @Setter(AccessLevel.NONE)
    private String derivedKeyType;
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
        derivedKeyLength = 0;
        derivedKey = null;
        derivedKeyType = null;
    }
    /**
     * Driver method for generating EMV Keys
     */
    public boolean generateKey() {
        if (objectIsValid()) {
            convertToTripleLengthTDEAKey();
            deriveRequestedKey();
        } else {
            return false;
        }
        System.out.println("TDEA Key input is: " + workTdeaInputKey);
        return true;
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
        } else if (inputKey.length() == KEY_LENGTH_TDEA_DOUBLE) {
            workTdeaInputKey = inputKey + inputKey.substring(0, KEY_LENGTH_TDEA_SINGLE);
        } else {
            workTdeaInputKey = inputKey;
        }
    }

    /**
     * Driver method for deriving the requested TDEA key from the TDEA key.
     */
    private void deriveRequestedKey(){
        switch (KeyType.valueOf(keyToGenerate)){
            case UNIQUE_DERIVATION_KEY:
                deriveUniqueDerivationKey();
            case SESSION_KEY:
                deriveSessionKey();
        }
    }
    /**
     * This method derives a card specific Unique Derivation Key from a master key.
     */
    private void deriveUniqueDerivationKey() {
        String udkLeft, udkRight;
        Xor xor = new Xor();
        System.out.println("PAN: " + pan);
        System.out.println("PAN Seq: " + panSequenceNumber);
        if (panSequenceNumber.length() == 2) {
            xor.setLeftOperand((pan + panSequenceNumber).substring(2));
        } else {
            xor.setLeftOperand((pan + "0" + panSequenceNumber).substring(2));
        }
        xor.setRightOperand("F".repeat(10));
        System.out.println("Xored value: " + xor.doXor());
    }
    /**
     * This method derives a Session Key from a master key.
     */
    private void deriveSessionKey() {

    }

}