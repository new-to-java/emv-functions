package com.bc.application.port.in.rest.cryptogramfunctions.command;

import com.bc.utilities.LoggerUtility;
import com.bc.utilities.AbstractSelfValidator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import static com.bc.model.pattern.CommonPattern.*;
/**
 * Application Cryptogram Generation command class with selfvalidator.
 */
@Slf4j
public class GenerateApplicationCryptogramCommand
        extends AbstractSelfValidator<GenerateApplicationCryptogramCommand>
        implements LoggerUtility {
    @NotEmpty
    @Pattern(regexp = IS_A_16_DIGIT_HEXADECIMAL_NUMBER, message = "Pan must be numeric, and exactly 16 digits long.")
    public String pan;
    @NotEmpty
    @Pattern(regexp = IS_A_1_OR_2_DIGIT_DECIMAL_NUMBER, message = "PanSequenceNumber must be numeric, and 1 to 2 digits long.")
    public String panSequenceNumber;
    @NotEmpty
    @Pattern(regexp = IS_A_VALID_TDEA_KEY, message = "IssuerMasterKey must be a single, double or triple length TDEA key, comprised of hexadecimal digits only.")
    public String issuerMasterKey;
    @NotEmpty
    @Pattern(regexp = IS_A_1_TO_12_DIGIT_DECIMAL_NUMBER, message = "AmountAuthorised must be numeric, and 1 to 12 digits long.")
    public String amountAuthorised;
    @Pattern(regexp = IS_A_1_TO_12_DIGIT_DECIMAL_NUMBER, message = "AmountOther must be numeric, and 1 to 12 digits long.")
    public String amountOther;
    //No country code validation is performed at this time
    @NotEmpty
    @Pattern(regexp = IS_A_3_DIGIT_DECIMAL_NUMBER, message = "TerminalCountryCode must be an ISO 3166-1 numeric code.")
    public String terminalCountryCode;
    @NotEmpty
    @Pattern(regexp = IS_A_10_DIGIT_HEXADECIMAL_NUMBER, message = "TerminalVerificationResults must be exactly 10 hexadecimal digits.")
    public String terminalVerificationResults;
    //No country code validation is performed at this time
    @NotEmpty
    @Pattern(regexp = IS_A_3_DIGIT_DECIMAL_NUMBER, message = "TransactionCurrencyCode must be an ISO 3166-1 numeric code.")
    public String transactionCurrencyCode;
    //No date validation is performed at this time
    @NotEmpty
    @Size(min = 10, max = 10, message = "Date must be in ISO Date Format (YYYY-MM-DD).")
    @Pattern(regexp = IS_VALID_ISO_DATE_YYYY_MM_DD)
    public String transactionDate;
    @NotEmpty
    @Pattern(regexp = IS_A_2_DIGIT_HEXADECIMAL_NUMBER, message = "TransactionType must be exactly 2 hexadecimal digits.")
    public String transactionType;
    @NotEmpty
    @Pattern(regexp = IS_A_8_DIGIT_HEXADECIMAL_NUMBER, message = "UnpredictableNumber must be exactly 8 hexadecimal digits.")
    public String unpredictableNumber;
    @NotEmpty
    @Pattern(regexp = IS_A_4_DIGIT_HEXADECIMAL_NUMBER, message = "UnpredictableNumber must be exactly 4 hexadecimal digits.")
    public String applicationInterchangeProfile;
    @NotEmpty
    @Pattern(regexp = IS_A_1_TO_4_DIGIT_HEXADECIMAL_NUMBER, message = "UnpredictableNumber must be between 1 to 4 hexadecimal digits long.")
    public String applicationTransactionCounter;
    @NotEmpty
    @Pattern(regexp = IS_VALID_IAD_FORMAT, message = "IssuerApplicationData must be between 1 to 64 hexadecimal digits long.")
    public String issuerApplicationData;

    /**
     * All args constructor for creating GenerateApplicationCryptogramCommand
     * @param pan PAN.
     * @param panSequenceNumber PAN Sequence number.
     * @param issuerMasterKey Issuer Master Key.
     * @param amountAuthorised Amount authorised.
     * @param amountOther Amount other.
     * @param terminalCountryCode Terminal Country Code.
     * @param terminalVerificationResults Terminal Verification Results.
     * @param transactionCurrencyCode Transaction Currency Code.
     * @param transactionDate Transaction Date.
     * @param transactionType Transaction Type.
     * @param unpredictableNumber Unpredictable Number.
     * @param applicationInterchangeProfile Application Interchange Profile.
     * @param applicationTransactionCounter Application Transaction Counter.
     * @param issuerApplicationData Issuer Application Data.
     */
    public GenerateApplicationCryptogramCommand(String pan,
                                                String panSequenceNumber,
                                                String issuerMasterKey,
                                                String amountAuthorised,
                                                String amountOther,
                                                String terminalCountryCode,
                                                String terminalVerificationResults,
                                                String transactionCurrencyCode,
                                                String transactionDate,
                                                String transactionType,
                                                String unpredictableNumber,
                                                String applicationInterchangeProfile,
                                                String applicationTransactionCounter,
                                                String issuerApplicationData) {
        this.pan = pan;
        this.panSequenceNumber = panSequenceNumber;
        this.issuerMasterKey = issuerMasterKey;
        this.amountAuthorised = amountAuthorised;
        this.amountOther = amountOther;
        this.terminalCountryCode = terminalCountryCode;
        this.terminalVerificationResults = terminalVerificationResults;
        this.transactionCurrencyCode = transactionCurrencyCode;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.unpredictableNumber = unpredictableNumber;
        this.applicationInterchangeProfile = applicationInterchangeProfile;
        this.applicationTransactionCounter = applicationTransactionCounter;
        this.issuerApplicationData = issuerApplicationData;
        // Call self validate
        selfValidate();
        logInfo(log,
                "Self validation successful for object {}.",
                this
        );
    }
    /**
     * Override method to return a string representation of the class.
     * @return Attributes converted to string.
     */
    @Override
    public String toString() {
        return "GenerateApplicationCryptogramCommand{" +
                "pan='" + pan + '\'' +
                ", panSequenceNumber='" + panSequenceNumber + '\'' +
                ", issuerMasterKey='" + issuerMasterKey + '\'' +
                ", amountAuthorised='" + amountAuthorised + '\'' +
                ", amountOther='" + amountOther + '\'' +
                ", terminalCountryCode='" + terminalCountryCode + '\'' +
                ", terminalVerificationResults='" + terminalVerificationResults + '\'' +
                ", transactionCurrencyCode='" + transactionCurrencyCode + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", unpredictableNumber='" + unpredictableNumber + '\'' +
                ", applicationInterchangeProfile='" + applicationInterchangeProfile + '\'' +
                ", applicationTransactionCounter='" + applicationTransactionCounter + '\'' +
                ", issuerApplicationData='" + issuerApplicationData + '\'' +
                '}';
    }

}
