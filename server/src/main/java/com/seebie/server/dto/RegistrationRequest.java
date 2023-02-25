package com.seebie.server.dto;

import com.seebie.server.validation.NoUrlEncoding;
import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(@NoUrlEncoding String username, @NotNull String plainTextPassword, String email) {

}

