package com.bc.application.port.in.rest.cryptogramfunctions.mapper;


import com.bc.application.domain.ApplicationCryptogramRequest;
import com.bc.model.dto.GenerateACRequest;
import org.mapstruct.Mapper;

/**
 * Mapper interface for mapping the GenerateACRequest REST API payload to the core domain ApplicationCryptogramRequest
 * object.
 */

@Mapper(componentModel = "cdi")
public interface GenerateACRequestMapper {

    ApplicationCryptogramRequest mapToGenerateACRequest(GenerateACRequest generateACRequest);

}
