package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;

public record PersonalInfo(@NotNull String email, String displayName) { }

