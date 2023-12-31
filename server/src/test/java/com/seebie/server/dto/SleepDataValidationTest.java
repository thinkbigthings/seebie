package com.seebie.server.dto;

import com.seebie.server.test.data.ZoneIds;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepDataValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final String notes = "";
    private static final int minutesAwake = 1;
    private static final ZonedDateTime stop = ZonedDateTime.now();
    private static final ZonedDateTime start = stop.minusHours(8);
    private static final String zoneId = ZoneIds.AMERICA_NEW_YORK;

    private static List<Arguments> provideSleepDataArguments() {
        return List.of(
            Arguments.of(new SleepData(notes, minutesAwake, start, stop, zoneId), 0),
            Arguments.of(new SleepData(null, minutesAwake, start, stop, zoneId), 1),
            Arguments.of(new SleepData(null, -1, start, stop, zoneId), 2),
            Arguments.of(new SleepData(null, -1, null, stop, zoneId), 3),
            Arguments.of(new SleepData(null, -1, null, null, zoneId), 4),
            Arguments.of(new SleepData(null, -1, null, null, null), 5),
            Arguments.of(new SleepData(null, -1, null, null, "invalid"), 5)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSleepDataArguments")
    public void testSleepDataValidation(SleepData data, int numberViolations) {

        var violations = validator.validate(data);

        assertEquals(numberViolations, violations.size());
    }

}
