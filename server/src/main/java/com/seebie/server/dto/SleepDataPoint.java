package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record SleepDataPoint(@NotNull ZonedDateTime stopTime, int durationMinutes) {


}
