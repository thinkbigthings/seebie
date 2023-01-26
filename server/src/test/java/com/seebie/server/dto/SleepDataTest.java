package com.seebie.server.dto;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepDataTest {


    @Test
    public void testValidation() {

        var validator = Validation.buildDefaultValidatorFactory().getValidator();

        // annotations are applied on record components
        var invalidSleep = new SleepData(LocalDate.now(), 0, "", 0, new HashSet<>());

        // these will be applied on sending to a controller
        var violations = validator.validate(invalidSleep);

        assertEquals(1, violations.size());
    }
}
