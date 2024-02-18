package com.bc.utilities;

import com.bc.application.enumeration.CryptogramVersionNumber;
import lombok.extern.slf4j.Slf4j;

/**
 * Class defining methods for generating Payment Scheme specific Application Cryptogram (ARQC) and Response Cryptogram (ARPC).
 * Note: ARPC derivation implementation is pending.
 */
@Slf4j
public class VisaApplicationCryptogramGenerator
        extends AbstractApplicationCryptogramGenerator {

    /**
     * Pad the Card Verification Results as the final data element for Mastercard payment scheme transaction data
     * for Application Cryptogram generation.
     * @param transactionDataBuilder  Transaction data for cryptogram generation.
     * @param cryptogramVersionNumber Cryptogram Version Number from Issuer Application Data.
     * @param cardVerificationResults Card Verification Results from Issuer Application Data.
     * @param issuerApplicationData   Issuer Application Data.
     * @return Padded transaction data for Application Cryptogram generation.
     */
    @Override
    protected StringBuilder appendFinalDataElementToTransactionData(StringBuilder transactionDataBuilder,
                                                                    CryptogramVersionNumber cryptogramVersionNumber,
                                                                    String cardVerificationResults,
                                                                    String issuerApplicationData) {
        switch (cryptogramVersionNumber){
            case CVN10:
                return transactionDataBuilder.append(cardVerificationResults);
            case CVN14:
            case CVN18:
            case CVN22:
            case CVN2C:
                return transactionDataBuilder.append(issuerApplicationData);
        }
        return null;
    }
    /**
     * Pad Transaction data based on ISO 97971 Method1 or Method2 padding for Visa payment scheme based on
     * Cryptogram Version Number.
     * @param transactionData         Transaction data to be padded.
     * @param cryptogramVersionNumber Cryptogram Version Number.
     * @return Padded transaction data.
     */
    @Override
    protected String isoPadTransactionData(String transactionData,
                                           CryptogramVersionNumber cryptogramVersionNumber) {
        switch (cryptogramVersionNumber){
            case CVN10:
                return ISOIEC97971Padding.performIsoIec97971Method1Padding(transactionData);
            case CVN14:
            case CVN18:
            case CVN22:
            case CVN2C:
                return  ISOIEC97971Padding.performIsoIec97971Method2Padding(transactionData);
        }
        return null;
    }
}