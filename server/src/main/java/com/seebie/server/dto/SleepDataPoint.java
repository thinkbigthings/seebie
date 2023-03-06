package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.ZonedDateTime;

public record SleepDataPoint(@NotNull ZonedDateTime stopTime, @PositiveOrZero int durationMinutes) {


}
