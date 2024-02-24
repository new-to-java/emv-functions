package com.bc.application.enumeration;

import lombok.Getter;

/**
 * Enum defining the components details of Visa Payment Scheme Issuer Application Data (9F10).
 */
@Getter
public enum VisaIadElements {
    // Visa Discretionary Data length
    LENGTH("Length", 2, "Length of Visa Discretionary Data in IAD."),
    // Derivation Key Index
    DKI("DKI", 2, "Derivation Key Index."),
    CVN_FMT_2("CVN", 2, "Cryptogram Version Number - Format 2."),
    // Cryptogram Version Number
    CVN("CVN", 2, "Cryptogram Version Number."),
    DKI_FMT2("DKI", 2, "Derivation Key Index - Format 2."),
    // Cryptogram Version Number
    CVR("CVR", 8, "Card Verification Results."),
    CVR_FMT_2("CVR", 10, "Card Verification Results - Format 2."),
    // Issuer Discretionary Data Length
    IDD_LENGTH("IDD_LENGTH", 2, "Issuer Discretionary Data length."),
    // Issuer Discretionary OPTION ID
    IDD_OPTION_ID("IDD_OPTION_ID", 2, "Issuer Discretionary Data Option ID."),
    // Issuer Discretionary Data
    IDD("IDD", 0, "Issuer Discretionary Data");
    // Variables
    private final String label;
    private final int length;
    private final String description;
    /**
     * Constructor for the enum
     * @param label Label of the enum.
     * @param length Length of the enum.
     * @param description Description of enum.
     */
    VisaIadElements(String label, int length, String description) {
        this.label = label;
        this.length = length;
        this.description = description;
    }
}