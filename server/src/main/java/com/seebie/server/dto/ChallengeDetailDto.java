package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ChallengeDetailDto(@NotNull Long id, ChallengeDto challenge) {

    public ChallengeDetailDto(Long id, String name, String description, LocalDate start, LocalDate finish) {
        this(id, new ChallengeDto(name, description, start, finish));
    }
}
