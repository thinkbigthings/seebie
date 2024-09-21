package com.seebie.server.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.ZoneId;

public class ZoneIdValidator implements ConstraintValidator<ZoneIdConstraint, String> {

    @Override
    public void initialize(ZoneIdConstraint constraint) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // Null values are NOT considered valid
        if (value == null) {
            return false;
        }

        try {
            ZoneId.of(value);
        }
        catch (DateTimeException e) {
            return false;
        }

        return true;
    }
}
