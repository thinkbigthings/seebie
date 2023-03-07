package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public record SleepDataPoint(@NotNull LocalDate x, @PositiveOrZero BigDecimal y) {

    public SleepDataPoint(ZonedDateTime stopTime, int minutes) {
        this(stopTime.toLocalDate(), new BigDecimal((double)minutes / 60).setScale(2, RoundingMode.HALF_UP));
    }

}
