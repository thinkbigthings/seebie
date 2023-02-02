package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public record SleepData(@NotNull String notes,
                        @PositiveOrZero int outOfBed,
                        @NotNull Set<String> tags,
                        @NotNull ZonedDateTime startTime,
                        @NotNull ZonedDateTime stopTime)
{

    public SleepData() {
        this("", 0, new HashSet<>(), ZonedDateTime.now(), ZonedDateTime.now());
    }

    public SleepData(String notes, int outOfBed, Set<String> tags, ZonedDateTime startTime, ZonedDateTime stopTime) {
        this.notes = notes;
        this.outOfBed = outOfBed;
        this.tags = unmodifiableSet(tags);
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

}
