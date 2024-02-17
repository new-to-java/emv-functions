package com.bc.application.enumeration;

import lombok.RequiredArgsConstructor;

/**
 * This enumeration defines valid values for Cryptogram Version Numbers supported.
 */
@RequiredArgsConstructor
public enum CryptogramVersionNumber {

    CVN10("CVN10"),
    CVN14("CVN14"),
    CVN18("CVN18"),
    CVN22("CVN22"),
    CVN2C("CVN2C");

    public final String value;

    /**
     * Method to check if the enum object value is set to CVN10.
     * @return True when value is set to CVN10.
     */
    public boolean isCVN10(){
        return this.equals(CVN10);
    }

    /**
     * Method to check if the enum object value is set to CVN14.
     * @return True when value is set to CVN14.
     */
    public boolean isCVN14(){
        return this.equals(CVN14);
    }

    /**
     * Method to check if the enum object value is set to CVN18.
     * @return True when value is set to CVN18.
     */
    public boolean isCVN18(){
        return this.equals(CVN18);
    }

    /**
     * Method to check if the enum object value is set to CVN22.
     * @return True when value is set to CVN22.
     */
    public boolean isCVN22(){
        return this.equals(CVN22);
    }
    /**
     * Method to check if the enum object value is set to CVN2C.
     * @return True when value is set to CVN2C.
     */
    public boolean isCVN2C(){
        return this.equals(CVN2C);
    }

}