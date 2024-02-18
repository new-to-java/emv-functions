package com.bc.utilities;

import jakarta.validation.*;
import java.util.Set;

/**
 * Class defining the methods for performing bean validation and raising constraint violation, if invalid attributes
 * are detected.
 */
public abstract class AbstractSelfValidator<T> {
    private final Validator validator;
    /**
     * Constructor
     */
    public AbstractSelfValidator(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    /**
     * This method checks if an object passes all constraints validations configured on the class
     * and raise exception on failure.
     */
    public void selfValidate(){
        Set<ConstraintViolation<T>> constraintViolations = validator.validate((T) this);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}