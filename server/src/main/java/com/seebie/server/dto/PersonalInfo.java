package com.seebie.server.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonalInfo(@NotBlank String email, @NotBlank String displayName, boolean notificationsEnabled) {

    public PersonalInfo(String email, @NotBlank String displayName) {
        this(email, displayName, false);
    }

    public PersonalInfo withNotificationEnabled(boolean newNotificationsEnabled) {
        return new PersonalInfo(email, displayName, newNotificationsEnabled);
    }
}

