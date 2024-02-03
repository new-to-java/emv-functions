package com.bc.service;

import com.bc.domain.ApplicationCryptogramRequest;
import com.bc.domain.ApplicationCryptogramResponse;
import com.bc.enumeration.CryptogramVersionNumber;
import com.bc.enumeration.KeyType;
import com.bc.utilities.DeterminePaymentScheme;
import com.bc.utilities.EMVKeyDerivator;
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
        EMVKeyDerivator emvKeyDerivator = buildUDKGenerationRequest(applicationCryptogramRequest);
        debugLog(emvKeyDerivator);
        String udkGenerated = emvKeyDerivator.generateKey();
        emvKeyDerivator = buildSessionKeyGenerationRequest(applicationCryptogramRequest, udkGenerated);
        return buildGenerateACResponse(emvKeyDerivator);
    }
    /**
     * Method to build and map Application Cryptogram Response.
     * @param emvKeyDerivator EMVKeyDerivator object with the Application Cryptogram.
     * @return ApplicationCryptogramResponse object containing generated Application Cryptogram.
     */
    private ApplicationCryptogramResponse buildGenerateACResponse(EMVKeyDerivator emvKeyDerivator){

        ApplicationCryptogramResponse applicationCryptogramResponse = new ApplicationCryptogramResponse();
        String ARQC = "ARQC";
        applicationCryptogramResponse.setApplicationCryptogram(emvKeyDerivator.generateKey());
        applicationCryptogramResponse.setApplicationCryptogramType(ARQC);

        return applicationCryptogramResponse;

    }
    /**
     * Build a mapping for generating a Unique Derivation Key. This method uses attributes from
     * ApplicationCrypotgramRequest object and sets other input attributes as necessary.
     * @param applicationCryptogramRequest ApplicationCryptogramRequest object built from request.
     * @return EMVKeyDerivator object mapped for UDK generation.
     */
    private EMVKeyDerivator buildUDKGenerationRequest(ApplicationCryptogramRequest applicationCryptogramRequest){

        EMVKeyDerivator emvKeyDerivator = new EMVKeyDerivator();
        emvKeyDerivator.setInputKey(applicationCryptogramRequest.getCryptogramMasterKey());
        emvKeyDerivator.setPan(applicationCryptogramRequest.getPan());
        emvKeyDerivator.setPanSequenceNumber(applicationCryptogramRequest.getPanSequenceNumber());
        emvKeyDerivator.setApplicationTransactionCounter(applicationCryptogramRequest.getApplicationTransactionCounter());
        emvKeyDerivator.setPaymentScheme(DeterminePaymentScheme.fromPan(emvKeyDerivator.getPan()).toString());
        emvKeyDerivator.setInputKeyType(KeyType.CRYPTOGRAM_MASTER_KEY.toString());
        emvKeyDerivator.setCryptogramVersionNumber(CryptogramVersionNumber.VISA_CVN18.name());
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
                                                             String uniqueDerivationKey){

        EMVKeyDerivator emvKeyDerivator = new EMVKeyDerivator();
        emvKeyDerivator.setInputKey(uniqueDerivationKey);
        emvKeyDerivator.setPan(applicationCryptogramRequest.getPan());
        emvKeyDerivator.setPanSequenceNumber(applicationCryptogramRequest.getPanSequenceNumber());
        emvKeyDerivator.setApplicationTransactionCounter(applicationCryptogramRequest.getApplicationTransactionCounter());
        emvKeyDerivator.setPaymentScheme(DeterminePaymentScheme.fromPan(emvKeyDerivator.getPan()).toString());
        emvKeyDerivator.setInputKeyType(KeyType.UNIQUE_DERIVATION_KEY.name());
        emvKeyDerivator.setCryptogramVersionNumber(CryptogramVersionNumber.VISA_CVN18.name());
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