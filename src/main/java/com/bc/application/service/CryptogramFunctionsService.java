package com.bc.application.service;

import com.bc.application.domain.CryptogramRequest;
import com.bc.application.domain.CryptogramResponse;
import com.bc.application.enumeration.CryptogramVersionNumber;
import com.bc.application.enumeration.EMVUDKDerivationMethod;
import com.bc.application.enumeration.PaymentScheme;
import com.bc.application.port.in.rest.cryptogramfunctions.command.GenerateApplicationCryptogramCommand;
import com.bc.application.port.in.rest.cryptogramfunctions.mapper.GenerateACCommandToDomainMapper;
import com.bc.utilities.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Core domain service hosting the methods for performing Visa Payment scheme specific cryptogram related functions.
 */
@Slf4j
@ApplicationScoped
public abstract class CryptogramFunctionsService
        implements LoggerUtility {
    @Inject
    GenerateACCommandToDomainMapper mapper;
    private Map<String, String> mappedIad = new LinkedHashMap<>();
    private String uniqueDerivationKey;
    private String applicationCryptogramKey;
    private CryptogramVersionNumber cryptogramVersionNumber;
    private String cardVerificationResults;
    private PaymentScheme paymentScheme;
    /**
     * Driver method for generating an Application Cryptogram.
     * @param command command object with the Application Cryptogram generation request.
     * @return CryptogramResponse domain object with the generated cryptogram value.
     */
    public abstract CryptogramResponse getApplicationCryptogram(GenerateApplicationCryptogramCommand command);
    /**
     * Method to build and map the command object to Cryptogram Request domain object.
     * @param command Application Cryptogram Request command object.
     * @return CryptogramRequest domain object generated by mapping command object.
     */
    protected CryptogramRequest buildDomainObjectFromCommand(GenerateApplicationCryptogramCommand command) {
        return mapper.mapGenerateACCommandToDomain(command);
    }
    /**
     * Initialise all required class variables and derive the application cryptogram generation key.
     * @param issuerMasterKey Issuer Master Key for Cryptogram Generation.
     * @param pan Primary Account Number from request.
     * @param panSequenceNumber Primary Account Number from request.
     * @param applicationTransactionCounter Application Transaction Counter from request.
     * @param unpredictableNumber Unpredictable Number from request.
     */
    protected void initialiseCryptogramRequirements(String issuerMasterKey,
                                                    String pan,
                                                    String panSequenceNumber,
                                                    String applicationTransactionCounter,
                                                    String unpredictableNumber,
                                                    String issuerApplicationData){
        mappedIad = parseIssuerApplicationData(issuerApplicationData); // Parse IAD
        setPaymentScheme(pan); // Determine Payment Scheme from PAN
        parsedIadAndSetCvnCvr(); // Set CVN and CVR from parsed IAD
        setApplicationCryptogramGenerationKey(issuerMasterKey,
                pan,
                panSequenceNumber,
                applicationTransactionCounter,
                unpredictableNumber
        );
    }
    /**
     * Method to parse Issuer Application Data based on the Payment Scheme specific implementation of IAD.
     * This method will set the parsed IAD to the mappedIad class variable.
     */
    protected abstract Map<String, String> parseIssuerApplicationData(String issuerApplicationData);
    /**
     * Set Payment Scheme based on the first digit of PAN.
     */
    private void setPaymentScheme(String pan){
        paymentScheme = DeterminePaymentScheme.fromPan(pan);
        logDebug(log,
                "Payment Scheme set based on PAN: {}.",
                paymentScheme
        );
    }
    /**
     * Call Payment Scheme specific IAD parser with IAD from request as input and get a parsed IAD map, and setup CVN and CVR
     * from the parsed IAD.
     */
    private void parsedIadAndSetCvnCvr(){
        logDebug(log,
                "Parsed IAD: {}.",
                mappedIad
        );
        setCvnFromMappedIad();
        setCvrFromMappedIad();
    }
    /**
     * Search mapped IAD data using key "CVN" and set the Cryptogram Version Number based on the value.
     */
    private void setCvnFromMappedIad(){
        final String CVN_NAME = "CVN";
        cryptogramVersionNumber = CryptogramVersionNumber.valueOf(
                mappedIad.get(CVN_NAME)
        );
        logDebug(log,
                "CVN set based on mapped IAD: {}.",
                cryptogramVersionNumber
        );
    }
    /**
     * Search mapped IAD data using key "CVR" and set the Card Verification Results based on the value.
     */
    private void setCvrFromMappedIad(){
        final String CVR_NAME = "CVR";
        cardVerificationResults = mappedIad.get(
                CVR_NAME
        );
        logDebug(log,
                "CVR set based on mapped IAD: {}.",
                cardVerificationResults
        );
    }
    /**
     * Driver method which derives Unique Derivation Key from Issuer Master Key, and subsequently derives a Session Key
     * from the derives Unique Derivation Key.
     * @param issuerMasterKey Issuer Master Key for Cryptogram Generation.
     * @param pan Primary Account Number from request.
     * @param panSequenceNumber Primary Account Sequence Number from request.
     * @param applicationTransactionCounter Application Transaction Counter from request.
     * @param unpredictableNumber Unpredictable Number from request.
     */
    private void setApplicationCryptogramGenerationKey(String issuerMasterKey,
                                             String pan,
                                             String panSequenceNumber,
                                             String applicationTransactionCounter,
                                             String unpredictableNumber){
        // Build UDK from IMK
        buildUniqueDerivationKeyFromIssuerMasterKey(issuerMasterKey,
                pan,
                panSequenceNumber);
        //Build SK from UDK
        buildSessionKeyFromUniqueDerivationKey(applicationTransactionCounter,
                unpredictableNumber
        );
    }
    /**
     * Build Unique Derivation Key from the Issuer Master Key received from input.
     * @param issuerMasterKey Issuer Master Key for Cryptogram Generation.
     * @param pan Primary Account Number from request.
     * @param panSequenceNumber Primary Account Sequence Number from request.
     */
    private void buildUniqueDerivationKeyFromIssuerMasterKey(String issuerMasterKey,
                                                             String pan,
                                                             String panSequenceNumber){
        uniqueDerivationKey = getUniqueDerivationKey(issuerMasterKey,
                pan,
                panSequenceNumber,
                cryptogramVersionNumber
        );
        logDebug(log,
                "UDK generated: {}.",
                uniqueDerivationKey
        );
    }
    /**
     * Build Session Key from the derived Unique Derivation Key and set class variable applicationCryptogramKey.
     * @param applicationTransactionCounter ApplicationTransactionCounter from input.
     */
    private void buildSessionKeyFromUniqueDerivationKey(String applicationTransactionCounter, String unpredictableNumber){
        applicationCryptogramKey = getSessionKey(uniqueDerivationKey,
                applicationTransactionCounter,
                unpredictableNumber,
                cryptogramVersionNumber
        );
        logDebug(log,
                "Session Key generated: {}.",
                applicationCryptogramKey
        );
    }
    /**
     * Method to derive Unique Derivation Key (UDK) from Issuer Master Key (IMK) for cryptogram generation.
     * @param issuerMasterKey Issuer Master Key from request.
     * @param pan Primary Account Number from request.
     * @param panSequenceNumber PAN sequence number from request.
     * @param cryptogramVersionNumber Cryptogram version number determined from Issuer Application Data.
     * @return UDK generated from IMK.
     */
    private String getUniqueDerivationKey(String issuerMasterKey,
                                            String pan,
                                            String panSequenceNumber,
                                            CryptogramVersionNumber cryptogramVersionNumber) {
        EMVUniqueDerivationKeyDerivator emvUdkDerivator = new EMVUniqueDerivationKeyDerivator(issuerMasterKey,
                pan,
                panSequenceNumber,
                paymentScheme,
                cryptogramVersionNumber,
                EMVUDKDerivationMethod.METHOD_A
        );
        // The UDK derivation must be enhanced for CVN 22, as CVN 22 uses METHOD_B.
        return emvUdkDerivator.generateUniqueDerivationKey();
    }
    /**
     * Method to derive Unique Derivation Key (UDK) from Issuer Master Key (IMK) for cryptogram generation.
     * @param uniqueDerivationKey UDK derived from IMK.
     * @param applicationTransactionCounter Application Transaction Counter from request.
     * @param unpredictableNumber Unpredictable Number from input.
     * @param cryptogramVersionNumber Cryptogram version number determined from Issuer Application Data.
     * @return Session Key generated from UDK.
     */
    private String getSessionKey(String uniqueDerivationKey,
                                   String applicationTransactionCounter,
                                   String unpredictableNumber,
                                   CryptogramVersionNumber cryptogramVersionNumber) {
        EMVSessionKeyDerivator emvSessionKeyDerivator = new EMVSessionKeyDerivator(uniqueDerivationKey,
                applicationTransactionCounter,
                unpredictableNumber,
                cryptogramVersionNumber,
                paymentScheme
        );
        return emvSessionKeyDerivator.generateSessionKey();
    }

    /**
     * Generate the application cryptogram after deriving the session key using the cryptogram generation request data.
     * @param cryptogramRequest Cryptogram generation request domain object.
     * @return Application Cryptogram generated.
     */
    protected CryptogramResponse generateCryptogram(CryptogramRequest cryptogramRequest){
        String arqc = generateCryptogram(cryptogramRequest,
                applicationCryptogramKey,
                cryptogramVersionNumber,
                cardVerificationResults,
                paymentScheme);
        return buildResponseObjectFromDomain(arqc);
    }
    /**
     * Method to build and map the Cryptogram Response domain object to Response DTO class.
     * @param arqc Application Cryptogram generated.
     * @return CryptogramResponse object containing generated Application Cryptogram.
     */
    private CryptogramResponse buildResponseObjectFromDomain(String arqc) {
        CryptogramResponse cryptogramResponse = new CryptogramResponse();
        cryptogramResponse.setRequestCryptogram(arqc);
        logDebug(log,
                "Response object generated: {}.",
                cryptogramResponse
        );
        return cryptogramResponse;
    }
    /**
     * Method to call the Payment Scheme specific cryptogram generation request.
     * @param cryptogramRequest Cryptogram Request domain object.
     * @return Application Cryptogram generated by Payment Scheme specific service.
     */
    protected abstract String generateCryptogram(CryptogramRequest cryptogramRequest,
                                                 String sessionKey,
                                                 CryptogramVersionNumber cryptogramVersionNumber,
                                                 String cardVerificationResults,
                                                 PaymentScheme paymentScheme);
}