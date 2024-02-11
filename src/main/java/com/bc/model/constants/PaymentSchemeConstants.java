package com.bc.model.constants;

/**
 * This class contains definition of constant attributes that are related to Payment Schemes.
 */
public class PaymentSchemeConstants {
    public static final int DEFAULT_PAN_LENGTH = 16;
    public static final String PAN_FIRST_DIGIT_IS_FOUR = "4";
    public static final String PAN_FIRST_DIGIT_IS_FIVE = "5";
    public static final String PAN_FIRST_DIGIT_IS_SIX = "6";
    // Payment schemes recognized by the utility
    public static final String MASTERCARD = "MASTERCARD";
    public static final String VISA = "VISA";
    public static final String PRIVATE_LABEL = "PRIVATE_LABEL";
    public static final String VALID_PAYMENT_SCHEME = "^(MASTERCARD|VISA|PRIVATE_LABEL)$";
    // Cryptogram Version Numbers recognized by the utility
    public static final String VISA_CVN10 = "VISA_CVN10";
    public static final String VISA_CVN18 = "VISA_CVN18";
    public static final String VISA_CVN22 = "VISA_CVN22";
    public static final String MASTERCARD_CVN10 = "MASTERCARD_CVN10";
    public static final String MASTERCARD_CVN14 = "MASTERCARD_CVN14";
    public static final String VALID_CRYPTOGRAM_VERSION_NUMBER = "^(VISA_CVN10|VISA_CVN18|VISA_CVN_22|MASTERCARD_CVN10|MASTERCARD_CVN14)$";
}