package com.bc.model.constants;

/**
 * Class defining static values that are used in IAD parsing methods.
 */
public class IADStaticData {
    //-----------------------------------------------------------------------------------------------------------------
    //                                            Error messages and Patterns
    //-----------------------------------------------------------------------------------------------------------------
    // Visa IAD validator and error messages
    public final static String VISA_IAD_STARTS_WITH_06_OR_1F = "^(06|1F|1f).*"; // Visa IAD starting character, i.e., must begin with "06" or "1F" or "1f".
    public final static String VISA_IAD_START_BYTE_ERROR = "Visa IAD must begin with \"06\" or \"1F\" or \"1f\".";
    public final static String VISA_VALID_IAD_FORMAT = "^[\\da-fA-F]{14}(?:[\\da-fA-F]{2}){0,25}$"; // Visa IAD must be 14 to 64 hexadecimal numbers with even stepping
    public final static String VISA_IAD_FORMAT_ERROR = "Visa IAD must only contain an even number of hexadecimal digits, between 14 to 64 characters.";
    // Mastercard IAD validator and error messages
    public final static String MASTERCARD_VALID_IAD_FORMAT = "^(?:[\\dA-Fa-f]{36}|[\\dA-Fa-f]{40}|[\\dA-Fa-f]{52}|[\\dA-Fa-f]{56})$"; // Mastercard IAD must be 36, 40, 52 or 56 hexadecimal numbers
    public final static String MASTERCARD_IAD_FORMAT_ERROR = "Mastercard IAD must only contain hexadecimal digits of 36, 40, 52 or 56 characters.";
    //-----------------------------------------------------------------------------------------------------------------
    //                                                   Constants
    //-----------------------------------------------------------------------------------------------------------------
    // Visa Integer constants
    public final static int VISA_IAD_LENGTH = 2; // Length of Visa Discretionary Data for Format 0/1/3 IAD or total length of IAD for Format 2
    public final static int VISA_DKI_LENGTH = 2; // Length of Derivation Key Index
    public final static int VISA_CVN_LENGTH = 2; // Length of Cryptogram Version Number
    public final static int VISA_CVR_STANDARD_LENGTH = 8; // Length of Card Verification Results for standard IAD
    public final static int VISA_CVR_FORMAT2_LENGTH = 10; // Length of Card Verification Results for format 2 IAD
    public final static int VISA_IDD_LENGTH = 2; // Length of Issuer Discretionary Data for a Format 0/1/3 IAD
    public final static int VISA_IDD_OPTION_ID_LENGTH = 2; // Length of Issuer Discretionary Data Option ID
    // Mastercard Integer constants
    public final static int MASTECARD_DKI_LENGTH = 2; // Length of Derivation Key Index
    public final static int MASTERCARD_CVN_LENGTH = 2; // Length of Cryptogram Version Number
    public final static int MASTERCARD_CVN10_CVR_LENGTH = 8; // Length of Card Verification Results for CVN 10 cards
    public final static int MASTERCARD_CVR_LENGTH = 12; // Length of Card Verification Results
    public final static int MASTERCARD_DAC_ICC_LENGTH = 4; // Length of DAC/ICC Dynamic Number
    public final static int MASTERCARD_PLAIN_TEXT_COUNTERS_LENGTH = 16; // Length of plaintext offline counters
    public final static int MASTERCARD_ENCRYPTED_COUNTERS_LENGTH = 32; // Length of encrypted offline counters
    public final static int MASTERCARD_LAST_ONLINE_ATC_LENGTH = 4; // Length of last online ATC
    // Common String constants
    public final static String IAD_LENGTH_NAME = "Length";
    public final static String DKI_NAME = "DKI";
    public final static String CVN_NAME = "CVN";
    public final static String CVR_NAME = "CVR";
    // Visa String constants
    public final static String VISA_FORMAT_2_IAD_LENGTH = "1F";
    public final static String VISA_IDD_LENGTH_NAME = "IddLength";
    public final static String VISA_IDD_OPTION_ID = "IddOptionId";
    public final static String VISA_IDD_NAME = "IDD";
    public final static String VISA_IAD_FORMAT_NAME = "IADFormat";
    // Mastercard String constants
    public final static String MASTERCARD_DAC_ICC_NAME = "DAC/ICC";
    public final static String MASTERCARD_COUNTERS_NAME = "Counters";
    public final static String MASTERCARD_LAST_ONLINE_ATC_NAME = "LOATC";
    public final static String MASTERCARD_SKD_METHOD_NAME = "SKDMethod";
}