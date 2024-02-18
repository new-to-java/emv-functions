package com.bc.adapter.in.rest.cryptogramfunctions;

import com.bc.application.domain.CryptogramResponse;
import com.bc.application.enumeration.PaymentScheme;
import com.bc.application.port.in.rest.cryptogramfunctions.command.GenerateApplicationCryptogramCommand;
import com.bc.application.port.in.rest.cryptogramfunctions.mapper.GenerateACRequestToCommandMapper;
import com.bc.application.service.impl.VisaCryptogramFunctionsServiceImpl;
import com.bc.model.dto.GenerateACRequest;
import com.bc.model.dto.GenerateACResponse;
import com.bc.application.port.in.rest.cryptogramfunctions.mapper.GenerateACDomainToResponseMapper;
import com.bc.application.port.in.rest.cryptogramfunctions.client.CryptogramFunctionsAPI;
import com.bc.utilities.DeterminePaymentScheme;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.PublisherRxInvoker;

/**
 * REST API interface adaptor implementing the endpoints and methods that will host various EMV cryptogram functions.
 */
@ApplicationScoped
public class CryptogramFunctionsAPIResource implements CryptogramFunctionsAPI {
    // Service - Pending build and injection
    @Inject
    VisaCryptogramFunctionsServiceImpl visaCryptogramService;
    // Mappers
    @Inject
    GenerateACRequestToCommandMapper generateACRequestToCommandMapper;
    @Inject
    GenerateACDomainToResponseMapper generateACDomainToResponseMapper;
    /**
     * Method handling the generation of Application Cryptograms. This method performs the following functions:
     * - Validate the REST API GenerateACRequest payload and return any validation errors.
     * - Maps the validated request payload to core service request object.
     * - Calls the core service's generateAC method using the service request object.
     * - Retrieve the core service's response and:
     *   - Builds a valid response payload, if generateAC was successful.
     *   - Builds an error response payload, if generateAC was unsuccessful.
     * @param generateACRequest REST API request payload containing the GenerateAC request arributes.
     * @return REST API response payload or error response.
     */
    public Response generateApplicationCrptogram(GenerateACRequest generateACRequest){
        // Command object to validate the Request.
        GenerateApplicationCryptogramCommand generateApplicationCryptogramCommand =
                generateACRequestToCommandMapper.mapGenerateACRequestToCommand(generateACRequest);
        CryptogramResponse cryptogramResponse =
                determinePaymentSchemeAndGenerateCryptogram(generateApplicationCryptogramCommand);
        GenerateACResponse generateACResponse =
                generateACDomainToResponseMapper.mapFromApplicationCryptogramResponse(cryptogramResponse);
        return Response.status(Response.Status.CREATED).entity(generateACResponse).build();
    }
    /**
     * Determine payment scheme from PAN and call corresponding application cryptogram generation service.
     * @param generateApplicationCryptogramCommand Command object mapped from request.
     * @return Cryptogram response object.
     */
    private CryptogramResponse determinePaymentSchemeAndGenerateCryptogram(GenerateApplicationCryptogramCommand generateApplicationCryptogramCommand){
        PaymentScheme paymentScheme = DeterminePaymentScheme.fromPan(generateApplicationCryptogramCommand.pan);
        switch (paymentScheme){
            case VISA:
                return generateVisaApplicationCryptogram(generateApplicationCryptogramCommand);
            case MASTERCARD:
                return generateMastercardApplicationCryptogram(generateApplicationCryptogramCommand);
        }
        return null;
    }
    /**
     * Generate a Visa Payment Scheme specific application cryptogram.
     */
    private CryptogramResponse generateVisaApplicationCryptogram(GenerateApplicationCryptogramCommand generateApplicationCryptogramCommand){
        return visaCryptogramService.getApplicationCryptogram(generateApplicationCryptogramCommand);
    }
    /**
     * Generate a Mastercard Payment Scheme specific application cryptogram.
     */
    private CryptogramResponse generateMastercardApplicationCryptogram(GenerateApplicationCryptogramCommand generateApplicationCryptogramCommand){
        // TODO
        return new CryptogramResponse();
    }
}