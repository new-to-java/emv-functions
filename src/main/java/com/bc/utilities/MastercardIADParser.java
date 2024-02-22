package com.bc.utilities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.LinkedHashMap;
import java.util.Map;
import static com.bc.model.constants.IADStaticData.*;

/**
 * Class defining methods for parsing a Mastercard specific IAD and splitting the IAD into the following components.
 * - Key Derivation Index - Byte 1.
 * - Cryptogram Version Number - Byte 2.
 *      - bits 8 through 5 is always set to 0001
 *      - bit 4 = RFU
 *      - bit 3-2 = Session Key Derivation Method:
 *          - 00 - Mastercard Proprietary SKD
 *          - 01 - EMV Common SKD.
 *      - bit 1:
 *          - 0 - Counters not included in AC computation
 *          - 1 - Counters included in AC computation
 * - Card Verification Results - Byte 3-8.
 * - DAC/ICC Dynamic Number - Byte 9-10.
 * - Plaintext/encrypted counters - Byte 11-18 or 11-26.
 * - Last online ATC - Byte 19-20 or 27-28.
 */
@Getter
@Setter
@Slf4j
public class MastercardIADParser
        extends AbstractSelfValidator<MastercardIADParser>
        implements LoggerUtility {
    @NotEmpty
    @Pattern(regexp = MASTERCARD_VALID_IAD_FORMAT, message = MASTERCARD_IAD_FORMAT_ERROR)
    private String issuerApplicationData;
    @Setter(AccessLevel.NONE)
    private Map<String, String> parsedIssuerApplicationData = new LinkedHashMap<>();
    // Variables
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, Integer> mastercardIadDataNamesAndLengths = new LinkedHashMap<>();
    // Constants
    final String CVN_10 = "10";
    /**
     * Constructor with Issuer Application Data (IAD).
     */
    public MastercardIADParser(String issuerApplicationData){

        this.issuerApplicationData = issuerApplicationData;
        // Call self validate
        selfValidate();
        logDebug(log,
                "Self validation successful for object {}.",
                this
        );
        // Initialise IAD data item names and lengths based on format
        initialiseIadDataItemLengths();
        logDebug(log,
                "Initialized item lengths {}.",
                mastercardIadDataNamesAndLengths
        );
    }
    /**
     * Driver method for parsing a Mastercard Issuer Application Data element - EMV Tag 9F10
     * @return Map of parsed Mastercard IAD
     */
    public Map<String, String> parseIad(){
        Map<String, String> parsedIadDataItems = parseIadIntoDataItems();
        determineSessionKeyDerivationMethodFromCvn(parsedIadDataItems);
        updateParsedIadDataWithGenericCvn(parsedIadDataItems, parsedIadDataItems.get(CVN_NAME));
        parsedIssuerApplicationData.putAll(parsedIadDataItems);
        logDebug(log, "parsed IAD data {}.", parsedIssuerApplicationData);
        logDebug(log, "Class data {}.", this);
        return parsedIssuerApplicationData;
    }
    /**
     * Initialise the visaIadDataItemsLength tree map with the data items as keys and lengths as values.
     */
    private void initialiseIadDataItemLengths(){
        mastercardIadDataNamesAndLengths.put(DKI_NAME, MASTECARD_DKI_LENGTH);
        mastercardIadDataNamesAndLengths.put(CVN_NAME, MASTERCARD_CVN_LENGTH);
        mastercardIadDataNamesAndLengths.put(CVR_NAME, MASTERCARD_CVR_LENGTH);
        mastercardIadDataNamesAndLengths.put(MASTERCARD_DAC_ICC_NAME, MASTERCARD_DAC_ICC_LENGTH);
        if (iadContainsEncryptedCounters(issuerApplicationData)) {
            mastercardIadDataNamesAndLengths.put(MASTERCARD_COUNTERS_NAME, MASTERCARD_ENCRYPTED_COUNTERS_LENGTH);
        } else {
            mastercardIadDataNamesAndLengths.put(MASTERCARD_COUNTERS_NAME, MASTERCARD_PLAIN_TEXT_COUNTERS_LENGTH);
        }
        mastercardIadDataNamesAndLengths.put(MASTERCARD_LAST_ONLINE_ATC_NAME, MASTERCARD_LAST_ONLINE_ATC_LENGTH);
    }
    /**
     * Common parser logic for parsing of a Mastercard IAD into components.
     * @return Map of parsed Mastercard Data items.
     */
    private Map<String, String> parseIadIntoDataItems(){
        Map<String, String> parsedIadDataItems = new LinkedHashMap<>();
        boolean cvnVerified = false;
        int startingOffset = 0;
        // Iterate through each Mastercard IAD data item, length pair and parse IAD
        for (Map.Entry<String, Integer> iadDataItem : mastercardIadDataNamesAndLengths.entrySet()) {
            parseAndExtractIadDataItem(iadDataItem, startingOffset, parsedIadDataItems);
            startingOffset += iadDataItem.getValue();
            // Check if CVN is valid and update CVR length
            if (!cvnVerified){
                cvnVerified = isCvnVerified(parsedIadDataItems);
            }
        }
        return parsedIadDataItems;
    }
    /**
     * Method to check if CVN is verified or not and based on CVN update the CVR length.
     * @param parsedIadDataItems Map containing parsed IAD data items.
     * @return Always returns true, since CVN verification failure would result in exception.
     */
    private boolean isCvnVerified(Map<String, String> parsedIadDataItems) {
        if (parsedIadDataItems.containsKey(CVN_NAME)){
            String cvn = parsedIadDataItems.get(CVN_NAME);
            isValidMastercardCvn(cvn);
            updateCvn10CvrLength(cvn);
        }
        return true;
    }
    /**
     * Parse IAD and extract data items.
     * @param iadDataItem IAD data item map with name and length.
     * @param startingOffset Starting offset for substring operation.
     * @param parsedIadDataItems Map containing parsed IAD data items.
     */
    private void parseAndExtractIadDataItem(Map.Entry<String, Integer> iadDataItem, int startingOffset, Map<String, String> parsedIadDataItems) {
        if (issuerApplicationData.length() >= startingOffset
                + iadDataItem.getValue()) {
                parsedIadDataItems.put(
                        iadDataItem.getKey(),
                        issuerApplicationData.substring(
                                startingOffset,
                                startingOffset +
                                        iadDataItem.getValue()
                        )
                );

        }
    }

    /**
     * Verify and ensure that the CVN supplied in IAD is one of the supported CVNs, i.e., CVN10, CVN14, CVN16, CVN17,
     * CVN20, or CVN21, else throw error and terminate.
     * @param cvn Cryptogram Version Number from IAD
     */
    private void isValidMastercardCvn(String cvn){
        // Constants
        final String CVN_14 = "14";
        final String CVN_16 = "16";
        final String CVN_17 = "17"; // Future release, this CVN requires offline counters to be included
        final String CVN_20 = "20";
        final String CVN_21 = "21"; // Future release, this CVN requires offline counters to be included
        // Check CVN
        switch (cvn){
            case CVN_10:
            case CVN_14:
            case CVN_16:
            case CVN_20:
                return;
            case CVN_17:
            case CVN_21:
                throw new IllegalStateException(this.getClass().getName() + " --> CVN " + cvn +
                        " is currently not supported. Only CVNs: \"10\", \"14\", \"16\", and \"20\" are supported."
                );
            default:
                throw new IllegalStateException(this.getClass().getName() + " --> Unexpected value for CVN . " +
                        "expected \"10\", \"14\", \"16\", \"17\", \"20\", or \"21\" but received " + cvn.toUpperCase() + "."
                );
        }
    }

    /**
     * Reduce the length of the CVN to 8 bytes for Mastercard CVN10 IAD.
     * @param cvn CVN from IAD.
     */
    private void updateCvn10CvrLength(String cvn){
        if (cvn.equals(CVN_10)) {
            mastercardIadDataNamesAndLengths.put(CVR_NAME, MASTERCARD_CVN10_CVR_LENGTH);
        }
    }
    /**
     * Verify whether the issuer application data contains plaintext or encrypted counters.
     * @param issuerApplicationData Issuer application data to be verified.
     * @return True if encrypted counters exists, else return false.
     */
    private boolean iadContainsEncryptedCounters(String issuerApplicationData){
        return issuerApplicationData.length() > 40;
    }
    /**
     * Determine session key derivation method from CVN right nibble bits 3 and 2 and set the SKD method in parsed IAD.
     * @param parsedIadDataItems Map of parsed IAD with session key derivation method set.
     */
    private void determineSessionKeyDerivationMethodFromCvn(Map<String, String> parsedIadDataItems){
        // Static values
        final String MC_PROPRIETARY_SKD_CVN10 = "00"; // CVN 10
        final String MC_PROPRIETARY_SKD_CVNXX = "11"; // CVN 16, CVN 17
        final String EMV_CSK_SKD = "10"; // CVN 14, CVN 20
        final String MC_PROPRIETARY_SDK_NAME = "MCP_SKD";
        final String EMV_CSK_METHOD_NAME = "EMV_CSK";
        // Variables
        logDebug(log, "Parsed Mastercard IAD: {}.", parsedIadDataItems);
        String cvnRightNibble = Integer.toBinaryString(Integer.parseInt(parsedIadDataItems.get(CVN_NAME).substring(1, 2), 10));
        cvnRightNibble = Padding.padString(cvnRightNibble, "0", 4, true);
        String sessionKeyDerivationMethod = cvnRightNibble.substring(1,3);
        switch (sessionKeyDerivationMethod){
            case MC_PROPRIETARY_SKD_CVN10:
            case MC_PROPRIETARY_SKD_CVNXX:
                 parsedIadDataItems.put(MASTERCARD_SKD_METHOD_NAME, MC_PROPRIETARY_SDK_NAME);
                 break;
            case EMV_CSK_SKD:
                parsedIadDataItems.put(MASTERCARD_SKD_METHOD_NAME, EMV_CSK_METHOD_NAME);
                 break; default:
                 throw new IllegalStateException(this.getClass().getName() + " --> Unexpected value for bits 3 and 2 in CVN right nibble. " +
                         "expected \"00\", \"10\", or \"11\" but received " + sessionKeyDerivationMethod + "."
                 );
         }
    }
    /**
     * Update the Parsed IAD Map with the generic CVN value determined.
     * @param cvn CVN value extracted from IAD.
     * @param parsedIadData Parsed IAD map with Visa specific CVN value.
     */
    private void updateParsedIadDataWithGenericCvn(Map<String, String> parsedIadData,
                                                     String cvn){
        if (parsedIadData.containsKey(CVN_NAME)){
            parsedIadData.put(CVN_NAME,
                    translateMastercardCvnToGenericCvn(
                            cvn
                    )
            );
        }
    }
    /**
     * Translate Mastercard CVN to generic CVN value.
     * @param cvn CVN value from parsed IAD.
     * @return Generic CVN value.
     */
    private String translateMastercardCvnToGenericCvn(String cvn){
        final String CVN_PREFIX = "CVN";
        // Logic
        logDebug(log, "CVN Received: {}.", cvn);
        return CVN_PREFIX + cvn;
    }
    /**
     * Override method for the default toSting() method.
     * @return Class attributes converted to string.
     */
    @Override
    public String toString() {
        return "{" +
                "issuerApplicationData='" + issuerApplicationData + '\'' +
                "parsedIssuerApplicationData='" + parsedIssuerApplicationData + '\'' +
                '}';
    }
}