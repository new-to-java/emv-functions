package com.bc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class defining REST API attributes for Application Cryptogram generation response payload.
 */
public class GenerateACResponse {

    @JsonProperty("ApplicationCryptogram")
    public String applicationCryptogram;
    @JsonProperty("ApplicationCryptogramType")
    public String applicationCryptogramType;

}

