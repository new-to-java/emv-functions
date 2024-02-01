package com.bc.service;

import com.bc.domain.ApplicationCryptogramRequest;
import com.bc.domain.ApplicationCryptogramResponse;
import com.bc.enumeration.CryptogramVersionNumber;
import com.bc.enumeration.KeyType;
import com.bc.utilities.DeterminePaymentScheme;
import com.bc.utilities.EMVKeyDerivator;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Core domain service hosting the methods for performing various cryptogram related functions.
 */
@ApplicationScoped
public class CryptogramFunctionsService {

    public ApplicationCryptogramResponse generateAC(ApplicationCryptogramRequest applicationCryptogramRequest){
        printApplicationCryptogramRequest(applicationCryptogramRequest);
        ApplicationCryptogramResponse applicationCryptogramResponse = new ApplicationCryptogramResponse();
        applicationCryptogramResponse.setApplicationCryptogram("AAAAAAAA");
        applicationCryptogramResponse.setApplicationCryptogramType("ARQC");
        EMVKeyDerivator emvKeyDerivator = new EMVKeyDerivator();
        emvKeyDerivator.setInputKey("F0F0F0F0F0F0F0F0");
        emvKeyDerivator.setPan("4123456789011234");
        emvKeyDerivator.setPanSequenceNumber("01");
        emvKeyDerivator.setPaymentScheme(DeterminePaymentScheme.fromPan(emvKeyDerivator.getPan()).toString());
        emvKeyDerivator.setInputKeyType(KeyType.CRYPTOGRAM_MASTER_KEY.toString());
        emvKeyDerivator.setCryptogramVersionNumber(CryptogramVersionNumber.MASTERCARD_CVN14.name());
        emvKeyDerivator.setKeyToGenerate(KeyType.UNIQUE_DERIVATION_KEY.toString());
        if (emvKeyDerivator.generateKey()) {
            System.out.println("Key successfully generated!");
        } else {
            System.out.println("Key generation failed!");
        }

        return applicationCryptogramResponse;
    }

    private void printApplicationCryptogramRequest(ApplicationCryptogramRequest applicationCryptogramRequest){
        System.out.println("PAN                            : " + applicationCryptogramRequest.getPan());
        System.out.println("PANSequenceNumber              : " + applicationCryptogramRequest.getPanSequenceNumber());
        System.out.println("CryptogramMasterKey            : " + applicationCryptogramRequest.getCryptogramMasterKey());
        System.out.println("AmountAuthorised               : " + applicationCryptogramRequest.getAmountAuthorised());
        System.out.println("AmountOther                    : " + applicationCryptogramRequest.getAmountOther());
        System.out.println("TerminalCountryCode            : " + applicationCryptogramRequest.getTerminalCountryCode());
        System.out.println("TerminalVerificationResults    : " + applicationCryptogramRequest.getTerminalVerificationResults());
        System.out.println("TransactionCurrencyCode        : " + applicationCryptogramRequest.getTransactionCurrencyCode());
        System.out.println("TransactionData                : " + applicationCryptogramRequest.getTransactionDate());
        System.out.println("TransactionType                : " + applicationCryptogramRequest.getTransactionType());
        System.out.println("UnpredictableNumber            : " + applicationCryptogramRequest.getUnpredictableNumber());
        System.out.println("ApplicationInterchangeProfile  : " + applicationCryptogramRequest.getApplicationInterchangeProfile());
        System.out.println("ApplicationTransactionCounter  : " + applicationCryptogramRequest.getApplicationTransactionCounter());
        System.out.println("IssuerApplicationData          : " + applicationCryptogramRequest.getIssuerApplicationData());
    }

}