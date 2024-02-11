package com.bc.application.service;

import com.bc.application.domain.ApplicationCryptogramRequest;
import com.bc.application.domain.ApplicationCryptogramResponse;
import com.bc.application.enumeration.CryptogramVersionNumber;
import com.bc.application.enumeration.EMVUDKDerivationMethod;
import com.bc.application.enumeration.PaymentScheme;
import com.bc.utilities.*;
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
        EMVUniqueDerivationKeyDerivator EMVUniqueDerivationKeyDerivator = buildUDKGenerationRequest(applicationCryptogramRequest);
        String udkGenerated = EMVUniqueDerivationKeyDerivator.generateUniqueDerivationKey();
        EMVSessionKeyDerivator emvSessionKeyDerivator = buildSessionKeyGenerationRequest(applicationCryptogramRequest, udkGenerated);
        String sessionKey = emvSessionKeyDerivator.generateSessionKey();
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
     * @return EMVUniqueDerivationKeyDerivator object mapped for UDK generation.
     */
    private EMVUniqueDerivationKeyDerivator buildUDKGenerationRequest(ApplicationCryptogramRequest applicationCryptogramRequest){

        PaymentScheme paymentScheme = DeterminePaymentScheme.fromPan(applicationCryptogramRequest.getPan());
        CryptogramVersionNumber cryptogramVersionNumber = determineCvnFromIad(applicationCryptogramRequest.getIssuerApplicationData(),
                paymentScheme);
        // Build UDK Derivation request using EMV Method A
        // Method B is not implemented and used for PAN longer than 16 digits.
        return new EMVUniqueDerivationKeyDerivator(applicationCryptogramRequest.getIssuerMasterKey(),
                applicationCryptogramRequest.getPan(),
                applicationCryptogramRequest.getPanSequenceNumber(),
                paymentScheme, cryptogramVersionNumber, EMVUDKDerivationMethod.METHOD_A);

    }
    /**
     * Determine Cryptogram Version Number from Issuer Application Data (IAD) supplied in the request.
     * @param issuerApplicationData Issuer Application Data supplied in the request.
     * @param paymentScheme Payment scheme determine based on the first digit of the PAN.
     * @return Cryptogram Version Number derived from IAD supplied.
     */
    private CryptogramVersionNumber determineCvnFromIad(String issuerApplicationData, PaymentScheme paymentScheme){
        switch (paymentScheme){
            case VISA:
                VisaIADParser visaIADParser = new VisaIADParser();
                visaIADParser.setIssuerApplicationData(issuerApplicationData);
                return visaIADParser.parseIad().getCryptogramVersionNumber();
            case MASTERCARD: // WIP
                return null;
        }
        return null;
    }
    /**
     * Build a mapping for generating a Session Key. This method uses attributes from
     * ApplicationCryptogramRequest and the UDK generated and sets other input attributes as necessary.
     * @param applicationCryptogramRequest ApplicationCryptogramRequest object built from request.
     * @param uniqueDerivationKey UDK generated.
     * @return EMVUniqueDerivationKeyDerivator object mapped for Session Key generation.
     */
    private EMVSessionKeyDerivator buildSessionKeyGenerationRequest(ApplicationCryptogramRequest applicationCryptogramRequest,
                                                                             String uniqueDerivationKey){

        PaymentScheme paymentScheme = DeterminePaymentScheme.fromPan(applicationCryptogramRequest.getPan());
        CryptogramVersionNumber cryptogramVersionNumber = determineCvnFromIad(applicationCryptogramRequest.getIssuerApplicationData(),
                paymentScheme);

        return new EMVSessionKeyDerivator(uniqueDerivationKey, applicationCryptogramRequest.getApplicationTransactionCounter(),
                cryptogramVersionNumber);

    }
    /**
     * Method for logging the input data and output data for the CryptogramFunctions service request, when the debug log level is enabled.
     */
    private void debugLog(Object object){
        if (log.isDebugEnabled()) {
            log.debug(this.getClass() + " Debug log follows: ");
            log.debug("Log for '" + object.getClass() + "' --> Attribute values : {}", object);
        }
    }

}