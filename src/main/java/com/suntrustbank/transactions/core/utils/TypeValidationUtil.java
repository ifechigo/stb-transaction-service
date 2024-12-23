package com.suntrustbank.transactions.core.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;

import java.util.Set;

public class TypeValidationUtil {

    /**
     * Validates that the provided object is of the specified type.
     *
     * @param object The object to validate.
     * @param clazz  The class to check the type against.
     * @param <T>    The type of the class.
     * @return The class if the object is of the specified type.
     * @throws GenericErrorCodeException if the object is not of the expected type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T validateType(@NotNull Object object, @NotNull Class<T> clazz) {
        if (clazz == null) {
            throw GenericErrorCodeException.badRequest("Class type cannot be null.");
        }
        if (object == null) {
            throw GenericErrorCodeException.badRequest("Object cannot be null.");
        }
        if (!clazz.isInstance(object)) {
            throw GenericErrorCodeException.badRequest("Object is not of the expected type: " + clazz.getName());
        }

        T validatedObject = clazz.cast(object);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> violations = validator.validate(validatedObject);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                errorMessage.append(violation.getMessage())
                        .append("; ");
            }
            throw GenericErrorCodeException.badRequest(errorMessage.toString());
        }
        return validatedObject;
    }
}