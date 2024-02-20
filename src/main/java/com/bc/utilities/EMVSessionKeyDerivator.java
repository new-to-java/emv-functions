package com.bc.utilities;

import com.bc.application.enumeration.CryptogramVersionNumber;
import com.bc.application.enumeration.PaymentScheme;
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
        extends AbstractSelfValidator<EMVSessionKeyDerivator>
        implements LoggerUtility {
    //Input attributes
    @NotNull
    @Pattern(regexp = IS_A_VALID_TDEA_KEY)
    private String inputKey;
    @NotNull
    @Pattern(regexp = IS_A_1_TO_4_DIGIT_HEXADECIMAL_NUMBER)
    private String applicationTransactionCounter;
    @NotNull
    @Pattern(regexp = IS_A_8_DIGIT_HEXADECIMAL_NUMBER)
    private String unpredictableNumber;
    @NotNull
    private CryptogramVersionNumber cryptogramVersionNumber;
    @NotNull
    private PaymentScheme paymentScheme;
    /**
     * All args constructor
     */
    public EMVSessionKeyDerivator(String inputKey,
                                  String applicationTransactionCounter,
                                  String unpredictableNumber,
                                  CryptogramVersionNumber cryptogramVersionNumber,
                                  PaymentScheme paymentScheme){
        this.inputKey = inputKey;
        this.applicationTransactionCounter = applicationTransactionCounter;
        this.unpredictableNumber = unpredictableNumber;
        this.cryptogramVersionNumber = cryptogramVersionNumber;
        this.paymentScheme = paymentScheme;
        // Call self validate
        selfValidate();
        logInfo(log,
                "Self validation successful for object {}.",
                this
        );
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
        switch (paymentScheme){
            case VISA:
                return getVisaSessionKey();
            case MASTERCARD:
                return getMastercardSessionKey();
            default:
                return null;
        }
    }
    /**
     * Method used to derive a Mastercard Payment Scheme Session Key from a Master Key.
     * @return Generated Session Key
     */
    private String getMastercardSessionKey() {
        String sessionKey;
        switch (cryptogramVersionNumber){
            case CVN10:
                logInfo(log, "CVN10 Mastercard session key derivation.");
                sessionKey = getMastercardProprietarySessionKeyDerivationMethodBasedKey();
                logDebug(log, "Mastercard Proprietary Session Key derivation based key: {}.", sessionKey);
                return sessionKey;
            case CVN14:
                logInfo(log, "CVN14 Mastercard session key derivation.");
                sessionKey = getEMVCommonSessionKeyDerivationMethodBasedKey();
                logDebug(log, "EMV CSK method based Session Key derived from UDK: {}.", sessionKey);
                return sessionKey;
            default:
                return null;
        }
    }
    /**
     * Method used to derive a Visa Payment Scheme Session Key from a Master Key.
     * @return Generated Session Key
     */
    private String getVisaSessionKey() {
        String sessionKey;
        switch (cryptogramVersionNumber){
            case CVN10:
                logInfo(log, "CVN10 Visa session key derivation.");
                sessionKey = udkAsSessionKey();
                logDebug(log, "UDK returned as session key: {}.", sessionKey);
                return sessionKey;
            case CVN14:
            case CVN18:
            case CVN22: // CVN 22 will not work correctly,
                        // since the CVN 22 UDK derivation mechanism uses EMV Option B UDK derivation.
                        // This has not been implemented yet.
                logInfo(log, "CVN14/CVN18/CVN22 Visa session key derivation.");
                sessionKey = getEMVCommonSessionKeyDerivationMethodBasedKey();
                logDebug(log, "EMV CSK method based Session Key derived from UDK: {}.", sessionKey);
                return sessionKey;
            default:
                return null;
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
        logDebug(log,
                "Session Key components generated: Component A {} / Component B {}.",
                emvCskKeyAComponent,
                emvCskKeyBComponent
        );
        logDebug(log,
                "Session Key generated: Key A {} / Key B {}.",
                emvCskKeyA,
                emvCskKeyB
        );
        return emvCskKeyA +
                emvCskKeyB;

    }
    /**
     * Method used to build the EMV Common Session Key Derivation Key A component.
     * @return EMV CSK A component.
     */
    private String getEMVCommonSessionKeyAComponent(){

        String paddedAtc = Padding.padString(applicationTransactionCounter,
                "0",
                4,
                true
        );
        return paddedAtc +
                "F00000000000";

    }
    /**
     * Method used to build the EMV Common Session Key Derivation Key B component.
     * @return EMV CSK B component.
     */
    private String getEMVCommonSessionKeyBComponent(){
        String paddedAtc = Padding.padString(applicationTransactionCounter,
                "0",
                4,
                true
        );
        return paddedAtc +
                "0F0000000000";
    }
    /**
     * Generate a Session Key using Mastercard Proprietary Session Key (Proprietary SKD) derivation method,
     * this implementation is similar to the EMV Book 2 and VIS 1.6 - D.7.2 EMV CSK method, i.e., uses ATC as a
     * diversification factor for session key derivation, however this method additionally uses the Unpredictable Number
     * also as a diversification factor in session key generation.
     * @return Session Key generated using Mastercard Proprietary SKD method.
     */
    private String getMastercardProprietarySessionKeyDerivationMethodBasedKey(){

        String mcProprietarySkdKeyAComponent, mcProprietarySkdKeyBComponent;
        String mcProprietarySkdKeyA, mcProprietarySkdKeyB;
        // Get UDK Key A and build MC Proprietary SKD Key A Component
        mcProprietarySkdKeyAComponent = getMastercardProprietarySessionKeyAComponent();
        mcProprietarySkdKeyA = tripleDESEncrypt(mcProprietarySkdKeyAComponent,
                inputKey);
        // Get UDK Key B and build MC Proprietary SKD Key B Component
        mcProprietarySkdKeyBComponent = getMastercardProprietarySessionKeyBComponent();
        mcProprietarySkdKeyB = tripleDESEncrypt(mcProprietarySkdKeyBComponent,
                inputKey);
        // Return generated MC Proprietary SKD method based session key
        logDebug(log,
                "Session Key components generated: Component A {} / Component B {}.",
                mcProprietarySkdKeyAComponent,
                mcProprietarySkdKeyBComponent
        );
        logDebug(log,
                "Session Key generated: Key A {} / Key B {}.",
                mcProprietarySkdKeyA,
                mcProprietarySkdKeyB
        );
        return mcProprietarySkdKeyA +
                mcProprietarySkdKeyB;

    }
    /**
     * Method used to build the Mastercard Proprietary SKD method Key A component.
     * @return EMV CSK A component.
     */
    private String getMastercardProprietarySessionKeyAComponent(){
        String paddedAtc = Padding.padString(applicationTransactionCounter,
                "0",
                4,
                true
        );
        return paddedAtc +
                "F000" +
                unpredictableNumber;
    }
    /**
     * Method used to build the Mastercard Proprietary SKD method Key A component.
     * @return EMV CSK B component.
     */
    private String getMastercardProprietarySessionKeyBComponent(){
        String paddedAtc = Padding.padString(applicationTransactionCounter,
                "0",
                4,
                true
        );
        return paddedAtc +
                "0F00" +
                unpredictableNumber;
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
                "unpredictableNumber='" + unpredictableNumber + '\'' +
                "cryptogramVersionNumber='" + cryptogramVersionNumber + '\'' +
                '}';
    }
}