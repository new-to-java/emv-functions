package com.bc.application.port.in.rest.cryptogramfunctions.mapper;

import com.bc.application.domain.CryptogramRequest;
import com.bc.application.port.in.rest.cryptogramfunctions.command.GenerateApplicationCryptogramCommand;
import org.mapstruct.Mapper;

/**
 * Mapper interface for mapping the GenerateACRequest REST API payload to the core domain CryptogramRequest
 * object.
 */
@Mapper(componentModel = "cdi")
public interface GenerateACCommandToDomainMapper {
    CryptogramRequest mapGenerateACCommandToDomain(GenerateApplicationCryptogramCommand command);

}