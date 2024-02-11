package com.bc.application.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Core domain class defining attributes for Application Cryptogram.
 */

@Getter
@Setter
public class ApplicationCryptogramRequest {

    private String pan;
    private String panSequenceNumber;
    private String issuerMasterKey;
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

    /**
     * Override method for the object's default toString method.
     * @return String representing object's attribute values.
     */
    @Override
    public String toString() {
        return "{" +
                "pan='" + pan + '\'' +
                ", panSequenceNumber='" + panSequenceNumber + '\'' +
                ", cryptogramMasterKey='" + issuerMasterKey + '\'' +
                ", amountAuthorised='" + amountAuthorised + '\'' +
                ", amountOther='" + amountOther + '\'' +
                ", terminalCountryCode='" + terminalCountryCode + '\'' +
                ", terminalVerificationResults='" + terminalVerificationResults + '\'' +
                ", transactionCurrencyCode='" + transactionCurrencyCode + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", unpredictableNumber='" + unpredictableNumber + '\'' +
                ", applicationInterchangeProfile='" + applicationInterchangeProfile + '\'' +
                ", applicationTransactionCounter='" + applicationTransactionCounter + '\'' +
                ", issuerApplicationData='" + issuerApplicationData + '\'' +
                '}';
    }

}
