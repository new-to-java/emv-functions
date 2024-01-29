package com.bc.mapper;


import com.bc.domain.ApplicationCryptogramRequest;
import com.bc.dto.GenerateACRequest;
import org.mapstruct.Mapper;

/**
 * Mapper interface for mapping the GenerateACRequest REST API payload to the core domain ApplicationCryptogramRequest
 * object.
 */

@Mapper(componentModel = "cdi")
public interface GenerateACRequestMapper {

    ApplicationCryptogramRequest mapToGenerateACRequest(GenerateACRequest generateACRequest);

}
