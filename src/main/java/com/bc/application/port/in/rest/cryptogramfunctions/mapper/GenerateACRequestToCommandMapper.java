package com.bc.application.port.in.rest.cryptogramfunctions.mapper;


import com.bc.application.port.in.rest.cryptogramfunctions.command.GenerateApplicationCryptogramCommand;
import com.bc.model.dto.GenerateACRequest;
import org.mapstruct.Mapper;

/**
 * Mapper interface for mapping the GenerateACRequest REST API payload to the Generate Application Cryptogram Command
 * mapper.
 */

@Mapper(componentModel = "cdi")
public interface GenerateACRequestToCommandMapper {
    GenerateApplicationCryptogramCommand mapGenerateACRequestToCommand(GenerateACRequest generateACRequest);

}
