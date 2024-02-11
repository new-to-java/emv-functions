package com.bc.application.enumeration;

import lombok.RequiredArgsConstructor;

/**
 * This enumeration defines valid values for Payment Schemes supported.
 */
@RequiredArgsConstructor
public enum PaymentScheme {

    MASTERCARD("MSTR"),
    VISA("VISA"),
    PRIVATELABEL("PLCC"),

    UNKNOWN("UNKNOWN");

    private final String value;

    /**
     * Method to check if the enum object value is set to MSTR (Mastercard).
     * @return True when value is set to MSTR.
     */
    public boolean isMastercard(){
        return this.equals(MASTERCARD);
    }
    /**
     * Method to check if the enum object value is set to VISA (Visa).
     * @return True when value is set to VISA.
     */
    public boolean isVisa(){
        return this.equals(VISA);
    }
    /**
     * Method to check if the enum object value is set to PLCC (Private Label).
     * @return True when value is set to PLCC.
     */
    public boolean isPrivateLabel(){
        return this.equals(PRIVATELABEL);
    }
    /**
     * Method to check if the enum object value is set to UNKNOWN (Unknown Payment Scheme).
     * @return True when value is set to UNKNOWN.
     */
    public boolean isUnknown(){
        return this.equals(UNKNOWN);
    }
}