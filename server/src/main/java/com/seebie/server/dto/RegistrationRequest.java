package com.seebie.server.dto;

import com.seebie.server.validation.NoUrlEncoding;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(@NoUrlEncoding String displayName, @NotBlank String plainTextPassword, @NotBlank String email) {

}

