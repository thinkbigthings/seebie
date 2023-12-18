package com.seebie.server.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChallengeValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final String description = "Description goes here";

    private static List<Arguments> provideSleepDataArguments() {
        var start = LocalDate.now();
        var finish = start.plusDays(14);
        return List.of(
            Arguments.of(new Challenge("noends", description, start, finish), 0),
            Arguments.of(new Challenge("", description, start, finish), 1),
            Arguments.of(new Challenge(" whitespace at front", description, start, finish), 1),
            Arguments.of(new Challenge("whitespace at end ", description, start, finish), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSleepDataArguments")
    public void testScanForNotifications(Challenge data, int numberViolations) {

        var violations = validator.validate(data);

        assertEquals(numberViolations, violations.size());
    }

}
