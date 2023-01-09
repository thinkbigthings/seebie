package com.seebie.server.dto;

import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


public record Sleep(LocalDate dateAwakened, @Positive int minutes, String notes, boolean outOfBed, Set<String> tags) {

    public Sleep(LocalDate dateAwakened, @Positive int minutes) {
        this(dateAwakened, minutes, "", false, new HashSet<>());
    }

    public Sleep(LocalDate dateAwakened, @Positive int minutes, String notes, boolean outOfBed) {
        this(dateAwakened, minutes, notes, outOfBed, new HashSet<>());
    }
}
