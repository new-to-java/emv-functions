package com.bc.utilities;

import com.bc.application.enumeration.CryptogramVersionNumber;
import com.bc.application.enumeration.EMVUDKDerivationMethod;
import com.bc.application.enumeration.PaymentScheme;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static com.bc.model.pattern.CommonPattern.*;
import static com.bc.model.constants.PaymentSchemeConstants.*;
/**
 * This class implements the methods for deriving various cryptographic keys used in the EMV functions.
 * Note: All attributes except the key are mandatory input for the proper functioning of the key derivation methods.
 */
@Getter
@Slf4j
public class EMVUniqueDerivationKeyDerivator
        extends SelfValidator<EMVUniqueDerivationKeyDerivator>
        implements LoggerUtility {
    //Input attributes
    @NotNull
    @Pattern(regexp = IS_A_VALID_TDEA_KEY)
    private String inputKey;
    @NotNull
    @Pattern(regexp = IS_A_16_DIGIT_DECIMAL_NUMBER)
    private String pan;
    @NotNull
    @Pattern(regexp = IS_A_1_OR_2_DIGIT_DECIMAL_NUMBER)
    private String panSequenceNumber;
    @NotNull
    private PaymentScheme paymentScheme;
    @NotNull
    private CryptogramVersionNumber cryptogramVersionNumber;
    @NotNull
    private EMVUDKDerivationMethod emvudkDerivationMethod;
    /**
     * All args constructor
     */
    public EMVUniqueDerivationKeyDerivator(String inputKey,
                                           String pan,
                                           String panSequenceNumber,
                                           PaymentScheme paymentScheme,
                                           CryptogramVersionNumber cryptogramVersionNumber,
                                           EMVUDKDerivationMethod emvudkDerivationMethod) {
        this.inputKey = inputKey;
        this.pan = pan;
        this.panSequenceNumber = panSequenceNumber;
        this.paymentScheme = paymentScheme;
        this.cryptogramVersionNumber = cryptogramVersionNumber;
        this.emvudkDerivationMethod = emvudkDerivationMethod;
        selfValidate();
        logInfo(log,
                "Self validated successful for object {}.",
                this
        );
    }
    /**
     * Driver method for generating the requested TDEA key from the Master key.
     */
    public String generateUniqueDerivationKey() {
        logDebug(log,
                "Class data {}.",
                this
        );
        return generateUdk();

    }
    /**
     * Driver method for generating the requested TDEA key from the Master key.
     */
    private String generateUdk(){

        if (emvudkDerivationMethod.isMETHOD_A()) {
            logInfo(log,
                    "UDK derivation using EMV Option A.",
                    emvudkDerivationMethod
            );
            return getUniqueDerivationKeyOptionA();
        }
        return null;

    }
    /**
     * This method derives a card specific Unique Derivation Key from a master key using the Option A,
     * as described in EMV Book 2 - A1.4.1 (reference version: v4.1).
     */
    private String getUniqueDerivationKeyOptionA() {

        String udkKeyAComponent, udkKeyBComponent;
        String udkKeyA, udkKeyB;
        // Build UDK A component
        udkKeyAComponent = getUdkKeyAComponent();
        // Build UDK B component
        udkKeyBComponent = getUdkKeyBComponent(udkKeyAComponent);
        // Build UDK Key A and UDK Key B
        udkKeyA = tripleDESEncrypt(udkKeyAComponent,
                inputKey
        );
        udkKeyB = tripleDESEncrypt(udkKeyBComponent,
                inputKey
        );
        logDebug(log,
                "UDK components generated: Component A {} / Component B {}.",
                udkKeyAComponent,
                udkKeyBComponent
        );
        logDebug(log,
                "UDK: Key A {} / Key B {}.",
                udkKeyA,
                udkKeyB
        );
        return udkKeyA + udkKeyB;

    }
    /**
     * Method used to build the UDK Key A component.
     * @return UDK Key A component.
     */
    private String getUdkKeyAComponent(){
        if (panSequenceNumber.length() == 2) {
            return  (pan +
                    panSequenceNumber)
                    .substring(2
                    );
        } else {
            return (pan +
                    "0" +
                    panSequenceNumber)
                    .substring(2
                    );
        }
    }
    /**
     * Method used to build the UDK Key B component.
     * @return UDK Key B component.
     */
    private String getUdkKeyBComponent(String udkLeftComponent){
        Xor xor = new Xor(udkLeftComponent,
                "F".repeat(
                        DEFAULT_PAN_LENGTH
                )
        );
        return xor.doXor();
    }
    /**
     * Perform Triple DES Encryption.
     * @param inputData Input data to be encrypted.
     * @param inputKey Triple DES Key to encrypt the data.
     * @return Encrypted data String.
     */
    private String tripleDESEncrypt(String inputData,
                                    String inputKey) {
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
                ", pan='" + pan + '\'' +
                ", panSequenceNumber='" + panSequenceNumber + '\'' +
                ", paymentScheme='" + paymentScheme + '\'' +
                ", cryptogramVersionNumber='" + cryptogramVersionNumber + '\'' +
                ", emvudkDerivationMethod='" + emvudkDerivationMethod + '\'' +
                '}';
    }
}