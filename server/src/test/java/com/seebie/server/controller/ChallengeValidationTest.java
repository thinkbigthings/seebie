package com.seebie.server.controller;

import com.seebie.server.dto.Challenge;
import com.seebie.server.dto.SleepData;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChallengeValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static List<Arguments> provideSleepDataArguments() {
        return List.of(
            Arguments.of(new Challenge("note", "description", now(), now().plusDays(14L)), 0),
            Arguments.of(new Challenge("", "description", now(), now().plusDays(14L)), 1),
            Arguments.of(new Challenge("", "", now(), now().plusDays(14L)), 2),
            Arguments.of(new Challenge("", "", null, now().plusDays(14L)), 3),
            Arguments.of(new Challenge("", "", null, null), 4)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSleepDataArguments")
    public void testScanForNotifications(Challenge data, int numberViolations) {

        var violations = validator.validate(data);

        assertEquals(numberViolations, violations.size());
    }

}
