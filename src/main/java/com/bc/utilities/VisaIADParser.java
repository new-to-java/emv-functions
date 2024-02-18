package com.bc.utilities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import static com.bc.model.constants.IADStaticData.*;
/**
 * Class defining methods for parsing a Visa specific IAD and splitting the IAD into the following components.
 * - Length Indicator - Byte 1 - Set to "06" for Format 0/1/3 IAD and set to "1F" for Format 2 IAD.
 * - Visa Discretionary Data:
 *      - Format 0/1/3: Derivation Key index (DKI) or Format 2: Cryptogram Version Number (CVN)
 *      - Format 0/1/3: Cryptogram Version Number (CVN) or Format 2: Cryptogram Version Number (DKI)
 *      - Card Verification Results (CVR)
 * - This IAD also contains following optional data:
 *      - Issuer Discretionary Data Length (Only applicable for Format 0/1/3 IAD)
 *      - Issuer Discretionary Data Option ID
 *      - Issuer Discretionary Data
 */
@Getter
@Setter
@Slf4j
public class VisaIADParser
        extends AbstractSelfValidator<VisaIADParser>
        implements LoggerUtility {
    @NotEmpty
    @Pattern.List({
            @Pattern(regexp = VISA_IAD_STARTS_WITH_06_OR_1F, message = VISA_IAD_START_BYTE_ERROR),
            @Pattern(regexp = VALID_IAD_FORMAT, message = IAD_FORMAT_ERROR)
    })
    private String issuerApplicationData;
    @Setter(AccessLevel.NONE)
    private Map<String, String> parsedIssuerApplicationData = new LinkedHashMap<>();
    // Variables
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, Integer> visaIadDataNamesAndLengths = new LinkedHashMap<>();
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean iadIsVisaFormat2;
    private int lastProcessedOffset;
    /**
     * Constructor with Issuer Application Data (IAD).
     */
    public VisaIADParser(String issuerApplicationData){

        this.issuerApplicationData = issuerApplicationData;
        // Call self validate
        selfValidate();
        logInfo(log,
                "Self validated successful for object {}.",
                this
        );
        // Initialise IAD data item names and lengths based on format
        initialiseIadDataItemLengths();
        logDebug(log,
                "Initialized item lengths {}.",
                visaIadDataNamesAndLengths
        );
    }
    /**
     * Driver method for parsing a Visa Issuer Application Data element - EMV Tag 9F10
     * @return Map of parsed Visa IAD
     */
    public Map<String, String> parseIad(){
        Map<String, String> parsedIadDataItems = parseIadIntoDataItems();
        parseIddIfAvailableInIad(parsedIadDataItems);
        overlayIddOptionIdWithRightNibbleOfIddOptionId(parsedIadDataItems);
        setIadFormatFromCvn(parsedIadDataItems);
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
        iadIsVisaFormat2 = iadIsAVisaFormat2Iad();
        if (iadIsVisaFormat2){
            buildFormat2IadDataNamesAndLengths();
        } else {
            buildStandardIadDataNamesAndLengths();
        }
        logDebug(log, "Visa Format 2 IAD: {}.", iadIsVisaFormat2);
    }
    /**
     * Check if the IAD is Format 2 by verifying the starting byte is set to "1F", if yes return true, else return false.
     */
    private boolean iadIsAVisaFormat2Iad(){
        return issuerApplicationData.toUpperCase().startsWith(FORMAT_2_IAD_LENGTH);
    }
    /**
     * Initialise the Visa IAD Data Names and Lengths map for Format 0/1/3 IAD data item names and lengths.
     */
    private void buildStandardIadDataNamesAndLengths() {
        visaIadDataNamesAndLengths.put(IAD_LENGTH_NAME, IAD_LENGTH);
        visaIadDataNamesAndLengths.put(DKI_NAME, DKI_LENGTH);
        visaIadDataNamesAndLengths.put(CVN_NAME, CVN_LENGTH);
        visaIadDataNamesAndLengths.put(CVR_NAME, CVR_STANDARD_LENGTH);
        visaIadDataNamesAndLengths.put(IDD_LENGTH_NAME, IDD_LENGTH);
        visaIadDataNamesAndLengths.put(IDD_OPTION_ID, IDD_OPTION_ID_LENGTH);
        visaIadDataNamesAndLengths.put(IDD_NAME, 0);
    }
    /**
     * Initialise the Visa IAD Data Names and Lengths map for Format 2 IAD data item names and lengths.
     */
    private void buildFormat2IadDataNamesAndLengths() {
        visaIadDataNamesAndLengths.put(IAD_LENGTH_NAME, IAD_LENGTH);
        visaIadDataNamesAndLengths.put(CVN_NAME, CVN_LENGTH);
        visaIadDataNamesAndLengths.put(DKI_NAME, DKI_LENGTH);
        visaIadDataNamesAndLengths.put(CVR_NAME, CVR_FORMAT2_LENGTH);
        visaIadDataNamesAndLengths.put(IDD_OPTION_ID, IDD_OPTION_ID_LENGTH);
        visaIadDataNamesAndLengths.put(IDD_NAME, 0);
    }
    /**
     * Common parser logic for performing the initial parsing of a Visa IAD into Visa Discretionary Data (VDD) components.
     * @return Map of parsed Visa Discretionary Data items.
     */
    private Map<String, String> parseIadIntoDataItems(){
        Map<String, String> parsedIadDataItems = new LinkedHashMap<>();
        int startingOffset = 0;
        // Iterate through each Visa IAD data item, length pair and parse IAD
        for (Map.Entry<String, Integer> iadDataItem : visaIadDataNamesAndLengths.entrySet()) {
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
                startingOffset += iadDataItem.getValue();
            }
        }
        // Set last processed offset to class variable for later reference
        lastProcessedOffset = startingOffset;
        return parsedIadDataItems;
    }
    /**
     * Check if Issuer Discretionary Data (IDD) is available in IAD and parse it.
     * @param parsedIadDataItems Map of parsed IAD, containing IDD not parsed.
     */
    private void parseIddIfAvailableInIad(Map<String, String> parsedIadDataItems){
        // Check if IDD is available in IAD, extract IDD
        if (issuerApplicationData.length() > lastProcessedOffset){
            // Process Visa Format 2 IAD for extracting IDD
            if (iadIsVisaFormat2){
                String iddOptionId = parsedIadDataItems.get(IDD_OPTION_ID);
                parsedIadDataItems.put(
                        IDD_NAME,
                        iddOptionId +
                        issuerApplicationData.substring(lastProcessedOffset)
                );
                // Process Visa Format 0/1/3 IAD for extracting IDD
            } else {
                String iddLength = parsedIadDataItems.get(IDD_LENGTH_NAME);
                String iddOptionId = parsedIadDataItems.get(IDD_OPTION_ID);
                parsedIadDataItems.put(
                        IDD_NAME,
                        iddLength +
                        iddOptionId +
                        issuerApplicationData.substring(lastProcessedOffset)
                );
            }
        }
    }
    /**
     * Extract right nibble from the current value assigned to the key "IddOptionId", and overlay the value of the same key.
     * @param parsedIadDataItems Map of parsed IAD with IDD option ID.
     */
    private void overlayIddOptionIdWithRightNibbleOfIddOptionId(Map<String, String> parsedIadDataItems){
        if (parsedIadDataItems.containsKey(IDD_OPTION_ID)){
            parsedIadDataItems.put(
                    IDD_OPTION_ID,
                    String.valueOf(
                            parsedIadDataItems.get(
                                    IDD_OPTION_ID
                                    ).charAt(1)
                    )
            );
        }
    }
    /**
     * Extract left nibble from the CVN and add it as value to the key "IADFormat", and overlay the value of the same key.
     * @param parsedIadDataItems Map of parsed IAD with IAD Format value.
     */
    private void setIadFormatFromCvn(Map<String, String> parsedIadDataItems){
        if (parsedIadDataItems.containsKey(CVN_NAME)){
            parsedIadDataItems.put(
                    IAD_FORMAT_NAME,
                    String.valueOf(
                            parsedIadDataItems.get(
                                    CVN_NAME
                            ).charAt(0)
                    )
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
                    translateVisaCvnToGenericCvn(
                            cvn
                    )
            );
        }
    }
    /**
     * Translate Visa CVN to generic CVN value.
     * @param cvn CVN value from parsed IAD.
     * @return Generic CVN value.
     */
    private String translateVisaCvnToGenericCvn(String cvn){
        final String CVN_PREFIX = "CVN";
        final String NUMBER_10 = "10";
        final String NUMBER_14 = "14";
        final String NUMBER_18 = "18";
        switch (cvn.toUpperCase()){
            case "0A":
                logDebug(log, "CVN Received: {}.", cvn);
                return CVN_PREFIX + NUMBER_10;
            case "0E":
                logDebug(log, "CVN Received: {}.", cvn);
                return CVN_PREFIX + NUMBER_14;
            case "12":
                logDebug(log, "CVN Received: {}.", cvn);
                return CVN_PREFIX + NUMBER_18;
            case "22":
            case "2C":
                logDebug(log, "CVN Received: {}.", cvn);
                return CVN_PREFIX + cvn;
            default:
                return null;
        }
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
                "visaIadDataNamesAndLengths='" + visaIadDataNamesAndLengths + '\'' +
                "iadIsVisaFormat2='" + iadIsVisaFormat2 + '\'' +
                "lastProcessedOffset='" + lastProcessedOffset + '\'' +
                '}';
    }
}