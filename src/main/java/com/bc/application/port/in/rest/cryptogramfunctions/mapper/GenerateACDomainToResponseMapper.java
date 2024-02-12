package com.bc.application.port.in.rest.cryptogramfunctions.mapper;

import com.bc.application.domain.CryptogramResponse;
import com.bc.model.dto.GenerateACResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping the GenerateACResponse REST API payload from the core domain CryptogramResponse
 * object.
 */
@Mapper(componentModel = "cdi")
public interface GenerateACDomainToResponseMapper {

    @Mapping(source = "requestCryptogram", target = "applicationCryptogram")
    @Mapping(source = "responseCryptogram", target = "applicationResponseCryptogram")
    GenerateACResponse mapFromApplicationCryptogramResponse(CryptogramResponse cryptogramResponse);

}