package com.bc.utilities;

import com.bc.application.enumeration.PaymentScheme;
import lombok.extern.slf4j.Slf4j;
import static com.bc.model.constants.PaymentSchemeConstants.*;
/**
 * This utility class contains static methods that can be used for determining payment scheme.
 */
@Slf4j
public class DeterminePaymentScheme {

    /**
     * Determine Payment Scheme from PAN
     * @param pan Primary account number (card number).
     * @return Returns the derived Payment Scheme or UNKNOWN, if unsupported or pan is invalid.
     */
    public static PaymentScheme fromPan(String pan){
        log.debug("com.bc.utilities.DeterminePaymentScheme --> PAN input: {}/Length of PAN: {}.", pan, pan.length());
        if (pan.length() == DEFAULT_PAN_LENGTH) {
            switch (pan.substring(0,1)){
                case PAN_FIRST_DIGIT_IS_FOUR:
                    return PaymentScheme.VISA;
                case PAN_FIRST_DIGIT_IS_FIVE:
                    return PaymentScheme.MASTERCARD;
                case PAN_FIRST_DIGIT_IS_SIX:
                    return PaymentScheme.PRIVATELABEL;
            }
        }
        return PaymentScheme.UNKNOWN;
    }

}