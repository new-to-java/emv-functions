package com.bc.port;

import com.bc.dto.GenerateACRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST API interface port defining endpoints and methods that will host various EMV cryptogram functions.
 */
@RegisterRestClient
@Path("/CryptogramFunctions")
public interface CryptogramFunctionsAPI {

    /**
     * Method signature definition for EMV Application Cryptogram generation.
     */
    @POST
    @Path("/GenerateCryptogram/Request")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response generateApplicationCrptogram(@Valid GenerateACRequest generateACRequest);

}
