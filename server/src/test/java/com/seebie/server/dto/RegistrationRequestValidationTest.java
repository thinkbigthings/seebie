package com.seebie.server.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationRequestValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testValidValues() {

        // annotations are applied on record components
        var validRegistration = new RegistrationRequest("username_here", "password", "x@y.com");

        // these will be applied on sending to a controller
        var violations = validator.validate(validRegistration);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidValues() {

        // annotations are applied on record components
        var invalidRegistration = new RegistrationRequest("spaces require encoding", "password", "x@y.com");

        // these will be applied on sending to a controller
        var violations = validator.validate(invalidRegistration);

        assertEquals(1, violations.size());
    }
}
