package com.bc.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Core domain class defining attributes for Application Cryptogram generation request.
 */

@Getter
@Setter
public class ApplicationCryptogramRequest {

    private String pan;
    private String panSequenceNumber;
    private String cryptogramMasterKey;
    private String amountAuthorised;
    private String amountOther;
    private String terminalCountryCode;
    private String terminalVerificationResults;
    private String transactionCurrencyCode;
    private String transactionDate;
    private String transactionType;
    private String unpredictableNumber;
    private String applicationInterchangeProfile;
    private String applicationTransactionCounter;
    private String issuerApplicationData;

}
