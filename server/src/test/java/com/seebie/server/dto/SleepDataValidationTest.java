package com.seebie.server.dto;

import com.seebie.server.repository.NotificationMessageServiceTest;
import com.seebie.server.test.data.AppRequest;
import com.seebie.server.test.data.ZoneIds;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;

public class SleepDataValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

//    public SleepData(String notes, int minutesAwake, Set<String> tags, ZonedDateTime startTime, ZonedDateTime stopTime, String zoneId) {
    private static final String notes = "";
    private static final int minutesAwake = 1; // invalid -1
    private static final ZonedDateTime stop = ZonedDateTime.now();
    private static final ZonedDateTime start = stop.minusHours(8);
    private static final Set<String> tags = new HashSet<>();
    private static final String zoneId = ZoneIds.AMERICA_NEW_YORK;

    private static List<Arguments> provideSleepDataArguments() {
        return List.of(
            Arguments.of(new SleepData(notes, minutesAwake, tags, start, stop, zoneId), 0),
            Arguments.of(new SleepData(null, minutesAwake, tags, start, stop, zoneId), 1),
            Arguments.of(new SleepData(null, -1, tags, start, stop, zoneId), 2),
            Arguments.of(new SleepData(null, -1, null, start, stop, zoneId), 3),
            Arguments.of(new SleepData(null, -1, null, null, stop, zoneId), 4),
            Arguments.of(new SleepData(null, -1, null, null, null, zoneId), 5),
            Arguments.of(new SleepData(null, -1, null, null, null, null), 6),
            Arguments.of(new SleepData(null, -1, null, null, null, "invalid"), 6)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSleepDataArguments")
    public void testScanForNotifications(SleepData data, int numberViolations) {

        var violations = validator.validate(data);

        assertEquals(numberViolations, violations.size());
    }

}
