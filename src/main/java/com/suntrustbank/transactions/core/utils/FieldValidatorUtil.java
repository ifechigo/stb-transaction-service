package com.suntrustbank.transactions.core.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;

import java.util.Set;

public class FieldValidatorUtil {

    /**
     * Validates that the provided object is of the specified type.
     *
     * @param object The object to validate.
     * @param <T>    The type of the class.
     * @return The class if the object is of the specified type.
     * @throws GenericErrorCodeException if the object is not of the expected type.
     */
    public static <T> void validate(@NotNull T object) throws GenericErrorCodeException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                errorMessage.append(violation.getMessage())
                        .append("; ");
            }
            throw GenericErrorCodeException.badRequest(errorMessage.toString());
        }
    }
}