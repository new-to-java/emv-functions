package com.bc.application.enumeration;

import lombok.RequiredArgsConstructor;

/**
 * This enumeration defines valid values for Key Types supported.
 */
@RequiredArgsConstructor
public enum KeyType {

    ISSUER_MASTER_KEY("IMK"),
    UNIQUE_DERIVATION_KEY("UDK"),
    UNIQUE_SESSION_KEY("USK");

    private final String value;

    /**
     * Method to check if the enum object value is set to IMK (Issuer Master KEY).
     * @return True when value is set to IMK.
     */
    public boolean isIssuerMasterKey(){
        return this.equals(ISSUER_MASTER_KEY);
    }
    /**
     * Method to check if the enum object value is set to UDK (Unique Derivation Key).
     * @return True when value is set to UDK.
     */
    public boolean isUniqueDerivationKey(){
        return this.equals(UNIQUE_DERIVATION_KEY);
    }
    /**
     * Method to check if the enum object value is set to USK (Unique Session Key).
     * @return True when value is set to USK.
     */
    public boolean isSessionKey(){
        return this.equals(UNIQUE_SESSION_KEY);
    }

}