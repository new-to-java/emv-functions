package com.bc.application.enumeration;

import lombok.RequiredArgsConstructor;

/**
 * This enumeration defines valid values for Cryptogram Version Numbers supported.
 */
@RequiredArgsConstructor
public enum CryptogramVersionNumber {

    CVN10("CVN10"),
    CVN14("CVN14"),
    CVN16("CVN16"),
    CVN17("CVN17"),
    CVN18("CVN18"),
    CVN20("CVN20"),
    CVN21("CVN21"),
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
     * Method to check if the enum object value is set to CVN16.
     * @return True when value is set to CVN16.
     */
    public boolean isCVN16(){
        return this.equals(CVN16);
    }

    /**
     * Method to check if the enum object value is set to CVN17.
     * @return True when value is set to CVN17.
     */
    public boolean isCVN17(){
        return this.equals(CVN17);
    }

    /**
     * Method to check if the enum object value is set to CVN18.
     * @return True when value is set to CVN18.
     */
    public boolean isCVN18(){
        return this.equals(CVN18);
    }

    /**
     * Method to check if the enum object value is set to CVN20.
     * @return True when value is set to CVN20.
     */
    public boolean isCVN20(){
        return this.equals(CVN20);
    }

    /**
     * Method to check if the enum object value is set to CVN21.
     * @return True when value is set to CVN21.
     */
    public boolean isCVN21(){
        return this.equals(CVN21);
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