package com.bc.application.service.impl;

import com.bc.application.domain.CryptogramRequest;
import com.bc.application.domain.CryptogramResponse;
import com.bc.application.enumeration.CryptogramVersionNumber;
import com.bc.application.enumeration.PaymentScheme;
import com.bc.application.port.in.rest.cryptogramfunctions.command.GenerateApplicationCryptogramCommand;
import com.bc.application.service.AbstractCryptogramFunctionsService;
import com.bc.utilities.VisaApplicationCryptogramGenerator;
import com.bc.utilities.VisaIADParser;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
/**
 * Core domain service implementing the methods for Visa Payment scheme specific cryptogram functions.
 */
@Slf4j
@ApplicationScoped
public class VisaCryptogramFunctionsServiceImpl
        extends AbstractCryptogramFunctionsService {
    /**
     * Driver method for generating an Application Cryptogram.
     *
     * @param command command object with the Application Cryptogram generation request.
     * @return CryptogramResponse domain object with the generated cryptogram value.
     */
    @Override
    public CryptogramResponse getApplicationCryptogram(GenerateApplicationCryptogramCommand command) {
        logDebug(log, "Command object received: {}.", command);
        CryptogramRequest cryptogramRequest = buildDomainObjectFromCommand(command);
        logDebug(log, "Domain objectCommand mapped from command: {}.", cryptogramRequest);
        initialiseCryptogramRequirements(cryptogramRequest.getIssuerMasterKey(),
                cryptogramRequest.getPan(),
                cryptogramRequest.getPanSequenceNumber(),
                cryptogramRequest.getApplicationTransactionCounter(),
                cryptogramRequest.getUnpredictableNumber(),
                cryptogramRequest.getIssuerApplicationData()
        );
        return generateCryptogram(cryptogramRequest);
    }
    /**
     * Method to parse Issuer Application Data based on the Payment Scheme specific implementation of IAD.
     * This method will set the parsed IAD to the mappedIad class variable.
     */
    @Override
    protected Map<String, String> parseIssuerApplicationData(String issuerApplicationData) {
        return new VisaIADParser(issuerApplicationData).parseIad();
    }
    /**
     * Method to call the Payment Scheme specific cryptogram generation request.
     *
     * @param cryptogramRequest Cryptogram Request domain object.
     * @param sessionKey Session key to be used for cryptogram generation.
     * @param cryptogramVersionNumber Cryptogram Version Number.
     * @param cardVerificationResults Card Verification results.
     * @return Application Cryptogram generated by Payment Scheme specific service.
     */
    @Override
    protected String generateCryptogram(CryptogramRequest cryptogramRequest,
                                        String sessionKey,
                                        CryptogramVersionNumber cryptogramVersionNumber,
                                        String cardVerificationResults,
                                        PaymentScheme paymentScheme) {
        VisaApplicationCryptogramGenerator visaApplicationCryptogramGenerator = new VisaApplicationCryptogramGenerator();
        return visaApplicationCryptogramGenerator
                .generateApplicationCryptogram(cryptogramRequest,
                        sessionKey,
                        cryptogramVersionNumber,
                        cardVerificationResults,
                        paymentScheme
                );
    }
}