package com.seebie.server.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

public record User(@NotBlank String email,
                   UUID publicId,
                   String registrationTime,
                   Set<String> roles,
                   PersonalInfo personalInfo) {
}
