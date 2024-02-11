package com.bc.utilities;

import com.bc.application.enumeration.CryptogramVersionNumber;
import com.bc.application.enumeration.KeyType;
import lombok.extern.slf4j.Slf4j;
/**
 * Defines methods for Visa UDK and Session Key derivation.
 */
@Slf4j
public class VisaKeyDerivator extends SelfValidator<VisaKeyDerivator> {

    private CryptogramVersionNumber cryptogramVersionNumber;
    private KeyType inputKeyType;
    private String inputKey;
    private KeyType keyToDerive;

    /**
     * All args constructor with self validation.
     */
    public VisaKeyDerivator(CryptogramVersionNumber cryptogramVersionNumber, String inputKey, KeyType inputKeyType, KeyType keyToDerive){
        this.cryptogramVersionNumber = cryptogramVersionNumber;
        this.inputKey = inputKey;
        this.inputKeyType = inputKeyType;
        this.keyToDerive = keyToDerive;
    }
    public String deriveKey(){
        return null;
    }

    /**
     * Method used to derive Unique Derivation Key (UDK) to be used for Visa payment scheme.
     * @return Unique Derivation Key.
     */
    private String deriveUniqueDerivationKey() {

        return null;

    }
    /**
     * Method used to generate Session Key to be used for Visa payment scheme.
     * @return Session Key.
     */
    private String deriveSessionKey() {

        return null;
    }

}