package com.seebie.server.dto;

import java.util.Set;

public record User(String username,
                   String registrationTime,
                   Set<String> roles,
                   PersonalInfo personalInfo) {

}

