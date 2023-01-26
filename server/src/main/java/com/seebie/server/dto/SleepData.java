package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


public record SleepData(@NotNull LocalDate dateAwakened, @Positive int minutes, @NotNull String notes, @PositiveOrZero int outOfBed, @NotNull Set<String> tags) {

    public SleepData(LocalDate dateAwakened, int minutes) {
        this(dateAwakened, minutes, "", 0, new HashSet<>());
    }

    public SleepData(LocalDate dateAwakened, int minutes, String notes, int outOfBed) {
        this(dateAwakened, minutes, notes, outOfBed, new HashSet<>());
    }
}
