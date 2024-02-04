package com.bc.utilities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class defining methods for parsing a Visa specific IAD and splitting the IAD into the following components.
 * - Length Indicator - Byte 1.
 * - Derivation Key index (DKI) - Byte 2.
 * - Cryptogram Version Number (CVN) - Byte 3 - In hexadecimal format.
 * - Card Verification Results (CVR) -
 * - Issuer Discretionary Data (IDD) -
 */
@Getter
@Setter
@Slf4j
public class VisaIADParser {
    @NotEmpty
    @Pattern(regexp = "^[\\da-fA-F]{14,64}+$")
    private String issuerApplicationData;
    @Setter(AccessLevel.NONE)
    private String lengthIndicator;
    @Setter(AccessLevel.NONE)
    private String derivationKeyIndex;
    @Setter(AccessLevel.NONE)
    private String cryptogramVersionNumber;
    @Setter(AccessLevel.NONE)
    private String cardVerificationResults;
    @Setter(AccessLevel.NONE)
    private String issuerDiscretionaryData;
    @Setter(AccessLevel.NONE)
    private String issuerApplicationDataFormat;
    /**
     * Constructor with only Issuer Application Data (IAD).
     */
    public VisaIADParser(){
        this.issuerApplicationData = null;
        this.lengthIndicator = null;
        this.derivationKeyIndex = null;
        this.cryptogramVersionNumber = null;
        this.cardVerificationResults = null;
        this.issuerDiscretionaryData = null;
        this.issuerApplicationDataFormat = null;
    }
    public VisaIADParser parseIad(){
        // Call sub methods to parse each component of the IAD.
        extractLengthIndicator();
        extractDerivationKeyIndex();
        extractCryptogramVersionNumber();
        extractcardVerificationResults();
        extractIssuerDiscretionaryData();
        // Call logger
        debugLog();
        // Return the object
        return this;

    }
    /**
     * Method for extracting Length Indicator.
     * Start: 0
     * End: 2
     */
    private void extractLengthIndicator() {
        lengthIndicator = issuerApplicationData.substring(0, 2);
        if (lengthIndicator.equals("06")){
            issuerApplicationDataFormat = "0/1/3";
        } else {
            issuerApplicationDataFormat = "2";
        }
    }
    /**
     * Method for extracting Derivation Key Index.
     * Start: 2
     * End: 4
     */
    private void extractDerivationKeyIndex() {
        derivationKeyIndex = issuerApplicationData.substring(2, 4);
    }
    /**
     * Method for extracting Cryptogram Version Number.
     * Start: 4
     * End: 6
     */
    private void extractCryptogramVersionNumber() {
        // Extract CVN in hexadecimal format and convert to decimal.
        cryptogramVersionNumber = String.valueOf(Integer.parseInt(issuerApplicationData.substring(4, 6), 16));
    }
    /**
     * Method for extracting Card Verification Results.
     * Start: 6
     * End: 8
     */
    private void extractcardVerificationResults() {
        // Extract length of CVR in hexadecimal format and convert to decimal.
        int cvrLength = Integer.parseInt(issuerApplicationData.substring(6, 8), 16);
        int cvrEndOffset = 8 + (cvrLength * 2);
        cardVerificationResults = issuerApplicationData.substring(6, cvrEndOffset);
    }
    /**
     * Method for extracting Issuer Discretionary Data.
     * Start: 8
     * End: 8 + Length of CVR * 2
     */
    private void extractIssuerDiscretionaryData() {
        // Extract length of CVR in hexadecimal format and convert to decimal.
        int cvrLength = Integer.parseInt(issuerApplicationData.substring(6, 8), 16);
        // Double CVR length, since the length is in bytes and each byte takes 2 hex characters to represent.
        // Add the doubled CVR length to the starting offset to get ending offset.
        int cvrEndOffset = 8 + (cvrLength * 2);
        issuerDiscretionaryData = issuerApplicationData.substring(cvrEndOffset);
    }
    /**
     * Override method for the object's default toString method.
     * @return String representing object's attribute values.
     */
    @Override
    public String toString() {
        return "{" +
                "issuerApplicationData='" + issuerApplicationData + '\'' +
                "lengthIndicator='" + lengthIndicator + '\'' +
                "derivationKeyIndex='" + derivationKeyIndex + '\'' +
                "cryptogramVersionNumber='" + cryptogramVersionNumber + '\'' +
                "cardVerificationResults='" + cardVerificationResults + '\'' +
                "issuerDiscretionaryData='" + issuerDiscretionaryData + '\'' +
                '}';
    }
    /**
     * Method for logging the input data and output data for the VisaIADParser function, when the debug log level is enabled.
     */
    private void debugLog(){
        if (log.isDebugEnabled()) {
            log.debug(" Debug log : {}", this);
        }
    }

}