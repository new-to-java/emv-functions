package com.bc.utilities;

import com.bc.enumeration.CryptogramVersionNumber;
import com.bc.enumeration.KeyType;
import com.bc.enumeration.PaymentScheme;
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
    //Output attributes
    private String derivedKey;
    private int derivedKeyLength;
    private String outputKeyType;
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
        derivedKeyLength = 0;
        derivedKey = null;
        outputKeyType = null;
    }

    /**
     * Driver method for generating EMV Keys
     */
    public boolean generateKey() {
        if (objectIsValid()) {
            convertToTripleLengthTDEAKey();
        } else {
            return false;
        }
        System.out.println("TDEA Key input is: " + workTdeaInputKey);
        return true;
    }
    /**
     * Method for invoking the selfvalidator bean validator class' isAValidObject method for validating
     * the input attributes for a Key generation request.
     */
    private boolean objectIsValid(){

        SelfValidator<EMVKeyDerivator> validator = new SelfValidator<>();
        return validator.isAValidObject(this);

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

    private void deriveRequestedKey(){

    }

}