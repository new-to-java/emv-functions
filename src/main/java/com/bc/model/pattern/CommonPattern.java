package com.bc.model.pattern;

/**
 * This class contains constants defining the commonly used patterns
 */
public class CommonPattern {

    // Decimal numbers
    // 3 Digit decimal number
    public static final String IS_A_3_DIGIT_DECIMAL_NUMBER = "^[\\d]{3}+$";
    // 16 Digit decimal number
    public static final String IS_A_16_DIGIT_DECIMAL_NUMBER = "^[\\d]{16}$";
    // 1 or 2 Digit decimal number
    public static final String IS_A_1_OR_2_DIGIT_DECIMAL_NUMBER = "^[\\d]{1,2}$";
    // 1 or 12 Digit decimal number
    public static final String IS_A_1_TO_12_DIGIT_DECIMAL_NUMBER = "^[\\d]{1,12}$";
    // Hexadecimal numbers
    // 10 Digit hexadecimal number
    public static final String IS_A_10_DIGIT_HEXADECIMAL_NUMBER = "^[\\da-fA-F]{10}$";
    // 16 Digit hexadecimal number
    public static final String IS_A_16_DIGIT_HEXADECIMAL_NUMBER = "^[\\da-fA-F]{16}$";
    // 1 to 4 Digit hexadecimal number
    public static final String IS_A_1_TO_4_DIGIT_HEXADECIMAL_NUMBER = "^[\\da-fA-F]{1,4}$";
    // 2 Digit hexadecimal number
    public static final String IS_A_2_DIGIT_HEXADECIMAL_NUMBER = "^[\\da-fA-F]{2}$";
    // 4 Digit hexadecimal number
    public static final String IS_A_4_DIGIT_HEXADECIMAL_NUMBER = "^[\\da-fA-F]{4}$";
    // 8 Digit hexadecimal number
    public static final String IS_A_8_DIGIT_HEXADECIMAL_NUMBER = "^[\\da-fA-F]{8}$";
    // TDEA Key 16, 32 or 48 Hexadecimal numbers
    public static final String IS_A_VALID_TDEA_KEY = "^[\\da-fA-F]{16}(?:[\\da-fA-F]{16}){0,2}$";
    // IAD 14 to 64 Hexadecimal numbers with even stepping
    public static final String IS_VALID_IAD_FORMAT = "^[\\da-fA-F]{14}(?:[\\da-fA-F]{2}){0,25}$";
    // Date Pattern
    // ISO Date - YYYY-MM-DD format
    public static final String IS_VALID_ISO_DATE_YYYY_MM_DD = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";

}