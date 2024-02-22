package com.bc.utilities;

import com.bc.application.enumeration.CryptogramVersionNumber;
import lombok.extern.slf4j.Slf4j;

/**
 * Class defining methods for generating Payment Scheme specific Application Cryptogram (ARQC) and Response Cryptogram (ARPC).
 * Note: ARPC derivation implementation is pending.
 */
@Slf4j
public class MastercardApplicationCryptogramGenerator
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
            case CVN14:
            case CVN16:
            case CVN20:
                return transactionDataBuilder.append(cardVerificationResults);
            case CVN17: // Offline counter data must also be appended to compute ARQC, this is pending implementation
            case CVN21: // Offline counter data must also be appended to compute ARQC, this is pending implementation
                throw new IllegalStateException(this.getClass().getName() + " --> CVN: " + cryptogramVersionNumber +
                        " is currently not supported. Only CVNs: \"10\", \"14\", \"16\", and \"20\" are supported."
                );
            default:
                throw new IllegalStateException(this.getClass().getName() + " --> Unexpected value for CVN. " +
                        "Expected \"10\", \"14\", \"16\", \"17\", \"20\", or \"21\" but received " + cryptogramVersionNumber + "."
                );
        }
    }
    /**
     * Pad Transaction data based on ISO 97971 Method2 padding for Mastercard payment scheme.
     * @param transactionData         Transaction data to be padded.
     * @param cryptogramVersionNumber Cryptogram Version Number.
     * @return Padded transaction data.
     */
    @Override
    protected String isoPadTransactionData(String transactionData,
                                           CryptogramVersionNumber cryptogramVersionNumber) {
        return  ISOIEC97971Padding.performIsoIec97971Method2Padding(transactionData);
    }
}