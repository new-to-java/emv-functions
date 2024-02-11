package com.bc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class defining REST API attributes for Application Cryptogram generation response payload.
 */
public class GenerateACResponse {

    @JsonProperty("ApplicationCryptogramRequest")
    public String applicationCryptogram;
    @JsonProperty("ApplicationCryptogramType")
    public String applicationCryptogramType;

}

