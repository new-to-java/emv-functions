package com.bc.model.constants;

/**
 * This class contains constants defining the commonly used patterns
 */
public class CommonConstants {

    // Decimal numbers
    public static final String IS_A_DECIMAL_NUMBER = "^[\\d]+$";
    // 2 Digit decimal number
    public static final String IS_A_1_OR_2_DIGIT_DECIMAL_NUMBER = "^[\\d]{1,2}$";
    // 16 Digit decimal number
    public static final String IS_A_16_DIGIT_DECIMAL_NUMBER = "^[\\d]{16}$";
    // Hexadecimal numbers
    public static final String IS_A_HEXADECIMAL_NUMBER = "^[\\da-fA-F]+$";
    // 4 Digit hexadecimal number
    public static final String IS_A_4_DIGIT_HEXADECIMAL_NUMBER = "^[\\da-fA-F]{4}$";
    // 1 to 4 Digit hexadecimal number
    public static final String IS_A_1_TO_4_DIGIT_HEXADECIMAL_NUMBER = "^[\\da-fA-F]{1,4}$";
    // TDEA Key 16, 32 or 48 Hexadecimal numbers
    public static final String IS_A_VALID_TDEA_KEY = "^[\\da-fA-F]{16}(?:[\\da-fA-F]{16}){0,2}$";
    // IAD 14 to 64 Hexadecimal numbers with even stepping
    public static final String IS_VALID_IAD_FORMAT = "^[\\da-fA-F]{14}(?:[\\da-fA-F]{2}){0,25}$";

}