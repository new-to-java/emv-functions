package com.bc.utilities;

import com.bc.enumeration.PaymentScheme;

import static com.bc.constants.PaymentSchemeConstants.*;

/**
 * This utility class contains static methods that can be used for determining payment scheme.
 */
public class DeterminePaymentScheme {

    /**
     * Determine Payment Scheme from PAN
     * @param pan Primary account number (card number).
     * @return Returns the derived Payment Scheme or UNKNOWN, if unsupported or pan is invalid.
     */
    public static PaymentScheme fromPan(String pan){
        if (pan != null && pan.length() == DEFAULT_PAN_LENGTH) {
            switch (pan.substring(0,1)){
                case PAN_FIRST_DIGIT_IS_FOUR:
                    return PaymentScheme.VISA;
                case PAN_FIRST_DIGIT_IS_FIVE:
                    return PaymentScheme.MASTERCARD;
                default:
                    return PaymentScheme.UNKNOWN;
            }
        }
        return PaymentScheme.UNKNOWN;
    }

}