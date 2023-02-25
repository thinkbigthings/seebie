package com.seebie.server.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonalInfo(@NotBlank String email, @NotBlank String displayName) { }

