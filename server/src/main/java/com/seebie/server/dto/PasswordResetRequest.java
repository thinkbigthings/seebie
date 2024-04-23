package com.seebie.server.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String plainTextPassword) {

}
