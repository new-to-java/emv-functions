package com.bc.utilities;

import com.bc.application.enumeration.CryptogramVersionNumber;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static com.bc.model.pattern.CommonPattern.IS_VALID_IAD_FORMAT;

/**
 * Class defining methods for parsing a Visa specific IAD and splitting the IAD into the following components.
 * - Length Indicator - Byte 1 - Set to "06" for Format 0/1/3 IAD and set to "1F" for Format 2 IAD.
 * - Visa Discretionary Data:
 *      - Derivation Key index (DKI) - Byte 2.
 *      - Cryptogram Version Number (CVN) - Byte 3 - In hexadecimal format.
 *      - Card Verification Results (CVR) -
 * - Issuer Discretionary Data Length (Optional)
 * - Issuer Discretionary Data Length (Optional)
 */
@Getter
@Setter
@Slf4j
public class VisaIADParser {
    @NotEmpty
    @Pattern(regexp = IS_VALID_IAD_FORMAT)
    private String issuerApplicationData;
    @Setter(AccessLevel.NONE)
    private String lengthIndicator;
    @Setter(AccessLevel.NONE)
    private String derivationKeyIndex;
    @Setter(AccessLevel.NONE)
    private CryptogramVersionNumber cryptogramVersionNumber;
    @Setter(AccessLevel.NONE)
    private String cardVerificationResults;
    @Setter(AccessLevel.NONE)
    private String issuerDiscretionaryDataLength;
    @Setter(AccessLevel.NONE)
    private String issuerDiscretionaryData;
    @Setter(AccessLevel.NONE)
    private String issuerApplicationDataFormat;
    /**
     * Constructor with only Issuer Application Data (IAD).
     */
    public VisaIADParser(String issuerApplicationData){
        this.issuerApplicationData = issuerApplicationData;
        this.lengthIndicator = null;
        this.derivationKeyIndex = null;
        this.cryptogramVersionNumber = null;
        this.cardVerificationResults = null;
        this.issuerDiscretionaryData = null;
        this.issuerApplicationDataFormat = null;
    }
    public VisaIADParser parseIad(){
        // Check IAD total length
        iadLengthCheck();
        // Call sub methods to parse each component of the IAD.
        extractVisaDiscretionaryDataLength();
        extractDerivationKeyIndex();
        extractCryptogramVersionNumber();
        extractcardVerificationResults();
        extractIssuerDiscretionaryDataLength();
        // Call logger
        debugLog();
        // Return the object
        return this;

    }

    /**
     * Check and ensure that the IAD is formed of even number of characters, since the IAD is in hexadecimal format,
     * if not raise a runtime exception and terminate.
     */
    private void iadLengthCheck(){
        if ((issuerApplicationData.length() % 2) != 0){
            log.debug(this.getClass() + " - IAD received in input   '{}'.", issuerApplicationData);
            log.debug(this.getClass() + " - Visa IAD total length must be a multiple of 2, actual '{}'.", issuerApplicationData.length());
            throw new RuntimeException("Visa IAD total length must be a multiple of 2, received " + issuerApplicationData.length() + "!");
        }
    }

    /**
     * Method for extracting Visa Discretionary Data Length.
     * Notes:
     *      - Must be "06" for Format 0/1/3 IAD.
     *      - Must be "1F" for Format 2 IAD.
     * Start: 0
     * End: 2
     */
    private void extractVisaDiscretionaryDataLength() {
        final String IAD_LENGTH_06 = "06";
        final String IAD_LENGTH_1F = "1F";
        final String IAD_FORMAT_013 = "0/1/3";
        final String IAD_FORMAT_2 = "2";
        // Check VDD length
        lengthIndicator = issuerApplicationData.substring(0, 2);
        if (lengthIndicator.equalsIgnoreCase(IAD_LENGTH_06)){
            issuerApplicationDataFormat = IAD_FORMAT_013;
        } else if (lengthIndicator.equalsIgnoreCase(IAD_LENGTH_1F)) {
            issuerApplicationDataFormat = IAD_FORMAT_2;
        } else {
            log.debug(this.getClass() + " - IAD received in input   '{}'.", issuerApplicationData);
            log.debug(this.getClass() + " - Invalid Visa Discretionary Data length indicator '{}'.", lengthIndicator);
            throw new RuntimeException("Visa Discretionary Data length indicator invalid, must be '06' for Format 0/1/3 or '1F' for Format 2, but received " + lengthIndicator + "!");
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
        String cvn = String.valueOf(Integer.parseInt(issuerApplicationData.substring(4, 6), 16));
        switch (cvn){
            case "10":
                cryptogramVersionNumber = CryptogramVersionNumber.CVN10;
                break;
            case "18":
                cryptogramVersionNumber = CryptogramVersionNumber.CVN18;
                break;
            case "22":
                cryptogramVersionNumber = CryptogramVersionNumber.CVN22;
        }
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
     * Method for extracting Issuer Discretionary Data length and Issuer Discretionary Data if present.
     * Issuer Discretionary Data Length:
     *      Start: 14
     *      End: 16
     * Issuer Discretionary Data
     *      Start: 16
     */
    private void extractIssuerDiscretionaryDataLength() {
        // Extract length of IDD in hexadecimal format and convert to decimal.
        if (issuerApplicationData.length() > 14){
            int cvrLength = Integer.parseInt(issuerApplicationData.substring(6, 8), 16);
            issuerDiscretionaryDataLength = issuerApplicationData.substring(14, 16);
            // Convert Issuer Discretionary Data length from Hexadecimal to Decimal
            int iddLengthExpected = Integer.parseInt(issuerApplicationData.substring(14, 16), 16);
            issuerDiscretionaryData = issuerApplicationData.substring(16);
            // Halve the length of the actual Issuer Discretionary Data to get length in bytes and compare against expected length from IAD.
            if ((issuerDiscretionaryData.length() / 2) != iddLengthExpected){
                log.warn(this.getClass() + " - Invalid Visa IAD received in input   '{}'.", issuerApplicationData);
                log.warn(this.getClass() + " - Issuer Discretionary Data (IDD) does not match the length of IDD specified in IAD. Length expected for IDD as per IAD in hexadecimal: '{}',  Decimal: {}, actual length {}."
                        , issuerDiscretionaryDataLength, iddLengthExpected, issuerDiscretionaryData.length());
            }
        }

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