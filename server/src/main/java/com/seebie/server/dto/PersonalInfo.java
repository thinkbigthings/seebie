package com.seebie.server.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonalInfo(@NotBlank String displayName, boolean notificationsEnabled) {

    public PersonalInfo withNotificationEnabled(boolean newNotificationsEnabled) {
        return new PersonalInfo(displayName, newNotificationsEnabled);
    }
}

