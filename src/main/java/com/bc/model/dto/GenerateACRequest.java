package com.bc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * DTO class defining REST API attributes for Application Cryptogram generation request payload.
 */
public class GenerateACRequest {

    @JsonProperty("Pan")
    public String pan;
    @JsonProperty("PanSequenceNumber")
    public String panSequenceNumber;
    @JsonProperty("IssuerMasterKey")
    public String issuerMasterKey;
    @JsonProperty("AmountAuthorised")
    public String amountAuthorised;
    @JsonProperty("AmountOther")
    public String amountOther;
    @JsonProperty("TerminalCountryCode")
    public String terminalCountryCode;
    @JsonProperty("TerminalVerificationResults")
    public String terminalVerificationResults;
    @JsonProperty("TransactionCurrencyCode")
    public String transactionCurrencyCode;
    @JsonProperty("TransactionDate")
    public String transactionDate;
    @JsonProperty("TransactionType")
    public String transactionType;
    @JsonProperty("UnpredictableNumber")
    public String unpredictableNumber;
    @JsonProperty("ApplicationInterchangeProfile")
    public String applicationInterchangeProfile;
    @JsonProperty("ApplicationTransactionCounter")
    public String applicationTransactionCounter;
    @JsonProperty("IssuerApplicationData")
    public String issuerApplicationData;

}
