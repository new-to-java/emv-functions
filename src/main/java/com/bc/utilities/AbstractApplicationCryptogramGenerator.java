package com.bc.utilities;

import com.bc.application.domain.CryptogramRequest;
import com.bc.application.enumeration.CryptogramVersionNumber;
import com.bc.application.enumeration.PaymentScheme;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Class defining methods for generating Payment Scheme specific Application Cryptogram (ARQC) and Response Cryptogram (ARPC).
 * Note: ARPC derivation implementation is pending.
 */
@Slf4j
public abstract class AbstractApplicationCryptogramGenerator
        implements LoggerUtility {
    /**
     * Driver method for generating Application Cryptogram based on Cryptogram Version Number (CVN),
     * based on Payment Scheme and will call CVN specific methods to generate the Application Cryptogram.
     * @return Application Cryptogram (ARQC).
     */
    public String generateApplicationCryptogram(CryptogramRequest cryptogramRequest,
                                                          String sessionKey,
                                                          CryptogramVersionNumber cryptogramVersionNumber,
                                                          String cardVerificationResults,
                                                          PaymentScheme paymentScheme){
        // Build Application Cryptogram transaction data
        String transactionData = buildTransactionData(cryptogramRequest,
                cryptogramVersionNumber,
                cardVerificationResults
        );
        logDebug(log,
                "{} Cryptogram Transaction data generated: {}",
                paymentScheme.toString(),
                transactionData
        );
        // PAD transaction data based on CVN
        transactionData = isoPadTransactionData(transactionData,
                cryptogramVersionNumber
        );
        logDebug(log,
                "{} transaction data with ISO 97971 padding: {}",
                paymentScheme.toString(),
                transactionData);
        return generateArqc(transactionData, sessionKey);
    }

    /**
     * Perform transaction data padding based on ISO 97971 padding method based on payment scheme.
     * or Method2 padding.
     *
     * @param cryptogramVersionNumber Cryptogram Version Number.
     * @param transactionData         Transaction data to be padded.
     * @return Padded transaction data.
     */
    protected abstract String isoPadTransactionData(String transactionData,
                                                   CryptogramVersionNumber cryptogramVersionNumber);
    /**
     * Generate Payment Scheme specific transaction data for Application Cryptogram generation.
     * @param cryptogramRequest Application cryptogram generation request received.
     * @param cryptogramVersionNumber Cryptogram Version Number.
     * @param cardVerificationResults Card Verification Results.
     * @return Formatted Payment Scheme specific transaction data generation for generating Application Cryptogram.
     */
    private String buildTransactionData(CryptogramRequest cryptogramRequest,
                                                  CryptogramVersionNumber cryptogramVersionNumber,
                                                  String cardVerificationResults){
        StringBuilder transactionDataBuilder = new StringBuilder();
        // Pad and build transaction data
        //  1. Amount authorised                - Length: 12 characters
        transactionDataBuilder.append(getAndFormatAmount(cryptogramRequest.getAmountAuthorised()));
        //  2. Amount Other                     - Length: 12 characters
        transactionDataBuilder.append(getAndFormatAmount(cryptogramRequest.getAmountOther()));
        //  3. Terminal Country Code            - Length: 4 characters
        transactionDataBuilder.append(getAndFormatCountryCode(cryptogramRequest.getTerminalCountryCode()));
        //  4. Terminal Verification Results    - Length: 10 characters
        transactionDataBuilder.append(cryptogramRequest.getTerminalVerificationResults());
        //  5. Transaction Currency Code        - Length: 4 characters
        transactionDataBuilder.append(getAndFormatCurrencyCode(cryptogramRequest.getTransactionCurrencyCode()));
        //  6. Transaction Date (YYMMDD format) - Length: 6 characters
        transactionDataBuilder.append(getAndFormatTransactionDateToYYMMDD(cryptogramRequest.getTransactionDate()));
        //  7. Transaction Type                 - Length: 2 characters
        transactionDataBuilder.append(cryptogramRequest.getTransactionType());
        //  8. Unpredictable Number             - Length: 8 characters
        transactionDataBuilder.append(cryptogramRequest.getUnpredictableNumber());
        //  9. Application Interchange Profile  - Length: 4 characters
        transactionDataBuilder.append(cryptogramRequest.getApplicationInterchangeProfile());
        // 10. Application Transaction Counter  - Length: 4 characters
        transactionDataBuilder.append(getAndFormatApplicationTransactionCounter(cryptogramRequest.getApplicationTransactionCounter()));
        // 11. CVR  or IAD (Based on Payment Scheme and CVN) - Length 8 or 12 characters for CVR,
        // or Length between 14 and 64 characters for IAD
        return appendFinalDataElementToTransactionData(transactionDataBuilder,
                cryptogramVersionNumber,
                cardVerificationResults,
                cryptogramRequest.getIssuerApplicationData()).toString();
    }
    /**
     * Pad transaction data for Application Cryptogram generation based on payment scheme.
     * @param transactionDataBuilder Transaction data for cryptogram generation.
     * @param cryptogramVersionNumber Cryptogram Version Number from Issuer Application Data.
     * @param cardVerificationResults Card Verification Results from Issuer Application Data.
     * @param issuerApplicationData Issuer Application Data.
     * @return Padded transaction data for Application Cryptogram generation.
     */
    protected abstract StringBuilder appendFinalDataElementToTransactionData(StringBuilder transactionDataBuilder,
                                                                   CryptogramVersionNumber cryptogramVersionNumber,
                                                                   String cardVerificationResults,
                                                                   String issuerApplicationData);
    /**
     * Format amount and ensure that they are 12 characters long.
     * @param amount Amount Authorised or Amount Other values received from input.
     * @return Left padded amount with 12 character length.
     */
    private String getAndFormatAmount(String amount){
        return Padding.padString(amount,
                "0",
                12,
                true
        );
    }
    /**
     * Format country code and ensure that it is 4 characters long.
     * @param countryCode Terminal country code value received from input.
     * @return Left padded country code with 4 character length.
     */
    private String getAndFormatCountryCode(String countryCode){
        return Padding.padString(countryCode,
                "0",
                4,
                true
        );
    }
    /**
     * Format currency code and ensure that it is 4 characters long.
     * @param currencyCode Transaction currency code value received from input.
     * @return Left padded currency code with 4 character length.
     */
    private String getAndFormatCurrencyCode(String currencyCode){
        return Padding.padString(currencyCode,
                "0",
                4,
                true
        );
    }
    /**
     * Format transaction date from ISO Format (YYYY-MM-DD) to YYMMDD format.
     * @param transactionDate Transaction date value received from input.
     * @return Transaction date in YYMMDD format.
     */
    private String getAndFormatTransactionDateToYYMMDD(String transactionDate){
        // Parse the YYYY-MM-DD date to YYMMDD format date
        return (transactionDate.substring(2, 4) +  // Extract 2 Year
                transactionDate.substring(5, 7) + // Extract Month
                transactionDate.substring(8, 10) // Extract Date
        );
    }
    /**
     * Format Application Transaction Counter (ATC) and ensure that it is 4 characters long.
     * @param applicationTransactionCounter ATC value received from input.
     * @return Left padded ATC with 4 character length.
     */
    private String getAndFormatApplicationTransactionCounter(String applicationTransactionCounter){
        return Padding.padString(applicationTransactionCounter,
                "0",
                4,
                true
        );
    }
    /**
     * Generate Application Cryptogram by segmenting the formatted transaction data into 8 byte blocks or 16 hexadecimal characters each.
     * @param formattedTransactionData Transaction data padded and formatted based on CVN.
     * @param sessionKey Session Key to be used in Application Cryptogram generation.
     * @return Generated Application Cryptogram.
     */
    private String generateArqc(String formattedTransactionData,
                                String sessionKey) {
        List<String> splitArqcData = splitTransactionData(formattedTransactionData);
        String sessionKeyA = sessionKey
                .substring(0,
                        16
                );
        String sessionKeyB = sessionKey
                .substring(16,
                        32
                );
        int loopCounter = 0;
        String encryptedData = null;
        for (String arqcData : splitArqcData) {
            // Skip XOR operation for first segment of ARQC data.
            if (loopCounter == 0) {
                encryptedData = tripleDESEncrypt(arqcData,
                        sessionKeyA
                );
            } else { // Perform XOR operation with previously encrypted data segment for all remaining segments, prior to doing encryption.
                encryptedData = performXor(encryptedData,
                        arqcData
                );
                encryptedData = tripleDESEncrypt(encryptedData,
                        sessionKeyA
                );
            }
            loopCounter += 1;
        }
        // All data segments have been processed, now decrypt the encryptedData using sessionKeyB
        encryptedData = tripleDESDecrypt(encryptedData,
                sessionKeyB
        );
        // Encrypt the SessionKeyB decrypted data using SessionKeyA to generate the ARQC.
        encryptedData = tripleDESEncrypt(encryptedData,
                sessionKeyA
        );
        logInfo(log,
                "ARQC generated: {}.",
                encryptedData
        );
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
            splitTransactionData.add(formattedTransactionData
                    .substring(parsedData,
                            parsedData + 16
                    )
            );
            parsedData = parsedData + 16;
        } while(parsedData < stringLength);
        logDebug(log,
                "Split transaction data: {}.",
                splitTransactionData
        );
        return splitTransactionData;
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
     * Perform Triple DES Decryption.
     * @param inputData Input data to be decrypted.
     * @param inputKey Triple DES Key to decrypt the data.
     * @return Decrypted data String.
     */
    private String tripleDESDecrypt(String inputData,
                                    String inputKey) {
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
    private String performXor(String leftOperand,
                              String rightOperand) {
        Xor xor = new Xor(leftOperand,
                rightOperand
        );
        return xor.doXor();
    }
}