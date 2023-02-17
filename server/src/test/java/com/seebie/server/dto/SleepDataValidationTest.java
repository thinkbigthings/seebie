package com.seebie.server.dto;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepDataValidationTest {


    @Test
    public void testValidation() {

        var validator = Validation.buildDefaultValidatorFactory().getValidator();

        // annotations are applied on record components
        var invalidSleep = new SleepData(null, 0, emptySet(), null, null);

        // these will be applied on sending to a controller
        var violations = validator.validate(invalidSleep);

        assertEquals(3, violations.size());
    }
}
