package com.bc.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class defining REST API attributes for Application Cryptogram generation response payload.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateACResponse {
    @JsonProperty("ARQC")
    public String applicationCryptogram;
    @JsonProperty("ARPC")
    public String applicationResponseCryptogram;

}