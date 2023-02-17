package com.seebie.server.dto;

import com.seebie.server.validation.NoUrlEncoding;

public record RegistrationRequest(@NoUrlEncoding String username, String plainTextPassword, String email) {

}

