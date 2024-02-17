package com.bc.application.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Core domain class defining attributes for Application Cryptogram generation response.
 */
@Getter
@Setter
public class CryptogramResponse {

    private String requestCryptogram;
    private String responseCryptogram;
    /**
     * Override method for the object's default toString method.
     * @return String representing object's attribute values.
     */
    @Override
    public String toString() {
        return "CryptogramResponse{" +
                "requestCryptogram='" + requestCryptogram + '\'' +
                ", responseCryptogram='" + responseCryptogram + '\'' +
                '}';
    }
}