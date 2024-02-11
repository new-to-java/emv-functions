package com.bc.application.enumeration;

import lombok.RequiredArgsConstructor;

/**
 * This class defines the EMV Unique Derivation Key Derivation methods to be used.
 */
@RequiredArgsConstructor
public enum EMVUDKDerivationMethod {

    METHOD_A("METHOD_A"),
    METHOD_B("METHOD_B");

    private final String value;

    /**
     * Method to check if the enum object value is set to CVN10.
     * @return True when value is set to CVN10.
     */
    public boolean isMETHOD_A(){
        return this.equals(METHOD_A);
    }

    /**
     * Method to check if the enum object value is set to CVN14.
     * @return True when value is set to CVN14.
     */
    public boolean isMETHOD_B(){
        return this.equals(METHOD_B);
    }

}