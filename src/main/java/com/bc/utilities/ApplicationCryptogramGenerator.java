package com.bc.utilities;

import com.bc.domain.ApplicationCryptogramRequest;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class defining methods for generating an Application Cryptogram (ARQC) for Visa and Mastercard Payment Schemes.
 */
@Slf4j
public class ApplicationCryptogramGenerator {

    /**
     * Driver method for generating Application Cryptogram based on Cryptogram Version Number (CVN) for Visa
     * and will call CVN specific methods to generate the Application Cryptogram.
     * @return Application Cryptogram (ARQC).
     */
    public String getVisaApplicationCryptogram(ApplicationCryptogramRequest applicationCryptogramRequest, String sessionKey){
        VisaIADParser visaIADParser = getParsedVisaIAD(applicationCryptogramRequest.getIssuerApplicationData());
        String transactionData = buildVisaTransactionData(applicationCryptogramRequest, visaIADParser);
        if (Objects.equals(visaIADParser.getCryptogramVersionNumber(), "10")){
            transactionData = padTransactionDataISO_IEC_9797_1(transactionData);
        } else {
            transactionData = padTransactionDataISO_IEC_9797_2(transactionData);
        }
        return generateArqc(transactionData, sessionKey);

    }
    /**
     * Method to extract and parse Issuer Application Data (IAD).
     * @param issuerApplicationData IAD from request.
     * @return Parsed IAD object.
     */
    private VisaIADParser getParsedVisaIAD(String issuerApplicationData) {
        VisaIADParser visaIADParser = new VisaIADParser();
        visaIADParser.setIssuerApplicationData(issuerApplicationData);
        visaIADParser.parseIad();
        return visaIADParser;
    }
    /**
     * Generate Visa transaction data for Application Cryptogram generation.
     * @param applicationCryptogramRequest Application cryptogram generation request received.
     * @param visaIADParser Parsed Visa IAD data.
     * @return Non ISO/IEC 9797 formatted Visa transaction data generation for generating Application Cryptogram.
     */
    private String buildVisaTransactionData(ApplicationCryptogramRequest applicationCryptogramRequest, VisaIADParser visaIADParser){
        StringBuilder transactionData = new StringBuilder();
        // Pad and build transaction data
        //  1. Amount authorised                - Length: 12 characters
        transactionData.append(getAndFormatAmount(applicationCryptogramRequest.getAmountAuthorised()));
        //  2. Amount Other                     - Length: 12 characters
        transactionData.append(getAndFormatAmount(applicationCryptogramRequest.getAmountOther()));
        //  3. Terminal Country Code            - Length: 4 characters
        transactionData.append(getAndFormatCountryCode(applicationCryptogramRequest.getTerminalCountryCode()));
        //  4. Terminal Verification Results    - Length: 10 characters
        transactionData.append(applicationCryptogramRequest.getTerminalVerificationResults());
        //  5. Transaction Currency Code        - Length: 4 characters
        transactionData.append(getAndFormatCurrencyCode(applicationCryptogramRequest.getTransactionCurrencyCode()));
        //  6. Transaction Date (YYMMDD format) - Length: 6 characters
        transactionData.append(getAndFormatTransactionDateToYYMMDD(applicationCryptogramRequest.getTransactionDate()));
        //  7. Transaction Type                 - Length: 2 characters
        transactionData.append(applicationCryptogramRequest.getTransactionType());
        //  8. Unpredictable Number             - Length: 8 characters
        transactionData.append(applicationCryptogramRequest.getUnpredictableNumber());
        //  9. Application Interchange Profile  - Length: 4 characters
        transactionData.append(applicationCryptogramRequest.getApplicationInterchangeProfile());
        // 10. Application Transaction Counter  - Length: 4 characters
        transactionData.append(applicationCryptogramRequest.getApplicationTransactionCounter());
        // 11. CVR  or IAD (Based on CVN)       - Length 8 characters or Length between 14 and 64 characters
        switch (visaIADParser.getCryptogramVersionNumber()){
            case "10": // For CVN 10 Visa cards use 4 byte CVR
                log.info("CVN 10 Detected!");
                transactionData.append(visaIADParser.getCardVerificationResults());
                break;
            case "18": // For CVN 18 Visa cards use 14 to 64 byte IAD as is from the request
            case "22": // For CVN 22 Visa cards use 14 to 64 byte IAD as is from the request
                log.info("CVN 18/CVN 22 Detected!");
                transactionData.append(applicationCryptogramRequest.getIssuerApplicationData());
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
     * Verify transaction data length and ensure it is a multiple of 16, else pad with "0" char
     * to make the length a multiple of 16.
     * @param transactionData Transaction data string.
     * @return ISO/IEC-9797-1 formatted transaction data string.
     */
    private String padTransactionDataISO_IEC_9797_1(String transactionData){
        int transactionDataLength = transactionData.length();
        int expectedTransactionDataLength = ((int) Math.ceil((float) transactionDataLength / 16) * 16);
        // Check if transaction data is multiple of 16, else pad with x"0" chars till the length is a multiple of 16.
        if (transactionDataLength  == expectedTransactionDataLength){
            log.info(this.getClass() + " - 9797-1 Transaction Data                 : " + transactionData);
            log.info(this.getClass() + " - 9797-1 Transaction Data Length          : " + transactionDataLength);
            log.info(this.getClass() + " - 9797-1 Expected Transaction Data Length : " + expectedTransactionDataLength);
            return transactionData;
        } else {
            log.info(this.getClass() + " - 9797-1 Transaction Data                 : " + transactionData);
            log.info(this.getClass() + " - 9797-1 Transaction Data Length          : " + transactionDataLength);
            log.info(this.getClass() + " - 9797-1 Expected Transaction Data Length : " + expectedTransactionDataLength);
            return Padding.padString(transactionData, "0", expectedTransactionDataLength, false);
        }
    }
    /**
     * Pad transaction data with mandatory x"80" character, verify transaction data length and
     * ensure it is a multiple of 16, else pad with "0" char to make the length a multiple of 16.
     * @param transactionData Transaction data string.
     * @return ISO/IEC-9797-2 formatted transaction data string.
     */
    private String padTransactionDataISO_IEC_9797_2(String transactionData){
        transactionData = transactionData + "80";
        int transactionDataLength = transactionData.length();
        int expectedTransactionDataLength = ((int) Math.ceil((float) transactionDataLength / 16) * 16);
        // Check if transaction data is multiple of 16, else pad with x"0" chars till the length is a multiple of 16.
        if (transactionDataLength  == expectedTransactionDataLength){
            log.info(this.getClass() + " - 9797-2 Transaction Data                 : " + transactionData);
            log.info(this.getClass() + " - 9797-2 Transaction Data Length          : " + transactionDataLength);
            log.info(this.getClass() + " - 9797-2 Expected Transaction Data Length : " + expectedTransactionDataLength);
            return transactionData;
        } else {
            log.info(this.getClass() + " - 9797-2 Transaction Data                 : " + transactionData);
            log.info(this.getClass() + " - 9797-2 Transaction Data Length          : " + transactionDataLength);
            log.info(this.getClass() + " - 9797-2 Expected Transaction Data Length : " + expectedTransactionDataLength);
            return Padding.padString(transactionData, "0", expectedTransactionDataLength, false);
        }
    }

    private String generateArqc(String formattedTransactionData, String sessionKey) {
        List<String> splitArqcData = splitTransactionData(formattedTransactionData);
        String sessionKeyA = sessionKey.substring(0, 16);
        String sessionKeyB = sessionKey.substring(16, 32);
        log.info(this.getClass() + " - Session Key A: " + sessionKeyA);
        log.info(this.getClass() + " - Session Key B: " + sessionKeyB);
        int loopCounter = 0;
        String encryptedData = null;
        for (String arqcData : splitArqcData) {
            // Skip XOR operation for first segment of ARQC data.
            if (loopCounter == 0) {
                encryptedData = tripleDESEncypt(arqcData, sessionKeyA);
            } else { // Perform XOR operation with previously encrypted data segment for all remaining segments, prior to doing encryption.
                encryptedData = performXor(encryptedData, arqcData);
                encryptedData = tripleDESEncypt(encryptedData, sessionKeyA);
            }
            loopCounter += 1;
        }
        // All data segments have been processed, now decrypt the encryptedData using sessionKeyB
        encryptedData = tripleDESDecrypt(encryptedData, sessionKeyB);
        // Encrypt the SessionKeyB decrypted data using SessionKeyA to generate the ARQC.
        encryptedData = tripleDESEncypt(encryptedData, sessionKeyA);
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
    private String tripleDESEncypt(String inputData, String inputKey) {

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

        Xor xor = new Xor();
        xor.setLeftOperand(leftOperand);
        xor.setRightOperand(rightOperand);
        return xor.doXor();

    }

}