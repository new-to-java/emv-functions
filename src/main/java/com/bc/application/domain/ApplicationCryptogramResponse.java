package com.bc.application.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Core domain class defining attributes for Application Cryptogram generation response.
 */
@Getter
@Setter
public class ApplicationCryptogramResponse {

    private String applicationCryptogram;
    private String applicationCryptogramType;

}