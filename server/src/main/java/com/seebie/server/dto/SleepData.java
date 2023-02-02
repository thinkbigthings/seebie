package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;


public record SleepData(@NotNull LocalDate dateAwakened,
                        @Positive int minutes,
                        @NotNull String notes,
                        @PositiveOrZero int outOfBed,
                        @NotNull Set<String> tags,
                        @NotNull ZonedDateTime startTime,
                        @NotNull ZonedDateTime stopTime)
{

    public SleepData(LocalDate dateAwakened, int minutes, String notes, int outOfBed, Set<String> tags) {
        this(dateAwakened, minutes, notes, outOfBed, tags, ZonedDateTime.now(), ZonedDateTime.now());
    }

    public SleepData(LocalDate dateAwakened, int minutes, String notes, int outOfBed, Set<String> tags, ZonedDateTime startTime, ZonedDateTime stopTime) {
        this.dateAwakened = dateAwakened;
        this.minutes = minutes;
        this.notes = notes;
        this.outOfBed = outOfBed;
        this.tags = Collections.unmodifiableSet(tags);
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

}
