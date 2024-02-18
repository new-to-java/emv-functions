package com.bc.adapter.in.rest.cryptogramfunctions;

import com.bc.application.domain.CryptogramResponse;
import com.bc.application.port.in.rest.cryptogramfunctions.command.GenerateApplicationCryptogramCommand;
import com.bc.application.port.in.rest.cryptogramfunctions.mapper.GenerateACRequestToCommandMapper;
import com.bc.application.service.impl.VisaCryptogramFunctionsServiceImpl;
import com.bc.model.dto.GenerateACRequest;
import com.bc.model.dto.GenerateACResponse;
import com.bc.application.port.in.rest.cryptogramfunctions.mapper.GenerateACDomainToResponseMapper;
import com.bc.application.port.in.rest.cryptogramfunctions.client.CryptogramFunctionsAPI;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

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
                visaCryptogramService.getApplicationCryptogram(generateApplicationCryptogramCommand);
        GenerateACResponse generateACResponse =
                generateACDomainToResponseMapper.mapFromApplicationCryptogramResponse(cryptogramResponse);
        return Response.status(Response.Status.CREATED).entity(generateACResponse).build();
    }
    // Implement the following methods:
    // Driver method which will
    //      Call another method to determine payment scheme
    //      Based on payment scheme, will call the corresponding Cryptogram Service
    //      Return the cryptogram generated to the API resource.

}
