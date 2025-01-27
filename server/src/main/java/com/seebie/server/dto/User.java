package com.seebie.server.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record User(@NotBlank String email,
                   String publicId,
                   String registrationTime,
                   Set<String> roles,
                   PersonalInfo personalInfo) {
}
