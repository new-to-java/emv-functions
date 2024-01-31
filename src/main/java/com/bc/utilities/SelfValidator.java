package com.bc.utilities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

/**
 * Class defining the methods for performing bean validation and raising constraint violation, if invalid attributes
 * are detected.
 */
public class SelfValidator <T> {

    private final Validator validator;

    /**
     * Constructor
     */
    public SelfValidator(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    /**
     * This method checks if an object passes all constraints validations configured on the class
     * and raise exception on failure.
     * @return True if object is in a valid state.
     */
    public boolean isAValidObject(T object){
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
        if(!constraintViolations.isEmpty()){
            throw new IllegalStateException("Validation failure for " + object.getClass() + constraintViolations);
        }
        return true;
    }

}
