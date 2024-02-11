package com.bc.adapter.in.rest.cryptogramfunctions;

import com.bc.application.domain.ApplicationCryptogramRequest;
import com.bc.application.domain.ApplicationCryptogramResponse;
import com.bc.model.dto.GenerateACRequest;
import com.bc.model.dto.GenerateACResponse;
import com.bc.application.port.in.rest.cryptogramfunctions.mapper.GenerateACRequestMapper;
import com.bc.application.port.in.rest.cryptogramfunctions.mapper.GenerateACResponseMapper;
import com.bc.application.port.in.rest.cryptogramfunctions.client.CryptogramFunctionsAPI;
import com.bc.application.service.CryptogramFunctionsService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

/**
 * REST API interface adaptor implementing the endpoints and methods that will host various EMV cryptogram functions.
 */
@ApplicationScoped
public class CryptogramFunctionsAPIResource implements CryptogramFunctionsAPI {
    // Service - Pending build and injection
    @Inject
    CryptogramFunctionsService cryptogramFunctionsService;
    // Mappers
    @Inject
    GenerateACRequestMapper generateACRequestMapper;
    @Inject
    GenerateACResponseMapper generateACResponseMapper;
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
    public Response generateApplicationCrptogram(@Valid GenerateACRequest generateACRequest){
        // Assumption is to use Hibernate validators with patterns to cover length+data checks and
        // no service layer validation will be done as of now.
        ApplicationCryptogramRequest applicationCryptogramRequest =
                generateACRequestMapper.mapToGenerateACRequest(generateACRequest);
        ApplicationCryptogramResponse applicationCryptogramResponse =
                cryptogramFunctionsService.generateAC(applicationCryptogramRequest);
        GenerateACResponse generateACResponse =
                generateACResponseMapper.mapFromApplicationCryptogramResponse(applicationCryptogramResponse);
        return Response.status(Response.Status.OK).entity(generateACResponse).build();
    }

}
