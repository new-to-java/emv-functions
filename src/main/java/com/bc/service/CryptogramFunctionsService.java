package com.bc.service;

import com.bc.domain.ApplicationCryptogramRequest;
import com.bc.domain.ApplicationCryptogramResponse;
import com.bc.enumeration.CryptogramVersionNumber;
import com.bc.enumeration.KeyType;
import com.bc.utilities.ApplicationCryptogramGenerator;
import com.bc.utilities.DeterminePaymentScheme;
import com.bc.utilities.EMVKeyDerivator;
import com.bc.utilities.VisaIADParser;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

/**
 * Core domain service hosting the methods for performing various cryptogram related functions.
 */
@ApplicationScoped
@Slf4j
public class CryptogramFunctionsService {
    public ApplicationCryptogramResponse generateAC(ApplicationCryptogramRequest applicationCryptogramRequest){
        debugLog(applicationCryptogramRequest);
        VisaIADParser visaIADParser = new VisaIADParser();
        visaIADParser.setIssuerApplicationData(applicationCryptogramRequest.getIssuerApplicationData());
        visaIADParser = visaIADParser.parseIad();
        EMVKeyDerivator emvKeyDerivator = buildUDKGenerationRequest(applicationCryptogramRequest, visaIADParser.getCryptogramVersionNumber());
        debugLog(emvKeyDerivator);
        String udkGenerated = emvKeyDerivator.generateKey();
        emvKeyDerivator = buildSessionKeyGenerationRequest(applicationCryptogramRequest, udkGenerated, visaIADParser.getCryptogramVersionNumber());
        String sessionKey = emvKeyDerivator.generateKey();

        log.info(this.getClass() + " - Parsed IAD: IAD Format: {}, Length Indicator:  {}, DKI: {}, CVN: {}, CVR: {}, IDD: {}.",
                visaIADParser.getIssuerApplicationDataFormat(), visaIADParser.getLengthIndicator(), visaIADParser.getDerivationKeyIndex(),
                visaIADParser.getCryptogramVersionNumber(), visaIADParser.getCardVerificationResults(),
                visaIADParser.getIssuerDiscretionaryData());
        ApplicationCryptogramGenerator applicationCryptogramGenerator = new ApplicationCryptogramGenerator();
        String arqc = applicationCryptogramGenerator.getVisaApplicationCryptogram(applicationCryptogramRequest, sessionKey);
        return buildGenerateACResponse(arqc);
    }
    /**
     * Method to build and map Application Cryptogram Response.
     * @param arqc Application Cryptogram generated.
     * @return ApplicationCryptogramResponse object containing generated Application Cryptogram.
     */
    private ApplicationCryptogramResponse buildGenerateACResponse(String  arqc){

        ApplicationCryptogramResponse applicationCryptogramResponse = new ApplicationCryptogramResponse();
        applicationCryptogramResponse.setApplicationCryptogram(arqc);
        applicationCryptogramResponse.setApplicationCryptogramType("ARQC");

        return applicationCryptogramResponse;

    }
    /**
     * Build a mapping for generating a Unique Derivation Key. This method uses attributes from
     * ApplicationCrypotgramRequest object and sets other input attributes as necessary.
     * @param applicationCryptogramRequest ApplicationCryptogramRequest object built from request.
     * @return EMVKeyDerivator object mapped for UDK generation.
     */
    private EMVKeyDerivator buildUDKGenerationRequest(ApplicationCryptogramRequest applicationCryptogramRequest, String cryptogramVersionNumber){

        EMVKeyDerivator emvKeyDerivator = new EMVKeyDerivator();
        emvKeyDerivator.setInputKey(applicationCryptogramRequest.getCryptogramMasterKey());
        emvKeyDerivator.setPan(applicationCryptogramRequest.getPan());
        emvKeyDerivator.setPanSequenceNumber(applicationCryptogramRequest.getPanSequenceNumber());
        emvKeyDerivator.setApplicationTransactionCounter(applicationCryptogramRequest.getApplicationTransactionCounter());
        emvKeyDerivator.setPaymentScheme(DeterminePaymentScheme.fromPan(emvKeyDerivator.getPan()).toString());
        emvKeyDerivator.setInputKeyType(KeyType.CRYPTOGRAM_MASTER_KEY.toString());
        switch (cryptogramVersionNumber){
            case "10":
                emvKeyDerivator.setCryptogramVersionNumber(CryptogramVersionNumber.VISA_CVN10.name());
                break;
            case "18":
            case "22":
                emvKeyDerivator.setCryptogramVersionNumber(CryptogramVersionNumber.VISA_CVN18.name());
        }
        emvKeyDerivator.setKeyToGenerate(KeyType.UNIQUE_DERIVATION_KEY.toString());

        return emvKeyDerivator;

    }
    /**
     * Build a mapping for generating a Session Key. This method uses attributes from
     * ApplicationCrypotgramRequest and the UDK generated and sets other input attributes as necessary.
     * @param applicationCryptogramRequest ApplicationCryptogramRequest object built from request.
     * @param uniqueDerivationKey UDK generated.
     * @return EMVKeyDerivator object mapped for Session Key generation.
     */
    private EMVKeyDerivator buildSessionKeyGenerationRequest(ApplicationCryptogramRequest applicationCryptogramRequest,
                                                             String uniqueDerivationKey, String cryptogramVersionNumber){

        EMVKeyDerivator emvKeyDerivator = new EMVKeyDerivator();
        emvKeyDerivator.setInputKey(uniqueDerivationKey);
        emvKeyDerivator.setPan(applicationCryptogramRequest.getPan());
        emvKeyDerivator.setPanSequenceNumber(applicationCryptogramRequest.getPanSequenceNumber());
        emvKeyDerivator.setApplicationTransactionCounter(applicationCryptogramRequest.getApplicationTransactionCounter());
        emvKeyDerivator.setPaymentScheme(DeterminePaymentScheme.fromPan(emvKeyDerivator.getPan()).toString());
        emvKeyDerivator.setInputKeyType(KeyType.UNIQUE_DERIVATION_KEY.name());
        switch (cryptogramVersionNumber){
            case "10":
                emvKeyDerivator.setCryptogramVersionNumber(CryptogramVersionNumber.VISA_CVN10.name());
                break;
            case "18":
            case "22":
                emvKeyDerivator.setCryptogramVersionNumber(CryptogramVersionNumber.VISA_CVN18.name());
        }
        emvKeyDerivator.setKeyToGenerate(KeyType.SESSION_KEY.name());

        return emvKeyDerivator;

    }
    /**
     * Method for logging the input data and output data for the CryptogramFunctions service request, when the debug log level is enabled.
     */
    private void debugLog(Object objectToLog){

        if (log.isDebugEnabled()) {
            log.debug(" Debug log : {}", objectToLog);
        }

    }

}