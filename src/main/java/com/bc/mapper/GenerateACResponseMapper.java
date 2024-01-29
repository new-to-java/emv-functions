package com.bc.mapper;

import com.bc.domain.ApplicationCryptogramResponse;
import com.bc.dto.GenerateACResponse;
import org.mapstruct.Mapper;

/**
 * Mapper interface for mapping the GenerateACResponse REST API payload from the core domain ApplicationCryptogramResponse
 * object.
 */
@Mapper(componentModel = "cdi")
public interface GenerateACResponseMapper {

    GenerateACResponse mapFromApplicationCryptogramResponse(ApplicationCryptogramResponse applicationCryptogramResponse);

}