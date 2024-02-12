package com.bc.utilities;

import com.bc.application.domain.CryptogramRequest;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * Class defining methods for generating a Visa Payment Scheme Application Cryptogram (ARQC) and Response Cryptogram (ARPC).
 * Note: ARPC derivation implementation is pending.
 */
@Slf4j
public class VisaApplicationCryptogramGenerator {

    /**
     * Driver method for generating Application Cryptogram based on Cryptogram Version Number (CVN) for Visa
     * and will call CVN specific methods to generate the Application Cryptogram.
     * @return Application Cryptogram (ARQC).
     */
    public String getVisaApplicationCryptogram(CryptogramRequest cryptogramRequest, String sessionKey){
        VisaIADParser visaIADParser = new VisaIADParser(cryptogramRequest.getIssuerApplicationData()).parseIad();
        String transactionData = buildVisaTransactionData(cryptogramRequest, visaIADParser);
        if (visaIADParser.getCryptogramVersionNumber().isCVN10()){
            transactionData = ISOIEC97971Padding.performIsoIec97971Method1Padding(transactionData);
        } else {
            transactionData = ISOIEC97971Padding.performIsoIec97971Method2Padding(transactionData);
        }
        return generateArqc(transactionData, sessionKey);
    }
    /**
     * Method to call the Visa IAD Parser and return a VisaIADParser object with the parsed IAD.
     * @param issuerApplicationData Visa Issuer Application Data to be parsed.
     * @return Parsed Visa IAD.
     */
    private VisaIADParser getParsedVisaIad(String issuerApplicationData){
        VisaIADParser visaIADParser = new VisaIADParser(issuerApplicationData);
        visaIADParser.setIssuerApplicationData(issuerApplicationData);
        return visaIADParser.parseIad();
    }
    /**
     * Generate Visa transaction data for Application Cryptogram generation.
     * @param cryptogramRequest Application cryptogram generation request received.
     * @param visaIADParser Parsed Visa IAD data.
     * @return Non ISO/IEC 9797 formatted Visa transaction data generation for generating Application Cryptogram.
     */
    private String buildVisaTransactionData(CryptogramRequest cryptogramRequest, VisaIADParser visaIADParser){
        StringBuilder transactionData = new StringBuilder();
        // Pad and build transaction data
        //  1. Amount authorised                - Length: 12 characters
        transactionData.append(getAndFormatAmount(cryptogramRequest.getAmountAuthorised()));
        //  2. Amount Other                     - Length: 12 characters
        transactionData.append(getAndFormatAmount(cryptogramRequest.getAmountOther()));
        //  3. Terminal Country Code            - Length: 4 characters
        transactionData.append(getAndFormatCountryCode(cryptogramRequest.getTerminalCountryCode()));
        //  4. Terminal Verification Results    - Length: 10 characters
        transactionData.append(cryptogramRequest.getTerminalVerificationResults());
        //  5. Transaction Currency Code        - Length: 4 characters
        transactionData.append(getAndFormatCurrencyCode(cryptogramRequest.getTransactionCurrencyCode()));
        //  6. Transaction Date (YYMMDD format) - Length: 6 characters
        transactionData.append(getAndFormatTransactionDateToYYMMDD(cryptogramRequest.getTransactionDate()));
        //  7. Transaction Type                 - Length: 2 characters
        transactionData.append(cryptogramRequest.getTransactionType());
        //  8. Unpredictable Number             - Length: 8 characters
        transactionData.append(cryptogramRequest.getUnpredictableNumber());
        //  9. Application Interchange Profile  - Length: 4 characters
        transactionData.append(cryptogramRequest.getApplicationInterchangeProfile());
        // 10. Application Transaction Counter  - Length: 4 characters
        transactionData.append(getAndFormatApplicationTransactionCounter(cryptogramRequest.getApplicationTransactionCounter()));
        // 11. CVR  or IAD (Based on CVN)       - Length 8 characters or Length between 14 and 64 characters
        switch (visaIADParser.getCryptogramVersionNumber()){
            case CVN10: // For CVN 10 Visa cards use 4 byte CVR
                log.debug("CVN 10 Detected!");
                transactionData.append(visaIADParser.getCardVerificationResults());
                break;
            case CVN18: // For CVN 18 Visa cards use 14 to 64 byte IAD as is from the request
            case CVN22: // For CVN 22 Visa cards use 14 to 64 byte IAD as is from the request
                log.debug("CVN 18/CVN 22 Detected!");
                transactionData.append(cryptogramRequest.getIssuerApplicationData());
                break;
        }
        return transactionData.toString();

    }
    /**
     * Format amount and ensure that they are 12 characters long.
     * @param amount Amount Authorised or Amount Other values received from input.
     * @return Left padded amount with 12 character length.
     */
    private String getAndFormatAmount(String amount){
        return Padding.padString(amount, "0", 12, true);
    }
    /**
     * Format country code and ensure that it is 4 characters long.
     * @param countryCode Terminal country code value received from input.
     * @return Left padded country code with 4 character length.
     */
    private String getAndFormatCountryCode(String countryCode){
        return Padding.padString(countryCode, "0", 4, true);
    }
    /**
     * Format currency code and ensure that it is 4 characters long.
     * @param currencyCode Transaction currency code value received from input.
     * @return Left padded currency code with 4 character length.
     */
    private String getAndFormatCurrencyCode(String currencyCode){
        return Padding.padString(currencyCode, "0", 4, true);
    }
    /**
     * Format transaction date from ISO Format (YYYY-MM-DD) to YYMMDD format.
     * @param transactionDate Transaction date value received from input.
     * @return Transaction date in YYMMDD format.
     */
    private String getAndFormatTransactionDateToYYMMDD(String transactionDate){
        // Parse the YYYY-MM-DD date to YYMMDD format date
        return transactionDate.substring(2, 4) +  // Extract 2 Year
                transactionDate.substring(5, 7) + // Extract Month
                transactionDate.substring(8, 10); // Extract Date
    }
    /**
     * Format Application Transaction Counter (ATC) and ensure that it is 4 characters long.
     * @param applicationTransactionCounter ATC value received from input.
     * @return Left padded ATC with 4 character length.
     */
    private String getAndFormatApplicationTransactionCounter(String applicationTransactionCounter){
        return Padding.padString(applicationTransactionCounter, "0", 4, true);
    }
    /**
     * Generate Application Cryptogram by segmenting the formatted transaction data into 8 byte blocks or 16 hexadecimal characters each.
     * @param formattedTransactionData Transaction data padded and formatted based on CVN.
     * @param sessionKey Session Key to be used in Application Cryptogram generation.
     * @return Generated Application Cryptogram.
     */
    private String generateArqc(String formattedTransactionData, String sessionKey) {
        List<String> splitArqcData = splitTransactionData(formattedTransactionData);
        String sessionKeyA = sessionKey.substring(0, 16);
        String sessionKeyB = sessionKey.substring(16, 32);
        log.debug(this.getClass() + " - Session Key A: " + sessionKeyA);
        log.debug(this.getClass() + " - Session Key B: " + sessionKeyB);
        int loopCounter = 0;
        String encryptedData = null;
        for (String arqcData : splitArqcData) {
            // Skip XOR operation for first segment of ARQC data.
            if (loopCounter == 0) {
                encryptedData = tripleDESEncrypt(arqcData, sessionKeyA);
            } else { // Perform XOR operation with previously encrypted data segment for all remaining segments, prior to doing encryption.
                encryptedData = performXor(encryptedData, arqcData);
                encryptedData = tripleDESEncrypt(encryptedData, sessionKeyA);
            }
            loopCounter += 1;
        }
        // All data segments have been processed, now decrypt the encryptedData using sessionKeyB
        encryptedData = tripleDESDecrypt(encryptedData, sessionKeyB);
        // Encrypt the SessionKeyB decrypted data using SessionKeyA to generate the ARQC.
        encryptedData = tripleDESEncrypt(encryptedData, sessionKeyA);
        log.info(this.getClass() + " - ARQC Generated :" + encryptedData);

        return encryptedData;

    }
    /**
     * Split transaction data into 8 byte blocks/16 hexadecimal characters each.
     * @param formattedTransactionData ISO/IEC-9797 formatted transaction data.
     * @return Transaction data split into 8 byte blocks/16 hexadecimal characters.
     */
    private List<String> splitTransactionData(String formattedTransactionData){
        List<String> splitTransactionData = new ArrayList<>();
        int stringLength = formattedTransactionData.length();
        int parsedData = 0;
        do {
            splitTransactionData.add(formattedTransactionData.substring(parsedData, parsedData + 16));
            parsedData = parsedData + 16;
        } while(parsedData < stringLength);

        return splitTransactionData;

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
     * Perform Triple DES Decryption.
     * @param inputData Input data to be decrypted.
     * @param inputKey Triple DES Key to decrypt the data.
     * @return Decrypted data String.
     */
    private String tripleDESDecrypt(String inputData, String inputKey) {

        TripleDES tripleDES = new TripleDES();
        tripleDES.setInputData(inputData);
        tripleDES.setKey(inputKey);
        return tripleDES.decrypt();

    }
    /**
     * Perform Xor operation on transaction data.
     * @param leftOperand Left operand for Xor operation.
     * @param rightOperand Right operand for Xor operation.
     * @return Xor'ed data string.
     */
    private String performXor(String leftOperand, String rightOperand) {

        Xor xor = new Xor(leftOperand, rightOperand);
        return xor.doXor();

    }

}