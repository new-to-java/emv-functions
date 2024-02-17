package com.bc.utilities;

import com.bc.application.enumeration.CryptogramVersionNumber;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static com.bc.model.pattern.CommonPattern.*;
/**
 * This class implements the methods for deriving EMV Session Key derivation methods.
 */
@Setter
@Getter
@Slf4j
public class EMVSessionKeyDerivator
        extends SelfValidator<EMVSessionKeyDerivator>
        implements LoggerUtility {
    //Input attributes
    @NotNull
    @Pattern(regexp = IS_A_VALID_TDEA_KEY)
    private String inputKey;
    @NotNull
    @Pattern(regexp = IS_A_1_TO_4_DIGIT_HEXADECIMAL_NUMBER)
    private String applicationTransactionCounter;
    @NotNull
    private CryptogramVersionNumber cryptogramVersionNumber;
    /**
     * All args constructor
     */
    public EMVSessionKeyDerivator(String inputKey,
                                  String applicationTransactionCounter,
                                  CryptogramVersionNumber cryptogramVersionNumber){
        this.inputKey = inputKey;
        this.applicationTransactionCounter = applicationTransactionCounter;
        this.cryptogramVersionNumber = cryptogramVersionNumber;
        selfValidate();
        logInfo(log, "Self validated.");

    }
    /**
     * Driver method for generating the requested Session Key from the Master key.
     */
    public String generateSessionKey() {
        return getSessionKey();
    }
    /**
     * Driver method used to derive a Session Key from a Master Key based on Payment Scheme.
     * @return Generated Session Key
     */
    private String getSessionKey() {
        String sessionKey;
        switch (cryptogramVersionNumber){
            case CVN14:
            case CVN18:
            case CVN22:
                logInfo(log, "CVN14/CVN18/CVN22 session key derivation.");
                sessionKey = getEMVCommonSessionKeyDerivationMethodBasedKey();
                logDebug(log, "Session Key derived from UDK: {}.", sessionKey);
                return sessionKey;
            default:
                logInfo(log, "CVN10 session key derivation.");
                sessionKey = udkAsSessionKey();
                logDebug(log, "UDK itself as session key: {}.", sessionKey);
                return sessionKey;
        }
    }
    /**
     * Return Unique Derivation Key itself as session key.
     * @return Session Key generated using EMV CSK method.
     */
    private String udkAsSessionKey(){

        return inputKey;

    }
    /**
     * Generate a Session Key using EMV Common Session Key derivation method, this implementation is based on the
     * details provided in EMV Book 2 and VIS 1.6 - D.7.2.
     * @return Session Key generated using EMV CSK method.
     */
    private String getEMVCommonSessionKeyDerivationMethodBasedKey(){

        String emvCskKeyAComponent, emvCskKeyBComponent;
        String emvCskKeyA, emvCskKeyB;
        // Get UDK Key A and build EMV CS Key A Component
        emvCskKeyAComponent = getEMVCommonSessionKeyAComponent();
        emvCskKeyA = tripleDESEncrypt(emvCskKeyAComponent,
                inputKey);
        // Get UDK Key B and build EMV CS Key B Component
        emvCskKeyBComponent = getEMVCommonSessionKeyBComponent();
        emvCskKeyB = tripleDESEncrypt(emvCskKeyBComponent,
                inputKey);
        // Return generated EMV CSK method session key
        logDebug(log, "Session Key components generated: Component A {} / Component B {}.", emvCskKeyAComponent, emvCskKeyBComponent);
        logDebug(log, "Session Key generated: Key A {} / Key B {}.", emvCskKeyA, emvCskKeyB);
        return emvCskKeyA + emvCskKeyB;

    }
    /**
     * Method used to build the EMV Common Session Key Derivation Key A component.
     * @return EMV CSK A component.
     */
    private String getEMVCommonSessionKeyAComponent(){

        String paddedAtc = Padding.padString(applicationTransactionCounter, "0", 4, true);
        return paddedAtc + "F00000000000";

    }
    /**
     * Method used to build the EMV Common Session Key Derivation Key B component.
     * @return EMV CSK B component.
     */
    private String getEMVCommonSessionKeyBComponent(){

        String paddedAtc = Padding.padString(applicationTransactionCounter, "0", 4, true);
        return paddedAtc + "0F0000000000";

    }
    /**
     * Perform Triple DES Encryption.
     * @param inputData Input data to be encrypted.
     * @param inputKey Triple DES Key to encrypt the data.
     * @return Encrypted data String.
     */
    private String tripleDESEncrypt(String inputData, String inputKey) {

        TripleDES tripleDES = new TripleDES();
        tripleDES.setInputData(inputData);
        tripleDES.setKey(inputKey);
        return tripleDES.encrypt();

    }
    /**
     * Override method for the object's default toString method.
     * @return String representing object's attribute values.
     */
    @Override
    public String toString() {
        return "{" +
                "inputKey='" + inputKey + '\'' +
                "applicationTransactionCounter='" + applicationTransactionCounter + '\'' +
                ", cryptogramVersionNumber='" + cryptogramVersionNumber + '\'' +
                '}';
    }
}