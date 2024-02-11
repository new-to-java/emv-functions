package com.bc.application.port.in.rest.cryptogramfunctions.mapper;

import com.bc.application.domain.ApplicationCryptogramResponse;
import com.bc.model.dto.GenerateACResponse;
import org.mapstruct.Mapper;

/**
 * Mapper interface for mapping the GenerateACResponse REST API payload from the core domain ApplicationCryptogramResponse
 * object.
 */
@Mapper(componentModel = "cdi")
public interface GenerateACResponseMapper {

    GenerateACResponse mapFromApplicationCryptogramResponse(ApplicationCryptogramResponse applicationCryptogramResponse);

}