package com.bc.model.constants;

/**
 * Class defining static values that are used in IAD parsing methods.
 */
public class IADStaticData {
    //-----------------------------------------------------------------------------------------------------------------
    //                                            Error messages and Patterns
    //-----------------------------------------------------------------------------------------------------------------
    // Visa IAD start byte validator and error message
    public final static String VISA_IAD_STARTS_WITH_06_OR_1F = "^(06|1F|1f).*"; // Visa IAD starting character, i.e., must begin with "06" or "1F" or "1f".
    public final static String VISA_IAD_START_BYTE_ERROR = "Visa IAD must begin with \"06\" or \"1F\" or \"1f\".";
    // IAD Format validator and error message
    public final static String VALID_IAD_FORMAT = "^[\\da-fA-F]{14}(?:[\\da-fA-F]{2}){0,25}$"; // IAD 14 to 64 Hexadecimal numbers with even stepping
    public final static String IAD_FORMAT_ERROR = "IAD must contain even number of hexadecimal digits between 14 to 64 characters long.";
    //-----------------------------------------------------------------------------------------------------------------
    //                                                   Constants
    //-----------------------------------------------------------------------------------------------------------------
    // Integer constants
    public final static int IAD_LENGTH = 2; // Length of Visa Discretionary Data for Format 0/1/3 IAD or total length of IAD for Format 2
    public final static int DKI_LENGTH = 2; // Length of Derivation Key Index
    public final static int CVN_LENGTH = 2; // Length of Cryptogram Version Number
    public final static int CVR_STANDARD_LENGTH = 8; // Length of Card Verification Results for standard IAD
    public final static int CVR_FORMAT2_LENGTH = 10; // Length of Card Verification Results for format 2 IAD
    public final static int IDD_LENGTH = 2; // Length of Issuer Discretionary Data for a Format 0/1/3 IAD
    public final static int IDD_OPTION_ID_LENGTH = 2; // Length of Issuer Discretionary Data Option ID
    // String constants
    public final static String FORMAT_2_IAD_LENGTH = "1F";
    public final static String IAD_LENGTH_NAME = "Length";
    public final static String DKI_NAME = "DKI";
    public final static String CVN_NAME = "CVN";
    public final static String CVR_NAME = "CVR";
    public final static String IDD_LENGTH_NAME = "IddLength";
    public final static String IDD_OPTION_ID = "IddOptionId";
    public final static String IDD_NAME = "IDD";
    public final static String IAD_FORMAT_NAME = "IADFormat";
}