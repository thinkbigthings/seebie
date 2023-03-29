package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.ZonedDateTime;
import java.util.HashSet;

public record SleepDetails(@NotNull Long id, @PositiveOrZero int minutesAsleep, SleepData sleepData) {

    public SleepDetails(Long id, int minutesAsleep, String notes, int minutesAwake, ZonedDateTime startTime, ZonedDateTime stopTime) {
        this(id, minutesAsleep, new SleepData(notes, minutesAwake, new HashSet<>(), startTime, stopTime));
    }
}
