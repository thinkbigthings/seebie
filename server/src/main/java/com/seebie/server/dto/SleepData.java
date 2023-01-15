package com.seebie.server.dto;

import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


public record SleepData(LocalDate dateAwakened, @Positive int minutes, String notes, @Positive int outOfBed, Set<String> tags) {

    public SleepData(LocalDate dateAwakened, int minutes) {
        this(dateAwakened, minutes, "", 0, new HashSet<>());
    }

    public SleepData(LocalDate dateAwakened, int minutes, String notes, int outOfBed) {
        this(dateAwakened, minutes, notes, outOfBed, new HashSet<>());
    }
}
