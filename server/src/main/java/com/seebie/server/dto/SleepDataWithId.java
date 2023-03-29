package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.HashSet;

public record SleepDataWithId(@NotNull Long id, SleepData sleepData) {

    public SleepDataWithId(Long id, String notes, int minutesAwake, ZonedDateTime startTime, ZonedDateTime stopTime) {
        this(id, new SleepData(notes, minutesAwake, new HashSet<>(), startTime, stopTime));
    }
}
