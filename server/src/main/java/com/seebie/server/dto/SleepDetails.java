package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record SleepDetails(@NotNull Long id, @PositiveOrZero int minutesAsleep, SleepData sleepData) {

    public SleepDetails(Long id, int minutesAsleep, String notes, int minutesAwake, ZonedDateTime startTime, ZonedDateTime stopTime, String zoneId) {
        this(id, minutesAsleep, new SleepData(notes, minutesAwake, startTime, stopTime, zoneId));
    }

    public SleepDetails(Long id, int minutesAsleep, String notes, int minutesAwake, LocalDateTime startTime, LocalDateTime stopTime, String zoneId) {
        this(id, minutesAsleep, new SleepData(notes, minutesAwake, startTime, stopTime, zoneId));
    }
}
