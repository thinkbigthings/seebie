package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ChallengeDetails(@NotNull Long id, Challenge challenge) {

    public ChallengeDetails(Long id, String name, String description, LocalDate start, LocalDate finish) {
        this(id, new Challenge(name, description, start, finish));
    }
}
