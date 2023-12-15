package com.seebie.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record Challenge(@NotBlank String name, @NotBlank String description, @NotNull LocalDate start, @NotNull LocalDate finish) {

}
