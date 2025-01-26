package com.seebie.server.dto;

import java.util.Set;

public record User(String publicId,
                   String registrationTime,
                   Set<String> roles,
                   PersonalInfo personalInfo) {

}

