package com.bc.utilities;

import com.bc.application.enumeration.VisaIadElements;
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
            @Pattern(regexp = VISA_VALID_IAD_FORMAT, message = VISA_IAD_FORMAT_ERROR)
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
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int lastProcessedOffset;
    /**
     * Constructor with Issuer Application Data (IAD).
     */
    public VisaIADParser(String issuerApplicationData){

        this.issuerApplicationData = issuerApplicationData;
        // Call self validate
        selfValidate();
        iadIsVisaFormat2 = iadIsAVisaFormat2Iad();
        logDebug(log,
                "Self validation successful for object {}.",
                this
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
     * Check if the IAD is Format 2 by verifying the starting byte is set to "1F", if yes return true, else return false.
     */
    private boolean iadIsAVisaFormat2Iad(){
        return issuerApplicationData.toUpperCase().startsWith(VISA_FORMAT_2_IAD_LENGTH);
    }
    /**
     * Common parser logic for performing the initial parsing of a Visa IAD into Visa Discretionary Data (VDD) components.
     * @return Map of parsed Visa Discretionary Data items.
     */
    private Map<String, String> parseIadIntoDataItems(){
        Map<String, String> parsedIadDataItems = new LinkedHashMap<>();
        Iterator<VisaIadElements> visaIadElementsIterator =  Arrays.stream(VisaIadElements.values()).iterator();
        extractVisaIadComponents(visaIadElementsIterator, parsedIadDataItems);
        return parsedIadDataItems;
    }
    /**
     * Parse Visa IAD and extract data items.
     * @param parsedIadDataItems Map containing parsed IAD data items.
     */
    private void extractVisaIadComponents(Iterator<VisaIadElements> visaIadElementsIterator, Map<String, String> parsedIadDataItems) {

        int startingOffset = 0;
        VisaIadElements visaIadElement;

        while (visaIadElementsIterator.hasNext()){
            visaIadElement = visaIadElementsIterator.next();
            if (skipProcessingElement(visaIadElement)) {
                continue;
            }
            extractIadElement(parsedIadDataItems, startingOffset, visaIadElement);
            startingOffset += visaIadElement.getLength();
        }
        lastProcessedOffset = startingOffset;
    }
    /**
     * Depending on IAD format, skip processing elements that are not relevant for a given IAD format.
     * @param visaIadElement Next element from the enum class.
     * @return True if element is not relevant for IAD format, else return false.
     */
    private boolean skipProcessingElement(VisaIadElements visaIadElement) {
        switch (visaIadElement) {
            case DKI:
            case CVN:
            case CVR:
            case IDD_LENGTH:
                if (iadIsVisaFormat2) {
                    return true;
                }
                break;
            case DKI_FMT2:
            case CVN_FMT_2:
            case CVR_FMT_2:
                if (!iadIsVisaFormat2) {
                    return true;
                }
                break;
        }
        return false;
    }
    /**
     * Extract a specific IAD element from the IAD
     * @param parsedIadDataItems Extract a specific IAD element from the IAD.
     * @param startingOffset Starting offset to extract the next element.
     * @param visaIadElement Next element to extract.
     */
    private void extractIadElement(Map<String, String> parsedIadDataItems, int startingOffset, VisaIadElements visaIadElement) {
        if (issuerApplicationData.length() >= (startingOffset + visaIadElement.getLength())) {
            if (visaIadElement.equals(VisaIadElements.IDD)){
                parsedIadDataItems.put(visaIadElement.getLabel(),
                        issuerApplicationData.substring(startingOffset));
            } else {
                parsedIadDataItems.put(visaIadElement.getLabel(),
                        issuerApplicationData.substring(
                                startingOffset,
                                startingOffset + visaIadElement.getLength()));
            }
        }
        log.debug("Parsed IAD: " + parsedIadDataItems);
    }
    /**
     * Check if Issuer Discretionary Data (IDD) is available in IAD and parse it.
     * @param parsedIadDataItems Map of parsed IAD, containing IDD not parsed.
     */
    private void parseIddIfAvailableInIad(Map<String, String> parsedIadDataItems){
        // Check if IDD is available in IAD, extract IDD
        String idd = "";
        if (!iadIsVisaFormat2) {
            idd = parsedIadDataItems.get(VisaIadElements.IDD_LENGTH.getLabel());
        }
        idd = idd + parsedIadDataItems.get(VisaIadElements.IDD_OPTION_ID.getLabel()) +
        parsedIadDataItems.get(VisaIadElements.IDD.getLabel());
        parsedIadDataItems.put(VisaIadElements.IDD.getLabel(), idd);
    }
    /**
     * Extract right nibble from the current value assigned to the key "IddOptionId", and overlay the value of the same key.
     * @param parsedIadDataItems Map of parsed IAD with IDD option ID.
     */
    private void overlayIddOptionIdWithRightNibbleOfIddOptionId(Map<String, String> parsedIadDataItems){
        String iddOptionIdLabel = VisaIadElements.IDD_OPTION_ID.getLabel();
        if (parsedIadDataItems.containsKey(iddOptionIdLabel)){
            parsedIadDataItems.put(
                    iddOptionIdLabel,
                    String.valueOf(
                            parsedIadDataItems.get(
                                    iddOptionIdLabel
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
        if (parsedIadDataItems.containsKey(VisaIadElements.CVN.getLabel())){
            parsedIadDataItems.put(
                    VISA_IAD_FORMAT_NAME,
                    String.valueOf(
                            parsedIadDataItems.get(
                                    VisaIadElements.CVN.getLabel()
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
        // Constants
        final String CVN_PREFIX = "CVN";
        final String NUMBER_10 = "10";
        final String NUMBER_14 = "14";
        final String NUMBER_18 = "18";
        final String VISA_CVN_10 = "0A";
        final String VISA_CVN_14 = "0E";
        final String VISA_CVN_18 = "12";
        final String VISA_CVN_22 = "22";
        final String VISA_CVN_2C = "2C";
        // Logic
        logDebug(log, "CVN Received: {}.", cvn.toUpperCase());
        switch (cvn.toUpperCase()){
            case VISA_CVN_10:
                return CVN_PREFIX + NUMBER_10;
            case VISA_CVN_14:
                return CVN_PREFIX + NUMBER_14;
            case VISA_CVN_18:
                return CVN_PREFIX + NUMBER_18;
            case VISA_CVN_22:
            case VISA_CVN_2C:
                return CVN_PREFIX + cvn;
            default:
                throw new IllegalStateException(this.getClass().getName() + " --> Unexpected value for CVN . " +
                        "expected \"0A\", \"0E\", \"12\", \"22\", or \"2C\" but received " + cvn.toUpperCase() + "."
                );
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
                "iadIsVisaFormat2='" + iadIsVisaFormat2 + '\'' +
                "lastProcessedOffset='" + lastProcessedOffset + '\'' +
                '}';
    }
}