package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public record SleepData(@NotNull String notes,
                        @PositiveOrZero int minutesAwake,
                        @NotNull Set<String> tags,
                        @NotNull ZonedDateTime startTime,
                        @NotNull ZonedDateTime stopTime)
{

    public SleepData() {
        this("", 0, new HashSet<>(), ZonedDateTime.now().minusHours(8L), ZonedDateTime.now());
    }

    public SleepData(ZonedDateTime startTime, ZonedDateTime stopTime) {
        this("", 0, new HashSet<>(), startTime, stopTime);
    }

    public SleepData(String notes, int minutesAwake, ZonedDateTime startTime, ZonedDateTime stopTime) {
        this(notes, minutesAwake, new HashSet<>(), startTime, stopTime);
    }

    public SleepData(String notes, int minutesAwake, Set<String> tags, ZonedDateTime startTime, ZonedDateTime stopTime) {
        this.notes = notes;
        this.minutesAwake = minutesAwake;
        this.tags = unmodifiableSet(tags);
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

}
